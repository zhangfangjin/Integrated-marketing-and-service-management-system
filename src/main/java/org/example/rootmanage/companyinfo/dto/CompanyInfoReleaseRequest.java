package org.example.rootmanage.companyinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * 公司信息发布请求DTO
 */
@Data
public class CompanyInfoReleaseRequest {

    @NotBlank(message = "标题不能为空")
    private String title; // 标题

    @NotNull(message = "类型不能为空")
    private UUID typeId; // 类型ID

    private String content; // 内容

    private String attachments; // 附件（存储附件路径，多个用逗号分隔）
}

