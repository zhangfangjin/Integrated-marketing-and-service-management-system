package org.example.rootmanage.remotemonitoring;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.MeterAttributeRequest;
import org.example.rootmanage.remotemonitoring.dto.MeterMeasurementRequest;
import org.example.rootmanage.remotemonitoring.dto.MeterRequest;
import org.example.rootmanage.remotemonitoring.entity.*;
import org.example.rootmanage.remotemonitoring.repository.MeterAttributeRepository;
import org.example.rootmanage.remotemonitoring.repository.MeterMeasurementRepository;
import org.example.rootmanage.remotemonitoring.repository.MeterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 表计服务类
 * 提供表计信息的管理功能
 */
@Service
@RequiredArgsConstructor
public class MeterService {

    private final MeterRepository meterRepository;
    private final MeterAttributeRepository meterAttributeRepository;
    private final MeterMeasurementRepository meterMeasurementRepository;

    /**
     * 获取所有表计
     */
    @Transactional(readOnly = true)
    public List<Meter> findAll() {
        return meterRepository.findAll();
    }

    /**
     * 根据表计类型查找
     */
    @Transactional(readOnly = true)
    public List<Meter> findByMeterType(MeterType meterType) {
        return meterRepository.findByMeterType(meterType);
    }

    /**
     * 根据空间节点ID查找
     */
    @Transactional(readOnly = true)
    public List<Meter> findBySpaceNodeId(UUID spaceNodeId) {
        return meterRepository.findBySpaceNodeId(spaceNodeId);
    }

    /**
     * 根据设备ID查找
     */
    @Transactional(readOnly = true)
    public List<Meter> findByDeviceId(UUID deviceId) {
        return meterRepository.findByDeviceId(deviceId);
    }

