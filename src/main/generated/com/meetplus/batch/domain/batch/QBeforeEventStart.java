package com.meetplus.batch.domain.batch;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBeforeEventStart is a Querydsl query type for BeforeEventStart
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBeforeEventStart extends EntityPathBase<BeforeEventStart> {

    private static final long serialVersionUID = -1930975727L;

    public static final QBeforeEventStart beforeEventStart = new QBeforeEventStart("beforeEventStart");

    public final StringPath auctionUuid = createString("auctionUuid");

    public final DateTimePath<java.time.LocalDateTime> eventStartTime = createDateTime("eventStartTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath jobState = createBoolean("jobState");

    public QBeforeEventStart(String variable) {
        super(BeforeEventStart.class, forVariable(variable));
    }

    public QBeforeEventStart(Path<? extends BeforeEventStart> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBeforeEventStart(PathMetadata metadata) {
        super(BeforeEventStart.class, metadata);
    }

}

