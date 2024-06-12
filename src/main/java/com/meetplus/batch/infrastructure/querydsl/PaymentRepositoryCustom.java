package com.meetplus.batch.infrastructure.querydsl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepositoryCustom {

    List<String> findAuctionUuidsByDateRange(LocalDateTime startTime, LocalDateTime endTime);

    BigDecimal getTotalAmountByAuctionUuid(String auctionUuid, LocalDateTime startTime, LocalDateTime endTime);
}
