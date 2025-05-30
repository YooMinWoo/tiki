package com.example.tiki.notion.repository;

import com.example.tiki.notion.domain.entity.Notion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotionRepository extends JpaRepository<Notion, Long>, NotionRepositoryCustom {

}
