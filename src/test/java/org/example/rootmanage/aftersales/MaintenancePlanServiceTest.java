package org.example.rootmanage.aftersales;

import org.example.rootmanage.aftersales.dto.MaintenancePlanRequest;
import org.example.rootmanage.aftersales.dto.ReplacementPartItemRequest;
import org.example.rootmanage.aftersales.entity.Device;
import org.example.rootmanage.aftersales.entity.MaintenancePlan;
import org.example.rootmanage.aftersales.entity.PlanType;
import org.example.rootmanage.aftersales.repository.DeviceRepository;
import org.example.rootmanage.aftersales.repository.MaintenancePlanRepository;
import org.example.rootmanage.aftersales.repository.ReplacementPartItemRepository;
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
 * 维护计划服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("维护计划服务测试")
class MaintenancePlanServiceTest {

    @Mock
    private MaintenancePlanRepository maintenancePlanRepository;

    @Mock
    private ReplacementPartItemRepository replacementPartItemRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private MaintenancePlanService maintenancePlanService;

    private UUID planId;
    private UUID deviceId;
    private MaintenancePlan plan;
    private Device device;

    @BeforeEach
    void setUp() {
        planId = UUID.randomUUID();
        deviceId = UUID.randomUUID();

        device = new Device();
        device.setId(deviceId);
        device.setDeviceNumber("DEV001");
        device.setDeviceModel("泵");

        plan = new MaintenancePlan();
        plan.setId(planId);
        plan.setPlanType(PlanType.SPECIFIC_DEVICE);
        plan.setDevice(device);
        plan.setMaintenanceCycle("每3个月");
        plan.setMaintenanceItems("检查、清洁、润滑");
        plan.setEnabled(true);
    }

    @Test
    @DisplayName("创建维护计划 - 成功")
    void testCreate_Success() {
        // Given
        MaintenancePlanRequest request = new MaintenancePlanRequest();
        request.setPlanType(PlanType.SPECIFIC_DEVICE);
        request.setDeviceId(deviceId);
        request.setMaintenanceCycle("每6个月");
        request.setMaintenanceItems("全面检查");
        request.setEnabled(true);

        ReplacementPartItemRequest partRequest = new ReplacementPartItemRequest();
        partRequest.setPartDrawingNumber("PART001");
        partRequest.setPartName("密封圈");
        partRequest.setQuantity(2);
        request.setReplacementParts(Arrays.asList(partRequest));

        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        when(maintenancePlanRepository.save(any(MaintenancePlan.class))).thenAnswer(invocation -> {
            MaintenancePlan p = invocation.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        // When
        MaintenancePlan result = maintenancePlanService.create(request);

        // Then
        assertNotNull(result);
        assertEquals(PlanType.SPECIFIC_DEVICE, result.getPlanType());
        assertEquals("每6个月", result.getMaintenanceCycle());
        verify(deviceRepository, times(1)).findById(deviceId);
        verify(maintenancePlanRepository, times(1)).save(any(MaintenancePlan.class));
    }

    @Test
    @DisplayName("创建维护计划 - 设备不存在，抛出异常")
    void testCreate_DeviceNotExists() {
        // Given
        MaintenancePlanRequest request = new MaintenancePlanRequest();
        request.setPlanType(PlanType.SPECIFIC_DEVICE);
        request.setDeviceId(deviceId);

        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            maintenancePlanService.create(request);
        });
        verify(deviceRepository, times(1)).findById(deviceId);
        verify(maintenancePlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("根据设备ID查找维护计划 - 成功")
    void testFindByDeviceId_Success() {
        // Given
        when(maintenancePlanRepository.findByDeviceId(PlanType.SPECIFIC_DEVICE, deviceId)).thenReturn(Optional.of(plan));

        // When
        MaintenancePlan result = maintenancePlanService.findByDeviceId(deviceId);

        // Then
        assertNotNull(result);
        assertEquals(planId, result.getId());
        assertEquals(deviceId, result.getDevice().getId());
        verify(maintenancePlanRepository, times(1)).findByDeviceId(PlanType.SPECIFIC_DEVICE, deviceId);
    }

    @Test
    @DisplayName("根据设备类型查找维护计划 - 成功")
    void testFindByDeviceType_Success() {
        // Given
        String deviceType = "泵";
        when(maintenancePlanRepository.findByDeviceType(PlanType.DEVICE_TYPE, deviceType)).thenReturn(Optional.of(plan));

        // When
        MaintenancePlan result = maintenancePlanService.findByDeviceType(deviceType);

        // Then
        assertNotNull(result);
        verify(maintenancePlanRepository, times(1)).findByDeviceType(PlanType.DEVICE_TYPE, deviceType);
    }

    @Test
    @DisplayName("更新维护计划 - 成功")
    void testUpdate_Success() {
        // Given
        MaintenancePlanRequest request = new MaintenancePlanRequest();
        request.setPlanType(PlanType.SPECIFIC_DEVICE);
        request.setDeviceId(deviceId);
        request.setMaintenanceCycle("每12个月");
        request.setMaintenanceItems("大修");

        when(maintenancePlanRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        when(maintenancePlanRepository.save(any(MaintenancePlan.class))).thenReturn(plan);

        // When
        MaintenancePlan result = maintenancePlanService.update(planId, request);

        // Then
        assertNotNull(result);
        assertEquals("每12个月", result.getMaintenanceCycle());
        assertEquals("大修", result.getMaintenanceItems());
        verify(maintenancePlanRepository, times(1)).findById(planId);
        verify(maintenancePlanRepository, times(1)).save(any(MaintenancePlan.class));
    }

    @Test
    @DisplayName("删除维护计划 - 成功")
    void testDelete_Success() {
        // Given
        when(maintenancePlanRepository.findById(planId)).thenReturn(Optional.of(plan));

        // When
        maintenancePlanService.delete(planId);

        // Then
        verify(maintenancePlanRepository, times(1)).findById(planId);
        verify(replacementPartItemRepository, times(1)).deleteByMaintenancePlanId(planId);
        verify(maintenancePlanRepository, times(1)).delete(plan);
    }
}




