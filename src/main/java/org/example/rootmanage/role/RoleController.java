package org.example.rootmanage.role;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.role.dto.RoleRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<Role> list() {
        return roleService.findAll();
    }

    @PostMapping
    public Role create(@RequestBody RoleRequest request) {
        return roleService.create(request);
    }

    @PutMapping("/{id}")
    public Role update(@PathVariable UUID id, @RequestBody RoleRequest request) {
        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        roleService.delete(id);
    }
}

