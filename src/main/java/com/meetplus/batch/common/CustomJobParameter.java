package com.meetplus.batch.common;

import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class CustomJobParameter {
    @Value("#{jobParameters['startTime']}")
    private LocalDateTime startTime = DateRangeUtil.getStartTime();
    @Value("#{jobParameters['endTime']}")
    private LocalDateTime endTime = DateRangeUtil.getEndTime();
}


