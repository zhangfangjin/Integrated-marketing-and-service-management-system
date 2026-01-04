package org.example.rootmanage.contract.repository;

import org.example.rootmanage.contract.entity.ContractPaymentStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 合同付款阶段Repository接口
 */
public interface ContractPaymentStageRepository extends JpaRepository<ContractPaymentStage, UUID> {

    /**
     * 根据合同ID查找所有付款阶段
     */
    @Query("SELECT s FROM ContractPaymentStage s WHERE s.contract.id = :contractId")
    List<ContractPaymentStage> findByContractId(@Param("contractId") UUID contractId);

    /**
     * 根据合同ID删除所有付款阶段
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ContractPaymentStage s WHERE s.contract.id = :contractId")
    void deleteByContractId(@Param("contractId") UUID contractId);

    /**
     * 根据合同编号查找所有付款阶段
     */
    @Query("SELECT s FROM ContractPaymentStage s WHERE s.contract.contractNumber = :contractNumber")
    List<ContractPaymentStage> findByContractNumber(@Param("contractNumber") String contractNumber);

    /**
     * 根据关键词搜索付款阶段（合同编号、合同名称、客户名称）
     */
    @Query("SELECT s FROM ContractPaymentStage s WHERE " +
            "s.contract.contractNumber LIKE %:keyword% OR " +
            "s.contract.contractName LIKE %:keyword% OR " +
            "s.contract.customerName LIKE %:keyword%")
    List<ContractPaymentStage> searchByKeyword(@Param("keyword") String keyword);
}

