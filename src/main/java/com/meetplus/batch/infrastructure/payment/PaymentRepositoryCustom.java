package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepositoryCustom {

    List<String> getAuctionUuidsByDateRange(LocalDateTime startTime, LocalDateTime endTime);

    BigDecimal getTotalAmountByAuctionUuid(String auctionUuid, LocalDateTime startTime, LocalDateTime endTime);

    List<Payment> getPaymentsByPaymentStatusAndBetweenStartTimeAndEndTime(PaymentStatus paymentStatus, LocalDateTime startTime, LocalDateTime endTime);
}
