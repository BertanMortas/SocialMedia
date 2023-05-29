package com.bilgeadam.rabbitmq.consumer;

import com.bilgeadam.rabbitmq.model.RegisterElasticModel;
import com.bilgeadam.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j // konsolda loglama işlemleri için kullanılır (simple logging facede for java)
public class CreateUserConsumer {
    private final UserProfileService userProfileService;
    @RabbitListener(queues = ("${rabbitmq.queueElasticRegister}"))
    public void createNewUser(RegisterElasticModel userModel){
        log.info("User {}", userModel.toString());
        userProfileService.createUserRabbitMq(userModel);
    }
}
