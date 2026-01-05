package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.rootmanage.remotemonitoring.entity.AnalysisModelType;

import java.util.List;
import java.util.UUID;

/**
 * 分析模型请求DTO
 */
@Data
public class AnalysisModelRequest {

    /**
     * 分析模型编码
     */
    @NotBlank(message = "分析模型编码不能为空")
    private String modelCode;

    /**
     * 分析模型名称
     */
    @NotBlank(message = "分析模型名称不能为空")
    private String modelName;

    /**
     * 分析模型类型
     */
    private AnalysisModelType modelType;

    /**
     * 父模型ID
     */
    private UUID parentId;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 关联的点位列表
     */
    private List<AnalysisModelPointRequest> points;

    /**
     * 备注
     */
    private String remark;
}





