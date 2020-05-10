package com.messio.demo.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/app")
class AppController {
    private val logger: Logger = LoggerFactory.getLogger(AppController::class.java)

    @RequestMapping(value = ["{path:.+}"])
    fun redirect(@PathVariable path: String): String {
        logger.debug("Mapped to app redirect: $path")
        return "forward:/"
    }

    private val fwd = ModelAndView("forward:index.html")

/*
    @GetMapping
    fun apiIndex() = fwd

    @GetMapping("/static/css/{ext}")
    fun apiCssResources(@PathVariable ext: String): ModelAndView {
        logger.debug("Ext: $ext")
        return ModelAndView("forward:/static/css/$ext")
    }

    @GetMapping("/static/js/{ext}")
    fun apiJsResources(@PathVariable ext: String): ModelAndView {
        logger.debug("Ext: $ext")
        return ModelAndView("forward:/static/js/$ext")
    }
*/
}
