package com.example.aggregatormongo.service;

import com.example.aggregatormongo.inbound.dto.AggregationDto;
import com.example.aggregatormongo.mapping.AggregationMapping;
import com.example.aggregatormongo.message_storage.documents.FailLog;
import com.example.aggregatormongo.message_storage.documents.Processing;
import com.example.aggregatormongo.message_storage.documents.SuccessLog;
import com.example.aggregatormongo.message_storage.repository.FailLogRepository;
import com.example.aggregatormongo.message_storage.repository.ProcessingRepository;
import com.example.aggregatormongo.message_storage.repository.SuccessLogRepository;
import com.example.aggregatormongo.outbound.http.HttpOutbound;
import com.example.aggregatormongo.outbound.rabbit.AggregationRabbitProducer;
import com.example.aggregatormongo.timer.dto.TimerDto;
import com.example.aggregatormongo.timer.job.TimerTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScheduledExecutorService;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregationService {

    private final AggregationMapping aggregationMapping;
    private final RScheduledExecutorService rScheduledExecutorService;
    private final AggregationRabbitProducer aggregationRabbitProducer;
    private final ProcessingRepository processingRepository;
    private final SuccessLogRepository successLogRepository;
    private final FailLogRepository failLogRepository;

    private final HttpOutbound httpOutbound;

    @EventListener
    @Transactional
    public void processAggregation(AggregationDto aggregationDto){
        List<Processing> findList = processingRepository.findAllByTransactionId(aggregationDto.getTransactionId());

        //Aggregation에 처음 도착한거라면 timer 등록
        if(findList.isEmpty()){
            registerTimer(aggregationDto);
        }

        //일단 온 메세지 저장
        findList.add(processingRepository.save(aggregationDto.toDocument()));
        log.info("transactionId {}, index {} is collected", aggregationDto.getTransactionId(), aggregationDto.getIndex());

        //total 집계 완료시 다음 진행
        // 다음 큐로 메세지 전송
        // processingList -> success로 이관
        if(findList.size() == aggregationDto.getTotal()){
            log.info("transactionId {} aggregation completed. totalMessage : {}", aggregationDto.getTransactionId(), aggregationDto.getTotal());
            SuccessLog successLog = SuccessLog.fromProcessing(findList);
            successLogRepository.save(successLog);
            processingRepository.deleteAll(findList);
            sendToNext(aggregationDto, successLog);
        }
    }

    private void sendToNext(AggregationDto aggregationDto, SuccessLog successLog) {
        AggregationMapping.MappingDetailInfo mappingDetailInfo = aggregationMapping.getMapping().get(aggregationDto.getFrom());
        List<String> outputs = mappingDetailInfo.getOutputs();

        outputs.forEach(s -> {
            if(mappingDetailInfo.getOutType() == AggregationMapping.OutType.RABBIT){
                aggregationRabbitProducer.dispatch("", s, successLog);
            } else if (mappingDetailInfo.getOutType() == AggregationMapping.OutType.HTTP) {
                httpOutbound.dispatch(HttpMethod.POST, s, successLog);
            }
        });
    }

    private void registerTimer(AggregationDto aggregationDto) {
        Duration waitTime = aggregationMapping.getMapping().get(aggregationDto.getFrom()).getWaitTime();
        if (waitTime != null) {
            rScheduledExecutorService.schedule(
                    new TimerTask(new TimerDto(aggregationDto.getTransactionId())),
                    waitTime.getSeconds(),
                    TimeUnit.SECONDS
            );
        }
    }

    @EventListener
    @Transactional
    public void processAggregation(TimerDto timerDto){
        String transactionId = timerDto.getTransactionId();

        Optional<SuccessLog> findSuccessLogOptional = successLogRepository.findByTransactionId(transactionId);

        //성공 기록이 없을경우 실패로 간주해서 다음 진행
        //processingList -> fail로 이관
        if(findSuccessLogOptional.isEmpty()){
            log.error("aggregation fail {}", transactionId);
            List<Processing> findList = processingRepository.findAllByTransactionId(transactionId);

            FailLog failLog = FailLog.fromProcessing(findList);

            failLogRepository.save(failLog);
            processingRepository.deleteAll(findList);
        }
    }
}
