package org.example.rootmanage.aftersales.repository;

import org.example.rootmanage.aftersales.entity.AfterSalesExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 售后经验Repository接口
 */
public interface AfterSalesExperienceRepository extends JpaRepository<AfterSalesExperience, UUID> {

    /**
     * 根据设备类型查找售后经验列表
     */
    List<AfterSalesExperience> findByDeviceTypeOrderByCaseRegistrationDateDesc(String deviceType);

    /**
     * 根据案例类型查找售后经验列表
     */
    List<AfterSalesExperience> findByCaseTypeOrderByCaseRegistrationDateDesc(String caseType);

    /**
     * 根据关键词搜索售后经验（客户名称、设备类型、案例类型、案例备注）
     */
    @Query("SELECT e FROM AfterSalesExperience e WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "e.customerName LIKE %:keyword% OR " +
           "e.deviceType LIKE %:keyword% OR " +
           "e.caseType LIKE %:keyword% OR " +
           "e.caseRemark LIKE %:keyword%)")
    List<AfterSalesExperience> searchAfterSalesExperiences(@Param("keyword") String keyword);
}





