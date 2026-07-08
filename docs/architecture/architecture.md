# 系统架构设计

> 技术栈：Spring Boot + MyBatis + MySQL + 原生 HTML/JS
> 参考来源：Phase 1 原型 `docs/prototype/index.html`

---

## 原型 → 后端映射总览

```
原型页面 (routes)       后端 Controller              数据库表
───────────────────────────────────────────────────────────
login.html              AuthController              tb_user
dashboard               DashboardController         (聚合查询)
project                 ProjectController           tb_project + tb_project_member
task                    TaskController              tb_task
timeEntry               TimeEntryController         tb_time_entry
approval                ApprovalController          tb_time_entry + tb_approval_log
statisticsEmp           StatisticsController        (聚合查询)
statisticsProj          StatisticsController        (聚合查询)
userMgmt                UserController              tb_user
roleMgmt                RoleController              tb_role + tb_role_permission + tb_user_role
sidebar/menu            (前端根据 rolePermissions 数据驱动)
```

## 分层架构

```
┌─────────────────────────────────────────────────┐
│   前端 (原生 HTML/CSS/JS)                         │
│   基于 docs/prototype/index.html 改造              │
│   - 替换 page2Data 静态数据为 AJAX 调用             │
│   - 保留 routes/rolePermissions 前端路由+权限逻辑    │
│   - 保留所有页面 render 函数模板                     │
└──────────────────┬──────────────────────────────┘
                   │ HTTP REST API (JSON)
┌──────────────────▼──────────────────────────────┐
│              Controller 层                        │
│  接收前端请求，调用 Service，返回 JSON              │
├─────────────────────────────────────────────────┤
│              Service 层                           │
│  业务逻辑：RBAC 权限校验、审批链、工时状态流转        │
├─────────────────────────────────────────────────┤
│              Mapper 层 (MyBatis)                  │
│  对应 tb_ 前缀的数据库表                            │
└──────────────────┬──────────────────────────────┘
                   │ JDBC
┌──────────────────▼──────────────────────────────┐
│              MySQL 数据库                          │
│  project_time_manage                              │
└─────────────────────────────────────────────────┘
```

## 原型数据模型 → 数据库表字段映射

### tb_project（对应 page2Data.project）

| 原型字段 | 数据库字段 | 类型 | 说明 |
|:---------|:----------|:-----|:-----|
| name | name | VARCHAR(100) | 项目名称 |
| code | code | VARCHAR(50) | 项目编号 |
| dept | dept | VARCHAR(100) | 所属部门，默认研发与交付中心 |
| memberList | → tb_project_member | 关联表 | 项目成员列表 |
| members | memberCount | INT（计算） | SELECT COUNT(*) |
| status | status | TINYINT | 1=启用, 0=停用（原型用 approved/pending）|
| date | start_date | DATE | 开始日期 |
| endDate | end_date | DATE | 结束日期 |

**原型筛选条件 → API 参数：** 项目名称(模糊), 项目编号(模糊), 状态(下拉)

### tb_task（对应 page2Data.task）

| 原型字段 | 数据库字段 | 类型 | 说明 |
|:---------|:----------|:-----|:-----|
| name | name | VARCHAR(100) | 任务名称 |
| code | code | VARCHAR(50) | 任务编号 |
| project | project_id | BIGINT FK | 所属项目 |
| assignees | （已去掉） | — | 不再分配人员 |
| status | status | TINYINT | 1=启用, 0=停用 |

**原型筛选条件 → API 参数：** 任务名称(模糊), 项目(下拉), 状态(下拉)

### tb_time_entry（对应 page2Data.timeEntry）

| 原型字段 | 数据库字段 | 类型 | 说明 |
|:---------|:----------|:-----|:-----|
| name | user_id | BIGINT FK | 填报人（原型用员工名） |
| project | project_id | BIGINT FK | 项目 |
| task | task_id | BIGINT FK | 任务（项目经理可不选） |
| date | work_date | DATE | 工作日期 |
| hours | hours | DECIMAL(4,1) | 时长，0.5~24 |
| content | content | VARCHAR(500) | 工作内容 |
| status | status | TINYINT | 0=待审批,1=已通过,2=已驳回 |
| — | reject_reason | VARCHAR(500) | 驳回原因（新加） |

**原型筛选条件 → API 参数：** 员工(模糊), 项目(下拉), 任务(下拉), 日期范围, 状态(下拉)

### tb_approval（对应 page2Data.approval，无独立表，与 tb_time_entry 共用）

| 原型字段 | 数据库字段 | 类型 | 说明 |
|:---------|:----------|:-----|:-----|
| name | user_id | BIGINT FK | 提交人 |
| role | → tb_user_role | 关联 | 用于按角色过滤 |
| project | project_id | BIGINT FK | 项目 |
| date | work_date | DATE | 日期 |
| hours | hours | DECIMAL(4,1) | 时长 |
| content | content | VARCHAR(500) | 工作内容 |

**原型筛选条件 → API 参数：** 员工(模糊), 项目(下拉), 任务(下拉), 日期范围, 状态(下拉)

### tb_user（对应 page2Data.userMgmt + 登录页）

| 原型字段 | 数据库字段 | 类型 | 说明 |
|:---------|:----------|:-----|:-----|
| user | username | VARCHAR(50) | 登录名 |
| name | name | VARCHAR(50) | 姓名 |
| role | → tb_user_role + tb_role | 多对多 | 可多角色 |
| dept | dept | VARCHAR(100) | 默认研发与交付中心 |
| status | status | TINYINT | 1=启用, 0=停用 |
| （密码） | password | VARCHAR(255) | BCrypt，默认 uu888888 |

**原型筛选条件 → API 参数：** 用户名(模糊), 姓名(模糊), 角色(从角色管理动态加载), 状态(下拉)

