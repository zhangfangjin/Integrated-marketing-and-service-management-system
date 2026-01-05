package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.AfterSalesExperienceRequest;
import org.example.rootmanage.aftersales.entity.AfterSalesExperience;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 售后经验管理Controller
 */
@RestController
@RequestMapping("/api/after-sales-experiences")
@RequiredArgsConstructor
public class AfterSalesExperienceController {

    private final AfterSalesExperienceService experienceService;

    /**
     * 获取售后经验列表
     */
    @GetMapping
    public List<AfterSalesExperience> list(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String deviceType,
                                           @RequestParam(required = false) String caseType) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return experienceService.searchAfterSalesExperiences(keyword.trim());
        }
        if (deviceType != null && !deviceType.trim().isEmpty()) {
            return experienceService.findByDeviceType(deviceType.trim());
        }
        if (caseType != null && !caseType.trim().isEmpty()) {
            return experienceService.findByCaseType(caseType.trim());
        }
        return experienceService.findAll();
    }

    /**
     * 根据ID获取售后经验详情
     */
    @GetMapping("/{id}")
    public AfterSalesExperience getById(@PathVariable UUID id) {
        return experienceService.findById(id);
    }

    /**
     * 创建售后经验
     */
    @PostMapping
    public AfterSalesExperience create(@RequestBody @Validated AfterSalesExperienceRequest request) {
        return experienceService.create(request);
    }

    /**
     * 更新售后经验
     */
    @PutMapping("/{id}")
    public AfterSalesExperience update(@PathVariable UUID id, @RequestBody @Validated AfterSalesExperienceRequest request) {
        return experienceService.update(id, request);
    }

    /**
     * 删除售后经验
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        experienceService.delete(id);
    }
}





