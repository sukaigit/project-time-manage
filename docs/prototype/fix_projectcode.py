# Add project number field to project management
with open('index.html', 'r', encoding='utf-8') as f:
    content = f.read()

# === 1. Table header: add 项目编号 after 项目名称 ===
content = content.replace(
    '<th>项目名称</th><th>成员数</th><th>状态</th><th>开始日期</th><th>结束日期</th><th>操作</th>',
    '<th>项目名称</th><th>项目编号</th><th>成员数</th><th>状态</th><th>开始日期</th><th>结束日期</th><th>操作</th>'
)

# === 2. Page 1 data rows - ERP 系统开发 ===
old_erp = "<tr><td>ERP 系统开发</td><td>12</td><td><span class=\"badge badge-approved\">启用</span></td><td>2026-06-01</td><td>2026-12-31</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('ERP 系统开发',12,'启用','2026-06-01','2026-12-31')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？项目下有工时记录则不可删除。','删除项目')\">删除</button></td></tr>"
new_erp = "<tr><td>ERP 系统开发</td><td>PRJ-2026-001</td><td>12</td><td><span class=\"badge badge-approved\">启用</span></td><td>2026-06-01</td><td>2026-12-31</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('ERP 系统开发','PRJ-2026-001',12,'启用','2026-06-01','2026-12-31')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？项目下有工时记录则不可删除。','删除项目')\">删除</button></td></tr>"
content = content.replace(old_erp, new_erp)

# === 3. Page 1 data rows - APP 开发 ===
old_app = "<tr><td>APP 开发</td><td>8</td><td><span class=\"badge badge-approved\">启用</span></td><td>2026-06-15</td><td>2026-12-31</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('APP 开发',8,'启用','2026-06-15','2026-12-31')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>"
new_app = "<tr><td>APP 开发</td><td>PRJ-2026-002</td><td>8</td><td><span class=\"badge badge-approved\">启用</span></td><td>2026-06-15</td><td>2026-12-31</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('APP 开发','PRJ-2026-002',8,'启用','2026-06-15','2026-12-31')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>"
content = content.replace(old_app, new_app)

# === 4. Page 1 data rows - 内部运维平台 ===
old_ops = "<tr><td>内部运维平台</td><td>4</td><td><span class=\"badge badge-pending\">停用</span></td><td>2026-05-20</td><td>2026-09-30</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('内部运维平台',4,'停用','2026-05-20','2026-09-30')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>"
new_ops = "<tr><td>内部运维平台</td><td>PRJ-2026-003</td><td>4</td><td><span class=\"badge badge-pending\">停用</span></td><td>2026-05-20</td><td>2026-09-30</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('内部运维平台','PRJ-2026-003',4,'停用','2026-05-20','2026-09-30')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>"
content = content.replace(old_ops, new_ops)

# === 5. Page 2 data: add code field ===
content = content.replace(
    "{ name: '数据分析平台', members: 6, status: 'approved', date: '2026-05-10', endDate: '2026-08-10' },\n    { name: '移动办公 APP', members: 10, status: 'approved', date: '2026-06-20', endDate: '2026-12-31' },\n    { name: '客户关系管理系统', members: 5, status: 'pending', date: '2026-07-01', endDate: '2026-11-30' }",
    "{ name: '数据分析平台', code: 'PRJ-2026-004', members: 6, status: 'approved', date: '2026-05-10', endDate: '2026-08-10' },\n    { name: '移动办公 APP', code: 'PRJ-2026-005', members: 10, status: 'approved', date: '2026-06-20', endDate: '2026-12-31' },\n    { name: '客户关系管理系统', code: 'PRJ-2026-006', members: 5, status: 'pending', date: '2026-07-01', endDate: '2026-11-30' }"
)

# === 6. Page 2 template row: add code cell + update edit params ===
old_p2_template = "<tr><td>${d.name}</td><td>${d.members}</td><td><span class=\"badge badge-${d.status}\">${d.status === 'approved' ? '启用' : '停用'}</span></td><td>${d.date}</td><td>${d.endDate || '-'}</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('${d.name}',${d.members},'${d.status === 'approved' ? '启用' : '停用'}','${d.date}','${d.endDate || '-'}')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>"
new_p2_template = "<tr><td>${d.name}</td><td>${d.code || '-'}</td><td>${d.members}</td><td><span class=\"badge badge-${d.status}\">${d.status === 'approved' ? '启用' : '停用'}</span></td><td>${d.date}</td><td>${d.endDate || '-'}</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('${d.name}','${d.code || '-'}',${d.members},'${d.status === 'approved' ? '启用' : '停用'}','${d.date}','${d.endDate || '-'}')\">编辑</button> <button class=\"btn btn-sm btn-danger\" onclick=\"showConfirm('确定要删除该项目吗？','删除项目')\">删除</button></td></tr>"
content = content.replace(old_p2_template, new_p2_template)

# === 7. showProjectEditForm function: add code parameter ===
content = content.replace(
    "function showProjectEditForm(name, members, status, date, endDate) {",
    "function showProjectEditForm(name, code, members, status, date, endDate) {"
)

# === 8. Edit form: add 项目编号 field after 项目名称 ===
content = content.replace(
    '<label class="form-label">项目名称 <span style="color:#ff453a">*</span></label>\n        <input type="text" class="form-input" value="${name}">\n      </div>\n      <div class="form-group">\n        <label class="form-label">项目描述</label>',
    '<label class="form-label">项目名称 <span style="color:#ff453a">*</span></label>\n        <input type="text" class="form-input" value="${name}">\n      </div>\n      <div class="form-group">\n        <label class="form-label">项目编号</label>\n        <input type="text" class="form-input" value="${code}" readonly style="background:#f5f5f7;color:#7a7a7a">\n      </div>\n      <div class="form-group">\n        <label class="form-label">项目描述</label>'
)

# === 9. New project form: add 项目编号 field after 项目名称 ===
content = content.replace(
    '<label class="form-label">项目名称 <span style="color:#ff453a">*</span></label>\n        <input type="text" class="form-input" placeholder="请输入项目名称">\n      </div>\n      <div class="form-group">\n        <label class="form-label">项目描述</label>',
    '<label class="form-label">项目名称 <span style="color:#ff453a">*</span></label>\n        <input type="text" class="form-input" placeholder="请输入项目名称">\n      </div>\n      <div class="form-group">\n        <label class="form-label">项目编号</label>\n        <input type="text" class="form-input" placeholder="如 PRJ-2026-001" value="PRJ-2026-">\n      </div>\n      <div class="form-group">\n        <label class="form-label">项目描述</label>'
)

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Done - project code added to forms + table + data')
