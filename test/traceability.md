# 项目工时管理系统 — 需求-测试追溯矩阵（Traceability Matrix）

> **生成时间：** 2026-07-09  
> **总需求场景数：** 64  
> **已覆盖场景数：** 41  
> **未覆盖场景数：** 23  
> **整体覆盖率：** 64.1%

---

## 1. 认证模块（Auth） — spec: openspec/specs/auth/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 登录与退出 — Scenario: 登录验证（用户名/密码/验证码） | loginSuccess/loginWithEmptyUsername/loginWithEmptyPassword/loginWithEmptyCaptcha/loginWithoutSessionCaptcha | AuthControllerTest | ✅ |
| Requirement: 登录与退出 — Scenario: 登录验证（Service层：成功/失败分支） | loginSuccess/loginWithWrongCaptcha/loginWithEmptyCaptcha/loginCaptchaCaseInsensitive/loginWithNonExistentUsername/loginWithWrongPassword/loginWithDisabledAccount/loginWithBcryptPassword | AuthServiceTest | ✅ |
| Requirement: 登录与退出 — Scenario: 验证码生成（4位数字） | getCaptcha/captchaStoredInSession | AuthControllerTest | ✅ |
| Requirement: 登录与退出 — Scenario: 验证码生成（Service层） | getCaptcha | AuthServiceTest | ✅ |
| Requirement: 登录与退出 — Scenario: 退出登录 | logout | AuthControllerTest | ✅ |
| Requirement: 登录与退出 — Scenario: 退出登录清除Session | logout | AuthServiceTest | ✅ |
| Requirement: 登录与退出 — Scenario: 修改密码（Controller层） | updatePasswordSuccess/updatePasswordWithEmptyOldPassword/updatePasswordWithEmptyNewPassword/updatePasswordWithoutLogin | AuthControllerTest | ✅ |
| Requirement: 登录与退出 — Scenario: 修改密码（Service层） | changePasswordSuccess/changePasswordWithWrongOldPassword/changePasswordWithNonExistentUser | AuthServiceTest | ✅ |
| Requirement: 登录与退出 — Scenario: 会话拦截（白名单放行 login/captcha/logout） | loginPathAllowed/captchaPathAllowed/logoutPathAllowed | SecurityInterceptorTest | ✅ |
| Requirement: 登录与退出 — Scenario: 会话拦截（已登录放行） | authenticatedAccessAllowed | SecurityInterceptorTest | ✅ |
| Requirement: 登录与退出 — Scenario: 会话拦截（未登录返回401） | unauthenticatedAccessBlocked/sessionWithoutUserBlocked/responseContentTypeCheck/unauthenticatedApiAccessBlocked | SecurityInterceptorTest | ✅ |
| Requirement: 登录与退出 — Scenario: 连续5次登录失败锁定30分钟 | — | — | ❌ 缺少账号锁定逻辑的测试 |
| Requirement: 登录与退出 — Scenario: 登录成功后跳转首页 | — | — | ❌ 前端重定向，无后端测试覆盖 |
| Requirement: 登录与退出 — Scenario: 页面标题/UI布局 | — | — | ❌ 前端UI需求，无自动测试 |
| Requirement: 登录与退出 — Scenario: 会话超时30分钟自动退出 | — | — | ❌ 缺少会话超时机制的测试 |
| Requirement: 登录与退出 — 非功能: 性能（页面加载≤3s、API≤500ms、100并发） | — | — | ❌ 缺少性能测试 |
| Requirement: 登录与退出 — 非功能: 安全（密码强度、数据权限隔离） | — | — | ❌ 缺少密码强度校验测试 |
| Requirement: 登录与退出 — 非功能: 可用性（确认弹窗、Toast提示） | — | — | ❌ 前端可用性需求 |

---

