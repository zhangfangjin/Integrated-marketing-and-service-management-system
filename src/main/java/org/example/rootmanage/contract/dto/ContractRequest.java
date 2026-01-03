package org.example.rootmanage.contract.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

/**
 * 合同创建/更新请求DTO
 */
@Data
public class ContractRequest {

    /**
     * 合同编号
     */
    @NotBlank
    private String contractNumber;

    /**
     * 合同名称
     */
    @NotBlank
    private String contractName;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 销售机会
     */
    private String salesOpportunity;

    /**
     * 签订日期
     */
    private Date signingDate;

    /**
     * 排产日期
     */
    private Date scheduleDate;

    /**
     * 交货日期
     */
    private Date deliveryDate;

    /**
     * 货物到站
     */
    private String deliveryStation;

    /**
     * 运费支付
     */
    private String freightPayment;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 付款方式
     */
    private String paymentMethod;

    /**
     * 总价（元）
     */
    private Double totalPrice;

    /**
     * 备注
     */
    private String contractRemark;

    /**
     * 订货单位
     */
    private String orderingUnit;

    /**
     * 订货代表
     */
    private String orderingRepresentative;

    /**
     * 订货电话
     */
    private String orderingPhone;

    /**
     * 订货地址
     */
    private String orderingAddress;

    /**
     * 订货邮编
     */
    private String orderingPostcode;

    /**
     * 订货片区
     */
    private String orderingArea;

    /**
     * 附件路径
     */
    private String attachment;

    /**
     * 负责人ID
     */
    private UUID managerId;

    /**
     * 经办部门
     */
    private String handlerDepartment;

    /**
     * 经办人
     */
    private String handlerName;

    /**
     * 经办日期
     */
    private Date handleDate;

    /**
     * 合同细目列表
     */
    private List<ContractDetailRequest> details;

    /**
     * 付款阶段列表
     */
    private List<ContractPaymentStageRequest> paymentStages;

    /**
     * 占比划分列表
     */
    private List<ContractProportionRequest> proportions;
}

