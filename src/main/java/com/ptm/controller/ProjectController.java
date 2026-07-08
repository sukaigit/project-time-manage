package com.ptm.controller;

import com.ptm.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class ProjectController {

    @GetMapping
    public ResponseResult<List<Map<String, Object>>> list(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size) {
        // TODO: 实现分页查询项目列表
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1L);
        item.put("name", "示例项目");
        item.put("code", "P001");
        item.put("status", 1);
        item.put("dept", "技术部");
        list.add(item);
        return ResponseResult.success(list);
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody Map<String, Object> params) {
        // TODO: 实现创建项目
        return ResponseResult.success(null);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id,
                                       @RequestBody Map<String, Object> params) {
        // TODO: 实现更新项目
        return ResponseResult.success(null);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        // TODO: 实现删除项目
        return ResponseResult.success(null);
    }

    @GetMapping("/{id}/members")
    public ResponseResult<List<Map<String, Object>>> getMembers(@PathVariable Long id) {
        // TODO: 实现查询项目成员列表
        List<Map<String, Object>> members = new ArrayList<>();
        Map<String, Object> member = new HashMap<>();
        member.put("userId", 1L);
        member.put("name", "张三");
        members.add(member);
        return ResponseResult.success(members);
    }

    @PutMapping("/{id}/members")
    public ResponseResult<Void> updateMembers(@PathVariable Long id,
                                              @RequestBody List<Long> userIds) {
        // TODO: 实现更新项目成员
        return ResponseResult.success(null);
    }
}
