package com.bilgeadam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

@SpringBootApplication
public class MailServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailServiceApplication.class);
    }
   /* private final JavaMailSender javaMailSender;
    public MailServiceApplication(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    @EventListener(ApplicationReadyEvent.class)
    public void sendMail(){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("bertanjava7@gmail.com");
        mailMessage.setTo("bertanmortas@gmail.com");
        mailMessage.setSubject("Aktivasyon Kodu");
        mailMessage.setText(UUID.randomUUID().toString());
        javaMailSender.send(mailMessage);
    }*/
}