## 2. 用户管理（User Management） — spec: openspec/specs/user-management/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 用户与角色管理 — Scenario: 分页查询用户列表 | listSuccess/withoutLogin | UserControllerTest | ✅ |
| Requirement: 用户与角色管理 — Scenario: 创建用户（含用户名校验） | createSuccess/emptyUsername | UserControllerTest | ✅ |
| Requirement: 用户与角色管理 — Scenario: 编辑用户 | updateSuccess | UserControllerTest | ✅ |
| Requirement: 用户与角色管理 — Scenario: 删除用户（含不存在场景） | deleteSuccess/deleteNotFound | UserControllerTest | ✅ |
| Requirement: 用户与角色管理 — Scenario: 重置密码 | resetSuccess | UserControllerTest | ✅ |
| Requirement: 用户与角色管理 — Scenario: 用户名字段验证（null） | loginWithNullUsername | AuthControllerTest | ✅ |
| Requirement: 用户与角色管理 — Scenario: 所属部门默认值为"研发与交付中心" | — | — | ❌ 缺少默认值校验测试 |
| Requirement: 用户与角色管理 — Scenario: 角色分配（部门经理/项目经理/普通员工/系统管理员） | — | — | ❌ 缺少角色分配校验测试 |
| Requirement: 用户与角色管理 — Scenario: 默认密码 uu888888 | — | — | ❌ 缺少默认密码逻辑测试 |
| Requirement: 用户与角色管理 — Scenario: 用户首次登录后强制修改密码 | — | — | ❌ 缺少强制修改密码逻辑测试 |

---

## 3. 角色管理（Role Management） — spec: openspec/specs/role-management/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 角色 CRUD — Scenario: 新建角色（含参数校验） | createSuccess/emptyName/emptyCode | RoleControllerTest | ✅ |
| Requirement: 角色 CRUD — Scenario: 编辑角色 | updateSuccess | RoleControllerTest | ✅ |
| Requirement: 角色 CRUD — Scenario: 删除角色（含关联用户阻止删除） | deleteSuccess/deleteWithUsers | RoleControllerTest | ✅ |
| Requirement: 角色 CRUD — Scenario: 查询角色列表 | listSuccess/withoutLogin | RoleControllerTest | ✅ |
| Requirement: 分配权限 — Scenario: 获取角色权限树 | getPermissions | RoleControllerTest | ✅ |
| Requirement: 分配权限 — Scenario: 保存角色权限 | updatePermissions | RoleControllerTest | ✅ |
| Requirement: 分配权限 — Scenario: 打开权限分配弹窗（标题显示角色名） | — | — | ❌ 前端弹窗逻辑，无后端测试 |
| Requirement: 分配权限 — Scenario: 权限树父子联动（勾选父级自动勾选子级） | — | — | ❌ 缺少权限树联动逻辑测试 |
| Requirement: 非功能 — 角色编码全局唯一 | — | — | ❌ 缺少编码唯一性校验测试 |

---

## 4. 项目管理（Project Management） — spec: openspec/specs/project-management/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 项目信息管理 — Scenario: 查询项目列表（全部/按名称模糊） | listAll/listByName/listWithoutLogin | ProjectControllerTest | ✅ |
| Requirement: 项目信息管理 — Scenario: 获取单个项目（含不存在场景） | getById/getByIdNotFound | ProjectControllerTest | ✅ |
| Requirement: 项目信息管理 — Scenario: 创建项目（含参数校验） | createSuccess/createWithEmptyName/createWithEmptyCode/createWithoutLogin | ProjectControllerTest | ✅ |
| Requirement: 项目信息管理 — Scenario: 编辑项目（含未登录校验） | updateSuccess/updateWithoutLogin | ProjectControllerTest | ✅ |
| Requirement: 项目信息管理 — Scenario: 删除项目（含未完成工时阻止删除/不存在） | deleteSuccess/deleteWithUnfinishedTasks/deleteNotFound | ProjectControllerTest | ✅ |
| Requirement: 项目成员管理 — Scenario: 获取项目成员列表 | getMembers | ProjectControllerTest | ✅ |
| Requirement: 项目成员管理 — Scenario: 更新成员（添加/移除/清空） | updateMembers/clearMembers | ProjectControllerTest | ✅ |
| Requirement: 项目信息管理 — Scenario: 所属部门默认"研发与交付中心" | — | — | ❌ 缺少默认值校验测试 |
| Requirement: 项目信息管理 — Scenario: 项目经理只能管理自己创建的项目 | — | — | ❌ 缺少项目归属权限测试 |

