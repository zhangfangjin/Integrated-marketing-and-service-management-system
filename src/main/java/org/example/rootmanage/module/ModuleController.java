package org.example.rootmanage.module;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.module.dto.ModuleRequest;
import org.example.rootmanage.module.dto.ModuleTreeNode;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping
    public List<ModuleEntity> list() {
        return moduleService.findAll();
    }

    @GetMapping("/tree")
    public List<ModuleTreeNode> tree() {
        return moduleService.tree();
    }

    @PostMapping
    public ModuleEntity create(@RequestBody ModuleRequest request) {
        return moduleService.create(request);
    }

    @PutMapping("/{id}")
    public ModuleEntity update(@PathVariable UUID id, @RequestBody ModuleRequest request) {
        return moduleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        moduleService.delete(id);
    }
}

