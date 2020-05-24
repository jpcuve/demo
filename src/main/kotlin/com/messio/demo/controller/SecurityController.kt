package com.messio.demo.controller

import com.messio.demo.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(SECURITY_WEB_CONTEXT)
@CrossOrigin(allowCredentials = "true")
class SecurityController(
        val facade: Facade,
        val keyManager: KeyManager
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

    @PostMapping("/firebase-sign-in")
    fun apiFirebaseSignIn(@RequestBody firebaseSignInValue: FirebaseSignInValue): TokenValue {
        if (firebaseSignInValue.isAnonymous || firebaseSignInValue.email == null) {
            facade.userRepository.findTopByAnonymous(true)
        } else {
            facade.userRepository.findTopByEmail(firebaseSignInValue.email)
        }?.let {
            logger.debug("Login successful for: ${it.email}")
            val token = keyManager.buildToken(it)
            logger.debug("Token: $token")
            return TokenValue(token)
        }
        throw CustomException("Invalid sign-in")
    }

    @GetMapping("/sign-out")
    fun apiSignOut(): TokenValue {
        return TokenValue()
    }
}

