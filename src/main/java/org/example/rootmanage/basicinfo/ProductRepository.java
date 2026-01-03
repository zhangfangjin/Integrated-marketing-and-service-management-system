package org.example.rootmanage.basicinfo;

import org.example.rootmanage.basicinfo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * 搜索产品（支持按产品编码、产品名称模糊查询）
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "p.productCode LIKE %:keyword% OR " +
           "p.productName LIKE %:keyword%)")
    List<Product> searchProducts(@Param("keyword") String keyword);

    /**
     * 查询所有启用的产品
     */
    List<Product> findByActiveTrue();

    /**
     * 根据产品编码查询
     */
    Product findByProductCode(String productCode);
}

