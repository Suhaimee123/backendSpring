package com.pimsmart.V1.config

import org.springframework.core.io.ByteArrayResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service


@Service
class MailService(private val mailSender: JavaMailSender) {

    fun sendEmailWithAttachment(to: String, subject: String, content: String, attachment: ByteArray?, fileName: String?) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(content, true) // ส่ง HTML content

        // แนบไฟล์ถ้า attachment และ fileName ไม่เป็น null
        if (attachment != null && fileName != null) {
            helper.addAttachment(fileName, ByteArrayResource(attachment))
        }

        mailSender.send(message)
        println("Email sent successfully to $to with attachment $fileName")
    }
}
