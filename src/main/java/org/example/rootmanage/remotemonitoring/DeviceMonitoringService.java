package org.example.rootmanage.remotemonitoring;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.CurveAnalysisRequest;
import org.example.rootmanage.remotemonitoring.dto.CurveDataResponse;
import org.example.rootmanage.remotemonitoring.entity.*;
import org.example.rootmanage.remotemonitoring.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备运行监视服务类
 * 提供实时监视、曲线分析、在线数据分析等功能
 */
@Service
@RequiredArgsConstructor
public class DeviceMonitoringService {

    private final DataPointRepository dataPointRepository;
    private final DeviceRunningDataRepository runningDataRepository;
    private final DataStatisticsRepository statisticsRepository;
    private final AnalysisModelRepository analysisModelRepository;
    private final AnalysisModelPointRepository analysisModelPointRepository;

    // ==================== 实时监视功能 ====================

    /**
     * 获取所有点位的实时数据
     */
    @Transactional(readOnly = true)
    public List<DataPoint> getRealTimeData() {
        return dataPointRepository.findByEnabled(true);
    }

    /**
     * 根据点位ID获取实时数据
     */
    @Transactional(readOnly = true)
    public DataPoint getRealTimeDataByPointId(UUID dataPointId) {
        return dataPointRepository.findById(dataPointId)
                .orElseThrow(() -> new IllegalArgumentException("点位不存在"));
    }

    /**
     * 根据分析模型获取实时数据
     */
    @Transactional(readOnly = true)
    public List<DataPoint> getRealTimeDataByAnalysisModel(UUID analysisModelId) {
        List<AnalysisModelPoint> modelPoints = analysisModelPointRepository
                .findByAnalysisModelIdOrderBySortOrder(analysisModelId);

        List<UUID> pointIds = modelPoints.stream()
                .map(AnalysisModelPoint::getDataPointId)
                .collect(Collectors.toList());

        return dataPointRepository.findByIdIn(pointIds);
    }

    // ==================== 曲线分析功能 ====================

