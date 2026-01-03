package org.example.rootmanage.basicinfo;

import org.example.rootmanage.basicinfo.entity.SalesArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalesAreaRepository extends JpaRepository<SalesArea, UUID> {

    /**
     * 搜索销售片区（支持按片区名称、片区编号模糊查询）
     */
    @Query("SELECT sa FROM SalesArea sa WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "sa.areaName LIKE %:keyword% OR " +
           "sa.areaCode LIKE %:keyword%)")
    List<SalesArea> searchSalesAreas(@Param("keyword") String keyword);

    /**
     * 查询所有启用的销售片区
     */
    List<SalesArea> findByActiveTrue();

    /**
     * 根据片区编号查询
     */
    Optional<SalesArea> findByAreaCode(String areaCode);
}

