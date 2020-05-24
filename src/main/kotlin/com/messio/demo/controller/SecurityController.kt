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
        facade.userRepository.findTopByFirebaseUid(firebaseSignInValue.uid)?.let {
            return TokenValue(keyManager.buildToken(it))
        }
        var user: User? = null
        if (!firebaseSignInValue.isAnonymous && firebaseSignInValue.email != null) {
            user = facade.userRepository.findTopByEmail(firebaseSignInValue.email)
        }
        if (user == null){
            user = User(
                    firebaseUid = firebaseSignInValue.uid,
                    anonymous = firebaseSignInValue.isAnonymous,
                    email = firebaseSignInValue.email ?: firebaseSignInValue.uid)
            user.account = facade.accountRepository.findById(1).orElse(null)
        }
        user.firebaseUid = firebaseSignInValue.uid
        user.displayName = firebaseSignInValue.displayName
        user = facade.userRepository.save(user)
        logger.debug("Login successful for: ${user.email}")
        val token = keyManager.buildToken(user)
        logger.debug("Token: $token")
        return TokenValue(token)
    }

    @GetMapping("/sign-out")
    fun apiSignOut(): TokenValue {
        return TokenValue()
    }
}

