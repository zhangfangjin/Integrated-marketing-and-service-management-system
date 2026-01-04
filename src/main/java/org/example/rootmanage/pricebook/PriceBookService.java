package org.example.rootmanage.pricebook;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.basicinfo.ProductRepository;
import org.example.rootmanage.pricebook.dto.PriceBookRequest;
import org.example.rootmanage.pricebook.entity.PriceBook;
import org.example.rootmanage.pricebook.repository.PriceBookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 价格本Service
 */
@Service
@RequiredArgsConstructor
public class PriceBookService {

    private final PriceBookRepository priceBookRepository;
    private final ProductRepository productRepository;

    /**
     * 获取所有价格记录
     */
    public List<PriceBook> findAll() {
        return priceBookRepository.findAll();
    }

    /**
     * 获取所有启用的价格记录
     */
    public List<PriceBook> findAllActive() {
        return priceBookRepository.findAllActive();
    }

    /**
     * 根据ID查找价格记录
     */
    public PriceBook findById(UUID id) {
        return priceBookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("价格记录不存在"));
    }

    /**
     * 根据产品ID查找价格记录
     */
    public List<PriceBook> findByProductId(UUID productId) {
        return priceBookRepository.findByProductId(productId);
    }

    /**
     * 根据产品ID和价格类型查找价格记录
     */
    public List<PriceBook> findByProductIdAndPriceType(UUID productId, String priceType) {
        return priceBookRepository.findByProductIdAndPriceType(productId, priceType);
    }

    /**
     * 根据关键词搜索价格记录
     */
    public List<PriceBook> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return priceBookRepository.findAllActive();
        }
        return priceBookRepository.searchByKeyword("%" + keyword.trim() + "%");
    }

    /**
     * 创建价格记录
     */
    @Transactional
    public PriceBook create(PriceBookRequest request) {
        PriceBook priceBook = new PriceBook();
        priceBook.setProduct(productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("产品不存在")));
        priceBook.setVersionNumber(request.getVersionNumber());
        priceBook.setPriceType(request.getPriceType());
        priceBook.setUnitPrice(request.getUnitPrice());
        priceBook.setCurrency(request.getCurrency() != null ? request.getCurrency() : "CNY");
        priceBook.setEffectiveDate(request.getEffectiveDate());
        priceBook.setExpiryDate(request.getExpiryDate());
        priceBook.setActive(request.getActive() != null ? request.getActive() : true);
        priceBook.setRemark(request.getRemark());
        return priceBookRepository.save(priceBook);
    }

    /**
     * 更新价格记录
     */
    @Transactional
    public PriceBook update(UUID id, PriceBookRequest request) {
        PriceBook priceBook = priceBookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("价格记录不存在"));
        
        if (request.getProductId() != null && !request.getProductId().equals(priceBook.getProduct().getId())) {
            priceBook.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("产品不存在")));
        }
        
        if (request.getVersionNumber() != null) {
            priceBook.setVersionNumber(request.getVersionNumber());
        }
        if (request.getPriceType() != null) {
            priceBook.setPriceType(request.getPriceType());
        }
        if (request.getUnitPrice() != null) {
            priceBook.setUnitPrice(request.getUnitPrice());
        }
        if (request.getCurrency() != null) {
            priceBook.setCurrency(request.getCurrency());
        }
        priceBook.setEffectiveDate(request.getEffectiveDate());
        priceBook.setExpiryDate(request.getExpiryDate());
        if (request.getActive() != null) {
            priceBook.setActive(request.getActive());
        }
        priceBook.setRemark(request.getRemark());
        
        return priceBookRepository.save(priceBook);
    }

    /**
     * 删除价格记录
     */
    @Transactional
    public void delete(UUID id) {
        if (!priceBookRepository.existsById(id)) {
            throw new IllegalArgumentException("价格记录不存在");
        }
        priceBookRepository.deleteById(id);
    }
}

