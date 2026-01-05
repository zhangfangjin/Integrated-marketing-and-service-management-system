package org.example.rootmanage.aftersales.repository;

import org.example.rootmanage.aftersales.entity.MaintenancePlan;
import org.example.rootmanage.aftersales.entity.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 维护计划Repository接口
 */
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, UUID> {

    /**
     * 根据设备类型查找维护计划（每种设备类型只能有一个计划）
     */
    @Query("SELECT p FROM MaintenancePlan p WHERE p.planType = :planType AND p.deviceType = :deviceType")
    Optional<MaintenancePlan> findByDeviceType(@Param("planType") PlanType planType, @Param("deviceType") String deviceType);

    /**
     * 根据设备ID查找维护计划
     */
    @Query("SELECT p FROM MaintenancePlan p WHERE p.planType = :planType AND p.device.id = :deviceId")
    Optional<MaintenancePlan> findByDeviceId(@Param("planType") PlanType planType, @Param("deviceId") UUID deviceId);

    /**
     * 根据计划类型查找所有维护计划
     */
    List<MaintenancePlan> findByPlanTypeOrderByCreateTimeDesc(PlanType planType);

    /**
     * 查找所有启用的维护计划
     */
    @Query("SELECT p FROM MaintenancePlan p WHERE p.enabled = true")
    List<MaintenancePlan> findAllEnabled();

    /**
     * 根据关键词搜索维护计划
     */
    @Query("SELECT p FROM MaintenancePlan p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "p.deviceType LIKE %:keyword% OR " +
           "p.deviceNumber LIKE %:keyword% OR " +
           "p.deviceName LIKE %:keyword%)")
    List<MaintenancePlan> searchMaintenancePlans(@Param("keyword") String keyword);
}

