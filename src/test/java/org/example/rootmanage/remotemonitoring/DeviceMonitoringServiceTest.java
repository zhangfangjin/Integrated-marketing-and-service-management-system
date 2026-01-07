package org.example.rootmanage.remotemonitoring;

import org.example.rootmanage.remotemonitoring.dto.CurveAnalysisRequest;
import org.example.rootmanage.remotemonitoring.dto.CurveDataResponse;
import org.example.rootmanage.remotemonitoring.entity.*;
import org.example.rootmanage.remotemonitoring.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 设备运行监视服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("设备运行监视服务测试")
class DeviceMonitoringServiceTest {

    @Mock
    private DataPointRepository dataPointRepository;

    @Mock
    private DeviceRunningDataRepository runningDataRepository;

    @Mock
    private DataStatisticsRepository statisticsRepository;

    @Mock
    private AnalysisModelRepository analysisModelRepository;

    @Mock
    private AnalysisModelPointRepository analysisModelPointRepository;

    @InjectMocks
    private DeviceMonitoringService monitoringService;

    private UUID pointId1;
    private UUID pointId2;
    private UUID modelId;
    private DataPoint dataPoint1;
    private DataPoint dataPoint2;
    private DeviceRunningData runningData;

    @BeforeEach
    void setUp() {
        pointId1 = UUID.randomUUID();
        pointId2 = UUID.randomUUID();
        modelId = UUID.randomUUID();

        dataPoint1 = new DataPoint();
        dataPoint1.setId(pointId1);
        dataPoint1.setPointCode("P001");
        dataPoint1.setPointName("压力点位");
        dataPoint1.setUnit("MPa");
        dataPoint1.setCurrentValue(10.5);
        dataPoint1.setEnabled(true);

        dataPoint2 = new DataPoint();
        dataPoint2.setId(pointId2);
        dataPoint2.setPointCode("P002");
        dataPoint2.setPointName("温度点位");
        dataPoint2.setUnit("℃");
        dataPoint2.setCurrentValue(25.0);
        dataPoint2.setEnabled(true);

        runningData = new DeviceRunningData();
        runningData.setId(UUID.randomUUID());
        runningData.setDataPointId(pointId1);
        runningData.setCollectionTime(LocalDateTime.now());
        runningData.setValue(10.5);
        runningData.setQuality(DataQuality.GOOD);
        runningData.setSource(DataSource.AUTO);
    }

