//package com.meetplus.batch.kafka;
//
//import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.LinkedHashMap;
//
//import com.meetplus.batch.domain.batch.BeforeEventStart;
//import com.meetplus.batch.infrastructure.batch.BeforeEventStartRepository;
//import com.meetplus.batch.kafka.dto.NewAuctionPostDto;
//import com.meetplus.batch.schedule.BeforeEventStartSchedule;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.messaging.handler.annotation.Headers;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class KafkaConsumerCluster {
//    private final BeforeEventStartSchedule beforeEventStartSchedule;
//    private final BeforeEventStartRepository beforeEventStartRepository;
//
//    @KafkaListener(topics = Topics.Constant.AUCTION_POST_SERVICE, groupId = "${spring.kafka.consumer.group-id}")
//    public void eventStart(@Payload LinkedHashMap<String, Object> message,
//                           @Headers MessageHeaders messageHeaders) {
//        log.info("consumer: success >>> message: {}, headers: {}", message.toString(),
//                messageHeaders);
//
//        // 시간 형식 지정
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//        OffsetDateTime offsetDateTime = OffsetDateTime.parse(message.get("eventStartTime").toString(), formatter);
//        LocalDateTime eventStartTime = offsetDateTime.toLocalDateTime();
//
//        //message를 NewAuctionPostDto 변환
//        NewAuctionPostDto newAuctionPostDto = NewAuctionPostDto.builder()
//                .auctionUuid(message.get("auctionUuid").toString())
//                .eventStartTime(eventStartTime)
//                .build();
//
//        log.info("consumer: success >>> newAuctionPostDto: {}", newAuctionPostDto.toString());
//
//        // DB 저장
//        saveData(newAuctionPostDto);
//
//        // 스케줄러 등록
//        beforeEventStartSchedule.scheduleJob(newAuctionPostDto);
//    }
//
//    private void saveData(NewAuctionPostDto newAuctionPostDto) {
//        try {
//            beforeEventStartRepository.save(BeforeEventStart.builder()
//                    .auctionUuid(newAuctionPostDto.getAuctionUuid())
//
//                    // builder 로직에 eventStartTime을 넣으면 executionTime으로 변환 로직이 있음
//                    .eventStartTime(newAuctionPostDto.getEventStartTime())
//                    .build());
//        } catch (Exception e) {
//            log.warn("Before Event Start Data Save Fail");
//        }
//    }
//}
