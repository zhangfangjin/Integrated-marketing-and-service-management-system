package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.rootmanage.remotemonitoring.entity.AttributeValueType;

/**
 * 表计属性请求DTO
 */
@Data
public class MeterAttributeRequest {

    /**
     * 属性名称
     */
    @NotBlank(message = "属性名称不能为空")
    private String attributeName;

    /**
     * 属性编码
     */
    @NotBlank(message = "属性编码不能为空")
    private String attributeCode;

    /**
     * 属性值
     */
    private String attributeValue;

    /**
     * 属性值类型
     */
    private AttributeValueType valueType;

    /**
     * 属性单位
     */
    private String unit;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}





