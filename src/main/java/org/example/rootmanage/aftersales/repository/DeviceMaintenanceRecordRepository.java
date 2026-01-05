package org.example.rootmanage.aftersales.repository;

import org.example.rootmanage.aftersales.entity.DeviceMaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 设备维护记录Repository接口
 */
public interface DeviceMaintenanceRecordRepository extends JpaRepository<DeviceMaintenanceRecord, UUID> {

    /**
     * 根据设备ID查找维护记录列表
     */
    @Query("SELECT r FROM DeviceMaintenanceRecord r WHERE r.device.id = :deviceId ORDER BY r.maintenanceDate DESC, r.createTime DESC")
    List<DeviceMaintenanceRecord> findByDeviceId(@Param("deviceId") UUID deviceId);

    /**
     * 根据设备编号查找维护记录列表
     */
    List<DeviceMaintenanceRecord> findByDeviceNumberOrderByMaintenanceDateDesc(String deviceNumber);
}





