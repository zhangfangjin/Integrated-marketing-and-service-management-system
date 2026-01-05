package org.example.rootmanage.aftersales.repository;

import org.example.rootmanage.aftersales.entity.ReplacementPartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 换件清单项Repository接口
 */
public interface ReplacementPartItemRepository extends JpaRepository<ReplacementPartItem, UUID> {

    /**
     * 根据维护计划ID查找换件清单项列表
     */
    @Query("SELECT i FROM ReplacementPartItem i WHERE i.maintenancePlan.id = :planId ORDER BY i.orderNo ASC, i.createTime ASC")
    List<ReplacementPartItem> findByMaintenancePlanId(@Param("planId") UUID planId);

    /**
     * 删除指定维护计划的所有换件清单项
     */
    @Modifying
    @Query("DELETE FROM ReplacementPartItem i WHERE i.maintenancePlan.id = :planId")
    void deleteByMaintenancePlanId(@Param("planId") UUID planId);
}





