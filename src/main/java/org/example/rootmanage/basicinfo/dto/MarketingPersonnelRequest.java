package org.example.rootmanage.basicinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class MarketingPersonnelRequest {

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String gender; // 性别

    private LocalDate birthday; // 生日

    private String contactInfo; // 联系方式

    private UUID responsibleAreaId; // 负责区域ID

    private String position; // 职务

    private String remark; // 备注

    private Boolean active = true;
}

