package com.meetplus.batch.infrastructure;

import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.Payment;
import com.meetplus.batch.infrastructure.querydsl.PaymentRepositoryCustom;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT p FROM Payment p WHERE p.completionAt BETWEEN :start AND :end")
    List<Payment> findAllByCompletionAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
