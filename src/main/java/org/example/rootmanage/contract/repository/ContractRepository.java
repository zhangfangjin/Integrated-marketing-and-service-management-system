package org.example.rootmanage.contract.repository;

import org.example.rootmanage.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 合同Repository接口
 */
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    /**
     * 根据合同编号查找合同
     */
    Optional<Contract> findByContractNumber(String contractNumber);

    /**
     * 根据合同状态查找合同列表
     */
    List<Contract> findByContractStatus(String contractStatus);

    /**
     * 根据合同编号或合同名称模糊查询
     */
    @Query("SELECT c FROM Contract c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "c.contractNumber LIKE %:keyword% OR " +
           "c.contractName LIKE %:keyword% OR " +
           "c.customerName LIKE %:keyword%)")
    List<Contract> searchContracts(@Param("keyword") String keyword);
    /**
     * 根据负责人ID查找合同列表
     */
    List<Contract> findByManagerId(UUID managerId);

    /**
     * 根据合同编号或合同名称模糊查询（仅限指定负责人）
     */
    @Query("SELECT c FROM Contract c WHERE " +
           "c.managerId = :managerId AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "c.contractNumber LIKE %:keyword% OR " +
           "c.contractName LIKE %:keyword% OR " +
           "c.customerName LIKE %:keyword%)")
    List<Contract> searchContractsByManagerId(@Param("keyword") String keyword, @Param("managerId") UUID managerId);
}

