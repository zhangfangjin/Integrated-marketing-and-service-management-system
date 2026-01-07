package org.example.rootmanage.remotemonitoring;

import org.example.rootmanage.remotemonitoring.dto.DataPointRequest;
import org.example.rootmanage.remotemonitoring.dto.ManualDataInputRequest;
import org.example.rootmanage.remotemonitoring.entity.*;
import org.example.rootmanage.remotemonitoring.repository.DataPointRepository;
import org.example.rootmanage.remotemonitoring.repository.DeviceRunningDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 数据点位服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("数据点位服务测试")
class DataPointServiceTest {

    @Mock
    private DataPointRepository dataPointRepository;

    @Mock
    private DeviceRunningDataRepository deviceRunningDataRepository;

    @InjectMocks
    private DataPointService dataPointService;

    private UUID pointId;
    private DataPoint dataPoint;

    @BeforeEach
    void setUp() {
        pointId = UUID.randomUUID();

        dataPoint = new DataPoint();
        dataPoint.setId(pointId);
        dataPoint.setPointCode("P001");
        dataPoint.setPointName("测试点位");
        dataPoint.setPointType(PointType.REAL);
        dataPoint.setDataType(MeasurementType.ANALOG);
        dataPoint.setMultiplier(1.0);
        dataPoint.setCollectionMode(CollectionMode.POLLING);
        dataPoint.setCollectionInterval(60);
        dataPoint.setPrecision(2);
        dataPoint.setEnabled(true);
    }

    @Test
    @DisplayName("创建点位 - 成功")
    void testCreate_Success() {
        // Given
        DataPointRequest request = new DataPointRequest();
        request.setPointCode("P002");
        request.setPointName("新点位");
        request.setPointType(PointType.REAL);
        request.setDataType(MeasurementType.ANALOG);
        request.setCollectionMode(CollectionMode.POLLING);
        request.setCollectionInterval(30);
        request.setEnabled(true);

        when(dataPointRepository.findByPointCode("P002")).thenReturn(Optional.empty());
        when(dataPointRepository.save(any(DataPoint.class))).thenAnswer(invocation -> {
            DataPoint point = invocation.getArgument(0);
            point.setId(UUID.randomUUID());
            return point;
        });

        // When
        DataPoint result = dataPointService.create(request);

        // Then
        assertNotNull(result);
        assertEquals("P002", result.getPointCode());
        assertEquals("新点位", result.getPointName());
        assertEquals(PointType.REAL, result.getPointType());
        assertEquals(CollectionMode.POLLING, result.getCollectionMode());
        assertEquals(30, result.getCollectionInterval());
        verify(dataPointRepository, times(1)).findByPointCode("P002");
        verify(dataPointRepository, times(1)).save(any(DataPoint.class));
    }

    @Test
    @DisplayName("创建点位 - 编码已存在，抛出异常")
    void testCreate_PointCodeExists() {
        // Given
        DataPointRequest request = new DataPointRequest();
        request.setPointCode("P001");

        when(dataPointRepository.findByPointCode("P001")).thenReturn(Optional.of(dataPoint));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            dataPointService.create(request);
        });
        verify(dataPointRepository, times(1)).findByPointCode("P001");
        verify(dataPointRepository, never()).save(any());
    }

    @Test
    @DisplayName("手动录入数据 - 成功")
    void testInputManualData_Success() {
        // Given
        ManualDataInputRequest request = new ManualDataInputRequest();
        request.setDataPointId(pointId);
        request.setValue(100.5);
        request.setInputById(UUID.randomUUID());
        request.setInputByName("测试用户");
        request.setCollectionTime(LocalDateTime.now());

        when(dataPointRepository.findById(pointId)).thenReturn(Optional.of(dataPoint));
        when(dataPointRepository.save(any(DataPoint.class))).thenReturn(dataPoint);
        when(deviceRunningDataRepository.save(any(DeviceRunningData.class))).thenAnswer(invocation -> {
            DeviceRunningData data = invocation.getArgument(0);
            data.setId(UUID.randomUUID());
            return data;
        });

        // When
        DeviceRunningData result = dataPointService.inputManualData(request);

        // Then
        assertNotNull(result);
        assertEquals(pointId, result.getDataPointId());
        assertEquals(100.5, result.getValue());
        assertEquals(100.5, result.getRawValue());
        assertEquals(DataQuality.GOOD, result.getQuality());
        assertEquals(DataSource.MANUAL, result.getSource());
        assertEquals(100.5, dataPoint.getCurrentValue());
        assertNotNull(dataPoint.getLastCollectionTime());
        verify(dataPointRepository, times(1)).findById(pointId);
        verify(dataPointRepository, times(1)).save(any(DataPoint.class));
        verify(deviceRunningDataRepository, times(1)).save(any(DeviceRunningData.class));
    }

    @Test
    @DisplayName("手动录入数据 - 点位不存在，抛出异常")
    void testInputManualData_PointNotExists() {
        // Given
        ManualDataInputRequest request = new ManualDataInputRequest();
        request.setDataPointId(pointId);
        request.setValue(100.5);

        when(dataPointRepository.findById(pointId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            dataPointService.inputManualData(request);
        });
        verify(dataPointRepository, times(1)).findById(pointId);
        verify(deviceRunningDataRepository, never()).save(any());
    }

    @Test
    @DisplayName("更新点位当前值 - 成功")
    void testUpdateCurrentValue_Success() {
        // Given
        Double value = 150.0;
        LocalDateTime collectionTime = LocalDateTime.now();

        when(dataPointRepository.findById(pointId)).thenReturn(Optional.of(dataPoint));
        when(dataPointRepository.save(any(DataPoint.class))).thenReturn(dataPoint);

        // When
        dataPointService.updateCurrentValue(pointId, value, collectionTime);

        // Then
        assertEquals(value, dataPoint.getCurrentValue());
        assertEquals(collectionTime, dataPoint.getLastCollectionTime());
        verify(dataPointRepository, times(1)).findById(pointId);
        verify(dataPointRepository, times(1)).save(any(DataPoint.class));
    }

    @Test
    @DisplayName("删除点位 - 成功")
    void testDelete_Success() {
        // Given
        when(dataPointRepository.findById(pointId)).thenReturn(Optional.of(dataPoint));

        // When
        dataPointService.delete(pointId);

        // Then
        verify(dataPointRepository, times(1)).findById(pointId);
        verify(dataPointRepository, times(1)).delete(dataPoint);
    }

    @Test
    @DisplayName("根据ID查找点位 - 成功")
    void testFindById_Success() {
        // Given
        when(dataPointRepository.findById(pointId)).thenReturn(Optional.of(dataPoint));

        // When
        DataPoint result = dataPointService.findById(pointId);

        // Then
        assertNotNull(result);
        assertEquals(pointId, result.getId());
        assertEquals("P001", result.getPointCode());
        verify(dataPointRepository, times(1)).findById(pointId);
    }
}

















