package com.meetplus.batch.job;

import com.meetplus.batch.application.dto.AuctionTotalAmountDto;
import com.meetplus.batch.common.CustomJobParameter;
import com.meetplus.batch.infrastructure.payment.PaymentRepository;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
public class PaymentSumItemReader implements ItemReader<AuctionTotalAmountDto> {

    private Iterator<AuctionTotalAmountDto> auctionTotalAmountDtoIterator;
    private final PaymentRepository paymentRepository;
    private final CustomJobParameter customJobParameter;

    public PaymentSumItemReader(PaymentRepository paymentRepository,
        CustomJobParameter customJobParameter) {
        this.paymentRepository = paymentRepository;
        this.customJobParameter = customJobParameter;
    }

    @Override
    public AuctionTotalAmountDto read() throws Exception {
        if (auctionTotalAmountDtoIterator == null) {
            List<AuctionTotalAmountDto> auctionTotalAmountDtos = paymentRepository.getAuctionTotalAmountsByDateRange(
                customJobParameter.getPaymentJobStartTime(),
                customJobParameter.getPaymentJobEndTime());
            auctionTotalAmountDtoIterator = auctionTotalAmountDtos.iterator();
        }

        if (auctionTotalAmountDtoIterator.hasNext()) {
            return auctionTotalAmountDtoIterator.next();
        } else {
            return null;
        }
    }
}
