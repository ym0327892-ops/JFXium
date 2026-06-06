# JFXium 全量代码健康检查审核报告

> 审核日期：2026-06-05 | 审核范围：全部代码（组件层 + 基础设施层，150+ 文件）
> 审核基准：docs/SKILL.md 强约束、AGENTS.md 架构规范、BUG.md 历史教训

---

## 总体健康评分：B+ (78/100)

| 维度 | 得分 | 权重 | 加权 |
|------|------|------|------|
| A. SKILL 合规性 (CSS/LESS) | 70 | 35% | 24.5 |
| B. 架构一致性 (Java) | 75 | 30% | 22.5 |
| C. LESS-Java 对齐 | 85 | 20% | 17.0 |
| D. Token 体系 | 80 | 10% | 8.0 |
| E. 基础设施 | 85 | 5% | 4.3 |
| **总计** | | | **78.3** |

## 问题统计

| 严重程度 | 数量 |
|----------|------|
| Critical | 1 |
| Warning | 6 |
| Info | 5 |

---

## A. SKILL 合规性 (CSS/LESS)

### A3: `-fx-transition` 残留 (Critical)

**BUG #60 声称已修复，但源文件仍残留 4 处。**

| # | 文件 | 行 | 内容 |
|---|------|-----|------|
| 1 | `_animation.less` | 12 | `-fx-transition: opacity 200ms ease-out;` (.fade-in.visible) |
| 2 | `_animation.less` | 24 | `-fx-transition: all 200ms ease-out;` (.slide-up.visible) |
| 3 | `_animation.less` | 38 | `-fx-transition: all 200ms ease-out;` (.scale-in.visible) |
| 4 | `_animation.less` | 48 | `-fx-transition: translate-x 100ms ease-in-out;` (.shake.error) |

**建议**: 删除这 4 行。动画效果由 `AnimationAnt.java` 的 Java `Timeline` API 驱动，CSS 静态定义初始态和终态即可，不需要 transition。

---

### A5: TabsAnt 硬编码 setStyle 颜色 (Warning)

**文件**: `jfxium/src/main/java/org/openkawu/jfxium/component/composite/TabsAnt.java`

| # | 行 | 问题 |
|---|-----|------|
| 1 | 169 | `indicatorPane.setStyle("-fx-background-color: #f0f0f0;")` — 硬编码灰色 |
| 2 | 177 | `indicator.setStyle("-fx-background-color: #1677ff; -fx-background-radius: 2px;")` — 硬编码蓝色 |
| 3 | 229 | `contentAreaRef.setStyle("-fx-background-color: transparent; -fx-padding: 16px;")` — 内联样式 |
| 4 | 306 | `sb.append("-fx-background-color: rgba(0,0,0,0.02);")` — CARD 模式背景硬编码 |
| 5 | 131, 287, 298 | 多处动态拼接 `setStyle()` 字符串 |

**问题严重性**: TabsAnt 的指示条颜色直接硬编码 `#1677ff`，主题切换（如暗色模式、MUI 主题）后指示条颜色不会跟随变化。

**建议**: 将指示条颜色改为 CSS 变量引用（`-color-accent-emphasis`），或在 LESS 中定义 `.tabs-indicator` 样式类，通过 styleClass 控制。`padding` 和 `font-size` 也应改用 token 体系。

---

### A4/A5: Java 端 Color.web() 硬编码 (Warning)

| # | 文件 | 行 | 内容 | 严重度 |
|---|------|-----|------|--------|
| 1 | `TagAnt.java` | 192 | `Color.web("#8c959f")` — 关闭按钮 X 颜色 | Warning |
| 2 | `RateAnt.java` | 203, 206, 209 | `Color.web("#faad14")`, `Color.web("#d9d9d9")` — 星星颜色 | Warning |
| 3 | `SkeletonAnt.java` | 123 | `Color.web("#ffffff", 0.1)` — 闪烁效果 | Info |
| 4 | `WatermarkAnt.java` | 275 | `Color.web("#000000")` — 默认水印色 | Info |
| 5 | `ThemeColor.java` | 78, 114 | `Color.web(hexColor)` — 基础设施，合理使用 | OK |

**说明**: `ThemeColor.java` 中的 `Color.web()` 是将用户输入的主题色 hex 转为 Color 对象，属于必要使用。其他组件中的硬编码应考虑通过 styleClass 或 token 参数化。

---

### A1: LESS 硬编码颜色 — 通过 (Good)

LESS 组件文件中**未发现非 token 定义行的硬编码 hex 颜色**。所有样式均使用 `@color-*` 变量或 `-color-*` CSS 自定义属性，符合 SKILL 强约束 #1。

---

### A8: 色阶完整性 — 通过 (Good)

