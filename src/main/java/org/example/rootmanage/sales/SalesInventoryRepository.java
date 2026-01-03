package org.example.rootmanage.sales;

import org.example.rootmanage.sales.entity.SalesInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SalesInventoryRepository extends JpaRepository<SalesInventory, UUID> {

    /**
     * 根据产品ID查询库存
     */
    List<SalesInventory> findByProductId(UUID productId);

    /**
     * 根据仓库ID查询库存
     */
    List<SalesInventory> findByWarehouseId(UUID warehouseId);

    /**
     * 搜索库存（支持按产品名称、产品编码模糊查询）
     */
    @Query("SELECT i FROM SalesInventory i JOIN i.product p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "p.productName LIKE %:keyword% OR " +
           "p.productCode LIKE %:keyword%)")
    List<SalesInventory> searchInventories(@Param("keyword") String keyword);
}

