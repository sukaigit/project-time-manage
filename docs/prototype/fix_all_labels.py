# Add field labels to all filter bars
with open('index.html', 'r', encoding='utf-8') as f:
    content = f.read()

label_style = 'font-size:13px;color:#6e6e73;margin-right:4px;white-space:nowrap'
label_gap = 'font-size:13px;color:#6e6e73;margin-right:4px;margin-left:8px;white-space:nowrap'

# === 1. 任务管理 ===
old_task = (
    '<div class="filter-bar">\n'
    '      <input type="text" class="form-input" placeholder="搜索任务名称..." value="用户">\n'
    '      <select class="form-input form-select"><option>全部项目</option><option>ERP 系统开发</option><option>APP 开发</option></select>\n'
    '      <select class="form-input form-select"><option>全部状态</option><option>启用</option><option>停用</option></select>\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '    </div>'
)
new_task = (
    '<div class="filter-bar">\n'
    '      <span style="' + label_style + '">任务名称</span>\n'
    '      <input type="text" class="form-input" placeholder="搜索任务名称..." value="用户">\n'
    '      <span style="' + label_gap + '">项目</span>\n'
    '      <select class="form-input form-select"><option>全部项目</option><option>ERP 系统开发</option><option>APP 开发</option></select>\n'
    '      <span style="' + label_gap + '">状态</span>\n'
    '      <select class="form-input form-select"><option>全部状态</option><option>启用</option><option>停用</option></select>\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '    </div>'
)
content = content.replace(old_task, new_task)

# === 2. 工时审批 ===
old_approval = (
    '<div class="filter-bar">\n'
    '      <input type="text" class="form-input" placeholder="搜索员工..." value="李工">\n'
    '      <select class="form-input form-select"><option>全部项目</option><option>ERP 系统</option><option>APP 开发</option></select>\n'
    '      <input type="date" class="form-input date-input" value="2026-07-01">\n'
    '      <span class="filter-label">至</span>\n'
    '      <input type="date" class="form-input date-input" value="2026-07-31">\n'
    '      <select class="form-input form-select"><option>全部状态</option><option>待审批</option><option>已通过</option><option>已驳回</option></select>\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '    </div>'
)
new_approval = (
    '<div class="filter-bar">\n'
    '      <span style="' + label_style + '">员工</span>\n'
    '      <input type="text" class="form-input" placeholder="搜索员工..." value="李工">\n'
    '      <span style="' + label_gap + '">项目</span>\n'
    '      <select class="form-input form-select"><option>全部项目</option><option>ERP 系统</option><option>APP 开发</option></select>\n'
    '      <span style="' + label_gap + '">日期</span>\n'
    '      <input type="date" class="form-input date-input" value="2026-07-01">\n'
    '      <span class="filter-label">至</span>\n'
    '      <input type="date" class="form-input date-input" value="2026-07-31">\n'
    '      <span style="' + label_gap + '">状态</span>\n'
    '      <select class="form-input form-select"><option>全部状态</option><option>待审批</option><option>已通过</option><option>已驳回</option></select>\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '    </div>'
)
content = content.replace(old_approval, new_approval)

# === 3. 工时统计 ===
old_stats = (
    '<div class="filter-bar">\n'
    '      <select class="form-input form-select" style="width:120px"><option>2026年</option></select>\n'
    '      <select class="form-input form-select" style="width:120px"><option>7月</option></select>\n'
    '      <select class="form-input form-select"><option>全部项目</option><option>ERP 系统开发</option><option>APP 开发</option></select>\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '      <button class="btn btn-secondary btn-sm" style="margin-left:auto" onclick="showToast(\'导出成功\',\'success\')">导出 Excel</button>\n'
    '    </div>'
)
new_stats = (
    '<div class="filter-bar">\n'
    '      <span style="' + label_style + '">年份</span>\n'
    '      <select class="form-input form-select" style="width:120px"><option>2026年</option></select>\n'
    '      <span style="' + label_gap + '">月份</span>\n'
    '      <select class="form-input form-select" style="width:120px"><option>7月</option></select>\n'
    '      <span style="' + label_gap + '">项目</span>\n'
    '      <select class="form-input form-select"><option>全部项目</option><option>ERP 系统开发</option><option>APP 开发</option></select>\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '      <button class="btn btn-secondary btn-sm" style="margin-left:auto" onclick="showToast(\'导出成功\',\'success\')">导出 Excel</button>\n'
    '    </div>'
)
content = content.replace(old_stats, new_stats)

# === 4. 用户管理 ===
old_user = (
    '<div class="filter-bar">\n'
    '      <input type="text" class="form-input" placeholder="用户名..." value="zhang">\n'
    '      <input type="text" class="form-input" placeholder="姓名..." value="">\n'
    '      <select class="form-input form-select"><option>全部角色</option><option>系统管理员</option><option>项目经理</option><option>普通员工</option></select>\n'
    '      <select class="form-input form-select"><option>全部状态</option><option>启用</option><option>停用</option></select>\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '    </div>'
)
new_user = (
    '<div class="filter-bar">\n'
    '      <span style="' + label_style + '">用户名</span>\n'
    '      <input type="text" class="form-input" placeholder="用户名..." value="zhang">\n'
    '      <span style="' + label_gap + '">姓名</span>\n'
    '      <input type="text" class="form-input" placeholder="姓名..." value="">\n'
    '      <span style="' + label_gap + '">角色</span>\n'
    '      <select class="form-input form-select"><option>全部角色</option><option>系统管理员</option><option>项目经理</option><option>普通员工</option></select>\n'
    '      <span style="' + label_gap + '">状态</span>\n'
    '      <select class="form-input form-select"><option>全部状态</option><option>启用</option><option>停用</option></select>\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '    </div>'
)
content = content.replace(old_user, new_user)

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Done - all filter bars labeled')
