package org.example.rootmanage.basicinfo;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.account.UserAccountRepository;
import org.example.rootmanage.basicinfo.dto.CustomerKeyPersonRequest;
import org.example.rootmanage.basicinfo.dto.CustomerRequest;
import org.example.rootmanage.basicinfo.dto.MarketingPersonnelRequest;
import org.example.rootmanage.basicinfo.dto.PersonnelTransferRequest;
import org.example.rootmanage.basicinfo.dto.ProductRequest;
import org.example.rootmanage.basicinfo.dto.SalesAreaRequest;
import org.example.rootmanage.basicinfo.dto.TeamRequest;
import org.example.rootmanage.basicinfo.entity.Customer;
import org.example.rootmanage.basicinfo.entity.CustomerKeyPerson;
import org.example.rootmanage.basicinfo.entity.MarketingPersonnel;
import org.example.rootmanage.basicinfo.entity.Product;
import org.example.rootmanage.basicinfo.entity.SalesArea;
import org.example.rootmanage.basicinfo.entity.Team;
import org.example.rootmanage.option.OptionItem;
import org.example.rootmanage.option.OptionItemRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicInfoService {

    private final CustomerRepository customerRepository;
    private final CustomerKeyPersonRepository customerKeyPersonRepository;
    private final TeamRepository teamRepository;
    private final ProductRepository productRepository;
    private final SalesAreaRepository salesAreaRepository;
    private final MarketingPersonnelRepository marketingPersonnelRepository;
    private final OptionItemRepository optionItemRepository;
    private final UserAccountRepository userAccountRepository;

    // ========== 客户管理 ==========

    @Transactional(readOnly = true)
    public List<Customer> findAllCustomers(String keyword) {
        List<Customer> customers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            customers = customerRepository.searchCustomers(keyword.trim());
        } else {
            customers = customerRepository.findAll();
        }
        // 初始化懒加载的关联对象
        customers.forEach(customer -> {
            if (customer.getCustomerType() != null) {
                Hibernate.initialize(customer.getCustomerType());
            }
            if (customer.getIndustry() != null) {
                Hibernate.initialize(customer.getIndustry());
            }
        });
        return customers;
    }

    @Transactional
    public Customer createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setCustomerName(request.getCustomerName());
        customer.setContactPerson(request.getContactPerson());
        customer.setContactPhone(request.getContactPhone());
        customer.setContactEmail(request.getContactEmail());
        customer.setAddress(request.getAddress());
        customer.setCreditCode(request.getCreditCode());
        customer.setLegalRepresentative(request.getLegalRepresentative());
        customer.setRemark(request.getRemark());
        customer.setActive(request.getActive() != null ? request.getActive() : true);

        if (request.getCustomerTypeId() != null) {
            OptionItem customerType = optionItemRepository.findById(request.getCustomerTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("客户类型选项不存在"));
            customer.setCustomerType(customerType);
        }

        if (request.getIndustryId() != null) {
            OptionItem industry = optionItemRepository.findById(request.getIndustryId())
                    .orElseThrow(() -> new IllegalArgumentException("所属行业选项不存在"));
            customer.setIndustry(industry);
        }

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        customer.setCustomerName(request.getCustomerName());
        customer.setContactPerson(request.getContactPerson());
        customer.setContactPhone(request.getContactPhone());
        customer.setContactEmail(request.getContactEmail());
        customer.setAddress(request.getAddress());
        customer.setCreditCode(request.getCreditCode());
        customer.setLegalRepresentative(request.getLegalRepresentative());
        customer.setRemark(request.getRemark());
        if (request.getActive() != null) {
            customer.setActive(request.getActive());
        }

        if (request.getCustomerTypeId() != null) {
            OptionItem customerType = optionItemRepository.findById(request.getCustomerTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("客户类型选项不存在"));
            customer.setCustomerType(customerType);
        } else {
            customer.setCustomerType(null);
        }

        if (request.getIndustryId() != null) {
            OptionItem industry = optionItemRepository.findById(request.getIndustryId())
                    .orElseThrow(() -> new IllegalArgumentException("所属行业选项不存在"));
            customer.setIndustry(industry);
        } else {
            customer.setIndustry(null);
        }

        Customer saved = customerRepository.save(customer);
        // 初始化懒加载的关联对象
        if (saved.getCustomerType() != null) {
            Hibernate.initialize(saved.getCustomerType());
        }
        if (saved.getIndustry() != null) {
            Hibernate.initialize(saved.getIndustry());
        }
        return saved;
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        customer.setActive(false);
        customerRepository.save(customer);
    }

    // ========== 客户关键人物管理 ==========

    @Transactional(readOnly = true)
    public List<CustomerKeyPerson> findAllKeyPersonsByCustomerId(UUID customerId) {
        List<CustomerKeyPerson> keyPersons = customerKeyPersonRepository.findByCustomerId(customerId);
        // 初始化懒加载的关联对象
        keyPersons.forEach(keyPerson -> {
            if (keyPerson.getCustomer() != null) {
                Hibernate.initialize(keyPerson.getCustomer());
            }
        });
        return keyPersons;
    }

    @Transactional
    public CustomerKeyPerson createKeyPerson(CustomerKeyPersonRequest request) {
        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        CustomerKeyPerson keyPerson = new CustomerKeyPerson();
        keyPerson.setCustomer(customer);
        keyPerson.setName(request.getName());
        keyPerson.setCode(request.getCode());
        keyPerson.setGender(request.getGender());
        keyPerson.setSalutation(request.getSalutation());
        keyPerson.setDirectSuperior(request.getDirectSuperior());
        keyPerson.setPlaceOfOrigin(request.getPlaceOfOrigin());
        keyPerson.setBirthday(request.getBirthday());
        keyPerson.setMaritalStatus(request.getMaritalStatus());
        keyPerson.setPosition(request.getPosition());
        keyPerson.setHobbies(request.getHobbies());
        keyPerson.setIsPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false);
        keyPerson.setContactInfo(request.getContactInfo());
        keyPerson.setRemark(request.getRemark());

        CustomerKeyPerson saved = customerKeyPersonRepository.save(keyPerson);
        // 初始化懒加载的关联对象
        if (saved.getCustomer() != null) {
            Hibernate.initialize(saved.getCustomer());
        }
        return saved;
    }

    @Transactional
    public CustomerKeyPerson updateKeyPerson(UUID id, CustomerKeyPersonRequest request) {
        CustomerKeyPerson keyPerson = customerKeyPersonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("关键人物不存在"));

        if (request.getCustomerId() != null && !keyPerson.getCustomer().getId().equals(request.getCustomerId())) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("客户不存在"));
            keyPerson.setCustomer(customer);
        }

        keyPerson.setName(request.getName());
        keyPerson.setCode(request.getCode());
        keyPerson.setGender(request.getGender());
        keyPerson.setSalutation(request.getSalutation());
        keyPerson.setDirectSuperior(request.getDirectSuperior());
        keyPerson.setPlaceOfOrigin(request.getPlaceOfOrigin());
        keyPerson.setBirthday(request.getBirthday());
        keyPerson.setMaritalStatus(request.getMaritalStatus());
        keyPerson.setPosition(request.getPosition());
        keyPerson.setHobbies(request.getHobbies());
        if (request.getIsPrimary() != null) {
            keyPerson.setIsPrimary(request.getIsPrimary());
        }
        keyPerson.setContactInfo(request.getContactInfo());
        keyPerson.setRemark(request.getRemark());

        CustomerKeyPerson saved = customerKeyPersonRepository.save(keyPerson);
        // 初始化懒加载的关联对象
        if (saved.getCustomer() != null) {
            Hibernate.initialize(saved.getCustomer());
        }
        return saved;
    }

    @Transactional
    public void deleteKeyPerson(UUID id) {
        CustomerKeyPerson keyPerson = customerKeyPersonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("关键人物不存在"));
        customerKeyPersonRepository.delete(keyPerson);
    }

    @Transactional
    public void deleteKeyPersons(List<UUID> ids) {
        for (UUID id : ids) {
            customerKeyPersonRepository.deleteById(id);
        }
    }

    // ========== 团队信息管理 ==========

    @Transactional(readOnly = true)
    public List<Team> findAllTeams(String keyword) {
        List<Team> teams;
        if (keyword != null && !keyword.trim().isEmpty()) {
            teams = teamRepository.searchTeams(keyword.trim());
        } else {
            teams = teamRepository.findAll();
        }
        // 初始化懒加载的关联对象
        teams.forEach(team -> {
            if (team.getLeader() != null) {
                Hibernate.initialize(team.getLeader());
            }
            if (team.getDepartment() != null) {
                Hibernate.initialize(team.getDepartment());
            }
        });
        return teams;
    }

    @Transactional
    public Team createTeam(TeamRequest request) {
        Team team = new Team();
        team.setTeamName(request.getTeamName());
        team.setDescription(request.getDescription());
        team.setRemark(request.getRemark());
        team.setActive(request.getActive() != null ? request.getActive() : true);

        if (request.getLeaderId() != null) {
            UserAccount leader = userAccountRepository.findById(request.getLeaderId())
                    .orElseThrow(() -> new IllegalArgumentException("团队负责人不存在"));
            team.setLeader(leader);
        }

        if (request.getDepartmentId() != null) {
            OptionItem department = optionItemRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("所属部门选项不存在"));
            team.setDepartment(department);
        }

        return teamRepository.save(team);
    }

    @Transactional
    public Team updateTeam(UUID id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));

        team.setTeamName(request.getTeamName());
        team.setDescription(request.getDescription());
        team.setRemark(request.getRemark());
        if (request.getActive() != null) {
            team.setActive(request.getActive());
        }

        if (request.getLeaderId() != null) {
            UserAccount leader = userAccountRepository.findById(request.getLeaderId())
                    .orElseThrow(() -> new IllegalArgumentException("团队负责人不存在"));
            team.setLeader(leader);
        } else {
            team.setLeader(null);
        }

        if (request.getDepartmentId() != null) {
            OptionItem department = optionItemRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("所属部门选项不存在"));
            team.setDepartment(department);
        } else {
            team.setDepartment(null);
        }

        Team saved = teamRepository.save(team);
        // 初始化懒加载的关联对象
        if (saved.getLeader() != null) {
            Hibernate.initialize(saved.getLeader());
        }
        if (saved.getDepartment() != null) {
            Hibernate.initialize(saved.getDepartment());
        }
        return saved;
    }

    @Transactional
    public void deleteTeam(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));
        team.setActive(false);
        teamRepository.save(team);
    }

    // ========== 产品管理 ==========

    @Transactional(readOnly = true)
    public List<Product> findAllProducts(String keyword) {
        List<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productRepository.searchProducts(keyword.trim());
        } else {
            products = productRepository.findAll();
        }
        // 初始化懒加载的关联对象
        products.forEach(product -> {
            if (product.getProductType() != null) {
                Hibernate.initialize(product.getProductType());
            }
            if (product.getProductCategory() != null) {
                Hibernate.initialize(product.getProductCategory());
            }
        });
        return products;
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        // 检查产品编码是否已存在
        if (productRepository.findByProductCode(request.getProductCode()) != null) {
            throw new IllegalStateException("产品编码已存在");
        }

        Product product = new Product();
        product.setProductCode(request.getProductCode());
        product.setProductName(request.getProductName());
        product.setSpecification(request.getSpecification());
        product.setUnit(request.getUnit());
        product.setStandardPrice(request.getStandardPrice());
        product.setCostPrice(request.getCostPrice());
        product.setDescription(request.getDescription());
        product.setSize(request.getSize());
        product.setWeight(request.getWeight());
        product.setDeliveryCycle(request.getDeliveryCycle());
        product.setAttachments(request.getAttachments());
        product.setRemark(request.getRemark());
        product.setActive(request.getActive() != null ? request.getActive() : true);

        if (request.getProductTypeId() != null) {
            OptionItem productType = optionItemRepository.findById(request.getProductTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("产品类型选项不存在"));
            product.setProductType(productType);
        }

        if (request.getProductCategoryId() != null) {
            OptionItem productCategory = optionItemRepository.findById(request.getProductCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("产品分类选项不存在"));
            product.setProductCategory(productCategory);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("产品不存在"));

        // 检查产品编码是否被其他产品使用
        Product existingProduct = productRepository.findByProductCode(request.getProductCode());
        if (existingProduct != null && !existingProduct.getId().equals(id)) {
            throw new IllegalStateException("产品编码已被其他产品使用");
        }

        product.setProductCode(request.getProductCode());
        product.setProductName(request.getProductName());
        product.setSpecification(request.getSpecification());
        product.setUnit(request.getUnit());
        product.setStandardPrice(request.getStandardPrice());
        product.setCostPrice(request.getCostPrice());
        product.setDescription(request.getDescription());
        product.setSize(request.getSize());
        product.setWeight(request.getWeight());
        product.setDeliveryCycle(request.getDeliveryCycle());
        product.setAttachments(request.getAttachments());
        product.setRemark(request.getRemark());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        if (request.getProductTypeId() != null) {
            OptionItem productType = optionItemRepository.findById(request.getProductTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("产品类型选项不存在"));
            product.setProductType(productType);
        } else {
            product.setProductType(null);
        }

        if (request.getProductCategoryId() != null) {
            OptionItem productCategory = optionItemRepository.findById(request.getProductCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("产品分类选项不存在"));
            product.setProductCategory(productCategory);
        } else {
            product.setProductCategory(null);
        }

        Product saved = productRepository.save(product);
        // 初始化懒加载的关联对象
        if (saved.getProductType() != null) {
            Hibernate.initialize(saved.getProductType());
        }
        if (saved.getProductCategory() != null) {
            Hibernate.initialize(saved.getProductCategory());
        }
        return saved;
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        product.setActive(false);
        productRepository.save(product);
    }

    // ========== 销售片区管理 ==========

    @Transactional(readOnly = true)
    public List<SalesArea> findAllSalesAreas(String keyword) {
        List<SalesArea> salesAreas;
        if (keyword != null && !keyword.trim().isEmpty()) {
            salesAreas = salesAreaRepository.searchSalesAreas(keyword.trim());
        } else {
            salesAreas = salesAreaRepository.findAll();
        }
        // 初始化懒加载的关联对象
        salesAreas.forEach(area -> {
            if (area.getSuperiorDepartment() != null) {
                Hibernate.initialize(area.getSuperiorDepartment());
            }
        });
        return salesAreas;
    }

    @Transactional
    public SalesArea createSalesArea(SalesAreaRequest request) {
        // 检查片区编号是否已存在
        if (salesAreaRepository.findByAreaCode(request.getAreaCode()).isPresent()) {
            throw new IllegalStateException("片区编号已存在");
        }

        SalesArea salesArea = new SalesArea();
        salesArea.setAreaName(request.getAreaName());
        salesArea.setAreaCode(request.getAreaCode());
        salesArea.setRemark(request.getRemark());
        salesArea.setActive(request.getActive() != null ? request.getActive() : true);

        if (request.getSuperiorDepartmentId() != null) {
            OptionItem department = optionItemRepository.findById(request.getSuperiorDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("上级部门选项不存在"));
            salesArea.setSuperiorDepartment(department);
        }

        SalesArea saved = salesAreaRepository.save(salesArea);
        if (saved.getSuperiorDepartment() != null) {
            Hibernate.initialize(saved.getSuperiorDepartment());
        }
        return saved;
    }

    @Transactional
    public SalesArea updateSalesArea(UUID id, SalesAreaRequest request) {
        SalesArea salesArea = salesAreaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售片区不存在"));

        // 检查片区编号是否被其他片区使用
        Optional<SalesArea> existingArea = salesAreaRepository.findByAreaCode(request.getAreaCode());
        if (existingArea.isPresent() && !existingArea.get().getId().equals(id)) {
            throw new IllegalStateException("片区编号已被其他片区使用");
        }

        salesArea.setAreaName(request.getAreaName());
        salesArea.setAreaCode(request.getAreaCode());
        salesArea.setRemark(request.getRemark());
        if (request.getActive() != null) {
            salesArea.setActive(request.getActive());
        }

        if (request.getSuperiorDepartmentId() != null) {
            OptionItem department = optionItemRepository.findById(request.getSuperiorDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("上级部门选项不存在"));
            salesArea.setSuperiorDepartment(department);
        } else {
            salesArea.setSuperiorDepartment(null);
        }

        SalesArea saved = salesAreaRepository.save(salesArea);
        if (saved.getSuperiorDepartment() != null) {
            Hibernate.initialize(saved.getSuperiorDepartment());
        }
        return saved;
    }

    @Transactional
    public void deleteSalesArea(UUID id) {
        SalesArea salesArea = salesAreaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售片区不存在"));
        salesArea.setActive(false);
        salesAreaRepository.save(salesArea);
    }

    // ========== 营销人员管理 ==========

    @Transactional(readOnly = true)
    public List<MarketingPersonnel> findAllMarketingPersonnel(String keyword) {
        List<MarketingPersonnel> personnelList;
        if (keyword != null && !keyword.trim().isEmpty()) {
            personnelList = marketingPersonnelRepository.searchMarketingPersonnel(keyword.trim());
        } else {
            personnelList = marketingPersonnelRepository.findAll();
        }
        // 初始化懒加载的关联对象
        personnelList.forEach(personnel -> {
            if (personnel.getResponsibleArea() != null) {
                Hibernate.initialize(personnel.getResponsibleArea());
            }
        });
        return personnelList;
    }

    @Transactional(readOnly = true)
    public List<MarketingPersonnel> findMarketingPersonnelByArea(UUID areaId) {
        List<MarketingPersonnel> personnelList = marketingPersonnelRepository.findByResponsibleAreaIdAndActiveTrue(areaId);
        personnelList.forEach(personnel -> {
            if (personnel.getResponsibleArea() != null) {
                Hibernate.initialize(personnel.getResponsibleArea());
            }
        });
        return personnelList;
    }

    @Transactional
    public MarketingPersonnel createMarketingPersonnel(MarketingPersonnelRequest request) {
        MarketingPersonnel personnel = new MarketingPersonnel();
        personnel.setName(request.getName());
        personnel.setGender(request.getGender());
        personnel.setBirthday(request.getBirthday());
        personnel.setContactInfo(request.getContactInfo());
        personnel.setPosition(request.getPosition());
        personnel.setRemark(request.getRemark());
        personnel.setActive(request.getActive() != null ? request.getActive() : true);

        if (request.getResponsibleAreaId() != null) {
            SalesArea area = salesAreaRepository.findById(request.getResponsibleAreaId())
                    .orElseThrow(() -> new IllegalArgumentException("负责区域不存在"));
            personnel.setResponsibleArea(area);
        }

        MarketingPersonnel saved = marketingPersonnelRepository.save(personnel);
        if (saved.getResponsibleArea() != null) {
            Hibernate.initialize(saved.getResponsibleArea());
        }
        return saved;
    }

    @Transactional
    public MarketingPersonnel updateMarketingPersonnel(UUID id, MarketingPersonnelRequest request) {
        MarketingPersonnel personnel = marketingPersonnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("营销人员不存在"));

        personnel.setName(request.getName());
        personnel.setGender(request.getGender());
        personnel.setBirthday(request.getBirthday());
        personnel.setContactInfo(request.getContactInfo());
        personnel.setPosition(request.getPosition());
        personnel.setRemark(request.getRemark());
        if (request.getActive() != null) {
            personnel.setActive(request.getActive());
        }

        if (request.getResponsibleAreaId() != null) {
            SalesArea area = salesAreaRepository.findById(request.getResponsibleAreaId())
                    .orElseThrow(() -> new IllegalArgumentException("负责区域不存在"));
            personnel.setResponsibleArea(area);
        } else {
            personnel.setResponsibleArea(null);
        }

        MarketingPersonnel saved = marketingPersonnelRepository.save(personnel);
        if (saved.getResponsibleArea() != null) {
            Hibernate.initialize(saved.getResponsibleArea());
        }
        return saved;
    }

    @Transactional
    public void deleteMarketingPersonnel(UUID id) {
        MarketingPersonnel personnel = marketingPersonnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("营销人员不存在"));
        personnel.setActive(false);
        marketingPersonnelRepository.save(personnel);
    }

    // ========== 片区人员调动管理 ==========

    @Transactional
    public void transferPersonnel(PersonnelTransferRequest request) {
        // 验证目标片区存在
        SalesArea targetArea = salesAreaRepository.findById(request.getTargetAreaId())
                .orElseThrow(() -> new IllegalArgumentException("目标片区不存在"));

        // 批量调动人员
        for (UUID personnelId : request.getPersonnelIds()) {
            MarketingPersonnel personnel = marketingPersonnelRepository.findById(personnelId)
                    .orElseThrow(() -> new IllegalArgumentException("营销人员不存在: " + personnelId));
            personnel.setResponsibleArea(targetArea);
            marketingPersonnelRepository.save(personnel);
        }
    }
}

