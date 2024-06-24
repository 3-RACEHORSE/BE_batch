package com.meetplus.batch.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleTimeEnum {
    BEFORE_EVENT_START_12(43_200_000),
    BEFORE_EVENT_START_24(86_400_000)
    ;

    private final long time;
}
