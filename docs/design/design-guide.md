# 设计指南 — Apple 风格

基于 Apple 官方设计语言提取，适用于项目工时管理系统后台。

## 设计原则

- **干净、克制**：一个主色（Action Blue #0066cc），没有多余装饰
- **内容优先**：UI 后退，数据和操作为核心
- **一致性**：所有交互元素使用统一的圆角、间距和动画规则

## 色彩系统

| Token | 值 | 用途 |
|-------|-----|------|
| `--color-primary` | #0066cc | 主要操作按钮、链接、激活态 |
| `--color-text` | #1d1d1f | 正文 |
| `--color-text-muted` | #7a7a7a | 次要文字 |
| `--color-canvas` | #ffffff | 卡片、表单、内容区背景 |
| `--color-canvas-secondary` | #f5f5f7 | 页面背景 |
| `--color-navbar` | #000000 | 顶部导航栏 |
| `--color-divider` | #e0e0e0 | 表格边框、分割线 |
| `--color-success` | #30d158 | 成功/已通过 |
| `--color-warning` | #ff9f0a | 警告/待审批 |
| `--color-danger` | #ff453a | 错误/驳回/删除 |

## 字体

- 标题/展示：`SF Pro Display` → `Inter`（备选）
- 正文/UI：`SF Pro Text` → `Inter`（备选）
- 标题字号：28px / 22px / 18px，weight 600，letter-spacing -0.02em
- 正文字号：15px，line-height 1.5

## 间距

8px 为基准单位：8 / 12 / 16 / 24 / 32 / 48

## 圆角

| Token | 值 | 用途 |
|-------|-----|------|
| `--radius-sm` | 6px | 次要组件 |
| `--radius-md` | 8px | 输入框 |
| `--radius-lg` | 12px | 卡片、弹窗 |
| `--radius-pill` | 9999px | 按钮 |

## 按钮

- **主要按钮**：`--color-primary` 背景，白色文字，pill 圆角
- **次要按钮**：透明背景，`--color-primary` 文字+边框，pill 圆角
- **危险按钮**：`--color-danger` 背景，白色文字
- 按下态：`transform: scale(0.95)`（全系统统一）

## 布局

- 顶部导航栏：48px 高，纯黑色背景，Logo 居左，用户信息居右
- 左侧菜单栏：220px 宽，激活项蓝色左边框
- 内容区：全白卡片 + 浅灰背景

## 状态标签

| 状态 | 颜色 |
|------|------|
| 待审批 | 黄色背景 (#fff3cd) / 深黄文字 (#856404) |
| 已通过 | 绿色背景 (#d4edda) / 深绿文字 (#155724) |
| 已驳回 | 红色背景 (#f8d7da) / 深红文字 (#721c24) |

## 交互规范

- 所有危险操作（删除、禁用）需弹窗二次确认
- 操作成功/失败使用 Toast 通知（右上角滑入）
- 按钮无文本变化时统一使用 `scale(0.95)` 按下反馈
