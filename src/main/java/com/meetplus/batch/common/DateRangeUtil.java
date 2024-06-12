package com.meetplus.batch.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class DateRangeUtil {
    public static LocalDateTime getStartTime() {
        ZonedDateTime zonedNow = ZonedDateTime.now(ZoneId.systemDefault());
        LocalDateTime now = zonedNow.toLocalDateTime();
        LocalDateTime startOfToday = now.truncatedTo(ChronoUnit.DAYS).plusHours(5);

        if (now.isBefore(startOfToday)) {
            // 현재 시간이 오늘 오전 5시 이전이면 전날 오전 5시로 설정
            return startOfToday.minusDays(1);
        } else {
            return startOfToday;
        }
    }

    public static LocalDateTime getEndTime() {
        return getStartTime().plusDays(1);
    }
}