`variables.less` 和 `variables-dark.less` 均定义了完整的 0-9 色阶体系（accent、success、warning、danger 各 10 级 + base 11 级），语义变量映射正确。`_tokens.less` 在 `:root` 下正确输出 JavaFX CSS 自定义属性。

---

### A2: 组件状态完整性 — 基本通过 (Info)

主组件（Button、Input、CheckBox、Radio、Slider、Switch 等）均定义了 `:hover`、`:armed`/`:pressed`、`:disabled`、`:focused` 状态。`_focus.less` 全局处理了 `:focused` 的 focus-color 移除。

**注意**: 部分复合组件（TimelineAnt、StatisticAnt 等）作为展示类组件，缺少交互状态是可接受的。

---

## B. 架构一致性 (Java)

### B1: Builder 未继承 AbstractStyleBuilder (Warning)

以下 **~39 个** Builder 未继承 `AbstractStyleBuilder`，各自重复实现 padding/style/styleClass/prefWidth 等逻辑：

**overlay/ (8 个，全部缺失)**:
- DrawerAnt.Builder — 未继承
- PopconfirmAnt.Builder — 未继承
- DropdownAnt.Builder — 未继承
- NotificationAnt.Builder — 未继承
- ModalAnt.Builder — 未继承
- MessageAnt.Builder — 未继承
- ContextMenuAnt.Builder — 未继承
- PopoverAnt.Builder — 未继承

**composite/ (23 个缺失)**:
- ImageAnt, TransferAnt, AnchorAnt, CascaderAnt, TimelineAnt, StepsAnt, CarouselAnt, AutoCompleteAnt, BreadcrumbAnt, ListAnt, CalendarAnt, UploadAnt, InputNumberAnt, AvatarAnt, SegmentedAnt, ResultAnt, TreeSelectAnt, DescriptionsAnt, CollapseAnt — 均未继承

**control/ (1 个缺失)**:
- MentionsAnt.Builder — 未继承

**已继承的 (25 个)**: MenuButtonAnt, SplitButtonAnt, TooltipAnt, TreeAnt, ToggleButtonAnt, PaginationAnt, SpinnerAnt, AccordionAnt, TitledPaneAnt, TableAnt, SpinAnt, SwitchAnt, BackTopAnt, TimePickerAnt, AlertAnt, ResizablePanelAnt, FlexAnt, GridAnt, SpaceAnt, ScrollContainerAnt, DividerAnt, + 全部 5 个 template Builder

**建议**: 分批迁移。overlay 组件优先（共用一套遮罩/面板样式），然后是 composite 组件。迁移后可消除约 200+ 行重复代码。

---

### B5: 部分组件大量使用 setStyle 动态拼接 (Warning)

**TabsAnt.java** 是最严重的案例——`getTabStyle()`、`getTabBarStyle()`、`getTabPadding()` 等方法在 Java 端动态拼接 CSS 字符串，包含 fontSize、padding、background-color 等属性。这类样式应该在 LESS 中通过 styleClass 状态切换（如 `.tab-active`、`.tab-disabled`、`.tab-small`、`.tab-card`），而不是在 Java 端拼字符串。

---

### B3/B4: 组件分层正确性 — 通过 (Good)

- control/ 下的组件正确 extends 了 JavaFX 原生控件（ButtonAnt extends Button, InputAnt extends TextField 等）
- composite/ 下的组件正确使用了组合模式（CardAnt 使用 VBox 容器, AlertAnt 使用 HBox 组合等）
- 未发现分层错误

**注意**: `component.layout/` 下的 AnchorPaneAnt, BorderPaneAnt 等 extends JavaFX 原生 Pane，这符合 JavaFX 布局容器的扩展惯例（它们不是 final 类）。

---

## C. LESS-Java 对齐

### C3: `_index.less` 导入完整性 — 通过 (Good)

64 个组件 .less 文件全部在 `components/_index.less` 中通过 `@import` 注册。无遗漏。

### C1: styleClass 命名一致性 — 基本通过 (Info)

