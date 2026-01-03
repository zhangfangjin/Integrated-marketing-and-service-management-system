package org.example.rootmanage.basicinfo;

import org.example.rootmanage.basicinfo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    /**
     * 搜索客户（支持按客户名称、联系人、联系电话模糊查询）
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "c.customerName LIKE %:keyword% OR " +
           "c.contactPerson LIKE %:keyword% OR " +
           "c.contactPhone LIKE %:keyword%)")
    List<Customer> searchCustomers(@Param("keyword") String keyword);

    /**
     * 查询所有启用的客户
     */
    List<Customer> findByActiveTrue();
}

