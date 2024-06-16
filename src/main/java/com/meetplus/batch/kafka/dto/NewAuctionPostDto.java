package com.meetplus.batch.kafka.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class NewAuctionPostDto {
    private String auctionUuid;
    private LocalDateTime eventStartTime;

    @Builder
    public NewAuctionPostDto(String auctionUuid, LocalDateTime eventStartTime) {
        this.auctionUuid = auctionUuid;
        this.eventStartTime = eventStartTime;
    }
}
