package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT p FROM Payment p WHERE p.completionAt BETWEEN :start AND :end")
    List<Payment> findAllByCompletionAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

//    @Query("SELECT p FROM Payment p WHERE p.completionAt BETWEEN :start AND :end")
//    List<Payment> findAllByCompletionAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Payment> findByAuctionUuidAndPaymentStatus(String auctionUuid, PaymentStatus paymentStatus);

}
