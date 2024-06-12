package com.meetplus.batch.infrastructure;

import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

}
