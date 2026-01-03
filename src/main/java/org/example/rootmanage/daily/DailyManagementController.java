package org.example.rootmanage.daily;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rootmanage.daily.dto.DestinationManagementRequest;
import org.example.rootmanage.daily.dto.WeeklyReportRequest;
import org.example.rootmanage.daily.entity.DestinationManagement;
import org.example.rootmanage.daily.entity.WeeklyReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/daily-management")
@RequiredArgsConstructor
@Validated
public class DailyManagementController {

    private final DailyManagementService dailyManagementService;

    // ========== 去向管理 ==========

    /**
     * 查询去向信息列表
     */
    @GetMapping("/destinations")
    public ResponseEntity<List<DestinationManagement>> getDestinations(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID userId) {
        List<DestinationManagement> destinations = dailyManagementService.findAllDestinations(keyword, userId);
        return ResponseEntity.ok(destinations);
    }

    /**
     * 根据ID查询去向信息
     */
    @GetMapping("/destinations/{id}")
    public ResponseEntity<DestinationManagement> getDestinationById(@PathVariable UUID id) {
        return dailyManagementService.findDestinationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建去向信息
     */
    @PostMapping("/destinations")
    public ResponseEntity<DestinationManagement> createDestination(
            @Valid @RequestBody DestinationManagementRequest request) {
        DestinationManagement destination = dailyManagementService.createDestination(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(destination);
    }

    /**
     * 更新去向信息
     */
    @PutMapping("/destinations/{id}")
    public ResponseEntity<DestinationManagement> updateDestination(
            @PathVariable UUID id,
            @Valid @RequestBody DestinationManagementRequest request) {
        DestinationManagement destination = dailyManagementService.updateDestination(id, request);
        return ResponseEntity.ok(destination);
    }

    /**
     * 提交去向信息
     */
    @PostMapping("/destinations/{id}/submit")
    public ResponseEntity<DestinationManagement> submitDestination(@PathVariable UUID id) {
        DestinationManagement destination = dailyManagementService.submitDestination(id);
        return ResponseEntity.ok(destination);
    }

    /**
     * 删除去向信息
     */
    @DeleteMapping("/destinations/{id}")
    public ResponseEntity<Void> deleteDestination(@PathVariable UUID id) {
        dailyManagementService.deleteDestination(id);
        return ResponseEntity.noContent().build();
    }

    // ========== 周报管理 ==========

    /**
     * 查询周报列表
     */
    @GetMapping("/weekly-reports")
    public ResponseEntity<List<WeeklyReport>> getWeeklyReports(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID userId) {
        List<WeeklyReport> reports = dailyManagementService.findAllWeeklyReports(keyword, userId);
        return ResponseEntity.ok(reports);
    }

    /**
     * 根据ID查询周报
     */
    @GetMapping("/weekly-reports/{id}")
    public ResponseEntity<WeeklyReport> getWeeklyReportById(@PathVariable UUID id) {
        return dailyManagementService.findWeeklyReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建周报
     */
    @PostMapping("/weekly-reports")
    public ResponseEntity<WeeklyReport> createWeeklyReport(
            @Valid @RequestBody WeeklyReportRequest request) {
        WeeklyReport report = dailyManagementService.createWeeklyReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    /**
     * 更新周报
     */
    @PutMapping("/weekly-reports/{id}")
    public ResponseEntity<WeeklyReport> updateWeeklyReport(
            @PathVariable UUID id,
            @Valid @RequestBody WeeklyReportRequest request) {
        WeeklyReport report = dailyManagementService.updateWeeklyReport(id, request);
        return ResponseEntity.ok(report);
    }

    /**
     * 提交周报
     */
    @PostMapping("/weekly-reports/{id}/submit")
    public ResponseEntity<WeeklyReport> submitWeeklyReport(@PathVariable UUID id) {
        WeeklyReport report = dailyManagementService.submitWeeklyReport(id);
        return ResponseEntity.ok(report);
    }

    /**
     * 删除周报
     */
    @DeleteMapping("/weekly-reports/{id}")
    public ResponseEntity<Void> deleteWeeklyReport(@PathVariable UUID id) {
        dailyManagementService.deleteWeeklyReport(id);
        return ResponseEntity.noContent().build();
    }
}

