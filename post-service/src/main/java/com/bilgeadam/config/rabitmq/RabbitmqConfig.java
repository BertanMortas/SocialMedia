package com.bilgeadam.config.rabitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    private String exchange = "exchange";
    private String queueCreatePost = "createPost";
    private String createPostBindingKey = "bindingKey";
    @Bean
    Queue queueCreatePost(){
        return new Queue(queueCreatePost);
    }
    @Bean
    DirectExchange exchange(){
        return new DirectExchange(exchange);
    }
    @Bean
    Binding bindingCreatePost(final Queue queueCreatePost, final DirectExchange exchange){
        return BindingBuilder.bind(queueCreatePost).to(exchange).with(createPostBindingKey);
    }
}
