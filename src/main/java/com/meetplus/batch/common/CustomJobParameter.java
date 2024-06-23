package com.meetplus.batch.common;

import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class CustomJobParameter {
    private LocalDateTime paymentJobStartTime;
    private LocalDateTime paymentJobEndTime;

    @Value("#{jobParameters['paymentJobStartTime']}")
    public void setPaymentJobStartTime(String paymentJobStartTime) {
        if(paymentJobStartTime != null && !paymentJobStartTime.isEmpty()) {
            this.paymentJobStartTime = LocalDateTime.parse(paymentJobStartTime);
        }
    }

    @Value("#{jobParameters['paymentJobEndTime']}")
    public void setPaymentJobEndTime(String paymentJobEndTime) {
        if(paymentJobEndTime != null && !paymentJobEndTime.isEmpty()) {
            this.paymentJobEndTime = LocalDateTime.parse(paymentJobEndTime);
        }
    }
}


