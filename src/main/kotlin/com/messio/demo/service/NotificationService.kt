package com.messio.demo.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.messio.demo.Facade
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class NotificationService(
        val facade: Facade,
        val firebaseMessaging: FirebaseMessaging
) {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    @Async
    fun sendNotification(email: String) {
        logger.info("Sending notification to: $email")
        facade.userRepository.findTopByEmail(email)?.let {
            logger.info("User found: $email")
            val registrationToken = it.messagingToken
            registrationToken?.let {
                logger.info("Registration token: $registrationToken")
                val message = Message.builder()
                        .putData("score", "850")
                        .putData("time", "2:45")
                        .setToken(registrationToken)
                        .build()
                val response = firebaseMessaging.send(message)
                logger.info("Succesfully sent message: $response")
            }
        }
    }
}