package com.projects.cavany.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @PostMapping("/send-meal-plan")
    public ResponseEntity<?> sendMealPlanEmail(@RequestBody Map<String, Object> request) {
        String to = (String) request.get("email");
        
        Object mealPlanObj = request.get("mealPlan");
        if (!(mealPlanObj instanceof Map)) {
            return ResponseEntity.badRequest().body("Invalid meal plan format");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> mealPlan = (Map<String, Object>) mealPlanObj;

        if (to == null || mealPlan == null) {
            return ResponseEntity.badRequest().body("Email and meal plan are required");
        }

        try {
            sendEmail(to, "Your Meal Plan", mealPlan);
            return ResponseEntity.ok("Meal plan email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to send email: " + e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, Map<String, Object> templateModel) throws MessagingException {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = templateEngine.process("emailTemplate.html", thymeleafContext);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true indicates HTML content
        helper.setFrom("orders@cavanyfoods.com");

        mailSender.send(message);
    }
}
