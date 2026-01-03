package org.example.rootmanage.auth;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.auth.dto.LoginRequest;
import org.example.rootmanage.auth.dto.LoginResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Validated LoginRequest request) {
        return authService.login(request);
    }
}

