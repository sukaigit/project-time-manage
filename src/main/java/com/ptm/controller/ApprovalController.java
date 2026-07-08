package com.ptm.controller;

import com.ptm.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class ApprovalController {

    @GetMapping
    public ResponseResult<List<Map<String, Object>>> list(@RequestParam(required = false) Integer status,
                                                          @RequestParam(required = false) Long projectId,
                                                          @RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size) {
        // TODO: 实现分页查询审批列表
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1L);
        item.put("timeEntryId", 1L);
        item.put("userId", 1L);
        item.put("userName", "张三");
        item.put("hours", 8.0);
        item.put("workDate", "2024-01-15");
        item.put("status", 1); // 待审批
        list.add(item);
        return ResponseResult.success(list);
    }

    @PostMapping("/{id}/approve")
    public ResponseResult<Void> approve(@PathVariable Long id, HttpSession session) {
        // TODO: 实现审批通过逻辑
        return ResponseResult.success(null);
    }

    @PostMapping("/{id}/reject")
    public ResponseResult<Void> reject(@PathVariable Long id,
                                       @RequestBody Map<String, String> params) {
        String reason = params.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseResult.error(400, "驳回原因不能为空");
        }
        // TODO: 实现驳回逻辑
        return ResponseResult.success(null);
    }
}
