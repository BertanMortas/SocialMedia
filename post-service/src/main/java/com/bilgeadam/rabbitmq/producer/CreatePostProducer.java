package com.bilgeadam.rabbitmq.producer;

import com.bilgeadam.rabbitmq.model.AuthIdModel;
import com.bilgeadam.rabbitmq.model.GetUserprofileModel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePostProducer {
    private String exchange = "exchange";
    private String createPostBindingKey = "bindingKey";
    private final RabbitTemplate rabbitTemplate;
    public Object createPost(AuthIdModel model){
        /**
         bi ara alttakini dene denerken obje değişip GetUserprofileModel olarak ayarlanacak
         ayrıca rabit configler post ve user için
        @Bean
        public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
            final var rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(jsonMessageConverter());
            return rabbitTemplate;
        }

        @Bean
        public Jackson2JsonMessageConverter jsonMessageConverter() {
            return new Jackson2JsonMessageConverter();
        }
        bunlar eklenecek
        return rabbitTemplate.convertSendAndReceiveAsType(exchange,createPostBindingKey,model,new ParameterizedTypeReference<GetUserprofileModel>(){});
        */
        return rabbitTemplate.convertSendAndReceive(exchange,createPostBindingKey,model);
    }
}
