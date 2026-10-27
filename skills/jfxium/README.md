# JFXium Agent Skill

标准 Skill 包：`SKILL.md` 是自包含入口（可直接单文件复制），`references/` 是按需加载的深水区，
`scripts/` 可执行，`assets/` 放生成数据，`dist/` 是打包好可直接粘贴的单文件。

## 目录

```
skills/jfxium/
├── SKILL.md                     # 自包含技能入口（触发条件 + 工作流 + 红线 + 控件速查 + 片段）
├── references/
│   ├── rules.md                 # 完整 11 条红线 + API 契约
│   ├── native-map.md            # 原生 JavaFX → *Ant 完整对照
│   ├── recipes.md               # 表单/表格/弹窗/主题/i18n/线程完整片段
│   └── agent-prompt.md          # 非 Skill 型工具用的 system prompt
├── assets/catalog/
│   ├── components.md            # 全量组件详情（生成物）
│   ├── components.index.md      # 紧凑速查（生成物）
│   └── components.json          # 机器可读（生成物）
├── scripts/
│   ├── CatalogGen.java          # 重建 catalog
│   ├── Bundle.java              # 生成 dist/jfxium-skill.md
│   └── JfxLint.java             # 红线检查
├── integrations/
│   ├── cursor-jfxium.mdc
│   └── copilot-instructions.md
└── dist/jfxium-skill.md         # 单文件技能（SKILL.md + 组件速查）
```

## 三种接入方式

### 1. 单文件技能（Qoder / 任意支持单文件 skill 的工具）

```bash
mkdir -p .qoder/skills
cp skills/jfxium/SKILL.md .qoder/skills/jfxium.md
```

`SKILL.md` 已自包含（规则/控件速查/片段），不依赖同目录其它文件。

### 2. Skill 目录（Claude Code 等支持目录型 Skill 的工具）

```bash
mkdir -p .claude/skills
cp -r skills/jfxium .claude/skills/jfxium
```

Agent 读 `SKILL.md` 的 frontmatter 判断触发；`references/`、`assets/` 按需读取。

### 3. 纯聊天 / 不支持 Skill 的工具

直接粘贴 **`skills/jfxium/dist/jfxium-skill.md`**（SKILL 正文 + 组件速查索引，单文件、无外部依赖）。

### 规则型工具（Cursor / Copilot / AGENTS.md）

```bash
cp skills/jfxium/integrations/cursor-jfxium.mdc .cursor/rules/jfxium.mdc
cp skills/jfxium/integrations/copilot-instructions.md .github/copilot-instructions.md

# Codex / 其它 AGENTS.md 协议：追加 system prompt
cat skills/jfxium/references/agent-prompt.md >> AGENTS.md
```

## 使用

```bash
# 红线检查（ERROR 退出码 1）
java skills/jfxium/scripts/JfxLint.java src/main/java

# 维护者：重建组件目录与单文件包
java skills/jfxium/scripts/CatalogGen.java
java skills/jfxium/scripts/Bundle.java

# 一条命令验证整包是否最新（CI 同款）
java skills/jfxium/scripts/CatalogGen.java && java skills/jfxium/scripts/Bundle.java
git diff --exit-code -- skills/jfxium/assets/catalog skills/jfxium/dist
```

## 版本与发布

- `SKILL.md` 正文标注目标版本；catalog 的 `version` 来自 `pom.xml`。
- 下游 catalog 版本必须与依赖的 `jfxium` 版本一致。
- Release 附件包含 `jfxium-agent-skill-<version>.tgz`（整个 `skills/jfxium`，含 `dist/`）。
- CI 拦截漂移：catalog / dist 未随 API 或文档更新时直接失败。

## 设计说明

- **单文件优先**：先保证 `SKILL.md` / `skills/jfxium/dist/jfxium-skill.md` 能直接复制生效，再按需加载深水区。
- **渐进披露**：`SKILL.md` 只放触发条件、红线、速查与常用片段；完整 API 在 catalog / references。
- **可执行验收**：prompt 不能保证模型听话，`JfxLint` 用确定性规则兜底。
- **数据与提示词分离**：catalog 从源码生成，避免提示词里的 API 列表过期。
- **下一步**：可把 `assets/catalog/*.json` + `JfxLint` 包成 MCP server
  （`search_components` / `get_component` / `lint`），本目录结构无需变化。
