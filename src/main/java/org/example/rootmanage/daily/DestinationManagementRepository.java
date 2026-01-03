package org.example.rootmanage.daily;

import org.example.rootmanage.daily.entity.DestinationManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DestinationManagementRepository extends JpaRepository<DestinationManagement, UUID> {

    /**
     * 根据用户ID查询去向信息
     */
    List<DestinationManagement> findByUserIdOrderByTimeDesc(UUID userId);

    /**
     * 查询所有已提交的去向信息
     */
    List<DestinationManagement> findBySubmittedTrueOrderByTimeDesc();

    /**
     * 根据关键词搜索去向信息（活动名称或地点）
     */
    @Query("SELECT d FROM DestinationManagement d WHERE d.submitted = true " +
           "AND (d.activityName LIKE %:keyword% OR d.location LIKE %:keyword%) " +
           "ORDER BY d.time DESC")
    List<DestinationManagement> searchByKeyword(@Param("keyword") String keyword);
}

