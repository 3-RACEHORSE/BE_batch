package com.meetplus.batch.infrastructure.batch;

import com.meetplus.batch.domain.batch.BeforeEventStart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeforeEventStartRepository extends JpaRepository<BeforeEventStart, Long> {
    Optional<BeforeEventStart> findByAuctionUuid(String auctionUuid);

    List<BeforeEventStart> findByJobStateFalse();
}
