package com.messio.demo

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest

const val SECURITY_WEB_CONTEXT = "/auth"
const val AUTHORIZATION_PREFIX = "Bearer "

@RestController
@RequestMapping(SECURITY_WEB_CONTEXT)
@CrossOrigin(allowCredentials = "true")
class SecurityController(val facade: Facade, val keyManager: KeyManager, val passwordEncoder: PasswordEncoder) {
    private val logger: Logger = LoggerFactory.getLogger(SecurityController::class.java)

    @GetMapping()
    fun apiRoot(): Map<String, String> {
        return mapOf("ok" to "status")
    }

    @GetMapping("/error")
    fun apiError(): ResponseEntity<HttpStatus> {
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/sign-in")
    fun apiSignIn(@RequestBody signInValue: SignInValue, @Autowired req: HttpServletRequest): ProfileValue {
        facade.userRepository.findTopByEmail(signInValue.email)?.let {
            if (passwordEncoder.matches(signInValue.password, it.pass)){
                logger.debug("Login successful for: ${signInValue.email}")
                val token = keyManager.buildToken(it)
                logger.debug("Token: $token")
                return ProfileValue(true, token, it.name, listOf())
            }
        }
        throw CustomException("Invalid email / password")
    }

    @GetMapping("/sign-out")
    fun apiSignOut(): ProfileValue {
        return ProfileValue()
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

class KeyManager(val key: SecretKey) {

    fun buildToken(user: User): String = Jwts
            .builder()
            .setSubject(user.email)
            .claim("roles", "a,b,c")
            .signWith(key)
            .compact()

    fun verifyToken(token: String): Jws<Claims> = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
}


@Configuration
class SecurityConfiguration(val facade: Facade) : WebSecurityConfigurerAdapter() {
    private val logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)
    private val key = Keys.hmacShaKeyFor("my_secret_key_must_be_long_enough".toByteArray(StandardCharsets.UTF_8));
    // private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

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
