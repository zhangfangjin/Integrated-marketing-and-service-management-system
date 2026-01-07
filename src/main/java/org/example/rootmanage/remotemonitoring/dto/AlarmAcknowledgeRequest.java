package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * 报警确认请求DTO
 */
@Data
public class AlarmAcknowledgeRequest {

    /**
     * 确认人ID
     */
    @NotNull(message = "确认人ID不能为空")
    private UUID acknowledgedById;

    /**
     * 确认人姓名
     */
    private String acknowledgedByName;

    /**
     * 处理备注
     */
    private String handleRemark;
}

















