package org.example.rootmanage.contract.repository;

import org.example.rootmanage.contract.entity.ContractProportion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 合同占比划分Repository接口
 */
public interface ContractProportionRepository extends JpaRepository<ContractProportion, UUID> {

    /**
     * 根据合同ID查找所有占比划分
     */
    @Query("SELECT p FROM ContractProportion p WHERE p.contract.id = :contractId")
    List<ContractProportion> findByContractId(@Param("contractId") UUID contractId);

    /**
     * 根据合同ID删除所有占比划分
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ContractProportion p WHERE p.contract.id = :contractId")
    void deleteByContractId(@Param("contractId") UUID contractId);
}

