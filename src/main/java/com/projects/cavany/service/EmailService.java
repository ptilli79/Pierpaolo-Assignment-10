package com.projects.cavany.service;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendThymeleafEmail(
            String to,
            String subject,
            Map<String, Object> templateModel,
            File[] attachments) throws MessagingException {
        try {
            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);
            String htmlBody = templateEngine.process("emailTemplate.html", thymeleafContext);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (attachments != null) {
                for (File attachment : attachments) {
                    FileSystemResource file = new FileSystemResource(attachment);
                    helper.addAttachment(file.getFilename(), file);
                }
            }

            helper.setFrom("orders@cavanyfoods.com");
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (MailException e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new MessagingException("Failed to send email", e);
        }
    }
}

