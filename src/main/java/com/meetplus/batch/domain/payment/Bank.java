package com.meetplus.batch.domain.payment;

import com.meetplus.batch.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "bank")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bank extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_id")
    private Long id;
    @Column(name = "auction_uuid", nullable = false, length = 30)
    private String auctionUuid;
    @Column(name = "donation", nullable = false, length = 100)
    private BigDecimal donation;

    @Builder
    public Bank(Long id, String auctionUuid, BigDecimal donation) {
        this.id = id;
        this.auctionUuid = auctionUuid;
        this.donation = donation;
    }
}
