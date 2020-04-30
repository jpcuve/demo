package com.messio.demo

open class ResetPasswordValue(open var email: String?)
class SignInValue(override var email: String?, var password: String?): ResetPasswordValue(email)