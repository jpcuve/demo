package com.messio.demo

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalTime
import java.util.*

@Service
class SchedulerService @Autowired constructor(val publisher: ApplicationEventPublisher) {
    private val logger = LoggerFactory.getLogger(SchedulerService::class.java)
    private val events: SortedMap<LocalTime, BaseEvent> = TreeMap()

    fun enter(event: BaseEvent) {
        logger.debug("Entering ${event.name}")
        events[event.instant] = event
    }

    fun enterAbs(event: BaseEvent) {

    }

    fun run(blocking: Boolean = true){
        events.values.forEach {
            publisher.publishEvent(it)
        }
    }
}