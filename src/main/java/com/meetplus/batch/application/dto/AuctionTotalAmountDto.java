package com.meetplus.batch.application.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionTotalAmountDto {

    private String auctionUuid;
    private BigDecimal totalAmount;

    @Builder
    public AuctionTotalAmountDto(String auctionUuid, BigDecimal totalAmount) {
        this.auctionUuid = auctionUuid;
        this.totalAmount = totalAmount;
    }
}

