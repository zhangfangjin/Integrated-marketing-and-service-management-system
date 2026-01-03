package org.example.rootmanage.module;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.module.dto.ModuleRequest;
import org.example.rootmanage.module.dto.ModuleTreeNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public List<ModuleEntity> findAll() {
        return moduleRepository.findAllByOrderByOrderNoAsc();
    }

    @Transactional
    public ModuleEntity create(ModuleRequest request) {
        ModuleEntity module = new ModuleEntity();
        apply(module, request);
        if (request.getParentId() != null) {
            ModuleEntity parent = moduleRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("父模块不存在"));
            module.setParent(parent);
        }
        return moduleRepository.save(module);
    }

    @Transactional
    public ModuleEntity update(UUID id, ModuleRequest request) {
        ModuleEntity module = moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模块不存在"));
        apply(module, request);
        if (request.getParentId() != null) {
            ModuleEntity parent = moduleRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("父模块不存在"));
            module.setParent(parent);
        } else {
            module.setParent(null);
        }
        return moduleRepository.save(module);
    }

    private void apply(ModuleEntity module, ModuleRequest request) {
        module.setZhName(request.getZhName());
        module.setEnName(request.getEnName());
        module.setLevel(request.getLevel());
        module.setOrderNo(request.getOrderNo());
        module.setPath(request.getPath());
        module.setIcon(request.getIcon());
        module.setGroupCode(request.getGroupCode());
        module.setPermissionKey(request.getPermissionKey());
        module.setParentNode(Boolean.TRUE.equals(request.getParentNode()));
        module.setExpanded(Boolean.TRUE.equals(request.getExpanded()));
        module.setVisible(Boolean.TRUE.equals(request.getVisible()));
    }

    @Transactional
    public void delete(UUID id) {
        moduleRepository.deleteById(id);
    }

    public List<ModuleTreeNode> tree() {
        List<ModuleEntity> modules = moduleRepository.findAll();
        modules.sort(Comparator.comparing(ModuleEntity::getOrderNo));

        Map<UUID, ModuleTreeNode> nodeMap = modules.stream().collect(Collectors.toMap(
                ModuleEntity::getId,
                this::toNode
        ));

        List<ModuleTreeNode> roots = nodeMap.values().stream()
                .filter(n -> {
                    ModuleEntity entity = modules.stream()
                            .filter(m -> m.getId().equals(n.getId()))
                            .findFirst()
                            .orElse(null);
                    return entity != null && entity.getParent() == null;
                })
                .sorted(Comparator.comparing(ModuleTreeNode::getOrderNo))
                .collect(Collectors.toList());

        for (ModuleEntity module : modules) {
            if (module.getParent() != null) {
                ModuleTreeNode parentNode = nodeMap.get(module.getParent().getId());
                if (parentNode != null) {
                    ModuleTreeNode child = nodeMap.get(module.getId());
                    parentNode.getChildren().add(child);
                    parentNode.getChildren().sort(Comparator.comparing(ModuleTreeNode::getOrderNo));
                }
            }
        }
        return roots;
    }

    private ModuleTreeNode toNode(ModuleEntity m) {
        ModuleTreeNode node = new ModuleTreeNode();
        node.setId(m.getId());
        node.setZhName(m.getZhName());
        node.setEnName(m.getEnName());
        node.setLevel(m.getLevel());
        node.setOrderNo(m.getOrderNo());
        node.setPath(m.getPath());
        node.setIcon(m.getIcon());
        node.setGroupCode(m.getGroupCode());
        node.setPermissionKey(m.getPermissionKey());
        node.setParentNode(Boolean.TRUE.equals(m.getParentNode()));
        node.setExpanded(Boolean.TRUE.equals(m.getExpanded()));
        node.setVisible(Boolean.TRUE.equals(m.getVisible()));
        return node;
    }
}

