package com.meetplus.batch.kafka.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SendToChatDto {
    private String auctionUuid;
    private List<String> memberUuids;

    @Builder
    public SendToChatDto(String auctionUuid, List<String> memberUuids) {
        this.auctionUuid = auctionUuid;
        this.memberUuids = memberUuids;
    }
}