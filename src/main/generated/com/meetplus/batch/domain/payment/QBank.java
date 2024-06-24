package com.meetplus.batch.domain.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBank is a Querydsl query type for Bank
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBank extends EntityPathBase<Bank> {

    private static final long serialVersionUID = 1599025082L;

    public static final QBank bank = new QBank("bank");

    public final com.meetplus.batch.common.QBaseTimeEntity _super = new com.meetplus.batch.common.QBaseTimeEntity(this);

    public final StringPath auctionUuid = createString("auctionUuid");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<java.math.BigDecimal> donation = createNumber("donation", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QBank(String variable) {
        super(Bank.class, forVariable(variable));
    }

    public QBank(Path<? extends Bank> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBank(PathMetadata metadata) {
        super(Bank.class, metadata);
    }

}

