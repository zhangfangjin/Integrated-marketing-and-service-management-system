package org.example.rootmanage.aftersales.dto;

import lombok.Data;
import org.example.rootmanage.aftersales.entity.AfterSalesType;
import org.example.rootmanage.aftersales.entity.AfterSalesOrder;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 售后服务单响应DTO
 */
@Data
public class AfterSalesOrderResponse {

    /**
     * 服务单ID
     */
    private UUID id;

    /**
     * 服务单号
     */
    private String serviceOrderNumber;

    /**
     * 售后类型
     */
    private AfterSalesType serviceType;

    /**
     * 合同ID
     */
    private UUID contractId;

    /**
     * 合同编号
     */
    private String contractNumber;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 业主单位（客户单位）
     */
    private String customerUnit;

    /**
     * 付款计划
     */
    private String paymentPlan;

    /**
     * 售后时间
     */
    private Date serviceDate;

    /**
     * 地区
     */
    private String region;

    /**
     * 维修方案附件路径
     */
    private String maintenancePlanAttachment;

    /**
     * 换件清单附件路径
     */
    private String replacementPartsListAttachment;

    /**
     * 客户请求附件路径
     */
    private String customerRequestAttachment;

    /**
     * 备注
     */
    private String remark;

    /**
     * 受理人员ID
     */
    private UUID handlerId;

    /**
     * 受理人员姓名
     */
    private String handlerName;

    /**
     * 服务状态
     */
    private String serviceStatus;

    /**
     * 是否需要审批
     */
    private Boolean needsApproval;

    /**
     * 审批状态
     */
    private String approvalStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 完成日期
     */
    private Date completionDate;

    /**
     * 设备编号
     */
    private String deviceNumber;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 售后总结
     */
    private String serviceSummary;

    /**
     * 售后评价
     */
    private String evaluation;

    /**
     * 评价人ID
     */
    private UUID evaluatorId;

    /**
     * 评价人姓名
     */
    private String evaluatorName;

    /**
     * 评价时间
     */
    private LocalDateTime evaluationTime;

    /**
     * 是否已关闭
     */
    private Boolean isClosed;

    /**
     * 从实体类转换为响应DTO
     */
    public static AfterSalesOrderResponse fromEntity(AfterSalesOrder order) {
        AfterSalesOrderResponse response = new AfterSalesOrderResponse();
        if (order != null) {
            response.setId(order.getId());
            response.setServiceOrderNumber(order.getServiceOrderNumber());
            response.setServiceType(order.getServiceType());
            if (order.getContract() != null) {
                response.setContractId(order.getContract().getId());
            }
            response.setContractNumber(order.getContractNumber());
            response.setContractName(order.getContractName());
            response.setCustomerUnit(order.getCustomerUnit());
            response.setPaymentPlan(order.getPaymentPlan());
            response.setServiceDate(order.getServiceDate());
            response.setRegion(order.getRegion());
            response.setMaintenancePlanAttachment(order.getMaintenancePlanAttachment());
            response.setReplacementPartsListAttachment(order.getReplacementPartsListAttachment());
            response.setCustomerRequestAttachment(order.getCustomerRequestAttachment());
            response.setRemark(order.getRemark());
            response.setHandlerId(order.getHandlerId());
            response.setHandlerName(order.getHandlerName());
            response.setServiceStatus(order.getServiceStatus());
            response.setNeedsApproval(order.getNeedsApproval());
            response.setApprovalStatus(order.getApprovalStatus());
            response.setCreateTime(order.getCreateTime());
            response.setUpdateTime(order.getUpdateTime());
            response.setCompletionDate(order.getCompletionDate());
            response.setDeviceNumber(order.getDeviceNumber());
            response.setDeviceName(order.getDeviceName());
            response.setServiceSummary(order.getServiceSummary());
            response.setEvaluation(order.getEvaluation());
            response.setEvaluatorId(order.getEvaluatorId());
            response.setEvaluatorName(order.getEvaluatorName());
            response.setEvaluationTime(order.getEvaluationTime());
            response.setIsClosed(order.getIsClosed());
        }
        return response;
    }
}