检查已知 BUG 案例：
- TableAnt `jfx-table-striped` ↔ LESS `.table-view.jfx-table-striped` — 一致 (BUG #37 已修复)
- CardAnt `card-body` ↔ LESS `.card .card-body` — 一致 (BUG #38 已修复)
- CodeBlockAnt `jfx-codeblock-copy-btn` ↔ LESS `.jfx-codeblock-copy-btn` — 一致 (BUG #33 已修复)

**注意**: TagAnt 使用字符串字面量（如 `"tag"`, `"tag-label"`）而非 `CssClasses` 常量，建议迁移。

---

### C2: CssClasses.java 常量使用率 (Info)

`CssClasses.java` 定义了约 200+ 个常量，覆盖了 ButtonAnt、CardAnt、FormAnt、TableAnt、MenuAnt、SwitchAnt、BadgeAnt、AlertAnt、ProgressAnt、SliderAnt、CodeBlockAnt、InputAnt 等主要组件。

但大量 composite 组件（TagAnt、RateAnt、TimelineAnt、CarouselAnt、CalendarAnt 等）使用字符串字面量而非 CssClasses 常量。建议逐步迁移。

---

## D. Token 体系

### D1: 硬编码 px 值 — 基本通过 (Good)

LESS 组件文件中的高度/间距/字号均使用 token（`@control-height`、`@ctrl-padding-y`、`@spacing-sm`、`@font-size-md` 等），紧凑模式能正确联动。少量硬编码 px（如 Slider thumb 14×14、Switch 轨道 44×22）属于"尺寸被钳死"的造型件，按 SKILL §23 规范不需要跟紧凑联动。

### D3: `@border-radius-full` 使用 — 通过 (已修复)

`_slider.less` 中 track 和 colored-track 已从 `@border-radius-full` 改为 `@border-radius-md`（BUG #56 已修复）。thumb 使用 `@border-radius-full` 是安全的（thumb 尺寸被硬钳死在 14×14）。

Badge、Switch、Radio、Progress 等组件对 `@border-radius-full` 的使用场景均为尺寸受限节点，无风险。

### 紧凑模式 token 覆盖 — 通过 (Good)

`theme-light-compact.less` / `theme-dark-compact.less` / `theme-mui-compact.less` 均正确覆盖了 control-height、spacing、padding 等 token 后再 `@import theme-base.less`，紧凑模式生效正确。

---

## E. 基础设施

### E1: module-info.java 导出完整性 — 通过 (Good)

`module-info.java` 导出了 17 个包，覆盖所有公开 API 包。`requires` 声明了 `javafx.controls`、`javafx.fxml`、`java.logging`，无冗余依赖。

### E2: i18n 资源文件 — 通过 (Good)

- `messages.properties` (fallback) + `messages_zh_CN.properties` (默认) + `messages_en.properties` — 三文件齐全
- 键命名遵循 `<component>.<element>` 规范
- 零第三方依赖，使用 JDK `ResourceBundle`

### E3: 重复代码 — 见 B1 (Info)

Builder 中重复的 style/styleClass/padding 字段和方法是最大的重复代码源。迁移到 `AbstractStyleBuilder` 后可消除。

### ThemeManager — 通过 (Good)

三维状态机（Family x dark x compact）设计合理。`setPrimaryColor()` 去掉了 `.root{}` 包裹（BUG #62 已修复），主题切换后自动重新应用主题色。

---

## 问题优先级修复建议

| 优先级 | 问题 | 修复工作量 | 影响 |
|--------|------|-----------|------|
| P0 | `_animation.less` 4 处 `-fx-transition` | 5 分钟 | Critical - 运行时静默失败 |
| P1 | TabsAnt 硬编码 `#1677ff` / `#f0f0f0` 指示条 | 30 分钟 | Warning - 主题切换后颜色不跟随 |
| P2 | TabsAnt `setStyle()` 动态拼接样式迁移到 styleClass | 1-2 小时 | Warning - 架构不一致 |
| P3 | overlay 8 个 Builder 迁移到 AbstractStyleBuilder | 2-3 小时 | Warning - 消除重复代码 |
| P4 | composite 23 个 Builder 迁移到 AbstractStyleBuilder | 4-6 小时 | Info - 渐进式改进 |
| P5 | `Color.web()` 硬编码参数化（TagAnt, RateAnt 等） | 1-2 小时 | Info - 主题灵活性 |
| P6 | 字符串字面量 styleClass 迁移到 CssClasses 常量 | 2-3 小时 | Info - 类型安全 |

---

## 亮点总结

1. **LESS 颜色体系非常健康** — 全项目 LESS 文件中未发现硬编码颜色，0-9 色阶体系完整，`:root` 下 CSS 变量正确输出，ThemeManager 可运行时切换主题色
2. **CssClasses.java 体系完善** — 200+ 常量，覆盖所有主要组件，命名规范统一
3. **`_index.less` 导入完整** — 64 个组件文件无一遗漏
4. **紧凑模式 token 覆盖正确** — compact 主题通过覆盖 token 再 `@import theme-base` 的方式实现，而非手写 CSS
5. **i18n 体系干净** — 零第三方依赖，三文件齐全
6. **module-info.java 导出准确** — 无冗余 requires，无遗漏 exports
7. **BUG 修复质量高** — BUG #37(TableAnt striped)、#38(CardAnt compact)、#56(Slider overflow)、#62(ThemeManager root) 等修复到位且正确
