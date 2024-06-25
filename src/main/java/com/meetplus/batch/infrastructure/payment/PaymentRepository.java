package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Payment> findByAuctionUuidAndPaymentStatus(String auctionUuid, PaymentStatus paymentStatus);


}
