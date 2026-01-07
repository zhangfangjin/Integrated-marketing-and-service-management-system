package org.example.rootmanage.remotemonitoring;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.AnalysisModelPointRequest;
import org.example.rootmanage.remotemonitoring.dto.AnalysisModelRequest;
import org.example.rootmanage.remotemonitoring.entity.AnalysisModel;
import org.example.rootmanage.remotemonitoring.entity.AnalysisModelPoint;
import org.example.rootmanage.remotemonitoring.entity.AnalysisModelType;
import org.example.rootmanage.remotemonitoring.repository.AnalysisModelPointRepository;
import org.example.rootmanage.remotemonitoring.repository.AnalysisModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 分析模型服务类
 * 提供分析模型的管理功能
 */
@Service
@RequiredArgsConstructor
public class AnalysisModelService {

    private final AnalysisModelRepository analysisModelRepository;
    private final AnalysisModelPointRepository analysisModelPointRepository;

    /**
     * 获取所有分析模型
     */
    @Transactional(readOnly = true)
    public List<AnalysisModel> findAll() {
        return analysisModelRepository.findAll();
    }

    /**
     * 获取根模型（树形结构）
     */
    @Transactional(readOnly = true)
    public List<AnalysisModel> findRootModels() {
        return analysisModelRepository.findByParentIdIsNullOrderBySortOrder();
    }

    /**
     * 根据父模型ID获取子模型
     */
    @Transactional(readOnly = true)
    public List<AnalysisModel> findByParentId(UUID parentId) {
        return analysisModelRepository.findByParentIdOrderBySortOrder(parentId);
    }

    /**
     * 根据模型类型查找
     */
    @Transactional(readOnly = true)
    public List<AnalysisModel> findByModelType(AnalysisModelType modelType) {
        return analysisModelRepository.findByModelTypeOrderBySortOrder(modelType);
    }

