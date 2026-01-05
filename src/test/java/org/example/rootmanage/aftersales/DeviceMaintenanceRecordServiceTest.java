package org.example.rootmanage.aftersales;

import org.example.rootmanage.aftersales.dto.DeviceMaintenanceRecordRequest;
import org.example.rootmanage.aftersales.entity.Device;
import org.example.rootmanage.aftersales.entity.DeviceMaintenanceRecord;
import org.example.rootmanage.aftersales.repository.DeviceMaintenanceRecordRepository;
import org.example.rootmanage.aftersales.repository.DeviceRepository;
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
 * 设备维护记录服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("设备维护记录服务测试")
class DeviceMaintenanceRecordServiceTest {

    @Mock
    private DeviceMaintenanceRecordRepository maintenanceRecordRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceMaintenanceRecordService maintenanceRecordService;

    private UUID recordId;
    private UUID deviceId;
    private Device device;
    private DeviceMaintenanceRecord record;

    @BeforeEach
    void setUp() {
        recordId = UUID.randomUUID();
        deviceId = UUID.randomUUID();

        device = new Device();
        device.setId(deviceId);
        device.setDeviceNumber("DEV001");
        device.setDeviceName("测试设备");

        record = new DeviceMaintenanceRecord();
        record.setId(recordId);
        record.setDevice(device);
        record.setMaintenanceDate(Date.valueOf(LocalDate.now()));
        record.setFaultReason("故障原因");
        record.setSolution("解决方案");
        record.setPartsReplacement("更换零件");
    }

    @Test
    @DisplayName("创建维护记录 - 成功")
    void testCreate_Success() {
        // Given
        DeviceMaintenanceRecordRequest request = new DeviceMaintenanceRecordRequest();
        request.setDeviceId(deviceId);
        request.setMaintenanceDate(Date.valueOf(LocalDate.now()));
        request.setFaultReason("新故障");
        request.setSolution("新方案");
        request.setPartsReplacement("新零件");

        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        when(maintenanceRecordRepository.save(any(DeviceMaintenanceRecord.class))).thenAnswer(invocation -> {
            DeviceMaintenanceRecord r = invocation.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        // When
        DeviceMaintenanceRecord result = maintenanceRecordService.create(request);

        // Then
        assertNotNull(result);
        assertEquals(deviceId, result.getDevice().getId());
        assertEquals("新故障", result.getFaultReason());
        assertEquals("新方案", result.getSolution());
        verify(deviceRepository, times(1)).findById(deviceId);
        verify(maintenanceRecordRepository, times(1)).save(any(DeviceMaintenanceRecord.class));
    }

    @Test
    @DisplayName("创建维护记录 - 设备不存在，抛出异常")
    void testCreate_DeviceNotExists() {
        // Given
        DeviceMaintenanceRecordRequest request = new DeviceMaintenanceRecordRequest();
        request.setDeviceId(deviceId);

        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            maintenanceRecordService.create(request);
        });
        verify(deviceRepository, times(1)).findById(deviceId);
        verify(maintenanceRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("更新维护记录 - 成功")
    void testUpdate_Success() {
        // Given
        DeviceMaintenanceRecordRequest request = new DeviceMaintenanceRecordRequest();
        request.setDeviceId(deviceId);
        request.setFaultReason("更新后的故障");
        request.setSolution("更新后的方案");

        when(maintenanceRecordRepository.findById(recordId)).thenReturn(Optional.of(record));
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        when(maintenanceRecordRepository.save(any(DeviceMaintenanceRecord.class))).thenReturn(record);

        // When
        DeviceMaintenanceRecord result = maintenanceRecordService.update(recordId, request);

        // Then
        assertNotNull(result);
        assertEquals("更新后的故障", result.getFaultReason());
        assertEquals("更新后的方案", result.getSolution());
        verify(maintenanceRecordRepository, times(1)).findById(recordId);
        verify(maintenanceRecordRepository, times(1)).save(any(DeviceMaintenanceRecord.class));
    }

    @Test
    @DisplayName("根据设备ID查找维护记录 - 成功")
    void testFindByDeviceId_Success() {
        // Given
        List<DeviceMaintenanceRecord> records = Arrays.asList(record);
        when(maintenanceRecordRepository.findByDeviceId(deviceId)).thenReturn(records);

        // When
        List<DeviceMaintenanceRecord> result = maintenanceRecordService.findByDeviceId(deviceId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(recordId, result.get(0).getId());
        verify(maintenanceRecordRepository, times(1)).findByDeviceId(deviceId);
    }

    @Test
    @DisplayName("根据设备编号查找维护记录 - 成功")
    void testFindByDeviceNumber_Success() {
        // Given
        String deviceNumber = "DEV001";
        List<DeviceMaintenanceRecord> records = Arrays.asList(record);
        when(maintenanceRecordRepository.findByDeviceNumberOrderByMaintenanceDateDesc(deviceNumber)).thenReturn(records);

        // When
        List<DeviceMaintenanceRecord> result = maintenanceRecordService.findByDeviceNumber(deviceNumber);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(maintenanceRecordRepository, times(1)).findByDeviceNumberOrderByMaintenanceDateDesc(deviceNumber);
    }

    @Test
    @DisplayName("删除维护记录 - 成功")
    void testDelete_Success() {
        // Given
        when(maintenanceRecordRepository.findById(recordId)).thenReturn(Optional.of(record));

        // When
        maintenanceRecordService.delete(recordId);

        // Then
        verify(maintenanceRecordRepository, times(1)).findById(recordId);
        verify(maintenanceRecordRepository, times(1)).delete(record);
    }
}




