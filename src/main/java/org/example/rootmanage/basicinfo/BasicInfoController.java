package org.example.rootmanage.basicinfo;

import lombok.RequiredArgsConstructor;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 基本信息管理Controller
 */
@RestController
@RequestMapping("/api/basicinfo")
@RequiredArgsConstructor
public class BasicInfoController {

    private final BasicInfoService basicInfoService;

    // ========== 客户管理 ==========

    @GetMapping("/customers")
    public List<Customer> getCustomers(@RequestParam(required = false) String keyword) {
        return basicInfoService.findAllCustomers(keyword);
    }

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody @Validated CustomerRequest request) {
        return basicInfoService.createCustomer(request);
    }

    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@PathVariable UUID id, @RequestBody @Validated CustomerRequest request) {
        return basicInfoService.updateCustomer(id, request);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        basicInfoService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    // ========== 客户关键人物管理 ==========

    @GetMapping("/customers/{customerId}/key-persons")
    public List<CustomerKeyPerson> getKeyPersons(@PathVariable UUID customerId) {
        return basicInfoService.findAllKeyPersonsByCustomerId(customerId);
    }

    @PostMapping("/customers/{customerId}/key-persons")
    public CustomerKeyPerson createKeyPerson(
            @PathVariable UUID customerId,
            @RequestBody @Validated CustomerKeyPersonRequest request) {
        request.setCustomerId(customerId);
        return basicInfoService.createKeyPerson(request);
    }

    @PutMapping("/customers/{customerId}/key-persons/{id}")
    public CustomerKeyPerson updateKeyPerson(
            @PathVariable UUID customerId,
            @PathVariable UUID id,
            @RequestBody @Validated CustomerKeyPersonRequest request) {
        request.setCustomerId(customerId);
        return basicInfoService.updateKeyPerson(id, request);
    }

    @DeleteMapping("/customers/{customerId}/key-persons/{id}")
    public ResponseEntity<Void> deleteKeyPerson(
            @PathVariable UUID customerId,
            @PathVariable UUID id) {
        basicInfoService.deleteKeyPerson(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/customers/{customerId}/key-persons")
    public ResponseEntity<Void> deleteKeyPersons(
            @PathVariable UUID customerId,
            @RequestBody List<UUID> ids) {
        basicInfoService.deleteKeyPersons(ids);
        return ResponseEntity.ok().build();
    }

    // ========== 团队信息管理 ==========

    @GetMapping("/teams")
    public List<Team> getTeams(@RequestParam(required = false) String keyword) {
        return basicInfoService.findAllTeams(keyword);
    }

    @PostMapping("/teams")
    public Team createTeam(@RequestBody @Validated TeamRequest request) {
        return basicInfoService.createTeam(request);
    }

    @PutMapping("/teams/{id}")
    public Team updateTeam(@PathVariable UUID id, @RequestBody @Validated TeamRequest request) {
        return basicInfoService.updateTeam(id, request);
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        basicInfoService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }

    // ========== 产品管理 ==========

    @GetMapping("/products")
    public List<Product> getProducts(@RequestParam(required = false) String keyword) {
        return basicInfoService.findAllProducts(keyword);
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody @Validated ProductRequest request) {
        return basicInfoService.createProduct(request);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable UUID id, @RequestBody @Validated ProductRequest request) {
        return basicInfoService.updateProduct(id, request);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        basicInfoService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    // ========== 销售片区管理 ==========

    @GetMapping("/sales-areas")
    public List<SalesArea> getSalesAreas(@RequestParam(required = false) String keyword) {
        return basicInfoService.findAllSalesAreas(keyword);
    }

    @PostMapping("/sales-areas")
    public SalesArea createSalesArea(@RequestBody @Validated SalesAreaRequest request) {
        return basicInfoService.createSalesArea(request);
    }

    @PutMapping("/sales-areas/{id}")
    public SalesArea updateSalesArea(@PathVariable UUID id, @RequestBody @Validated SalesAreaRequest request) {
        return basicInfoService.updateSalesArea(id, request);
    }

    @DeleteMapping("/sales-areas/{id}")
    public ResponseEntity<Void> deleteSalesArea(@PathVariable UUID id) {
        basicInfoService.deleteSalesArea(id);
        return ResponseEntity.ok().build();
    }

    // ========== 营销人员管理 ==========

    @GetMapping("/marketing-personnel")
    public List<MarketingPersonnel> getMarketingPersonnel(@RequestParam(required = false) String keyword) {
        return basicInfoService.findAllMarketingPersonnel(keyword);
    }

    @GetMapping("/sales-areas/{areaId}/marketing-personnel")
    public List<MarketingPersonnel> getMarketingPersonnelByArea(@PathVariable UUID areaId) {
        return basicInfoService.findMarketingPersonnelByArea(areaId);
    }

    @PostMapping("/marketing-personnel")
    public MarketingPersonnel createMarketingPersonnel(@RequestBody @Validated MarketingPersonnelRequest request) {
        return basicInfoService.createMarketingPersonnel(request);
    }

    @PutMapping("/marketing-personnel/{id}")
    public MarketingPersonnel updateMarketingPersonnel(
            @PathVariable UUID id,
            @RequestBody @Validated MarketingPersonnelRequest request) {
        return basicInfoService.updateMarketingPersonnel(id, request);
    }

    @DeleteMapping("/marketing-personnel/{id}")
    public ResponseEntity<Void> deleteMarketingPersonnel(@PathVariable UUID id) {
        basicInfoService.deleteMarketingPersonnel(id);
        return ResponseEntity.ok().build();
    }

    // ========== 片区人员调动管理 ==========

    @PostMapping("/personnel/transfer")
    public ResponseEntity<Void> transferPersonnel(@RequestBody @Validated PersonnelTransferRequest request) {
        basicInfoService.transferPersonnel(request);
        return ResponseEntity.ok().build();
    }
}

