package com.meetplus.batch.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "total_settlement")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TotalDonationSettlement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "total_donation", nullable = false)
	private BigDecimal totalDonation;

	@Builder
	public TotalDonationSettlement(Long id, BigDecimal totalDonation) {
		this.id = id;
		this.totalDonation = totalDonation;
	}
}
