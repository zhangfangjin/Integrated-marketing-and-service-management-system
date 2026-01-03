package org.example.rootmanage.daily.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 周报管理请求DTO
 */
@Data
public class WeeklyReportRequest {

    @NotBlank(message = "周报名称不能为空")
    private String reportName; // 周报名称

    @NotNull(message = "周报时间不能为空")
    private LocalDateTime reportTime; // 周报时间

    private String content; // 周报内容

    private String remark; // 周报备注

    private UUID userId; // 用户ID（可选，如果不提供则使用当前登录用户）
}

