package com.messio.demo

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@CrossOrigin
class AuthenticationController(val facade: Facade) {
    @PostMapping("/sign-in")
    fun apiSignIn(@RequestBody signInValue: SignInValue): UserValue {
        val user: User = facade.userRepository.findTopByEmail(signInValue.email) ?: User()
        return UserValue(user.email)
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