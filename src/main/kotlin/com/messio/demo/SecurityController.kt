package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(SECURITY_WEB_CONTEXT)
@CrossOrigin(allowCredentials = "true")
class SecurityController(val facade: Facade, val keyManager: KeyManager, val passwordEncoder: PasswordEncoder) {
    private val logger: Logger = LoggerFactory.getLogger(SecurityController::class.java)

    @GetMapping()
    fun apiRoot(): Map<String, String> {
        return mapOf("status" to "ok")
    }

    @GetMapping("/error")
    fun apiError(): ResponseEntity<HttpStatus> {
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/sign-in")
    fun apiSignIn(@RequestBody signInValue: SignInValue, @Autowired req: HttpServletRequest): TokenValue {
        facade.userRepository.findTopByEmail(signInValue.email)?.let {
            if (passwordEncoder.matches(signInValue.password, it.pass)){
                logger.debug("Login successful for: ${signInValue.email}")
                val token = keyManager.buildToken(it)
                logger.debug("Token: $token")
                return TokenValue(token)
            }
        }
        throw CustomException("Invalid email / password")
    }

    @GetMapping("/sign-out")
    fun apiSignOut(): TokenValue {
        return TokenValue()
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

