package com.meetplus.batch.kafka.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class EventPreviewDto {
    private String auctionUuid;
}
