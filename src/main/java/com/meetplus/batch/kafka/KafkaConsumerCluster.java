package com.meetplus.batch.kafka;

import com.meetplus.batch.kafka.Topics.Constant;
import com.meetplus.batch.kafka.dto.EventStartTimeDto;
import com.meetplus.batch.schedule.BeforeEventStartSchedule;
import java.util.LinkedHashMap;
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

    private final BeforeEventStartSchedule beforeEventStartSchedule;

    @KafkaListener(topics = Constant.EVENT_START_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void eventStart(@Payload LinkedHashMap<String, Object> message,
        @Headers MessageHeaders messageHeaders) {

        try {
            EventStartTimeDto eventStartTimeDto = EventStartTimeDto.builder()
                .auctionUuid(message.get("auctionUuid").toString())
                .eventStartTime((long) message.get("eventStartTime"))
                .build();
            beforeEventStartSchedule.scheduleJob(eventStartTimeDto);
        } catch (NullPointerException e) {
            log.error("Invalid value of message: {}", message);
        } catch (Exception e) {
            log.error("Error occurred while scheduling job: {}", e.getMessage());
        }
    }
}
