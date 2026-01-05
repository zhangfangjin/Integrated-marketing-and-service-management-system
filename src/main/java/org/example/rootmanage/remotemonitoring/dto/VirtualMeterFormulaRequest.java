package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * 虚拟表计公式请求DTO
 */
@Data
public class VirtualMeterFormulaRequest {

    /**
     * 公式编码
     */
    @NotBlank(message = "公式编码不能为空")
    private String formulaCode;

    /**
     * 公式名称
     */
    @NotBlank(message = "公式名称不能为空")
    private String formulaName;

    /**
     * 输出点位ID
     */
    @NotNull(message = "输出点位ID不能为空")
    private UUID outputPointId;

    /**
     * 公式表达式
     */
    @NotBlank(message = "公式表达式不能为空")
    private String expression;

    /**
     * 公式说明
     */
    private String description;

    /**
     * 公式参数列表
     */
    private List<FormulaParameterRequest> parameters;

    /**
     * 计算精度
     */
    private Integer precision;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;
}