### tb_role（对应 page2Data.roleMgmt）

| 原型字段 | 数据库字段 | 类型 | 说明 |
|:---------|:----------|:-----|:-----|
| name | name | VARCHAR(50) | 角色名称 |
| code | code | VARCHAR(50) | 角色编码（admin/...）|
| status | status | TINYINT | 1=启用, 0=停用 |
| date | create_time | DATETIME | 创建时间 |
| note | note | VARCHAR(255) | 备注 |

**原型筛选条件 → API 参数：** 角色名称(模糊), 状态(下拉)

### tb_role_permission（对应原型 rolePermissions 对象）

| 原型结构 | 数据库结构 |
|:---------|:----------|
| rolePermissions[roleCode][menuKey] = actions[] | role_id + menu_key + actions(JSON) |

### 仪表盘（原型 renderDashboard + 统计卡片 + TOP5）

| 类型 | 数据来源 |
|:-----|:---------|
| 项目总数 | SELECT COUNT(*) FROM tb_project |
| 任务总数 | SELECT COUNT(*) FROM tb_task |
| 成员总数 | SELECT COUNT(*) FROM tb_user |
| 本月工时 | SELECT SUM(hours) FROM tb_time_entry WHERE status=1 AND MONTH(work_date)=? |
| 累计工时 | SELECT SUM(hours) FROM tb_time_entry WHERE status=1 |
| 近期工时动态 | SELECT TOP 4 FROM tb_time_entry ORDER BY create_time DESC |
| TOP5 排名 | SELECT project/user, SUM(hours) GROUP BY ... ORDER BY SUM DESC LIMIT 5 |

## 原型页面 → 前端文件映射（Phase 3）

| 原型页面 | 目标文件 | 说明 |
|:---------|:---------|:-----|
| login.html | `src/main/resources/static/login.html` | 不变 |
| index.html（整体） | `src/main/resources/static/index.html` | 保留 routes + rolePermissions + render函数，替换 page2Data 为 AJAX |
| dashboard 渲染 | → 调用 GET /api/dashboard | 替换 page2Data.dashboard |
| project 渲染 | → 调用 GET /api/projects | 替换 page2Data.project |
| task 渲染 | → 调用 GET /api/tasks | 替换 page2Data.task |
| timeEntry 渲染 | → 调用 GET /api/time-entries | 替换 page2Data.timeEntry |
| approval 渲染 | → 调用 GET /api/approvals | 替换 page2Data.approval |
| statisticsEmp 渲染 | → 调用 GET /api/statistics/employees | 替换 page2Data.statisticsEmp |
| statisticsProj 渲染 | → 调用 GET /api/statistics/projects | 替换 page2Data.statisticsProj |
| userMgmt 渲染 | → 调用 GET /api/users | 替换 page2Data.userMgmt |
| roleMgmt 渲染 | → 调用 GET /api/roles | 替换 page2Data.roleMgmt |

## 前端数据替换策略

原型中每个页面渲染函数（如 renderProject）目前从 `page2Data` 读取静态数据。
Phase 3 改造时，在每个 render 函数开头加 AJAX 调用：

```javascript
// 例：renderProject 改造模式
function renderProject() {
  fetch('/api/projects?page=' + pageState.project)
    .then(r => r.json())
    .then(data => {
      // 原有 page2Data.project.map(...) 逻辑保持不变
      // 只是数据来源从 page2Data 改为 data.list
      renderProjectTable(data.list);
    });
}
```

## 模块划分

### 1. 认证模块 (auth) — 原型 login.html
- Controller: `AuthController`
- 功能：登录/退出、验证码、修改密码

### 2. 项目管理 (project) — 原型 renderProject
- Controller: `ProjectController`
- 功能：项目CRUD、项目成员管理

### 3. 任务管理 (task) — 原型 renderTask
- Controller: `TaskController`
- 功能：任务CRUD

### 4. 工时管理 (timeEntry) — 原型 renderTimeEntry
- Controller: `TimeEntryController`
- 功能：工时CRUD（仅已驳回可编辑）、任务按项目过滤

### 5. 工时审批 (approval) — 原型 renderApproval
- Controller: `ApprovalController`
- 功能：按角色过滤、通过/驳回（驳回调需填原因）

### 6. 工时统计 (statistics) — 原型 renderStatisticsEmp + renderStatisticsProj
- Controller: `StatisticsController`
- 功能：员工/项目工时统计列表

### 7. 用户管理 (userMgmt) — 原型 renderUserMgmt
- Controller: `UserController`
- 功能：用户CRUD、重置密码、角色分配

### 8. 角色管理 (roleMgmt) — 原型 renderRoleMgmt + renderPermissionCheckboxes
- Controller: `RoleController`
- 功能：角色CRUD、权限树分配

### 9. 仪表盘 (dashboard) — 原型 renderDashboard
- Controller: `DashboardController`
- 功能：统计卡片 + TOP5排名 + 近期动态

## 权限模型 (RBAC)

数据源：原型 `rolePermissions` 对象

```
tb_user ──→ tb_user_role ──→ tb_role ──→ tb_role_permission
                                            ↓
                                      前端 routes + rolePermissions 驱动侧边栏
```

- 原型 `renderSidebar()` 根据 `rolePermissions[currentRole][key]` 过滤菜单 → 后端只需返回用户角色列表
- 审批链：部门经理(role=dept_manager) → 审批项目经理提交的工时；项目经理(role=project_manager) → 审批普通员工

## 原型分页逻辑

原型 `pageState` + `pagination()` + `switchPage()` 模式：
- 每个模块维护 currentPage
- 翻页时重新调用 render（前端重新请求 API）
- 后端标准分页：`page`(1-based) + `size`(默认20) + `total` + `list`
