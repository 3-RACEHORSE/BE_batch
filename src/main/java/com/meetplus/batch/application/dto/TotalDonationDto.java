package com.meetplus.batch.application.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalDonationDto {

    private BigDecimal totalDonation;

    @Builder
    public TotalDonationDto(BigDecimal totalDonation) {
        this.totalDonation = totalDonation;
    }
}
