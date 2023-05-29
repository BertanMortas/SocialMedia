package com.bilgeadam.rabbitmq.consumer;

import com.bilgeadam.rabbitmq.model.RegisterMailModel;
import com.bilgeadam.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterMailConsumer {
    private final MailService mailService;
    //private final JavaMailSender javaMailSender;
    @RabbitListener(queues = ("${rabbitmq.registerMailQueue}"))
    public void sendMailFromRegisterMailQueue(RegisterMailModel model){
        mailService.sendMail(model);
    }
}
