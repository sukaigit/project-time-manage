with open('index.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Page 1 - ERP 系统开发
content = content.replace(
    "2026-06-01</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('ERP 系统开发',12,'启用','2026-06-01')",
    "2026-06-01 09:30:25</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('ERP 系统开发',12,'启用','2026-06-01 09:30:25')"
)

# Page 1 - APP 开发
content = content.replace(
    "2026-06-15</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('APP 开发',8,'启用','2026-06-15')",
    "2026-06-15 14:20:00</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('APP 开发',8,'启用','2026-06-15 14:20:00')"
)

# Page 1 - 内部运维平台
content = content.replace(
    "2026-05-20</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('内部运维平台',4,'停用','2026-05-20')",
    "2026-05-20 10:05:18</td><td><button class=\"btn btn-sm btn-secondary\" onclick=\"showProjectEditForm('内部运维平台',4,'停用','2026-05-20 10:05:18')"
)

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Done - all dates updated to include seconds')
