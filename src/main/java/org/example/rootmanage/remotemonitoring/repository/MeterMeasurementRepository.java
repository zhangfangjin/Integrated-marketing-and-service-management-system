package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.MeterMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 表计检测属性数据访问接口
 */
@Repository
public interface MeterMeasurementRepository extends JpaRepository<MeterMeasurement, UUID> {

    /**
     * 根据表计ID查找检测属性列表
     */
    List<MeterMeasurement> findByMeterIdOrderBySortOrder(UUID meterId);

    /**
     * 根据表计ID删除所有检测属性
     */
    void deleteByMeterId(UUID meterId);

    /**
     * 根据启用状态查找
     */
    List<MeterMeasurement> findByEnabled(Boolean enabled);
}

















