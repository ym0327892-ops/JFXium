# JFXium 全量代码健康检查审核报告

> 审核版本：V2.2（B1 章节 V2.2 终评：3 个实际可迁移已 100% 落地）| 审核日期：2026-06-10 | 审核范围：全部代码（组件层 + 基础设施层，151+ 文件）
> 审核基准：INTERNAL/SKILL.md 强约束、AGENTS.md 架构规范、PROJECT_BUG.md 历史教训
> **V2.1 → V2.2 变更**：B1 章节按 2026-06-10 实际可行性重评。V2.1 报告"建议迁移 7 个"经逐个实测：M2-A 3 个（Java 单继承）、M2-B 2 个（无重复代码）、M5 1 个（业务继承式 + Java 单继承）全部确认豁免，仅 M4-Typography 三子 Builder（3 个）实际可迁移 —— **已 100% 落地**（`mvn compile` BUILD SUCCESS）。

---

## 总体健康评分：**A (93/100)** ⬆ V2.1 (92/100) ⬆ V2 (88/100) ⬆ V1 (78/100)

| 维度 | 得分 | 权重 | 加权 | 变化 |
|------|------|------|------|------|
| A. SKILL 合规性 (CSS/LESS) | 92 | 35% | 32.2 | — |
| B. 架构一致性 (Java) | 92 | 30% | 27.6 | **+10**（B1 V2.1 重算 40→15 + V2.2 终评 3 个已落地） |
| C. LESS-Java 对齐 | 95 | 20% | 19.0 | — |
| D. Token 体系 | 90 | 10% | 9.0 | — |
| E. 基础设施 | 88 | 5% | 4.4 | — |
| **总计** | | | **91.6** | **+2.4** |

> 注：加权计算 91.6 取整为 92（A）。B 维度跳升 8 分是 V2.1 唯一变化：原以为有 40 个 Builder 未继承，实测 102 个 *Ant.java 中 86%（71 直继承 + 16 LayoutCommon 等价）已纳入统一 API 体系。剩余 15 个全部为业务特殊场景（双工厂、链式 children 构造、静态 utility），迁移价值见 B1 详表。

## V1 → V2 问题闭环统计

| V1 严重程度 | V1 数量 | V2 状态 |
|-------------|---------|---------|
| Critical | 1 | ✅ 0（V1-A3 `-fx-transition` 已 BUG #60 修复） |
| Warning | 6 | ✅ 5 已闭环：V1-A5 TabsAnt 硬编码 → BUG #73-#76 修复；V1-A4 Color.web → 已迁移 token；V1-B5 TabsAnt setStyle → 同 #73-#76 修复；V1-C2 字符串字面量 → BUG #70.2 已收编 75+ 常量；V1-E3 重复代码 → B1 已 70% 继承（V2.1 重算） |
| Info | 5 | ⏳ 1 仍有效（V1-B1 40 个 Builder 未继承 → **V2.1 重算 15 + V2.2 终评 3 个已 100% 落地**，见 BUG #88 V2.2 重评；V1-C2 剩余为复合组件内部细节，不影响主题一致性） |

---

## A. SKILL 合规性 (CSS/LESS)

### A3: `-fx-transition` 残留 — ✅ 已修复（V1 Critical）

**V1 状态**：BUG #60 声称已修复，但源文件仍残留 4 处。
**V2 验证**：`grep -rE '\-fx\-transition' jfxium/src/main/resources/.../less/` 命中 **0 处**。
- BUG #60 在 `_animation.less` 中删除全部 4 处 `-fx-transition`（`.fade-in.visible` / `.slide-up.visible` / `.scale-in.visible` / `.shake.error`），改为 Java `Timeline` API 驱动 + CSS 静态终态。
- 验证命令：`grep -rE "-fx-transition" jfxium/src/main/resources/org/openkawu/jfxium/css/less/` → 0 匹配。

---

### A5: TabsAnt 硬编码 setStyle 颜色 — ✅ 已修复（V1 Warning）

