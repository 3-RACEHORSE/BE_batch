package com.meetplus.batch.kafka.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlarmDto {

    private List<String> receiverUuids;
    private String message;
    private String eventType;
    private String url;

    @Builder
    public AlarmDto(List<String> receiverUuids, String message, String eventType, String url) {
        this.receiverUuids = receiverUuids;
        this.message = message;
        this.eventType = eventType;
        this.url = url;
    }
}
