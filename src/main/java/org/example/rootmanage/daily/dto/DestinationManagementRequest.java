package org.example.rootmanage.daily.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 去向管理请求DTO
 */
@Data
public class DestinationManagementRequest {

    @NotBlank(message = "营销活动名称不能为空")
    private String activityName; // 营销活动名称

    @NotBlank(message = "地点不能为空")
    private String location; // 地点

    @NotNull(message = "时间不能为空")
    private LocalDateTime time; // 时间

    private UUID userId; // 用户ID（可选，如果不提供则使用当前登录用户）

    private String remark; // 备注
}

