package com.example.tiki.team.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTeamUserHistory is a Querydsl query type for TeamUserHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamUserHistory extends EntityPathBase<TeamUserHistory> {

    private static final long serialVersionUID = -1456546204L;

    public static final QTeamUserHistory teamUserHistory = new QTeamUserHistory("teamUserHistory");

    public final com.example.tiki.global.entity.QBaseEntity _super = new com.example.tiki.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final EnumPath<com.example.tiki.team.domain.enums.TeamUserRole> currentRole = createEnum("currentRole", com.example.tiki.team.domain.enums.TeamUserRole.class);

    public final EnumPath<com.example.tiki.team.domain.enums.TeamUserStatus> currentStatus = createEnum("currentStatus", com.example.tiki.team.domain.enums.TeamUserStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final EnumPath<com.example.tiki.team.domain.enums.TeamUserRole> previousRole = createEnum("previousRole", com.example.tiki.team.domain.enums.TeamUserRole.class);

    public final EnumPath<com.example.tiki.team.domain.enums.TeamUserStatus> previousStatus = createEnum("previousStatus", com.example.tiki.team.domain.enums.TeamUserStatus.class);

    public final NumberPath<Long> teamId = createNumber("teamId", Long.class);

    public final NumberPath<Long> teamUserId = createNumber("teamUserId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QTeamUserHistory(String variable) {
        super(TeamUserHistory.class, forVariable(variable));
    }

    public QTeamUserHistory(Path<? extends TeamUserHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeamUserHistory(PathMetadata metadata) {
        super(TeamUserHistory.class, metadata);
    }

}

