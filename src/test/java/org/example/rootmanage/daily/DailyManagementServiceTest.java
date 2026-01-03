package org.example.rootmanage.daily;

import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.account.UserAccountRepository;
import org.example.rootmanage.daily.dto.DestinationManagementRequest;
import org.example.rootmanage.daily.dto.WeeklyReportRequest;
import org.example.rootmanage.daily.entity.DestinationManagement;
import org.example.rootmanage.daily.entity.WeeklyReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 日常管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("日常管理服务测试")
class DailyManagementServiceTest {

    @Mock
    private DestinationManagementRepository destinationManagementRepository;

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private DailyManagementService dailyManagementService;

    private UUID destinationId;
    private UUID reportId;
    private UUID userId;
    private DestinationManagement destination;
    private WeeklyReport report;
    private UserAccount user;
    private DestinationManagementRequest destinationRequest;
    private WeeklyReportRequest reportRequest;

    @BeforeEach
    void setUp() {
        destinationId = UUID.randomUUID();
        reportId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = new UserAccount();
        user.setId(userId);
        user.setName("测试用户");

        destination = new DestinationManagement();
        destination.setId(destinationId);
        destination.setActivityName("测试活动");
        destination.setLocation("测试地点");
        destination.setTime(LocalDateTime.now());
        destination.setUser(user);
        destination.setSubmitted(false);

        report = new WeeklyReport();
        report.setId(reportId);
        report.setReportName("工作汇报");
        report.setReportTime(LocalDateTime.now());
        report.setContent("本周工作总结");
        report.setRemark("无");
        report.setUser(user);
        report.setSubmitted(false);

        destinationRequest = new DestinationManagementRequest();
        destinationRequest.setActivityName("测试活动");
        destinationRequest.setLocation("测试地点");
        destinationRequest.setTime(LocalDateTime.now());
        destinationRequest.setUserId(userId);

        reportRequest = new WeeklyReportRequest();
        reportRequest.setReportName("工作汇报");
        reportRequest.setReportTime(LocalDateTime.now());
        reportRequest.setContent("本周工作总结");
        reportRequest.setRemark("无");
        reportRequest.setUserId(userId);
    }

    // ========== 去向管理测试 ==========

    @Test
    @DisplayName("查询去向信息列表 - 成功")
    void testFindAllDestinations_Success() {
        List<DestinationManagement> destinations = Arrays.asList(destination);
        when(destinationManagementRepository.findBySubmittedTrueOrderByTimeDesc()).thenReturn(destinations);

        List<DestinationManagement> result = dailyManagementService.findAllDestinations(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(destinationManagementRepository, times(1)).findBySubmittedTrueOrderByTimeDesc();
    }

    @Test
    @DisplayName("查询去向信息 - 按用户ID")
    void testFindAllDestinations_ByUserId() {
        List<DestinationManagement> destinations = Arrays.asList(destination);
        when(destinationManagementRepository.findByUserIdOrderByTimeDesc(userId)).thenReturn(destinations);

        List<DestinationManagement> result = dailyManagementService.findAllDestinations(null, userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(destinationManagementRepository, times(1)).findByUserIdOrderByTimeDesc(userId);
    }

    @Test
    @DisplayName("查询去向信息 - 带关键词")
    void testFindAllDestinations_WithKeyword() {
        List<DestinationManagement> destinations = Arrays.asList(destination);
        when(destinationManagementRepository.searchByKeyword("测试")).thenReturn(destinations);

        List<DestinationManagement> result = dailyManagementService.findAllDestinations("测试", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(destinationManagementRepository, times(1)).searchByKeyword("测试");
    }

    @Test
    @DisplayName("根据ID查询去向信息 - 成功")
    void testFindDestinationById_Success() {
        when(destinationManagementRepository.findById(destinationId)).thenReturn(Optional.of(destination));

        Optional<DestinationManagement> result = dailyManagementService.findDestinationById(destinationId);

        assertTrue(result.isPresent());
        assertEquals(destinationId, result.get().getId());
        verify(destinationManagementRepository, times(1)).findById(destinationId);
    }

    @Test
    @DisplayName("创建去向信息 - 成功")
    void testCreateDestination_Success() {
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(user));
        when(destinationManagementRepository.save(any(DestinationManagement.class))).thenReturn(destination);

        DestinationManagement result = dailyManagementService.createDestination(destinationRequest);

        assertNotNull(result);
        assertEquals(destinationId, result.getId());
        verify(userAccountRepository, times(1)).findById(userId);
        verify(destinationManagementRepository, times(1)).save(any(DestinationManagement.class));
    }

    @Test
    @DisplayName("创建去向信息 - 用户不存在")
    void testCreateDestination_UserNotFound() {
        when(userAccountRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            dailyManagementService.createDestination(destinationRequest);
        });

        verify(userAccountRepository, times(1)).findById(userId);
        verify(destinationManagementRepository, never()).save(any());
    }

    @Test
    @DisplayName("更新去向信息 - 成功")
    void testUpdateDestination_Success() {
        when(destinationManagementRepository.findById(destinationId)).thenReturn(Optional.of(destination));
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(user));
        when(destinationManagementRepository.save(any(DestinationManagement.class))).thenReturn(destination);

        DestinationManagement result = dailyManagementService.updateDestination(destinationId, destinationRequest);

        assertNotNull(result);
        verify(destinationManagementRepository, times(1)).findById(destinationId);
        verify(destinationManagementRepository, times(1)).save(any(DestinationManagement.class));
    }

