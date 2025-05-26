package com.example.tiki.team.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTeamUser is a Querydsl query type for TeamUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamUser extends EntityPathBase<TeamUser> {

    private static final long serialVersionUID = -2021987312L;

    public static final QTeamUser teamUser = new QTeamUser("teamUser");

    public final com.example.tiki.global.entity.QBaseEntity _super = new com.example.tiki.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> joinedAt = createDateTime("joinedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Long> teamId = createNumber("teamId", Long.class);

    public final EnumPath<com.example.tiki.team.domain.enums.TeamUserRole> teamUserRole = createEnum("teamUserRole", com.example.tiki.team.domain.enums.TeamUserRole.class);

    public final EnumPath<com.example.tiki.team.domain.enums.TeamUserStatus> teamUserStatus = createEnum("teamUserStatus", com.example.tiki.team.domain.enums.TeamUserStatus.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QTeamUser(String variable) {
        super(TeamUser.class, forVariable(variable));
    }

    public QTeamUser(Path<? extends TeamUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeamUser(PathMetadata metadata) {
        super(TeamUser.class, metadata);
    }

}

