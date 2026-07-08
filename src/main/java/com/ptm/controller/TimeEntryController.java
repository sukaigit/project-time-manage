package com.ptm.controller;

import com.ptm.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/time-entries")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class TimeEntryController {

    @GetMapping
    public ResponseResult<List<Map<String, Object>>> list(@RequestParam(required = false) Long userId,
                                                          @RequestParam(required = false) Long projectId,
                                                          @RequestParam(required = false) String workDate,
                                                          @RequestParam(required = false) Integer status,
                                                          @RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size) {
        // TODO: 实现分页查询工时列表
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> entry = new HashMap<>();
        entry.put("id", 1L);
        entry.put("userId", 1L);
        entry.put("projectId", 1L);
        entry.put("taskId", 1L);
        entry.put("workDate", "2024-01-15");
        entry.put("hours", 8.0);
        entry.put("content", "完成模块开发");
        entry.put("status", 0); // 0:待提交 1:已提交 2:已驳回 3:已通过
        list.add(entry);
        return ResponseResult.success(list);
    }

    @PostMapping
    public ResponseResult<Void> create(@RequestBody Map<String, Object> params) {
        // TODO: 实现创建工时记录
        return ResponseResult.success(null);
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> update(@PathVariable Long id,
                                       @RequestBody Map<String, Object> params) {
        // TODO: 实现更新工时记录（仅已驳回状态可编辑）
        Integer status = (Integer) params.get("status");
        if (status != null && status != 2) {
            return ResponseResult.error(400, "仅已驳回的工时记录可编辑");
        }
        return ResponseResult.success(null);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        // TODO: 实现删除工时记录
        return ResponseResult.success(null);
    }
}
