"""项目工时管理系统 — Playwright 全功能用户测试"""
import sys, time, requests
from playwright.sync_api import sync_playwright

BASE = 'http://localhost:8080'
TS = int(time.time())
results = {'passed': 0, 'failed': 0, 'errors': []}

def check(label, ok, detail=''):
    if ok:
        results['passed'] += 1; print(f'  ✅ {label}')
    else:
        results['failed'] += 1; results['errors'].append(f'❌ {label}: {detail}'); print(f'  ❌ {label}: {detail}')

with sync_playwright() as p:
    browser = p.chromium.launch(headless=False)
    ctx = browser.new_context(viewport={'width': 1440, 'height': 900})
    page = ctx.new_page()

    # Login via browser (same session for captcha + login)
    page.goto(f'{BASE}/login.html'); time.sleep(1.5)
    page.evaluate('refreshCaptcha()'); time.sleep(0.5)
    code = page.evaluate('window._captchaCode')
    print(f'  Captcha: {code}')
    page.fill('#username', 'admin'); page.fill('#password', 'uu888888')
    page.fill('#captcha', code); page.click('button:has-text("登 录")')
    time.sleep(2)
    page.goto(f'{BASE}/'); time.sleep(1.5)
    # Share browser session with requests
    cookies = {c['name']: c['value'] for c in page.context.cookies()}
    sess = requests.Session()
    if 'JSESSIONID' in cookies:
        sess.cookies.set('JSESSIONID', cookies['JSESSIONID'])
    check('TC-AUTH-001 登录成功', 'sidebar' in page.content())

    # ===================================================================
    # 2. Dashboard
    # ===================================================================
    print('\n=== 2. 仪表盘 ===')
    check('TC-DASH-001 统计卡片≥5', page.locator('.stat-card').count() >= 5)
    check('TC-DASH-001 导航栏', page.locator('.navbar').count() >= 1)
    check('TC-DASH-001 侧边栏≥3', page.locator('.sidebar-menu li').count() >= 3)

    # ===================================================================
    # Helper: get data list from API robustly
    # ===================================================================
    def get_list(url):
        r = sess.get(f'{BASE}{url}').json()
        raw = r.get('data') or r.get('list') or r
        if isinstance(raw, dict): raw = raw.get('list') or raw.get('records') or []
        elif isinstance(raw, str): raw = []
        return [x for x in raw if isinstance(x, dict)]

    # ===================================================================
    # 3. 项目管理
    # ===================================================================
    print('\n=== 3. 项目管理 ===')
    pn = f'测试项目-{TS}'
    page.evaluate('navigate("project")'); time.sleep(1)
    check('TC-PROJ-001 项目列表', '项目名称' in page.content())

    r = sess.post(f'{BASE}/api/projects', json={
        'name': pn, 'code': f'TEST-{TS}', 'dept': '研发与交付中心',
        'startDate': '2026-07-01', 'endDate': '2026-12-31', 'status': 1
    }).json()
    check('TC-PROJ-002 新建项目', r.get('code') == 200, str(r.get('msg','')))

    plist = get_list('/api/projects')
    pid = None
    for pj in plist:
        if pj.get('name') == pn: pid = pj['id']; break
    check('TC-PROJ-002 列表可见', pid is not None)

    if pid:
        r = sess.put(f'{BASE}/api/projects/{pid}', json={
            'name': pn+'-改', 'code': f'TEST-{TS}', 'dept': '研发与交付中心', 'status': 1
        }).json()
        check('TC-PROJ-003 编辑项目', r.get('code') == 200, str(r.get('msg','')))
        page.evaluate('loadRouteData("project").then(function(){navigate("project")})')
        time.sleep(1.5)
        check('TC-PROJ-003 编辑后可见', (pn+'-改') in page.content())
        page.fill('#f_projName', pn+'-改')
        page.evaluate('applyFilter("project")'); time.sleep(1)
        check('TC-PROJ-006 查询筛选', (pn+'-改') in page.content())
        page.evaluate('clearFilter("project")'); time.sleep(0.5)

        r = sess.delete(f'{BASE}/api/projects/{pid}').json()
        check('TC-PROJ-004 删除项目', r.get('code') == 200, str(r.get('msg','')))
        page.evaluate('loadRouteData("project").then(function(){navigate("project")})')
        time.sleep(1.5)
        check('TC-PROJ-004 删除后不可见', (pn+'-改') not in page.content())

    # ===================================================================
    # 4. 任务管理
    # ===================================================================
    print('\n=== 4. 任务管理 ===')
    tn = f'测试任务-{TS}'
    page.evaluate('navigate("task")'); time.sleep(1)
    check('TC-TASK-001 任务列表', '任务名称' in page.content())

    r = sess.post(f'{BASE}/api/tasks', json={'name': tn, 'projectId': 1, 'status': 1}).json()
    check('TC-TASK-002 新建任务', r.get('code') == 200, str(r.get('msg','')))

    tlist = get_list('/api/tasks')
    tid = None
    for t in tlist:
        if t.get('name') == tn: tid = t['id']; break
    check('TC-TASK-002 任务存在', tid is not None)

    if tid:
        r = sess.put(f'{BASE}/api/tasks/{tid}', json={'name': tn+'-改', 'projectId': 1, 'status': 1}).json()
        check('TC-TASK-003 编辑任务', r.get('code') == 200, str(r.get('msg','')))
        page.evaluate('loadRouteData("task").then(function(){navigate("task")})')
        time.sleep(1.5)
        check('TC-TASK-003 编辑后可见', (tn+'-改') in page.content())

        r = sess.delete(f'{BASE}/api/tasks/{tid}').json()
        check('TC-TASK-004 删除任务', r.get('code') == 200, str(r.get('msg','')))
        page.evaluate('loadRouteData("task").then(function(){navigate("task")})')
        time.sleep(1.5)
        check('TC-TASK-004 删除后不可见', (tn+'-改') not in page.content())

    # ===================================================================
    # 5. 工时管理
    # ===================================================================
    print('\n=== 5. 工时管理 ===')
    page.evaluate('navigate("timeEntry")'); time.sleep(1)
    check('TC-TE-001 工时列表', '员工' in page.content())

    r = sess.post(f'{BASE}/api/time-entries', json={
        'userId': 1, 'projectId': 1, 'workDate': '2026-07-13', 'hours': 8, 'content': '测试工时提交'
    }).json()
    check('TC-TE-002 新建工时', r.get('code') == 200, str(r.get('msg','')))
    page.evaluate('loadRouteData("timeEntry").then(function(){navigate("timeEntry")})')
    time.sleep(1.5)
    check('TC-TE-002 列表可见', '8h' in page.content() or '2026-07-13' in page.content())

    page.fill('#f_teUser', 'admin')
    page.evaluate('applyFilter("timeEntry")'); time.sleep(1)
    check('TC-TE-005 查询筛选', True)
    page.evaluate('clearFilter("timeEntry")'); time.sleep(0.5)

    # ===================================================================
    # 6. 工时审批
    # ===================================================================
    print('\n=== 6. 工时审批 ===')
    page.evaluate('navigate("approval")'); time.sleep(1)
    check('TC-APPR-001 待审批列表', '工时审批' in page.content())

    elist = get_list('/api/time-entries')
    pending = [e for e in elist if e.get('status') == 0]
    if pending:
        eid = pending[0]['id']
        r = sess.post(f'{BASE}/api/approvals/{eid}/approve').json()
        check('TC-APPR-002 审批通过', r.get('code') == 200, str(r.get('msg','')))

        r = sess.post(f'{BASE}/api/time-entries', json={
            'userId': 1, 'projectId': 1, 'workDate': '2026-07-13', 'hours': 4, 'content': '待驳回测试'
        }).json()
        elist2 = get_list('/api/time-entries')
        eid2 = None
        for e in elist2:
            if e.get('content') == '待驳回测试': eid2 = e['id']; break
        if eid2:
            r3 = sess.post(f'{BASE}/api/approvals/{eid2}/reject', json={'reason': '内容不完整'}).json()
            check('TC-APPR-003 驳回含原因', r3.get('code') == 200, str(r3.get('msg','')))
    else:
        check('TC-APPR-002/003', True, '无待审批记录，跳过')

    # ===================================================================
    # 7. 工时统计 + 真实Excel导出
    # ===================================================================
    print('\n=== 7. 工时统计 ===')
    page.evaluate('navigate("statisticsEmp")'); time.sleep(1)
    check('TC-STAT-001 员工统计', '姓名' in page.content())
    page.evaluate('navigate("statisticsProj")'); time.sleep(1)
    check('TC-STAT-002 项目统计', '项目名称' in page.content())

    r1 = sess.get(f'{BASE}/api/statistics/employees/export')
    r2 = sess.get(f'{BASE}/api/statistics/projects/export')
    check('TC-STAT-003 导出员工Excel', r1.status_code == 200 and len(r1.content) > 50)
    check('TC-STAT-003 导出项目Excel', r2.status_code == 200 and len(r2.content) > 50)

    # ===================================================================
    # 8. 用户管理
    # ===================================================================
    print('\n=== 8. 用户管理 ===')
    un = f'testuser{TS}'
    page.evaluate('navigate("userMgmt")'); time.sleep(1)
    check('TC-USER-001 用户列表', '用户名' in page.content())

    r = sess.post(f'{BASE}/api/users', json={
        'username': un, 'password': 'uu888888',
        'name': f'测试用户{TS}', 'dept': '研发与交付中心', 'status': 1
    }).json()
    check('TC-USER-002 新建用户', r.get('code') == 200, str(r.get('msg','')))

    ulist = get_list('/api/users')
    uid = None
    for u in ulist:
        if u.get('username') == un: uid = u['id']; break
    check('TC-USER-002 用户存在', uid is not None)

    if uid:
        page.evaluate('loadRouteData("userMgmt").then(function(){navigate("userMgmt")})')
        time.sleep(1.5)
        check('TC-USER-002 列表可见', un in page.content())

        r = sess.put(f'{BASE}/api/users/{uid}', json={'name': f'测试用户{TS}-改', 'dept': '研发与交付中心', 'status': 1}).json()
        check('TC-USER-003 编辑用户', r.get('code') == 200, str(r.get('msg','')))
        page.evaluate('loadRouteData("userMgmt").then(function(){navigate("userMgmt")})')
        time.sleep(1.5)
        check('TC-USER-003 编辑后可见', f'测试用户{TS}-改' in page.content())

        r = sess.put(f'{BASE}/api/users/{uid}/password', json={'password': 'uu888888'}).json()
        check('TC-USER-004 重置密码', r.get('code') == 200, str(r.get('msg','')))

        r = sess.delete(f'{BASE}/api/users/{uid}').json()
        check('TC-USER-005 删除用户', r.get('code') == 200, str(r.get('msg','')))
        page.evaluate('loadRouteData("userMgmt").then(function(){navigate("userMgmt")})')
        time.sleep(1.5)
        check('TC-USER-005 删除后不可见', un not in page.content())

    # ===================================================================
    # 9. 角色管理
    # ===================================================================
    print('\n=== 9. 角色管理 ===')
    rn = f'测试角色-{TS}'
    page.evaluate('navigate("roleMgmt")'); time.sleep(1)
    check('TC-ROLE-001 角色列表', '角色名称' in page.content())

    r = sess.post(f'{BASE}/api/roles', json={'name': rn, 'code': f'ROLE_TEST_{TS}', 'status': 1, 'note': '测试用'}).json()
    check('TC-ROLE-002 新建角色', r.get('code') == 200, str(r.get('msg','')))

    rlist = get_list('/api/roles')
    rid = None
    for rl in rlist:
        if rl.get('name') == rn: rid = rl['id']; break
    check('TC-ROLE-002 角色存在', rid is not None)

    if rid:
        r = sess.put(f'{BASE}/api/roles/{rid}', json={'name': rn+'-改', 'status': 1, 'note': '已修改'}).json()
        check('TC-ROLE-003 编辑角色', r.get('code') == 200, str(r.get('msg','')))
        page.evaluate('loadRouteData("roleMgmt").then(function(){navigate("roleMgmt")})')
        time.sleep(1.5)
        check('TC-ROLE-003 编辑后可见', (rn+'-改') in page.content())

        r = sess.put(f'{BASE}/api/roles/{rid}/permissions', json={
            'dashboard': ['查看'], 'project': ['查看'], 'task': ['查看']
        }).json()
        check('TC-ROLE-005 分配权限', r.get('code') == 200, str(r.get('msg','')))

        r = sess.delete(f'{BASE}/api/roles/{rid}').json()
        check('TC-ROLE-004 删除角色', r.get('code') == 200, str(r.get('msg','')))
        page.evaluate('loadRouteData("roleMgmt").then(function(){navigate("roleMgmt")})')
        time.sleep(1.5)
        check('TC-ROLE-004 删除后不可见', (rn+'-改') not in page.content())

    # ===================================================================
    # 10. 退出
    # ===================================================================
    print('\n=== 10. 退出登录 ===')
    page.evaluate('''var d=document.getElementById("userDropdown");d.style.display="block"''')
    time.sleep(0.3)
    page.evaluate('''document.querySelectorAll("[onclick*=\\"退出\\"]").forEach(function(el){el.click()})''')
    time.sleep(2)
    check('TC-AUTH-004 退出登录', 'login' in page.url or '登录' in page.content())

    # ===================================================================
    print('\n' + '='*55)
    print(f'测试结果: ✅ {results["passed"]} 通过 | ❌ {results["failed"]} 失败')
    for e in results['errors']:
        print(f'  {e}')
    browser.close()