    /**
     * 获取曲线分析数据
     */
    @Transactional(readOnly = true)
    public List<CurveDataResponse> getCurveAnalysisData(CurveAnalysisRequest request) {
        List<UUID> pointIds;

        // 如果指定了分析模型，从分析模型获取点位
        if (request.getAnalysisModelId() != null) {
            List<AnalysisModelPoint> modelPoints;
            if (request.getCurveGroup() != null) {
                modelPoints = analysisModelPointRepository.findByAnalysisModelIdAndCurveGroupOrderBySortOrder(
                        request.getAnalysisModelId(), request.getCurveGroup());
            } else {
                modelPoints = analysisModelPointRepository.findByAnalysisModelIdOrderBySortOrder(
                        request.getAnalysisModelId());
            }
            pointIds = modelPoints.stream()
                    .map(AnalysisModelPoint::getDataPointId)
                    .collect(Collectors.toList());
        } else {
            pointIds = request.getDataPointIds();
        }

        if (pointIds == null || pointIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<CurveDataResponse> responses = new ArrayList<>();

        // 获取点位信息
        List<DataPoint> dataPoints = dataPointRepository.findByIdIn(pointIds);
        Map<UUID, DataPoint> pointMap = dataPoints.stream()
                .collect(Collectors.toMap(DataPoint::getId, p -> p));

        // 根据时间粒度获取数据
        if (request.getPeriod() != null && request.getPeriod() != StatisticsPeriod.HOURLY) {
            // 使用统计数据
            LocalDate startDate = request.getStartTime().toLocalDate();
            LocalDate endDate = request.getEndTime().toLocalDate();

            List<DataStatistics> statistics = statisticsRepository.findByDataPointIdsAndPeriodAndDateRange(
                    pointIds, request.getPeriod(), startDate, endDate);

            // 按点位分组
            Map<UUID, List<DataStatistics>> statsByPoint = statistics.stream()
                    .collect(Collectors.groupingBy(DataStatistics::getDataPointId));

            for (UUID pointId : pointIds) {
                DataPoint point = pointMap.get(pointId);
                if (point == null) continue;

                CurveDataResponse response = new CurveDataResponse();
                response.setDataPointId(pointId);
                response.setPointName(point.getPointName());
                response.setUnit(point.getUnit());

                List<CurveDataResponse.DataPointValue> values = new ArrayList<>();
                List<DataStatistics> pointStats = statsByPoint.getOrDefault(pointId, new ArrayList<>());

                for (DataStatistics stat : pointStats) {
                    CurveDataResponse.DataPointValue dpv = new CurveDataResponse.DataPointValue();
                    dpv.setTime(stat.getStartTime());
                    dpv.setAvgValue(stat.getAvgValue());
                    dpv.setMaxValue(stat.getMaxValue());
                    dpv.setMinValue(stat.getMinValue());
                    dpv.setValue(stat.getAvgValue());
                    values.add(dpv);
                }

                response.setDataPoints(values);
                responses.add(response);
            }
        } else {
            // 使用原始数据
            List<DeviceRunningData> runningData = runningDataRepository.findByDataPointIdsAndTimeRange(
                    pointIds, request.getStartTime(), request.getEndTime());

            // 按点位分组
            Map<UUID, List<DeviceRunningData>> dataByPoint = runningData.stream()
                    .collect(Collectors.groupingBy(DeviceRunningData::getDataPointId));

            for (UUID pointId : pointIds) {
                DataPoint point = pointMap.get(pointId);
                if (point == null) continue;

                CurveDataResponse response = new CurveDataResponse();
                response.setDataPointId(pointId);
                response.setPointName(point.getPointName());
                response.setUnit(point.getUnit());

                List<CurveDataResponse.DataPointValue> values = new ArrayList<>();
                List<DeviceRunningData> pointData = dataByPoint.getOrDefault(pointId, new ArrayList<>());

                for (DeviceRunningData data : pointData) {
                    CurveDataResponse.DataPointValue dpv = new CurveDataResponse.DataPointValue();
                    dpv.setTime(data.getCollectionTime());
                    dpv.setValue(data.getValue());
                    values.add(dpv);
                }

                response.setDataPoints(values);
                responses.add(response);
            }
        }

        return responses;
    }

    /**
     * 获取小时曲线分析数据
     */
    @Transactional(readOnly = true)
    public List<CurveDataResponse> getHourlyCurveData(List<UUID> pointIds, LocalDate date) {
        return statisticsRepository.findByDataPointIdsAndPeriodAndDateRange(
                        pointIds, StatisticsPeriod.HOURLY, date, date)
                .stream()
                .collect(Collectors.groupingBy(DataStatistics::getDataPointId))
                .entrySet().stream()
                .map(entry -> {
                    UUID pointId = entry.getKey();
                    DataPoint point = dataPointRepository.findById(pointId).orElse(null);
                    if (point == null) return null;

                    CurveDataResponse response = new CurveDataResponse();
                    response.setDataPointId(pointId);
                    response.setPointName(point.getPointName());
                    response.setUnit(point.getUnit());

                    List<CurveDataResponse.DataPointValue> values = entry.getValue().stream()
                            .sorted(Comparator.comparing(DataStatistics::getHourOfDay))
                            .map(stat -> {
                                CurveDataResponse.DataPointValue dpv = new CurveDataResponse.DataPointValue();
                                dpv.setTime(stat.getStartTime());
                                dpv.setAvgValue(stat.getAvgValue());
                                dpv.setMaxValue(stat.getMaxValue());
                                dpv.setMinValue(stat.getMinValue());
                                dpv.setValue(stat.getAvgValue());
                                return dpv;
                            })
                            .collect(Collectors.toList());

                    response.setDataPoints(values);
                    return response;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 获取日曲线分析数据
     */
    @Transactional(readOnly = true)
    public List<CurveDataResponse> getDailyCurveData(List<UUID> pointIds, LocalDate startDate, LocalDate endDate) {
        CurveAnalysisRequest request = new CurveAnalysisRequest();
        request.setDataPointIds(pointIds);
        request.setStartTime(startDate.atStartOfDay());
        request.setEndTime(endDate.atTime(23, 59, 59));
        request.setPeriod(StatisticsPeriod.DAILY);
        return getCurveAnalysisData(request);
    }

    // ==================== 在线数据分析 ====================

    /**
     * 获取日报数据
     */
    @Transactional(readOnly = true)
    public List<DataStatistics> getDailyReport(List<UUID> pointIds, LocalDate date) {
        return statisticsRepository.findByDataPointIdsAndPeriodAndDateRange(
                pointIds, StatisticsPeriod.HOURLY, date, date);
    }

    /**
     * 获取周报数据
     */
    @Transactional(readOnly = true)
    public List<DataStatistics> getWeeklyReport(List<UUID> pointIds, LocalDate startDate, LocalDate endDate) {
        return statisticsRepository.findByDataPointIdsAndPeriodAndDateRange(
                pointIds, StatisticsPeriod.DAILY, startDate, endDate);
    }

    /**
     * 获取月报数据
     */
    @Transactional(readOnly = true)
    public List<DataStatistics> getMonthlyReport(List<UUID> pointIds, LocalDate startDate, LocalDate endDate) {
        return statisticsRepository.findByDataPointIdsAndPeriodAndDateRange(
                pointIds, StatisticsPeriod.DAILY, startDate, endDate);
    }

    // ==================== 历史数据查询 ====================

    /**
     * 查询历史数据
     */
    @Transactional(readOnly = true)
    public List<DeviceRunningData> getHistoricalData(UUID dataPointId, LocalDateTime startTime, LocalDateTime endTime) {
        return runningDataRepository.findByDataPointIdAndTimeRange(dataPointId, startTime, endTime);
    }

    /**
     * 获取点位最新数据
     */
    @Transactional(readOnly = true)
    public DeviceRunningData getLatestData(UUID dataPointId) {
        return runningDataRepository.findFirstByDataPointIdOrderByCollectionTimeDesc(dataPointId)
                .orElse(null);
    }

    /**
     * 保存采集数据
     */
    @Transactional
    public DeviceRunningData saveRunningData(UUID dataPointId, Double value, LocalDateTime collectionTime) {
        DataPoint point = dataPointRepository.findById(dataPointId)
                .orElseThrow(() -> new IllegalArgumentException("点位不存在"));

        DeviceRunningData data = new DeviceRunningData();
        data.setDataPointId(dataPointId);
        data.setCollectionTime(collectionTime != null ? collectionTime : LocalDateTime.now());
        data.setRawValue(value);
        data.setValue(value * (point.getMultiplier() != null ? point.getMultiplier() : 1.0));
        data.setQuality(DataQuality.GOOD);
        data.setSource(DataSource.AUTO);

        // 更新点位当前值
        point.setCurrentValue(data.getValue());
        point.setLastCollectionTime(data.getCollectionTime());
        dataPointRepository.save(point);

        return runningDataRepository.save(data);
    }
}

















