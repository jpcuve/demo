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
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest

const val SECURITY_WEB_CONTEXT = "/auth"
const val AUTHORIZATION_PREFIX = "Bearer "

class KeyManager(val key: SecretKey) {

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
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    private val logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)
    private val key = Keys.hmacShaKeyFor("my_secret_key_must_be_long_enough".toByteArray(StandardCharsets.UTF_8));

    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .addFilter(preAuthTokenHeaderFilter())
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("$SECURITY_WEB_CONTEXT/**").permitAll()
                .antMatchers("/test-error").permitAll()
                .anyRequest().authenticated()
    }

    @Bean
    fun preAuthTokenHeaderFilter(): Filter {
        val filter = object : AbstractPreAuthenticatedProcessingFilter() {
            override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any? {
                logger.debug("Request: ${request.method} ${request.requestURI}")
                for (name in request.headerNames) {
                    logger.debug(" Header: $name=${request.getHeader(name)}")
                }
                val header = request.getHeader("Authorization")
                logger.debug("Authorization: $header")
                return header
            }

            override fun getPreAuthenticatedCredentials(request: HttpServletRequest): Any {
                return "N/A"
            }
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
    fun keyManager() = KeyManager(key)

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder(10, SecureRandom())
}
