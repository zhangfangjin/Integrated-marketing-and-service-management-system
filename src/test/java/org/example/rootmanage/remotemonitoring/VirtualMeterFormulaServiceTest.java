package org.example.rootmanage.remotemonitoring;

import org.example.rootmanage.remotemonitoring.dto.VirtualMeterFormulaRequest;
import org.example.rootmanage.remotemonitoring.entity.VirtualMeterFormula;
import org.example.rootmanage.remotemonitoring.repository.FormulaParameterRepository;
import org.example.rootmanage.remotemonitoring.repository.VirtualMeterFormulaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 虚拟表计公式服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("虚拟表计公式服务测试")
class VirtualMeterFormulaServiceTest {

    @Mock
    private VirtualMeterFormulaRepository formulaRepository;

    @Mock
    private FormulaParameterRepository parameterRepository;

    @InjectMocks
    private VirtualMeterFormulaService formulaService;

    private UUID formulaId;
    private VirtualMeterFormula formula;

    @BeforeEach
    void setUp() {
        formulaId = UUID.randomUUID();

        formula = new VirtualMeterFormula();
        formula.setId(formulaId);
        formula.setFormulaCode("F001");
        formula.setFormulaName("测试公式");
        formula.setExpression("P001 + P002");
        formula.setEnabled(true);
    }

    @Test
    @DisplayName("创建公式 - 成功")
    void testCreate_Success() {
        // Given
        VirtualMeterFormulaRequest request = new VirtualMeterFormulaRequest();
        request.setFormulaCode("F002");
        request.setFormulaName("新公式");
        request.setOutputPointId(UUID.randomUUID());
        request.setExpression("P001 * 2 + P002");
        request.setEnabled(true);

        when(formulaRepository.findByFormulaCode("F002")).thenReturn(Optional.empty());
        when(formulaRepository.save(any(VirtualMeterFormula.class))).thenAnswer(invocation -> {
            VirtualMeterFormula f = invocation.getArgument(0);
            f.setId(UUID.randomUUID());
            return f;
        });

        // When
        VirtualMeterFormula result = formulaService.create(request);

        // Then
        assertNotNull(result);
        assertEquals("F002", result.getFormulaCode());
        assertEquals("新公式", result.getFormulaName());
        assertEquals("P001 * 2 + P002", result.getExpression());
        verify(formulaRepository, times(1)).findByFormulaCode("F002");
        verify(formulaRepository, times(1)).save(any(VirtualMeterFormula.class));
    }

    @Test
    @DisplayName("创建公式 - 编码已存在，抛出异常")
    void testCreate_FormulaCodeExists() {
        // Given
        VirtualMeterFormulaRequest request = new VirtualMeterFormulaRequest();
        request.setFormulaCode("F001");
        request.setFormulaName("测试公式");
        request.setOutputPointId(UUID.randomUUID());
        request.setExpression("P001 + P002");

        when(formulaRepository.findByFormulaCode("F001")).thenReturn(Optional.of(formula));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            formulaService.create(request);
        });
        verify(formulaRepository, times(1)).findByFormulaCode("F001");
        verify(formulaRepository, never()).save(any());
    }

    @Test
    @DisplayName("更新公式 - 成功")
    void testUpdate_Success() {
        // Given
        VirtualMeterFormulaRequest request = new VirtualMeterFormulaRequest();
        request.setFormulaCode("F001");
        request.setFormulaName("更新后的公式");
        request.setOutputPointId(UUID.randomUUID());
        request.setExpression("P001 + P002 * 2");
        request.setEnabled(false);

        when(formulaRepository.findById(formulaId)).thenReturn(Optional.of(formula));
        // 注意：当formulaCode相同时，不会调用findByFormulaCode，所以不需要mock
        when(formulaRepository.save(any(VirtualMeterFormula.class))).thenReturn(formula);

        // When
        VirtualMeterFormula result = formulaService.update(formulaId, request);

        // Then
        assertNotNull(result);
        assertEquals("更新后的公式", result.getFormulaName());
        assertEquals("P001 + P002 * 2", result.getExpression());
        verify(formulaRepository, times(1)).findById(formulaId);
        verify(formulaRepository, never()).findByFormulaCode(anyString());
        verify(parameterRepository, times(1)).deleteByFormulaId(formulaId);
        verify(formulaRepository, times(1)).save(any(VirtualMeterFormula.class));
    }

    @Test
    @DisplayName("删除公式 - 成功")
    void testDelete_Success() {
        // Given
        when(formulaRepository.findById(formulaId)).thenReturn(Optional.of(formula));

        // When
        formulaService.delete(formulaId);

        // Then
        verify(formulaRepository, times(1)).findById(formulaId);
        verify(parameterRepository, times(1)).deleteByFormulaId(formulaId);
        verify(formulaRepository, times(1)).delete(formula);
    }

    @Test
    @DisplayName("根据ID查找公式 - 成功")
    void testFindById_Success() {
        // Given
        when(formulaRepository.findById(formulaId)).thenReturn(Optional.of(formula));

        // When
        VirtualMeterFormula result = formulaService.findById(formulaId);

        // Then
        assertNotNull(result);
        assertEquals(formulaId, result.getId());
        assertEquals("F001", result.getFormulaCode());
        verify(formulaRepository, times(1)).findById(formulaId);
    }
}


