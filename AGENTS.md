# AGENTS.md

This file provides guidance to Qoder (qoder.com) when working with code in this repository.

---

## ⚡ 致命红线（违反直接打回）

> ⚠️ **速查表必须与 `.qoder/rules/red-lines.md` 严格 1:1 对齐**——11 条独立条目,不可合并。权威来源以该文件为准,本表仅作 AI 上下文快速提醒。

1. **禁止 `setStyle()` 写颜色/px** → 走 styleClass + LESS
2. **禁止 CSS `box-shadow`** → 用 `-fx-effect: dropshadow(...)`
3. **禁止 CSS `:active` / `:focus`** → 用 `:pressed`(`:armed`) / `:focused`
4. **禁止 `-fx-transition` 用于交互状态**（动画类 `.fade-in` 等除外）→ 直接定义状态颜色，用伪类切换
5. **禁止容器吞 padding** → 组合控件容器 `-fx-padding: 0`，下放到子节点
6. **禁止 `@border-radius-full`(9999px) 用于尺寸未钳制的节点** → track/进度条用 `@border-radius-md`
7. **禁止 `build()` 返回类型撒谎** → 返回什么就是什么，不包不装
8. **禁止新建 styleClass 不带 `jfx-` 前缀** → 避免与 modena 冲突
9. **禁止 `.arrow` 节点只设颜色不设 shape** → 必须显式 `-fx-shape` + min/pref 尺寸
10. **禁止新增 public 类不同步 `module-info.java` exports** → 否则下游不可见
11. **禁止业务代码 `new` 原生 JavaFX 控件**（Label/CheckBox/Hyperlink/Button/TextField/ComboBox/RadioButton/TextArea/Slider/ProgressBar/TableView/TreeView/...） → 统一走 `XxxAnt.create(...)` 链式 API。框架内部 `extends XxxAnt` 的实现类与临时 helper 除外。**理由**:绕过 styleClass + LESS 主题系统 / 失去幂等 toggle / 无法享受封装特性（委托 / Bug 自愈 / i18n 收口）

---

## .qoder Skills & Rules 索引

| 文件 | 定位 | 何时加载 |
|------|------|----------|
| `.qoder/rules/a.md` | AtlantaFX CSS 样式参照规则（always-on） | 始终生效 |
| `.qoder/rules/red-lines.md` | 致命红线（always-on） | 始终生效 |
| `.qoder/rules/less-lint.md` | LESS 编写规范 | 编辑 `*.less` 时自动触发 |
| `.qoder/skills/code-standard.md` | 通用编码技能（跨项目复用） | 编写/审查/重构代码时 |
| `.qoder/skills/project-constraints.md` | 项目技术约束（色阶/交互/CSS） | 写组件代码前 |
| `.qoder/skills/component-pattern.md` | 组件设计模式（微组件/反模式） | 新建/重构组件前 |
| `.qoder/skills/workflow.md` | 构建/测试/调试工作流 | 执行构建/测试/发布时 |
| `.qoder/skills/design-reference.md` | 设计风格参考体系（JetBrains/Qt/桌面工具） | 新建组件、调整样式、审查视觉一致性时 |
| `.qoder/skills/demo-discipline.md` | jfxium-demo 工程项目展示纪律（JFXium 控件优先 + util 孵化 + 整合评审） | 编写/修改/审查 jfxium-demo 代码时 |

---

## Project Overview

JFXium is a JavaFX UI framework inspired by professional desktop tools (JetBrains IDE, Qt Widgets, DBeaver). It wraps and enhances JavaFX native controls with a Builder-pattern API, LESS-based theming (11 built-in themes), and over 94 components covering controls, composites, overlays, layouts, and business templates. Zero FXML — all UI is constructed in pure Java code.

- **Java 21** (pom.xml `<java.version>21</java.version>`), JavaFX 21.0.6, Maven 3.8+
  - Note: README.md mentions Java 17+ but the actual build requires Java 21.
- **GroupId**: `org.openkawu`, **ArtifactId**: `jfxium`, **Version**: `1.0-SNAPSHOT`

---

## Key Reference Documents

