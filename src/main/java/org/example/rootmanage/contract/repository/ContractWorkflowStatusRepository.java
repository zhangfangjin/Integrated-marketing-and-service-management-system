package org.example.rootmanage.contract.repository;

import org.example.rootmanage.contract.entity.ContractWorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 合同流程审批状态Repository接口
 */
public interface ContractWorkflowStatusRepository extends JpaRepository<ContractWorkflowStatus, UUID> {

    /**
     * 根据合同ID查找所有流程状态记录
     */
    @Query("SELECT s FROM ContractWorkflowStatus s WHERE s.contract.id = :contractId ORDER BY s.createTime ASC")
    List<ContractWorkflowStatus> findByContractIdOrderByCreateTimeAsc(@Param("contractId") UUID contractId);

    /**
     * 根据合同ID和节点名称查找流程状态
     */
    @Query("SELECT s FROM ContractWorkflowStatus s WHERE s.contract.id = :contractId AND s.nodeName = :nodeName")
    List<ContractWorkflowStatus> findByContractIdAndNodeName(@Param("contractId") UUID contractId, @Param("nodeName") String nodeName);

    /**
     * 根据合同ID删除所有流程状态记录
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ContractWorkflowStatus s WHERE s.contract.id = :contractId")
    void deleteByContractId(@Param("contractId") UUID contractId);
}

