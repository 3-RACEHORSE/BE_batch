package com.meetplus.batch.infrastructure.payment;

import com.meetplus.batch.domain.payment.TotalDonationSettlement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalSettlementRepository extends JpaRepository<TotalDonationSettlement, Long> {

}
