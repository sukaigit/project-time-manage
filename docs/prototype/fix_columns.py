# Change project list: 创建时间→开始日期, 结束时间→结束日期, add date filters
with open('index.html', 'r', encoding='utf-8') as f:
    content = f.read()

# === 1. Table header: 创建时间 → 开始日期, 结束时间 → 结束日期 ===
content = content.replace(
    '<th>项目名称</th><th>成员数</th><th>状态</th><th>创建时间</th><th>结束时间</th><th>操作</th>',
    '<th>项目名称</th><th>成员数</th><th>状态</th><th>开始日期</th><th>结束日期</th><th>操作</th>'
)

# === 2. Filter bar: add date range after status dropdown ===
old_filter = (
    '<div class="filter-bar">\n'
    '      <input type="text" class="form-input" placeholder="搜索项目名称..." value="ERP">\n'
    '      <select class="form-input form-select"><option>全部状态</option><option>启用</option><option>停用</option></select>\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '    </div>'
)

new_filter = (
    '<div class="filter-bar">\n'
    '      <input type="text" class="form-input" placeholder="搜索项目名称..." value="ERP">\n'
    '      <select class="form-input form-select"><option>全部状态</option><option>启用</option><option>停用</option></select>\n'
    '      <input type="date" class="form-input" style="width:150px" value="" placeholder="开始日期">\n'
    '      <input type="date" class="form-input" style="width:150px" value="" placeholder="结束日期">\n'
    '      <button class="btn btn-secondary btn-sm" onclick="showToast(\'查询完成\',\'success\')">查询</button>\n'
    '      <button class="btn btn-sm" style="border:1px solid #d2d2d7;border-radius:9999px;color:#6e6e73" onclick="showToast(\'已重置\',\'success\')">重置</button>\n'
    '    </div>'
)

content = content.replace(old_filter, new_filter)

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Done - table headers updated, date filters added')
