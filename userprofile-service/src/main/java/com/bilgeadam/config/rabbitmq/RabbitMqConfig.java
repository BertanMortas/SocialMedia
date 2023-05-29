package com.bilgeadam.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    // register consumer queue consumer
    @Value("${rabbitmq.queueRegister}")
    String queueNameRegister;
    @Bean
    Queue registerQueue(){
        return new Queue(queueNameRegister);
    }

    // create post consumer
    private String queueCreatePost = "createPost";
    @Bean
    Queue queueCreatePost(){
        return new Queue(queueCreatePost);
    }

    // resiter elasticsearch producer
    @Value("${rabbitmq.exchange}")
    private String exchange;
    @Value("${rabbitmq.queueElasticRegister}")
    private String queueElasticRegister;
    @Value("${rabbitmq.elasticRegisterBindingKey}")
    private String elasticRegisterBindingKey;
    @Bean
    Queue queueElasticRegister(){
        return new Queue(queueElasticRegister);
    }
    @Bean
    DirectExchange exchange(){
        return new DirectExchange(exchange);
    }
    @Bean
    public Binding bindingElasticRegister(final Queue queueElasticRegister,final DirectExchange exchange){
        return BindingBuilder.bind(queueElasticRegister).to(exchange).with(elasticRegisterBindingKey);
    }

}
