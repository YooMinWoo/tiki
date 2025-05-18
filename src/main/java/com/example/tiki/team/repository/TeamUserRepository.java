package com.example.tiki.team.repository;

import com.example.tiki.team.domain.TeamRole;
import com.example.tiki.team.domain.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    @Query("SELECT tu FROM TeamUser tu WHERE tu.userId = :userId AND tu.teamId = :teamId ORDER BY tu.id DESC limit 1")
    Optional<TeamUser> findLatestByUserIdAndTeamId(@Param("userId") Long userId, @Param("teamId") Long teamId);

    @Query(value = """
            SELECT tu.*
              FROM team_user tu
              JOIN (
                  SELECT user_id, MAX(team_user_id) AS latest_id
                  FROM team_user
                  WHERE team_id = :teamId
                  GROUP BY user_id
              ) latest ON tu.team_user_id = latest.latest_id
              WHERE tu.team_id = :teamId
                AND tu.team_role IN (:teamRoles)
    """, nativeQuery = true)
    List<TeamUser> findAllByTeamIdAndTeamRoleIn(@Param("teamId") Long teamId, @Param("teamRoles") List<String> teamRoles);

    @Query("SELECT tu.userId FROM TeamUser tu WHERE tu.teamId = :teamId AND tu.teamRole = 'ROLE_LEADER'")
    Optional<Long> findLeaderUserIdByTeamId(@Param("teamId") Long teamId);

    @Query("""
        select tu.userId
        from TeamUser tu
        where tu.teamRole = 'ROLE_LEADER'
          and tu.teamId = :teamId
          and tu.id = (
              select max(subTu.id)
              from TeamUser subTu
              where subTu.teamRole = 'ROLE_LEADER' and subTu.teamId = :teamId
          )
    """)
    Long findLeaderId(@Param("teamId") Long teamId);
}
