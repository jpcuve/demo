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
    private val logger: Logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    override fun configure(http: HttpSecurity) {
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
                val authorizationHeader = request.getHeader("Authorization") ?: return null
                logger.debug("Authorization header: $authorizationHeader")
                // here decode the JWT token, return the token contents or null
                return UserValue(email = "text")
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
        return AuthenticationManager { authentication ->
            // check out if authentication ok, set the granted authorities as well, set authenticated = true
            authentication ?: throw BadCredentialsException("Not authenticated")
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10, SecureRandom())
    }

}
