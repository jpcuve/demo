package com.messio.demo.service

import com.messio.demo.APP_WEB_CONTEXT
import com.messio.demo.AppProperties
import com.samskivert.mustache.Mustache
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import java.io.InputStreamReader

@Service
class MailService(
        val javaMailSender: JavaMailSender,
        val compiler: Mustache.Compiler,
        val appProperties: AppProperties
) {
    private val logger = LoggerFactory.getLogger(MailService::class.java)

    @Async
    fun mailUpdatePasswordLink(to: String, token: String) {
        val file = ResourceUtils.getFile("classpath:email/reset-password.mustache")
        val source = InputStreamReader(file.inputStream(), Charsets.UTF_8)
        val template = compiler.compile(source)
        logger.debug("Template: $template")
        val resetPasswordUrl = "${appProperties.baseUrl}$APP_WEB_CONTEXT/update-password?$token"
        val s = template.execute(mapOf("reset-password-url" to resetPasswordUrl))
        logger.debug("Output: $s")
        val messagePreparator = MimeMessagePreparator { mimeMessage ->
            val helper = MimeMessageHelper(mimeMessage)
            helper.setFrom(appProperties.mailFrom)
            helper.setTo(to)
            helper.setSubject("Reset password")
            helper.setText(s, true)
        }
        javaMailSender.send(messagePreparator)
    }
}