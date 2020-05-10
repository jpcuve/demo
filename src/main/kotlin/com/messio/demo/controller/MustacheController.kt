package com.messio.demo.controller

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.Writer

@Controller
@RequestMapping("/mustache")
class MustacheController {
    @GetMapping
    fun apiIndex(model: Model): String {
        return "index"
    }
}
