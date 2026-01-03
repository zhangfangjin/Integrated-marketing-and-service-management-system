package org.example.rootmanage.companyinfo;

import org.example.rootmanage.companyinfo.entity.CompanyInfoRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyInfoReleaseRepository extends JpaRepository<CompanyInfoRelease, UUID> {

    /**
     * 查询所有启用的信息发布
     */
    List<CompanyInfoRelease> findByActiveTrueOrderByCreateTimeDesc();

    /**
     * 根据关键词搜索信息发布（标题或内容）
     */
    @Query("SELECT c FROM CompanyInfoRelease c WHERE c.active = true " +
           "AND (c.title LIKE %:keyword% OR c.content LIKE %:keyword%) " +
           "ORDER BY c.createTime DESC")
    List<CompanyInfoRelease> searchByKeyword(@Param("keyword") String keyword);
}

