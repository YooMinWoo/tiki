-- 시퀀스 테이블들 (Hibernate-style sequence simulation)
CREATE TABLE follow_seq (next_val BIGINT);
INSERT INTO follow_seq VALUES (1);

CREATE TABLE match_post_seq (next_val BIGINT);
INSERT INTO match_post_seq VALUES (1);

CREATE TABLE match_request_seq (next_val BIGINT);
INSERT INTO match_request_seq VALUES (1);

CREATE TABLE notice_seq (next_val BIGINT);
INSERT INTO notice_seq VALUES (1);

CREATE TABLE notification_seq (next_val BIGINT);
INSERT INTO notification_seq VALUES (1);

CREATE TABLE recruitment_seq (next_val BIGINT);
INSERT INTO recruitment_seq VALUES (1);

CREATE TABLE team_seq (next_val BIGINT);
INSERT INTO team_seq VALUES (1);

CREATE TABLE team_user_history_seq (next_val BIGINT);
INSERT INTO team_user_history_seq VALUES (1);

CREATE TABLE team_user_seq (next_val BIGINT);
INSERT INTO team_user_seq VALUES (1);

CREATE TABLE user_seq (next_val BIGINT);
INSERT INTO user_seq VALUES (1);

-- 메인 테이블들
CREATE TABLE follow (
    created_date DATETIME(6),
    follow_id BIGINT NOT NULL,
    team_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (follow_id)
) ENGINE=InnoDB;

CREATE TABLE match_post (
    latitude FLOAT(53),
    longitude FLOAT(53),
    applicant_team_id BIGINT,
    created_date DATETIME(6),
    end_time DATETIME(6),
    host_team_id BIGINT,
    last_modified_date DATETIME(6),
    match_post_id BIGINT NOT NULL,
    start_time DATETIME(6),
    building_number VARCHAR(255),
    city VARCHAR(255),
    content VARCHAR(255),
    detail_address VARCHAR(255),
    region VARCHAR(255),
    road_name VARCHAR(255),
    title VARCHAR(255),
    match_status ENUM ('CANCELED','COMPLETED','DELETED','DELETED_BY_ADMIN','MATCHED','OPEN','UNMATCHED'),
    PRIMARY KEY (match_post_id)
) ENGINE=InnoDB;

CREATE TABLE match_request (
    applicant_team_id BIGINT,
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    match_post_id BIGINT,
    match_request_id BIGINT NOT NULL,
    request_status ENUM ('ACCEPTED','CANCELED','CANCELED_BY_ADMIN','PENDING','REJECTED'),
    PRIMARY KEY (match_request_id)
) ENGINE=InnoDB;

CREATE TABLE notice (
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    notion_id BIGINT NOT NULL,
    user_id BIGINT,
    content TEXT,
    title VARCHAR(100),
    notice_status ENUM ('DELETED','OPEN'),
    PRIMARY KEY (notion_id)
) ENGINE=InnoDB;

CREATE TABLE notification (
    is_read BIT NOT NULL,
    created_date DATETIME(6),
    notification_id BIGINT NOT NULL,
    target_id BIGINT,
    user_id BIGINT,
    message VARCHAR(255),
    notification_type ENUM (
        'APPROVE','DISBAND','FOLLOW','JOIN','KICK','LEFT',
        'MATCHPOST','MATCHPOSTLIST','MATCHREQUEST',
        'RECRUIT','RECRUITLIST','REJECT','SCHEDULE','TEAMPAGE'
    ),
    PRIMARY KEY (notification_id)
) ENGINE=InnoDB;

CREATE TABLE recruitment (
    closed_at DATETIME(6),
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    opened_at DATETIME(6),
    recruitment_id BIGINT NOT NULL,
    team_id BIGINT,
    content TEXT,
    title VARCHAR(255),
    recruitment_status ENUM ('CLOSE','DELETED','DELETED_BY_ADMIN','OPEN'),
    PRIMARY KEY (recruitment_id)
) ENGINE=InnoDB;

CREATE TABLE team (
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    team_id BIGINT NOT NULL,
    team_description TEXT,
    team_name VARCHAR(100),
    team_status ENUM ('ACTIVE','DISBANDED','INACTIVE'),
    PRIMARY KEY (team_id)
) ENGINE=InnoDB;

CREATE TABLE team_user (
    created_date DATETIME(6),
    joined_at DATETIME(6),
    last_modified_date DATETIME(6),
    team_id BIGINT,
    team_user_id BIGINT NOT NULL,
    user_id BIGINT,
    team_user_role ENUM ('ROLE_LEADER','ROLE_MANAGER','ROLE_MEMBER'),
    team_user_status ENUM ('APPROVED','DISBANDED','KICKED','LEFT','REJECTED','WAITING'),
    PRIMARY KEY (team_user_id)
) ENGINE=InnoDB;

CREATE TABLE team_user_history (
    current_role enum ('ROLE_LEADER','ROLE_MANAGER','ROLE_MEMBER'),
    current_status enum ('APPROVED','DISBANDED','KICKED','LEFT','REJECTED','WAITING'),
    previous_role enum ('ROLE_LEADER','ROLE_MANAGER','ROLE_MEMBER'),
    previous_status enum ('APPROVED','DISBANDED','KICKED','LEFT','REJECTED','WAITING'),
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    team_id BIGINT,
    team_user_history_id BIGINT NOT NULL,
    team_user_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (team_user_history_id)
) ENGINE=InnoDB;

CREATE TABLE user (
    date_of_birth DATE,
    email_push BIT NOT NULL,
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    user_id BIGINT NOT NULL,
    email VARCHAR(255),
    introduce TEXT,
    name VARCHAR(100),
    password VARCHAR(255),
    role ENUM ('ROLE_ADMIN','ROLE_USER'),
    PRIMARY KEY (user_id)
) ENGINE=InnoDB;