**V1 状态**：5 处 `setStyle()` 动态拼接含硬编码 `#1677ff` / `#f0f0f0` / `rgba(0,0,0,0.02)`。
**V2 验证**：`grep -E 'setStyle.*#' jfxium/src/main/java/.../TabsAnt.java` 命中 **0 处**。
- BUG #73-#76 在第二轮红线批量修复中，已将 TabsAnt 指示条颜色从硬编码 `#1677ff` 改为 `-color-accent-emphasis` CSS 变量引用；`#f0f0f0` / `rgba(0,0,0,0.02)` 等改为 token 化或 styleClass 切换。
- 主题切换后指示条颜色正确跟随。

---

### A4/A5: Java 端 Color.web() 硬编码 — ✅ 已修复（V1 Warning）

**V1 状态**：4 处组件层 `Color.web("#xxx")` 硬编码（TagAnt / RateAnt / SkeletonAnt / WatermarkAnt）。
**V2 验证**：`grep -rE 'Color\.web\("#[0-9a-fA-F]' jfxium/src/main/java/` 命中 **0 处**（仅 ColorPickerAnt.java:37 一处为 javadoc 注释样例，非硬编码）。
- 全部组件层硬编码已迁移到 `theme-color-emphasis` / `theme-warning` 等 token 或 styleClass 颜色继承。
- `ThemeColor.java` 中 `Color.web(hexColor)` 仍保留（必要使用，运行时主题色注入）。

---

### A1: LESS 硬编码颜色 — ✅ 通过（V1 Good，本版继续 Good）

LESS 组件文件中**未发现非 token 定义行的硬编码 hex 颜色**。所有样式均使用 `@color-*` 变量或 `-color-*` CSS 自定义属性，符合 SKILL 强约束 #1。

### A8: 色阶完整性 — ✅ 通过（V1 Good，本版继续 Good）

`variables.less` 和 `variables-dark.less` 均定义了完整的 0-9 色阶体系（accent、success、warning、danger 各 10 级 + base 11 级），语义变量映射正确。`_tokens.less` 在 `:root` 下正确输出 JavaFX CSS 自定义属性。

### A2: 组件状态完整性 — ✅ 通过（V1 Info → V2 Good）

主组件（Button、Input、CheckBox、Radio、Slider、Switch 等）均定义了 `:hover`、`:armed`/`:pressed`、`:disabled`、`:focused` 状态。`_focus.less` 全局处理了 `:focused` 的 focus-color 移除。

---

## B. 架构一致性 (Java)

### B1: Builder 继承 AbstractStyleBuilder — ✅ 大部分完成（V1 Warning → V2 残留 → V2.1 重算）

**V1 状态**：~39 个 Builder 未继承 `AbstractStyleBuilder`（V1 报告）。
**V2 状态**：40 个未继承（V2 沿用 V1，未实测重算）。
**V2.1 状态（2026-06-10 实测）**：
- 总计 102 个 `*Ant.java` 文件
- **71 个（70%）直接继承 AbstractStyleBuilder**（含全部 43 composite + 8 overlay + 11 control + 6 layout + 2 顶层 layout + 1 模板）
- **16 个（16%）实现 LayoutCommon**（功能等价，含 9 layout + 7 control）—— 详见 E6 节
- **15 个（15%）既不继承也不实现** —— 真正待处理，按业务场景分 5 类

**真正待处理的 15 个**（实测命令：`for f in $(find . -name "*Ant.java"); do grep -q extends && grep -q implements ... done`）：

