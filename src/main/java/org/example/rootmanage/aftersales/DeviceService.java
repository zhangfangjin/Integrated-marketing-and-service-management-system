package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.DeviceRequest;
import org.example.rootmanage.aftersales.entity.Device;
import org.example.rootmanage.aftersales.repository.DeviceRepository;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.repository.ContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 设备服务类
 * 提供设备管理的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final ContractRepository contractRepository;

    /**
     * 查询所有设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    /**
     * 根据关键词搜索设备
     */
    @Transactional(readOnly = true)
    public List<Device> searchDevices(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return deviceRepository.searchDevices(keyword.trim());
        }
        return deviceRepository.findAll();
    }

    /**
     * 根据合同ID查询设备列表
     */
    @Transactional(readOnly = true)
    public List<Device> findByContractId(UUID contractId) {
        return deviceRepository.findByContractId(contractId);
    }

    /**
     * 根据ID查找设备
     */
    @Transactional(readOnly = true)
    public Device findById(UUID id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
    }

    /**
     * 创建设备
     */
    @Transactional
    public Device create(DeviceRequest request) {
        // 检查设备编号是否已存在
        deviceRepository.findByDeviceNumber(request.getDeviceNumber())
                .ifPresent(d -> {
                    throw new IllegalStateException("设备编号已存在");
                });

        Device device = new Device();
        device.setDeviceNumber(request.getDeviceNumber());
        device.setDeviceName(request.getDeviceName());
        device.setDeviceModel(request.getDeviceModel());
        device.setProductionDate(request.getProductionDate());
        device.setOperatingParameters(request.getOperatingParameters());
        device.setPartsInfo(request.getPartsInfo());
        device.setRemark(request.getRemark());

        // 设置合同关联
        if (request.getContractId() != null) {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
            device.setContract(contract);
            device.setContractNumber(contract.getContractNumber());
        } else if (request.getContractNumber() != null) {
            contractRepository.findByContractNumber(request.getContractNumber())
                    .ifPresent(contract -> {
                        device.setContract(contract);
                        device.setContractNumber(contract.getContractNumber());
                    });
        }

        return deviceRepository.save(device);
    }

    /**
     * 更新设备
     */
    @Transactional
    public Device update(UUID id, DeviceRequest request) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("设备不存在"));

        // 检查设备编号是否被其他设备使用
        if (!device.getDeviceNumber().equals(request.getDeviceNumber())) {
            deviceRepository.findByDeviceNumber(request.getDeviceNumber())
                    .ifPresent(d -> {
                        throw new IllegalStateException("设备编号已被其他设备使用");
                    });
        }

        device.setDeviceNumber(request.getDeviceNumber());
        device.setDeviceName(request.getDeviceName());
        device.setDeviceModel(request.getDeviceModel());
        device.setProductionDate(request.getProductionDate());
        device.setOperatingParameters(request.getOperatingParameters());
        device.setPartsInfo(request.getPartsInfo());
        device.setRemark(request.getRemark());

        // 更新合同关联
        if (request.getContractId() != null) {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
            device.setContract(contract);
            device.setContractNumber(contract.getContractNumber());
        } else if (request.getContractNumber() != null) {
            contractRepository.findByContractNumber(request.getContractNumber())
                    .ifPresent(contract -> {
                        device.setContract(contract);
                        device.setContractNumber(contract.getContractNumber());
                    });
        }

        return deviceRepository.save(device);
    }

    /**
     * 删除设备
     */
    @Transactional
    public void delete(UUID id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        deviceRepository.delete(device);
    }
}

