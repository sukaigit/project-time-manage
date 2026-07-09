package com.ptm.service;

import com.ptm.entity.Role;
import com.ptm.entity.TimeEntry;
import com.ptm.entity.User;
import com.ptm.mapper.TimeEntryMapper;
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
class TimeEntryServiceTest {

    @Mock
    private TimeEntryMapper timeEntryMapper;

    @Mock
    private HttpSession session;

    @InjectMocks
    private TimeEntryService timeEntryService;

    private User adminUser;
    private User normalUser;
    private Role adminRole;
    private Role userRole;
    private TimeEntry normalEntry;
    private TimeEntry rejectedEntry;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setName("管理员");

        normalUser = new User();
        normalUser.setId(2L);
        normalUser.setUsername("zhangsan");
        normalUser.setName("张三");

        adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setCode("admin");
        adminRole.setName("管理员");

        userRole = new Role();
        userRole.setId(2L);
        userRole.setCode("user");
        userRole.setName("普通用户");

        normalEntry = new TimeEntry();
        normalEntry.setId(1L);
        normalEntry.setUserId(2L);
        normalEntry.setProjectId(1L);
        normalEntry.setTaskId(1L);
        normalEntry.setWorkDate("2024-06-01");
        normalEntry.setHours(8.0);
        normalEntry.setContent("开发任务");
        normalEntry.setStatus(0);

