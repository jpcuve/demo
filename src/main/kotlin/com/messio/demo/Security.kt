package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import java.security.SecureRandom
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest


@Configuration
class SecurityConfiguration(val facade: Facade) : WebSecurityConfigurerAdapter() {
    private val logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)
    private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val token = Jwts.builder().setSubject("Joe").signWith(key).compact()

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
                val authorizationHeader = request.getHeader("Authorization")
                logger.debug("Authorization header: $authorizationHeader")
                // return the token as principal, or null
                return token
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
                // check out if authentication ok, set the granted authorities as well, set authenticated = true
                try {
                    val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                    logger.debug("Claims: ${claims}")
                    authentication.isAuthenticated = true
                    return authentication
                } catch (e: JwtException) {
                    throw BadCredentialsException("Not authenticated", e)
                }
            }
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10, SecureRandom())
    }

}
