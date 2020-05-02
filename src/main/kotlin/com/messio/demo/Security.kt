package com.messio.demo

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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
import org.springframework.web.bind.annotation.*
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.*
import javax.servlet.Filter
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest

const val SECURITY_WEB_CONTEXT = "/auth"
const val AUTHORIZATION_PREFIX = "Bearer "

@RestController
@RequestMapping(SECURITY_WEB_CONTEXT)
@CrossOrigin
class SecurityController(val facade: Facade) {
    private val logger: Logger = LoggerFactory.getLogger(SecurityController::class.java)

    @PostMapping("/sign-in")
    fun apiSignIn(@RequestBody signInValue: SignInValue, @Autowired req: HttpServletRequest): UserValue {
        try {
            req.login(signInValue.email, signInValue.password)
            val user: User = facade.userRepository.findTopByEmail(signInValue.email) ?: User()
            return UserValue(user.email)
        } catch (e: ServletException) {
            logger.info("Login failed: ${signInValue.email}")
        }
        return UserValue()
    }

    @GetMapping("/sign-out")
    fun apiSignOut(): UserValue {
        return UserValue()
    }

    @PostMapping("/sign-up")
    fun apiSignUp(@RequestBody signUpValue: SignUpValue): String {
        return "ok"
    }

    @PostMapping("/update-password")
    fun apiUpdatePassword(@RequestBody updatePasswordValue: UpdatePasswordValue): String {
        return "ok"
    }

    @PostMapping("/reset-password")
    fun apiResetPassword(@RequestBody resetPasswordValue: ResetPasswordValue): String {
        return "ok"
    }

    @PostMapping("/google-sign-in")
    fun apiGoogleSignIn(@RequestBody googleSignInValue: GoogleSignInValue): String {
        return "ok"
    }
}


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
                .addFilter(preAuthTokenHeaderFilter())
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("$SECURITY_WEB_CONTEXT/**").permitAll()
                .anyRequest().authenticated()
    }

    @Bean
    fun preAuthTokenHeaderFilter(): Filter {
        val filter = object : AbstractPreAuthenticatedProcessingFilter() {
            override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any? {
                logger.debug("Request: ${request.method} ${request.requestURI}")
                for (name in request.headerNames){
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
    override fun authenticationManager(): AuthenticationManager {
        return object : AuthenticationManager {
            override fun authenticate(authentication: Authentication): Authentication {
                val authorizationHeader = authentication.principal.toString()
                if (authorizationHeader.startsWith(AUTHORIZATION_PREFIX, ignoreCase = true)) {
                    val token = authorizationHeader.substring(AUTHORIZATION_PREFIX.length).trim()
                    try {
                        val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                        logger.debug("Claims: ${claims}")
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
