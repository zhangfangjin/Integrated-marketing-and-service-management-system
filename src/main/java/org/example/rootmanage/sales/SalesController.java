package org.example.rootmanage.sales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.sales.dto.*;
import org.example.rootmanage.sales.entity.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 销售管理Controller
 */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    // ========== 客户来访管理 ==========

    @GetMapping("/customer-visits")
    public List<CustomerVisit> getCustomerVisits(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return salesService.findAllCustomerVisits(customerId, startTime, endTime);
    }

    @PostMapping("/customer-visits")
    public CustomerVisit createCustomerVisit(@RequestBody @Validated CustomerVisitRequest request) {
        return salesService.createCustomerVisit(request);
    }

    @PutMapping("/customer-visits/{id}")
    public CustomerVisit updateCustomerVisit(@PathVariable UUID id, @RequestBody @Validated CustomerVisitRequest request) {
        return salesService.updateCustomerVisit(id, request);
    }

    @DeleteMapping("/customer-visits/{id}")
    public ResponseEntity<Void> deleteCustomerVisit(@PathVariable UUID id) {
        salesService.deleteCustomerVisit(id);
        return ResponseEntity.ok().build();
    }

    // ========== 销售机会管理 ==========

    @GetMapping("/opportunities")
    public List<SalesOpportunity> getSalesOpportunities(@RequestParam(required = false) String keyword) {
        return salesService.findAllSalesOpportunities(keyword);
    }

    @PostMapping("/opportunities")
    public SalesOpportunity createSalesOpportunity(@RequestBody @Validated SalesOpportunityRequest request) {
        return salesService.createSalesOpportunity(request);
    }

    @PutMapping("/opportunities/{id}")
    public SalesOpportunity updateSalesOpportunity(@PathVariable UUID id, @RequestBody @Validated SalesOpportunityRequest request) {
        return salesService.updateSalesOpportunity(id, request);
    }

    @DeleteMapping("/opportunities/{id}")
    public ResponseEntity<Void> deleteSalesOpportunity(@PathVariable UUID id) {
        salesService.deleteSalesOpportunity(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/opportunities/{id}/submit")
    public SalesOpportunity submitSalesOpportunity(@PathVariable UUID id) {
        return salesService.submitSalesOpportunity(id);
    }

    @PostMapping("/opportunities/transfer")
    public ResponseEntity<Void> transferSalesOpportunity(@RequestBody @Validated OpportunityTransferRequest request) {
        salesService.transferSalesOpportunity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/opportunities/assign")
    public ResponseEntity<Void> assignSalesOpportunity(@RequestBody @Validated OpportunityAssignRequest request) {
        salesService.assignSalesOpportunity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/opportunities/close")
    public SalesOpportunity closeSalesOpportunity(@RequestBody @Validated OpportunityCloseRequest request) {
        return salesService.closeSalesOpportunity(request);
    }

    @GetMapping("/opportunities/unassigned")
    public List<SalesOpportunity> getUnassignedOpportunities() {
        return salesService.findUnassignedOpportunities();
    }

    // ========== 销售机会跟踪 ==========

    @GetMapping("/opportunities/{opportunityId}/trackings")
    public List<SalesOpportunityTracking> getOpportunityTrackings(@PathVariable UUID opportunityId) {
        return salesService.findTrackingByOpportunityId(opportunityId);
    }

    @PostMapping("/opportunities/trackings")
    public SalesOpportunityTracking createTracking(@RequestBody @Validated SalesOpportunityTrackingRequest request) {
        return salesService.createTracking(request);
    }

    @PutMapping("/opportunities/trackings/{id}")
    public SalesOpportunityTracking updateTracking(@PathVariable UUID id, @RequestBody @Validated SalesOpportunityTrackingRequest request) {
        return salesService.updateTracking(id, request);
    }

    @DeleteMapping("/opportunities/trackings/{id}")
    public ResponseEntity<Void> deleteTracking(@PathVariable UUID id) {
        salesService.deleteTracking(id);
        return ResponseEntity.ok().build();
    }

    // ========== 销售库存查询 ==========

    @GetMapping("/inventories")
    public List<SalesInventory> getSalesInventories(@RequestParam(required = false) String keyword) {
        return salesService.findAllSalesInventories(keyword);
    }

    @PostMapping("/inventories")
    public SalesInventory createSalesInventory(@RequestBody @Validated SalesInventoryRequest request) {
        return salesService.createSalesInventory(request);
    }

    @PutMapping("/inventories/{id}")
    public SalesInventory updateSalesInventory(@PathVariable UUID id, @RequestBody @Validated SalesInventoryRequest request) {
        return salesService.updateSalesInventory(id, request);
    }

    @DeleteMapping("/inventories/{id}")
    public ResponseEntity<Void> deleteSalesInventory(@PathVariable UUID id) {
        salesService.deleteSalesInventory(id);
        return ResponseEntity.ok().build();
    }

    // ========== 销售报价管理 ==========

    @GetMapping("/quotations")
    public List<SalesQuotation> getSalesQuotations(@RequestParam(required = false) String keyword) {
        return salesService.findAllSalesQuotations(keyword);
    }

    @PostMapping("/quotations")
    public SalesQuotation createSalesQuotation(@RequestBody @Validated SalesQuotationRequest request) {
        return salesService.createSalesQuotation(request);
    }

    @PutMapping("/quotations/{id}")
    public SalesQuotation updateSalesQuotation(@PathVariable UUID id, @RequestBody @Validated SalesQuotationRequest request) {
        return salesService.updateSalesQuotation(id, request);
    }

    @DeleteMapping("/quotations/{id}")
    public ResponseEntity<Void> deleteSalesQuotation(@PathVariable UUID id) {
        salesService.deleteSalesQuotation(id);
        return ResponseEntity.ok().build();
    }

    // ========== 销售降库管理 ==========

    @GetMapping("/inventory-reductions")
    public List<SalesInventoryReduction> getSalesInventoryReductions(@RequestParam(required = false) String keyword) {
        return salesService.findAllSalesInventoryReductions(keyword);
    }

    @PostMapping("/inventory-reductions")
    public SalesInventoryReduction createSalesInventoryReduction(@RequestBody @Validated SalesInventoryReductionRequest request) {
        return salesService.createSalesInventoryReduction(request);
    }

    @PutMapping("/inventory-reductions/{id}")
    public SalesInventoryReduction updateSalesInventoryReduction(@PathVariable UUID id, @RequestBody @Validated SalesInventoryReductionRequest request) {
        return salesService.updateSalesInventoryReduction(id, request);
    }

    @DeleteMapping("/inventory-reductions/{id}")
    public ResponseEntity<Void> deleteSalesInventoryReduction(@PathVariable UUID id) {
        salesService.deleteSalesInventoryReduction(id);
        return ResponseEntity.ok().build();
    }

    // ========== 投标管理 ==========

    @GetMapping("/biddings")
    public List<Bidding> getBiddings(@RequestParam(required = false) String keyword) {
        return salesService.findAllBiddings(keyword);
    }

    @PostMapping("/biddings")
    public Bidding createBidding(@RequestBody @Validated BiddingRequest request) {
        return salesService.createBidding(request);
    }

    @PutMapping("/biddings/{id}")
    public Bidding updateBidding(@PathVariable UUID id, @RequestBody @Validated BiddingRequest request) {
        return salesService.updateBidding(id, request);
    }

    @DeleteMapping("/biddings/{id}")
    public ResponseEntity<Void> deleteBidding(@PathVariable UUID id) {
        salesService.deleteBidding(id);
        return ResponseEntity.ok().build();
    }
}

