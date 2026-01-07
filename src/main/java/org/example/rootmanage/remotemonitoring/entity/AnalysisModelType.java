package org.example.rootmanage.remotemonitoring.entity;

/**
 * 分析模型类型枚举
 */
public enum AnalysisModelType {
    /**
     * 按设备分类
     */
    DEVICE("按设备分类"),

    /**
     * 按销售对象分类
     */
    CUSTOMER("按销售对象分类"),

    /**
     * 按区域分类
     */
    REGION("按区域分类"),

    /**
     * 按设备类型分类
     */
    DEVICE_TYPE("按设备类型分类"),

    /**
     * 综合分析
     */
    COMPREHENSIVE("综合分析");

    private final String description;

    AnalysisModelType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

















