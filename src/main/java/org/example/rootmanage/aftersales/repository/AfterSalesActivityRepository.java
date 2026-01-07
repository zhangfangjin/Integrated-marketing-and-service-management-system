package org.example.rootmanage.aftersales.repository;

import org.example.rootmanage.aftersales.entity.AfterSalesActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 售后服务活动Repository接口
 */
public interface AfterSalesActivityRepository extends JpaRepository<AfterSalesActivity, UUID> {

    /**
     * 根据售后服务单ID查找活动列表
     */
    @Query("SELECT a FROM AfterSalesActivity a WHERE a.afterSalesOrder.id = :orderId ORDER BY a.createTime ASC")
    List<AfterSalesActivity> findByAfterSalesOrderId(@Param("orderId") UUID orderId);

    /**
     * 根据活动类型查找活动列表
     */
    @Query("SELECT a FROM AfterSalesActivity a WHERE a.afterSalesOrder.id = :orderId AND a.activityType = :activityType ORDER BY a.createTime ASC")
    List<AfterSalesActivity> findByAfterSalesOrderIdAndActivityType(@Param("orderId") UUID orderId, @Param("activityType") String activityType);
}

















