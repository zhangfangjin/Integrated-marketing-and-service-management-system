package org.example.rootmanage.remotemonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.rootmanage.remotemonitoring.entity.SpaceNodeType;

import java.util.UUID;

/**
 * 空间节点请求DTO
 */
@Data
public class SpaceNodeRequest {

    /**
     * 节点编码（唯一标识）
     */
    @NotBlank(message = "节点编码不能为空")
    private String nodeCode;

    /**
     * 节点名称
     */
    @NotBlank(message = "节点名称不能为空")
    private String nodeName;

    /**
     * 节点类型
     */
    private SpaceNodeType nodeType;

    /**
     * 父节点ID
     */
    private UUID parentId;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 地址
     */
    private String address;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 节点描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;
}

















