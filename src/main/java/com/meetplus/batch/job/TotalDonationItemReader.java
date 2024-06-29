package com.meetplus.batch.job;

import com.meetplus.batch.application.dto.TotalDonationDto;
import com.meetplus.batch.domain.payment.TotalDonationSettlement;
import com.meetplus.batch.infrastructure.payment.BankRepository;
import com.meetplus.batch.infrastructure.payment.TotalSettlementRepository;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TotalDonationItemReader implements ItemReader<TotalDonationDto> {

	private Iterator<TotalDonationDto> totalDonationDtoIterator;
	private final BankRepository bankRepository;
	private final TotalSettlementRepository totalSettlementRepository;

	public TotalDonationItemReader(BankRepository bankRepository,
		TotalSettlementRepository totalSettlementRepository) {
		this.bankRepository = bankRepository;
		this.totalSettlementRepository = totalSettlementRepository;
	}

	@Override
	public TotalDonationDto read() throws Exception {
		if (totalDonationDtoIterator == null) {

			TotalDonationSettlement totalDonationSettlement = totalSettlementRepository.findTopByOrderByLastSettlementDateDesc();
			log.info("totalDonationSettlement: {}", totalDonationSettlement);
			LocalDateTime lastSettlementDate = totalDonationSettlement == null ? LocalDateTime.MIN : totalDonationSettlement.getLastSettlementDate();
			log.info("lastSettlementDate: {}", lastSettlementDate);
			TotalDonationDto totalDonationDtos = bankRepository.getBankTotalAmountsAfter(lastSettlementDate);
			log.info("totalDonationDtos: {}", totalDonationDtos);
			totalDonationDtoIterator = List.of(totalDonationDtos).iterator();
		}

		if (totalDonationDtoIterator.hasNext()) {
			return totalDonationDtoIterator.next();
		} else {
			return null;
		}
	}
}
