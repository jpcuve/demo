package com.messio.demo

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SchedulerService {
    val logger = LoggerFactory.getLogger(SchedulerService::class.java)

    init {
        logger.debug("Scheduler service initializing")
    }

    fun enter(event: BaseEvent) {

    }

    fun enterAbs(event: BaseEvent) {

    }
}