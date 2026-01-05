package org.example.rootmanage.remotemonitoring;

import org.example.rootmanage.remotemonitoring.dto.MeterRequest;
import org.example.rootmanage.remotemonitoring.entity.Meter;
import org.example.rootmanage.remotemonitoring.entity.MeterType;
import org.example.rootmanage.remotemonitoring.repository.MeterAttributeRepository;
import org.example.rootmanage.remotemonitoring.repository.MeterMeasurementRepository;
import org.example.rootmanage.remotemonitoring.repository.MeterRepository;
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
 * 表计服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("表计服务测试")
class MeterServiceTest {

    @Mock
    private MeterRepository meterRepository;

    @Mock
    private MeterAttributeRepository meterAttributeRepository;

    @Mock
    private MeterMeasurementRepository meterMeasurementRepository;

    @InjectMocks
    private MeterService meterService;

    private UUID meterId;
    private Meter meter;

    @BeforeEach
    void setUp() {
        meterId = UUID.randomUUID();

        meter = new Meter();
        meter.setId(meterId);
        meter.setMeterCode("M001");
        meter.setMeterName("测试表计");
        meter.setMeterType(MeterType.WATER);
        meter.setEnabled(true);
    }

    @Test
    @DisplayName("创建表计 - 成功")
    void testCreate_Success() {
        // Given
        MeterRequest request = new MeterRequest();
        request.setMeterCode("M002");
        request.setMeterName("新表计");
        request.setMeterType(MeterType.ELECTRIC);
        request.setEnabled(true);

        when(meterRepository.findByMeterCode("M002")).thenReturn(Optional.empty());
        when(meterRepository.save(any(Meter.class))).thenAnswer(invocation -> {
            Meter m = invocation.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        // When
        Meter result = meterService.create(request);

        // Then
        assertNotNull(result);
        assertEquals("M002", result.getMeterCode());
        assertEquals("新表计", result.getMeterName());
        assertEquals(MeterType.ELECTRIC, result.getMeterType());
        verify(meterRepository, times(1)).findByMeterCode("M002");
        verify(meterRepository, times(1)).save(any(Meter.class));
    }

    @Test
    @DisplayName("创建表计 - 编码已存在，抛出异常")
    void testCreate_MeterCodeExists() {
        // Given
        MeterRequest request = new MeterRequest();
        request.setMeterCode("M001");

        when(meterRepository.findByMeterCode("M001")).thenReturn(Optional.of(meter));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            meterService.create(request);
        });
        verify(meterRepository, times(1)).findByMeterCode("M001");
        verify(meterRepository, never()).save(any());
    }

    @Test
    @DisplayName("更新表计 - 成功")
    void testUpdate_Success() {
        // Given
        MeterRequest request = new MeterRequest();
        request.setMeterCode("M001");
        request.setMeterName("更新后的表计");
        request.setMeterType(MeterType.PRESSURE);
        request.setEnabled(false);

        when(meterRepository.findById(meterId)).thenReturn(Optional.of(meter));
        // 注意：当meterCode相同时，不会调用findByMeterCode，所以不需要mock
        when(meterRepository.save(any(Meter.class))).thenReturn(meter);

        // When
        Meter result = meterService.update(meterId, request);

        // Then
        assertNotNull(result);
        assertEquals("更新后的表计", result.getMeterName());
        assertEquals(MeterType.PRESSURE, result.getMeterType());
        verify(meterRepository, times(1)).findById(meterId);
        verify(meterRepository, never()).findByMeterCode(anyString());
        verify(meterRepository, times(1)).save(any(Meter.class));
    }

    @Test
    @DisplayName("删除表计 - 成功")
    void testDelete_Success() {
        // Given
        when(meterRepository.findById(meterId)).thenReturn(Optional.of(meter));

        // When
        meterService.delete(meterId);

        // Then
        verify(meterRepository, times(1)).findById(meterId);
        verify(meterAttributeRepository, times(1)).deleteByMeterId(meterId);
        verify(meterMeasurementRepository, times(1)).deleteByMeterId(meterId);
        verify(meterRepository, times(1)).delete(meter);
    }

    @Test
    @DisplayName("根据ID查找表计 - 成功")
    void testFindById_Success() {
        // Given
        when(meterRepository.findById(meterId)).thenReturn(Optional.of(meter));

        // When
        Meter result = meterService.findById(meterId);

        // Then
        assertNotNull(result);
        assertEquals(meterId, result.getId());
        assertEquals("M001", result.getMeterCode());
        verify(meterRepository, times(1)).findById(meterId);
    }
}


