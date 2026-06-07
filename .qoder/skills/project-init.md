---
name: project-init
description: >
  新项目的标准化初始化流程与目录约定。适用于任何新项目（含多模块仓库、
  库项目、应用项目）。在搭建新仓库、制定文档结构、规划跟踪机制时使用。
  跨项目通用。
---

# 项目初始化与目录约定

> 核心目标：**让新项目"出生即合规"**——开箱即拥有可被 AI 安全协作的目录、
> 文档、跟踪三件套。

## 一、初始化清单（按顺序执行）

1. **建立根目录入口**：创建 `README.md`（1 段简介 + 链接）和 `AGENTS.md`（AI 入职）
2. **建立 `.qoder/`**：创建 `skills/` 和 `rules/` 两个子目录
3. **建立对外文档**：创建 `docs/` 目录，文件名用中文
4. **建立对内知识**：创建 `INTERNAL/`（或 `AI/`）目录
5. **建立跟踪三件套**：根目录平级创建 `PROJECT_PLAN.md` / `PROJECT_BUG.md` / `PROJECT_ACCEPTANCE.md` / `PROJECT_AUDIT_REPORT.md`

## 二、目录结构标准

```
my-project/
├── README.md                # 入口（人类 + AI 都看到）
├── AGENTS.md                # AI 入职手册（项目全貌 + 红线速查）
│
├── .qoder/                  # 工具配置（仅 Qoder 引擎读取）
│   ├── skills/              # 按需加载的技能
│   └── rules/               # 自动注入的强约束
│
├── docs/                    # 对外文档（外部使用者视角）
│   └── *.md                 # 中文文件名
│
├── INTERNAL/                # 对内知识（项目成员视角）
│   └── *.md                 # SKILL.md / ARCHITECTURE.md / PROJECT_AUDIT_REPORT.md
│
├── PROJECT_PLAN.md                  # 根目录跟踪（计划）
├── PROJECT_BUG.md                   # 根目录跟踪（Bug，顺序编号 #1…）
├── PROJECT_ACCEPTANCE.md            # 根目录跟踪（QA 验收）
└── PROJECT_AUDIT_REPORT.md          # 根目录跟踪（架构审计，可选）
```

## 三、三层分离原则

| 层 | 目录 | 加载方式 | 内容性质 |
|----|------|----------|----------|
| **入口层** | `README.md` / `AGENTS.md` | 永久可见 | 项目门面 |
| **技能层** | `.qoder/skills/` | 按需 @ 触发 | 知识/方法论 |
| **强制层** | `.qoder/rules/` | always-on / glob 触发 | 红线/强约束 |
| **用户层** | `docs/` | 人类阅读 | 对外 API、上手 |
| **资产层** | `INTERNAL/` | 人类 + AI 引用 | 对内架构、决策 |
| **跟踪层** | 根目录 4 个 .md | 全员可见 | 计划/缺陷/验收/审计 |

**禁止混淆**：
- ❌ 把对外文档塞进 `INTERNAL/`
- ❌ 把对内知识塞进 `docs/`
- ❌ 把跟踪文档埋到子目录
- ❌ 把项目内容塞进 `.qoder/`

## 四、.qoder/ 子目录分工

| 子目录 | 加载方式 | 典型文件 | 决策原则 |
|--------|----------|----------|----------|
| `skills/` | AI 主动 @ 触发 | code-standard、project-init、component-pattern、workflow | "什么时候需要看这个？" |
| `rules/` | 会话开始 / glob 匹配自动注入 | red-lines、less-lint | "什么时候必须强制执行？" |

**判断口诀**：
- **"删了它 Qoder 还工作吗？"** → 否 → `.qoder/`
- **"改一行需要重启 Qoder 吗？"** → 是 → `.qoder/`
- **"是工具行为还是项目知识？"** → 行为 → `.qoder/`，知识 → `INTERNAL/`

## 五、跟踪文档最小骨架

### PROJECT_BUG.md
```markdown
# Bug 跟踪
## #1 [BUG-001] 简述
- 发现时间、根因、修复、回归验证
```

### PROJECT_ACCEPTANCE.md
```markdown
# 验收清单
## 主题/模式切换
- [ ] 场景 1
## 关键组件
- [ ] 默认/悬停/按下/禁用 四态正确
```

### PROJECT_AUDIT_REPORT.md
```markdown
# 审计报告
## v1.0 审计（YYYY-MM-DD）
- 范围、结论、对应 BUG 编号
```

### PROJECT_PLAN.md
```markdown
# 开发计划
## 当前阶段：v1.0
- [ ] 任务 1
- [ ] 任务 2
```

## 六、命名规范

| 类别 | 命名风格 | 示例 |
|------|----------|------|
| 对外文档（`docs/`） | 中文 | 快速上手.md、主题系统.md |
| 对内知识（`INTERNAL/`） | 英文大写 | SKILL.md、ARCHITECTURE.md |
| 跟踪文档（根目录） | 英文大写 | PROJECT_BUG.md、PROJECT_ACCEPTANCE.md |
| 跟踪项编号 | `[BUG-NNN]` 顺序递增 | #1 [BUG-001] |

## 七、避免的反模式

| 反模式 | 后果 | 正确做法 |
|--------|------|----------|
| 项目结构同时写进 3 个文件 | 信息过期不一致 | 单一信息源：只写在 `AGENTS.md` 一处 |
| 跟踪文档埋进子目录 | 没人看 → 失效 | 根目录平级 |
| `INTERNAL/` 改名 `docs/internal/` | 对内对外混在一起 | 根目录平级，命名清晰 |
| 用 `.internal/` 加点隐藏 | 团队成员不知道存在 | 普通目录 + 大写醒目 |
| skill 文件加得过多 | 维护成本飙升 | 不超过 5 个，宁缺毋滥 |

## 八、起步最小 skill 集

新项目起步时 `.qoder/skills/` 只复制 2 个通用 skill：

1. **`code-standard.md`** —— 跨项目可复用的通用编码规范
2. **`project-init.md`** —— 本文件，跨项目可复用的项目初始化约定

后续按需扩展：
- 出现项目特定技术约束（颜色/CSS/交互） → 新建 `project-constraints.md`
- 出现组件设计模式需求 → 新建 `component-pattern.md`
- 出现构建/测试/发布命令 → 新建 `workflow.md`
- **硬上限 5 个 skill**

## 九、与其他 skill 的关系

| 文件 | 关系 |
|------|------|
| `code-standard.md` | 写代码时看（命名/格式/安全）—— 与本 skill **正交** |
| `project-constraints.md` | 项目特定技术约束（颜色/交互/CSS）—— 项目建好后再建 |
| `component-pattern.md` | 组件设计模式—— 项目建好后再建 |
| `workflow.md` | 开发期工作流（构建/测试/发布）—— 互补 |
| `AGENTS.md` | 项目特定，引用本 skill 描述的"骨架"—— **不要重复内容** |
