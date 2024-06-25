package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.application.dto.TotalDonationDto;
import java.util.List;

public interface BankRepositoryCustom {

	List<TotalDonationDto> getBankTotalAmounts();
}
