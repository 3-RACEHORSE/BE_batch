package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.common.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepositoryCustom {

    List<String> getAuctionUuidsByDateRange(LocalDateTime startTime, LocalDateTime endTime);

    BigDecimal getTotalAmountByAuctionUuid(String auctionUuid, LocalDateTime startTime, LocalDateTime endTime);

    List<String> getMemberUuidsByAuctionUuidAndPaymentStatus(String auctionUuid, PaymentStatus status);
}
