package org.example.rootmanage.basicinfo;

import org.example.rootmanage.basicinfo.entity.MarketingPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MarketingPersonnelRepository extends JpaRepository<MarketingPersonnel, UUID> {

    /**
     * 搜索营销人员（支持按姓名、联系方式模糊查询）
     */
    @Query("SELECT mp FROM MarketingPersonnel mp WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "mp.name LIKE %:keyword% OR " +
           "mp.contactInfo LIKE %:keyword%)")
    List<MarketingPersonnel> searchMarketingPersonnel(@Param("keyword") String keyword);

    /**
     * 查询所有启用的营销人员
     */
    List<MarketingPersonnel> findByActiveTrue();

    /**
     * 根据负责区域查询营销人员
     */
    List<MarketingPersonnel> findByResponsibleAreaId(UUID areaId);

    /**
     * 根据负责区域查询启用的营销人员
     */
    List<MarketingPersonnel> findByResponsibleAreaIdAndActiveTrue(UUID areaId);
}

