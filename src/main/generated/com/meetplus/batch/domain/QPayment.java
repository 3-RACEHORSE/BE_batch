package com.meetplus.batch.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = 1300250032L;

    public static final QPayment payment = new QPayment("payment");

    public final com.meetplus.batch.common.QBaseTimeEntity _super = new com.meetplus.batch.common.QBaseTimeEntity(this);

    public final NumberPath<java.math.BigDecimal> amountPaid = createNumber("amountPaid", java.math.BigDecimal.class);

    public final StringPath auctionUuid = createString("auctionUuid");

    public final DateTimePath<java.time.LocalDateTime> completionAt = createDateTime("completionAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memberUuid = createString("memberUuid");

    public final StringPath paymentMethod = createString("paymentMethod");

    public final StringPath paymentNumber = createString("paymentNumber");

    public final EnumPath<com.meetplus.batch.common.PaymentStatus> paymentStatus = createEnum("paymentStatus", com.meetplus.batch.common.PaymentStatus.class);

    public final StringPath paymentUuid = createString("paymentUuid");

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public QPayment(String variable) {
        super(Payment.class, forVariable(variable));
    }

    public QPayment(Path<? extends Payment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayment(PathMetadata metadata) {
        super(Payment.class, metadata);
    }

}

