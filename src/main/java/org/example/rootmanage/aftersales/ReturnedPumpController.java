package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.ReturnedPumpRequest;
import org.example.rootmanage.aftersales.entity.ReturnedPump;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 返厂泵管理Controller
 */
@RestController
@RequestMapping("/api/returned-pumps")
@RequiredArgsConstructor
public class ReturnedPumpController {

    private final ReturnedPumpService returnedPumpService;

    /**
     * 获取返厂泵列表
     */
    @GetMapping
    public List<ReturnedPump> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return returnedPumpService.searchReturnedPumps(keyword.trim());
        }
        return returnedPumpService.findAll();
    }

    /**
     * 根据合同ID获取返厂泵列表
     */
    @GetMapping("/contract/{contractId}")
    public List<ReturnedPump> listByContract(@PathVariable UUID contractId) {
        return returnedPumpService.findByContractId(contractId);
    }

    /**
     * 根据ID获取返厂泵详情
     */
    @GetMapping("/{id}")
    public ReturnedPump getById(@PathVariable UUID id) {
        return returnedPumpService.findById(id);
    }

    /**
     * 创建返厂泵信息
     */
    @PostMapping
    public ReturnedPump create(@RequestBody @Validated ReturnedPumpRequest request) {
        return returnedPumpService.create(request);
    }

    /**
     * 更新返厂泵信息
     */
    @PutMapping("/{id}")
    public ReturnedPump update(@PathVariable UUID id, @RequestBody @Validated ReturnedPumpRequest request) {
        return returnedPumpService.update(id, request);
    }

    /**
     * 删除返厂泵信息
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        returnedPumpService.delete(id);
    }
}

