    /**
     * 搜索分析模型
     */
    @Transactional(readOnly = true)
    public List<AnalysisModel> searchAnalysisModels(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return analysisModelRepository.searchAnalysisModels(keyword.trim());
        }
        return analysisModelRepository.findAll();
    }

    /**
     * 根据ID查找分析模型
     */
    @Transactional(readOnly = true)
    public AnalysisModel findById(UUID id) {
        return analysisModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分析模型不存在"));
    }

    /**
     * 获取分析模型的关联点位列表
     */
    @Transactional(readOnly = true)
    public List<AnalysisModelPoint> findModelPoints(UUID modelId) {
        return analysisModelPointRepository.findByAnalysisModelIdOrderBySortOrder(modelId);
    }

    /**
     * 根据曲线分组获取点位
     */
    @Transactional(readOnly = true)
    public List<AnalysisModelPoint> findModelPointsByCurveGroup(UUID modelId, String curveGroup) {
        return analysisModelPointRepository.findByAnalysisModelIdAndCurveGroupOrderBySortOrder(modelId, curveGroup);
    }

    /**
     * 创建分析模型
     */
    @Transactional
    public AnalysisModel create(AnalysisModelRequest request) {
        // 检查编码是否已存在
        analysisModelRepository.findByModelCode(request.getModelCode())
                .ifPresent(m -> {
                    throw new IllegalStateException("分析模型编码已存在");
                });

        AnalysisModel model = new AnalysisModel();
        model.setModelCode(request.getModelCode());
        model.setModelName(request.getModelName());
        model.setModelType(request.getModelType() != null ? request.getModelType() : AnalysisModelType.DEVICE);
        model.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        model.setDescription(request.getDescription());
        model.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        model.setRemark(request.getRemark());

        // 设置父模型和层级
        if (request.getParentId() != null) {
            AnalysisModel parent = analysisModelRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("父模型不存在"));
            model.setParentId(request.getParentId());
            model.setLevel(parent.getLevel() + 1);
        } else {
            model.setLevel(1);
        }

        AnalysisModel savedModel = analysisModelRepository.save(model);

        // 保存关联点位
        if (request.getPoints() != null) {
            for (AnalysisModelPointRequest pointRequest : request.getPoints()) {
                AnalysisModelPoint point = new AnalysisModelPoint();
                point.setAnalysisModelId(savedModel.getId());
                point.setDataPointId(pointRequest.getDataPointId());
                point.setDisplayName(pointRequest.getDisplayName());
                point.setCurveGroup(pointRequest.getCurveGroup());
                point.setCurveColor(pointRequest.getCurveColor());
                point.setYAxisMin(pointRequest.getYAxisMin());
                point.setYAxisMax(pointRequest.getYAxisMax());
                point.setSortOrder(pointRequest.getSortOrder() != null ? pointRequest.getSortOrder() : 0);
                point.setRemark(pointRequest.getRemark());
                analysisModelPointRepository.save(point);
            }
        }

        return savedModel;
    }

    /**
     * 更新分析模型
     */
    @Transactional
    public AnalysisModel update(UUID id, AnalysisModelRequest request) {
        AnalysisModel model = analysisModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分析模型不存在"));

        // 检查编码是否被其他模型使用
        if (!model.getModelCode().equals(request.getModelCode())) {
            analysisModelRepository.findByModelCode(request.getModelCode())
                    .ifPresent(m -> {
                        throw new IllegalStateException("分析模型编码已被其他模型使用");
                    });
        }

        model.setModelCode(request.getModelCode());
        model.setModelName(request.getModelName());
        if (request.getModelType() != null) {
            model.setModelType(request.getModelType());
        }
        if (request.getSortOrder() != null) {
            model.setSortOrder(request.getSortOrder());
        }
        model.setDescription(request.getDescription());
        if (request.getEnabled() != null) {
            model.setEnabled(request.getEnabled());
        }
        model.setRemark(request.getRemark());

        // 更新父模型
        if (request.getParentId() != null && !request.getParentId().equals(model.getParentId())) {
            if (request.getParentId().equals(id)) {
                throw new IllegalArgumentException("不能将模型设置为自己的父模型");
            }
            AnalysisModel parent = analysisModelRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("父模型不存在"));
            model.setParentId(request.getParentId());
            model.setLevel(parent.getLevel() + 1);
        } else if (request.getParentId() == null && model.getParentId() != null) {
            model.setParentId(null);
            model.setLevel(1);
        }

        // 更新关联点位（先删除再新增）
        analysisModelPointRepository.deleteByAnalysisModelId(id);
        if (request.getPoints() != null) {
            for (AnalysisModelPointRequest pointRequest : request.getPoints()) {
                AnalysisModelPoint point = new AnalysisModelPoint();
                point.setAnalysisModelId(id);
                point.setDataPointId(pointRequest.getDataPointId());
                point.setDisplayName(pointRequest.getDisplayName());
                point.setCurveGroup(pointRequest.getCurveGroup());
                point.setCurveColor(pointRequest.getCurveColor());
                point.setYAxisMin(pointRequest.getYAxisMin());
                point.setYAxisMax(pointRequest.getYAxisMax());
                point.setSortOrder(pointRequest.getSortOrder() != null ? pointRequest.getSortOrder() : 0);
                point.setRemark(pointRequest.getRemark());
                analysisModelPointRepository.save(point);
            }
        }

        return analysisModelRepository.save(model);
    }

    /**
     * 删除分析模型
     */
    @Transactional
    public void delete(UUID id) {
        AnalysisModel model = analysisModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分析模型不存在"));

        // 检查是否有子模型
        List<AnalysisModel> children = analysisModelRepository.findByParentIdOrderBySortOrder(id);
        if (!children.isEmpty()) {
            throw new IllegalStateException("该模型存在子模型，无法删除");
        }

        analysisModelPointRepository.deleteByAnalysisModelId(id);
        analysisModelRepository.delete(model);
    }
}

















