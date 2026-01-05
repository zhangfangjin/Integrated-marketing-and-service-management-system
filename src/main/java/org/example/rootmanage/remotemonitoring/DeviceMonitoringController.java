package org.example.rootmanage.remotemonitoring;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.CurveAnalysisRequest;
import org.example.rootmanage.remotemonitoring.dto.CurveDataResponse;
import org.example.rootmanage.remotemonitoring.entity.DataPoint;
import org.example.rootmanage.remotemonitoring.entity.DataStatistics;
import org.example.rootmanage.remotemonitoring.entity.DeviceRunningData;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 设备运行监视控制器
 * 提供实时监视、曲线分析、在线数据分析的REST API
 */
@RestController
@RequestMapping("/api/remote-monitoring/monitoring")
@RequiredArgsConstructor
public class DeviceMonitoringController {

    private final DeviceMonitoringService monitoringService;

    // ==================== 实时监视功能 ====================

    /**
     * 获取所有点位的实时数据
     */
    @GetMapping("/realtime")
    public List<DataPoint> getRealTimeData() {
        return monitoringService.getRealTimeData();
    }

    /**
     * 根据点位ID获取实时数据
     */
    @GetMapping("/realtime/{dataPointId}")
    public DataPoint getRealTimeDataByPointId(@PathVariable UUID dataPointId) {
        return monitoringService.getRealTimeDataByPointId(dataPointId);
    }

    /**
     * 根据分析模型获取实时数据
     */
    @GetMapping("/realtime/analysis-model/{analysisModelId}")
    public List<DataPoint> getRealTimeDataByAnalysisModel(@PathVariable UUID analysisModelId) {
        return monitoringService.getRealTimeDataByAnalysisModel(analysisModelId);
    }

    // ==================== 曲线分析功能 ====================

    /**
     * 获取曲线分析数据
     */
    @PostMapping("/curve-analysis")
    public List<CurveDataResponse> getCurveAnalysisData(@RequestBody CurveAnalysisRequest request) {
        return monitoringService.getCurveAnalysisData(request);
    }

    /**
     * 获取小时曲线分析数据
     */
    @GetMapping("/curve-analysis/hourly")
    public List<CurveDataResponse> getHourlyCurveData(
            @RequestParam List<UUID> pointIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return monitoringService.getHourlyCurveData(pointIds, date);
    }

    /**
     * 获取日曲线分析数据
     */
    @GetMapping("/curve-analysis/daily")
    public List<CurveDataResponse> getDailyCurveData(
            @RequestParam List<UUID> pointIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return monitoringService.getDailyCurveData(pointIds, startDate, endDate);
    }

    // ==================== 在线数据分析 ====================

    /**
     * 获取日报数据
     */
    @GetMapping("/reports/daily")
    public List<DataStatistics> getDailyReport(
            @RequestParam List<UUID> pointIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return monitoringService.getDailyReport(pointIds, date);
    }

    /**
     * 获取周报数据
     */
    @GetMapping("/reports/weekly")
    public List<DataStatistics> getWeeklyReport(
            @RequestParam List<UUID> pointIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return monitoringService.getWeeklyReport(pointIds, startDate, endDate);
    }

    /**
     * 获取月报数据
     */
    @GetMapping("/reports/monthly")
    public List<DataStatistics> getMonthlyReport(
            @RequestParam List<UUID> pointIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return monitoringService.getMonthlyReport(pointIds, startDate, endDate);
    }

    // ==================== 历史数据查询 ====================

    /**
     * 查询历史数据
     */
    @GetMapping("/history/{dataPointId}")
    public List<DeviceRunningData> getHistoricalData(
            @PathVariable UUID dataPointId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return monitoringService.getHistoricalData(dataPointId, startTime, endTime);
    }

    /**
     * 获取点位最新数据
     */
    @GetMapping("/history/{dataPointId}/latest")
    public DeviceRunningData getLatestData(@PathVariable UUID dataPointId) {
        return monitoringService.getLatestData(dataPointId);
    }
}