        rejectedEntry = new TimeEntry();
        rejectedEntry.setId(2L);
        rejectedEntry.setUserId(2L);
        rejectedEntry.setProjectId(1L);
        rejectedEntry.setTaskId(1L);
        rejectedEntry.setWorkDate("2024-06-02");
        rejectedEntry.setHours(4.0);
        rejectedEntry.setContent("驳回的工时");
        rejectedEntry.setStatus(2);
    }

    @Nested
    @DisplayName("列表查询测试")
    class ListTest {

        @Test
        @DisplayName("管理员查询列表 - 看到所有数据")
        void listAsAdmin() {
            when(session.getAttribute("user")).thenReturn(adminUser);
            when(session.getAttribute("roles")).thenReturn(Collections.singletonList(adminRole));

            Map<String, Object> mockEntry = new HashMap<>();
            mockEntry.put("id", 1L);
            mockEntry.put("hours", 8.0);
            List<Map<String, Object>> mockList = Collections.singletonList(mockEntry);

            when(timeEntryMapper.list(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(mockList);
            when(timeEntryMapper.count(any(), any(), any(), any(), any(), any()))
                    .thenReturn(1L);

            ResponseResult<Map<String, Object>> result = timeEntryService.list(1, 10, null, 1L, 1L, null, null, null);

            assertEquals(200, result.getCode());
            Map<String, Object> data = result.getData();
            assertEquals(1L, data.get("total"));
            assertEquals(1, data.get("page"));
            assertEquals(10, data.get("size"));

            verify(timeEntryMapper).list(0, 10, null, 1L, 1L, null, null, null);
        }

        @Test
        @DisplayName("普通用户查询列表 - 只能看到自己的数据")
        void listAsNormalUser() {
            when(session.getAttribute("user")).thenReturn(normalUser);
            when(session.getAttribute("roles")).thenReturn(Collections.singletonList(userRole));

            Map<String, Object> mockEntry = new HashMap<>();
            mockEntry.put("id", 1L);
            mockEntry.put("hours", 8.0);
            List<Map<String, Object>> mockList = Collections.singletonList(mockEntry);

            when(timeEntryMapper.list(anyInt(), anyInt(), eq(2L), any(), any(), any(), any(), any()))
                    .thenReturn(mockList);
            when(timeEntryMapper.count(eq(2L), any(), any(), any(), any(), any()))
                    .thenReturn(1L);

            ResponseResult<Map<String, Object>> result = timeEntryService.list(1, 10, null, 1L, 1L, null, null, null);

            assertEquals(200, result.getCode());
            verify(timeEntryMapper).list(0, 10, 2L, 1L, 1L, null, null, null);
        }

        @Test
        @DisplayName("用户未登录时也能查询（会话可能超时）")
        void listWithoutSession() {
            when(session.getAttribute("user")).thenReturn(null);
            when(session.getAttribute("roles")).thenReturn(null);

            when(timeEntryMapper.list(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            when(timeEntryMapper.count(any(), any(), any(), any(), any(), any()))
                    .thenReturn(0L);

            ResponseResult<Map<String, Object>> result = timeEntryService.list(1, 10, null, null, null, null, null, null);

            assertEquals(200, result.getCode());
            verify(timeEntryMapper).list(0, 10, null, null, null, null, null, null);
        }

        @Test
        @DisplayName("列表为空时返回空结果")
        void listEmpty() {
            when(session.getAttribute("user")).thenReturn(adminUser);
            when(session.getAttribute("roles")).thenReturn(Collections.singletonList(adminRole));

            when(timeEntryMapper.list(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            when(timeEntryMapper.count(any(), any(), any(), any(), any(), any()))
                    .thenReturn(0L);

            ResponseResult<Map<String, Object>> result = timeEntryService.list(1, 10, null, null, null, null, null, null);

            assertEquals(200, result.getCode());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.getData().get("list");
            assertTrue(list.isEmpty());
        }
    }

    @Nested
    @DisplayName("根据ID查询测试")
    class GetByIdTest {

        @Test
        @DisplayName("查询工时记录成功")
        void getByIdSuccess() {
            when(timeEntryMapper.findById(1L)).thenReturn(normalEntry);

            ResponseResult<TimeEntry> result = timeEntryService.getById(1L);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(8.0, result.getData().getHours());
        }

        @Test
        @DisplayName("工时记录不存在 - 返回404")
        void getByIdNotFound() {
            when(timeEntryMapper.findById(999L)).thenReturn(null);

            ResponseResult<TimeEntry> result = timeEntryService.getById(999L);

            assertEquals(404, result.getCode());
            assertEquals("工时记录不存在", result.getMsg());
            assertNull(result.getData());
        }
    }

    @Nested
    @DisplayName("新增工时测试")
    class AddTest {

        @Test
        @DisplayName("新增工时成功 - 用户登录自动设置userId")
        void addSuccessWithLoggedInUser() {
            when(session.getAttribute("user")).thenReturn(normalUser);

            TimeEntry newEntry = new TimeEntry();
            newEntry.setProjectId(1L);
            newEntry.setTaskId(1L);
            newEntry.setWorkDate("2024-06-10");
            newEntry.setHours(6.0);
            newEntry.setContent("完成模块开发");

            when(timeEntryMapper.insert(any(TimeEntry.class))).thenReturn(1);

            ResponseResult<Void> result = timeEntryService.add(newEntry);

            assertEquals(200, result.getCode());
            assertEquals(2L, newEntry.getUserId());
            assertEquals(0, newEntry.getStatus());
            verify(timeEntryMapper).insert(newEntry);
        }

        @Test
        @DisplayName("新增工时成功 - 用户未登录时userId为null")
        void addSuccessWithoutLoggedInUser() {
            when(session.getAttribute("user")).thenReturn(null);

            TimeEntry newEntry = new TimeEntry();
            newEntry.setProjectId(1L);
            newEntry.setTaskId(1L);
            newEntry.setWorkDate("2024-06-10");
            newEntry.setHours(6.0);
            newEntry.setContent("完成模块开发");

            when(timeEntryMapper.insert(any(TimeEntry.class))).thenReturn(1);

            ResponseResult<Void> result = timeEntryService.add(newEntry);

            assertEquals(200, result.getCode());
            assertNull(newEntry.getUserId());
            assertEquals(0, newEntry.getStatus());
        }

        @Test
        @DisplayName("新增工时成功 - 保留自定义状态")
        void addWithCustomStatus() {
            when(session.getAttribute("user")).thenReturn(normalUser);

            TimeEntry newEntry = new TimeEntry();
            newEntry.setStatus(1);

            when(timeEntryMapper.insert(any(TimeEntry.class))).thenReturn(1);

            ResponseResult<Void> result = timeEntryService.add(newEntry);

            assertEquals(200, result.getCode());
            assertEquals(1, newEntry.getStatus());
        }
    }

    @Nested
    @DisplayName("更新工时测试")
    class UpdateTest {

        @Test
        @DisplayName("更新已驳回的工时记录成功")
        void updateRejectedEntrySuccess() {
            TimeEntry updateEntry = new TimeEntry();
            updateEntry.setId(2L);
            updateEntry.setHours(6.0);
            updateEntry.setContent("修改后的内容");

            when(timeEntryMapper.findById(2L)).thenReturn(rejectedEntry);
            when(timeEntryMapper.update(any(TimeEntry.class))).thenReturn(1);

            ResponseResult<Void> result = timeEntryService.update(updateEntry);

            assertEquals(200, result.getCode());
            assertEquals(0, updateEntry.getStatus());
            verify(timeEntryMapper).update(updateEntry);
        }

        @Test
        @DisplayName("更新非驳回状态的工时记录 - 返回400")
        void updateNonRejectedEntry() {
            TimeEntry updateEntry = new TimeEntry();
            updateEntry.setId(1L);

            when(timeEntryMapper.findById(1L)).thenReturn(normalEntry);

            ResponseResult<Void> result = timeEntryService.update(updateEntry);

            assertEquals(400, result.getCode());
            assertEquals("只有已驳回的工时记录才能修改", result.getMsg());
            verify(timeEntryMapper, never()).update(any(TimeEntry.class));
        }

        @Test
        @DisplayName("更新不存在的工时记录 - 返回404")
        void updateNotFound() {
            TimeEntry updateEntry = new TimeEntry();
            updateEntry.setId(999L);

            when(timeEntryMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = timeEntryService.update(updateEntry);

            assertEquals(404, result.getCode());
            assertEquals("工时记录不存在", result.getMsg());
            verify(timeEntryMapper, never()).update(any(TimeEntry.class));
        }
    }

    @Nested
    @DisplayName("删除工时测试")
    class DeleteTest {

        @Test
        @DisplayName("删除工时记录成功")
        void deleteSuccess() {
            when(timeEntryMapper.findById(1L)).thenReturn(normalEntry);
            when(timeEntryMapper.deleteById(1L)).thenReturn(1);

            ResponseResult<Void> result = timeEntryService.delete(1L);

            assertEquals(200, result.getCode());
            verify(timeEntryMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的工时记录 - 返回404")
        void deleteNotFound() {
            when(timeEntryMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = timeEntryService.delete(999L);

            assertEquals(404, result.getCode());
            assertEquals("工时记录不存在", result.getMsg());
            verify(timeEntryMapper, never()).deleteById(anyLong());
        }
    }
}
