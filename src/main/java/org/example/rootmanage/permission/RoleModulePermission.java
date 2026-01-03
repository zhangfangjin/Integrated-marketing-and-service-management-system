package org.example.rootmanage.permission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.rootmanage.common.BaseEntity;
import org.example.rootmanage.module.ModuleEntity;
import org.example.rootmanage.role.Role;

@Getter
@Setter
@Entity
@Table(name = "role_module_permission")
public class RoleModulePermission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id")
    private ModuleEntity module;

    @Column(nullable = false)
    private Boolean canRead = true;

    @Column(nullable = false)
    private Boolean canAdd = false;

    @Column(nullable = false)
    private Boolean canUpdate = false;

    @Column(nullable = false)
    private Boolean canSee = true;
}

