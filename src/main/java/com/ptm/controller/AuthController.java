package com.ptm.controller;

import com.ptm.service.AuthService;
import com.ptm.util.CaptchaUtil;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseResult<Map<String, Object>> login(@RequestBody Map<String, String> params,
                                                     HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");
        String captcha = params.get("captcha");

        // 参数校验
        if (username == null || username.trim().isEmpty()) {
            return ResponseResult.error(400, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseResult.error(400, "密码不能为空");
        }
        if (captcha == null || captcha.trim().isEmpty()) {
            return ResponseResult.error(400, "验证码不能为空");
        }

        // 必须确保 session 中的 captcha 由本 controller 的 /captcha 端点设置
        if (session.getAttribute("captcha") == null) {
            return ResponseResult.error(400, "请先获取验证码");
        }

        return authService.login(username, password, captcha);
    }

    @PostMapping("/logout")
    public ResponseResult<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseResult.success(null);
    }

    @GetMapping("/captcha")
    public ResponseResult<Map<String, String>> captcha(HttpSession session) {
        CaptchaUtil.CaptchaResult captcha = CaptchaUtil.generateCaptcha();
        // 将验证码存入 session，供登录时校验
        session.setAttribute("captcha", captcha.getCode());
        Map<String, String> result = new HashMap<>();
        result.put("image", captcha.getImage());
        result.put("key", session.getId());
        return ResponseResult.success(result);
    }

    @PutMapping("/password")
    public ResponseResult<Void> updatePassword(@RequestBody Map<String, String> params,
                                               HttpSession session) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return ResponseResult.error(400, "原密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseResult.error(400, "新密码不能为空");
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseResult.unauthorized();
        }

        return authService.changePassword(userId, oldPassword, newPassword);
    }
}
