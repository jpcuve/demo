package com.messio.demo

data class SignInValue(val email: String = "", val password: String = "")
data class SocialSignInValue(val social: String = "", val identity: String = "")
data class GoogleSignInValue(val token: String = "")
data class SignUpValue(val email: String = "", val password: String = "", val name: String = "")
data class UpdatePasswordValue(val newPassword: String = "", val newPasswordConfirmation: String = "", val token: String = "")
data class ResetPasswordValue(val email: String = "")
data class ProfileValue(val identified: Boolean = false, val token: String = "", val name: String = "", val roles: List<String> = listOf())
data class TokenValue(val token: String = "")