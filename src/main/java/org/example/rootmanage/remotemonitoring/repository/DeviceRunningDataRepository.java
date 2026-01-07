package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.DeviceRunningData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 设备运行数据数据访问接口
 */
@Repository
public interface DeviceRunningDataRepository extends JpaRepository<DeviceRunningData, UUID> {

    /**
     * 根据点位ID查找最新数据
     */
    Optional<DeviceRunningData> findFirstByDataPointIdOrderByCollectionTimeDesc(UUID dataPointId);

    /**
     * 根据点位ID和时间范围查找
     */
    @Query("SELECT d FROM DeviceRunningData d WHERE d.dataPointId = :dataPointId " +
            "AND d.collectionTime BETWEEN :startTime AND :endTime ORDER BY d.collectionTime ASC")
    List<DeviceRunningData> findByDataPointIdAndTimeRange(
            @Param("dataPointId") UUID dataPointId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 根据多个点位ID和时间范围查找
     */
    @Query("SELECT d FROM DeviceRunningData d WHERE d.dataPointId IN :dataPointIds " +
            "AND d.collectionTime BETWEEN :startTime AND :endTime ORDER BY d.collectionTime ASC")
    List<DeviceRunningData> findByDataPointIdsAndTimeRange(
            @Param("dataPointIds") List<UUID> dataPointIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 根据点位ID删除历史数据
     */
    void deleteByDataPointId(UUID dataPointId);

    /**
     * 删除指定时间之前的数据
     */
    void deleteByCollectionTimeBefore(LocalDateTime beforeTime);
}

















