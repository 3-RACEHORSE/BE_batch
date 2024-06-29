package com.meetplus.batch.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalDonationDto {

	private BigDecimal totalDonation;
	private LocalDateTime lastSettlementDate;

	public TotalDonationDto(BigDecimal totalDonation) {
		this.totalDonation = totalDonation;
	}

	@Builder
	public TotalDonationDto(BigDecimal totalDonation, LocalDateTime lastSettlementDate) {
		this.totalDonation = totalDonation;
		this.lastSettlementDate = lastSettlementDate;
	}
}
