package com.messio.demo.controller

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.messio.demo.*
import com.samskivert.mustache.Mustache
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping(SECURITY_WEB_CONTEXT)
@CrossOrigin(allowCredentials = "true")
class SecurityController(
        val facade: Facade,
        val mailService: MailService,
        val keyManager: KeyManager,
        val passwordEncoder: PasswordEncoder,
        val appProperties: AppProperties
) {
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
    fun apiSignIn(@RequestBody signInValue: SignInValue): TokenValue {
        facade.userRepository.findTopByEmail(signInValue.email)?.let {
            if (passwordEncoder.matches(signInValue.password, it.pass)) {
                logger.debug("Login successful for: ${signInValue.email}")
                val token = keyManager.buildToken(it)
                logger.debug("Token: $token")
                return TokenValue(token)
            }
        }
        throw CustomException("Invalid email / password")
    }

    @PostMapping("/social-sign-in")
    fun apiSocialSignIn(@RequestBody socialSignInValue: SocialSignInValue): TokenValue {
        logger.debug("Social sign-in: $socialSignInValue")
        when (socialSignInValue.social) {
            "google" -> {
                val transport = GoogleNetHttpTransport.newTrustedTransport()
                val jsonFactory = JacksonFactory.getDefaultInstance()
                val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                        .setAudience(listOf(appProperties.google))
                        .build()
                verifier.verify(socialSignInValue.identity)?.let {
                    val payload = it.payload
                    val userId = payload.subject
                    logger.debug("Google user id: $userId") // must be used for user detection
                    facade.userRepository.findTopByGoogleId(userId)?.let {
                        logger.debug("Login successful for: ${payload.email}")
                        val token = keyManager.buildToken(it)
                        logger.debug("Token: $token")
                        return TokenValue(token)
                    }
                }
            }
        }
        throw CustomException("Invalid token")
    }

    @GetMapping("/sign-out")
    fun apiSignOut(): TokenValue {
        return TokenValue()
    }

    @PostMapping("/sign-up")
    fun apiSignUp(@RequestBody signUpValue: SignUpValue): Map<String, String> {
        facade.userRepository.findTopByEmail(signUpValue.email)?.let {
            throw CustomException("User ${signUpValue.email} already exists")
        }
        facade.bankRepository.findTopByName("DEFAULT")?.let {
            val account = Account(name = signUpValue.name)
            account.bank = it
            facade.accountRepository.save(account)
            logger.debug("Account created: ${account.name}")
            val user = User(
                    email = signUpValue.email,
                    name = signUpValue.name,
                    pass = passwordEncoder.encode(signUpValue.password)
            )
            user.account = account
            facade.userRepository.save(user)
            logger.debug("User created: ${user.email}")
            return mapOf("status" to "ok")
        }
        throw CustomException("Sign-up failed")
    }

    @PostMapping("/update-password")
    fun apiUpdatePassword(
            @RequestBody updatePasswordValue: UpdatePasswordValue,
            @Autowired req: HttpServletRequest
    ): Map<String, String> {
        // either authenticate using request or jwt token
        logger.debug("Update password value: $updatePasswordValue")
        val username = if (updatePasswordValue.token.isNotEmpty()) {
            val claims = keyManager.verifyToken(updatePasswordValue.token)
            claims.body.subject
        } else {
            req.userPrincipal.name
        }
        username?.let {
            facade.userRepository.findTopByEmail(username)?.let { user ->
                if (updatePasswordValue.newPassword == updatePasswordValue.newPasswordConfirmation) {
                    user.pass = passwordEncoder.encode(updatePasswordValue.newPassword)
                    facade.userRepository.save(user)
                    return mapOf("status" to "ok")
                }
            }
        }
        throw CustomException("Update password failed")
    }

    @PostMapping("/reset-password")
    fun apiResetPassword(@RequestBody resetPasswordValue: ResetPasswordValue): String {
        facade.userRepository.findTopByEmail(resetPasswordValue.email)?.let {
            val token = keyManager.buildToken(it)  // TODO short expiry
            mailService.mailUpdatePasswordLink(it.email, token)
        }
        throw CustomException("User not found: ${resetPasswordValue.email}")
    }
}

