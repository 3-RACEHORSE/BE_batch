package com.meetplus.batch.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    PENDING, //결제 대기
    COMPLETE, //결제 완료
    CANCEL, //결제 취소
    REFUND // 환불
}
