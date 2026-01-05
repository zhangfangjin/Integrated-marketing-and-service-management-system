package org.example.rootmanage.aftersales.repository;

import org.example.rootmanage.aftersales.entity.AfterSalesOrder;
import org.example.rootmanage.aftersales.entity.AfterSalesType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 售后服务单Repository接口
 */
public interface AfterSalesOrderRepository extends JpaRepository<AfterSalesOrder, UUID> {

    /**
     * 根据服务单号查找售后服务单
     */
    Optional<AfterSalesOrder> findByServiceOrderNumber(String serviceOrderNumber);

    /**
     * 根据售后类型查找售后服务单列表
     */
    List<AfterSalesOrder> findByServiceType(AfterSalesType serviceType);

    /**
     * 根据服务状态查找售后服务单列表
     */
    List<AfterSalesOrder> findByServiceStatus(String serviceStatus);

    /**
     * 根据合同ID查找售后服务单列表
     */
    @Query("SELECT a FROM AfterSalesOrder a WHERE a.contract.id = :contractId")
    List<AfterSalesOrder> findByContractId(@Param("contractId") UUID contractId);

    /**
     * 根据受理人员ID查找售后服务单列表
     */
    List<AfterSalesOrder> findByHandlerId(UUID handlerId);

    /**
     * 根据关键词搜索售后服务单（服务单号、合同编号、合同名称、业主单位）
     */
    @Query("SELECT a FROM AfterSalesOrder a WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "a.serviceOrderNumber LIKE %:keyword% OR " +
           "a.contractNumber LIKE %:keyword% OR " +
           "a.contractName LIKE %:keyword% OR " +
           "a.customerUnit LIKE %:keyword%)")
    List<AfterSalesOrder> searchAfterSalesOrders(@Param("keyword") String keyword);
}





