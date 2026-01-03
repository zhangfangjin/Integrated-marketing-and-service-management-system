package org.example.rootmanage.auth;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.account.UserAccount;
import org.example.rootmanage.account.UserAccountRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token服务，用于存储和验证token
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final UserAccountRepository userAccountRepository;
    
    // token -> userId 的映射（内存存储）
    private final Map<String, UUID> tokenMap = new ConcurrentHashMap<>();

    /**
     * 存储token和用户ID的映射
     */
    public void storeToken(String token, UUID userId) {
        tokenMap.put(token, userId);
    }

    /**
     * 根据token获取用户ID
     */
    public UUID getUserIdByToken(String token) {
        return tokenMap.get(token);
    }

    /**
     * 根据token获取用户信息
     * 使用 @Transactional 确保在事务内访问，避免懒加载问题
     */
    @Transactional(readOnly = true)
    public UserAccount getUserByToken(String token) {
        UUID userId = getUserIdByToken(token);
        if (userId == null) {
            return null;
        }
        UserAccount user = userAccountRepository.findById(userId).orElse(null);
        if (user != null) {
            // 初始化所有懒加载的关联对象，确保在事务内完成
            if (user.getRole() != null) {
                Hibernate.initialize(user.getRole());
                // 访问属性确保完全加载
                user.getRole().getName();
                user.getRole().getDescription();
            }
            if (user.getPosition() != null) {
                Hibernate.initialize(user.getPosition());
            }
            if (user.getRegion() != null) {
                Hibernate.initialize(user.getRegion());
            }
        }
        return user;
    }

    /**
     * 移除token
     */
    public void removeToken(String token) {
        tokenMap.remove(token);
    }

    /**
     * 验证token是否有效
     */
    public boolean isValidToken(String token) {
        return token != null && tokenMap.containsKey(token);
    }
}

