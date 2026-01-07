package org.example.rootmanage.remotemonitoring;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.remotemonitoring.dto.DataPointRequest;
import org.example.rootmanage.remotemonitoring.dto.ManualDataInputRequest;
import org.example.rootmanage.remotemonitoring.entity.*;
import org.example.rootmanage.remotemonitoring.repository.DataPointRepository;
import org.example.rootmanage.remotemonitoring.repository.DeviceRunningDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 数据点位服务类
 * 提供数据点位的管理功能和手动数据录入
 */
@Service
@RequiredArgsConstructor
public class DataPointService {

    private final DataPointRepository dataPointRepository;
    private final DeviceRunningDataRepository deviceRunningDataRepository;

    /**
     * 获取所有点位
     */
    @Transactional(readOnly = true)
    public List<DataPoint> findAll() {
        return dataPointRepository.findAll();
    }

    /**
     * 根据点位类型查找
     */
    @Transactional(readOnly = true)
    public List<DataPoint> findByPointType(PointType pointType) {
        return dataPointRepository.findByPointType(pointType);
    }

    /**
     * 根据表计ID查找
     */
    @Transactional(readOnly = true)
    public List<DataPoint> findByMeterId(UUID meterId) {
        return dataPointRepository.findByMeterId(meterId);
    }

    /**
     * 根据采集模式查找
     */
    @Transactional(readOnly = true)
    public List<DataPoint> findByCollectionMode(CollectionMode collectionMode) {
        return dataPointRepository.findByCollectionMode(collectionMode);
    }

    /**
     * 搜索点位
     */
    @Transactional(readOnly = true)
    public List<DataPoint> searchDataPoints(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return dataPointRepository.searchDataPoints(keyword.trim());
        }
        return dataPointRepository.findAll();
    }

    /**
     * 根据ID查找点位
     */
    @Transactional(readOnly = true)
    public DataPoint findById(UUID id) {
        return dataPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("点位不存在"));
    }

    /**
     * 根据多个ID查找点位
     */
    @Transactional(readOnly = true)
    public List<DataPoint> findByIds(List<UUID> ids) {
        return dataPointRepository.findByIdIn(ids);
    }

    /**
     * 创建点位
     */
    @Transactional
    public DataPoint create(DataPointRequest request) {
        // 检查编码是否已存在
        dataPointRepository.findByPointCode(request.getPointCode())
                .ifPresent(p -> {
                    throw new IllegalStateException("点位编码已存在");
                });

        DataPoint point = new DataPoint();
        setDataPointProperties(point, request);

        return dataPointRepository.save(point);
    }

    /**
     * 更新点位
     */
    @Transactional
    public DataPoint update(UUID id, DataPointRequest request) {
        DataPoint point = dataPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("点位不存在"));

        // 检查编码是否被其他点位使用
        if (!point.getPointCode().equals(request.getPointCode())) {
            dataPointRepository.findByPointCode(request.getPointCode())
                    .ifPresent(p -> {
                        throw new IllegalStateException("点位编码已被其他点位使用");
                    });
        }

        setDataPointProperties(point, request);

        return dataPointRepository.save(point);
    }

    /**
     * 删除点位
     */
    @Transactional
    public void delete(UUID id) {
        DataPoint point = dataPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("点位不存在"));
        dataPointRepository.delete(point);
    }

    /**
     * 手动录入数据
     */
    @Transactional
    public DeviceRunningData inputManualData(ManualDataInputRequest request) {
        DataPoint point = dataPointRepository.findById(request.getDataPointId())
                .orElseThrow(() -> new IllegalArgumentException("点位不存在"));

        DeviceRunningData data = new DeviceRunningData();
        data.setDataPointId(request.getDataPointId());
        data.setCollectionTime(request.getCollectionTime() != null ? request.getCollectionTime() : LocalDateTime.now());
        data.setValue(request.getValue());
        data.setRawValue(request.getValue()); // 手动录入时原始值等于采集值
        data.setQuality(DataQuality.GOOD);
        data.setSource(DataSource.MANUAL);
        data.setInputById(request.getInputById());
        data.setInputByName(request.getInputByName());
        data.setRemark(request.getRemark());

        // 更新点位的当前值
        point.setCurrentValue(request.getValue());
        point.setLastCollectionTime(data.getCollectionTime());
        dataPointRepository.save(point);

        return deviceRunningDataRepository.save(data);
    }

    /**
     * 更新点位当前值（用于自动采集）
     */
    @Transactional
    public void updateCurrentValue(UUID dataPointId, Double value, LocalDateTime collectionTime) {
        DataPoint point = dataPointRepository.findById(dataPointId)
                .orElseThrow(() -> new IllegalArgumentException("点位不存在"));

        point.setCurrentValue(value);
        point.setLastCollectionTime(collectionTime);
        dataPointRepository.save(point);
    }

    /**
     * 设置点位属性
     */
    private void setDataPointProperties(DataPoint point, DataPointRequest request) {
        point.setPointCode(request.getPointCode());
        point.setPointName(request.getPointName());
        point.setPointType(request.getPointType() != null ? request.getPointType() : PointType.REAL);
        point.setDataType(request.getDataType() != null ? request.getDataType() : MeasurementType.ANALOG);
        point.setMeterId(request.getMeterId());
        point.setMeasurementId(request.getMeasurementId());
        point.setUnit(request.getUnit());
        point.setMultiplier(request.getMultiplier() != null ? request.getMultiplier() : 1.0);
        point.setCollectionMode(request.getCollectionMode() != null ? request.getCollectionMode() : CollectionMode.POLLING);
        point.setCollectionInterval(request.getCollectionInterval() != null ? request.getCollectionInterval() : 60);
        point.setPrecision(request.getPrecision() != null ? request.getPrecision() : 2);
        point.setMinValue(request.getMinValue());
        point.setMaxValue(request.getMaxValue());
        point.setProtocol(request.getProtocol());
        point.setCommAddress(request.getCommAddress());
        point.setRegisterAddress(request.getRegisterAddress());
        point.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        point.setAlarmEnabled(request.getAlarmEnabled() != null ? request.getAlarmEnabled() : false);
        point.setRemark(request.getRemark());
    }
}

















