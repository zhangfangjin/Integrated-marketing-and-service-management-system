package org.example.rootmanage.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByIdCard(String idCard);

    Optional<UserAccount> findByPhone(String phone);

    Optional<UserAccount> findByUsername(String username);

    List<UserAccount> findByStatus(AccountStatus status);

    /**
     * 搜索人员档案（支持按姓名、身份证号、手机号模糊查询）
     * 只返回已审核通过或已禁用的人员
     */
    @Query("SELECT u FROM UserAccount u WHERE " +
           "(u.status = 'APPROVED' OR u.status = 'DISABLED') AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "u.name LIKE %:keyword% OR " +
           "u.idCard LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword%)")
    List<UserAccount> searchPersonnel(@Param("keyword") String keyword);
}