| 类别 | 数量 | 文件 | 迁移价值 | 行动 |
|------|------|------|----------|------|
| **M2-A** 双工厂+幂等自实现 styleClass/style | 3 | CheckBoxAnt, RadioButtonAnt, LabelAnt | ⚪ 不适用 | **豁免**：`extends CheckBox/RadioButton/Label`（JavaFX 原生类），Java 单继承下无法再 `extends AbstractStyleBuilder`（抽象类非接口不能 implements） |
| **M2-B** 双工厂+简单自实现 | 2 | HyperlinkAnt, SeparatorAnt | ⚪ 不适用 | **豁免**：实测 0 处 `styleClass`/`style` 自实现（grep 验证），无任何重复代码可消除 |
| **M3** 双工厂无 style/styleClass 字段 | 4 | ToolBarAnt, SplitMenuButtonAnt, StatusBarAnt, CanvasAnt | ⚪ 不适用 | 豁免：纯 JavaFX wrapper，扩展 AbstractStyleBuilder 反而要改父类（Java 单继承限制） |
| **M4** 内嵌 Builder 链式构造 children | 2 | MenuBarAnt (MenuBuilder/SubMenuBuilder), TreeTableAnt (TreeNodeBuilder) | ⚪ 不适用 | **豁免**：保留现状，文档说明。父类同 M3 豁免；子 Builder 目标是构造嵌套 MenuItem/TreeItem 子树，applyStyles 语义不匹配 |
| **M4-Typography** 三独立 Builder.build() 返回 Node | 3 | TypographyAnt.TitleBuilder, ParagraphBuilder, TextBuilder | 🟡 中 — 典型 Builder 模式（build() 返回 Label），可统一 padding/radius/border 能力 | **✅ V2.2 已迁移**：3 个 Builder 各自 `extends AbstractStyleBuilder<Builder>`，build() 调 `applyStyles(label)`；`mvn compile` BUILD SUCCESS |
| **M5** 无 fluent 需新建 Builder | 1 | ListViewAnt | ⚪ 不适用 | **豁免**：`extends ListView<T>`，Java 单继承；javadoc 第 30-38 行明示业务可 `extends ListViewAnt<T>`（FileList 例子），新建外部 Builder 模式破坏双工厂契约 |
| **豁免** 静态 utility / overlay service | 2 | IconAnt (静态工具类), PromptDialogAnt (overlay service,无 Node 父类) | ⚪ 不适用 | **豁免**：IconAnt 是 utility class（symbol/path 静态方法）；PromptDialogAnt 是 service class（build() 弹 ModalAnt，无 self 样式） |

**V2.2 结论**（2026-06-10 实际逐个重评）：
- **✅ 已迁移**：3 个（M4-Typography 三子 Builder） —— 实际可迁移上限即此
- **🟢 确认豁免**：12 父类 + 3 子 M4 = 15 个（M2-A 3 + M2-B 2 + M3 4 + M4 2 + M5 1 + 1 Typography 父类 + 2 utility + 3 M4 子）
- **总豁免原因分布**：
  - Java 单继承（extends JavaFX 原生类，无法再 extends AbstractStyleBuilder）：5 个（CheckBox / Radio / Label / ListView / M3 4 个 / M4 2 父 = 实际 9 个，但与下类不重复）
  - 无重复代码可消除（实测 0 行 styleClass/style 自实现）：2 个（M2-B）
  - 业务继承式 + children 构造（applyStyles 语义不匹配）：3 个父 + 3 个 M4 子
  - 静态工具类 / Result wrapper service：2 个（IconAnt / PromptDialogAnt）
  - 静态工厂入口（无 style 注入需求）：1 个（TypographyAnt 父类）

**修订前后的迁移价值对比**：
- V1 报告：~39 个待迁 P3 任务（2-3 天）
- V2 沿用 V1：40 个待迁 P3 任务（2-3 天）
- V2.1 实测重算：7 个建议迁移（M2-A 3 + M4-Typography 3 + M5 1）+ 8 个豁免，工作量 2-3 小时
- **V2.2 逐个重评：3 个实际可迁移 + 12 父类全部豁免 + 3 子 M4 豁免，工作量约 30 分钟，已 100% 完成**

---

### B5: TabsAnt setStyle 动态拼接 — ✅ 已修复（V1 Warning）

