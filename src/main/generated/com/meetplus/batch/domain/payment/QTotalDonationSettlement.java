package com.meetplus.batch.domain.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTotalDonationSettlement is a Querydsl query type for TotalDonationSettlement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTotalDonationSettlement extends EntityPathBase<TotalDonationSettlement> {

    private static final long serialVersionUID = 556574337L;

    public static final QTotalDonationSettlement totalDonationSettlement = new QTotalDonationSettlement("totalDonationSettlement");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastSettlementDate = createDateTime("lastSettlementDate", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> totalDonation = createNumber("totalDonation", java.math.BigDecimal.class);

    public QTotalDonationSettlement(String variable) {
        super(TotalDonationSettlement.class, forVariable(variable));
    }

    public QTotalDonationSettlement(Path<? extends TotalDonationSettlement> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTotalDonationSettlement(PathMetadata metadata) {
        super(TotalDonationSettlement.class, metadata);
    }

}

