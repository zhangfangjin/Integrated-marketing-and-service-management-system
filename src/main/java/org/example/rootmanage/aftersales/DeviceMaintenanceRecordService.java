package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.DeviceMaintenanceRecordRequest;
import org.example.rootmanage.aftersales.entity.Device;
import org.example.rootmanage.aftersales.entity.DeviceMaintenanceRecord;
import org.example.rootmanage.aftersales.repository.DeviceMaintenanceRecordRepository;
import org.example.rootmanage.aftersales.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 设备维护记录服务类
 */
@Service
@RequiredArgsConstructor
public class DeviceMaintenanceRecordService {

    private final DeviceMaintenanceRecordRepository recordRepository;
    private final DeviceRepository deviceRepository;

    /**
     * 查询所有维护记录
     */
    @Transactional(readOnly = true)
    public List<DeviceMaintenanceRecord> findAll() {
        return recordRepository.findAll();
    }

    /**
     * 根据设备ID查询维护记录列表
     */
    @Transactional(readOnly = true)
    public List<DeviceMaintenanceRecord> findByDeviceId(UUID deviceId) {
        return recordRepository.findByDeviceId(deviceId);
    }

    /**
     * 根据设备编号查询维护记录列表
     */
    @Transactional(readOnly = true)
    public List<DeviceMaintenanceRecord> findByDeviceNumber(String deviceNumber) {
        return recordRepository.findByDeviceNumberOrderByMaintenanceDateDesc(deviceNumber);
    }

    /**
     * 根据ID查找维护记录
     */
    @Transactional(readOnly = true)
    public DeviceMaintenanceRecord findById(UUID id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("维护记录不存在"));
    }

    /**
     * 创建维护记录
     */
    @Transactional
    public DeviceMaintenanceRecord create(DeviceMaintenanceRecordRequest request) {
        Device device = null;
        if (request.getDeviceId() != null) {
            device = deviceRepository.findById(request.getDeviceId())
                    .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        } else if (request.getDeviceNumber() != null) {
            device = deviceRepository.findByDeviceNumber(request.getDeviceNumber())
                    .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        } else {
            throw new IllegalArgumentException("设备ID或设备编号不能为空");
        }

        DeviceMaintenanceRecord record = new DeviceMaintenanceRecord();
        record.setDevice(device);
        record.setDeviceNumber(device.getDeviceNumber());
        record.setMaintenanceDate(request.getMaintenanceDate());
        record.setFaultReason(request.getFaultReason());
        record.setSolution(request.getSolution());
        record.setPartsReplacement(request.getPartsReplacement());
        record.setIsExternalPartsIssue(request.getIsExternalPartsIssue() != null ? request.getIsExternalPartsIssue() : false);
        record.setExternalSupplier(request.getExternalSupplier());
        record.setExternalDeviceModel(request.getExternalDeviceModel());
        record.setMaintenancePersonId(request.getMaintenancePersonId());
        record.setMaintenancePersonName(request.getMaintenancePersonName());
        record.setRemark(request.getRemark());

        return recordRepository.save(record);
    }

    /**
     * 更新维护记录
     */
    @Transactional
    public DeviceMaintenanceRecord update(UUID id, DeviceMaintenanceRecordRequest request) {
        DeviceMaintenanceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("维护记录不存在"));

        // 更新设备关联
        if (request.getDeviceId() != null) {
            Device device = deviceRepository.findById(request.getDeviceId())
                    .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
            record.setDevice(device);
            record.setDeviceNumber(device.getDeviceNumber());
        } else if (request.getDeviceNumber() != null) {
            deviceRepository.findByDeviceNumber(request.getDeviceNumber())
                    .ifPresent(device -> {
                        record.setDevice(device);
                        record.setDeviceNumber(device.getDeviceNumber());
                    });
        }

        record.setMaintenanceDate(request.getMaintenanceDate());
        record.setFaultReason(request.getFaultReason());
        record.setSolution(request.getSolution());
        record.setPartsReplacement(request.getPartsReplacement());
        if (request.getIsExternalPartsIssue() != null) {
            record.setIsExternalPartsIssue(request.getIsExternalPartsIssue());
        }
        record.setExternalSupplier(request.getExternalSupplier());
        record.setExternalDeviceModel(request.getExternalDeviceModel());
        record.setMaintenancePersonId(request.getMaintenancePersonId());
        record.setMaintenancePersonName(request.getMaintenancePersonName());
        record.setRemark(request.getRemark());

        return recordRepository.save(record);
    }

    /**
     * 删除维护记录
     */
    @Transactional
    public void delete(UUID id) {
        DeviceMaintenanceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("维护记录不存在"));
        recordRepository.delete(record);
    }
}





