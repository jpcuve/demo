package com.messio.demo.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping




// @Controller
// @RequestMapping("/app")
class AppController {
    @RequestMapping("/{path:[^\\.]+}/**")
    fun forward(): String {
        return "forward:/app/index.html"
    }
}