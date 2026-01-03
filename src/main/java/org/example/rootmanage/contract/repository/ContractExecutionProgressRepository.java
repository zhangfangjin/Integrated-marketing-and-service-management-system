package org.example.rootmanage.contract.repository;

import org.example.rootmanage.contract.entity.ContractExecutionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * 合同执行进度Repository接口
 */
public interface ContractExecutionProgressRepository extends JpaRepository<ContractExecutionProgress, UUID> {

    /**
     * 根据合同ID查找执行进度
     */
    @Query("SELECT p FROM ContractExecutionProgress p WHERE p.contract.id = :contractId")
    Optional<ContractExecutionProgress> findByContractId(@Param("contractId") UUID contractId);
}

