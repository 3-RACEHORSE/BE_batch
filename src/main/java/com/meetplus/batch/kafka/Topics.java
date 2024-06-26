package com.meetplus.batch.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topics {
    ALARM_TOPIC(Constant.ALARM_TOPIC),
    CHAT_SERVICE(Constant.CHAT_SERVICE),
    AUCTION_POST_SERVICE(Constant.AUCTION_POST_SERVICE),
    PAYMENT_SERVICE(Constant.PAYMENT_SERVICE),
    EVENT_START_TOPIC(Constant.EVENT_START_TOPIC)
    ;

    public static class Constant {
        public static final String ALARM_TOPIC = "alarm-topic";
        public static final String CHAT_SERVICE = "chat-topic";
        public static final String AUCTION_POST_SERVICE = "new-auction-post-topic";
        public static final String PAYMENT_SERVICE = "event-preview-topic";
        public static final String AUCTION_POST_DONATION_UPDATE = "auction-post-donation-update-topic";
        public static final String EVENT_START_TOPIC = "event-start-topic";
        public static final String SEND_TO_AUCTION_POST_FOR_CREATE_CHATROOM = "send-to-auction-post-for-create-chatroom-topic";
    }

    private final String topic;
}
