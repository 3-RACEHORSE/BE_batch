package com.meetplus.batch.infrastructure.payment;

import static com.meetplus.batch.domain.payment.QBank.bank;

import com.meetplus.batch.application.dto.TotalDonationDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class BankRepositoryCustomImpl implements BankRepositoryCustom {

	private final JPAQueryFactory bankQueryFactory;

	public BankRepositoryCustomImpl(
		@Qualifier("bankJpaQueryFactory") JPAQueryFactory bankQueryFactory) {
		this.bankQueryFactory = bankQueryFactory;
	}

	@Override
	public TotalDonationDto getBankTotalAmountsAfter(LocalDateTime lastSettlementTime) {

		if (lastSettlementTime == null) {
			lastSettlementTime = LocalDateTime.of(1970, 1, 1, 0, 0);
		}

		return bankQueryFactory
			.select(Projections.constructor(
				TotalDonationDto.class,
				bank.donation.sum().coalesce(BigDecimal.ZERO), // 합계가 null인 경우 0으로 대체
				bank.createdAt.max()
			))
			.from(bank)
			.where(bank.createdAt.after(lastSettlementTime))
			.fetchOne();
	}
}
