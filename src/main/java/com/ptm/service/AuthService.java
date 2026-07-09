package com.ptm.service;

import com.ptm.entity.Role;
import com.ptm.entity.RolePermission;
import com.ptm.entity.User;
import com.ptm.mapper.RoleMapper;
import com.ptm.mapper.RolePermissionMapper;
import com.ptm.mapper.UserMapper;
import com.ptm.mapper.UserRoleMapper;
import com.ptm.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private HttpSession session;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     */
    public ResponseResult<Map<String, Object>> login(String username, String password, String captcha) {
        // 校验验证码
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(captcha)) {
            return ResponseResult.error(400, "验证码错误");
        }
        // 清除验证码
        session.removeAttribute("captcha");

        // 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return ResponseResult.error(400, "用户名或密码错误");
        }

        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseResult.error(400, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            return ResponseResult.error(400, "账号已被禁用");
        }

        // 获取角色
        List<Role> roles = roleMapper.findByUserId(user.getId());

        // 获取权限
        List<Map<String, Object>> permissions = new ArrayList<>();
        for (Role role : roles) {
            List<RolePermission> rps = rolePermissionMapper.findByRoleId(role.getId());
            for (RolePermission rp : rps) {
                Map<String, Object> perm = new HashMap<>();
                perm.put("menuKey", rp.getMenuKey());
                perm.put("actions", rp.getActions() != null ?
                        Arrays.asList(rp.getActions().split(",")) : new ArrayList<>());
                permissions.add(perm);
            }
        }

        // 存入 session
        session.setAttribute("userId", user.getId());
        session.setAttribute("user", user);
        session.setAttribute("roles", roles);
        session.setAttribute("permissions", permissions);

        // 组装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("roles", roles);
        data.put("permissions", permissions);

        return ResponseResult.success(data);
    }

    /**
     * 退出登录
     */
    public ResponseResult<Void> logout() {
        session.invalidate();
        return ResponseResult.success(null);
    }

    /**
     * 修改密码
     */
    public ResponseResult<Void> changePassword(Long userId, String oldPwd, String newPwd) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return ResponseResult.error(400, "用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return ResponseResult.error(400, "原密码错误");
        }

        // 加密新密码
        String encodedNewPwd = passwordEncoder.encode(newPwd);
        userMapper.updatePassword(userId, encodedNewPwd);
        return ResponseResult.success(null);
    }

    /**
     * 获取验证码（生成4位数字验证码，存入session）
     */
    public ResponseResult<String> getCaptcha() {
        // 生成4位随机数字验证码
        String captcha = String.format("%04d", new Random().nextInt(10000));
        session.setAttribute("captcha", captcha);
        return ResponseResult.success(captcha);
    }
}
