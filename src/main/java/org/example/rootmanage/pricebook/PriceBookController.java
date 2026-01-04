package org.example.rootmanage.pricebook;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.pricebook.dto.PriceBookRequest;
import org.example.rootmanage.pricebook.entity.PriceBook;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 价格本管理Controller
 */
@RestController
@RequestMapping("/api/pricebook")
@RequiredArgsConstructor
public class PriceBookController {

    private final PriceBookService priceBookService;

    /**
     * 获取所有价格记录
     */
    @GetMapping
    public List<PriceBook> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return priceBookService.searchByKeyword(keyword);
        }
        return priceBookService.findAllActive();
    }

    /**
     * 根据ID获取价格记录
     */
    @GetMapping("/{id}")
    public PriceBook getById(@PathVariable UUID id) {
        return priceBookService.findById(id);
    }

    /**
     * 根据产品ID获取价格记录
     */
    @GetMapping("/product/{productId}")
    public List<PriceBook> getByProductId(@PathVariable UUID productId) {
        return priceBookService.findByProductId(productId);
    }

    /**
     * 根据产品ID和价格类型获取价格记录
     */
    @GetMapping("/product/{productId}/type/{priceType}")
    public List<PriceBook> getByProductIdAndType(
            @PathVariable UUID productId,
            @PathVariable String priceType) {
        return priceBookService.findByProductIdAndPriceType(productId, priceType);
    }

    /**
     * 创建价格记录
     */
    @PostMapping
    public PriceBook create(@RequestBody @Validated PriceBookRequest request) {
        return priceBookService.create(request);
    }

    /**
     * 更新价格记录
     */
    @PutMapping("/{id}")
    public PriceBook update(@PathVariable UUID id, @RequestBody @Validated PriceBookRequest request) {
        return priceBookService.update(id, request);
    }

    /**
     * 删除价格记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        priceBookService.delete(id);
        return ResponseEntity.ok().build();
    }
}

