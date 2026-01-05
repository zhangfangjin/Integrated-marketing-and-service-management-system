package org.example.rootmanage.aftersales;

import org.example.rootmanage.aftersales.dto.AfterSalesExperienceRequest;
import org.example.rootmanage.aftersales.entity.AfterSalesExperience;
import org.example.rootmanage.aftersales.repository.AfterSalesExperienceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 售后经验服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("售后经验服务测试")
class AfterSalesExperienceServiceTest {

    @Mock
    private AfterSalesExperienceRepository experienceRepository;

    @InjectMocks
    private AfterSalesExperienceService experienceService;

    private UUID experienceId;
    private AfterSalesExperience experience;

    @BeforeEach
    void setUp() {
        experienceId = UUID.randomUUID();

        experience = new AfterSalesExperience();
        experience.setId(experienceId);
        experience.setCustomerName("测试客户");
        experience.setDeviceType("泵");
        experience.setCaseType("故障处理");
        experience.setCaseRegistrationDate(Date.valueOf(LocalDate.now()));
        experience.setExperienceSummary("测试经验总结");
    }

    @Test
    @DisplayName("创建售后经验 - 成功")
    void testCreate_Success() {
        // Given
        AfterSalesExperienceRequest request = new AfterSalesExperienceRequest();
        request.setCustomerName("新客户");
        request.setDeviceType("泵");
        request.setCaseType("故障处理");
        request.setCaseRegistrationDate(Date.valueOf(LocalDate.now()));
        request.setExperienceSummary("新的经验总结");

        when(experienceRepository.save(any(AfterSalesExperience.class))).thenAnswer(invocation -> {
            AfterSalesExperience e = invocation.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        // When
        AfterSalesExperience result = experienceService.create(request);

        // Then
        assertNotNull(result);
        assertEquals("新客户", result.getCustomerName());
        assertEquals("泵", result.getDeviceType());
        assertEquals("新的经验总结", result.getExperienceSummary());
        verify(experienceRepository, times(1)).save(any(AfterSalesExperience.class));
    }

    @Test
    @DisplayName("更新售后经验 - 成功")
    void testUpdate_Success() {
        // Given
        AfterSalesExperienceRequest request = new AfterSalesExperienceRequest();
        request.setCustomerName("更新后的客户");
        request.setDeviceType("泵");
        request.setCaseType("安装调试");
        request.setExperienceSummary("更新后的经验总结");

        when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
        when(experienceRepository.save(any(AfterSalesExperience.class))).thenReturn(experience);

        // When
        AfterSalesExperience result = experienceService.update(experienceId, request);

        // Then
        assertNotNull(result);
        assertEquals("更新后的客户", result.getCustomerName());
        assertEquals("安装调试", result.getCaseType());
        assertEquals("更新后的经验总结", result.getExperienceSummary());
        verify(experienceRepository, times(1)).findById(experienceId);
        verify(experienceRepository, times(1)).save(any(AfterSalesExperience.class));
    }

    @Test
    @DisplayName("搜索售后经验 - 成功")
    void testSearchExperiences_Success() {
        // Given
        String keyword = "测试";
        List<AfterSalesExperience> experiences = Arrays.asList(experience);
        when(experienceRepository.searchAfterSalesExperiences(keyword)).thenReturn(experiences);

        // When
        List<AfterSalesExperience> result = experienceService.searchAfterSalesExperiences(keyword);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(experienceRepository, times(1)).searchAfterSalesExperiences(keyword);
    }

    @Test
    @DisplayName("根据设备类型查找售后经验 - 成功")
    void testFindByDeviceType_Success() {
        // Given
        String deviceType = "泵";
        List<AfterSalesExperience> experiences = Arrays.asList(experience);
        when(experienceRepository.findByDeviceTypeOrderByCaseRegistrationDateDesc(deviceType)).thenReturn(experiences);

        // When
        List<AfterSalesExperience> result = experienceService.findByDeviceType(deviceType);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(experienceRepository, times(1)).findByDeviceTypeOrderByCaseRegistrationDateDesc(deviceType);
    }

    @Test
    @DisplayName("删除售后经验 - 成功")
    void testDelete_Success() {
        // Given
        when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));

        // When
        experienceService.delete(experienceId);

        // Then
        verify(experienceRepository, times(1)).findById(experienceId);
        verify(experienceRepository, times(1)).delete(experience);
    }

    @Test
    @DisplayName("根据ID查找售后经验 - 成功")
    void testFindById_Success() {
        // Given
        when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));

        // When
        AfterSalesExperience result = experienceService.findById(experienceId);

        // Then
        assertNotNull(result);
        assertEquals(experienceId, result.getId());
        assertEquals("测试客户", result.getCustomerName());
        verify(experienceRepository, times(1)).findById(experienceId);
    }
}