    @Test
    @DisplayName("获取所有点位的实时数据 - 成功")
    void testGetRealTimeData_Success() {
        // Given
        when(dataPointRepository.findByEnabled(true))
                .thenReturn(Arrays.asList(dataPoint1, dataPoint2));

        // When
        List<DataPoint> result = monitoringService.getRealTimeData();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dataPointRepository, times(1)).findByEnabled(true);
    }

    @Test
    @DisplayName("根据点位ID获取实时数据 - 成功")
    void testGetRealTimeDataByPointId_Success() {
        // Given
        when(dataPointRepository.findById(pointId1)).thenReturn(Optional.of(dataPoint1));

        // When
        DataPoint result = monitoringService.getRealTimeDataByPointId(pointId1);

        // Then
        assertNotNull(result);
        assertEquals(pointId1, result.getId());
        assertEquals("P001", result.getPointCode());
        verify(dataPointRepository, times(1)).findById(pointId1);
    }

    @Test
    @DisplayName("根据分析模型获取实时数据 - 成功")
    void testGetRealTimeDataByAnalysisModel_Success() {
        // Given
        AnalysisModelPoint modelPoint1 = new AnalysisModelPoint();
        modelPoint1.setDataPointId(pointId1);
        AnalysisModelPoint modelPoint2 = new AnalysisModelPoint();
        modelPoint2.setDataPointId(pointId2);

        when(analysisModelPointRepository.findByAnalysisModelIdOrderBySortOrder(modelId))
                .thenReturn(Arrays.asList(modelPoint1, modelPoint2));
        when(dataPointRepository.findByIdIn(Arrays.asList(pointId1, pointId2)))
                .thenReturn(Arrays.asList(dataPoint1, dataPoint2));

        // When
        List<DataPoint> result = monitoringService.getRealTimeDataByAnalysisModel(modelId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(analysisModelPointRepository, times(1))
                .findByAnalysisModelIdOrderBySortOrder(modelId);
        verify(dataPointRepository, times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("保存采集数据 - 成功")
    void testSaveRunningData_Success() {
        // Given
        Double value = 15.0;
        LocalDateTime collectionTime = LocalDateTime.now();

        when(dataPointRepository.findById(pointId1)).thenReturn(Optional.of(dataPoint1));
        when(dataPointRepository.save(any(DataPoint.class))).thenReturn(dataPoint1);
        when(runningDataRepository.save(any(DeviceRunningData.class))).thenAnswer(invocation -> {
            DeviceRunningData data = invocation.getArgument(0);
            data.setId(UUID.randomUUID());
            return data;
        });

        // When
        DeviceRunningData result = monitoringService.saveRunningData(pointId1, value, collectionTime);

        // Then
        assertNotNull(result);
        assertEquals(pointId1, result.getDataPointId());
        assertEquals(value, result.getRawValue());
        assertEquals(value, result.getValue());
        assertEquals(DataQuality.GOOD, result.getQuality());
        assertEquals(DataSource.AUTO, result.getSource());
        assertEquals(value, dataPoint1.getCurrentValue());
        assertEquals(collectionTime, dataPoint1.getLastCollectionTime());
        verify(dataPointRepository, times(1)).findById(pointId1);
        verify(dataPointRepository, times(1)).save(any(DataPoint.class));
        verify(runningDataRepository, times(1)).save(any(DeviceRunningData.class));
    }

    @Test
    @DisplayName("获取点位最新数据 - 成功")
    void testGetLatestData_Success() {
        // Given
        when(runningDataRepository.findFirstByDataPointIdOrderByCollectionTimeDesc(pointId1))
                .thenReturn(Optional.of(runningData));

        // When
        DeviceRunningData result = monitoringService.getLatestData(pointId1);

        // Then
        assertNotNull(result);
        assertEquals(pointId1, result.getDataPointId());
        assertEquals(10.5, result.getValue());
        verify(runningDataRepository, times(1))
                .findFirstByDataPointIdOrderByCollectionTimeDesc(pointId1);
    }

    @Test
    @DisplayName("获取点位最新数据 - 无数据，返回null")
    void testGetLatestData_NoData() {
        // Given
        when(runningDataRepository.findFirstByDataPointIdOrderByCollectionTimeDesc(pointId1))
                .thenReturn(Optional.empty());

        // When
        DeviceRunningData result = monitoringService.getLatestData(pointId1);

        // Then
        assertNull(result);
        verify(runningDataRepository, times(1))
                .findFirstByDataPointIdOrderByCollectionTimeDesc(pointId1);
    }

    @Test
    @DisplayName("查询历史数据 - 成功")
    void testGetHistoricalData_Success() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();

        when(runningDataRepository.findByDataPointIdAndTimeRange(pointId1, startTime, endTime))
                .thenReturn(Arrays.asList(runningData));

        // When
        List<DeviceRunningData> result = monitoringService.getHistoricalData(pointId1, startTime, endTime);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(runningDataRepository, times(1))
                .findByDataPointIdAndTimeRange(pointId1, startTime, endTime);
    }

    @Test
    @DisplayName("获取曲线分析数据 - 使用原始数据")
    void testGetCurveAnalysisData_WithRawData() {
        // Given
        CurveAnalysisRequest request = new CurveAnalysisRequest();
        request.setDataPointIds(Arrays.asList(pointId1, pointId2));
        request.setStartTime(LocalDateTime.now().minusHours(1));
        request.setEndTime(LocalDateTime.now());
        request.setPeriod(StatisticsPeriod.HOURLY);

        DeviceRunningData data1 = new DeviceRunningData();
        data1.setDataPointId(pointId1);
        data1.setCollectionTime(LocalDateTime.now().minusMinutes(30));
        data1.setValue(10.0);

        DeviceRunningData data2 = new DeviceRunningData();
        data2.setDataPointId(pointId2);
        data2.setCollectionTime(LocalDateTime.now().minusMinutes(15));
        data2.setValue(20.0);

        when(dataPointRepository.findByIdIn(Arrays.asList(pointId1, pointId2)))
                .thenReturn(Arrays.asList(dataPoint1, dataPoint2));
        when(runningDataRepository.findByDataPointIdsAndTimeRange(
                any(), any(), any())).thenReturn(Arrays.asList(data1, data2));

        // When
        List<CurveDataResponse> result = monitoringService.getCurveAnalysisData(request);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dataPointRepository, times(1)).findByIdIn(any());
        verify(runningDataRepository, times(1)).findByDataPointIdsAndTimeRange(any(), any(), any());
    }
}

















