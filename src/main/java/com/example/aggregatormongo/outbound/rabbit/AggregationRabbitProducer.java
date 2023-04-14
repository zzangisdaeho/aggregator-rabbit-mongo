package com.example.aggregatormongo.outbound.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationRabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    public void dispatch(String exchangeName, String routingKey, Object payload) {
        try {
            Message message = MessageBuilder
                    .withBody(objectMapper.writeValueAsString(payload).getBytes())
                    .andProperties(MessagePropertiesBuilder.newInstance().setTimestamp(new Date()).build())
                    .build();

            rabbitTemplate.send(exchangeName, routingKey, message);
        } catch (JsonProcessingException e) {
            log.error("produce message to exchange {}, routingKey {} fail", exchangeName, routingKey, e);
            throw new RuntimeException(e);
        }
    }
}
