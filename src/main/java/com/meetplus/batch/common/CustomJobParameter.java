package com.meetplus.batch.common;

import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class CustomJobParameter {
    private LocalDateTime paymentJobStartTime;
    private LocalDateTime paymentJobEndTime;

    @Value("#{jobParameters['paymentJobStartTime']}")
    public void setPaymentJobStartTime(LocalDateTime paymentJobStartTime) {
        if(paymentJobStartTime != null) {
            this.paymentJobStartTime = paymentJobStartTime;
        }
    }

    @Value("#{jobParameters['paymentJobEndTime']}")
    public void setPaymentJobEndTime(LocalDateTime paymentJobEndTime) {
        if(paymentJobEndTime != null) {
            this.paymentJobEndTime = paymentJobEndTime;
        }
    }
}


