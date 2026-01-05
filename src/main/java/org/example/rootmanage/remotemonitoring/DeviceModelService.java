package org.example.rootmanage.remotemonitoring;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.DeviceModelAttributeRequest;
import org.example.rootmanage.remotemonitoring.dto.DeviceModelRequest;
import org.example.rootmanage.remotemonitoring.entity.AttributeValueType;
import org.example.rootmanage.remotemonitoring.entity.DeviceModel;
import org.example.rootmanage.remotemonitoring.entity.DeviceModelAttribute;
import org.example.rootmanage.remotemonitoring.repository.DeviceModelAttributeRepository;
import org.example.rootmanage.remotemonitoring.repository.DeviceModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 设备模型服务类
 * 提供设备模型的管理功能
 */
@Service
@RequiredArgsConstructor
public class DeviceModelService {

    private final DeviceModelRepository deviceModelRepository;
    private final DeviceModelAttributeRepository attributeRepository;

    /**
     * 获取所有设备模型
     */
    @Transactional(readOnly = true)
    public List<DeviceModel> findAll() {
        return deviceModelRepository.findAll();
    }

    /**
     * 根据设备类型查找
     */
    @Transactional(readOnly = true)
    public List<DeviceModel> findByDeviceType(String deviceType) {
        return deviceModelRepository.findByDeviceType(deviceType);
    }

    /**
     * 获取启用的设备模型
     */
    @Transactional(readOnly = true)
    public List<DeviceModel> findEnabled() {
        return deviceModelRepository.findByEnabled(true);
    }

    /**
     * 搜索设备模型
     */
    @Transactional(readOnly = true)
    public List<DeviceModel> searchDeviceModels(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return deviceModelRepository.searchDeviceModels(keyword.trim());
        }
        return deviceModelRepository.findAll();
    }

    /**
     * 根据ID查找设备模型
     */
    @Transactional(readOnly = true)
    public DeviceModel findById(UUID id) {
        return deviceModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("设备模型不存在"));
    }

    /**
     * 创建设备模型
     */
    @Transactional
    public DeviceModel create(DeviceModelRequest request) {
        // 检查编码是否已存在
        deviceModelRepository.findByModelCode(request.getModelCode())
                .ifPresent(m -> {
                    throw new IllegalStateException("设备模型编码已存在");
                });

        DeviceModel model = new DeviceModel();
        setDeviceModelProperties(model, request);

        DeviceModel savedModel = deviceModelRepository.save(model);

        // 保存属性
        if (request.getAttributes() != null) {
            for (DeviceModelAttributeRequest attrRequest : request.getAttributes()) {
                DeviceModelAttribute attr = new DeviceModelAttribute();
                attr.setDeviceModelId(savedModel.getId());
                attr.setAttributeName(attrRequest.getAttributeName());
                attr.setAttributeCode(attrRequest.getAttributeCode());
                attr.setDefaultValue(attrRequest.getDefaultValue());
                attr.setValueType(attrRequest.getValueType() != null ? attrRequest.getValueType() : AttributeValueType.STRING);
                attr.setUnit(attrRequest.getUnit());
                attr.setRequired(attrRequest.getRequired() != null ? attrRequest.getRequired() : false);
                attr.setSortOrder(attrRequest.getSortOrder() != null ? attrRequest.getSortOrder() : 0);
                attr.setRemark(attrRequest.getRemark());
                attributeRepository.save(attr);
            }
        }

        return savedModel;
    }

    /**
     * 更新设备模型
     */
    @Transactional
    public DeviceModel update(UUID id, DeviceModelRequest request) {
        DeviceModel model = deviceModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("设备模型不存在"));

        // 检查编码是否被其他模型使用
        if (!model.getModelCode().equals(request.getModelCode())) {
            deviceModelRepository.findByModelCode(request.getModelCode())
                    .ifPresent(m -> {
                        throw new IllegalStateException("设备模型编码已被其他模型使用");
                    });
        }

        setDeviceModelProperties(model, request);

        // 更新属性（先删除再新增）
        attributeRepository.deleteByDeviceModelId(id);
        if (request.getAttributes() != null) {
            for (DeviceModelAttributeRequest attrRequest : request.getAttributes()) {
                DeviceModelAttribute attr = new DeviceModelAttribute();
                attr.setDeviceModelId(id);
                attr.setAttributeName(attrRequest.getAttributeName());
                attr.setAttributeCode(attrRequest.getAttributeCode());
                attr.setDefaultValue(attrRequest.getDefaultValue());
                attr.setValueType(attrRequest.getValueType() != null ? attrRequest.getValueType() : AttributeValueType.STRING);
                attr.setUnit(attrRequest.getUnit());
                attr.setRequired(attrRequest.getRequired() != null ? attrRequest.getRequired() : false);
                attr.setSortOrder(attrRequest.getSortOrder() != null ? attrRequest.getSortOrder() : 0);
                attr.setRemark(attrRequest.getRemark());
                attributeRepository.save(attr);
            }
        }

        return deviceModelRepository.save(model);
    }

    /**
     * 删除设备模型
     */
    @Transactional
    public void delete(UUID id) {
        DeviceModel model = deviceModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("设备模型不存在"));

        attributeRepository.deleteByDeviceModelId(id);
        deviceModelRepository.delete(model);
    }

    /**
     * 设置设备模型属性
     */
    private void setDeviceModelProperties(DeviceModel model, DeviceModelRequest request) {
        model.setModelCode(request.getModelCode());
        model.setModelName(request.getModelName());
        model.setDeviceType(request.getDeviceType());
        model.setBrand(request.getBrand());
        model.setSpecification(request.getSpecification());
        model.setRatedPower(request.getRatedPower());
        model.setRatedVoltage(request.getRatedVoltage());
        model.setRatedCurrent(request.getRatedCurrent());
        model.setRatedFlow(request.getRatedFlow());
        model.setRatedHead(request.getRatedHead());
        model.setDescription(request.getDescription());
        model.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        model.setRemark(request.getRemark());
    }
}





