package org.example.rootmanage.account;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.account.dto.RegistrationRequest;
import org.example.rootmanage.account.dto.RegistrationReviewRequest;
import org.example.rootmanage.account.dto.ResetPasswordRequest;
import org.example.rootmanage.account.dto.UpdatePersonnelRequest;
import org.example.rootmanage.option.OptionItem;
import org.example.rootmanage.option.OptionItemRepository;
import org.example.rootmanage.role.Role;
import org.example.rootmanage.role.RoleRepository;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserAccountRepository userAccountRepository;
    private final OptionItemRepository optionItemRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<UserAccount> findByIdCard(String idCard) {
        return userAccountRepository.findByIdCard(idCard);
    }

    @Transactional(readOnly = true)
    public List<UserAccount> findPending() {
        List<UserAccount> accounts = userAccountRepository.findByStatus(AccountStatus.PENDING);
        // 初始化懒加载的关联对象
        accounts.forEach(account -> {
            if (account.getRole() != null) {
                Hibernate.initialize(account.getRole());
            }
            if (account.getPosition() != null) {
                Hibernate.initialize(account.getPosition());
            }
            if (account.getRegion() != null) {
                Hibernate.initialize(account.getRegion());
            }
        });
        return accounts;
    }

    @Transactional
    public UserAccount register(RegistrationRequest request) {
        userAccountRepository.findByIdCard(request.getIdCard()).ifPresent(u -> {
            throw new IllegalStateException("身份证已注册");
        });
        userAccountRepository.findByPhone(request.getPhone()).ifPresent(u -> {
            throw new IllegalStateException("手机号已注册");
        });
        OptionItem position = optionItemRepository.findById(request.getPositionId())
                .orElseThrow(() -> new IllegalArgumentException("岗位选项不存在"));
        OptionItem region = optionItemRepository.findById(request.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("所属片区选项不存在"));

        UserAccount user = new UserAccount();
        user.setName(request.getName());
        user.setIdCard(request.getIdCard());
        user.setPhone(request.getPhone());
        user.setBirthDate(request.getBirthDate());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setPosition(position);
        user.setRegion(region);
        user.setStatus(AccountStatus.PENDING);
        // 占位信息，待审核后覆盖
        user.setUsername(request.getPhone());
        user.setPassword(passwordEncoder.encode("PENDING"));
        return userAccountRepository.save(user);
    }

    @Transactional
    public UserAccount approve(UUID userId, RegistrationReviewRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (user.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("用户非待审核状态");
        }
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));
        user.setUsername(user.getPhone());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(role);
        user.setStatus(AccountStatus.APPROVED);
        user.setRemark(request.getRemark());
        return userAccountRepository.save(user);
    }

    @Transactional
    public UserAccount reject(UUID userId, String remark) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (user.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("用户非待审核状态");
        }
        user.setStatus(AccountStatus.REJECTED);
        user.setRemark(remark);
        return userAccountRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserAccount> findAll() {
        List<UserAccount> accounts = userAccountRepository.findAll();
        // 初始化懒加载的关联对象
        accounts.forEach(account -> {
            if (account.getRole() != null) {
                Hibernate.initialize(account.getRole());
            }
            if (account.getPosition() != null) {
                Hibernate.initialize(account.getPosition());
            }
            if (account.getRegion() != null) {
                Hibernate.initialize(account.getRegion());
            }
        });
        return accounts;
    }

    @Transactional
    public UserAccount resetPassword(UUID userId, ResetPasswordRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userAccountRepository.save(user);
    }

    @Transactional
    public UserAccount updateRole(UUID userId, UUID roleId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));
        user.setRole(role);
        return userAccountRepository.save(user);
    }

    @Transactional
    public UserAccount changeStatus(UUID userId, AccountStatus status) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setStatus(status);
        return userAccountRepository.save(user);
    }

    /**
     * 更新人员信息（用于人员档案管理）
     */
    @Transactional
    public UserAccount updatePersonnel(UUID userId, UpdatePersonnelRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 检查身份证号是否被其他用户使用
        if (!user.getIdCard().equals(request.getIdCard())) {
            userAccountRepository.findByIdCard(request.getIdCard()).ifPresent(u -> {
                if (!u.getId().equals(userId)) {
                    throw new IllegalStateException("身份证号已被其他用户使用");
                }
            });
        }

        // 检查手机号是否被其他用户使用
        if (!user.getPhone().equals(request.getPhone())) {
            userAccountRepository.findByPhone(request.getPhone()).ifPresent(u -> {
                if (!u.getId().equals(userId)) {
                    throw new IllegalStateException("手机号已被其他用户使用");
                }
            });
        }

        OptionItem position = optionItemRepository.findById(request.getPositionId())
                .orElseThrow(() -> new IllegalArgumentException("岗位选项不存在"));
        OptionItem region = optionItemRepository.findById(request.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("所属片区选项不存在"));

        user.setName(request.getName());
        user.setIdCard(request.getIdCard());
        user.setPhone(request.getPhone());
        user.setBirthDate(request.getBirthDate());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setPosition(position);
        user.setRegion(region);

        UserAccount savedUser = userAccountRepository.save(user);
        
        // 初始化懒加载的关联对象，确保序列化时不会出错
        if (savedUser.getRole() != null) {
            Hibernate.initialize(savedUser.getRole());
        }
        if (savedUser.getPosition() != null) {
            Hibernate.initialize(savedUser.getPosition());
        }
        if (savedUser.getRegion() != null) {
            Hibernate.initialize(savedUser.getRegion());
        }
        
        return savedUser;
    }

    /**
     * 删除人员（软删除：设置为禁用状态，不真正删除数据）
     */
    @Transactional
    public void deletePersonnel(UUID userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setStatus(AccountStatus.DISABLED);
        userAccountRepository.save(user);
    }

    /**
     * 搜索人员档案（支持按姓名、身份证号、手机号模糊查询）
     */
    @Transactional(readOnly = true)
    public List<UserAccount> searchPersonnel(String keyword) {
        List<UserAccount> accounts = userAccountRepository.searchPersonnel(keyword);
        // 初始化懒加载的关联对象
        accounts.forEach(account -> {
            if (account.getRole() != null) {
                Hibernate.initialize(account.getRole());
            }
            if (account.getPosition() != null) {
                Hibernate.initialize(account.getPosition());
            }
            if (account.getRegion() != null) {
                Hibernate.initialize(account.getRegion());
            }
        });
        return accounts;
    }
}

