package org.example.rootmanage.remotemonitoring.entity;

/**
 * 属性值类型枚举
 */
public enum AttributeValueType {
    /**
     * 字符串
     */
    STRING("字符串"),

    /**
     * 数值
     */
    NUMBER("数值"),

    /**
     * 日期
     */
    DATE("日期"),

    /**
     * 布尔值
     */
    BOOLEAN("布尔值");

    private final String description;

    AttributeValueType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}





