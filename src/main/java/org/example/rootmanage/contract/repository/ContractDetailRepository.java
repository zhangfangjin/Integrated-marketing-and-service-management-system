package org.example.rootmanage.contract.repository;

import org.example.rootmanage.contract.entity.ContractDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 合同细目Repository接口
 */
public interface ContractDetailRepository extends JpaRepository<ContractDetail, UUID> {

    /**
     * 根据合同ID查找所有细目
     */
    @Query("SELECT d FROM ContractDetail d WHERE d.contract.id = :contractId")
    List<ContractDetail> findByContractId(@Param("contractId") UUID contractId);

    /**
     * 根据合同ID删除所有细目
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ContractDetail d WHERE d.contract.id = :contractId")
    void deleteByContractId(@Param("contractId") UUID contractId);
}

