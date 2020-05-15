package com.messio.demo

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest

const val SECURITY_WEB_CONTEXT = "/auth"
const val AUTHORIZATION_PREFIX = "Bearer "
const val APP_WEB_CONTEXT = "/dummy"


class KeyManager(secretKey: String) {
    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8));

    fun buildToken(user: User): String = Jwts
            .builder()
            .setSubject(user.email)
            .claim("roles", user.securityRoles.joinToString(","))
            .signWith(key)
            .compact()

    fun verifyToken(token: String): Jws<Claims> = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
}

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
class SecurityConfiguration(val appProperties: AppProperties) : WebSecurityConfigurerAdapter() {
    private val logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable() // not for production
                .headers().frameOptions().disable().and()  // not for production, necessary for H2 console
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilter(preAuthTokenHeaderFilter())
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("$APP_WEB_CONTEXT/**", "/").permitAll()
                .antMatchers("$SECURITY_WEB_CONTEXT/**").permitAll()
                .antMatchers("/mustache/**").permitAll()
                .anyRequest().authenticated()
    }

    @Bean
    fun preAuthTokenHeaderFilter(): Filter {
        val filter = object : AbstractPreAuthenticatedProcessingFilter() {
            override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any?
                    = request.getHeader("Authorization")
            override fun getPreAuthenticatedCredentials(request: HttpServletRequest): Any = "N/A"
        }
        filter.setAuthenticationManager(authenticationManager())
        return filter
    }

    @Bean
    override fun authenticationManager() = object : AuthenticationManager {
        override fun authenticate(authentication: Authentication): Authentication? {
            val authorizationHeader = authentication.principal.toString()
            if (authorizationHeader.startsWith(AUTHORIZATION_PREFIX, ignoreCase = true)) {
                val token = authorizationHeader.substring(AUTHORIZATION_PREFIX.length).trim()
                try {
                    val claims = keyManager().verifyToken(token)
                    logger.debug("Claims: $claims")
                    val grantedAuthorities = claims.body["roles"].toString()
                            .split(",")
                            .map { SimpleGrantedAuthority(it) }
                            .toList()
                    return UsernamePasswordAuthenticationToken(
                            claims.body.subject,
                            null,
                            grantedAuthorities)
                } catch (e: JwtException) {
                    logger.error("Cannot decode jwt", e)
                    throw BadCredentialsException("Not authenticated")
                }
            }
            return authentication
        }
    }

    @Bean
    fun keyManager() = KeyManager(appProperties.secretKey)

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder(10, SecureRandom())
}
