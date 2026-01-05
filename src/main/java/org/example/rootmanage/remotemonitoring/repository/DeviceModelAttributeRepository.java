package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.DeviceModelAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 设备模型属性数据访问接口
 */
@Repository
public interface DeviceModelAttributeRepository extends JpaRepository<DeviceModelAttribute, UUID> {

    /**
     * 根据设备模型ID查找属性列表
     */
    List<DeviceModelAttribute> findByDeviceModelIdOrderBySortOrder(UUID deviceModelId);

    /**
     * 根据设备模型ID删除所有属性
     */
    void deleteByDeviceModelId(UUID deviceModelId);
}





