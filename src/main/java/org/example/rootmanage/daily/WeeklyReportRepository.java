package org.example.rootmanage.daily;

import org.example.rootmanage.daily.entity.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, UUID> {

    /**
     * 根据用户ID查询周报
     */
    List<WeeklyReport> findByUserIdOrderByReportTimeDesc(UUID userId);

    /**
     * 查询所有已提交的周报
     */
    List<WeeklyReport> findBySubmittedTrueOrderByReportTimeDesc();

    /**
     * 根据关键词搜索周报（周报名称或内容）
     */
    @Query("SELECT w FROM WeeklyReport w WHERE w.submitted = true " +
           "AND (w.reportName LIKE %:keyword% OR w.content LIKE %:keyword%) " +
           "ORDER BY w.reportTime DESC")
    List<WeeklyReport> searchByKeyword(@Param("keyword") String keyword);
}

