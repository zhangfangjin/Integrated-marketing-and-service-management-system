package org.example.rootmanage.basicinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TeamRequest {

    @NotBlank(message = "团队名称不能为空")
    private String teamName;

    private UUID leaderId;

    private UUID departmentId;

    private String description;

    private String remark;

    private Boolean active = true;
}

