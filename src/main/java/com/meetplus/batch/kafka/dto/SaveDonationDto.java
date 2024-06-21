package com.meetplus.batch.kafka.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SaveDonationDto {
	private String auctionUuid;
	private BigDecimal donation;

	@Builder
	public SaveDonationDto(String auctionUuid, BigDecimal donation) {
		this.auctionUuid = auctionUuid;
		this.donation = donation;
	}
}
