package com.messio.demo

data class SignInValue(val email: String = "", val password: String = "")
data class GoogleSignInValue(val token: String = "")
data class SignUpValue(val email: String = "", val password: String = "", val name: String = "")
data class UpdatePasswordValue(val newPassword: String = "", val newPasswordConfirmation: String = "", val token: String = "")
data class ResetPasswordValue(val email: String = "")
data class UserValue(val email: String = "")