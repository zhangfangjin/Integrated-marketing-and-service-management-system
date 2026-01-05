package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.AlarmLevel;
import org.example.rootmanage.remotemonitoring.entity.AlarmRecord;
import org.example.rootmanage.remotemonitoring.entity.AlarmStatus;
import org.example.rootmanage.remotemonitoring.entity.AlarmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 报警记录数据访问接口
 */
@Repository
public interface AlarmRecordRepository extends JpaRepository<AlarmRecord, UUID> {

    /**
     * 根据点位ID查找
     */
    List<AlarmRecord> findByDataPointIdOrderByAlarmTimeDesc(UUID dataPointId);

    /**
     * 根据报警配置ID查找
     */
    List<AlarmRecord> findByAlarmConfigIdOrderByAlarmTimeDesc(UUID alarmConfigId);

    /**
     * 根据报警状态查找
     */
    List<AlarmRecord> findByStatusOrderByAlarmTimeDesc(AlarmStatus status);

    /**
     * 根据报警级别查找
     */
    List<AlarmRecord> findByAlarmLevelOrderByAlarmTimeDesc(AlarmLevel alarmLevel);

    /**
     * 根据报警类型查找
     */
    List<AlarmRecord> findByAlarmTypeOrderByAlarmTimeDesc(AlarmType alarmType);

    /**
     * 查找活动的报警（未恢复）
     */
    @Query("SELECT a FROM AlarmRecord a WHERE a.status = 'ACTIVE' ORDER BY a.alarmTime DESC")
    List<AlarmRecord> findActiveAlarms();

    /**
     * 根据时间范围查找
     */
    @Query("SELECT a FROM AlarmRecord a WHERE a.alarmTime BETWEEN :startTime AND :endTime ORDER BY a.alarmTime DESC")
    List<AlarmRecord> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据点位ID和状态查找
     */
    List<AlarmRecord> findByDataPointIdAndStatusOrderByAlarmTimeDesc(UUID dataPointId, AlarmStatus status);
}





