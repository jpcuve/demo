package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/auth")
@CrossOrigin
class AuthenticationController(val facade: Facade) {
    private val logger: Logger = LoggerFactory.getLogger(AuthenticationController::class.java)

    @PostMapping("/sign-in")
    fun apiSignIn(@RequestBody signInValue: SignInValue, @Autowired req: HttpServletRequest): UserValue {
        try {
            req.login(signInValue.email, signInValue.password)
            val user: User = facade.userRepository.findTopByEmail(signInValue.email) ?: User()
            return UserValue(user.email)
        } catch (e: ServletException){
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