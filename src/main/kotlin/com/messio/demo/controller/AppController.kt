package com.messio.demo.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
@RequestMapping("/app")
class AppController {
    private val fwd = ModelAndView("forward:index.html")

    @GetMapping
    fun apiIndex() = fwd
}
