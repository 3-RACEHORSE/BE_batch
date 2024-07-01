package com.meetplus.batch.job;

import com.meetplus.batch.common.CustomJobParameter;
import com.meetplus.batch.common.PaymentStatus;
import com.meetplus.batch.domain.payment.Payment;
import com.meetplus.batch.infrastructure.payment.PaymentRepository;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class PaymentCancelItemReader implements ItemReader<Payment> {

    private final PaymentRepository paymentRepository;
    private final CustomJobParameter customJobParameter;
    private Iterator<Payment> paymentsIterator;

    public PaymentCancelItemReader(PaymentRepository paymentRepository,
        CustomJobParameter customJobParameter) {
        this.paymentRepository = paymentRepository;
        this.customJobParameter = customJobParameter;
    }

    @Override
    public Payment read() throws Exception {
        if (paymentsIterator == null) {
            List<Payment> payments = paymentRepository.getPaymentsByPaymentStatusAndDateRange(
                PaymentStatus.PENDING,
                customJobParameter.getPaymentJobStartTime(),
                customJobParameter.getPaymentJobEndTime()
            );
            paymentsIterator = payments.iterator();
        }

        if (paymentsIterator.hasNext()) {
            return paymentsIterator.next();
        } else {
            return null;
        }
    }
}
