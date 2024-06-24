package com.meetplus.batch.listener;

import com.meetplus.batch.domain.payment.Bank;
import com.meetplus.batch.kafka.KafkaProducerCluster;
import com.meetplus.batch.kafka.Topics;
import com.meetplus.batch.kafka.dto.SaveDonationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

@RequiredArgsConstructor
public class PaymentSumItemWriteListener implements ItemWriteListener<Bank> {

    private final KafkaProducerCluster producer;

    @Override
    public void beforeWrite(Chunk<? extends Bank> items) {

    }

    @Override
    public void afterWrite(Chunk<? extends Bank> items) {
        items.getItems().forEach(item -> {
            producer.sendMessage(Topics.Constant.AUCTION_POST_DONATION_UPDATE,
                SaveDonationDto.builder()
                    .auctionUuid(item.getAuctionUuid())
                    .donation(item.getDonation())
                    .build());
        });
    }

    @Override
    public void onWriteError(Exception e, Chunk<? extends Bank> items) {

    }
}
