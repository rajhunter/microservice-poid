package com.devtechi.dev.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String toEmail, String orderNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Order Confirmation - " + orderNumber);
        message.setText("Thank you for placing your order. Your order number is: " + orderNumber);
        message.setFrom("your_email@gmail.com");

        mailSender.send(message);
        log.info("Email  Notification sent form Order service emailId - {}",orderNumber);

    }
}