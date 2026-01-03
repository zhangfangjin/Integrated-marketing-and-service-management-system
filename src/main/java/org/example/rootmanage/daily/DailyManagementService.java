package org.example.rootmanage.daily;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.account.UserAccountRepository;
import org.example.rootmanage.daily.dto.DestinationManagementRequest;
import org.example.rootmanage.daily.dto.WeeklyReportRequest;
import org.example.rootmanage.daily.entity.DestinationManagement;
import org.example.rootmanage.daily.entity.WeeklyReport;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailyManagementService {

    private final DestinationManagementRepository destinationManagementRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final UserAccountRepository userAccountRepository;

    // ========== 去向管理 ==========

    /**
     * 查询去向信息列表
     */
    @Transactional(readOnly = true)
    public List<DestinationManagement> findAllDestinations(String keyword, UUID userId) {
        List<DestinationManagement> destinations;
        if (userId != null) {
            destinations = destinationManagementRepository.findByUserIdOrderByTimeDesc(userId);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            destinations = destinationManagementRepository.searchByKeyword(keyword.trim());
        } else {
            destinations = destinationManagementRepository.findBySubmittedTrueOrderByTimeDesc();
        }
        // 初始化用户关联
        destinations.forEach(destination -> {
            if (destination.getUser() != null) {
                Hibernate.initialize(destination.getUser());
            }
        });
        return destinations;
    }

    /**
     * 根据ID查询去向信息
     */
    @Transactional(readOnly = true)
    public Optional<DestinationManagement> findDestinationById(UUID id) {
        Optional<DestinationManagement> destination = destinationManagementRepository.findById(id);
        destination.ifPresent(d -> {
            if (d.getUser() != null) {
                Hibernate.initialize(d.getUser());
            }
        });
        return destination;
    }

    /**
     * 创建去向信息
     */
    @Transactional
    public DestinationManagement createDestination(DestinationManagementRequest request) {
        DestinationManagement destination = new DestinationManagement();
        destination.setActivityName(request.getActivityName());
        destination.setLocation(request.getLocation());
        destination.setTime(request.getTime());
        destination.setRemark(request.getRemark());
        destination.setSubmitted(false);

        // 设置用户
        if (request.getUserId() != null) {
            UserAccount user = userAccountRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            destination.setUser(user);
        }

        return destinationManagementRepository.save(destination);
    }

    /**
     * 更新去向信息
     */
    @Transactional
    public DestinationManagement updateDestination(UUID id, DestinationManagementRequest request) {
        DestinationManagement destination = destinationManagementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("去向信息不存在"));

        destination.setActivityName(request.getActivityName());
        destination.setLocation(request.getLocation());
        destination.setTime(request.getTime());
        destination.setRemark(request.getRemark());

        // 更新用户
        if (request.getUserId() != null) {
            UserAccount user = userAccountRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            destination.setUser(user);
        }

        return destinationManagementRepository.save(destination);
    }

    /**
     * 提交去向信息
     */
    @Transactional
    public DestinationManagement submitDestination(UUID id) {
        DestinationManagement destination = destinationManagementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("去向信息不存在"));
        destination.setSubmitted(true);
        return destinationManagementRepository.save(destination);
    }

    /**
     * 删除去向信息
     */
    @Transactional
    public void deleteDestination(UUID id) {
        destinationManagementRepository.deleteById(id);
    }

    // ========== 周报管理 ==========

    /**
     * 查询周报列表
     */
    @Transactional(readOnly = true)
    public List<WeeklyReport> findAllWeeklyReports(String keyword, UUID userId) {
        List<WeeklyReport> reports;
        if (userId != null) {
            reports = weeklyReportRepository.findByUserIdOrderByReportTimeDesc(userId);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            reports = weeklyReportRepository.searchByKeyword(keyword.trim());
        } else {
            reports = weeklyReportRepository.findBySubmittedTrueOrderByReportTimeDesc();
        }
        // 初始化用户关联
        reports.forEach(report -> {
            if (report.getUser() != null) {
                Hibernate.initialize(report.getUser());
            }
        });
        return reports;
    }

    /**
     * 根据ID查询周报
     */
    @Transactional(readOnly = true)
    public Optional<WeeklyReport> findWeeklyReportById(UUID id) {
        Optional<WeeklyReport> report = weeklyReportRepository.findById(id);
        report.ifPresent(r -> {
            if (r.getUser() != null) {
                Hibernate.initialize(r.getUser());
            }
        });
        return report;
    }

    /**
     * 创建周报
     */
    @Transactional
    public WeeklyReport createWeeklyReport(WeeklyReportRequest request) {
        WeeklyReport report = new WeeklyReport();
        report.setReportName(request.getReportName());
        report.setReportTime(request.getReportTime());
        report.setContent(request.getContent());
        report.setRemark(request.getRemark());
        report.setSubmitted(false);

        // 设置用户
        if (request.getUserId() != null) {
            UserAccount user = userAccountRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            report.setUser(user);
        }

        return weeklyReportRepository.save(report);
    }

    /**
     * 更新周报
     */
    @Transactional
    public WeeklyReport updateWeeklyReport(UUID id, WeeklyReportRequest request) {
        WeeklyReport report = weeklyReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("周报不存在"));

        report.setReportName(request.getReportName());
        report.setReportTime(request.getReportTime());
        report.setContent(request.getContent());
        report.setRemark(request.getRemark());

        // 更新用户
        if (request.getUserId() != null) {
            UserAccount user = userAccountRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            report.setUser(user);
        }

        return weeklyReportRepository.save(report);
    }

    /**
     * 提交周报
     */
    @Transactional
    public WeeklyReport submitWeeklyReport(UUID id) {
        WeeklyReport report = weeklyReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("周报不存在"));
        report.setSubmitted(true);
        return weeklyReportRepository.save(report);
    }

    /**
     * 删除周报
     */
    @Transactional
    public void deleteWeeklyReport(UUID id) {
        weeklyReportRepository.deleteById(id);
    }
}

