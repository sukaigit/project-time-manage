package com.ptm.controller;

import com.ptm.entity.User;
import com.ptm.service.UserService;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseResult<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Integer status) {
        return userService.list(page, size, username, name, roleId, status);
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseResult.error(400, "用户名不能为空");
        }
        return userService.add(user);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        return userService.delete(id);
    }

    @PostMapping("/{id}/reset-password")
    public ResponseResult<Void> resetPassword(@PathVariable Long id) {
        return userService.resetPassword(id);
    }
}
