package org.example.rootmanage.aftersales.repository;

import org.example.rootmanage.aftersales.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 设备Repository接口
 */
public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /**
     * 根据设备编号查找设备
     */
    Optional<Device> findByDeviceNumber(String deviceNumber);

    /**
     * 根据合同ID查找设备列表
     */
    @Query("SELECT d FROM Device d WHERE d.contract.id = :contractId")
    List<Device> findByContractId(@Param("contractId") UUID contractId);

    /**
     * 根据关键词搜索设备（设备编号、设备名称）
     */
    @Query("SELECT d FROM Device d WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "d.deviceNumber LIKE %:keyword% OR " +
           "d.deviceName LIKE %:keyword%)")
    List<Device> searchDevices(@Param("keyword") String keyword);
}





