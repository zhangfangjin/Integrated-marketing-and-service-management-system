package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.Meter;
import org.example.rootmanage.remotemonitoring.entity.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 表计数据访问接口
 */
@Repository
public interface MeterRepository extends JpaRepository<Meter, UUID> {

    /**
     * 根据表计编号查找
     */
    Optional<Meter> findByMeterCode(String meterCode);

    /**
     * 根据表计类型查找
     */
    List<Meter> findByMeterType(MeterType meterType);

    /**
     * 根据空间节点ID查找
     */
    List<Meter> findBySpaceNodeId(UUID spaceNodeId);

    /**
     * 根据设备ID查找
     */
    List<Meter> findByDeviceId(UUID deviceId);

    /**
     * 搜索表计
     */
    @Query("SELECT m FROM Meter m WHERE " +
            "m.meterName LIKE %:keyword% OR " +
            "m.meterCode LIKE %:keyword% OR " +
            "m.meterModel LIKE %:keyword%")
    List<Meter> searchMeters(@Param("keyword") String keyword);

    /**
     * 根据启用状态查找
     */
    List<Meter> findByEnabled(Boolean enabled);
}





