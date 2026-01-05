package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * 分析模型点位关联请求DTO
 */
@Data
public class AnalysisModelPointRequest {

    /**
     * 关联点位ID
     */
    @NotNull(message = "关联点位ID不能为空")
    private UUID dataPointId;

    /**
     * 点位显示名称
     */
    private String displayName;

    /**
     * 曲线分析分组
     */
    private String curveGroup;

    /**
     * 曲线颜色
     */
    private String curveColor;

    /**
     * Y轴最小值
     */
    private Double yAxisMin;

    /**
     * Y轴最大值
     */
    private Double yAxisMax;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}





