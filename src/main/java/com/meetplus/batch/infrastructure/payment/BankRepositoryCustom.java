package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.application.dto.TotalDonationDto;

public interface BankRepositoryCustom {

	TotalDonationDto getBankTotalAmounts();
}
