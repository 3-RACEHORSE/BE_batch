package com.meetplus.batch.common;

import com.meetplus.batch.state.TimeZoneChangeEnum;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class DateRangeUtil {

    public static LocalDateTime getStartTime(long hour) {
        ZonedDateTime zonedNow = ZonedDateTime.now(ZoneId.systemDefault());
        LocalDateTime now = zonedNow.toLocalDateTime().plusHours(TimeZoneChangeEnum.KOREA.getTimeDiff());
        LocalDateTime startOfToday = now.truncatedTo(ChronoUnit.DAYS).plusHours(hour);

        if (now.isBefore(startOfToday)) {
            return startOfToday.minusDays(1);
        } else {
            return startOfToday;
        }
    }

    public static LocalDateTime getEndTime(long hour) {
        return getStartTime(hour).plusDays(1);
    }
}
