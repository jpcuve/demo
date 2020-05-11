package com.messio.demo

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppProperties {
    @Value("\${app.default-bank}")
    lateinit var defaultBank: String
    @Value("\${app.google}")
    lateinit var google: String
    @Value("\${app.secret-key}")
    lateinit var secretKey: String
    @Value("\${app.mail-from}")
    lateinit var mailFrom: String
    @Value("\${app.base-url}")
    lateinit var baseUrl: String
}