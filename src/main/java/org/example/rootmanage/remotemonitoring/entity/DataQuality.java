package org.example.rootmanage.remotemonitoring.entity;

/**
 * 数据质量枚举
 */
public enum DataQuality {
    /**
     * 良好
     */
    GOOD("良好"),

    /**
     * 坏数据
     */
    BAD("坏数据"),

    /**
     * 不确定
     */
    UNCERTAIN("不确定");

    private final String description;

    DataQuality(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

















