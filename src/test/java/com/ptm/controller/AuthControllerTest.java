package com.ptm.controller;

import com.ptm.service.AuthService;
import com.ptm.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private MockHttpSession session;
    private final String validCaptcha = "TEST";
    private final String validUsername = "admin";
    private final String validPassword = "uu888888";

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        // Pre-set captcha in session for login tests that need it
        // Each test sets it explicitly when needed
    }

    @Nested
    @DisplayName("登录接口 POST /api/auth/login")
    class LoginTest {

        @Test
        @DisplayName("登录成功 - 返回200和数据")
        void loginSuccess() throws Exception {
            session.setAttribute("captcha", validCaptcha);

            Map<String, Object> data = new HashMap<>();
            data.put("username", validUsername);
            when(authService.login(eq(validUsername), eq(validPassword), eq(validCaptcha)))
                    .thenReturn(ResponseResult.success(data));

            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"captcha\":\"%s\"}",
                    validUsername, validPassword, validCaptcha);

            mockMvc.perform(post("/api/auth/login")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value(validUsername));
        }

        @Test
        @DisplayName("空用户名 - 返回400")
        void loginWithEmptyUsername() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"\",\"password\":\"%s\",\"captcha\":\"%s\"}",
                    validPassword, validCaptcha);

            mockMvc.perform(post("/api/auth/login")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("用户名不能为空"));
        }

        @Test
        @DisplayName("null用户名 - 返回400")
        void loginWithNullUsername() throws Exception {
            String requestBody = String.format(
                    "{\"password\":\"%s\",\"captcha\":\"%s\"}",
                    validPassword, validCaptcha);

            mockMvc.perform(post("/api/auth/login")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("用户名不能为空"));
        }

        @Test
        @DisplayName("空密码 - 返回400")
        void loginWithEmptyPassword() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"\",\"captcha\":\"%s\"}",
                    validUsername, validCaptcha);

            mockMvc.perform(post("/api/auth/login")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("密码不能为空"));
        }

        @Test
        @DisplayName("空验证码 - 返回400")
        void loginWithEmptyCaptcha() throws Exception {
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"captcha\":\"\"}",
                    validUsername, validPassword);

            mockMvc.perform(post("/api/auth/login")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("验证码不能为空"));
        }

        @Test
        @DisplayName("session中没有验证码 - 返回400")
        void loginWithoutSessionCaptcha() throws Exception {
            // session has no captcha attribute
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"captcha\":\"%s\"}",
                    validUsername, validPassword, validCaptcha);

            mockMvc.perform(post("/api/auth/login")
                            .session(session)  // session exists but has no captcha
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("请先获取验证码"));
        }
    }

    @Nested
    @DisplayName("退出接口 POST /api/auth/logout")
    class LogoutTest {

        @Test
        @DisplayName("退出登录成功")
        void logout() throws Exception {
            mockMvc.perform(post("/api/auth/logout")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("获取验证码 GET /api/auth/captcha")
    class CaptchaTest {

        @Test
        @DisplayName("获取验证码成功")
        void getCaptcha() throws Exception {
            mockMvc.perform(get("/api/auth/captcha")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.image").exists())
                    .andExpect(jsonPath("$.data.key").exists());
        }

        @Test
        @DisplayName("验证码存入session")
        void captchaStoredInSession() throws Exception {
            mockMvc.perform(get("/api/auth/captcha")
                            .session(session))
                    .andExpect(status().isOk());

            // Verify captcha was stored in session
            Object captchaAttr = session.getAttribute("captcha");
            assert captchaAttr != null : "Captcha should be stored in session";
            assert captchaAttr instanceof String : "Captcha should be a string";
            assert ((String) captchaAttr).length() == 4 : "Captcha should be 4 characters";
        }
    }

    @Nested
    @DisplayName("修改密码 PUT /api/auth/password")
    class UpdatePasswordTest {

        @Test
        @DisplayName("修改密码成功")
        void updatePasswordSuccess() throws Exception {
            session.setAttribute("userId", 1L);
            session.setAttribute("user", new Object());
            when(authService.changePassword(eq(1L), eq("oldPass123"), eq("newPass456")))
                    .thenReturn(ResponseResult.success(null));

            String requestBody = "{\"oldPassword\":\"oldPass123\",\"newPassword\":\"newPass456\"}";

            mockMvc.perform(put("/api/auth/password")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("空原密码 - 返回400")
        void updatePasswordWithEmptyOldPassword() throws Exception {
            session.setAttribute("user", new Object());
            String requestBody = "{\"oldPassword\":\"\",\"newPassword\":\"newPass456\"}";

            mockMvc.perform(put("/api/auth/password")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("原密码不能为空"));
        }

        @Test
        @DisplayName("空新密码 - 返回400")
        void updatePasswordWithEmptyNewPassword() throws Exception {
            session.setAttribute("user", new Object());
            String requestBody = "{\"oldPassword\":\"oldPass123\",\"newPassword\":\"\"}";

            mockMvc.perform(put("/api/auth/password")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.msg").value("新密码不能为空"));
        }

        @Test
        @DisplayName("未登录修改密码 - 返回401")
        void updatePasswordWithoutLogin() throws Exception {
            // session has no userId
            String requestBody = "{\"oldPassword\":\"oldPass123\",\"newPassword\":\"newPass456\"}";

            mockMvc.perform(put("/api/auth/password")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }
}
