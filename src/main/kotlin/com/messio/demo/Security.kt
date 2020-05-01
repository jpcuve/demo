package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.SecureRandom


@Configuration
class SecurityConfiguration(val facade: Facade) : WebSecurityConfigurerAdapter() {
    private val logger: Logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                // .antMatchers("/h2-console").hasAuthority("ADMIN")
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
    }

    @Bean
    fun authenticationProvider(userDetailsService: UserDetailsService?, passwordEncoder: PasswordEncoder?): AuthenticationProvider {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder)
        return authenticationProvider
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username: String -> facade.userRepository.findTopByEmail(username) }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10, SecureRandom())
    }
}