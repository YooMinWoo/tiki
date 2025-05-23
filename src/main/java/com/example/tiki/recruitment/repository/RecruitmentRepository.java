package com.example.tiki.recruitment.repository;

import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.recruitment.dto.RecruitmentStatusVisible;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.enums.TeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    // 특정 팀의 모든 상태의 공고
    List<Recruitment> findByTeamId(Long teamId);

    // 특정 팀의 특정 상태의 공고
    List<Recruitment> findByTeamIdAndRecruitmentStatus(Long teamId, RecruitmentStatus recruitmentStatus);

    // 특정 키워드(제목)를 포함하는 공고
    List<Recruitment> findByTitleContaining(String keyword);

    // 특정 키워드(제목)를 포함하고 특정 상태를 조회하는
    List<Recruitment> findByTitleContainingAndRecruitmentStatus(String keyword, RecruitmentStatus status);

    // 특정 키워드(제목)를 포함하는 공고 (삭제된 공고 제외)
    List<Recruitment> findByTitleContainingAndRecruitmentStatusNot(String keyword, RecruitmentStatus recruitmentStatus);
}
