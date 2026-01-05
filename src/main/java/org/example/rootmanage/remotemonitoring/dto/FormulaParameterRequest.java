package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.rootmanage.remotemonitoring.entity.FormulaOperator;

import java.util.UUID;

/**
 * 公式参数请求DTO
 */
@Data
public class FormulaParameterRequest {

    /**
     * 参数名称
     */
    @NotBlank(message = "参数名称不能为空")
    private String parameterName;

    /**
     * 关联点位ID
     */
    @NotNull(message = "关联点位ID不能为空")
    private UUID dataPointId;

    /**
     * 系数/权重
     */
    private Double coefficient;

    /**
     * 运算符
     */
    private FormulaOperator operator;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}





