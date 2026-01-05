package org.example.rootmanage.remotemonitoring.entity;

/**
 * 统计周期枚举
 */
public enum StatisticsPeriod {
    /**
     * 小时统计
     */
    HOURLY("小时"),

    /**
     * 日统计
     */
    DAILY("日"),

    /**
     * 周统计
     */
    WEEKLY("周"),

    /**
     * 月统计
     */
    MONTHLY("月");

    private final String description;

    StatisticsPeriod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}





