package com.meetplus.batch.infrastructure.payment;

import static com.meetplus.batch.domain.payment.QPayment.payment;

import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
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
    public List<String> getAuctionUuidsByDateRange(
        LocalDateTime startTime,
        LocalDateTime endTime) {
        return paymentQueryFactory
            .select(payment.auctionUuid)
            .distinct()
            .from(payment)
            .where(payment.completionAt.between(startTime, endTime))
            .fetch();
    }

    @Override
    public BigDecimal getTotalAmountByAuctionUuid(String auctionUuid, LocalDateTime startTime,
        LocalDateTime endTime) {
        BigDecimal totalAmount = paymentQueryFactory
            .selectDistinct(payment.amountPaid.sum())
            .from(payment)
            .where(payment.auctionUuid.eq(auctionUuid)
                .and(payment.completionAt.between(startTime, endTime)))
            .fetchOne();
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    @Override
    public List<Payment> getPaymentsByPaymentStatusAndBetweenStartTimeAndEndTime(
        PaymentStatus paymentStatus, LocalDateTime startTime, LocalDateTime endTime) {
        return paymentQueryFactory
            .select(payment)
            .from(payment)
            .where(payment.paymentStatus.eq(paymentStatus)
                .and(payment.createdAt.between(startTime, endTime)))
            .fetch();
    }
}
