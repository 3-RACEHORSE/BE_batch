package com.meetplus.batch.infrastructure.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.meetplus.batch.domain.payment.QPayment.payment;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findAuctionUuidsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return queryFactory
            .select(payment.auctionUuid)
            .distinct()
            .from(payment)
            .where(payment.completionAt.between(startTime, endTime))
            .fetch();
    }

    @Override
    public BigDecimal getTotalAmountByAuctionUuid(String auctionUuid, LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal totalAmount = queryFactory
            .selectDistinct(payment.amountPaid.sum())
            .from(payment)
            .where(payment.auctionUuid.eq(auctionUuid).and(payment.completionAt.between(startTime, endTime)))
            .fetchOne();
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }
}
