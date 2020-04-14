package com.messio.demo

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppProperties {
    @Value("\${app.default-bank}")
    lateinit var defaultBank: String

}