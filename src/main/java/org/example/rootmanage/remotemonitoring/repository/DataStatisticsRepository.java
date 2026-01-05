package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.DataStatistics;
import org.example.rootmanage.remotemonitoring.entity.StatisticsPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 数据统计数据访问接口
 */
@Repository
public interface DataStatisticsRepository extends JpaRepository<DataStatistics, UUID> {

    /**
     * 根据点位ID和统计周期查找
     */
    List<DataStatistics> findByDataPointIdAndStatisticsPeriodOrderByStatisticsDateAsc(
            UUID dataPointId, StatisticsPeriod statisticsPeriod);

    /**
     * 根据点位ID、统计周期和日期范围查找
     */
    @Query("SELECT d FROM DataStatistics d WHERE d.dataPointId = :dataPointId " +
            "AND d.statisticsPeriod = :period " +
            "AND d.statisticsDate BETWEEN :startDate AND :endDate " +
            "ORDER BY d.statisticsDate ASC, d.hourOfDay ASC")
    List<DataStatistics> findByDataPointIdAndPeriodAndDateRange(
            @Param("dataPointId") UUID dataPointId,
            @Param("period") StatisticsPeriod period,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 根据多个点位ID、统计周期和日期范围查找
     */
    @Query("SELECT d FROM DataStatistics d WHERE d.dataPointId IN :dataPointIds " +
            "AND d.statisticsPeriod = :period " +
            "AND d.statisticsDate BETWEEN :startDate AND :endDate " +
            "ORDER BY d.statisticsDate ASC, d.hourOfDay ASC")
    List<DataStatistics> findByDataPointIdsAndPeriodAndDateRange(
            @Param("dataPointIds") List<UUID> dataPointIds,
            @Param("period") StatisticsPeriod period,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 查找特定日期和小时的统计数据
     */
    Optional<DataStatistics> findByDataPointIdAndStatisticsPeriodAndStatisticsDateAndHourOfDay(
            UUID dataPointId, StatisticsPeriod statisticsPeriod, LocalDate statisticsDate, Integer hourOfDay);

    /**
     * 查找特定日期的统计数据
     */
    Optional<DataStatistics> findByDataPointIdAndStatisticsPeriodAndStatisticsDate(
            UUID dataPointId, StatisticsPeriod statisticsPeriod, LocalDate statisticsDate);
}





