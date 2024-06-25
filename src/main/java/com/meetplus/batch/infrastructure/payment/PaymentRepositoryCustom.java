package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.application.dto.AuctionTotalAmountDto;
import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

public interface PaymentRepositoryCustom {

    List<AuctionTotalAmountDto> getAuctionTotalAmountsByDateRange(LocalDateTime startTime,
        LocalDateTime endTime);

    List<String> getMemberUuidsByAuctionUuidAndPaymentStatus(String auctionUuid, PaymentStatus status);
    List<Payment> getPaymentsByPaymentStatusAndDateRange(
        PaymentStatus paymentStatus, LocalDateTime startTime, LocalDateTime endTime);
    List<String> getAuctionUuidsByDateRange(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
