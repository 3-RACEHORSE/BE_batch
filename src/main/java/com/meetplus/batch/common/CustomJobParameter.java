package com.meetplus.batch.common;

import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class CustomJobParameter {
<<<<<<< HEAD
    @Value("#{jobParameters['startTime']}")
    private LocalDateTime startTime = DateRangeUtil.getStartTime();
    @Value("#{jobParameters['endTime']}")
    private LocalDateTime endTime = DateRangeUtil.getEndTime();

=======
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
>>>>>>> cb6f6de42e457c20a40ac8905edffe9783767da5
}


