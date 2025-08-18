package com.devtechi.dev;

import com.devtechi.dev.config.EmailConfig;
import com.devtechi.dev.model.OrderPlaceEvent;
import com.devtechi.dev.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {
    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailConfig emailConfig;


    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics="notificationTopic")
    public void notificationHandler(OrderPlaceEvent orderPlaceEvent){
        log.info("Received Notification form Order service emailId - {}",emailConfig.getEmail());

        log.info("Received Notification form Order service - {}",orderPlaceEvent.getOrderNumber());
        // send email notification code
        emailService.sendOrderConfirmation(emailConfig.getEmail(), orderPlaceEvent.getOrderNumber());
        System.out.println("✅ Email sent to " + emailConfig.getEmail());
    }
}