    @Test
    @DisplayName("提交去向信息 - 成功")
    void testSubmitDestination_Success() {
        when(destinationManagementRepository.findById(destinationId)).thenReturn(Optional.of(destination));
        when(destinationManagementRepository.save(any(DestinationManagement.class))).thenReturn(destination);

        DestinationManagement result = dailyManagementService.submitDestination(destinationId);

        assertNotNull(result);
        assertTrue(result.getSubmitted());
        verify(destinationManagementRepository, times(1)).findById(destinationId);
        verify(destinationManagementRepository, times(1)).save(any(DestinationManagement.class));
    }

    @Test
    @DisplayName("删除去向信息 - 成功")
    void testDeleteDestination_Success() {
        doNothing().when(destinationManagementRepository).deleteById(destinationId);

        dailyManagementService.deleteDestination(destinationId);

        verify(destinationManagementRepository, times(1)).deleteById(destinationId);
    }

    // ========== 周报管理测试 ==========

    @Test
    @DisplayName("查询周报列表 - 成功")
    void testFindAllWeeklyReports_Success() {
        List<WeeklyReport> reports = Arrays.asList(report);
        when(weeklyReportRepository.findBySubmittedTrueOrderByReportTimeDesc()).thenReturn(reports);

        List<WeeklyReport> result = dailyManagementService.findAllWeeklyReports(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(weeklyReportRepository, times(1)).findBySubmittedTrueOrderByReportTimeDesc();
    }

    @Test
    @DisplayName("查询周报 - 按用户ID")
    void testFindAllWeeklyReports_ByUserId() {
        List<WeeklyReport> reports = Arrays.asList(report);
        when(weeklyReportRepository.findByUserIdOrderByReportTimeDesc(userId)).thenReturn(reports);

        List<WeeklyReport> result = dailyManagementService.findAllWeeklyReports(null, userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(weeklyReportRepository, times(1)).findByUserIdOrderByReportTimeDesc(userId);
    }

    @Test
    @DisplayName("根据ID查询周报 - 成功")
    void testFindWeeklyReportById_Success() {
        when(weeklyReportRepository.findById(reportId)).thenReturn(Optional.of(report));

        Optional<WeeklyReport> result = dailyManagementService.findWeeklyReportById(reportId);

        assertTrue(result.isPresent());
        assertEquals(reportId, result.get().getId());
        verify(weeklyReportRepository, times(1)).findById(reportId);
    }

    @Test
    @DisplayName("创建周报 - 成功")
    void testCreateWeeklyReport_Success() {
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(user));
        when(weeklyReportRepository.save(any(WeeklyReport.class))).thenReturn(report);

        WeeklyReport result = dailyManagementService.createWeeklyReport(reportRequest);

        assertNotNull(result);
        assertEquals(reportId, result.getId());
        verify(userAccountRepository, times(1)).findById(userId);
        verify(weeklyReportRepository, times(1)).save(any(WeeklyReport.class));
    }

    @Test
    @DisplayName("更新周报 - 成功")
    void testUpdateWeeklyReport_Success() {
        when(weeklyReportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(user));
        when(weeklyReportRepository.save(any(WeeklyReport.class))).thenReturn(report);

        WeeklyReport result = dailyManagementService.updateWeeklyReport(reportId, reportRequest);

        assertNotNull(result);
        verify(weeklyReportRepository, times(1)).findById(reportId);
        verify(weeklyReportRepository, times(1)).save(any(WeeklyReport.class));
    }

    @Test
    @DisplayName("提交周报 - 成功")
    void testSubmitWeeklyReport_Success() {
        when(weeklyReportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(weeklyReportRepository.save(any(WeeklyReport.class))).thenReturn(report);

        WeeklyReport result = dailyManagementService.submitWeeklyReport(reportId);

        assertNotNull(result);
        assertTrue(result.getSubmitted());
        verify(weeklyReportRepository, times(1)).findById(reportId);
        verify(weeklyReportRepository, times(1)).save(any(WeeklyReport.class));
    }

    @Test
    @DisplayName("删除周报 - 成功")
    void testDeleteWeeklyReport_Success() {
        doNothing().when(weeklyReportRepository).deleteById(reportId);

        dailyManagementService.deleteWeeklyReport(reportId);

        verify(weeklyReportRepository, times(1)).deleteById(reportId);
    }
}