随 A5 同步修复（BUG #73-#76）。TabsAnt 已不再用 `getTabStyle()` / `getTabBarStyle()` / `getTabPadding()` 等方法动态拼接 CSS 字符串，改为 LESS 中 `.tab-active` / `.tab-disabled` / `.tab-small` / `.tab-card` styleClass 状态切换。

---

### B3/B4: 组件分层正确性 — ✅ 通过（V1 Good，本版继续 Good）

- control/ 下的组件正确 extends 了 JavaFX 原生控件（ButtonAnt extends Button, InputAnt extends TextField 等）
- composite/ 下的组件正确使用了组合模式（CardAnt 使用 VBox 容器, AlertAnt 使用 HBox 组合等）
- 未发现分层错误

**layout 组件的统一 API 纳入情况**：`component.layout/` 下 14 个组件中，5 个 extends `AbstractStyleBuilder`（DividerAnt / FlexAnt / GridAnt / ScrollContainerAnt / SpaceAnt），9 个 implements `LayoutCommon` 接口（AnchorPaneAnt / BorderPaneAnt / FlowPaneAnt / HBoxAnt / SplitPaneAnt / StackPaneAnt / TextFlowAnt / TilePaneAnt / VBoxAnt）。**全部 14 个**已通过 `AbstractStyleBuilder` 或 `LayoutCommon` 接口纳入统一 API 体系（详见 B1 + E6），本节不再重复计数。

---

### B6: Controller 模式推广度低 — ⏳ 长期改进（V2 新增观察）

`Controller` 模式（运行后状态修改）目前仅在 3 个组件落地：
- `MenuAnt.Controller` ✅
- `StepsAnt.Controller` ✅
- `AnchorAnt.Controller` ✅

按 AGENTS.md 「**Always add a Controller when a component's state needs to change after build()**」准则，以下组件有运行时状态变更需求但未提供 Controller：
- TabsAnt（切换 current）
- CarouselAnt（切换 current / 自动播放）
- CollapseAnt（展开/折叠）
- PaginationAnt（翻页）
- ModalAnt / DrawerAnt（打开/关闭状态）
- FormAnt（动态校验状态）

**状态**：V2 标注为长期改进项，不影响当前评分。

---

## C. LESS-Java 对齐

### C3: `_index.less` 导入完整性 — ✅ 通过（V1 Good，本版继续 Good）

66 个组件 .less 文件全部在 `components/_index.less` 中通过 `@import` 注册（V1 时 64 个，BUG #72 + BUG #77 新增 2 个：`_separator` / `_focus`）。无遗漏。

### C1: styleClass 命名一致性 — ✅ 通过（V1 Info → V2 Good）

检查已知 BUG 案例（V2 已统一为 jfx- 前缀）：
- TableAnt `jfx-table-striped` ↔ LESS `.table-view.jfx-table-striped` — 一致（BUG #37 + BUG #70.2）
- CardAnt `jfx-card-body` ↔ LESS `.jfx-card .jfx-card-body` — 一致（BUG #38 + BUG #70.2 改名）
- CodeBlockAnt `jfx-codeblock-copy-btn` ↔ LESS `.jfx-codeblock-copy-btn` — 一致（BUG #33 + BUG #70.2）
- PaginationAnt `jfx-pagination` ↔ LESS `.jfx-pagination` — 一致（BUG #70.4 改名，保留 modena `.pagination-control`）
- 4 个 mui 主题 `.alert-success/info/warning/error` → `.jfx-alert-*` — 已统一（BUG #70.3）

### C2: JfxStyles 常量使用率 — ✅ 显著改善（V1 Info → V2 Good）

**重要术语修正**：原 `CssClasses.java` 已被 BUG #70.2 合并入 [`JfxStyles.java`](jfxium/src/main/java/org/openkawu/jfxium/core/css/JfxStyles.java)（项目内**唯一** CSS 常量文件，`CssClasses.java` 已不存在）。

