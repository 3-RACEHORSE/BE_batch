package com.meetplus.batch.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import com.meetplus.batch.kafka.dto.NewAuctionPostDto;
import com.meetplus.batch.schedule.BeforeEventStartSchedule;
import com.meetplus.batch.state.ScheduleTimeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumerCluster {

    private final KafkaProducerCluster producer;
    private final BeforeEventStartSchedule beforeEventStartSchedule;

    @KafkaListener(topics = Topics.Constant.AUCTION_POST_SERVICE, groupId = "${spring.kafka.consumer.group-id}")
    public void eventStart(@Payload LinkedHashMap<String, Object> message,
        @Headers MessageHeaders messageHeaders) {
        log.info("consumer: success >>> message: {}, headers: {}", message.toString(),
            messageHeaders);

        //message를 NewAuctionPostDto 변환
        NewAuctionPostDto newAuctionPostDto = NewAuctionPostDto.builder()
            .auctionUuid(message.get("auctionUuid").toString())
            .eventStartTime(LocalDateTime.parse(message.get("eventStartTime").toString()))
            .build();

        log.info("consumer: success >>> newAuctionPostDto: {}", newAuctionPostDto.toString());

        // 스케줄러 등록
        //Todo
        // 실제 테스트가 필요
        beforeEventStartSchedule.scheduleJob(newAuctionPostDto);
    }

}
