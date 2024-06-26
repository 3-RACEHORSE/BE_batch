package com.meetplus.batch.job;

import com.meetplus.batch.application.dto.TotalDonationDto;
import com.meetplus.batch.infrastructure.payment.BankRepository;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class TotalDonationItemReader implements ItemReader<TotalDonationDto> {

	private Iterator<TotalDonationDto> totalDonationDtoIterator;
	private final BankRepository bankRepository;

	public TotalDonationItemReader(BankRepository bankRepository) {
		this.bankRepository = bankRepository;
	}

	@Override
	public TotalDonationDto read() throws Exception {
		if (totalDonationDtoIterator == null) {
			TotalDonationDto totalDonationDtos = bankRepository.getBankTotalAmounts();
			totalDonationDtoIterator = List.of(totalDonationDtos).iterator();
		}

		if (totalDonationDtoIterator.hasNext()) {
			return totalDonationDtoIterator.next();
		} else {
			return null;
		}
	}
}