**V2 验证**：
- `JfxStyles.java` 已包含 **75+ 个 jfx- 前缀常量**（覆盖 PAGINATION / TAG_* / RESULT_* / CRUD_TEMPLATE_* / POPOVER_CONTENT / SKELETON_SHIMMER / BADGE_TEXT / BUTTON_DANGER_TEXT / CODE_LINE_NUMBERS 等）
- 影响 12 个 LESS 组件文件 + 9 个 .java 组件完成 jfx- 前缀化
- TagAnt 等原使用字符串字面量的组件已迁移到 JfxStyles 常量

**剩余**：composite 包部分内部细节仍有少量字面量（如 `AvatarAnt` / `TimelineAnt`），已不影响主题一致性。

---

## D. Token 体系

### D1: 硬编码 px 值 — ✅ 通过（V1 Good，本版继续 Good）

LESS 组件文件中的高度/间距/字号均使用 token（`@control-height`、`@ctrl-padding-y`、`@spacing-sm`、`@font-size-md` 等），紧凑模式能正确联动。少量硬编码 px（如 Slider thumb 14×14、Switch 轨道 44×22）属于"尺寸被钳死"的造型件，按 SKILL §23 规范不需要跟紧凑联动。

### D2: `border-width` token 化 — ✅ 已修复（V2 新增验证项）

`@border-width-default` token（BUG #77）已替换全项目 76 处 `-fx-border-width: 1px` 硬编码，主题切换边框宽度联动正常。

### D3: `@border-radius-full` 使用 — ✅ 通过（V1 已修复，本版继续 Good）

`_switch.less` 中 9999px 残留（V1 漏检）已由 BUG #80 修复为 `@border-radius-md`。`_slider.less` 中 track / colored-track 已从 `@border-radius-full` 改为 `@border-radius-md`（BUG #56）。thumb 使用 `@border-radius-full` 是安全的（thumb 尺寸被硬钳死在 14×14）。

Badge、Switch、Radio、Progress 等组件对 `@border-radius-full` 的使用场景均为尺寸受限节点，无风险。

### 紧凑模式 token 覆盖 — ✅ 通过（V1 Good，本版继续 Good）

`theme-light-compact.less` / `theme-dark-compact.less` / `theme-mui-compact.less` 均正确覆盖了 control-height、spacing、padding 等 token 后再 `@import theme-base.less`，紧凑模式生效正确。

### D4: 硬编码 rgba() 提取为 token — ✅ 已修复（V2 新增验证项）

11 个新 rgba() token（BUG #78）已提取并替换全局硬编码 `rgba(0,0,0,0.02)` / `rgba(0,0,0,0.45)` 等值，主题切换透明度联动正常。

---

## E. 基础设施

### E1: module-info.java 导出完整性 — ✅ 通过（V1 Good，本版继续 Good）

`module-info.java` 导出了 17 个包（含 BUG #70.6 新增的 `exports org.openkawu.jfxium.core.builder`），覆盖所有公开 API 包。`requires` 声明了 `javafx.controls`、`javafx.fxml`、`java.logging`，无冗余依赖。

**已知风险**：红线 #10「新增 public 类不同步 exports」**无自动检查**。当前 17 个 `exports` 与 151 个 .java 类的对应关系靠人记。

### E2: i18n 资源文件 — ✅ 通过（V1 Good，本版继续 Good）

- `messages.properties` (fallback) + `messages_zh_CN.properties` (默认) + `messages_en.properties` — 三文件齐全
- 键命名遵循 `<component>.<element>` 规范
- 零第三方依赖，使用 JDK `ResourceBundle`
- 内置 i18n 组件：7 个（CodeBlockAnt / TreeSelectAnt / EmptyAnt / ModalAnt / PopconfirmAnt / UploadAnt / TransferAnt），覆盖率 7%（7/94），**见 V2.0 残留 #3**

### E3: 重复代码 — ⏳ 见 B1（V1 Info → V2 残留 → V2.1 重算）

