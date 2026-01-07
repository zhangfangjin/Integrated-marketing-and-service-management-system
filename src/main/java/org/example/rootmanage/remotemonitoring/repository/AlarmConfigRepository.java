package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.AlarmConfig;
import org.example.rootmanage.remotemonitoring.entity.AlarmLevel;
import org.example.rootmanage.remotemonitoring.entity.AlarmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 报警配置数据访问接口
 */
@Repository
public interface AlarmConfigRepository extends JpaRepository<AlarmConfig, UUID> {

    /**
     * 根据报警编码查找
     */
    Optional<AlarmConfig> findByAlarmCode(String alarmCode);

    /**
     * 根据点位ID查找
     */
    List<AlarmConfig> findByDataPointId(UUID dataPointId);

    /**
     * 根据点位ID和报警类型查找
     */
    Optional<AlarmConfig> findByDataPointIdAndAlarmType(UUID dataPointId, AlarmType alarmType);

    /**
     * 根据报警类型查找
     */
    List<AlarmConfig> findByAlarmType(AlarmType alarmType);

    /**
     * 根据报警级别查找
     */
    List<AlarmConfig> findByAlarmLevel(AlarmLevel alarmLevel);

    /**
     * 查找启用的报警配置
     */
    List<AlarmConfig> findByEnabled(Boolean enabled);

    /**
     * 搜索报警配置
     */
    @Query("SELECT a FROM AlarmConfig a WHERE " +
            "a.alarmName LIKE %:keyword% OR " +
            "a.alarmCode LIKE %:keyword%")
    List<AlarmConfig> searchAlarmConfigs(@Param("keyword") String keyword);
}

















