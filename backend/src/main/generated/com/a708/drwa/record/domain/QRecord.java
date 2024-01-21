package com.a708.drwa.record.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecord is a Querydsl query type for Record
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecord extends EntityPathBase<Record> {

    private static final long serialVersionUID = 363836145L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecord record = new QRecord("record");

    public final com.a708.drwa.gameinfo.domain.QGameInfo gameId;

    public final com.a708.drwa.member.domain.QMember memberId;

    public final NumberPath<Integer> recordId = createNumber("recordId", Integer.class);

    public final EnumPath<Result> result = createEnum("result", Result.class);

    public final EnumPath<Team> team = createEnum("team", Team.class);

    public QRecord(String variable) {
        this(Record.class, forVariable(variable), INITS);
    }

    public QRecord(Path<? extends Record> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecord(PathMetadata metadata, PathInits inits) {
        this(Record.class, metadata, inits);
    }

    public QRecord(Class<? extends Record> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.gameId = inits.isInitialized("gameId") ? new com.a708.drwa.gameinfo.domain.QGameInfo(forProperty("gameId")) : null;
        this.memberId = inits.isInitialized("memberId") ? new com.a708.drwa.member.domain.QMember(forProperty("memberId")) : null;
    }

}

