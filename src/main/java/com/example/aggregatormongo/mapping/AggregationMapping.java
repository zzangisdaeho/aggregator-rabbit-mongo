package com.example.aggregatormongo.mapping;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({AggregationMapping.class})
@ConfigurationProperties("aggregation")
@Getter
public class AggregationMapping {

    Map<String, MappingDetailInfo> mapping = new HashMap<>();

    @Getter
    public static class MappingDetailInfo {

        private List<String> outputs;

        private Duration waitTime;

        private OutType outType;

        public MappingDetailInfo(List<String> outputs, Duration waitTime, OutType outType) {
            this.outputs = outputs;
            this.waitTime = waitTime;
            this.outType = outType;
        }
    }

    public enum OutType {
        HTTP, RABBIT
    }
}
