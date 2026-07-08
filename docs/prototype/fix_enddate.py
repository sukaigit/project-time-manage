# Add end date to project management: form + table + data
with open('index.html', 'r', encoding='utf-8') as f:
    content = f.read()

# === 1. Table header: add 结束时间 column after 创建时间 ===
content = content.replace(
    '<th>项目名称</th><th>成员数</th><th>状态</th><th>创建时间</th><th>操作</th>',
    '<th>项目名称</th><th>成员数</th><th>状态</th><th>创建时间</th><th>结束时间</th><th>操作</th>'
)

# === 2. Page 1 - renderProject() inline rows (lines 184-186) ===
# ERP 系统开发 row
content = content.replace(
    "<tr><td>ERP 系统开发</td><td>12</td><td><span class=\"badge badge-approved\">启用</span></td><td>2026-06-01 09:30:25</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('ERP 系统开发',12,'启用','2026-06-01 09:30:25')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？项目下有工时记录则不可删除。','删除项目')\">删除</button></td></tr>",
    "<tr><td>ERP 系统开发</td><td>12</td><td><span class=\"badge badge-approved\">启用</span></td><td>2026-06-01 09:30:25</td><td>2026-12-31 23:59:59</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('ERP 系统开发',12,'启用','2026-06-01 09:30:25','2026-12-31 23:59:59')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？项目下有工时记录则不可删除。','删除项目')\">删除</button></td></tr>"
)

# APP 开发 row
content = content.replace(
    "<tr><td>APP 开发</td><td>8</td><td><span class=\"badge badge-approved\">启用</span></td><td>2026-06-15 14:20:00</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('APP 开发',8,'启用','2026-06-15 14:20:00')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>",
    "<tr><td>APP 开发</td><td>8</td><td><span class=\"badge badge-approved\">启用</span></td><td>2026-06-15 14:20:00</td><td>2026-12-31 23:59:59</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('APP 开发',8,'启用','2026-06-15 14:20:00','2026-12-31 23:59:59')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>"
)

# 内部运维平台 row
content = content.replace(
    "<tr><td>内部运维平台</td><td>4</td><td><span class=\"badge badge-pending\">停用</span></td><td>2026-05-20 10:05:18</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('内部运维平台',4,'停用','2026-05-20 10:05:18')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>",
    "<tr><td>内部运维平台</td><td>4</td><td><span class=\"badge badge-pending\">停用</span></td><td>2026-05-20 10:05:18</td><td>2026-09-30 23:59:59</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('内部运维平台',4,'停用','2026-05-20 10:05:18','2026-09-30 23:59:59')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>"
)

# === 3. Page 2 data: add endDate ===
content = content.replace(
    "{ name: '数据分析平台', members: 6, status: 'approved', date: '2026-05-10 11:20:30' },\n    { name: '移动办公 APP', members: 10, status: 'approved', date: '2026-06-20 16:45:12' },\n    { name: '客户关系管理系统', members: 5, status: 'pending', date: '2026-07-01 08:15:48' }",
    "{ name: '数据分析平台', members: 6, status: 'approved', date: '2026-05-10 11:20:30', endDate: '2026-08-10 23:59:59' },\n    { name: '移动办公 APP', members: 10, status: 'approved', date: '2026-06-20 16:45:12', endDate: '2026-12-31 23:59:59' },\n    { name: '客户关系管理系统', members: 5, status: 'pending', date: '2026-07-01 08:15:48', endDate: '2026-11-30 23:59:59' }"
)

# === 4. Page 2 renderProject() template row (line 188): add endDate cell + update edit params ===
content = content.replace(
    "<tr><td>${d.name}</td><td>${d.members}</td><td><span class=\"badge badge-${d.status}\">${d.status === 'approved' ? '启用' : '停用'}</span></td><td>${d.date}</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('${d.name}',${d.members},'${d.status === 'approved' ? '启用' : '停用'}','${d.date}')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>",
    "<tr><td>${d.name}</td><td>${d.members}</td><td><span class=\"badge badge-${d.status}\">${d.status === 'approved' ? '启用' : '停用'}</span></td><td>${d.date}</td><td>${d.endDate || '-'}</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('${d.name}',${d.members},'${d.status === 'approved' ? '启用' : '停用'}','${d.date}','${d.endDate || '-'}')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>"
)

# === 5. New project form: add 结束日期 after 开始日期 ===
content = content.replace(
    '<div class="form-group">\n        <label class="form-label">开始日期</label>\n        <input type="date" class="form-input" value="2026-07-01">\n      </div>\n      <div class="modal-footer">\n        <button class="btn btn-secondary" onclick="this.closest(\'.modal-overlay\').remove()">取消</button>\n        <button class="btn btn-primary" onclick="showToast(\'项目创建成功\',\'success\');this.closest(\'.modal-overlay\').remove()">保存</button>',
    '<div class="form-group">\n        <label class="form-label">开始日期</label>\n        <input type="date" class="form-input" value="2026-07-01">\n      </div>\n      <div class="form-group">\n        <label class="form-label">结束日期</label>\n        <input type="date" class="form-input" value="2026-12-31">\n      </div>\n      <div class="modal-footer">\n        <button class="btn btn-secondary" onclick="this.closest(\'.modal-overlay\').remove()">取消</button>\n        <button class="btn btn-primary" onclick="showToast(\'项目创建成功\',\'success\');this.closest(\'.modal-overlay\').remove()">保存</button>'
)

# === 6. showProjectEditForm function: add endDate parameter + form field ===
content = content.replace(
    "function showProjectEditForm(name, members, status, date) {",
    "function showProjectEditForm(name, members, status, date, endDate) {"
)

# Edit form: add 结束日期 after 开始日期 input
content = content.replace(
    '<label class="form-label">开始日期</label>\n        <input type="date" class="form-input" value="${date}">\n      </div>\n      <div class="modal-footer">\n        <button class="btn btn-secondary" onclick="this.closest(\'.modal-overlay\').remove()">取消</button>\n        <button class="btn btn-primary" onclick="showToast(\'项目更新成功\',\'success\');this.closest(\'.modal-overlay\').remove()">保存</button>',
    '<label class="form-label">开始日期</label>\n        <input type="date" class="form-input" value="${date}">\n      </div>\n      <div class="form-group">\n        <label class="form-label">结束日期</label>\n        <input type="date" class="form-input" value="${endDate || \'2026-12-31\'}">\n      </div>\n      <div class="modal-footer">\n        <button class="btn btn-secondary" onclick="this.closest(\'.modal-overlay\').remove()">取消</button>\n        <button class="btn btn-primary" onclick="showToast(\'项目更新成功\',\'success\');this.closest(\'.modal-overlay\').remove()">保存</button>'
)

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Done - end date added to all project forms + table columns + data')
