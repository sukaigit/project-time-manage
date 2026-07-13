"""项目工时管理系统 — Playwright 调试"""
from playwright.sync_api import sync_playwright
import requests, time

BASE = 'http://localhost:8080'

s = requests.Session()
cap = s.get(f'{BASE}/api/auth/captcha').json()
login = s.post(f'{BASE}/api/auth/login', json={
    'username': 'admin', 'password': 'uu888888', 'captcha': cap['data']['code']
}).json()
print(f'Login: {login["msg"]}')

with sync_playwright() as p:
    b = p.chromium.launch(headless=True)
    ctx = b.new_context()
    p2 = ctx.new_page()

    jses = s.cookies.get('JSESSIONID')
    ctx.add_cookies([{'name':'JSESSIONID','value':jses,'domain':'localhost','path':'/'}])

    p2.goto(f'{BASE}/')
    time.sleep(2)

    # Check what's on the page
    title = p2.title()
    has_fn = p2.evaluate('typeof window.navigate')
    has_sidebar = p2.evaluate('!!document.querySelector(".sidebar")')
    js_count = p2.evaluate('document.querySelectorAll("script").length')
    print(f'Title: {title}')
    print(f'typeof navigate: {has_fn}')
    print(f'has sidebar: {has_sidebar}')
    print(f'script count: {js_count}')

    # Try to find the issue
    console_msgs = []
    def on_console(msg):
        console_msgs.append(f'[{msg.type}] {msg.text}')
    p2.on('console', on_console)
    p2.evaluate('1+1')  # trigger any pending console
    time.sleep(0.5)
    for m in console_msgs[:10]:
        print(f'Console: {m}')

    # Access the function directly from the page
    try:
        result = p2.evaluate('''() => {
            try { return typeof window.renderDashboard; }
            catch(e) { return "error: " + e.message; }
        }()''')
        print(f'typeof renderDashboard: {result}')
    except Exception as e:
        print(f'eval error: {e}')

    b.close()
