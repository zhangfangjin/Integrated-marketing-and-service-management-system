package org.example.rootmanage.basicinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CustomerKeyPersonRequest {

    // customerId 从路径参数获取，不需要在请求体中验证
    private UUID customerId;

    @NotBlank(message = "联系人名称不能为空")
    private String name;

    private String code; // 联系人编码

    private String gender; // 性别

    private String salutation; // 称呼

    private String directSuperior; // 直接上级

    private String placeOfOrigin; // 籍贯

    private LocalDate birthday; // 生日

    private String maritalStatus; // 婚姻状况

    private String position; // 职务（决策者、部门主管、普通员工）

    private String hobbies; // 个人爱好

    private Boolean isPrimary = false; // 是否主要联系人

    private String contactInfo; // 联系方式

    private String remark; // 备注
}

