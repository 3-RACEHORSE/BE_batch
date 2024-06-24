package com.meetplus.batch.infrastructure.payment;

import static com.meetplus.batch.domain.payment.QPayment.payment;

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

@Repository
public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory paymentQueryFactory;

    public PaymentRepositoryCustomImpl(
        @Qualifier("paymentJpaQueryFactory") JPAQueryFactory paymentQueryFactory) {
        this.paymentQueryFactory = paymentQueryFactory;
    }

    @Override
    public List<AuctionTotalAmountDto> getAuctionTotalAmountsByDateRange(
        LocalDateTime startTime,
        LocalDateTime endTime) {
        return paymentQueryFactory
            .select(Projections.constructor(
                AuctionTotalAmountDto.class,
                payment.auctionUuid,
                payment.amountPaid.sum().coalesce(BigDecimal.ZERO) // 합계가 null인 경우 0으로 대체
            ))
            .from(payment)
            .where(payment.completionAt.between(startTime, endTime))
            .groupBy(payment.auctionUuid)
            .fetch();
    }


    @Override
    public List<Payment> getPaymentsByPaymentStatusAndDateRange(
        PaymentStatus paymentStatus, LocalDateTime startTime, LocalDateTime endTime) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(payment.paymentStatus.eq(paymentStatus));

        if (startTime != null && endTime != null) {
            builder.and(payment.updatedAt.between(startTime, endTime));
        } else if (startTime != null) {
            builder.and(payment.updatedAt.goe(startTime));
        } else if (endTime != null) {
            builder.and(payment.updatedAt.loe(endTime));
        }
        return paymentQueryFactory
            .select(payment)
            .from(payment)
            .where(builder)
            .fetch();
    }
}
