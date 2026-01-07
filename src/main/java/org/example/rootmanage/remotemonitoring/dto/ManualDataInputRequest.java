package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 手动录入数据请求DTO
 */
@Data
public class ManualDataInputRequest {

    /**
     * 点位ID
     */
    @NotNull(message = "点位ID不能为空")
    private UUID dataPointId;

    /**
     * 采集时间
     */
    private LocalDateTime collectionTime;

    /**
     * 采集值
     */
    @NotNull(message = "采集值不能为空")
    private Double value;

    /**
     * 录入人ID
     */
    private UUID inputById;

    /**
     * 录入人姓名
     */
    private String inputByName;

    /**
     * 备注
     */
    private String remark;
}

