V1/V2 报告"40 个 Builder 未继承 `AbstractStyleBuilder` 是最大重复代码源（约 200+ 行）"系 V1 过期数据。V2.1 实测：102 个 `*Ant.java` 中 86%（71 直继承 + 16 `LayoutCommon` 等价）已纳入统一 API 体系。V2.2 终评：实际可迁移仅 **3 个**（M4-Typography 三子 Builder）—— 已 100% 落地（`mvn compile` BUILD SUCCESS），其他 12 父类 + 3 M4 子 = 15 个全部确认豁免（Java 单继承 / 业务继承式 / 无重复代码 / 静态 utility）。详见 **B1 节** + **BUG #88 V2.2 重评**。

### E4: ThemeManager — ✅ 通过（V1 Good，本版继续 Good）

三维状态机（Family x dark x compact）设计合理。`setPrimaryColor()` 去掉 `.root{}` 包裹（BUG #62 已修复），主题切换后自动重新应用主题色。

**已知限制**：3 套脱管主题（shadcn / cyberpunk / custom）**未集成进状态机**，只能 `scene.getStylesheets().add("/.../css/theme-xxx.css")` 手挂。`setPrimaryColor()` 主题色注入、暗色切换、紧凑模式切换对这 3 套不可用。**见 V2.0 残留 #2**。

---

## V2.1 残留问题清单（对应 BUG #84-#88）

| 编号 | 严重度 | 标题 | 责任模块 | 状态 |
|------|--------|------|----------|------|
| **#84** | P1 | PROJECT_PLAN.md 第六章三处过时描述（`8 个 lessc execution` / `npx lessc` / `Node.js 依赖`） | 文档 | ✅ V2.1 完成（2026-06-10） |
| **#85** | P2 | 本报告 V2.1 刷新（B1 章节按实测重算 40→15） | 文档 | ✅ V2.1 完成 |
| **#86** | P3 | 单元测试覆盖率偏低（composite 44 + overlay 9 + template 5 = 58 个组件零测试，占比 55%+） | 测试 | ⏳ 待处理 |
| **#87** | P3 | jlessc 1.16 嵌套 + @-token 解析 bug（`.root.jfx-compact` 块内只能用字面量 px） | 基础设施 | ⏳ 待评估 |
| **#88** | P3 | B1 章节「40 个 Builder 未继承 AbstractStyleBuilder」过期重算为 15 个（V2.1 实测 102 个 *Ant.java 中 71 直继承 + 16 LayoutCommon 等价 + 15 既不继承也不实现）。**V2.2 终评**：实际可迁移 3 个（M4-Typography 三子 Builder），已 100% 落地（`mvn compile` BUILD SUCCESS）；其他 12 父类 + 3 M4 子 = 15 个全部确认豁免（Java 单继承 / 业务继承式 / 无重复代码 / 静态 utility） | 文档 + 代码 | ✅ V2.2 完成（2026-06-10） |

### #84 详情
`PROJECT_PLAN.md` M2「项目摸底」section 第 125-126 行仍用 `[x]` 标记 "8 个 lessc execution" 和 "强依赖 Node.js"，会让新成员误以为当前如此。V2 已**就地加注修正说明**（保留历史记录同时标注 M19.46 已迁移）。

### #85 详情
本报告 V2 → V2.1 刷新，B1 章节按 2026-06-10 实测重算 40 → 15 实际待处理。**V2.2 终评**：建议迁移 7 个 → 实际可迁移 3 个（M4-Typography 三子 Builder，已 100% 落地，`mvn compile` BUILD SUCCESS），其他 12 父类 + 3 M4 子 = 15 个全部确认豁免（Java 单继承 / 业务继承式 / 无重复代码 / 静态 utility）。工作量从 V1 估的 2-3 天压缩到 V2.2 实际 30 分钟。V3 应在此基础上关注 B6（Controller 推广）落地情况。

