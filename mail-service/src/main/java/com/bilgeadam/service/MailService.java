package com.bilgeadam.service;

import com.bilgeadam.dto.response.ForgotPasswordMailResponseDto;
import com.bilgeadam.rabbitmq.model.RegisterMailModel;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    //private final SimpleMailMessage mailMessage;
    public void sendMail(RegisterMailModel model) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //mailMessage.setFrom("bertanjava7@gmail.com");
        mailMessage.setFrom("${spring.mail.username}");
        mailMessage.setTo(model.getEmail());
        mailMessage.setSubject("Aktivasyon Kodu");
        mailMessage.setText("Sayın, "+ model.getUsername()+ " üyelik şifreniz: "+model.getActivationCode());
        javaMailSender.send(mailMessage);
    }

    public Boolean sendMailForgotPassword(ForgotPasswordMailResponseDto dto) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            //mailMessage.setFrom("bertanjava7@gmail.com");
            mailMessage.setFrom("${spring.mail.username}");
            mailMessage.setTo(dto.getEmail());
            mailMessage.setSubject("Aktivasyon Kodu");
            mailMessage.setText("yeni şifreniz: "+dto.getPassword());
            javaMailSender.send(mailMessage);
        }catch (Exception e){
            e.getMessage();
        }
        return true;
    }
}
