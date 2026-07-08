package com.ptm.controller;

import com.ptm.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class UserController {

    @GetMapping
    public ResponseResult<List<Map<String, Object>>> list(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String dept,
                                                          @RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size) {
        // TODO: 实现分页查询用户列表
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1L);
        user.put("username", "admin");
        user.put("name", "管理员");
        user.put("dept", "技术部");
        user.put("status", 1);
        list.add(user);
        return ResponseResult.success(list);
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody Map<String, Object> params) {
        // TODO: 实现创建用户
        return ResponseResult.success(null);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id,
                                       @RequestBody Map<String, Object> params) {
        // TODO: 实现更新用户
        return ResponseResult.success(null);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        // TODO: 实现删除用户
        return ResponseResult.success(null);
    }

    @PostMapping("/{id}/reset-password")
    public ResponseResult<Void> resetPassword(@PathVariable Long id) {
        // TODO: 实现重置密码
        return ResponseResult.success(null);
    }
}
