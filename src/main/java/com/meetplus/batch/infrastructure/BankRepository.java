package com.meetplus.batch.infrastructure;

import com.meetplus.batch.domain.Bank;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findByAuctionUuid(String auctionUuid);
}
