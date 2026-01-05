package org.example.rootmanage.remotemonitoring.entity;

/**
 * 点位类型枚举
 */
public enum PointType {
    /**
     * 实点（通过物理设备采集的数据点）
     */
    REAL("实点"),

    /**
     * 虚点（通过公式计算得到的数据点）
     */
    VIRTUAL("虚点");

    private final String description;

    PointType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}





