package org.example.rootmanage.remotemonitoring.entity;

/**
 * 空间节点类型枚举
 */
public enum SpaceNodeType {
    /**
     * 公司（销售对象公司）
     */
    COMPANY("公司"),

    /**
     * 区域
     */
    REGION("区域"),

    /**
     * 建筑
     */
    BUILDING("建筑"),

    /**
     * 楼层
     */
    FLOOR("楼层"),

    /**
     * 房间
     */
    ROOM("房间"),

    /**
     * 其他
     */
    OTHER("其他");

    private final String description;

    SpaceNodeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

















