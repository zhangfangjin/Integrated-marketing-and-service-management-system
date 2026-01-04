package org.example.rootmanage.pricebook.repository;

import org.example.rootmanage.pricebook.entity.PriceBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 价格本Repository接口
 */
public interface PriceBookRepository extends JpaRepository<PriceBook, UUID> {

    /**
     * 根据产品ID查找所有价格记录
     */
    @Query("SELECT p FROM PriceBook p WHERE p.product.id = :productId ORDER BY p.effectiveDate DESC")
    List<PriceBook> findByProductId(@Param("productId") UUID productId);

    /**
     * 根据产品ID和价格类型查找价格记录
     */
    @Query("SELECT p FROM PriceBook p WHERE p.product.id = :productId AND p.priceType = :priceType AND p.active = true ORDER BY p.effectiveDate DESC")
    List<PriceBook> findByProductIdAndPriceType(@Param("productId") UUID productId, @Param("priceType") String priceType);

    /**
     * 查找所有启用的价格记录
     */
    @Query("SELECT p FROM PriceBook p WHERE p.active = true ORDER BY p.product.id, p.effectiveDate DESC")
    List<PriceBook> findAllActive();

    /**
     * 根据关键词搜索（产品名称、产品编码、价格版本号）
     */
    @Query("SELECT p FROM PriceBook p WHERE " +
            "(p.product.productName LIKE :keyword OR " +
            "p.product.productCode LIKE :keyword OR " +
            "p.versionNumber LIKE :keyword) AND p.active = true")
    List<PriceBook> searchByKeyword(@Param("keyword") String keyword);
}

