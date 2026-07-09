package com.ptm.service;

import com.ptm.entity.User;
import com.ptm.entity.UserRole;
import com.ptm.mapper.UserMapper;
import com.ptm.mapper.UserRoleMapper;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private UserService userService;

    private User normalUser;
    private UserRole userRole;

    @BeforeEach
    void setUp() {
        normalUser = new User();
        normalUser.setId(1L);
        normalUser.setUsername("admin");
        normalUser.setPassword("encodedPassword");
        normalUser.setName("管理员");
        normalUser.setDept("技术部");
        normalUser.setStatus(1);
        normalUser.setFirstLogin(1);

        userRole = new UserRole();
        userRole.setId(1L);
        userRole.setUserId(1L);
        userRole.setRoleId(2L);
    }

    @Nested
    @DisplayName("列表查询测试")
    class ListTest {

        @Test
        @DisplayName("分页查询成功 - 返回用户列表")
        void listSuccess() {
            List<User> userList = Collections.singletonList(normalUser);
            when(userMapper.list(anyInt(), anyInt(), any(), any(), any(), any()))
                    .thenReturn(userList);
            when(userMapper.count(any(), any(), any(), any()))
                    .thenReturn(1L);
            when(userRoleMapper.findByUserId(1L)).thenReturn(Collections.singletonList(userRole));

            ResponseResult<Map<String, Object>> result = userService.list(1, 10, "admin", null, null, null);

            assertNotNull(result);
            assertEquals(200, result.getCode());
            Map<String, Object> data = result.getData();
            assertNotNull(data);
            assertEquals(1L, data.get("total"));
            assertEquals(1, data.get("page"));
            assertEquals(10, data.get("size"));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals(1L, list.get(0).get("id"));
            assertEquals(2L, list.get(0).get("roleId"));

            verify(userMapper).list(0, 10, "admin", null, null, null);
            verify(userMapper).count("admin", null, null, null);
            verify(userRoleMapper).findByUserId(1L);
        }

        @Test
        @DisplayName("分页查询没有角色时 - roleId 为 null")
        void listWithoutRole() {
            List<User> userList = Collections.singletonList(normalUser);
            when(userMapper.list(anyInt(), anyInt(), any(), any(), any(), any()))
                    .thenReturn(userList);
            when(userMapper.count(any(), any(), any(), any()))
                    .thenReturn(1L);
            when(userRoleMapper.findByUserId(1L)).thenReturn(null);

            ResponseResult<Map<String, Object>> result = userService.list(1, 10, null, null, null, null);

            assertEquals(200, result.getCode());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.getData().get("list");
            assertFalse(list.get(0).containsKey("roleId"));
        }

        @Test
        @DisplayName("分页查询空结果集时返回空列表")
        void listEmptyResult() {
            when(userMapper.list(anyInt(), anyInt(), any(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            when(userMapper.count(any(), any(), any(), any()))
                    .thenReturn(0L);

            ResponseResult<Map<String, Object>> result = userService.list(1, 10, null, null, null, null);

            assertEquals(200, result.getCode());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.getData().get("list");
            assertTrue(list.isEmpty());
            assertEquals(0L, result.getData().get("total"));
        }
    }

    @Nested
    @DisplayName("根据ID查询测试")
    class GetByIdTest {

        @Test
        @DisplayName("查询成功 - 返回用户信息")
        void getByIdSuccess() {
            when(userMapper.findById(1L)).thenReturn(normalUser);

            ResponseResult<User> result = userService.getById(1L);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("admin", result.getData().getUsername());
        }

        @Test
        @DisplayName("用户不存在 - 返回404")
        void getByIdNotFound() {
            when(userMapper.findById(999L)).thenReturn(null);

            ResponseResult<User> result = userService.getById(999L);

            assertEquals(404, result.getCode());
            assertEquals("用户不存在", result.getMsg());
            assertNull(result.getData());
        }
    }

    @Nested
    @DisplayName("新增用户测试")
    class AddTest {

        @Test
        @DisplayName("新增用户成功 - 使用默认密码和状态")
        void addSuccess() {
            User newUser = new User();
            newUser.setUsername("newUser");
            newUser.setName("新用户");
            newUser.setDept("市场部");

            when(userMapper.findByUsername("newUser")).thenReturn(null);
            when(userMapper.insert(any(User.class))).thenReturn(1);

            ResponseResult<Void> result = userService.add(newUser);

            assertEquals(200, result.getCode());
            assertNotNull(newUser.getPassword());
            assertTrue(newUser.getPassword().length() > 0);
            assertEquals(1, newUser.getStatus());
            assertEquals(1, newUser.getFirstLogin());
            verify(userMapper).insert(newUser);
        }

        @Test
        @DisplayName("用户名已存在 - 返回400")
        void addWithDuplicateUsername() {
            User newUser = new User();
            newUser.setUsername("admin");

            when(userMapper.findByUsername("admin")).thenReturn(normalUser);

            ResponseResult<Void> result = userService.add(newUser);

            assertEquals(400, result.getCode());
            assertEquals("用户名已存在", result.getMsg());
            verify(userMapper, never()).insert(any(User.class));
        }

        @Test
        @DisplayName("新增用户时保留传入的status和firstLogin")
        void addWithCustomStatus() {
            User newUser = new User();
            newUser.setUsername("custom");
            newUser.setStatus(0);
            newUser.setFirstLogin(0);

            when(userMapper.findByUsername("custom")).thenReturn(null);
            when(userMapper.insert(any(User.class))).thenReturn(1);

            ResponseResult<Void> result = userService.add(newUser);

            assertEquals(200, result.getCode());
            assertEquals(0, newUser.getStatus());
            assertEquals(0, newUser.getFirstLogin());
        }
    }

    @Nested
    @DisplayName("更新用户测试")
    class UpdateTest {

        @Test
        @DisplayName("更新用户成功")
        void updateSuccess() {
            User updateUser = new User();
            updateUser.setId(1L);
            updateUser.setUsername("adminUpdated");
            updateUser.setName("管理员新");
            updateUser.setDept("研发部");

            when(userMapper.findById(1L)).thenReturn(normalUser);
            when(userMapper.update(any(User.class))).thenReturn(1);

            ResponseResult<Void> result = userService.update(updateUser);

            assertEquals(200, result.getCode());
            verify(userMapper).update(updateUser);
        }

        @Test
        @DisplayName("更新不存在的用户 - 返回404")
        void updateNotFound() {
            User updateUser = new User();
            updateUser.setId(999L);

            when(userMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = userService.update(updateUser);

            assertEquals(404, result.getCode());
            assertEquals("用户不存在", result.getMsg());
            verify(userMapper, never()).update(any(User.class));
        }
    }

    @Nested
    @DisplayName("删除用户测试")
    class DeleteTest {

        @Test
        @DisplayName("删除用户成功 - 同时删除角色关联")
        void deleteSuccess() {
            when(userMapper.findById(1L)).thenReturn(normalUser);
            when(userRoleMapper.deleteByUserId(1L)).thenReturn(1);
            when(userMapper.deleteById(1L)).thenReturn(1);

            ResponseResult<Void> result = userService.delete(1L);

            assertEquals(200, result.getCode());
            verify(userRoleMapper).deleteByUserId(1L);
            verify(userMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的用户 - 返回404")
        void deleteNotFound() {
            when(userMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = userService.delete(999L);

            assertEquals(404, result.getCode());
            assertEquals("用户不存在", result.getMsg());
            verify(userRoleMapper, never()).deleteByUserId(anyLong());
            verify(userMapper, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("重置密码测试")
    class ResetPasswordTest {

        @Test
        @DisplayName("重置密码成功 - 重置为 uu888888")
        void resetPasswordSuccess() {
            when(userMapper.findById(1L)).thenReturn(normalUser);
            when(userMapper.updatePassword(anyLong(), anyString())).thenReturn(1);

            ResponseResult<Void> result = userService.resetPassword(1L);

            assertEquals(200, result.getCode());
            verify(userMapper).updatePassword(eq(1L), anyString());
        }

        @Test
        @DisplayName("重置密码时用户不存在 - 返回404")
        void resetPasswordUserNotFound() {
            when(userMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = userService.resetPassword(999L);

            assertEquals(404, result.getCode());
            assertEquals("用户不存在", result.getMsg());
            verify(userMapper, never()).updatePassword(anyLong(), anyString());
        }
    }

    @Nested
    @DisplayName("分配角色测试")
    class AssignRoleTest {

        @Test
        @DisplayName("分配角色成功（包含角色ID）")
        void assignRoleWithRoleId() {
            when(userRoleMapper.deleteByUserId(1L)).thenReturn(1);
            when(userRoleMapper.insertByParams(1L, 2L)).thenReturn(1);

            ResponseResult<Void> result = userService.assignRole(1L, 2L);

            assertEquals(200, result.getCode());
            verify(userRoleMapper).deleteByUserId(1L);
            verify(userRoleMapper).insertByParams(1L, 2L);
        }

        @Test
        @DisplayName("分配角色成功（角色ID为null，仅删除原角色）")
        void assignRoleWithNullRoleId() {
            when(userRoleMapper.deleteByUserId(1L)).thenReturn(1);

            ResponseResult<Void> result = userService.assignRole(1L, null);

            assertEquals(200, result.getCode());
            verify(userRoleMapper).deleteByUserId(1L);
            verify(userRoleMapper, never()).insertByParams(anyLong(), anyLong());
        }
    }
}
