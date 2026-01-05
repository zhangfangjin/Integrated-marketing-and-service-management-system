package org.example.rootmanage.remotemonitoring;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.VirtualMeterFormulaRequest;
import org.example.rootmanage.remotemonitoring.entity.VirtualMeterFormula;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 虚拟表计公式控制器
 * 提供公式配置的REST API
 */
@RestController
@RequestMapping("/api/remote-monitoring/formulas")
@RequiredArgsConstructor
public class VirtualMeterFormulaController {

    private final VirtualMeterFormulaService formulaService;

    /**
     * 获取所有公式（支持关键词搜索）
     */
    @GetMapping
    public List<VirtualMeterFormula> findAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return formulaService.searchFormulas(keyword);
        }
        return formulaService.findAll();
    }

    /**
     * 获取启用的公式
     */
    @GetMapping("/enabled")
    public List<VirtualMeterFormula> findEnabled() {
        return formulaService.findEnabled();
    }

    /**
     * 根据ID获取公式
     */
    @GetMapping("/{id}")
    public VirtualMeterFormula findById(@PathVariable UUID id) {
        return formulaService.findById(id);
    }

    /**
     * 创建公式
     */
    @PostMapping
    public VirtualMeterFormula create(@Valid @RequestBody VirtualMeterFormulaRequest request) {
        return formulaService.create(request);
    }

    /**
     * 更新公式
     */
    @PutMapping("/{id}")
    public VirtualMeterFormula update(@PathVariable UUID id, @Valid @RequestBody VirtualMeterFormulaRequest request) {
        return formulaService.update(id, request);
    }

    /**
     * 删除公式
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        formulaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}





