package org.example.rootmanage.basicinfo;

import org.example.rootmanage.basicinfo.entity.CustomerKeyPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerKeyPersonRepository extends JpaRepository<CustomerKeyPerson, UUID> {

    /**
     * 根据客户ID查询所有关键人物
     */
    List<CustomerKeyPerson> findByCustomerId(UUID customerId);

    /**
     * 根据客户ID删除所有关键人物
     */
    void deleteByCustomerId(UUID customerId);
}

