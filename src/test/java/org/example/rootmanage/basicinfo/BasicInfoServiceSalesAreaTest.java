package org.example.rootmanage.basicinfo;

import org.example.rootmanage.basicinfo.dto.SalesAreaRequest;
import org.example.rootmanage.basicinfo.entity.SalesArea;
import org.example.rootmanage.option.OptionItem;
import org.example.rootmanage.option.OptionItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 基本信息服务 - 销售片区管理功能测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("基本信息服务 - 销售片区管理测试")
class BasicInfoServiceSalesAreaTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerKeyPersonRepository customerKeyPersonRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SalesAreaRepository salesAreaRepository;

    @Mock
    private MarketingPersonnelRepository marketingPersonnelRepository;

    @Mock
    private OptionItemRepository optionItemRepository;

    @Mock
    private org.example.rootmanage.account.UserAccountRepository userAccountRepository;

    @InjectMocks
    private BasicInfoService basicInfoService;

    private UUID salesAreaId;
    private SalesArea salesArea;
    private SalesAreaRequest salesAreaRequest;
    private OptionItem superiorDepartment;

    @BeforeEach
    void setUp() {
        salesAreaId = UUID.randomUUID();

        // 创建测试销售片区实体
        salesArea = new SalesArea();
        salesArea.setId(salesAreaId);
        salesArea.setAreaName("上海片区");
        salesArea.setAreaCode("001");
        salesArea.setRemark("上海地区销售片区");
        salesArea.setActive(true);

        // 创建测试请求DTO
        salesAreaRequest = new SalesAreaRequest();
        salesAreaRequest.setAreaName("上海片区");
        salesAreaRequest.setAreaCode("001");
        salesAreaRequest.setRemark("上海地区销售片区");
        salesAreaRequest.setActive(true);

        // 创建测试选项项（上级部门）
        superiorDepartment = new OptionItem();
        superiorDepartment.setId(UUID.randomUUID());
        superiorDepartment.setTitle("成套处");
    }

    @Test
    @DisplayName("查询所有销售片区 - 成功")
    void testFindAllSalesAreas_Success() {
        // 准备数据
        List<SalesArea> salesAreas = Arrays.asList(salesArea);
        when(salesAreaRepository.findAll()).thenReturn(salesAreas);

        // 执行测试
        List<SalesArea> result = basicInfoService.findAllSalesAreas(null);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("上海片区", result.get(0).getAreaName());
        assertEquals("001", result.get(0).getAreaCode());
        verify(salesAreaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("查询销售片区 - 带关键词搜索")
    void testFindAllSalesAreas_WithKeyword() {
        // 准备数据
        List<SalesArea> salesAreas = Arrays.asList(salesArea);
        when(salesAreaRepository.searchSalesAreas("上海")).thenReturn(salesAreas);

        // 执行测试
        List<SalesArea> result = basicInfoService.findAllSalesAreas("上海");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("上海片区", result.get(0).getAreaName());
        verify(salesAreaRepository, times(1)).searchSalesAreas("上海");
    }

    @Test
    @DisplayName("创建销售片区 - 成功")
    void testCreateSalesArea_Success() {
        // 准备数据
        when(salesAreaRepository.findByAreaCode("001")).thenReturn(Optional.empty());
        when(salesAreaRepository.save(any(SalesArea.class))).thenReturn(salesArea);

        // 执行测试
        SalesArea result = basicInfoService.createSalesArea(salesAreaRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("上海片区", result.getAreaName());
        assertEquals("001", result.getAreaCode());
        assertEquals(true, result.getActive());
        verify(salesAreaRepository, times(1)).findByAreaCode("001");
        verify(salesAreaRepository, times(1)).save(any(SalesArea.class));
    }

    @Test
    @DisplayName("创建销售片区 - 带上级部门")
    void testCreateSalesArea_WithSuperiorDepartment() {
        // 准备数据
        UUID departmentId = superiorDepartment.getId();
        salesAreaRequest.setSuperiorDepartmentId(departmentId);

        when(salesAreaRepository.findByAreaCode("001")).thenReturn(Optional.empty());
        when(optionItemRepository.findById(departmentId)).thenReturn(Optional.of(superiorDepartment));
        when(salesAreaRepository.save(any(SalesArea.class))).thenReturn(salesArea);

        // 执行测试
        SalesArea result = basicInfoService.createSalesArea(salesAreaRequest);

        // 验证结果
        assertNotNull(result);
        verify(optionItemRepository, times(1)).findById(departmentId);
        verify(salesAreaRepository, times(1)).save(any(SalesArea.class));
    }

    @Test
    @DisplayName("创建销售片区 - 片区编号已存在")
    void testCreateSalesArea_AreaCodeExists() {
        // 准备数据
        when(salesAreaRepository.findByAreaCode("001")).thenReturn(Optional.of(salesArea));

        // 执行测试并验证异常
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> basicInfoService.createSalesArea(salesAreaRequest)
        );

        assertEquals("片区编号已存在", exception.getMessage());
        verify(salesAreaRepository, times(1)).findByAreaCode("001");
        verify(salesAreaRepository, never()).save(any(SalesArea.class));
    }

    @Test
    @DisplayName("创建销售片区 - 上级部门不存在")
    void testCreateSalesArea_SuperiorDepartmentNotFound() {
        // 准备数据
        UUID invalidDepartmentId = UUID.randomUUID();
        salesAreaRequest.setSuperiorDepartmentId(invalidDepartmentId);

        when(salesAreaRepository.findByAreaCode("001")).thenReturn(Optional.empty());
        when(optionItemRepository.findById(invalidDepartmentId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> basicInfoService.createSalesArea(salesAreaRequest)
        );

        assertEquals("上级部门选项不存在", exception.getMessage());
        verify(optionItemRepository, times(1)).findById(invalidDepartmentId);
        verify(salesAreaRepository, never()).save(any(SalesArea.class));
    }

    @Test
    @DisplayName("更新销售片区 - 成功")
    void testUpdateSalesArea_Success() {
        // 准备数据
        salesAreaRequest.setAreaName("更新后的片区名称");
        salesArea.setAreaName("更新后的片区名称");

        when(salesAreaRepository.findById(salesAreaId)).thenReturn(Optional.of(salesArea));
        when(salesAreaRepository.findByAreaCode("001")).thenReturn(Optional.of(salesArea));
        when(salesAreaRepository.save(any(SalesArea.class))).thenReturn(salesArea);

        // 执行测试
        SalesArea result = basicInfoService.updateSalesArea(salesAreaId, salesAreaRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("更新后的片区名称", result.getAreaName());
        verify(salesAreaRepository, times(1)).findById(salesAreaId);
        verify(salesAreaRepository, times(1)).save(any(SalesArea.class));
    }

    @Test
    @DisplayName("更新销售片区 - 销售片区不存在")
    void testUpdateSalesArea_NotFound() {
        // 准备数据
        when(salesAreaRepository.findById(salesAreaId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> basicInfoService.updateSalesArea(salesAreaId, salesAreaRequest)
        );

        assertEquals("销售片区不存在", exception.getMessage());
        verify(salesAreaRepository, times(1)).findById(salesAreaId);
        verify(salesAreaRepository, never()).save(any(SalesArea.class));
    }

    @Test
    @DisplayName("更新销售片区 - 片区编号已被其他片区使用")
    void testUpdateSalesArea_AreaCodeUsedByOther() {
        // 准备数据
        SalesArea otherArea = new SalesArea();
        otherArea.setId(UUID.randomUUID());
        otherArea.setAreaCode("001");

        when(salesAreaRepository.findById(salesAreaId)).thenReturn(Optional.of(salesArea));
        when(salesAreaRepository.findByAreaCode("001")).thenReturn(Optional.of(otherArea));

        // 执行测试并验证异常
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> basicInfoService.updateSalesArea(salesAreaId, salesAreaRequest)
        );

        assertEquals("片区编号已被其他片区使用", exception.getMessage());
        verify(salesAreaRepository, times(1)).findByAreaCode("001");
        verify(salesAreaRepository, never()).save(any(SalesArea.class));
    }

    @Test
    @DisplayName("删除销售片区 - 成功（软删除）")
    void testDeleteSalesArea_Success() {
        // 准备数据
        salesArea.setActive(true);
        when(salesAreaRepository.findById(salesAreaId)).thenReturn(Optional.of(salesArea));
        when(salesAreaRepository.save(any(SalesArea.class))).thenAnswer(invocation -> {
            SalesArea saved = invocation.getArgument(0);
            return saved;
        });

        // 执行测试
        assertDoesNotThrow(() -> basicInfoService.deleteSalesArea(salesAreaId));

        // 验证结果 - 软删除：将 active 设置为 false 并保存
        verify(salesAreaRepository, times(1)).findById(salesAreaId);
        verify(salesAreaRepository, times(1)).save(any(SalesArea.class));
        assertFalse(salesArea.getActive(), "删除后 active 应该为 false");
    }

    @Test
    @DisplayName("删除销售片区 - 销售片区不存在")
    void testDeleteSalesArea_NotFound() {
        // 准备数据
        when(salesAreaRepository.findById(salesAreaId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> basicInfoService.deleteSalesArea(salesAreaId)
        );

        assertEquals("销售片区不存在", exception.getMessage());
        verify(salesAreaRepository, times(1)).findById(salesAreaId);
        verify(salesAreaRepository, never()).save(any(SalesArea.class));
    }

    @Test
    @DisplayName("创建销售片区 - active默认为true")
    void testCreateSalesArea_ActiveDefaultTrue() {
        // 准备数据 - 不设置active
        salesAreaRequest.setActive(null);
        when(salesAreaRepository.findByAreaCode("001")).thenReturn(Optional.empty());
        when(salesAreaRepository.save(any(SalesArea.class))).thenAnswer(invocation -> {
            SalesArea saved = invocation.getArgument(0);
            saved.setId(salesAreaId);
            return saved;
        });

        // 执行测试
        SalesArea result = basicInfoService.createSalesArea(salesAreaRequest);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getActive());
    }
}

