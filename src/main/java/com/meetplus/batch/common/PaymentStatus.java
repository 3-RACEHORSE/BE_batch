package com.meetplus.batch.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    PENDING,
    COMPLETE,
    CANCEL,
    REFUND
}