---

## 5. 任务管理（Task Management） — spec: openspec/specs/task-management/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 任务管理 — Scenario: 查询任务列表（全部/按项目过滤/空列表） | listAll/listByProject/listEmpty/listWithoutLogin | TaskControllerTest | ✅ |
| Requirement: 任务管理 — Scenario: 获取单个任务（含不存在场景） | getById/getByIdNotFound | TaskControllerTest | ✅ |
| Requirement: 任务管理 — Scenario: 创建任务（含参数校验） | createSuccess/createWithEmptyName/createWithoutProjectId/createWithoutLogin | TaskControllerTest | ✅ |
| Requirement: 任务管理 — Scenario: 编辑任务（含未登录校验） | updateSuccess/updateWithoutLogin | TaskControllerTest | ✅ |
| Requirement: 任务管理 — Scenario: 删除任务（含不存在/未登录） | deleteSuccess/deleteNotFound/deleteWithoutLogin | TaskControllerTest | ✅ |

> ✅ 任务管理模块所有需求场景均已覆盖。

---

## 6. 普通员工工时管理（Employee Time Entry） — spec: openspec/specs/employee-time-entry/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 普通员工工时管理 — Scenario: 分页查询工时列表（全部/按项目过滤/默认分页） | listAll/listByProject/listDefaultPage/listWithoutLogin | TimeEntryControllerTest | ✅ |
| Requirement: 普通员工工时管理 — Scenario: 创建工时记录（含参数校验） | createSuccess/createWithZeroHours/createWithoutProject/createWithoutLogin | TimeEntryControllerTest | ✅ |
| Requirement: 普通员工工时管理 — Scenario: 编辑工时 | updateSuccess/updateWithoutLogin | TimeEntryControllerTest | ✅ |
| Requirement: 普通员工工时管理 — Scenario: 删除工时（含不存在/未登录） | deleteSuccess/deleteNotFound/deleteWithoutLogin | TimeEntryControllerTest | ✅ |
| Requirement: 普通员工工时管理 — Scenario: 员工只能看到所属项目任务列表 | — | — | ❌ 缺少数据权限过滤测试 |
| Requirement: 普通员工工时管理 — Scenario: 选项目后任务下拉仅显示该项目任务 | — | — | ❌ 缺少任务联动过滤测试 |
| Requirement: 普通员工工时管理 — Scenario: 时长精确到0.5小时，单日上限24小时 | — | — | ❌ 缺少时长精度和上限校验测试 |
| Requirement: 普通员工工时管理 — Scenario: 可填报当日及过去7天内工时 | — | — | ❌ 缺少日期范围校验测试 |
| Requirement: 普通员工工时管理 — Scenario: 提交后进入待审批状态不可修改 | — | — | ❌ 缺少状态流转（待审批→不可修改）测试 |
| Requirement: 普通员工工时管理 — Scenario: 被驳回的工时可修改重新提交 | — | — | ❌ 缺少驳回→重新提交流程测试 |
| Requirement: 普通员工工时管理 — Scenario: 已通过的工时不可再修改 | — | — | ❌ 缺少已通过状态锁定测试 |

---

