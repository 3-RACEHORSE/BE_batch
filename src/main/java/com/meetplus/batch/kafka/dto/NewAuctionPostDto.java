package com.meetplus.batch.kafka.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class NewAuctionPostDto {
    private String auctionUuid;
    private LocalDateTime eventStartTime;
}
