# Change project list dates from datetime to date-only format
with open('index.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Page 1 - hardcoded rows: change datetime to date-only
content = content.replace('2026-06-01 09:30:25', '2026-06-01')
content = content.replace('2026-06-15 14:20:00', '2026-06-15')
content = content.replace('2026-05-20 10:05:18', '2026-05-20')
content = content.replace('2026-12-31 23:59:59', '2026-12-31')
content = content.replace('2026-09-30 23:59:59', '2026-09-30')
content = content.replace('2026-05-10 11:20:30', '2026-05-10')
content = content.replace('2026-06-20 16:45:12', '2026-06-20')
content = content.replace('2026-07-01 08:15:48', '2026-07-01')
content = content.replace('2026-08-10 23:59:59', '2026-08-10')
content = content.replace('2026-11-30 23:59:59', '2026-11-30')

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Done - dates converted to date-only format')
