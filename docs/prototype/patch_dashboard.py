import re

with open("D:/hermes/workspace/project-time-manage/docs/prototype/index.html", "r", encoding="utf-8") as f:
    data = f.read()

old = '      </div>\n    </div>\n  `;\n}\n\nfunction renderProject()'
new_section = '      </div>\n    </div>\n    <div class="rank-grid" style="display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-top:24px">\n      <div class="card">\n        <div class="card-title">项目工时排名 TOP5（月度）</div>\n        <div class="table-wrapper">\n          <table>\n            <thead><tr><th style="width:36px">#</th><th>项目名称</th><th style="width:80px">工时</th></tr></thead>\n            <tbody>\n              <tr><td>1</td><td>ERP 系统开发</td><td>240h</td></tr>\n              <tr><td>2</td><td>APP 开发</td><td>186h</td></tr>\n              <tr><td>3</td><td>内部运维平台</td><td>96h</td></tr>\n              <tr><td>4</td><td>数据分析平台</td><td>72h</td></tr>\n              <tr><td>5</td><td>客户关系管理系统</td><td>48h</td></tr>\n            </tbody>\n          </table>\n        </div>\n      </div>\n      <div class="card">\n        <div class="card-title">项目工时排名 TOP5（年度）</div>\n        <div class="table-wrapper">\n          <table>\n            <thead><tr><th style="width:36px">#</th><th>项目名称</th><th style="width:80px">工时</th></tr></thead>\n            <tbody>\n              <tr><td>1</td><td>ERP 系统开发</td><td>957h</td></tr>\n              <tr><td>2</td><td>APP 开发</td><td>743h</td></tr>\n              <tr><td>3</td><td>内部运维平台</td><td>412h</td></tr>\n              <tr><td>4</td><td>数据分析平台</td><td>289h</td></tr>\n              <tr><td>5</td><td>客户关系管理系统</td><td>156h</td></tr>\n            </tbody>\n          </table>\n        </div>\n      </div>\n      <div class="card">\n        <div class="card-title">员工工时排名 TOP5（月度）</div>\n        <div class="table-wrapper">\n          <table>\n            <thead><tr><th style="width:36px">#</th><th>姓名</th><th style="width:80px">工时</th></tr></thead>\n            <tbody>\n              <tr><td>1</td><td>张经理</td><td>168h</td></tr>\n              <tr><td>2</td><td>赵工</td><td>152h</td></tr>\n              <tr><td>3</td><td>李工</td><td>128h</td></tr>\n              <tr><td>4</td><td>王工</td><td>112h</td></tr>\n              <tr><td>5</td><td>孙工</td><td>96h</td></tr>\n            </tbody>\n          </table>\n        </div>\n      </div>\n      <div class="card">\n        <div class="card-title">员工工时排名 TOP5（年度）</div>\n        <div class="table-wrapper">\n          <table>\n            <thead><tr><th style="width:36px">#</th><th>姓名</th><th style="width:80px">工时</th></tr></thead>\n            <tbody>\n              <tr><td>1</td><td>张经理</td><td>720h</td></tr>\n              <tr><td>2</td><td>赵工</td><td>635h</td></tr>\n              <tr><td>3</td><td>李工</td><td>512h</td></tr>\n              <tr><td>4</td><td>王工</td><td>445h</td></tr>\n              <tr><td>5</td><td>孙工</td><td>380h</td></tr>\n            </tbody>\n          </table>\n        </div>\n      </div>\n    </div>\n  `;\n}\n\nfunction renderProject()'

if old in data:
    data = data.replace(old, new_section, 1)
    with open("D:/hermes/workspace/project-time-manage/docs/prototype/index.html", "w", encoding="utf-8") as f:
        f.write(data)
    print("SUCCESS: File updated")
else:
    print("FAILED: old string not found")
    idx = data.find('renderProject()')
    if idx >= 0:
        print("Context:", repr(data[idx-120:idx]))
