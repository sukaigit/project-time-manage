package com.ptm.controller;

import com.ptm.service.ApprovalService;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @GetMapping
    public ResponseResult<List<Map<String, Object>>> list() {
        return approvalService.list();
    }

    @PostMapping("/{id}/approve")
    public ResponseResult<Void> approve(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseResult.unauthorized();
        }
        return approvalService.approve(id, userId);
    }

    @PostMapping("/{id}/reject")
    public ResponseResult<Void> reject(@PathVariable Long id,
                                       @RequestBody Map<String, String> params,
                                       HttpSession session) {
        String reason = params.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseResult.error(400, "驳回原因不能为空");
        }
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseResult.unauthorized();
        }
        return approvalService.reject(id, userId, reason.trim());
    }
}
