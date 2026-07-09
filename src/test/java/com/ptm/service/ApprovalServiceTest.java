package com.ptm.service;

import com.ptm.entity.ApprovalLog;
import com.ptm.entity.Role;
import com.ptm.entity.TimeEntry;
import com.ptm.entity.User;
import com.ptm.entity.UserRole;
import com.ptm.mapper.*;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {

    @Mock
    private TimeEntryMapper timeEntryMapper;

    @Mock
    private ApprovalLogMapper approvalLogMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ApprovalService approvalService;

    private User currentUser;
    private Role deptManagerRole;
    private Role projectManagerRole;
    private Role employeeRole;
    private Role adminRole;
    private TimeEntry pendingEntry;
    private TimeEntry approvedEntry;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("manager");
        currentUser.setName("部门经理");
        currentUser.setStatus(1);

        deptManagerRole = new Role();
        deptManagerRole.setId(1L);
        deptManagerRole.setName("部门经理");
        deptManagerRole.setCode("dept_manager");
        deptManagerRole.setStatus(1);

        projectManagerRole = new Role();
        projectManagerRole.setId(2L);
        projectManagerRole.setName("项目经理");
        projectManagerRole.setCode("project_manager");
        projectManagerRole.setStatus(1);

        employeeRole = new Role();
        employeeRole.setId(3L);
        employeeRole.setName("员工");
        employeeRole.setCode("employee");
        employeeRole.setStatus(1);

        adminRole = new Role();
        adminRole.setId(4L);
        adminRole.setName("管理员");
        adminRole.setCode("admin");
        adminRole.setStatus(1);

        pendingEntry = new TimeEntry();
        pendingEntry.setId(100L);
        pendingEntry.setUserId(2L);
        pendingEntry.setStatus(0);
        pendingEntry.setHours(8.0);
        pendingEntry.setContent("开发任务");

        approvedEntry = new TimeEntry();
        approvedEntry.setId(200L);
        approvedEntry.setUserId(3L);
        approvedEntry.setStatus(1);
        approvedEntry.setHours(4.0);
        approvedEntry.setContent("已完成");
    }

    @Nested
    @DisplayName("待审批列表查询测试")
    class ListTest {

        @Test
        @DisplayName("部门经理 - 能看到项目经理提交的待审批记录")
        void listAsDeptManager_shouldSeeProjectManagerEntries() {
            when(session.getAttribute("user")).thenReturn(currentUser);
            when(session.getAttribute("roles")).thenReturn(Arrays.asList(deptManagerRole));

            // One pending entry submitted by a project manager
            Map<String, Object> entry1 = new HashMap<>();
            entry1.put("id", 100L);
            entry1.put("user_id", 2L);
            entry1.put("status", 0);

            when(timeEntryMapper.list(0, 999999, null, null, null, null, null, 0))
                    .thenReturn(Arrays.asList(entry1));

            // Submitter (userId=2) has project_manager role
            UserRole ur = new UserRole();
            ur.setId(1L);
            ur.setUserId(2L);
            ur.setRoleId(2L);
            when(userRoleMapper.findByUserId(2L)).thenReturn(Arrays.asList(ur));
            when(roleMapper.findById(2L)).thenReturn(projectManagerRole);

            ResponseResult<List<Map<String, Object>>> result = approvalService.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("部门经理 - 过滤掉非项目经理提交的记录")
        void listAsDeptManager_shouldFilterNonProjectManager() {
            when(session.getAttribute("user")).thenReturn(currentUser);
            when(session.getAttribute("roles")).thenReturn(Arrays.asList(deptManagerRole));

            // Entry submitted by an employee (not project_manager)
            Map<String, Object> entry1 = new HashMap<>();
            entry1.put("id", 101L);
            entry1.put("user_id", 3L);
            entry1.put("status", 0);

            when(timeEntryMapper.list(0, 999999, null, null, null, null, null, 0))
                    .thenReturn(Arrays.asList(entry1));

            UserRole ur = new UserRole();
            ur.setId(2L);
            ur.setUserId(3L);
            ur.setRoleId(3L);
            when(userRoleMapper.findByUserId(3L)).thenReturn(Arrays.asList(ur));
            when(roleMapper.findById(3L)).thenReturn(employeeRole);

            ResponseResult<List<Map<String, Object>>> result = approvalService.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(0, result.getData().size());
        }

        @Test
        @DisplayName("项目经理 - 能看到普通员工提交的待审批记录")
        void listAsProjectManager_shouldSeeEmployeeEntries() {
            when(session.getAttribute("user")).thenReturn(currentUser);
            when(session.getAttribute("roles")).thenReturn(Arrays.asList(projectManagerRole));

            Map<String, Object> entry1 = new HashMap<>();
            entry1.put("id", 102L);
            entry1.put("user_id", 3L);
            entry1.put("status", 0);

            when(timeEntryMapper.list(0, 999999, null, null, null, null, null, 0))
                    .thenReturn(Arrays.asList(entry1));

            UserRole ur = new UserRole();
            ur.setId(3L);
            ur.setUserId(3L);
            ur.setRoleId(3L);
            when(userRoleMapper.findByUserId(3L)).thenReturn(Arrays.asList(ur));
            when(roleMapper.findById(3L)).thenReturn(employeeRole);

            ResponseResult<List<Map<String, Object>>> result = approvalService.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("项目经理 - 过滤掉admin/dept_manager/project_manager提交的记录")
        void listAsProjectManager_shouldFilterHighLevelRoles() {
            when(session.getAttribute("user")).thenReturn(currentUser);
            when(session.getAttribute("roles")).thenReturn(Arrays.asList(projectManagerRole));

            // Three entries: submitted by admin, dept_manager, and employee
            Map<String, Object> entryAdmin = new HashMap<>();
            entryAdmin.put("id", 201L);
            entryAdmin.put("user_id", 10L);
            entryAdmin.put("status", 0);

            Map<String, Object> entryDeptMgr = new HashMap<>();
            entryDeptMgr.put("id", 202L);
            entryDeptMgr.put("user_id", 11L);
            entryDeptMgr.put("status", 0);

            Map<String, Object> entryEmp = new HashMap<>();
            entryEmp.put("id", 203L);
            entryEmp.put("user_id", 12L);
            entryEmp.put("status", 0);

            when(timeEntryMapper.list(0, 999999, null, null, null, null, null, 0))
                    .thenReturn(Arrays.asList(entryAdmin, entryDeptMgr, entryEmp));

            // admin (userId=10)
            UserRole ur10 = new UserRole();
            ur10.setId(10L);
            ur10.setUserId(10L);
            ur10.setRoleId(4L);
            when(userRoleMapper.findByUserId(10L)).thenReturn(Arrays.asList(ur10));
            when(roleMapper.findById(4L)).thenReturn(adminRole);

            // dept_manager (userId=11)
            UserRole ur11 = new UserRole();
            ur11.setId(11L);
            ur11.setUserId(11L);
            ur11.setRoleId(1L);
            when(userRoleMapper.findByUserId(11L)).thenReturn(Arrays.asList(ur11));
            when(roleMapper.findById(1L)).thenReturn(deptManagerRole);

            // employee (userId=12)
            UserRole ur12 = new UserRole();
            ur12.setId(12L);
            ur12.setUserId(12L);
            ur12.setRoleId(3L);
            when(userRoleMapper.findByUserId(12L)).thenReturn(Arrays.asList(ur12));
            when(roleMapper.findById(3L)).thenReturn(employeeRole);

            ResponseResult<List<Map<String, Object>>> result = approvalService.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals(203L, ((Number) result.getData().get(0).get("id")).longValue());
        }

        @Test
        @DisplayName("未登录 - 返回401未授权")
        void listWithoutLogin_shouldReturnUnauthorized() {
            when(session.getAttribute("user")).thenReturn(null);

            ResponseResult<List<Map<String, Object>>> result = approvalService.list();

            assertEquals(401, result.getCode());
            assertEquals("未登录或会话超时", result.getMsg());
            verify(timeEntryMapper, never()).list(anyInt(), anyInt(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("会话无角色信息 - 返回401未授权")
        void listWithoutRoles_shouldReturnUnauthorized() {
            when(session.getAttribute("user")).thenReturn(currentUser);
            when(session.getAttribute("roles")).thenReturn(null);

            ResponseResult<List<Map<String, Object>>> result = approvalService.list();

            assertEquals(401, result.getCode());
            assertEquals("未登录或会话超时", result.getMsg());

            when(session.getAttribute("roles")).thenReturn(new ArrayList<>());
            result = approvalService.list();
            assertEquals(401, result.getCode());
        }

        @Test
        @DisplayName("提交者没有角色 - 跳过该记录")
        void list_submitterHasNoRoles_shouldSkip() {
            when(session.getAttribute("user")).thenReturn(currentUser);
            when(session.getAttribute("roles")).thenReturn(Arrays.asList(deptManagerRole));

            Map<String, Object> entry1 = new HashMap<>();
            entry1.put("id", 103L);
            entry1.put("user_id", 99L);
            entry1.put("status", 0);

            when(timeEntryMapper.list(0, 999999, null, null, null, null, null, 0))
                    .thenReturn(Arrays.asList(entry1));

            when(userRoleMapper.findByUserId(99L)).thenReturn(null);

            ResponseResult<List<Map<String, Object>>> result = approvalService.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(0, result.getData().size());
        }
    }

    @Nested
    @DisplayName("审批通过测试")
    class ApproveTest {

        @Test
        @DisplayName("审批通过成功 - 状态更新为1，写入审批日志")
        void approveSuccess() {
            when(timeEntryMapper.findById(100L)).thenReturn(pendingEntry);
            when(timeEntryMapper.updateStatus(100L, 1)).thenReturn(1);

            ResponseResult<Void> result = approvalService.approve(100L, 1L);

            assertEquals(200, result.getCode());
            verify(timeEntryMapper).updateStatus(100L, 1);
            verify(approvalLogMapper).insert(any(ApprovalLog.class));
        }

        @Test
        @DisplayName("工时记录不存在 - 返回404")
        void approve_notFound() {
            when(timeEntryMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = approvalService.approve(999L, 1L);

            assertEquals(404, result.getCode());
            assertEquals("工时记录不存在", result.getMsg());
            verify(timeEntryMapper, never()).updateStatus(anyLong(), anyInt());
            verify(approvalLogMapper, never()).insert(any());
        }

        @Test
        @DisplayName("工时记录不是待审批状态 - 返回400")
        void approve_notPendingStatus() {
            when(timeEntryMapper.findById(200L)).thenReturn(approvedEntry);

            ResponseResult<Void> result = approvalService.approve(200L, 1L);

            assertEquals(400, result.getCode());
            assertEquals("该工时记录不是待审批状态", result.getMsg());
            verify(timeEntryMapper, never()).updateStatus(anyLong(), anyInt());
            verify(approvalLogMapper, never()).insert(any());
        }

        @Test
        @DisplayName("审批通过 - 审批日志参数正确")
        void approve_verifyLogParams() {
            when(timeEntryMapper.findById(100L)).thenReturn(pendingEntry);
            when(timeEntryMapper.updateStatus(100L, 1)).thenReturn(1);

            approvalService.approve(100L, 2L);

            verify(approvalLogMapper).insert(argThat(log ->
                    log.getTimeEntryId().equals(100L)
                            && log.getApproverId().equals(2L)
                            && log.getAction() == 1
            ));
        }
    }

    @Nested
    @DisplayName("驳回测试")
    class RejectTest {

        @Test
        @DisplayName("驳回成功 - 状态更新为2，写入审批日志")
        void rejectSuccess() {
            when(timeEntryMapper.findById(100L)).thenReturn(pendingEntry);
            when(timeEntryMapper.updateStatus(100L, 2)).thenReturn(1);

            ResponseResult<Void> result = approvalService.reject(100L, 1L, "工时填写不合理");

            assertEquals(200, result.getCode());
            verify(timeEntryMapper).updateStatus(100L, 2);
            verify(approvalLogMapper).insert(any(ApprovalLog.class));
        }

        @Test
        @DisplayName("工时记录不存在 - 返回404")
        void reject_notFound() {
            when(timeEntryMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = approvalService.reject(999L, 1L, "理由");

            assertEquals(404, result.getCode());
            assertEquals("工时记录不存在", result.getMsg());
            verify(timeEntryMapper, never()).updateStatus(anyLong(), anyInt());
            verify(approvalLogMapper, never()).insert(any());
        }

        @Test
        @DisplayName("工时记录不是待审批状态 - 返回400")
        void reject_notPendingStatus() {
            when(timeEntryMapper.findById(200L)).thenReturn(approvedEntry);

            ResponseResult<Void> result = approvalService.reject(200L, 1L, "理由");

            assertEquals(400, result.getCode());
            assertEquals("该工时记录不是待审批状态", result.getMsg());
            verify(timeEntryMapper, never()).updateStatus(anyLong(), anyInt());
            verify(approvalLogMapper, never()).insert(any());
        }

        @Test
        @DisplayName("驳回 - 审批日志包含驳回原因")
        void reject_verifyLogContainsReason() {
            when(timeEntryMapper.findById(100L)).thenReturn(pendingEntry);
            when(timeEntryMapper.updateStatus(100L, 2)).thenReturn(1);

            approvalService.reject(100L, 3L, "内容不完整");

            verify(approvalLogMapper).insert(argThat(log ->
                    log.getTimeEntryId().equals(100L)
                            && log.getApproverId().equals(3L)
                            && log.getAction() == 2
                            && "内容不完整".equals(log.getReason())
            ));
        }
    }
}
