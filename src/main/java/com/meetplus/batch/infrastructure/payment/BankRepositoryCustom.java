package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.application.dto.TotalDonationDto;
import java.time.LocalDateTime;
import java.util.List;

public interface BankRepositoryCustom {

	TotalDonationDto getBankTotalAmountsAfter(LocalDateTime lastSettlementTime);
}
