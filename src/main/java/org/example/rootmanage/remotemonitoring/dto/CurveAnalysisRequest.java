package org.example.rootmanage.remotemonitoring.dto;

import lombok.Data;
import org.example.rootmanage.remotemonitoring.entity.StatisticsPeriod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 曲线分析请求DTO
 */
@Data
public class CurveAnalysisRequest {

    /**
     * 点位ID列表
     */
    private List<UUID> dataPointIds;

    /**
     * 分析模型ID
     */
    private UUID analysisModelId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 时间粒度
     */
    private StatisticsPeriod period;

    /**
     * 曲线分组（用于分类曲线分析）
     */
    private String curveGroup;
}

















