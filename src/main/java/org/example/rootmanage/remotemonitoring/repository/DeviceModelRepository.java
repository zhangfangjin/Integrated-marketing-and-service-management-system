package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 设备模型数据访问接口
 */
@Repository
public interface DeviceModelRepository extends JpaRepository<DeviceModel, UUID> {

    /**
     * 根据模型编码查找
     */
    Optional<DeviceModel> findByModelCode(String modelCode);

    /**
     * 根据设备类型查找
     */
    List<DeviceModel> findByDeviceType(String deviceType);

    /**
     * 查找启用的模型
     */
    List<DeviceModel> findByEnabled(Boolean enabled);

    /**
     * 搜索设备模型
     */
    @Query("SELECT d FROM DeviceModel d WHERE " +
            "d.modelName LIKE %:keyword% OR " +
            "d.modelCode LIKE %:keyword% OR " +
            "d.deviceType LIKE %:keyword%")
    List<DeviceModel> searchDeviceModels(@Param("keyword") String keyword);
}