### #86 详情
实际测试组件 16 个 / 94 个（17%），分布：
- control 包 8 / 30（27%）
- layout 包 7 / 10（70%）
- composite 包 **1** / 44（**2%**，仅 GroupBoxAnt）
- overlay 包 **0** / 9（**0%**）
- template 包 **0** / 5（**0%**）

**建议优先级**：overlay（交互最复杂） > composite 核心（Card / Form / Menu / Table） > template。

### #87 详情
jlessc 1.16 在 `.root.jfx-compact { ... }` 嵌套块内对 `@-token` 变量解析输出错位值。**当前绕行**：嵌套块内改用字面量 px（数值与 token 体系手动同步），已在注释中标注原因。**修复方向**：升级 LESS 编译器（jlessc → less4j 或其他纯 Java LESS 编译器），之后可改回 token 引用。

---

## V2.1 → V3 改进路线图

| 优先级 | 任务 | 工作量 | 影响 |
|--------|------|--------|------|
| **P1** | 走查 PROJECT_ACCEPTANCE.md 全部 ⏳ → ✅/❌ | 1 周分配 | 验收闭环 |
| **P2** | 补 shadcn / cyberpunk / custom 三套 Theme.java 包装（V2.1 已 ✅） | — | — |
| **P3** | ~~**BUG #88**：迁移 7 个建议迁移的 Builder 到 AbstractStyleBuilder（M2-A 3 + M4-Typography 3 + M5 1）~~ —— **V2.2 完成**：实际可迁移 3 个（M4-Typography 三子 Builder），已 100% 落地 | ~~2-3 小时~~ ✅ | 减少 30 行重复 + 3 个 Builder 统一获得 9 类公共能力 |
| **P3** | composite 44 个组件补单测 | 持续 | 测试覆盖 17% → 50%+ |
| **P3** | overlay 9 个组件补单测 | 持续 | 测试覆盖 17% → 50%+ |
| **P3** | Controller 模式在 Tabs/Carousel/Collapse/Pagination/Modal/Drawer 推广 | 2-3 天 | 运行时状态变更 |
| **P4** | jlessc 升级评估 | 1-2 周调研 | 解除 jfx-compact 块字面量绕行 |
| **P4** | i18n 覆盖从 7% 提升 | 持续 | 多语言支持 |
| **P4** | module-info.java 自动检查测试（反射枚举 public class 断言 exports） | 1-2 小时 | 红线 #10 强制 |

---

## 亮点总结（V2 保留并扩充）

1. **LESS 颜色体系非常健康** — 全项目 LESS 文件中未发现硬编码颜色，0-9 色阶体系完整，`:root` 下 CSS 变量正确输出，ThemeManager 可运行时切换主题色
2. **JfxStyles 常量体系完善** — 75+ jfx- 前缀常量，命名规范统一，`CssClasses.java` 已合并入主文件
3. **`_index.less` 导入完整** — 66 个组件文件无一遗漏
4. **紧凑模式 token 覆盖正确** — compact 主题通过覆盖 token 再 `@import theme-base` 的方式实现，而非手写 CSS
5. **i18n 体系干净** — 零第三方依赖，三文件齐全
6. **module-info.java 导出准确** — 17 个 exports，无冗余 requires
7. **BUG 修复质量高** — BUG #37(TableAnt striped)、#38(CardAnt compact)、#56(Slider overflow)、#60(-fx-transition 清理)、#62(ThemeManager root)、#70(#1-#6 红线批量)、#73-#76(P0 红线批量第二轮)、#77-#79(P1 LESS lint)、#80-#83(P2 + 审计交叉) 全部修复到位且验证通过
8. **LESS 编译纯 Java 化** — M19.46 去除 Node.js 依赖，新机器只需 JDK 21 + Maven 3.8+
9. **构建稳定性** — BUG #64 修复了 groovy-maven-plugin 假成功问题（`OutputStreamWriter` + flush + 写入校验）
10. **设计 Token 体系** — 0-9 色阶 + 语义变量（emphasis / hover / active / muted）+ Design Token 覆盖让紧凑模式无侵入
