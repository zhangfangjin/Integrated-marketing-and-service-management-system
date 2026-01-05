package org.example.rootmanage.aftersales.repository;

import org.example.rootmanage.aftersales.entity.MaintenanceProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 维修进度Repository接口
 */
public interface MaintenanceProgressRepository extends JpaRepository<MaintenanceProgress, UUID> {

    /**
     * 根据合同ID查找维修进度
     */
    @Query("SELECT m FROM MaintenanceProgress m WHERE m.contract.id = :contractId")
    Optional<MaintenanceProgress> findByContractId(@Param("contractId") UUID contractId);

    /**
     * 根据关键词搜索维修进度（合同编号、合同名称）
     */
    @Query("SELECT m FROM MaintenanceProgress m WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "m.contractNumber LIKE %:keyword% OR " +
           "m.contractName LIKE %:keyword%)")
    List<MaintenanceProgress> searchMaintenanceProgress(@Param("keyword") String keyword);
}





