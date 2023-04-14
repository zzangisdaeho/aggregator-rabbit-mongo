package com.example.aggregatormongo.inbound.rabbit;

import com.example.aggregatormongo.config.rabbit.AggregatorRabbitConfig;
import com.example.aggregatormongo.inbound.dto.AggregationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitInboundConsumer {

    private final ObjectMapper objectMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    @RabbitListener(queues = AggregatorRabbitConfig.QUEUE_NAME, id = "aggregationConsumer")
    public void receiveScheduleRegisterRequest(Message message, String payload) {
        try {
            Map<String, Object> headers = message.getMessageProperties().getHeaders();

            AggregationDto aggregationDto = AggregationDto.builder()
                    .transactionId((String) headers.get("transactionId"))
                    .total(convertToInt(headers.get("total")))
                    .index(convertToInt(headers.get("index")))
                    .data(payload)
                    .from(substringBeforeLast(message.getMessageProperties().getReceivedRoutingKey(), "."))
                    .build();

            applicationEventPublisher.publishEvent(aggregationDto);
        } catch (Exception e) {
            log.error("message handling fail. \nmessageProperties : {}, \nmessagePayload : {}", message.getMessageProperties(), payload, e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    public static String substringBeforeLast(String str, String delimiter) {
        int lastDelimiterIndex = str.lastIndexOf(delimiter);
        return lastDelimiterIndex != -1 ? str.substring(0, lastDelimiterIndex) : str;
    }

    private static int convertToInt(Object obj) {
        if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else {
            throw new IllegalArgumentException("Unsupported index type. Expected String or Long.");
        }
    }
}
