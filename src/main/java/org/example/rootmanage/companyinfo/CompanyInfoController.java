package org.example.rootmanage.companyinfo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.companyinfo.dto.CompanyInfoReleaseRequest;
import org.example.rootmanage.companyinfo.entity.CompanyInfoRelease;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/company-info")
@RequiredArgsConstructor
@Validated
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;

    /**
     * 查询信息发布列表
     */
    @GetMapping("/releases")
    public ResponseEntity<List<CompanyInfoRelease>> getReleases(
            @RequestParam(required = false) String keyword) {
        List<CompanyInfoRelease> releases = companyInfoService.findAllReleases(keyword);
        return ResponseEntity.ok(releases);
    }

    /**
     * 根据ID查询信息发布
     */
    @GetMapping("/releases/{id}")
    public ResponseEntity<CompanyInfoRelease> getReleaseById(@PathVariable UUID id) {
        return companyInfoService.findReleaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建信息发布
     */
    @PostMapping("/releases")
    public ResponseEntity<CompanyInfoRelease> createRelease(
            @Valid @RequestBody CompanyInfoReleaseRequest request) {
        CompanyInfoRelease release = companyInfoService.createRelease(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(release);
    }

    /**
     * 更新信息发布
     */
    @PutMapping("/releases/{id}")
    public ResponseEntity<CompanyInfoRelease> updateRelease(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyInfoReleaseRequest request) {
        CompanyInfoRelease release = companyInfoService.updateRelease(id, request);
        return ResponseEntity.ok(release);
    }

    /**
     * 删除信息发布
     */
    @DeleteMapping("/releases/{id}")
    public ResponseEntity<Void> deleteRelease(@PathVariable UUID id) {
        companyInfoService.deleteRelease(id);
        return ResponseEntity.noContent().build();
    }
}

