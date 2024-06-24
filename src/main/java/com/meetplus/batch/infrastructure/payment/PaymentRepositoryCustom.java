package com.meetplus.batch.infrastructure.payment;

<<<<<<< HEAD
import com.meetplus.batch.common.PaymentStatus;
import java.math.BigDecimal;
=======
import com.meetplus.batch.application.dto.AuctionTotalAmountDto;
import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
>>>>>>> cb6f6de42e457c20a40ac8905edffe9783767da5
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepositoryCustom {

    List<AuctionTotalAmountDto> getAuctionTotalAmountsByDateRange(LocalDateTime startTime,
        LocalDateTime endTime);

<<<<<<< HEAD
    BigDecimal getTotalAmountByAuctionUuid(String auctionUuid, LocalDateTime startTime, LocalDateTime endTime);

    List<String> getMemberUuidsByAuctionUuidAndPaymentStatus(String auctionUuid, PaymentStatus status);
=======
    List<Payment> getPaymentsByPaymentStatusAndDateRange(
        PaymentStatus paymentStatus, LocalDateTime startTime, LocalDateTime endTime);
>>>>>>> cb6f6de42e457c20a40ac8905edffe9783767da5
}
