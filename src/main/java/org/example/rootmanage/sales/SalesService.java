package org.example.rootmanage.sales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.account.UserAccountRepository;
import org.example.rootmanage.basicinfo.entity.Customer;
import org.example.rootmanage.basicinfo.entity.Product;
import org.example.rootmanage.basicinfo.CustomerRepository;
import org.example.rootmanage.basicinfo.ProductRepository;
import org.example.rootmanage.option.OptionItem;
import org.example.rootmanage.option.OptionItemRepository;
import org.example.rootmanage.sales.dto.*;
import org.example.rootmanage.sales.entity.*;
import org.example.rootmanage.basicinfo.entity.SalesArea;
import org.example.rootmanage.basicinfo.entity.MarketingPersonnel;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final CustomerVisitRepository customerVisitRepository;
    private final SalesOpportunityRepository salesOpportunityRepository;
    private final SalesOpportunityTrackingRepository salesOpportunityTrackingRepository;
    private final SalesOpportunityAssignmentRepository salesOpportunityAssignmentRepository;
    private final SalesInventoryRepository salesInventoryRepository;
    private final SalesQuotationRepository salesQuotationRepository;
    private final SalesInventoryReductionRepository salesInventoryReductionRepository;
    private final BiddingRepository biddingRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final org.example.rootmanage.basicinfo.SalesAreaRepository salesAreaRepository;
    private final org.example.rootmanage.basicinfo.MarketingPersonnelRepository marketingPersonnelRepository;
    private final UserAccountRepository userAccountRepository;
    private final OptionItemRepository optionItemRepository;

    // ========== 客户来访管理 ==========

    @Transactional(readOnly = true)
    public List<CustomerVisit> findAllCustomerVisits(UUID customerId, LocalDateTime startTime, LocalDateTime endTime) {
        List<CustomerVisit> visits;
        if (customerId != null) {
            visits = customerVisitRepository.findByCustomerIdOrderByVisitTimeDesc(customerId);
        } else if (startTime != null && endTime != null) {
            visits = customerVisitRepository.findByVisitTimeBetween(startTime, endTime);
        } else {
            visits = customerVisitRepository.findAll();
        }
        // 初始化懒加载的关联对象
        visits.forEach(visit -> {
            if (visit.getCustomer() != null) {
                Hibernate.initialize(visit.getCustomer());
            }
            if (visit.getReceptionist() != null) {
                Hibernate.initialize(visit.getReceptionist());
            }
        });
        return visits;
    }

    @Transactional
    public CustomerVisit createCustomerVisit(CustomerVisitRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        CustomerVisit visit = new CustomerVisit();
        visit.setCustomer(customer);
        visit.setVisitTime(request.getVisitTime());
        visit.setVisitPurpose(request.getVisitPurpose());
        visit.setVisitContent(request.getVisitContent());
        visit.setVisitResult(request.getVisitResult());
        visit.setRemark(request.getRemark());

        if (request.getReceptionistId() != null) {
            UserAccount receptionist = userAccountRepository.findById(request.getReceptionistId())
                    .orElseThrow(() -> new IllegalArgumentException("接待人不存在"));
            visit.setReceptionist(receptionist);
        }

        return customerVisitRepository.save(visit);
    }

    @Transactional
    public CustomerVisit updateCustomerVisit(UUID id, CustomerVisitRequest request) {
        CustomerVisit visit = customerVisitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("来访记录不存在"));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        visit.setCustomer(customer);
        visit.setVisitTime(request.getVisitTime());
        visit.setVisitPurpose(request.getVisitPurpose());
        visit.setVisitContent(request.getVisitContent());
        visit.setVisitResult(request.getVisitResult());
        visit.setRemark(request.getRemark());

        if (request.getReceptionistId() != null) {
            UserAccount receptionist = userAccountRepository.findById(request.getReceptionistId())
                    .orElseThrow(() -> new IllegalArgumentException("接待人不存在"));
            visit.setReceptionist(receptionist);
        } else {
            visit.setReceptionist(null);
        }

        CustomerVisit saved = customerVisitRepository.save(visit);
        if (saved.getCustomer() != null) {
            Hibernate.initialize(saved.getCustomer());
        }
        if (saved.getReceptionist() != null) {
            Hibernate.initialize(saved.getReceptionist());
        }
        return saved;
    }

    @Transactional
    public void deleteCustomerVisit(UUID id) {
        customerVisitRepository.deleteById(id);
    }

    // ========== 销售机会管理 ==========

    @Transactional(readOnly = true)
    public List<SalesOpportunity> findAllSalesOpportunities(String keyword) {
        List<SalesOpportunity> opportunities;
        if (keyword != null && !keyword.trim().isEmpty()) {
            opportunities = salesOpportunityRepository.searchOpportunities(keyword.trim());
        } else {
            opportunities = salesOpportunityRepository.findAll();
        }
        // 初始化懒加载的关联对象
        opportunities.forEach(opportunity -> {
            if (opportunity.getCustomer() != null) {
                Hibernate.initialize(opportunity.getCustomer());
            }
            if (opportunity.getProduct() != null) {
                Hibernate.initialize(opportunity.getProduct());
            }
            if (opportunity.getSalesperson() != null) {
                Hibernate.initialize(opportunity.getSalesperson());
            }
            if (opportunity.getOpportunityStage() != null) {
                Hibernate.initialize(opportunity.getOpportunityStage());
            }
            if (opportunity.getStatus() != null) {
                Hibernate.initialize(opportunity.getStatus());
            }
        });
        return opportunities;
    }

    @Transactional
    public SalesOpportunity createSalesOpportunity(SalesOpportunityRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        SalesOpportunity opportunity = new SalesOpportunity();
        opportunity.setCustomer(customer);
        opportunity.setOpportunityName(request.getOpportunityName());
        opportunity.setOpportunitySubject(request.getOpportunitySubject());
        opportunity.setOpportunityDate(request.getOpportunityDate() != null ? request.getOpportunityDate() : java.time.LocalDate.now());
        opportunity.setEstimatedAmount(request.getEstimatedAmount());
        opportunity.setBudget(request.getBudget());
        opportunity.setExpectedCloseDate(request.getExpectedCloseDate());
        opportunity.setSuccessProbability(request.getSuccessProbability());
        opportunity.setInventory(request.getInventory());
        opportunity.setReceived(request.getReceived() != null ? request.getReceived() : false);
        opportunity.setSubmitted(request.getSubmitted() != null ? request.getSubmitted() : false);
        opportunity.setDescription(request.getDescription());
        opportunity.setRemark(request.getRemark());

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("产品不存在"));
            opportunity.setProduct(product);
        }

        if (request.getSalespersonId() != null) {
            UserAccount salesperson = userAccountRepository.findById(request.getSalespersonId())
                    .orElseThrow(() -> new IllegalArgumentException("销售负责人不存在"));
            opportunity.setSalesperson(salesperson);
        }

        if (request.getOpportunityStageId() != null) {
            OptionItem stage = optionItemRepository.findById(request.getOpportunityStageId())
                    .orElseThrow(() -> new IllegalArgumentException("机会阶段选项不存在"));
            opportunity.setOpportunityStage(stage);
        }

        if (request.getLeadSourceId() != null) {
            OptionItem leadSource = optionItemRepository.findById(request.getLeadSourceId())
                    .orElseThrow(() -> new IllegalArgumentException("线索来源选项不存在"));
            opportunity.setLeadSource(leadSource);
        }

        if (request.getStatusId() != null) {
            OptionItem status = optionItemRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("状态选项不存在"));
            opportunity.setStatus(status);
        }

        return salesOpportunityRepository.save(opportunity);
    }

    @Transactional
    public SalesOpportunity updateSalesOpportunity(UUID id, SalesOpportunityRequest request) {
        SalesOpportunity opportunity = salesOpportunityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售机会不存在"));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        opportunity.setCustomer(customer);
        opportunity.setOpportunityName(request.getOpportunityName());
        opportunity.setOpportunitySubject(request.getOpportunitySubject());
        if (request.getOpportunityDate() != null) {
            opportunity.setOpportunityDate(request.getOpportunityDate());
        }
        opportunity.setEstimatedAmount(request.getEstimatedAmount());
        opportunity.setBudget(request.getBudget());
        opportunity.setExpectedCloseDate(request.getExpectedCloseDate());
        opportunity.setSuccessProbability(request.getSuccessProbability());
        opportunity.setInventory(request.getInventory());
        if (request.getReceived() != null) {
            opportunity.setReceived(request.getReceived());
        }
        if (request.getSubmitted() != null) {
            opportunity.setSubmitted(request.getSubmitted());
        }
        opportunity.setDescription(request.getDescription());
        opportunity.setRemark(request.getRemark());

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("产品不存在"));
            opportunity.setProduct(product);
        } else {
            opportunity.setProduct(null);
        }

        if (request.getSalespersonId() != null) {
            UserAccount salesperson = userAccountRepository.findById(request.getSalespersonId())
                    .orElseThrow(() -> new IllegalArgumentException("销售负责人不存在"));
            opportunity.setSalesperson(salesperson);
        } else {
            opportunity.setSalesperson(null);
        }

        if (request.getOpportunityStageId() != null) {
            OptionItem stage = optionItemRepository.findById(request.getOpportunityStageId())
                    .orElseThrow(() -> new IllegalArgumentException("机会阶段选项不存在"));
            opportunity.setOpportunityStage(stage);
        } else {
            opportunity.setOpportunityStage(null);
        }

        if (request.getLeadSourceId() != null) {
            OptionItem leadSource = optionItemRepository.findById(request.getLeadSourceId())
                    .orElseThrow(() -> new IllegalArgumentException("线索来源选项不存在"));
            opportunity.setLeadSource(leadSource);
        } else {
            opportunity.setLeadSource(null);
        }

        if (request.getStatusId() != null) {
            OptionItem status = optionItemRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("状态选项不存在"));
            opportunity.setStatus(status);
        } else {
            opportunity.setStatus(null);
        }

        SalesOpportunity saved = salesOpportunityRepository.save(opportunity);
        if (saved.getCustomer() != null) {
            Hibernate.initialize(saved.getCustomer());
        }
        if (saved.getProduct() != null) {
            Hibernate.initialize(saved.getProduct());
        }
        if (saved.getSalesperson() != null) {
            Hibernate.initialize(saved.getSalesperson());
        }
        if (saved.getOpportunityStage() != null) {
            Hibernate.initialize(saved.getOpportunityStage());
        }
        if (saved.getStatus() != null) {
            Hibernate.initialize(saved.getStatus());
        }
        return saved;
    }

    @Transactional
    public void deleteSalesOpportunity(UUID id) {
        // 删除关联的分配记录和跟踪记录
        salesOpportunityAssignmentRepository.deleteByOpportunityId(id);
        salesOpportunityTrackingRepository.findByOpportunityIdOrderByTrackingTimeDesc(id)
                .forEach(tracking -> salesOpportunityTrackingRepository.delete(tracking));
        salesOpportunityRepository.deleteById(id);
    }

    // ========== 销售机会提交 ==========

    @Transactional
    public SalesOpportunity submitSalesOpportunity(UUID id) {
        SalesOpportunity opportunity = salesOpportunityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售机会不存在"));
        opportunity.setSubmitted(true);
        SalesOpportunity saved = salesOpportunityRepository.save(opportunity);
        Hibernate.initialize(saved.getCustomer());
        return saved;
    }

    // ========== 销售机会传递 ==========

    @Transactional
    public void transferSalesOpportunity(OpportunityTransferRequest request) {
        SalesOpportunity opportunity = salesOpportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new IllegalArgumentException("销售机会不存在"));

        // 为每个目标片区创建分配记录
        for (UUID areaId : request.getTargetAreaIds()) {
            SalesArea area = salesAreaRepository.findById(areaId)
                    .orElseThrow(() -> new IllegalArgumentException("片区不存在: " + areaId));

            // 检查是否已存在分配记录
            boolean exists = salesOpportunityAssignmentRepository.findByOpportunityId(opportunity.getId())
                    .stream()
                    .anyMatch(a -> a.getArea().getId().equals(areaId));

            if (!exists) {
                SalesOpportunityAssignment assignment = new SalesOpportunityAssignment();
                assignment.setOpportunity(opportunity);
                assignment.setArea(area);
                assignment.setIsPrimary(false);
                salesOpportunityAssignmentRepository.save(assignment);
            }
        }
    }

    // ========== 销售机会分配员工 ==========

    @Transactional
    public void assignSalesOpportunity(OpportunityAssignRequest request) {
        SalesOpportunity opportunity = salesOpportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new IllegalArgumentException("销售机会不存在"));

        // 获取机会已分配的片区（如果没有，使用负责人的片区）
        List<SalesOpportunityAssignment> existingAssignments = salesOpportunityAssignmentRepository
                .findByOpportunityId(opportunity.getId());

        // 为每个营销人员创建分配记录
        for (UUID personnelId : request.getPersonnelIds()) {
            MarketingPersonnel personnel = marketingPersonnelRepository.findById(personnelId)
                    .orElseThrow(() -> new IllegalArgumentException("营销人员不存在: " + personnelId));

            if (personnel.getResponsibleArea() == null) {
                throw new IllegalArgumentException("营销人员未分配负责区域: " + personnelId);
            }

            SalesArea area = personnel.getResponsibleArea();

            // 检查是否已存在分配记录
            boolean exists = existingAssignments.stream()
                    .anyMatch(a -> a.getArea().getId().equals(area.getId()) && 
                                 a.getPersonnel() != null && 
                                 a.getPersonnel().getId().equals(personnelId));

            if (!exists) {
                SalesOpportunityAssignment assignment = new SalesOpportunityAssignment();
                assignment.setOpportunity(opportunity);
                assignment.setArea(area);
                assignment.setPersonnel(personnel);
                assignment.setIsPrimary(false);
                salesOpportunityAssignmentRepository.save(assignment);
            }
        }
    }

    // ========== 销售机会关闭 ==========

    @Transactional
    public SalesOpportunity closeSalesOpportunity(OpportunityCloseRequest request) {
        SalesOpportunity opportunity = salesOpportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new IllegalArgumentException("销售机会不存在"));

        opportunity.setCloseReason(request.getCloseReason());
        // 设置状态为已关闭（需要从选项项中获取）
        // 这里假设有一个"已关闭"状态的选项项，实际应该通过选项项ID设置

        SalesOpportunity saved = salesOpportunityRepository.save(opportunity);
        Hibernate.initialize(saved.getCustomer());
        return saved;
    }

    // ========== 销售机会跟踪 ==========

    @Transactional(readOnly = true)
    public List<SalesOpportunityTracking> findTrackingByOpportunityId(UUID opportunityId) {
        List<SalesOpportunityTracking> trackings = salesOpportunityTrackingRepository
                .findByOpportunityIdOrderByTrackingTimeDesc(opportunityId);
        trackings.forEach(tracking -> {
            if (tracking.getOpportunity() != null) {
                Hibernate.initialize(tracking.getOpportunity());
            }
            if (tracking.getStatus() != null) {
                Hibernate.initialize(tracking.getStatus());
            }
            if (tracking.getTracker() != null) {
                Hibernate.initialize(tracking.getTracker());
            }
        });
        return trackings;
    }

    @Transactional
    public SalesOpportunityTracking createTracking(SalesOpportunityTrackingRequest request) {
        SalesOpportunity opportunity = salesOpportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new IllegalArgumentException("销售机会不存在"));

        SalesOpportunityTracking tracking = new SalesOpportunityTracking();
        tracking.setOpportunity(opportunity);
        tracking.setTrackingTime(request.getTrackingTime());
        tracking.setMatter(request.getMatter());
        tracking.setDescription(request.getDescription());
        tracking.setAttachments(request.getAttachments());
        tracking.setRemark(request.getRemark());

        if (request.getStatusId() != null) {
            OptionItem status = optionItemRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("跟踪状态选项不存在"));
            tracking.setStatus(status);
        }

        if (request.getTrackerId() != null) {
            UserAccount tracker = userAccountRepository.findById(request.getTrackerId())
                    .orElseThrow(() -> new IllegalArgumentException("跟踪人不存在"));
            tracking.setTracker(tracker);
        }

        SalesOpportunityTracking saved = salesOpportunityTrackingRepository.save(tracking);
        if (saved.getOpportunity() != null) {
            Hibernate.initialize(saved.getOpportunity());
        }
        return saved;
    }

    @Transactional
    public SalesOpportunityTracking updateTracking(UUID id, SalesOpportunityTrackingRequest request) {
        SalesOpportunityTracking tracking = salesOpportunityTrackingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("跟踪记录不存在"));

        tracking.setTrackingTime(request.getTrackingTime());
        tracking.setMatter(request.getMatter());
        tracking.setDescription(request.getDescription());
        tracking.setAttachments(request.getAttachments());
        tracking.setRemark(request.getRemark());

        if (request.getStatusId() != null) {
            OptionItem status = optionItemRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("跟踪状态选项不存在"));
            tracking.setStatus(status);
        } else {
            tracking.setStatus(null);
        }

        if (request.getTrackerId() != null) {
            UserAccount tracker = userAccountRepository.findById(request.getTrackerId())
                    .orElseThrow(() -> new IllegalArgumentException("跟踪人不存在"));
            tracking.setTracker(tracker);
        } else {
            tracking.setTracker(null);
        }

        SalesOpportunityTracking saved = salesOpportunityTrackingRepository.save(tracking);
        if (saved.getOpportunity() != null) {
            Hibernate.initialize(saved.getOpportunity());
        }
        return saved;
    }

    @Transactional
    public void deleteTracking(UUID id) {
        salesOpportunityTrackingRepository.deleteById(id);
    }

    // ========== 查询待分配的机会 ==========

    @Transactional(readOnly = true)
    public List<SalesOpportunity> findUnassignedOpportunities() {
        List<SalesOpportunity> opportunities = salesOpportunityAssignmentRepository.findUnassignedOpportunities();
        opportunities.forEach(opportunity -> {
            if (opportunity.getCustomer() != null) {
                Hibernate.initialize(opportunity.getCustomer());
            }
            if (opportunity.getStatus() != null) {
                Hibernate.initialize(opportunity.getStatus());
            }
        });
        return opportunities;
    }

    // ========== 销售库存查询 ==========

    @Transactional(readOnly = true)
    public List<SalesInventory> findAllSalesInventories(String keyword) {
        List<SalesInventory> inventories;
        if (keyword != null && !keyword.trim().isEmpty()) {
            inventories = salesInventoryRepository.searchInventories(keyword.trim());
        } else {
            inventories = salesInventoryRepository.findAll();
        }
        // 初始化懒加载的关联对象
        inventories.forEach(inventory -> {
            if (inventory.getProduct() != null) {
                Hibernate.initialize(inventory.getProduct());
            }
            if (inventory.getWarehouse() != null) {
                Hibernate.initialize(inventory.getWarehouse());
            }
        });
        return inventories;
    }

    @Transactional
    public SalesInventory createSalesInventory(SalesInventoryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("产品不存在"));

        SalesInventory inventory = new SalesInventory();
        inventory.setProduct(product);
        inventory.setQuantity(request.getQuantity());
        inventory.setAvailableQuantity(request.getAvailableQuantity());
        inventory.setReservedQuantity(request.getReservedQuantity());
        inventory.setUnitPrice(request.getUnitPrice());
        inventory.setTotalValue(request.getTotalValue());
        inventory.setLocation(request.getLocation());
        inventory.setDrawingNo(request.getDrawingNo());
        inventory.setMaterial(request.getMaterial());
        inventory.setIsStagnant(request.getIsStagnant() != null ? request.getIsStagnant() : false);
        inventory.setRemark(request.getRemark());

        if (request.getWarehouseId() != null) {
            OptionItem warehouse = optionItemRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("仓库选项不存在"));
            inventory.setWarehouse(warehouse);
        }

        return salesInventoryRepository.save(inventory);
    }

    @Transactional
    public SalesInventory updateSalesInventory(UUID id, SalesInventoryRequest request) {
        SalesInventory inventory = salesInventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("库存记录不存在"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("产品不存在"));

        inventory.setProduct(product);
        inventory.setQuantity(request.getQuantity());
        inventory.setAvailableQuantity(request.getAvailableQuantity());
        inventory.setReservedQuantity(request.getReservedQuantity());
        inventory.setUnitPrice(request.getUnitPrice());
        inventory.setTotalValue(request.getTotalValue());
        inventory.setLocation(request.getLocation());
        inventory.setDrawingNo(request.getDrawingNo());
        inventory.setMaterial(request.getMaterial());
        if (request.getIsStagnant() != null) {
            inventory.setIsStagnant(request.getIsStagnant());
        }
        inventory.setRemark(request.getRemark());

        if (request.getWarehouseId() != null) {
            OptionItem warehouse = optionItemRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("仓库选项不存在"));
            inventory.setWarehouse(warehouse);
        } else {
            inventory.setWarehouse(null);
        }

        SalesInventory saved = salesInventoryRepository.save(inventory);
        if (saved.getProduct() != null) {
            Hibernate.initialize(saved.getProduct());
        }
        if (saved.getWarehouse() != null) {
            Hibernate.initialize(saved.getWarehouse());
        }
        return saved;
    }

    @Transactional
    public void deleteSalesInventory(UUID id) {
        salesInventoryRepository.deleteById(id);
    }

    // ========== 销售报价管理 ==========

    @Transactional(readOnly = true)
    public List<SalesQuotation> findAllSalesQuotations(String keyword) {
        List<SalesQuotation> quotations;
        if (keyword != null && !keyword.trim().isEmpty()) {
            quotations = salesQuotationRepository.searchQuotations(keyword.trim());
        } else {
            quotations = salesQuotationRepository.findAll();
        }
        // 初始化懒加载的关联对象
        quotations.forEach(quotation -> {
            if (quotation.getCustomer() != null) {
                Hibernate.initialize(quotation.getCustomer());
            }
            if (quotation.getSalesperson() != null) {
                Hibernate.initialize(quotation.getSalesperson());
            }
            if (quotation.getStatus() != null) {
                Hibernate.initialize(quotation.getStatus());
            }
        });
        return quotations;
    }

    @Transactional
    public SalesQuotation createSalesQuotation(SalesQuotationRequest request) {
        // 检查报价单号是否已存在
        if (salesQuotationRepository.findByQuotationNo(request.getQuotationNo()).isPresent()) {
            throw new IllegalStateException("报价单号已存在");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        SalesQuotation quotation = new SalesQuotation();
        quotation.setQuotationNo(request.getQuotationNo());
        quotation.setCustomer(customer);
        quotation.setQuotationDate(request.getQuotationDate());
        quotation.setValidUntil(request.getValidUntil());
        quotation.setTotalAmount(request.getTotalAmount());
        quotation.setProjectName(request.getProjectName());
        quotation.setQuotationContent(request.getQuotationContent());
        quotation.setTerms(request.getTerms());
        quotation.setRemark(request.getRemark());

        if (request.getSalespersonId() != null) {
            UserAccount salesperson = userAccountRepository.findById(request.getSalespersonId())
                    .orElseThrow(() -> new IllegalArgumentException("销售负责人不存在"));
            quotation.setSalesperson(salesperson);
        }

        if (request.getStatusId() != null) {
            OptionItem status = optionItemRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("状态选项不存在"));
            quotation.setStatus(status);
        }

        return salesQuotationRepository.save(quotation);
    }

    @Transactional
    public SalesQuotation updateSalesQuotation(UUID id, SalesQuotationRequest request) {
        SalesQuotation quotation = salesQuotationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("报价单不存在"));

        // 检查报价单号是否被其他报价单使用
        salesQuotationRepository.findByQuotationNo(request.getQuotationNo()).ifPresent(q -> {
            if (!q.getId().equals(id)) {
                throw new IllegalStateException("报价单号已被其他报价单使用");
            }
        });

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        quotation.setQuotationNo(request.getQuotationNo());
        quotation.setCustomer(customer);
        quotation.setQuotationDate(request.getQuotationDate());
        quotation.setValidUntil(request.getValidUntil());
        quotation.setTotalAmount(request.getTotalAmount());
        quotation.setProjectName(request.getProjectName());
        quotation.setQuotationContent(request.getQuotationContent());
        quotation.setTerms(request.getTerms());
        quotation.setRemark(request.getRemark());

        if (request.getSalespersonId() != null) {
            UserAccount salesperson = userAccountRepository.findById(request.getSalespersonId())
                    .orElseThrow(() -> new IllegalArgumentException("销售负责人不存在"));
            quotation.setSalesperson(salesperson);
        } else {
            quotation.setSalesperson(null);
        }

        if (request.getStatusId() != null) {
            OptionItem status = optionItemRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("状态选项不存在"));
            quotation.setStatus(status);
        } else {
            quotation.setStatus(null);
        }

        SalesQuotation saved = salesQuotationRepository.save(quotation);
        if (saved.getCustomer() != null) {
            Hibernate.initialize(saved.getCustomer());
        }
        if (saved.getSalesperson() != null) {
            Hibernate.initialize(saved.getSalesperson());
        }
        if (saved.getStatus() != null) {
            Hibernate.initialize(saved.getStatus());
        }
        return saved;
    }

    @Transactional
    public void deleteSalesQuotation(UUID id) {
        salesQuotationRepository.deleteById(id);
    }

    // ========== 销售降库管理 ==========

    @Transactional(readOnly = true)
    public List<SalesInventoryReduction> findAllSalesInventoryReductions(String keyword) {
        List<SalesInventoryReduction> reductions;
        if (keyword != null && !keyword.trim().isEmpty()) {
            reductions = salesInventoryReductionRepository.searchReductions(keyword.trim());
        } else {
            reductions = salesInventoryReductionRepository.findAll();
        }
        // 初始化懒加载的关联对象
        reductions.forEach(reduction -> {
            if (reduction.getProduct() != null) {
                Hibernate.initialize(reduction.getProduct());
            }
            if (reduction.getWarehouse() != null) {
                Hibernate.initialize(reduction.getWarehouse());
            }
            if (reduction.getReductionType() != null) {
                Hibernate.initialize(reduction.getReductionType());
            }
            if (reduction.getOperator() != null) {
                Hibernate.initialize(reduction.getOperator());
            }
        });
        return reductions;
    }

    @Transactional
    public SalesInventoryReduction createSalesInventoryReduction(SalesInventoryReductionRequest request) {
        // 检查降库单号是否已存在
        if (salesInventoryReductionRepository.findByReductionNo(request.getReductionNo()).isPresent()) {
            throw new IllegalStateException("降库单号已存在");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("产品不存在"));

        SalesInventoryReduction reduction = new SalesInventoryReduction();
        reduction.setReductionNo(request.getReductionNo());
        reduction.setProduct(product);
        reduction.setQuantity(request.getQuantity());
        reduction.setReductionDate(request.getReductionDate());
        reduction.setUnitPrice(request.getUnitPrice());
        reduction.setTotalAmount(request.getTotalAmount());
        reduction.setReason(request.getReason());
        reduction.setRemark(request.getRemark());

        if (request.getWarehouseId() != null) {
            OptionItem warehouse = optionItemRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("仓库选项不存在"));
            reduction.setWarehouse(warehouse);
        }

        if (request.getReductionTypeId() != null) {
            OptionItem reductionType = optionItemRepository.findById(request.getReductionTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("降库类型选项不存在"));
            reduction.setReductionType(reductionType);
        }

        if (request.getOperatorId() != null) {
            UserAccount operator = userAccountRepository.findById(request.getOperatorId())
                    .orElseThrow(() -> new IllegalArgumentException("操作人不存在"));
            reduction.setOperator(operator);
        }

        return salesInventoryReductionRepository.save(reduction);
    }

    @Transactional
    public SalesInventoryReduction updateSalesInventoryReduction(UUID id, SalesInventoryReductionRequest request) {
        SalesInventoryReduction reduction = salesInventoryReductionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("降库记录不存在"));

        // 检查降库单号是否被其他降库记录使用
        salesInventoryReductionRepository.findByReductionNo(request.getReductionNo()).ifPresent(r -> {
            if (!r.getId().equals(id)) {
                throw new IllegalStateException("降库单号已被其他降库记录使用");
            }
        });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("产品不存在"));

        reduction.setReductionNo(request.getReductionNo());
        reduction.setProduct(product);
        reduction.setQuantity(request.getQuantity());
        reduction.setReductionDate(request.getReductionDate());
        reduction.setUnitPrice(request.getUnitPrice());
        reduction.setTotalAmount(request.getTotalAmount());
        reduction.setReason(request.getReason());
        reduction.setRemark(request.getRemark());

        if (request.getWarehouseId() != null) {
            OptionItem warehouse = optionItemRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("仓库选项不存在"));
            reduction.setWarehouse(warehouse);
        } else {
            reduction.setWarehouse(null);
        }

        if (request.getReductionTypeId() != null) {
            OptionItem reductionType = optionItemRepository.findById(request.getReductionTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("降库类型选项不存在"));
            reduction.setReductionType(reductionType);
        } else {
            reduction.setReductionType(null);
        }

        if (request.getOperatorId() != null) {
            UserAccount operator = userAccountRepository.findById(request.getOperatorId())
                    .orElseThrow(() -> new IllegalArgumentException("操作人不存在"));
            reduction.setOperator(operator);
        } else {
            reduction.setOperator(null);
        }

        SalesInventoryReduction saved = salesInventoryReductionRepository.save(reduction);
        if (saved.getProduct() != null) {
            Hibernate.initialize(saved.getProduct());
        }
        if (saved.getWarehouse() != null) {
            Hibernate.initialize(saved.getWarehouse());
        }
        if (saved.getReductionType() != null) {
            Hibernate.initialize(saved.getReductionType());
        }
        if (saved.getOperator() != null) {
            Hibernate.initialize(saved.getOperator());
        }
        return saved;
    }

    @Transactional
    public void deleteSalesInventoryReduction(UUID id) {
        salesInventoryReductionRepository.deleteById(id);
    }

    // ========== 投标管理 ==========

    @Transactional(readOnly = true)
    public List<Bidding> findAllBiddings(String keyword) {
        List<Bidding> biddings;
        if (keyword != null && !keyword.trim().isEmpty()) {
            biddings = biddingRepository.searchBiddings(keyword.trim());
        } else {
            biddings = biddingRepository.findAll();
        }
        // 初始化懒加载的关联对象
        biddings.forEach(bidding -> {
            if (bidding.getCustomer() != null) {
                Hibernate.initialize(bidding.getCustomer());
            }
            if (bidding.getResponsiblePerson() != null) {
                Hibernate.initialize(bidding.getResponsiblePerson());
            }
            if (bidding.getBiddingStatus() != null) {
                Hibernate.initialize(bidding.getBiddingStatus());
            }
        });
        return biddings;
    }

    @Transactional
    public Bidding createBidding(BiddingRequest request) {
        // 检查投标编号是否已存在
        if (biddingRepository.findByBiddingNo(request.getBiddingNo()).isPresent()) {
            throw new IllegalStateException("投标编号已存在");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        Bidding bidding = new Bidding();
        bidding.setBiddingNo(request.getBiddingNo());
        bidding.setBiddingName(request.getBiddingName());
        bidding.setCustomer(customer);
        bidding.setProjectName(request.getProjectName());
        bidding.setBiddingDate(request.getBiddingDate());
        bidding.setDeadline(request.getDeadline());
        bidding.setBiddingAmount(request.getBiddingAmount());
        bidding.setEstimatedProfit(request.getEstimatedProfit());
        bidding.setTechnicalSolution(request.getTechnicalSolution());
        bidding.setQuotationSheet(request.getQuotationSheet());
        bidding.setBiddingSummary(request.getBiddingSummary());
        bidding.setAttachments(request.getAttachments());
        bidding.setOpportunityName(request.getOpportunityName());
        bidding.setOpportunityNo(request.getOpportunityNo());
        bidding.setProjectDescription(request.getProjectDescription());
        bidding.setBiddingContent(request.getBiddingContent());
        bidding.setCompetitorInfo(request.getCompetitorInfo());
        bidding.setRemark(request.getRemark());

        if (request.getOpportunityId() != null) {
            org.example.rootmanage.sales.entity.SalesOpportunity opportunity = salesOpportunityRepository.findById(request.getOpportunityId())
                    .orElseThrow(() -> new IllegalArgumentException("销售机会不存在"));
            bidding.setOpportunity(opportunity);
        }

        if (request.getBiddingTypeId() != null) {
            OptionItem biddingType = optionItemRepository.findById(request.getBiddingTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("投标类型选项不存在"));
            bidding.setBiddingType(biddingType);
        }

        if (request.getResponsiblePersonId() != null) {
            UserAccount responsiblePerson = userAccountRepository.findById(request.getResponsiblePersonId())
                    .orElseThrow(() -> new IllegalArgumentException("负责人不存在"));
            bidding.setResponsiblePerson(responsiblePerson);
        }

        if (request.getBiddingStatusId() != null) {
            OptionItem biddingStatus = optionItemRepository.findById(request.getBiddingStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("投标状态选项不存在"));
            bidding.setBiddingStatus(biddingStatus);
        }

        if (request.getSummaryStatusId() != null) {
            OptionItem summaryStatus = optionItemRepository.findById(request.getSummaryStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("投标总结状态选项不存在"));
            bidding.setSummaryStatus(summaryStatus);
        }

        return biddingRepository.save(bidding);
    }

    @Transactional
    public Bidding updateBidding(UUID id, BiddingRequest request) {
        Bidding bidding = biddingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("投标记录不存在"));

        // 检查投标编号是否被其他投标记录使用
        biddingRepository.findByBiddingNo(request.getBiddingNo()).ifPresent(b -> {
            if (!b.getId().equals(id)) {
                throw new IllegalStateException("投标编号已被其他投标记录使用");
            }
        });

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        bidding.setBiddingNo(request.getBiddingNo());
        bidding.setBiddingName(request.getBiddingName());
        bidding.setCustomer(customer);
        bidding.setProjectName(request.getProjectName());
        bidding.setBiddingDate(request.getBiddingDate());
        bidding.setDeadline(request.getDeadline());
        bidding.setBiddingAmount(request.getBiddingAmount());
        bidding.setEstimatedProfit(request.getEstimatedProfit());
        bidding.setTechnicalSolution(request.getTechnicalSolution());
        bidding.setQuotationSheet(request.getQuotationSheet());
        bidding.setBiddingSummary(request.getBiddingSummary());
        bidding.setAttachments(request.getAttachments());
        bidding.setOpportunityName(request.getOpportunityName());
        bidding.setOpportunityNo(request.getOpportunityNo());
        bidding.setProjectDescription(request.getProjectDescription());
        bidding.setBiddingContent(request.getBiddingContent());
        bidding.setCompetitorInfo(request.getCompetitorInfo());
        bidding.setRemark(request.getRemark());

        if (request.getOpportunityId() != null) {
            org.example.rootmanage.sales.entity.SalesOpportunity opportunity = salesOpportunityRepository.findById(request.getOpportunityId())
                    .orElseThrow(() -> new IllegalArgumentException("销售机会不存在"));
            bidding.setOpportunity(opportunity);
        } else {
            bidding.setOpportunity(null);
        }

        if (request.getBiddingTypeId() != null) {
            OptionItem biddingType = optionItemRepository.findById(request.getBiddingTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("投标类型选项不存在"));
            bidding.setBiddingType(biddingType);
        } else {
            bidding.setBiddingType(null);
        }

        if (request.getResponsiblePersonId() != null) {
            UserAccount responsiblePerson = userAccountRepository.findById(request.getResponsiblePersonId())
                    .orElseThrow(() -> new IllegalArgumentException("负责人不存在"));
            bidding.setResponsiblePerson(responsiblePerson);
        } else {
            bidding.setResponsiblePerson(null);
        }

        if (request.getBiddingStatusId() != null) {
            OptionItem biddingStatus = optionItemRepository.findById(request.getBiddingStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("投标状态选项不存在"));
            bidding.setBiddingStatus(biddingStatus);
        } else {
            bidding.setBiddingStatus(null);
        }

        if (request.getSummaryStatusId() != null) {
            OptionItem summaryStatus = optionItemRepository.findById(request.getSummaryStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("投标总结状态选项不存在"));
            bidding.setSummaryStatus(summaryStatus);
        } else {
            bidding.setSummaryStatus(null);
        }

        Bidding saved = biddingRepository.save(bidding);
        if (saved.getCustomer() != null) {
            Hibernate.initialize(saved.getCustomer());
        }
        if (saved.getResponsiblePerson() != null) {
            Hibernate.initialize(saved.getResponsiblePerson());
        }
        if (saved.getBiddingStatus() != null) {
            Hibernate.initialize(saved.getBiddingStatus());
        }
        return saved;
    }

    @Transactional
    public void deleteBidding(UUID id) {
        biddingRepository.deleteById(id);
    }
}

