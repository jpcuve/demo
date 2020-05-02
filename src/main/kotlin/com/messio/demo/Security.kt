package com.messio.demo

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest


@Configuration
class SecurityConfiguration(val facade: Facade) : WebSecurityConfigurerAdapter() {
    private val logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    // private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val key = Keys.hmacShaKeyFor("my_secret_key_must_be_long_enough".toByteArray(StandardCharsets.UTF_8));
    private val token = Jwts
            .builder()
            .setSubject("Joe")
            .claim("roles", "a,b,c")
            .signWith(key)
            .compact()

    override fun configure(http: HttpSecurity) {
        logger.debug("Token: $token")
        http
                .csrf().disable()
                .addFilter(preAuthTokenHeaderFilter())
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
    }

    @Bean
    fun preAuthTokenHeaderFilter(): Filter {
        val filter = object : AbstractPreAuthenticatedProcessingFilter() {
            override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any? {
                return request.getHeader("Authorization")
            }

            override fun getPreAuthenticatedCredentials(request: HttpServletRequest): Any {
                return "N/A"
            }
        }
        filter.setAuthenticationManager(authenticationManager())
        return filter
    }

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return object : AuthenticationManager {
            override fun authenticate(authentication: Authentication): Authentication {
                val authorizationHeader = authentication.principal.toString()
                if (authorizationHeader.startsWith("Bearer ", ignoreCase = true)){
                    val token = authorizationHeader.substring(7).trim()
                    try {
                        val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                        logger.debug("Claims: ${claims}")
                        val grantedAuthorities = claims.body["roles"].toString()
                                .split(",")
                                .map { SimpleGrantedAuthority(it) }
                                .toList()
                        return UsernamePasswordAuthenticationToken(claims.body.subject, null, grantedAuthorities)
                    } catch (e: JwtException) {
                        logger.error("Cannot decode jwt", e)
                    }
                }
                throw BadCredentialsException("Not authenticated")
            }
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10, SecureRandom())
    }

}
