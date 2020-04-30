package com.messio.demo

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@CrossOrigin
class AuthenticationController {
    @PostMapping("/sign-in")
    fun apiSignIn(credentialsValue: CredentialsValue): User? {
        return null
    }
}