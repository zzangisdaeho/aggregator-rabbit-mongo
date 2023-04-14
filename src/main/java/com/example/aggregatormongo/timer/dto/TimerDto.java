package com.example.aggregatormongo.timer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimerDto implements Serializable {

    private String transactionId;
}
