package org.example.rootmanage.basicinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SalesAreaRequest {

    @NotBlank(message = "片区名称不能为空")
    private String areaName;

    @NotBlank(message = "片区编号不能为空")
    private String areaCode;

    private UUID superiorDepartmentId;

    private String remark;

    private Boolean active = true;
}