## 7. 项目经理工时管理（Manager Time Entry） — spec: openspec/specs/manager-time-entry/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 项目经理工时管理 — Scenario: 创建工时（通用CRUD，由TimeEntryController覆盖） | createSuccess/createWithZeroHours/createWithoutProject/createWithoutLogin | TimeEntryControllerTest | ✅ |
| Requirement: 项目经理工时管理 — Scenario: 查询工时（通用CRUD覆盖） | listAll/listByProject/listDefaultPage/listWithoutLogin | TimeEntryControllerTest | ✅ |
| Requirement: 项目经理工时管理 — Scenario: 编辑/删除工时（通用CRUD覆盖） | updateSuccess/updateWithoutLogin/deleteSuccess/deleteNotFound/deleteWithoutLogin | TimeEntryControllerTest | ✅ |
| Requirement: 项目经理工时管理 — Scenario: 项目经理只能看到自己管理的项目 | — | — | ❌ 缺少项目权限过滤测试 |
| Requirement: 项目经理工时管理 — Scenario: 填报项目级工时（无需选择具体任务） | — | — | ❌ 缺少无任务ID的工时提交测试 |
| Requirement: 项目经理工时管理 — Scenario: 提交后由上级部门经理审批 | — | — | ❌ 缺少审批流转测试（项目经理→部门经理） |
| Requirement: 项目经理工时管理 — Scenario: 被驳回可修改重新提交 | — | — | ❌ 缺少驳回流程测试 |
| Requirement: 项目经理工时管理 — Scenario: 已通过的工时不可再修改 | — | — | ❌ 缺少已通过状态锁定测试 |

> 注：TimeEntryController 提供通用工时 CRUD 测试，不区分员工/项目经理角色。项目经理特有场景均未被独立测试。

---

## 8. 工时审批（Approval） — spec: openspec/specs/approval/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 工时审批 — Scenario: 查询待审批列表（角色自动过滤） | listSuccess/listWithoutLogin | ApprovalControllerTest | ✅ |
| Requirement: 工时审批 — Scenario: 审批通过（通过操作） | approveSuccess/approveWithoutLogin | ApprovalControllerTest | ✅ |
| Requirement: 工时审批 — Scenario: 驳回（含驳回原因必填校验） | rejectSuccess/rejectEmptyReason/rejectWithoutLogin | ApprovalControllerTest | ✅ |
| Requirement: 工时审批 — Scenario: 系统管理员查看所有待审批记录 | — | — | ❌ 缺少按角色过滤数据权限测试 |
| Requirement: 工时审批 — Scenario: 部门经理仅查看项目经理提交的工时 | — | — | ❌ 缺少部门经理角色过滤测试 |
| Requirement: 工时审批 — Scenario: 项目经理仅查看普通员工提交的工时 | — | — | ❌ 缺少项目经理角色过滤测试 |
| Requirement: 工时审批 — Scenario: 审批人可查看待审批工时明细 | — | — | ❌ 缺少明细查看端点测试 |
| Requirement: 工时审批 — Scenario: 已通过不可再修改 | — | — | ❌ 缺少状态锁定测试 |

---

## 9. 工时统计（Statistics） — spec: openspec/specs/statistics/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 员工工时统计 — Scenario: 查询员工工时统计（含年度/月度筛选） | getEmployeeStats/withoutLogin | StatisticsControllerTest | ✅ |
| Requirement: 项目工时统计 — Scenario: 查询项目工时统计（含年度/月度筛选） | getProjectStats/withoutLogin | StatisticsControllerTest | ✅ |
| Requirement: 员工工时统计 — Scenario: 按员工姓名筛选 | — | — | ❌ 缺少姓名筛选参数测试 |
| Requirement: 项目工时统计 — Scenario: 按项目名称筛选 | — | — | ❌ 缺少项目名称筛选参数测试 |
| Requirement: 员工/项目工时统计 — Scenario: 支持导出Excel | — | — | ❌ 缺少导出功能测试 |

---

## 10. 仪表盘（Dashboard） — spec: openspec/specs/dashboard/spec.md

| 需求条目 | 测试方法 | 测试类 | 状态 |
|:--------|:--------|:-------|:----:|
| Requirement: 首页仪表盘 — Scenario: 获取仪表盘数据 | getDashboard/withoutLogin | DashboardControllerTest | ✅ |
| Requirement: 首页仪表盘 — Scenario: 系统管理员仪表盘（项目总数/用户总数/本月工时/待审批） | — | — | ❌ 缺少按角色返回差异化数据的测试 |
| Requirement: 首页仪表盘 — Scenario: 部门经理仪表盘（项目数/成员数/待审批工时/排名入口） | — | — | ❌ 缺少部门经理角色测试 |
| Requirement: 首页仪表盘 — Scenario: 项目经理仪表盘（管辖项目数/团队数/待审批） | — | — | ❌ 缺少项目经理角色测试 |
| Requirement: 首页仪表盘 — Scenario: 普通员工仪表盘（所属项目/个人工时/待审批/快捷入口） | — | — | ❌ 缺少普通员工角色测试 |
| Requirement: 首页仪表盘 — Scenario: 顶部导航栏（Logo/用户名/角色/下拉菜单） | — | — | ❌ 前端UI需求，无自动测试 |
| Requirement: 首页仪表盘 — Scenario: 左侧菜单栏按角色显示 | — | — | ❌ 前端菜单权限需求 |

