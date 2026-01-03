package org.example.rootmanage.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 身份证验证响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdCardCheckResponse {
    private boolean registered;
    private String username;  // 账号名
    private String name;      // 姓名
}

