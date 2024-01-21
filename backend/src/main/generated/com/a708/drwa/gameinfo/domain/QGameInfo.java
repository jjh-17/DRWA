package com.a708.drwa.gameinfo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGameInfo is a Querydsl query type for GameInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGameInfo extends EntityPathBase<GameInfo> {

    private static final long serialVersionUID = -951961873L;

    public static final QGameInfo gameInfo = new QGameInfo("gameInfo");

    public final NumberPath<Integer> gameId = createNumber("gameId", Integer.class);

    public final StringPath keyword = createString("keyword");

    public final NumberPath<Integer> mvpMemberId = createNumber("mvpMemberId", Integer.class);

    public QGameInfo(String variable) {
        super(GameInfo.class, forVariable(variable));
    }

    public QGameInfo(Path<? extends GameInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGameInfo(PathMetadata metadata) {
        super(GameInfo.class, metadata);
    }

}

