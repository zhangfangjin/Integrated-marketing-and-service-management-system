package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.MaintenancePlanRequest;
import org.example.rootmanage.aftersales.dto.ReplacementPartItemRequest;
import org.example.rootmanage.aftersales.entity.Device;
import org.example.rootmanage.aftersales.entity.MaintenancePlan;
import org.example.rootmanage.aftersales.entity.PlanType;
import org.example.rootmanage.aftersales.entity.ReplacementPartItem;
import org.example.rootmanage.aftersales.repository.DeviceRepository;
import org.example.rootmanage.aftersales.repository.MaintenancePlanRepository;
import org.example.rootmanage.aftersales.repository.ReplacementPartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 维护计划服务类
 */
@Service
@RequiredArgsConstructor
public class MaintenancePlanService {

    private final MaintenancePlanRepository planRepository;
    private final ReplacementPartItemRepository partItemRepository;
    private final DeviceRepository deviceRepository;

    /**
     * 查询所有维护计划
     */
    @Transactional(readOnly = true)
    public List<MaintenancePlan> findAll() {
        return planRepository.findAll();
    }

    /**
     * 根据计划类型查询维护计划列表
     */
    @Transactional(readOnly = true)
    public List<MaintenancePlan> findByPlanType(PlanType planType) {
        return planRepository.findByPlanTypeOrderByCreateTimeDesc(planType);
    }

    /**
     * 根据设备类型查询维护计划（设备类型级别）
     */
    @Transactional(readOnly = true)
    public MaintenancePlan findByDeviceType(String deviceType) {
        return planRepository.findByDeviceType(PlanType.DEVICE_TYPE, deviceType)
                .orElse(null);
    }

    /**
     * 根据设备ID查询维护计划（具体设备级别）
     */
    @Transactional(readOnly = true)
    public MaintenancePlan findByDeviceId(UUID deviceId) {
        return planRepository.findByDeviceId(PlanType.SPECIFIC_DEVICE, deviceId)
                .orElse(null);
    }

    /**
     * 根据关键词搜索维护计划
     */
    @Transactional(readOnly = true)
    public List<MaintenancePlan> searchMaintenancePlans(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return planRepository.searchMaintenancePlans(keyword.trim());
        }
        return planRepository.findAll();
    }

    /**
     * 根据ID查找维护计划
     */
    @Transactional(readOnly = true)
    public MaintenancePlan findById(UUID id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("维护计划不存在"));
    }

    /**
     * 创建维护计划
     */
    @Transactional
    public MaintenancePlan create(MaintenancePlanRequest request) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setPlanType(request.getPlanType());
        plan.setMaintenanceCycle(request.getMaintenanceCycle());
        plan.setMaintenanceItems(request.getMaintenanceItems());
        plan.setPrecautions(request.getPrecautions());
        plan.setRemark(request.getRemark());
        plan.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        plan.setHasMaintenancePlan(true);

        // 根据计划类型设置关联
        if (request.getPlanType() == PlanType.DEVICE_TYPE) {
            // 设备类型级别：检查是否已存在该设备类型的计划
            if (request.getDeviceType() != null) {
                planRepository.findByDeviceType(PlanType.DEVICE_TYPE, request.getDeviceType())
                        .ifPresent(existing -> {
                            throw new IllegalStateException("该设备类型已存在维护计划");
                        });
                plan.setDeviceType(request.getDeviceType());
            }
        } else if (request.getPlanType() == PlanType.SPECIFIC_DEVICE) {
            // 具体设备级别
            if (request.getDeviceId() != null) {
                Device device = deviceRepository.findById(request.getDeviceId())
                        .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
                plan.setDevice(device);
                plan.setDeviceNumber(device.getDeviceNumber());
                plan.setDeviceName(device.getDeviceName());
            } else if (request.getDeviceNumber() != null) {
                deviceRepository.findByDeviceNumber(request.getDeviceNumber())
                        .ifPresent(device -> {
                            plan.setDevice(device);
                            plan.setDeviceNumber(device.getDeviceNumber());
                            plan.setDeviceName(device.getDeviceName());
                        });
            }
        }

        MaintenancePlan savedPlan = planRepository.save(plan);

        // 保存换件清单项
        if (request.getReplacementParts() != null && !request.getReplacementParts().isEmpty()) {
            saveReplacementParts(savedPlan, request.getReplacementParts());
        }

        return savedPlan;
    }

    /**
     * 更新维护计划
     */
    @Transactional
    public MaintenancePlan update(UUID id, MaintenancePlanRequest request) {
        MaintenancePlan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("维护计划不存在"));

        plan.setMaintenanceCycle(request.getMaintenanceCycle());
        plan.setMaintenanceItems(request.getMaintenanceItems());
        plan.setPrecautions(request.getPrecautions());
        plan.setRemark(request.getRemark());
        if (request.getEnabled() != null) {
            plan.setEnabled(request.getEnabled());
        }

        // 更新设备关联（如果需要）
        if (request.getPlanType() == PlanType.SPECIFIC_DEVICE) {
            if (request.getDeviceId() != null) {
                Device device = deviceRepository.findById(request.getDeviceId())
                        .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
                plan.setDevice(device);
                plan.setDeviceNumber(device.getDeviceNumber());
                plan.setDeviceName(device.getDeviceName());
            }
        }

        MaintenancePlan savedPlan = planRepository.save(plan);

        // 删除旧的换件清单项
        partItemRepository.deleteByMaintenancePlanId(id);

        // 保存新的换件清单项
        if (request.getReplacementParts() != null && !request.getReplacementParts().isEmpty()) {
            saveReplacementParts(savedPlan, request.getReplacementParts());
        }

        return savedPlan;
    }

    /**
     * 删除维护计划
     */
    @Transactional
    public void delete(UUID id) {
        MaintenancePlan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("维护计划不存在"));

        // 删除关联的换件清单项
        partItemRepository.deleteByMaintenancePlanId(id);

        // 删除维护计划
        planRepository.delete(plan);
    }

    /**
     * 保存换件清单项
     */
    private void saveReplacementParts(MaintenancePlan plan, List<ReplacementPartItemRequest> parts) {
        int orderNo = 1;
        for (ReplacementPartItemRequest partRequest : parts) {
            ReplacementPartItem item = new ReplacementPartItem();
            item.setMaintenancePlan(plan);
            item.setPartDrawingNumber(partRequest.getPartDrawingNumber());
            item.setPartName(partRequest.getPartName());
            item.setMaterial(partRequest.getMaterial());
            item.setQuantity(partRequest.getQuantity());
            item.setOrderNo(partRequest.getOrderNo() != null ? partRequest.getOrderNo() : orderNo++);
            partItemRepository.save(item);
        }
    }
}

