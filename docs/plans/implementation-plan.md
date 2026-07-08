# 实施计划 — 项目工时管理系统

> 基于 Spring Boot + MyBatis + MySQL + 原生 HTML/JS
> 原型基础：`docs/prototype/index.html`（所有前端页面模板已在原型中定义）

## 总体策略

1. Phase 3 从原型分支开始：`git checkout -b dev`
2. 原型 `index.html` 作为前端基础，**保留 routes + rolePermissions + 全部 render 函数**，替换 `page2Data` 静态数据为 AJAX 调用
3. 后端先搭骨架（Spring Boot + 数据源 + MyBatis），再逐个模块开发（TDD）
4. 每个功能模块：Model → Mapper → Service → Controller → 前端对接
5. 前端改造模式详见 `docs/architecture/architecture.md` →「前端数据替换策略」

## 任务分解

### Task 1：后端项目骨架搭建（预计 30min）

**目标：** 创建 Spring Boot 项目，配置 MyBatis + MySQL

**文件：**
- `pom.xml` — Maven 依赖（spring-boot-starter-web, mybatis-spring-boot-starter, mysql-connector-j, lombok, druid）
- `application.yml` — 数据库配置、端口、session
- `com.ptm` 基础包结构

**验证：** `mvn spring-boot:run` 启动成功

### Task 2：认证模块 + 基础框架（预计 45min）

**目标：** 登录/退出/验证码 + RBAC 拦截器

**文件：**
- `entity/User.java` / `entity/Role.java`
- `mapper/UserMapper.java` / `mapper/RoleMapper.java`
- `service/AuthService.java`
- `controller/AuthController.java`
- `config/WebMvcConfig.java` — 登录拦截器
- `config/SecurityInterceptor.java` — 按 URL + 角色鉴权
- `util/CaptchaUtil.java`

**验证：** 可登录、验证码刷新、权限不足返回 403

### Task 3：项目管理（预计 60min）

**目标：** 项目 CRUD + 项目成员管理

**文件：**
- `entity/Project.java`
- `entity/ProjectMember.java`
- `mapper/ProjectMapper.java`, `ProjectMemberMapper.java`
- `service/ProjectService.java`
- `controller/ProjectController.java`
- 前端：project.html（基于原型 project 页面改造）

**验证：** 项目 CRUD + 添加/移除成员

### Task 4：任务管理（预计 40min）

**目标：** 任务 CRUD，按项目过滤

**文件：**
- `entity/Task.java`
- `mapper/TaskMapper.java`
- `service/TaskService.java`
- `controller/TaskController.java`
- 前端：task.html

**验证：** 任务 CRUD

### Task 5：工时管理（预计 60min）

**目标：** 工时填报 + 编辑（仅已驳回）+ 删除 + 任务按项目过滤

**文件：**
- `entity/TimeEntry.java`
- `mapper/TimeEntryMapper.java`
- `service/TimeEntryService.java`
- `controller/TimeEntryController.java`
- 前端：time-entry.html

### Task 6：工时审批（预计 45min）

**目标：** 按角色过滤待审批数据 + 通过/驳回（含驳回原因）

**文件：**
- `entity/ApprovalLog.java`
- `mapper/ApprovalLogMapper.java`
- `service/ApprovalService.java`
- `controller/ApprovalController.java`
- 前端：approval.html

### Task 7：工时统计（预计 30min）

**目标：** 员工工时统计 + 项目工时统计

**文件：**
- `controller/StatisticsController.java`
- `service/StatisticsService.java`
- 前端：statistics-emp.html + statistics-proj.html

### Task 8：用户管理（预计 40min）

**目标：** 用户 CRUD + 重置密码 + 角色分配

**文件：**
- `mapper/UserMapper.java`（已有 Task2）
- `service/UserService.java`
- `controller/UserController.java`
- 前端：user-mgmt.html

### Task 9：角色管理（预计 40min）

**目标：** 角色 CRUD + 权限树分配

**文件：**
- `mapper/RolePermissionMapper.java`
- `service/RoleService.java`
- `controller/RoleController.java`
- 前端：role-mgmt.html

### Task 10：仪表盘（预计 30min）

**目标：** 首页统计卡片 + TOP5排名

**文件：**
- `controller/DashboardController.java`
- `service/DashboardService.java`
- 前端：dashboard.html

### Task 11：PR 审查与归档（预计 20min）

- Security scan（SQL注入/XSS/硬编码密码）
- 代码审查
- PR → Squash Merge → git tag v1.0

---

## 预计总耗时

| 阶段 | 预计 |
|:-----|:----|
| 后端骨架 | 30min |
| 认证+RBAC | 45min |
| 项目管理 | 60min |
| 任务管理 | 40min |
| 工时管理 | 60min |
| 工时审批 | 45min |
| 工时统计 | 30min |
| 用户管理 | 40min |
| 角色管理 | 40min |
| 仪表盘 | 30min |
| PR审查归档 | 20min |
| **合计** | **~7h** |

## 数据流向

```
前端 (AJAX) → Controller → Service → Mapper → MySQL
     ↑                          ↓
     └──────── JSON ←───────────┘
```
