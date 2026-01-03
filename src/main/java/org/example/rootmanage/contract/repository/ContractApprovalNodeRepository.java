package org.example.rootmanage.contract.repository;

import org.example.rootmanage.contract.entity.ContractApprovalNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 合同审批节点Repository接口
 */
public interface ContractApprovalNodeRepository extends JpaRepository<ContractApprovalNode, UUID> {

    /**
     * 根据合同ID查找所有审批节点
     */
    @Query("SELECT n FROM ContractApprovalNode n WHERE n.contract.id = :contractId ORDER BY n.approvalOrder ASC")
    List<ContractApprovalNode> findByContractIdOrderByApprovalOrderAsc(@Param("contractId") UUID contractId);

    /**
     * 根据合同ID和角色类型查找审批节点
     */
    @Query("SELECT n FROM ContractApprovalNode n WHERE n.contract.id = :contractId AND n.roleType = :roleType")
    List<ContractApprovalNode> findByContractIdAndRoleType(@Param("contractId") UUID contractId, @Param("roleType") String roleType);

    /**
     * 根据合同ID删除所有审批节点
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ContractApprovalNode n WHERE n.contract.id = :contractId")
    void deleteByContractId(@Param("contractId") UUID contractId);
}

