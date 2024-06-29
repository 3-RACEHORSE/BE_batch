package com.meetplus.batch.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "total_settlement")
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TotalDonationSettlement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "total_settlement_id")
	private Long id;
	@Column(name = "total_donation", nullable = false)
	private BigDecimal totalDonation;
	@Column(name = "last_settlement_date", nullable = false)
	private LocalDateTime lastSettlementDate;

	@Builder
	public TotalDonationSettlement(Long id, BigDecimal totalDonation, LocalDateTime lastSettlementDate) {
		this.id = id;
		this.totalDonation = totalDonation;
		this.lastSettlementDate = lastSettlementDate;
	}
}
