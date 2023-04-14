package com.example.aggregatormongo.timer.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;
import java.util.Date;

public class TimerTask implements Runnable, Serializable {

    private Object dto;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public TimerTask() {
    }

    public TimerTask(Object dto) {
        this.dto = dto;
    }

    @Override
    public void run() {
        System.out.println("timer task executed at " + new Date());
        applicationEventPublisher.publishEvent(dto);
    }

}
