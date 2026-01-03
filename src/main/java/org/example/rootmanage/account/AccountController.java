package org.example.rootmanage.account;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.account.dto.IdCardCheckResponse;
import org.example.rootmanage.account.dto.RegistrationRequest;
import org.example.rootmanage.account.dto.RegistrationReviewRequest;
import org.example.rootmanage.account.dto.ResetPasswordRequest;
import org.example.rootmanage.account.dto.UpdatePersonnelRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 注册阶段：身份证校验
     * 如果已注册，返回账号和姓名
     */
    @GetMapping("/register/check")
    public ResponseEntity<IdCardCheckResponse> checkIdCard(@RequestParam String idCard) {
        return accountService.findByIdCard(idCard)
                .map(user -> ResponseEntity.ok(new IdCardCheckResponse(true, user.getUsername(), user.getName())))
                .orElseGet(() -> ResponseEntity.ok(new IdCardCheckResponse(false, null, null)));
    }

    /**
     * 提交注册
     */
    @PostMapping("/register")
    public UserAccount register(@RequestBody @Validated RegistrationRequest request) {
        return accountService.register(request);
    }

    /**
     * 待审核列表（管理员）
     */
    @GetMapping("/register/pending")
    public List<UserAccount> pending() {
        return accountService.findPending();
    }

    /**
     * 审核通过，默认密码 123456、账号=手机号
     */
    @PutMapping("/register/{id}/approve")
    public UserAccount approve(@PathVariable UUID id, @RequestBody @Validated RegistrationReviewRequest request) {
        return accountService.approve(id, request);
    }

    /**
     * 审核拒绝
     */
    @PutMapping("/register/{id}/reject")
    public UserAccount reject(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String remark = body != null ? body.get("remark") : null;
        return accountService.reject(id, remark);
    }

    /**
     * 账号列表
     */
    @GetMapping("/accounts")
    public List<UserAccount> accounts(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return accountService.searchPersonnel(keyword.trim());
        }
        return accountService.findAll();
    }

    /**
     * 重置密码
     */
    @PutMapping("/accounts/{id}/reset-password")
    public UserAccount resetPassword(@PathVariable UUID id, @RequestBody @Validated ResetPasswordRequest request) {
        return accountService.resetPassword(id, request);
    }

    /**
     * 修改角色
     */
    @PutMapping("/accounts/{id}/role")
    public UserAccount updateRole(@PathVariable UUID id, @RequestBody Map<String, UUID> body) {
        UUID roleId = body.get("roleId");
        return accountService.updateRole(id, roleId);
    }

    /**
     * 启用/禁用等状态调整
     */
    @PutMapping("/accounts/{id}/status")
    public UserAccount changeStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return accountService.changeStatus(id, AccountStatus.valueOf(status));
    }

    /**
     * 更新人员信息（人员档案管理）
     */
    @PutMapping("/accounts/{id}")
    public UserAccount updatePersonnel(@PathVariable UUID id, @RequestBody @Validated UpdatePersonnelRequest request) {
        return accountService.updatePersonnel(id, request);
    }

    /**
     * 删除人员（软删除）
     */
    @DeleteMapping("/accounts/{id}")
    public void deletePersonnel(@PathVariable UUID id) {
        accountService.deletePersonnel(id);
    }
}

