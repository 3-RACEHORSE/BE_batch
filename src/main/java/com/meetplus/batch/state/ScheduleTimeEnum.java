package com.meetplus.batch.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleTimeEnum {
    // 행사 시작 시간 12시간 전
    BEFORE_EVENT_START(-12);
    private final int time;
}
