package com.example.aggregatormongo.inbound.dto;

import com.example.aggregatormongo.message_storage.documents.Processing;
import lombok.Builder;
import lombok.Data;
import org.springframework.amqp.core.Message;

@Data
@Builder
public class AggregationDto {

    private String transactionId;

    private String from;

    private int total;

    private int index;

    private Object data;

    private Message message;

    public Processing toDocument(){
        return Processing.builder()
                .total(this.total)
                .transactionId(this.transactionId)
                .index(this.index)
                .data(this.data)
                .build();
    }

}
