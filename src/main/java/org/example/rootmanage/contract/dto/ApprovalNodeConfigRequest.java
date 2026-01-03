package org.example.rootmanage.contract.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * 审批节点配置请求DTO
 */
@Data
public class ApprovalNodeConfigRequest {

    /**
     * 合同ID
     */
    private UUID contractId;

    /**
     * 片区负责人审批人列表
     */
    private List<ApproverRequest> areaManagers;

    /**
     * 部门负责人审批人列表
     */
    private List<ApproverRequest> departmentHeads;

    /**
     * 公司领导审批人列表
     */
    private List<ApproverRequest> companyLeaders;

    /**
     * 财务总监审批人列表
     */
    private List<ApproverRequest> financialDirectors;

    /**
     * 审批人请求DTO
     */
    @Data
    public static class ApproverRequest {
        /**
         * 审核人ID
         */
        private UUID approverId;

        /**
         * 审核人姓名
         */
        private String approverName;
    }
}

