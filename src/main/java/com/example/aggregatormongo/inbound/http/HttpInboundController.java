package com.example.aggregatormongo.inbound.http;

import com.example.aggregatormongo.inbound.dto.AggregationDto;
import com.example.aggregatormongo.mapping.AggregationMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HttpInboundController {

    private final AggregationMapping aggregationMapping;


    @PostMapping("aggregation")
    public void aggregate(@RequestBody AggregationDto aggregationDto){

    }

    @GetMapping("test")
    public void test(){
        System.out.println("aggregationMapping = " + aggregationMapping);
    }

}
