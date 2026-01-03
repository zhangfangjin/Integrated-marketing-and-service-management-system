package org.example.rootmanage.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "权限管理平台 API");
        info.put("version", "1.0.0");
        info.put("status", "运行中");
        info.put("baseUrl", "/api");
        info.put("endpoints", Map.of(
            "认证", "/api/auth/login",
            "注册", "/api/register",
            "角色管理", "/api/roles",
            "选项管理", "/api/options",
            "模块管理", "/api/modules",
            "权限配置", "/api/permissions"
        ));
        info.put("documentation", "查看 README.md 或 API_EXAMPLES.md 了解详细 API 文档");
        return info;
    }
}

