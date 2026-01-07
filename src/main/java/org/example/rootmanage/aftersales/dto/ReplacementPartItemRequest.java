package org.example.rootmanage.aftersales.dto;

import lombok.Data;

/**
 * 换件清单项请求DTO
 */
@Data
public class ReplacementPartItemRequest {

    /**
     * 零件图号
     */
    private String partDrawingNumber;

    /**
     * 名称
     */
    private String partName;

    /**
     * 材料
     */
    private String material;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 排序序号
     */
    private Integer orderNo;
}

