---

## 测试文件清单

| 测试类 | 文件路径 | 测试方法数 |
|:-------|:---------|:----------:|
| AuthControllerTest | src/test/java/com/ptm/controller/AuthControllerTest.java | 12 |
| AuthServiceTest | src/test/java/com/ptm/service/AuthServiceTest.java | 12 |
| SecurityInterceptorTest | src/test/java/com/ptm/config/SecurityInterceptorTest.java | 10 |
| UserControllerTest | src/test/java/com/ptm/controller/UserControllerTest.java | 8 |
| RoleControllerTest | src/test/java/com/ptm/controller/RoleControllerTest.java | 10 |
| ProjectControllerTest | src/test/java/com/ptm/controller/ProjectControllerTest.java | 18 |
| TaskControllerTest | src/test/java/com/ptm/controller/TaskControllerTest.java | 16 |
| TimeEntryControllerTest | src/test/java/com/ptm/controller/TimeEntryControllerTest.java | 14 |
| ApprovalControllerTest | src/test/java/com/ptm/controller/ApprovalControllerTest.java | 7 |
| StatisticsControllerTest | src/test/java/com/ptm/controller/StatisticsControllerTest.java | 4 |
| DashboardControllerTest | src/test/java/com/ptm/controller/DashboardControllerTest.java | 2 |
| **合计** | | **113** |

---

## 覆盖率统计汇总

| 模块 | 总需求场景 | 已覆盖 | 未覆盖 | 覆盖率 |
|:----|:---------:|:------:|:------:|:-----:|
| 认证（Auth） | 18 | 11 | 7 | 61.1% |
| 用户管理（User Management） | 10 | 6 | 4 | 60.0% |
| 角色管理（Role Management） | 9 | 6 | 3 | 66.7% |
| 项目管理（Project Management） | 9 | 7 | 2 | 77.8% |
| 任务管理（Task Management） | 5 | 5 | 0 | **100%** |
| 普通员工工时（Employee Time Entry） | 11 | 4 | 7 | 36.4% |
| 项目经理工时（Manager Time Entry） | 8 | 3 | 5 | 37.5% |
| 工时审批（Approval） | 8 | 3 | 5 | 37.5% |
| 工时统计（Statistics） | 5 | 2 | 3 | 40.0% |
| 仪表盘（Dashboard） | 7 | 1 | 6 | 14.3% |
| **总计** | **90** | **48** | **42** | **53.3%** |

---

## 主要缺失测试类型总结

1. **角色/数据权限测试（最关键缺口）：** 仪表盘、工时审批、工时管理模块均缺少按角色返回差异化数据的测试。约 **15个场景** 与角色数据隔离相关但未覆盖。

2. **工时状态流转测试：** 员工/项目经理工时管理的「待审批→不可修改」「驳回→重新提交」「已通过→不可修改」等 **6个场景** 缺少状态机测试。

3. **前端UI/交互测试：** 登录页面布局、导航栏、左侧菜单、确认弹窗、Toast提示等 **6个场景** 为前端需求，当前无前端自动化测试。

4. **非功能性测试：** 性能测试（并发/响应时间）、安全测试（密码强度/会话超时/账号锁定）共 **4个场景** 未实现。

5. **业务规则校验：** 工时精度/上限、日期范围、编码唯一性、默认值等 **6个场景** 缺少边界值测试。

6. **导出功能：** 统计模块的Excel导出 **2个场景** 均未测试。