| Document | Purpose |
|----------|---------|
| [.qoder/skills/project-constraints.md](.qoder/skills/project-constraints.md) | **Primary development specification** — color derivation, LESS rules, JavaFX CSS constraints, component design patterns |
| [PROJECT_PLAN.md](PROJECT_PLAN.md) | Development plan and progress tracking |
| [PROJECT_BUG.md](PROJECT_BUG.md) | Bug tracker and fix history (sequentially numbered, currently at #128) |
| [PROJECT_ACCEPTANCE.md](PROJECT_ACCEPTANCE.md) | QA acceptance checklist for manual UI verification |
| [.qoder/skills/pc-ui.md](.qoder/skills/pc-ui.md) | PC UI 设计标准（布局/间距/交互规范），涉及页面级布局、响应式设计时加载 |
| [.qoder/skills/project-init.md](.qoder/skills/project-init.md) | 项目初始化与脚手架指南，项目搭建、环境配置时加载 |
| [INTERNAL/SKILL.md](INTERNAL/SKILL.md) | 项目内部综合规范（动画/边框/构建/布局/主题），是约束的"宪法"级来源 |
| [INTERNAL/BUILDER_API_AUDIT.md](INTERNAL/BUILDER_API_AUDIT.md) | Builder API 审计报告——历史问题清单与重构参考 |
| [INTERNAL/QUICKSTART.md](INTERNAL/QUICKSTART.md) | 框架内部快速上手（与 `docs/cn/快速上手.md` 对偶,AI 视角） |
| `INTERNAL/{ANIMATION,BORDER,COMPONENTS,LAYOUT,THEME}.md` | 5 个领域专题深度文档,需要时按需加载 |

---

## Build & Run Commands

```bash
# Build core library only (includes LESS → CSS compilation)
./mvnw install -pl jfxium -DskipTests -q

# Run the Showcase Demo (automatically builds both modules)
./mvnw javafx:run -pl jfxium-demo

# Run all tests
./mvnw test -pl jfxium

# Run a single test class
./mvnw test -pl jfxium -Dtest=ClassName

# Run a specific test method
./mvnw test -pl jfxium -Dtest=ClassName#methodName

# Full clean build
./mvnw clean install -DskipTests
```

The demo `mainClass` is `org.openkawu.jfxium.jfxiumUiExample.JfxiumUiExampleApp` (configured in `jfxium-demo/pom.xml`).

---

## AI Standard Workflow

> 适用于所有"修改代码"类任务的标准操作序列。违反此序列极易留下半成品 bug（如本仓库 P0 修复曾因跳过验证步骤导致 `Write` 工具追加内容未被察觉）。

**完整 6 步流程**:

1. **搜索定位** → 用 `SearchCodebase` + `SearchMemory` 并行检索相关组件、已有 `*Ant` 实现、相关历史 bug
2. **读权威源** → 改动前必读 `.qoder/rules/red-lines.md`（不可降级为速查表）+ 涉及组件的 INTERNAL 专题文档
3. **改前快照** → 大文件 `Write` 重写前先 `wc -l` 备份,改后立即 `wc -l` 对比 + `grep -c "^(public class|public static class)"` 检查是否出现双重 class
4. **最小修改** → 优先用 `SearchReplace` 精准替换,避免大段重写引入未察觉的差异
5. **红线自检** → 修改完成后立即 `grep` 关键红线(如 `new (Label|Button|TextField|VBox|HBox|ComboBox|RadioButton|CheckBox|TextArea|Slider)\b`),命中即修
6. **编译验证** → `./mvnw install -pl jfxium -DskipTests -q` 必跑;`jfxium-demo` 也需要编译验证下游可见性

**关键陷阱**(踩过的坑,后续必须避免):

| 陷阱 | 表现 | 规避方法 |
|------|------|----------|
| `Write` 工具追加 | `wc -l` 翻倍 + 出现两个 `public class` | 改后三件套:`wc -l` + `grep class` + `grep 红线` |
| `.less` 改了不生效 | BUG #64:`groovy-maven-plugin` 假成功 | 在 CSS 输出文件中加 marker,重跑 `mvn generate-resources` 验证 |
| 速查表弱化红线 | 速查表合并了 `-fx-transition` 导致漏检 | 速查表必须与 `.qoder/rules/red-lines.md` 1:1 对齐 |
| i18n 字段锁死 Locale | `private String x = Messages.get("k")` 锁死 zh_CN | 字段初始化为 `null`,`build()` 中懒解析 + 监听 `Messages.localeProperty()` |
| 增量构建陈旧 `target/` | `install -DskipTests` 报 `NoSuchFileException *.class` / 诡异「找不到符号: 变量 Family/Preset、类 DrawerResult、IconAnt.Path」 | `-DskipTests` 只跳过测试**执行**不跳过测试**编译**;陈旧 `target/` 增量引用缺失 `.class` 即失败。先 `./mvnw clean` 再构建,**不要误判为源码缺符号**(主源码本身健康) |

---

## Project Structure (Multi-Module Maven)

```
JFXium/
├── pom.xml                          # Parent POM (Java 21, JavaFX 21.0.6, JUnit 5.12.1)
├── jfxium/                          # Core library module
│   ├── pom.xml                      # Dependencies: javafx-controls, javafx-fxml, ikonli
│   └── src/main/
│       ├── java/org/openkawu/jfxium/
│       │   ├── module-info.java     # Module declaration (see exports below)
│       │   ├── core/                # Foundation layer
│       │   │   ├── token/           # Design tokens (ColorToken, SpacingToken, etc.)
│       │   │   ├── theme/           # ThemeManager, Theme interface, ThemeColor
│       │   │   ├── css/             # JfxStyles, Background
│       │   │   ├── builder/         # AbstractStyleBuilder<SELF> — shared Builder base
│       │   │   ├── i18n/            # Messages (ResourceBundle, default zh_CN)
│       │   │   ├── form/            # FormContext, FormModel, Rule (validation)
│       │   │   ├── layout/          # OverlayManager, SceneLayout
│       │   │   ├── command/         # Command pattern
│       │   │   └── util/            # EventBus, TextFormatters, WindowManager, etc.
│       │   ├── component/
│       │   │   ├── base/            # Base component classes
│       │   │   ├── control/         # ~29 native JavaFX control wrappers
│       │   │   ├── composite/       # ~44 custom-built composite components
│       │   │   ├── overlay/         # ~9 popup/dialog overlay components
│       │   │   └── layout/          # ~12 layout container wrappers
│       │   ├── layout/              # Page-level layouts (AppShellAnt, LayoutAnt)
│       │   └── template/            # Business templates (CrudTemplate, LoginTemplate, etc.)
│       └── resources/org/openkawu/jfxium/
│           ├── css/                 # Compiled CSS outputs (11 theme files)
│           │   └── less/            # LESS source files
│           │       ├── variables-base.less   # Spacing, radius, fonts, control heights, mixins
│           │       ├── variables.less         # Light theme color tokens (0-9 scale)
│           │       ├── variables-dark.less    # Dark theme color tokens
│           │       ├── theme-base.less        # → @import "components/_index"
│           │       ├── theme-light.less       # → @import variables.less + theme-base.less
│           │       ├── theme-dark.less
│           │       ├── theme-*-compact.less   # Override size tokens, then @import theme-base
│           │       ├── theme-mui*.less        # Material Design variants
│           │       ├── theme-shadcn.less / theme-cyberpunk.less / theme-custom.less
│           │       └── components/            # 64 component-level .less files
│           └── i18n/                # messages.properties, messages_zh_CN, messages_en
└── jfxium-demo/                     # Showcase application (depends on jfxium)
    └── src/main/java/org/openkawu/jfxium/
        ├── jfxiumUiExample/         # Main demo entry (JfxiumUiExampleApp)
        ├── demo/showcase/           # ShowcaseDemo (component gallery)
        └── demo/admin/              # AdminDemo (CRUD reference)
```

---

## Module Exports (module-info.java)

```
module org.openkawu.jfxium {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;

    exports org.openkawu.jfxium.core.token;
    exports org.openkawu.jfxium.core.theme;
    exports org.openkawu.jfxium.core.css;
    exports org.openkawu.jfxium.core.layout;
    exports org.openkawu.jfxium.core.i18n;
    exports org.openkawu.jfxium.core.command;
    exports org.openkawu.jfxium.core.form;
    exports org.openkawu.jfxium.core.util;
    exports org.openkawu.jfxium.component.control;
    exports org.openkawu.jfxium.component.composite;
    exports org.openkawu.jfxium.component.overlay;
    exports org.openkawu.jfxium.component.base;
    exports org.openkawu.jfxium.component.layout;
    exports org.openkawu.jfxium.layout;
    exports org.openkawu.jfxium.template;
}
```

**⚠️ When adding new public classes**: You MUST sync the `exports` declarations here. A `public` class in a non-exported package will be invisible to `jfxium-demo` and downstream consumers. If adding new `requires` dependencies, also sync `jfxium/pom.xml`.

---

## Component Architecture

### Three component tiers

| Tier | Package | Description |
|------|---------|-------------|
| **control** | `component.control` | Thin wrappers over JavaFX native controls (`extends Button`, etc.) with Builder API and styleClass-based theming. Examples: ButtonAnt, InputAnt, ComboBoxAnt, TableAnt. |
| **composite** | `component.composite` | Custom components built from multiple JavaFX nodes. **Two coexisting patterns**: (1) Builder pattern — mostly for complex / multi-Builder / Controller components (CardAnt, AlertAnt, MenuAnt, FormAnt); (2) Inheritance pattern — for single-container composites that want LayoutCommon capabilities (BarAnt `extends HBoxAnt`). |
| **overlay** | `component.overlay` | Popup/dialog components using `Stage`, `Popup`, or `ContextMenu`. Examples: ModalAnt, DrawerAnt, DropdownAnt, MessageAnt. |

### Universal Builder pattern

Every component follows this pattern:
```java
MyComponentAnt comp = MyComponentAnt.create()   // static factory
    .property1(value)
    .property2(value)
    .onSomeEvent(handler)
    .build();                                     // returns the built Node/Control
```

### Inheritance-based composite pattern (M19.36+)

Simple composite components that are "one container + content" can inherit from `*Ant` layout classes to gain LayoutCommon capabilities without Builder boilerplate:

```java
public class BarAnt extends HBoxAnt {
    // BarAnt IS-A HBoxAnt — no separate Builder class needed
    // Auto-inherits LayoutCommon: background, borderRadius, borderXxx, padding, size...
    
    public static BarAnt create() { return new BarAnt(); }
    public BarAnt left(Node... nodes) { ...; return this; }
    public BarAnt build() {
        // Assemble children, return this
        return this;
    }
    
    // Must override ~30 LayoutCommon methods for covariant return (HBoxAnt → BarAnt)
    @Override public BarAnt background(Background bg) { super.background(bg); return this; }
    // ...
}
```

**When to use inheritance vs Builder**:
| Criterion | Inheritance (`extends VBoxAnt`) | Builder (`extends AbstractStyleBuilder`) |
|-----------|-------------------------------|----------------------------------------|
| Single root node | ✅ | ✅ |
| Need LayoutCommon | ✅ | ❌ (must go through applyStyles) |
| Has modify() / Controller | ⚠️ (type signature changes) | ✅ |
| Multi-Builder (bar/circle) | ❌ | ✅ |
| Complex Popup/Stage logic | ❌ | ✅ |

The `Abstract*Ant<SELF>` base classes (`AbstractVBoxAnt`, `AbstractHBoxAnt`, etc.) provide the generic self-type foundation. Public `*Ant` classes extend them as `AbstractVBoxAnt<VBoxAnt>` for a clean non-generic API.

### AbstractStyleBuilder<SELF>

The shared base class `org.openkawu.jfxium.core.builder.AbstractStyleBuilder<SELF>` provides:
- `style(String)` — inline CSS (use sparingly)
- `styleClass(String)` — add style classes (preferred)
- `padding(Insets)` / `padding(double)` — set padding
- `maxWidth/minWidth/prefWidth/maxHeight/minHeight/prefHeight(double)`
- `applyStyles(Node)` — apply accumulated styles in `build()`

All new Builder classes **must** extend `AbstractStyleBuilder` instead of re-implementing these fields.

### Controller pattern for runtime state changes

For components that need post-construction state modification (e.g., changing selected menu item, advancing steps), use the Controller pattern:
```java
// Builder exposes a controller() method
MenuAnt menu = MenuAnt.create().menu("File", ...).build();
MenuAnt.Controller ctrl = MenuAnt.controllerOf(menu);
ctrl.setSelectedKey("file");
```

This avoids rebuilding the entire component tree. **Always add a Controller** when a component's state needs to change after `build()`. Existing Controllers: `MenuAnt.Controller`, `StepsAnt.Controller`, `AnchorAnt.Controller`, `TabsAnt.Controller`, `CalendarAnt.Controller`, `PaginationAnt.Controller`.

### Builder return type contract

`build()` returns one of two types. Mixing them up produces subtle bugs (e.g., Modal that compiles but never appears).

| Type | Examples | `build()` returns | Usage |
|------|----------|-------------------|-------|
| **Direct node** | ButtonAnt, CardAnt, TableAnt, FormAnt | Direct Node (Button, VBox, BorderPane, etc.) | Add to container tree directly |
| **Result wrapper** | ModalAnt, DrawerAnt, DropdownAnt, MessageAnt, NotificationAnt | XxxResult object | Must call `.open(owner)` / `.show()` to display |

**Rule of thumb**: "persistently displayed in container tree" → direct node. "On-demand popup/overlay" → Result wrapper.

### Component design rules

1. **final JavaFX controls** (Button, CheckBox, etc.) must be composed, not extended. Use the composite pattern: wrap them as a field inside a container.
2. **Form components** (InputAnt, CheckBoxAnt, etc.) use an inheritance-based design (`extends` the native control).
3. **All colors/styles go through styleClass → LESS**, never inline `setStyle()` with hardcoded color values.
4. **New control** must: (a) add JfxStyles constants in `JfxStyles.java`, (b) add LESS styles in `components/_xxx.less`, (c) register in `components/_index.less`.
5. **Modifying native JavaFX control CSS** (`.button`, `.combo-box`, etc.): always read AtlantaFX source at `ant-design-ref/AntLantaFx/` first to verify selector hierarchy — never guess.

### New component checklist (6 steps)

1. Add `jfx-`-prefixed styleClass constants to `JfxStyles.java`
2. Create `components/_xxx.less` with component styles
3. Register `@import` in `components/_index.less`
4. Create Java class extending `AbstractStyleBuilder` (or inheriting from `*Ant` layout)
5. Confirm `module-info.java` exports the package
6. Add demo showcase in `jfxium-demo`

### JfxStyles naming convention

All styleClass constants go in [`JfxStyles.java`](jfxium/src/main/java/org/openkawu/jfxium/core/css/JfxStyles.java). **For new components, always use the `jfx-` prefix** to avoid clashes with modena built-in selectors (`.button`, `.label`, `.card`, etc.):

```java
// ✅ Correct: jfx- prefix avoids modena clash
public static final String MY_COMPONENT = "jfx-my-component";
public static final String MY_COMPONENT_HEADER = "jfx-my-component-header";

// ❌ Wrong: bare name may clash with modena or other CSS
public static final String MY_COMPONENT = "my-component";
```

**Existing inconsistency**: older components use bare names (`card`, `menu`, `form`, `steps`). These are legacy and should not be replicated in new code. The rule is: **new = `jfx-` prefix, always**.

**⚠️ `CssStyles.java` has been deleted** — it was dead code using old `-jfx-*` variable names incompatible with the current LESS `-color-*` token system. The only CSS constants file is `JfxStyles.java`.

---

## LESS Theming System

### Build pipeline

LESS source files are compiled to CSS by **jlessc** (pure Java, no Node.js required) via `groovy-maven-plugin` during the `generate-resources` phase. This runs automatically with `mvn compile` or `mvn install`.

**Themes compiled (11 CSS files)**: theme-light, theme-dark, theme-light-compact, theme-dark-compact, theme-mui, theme-mui-compact, theme-mui-dark, theme-mui-dark-compact, theme-shadcn, theme-cyberpunk, theme-custom. Of these, only the first 8 have Java wrapper classes (`*Theme.java`) accessible via `ThemeManager.applyTheme(...)`. The last 3 (shadcn / cyberpunk / custom) are loaded directly via `scene.getStylesheets().add("/org/openkawu/jfxium/css/theme-xxx.css")` and are **not** part of the ThemeManager state machine.

**⚠️ Build pitfall (BUG #64)**: If changes to `.less` files don't appear in compiled CSS, suspect the groovy-maven-plugin "fake success" issue — logs say "compiled successfully" but `Files.writeString` / Groovy `File.text` silently fail to write. Verify by adding a marker string to a CSS output file, re-running `mvn generate-resources -pl jfxium`, and checking if the marker was overwritten.

### Theme architecture

```
variables-{name}.less   →   Defines color tokens (0-9 scale)
variables-base.less      →   Defines size/spacing/radius tokens, mixins
theme-base.less          →   Imports components/_index.less (all component CSS)
theme-{name}.less        →   @import variables-{name}.less → @import theme-base.less
```

Compact themes additionally override size tokens (e.g., `@control-height: 28px`, `@spacing-sm: 6px`) before importing `theme-base.less`.

### Color token system (mandatory)

Every theme color must define a 0-9 scale:
```less
@color-accent-0: #e6f4ff;   // Lightest (hover backgrounds)
@color-accent-5: #1677ff;   // Base (primary emphasis)
@color-accent-6: #0958d9;   // Active/pressed
@color-accent-9: #001d66;   // Darkest
```

Semantic variables map to scale indices: `@color-accent-emphasis` → index 5, `@color-accent-hover` → index 0, `@color-accent-active` → index 6, `@color-accent-muted` → index 2.

### Critical CSS rules (from SKILL.md)

- **No `-fx-transition`** — JavaFX CSS does not support it. Define state colors directly (`.button:hover { ... }`).
- **No `box-shadow`** — Use `-fx-effect: dropshadow(gaussian, color, blur, spread, offsetX, offsetY)`.
- **No `padding` / `border`** — Always use `-fx-padding`, `-fx-border-color`, `-fx-background-radius`, etc.
- **Pseudo-class mapping**: Web `:active` → JavaFX `:pressed` or `:armed`; Web `:focus` → JavaFX `:focused`; selection → `:selected`.
- **SVG icons**: Use `-fx-shape: "M10 20..."` with `-fx-background-color`, never `background-image`.
- **All heights/paddings must use tokens** (e.g., `@control-height`, `@ctrl-padding-y`), not hardcoded px. This is how compact mode works — it overrides tokens.
- **`@border-radius-full` (9999px) only on size-clamped nodes** (fixed-size thumb, badge). Never on track/progress bars whose width is determined by parent layout.
- **Use native `-fx-border-*`** requires `-fx-border-style: solid` (modena defaults to `none`). Omit only when inheriting from controls that already define it (e.g., `.button`).

### Border strategy: background-insets vs native border

Two distinct border rendering techniques for different component types:

| Technique | When to use | Examples |
|-----------|-------------|----------|
| **`background-insets` layer stacking** | Interactive controls: multi-state (hover/focus/pressed), rounded corners, focus ring | ButtonAnt, InputAnt, ComboBoxAnt |
| **Native `-fx-border-*`** | Layout containers: 1px straight dividing lines, no rounded corners, no state switching | BarAnt, GroupBoxAnt, Separator |

- **Interactive controls use background-insets**: `-fx-background-color: borderColor, fillColor; -fx-background-insets: 0, 1;` — switching state only changes the first layer color, no redraw needed.
- **Layout containers use native border**: `-fx-border-color: transparent transparent -color-border-muted transparent; -fx-border-width: 0 0 1 0;` — simple, correct box-model, no anti-aliasing concerns on straight 1px lines.
- **Java visual structure ≠ CSS visual values**: Java code should only handle node structure and layout constraints (Hgrow, alignment). Visual spacing/padding/line-width must go through styleClass + LESS tokens. E.g., don't write `new HBox(8)` for spacing — use `-fx-spacing: @spacing-sm` in CSS.

---

## ThemeManager & Runtime Theming

```java
// Apply a theme
ThemeManager.getInstance().applyTheme(new LightTheme());
ThemeManager.getInstance().registerScene(scene);  // Watch for future theme changes

// Change primary color at runtime (injects CSS variables via data-URI stylesheet)
ThemeManager.getInstance().setPrimaryColor("#ff5722");
```

ThemeManager maintains a three-axis state machine: **Family** (Ant/MUI) × **dark** (boolean) × **compact** (boolean). The `ThemeManager.Family` enum exposes only `ANT_DESIGN` and `MUI` (note: `ANT_DESIGN` is a legacy enum name from the project's origin — the project no longer references Ant Design Web; the enum may be renamed to `DEFAULT` or `STANDARD` in a future release); the eight concrete `*Theme` classes are the 2×2×2 cartesian product of these three axes. Shadcn / Cyberpunk / Custom themes (no dark/compact variants) are intentionally excluded from this state machine — load them via `scene.getStylesheets().add("/org/openkawu/jfxium/css/theme-xxx.css")` instead. Theme switching re-applies the accent color automatically (BUG #62 fix).

---

## i18n System

- **Default locale**: `zh_CN` (Simplified Chinese)
- **Resources**: `jfxium/src/main/resources/org/openkawu/jfxium/i18n/messages*.properties`
- **API**: `Messages.get("key")`, `Messages.get("key", args...)`, `Messages.setLocale(Locale)`
- **Key naming**: `<component>.<element>` (e.g., `codeblock.copy`, `treeselect.placeholder`)
- Uses JDK `ResourceBundle` / `MessageFormat` — zero third-party deps
- Components with built-in i18n: CodeBlockAnt, TreeSelectAnt, EmptyAnt, ModalAnt, PopconfirmAnt, UploadAnt, TransferAnt

**⚠️ Builder field default trap**: Never initialize i18n fields at declaration time — that locks the locale at class-load time. Use `null` as placeholder and lazy-resolve in `build()`:
```java
// ❌ Wrong: field init locks locale forever
private String placeholder = Messages.get("treeselect.placeholder");

// ✅ Correct: null placeholder + lazy resolve
private String placeholder = null;
// In build():
String effective = placeholder != null ? placeholder : Messages.get("treeselect.placeholder");
```
For persistent components (not one-shot popups), register a `localeProperty()` listener when the user hasn't explicitly overridden the value.

---

## When Fixing Bugs

Follow the **dual traceability principle** (SKILL §22): when a demo bug is found, always investigate whether the root cause is in the framework (jfxium), not just the demo. **Never fix only the demo side.**

**`jfxium-demo` is the regression test suite.** These signals in demo code indicate a framework API gap that must be fixed at the source:
- Manual `key→label` mapping tables (→ callback should return the full object, not just key)
- Rebuilding entire component trees for state changes (→ needs a Controller)
- Inline `setStyle("-fx-...: -color-...")` string concatenation (→ missing Builder API)
- Callbacks declared but never wired in `build()` (→ dead callback bug)
- Repeated casts like `(VBox) component.build()` (→ `build()` return type not honest)

The bug tracker is `PROJECT_BUG.md` (currently at #128). New issues are numbered sequentially. The acceptance checklist is `PROJECT_ACCEPTANCE.md`.

### Defensive programming: null-safety & input validation

All Builder setter methods MUST defend against invalid inputs — this is a systematic requirement across all component packages (layout + composite). The rule is: **external input is untrusted by default**.

| Input type | Required behavior |
|------------|-------------------|
| Enum parameters | `null` → semantic default (e.g., `Type.DEFAULT`), never let NPE reach `switch` |
| Text parameters (`title`, `text`) | `null` → `""` empty string |
| Numeric parameters (`gap`, `size`, `count`) | Negative/NaN/Infinity → clamp to safe value (0 or min); count-like → min 1. Use `Double.isFinite(val) ? Math.max(0, val) : 0` pattern |
| Callback parameters (`onClose`, `onClick`, `action`) | `null` allowed — simply don't bind the handler |
| `Node...` / collection parameters | Allow empty; filter out null entries one-by-one |
| `maxSize` when paired with `minSize` | Ensure `max ≥ min` before passing to Region |

Additionally:
- **`build()` must be idempotent** — repeated calls must not accumulate spacer nodes, listeners, or temporary children.
- **Node parent lifecycle**: any content replacement must release the old parent before re-mounting (JavaFX single-parent rule).
- **FX thread boundary**: any code touching `snapshot()`, `Canvas`, or scene-graph Node rendering must check `Platform.isFxApplicationThread()` and switch if needed.
- **Module exports**: adding a `public` class in any package MUST sync `module-info.java` exports.

---

## Key Dependencies

| Dependency | Version | Notes |
|------------|---------|-------|
| JavaFX (controls, fxml) | 21.0.6 | Managed in parent POM |
| Ikonli (javafx + antdesignicons) | 12.3.1 | Icon library |
| jlessc | 1.16 | Pure Java LESS compiler (build-time only) |
| JUnit Jupiter | 5.12.1 | Testing |

**Removed dependencies** (no longer used): javafx-web, javafx-media.
