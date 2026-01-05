package org.example.rootmanage.remotemonitoring.entity;

/**
 * 数据来源枚举
 */
public enum DataSource {
    /**
     * 自动采集
     */
    AUTO("自动采集"),

    /**
     * 手动录入
     */
    MANUAL("手动录入"),

    /**
     * 公式计算
     */
    CALCULATED("公式计算");

    private final String description;

    DataSource(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}





