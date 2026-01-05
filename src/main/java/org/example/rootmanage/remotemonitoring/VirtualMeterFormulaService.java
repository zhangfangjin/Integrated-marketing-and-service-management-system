package org.example.rootmanage.remotemonitoring;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.FormulaParameterRequest;
import org.example.rootmanage.remotemonitoring.dto.VirtualMeterFormulaRequest;
import org.example.rootmanage.remotemonitoring.entity.FormulaOperator;
import org.example.rootmanage.remotemonitoring.entity.FormulaParameter;
import org.example.rootmanage.remotemonitoring.entity.VirtualMeterFormula;
import org.example.rootmanage.remotemonitoring.repository.FormulaParameterRepository;
import org.example.rootmanage.remotemonitoring.repository.VirtualMeterFormulaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 虚拟表计公式服务类
 * 提供虚拟表计公式的管理功能
 */
@Service
@RequiredArgsConstructor
public class VirtualMeterFormulaService {

    private final VirtualMeterFormulaRepository formulaRepository;
    private final FormulaParameterRepository parameterRepository;

    /**
     * 获取所有公式
     */
    @Transactional(readOnly = true)
    public List<VirtualMeterFormula> findAll() {
        return formulaRepository.findAll();
    }

    /**
     * 获取启用的公式
     */
    @Transactional(readOnly = true)
    public List<VirtualMeterFormula> findEnabled() {
        return formulaRepository.findByEnabled(true);
    }

    /**
     * 搜索公式
     */
    @Transactional(readOnly = true)
    public List<VirtualMeterFormula> searchFormulas(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return formulaRepository.searchFormulas(keyword.trim());
        }
        return formulaRepository.findAll();
    }

    /**
     * 根据ID查找公式
     */
    @Transactional(readOnly = true)
    public VirtualMeterFormula findById(UUID id) {
        return formulaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("公式不存在"));
    }

    /**
     * 创建公式
     */
    @Transactional
    public VirtualMeterFormula create(VirtualMeterFormulaRequest request) {
        // 检查编码是否已存在
        formulaRepository.findByFormulaCode(request.getFormulaCode())
                .ifPresent(f -> {
                    throw new IllegalStateException("公式编码已存在");
                });

        VirtualMeterFormula formula = new VirtualMeterFormula();
        formula.setFormulaCode(request.getFormulaCode());
        formula.setFormulaName(request.getFormulaName());
        formula.setOutputPointId(request.getOutputPointId());
        formula.setExpression(request.getExpression());
        formula.setDescription(request.getDescription());
        formula.setPrecision(request.getPrecision() != null ? request.getPrecision() : 2);
        formula.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        formula.setRemark(request.getRemark());

        VirtualMeterFormula savedFormula = formulaRepository.save(formula);

        // 保存参数
        if (request.getParameters() != null) {
            for (FormulaParameterRequest paramRequest : request.getParameters()) {
                FormulaParameter param = new FormulaParameter();
                param.setFormulaId(savedFormula.getId());
                param.setParameterName(paramRequest.getParameterName());
                param.setDataPointId(paramRequest.getDataPointId());
                param.setCoefficient(paramRequest.getCoefficient() != null ? paramRequest.getCoefficient() : 1.0);
                param.setOperator(paramRequest.getOperator() != null ? paramRequest.getOperator() : FormulaOperator.ADD);
                param.setSortOrder(paramRequest.getSortOrder() != null ? paramRequest.getSortOrder() : 0);
                param.setRemark(paramRequest.getRemark());
                parameterRepository.save(param);
            }
        }

        return savedFormula;
    }

    /**
     * 更新公式
     */
    @Transactional
    public VirtualMeterFormula update(UUID id, VirtualMeterFormulaRequest request) {
        VirtualMeterFormula formula = formulaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("公式不存在"));

        // 检查编码是否被其他公式使用
        if (!formula.getFormulaCode().equals(request.getFormulaCode())) {
            formulaRepository.findByFormulaCode(request.getFormulaCode())
                    .ifPresent(f -> {
                        throw new IllegalStateException("公式编码已被其他公式使用");
                    });
        }

        formula.setFormulaCode(request.getFormulaCode());
        formula.setFormulaName(request.getFormulaName());
        formula.setOutputPointId(request.getOutputPointId());
        formula.setExpression(request.getExpression());
        formula.setDescription(request.getDescription());
        if (request.getPrecision() != null) {
            formula.setPrecision(request.getPrecision());
        }
        if (request.getEnabled() != null) {
            formula.setEnabled(request.getEnabled());
        }
        formula.setRemark(request.getRemark());

        // 更新参数（先删除再新增）
        parameterRepository.deleteByFormulaId(id);
        if (request.getParameters() != null) {
            for (FormulaParameterRequest paramRequest : request.getParameters()) {
                FormulaParameter param = new FormulaParameter();
                param.setFormulaId(id);
                param.setParameterName(paramRequest.getParameterName());
                param.setDataPointId(paramRequest.getDataPointId());
                param.setCoefficient(paramRequest.getCoefficient() != null ? paramRequest.getCoefficient() : 1.0);
                param.setOperator(paramRequest.getOperator() != null ? paramRequest.getOperator() : FormulaOperator.ADD);
                param.setSortOrder(paramRequest.getSortOrder() != null ? paramRequest.getSortOrder() : 0);
                param.setRemark(paramRequest.getRemark());
                parameterRepository.save(param);
            }
        }

        return formulaRepository.save(formula);
    }

    /**
     * 删除公式
     */
    @Transactional
    public void delete(UUID id) {
        VirtualMeterFormula formula = formulaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("公式不存在"));

        parameterRepository.deleteByFormulaId(id);
        formulaRepository.delete(formula);
    }
}





