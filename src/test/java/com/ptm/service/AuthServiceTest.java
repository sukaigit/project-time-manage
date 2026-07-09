package com.ptm.service;

import com.ptm.entity.Role;
import com.ptm.entity.RolePermission;
import com.ptm.entity.User;
import com.ptm.mapper.RoleMapper;
import com.ptm.mapper.RolePermissionMapper;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthService authService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private User normalUser;
    private User disabledUser;
    private Role adminRole;
    private RolePermission userPerm;
    private RolePermission projectPerm;

    @BeforeEach
    void setUp() {
        // Normal active user
        normalUser = new User();
        normalUser.setId(1L);
        normalUser.setUsername("admin");
        normalUser.setPassword(encoder.encode("uu888888"));
        normalUser.setName("管理员");
        normalUser.setDept("技术部");
        normalUser.setStatus(1);

        // Disabled user
        disabledUser = new User();
        disabledUser.setId(2L);
        disabledUser.setUsername("disabledUser");
        disabledUser.setPassword(encoder.encode("password123"));
        disabledUser.setName("已禁用");
        disabledUser.setStatus(0);

        // Role
        adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("管理员");
        adminRole.setCode("ADMIN");
        adminRole.setStatus(1);

        // Permissions
        userPerm = new RolePermission();
        userPerm.setId(1L);
        userPerm.setRoleId(1L);
        userPerm.setMenuKey("user");
        userPerm.setActions("view,add,edit,delete");

        projectPerm = new RolePermission();
        projectPerm.setId(2L);
        projectPerm.setRoleId(1L);
        projectPerm.setMenuKey("project");
        projectPerm.setActions("view,add,edit");
    }

    @Nested
    @DisplayName("登录测试")
    class LoginTest {

        @Test
        @DisplayName("登录成功 - 返回用户信息+角色+权限")
        void loginSuccess() {
            String captchaCode = "A1B2";
            when(session.getAttribute("captcha")).thenReturn(captchaCode);
            when(userMapper.findByUsername("admin")).thenReturn(normalUser);
            when(roleMapper.findByUserId(1L)).thenReturn(Arrays.asList(adminRole));
            when(rolePermissionMapper.findByRoleId(1L)).thenReturn(Arrays.asList(userPerm, projectPerm));

            ResponseResult<Map<String, Object>> result = authService.login("admin", "uu888888", captchaCode);

            assertNotNull(result);
            assertEquals(200, result.getCode());

            Map<String, Object> data = result.getData();
            assertNotNull(data);

            // Verify user in response
            User responseUser = (User) data.get("user");
            assertNotNull(responseUser);
            assertEquals("admin", responseUser.getUsername());

            // Verify roles in response
            @SuppressWarnings("unchecked")
            List<Role> roles = (List<Role>) data.get("roles");
            assertNotNull(roles);
            assertEquals(1, roles.size());
            assertEquals("ADMIN", roles.get(0).getCode());

            // Verify permissions in response
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> permissions = (List<Map<String, Object>>) data.get("permissions");
            assertNotNull(permissions);
            assertEquals(2, permissions.size());

            // Verify session attributes set
            verify(session).setAttribute("userId", 1L);
            verify(session).setAttribute(eq("user"), any(User.class));
            verify(session).setAttribute(eq("roles"), anyList());
            verify(session).setAttribute(eq("permissions"), anyList());

            // Verify captcha cleared
            verify(session).removeAttribute("captcha");
        }

        @Test
        @DisplayName("验证码错误 - 返回400")
        void loginWithWrongCaptcha() {
            when(session.getAttribute("captcha")).thenReturn("CORRECT");

            ResponseResult<Map<String, Object>> result = authService.login("admin", "uu888888", "WRONG");

            assertEquals(400, result.getCode());
            assertEquals("验证码错误", result.getMsg());

            // Verify no DB queries were made
            verify(userMapper, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("验证码为空 - 返回400")
        void loginWithEmptyCaptcha() {
            when(session.getAttribute("captcha")).thenReturn(null);

            ResponseResult<Map<String, Object>> result = authService.login("admin", "uu888888", "ANY");

            assertEquals(400, result.getCode());
            assertEquals("验证码错误", result.getMsg());
        }

        @Test
        @DisplayName("验证码大小写不敏感")
        void loginCaptchaCaseInsensitive() {
            when(session.getAttribute("captcha")).thenReturn("AbCd");
            when(userMapper.findByUsername("admin")).thenReturn(normalUser);
            when(roleMapper.findByUserId(1L)).thenReturn(Arrays.asList(adminRole));
            when(rolePermissionMapper.findByRoleId(1L)).thenReturn(new ArrayList<>());

            ResponseResult<Map<String, Object>> result = authService.login("admin", "uu888888", "abcd");

            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("用户名不存在 - 返回400")
        void loginWithNonExistentUsername() {
            when(session.getAttribute("captcha")).thenReturn("A1B2");
            when(userMapper.findByUsername("unknown")).thenReturn(null);

            ResponseResult<Map<String, Object>> result = authService.login("unknown", "anyPass", "A1B2");

            assertEquals(400, result.getCode());
            assertEquals("用户名或密码错误", result.getMsg());
        }

        @Test
        @DisplayName("密码错误 - 返回400")
        void loginWithWrongPassword() {
            when(session.getAttribute("captcha")).thenReturn("A1B2");
            when(userMapper.findByUsername("admin")).thenReturn(normalUser);

            ResponseResult<Map<String, Object>> result = authService.login("admin", "wrongPassword", "A1B2");

            assertEquals(400, result.getCode());
            assertEquals("用户名或密码错误", result.getMsg());
        }

        @Test
        @DisplayName("账号禁用（status=0）- 返回400")
        void loginWithDisabledAccount() {
            when(session.getAttribute("captcha")).thenReturn("A1B2");
            when(userMapper.findByUsername("disabledUser")).thenReturn(disabledUser);

            ResponseResult<Map<String, Object>> result = authService.login("disabledUser", "password123", "A1B2");

            assertEquals(400, result.getCode());
            assertEquals("账号已被禁用", result.getMsg());
        }

        @Test
        @DisplayName("密码包含BCrypt编码的特殊字符也能正确匹配")
        void loginWithBcryptPassword() {
            when(session.getAttribute("captcha")).thenReturn("CAPT");
            when(userMapper.findByUsername("admin")).thenReturn(normalUser);
            when(roleMapper.findByUserId(1L)).thenReturn(Arrays.asList(adminRole));
            when(rolePermissionMapper.findByRoleId(1L)).thenReturn(new ArrayList<>());

            // The password "uu888888" should match the encoded password
            assertTrue(encoder.matches("uu888888", normalUser.getPassword()));

            ResponseResult<Map<String, Object>> result = authService.login("admin", "uu888888", "CAPT");
            assertEquals(200, result.getCode());
        }
    }

    @Nested
    @DisplayName("退出登录测试")
    class LogoutTest {

        @Test
        @DisplayName("退出登录清除session")
        void logout() {
            ResponseResult<Void> result = authService.logout();

            assertNotNull(result);
            assertEquals(200, result.getCode());
            verify(session).invalidate();
        }
    }

    @Nested
    @DisplayName("修改密码测试")
    class ChangePasswordTest {

        @Test
        @DisplayName("修改密码成功")
        void changePasswordSuccess() {
            when(userMapper.findById(1L)).thenReturn(normalUser);
            when(userMapper.updatePassword(anyLong(), anyString())).thenReturn(1);

            ResponseResult<Void> result = authService.changePassword(1L, "uu888888", "newPass123");

            assertEquals(200, result.getCode());
            verify(userMapper).updatePassword(eq(1L), anyString());
        }

        @Test
        @DisplayName("原密码错误 - 返回400")
        void changePasswordWithWrongOldPassword() {
            when(userMapper.findById(1L)).thenReturn(normalUser);

            ResponseResult<Void> result = authService.changePassword(1L, "wrongOldPass", "newPass123");

            assertEquals(400, result.getCode());
            assertEquals("原密码错误", result.getMsg());
            verify(userMapper, never()).updatePassword(anyLong(), anyString());
        }

        @Test
        @DisplayName("用户不存在 - 返回400")
        void changePasswordWithNonExistentUser() {
            when(userMapper.findById(999L)).thenReturn(null);

            ResponseResult<Void> result = authService.changePassword(999L, "oldPass", "newPass");

            assertEquals(400, result.getCode());
            assertEquals("用户不存在", result.getMsg());
            verify(userMapper, never()).updatePassword(anyLong(), anyString());
        }
    }

    @Nested
    @DisplayName("验证码生成测试")
    class CaptchaTest {

        @Test
        @DisplayName("获取验证码存入session")
        void getCaptcha() {
            ResponseResult<String> result = authService.getCaptcha();

            assertNotNull(result);
            assertEquals(200, result.getCode());

            String captchaCode = result.getData();
            assertNotNull(captchaCode);
            assertEquals(4, captchaCode.length());

            verify(session).setAttribute("captcha", captchaCode);
        }
    }
}
