package com.ptm.controller;

import com.ptm.entity.Role;
import com.ptm.service.RoleService;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseResult<List<Role>> list() {
        return roleService.list();
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody Role role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            return ResponseResult.error(400, "角色名称不能为空");
        }
        if (role.getCode() == null || role.getCode().trim().isEmpty()) {
            return ResponseResult.error(400, "角色编码不能为空");
        }
        return roleService.add(role);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return roleService.update(role);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        return roleService.delete(id);
    }

    @GetMapping("/{id}/permissions")
    public ResponseResult<Map<String, List<String>>> getPermissions(@PathVariable Long id) {
        return roleService.getPermissions(id);
    }

    @PutMapping("/{id}/permissions")
    public ResponseResult<Void> updatePermissions(@PathVariable Long id,
                                                  @RequestBody Map<String, List<String>> permissions) {
        return roleService.savePermissions(id, permissions);
    }
}
