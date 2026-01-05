package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.AfterSalesActivityRequest;
import org.example.rootmanage.aftersales.dto.AfterSalesOrderRequest;
import org.example.rootmanage.aftersales.dto.AfterSalesOrderResponse;
import org.example.rootmanage.aftersales.dto.AssignHandlerRequest;
import org.example.rootmanage.aftersales.dto.CompleteServiceRequest;
import org.example.rootmanage.aftersales.dto.EvaluateServiceRequest;
import org.example.rootmanage.aftersales.entity.AfterSalesActivity;
import org.example.rootmanage.aftersales.entity.AfterSalesOrder;
import org.example.rootmanage.aftersales.entity.AfterSalesType;
import org.example.rootmanage.aftersales.repository.AfterSalesActivityRepository;
import org.example.rootmanage.aftersales.repository.AfterSalesOrderRepository;
import org.example.rootmanage.contract.entity.Contract;
import org.example.rootmanage.contract.repository.ContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 售后服务服务类
 * 提供售后服务的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AfterSalesService {

    private final AfterSalesOrderRepository afterSalesOrderRepository;
    private final AfterSalesActivityRepository afterSalesActivityRepository;
    private final ContractRepository contractRepository;

    /**
     * 查询所有售后服务单列表
     */
    @Transactional(readOnly = true)
    public List<AfterSalesOrderResponse> findAll() {
        return afterSalesOrderRepository.findAll().stream()
                .map(AfterSalesOrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据关键词搜索售后服务单
     */
    @Transactional(readOnly = true)
    public List<AfterSalesOrderResponse> searchAfterSalesOrders(String keyword) {
        List<AfterSalesOrder> orders;
        if (keyword != null && !keyword.trim().isEmpty()) {
            orders = afterSalesOrderRepository.searchAfterSalesOrders(keyword.trim());
        } else {
            orders = afterSalesOrderRepository.findAll();
        }
        return orders.stream()
                .map(AfterSalesOrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查找售后服务单
     */
    @Transactional(readOnly = true)
    public AfterSalesOrderResponse findById(UUID id) {
        AfterSalesOrder order = afterSalesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("售后服务单不存在"));
        return AfterSalesOrderResponse.fromEntity(order);
    }

    /**
     * 创建售后服务单
     */
    @Transactional
    public AfterSalesOrderResponse create(AfterSalesOrderRequest request) {
        AfterSalesOrder order = new AfterSalesOrder();

        // 生成服务单号（如果未提供）
        String serviceOrderNumber = request.getServiceOrderNumber();
        if (serviceOrderNumber == null || serviceOrderNumber.trim().isEmpty()) {
            serviceOrderNumber = generateServiceOrderNumber();
        }

        // 检查服务单号是否已存在
        afterSalesOrderRepository.findByServiceOrderNumber(serviceOrderNumber)
                .ifPresent(o -> {
                    throw new IllegalStateException("服务单号已存在");
                });

        // 设置合同关联
        if (request.getContractId() != null) {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
            order.setContract(contract);
            // 设置冗余字段
            order.setContractNumber(contract.getContractNumber());
            order.setContractName(contract.getContractName());
        } else if (request.getContractNumber() != null) {
            // 如果没有提供合同ID，尝试根据合同编号查找
            contractRepository.findByContractNumber(request.getContractNumber())
                    .ifPresent(contract -> {
                        order.setContract(contract);
                        order.setContractNumber(contract.getContractNumber());
                        order.setContractName(contract.getContractName());
                    });
        }

        // 映射请求数据到实体
        mapRequestToEntity(request, order);
        order.setServiceOrderNumber(serviceOrderNumber);

        // 设置默认状态
        if (order.getServiceStatus() == null || order.getServiceStatus().trim().isEmpty()) {
            order.setServiceStatus("待分配");
        }

        // 如果是无偿三包类型，需要审批
        if (request.getServiceType() == AfterSalesType.FREE_WARRANTY) {
            order.setNeedsApproval(true);
            order.setApprovalStatus("待审批");
        } else {
            order.setNeedsApproval(false);
        }

        AfterSalesOrder savedOrder = afterSalesOrderRepository.save(order);
        return AfterSalesOrderResponse.fromEntity(savedOrder);
    }

    /**
     * 更新售后服务单
     */
    @Transactional
    public AfterSalesOrderResponse update(UUID id, AfterSalesOrderRequest request) {
        AfterSalesOrder order = afterSalesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("售后服务单不存在"));

        // 检查服务单号是否被其他服务单使用
        if (request.getServiceOrderNumber() != null 
                && !request.getServiceOrderNumber().equals(order.getServiceOrderNumber())) {
            afterSalesOrderRepository.findByServiceOrderNumber(request.getServiceOrderNumber())
                    .ifPresent(o -> {
                        throw new IllegalStateException("服务单号已被其他服务单使用");
                    });
        }

        // 更新合同关联
        if (request.getContractId() != null) {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new IllegalArgumentException("合同不存在"));
            order.setContract(contract);
            order.setContractNumber(contract.getContractNumber());
            order.setContractName(contract.getContractName());
        } else if (request.getContractNumber() != null) {
            contractRepository.findByContractNumber(request.getContractNumber())
                    .ifPresent(contract -> {
                        order.setContract(contract);
                        order.setContractNumber(contract.getContractNumber());
                        order.setContractName(contract.getContractName());
                    });
        }

        // 映射请求数据到实体
        mapRequestToEntity(request, order);

        // 如果是无偿三包类型，需要审批
        if (request.getServiceType() == AfterSalesType.FREE_WARRANTY) {
            order.setNeedsApproval(true);
            if (order.getApprovalStatus() == null || order.getApprovalStatus().trim().isEmpty()) {
                order.setApprovalStatus("待审批");
            }
        }

        AfterSalesOrder savedOrder = afterSalesOrderRepository.save(order);
        return AfterSalesOrderResponse.fromEntity(savedOrder);
    }

    /**
     * 删除售后服务单
     */
    @Transactional
    public void delete(UUID id) {
        AfterSalesOrder order = afterSalesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("售后服务单不存在"));
        afterSalesOrderRepository.delete(order);
    }

    /**
     * 指派受理人员
     */
    @Transactional
    public AfterSalesOrderResponse assignHandler(UUID id, AssignHandlerRequest request) {
        AfterSalesOrder order = afterSalesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("售后服务单不存在"));

        order.setHandlerId(request.getHandlerId());
        order.setHandlerName(request.getHandlerName());
        order.setServiceStatus("处理中");

        AfterSalesOrder savedOrder = afterSalesOrderRepository.save(order);
        return AfterSalesOrderResponse.fromEntity(savedOrder);
    }

    /**
     * 根据受理人员ID查询待办事宜（处理中的售后服务单）
     */
    @Transactional(readOnly = true)
    public List<AfterSalesOrderResponse> findPendingOrdersByHandler(UUID handlerId) {
        List<AfterSalesOrder> orders = afterSalesOrderRepository.findByHandlerId(handlerId);
        return orders.stream()
                .filter(order -> "处理中".equals(order.getServiceStatus()) || "待分配".equals(order.getServiceStatus()))
                .map(AfterSalesOrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 添加售后服务活动（跟踪记录）
     */
    @Transactional
    public AfterSalesActivity addActivity(AfterSalesActivityRequest request) {
        AfterSalesOrder order = afterSalesOrderRepository.findById(request.getAfterSalesOrderId())
                .orElseThrow(() -> new IllegalArgumentException("售后服务单不存在"));

        AfterSalesActivity activity = new AfterSalesActivity();
        activity.setAfterSalesOrder(order);
        activity.setActivityType(request.getActivityType());
        activity.setActivityDate(request.getActivityDate());
        activity.setDescription(request.getDescription());
        activity.setOperatorId(request.getOperatorId());
        activity.setOperatorName(request.getOperatorName());

        return afterSalesActivityRepository.save(activity);
    }

    /**
     * 查询售后服务单的活动列表
     */
    @Transactional(readOnly = true)
    public List<AfterSalesActivity> findActivitiesByOrderId(UUID orderId) {
        return afterSalesActivityRepository.findByAfterSalesOrderId(orderId);
    }

    /**
     * 完成售后服务
     */
    @Transactional
    public AfterSalesOrderResponse completeService(UUID id, CompleteServiceRequest request) {
        AfterSalesOrder order = afterSalesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("售后服务单不存在"));

        order.setDeviceNumber(request.getDeviceNumber());
        order.setDeviceName(request.getDeviceName());
        order.setServiceSummary(request.getServiceSummary());
        if (request.getCompletionDate() != null) {
            order.setCompletionDate(request.getCompletionDate());
        } else {
            order.setCompletionDate(new Date(System.currentTimeMillis()));
        }
        order.setServiceStatus("已完成");

        AfterSalesOrder savedOrder = afterSalesOrderRepository.save(order);
        return AfterSalesOrderResponse.fromEntity(savedOrder);
    }

    /**
     * 评价售后服务
     */
    @Transactional
    public AfterSalesOrderResponse evaluateService(UUID id, EvaluateServiceRequest request) {
        AfterSalesOrder order = afterSalesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("售后服务单不存在"));

        order.setEvaluation(request.getEvaluation());
        order.setEvaluatorId(request.getEvaluatorId());
        order.setEvaluatorName(request.getEvaluatorName());
        order.setEvaluationTime(LocalDateTime.now());
        if (request.getRemark() != null) {
            order.setRemark(request.getRemark());
        }
        order.setIsClosed(true);

        AfterSalesOrder savedOrder = afterSalesOrderRepository.save(order);
        return AfterSalesOrderResponse.fromEntity(savedOrder);
    }

    /**
     * 查询已完成的售后服务单（用于评价列表）
     */
    @Transactional(readOnly = true)
    public List<AfterSalesOrderResponse> findCompletedOrders() {
        List<AfterSalesOrder> orders = afterSalesOrderRepository.findAll();
        return orders.stream()
                .filter(order -> "已完成".equals(order.getServiceStatus()))
                .map(AfterSalesOrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 查询需要回访的售后服务单（已完成但未评价）
     */
    @Transactional(readOnly = true)
    public List<AfterSalesOrderResponse> findOrdersForEvaluation() {
        List<AfterSalesOrder> orders = afterSalesOrderRepository.findAll();
        return orders.stream()
                .filter(order -> "已完成".equals(order.getServiceStatus()) 
                        && (order.getEvaluation() == null || order.getEvaluation().trim().isEmpty()))
                .map(AfterSalesOrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 生成服务单号
     */
    private String generateServiceOrderNumber() {
        String prefix = "AS";
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return prefix + dateStr + timeStr;
    }

    /**
     * 将请求DTO映射到实体
     */
    private void mapRequestToEntity(AfterSalesOrderRequest request, AfterSalesOrder order) {
        if (request.getServiceOrderNumber() != null) {
            order.setServiceOrderNumber(request.getServiceOrderNumber());
        }
        order.setServiceType(request.getServiceType());
        if (request.getContractNumber() != null && order.getContractNumber() == null) {
            order.setContractNumber(request.getContractNumber());
        }
        if (request.getContractName() != null && order.getContractName() == null) {
            order.setContractName(request.getContractName());
        }
        order.setCustomerUnit(request.getCustomerUnit());
        order.setPaymentPlan(request.getPaymentPlan());
        order.setServiceDate(request.getServiceDate());
        order.setRegion(request.getRegion());
        order.setMaintenancePlanAttachment(request.getMaintenancePlanAttachment());
        order.setReplacementPartsListAttachment(request.getReplacementPartsListAttachment());
        order.setCustomerRequestAttachment(request.getCustomerRequestAttachment());
        order.setRemark(request.getRemark());
        if (request.getHandlerId() != null) {
            order.setHandlerId(request.getHandlerId());
        }
        if (request.getHandlerName() != null) {
            order.setHandlerName(request.getHandlerName());
        }
        if (request.getServiceStatus() != null) {
            order.setServiceStatus(request.getServiceStatus());
        }
        if (request.getCompletionDate() != null) {
            order.setCompletionDate(request.getCompletionDate());
        }
        if (request.getDeviceNumber() != null) {
            order.setDeviceNumber(request.getDeviceNumber());
        }
        if (request.getDeviceName() != null) {
            order.setDeviceName(request.getDeviceName());
        }
        if (request.getServiceSummary() != null) {
            order.setServiceSummary(request.getServiceSummary());
        }
    }
}

