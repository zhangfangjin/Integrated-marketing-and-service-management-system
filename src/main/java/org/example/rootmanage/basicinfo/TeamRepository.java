package org.example.rootmanage.basicinfo;

import org.example.rootmanage.basicinfo.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    /**
     * 搜索团队（支持按团队名称模糊查询）
     */
    @Query("SELECT t FROM Team t WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "t.teamName LIKE %:keyword%)")
    List<Team> searchTeams(@Param("keyword") String keyword);

    /**
     * 查询所有启用的团队
     */
    List<Team> findByActiveTrue();
}

