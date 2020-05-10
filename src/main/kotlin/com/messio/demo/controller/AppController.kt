package com.messio.demo.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

const val SINGLE_PAGE_BUILD_CONTEXT = "/dummy"
const val CONTEXT_LENGTH = SINGLE_PAGE_BUILD_CONTEXT.length

@Controller
@RequestMapping(SINGLE_PAGE_BUILD_CONTEXT)
class AppController {
    private val logger: Logger = LoggerFactory.getLogger(AppController::class.java)

    @GetMapping("/**/{path:^[^.]+\$}")  // anything that does not contain a dot is a route
    fun apiForwardRoute(@PathVariable path: String): String {
        logger.debug("Forward route: $path")
        return "forward:/index.html"
    }

    @GetMapping("/**/{path:.*\\..*}")  // anything else is a resource
    fun apiForwardResource(req: HttpServletRequest): String {
        logger.debug("Forward resource: ${req.requestURI}")
        return "forward:/${req.requestURI.substring(CONTEXT_LENGTH)}"
    }
}