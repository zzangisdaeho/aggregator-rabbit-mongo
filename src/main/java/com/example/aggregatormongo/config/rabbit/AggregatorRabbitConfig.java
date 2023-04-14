package com.example.aggregatormongo.config.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AggregatorRabbitConfig {

    public static final String EXCHANGE_TOPIC_NAME = "aggregation.exchange-topic.v0";

    public static final String QUEUE_NAME = "aggregation.queue.v0";

    public static final String BINDING_KEY = "#.process";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_TOPIC_NAME);
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .deadLetterExchange("dlx.exchange-fanout.dlx.v0")
                .deadLetterRoutingKey("")
                .build();
    }

    @Bean
    public Binding bindingToTopic (Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(BINDING_KEY);
    }

}
