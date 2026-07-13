"""项目工时管理系统 — Playwright 用户测试"""
import sys, json, time, os, requests
from playwright.sync_api import sync_playwright

BASE = 'http://localhost:8080'
results = {'passed': 0, 'failed': 0, 'errors': []}

def check(label, condition, detail=''):
    if condition:
        results['passed'] += 1; print(f'  ✅ {label}')
    else:
        results['failed'] += 1; results['errors'].append(f'❌ {label}: {detail}'); print(f'  ❌ {label}: {detail}')

with sync_playwright() as p:
    browser = p.chromium.launch(headless=False)
    context = browser.new_context(viewport={'width': 1440, 'height': 900})
    page = context.new_page()

    # ========== Login via API, then set session ==========
    print('\n=== 1. 登录 ===')
    sess = requests.Session()
    cap = sess.get(f'{BASE}/api/auth/captcha').json()
    code = cap['data']['code']
    login = sess.post(f'{BASE}/api/auth/login', json={
        'username': 'admin', 'password': 'uu888888', 'captcha': code
    }).json()
    print(f'  Login: {login["msg"]}, code={code}')
    check('1.1 API登录', login['code'] == 200, str(login))

    # Copy session cookie to Playwright
    jses = sess.cookies.get('JSESSIONID')
    if jses:
        context.add_cookies([{
            'name': 'JSESSIONID', 'value': jses,
            'domain': 'localhost', 'path': '/'
        }])
        print(f'  JSESSIONID: {jses[:20]}...')
        page.goto(f'{BASE}/')
        time.sleep(1)
        has_sidebar = 'sidebar' in page.content()
        check('1.2 首页加载', has_sidebar)
    else:
        print('  ❌ No JSESSIONID')
        check('1.2 首页加载', False)
        browser.close(); sys.exit(1)

    # ========== Dashboard ==========
    print('\n=== 2. 仪表盘 ===')
    check('2.1 统计卡片', page.locator('.stat-card').count() >= 3)
    check('2.2 导航栏', page.locator('.navbar').count() >= 1)
    check('2.3 侧边栏', page.locator('.sidebar').count() >= 1)

    # ========== Navigate all pages ==========
    print('\n=== 3. 页面导航 ===')
    pagelist = [
        ('project', '项目管理'), ('task', '任务管理'), ('timeEntry', '工时管理'),
        ('approval', '工时审批'), ('statisticsEmp', '员工工时统计'),
        ('statisticsProj', '项目工时统计'), ('userMgmt', '用户管理'), ('roleMgmt', '角色管理'),
    ]
    for i, (route, label) in enumerate(pagelist, 1):
        try:
            page.evaluate(f'navigate("{route}")')
            time.sleep(0.8)
            check(f'3.{i} {label}', label in page.content())
        except Exception as e:
            check(f'3.{i} {label}', False, str(e)[:60])

    # ========== CRUD Modals ==========
    print('\n=== 4. CRUD 弹窗 ===')
    cruds = [
        ('project', 'newProject'), ('task', 'newTask'),
        ('timeEntry', 'newTimeEntry'), ('userMgmt', 'newUser'), ('roleMgmt', 'newRole'),
    ]
    for i, (route, fn) in enumerate(cruds, 1):
        try:
            page.evaluate(f'navigate("{route}")')
            time.sleep(0.5)
            page.evaluate('document.querySelector(".modal-overlay")?.remove()')
            page.evaluate(fn + '()')
            time.sleep(1)
            shown = page.locator('.modal-overlay').count() > 0
            check(f'4.{i} {fn}', shown)
            page.evaluate('document.querySelector(".modal-overlay")?.remove()')
        except Exception as e:
            check(f'4.{i} {fn}', False, str(e)[:60])

    # ========== JS Console ==========
    print('\n=== 5. JS 控制台 ===')
    logs = []
    page.on('console', lambda m: logs.append(m))
    page.evaluate('navigate("dashboard")')
    time.sleep(1)
    errs = [l for l in logs if l.type == 'error']
    check('5.1 无JS报错', len(errs) == 0, f'{len(errs)} errors')

    # ========== Summary ==========
    print('\n' + '='*50)
    print(f'测试结果: ✅ {results["passed"]} 通过 | ❌ {results["failed"]} 失败')
    for e in results['errors']:
        print(f'  {e}')
    browser.close()
