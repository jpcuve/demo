package com.messio.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/master")
class MasterController {
    @GetMapping("/default")
    fun apiDefault() = "OK"
}