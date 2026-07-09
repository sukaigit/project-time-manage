package com.ptm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityInterceptorTest {

    private SecurityInterceptor interceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new SecurityInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Nested
    @DisplayName("白名单路径测试")
    class WhiteListTest {

        @Test
        @DisplayName("/api/auth/login 放行")
        void loginPathAllowed() throws Exception {
            request.setRequestURI("/api/auth/login");
            boolean result = interceptor.preHandle(request, response, null);
            assertTrue(result);
        }

        @Test
        @DisplayName("/api/auth/captcha 放行")
        void captchaPathAllowed() throws Exception {
            request.setRequestURI("/api/auth/captcha");
            boolean result = interceptor.preHandle(request, response, null);
            assertTrue(result);
        }

        @Test
        @DisplayName("/api/auth/logout 放行")
        void logoutPathAllowed() throws Exception {
            request.setRequestURI("/api/auth/logout");
            boolean result = interceptor.preHandle(request, response, null);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("已登录访问测试")
    class AuthenticatedTest {

        @Test
        @DisplayName("已登录用户访问非白名单路径 - 放行")
        void authenticatedAccessAllowed() throws Exception {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("user", new Object()); // any non-null object means logged in
            request.setSession(session);
            request.setRequestURI("/api/project/list");

            boolean result = interceptor.preHandle(request, response, null);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("未登录访问测试")
    class UnauthenticatedTest {

        @Test
        @DisplayName("未登录访问受保护接口 - 返回401")
        void unauthenticatedAccessBlocked() throws Exception {
            request.setRequestURI("/api/project/list");

            boolean result = interceptor.preHandle(request, response, null);
            assertFalse(result);
            assertEquals(200, response.getStatus());

            // Check JSON response body
            String jsonResponse = response.getContentAsString();
            assertNotNull(jsonResponse);
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> responseMap = mapper.readValue(jsonResponse, java.util.Map.class);
            assertEquals(401, responseMap.get("code"));
            assertEquals("未登录或会话超时", responseMap.get("msg"));
        }

        @Test
        @DisplayName("未登录访问静态资源（不在/api/下）- 会走拦截器但仍在/api/路径下")
        void unauthenticatedApiAccessBlocked() throws Exception {
            request.setRequestURI("/api/task/create");

            boolean result = interceptor.preHandle(request, response, null);
            assertFalse(result);
        }

        @Test
        @DisplayName("session存在但无user属性 - 返回401")
        void sessionWithoutUserBlocked() throws Exception {
            MockHttpSession session = new MockHttpSession();
            // session exists but no "user" attribute set
            request.setSession(session);
            request.setRequestURI("/api/project/list");

            boolean result = interceptor.preHandle(request, response, null);
            assertFalse(result);
        }

        @Test
        @DisplayName("响应ContentType为application/json;charset=utf-8")
        void responseContentTypeCheck() throws Exception {
            request.setRequestURI("/api/project/list");

            interceptor.preHandle(request, response, null);

            String contentType = response.getContentType();
            assertNotNull(contentType);
            assertTrue(contentType.contains("application/json"));
            assertTrue(contentType.contains("charset=utf-8"));
        }
    }
}
