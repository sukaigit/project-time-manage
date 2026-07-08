package com.ptm.controller;

import com.ptm.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class RoleController {

    @GetMapping
    public ResponseResult<List<Map<String, Object>>> list(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size) {
        // TODO: 实现分页查询角色列表
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> role = new HashMap<>();
        role.put("id", 1L);
        role.put("name", "管理员");
        role.put("code", "ADMIN");
        role.put("status", 1);
        list.add(role);
        return ResponseResult.success(list);
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody Map<String, Object> params) {
        // TODO: 实现创建角色
        return ResponseResult.success(null);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id,
                                       @RequestBody Map<String, Object> params) {
        // TODO: 实现更新角色
        return ResponseResult.success(null);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        // TODO: 实现删除角色
        return ResponseResult.success(null);
    }

    @GetMapping("/{id}/permissions")
    public ResponseResult<List<Map<String, Object>>> getPermissions(@PathVariable Long id) {
        // TODO: 实现查询角色权限
        List<Map<String, Object>> permissions = new ArrayList<>();
        Map<String, Object> perm = new HashMap<>();
        perm.put("menuKey", "project");
        perm.put("actions", "view,edit,delete");
        permissions.add(perm);
        return ResponseResult.success(permissions);
    }

    @PutMapping("/{id}/permissions")
    public ResponseResult<Void> updatePermissions(@PathVariable Long id,
                                                  @RequestBody List<Map<String, Object>> permissions) {
        // TODO: 实现更新角色权限
        return ResponseResult.success(null);
    }
}
