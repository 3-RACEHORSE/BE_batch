package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.application.dto.AuctionTotalAmountDto;
import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepositoryCustom {

	List<AuctionTotalAmountDto> getAuctionTotalAmountsByDateRange(LocalDateTime startTime,
		LocalDateTime endTime);

	List<String> getMemberUuidsByAuctionUuidAndPaymentStatus(String auctionUuid,
		PaymentStatus status);

	List<Payment> getPaymentsByPaymentStatusAndDateRange(
		PaymentStatus paymentStatus, LocalDateTime startTime, LocalDateTime endTime);

	List<String> getAuctionUuidsByDateRange(LocalDateTime localDateTime,
		LocalDateTime localDateTime1);
}
