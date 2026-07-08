package com.ptm.controller;

import com.ptm.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class TaskController {

    @GetMapping
    public ResponseResult<List<Map<String, Object>>> list(@RequestParam(required = false) Long projectId,
                                                          @RequestParam(required = false) String name,
                                                          @RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size) {
        // TODO: 实现分页查询任务列表
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> task = new HashMap<>();
        task.put("id", 1L);
        task.put("name", "示例任务");
        task.put("code", "T001");
        task.put("projectId", 1L);
        task.put("status", 1);
        list.add(task);
        return ResponseResult.success(list);
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody Map<String, Object> params) {
        // TODO: 实现创建任务
        return ResponseResult.success(null);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id,
                                       @RequestBody Map<String, Object> params) {
        // TODO: 实现更新任务
        return ResponseResult.success(null);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        // TODO: 实现删除任务
        return ResponseResult.success(null);
    }
}
