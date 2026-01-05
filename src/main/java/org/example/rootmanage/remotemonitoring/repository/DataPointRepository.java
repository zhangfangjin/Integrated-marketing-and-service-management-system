package org.example.rootmanage.remotemonitoring.repository;

import org.example.rootmanage.remotemonitoring.entity.CollectionMode;
import org.example.rootmanage.remotemonitoring.entity.DataPoint;
import org.example.rootmanage.remotemonitoring.entity.PointType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 数据点位数据访问接口
 */
@Repository
public interface DataPointRepository extends JpaRepository<DataPoint, UUID> {

    /**
     * 根据点位编码查找
     */
    Optional<DataPoint> findByPointCode(String pointCode);

    /**
     * 根据点位类型查找
     */
    List<DataPoint> findByPointType(PointType pointType);

    /**
     * 根据表计ID查找
     */
    List<DataPoint> findByMeterId(UUID meterId);

    /**
     * 根据采集模式查找
     */
    List<DataPoint> findByCollectionMode(CollectionMode collectionMode);

    /**
     * 查找启用的点位
     */
    List<DataPoint> findByEnabled(Boolean enabled);

    /**
     * 查找启用报警的点位
     */
    List<DataPoint> findByAlarmEnabled(Boolean alarmEnabled);

    /**
     * 搜索点位
     */
    @Query("SELECT d FROM DataPoint d WHERE " +
            "d.pointName LIKE %:keyword% OR " +
            "d.pointCode LIKE %:keyword%")
    List<DataPoint> searchDataPoints(@Param("keyword") String keyword);

    /**
     * 根据多个ID查找
     */
    List<DataPoint> findByIdIn(List<UUID> ids);
}





