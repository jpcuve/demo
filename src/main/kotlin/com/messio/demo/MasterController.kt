package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/master")
class MasterController(val appProperties: AppProperties) {
    private val logger: Logger = LoggerFactory.getLogger(MasterController::class.java)

    init {
        logger.debug("App prop: ${appProperties.defaultBank}")
    }

    @GetMapping("/default")
    fun apiDefault() = "OK"
}