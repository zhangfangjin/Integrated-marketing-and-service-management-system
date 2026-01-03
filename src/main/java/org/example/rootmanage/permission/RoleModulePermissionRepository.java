package org.example.rootmanage.permission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleModulePermissionRepository extends JpaRepository<RoleModulePermission, UUID> {

    List<RoleModulePermission> findByRoleId(UUID roleId);

    Optional<RoleModulePermission> findByRoleIdAndModuleId(UUID roleId, UUID moduleId);

    void deleteByRoleId(UUID roleId);
}

