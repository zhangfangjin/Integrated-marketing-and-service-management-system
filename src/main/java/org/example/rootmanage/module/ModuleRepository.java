package org.example.rootmanage.module;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<ModuleEntity, UUID> {

    List<ModuleEntity> findByParentIdOrderByOrderNoAsc(UUID parentId);

    List<ModuleEntity> findAllByOrderByOrderNoAsc();
    
    Optional<ModuleEntity> findByPath(String path);
}

