package org.example.rootmanage.aftersales.repository;

import org.example.rootmanage.aftersales.entity.ReturnedPump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 返厂泵Repository接口
 */
public interface ReturnedPumpRepository extends JpaRepository<ReturnedPump, UUID> {

    /**
     * 根据合同ID查找返厂泵列表
     */
    @Query("SELECT r FROM ReturnedPump r WHERE r.contract.id = :contractId")
    List<ReturnedPump> findByContractId(@Param("contractId") UUID contractId);

    /**
     * 根据关键词搜索返厂泵（合同编号、合同名称、返厂泵名称）
     */
    @Query("SELECT r FROM ReturnedPump r WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "r.contractNumber LIKE %:keyword% OR " +
           "r.contractName LIKE %:keyword% OR " +
           "r.pumpName LIKE %:keyword%)")
    List<ReturnedPump> searchReturnedPumps(@Param("keyword") String keyword);
}

















