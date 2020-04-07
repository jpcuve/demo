package com.messio.demo

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SchedulerService {
    private val logger = LoggerFactory.getLogger(SchedulerService::class.java)

    fun enter(event: BaseEvent) {
        logger.debug("Entering ${event.name}")
    }

    fun enterAbs(event: BaseEvent) {

    }

    fun run(blocking: Boolean = true){

    }
}