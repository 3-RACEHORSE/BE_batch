package com.meetplus.batch.domain.batch;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "before_event_start")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BeforeEventStart{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "before_event_start_id")
    private Long id;
    @Column(name = "auction_uuid", nullable = false, length = 30)
    private String auctionUuid;
    @Column(name = "event_start_time", nullable = false, length = 30)
    private LocalDateTime eventStartTime;
    @Column(name = "job_state", nullable = false)
    private boolean jobState;

    @Builder
    public BeforeEventStart(Long id, String auctionUuid, LocalDateTime eventStartTime) {
        this.id = id;
        this.auctionUuid = auctionUuid;
        // 실행할 시간 저장
        this.eventStartTime = eventStartTime;
        this.jobState = false;
    }

    public void setJobState(boolean jobState) {
        this.jobState = jobState;
    }
}
