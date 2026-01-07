package org.example.rootmanage.remotemonitoring;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.AnalysisModelRequest;
import org.example.rootmanage.remotemonitoring.entity.AnalysisModel;
import org.example.rootmanage.remotemonitoring.entity.AnalysisModelPoint;
import org.example.rootmanage.remotemonitoring.entity.AnalysisModelType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 分析模型控制器
 * 提供分析模型配置的REST API
 */
@RestController
@RequestMapping("/api/remote-monitoring/analysis-models")
@RequiredArgsConstructor
public class AnalysisModelController {

    private final AnalysisModelService analysisModelService;

    /**
     * 获取所有分析模型（支持关键词搜索）
     */
    @GetMapping
    public List<AnalysisModel> findAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return analysisModelService.searchAnalysisModels(keyword);
        }
        return analysisModelService.findAll();
    }

    /**
     * 获取分析模型树形结构（根模型）
     */
    @GetMapping("/tree")
    public List<AnalysisModel> getTree() {
        return analysisModelService.findRootModels();
    }

    /**
     * 根据父模型ID获取子模型
     */
    @GetMapping("/parent/{parentId}")
    public List<AnalysisModel> findByParentId(@PathVariable UUID parentId) {
        return analysisModelService.findByParentId(parentId);
    }

    /**
     * 根据模型类型查找
     */
    @GetMapping("/type/{modelType}")
    public List<AnalysisModel> findByModelType(@PathVariable AnalysisModelType modelType) {
        return analysisModelService.findByModelType(modelType);
    }

    /**
     * 根据ID获取分析模型
     */
    @GetMapping("/{id}")
    public AnalysisModel findById(@PathVariable UUID id) {
        return analysisModelService.findById(id);
    }

    /**
     * 获取分析模型的关联点位列表
     */
    @GetMapping("/{id}/points")
    public List<AnalysisModelPoint> findModelPoints(@PathVariable UUID id) {
        return analysisModelService.findModelPoints(id);
    }

    /**
     * 根据曲线分组获取点位
     */
    @GetMapping("/{id}/points/group/{curveGroup}")
    public List<AnalysisModelPoint> findModelPointsByCurveGroup(
            @PathVariable UUID id, @PathVariable String curveGroup) {
        return analysisModelService.findModelPointsByCurveGroup(id, curveGroup);
    }

    /**
     * 创建分析模型
     */
    @PostMapping
    public AnalysisModel create(@Valid @RequestBody AnalysisModelRequest request) {
        return analysisModelService.create(request);
    }

    /**
     * 更新分析模型
     */
    @PutMapping("/{id}")
    public AnalysisModel update(@PathVariable UUID id, @Valid @RequestBody AnalysisModelRequest request) {
        return analysisModelService.update(id, request);
    }

    /**
     * 删除分析模型
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        analysisModelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

















