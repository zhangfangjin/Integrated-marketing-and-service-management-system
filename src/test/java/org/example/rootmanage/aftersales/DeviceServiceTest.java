package org.example.rootmanage.aftersales;

import org.example.rootmanage.aftersales.dto.DeviceRequest;
import org.example.rootmanage.aftersales.entity.Device;
import org.example.rootmanage.aftersales.repository.DeviceRepository;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.repository.ContractRepository;
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
 * 设备服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("设备服务测试")
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private DeviceService deviceService;

    private UUID deviceId;
    private UUID contractId;
    private Device device;
    private Contract contract;

    @BeforeEach
    void setUp() {
        deviceId = UUID.randomUUID();
        contractId = UUID.randomUUID();

        contract = new Contract();
        contract.setId(contractId);
        contract.setContractNumber("CT001");
        contract.setContractName("测试合同");

        device = new Device();
        device.setId(deviceId);
        device.setDeviceNumber("DEV001");
        device.setDeviceName("测试设备");
        device.setDeviceModel("MODEL001");
        device.setContractNumber("CT001");
    }

    @Test
    @DisplayName("创建设备 - 成功")
    void testCreate_Success() {
        // Given
        DeviceRequest request = new DeviceRequest();
        request.setDeviceNumber("DEV002");
        request.setDeviceName("新设备");
        request.setDeviceModel("MODEL002");
        request.setContractId(contractId);
        request.setProductionDate(Date.valueOf(LocalDate.now()));

        when(deviceRepository.findByDeviceNumber("DEV002")).thenReturn(Optional.empty());
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> {
            Device d = invocation.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });

        // When
        Device result = deviceService.create(request);

        // Then
        assertNotNull(result);
        assertEquals("DEV002", result.getDeviceNumber());
        assertEquals("新设备", result.getDeviceName());
        assertEquals("MODEL002", result.getDeviceModel());
        assertEquals("CT001", result.getContractNumber());
        verify(deviceRepository, times(1)).findByDeviceNumber("DEV002");
        verify(contractRepository, times(1)).findById(contractId);
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    @DisplayName("创建设备 - 设备编号已存在，抛出异常")
    void testCreate_DeviceNumberExists() {
        // Given
        DeviceRequest request = new DeviceRequest();
        request.setDeviceNumber("DEV001");

        when(deviceRepository.findByDeviceNumber("DEV001")).thenReturn(Optional.of(device));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            deviceService.create(request);
        });
        verify(deviceRepository, times(1)).findByDeviceNumber("DEV001");
        verify(deviceRepository, never()).save(any());
    }

    @Test
    @DisplayName("更新设备 - 成功")
    void testUpdate_Success() {
        // Given
        DeviceRequest request = new DeviceRequest();
        request.setDeviceNumber("DEV001");
        request.setDeviceName("更新后的设备");
        request.setDeviceModel("MODEL003");

        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        // When
        Device result = deviceService.update(deviceId, request);

        // Then
        assertNotNull(result);
        assertEquals("更新后的设备", result.getDeviceName());
        assertEquals("MODEL003", result.getDeviceModel());
        verify(deviceRepository, times(1)).findById(deviceId);
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    @DisplayName("删除设备 - 成功")
    void testDelete_Success() {
        // Given
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

        // When
        deviceService.delete(deviceId);

        // Then
        verify(deviceRepository, times(1)).findById(deviceId);
        verify(deviceRepository, times(1)).delete(device);
    }

    @Test
    @DisplayName("根据ID查找设备 - 成功")
    void testFindById_Success() {
        // Given
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

        // When
        Device result = deviceService.findById(deviceId);

        // Then
        assertNotNull(result);
        assertEquals(deviceId, result.getId());
        assertEquals("DEV001", result.getDeviceNumber());
        verify(deviceRepository, times(1)).findById(deviceId);
    }

    @Test
    @DisplayName("根据合同ID查找设备 - 成功")
    void testFindByContractId_Success() {
        // Given
        List<Device> devices = Arrays.asList(device);
        when(deviceRepository.findByContractId(contractId)).thenReturn(devices);

        // When
        List<Device> result = deviceService.findByContractId(contractId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DEV001", result.get(0).getDeviceNumber());
        verify(deviceRepository, times(1)).findByContractId(contractId);
    }

    @Test
    @DisplayName("搜索设备 - 成功")
    void testSearchDevices_Success() {
        // Given
        String keyword = "测试";
        List<Device> devices = Arrays.asList(device);
        when(deviceRepository.searchDevices(keyword)).thenReturn(devices);

        // When
        List<Device> result = deviceService.searchDevices(keyword);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deviceRepository, times(1)).searchDevices(keyword);
    }
}