    /**
     * 搜索表计
     */
    @Transactional(readOnly = true)
    public List<Meter> searchMeters(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return meterRepository.searchMeters(keyword.trim());
        }
        return meterRepository.findAll();
    }

    /**
     * 根据ID查找表计
     */
    @Transactional(readOnly = true)
    public Meter findById(UUID id) {
        return meterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("表计不存在"));
    }

    /**
     * 创建表计
     */
    @Transactional
    public Meter create(MeterRequest request) {
        // 检查编号是否已存在
        meterRepository.findByMeterCode(request.getMeterCode())
                .ifPresent(m -> {
                    throw new IllegalStateException("表计编号已存在");
                });

        Meter meter = new Meter();
        setMeterProperties(meter, request);

        Meter savedMeter = meterRepository.save(meter);

        // 保存静态属性
        if (request.getStaticAttributes() != null) {
            for (MeterAttributeRequest attrRequest : request.getStaticAttributes()) {
                MeterAttribute attr = new MeterAttribute();
                attr.setMeterId(savedMeter.getId());
                attr.setAttributeName(attrRequest.getAttributeName());
                attr.setAttributeCode(attrRequest.getAttributeCode());
                attr.setAttributeValue(attrRequest.getAttributeValue());
                attr.setValueType(attrRequest.getValueType() != null ? attrRequest.getValueType() : AttributeValueType.STRING);
                attr.setUnit(attrRequest.getUnit());
                attr.setSortOrder(attrRequest.getSortOrder() != null ? attrRequest.getSortOrder() : 0);
                attr.setRemark(attrRequest.getRemark());
                meterAttributeRepository.save(attr);
            }
        }

        // 保存检测属性
        if (request.getMeasurements() != null) {
            for (MeterMeasurementRequest measRequest : request.getMeasurements()) {
                MeterMeasurement meas = new MeterMeasurement();
                meas.setMeterId(savedMeter.getId());
                meas.setMeasurementName(measRequest.getMeasurementName());
                meas.setMeasurementCode(measRequest.getMeasurementCode());
                meas.setMeasurementType(measRequest.getMeasurementType() != null ? measRequest.getMeasurementType() : MeasurementType.ANALOG);
                meas.setUnit(measRequest.getUnit());
                meas.setPrecision(measRequest.getPrecision() != null ? measRequest.getPrecision() : 2);
                meas.setMinValue(measRequest.getMinValue());
                meas.setMaxValue(measRequest.getMaxValue());
                meas.setDataPointId(measRequest.getDataPointId());
                meas.setSortOrder(measRequest.getSortOrder() != null ? measRequest.getSortOrder() : 0);
                meas.setEnabled(measRequest.getEnabled() != null ? measRequest.getEnabled() : true);
                meas.setRemark(measRequest.getRemark());
                meterMeasurementRepository.save(meas);
            }
        }

        return savedMeter;
    }

    /**
     * 更新表计
     */
    @Transactional
    public Meter update(UUID id, MeterRequest request) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("表计不存在"));

        // 检查编号是否被其他表计使用
        if (!meter.getMeterCode().equals(request.getMeterCode())) {
            meterRepository.findByMeterCode(request.getMeterCode())
                    .ifPresent(m -> {
                        throw new IllegalStateException("表计编号已被其他表计使用");
                    });
        }

        setMeterProperties(meter, request);

        // 更新静态属性（先删除再新增）
        meterAttributeRepository.deleteByMeterId(id);
        if (request.getStaticAttributes() != null) {
            for (MeterAttributeRequest attrRequest : request.getStaticAttributes()) {
                MeterAttribute attr = new MeterAttribute();
                attr.setMeterId(id);
                attr.setAttributeName(attrRequest.getAttributeName());
                attr.setAttributeCode(attrRequest.getAttributeCode());
                attr.setAttributeValue(attrRequest.getAttributeValue());
                attr.setValueType(attrRequest.getValueType() != null ? attrRequest.getValueType() : AttributeValueType.STRING);
                attr.setUnit(attrRequest.getUnit());
                attr.setSortOrder(attrRequest.getSortOrder() != null ? attrRequest.getSortOrder() : 0);
                attr.setRemark(attrRequest.getRemark());
                meterAttributeRepository.save(attr);
            }
        }

        // 更新检测属性
        meterMeasurementRepository.deleteByMeterId(id);
        if (request.getMeasurements() != null) {
            for (MeterMeasurementRequest measRequest : request.getMeasurements()) {
                MeterMeasurement meas = new MeterMeasurement();
                meas.setMeterId(id);
                meas.setMeasurementName(measRequest.getMeasurementName());
                meas.setMeasurementCode(measRequest.getMeasurementCode());
                meas.setMeasurementType(measRequest.getMeasurementType() != null ? measRequest.getMeasurementType() : MeasurementType.ANALOG);
                meas.setUnit(measRequest.getUnit());
                meas.setPrecision(measRequest.getPrecision() != null ? measRequest.getPrecision() : 2);
                meas.setMinValue(measRequest.getMinValue());
                meas.setMaxValue(measRequest.getMaxValue());
                meas.setDataPointId(measRequest.getDataPointId());
                meas.setSortOrder(measRequest.getSortOrder() != null ? measRequest.getSortOrder() : 0);
                meas.setEnabled(measRequest.getEnabled() != null ? measRequest.getEnabled() : true);
                meas.setRemark(measRequest.getRemark());
                meterMeasurementRepository.save(meas);
            }
        }

        return meterRepository.save(meter);
    }

    /**
     * 删除表计
     */
    @Transactional
    public void delete(UUID id) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("表计不存在"));

        // 删除关联的属性和检测属性
        meterAttributeRepository.deleteByMeterId(id);
        meterMeasurementRepository.deleteByMeterId(id);

        meterRepository.delete(meter);
    }

    /**
     * 设置表计属性
     */
    private void setMeterProperties(Meter meter, MeterRequest request) {
        meter.setMeterCode(request.getMeterCode());
        meter.setMeterName(request.getMeterName());
        meter.setMeterType(request.getMeterType() != null ? request.getMeterType() : MeterType.OTHER);
        meter.setMeterFunction(request.getMeterFunction());
        meter.setMeterModel(request.getMeterModel());
        meter.setMeterUnit(request.getMeterUnit());
        meter.setMultiplier(request.getMultiplier() != null ? request.getMultiplier() : 1.0);
        meter.setInstallLocation(request.getInstallLocation());
        meter.setInstallDate(request.getInstallDate());
        meter.setSpaceNodeId(request.getSpaceNodeId());
        meter.setDeviceId(request.getDeviceId());
        meter.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        meter.setProtocol(request.getProtocol());
        meter.setCommAddress(request.getCommAddress());
        meter.setRemark(request.getRemark());
    }
}





