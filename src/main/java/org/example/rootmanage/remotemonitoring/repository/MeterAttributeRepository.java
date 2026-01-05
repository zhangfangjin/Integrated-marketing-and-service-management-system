package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.MeterAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 表计属性数据访问接口
 */
@Repository
public interface MeterAttributeRepository extends JpaRepository<MeterAttribute, UUID> {

    /**
     * 根据表计ID查找属性列表
     */
    List<MeterAttribute> findByMeterIdOrderBySortOrder(UUID meterId);

    /**
     * 根据表计ID删除所有属性
     */
    void deleteByMeterId(UUID meterId);
}





