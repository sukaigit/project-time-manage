package com.ptm.service;

import com.ptm.entity.ApprovalLog;
import com.ptm.entity.Role;
import com.ptm.entity.TimeEntry;
import com.ptm.entity.User;
import com.ptm.mapper.*;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class ApprovalService {

    @Autowired
    private TimeEntryMapper timeEntryMapper;

    @Autowired
    private ApprovalLogMapper approvalLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private HttpSession session;

    /**
     * 按角色过滤待审批列表
     * 部门经理 → 只能看到项目经理提交的
     * 项目经理 → 只能看到普通员工提交的
     */
    public ResponseResult<List<Map<String, Object>>> list() {
        User currentUser = (User) session.getAttribute("user");
        List<Role> roles = (List<Role>) session.getAttribute("roles");

        if (currentUser == null || roles == null || roles.isEmpty()) {
            return ResponseResult.unauthorized();
        }

        boolean isDeptManager = roles.stream().anyMatch(r -> "dept_manager".equals(r.getCode()));
        boolean isProjectManager = roles.stream().anyMatch(r -> "project_manager".equals(r.getCode()));

        List<Map<String, Object>> allPending = timeEntryMapper.list(0, 999999, null, null, null, null, null, 0);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> entry : allPending) {
            Long submitterId = ((Number) entry.get("user_id")).longValue();
            List<Role> submitterRoles = getRolesByUserId(submitterId);

            boolean canApprove = false;
            for (Role sr : submitterRoles) {
                if (isDeptManager && "project_manager".equals(sr.getCode())) {
                    canApprove = true;
                    break;
                }
                if (isProjectManager && !"admin".equals(sr.getCode())
                        && !"dept_manager".equals(sr.getCode())
                        && !"project_manager".equals(sr.getCode())) {
                    canApprove = true;
                    break;
                }
            }

            if (canApprove) {
                result.add(entry);
            }
        }

        return ResponseResult.success(result);
    }

    /**
     * 审批通过
     */
    @Transactional
    public ResponseResult<Void> approve(Long id, Long approverId) {
        TimeEntry entry = timeEntryMapper.findById(id);
        if (entry == null) {
            return ResponseResult.error(404, "工时记录不存在");
        }
        if (entry.getStatus() != 0) {
            return ResponseResult.error(400, "该工时记录不是待审批状态");
        }
        timeEntryMapper.updateStatus(id, 1);
        ApprovalLog log = new ApprovalLog();
        log.setTimeEntryId(id);
        log.setApproverId(approverId);
        log.setAction(1);
        approvalLogMapper.insert(log);
        return ResponseResult.success(null);
    }

    /**
     * 驳回
     */
    @Transactional
    public ResponseResult<Void> reject(Long id, Long approverId, String reason) {
        TimeEntry entry = timeEntryMapper.findById(id);
        if (entry == null) {
            return ResponseResult.error(404, "工时记录不存在");
        }
        if (entry.getStatus() != 0) {
            return ResponseResult.error(400, "该工时记录不是待审批状态");
        }
        timeEntryMapper.updateStatus(id, 2);
        ApprovalLog log = new ApprovalLog();
        log.setTimeEntryId(id);
        log.setApproverId(approverId);
        log.setAction(2);
        log.setReason(reason);
        approvalLogMapper.insert(log);
        return ResponseResult.success(null);
    }

    private List<Role> getRolesByUserId(Long userId) {
        List<com.ptm.entity.UserRole> urs = userRoleMapper.findByUserId(userId);
        if (urs == null || urs.isEmpty()) {
            return Collections.emptyList();
        }
        List<Role> roles = new ArrayList<>();
        for (com.ptm.entity.UserRole ur : urs) {
            Role role = roleMapper.findById(ur.getRoleId());
            if (role != null) {
                roles.add(role);
            }
        }
        return roles;
    }
}
