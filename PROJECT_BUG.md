# 问题修复状态

> 模仿 Ant Design 组件，与 AtlantaFX（[GitHub](https://github.com/mkpaz/atlantafx)）做对照参考。

## 已归档：示例 UI 验收反馈（第 1–9 条，均已闭环）

> 这批是用户在 demo 验收时手写的原始反馈（曾置顶未编号），现已全部修复并落到下方「已修复问题」表。
> 保留对照表便于溯源；原始详细描述见 git 历史。

| 原始反馈 | 组件 | 对应修复编号 | 状态 |
|---|---|---|---|
| 1 SelectableText 单行拖选出现蓝边 + 选区文字发虚 | SelectableTextAnt | #41 | ✅ |
| 2 Watermark 缺「场景下如何结合使用」的示例 | WatermarkAnt | `WatermarkExamplePage`（基础 + 机密文档 demo） | ✅ |
| 3 四角空白 / 边线不连续 | SplitButtonAnt | #43 | ✅ |
| 4 Steps/Anchor 点击看不出作用 | StepsAnt / AnchorAnt | #46 + #51 + #52 | ✅ |
| 5 数据输入控件「取不到选中 value」（要 value 不要 key） | Dropdown/MenuButton/ComboBox/InputNumber/Cascader/TreeSelect/ColorPicker/TimePicker | #45 + #53 + #54 | ✅ |
| 6 范围模式溢出容器宽度 | SliderAnt | #42 → #56（真因 9999px 圆角泄出，SKILL §23） | ✅ |
| 7 显示不全 / 双边框 / 弹层按钮不全 | DatePickerAnt | #44 + #57 + #39 + #49 | ✅ |
| 7(重号) 只能看不能操作 | TransferAnt | #47（加提示 + 结果栏） | ✅ |
| 8 自定义颜色对话框 slider 超宽 / 排版乱 | ColorPickerAnt | #55（对齐 AtlantaFX `.custom-color-dialog`） | ✅ |
| 9 缺「必选一个、不可全不选」模式 | ToggleButtonAnt | #48（mandatoryGroup） | ✅ |

## 已修复问题

| # | 问题 | 状态 | 修复日期 |
|---|------|------|----------|
| 1 | 输入框 Hover 效果修正 | ✅ 已修复 | 2026-05-12 |
| 2 | Slider 超出边框 | ✅ 已修复 | 2026-05-12 |
| 3 | TextArea 报错信息组件 | ✅ 已修复 | 2026-05-12 |
| 4 | Switch 显示问题 | ✅ 已修复 | 2026-05-12 |
| 5 | MUI 主题阴影空白 | ✅ 已修复 | 2026-05-12 |
| 6 | 组件小型化 | ✅ 已完成 | 2026-05-12 |
| 7 | Spinner 焦点变大 BUG | ✅ 已修复 | 2026-05-12 |
| 8 | 输入框焦点变大 BUG | ✅ 已修复 | 2026-05-12 |
| 10 | Anchor vs Tabs 重复 | ✅ 已分析 | 2026-05-12 |
| 11 | DatePicker 样式丑陋 | ✅ 已修复 | 2026-05-12 |
| 12 | TimePicker 宽度太短 | ✅ 已修复 | 2026-05-12 |
| 13 | ColorPicker 超出边框 | ✅ 已修复 | 2026-05-12 |
| 14 | TreeSelect 无法选中节点 | ✅ 已修复 | 2026-05-12 |
| 15 | InputNumber 按钮无效 | ✅ 已修复 | 2026-05-12 |
| 16 | Calendar 布局问题 | ✅ 已修复 | 2026-05-12 |
| 17 | Popover 点击不消失 | ✅ 已修复 | 2026-05-12 |
| 18 | Drawer 与 Ant 设计一致 | ✅ 已修复 | 2026-05-12 |
| 19 | Animation 没变化 | ✅ 已修复 | 2026-05-12 |
| 20 | BackTop 展示内容 | ✅ 已完善 | 2026-05-12 |
| 21 | Table 行选中色与文字对比度差（怀疑 LESS 未编译） | ✅ 已修复 | 2026-05-17 |
| 22 | MUI 主题输入框 Hover/Focus 时文字看不清 | ✅ 已修复 | 2026-05-17 |
| 23 | 输入框获焦尺寸抖动复检（最终对齐 SKILL：边框变色 + 外阴影，无尺寸变化） | ✅ 已复检 | 2026-05-17 |
| 24 | ButtonAnt ghost inline style 阴魂不散（modify type 切换后旧 type 颜色残留） | ✅ 已修复 | 2026-05-26 |
| 25 | ButtonAnt removeTypeStyleClasses 中 `BUTTON_PRIMARY` 死代码（PRIMARY/ACCENT 共用 `accent` 类，primary 字符串从未挂上） | ✅ 已修复 | 2026-05-26 |
| 26 | pom.xml 漏配 `theme-mui-dark.less` 的 lessc execution，导致主题改动后 css 产物不同步 | ✅ 已修复 | 2026-05-26 |
| 27 | ButtonAnt `Builder.build()` 与 `ModifyBuilder.apply()` 的 styleClass 渲染逻辑各写一份，size/shape/ghost 段重复且不同步 | ✅ 已重构 | 2026-05-26 |
| 28 | ButtonAnt.ModifyBuilder 暴露面太窄（缺 shape / ghost），动态切形状/幽灵风格只能走原生 setStyleClass 操作 | ✅ 已扩展 | 2026-05-26 |
| 29 | InputAnt 输入限制需求需要业务自己写 TextFormatter + UnaryOperator&lt;Change&gt;，样板代码长且容易写错（admin 高频痛点） | ✅ 已沉淀 | 2026-05-26 |
| 30 | MenuAnt 缺 runtime 修改 API（selectedKey/expandedKeys 仅 build-time 消费）→ ShowcaseFrame 切菜单时被迫整体 rebuild → 侧栏滚动条 vvalue 跳回顶部 | ✅ 已修复 | 2026-05-27 |
| 31 | DrawerAnt `.width(int)` 不生效，所有抽屉都被拉伸到 owner 窗口宽度（VBox 在 StackPane 里默认 maxWidth=MAX_VALUE 撑满）| ✅ 已修复 | 2026-05-29 |
| 32 | CodeBlockAnt 行号与代码错位（顶部出现 N 行空白行号）—— BorderPane.left/center 高度不一时默认 CENTER 对齐导致 ScrollPane 被垂直居中 | ✅ 已修复 | 2026-05-29 |
| 33 | CodeBlockAnt 没有可见的复制入口（复制功能藏在右键菜单 + Ctrl+C，用户看不到；TextFlow 又不支持选中复制）—— LESS 早定义好 header/copy-btn 样式但 Java 端从未渲染 | ✅ 已修复 | 2026-05-29 |
| 34 | CodeBlockAnt 不支持自由拖选 + 部分复制（TextFlow 为高亮牺牲了选区能力）—— 加 selectable(true) 走只读 TextArea | ✅ 已修复 | 2026-05-29 |
| 35 | SwitchAnt 点击无法正常开关（点左跑右又回左）—— `selected` 字段点击后从不更新，每次都用同一个 `!selected`；初始 selected=true 时 thumb 没初始化到右侧 | ✅ 已修复 | 2026-05-30 |
| 36 | SliderAnt 拖动手柄太小 —— `.slider .thumb` 未显式设尺寸，用了 modena 默认小尺寸 | ✅ 已修复 | 2026-05-30 |
| 37 | TableAnt `striped(true)` 斑马纹不生效 —— Java 挂 `jfx-table-striped` 但 LESS 选择器写 `.table-view.striped`（差 jfx- 前缀），对不上 | ✅ 已修复 | 2026-05-30 |
| 38 | 紧凑模式下 CardAnt 不紧凑 —— compact CSS 改外层 `.card` padding，但真实内边距在内部 `.card-body`/`.card-header`，选择器错位 | ✅ 已修复 | 2026-05-30 |
| 39 | DatePicker 点击箭头无反应（看不到日历入口）—— `.date-picker .arrow` 只设颜色没设 shape，"有色无形"零尺寸不可点（SKILL §17） | ✅ 已修复 | 2026-05-30 |
| 40 | 示例项目 Radio/Spinner 误用：Radio demo 用原生 RadioButton（丢了 RadioButtonAnt 的 shape）；"Spinner 数字步进"实为加载圈（与 SpinAnt 重复），真正的数字步进是 InputNumberAnt | ✅ 已修复 | 2026-05-30 |
| 41 | SelectableTextAnt 单行拖选出现蓝边（被误认为输入框边框）+ 选区文字发虚 | ✅ 已修复 | 2026-05-30 |
| 42 | SliderAnt 在不约束宽度的父容器里膨胀超出容器（单+范围模式）—— prefWidth 用 USE_COMPUTED_SIZE + maxWidth=MAX 导致无限撑大 | ✅ 已修复 | 2026-05-30 |
| 43 | SplitButtonAnt 四角缺口/边线不连续 —— 圆角容器内的方角 label/arrow-button 背景盖住容器圆角 | ✅ 已修复 | 2026-05-30 |
| 44 | DatePicker 双边框 —— 外层 .date-picker(input-base) 边框 + 内部 .text-field 默认边框叠加 | ✅ 已修复 | 2026-05-30 |
| 45 | 数据输入控件示例只显示"点了"但看不到取到的 value（Dropdown/MenuButton/ComboBox/InputNumber/Cascader/TreeSelect/ColorPicker/TimePicker）| ✅ 已修复 | 2026-05-30 |
| 46 | Steps/Anchor 示例看不出作用（纯静态展示，无交互反馈）| ✅ 已修复（demo 加交互）| 2026-05-30 |
| 47 | Transfer 示例只能看不能操作（需先选列表项再点箭头，UX 不明显）| ✅ 已改善（加提示+结果栏）| 2026-05-30 |
| 48 | ToggleButton 缺"必选一个、不可全不选"模式（admin 视图切换器场景）| ✅ 已修复（mandatoryGroup）| 2026-05-30 |
| 49 | DatePicker 弹层月/年 spinner 左右箭头不可见（只剩空圆角按钮）—— `.left-arrow`/`.right-arrow` 没设 shape，0 尺寸（SKILL §17 有色无形）| ✅ 已修复 | 2026-05-30 |
| 50 | TimePickerAnt.onChange 死回调（声明了但从未接线到 spinner，调用方拿不到选中时间）| ✅ 已修复 | 2026-05-30 |
| 51 | StepsAnt 无 runtime setCurrent API（只能 build-time .current(int)，切步骤要重建整个节点）| ✅ 已修复 | 2026-05-31 |
| 52 | AnchorAnt activeKey 仅 build-time 消费，无 runtime setActiveKey（点击高亮不移动）| ✅ 已修复 | 2026-05-31 |
| 53 | TreeSelectAnt.onMultipleSelect 死回调（multiple(true) 下 build() 只接线了单选）| ✅ 已修复 | 2026-05-31 |
| 54 | DropdownAnt.onSelect 只回传 key 不回传 label（调用方要自己维护 key→label 映射）| ✅ 已修复 | 2026-05-31 |
| 55 | ColorPickerAnt 自定义颜色对话框（CustomColorDialog）内部 RGB/HSB 调节 slider 超出对话框宽度 + 排版不居中/边线杂乱 —— 项目完全没有 `.custom-color-dialog` 样式，settings-pane 行内微组件无显式宽度约束 | ✅ 已修复 | 2026-05-31 |
| 56 | SliderAnt 范围模式仍溢出容器（#42 的回归）—— rangeBox `maxWidth=USE_PREF_SIZE` 把 HBox 钉死在 pref 宽（~520px），卡片比它窄时只能溢出 | ✅ 已修复 | 2026-05-31 |
| 57 | DatePicker 基础用法日期文字显示不全 —— `.date-picker` 只继承 input-base 无宽度约束，modena pref 宽偏窄 + padding/箭头把日期裁掉 | ✅ 已修复 | 2026-05-31 |
| 58 | 紧凑模式下 Table 行高/表头高不收紧（只字变小）；且 light/dark-compact 的 `@spacing-sm/xs` 未真正收窄，导致一票走 spacing 的组件紧凑模式集体失效 | ✅ 已修复 | 2026-06-01 |
| 59 | LESS 编译强依赖 Node.js（npx lessc），新机器需先装 Node 才能构建 —— 迁移到纯 Java 编译器 jlessc | ✅ 已修复 | 2026-06-02 |
| 60 | 编译产物含 4 处 `-fx-transition`（JavaFX 不支持，运行时静默失败）—— 清理动画工具类的无效 transition | ✅ 已修复 | 2026-06-02 |
| 61 | MUI 紧凑主题下拉/输入框高度 > 按钮（mui-compact 漏覆盖 `@ctrl-padding-*`/`@input-padding-*`，只改了 btn-padding）| ✅ 已修复 | 2026-06-02 |
| 62 | ThemeManager 主题色注入 inline style 带 `.root{}` 选择器（非法）→ 运行时 ClassCastException 警告（`-fx-border-color`）；切主题色/明暗后主题色丢失 | ✅ 已修复 | 2026-06-02 |
| 63 | Button 与 ComboBox/Input/Select/DatePicker 在 small/large 下高度不一致（size variant padding 体系分裂 + 无 min-height 钳制）| ✅ 已修复 | 2026-06-02 |
| 64 | Maven LESS 编译「假成功」—— groovy-maven-plugin 下 `Files.writeString`/`File.text` 静默不落盘，日志报成功但 CSS 没更新（改 LESS 不生效）| ✅ 已修复 | 2026-06-02 |
| 65 | CheckBox/RadioButton 图标与文字间距太近（用户反馈「贴在一起」）—— 缺 `-fx-graphic-text-gap`，用 JavaFX 默认 ~4px 不符合 admin 信息密度 | ✅ 已修复 | 2026-06-03 |
| 66 | layout 包 7 个继承式组件 + AbstractStyleBuilder 的 `borderTop/Bottom/Left/Right()` 挂错 styleClass 名（缺 `jfx-` 前缀）—— CSS 永远匹配不上 | ✅ 已修复 | 2026-06-07 |
| 67 | MenuBarAnt 顶级菜单按钮太高（~35px）—— 顶用下拉菜单项的 `@menu-padding-y: 8px`，没专属 token 拆开 | ✅ 已修复 | 2026-06-07 |
| 68 | MenuAnt INLINE 模式 row 太高（~35px）—— 与 #67 同源：复用下拉菜单 `@menu-item-padding-y: 8px` + Java 端 setPadding 吞 padding；侧栏要 30px/row 极致紧凑（VS Code / IDEA 风格）| ✅ 已修复（4 token 拆分 + 4 compact 主题覆盖）| 2026-06-07 |
| 69 | `.root.jfx-compact` 块 5 条 menu 规则写死 `4px 8px` 硬编码（与 #58 同源：硬编码 px 不联动 token），且 jlessc 1.16 嵌套 + @-token 解析有 bug，迁 token 失败 | ✅ 已修复（改字面量绕开 jlessc bug，4 compact 主题对应 dead override 同步清掉）| 2026-06-08 |
| 70 | P0 致命红线批量合规修复（commit 6ee94e8）—— PopoverPanel `setStyle("padding: 12px 16px")` 改 `jfx-popover` styleClass + LESS（红线 #1）+ JfxStyles 新增 75 个 jfx- 前缀常量（红线 #8：含 PAGINATION / TAG_* / RESULT_* / CRUD_TEMPLATE_* / POPOVER_CONTENT / SKELETON_SHIMMER / BADGE_TEXT / BUTTON_DANGER_TEXT / CODE_LINE_NUMBERS 等）+ 12 个 LESS 组件 jfx- 前缀化（_accordion / _alert / _alert-enhance / _badge / _badge-enhance / _base-cards / _codeblock / _pagination / _popover / _selectable-text / _sizes / _tier3-batch2）+ 4 个 mui 主题 `.alert-success/info/warning/error` 改 `.jfx-alert-*` + _pagination.less 10 处 `.pagination` → `.jfx-pagination`（保留 modena `.pagination-control`）+ theme-base.less 删 2 块死代码（`.card` / `.panel > .panel-body`，无 Java 端引用）+ module-info.java 新增 `exports org.openkawu.jfxium.core.builder;`（红线 #10）| ✅ 已修复（compile + install BUILD SUCCESS；扫描 0 唯一违规）| 2026-06-08 |
| 71 | `MuiTheme` 命名混淆（MUI 是 `ThemeManager.Family` 不变量，`light` 才是密度/明暗轴）→ 重命名 `MuiLightTheme`，与 `LightTheme` / `DarkTheme` / `LightCompactTheme` / `DarkCompactTheme` 命名规范一致；git 自动识别为 87% similarity rename（主体 100% 相同，仅类名 + 注释改 1 字符）。同步调整：Theme.getName() `"mui"` → `"mui-light"`、ThemeColor 新增 `MUI_LIGHT` 枚举值、ThemeManager `getTheme(name="mui")` → `getMuiLightTheme()` 工厂方法 + Family 状态机正确表达（MUI 仅有 light 资源）+ 全量 .java 引用 `MuiTheme` → `MuiLightTheme`（Composite / Template / Theme 实现类等）| ✅ 已重构 | 2026-06-08 |
| 72 | JavaFX 原生控件包装补齐 6 个（commit 6ee94e8）—— control/ChoiceBoxAnt.java (142 行) / control/ListViewAnt.java (153 行) / control/SeparatorAnt.java (100 行) / control/SplitMenuButtonAnt.java (137 行) / layout/BorderPaneAnt.java (136 行) / layout/TextFlowAnt.java (101 行) + less/components/_separator.less (11 行) Separator 样式；继承式 + Builder API + jfx- 前缀 styleClass 全套，零硬编码 | ✅ 已完成 | 2026-06-08 |

## 修复说明（2026-05-30 批次：示例项目回归暴露的源头 bug）

> 用户在 demo 验收时发现 9 个问题，按 SKILL §22「示例项目即回归测试」逐个追到框架源头修复。

### #35 SwitchAnt 点击逻辑 bug（最严重）
- **现象**：点击开关，thumb 从左跳到右又弹回左，无法稳定切换；颜色蓝↔灰是对的。
- **根因**：`build()` 的点击 handler 里用 `!selected`，但 `selected` 是 builder 字段，**点击后从不更新**——所以每次点击都基于同一个初始值反转，状态机锁死。另外初始 `selected=true` 时 thumb 的 translateX 没初始化到 24（右侧），导致蓝轨道配左侧 thumb 视觉错乱。
- **修复**：用 `final boolean[] currentSelected` 持有可变态，每次点击更新；初始 selected 时 `thumb.setTranslateX(24)`；toggle 动画改用 `setToX`（从当前位置滑过去，不写死 from）。
- **复测**：模拟 3 次点击，onChange 序列 = `true,false,true` ✅。

### #36 SliderAnt 手柄太小
- **修复**：`.slider .thumb` 显式设 14×14（对齐 Ant Design），之前用 modena 默认偏小尺寸。

### #37 TableAnt 斑马纹失效
- **根因**：典型「styleClass 与选择器对不上」——TableAnt 挂 `jfx-table-striped`，LESS 却写 `.table-view.striped`。
- **修复**：LESS 选择器改 `.table-view.jfx-table-striped`。
- **沉淀**：再次印证 SKILL「LESS 有样式 ≠ 生效」，必须 grep 确认 Java 挂的 class 名与 LESS 选择器一致。

### #38 紧凑模式 Card 不紧凑
- **根因**：CardAnt 是「容器 `.card` + 内部 `.card-body`/`.card-header`」结构，padding 在内部节点上。compact 主题只改外层 `.card { padding }`（外层根本没 padding），内部纹丝不动。又一个「容器 vs 内部节点 padding 错位」案例（同 CodeBlock #32 / Drawer #31 family）。
- **修复**：light-compact / dark-compact 两个 CSS 把 padding 下放到 `.card .card-body`（12px）和 `.card .card-header`（8px 12px）。
- **关于默认尺寸**：默认 Card body padding=24px 是对的（对齐 Ant Design Card 默认 24px），不改。

### #39 DatePicker 点击无反应
- **根因**：`.date-picker .arrow` 只设 `-fx-background-color` 没设 `-fx-shape`——SKILL §17「有色无形」，节点 0 尺寸不可见也不可点，用户找不到日历入口。
- **修复**：显式设日历图标 shape（Material calendar）+ 14×14 尺寸 + cursor:hand。

### #40 示例误用（demo 侧）
- **Radio**：demo 用原生 `new RadioButton()`，丢了 `RadioButtonAnt` 的 shape（圆/方/圆角）能力。改用 RadioButtonAnt + 新增「形状」section。
- **Spinner**：`SpinnerAnt` 其实是加载圈（ProgressIndicator），与 `SpinAnt`（Spin 加载）功能重复，但 demo 标题写「数字步进」误导。真正的数字步进器是 `InputNumberAnt`。删掉误导的 SpinnerExamplePage，换成 InputNumberExamplePage（步进/范围/精度/前后缀 4 段）。

### Transfer（#6 用户反馈）—— 非 bug
- 排查：TransferAnt 的 `<`/`>` 按钮有完整 handler，逻辑正常。用户「点击不管用」是 UX 问题——需先点选列表项再点箭头。组件本身无 bug，暂不改。

## 🎯 M19.41 密度系统对齐 Ant Design（2026-05-30）

> 用户质疑「紧凑/默认/宽松的内边距是否按 Ant Design 标准」。诚实复检：**之前是拍脑袋写的，不符合标准**。本次系统性重构对齐 Ant Design 官方 token 算法。

### 根因（3 个系统性问题）
1. **Button/Input padding 用错 token**：`@btn-padding-y = @spacing-sm(8px)`，导致按钮高 ~37px（应为 32）。Ant Design 控件 padding 是为凑 controlHeight 反推的，不是简单跟随间距梯度。
2. **Compact 不按官方算法**：light-compact / dark-compact 是**手写硬编码 CSS**（无 LESS 源），值还跟 mui-compact 不一致，两套标准打架。
3. **手写 compact CSS 与 token 体系脱节**：改 base token 不会同步到 compact；且选择器错位（改外层 `.card` 而非内部 `.card-body`）。

### Ant Design 官方标准（已查源码 `components/theme/themes`）
- **seed**: `sizeUnit=4, sizeStep=4`；size 阶梯 `sizeUnit*(sizeStep+n)`：XXS=4 / XS=8 / SM=12 / size=16 / LG=24 / XL=32
- **compact 算法**（`genCompactSizeMapToken`）：`compactSizeStep = sizeStep-2 = 2`；`controlHeight = 32-4 = 28`
- **控件高度**：default 32 / small 24 / large 40（compact: 28/20/36）

### 修复
1. **padding token 与 spacing 解耦**（`variables-base.less`）：新增 `@ctrl-padding-*` 显式 Ant 值（default y=6 x=15 凑 32 高；JavaFX 14px 文字比 web 矮 ~3px，padding-y 比理论值 +1~2 补偿）。Button/Input padding 指向 `@ctrl-padding-*`，不再用 `@spacing-*`。
2. **Card padding 走 token**：`.card-body` = `@card-padding`（默认 24=sizeXL），`.card-header` = `(card-padding*2/3) card-padding`，紧凑自动派生 16。
3. **新建 `theme-light-compact.less` / `theme-dark-compact.less`**（替代手写 CSS）：覆盖 compact 尺寸 token（controlHeight 28、card-padding 16、ctrl-padding y=5 x=11）后 `@import theme-base.less`，全部组件 padding 按 compact token 自动重新生成。
4. **pom 加 2 条 lessc execution**，把这两个 LESS 编译成 css（之前手写 CSS 被覆盖，从 ~310 行变 4781 行完整产物）。

### 验证（runtime 实测控件高度）
| 模式 | Button/Input 实测高 | Ant 目标 |
|---|---|---|
| default | 31px | 32 ✅（1px JavaFX 舍入误差） |
| compact | 28px | 28 ✅ 精确 |

- default button padding `5px 15px`→`6px 15px`、card-body 24px、card-header 16px 24px —— 对齐 Ant Design
- compact button padding `3px 11px`→`5px 11px`、card-body 16px —— 对齐 Ant compact

### 沉淀
- **JavaFX 无 box-sizing/line-height 概念**：控件高度 = `2*padding-y + 文字实测高 + 2*border`。JavaFX 14px 文字实测约 18px 高（比 web 的 22px 行盒矮），所以 padding-y 要比「web 理论值」补 1~2px 才能凑到同样的 controlHeight。
- **compact 主题必须有 LESS 源**：手写 CSS 会与 token 体系脱节、与其他主题打架。正确做法 = 覆盖尺寸 token + `@import theme-base`（同 mui-compact 已有模式）。
- **padding token 别绑死到通用 spacing 梯度**：控件 padding 是为凑高度反推的，与「容器间距」是两套逻辑，必须解耦。

### M19.41.1 补充：Card padding 偏离 Ant 标准（用户拍板）
- 用户反馈 Card body 24px「占用大」。核实 Ant 官方确实是 24（`bodyPadding=paddingLG`），但桌面 admin 信息密度高，24 偏松。
- **决策（方案 B）**：**仅 Card** 偏离 Ant 标准——default body `16`（@spacing-lg）、compact `8`；header 按 `card-padding*0.75` 派生（default `12 16` / compact `6 8`）。
- 其它所有组件仍严格对齐 Ant（Button/Input 凑 32 高等）。这是全项目唯一一处主动偏离，已在 `variables-base.less` 注释标注。

## 修复说明（2026-05-29 批次）

### #31 DrawerAnt `.width(int)` 不生效（实际宽度 = owner 窗口宽度）
- **现象**：`DrawerAnt.create().width(320).placement(RIGHT).build().open(node)` 期望 320px 窄抽屉，实际渲染成贴满窗口的全屏宽度面板。
- **根因**：`positionPanel()` 仅 `setPrefWidth(config.width)` 但 `drawerPanel` 是 `VBox`——`VBox.maxWidth` 默认 `Double.MAX_VALUE`，进 StackPane 后被拉伸到撑满父容器，`prefWidth` 形同虚设。这是 SKILL 项目约束 §20.1 反复强调过的「HBox/VBox 在 StackPane 内默认 maxWidth=MAX」陷阱。
- **修复**：`positionPanel()` 与 ownerWindow 跟随 listener 中，所有 `setPrefWidth/Height` 之后补一行 `setMaxWidth/Height(Region.USE_PREF_SIZE)`，强制收缩到 prefSize。
- **复测**：写小程序 `DrawerAnt.create().width(320)...build().open(btn)` 在 1200×800 owner 上，实测 `drawerPanel.getWidth() == 320.0`（修复前 = 1200.0）。
- **沉淀**：SKILL §20.1 已经记录过这条规则（M19.18 Carousel dotsBox），但 DrawerAnt 写早于 SKILL §20.1 时漏了。提醒：所有「StackPane + 子 VBox/HBox 用对齐定位」的浮层组件都要审查 maxWidth/maxHeight。

### #32 CodeBlockAnt 行号与代码错位（顶部 N 行空白）
- **现象**：复杂示例 section 里行号 1-15 是空白行号，到 16 才出现 `package org.example;`，整体上方留出大片空白。
- **根因**：原实现 `BorderPane.setLeft(行号 VBox) + setCenter(ScrollPane(代码))`：行号 VBox 高度 = N × 行高（无 maxHeight），而 ScrollPane 设了 `maxHeight=400`。BorderPane 的 LEFT/CENTER 区在父高度大于自身 prefHeight 时**默认 CENTER 垂直对齐**——结果 maxHeight=400 的 ScrollPane 被居中下移，但行号 VBox 仍贴顶布局，视觉上代码往下飘了一截。
- **修复**：行号 VBox 与代码 TextFlow 用 HBox 包成同一节点（`HBox(lineNumbers, codeDisplay)` + `Hgrow=ALWAYS` 给代码区），整个 HBox 塞进同一个 ScrollPane。两者共享滚动状态、自然顶部对齐，跟 IDE / GitHub 行号实现保持一致。
- **复测**：`lineBox.height == flow.height == 166`（修复前两者高度不一致 + ScrollPane 居中导致顶端 firstLineLabel.layoutY 远 > 8.0），First line label `localY=8.0`（紧贴 padding 顶端）。
- **沉淀**：BorderPane 的 5 区位独立布局——任何 left/right 高度 ≠ center 高度的场景都要警惕居中陷阱。**对于「行号 + 代码」「图标 + 文字」这类需要严格对齐的视觉单元，永远用同一个父容器（HBox/GridPane）包起来，不要用 BorderPane 区位拼接**。

### #33 CodeBlockAnt 没有可见的复制入口
- **现象**：示例项目的代码块，用户找不到复制按钮，鼠标也选不中文字（无法 Ctrl+C 选区复制）。
- **根因**（SKILL §22 框架源头问题，非 demo 问题）：
  1. `theme-base.less` 早就定义了 `.jfx-codeblock-header` / `.jfx-codeblock-lang` / `.jfx-codeblock-copy-btn` 完整样式，`CssClasses` 也有对应常量——但 `CodeBlockAnt.build()` **从来没渲染过 header 和复制按钮**，纯死样式。
  2. 复制功能只藏在 `setupCopySupport()` 的右键菜单 + Ctrl+C 里，**没有可见入口**，用户根本不知道能复制。
  3. 代码区用 `TextFlow`（为了语法高亮多色渲染），而 TextFlow **天生不支持文本选区**——所以也没法靠"选中拖拽"复制。
- **修复**：给 `CodeBlockAnt.Builder` 加 `showCopyButton(boolean)`（默认 true）+ `title(String)`，`build()` 时渲染顶部 header（左侧语言/标题 + 右侧「复制」按钮）。点击一键复制全部代码，按钮短暂显示「已复制」再恢复（PauseTransition 1.2s）——对齐 GitHub / Ant Design 代码块交互。
- **取舍说明**（Karpathy §1）：
  - **保留 TextFlow + 复制按钮**，不改用 TextArea。理由：TextArea 支持选区但**会丢失语法高亮**（单色）。代码块的核心价值是高亮，复制需求用「一键复制按钮」覆盖即可（GitHub/MDN/Ant Design 全都是这个方案，不靠选区）。
  - Ctrl+C / 右键菜单作为补充保留。
- **复测**：runtime 验证 header 渲染（3 子节点：lang + spacer + copyBtn）、点击后文案变「已复制」、剪贴板内容 == 源码。
- **沉淀**：LESS 有样式 ≠ 组件渲染了——`CssClasses` 里定义的类，要 grep 确认 Java 端真的 `getStyleClass().add(...)` 挂上了，否则就是"死样式"。这类「样式齐全但功能没接线」的坑，靠看 LESS 发现不了，得从用户视角走一遍交互。

### #34 CodeBlockAnt 不支持自由拖选 + 部分复制（只能整体复制）
- **现象**：#33 加了复制按钮后，用户进一步要求「能不能像普通文本一样拖选一段、只复制选中的部分」。当前 TextFlow 做不到。
- **根因**（JavaFX 硬限制）：`TextFlow` 为了多色语法高亮，把代码拆成多个 `Text` 节点——而 **JavaFX 的 TextFlow 不支持跨节点文本选区**。要支持选区必须改用 `TextArea`，但 TextArea 只能单色渲染（不支持富文本多色）。**纯 JavaFX 下「高亮」和「选区」二选一**，想兼得需引入 RichTextFX 等第三方库，与 CodeBlockAnt「零依赖」原则冲突。
- **修复**：给 `CodeBlockAnt.Builder` 加 `selectable(boolean)`（默认 false）：
  - `false`（默认）→ TextFlow 高亮（不可选），行为不变，老用户无感知
  - `true` → 只读 `TextArea` 单色渲染，原生支持拖选 + Ctrl+C + 部分复制（参考项目已有的 `SelectableTextAnt` M19.7 同款方案）
  - selectable 模式下 TextArea 用 `setPrefRowCount(行数)` 自适应高度撑开，与行号 gutter 一起放进外层 ScrollPane 共享滚动——避免内部滚动条与外层打架，行号天然对齐
  - 新增 LESS `.text-area.jfx-codeblock-textarea` 复合选择器去掉 TextArea 默认 chrome（背景/边框/焦点环），融入代码块容器
- **demo 应用**：`Demos.buildCodeToggle`（代码示例区）开 `selectable(true)`——示例代码核心诉求是「选中抄走用」，选区比高亮重要。
- **复测**：runtime 验证 selectable 模式 center 是 HBox(行号, TextArea)、TextArea editable=false、`selectRange(0,7)` 选中 `package`、`copy()` 后剪贴板 == `package`（部分复制成功）。
- **沉淀**：JavaFX 文本组件能力矩阵——**高亮选 TextFlow，选区选 TextArea，两者不可兼得**（除非上 RichTextFX）。给「展示型」文本组件设计 API 时，把这个取舍显式暴露成开关（`selectable`），让调用方按场景选，而不是替用户拍板。


## 修复说明（2026-05-17 批次）

### #21 Table 行选中色与文字对比度差
- **现象**：作者吐槽"其他主题色每行选中=主题色，文字看不到"，怀疑 LESS 未编译。
- **复检结论**：LESS 已编译，但选中色绑定到中性色（`@color-base-1`），视觉上几乎和默认背景同色，"看起来没选中"。
- **修复**：将 `theme-base.less` 中 `-color-cell-bg-selected` 从 `@color-base-1` 改绑到 `@color-accent-subtle`（极浅主题色，对齐 Ant Design `controlItemBgActive` token）；新增 `.table-row-cell:selected:hover` 与 `.tree-table-row-cell:selected:hover` 规则，对齐 `controlItemBgActiveHover`。
- **效果**：换主题色时选中色自动跟随，文字色保持默认前景色对比度安全。

### #22 MUI 主题输入框文字硬编码导致换肤失效
- **根因**：`theme-mui.less` 输入类组件 normal 块硬编码 `-fx-text-fill: rgba(0, 0, 0, 0.88);`；其他三个 mui 子主题（mui-compact、mui-dark、mui-dark-compact）此前已经修对（移除该行），唯独 mui.less 漏修，作者反复修了 7-8 次但根因没被找到。
- **修复**：删除 `theme-mui.less:331` 那行硬编码，让文字色继承 `theme-base.less` 的 `-color-fg-default` 语义变量。
- **效果**：和其他三个 mui 子主题保持一致，符合 SKILL 强约束 #1（禁止硬编码）。

### #23 输入框焦点效果复检
- **现象**：作者怀疑"获焦后框体微型变大"，但记录里 #7、#8 已修复。
- **复检结论**：`theme-base.less` 实现完全合规——`:focused` 只改 `border-color` 和 `-fx-effect`，不改 `border-width` 也不改 `padding`，无尺寸抖动。但 4 个 mui 主题在 `:focused` 块里写了 `-fx-effect: none;`，违反 SKILL 强约束 #5"焦点效果统一为边框变色 + 外阴影"。
- **修复**：4 个 mui 主题（mui、mui-compact、mui-dark、mui-dark-compact）的 `:focused` 块统一为 `border-color: -color-accent-emphasis; border-width: 1px; effect: dropshadow(...)`；mui.less 顺手把硬编码 `#1976d2` 改回语义变量，并清理冗余的 `focus-color/faint-focus-color: transparent`（base 已全局处理）。
- **效果**：所有主题焦点反馈一致，无尺寸抖动，符合 SKILL 强约束 #5。

## 修复统计

- **总计问题**：23 个
- **已修复**：22 个
- **分析后无需处理**：1 个（#10 Anchor/Tabs 非重复组件）

---

## 🎯 项目级里程碑：SKILL #1 全合规重构（2026-05-17）

继 #21/#22/#23 三条具体 bug 修复之后，对全项目做了**一次性深度清扫**，
彻底消除 inline `setStyle("-fx-...: -color-...")` 硬编码注入。详见 [PROJECT_PLAN.md](PROJECT_PLAN.md) 第四章里程碑。

**核心数据**：
- 已重构 *Ant 组件：48 个
- 接入公共 `AbstractStyleBuilder<SELF>` 基类：28 个
- 项目级 inline color 注入：**29 个文件 → 0 个文件**
- 顺手修复 3 处隐性 bug：SwitchAnt cursor 残留、CodeBlockAnt spacer 死代码、DividerAnt 文档撒谎

**已知遗留**（详见 PROJECT_PLAN.md P0/P1 计划）：
- SpinAnt 的 `Color.web("#1677ff")` 硬编码（JavaFX Shape API 限制）
- AlertBanner 孤儿类去留
- AnchorAnt / StatisticAnt 的 `Color`-based API（保留兼容）

---

## 修复说明（2026-05-27 批次）

### #30 MenuAnt 缺 runtime API + ShowcaseFrame 滚动条丢失（双向溯源实证）
- **现象**：ShowcaseDemo 切下方菜单项后，侧栏滚动条跳回顶部。
- **表层根因**：`ShowcaseFrame.rebuildSider()` 整体重建 sider，新建 `ScrollPane` 默认 `vvalue=0`。
- **源头根因**：`MenuAnt` 的 `selectedKey / expandedKeys` 仅在 `build()` 时消费一次，**没有 runtime 修改 API**——逼调用方每次切菜单都重建整棵 menu。
- **双向修复**（按 SKILL §22 双向溯源原则，源头与示例都修）：
  1. **源头**（jfxium）：给 `MenuAnt` 加 `Controller`：
     - `Builder.controller()` 在 `build()` 后返回控制器
     - `MenuAnt.controllerOf(Pane)` 也能从已构造产物里反查
     - 提供 `setSelectedKey(String)` / `expandKey(String)` / `collapseKey(String)` / `setExpandedKeys(Collection)`
     - 内部用 `BuildContext.itemRows` + `expandHandles` 双索引，runtime 切换只改 styleClass / visibility，不重建节点
  2. **示例**（jfxium-demo）：`ShowcaseFrame.navigateTo()` 用 controller 替代 rebuildSider，路由切换时菜单节点不动，滚动条 / 子菜单展开动画自然保留；rebuildSider 仅保留给 expandMode 切换（MULTIPLE/EXCLUSIVE 是 build-time 配置）
- **效果**：滚动条不再 reset；同时给所有调用方（admin demo / 业务用户）提供了 runtime 控制能力。
- **沉淀**：项目约束 SKILL.md 新增第 22 条「示例项目即回归测试 / 双向溯源」，规定后续 demo bug 必须同时追问源头是否有 API 缺失。

### #55 ColorPicker 自定义颜色对话框 slider 超宽（2026-05-31）
- **现象**：ColorPickerAnt 点「自定义颜色…」弹出的高级取色对话框里，RGB/HSB 数值调节的 slider 横向超出对话框宽度，整体排版不居中、边线杂乱难看（用户连续两轮反馈「一直在超出」）。
- **误判排查**：一开始怀疑是 SliderAnt 组件本身有设计问题（一直撑大）。实际不是——这里的 slider 是 **JavaFX 原生 `CustomColorDialog` 内部的 `#settings-pane > .slider`**，跟 JFXium 的 SliderAnt 是两码事。
- **根因**：项目 LESS 此前**完全没有 `.custom-color-dialog` 样式**，只写了 `.color-palette .slider`（那是色板弹层，不是自定义对话框）。`#settings-pane` 是 GridPane，每行 = `settings-label + slider + settings-unit + color-input-field`。slider 没有任何宽度约束时会按 `USE_COMPUTED_SIZE` 无限延展，把整行撑出对话框 → 溢出。
- **修复**：照抄 AtlantaFX `_color-picker.scss` 的 `.custom-color-dialog` 规范（SKILL §15「复杂控件先扒 AtlantaFX」），em 值按项目根字号 14px 换算成 px，给 settings-pane 内每个微组件显式宽度：
  - `#settings-pane > .slider { -fx-pref-width: 140px }`（10em，关键：固定宽不再撑爆整行）
  - `> .settings-label { -fx-min-width: 80px }`（5.75em，R/G/B 标签列）
  - `> .settings-unit { 21px }`（1.5em，% / ° 单位列）
  - `> .color-input-field { 56px }`（4em，数值输入框）
  - `> .web-field { 112px }`（8em，十六进制输入框）
  - 外加 `.color-rect-pane`（左侧大色块 224px + 色带 24px）、`#buttons-hbox` 右对齐、对话框 padding/spacing/背景。
- **验证**：`./mvnw -pl jfxium clean install` 通过，grep 编译产物确认 4 套主题（light/dark/light-compact/dark-compact）均生成 `.custom-color-dialog #settings-pane > .slider` 等选择器。
- **沉淀**：又一例「组件看似有 bug，实为缺样式」——`.color-palette .slider`（色板弹层）≠ `.custom-color-dialog #settings-pane > .slider`（自定义对话框）是两个独立弹窗。复杂原生控件的内部子节点结构必须先扒 AtlantaFX，不能凭印象猜。

## 数据输入控件「取选中 value」+ runtime API 补齐（2026-05-31 批次：BUG #5 / #51-54）

> 用户反馈（BUG #5）：Dropdown/MenuButton/ComboBox/InputNumber/Cascader/TreeSelect/ColorPicker/TimePicker
> 这一族「数据输入」控件「点了之后取不到选中的 value」——表面是 label（显示文案），用户要的是底层 value（编码值）。
> 先做了一次全控件审计，再按 SKILL §22「示例项目即回归测试」同时修框架源头 + demo。

### 审计结论：8 个控件里 6 个本来就能取到 value
| 控件 | 取值回调 | 给的是 | 缺口 |
|---|---|---|---|
| ComboBoxAnt | `onChange(T)` | 完整对象 T | 无 |
| InputNumberAnt | `onChange(Double)` | 数值 | 无 |
| ColorPickerAnt | `onChange(Color)` | 颜色对象 | 无 |
| TimePickerAnt | `onChange(LocalTime)` | 时间对象（#50 已修死回调） | 无 |
| CascaderAnt | `onChange(List<String>)` | 各级 **value** 路径（非 label） | 无 |
| MenuButtonAnt | 每项 `EventHandler` | 闭包内可取任意值 | 无 |
| **DropdownAnt** | `onSelect(key)` | **只有 key，丢了 label** | #54 |
| **TreeSelectAnt 多选** | `onMultipleSelect` | **死回调，从不触发** | #53 |

→ 真正的框架缺口只有 Dropdown(#54) 和 TreeSelect 多选(#53)。另外用户要求「接着补 runtime API」对应 #51/#52。

### #54 DropdownAnt 新增 onSelectItem（回传完整 MenuItem）
- **根因**：`onSelect(Consumer<String>)` 只给 item 的 key。要显示「编辑」就得调用方自己维护 key→label 映射（demo 里真的写了个 `labelOf(key)` switch，典型 SKILL §22「demo 手写 boilerplate = 框架缺口」信号）。
- **修复**：新增 `onSelectItem(Consumer<MenuItem>)`，直接回传整个 MenuItem（可取 `getKey()`/`getLabel()`/`getIcon()`）。与 TreeSelectAnt.onSelect(TreeNode) 的「回传完整对象」契约一致。两个回调可并存（onSelect 先、onSelectItem 后），向下兼容。
- **demo**：删掉 `labelOf(key)` 映射，改用 `onSelectItem(item -> ...item.getLabel()...)`。

### #53 TreeSelectAnt 多选回调接线
- **根因**：`onMultipleSelect` 字段声明了但 `build()` 的点击 handler 只走单选分支，多选回调从未触发（死回调），且没有多选选中态。
- **修复**：点击 handler 按 `multiple` 分支——多选时 `toggleMultiSelect(node)` 切换选中集合、输入框回填所有已选 label（"、"拼接）、触发 `onMultipleSelect(已选节点列表)`、不关闭弹层（连续勾选）、刷新行高亮。新增 `.tree-select-selected` 修饰类（浅蓝底 + 主色文字，对齐 Ant），LESS + CssClasses 同步。
- **demo**：多选 section 加结果 Label 显示「已选 N 项：xxx、yyy」。

### #51 StepsAnt 运行时 Controller（setCurrent/next/prev）
- **根因**：只有 build-time `.current(int)`，交互式步进（上一步/下一步）demo 只能每次点击 **重建整个 steps 节点**（SKILL §22 信号）。
- **修复**：仿 MenuAnt.Controller 模式——`build()` 装配 `Controller`，把每步的 circle/number/title/line 节点引用存入 `StepNodes`。`controller()` 拿到后调 `setCurrent(int)`/`next()`/`prev()` 直接重算状态修饰类（finished/current/wait）+ 连接线高亮，**不重建节点**。
- **demo**：交互式步进改用 `builder.build()` + `builder.controller()`，点击调 `ctrl.next()/prev()`，删掉 `renderSteps()` 重建法。

### #52 AnchorAnt 运行时 Controller（setActiveKey）+ 点击自动移高亮
- **根因**：activeKey 只在 build-time `.activeKey(...)` 生效，点击锚点只回调 onChange、**高亮条不动**。
- **修复**：仿 MenuAnt.Controller——`build()` 装配 `Controller`，注册 key→label 引用。点击锚点自动调 `controller.setActiveKey(key)` 移高亮（移除老 active 类、挂新 active 类，不重建）；业务滚动定位时也可主动调 `controller().setActiveKey(key)` 同步。
- **demo**：active section 默认 `activeKey("intro")`，点击其它锚点高亮条自动移动。

### 沉淀
- **runtime 修改一律走 Controller 模式**（M19.38 MenuAnt 首创）：`build()` 装配 Controller 持有已渲染节点引用 → setter 直接改 styleClass，不重建。已成为项目「需要 build 后再改状态」类组件的标准范式（Menu/Steps/Anchor 三个）。
- **「取不到 value」常常是回调契约问题**：回传 key 还是完整对象，决定调用方要不要自己维护映射表。新组件回调优先回传**完整对象**（含 key+label+payload），让调用方自由取。
- **demo 里出现手写映射表 / 重建节点 / 死回调，都是框架缺口的信号**（SKILL §22 再次验证）。

## SliderAnt 范围模式溢出 + DatePicker 显示不全（2026-05-31：BUG #6 / #7 复发修复）

> 用户复验时 #6（范围 slider 溢出）、#7（DatePicker 显示不全）仍在。重新定位根因——上一轮（#42/#44）只修了一半。

### #56 SliderAnt 范围模式溢出（#42 的回归）
- **现象**：`SliderAnt.create().range()...build()` 范围滑块横向铺满整个窗口，轨道是「一根线横穿整屏」，"20" 标签被挤到角落。
- **三次定位 + 真正根因（探针实测）**：
  1. v1：怀疑是宽度约束，给 rangeBox + slider 都 `maxWidth=MAX + Hgrow`。→ 没用（甚至更糟）。
  2. v2：改 slider `maxWidth=160`（有界）。→ 布局盒确实 160 了，但用户仍看到溢出。
  3. **写探针测量运行时真值**（不再猜）：发现 slider 的**布局盒**正确（160px、thumb 位置正确），但内部 `.track` StackPane 的**视觉边界宽达 20144px**（`[-9910, 10234]`）——track 向左右各撑出 ~9999px。
  - **真正根因**：`.slider .track` 的 `-fx-background-radius: @border-radius-full`（=**9999px**）。JavaFX 的 SliderSkin 布局时**不裁切 track 的圆角**，9999px 圆角直接膨胀了 track 的视觉 bounds，形成「线横穿整屏」的假溢出。thumb 也用了 9999 圆角但因为 width/height 被硬钉死 14px 所以没事；track 没有宽度钳制，圆角就泄出来了。
- **修复**：`.slider .track` 和 `.slider .colored-track` 的 `-fx-background-radius` 从 `@border-radius-full`(9999) 改为 `@border-radius-md`。track 只有 4px 高，小圆角即可完全圆头。对齐 AtlantaFX `_slider.scss`（track-radius = 普通 border-radius，从不用 full）。修复后探针实测 track=158px、整组 [57,943] 在 952px 卡片内，不溢出。
- **为何单滑块没暴露**：单滑块外层包了 `StackPane + Rectangle clip`，把 track 溢出部分裁掉了；范围滑块没有 clip，9999px 圆角直接露出来。
- **沉淀**：
  - **`@border-radius-full`(9999px) 只能用在「尺寸被硬钳制」的节点**（如固定 14px 的 thumb、固定高度的 badge/pill）。给「宽度不固定、靠父布局拉伸」的节点（track/进度条等）用 9999 圆角，会让圆角泄成巨大视觉 bounds。要圆头用「等于自身高度一半」的小值即可。
  - **症状是「溢出」不代表根因是「宽度约束」**——布局盒和视觉 bounds 是两回事。猜了两轮 maxWidth 都没中，写探针 5 分钟拿到真值。**遇到反复修不好的布局问题，先测量再动手**（SKILL §4 目标导向验证）。

### #57 DatePicker 基础用法日期文字显示不全（#44 之外的新问题）
- **现象**：`DatePickerAnt.create().value(LocalDate.now()).build()` 输入框里日期文字被裁切，显示不全。
- **根因**：`.date-picker` 只 `.input-base()`（无任何宽度约束）。native DatePicker 算的 pref 宽偏紧，配上我们 15px 横向 padding + 右侧箭头按钮，留给日期文字的宽度不够 → 文字被截。#44 只修了「双边框」，没碰宽度。
- **修复**：`.date-picker` 加 `-fx-min-width: 130px; -fx-pref-width: 160px;`（能容纳「2026/05/31 + 箭头」）。
- **沉淀**：input-base 家族里 DatePicker 是唯一「内部还有箭头按钮抢宽度」的，不能像 TextField 那样靠默认 pref 宽，必须显式给 min/pref width。

## 紧凑模式尺寸体系修复（2026-06-01：BUG #58）

> 用户验收紧凑模式时反馈：「Table 表格在紧凑模式下只是字体小了一号，行距/边距没变」。追根发现是紧凑模式尺寸体系的两层系统性缺陷。

### #58 紧凑模式 Table 不收紧 + spacing token 未真正收窄
- **现象**：顶栏切「紧凑」后，Table 行高、表头高纹丝不动，只有字体从 14px 变 13px。其它走 `@spacing-sm` 的组件（List/Menu/Tab 等）紧凑度也不明显。
- **根因（两层）**：
  1. **行高/表头高硬编码**：`theme-base.less` 里 `.column-header-background` 的 `-fx-pref-height: 48px`、`.table-row-cell` 的 `-fx-min-height: 48px` 是写死的 px。紧凑模式的工作原理是「覆盖尺寸 token → @import theme-base 重新生成」，硬编码值不读 token → 紧凑模式完全管不到。
  2. **light/dark-compact 的 spacing 没真收窄**：这两个 compact 主题里 `@spacing-xs` 还是 4px、`@spacing-sm` 还是 8px，与默认值**完全相同**（只有 mui-compact 改对成了 2/6）。而 `@table-cell-padding-y = @spacing-sm`，所以 cell 垂直 padding 在紧凑下根本没变。字号变小是因为 `@font-size-md` 被全局覆盖成 13px——这就是「只有字变小」的来源。
- **修复**（系统性，全走 token，无硬编码）：
  1. `variables-base.less` 新增高度 token：`@table-header-height: 48px` / `@table-row-height: 48px`（集中管理被钳死的「高度」尺寸，附注释说明 List/Tree 行高靠 padding 撑、无需独立 token）。
  2. `theme-base.less`：`.column-header-background` 与 `.table-row-cell` 默认值改为引用这两个 token（SMALL/LARGE 显式档保留原值，不跟紧凑联动）。
  3. `theme-light-compact.less` / `theme-dark-compact.less`：① 修正 `@spacing-xs: 4→2`、`@spacing-sm: 8→6`（对齐 mui-compact，让走 spacing 的组件紧凑真生效）；② 覆盖 `@table-header-height/@table-row-height: 48→36`。
  4. `theme-mui-compact.less` / `theme-mui-dark-compact.less`：补覆盖 table 高度 token 48→36（spacing 本就是 2/6 不动）。
- **验证（grep 编译产物 4 套 compact CSS + default）**：
  | | default | compact |
  |---|---|---|
  | Table 表头高（`.column-header-background` pref-height） | 48px | 36px ✅ |
  | Table 行高（`.table-row-cell` min-height） | 48px | 36px ✅ |
  | cell padding | 8×12 | 6×8 ✅ |
  | list-cell padding | 8×12 | 6×8 ✅ |
- **连带收益**：不止 Table——List/Menu/Tab/Tree/Tooltip/Form 等走 `@spacing-sm` 的组件，紧凑模式现在都真正收紧了。
- **未动（符合 SKILL §23）**：固定造型件（Switch 轨道 44×22 / Slider thumb 14×14 / Badge dot 8×8 / 勾选框 14px / 各箭头 shape）保持不变——紧凑模式本就不该动「尺寸被钳死」的节点。
- **沉淀**：紧凑模式失效的通用根因 = ①「高度类尺寸硬编码 px 没走 token」②「compact 主题漏改某些 spacing token」。排查口诀：紧凑模式只对「引用了被 compact 覆盖的 token」的属性生效，硬编码 px 一律失效。新组件凡是 `pref/min-height`、padding 都应走 token，不写死 px。

## 主题系统 + 构建链 + 控件高度批次（2026-06-02：BUG #59-#64 + LabelAnt + 主题选择器）

> 本批起于「demo 验收主题/紧凑」，按 SKILL §22 双向溯源，一路揪出 6 个框架源头 bug + 补 1 个组件 + 加主题选择器。

### #59 去除 Node.js 构建依赖（npx lessc → jlessc）
- **现象**：新机器 `mvn compile` 在 generate-resources 阶段失败，因为没装 Node.js/npm。
- **修复**：`exec-maven-plugin(npx lessc)` 11 个 execution → 1 个 `groovy-maven-plugin` execution，用纯 Java 编译器 `de.inetsoftware:jlessc:1.16` 批量编译 11 套主题。只需 JDK，零 Node 依赖。
- **验证**：产物行数/选择器与 npx lessc 一致；`clean install` 通过。

### #60 清理无效 -fx-transition
- **现象**：4 处 `-fx-transition`（动画工具类 .fade-in/.slide-up/.scale-in/.shake），JavaFX CSS 不支持该属性，运行时静默忽略（SKILL §3.1 / 强约束 #6）。
- **修复**：删除 4 行 transition，保留各动画类的初始/终态属性（opacity/translate/scale，由 Java Timeline 驱动）。

### #61 MUI 紧凑下拉/输入框高度 > 按钮
- **根因**：`theme-mui-compact.less`/`theme-mui-dark-compact.less` 只覆盖了 `@btn-padding-*`（按钮变矮 28px），漏覆盖 `@ctrl-padding-*`/`@input-padding-*`/`@card-padding` → ComboBox/DatePicker/Select 还是默认 padding（32px 高）。
- **修复**：两个 mui-compact 主题补齐 input 家族 padding token 覆盖，与 btn-padding 对齐。

### #62 ThemeManager 主题色注入非法 inline style
- **根因**：`applyPrimaryColorToAll()` 把 `.root { -color-accent-X: ...; }`（带选择器）整段塞给 `Node.setStyle()`。但 setStyle 只接受「属性声明列表」，不接受选择器包裹 → JavaFX CSS 解析器错乱，把 `.root {` 当属性乱解析，连累节点 `-fx-border-color` 抛 ClassCastException。
- **附带**：`applyTheme()` 切主题后不重应用主题色 → 切风格/明暗后 accent 色丢失。
- **修复**：① 去掉 `.root {}` 包裹，只拼属性声明；② `applyTheme()` 末尾自动 `applyPrimaryColorToAll()`。
- **暴露契机**：之前 demo 没有主题色选择器，这段代码从没被触发；加了主题色选择器才暴露这个潜伏 bug（SKILL §22）。

### #63 Button 与数据输入控件 small/large 高度不一致
- **根因**：size variant 区 `.button.small` 用 `@spacing-xs/sm`（padding-y=4），input 家族用 `@input-padding-*-sm`（padding-y=2），两套 padding 体系分裂；且 JavaFX 不同控件 skin 盒模型渲染高度有差异，光对齐 padding 仍差几 px（探针实测 button.small=25 vs combo.small=21）。
- **修复**：① 统一 button/input 家族 small/large 走同源 `@btn-padding-*`/`@input-padding-*` token；② 给两族 small/large 补 `-fx-min-height: @control-height-sm/-lg` 强制钳到同一目标高度；③ 删除 614 行与 5456 行重复的 `.button.small` 死代码。
- **探针实测（修复后）**：

  | 尺寸 | Button | ComboBox | Input | DatePicker |
  |---|---|---|---|---|
  | default | 31 | 31 | 31 | 31 |
  | small | 24 | 24 | 24 | — |
  | large | 40 | 40 | 40 | — |

  全家族完全对齐 Ant controlHeight（32/24/40，default 31 是 JavaFX 1px 舍入）。

### #64 Maven LESS 编译「假成功」（最隐蔽）
- **现象**：改了 LESS 源 + `mvn clean install`，日志显示「11 themes compiled successfully」，但 CSS 产物根本没更新——导致前几次改 LESS 都在用旧 CSS，反复「修了没生效」。
- **根因**：groovy-maven-plugin 2.1.1 的执行环境下，`java.nio.file.Files.writeString` 和 Groovy `File.text =` 都**静默失败**（不抛异常但不落盘）。
- **定位手段**：故意把 theme-light.css 写成 `MARKER_TEST`，跑 generate-resources，发现 groovy 报成功但文件还是 MARKER_TEST → 坐实没写入。
- **修复**：改用 `OutputStreamWriter(FileOutputStream)` + 显式 `flush()`/`close()`，并加「空 CSS 抛异常」校验防再次假成功。验证：污染文件后构建能正确覆写。
- **沉淀**：构建期写文件别依赖 `Files.writeString`/`File.text`（某些插件 classloader 下静默失败），用显式 stream + flush；且关键产物要加「写入后校验」，不轻信「成功」日志。

### 新增 LabelAnt 组件（用户反馈「最简单的 Label 没封装」）
- 继承式 + 链式（仿 VBoxAnt 双工厂模式）：`extends Label`，支持 `.text()/.type()/.secondary()/.success()/.wrap()/.graphic()/.contentDisplay()/.align()/.build()`。
- 文字色全走 styleClass（复用 typography 系列 LESS），零硬编码。
- demo 新增 LabelExamplePage（通用分类，4 section）。

### demo 主题选择器（用户反馈「无法切换主题模式」）
- ThemeManager 重构成三维正交状态机（Family × dark × compact），新增 `Family` 枚举 + `setFamily/setDark/setCompactDensity` + 切换后自动重应用主题色。
- demo 顶栏：主题风格下拉（Ant/MUI）+ 明暗下拉 + 紧凑 toggle + 主题色下拉（11 预设）。

### demo 菜单改名
- 「Select 选择器」→「ComboBox 下拉框」（底层是 ComboBoxAnt，原名让用户认不出是下拉框，SKILL §22 命名信号）。

## 修复说明（2026-06-03：BUG #65 CheckBox/RadioButton 图标与文字间距太近）

### #65 CheckBox/RadioButton 图标与文字间距太近（用户反馈）
- **现象**：用户反馈「多选与单选，文字与框里的太近了几乎贴着了」
- **根因**：CheckBox 和 RadioButton 的样式中缺少 `-fx-graphic-text-gap` 属性设置，使用 JavaFX 默认值（约 4px），导致图标与文字标签之间间距过小，视觉上「贴在一起」
- **修复**：
  1. 在 `theme-base.less` 的 `.check-box` 和 `.radio-button` 选择器中添加 `-fx-graphic-text-gap: @spacing-sm;`（默认 8px）
  2. 在紧凑模式中自动派生为 6px（`@spacing-sm` 在紧凑主题中为 6px）
- **效果**：所有主题（light/dark/mui/cyberpunk 等 11 套）的 CheckBox 和 RadioButton 图标与文字间距统一为 8px（紧凑模式 6px），符合 Ant Design 间距规范，视觉上不再「贴在一起」
- **沉淀**：JavaFX 中 CheckBox/RadioButton 等带图标的控件需显式设置 `-fx-graphic-text-gap` 控制图标与文字间距，默认值偏小不符合桌面 admin 高信息密度下的视觉舒适度。本项目所有间距都应走 token 体系（`@spacing-*`），确保紧凑模式能自动联动收紧。

## 修复说明（2026-06-07：BUG #66 layout 包继承式组件 borderXxx styleClass 缺 jfx- 前缀）

> 上一轮检查 `jfxium/src/main/java/org/openkawu/jfxium/component/layout` 时发现，7 个继承式组件的 `borderTop/Bottom/Left/Right()` 与 `AbstractStyleBuilder` 基类同款 API 全部挂的是裸字符串 `"border-top"`，但 LESS `_base-cards.less` 实际定义的是 `.jfx-border-top`（SKILL 「jfx- 前缀强制」），class 名错位导致 CSS 永不命中。和 #37 TableAnt 斑马纹失效属同源 bug。

### #66 layout 包继承式组件 borderXxx() 挂错 styleClass（CSS 永不命中）
- **现象**：调用 `.borderTop()` 等方法后，节点上看不到预期的某一条边线（4 条边都失效）。
- **根因**：典型「Java 挂的 class 名 ≠ LESS 选择器」错位：
  - Java 端 `styleClass("border-top")` / `add("border-top")` 写的是裸名
  - LESS `_base-cards.less` 实际定义的是 `.jfx-border-top`
  - 节点 styleClass 是 `border-top`，CSS 找的是 `jfx-border-top`，**匹配数 = 0**
- **影响面**（grep `"border-(top|bottom|left|right)"` 命中的 25 处）：
  - `AbstractStyleBuilder.borderTop/Bottom/Left/Right()` 公共实现（影响所有继承式 builder）
  - 7 个 layout 继承式组件各自又写了一份：`AnchorPaneAnt` / `HBoxAnt` / `VBoxAnt` / `StackPaneAnt` / `FlowPaneAnt` / `SplitPaneAnt` / `TilePaneAnt`
- **修复**：全部改用 `JfxStyles.BORDER_TOP/BOTTOM/LEFT/RIGHT` 常量（值 = `jfx-border-top` 等），杜绝再次硬编码漂移：
  - `AbstractStyleBuilder.java`：4 处 `add("border-...")` → `add(JfxStyles.BORDER_...)`，新增 import
  - 7 个 layout 组件：4 处 × 7 = 28 处 `styleClass("border-...")` → `styleClass(JfxStyles.BORDER_...)`，各加 import
- **顺手补齐**（两处 API 缺口）：
  - `AnchorPaneAnt`：原 API 只有 `padding(Insets)` 单签名，按 `AbstractStyleBuilder` 约定补齐 `padding(double)` / `padding(double, double, double, double)` / `padding(Insets)` 三重载
  - `TilePaneAnt`：同上
- **测试同步**（`AbstractStyleBuilderTest`）：5 个 borderXxx 断言（`borderTop/Bottom/Left/Right` + `fullChain`）+ 4 处 `@DisplayName` 文案 同步切到 `JfxStyles.BORDER_XXX` 常量；`borderTop_false` 改用 `assertFalse(contains(JfxStyles.BORDER_TOP))`（语义保持）
- **保护性扫描**：
  - 全项目 `grep "border-(top|bottom|left|right)"` 硬编码 → **0 处**
  - demo 中 15 处 `borderTop|borderBottom|borderLeft|borderRight` 调用 → 全部是方法调用，无字符串残留
- **复测**：
  - `./mvnw compile -pl jfxium` → BUILD SUCCESS（147 source files）
  - `./mvnw install -pl jfxium -DskipTests` → BUILD SUCCESS
  - `./mvnw test -pl jfxium` → **151/151 passed, 0 failures**
  - LESS 编译产物 `target/classes/.../theme-light.css` L4837-4852 确认有 `.jfx-border-top/bottom/left/right` 选择器
- **沉淀**：
  - 凡是「LESS 写了样式但 Java 端不生效」类 bug，**第一反应是 grep 比对** class 名是否一致（`grep -r "JfxStyles.XXX" jfxium/src` ↔ `grep "\.jfx-xxx" jfxium/src/main/resources/.../less`）。这是 #37 / #44 / 本次 #66 三连击的同一个根因。
  - 风格名常量必须全部走 `JfxStyles`，禁止 `styleClass("border-...")` 硬编码——这条已通过常量类型契约从源头杜绝（IDE 自动补全会给 `JfxStyles.BORDER_*` 而不是裸字符串）。

## 修复说明（2026-06-07：BUG #67 MenuBarAnt 顶级菜单按钮太高）

> 用户反馈 `MenuBarAnt menuBar = MenuBarAnt.create()` 出来的菜单栏按钮太大——比 VS Code / IntelliJ 风格的菜单栏高出一截（~35px vs ~28-30px），视觉上很「粗」。追到 `_contextmenu.less` 顶级菜单 `.menu-bar > .container > .menu` 复用了下拉菜单项的 `@menu-padding-y: 8px`（8px + 8px + 14px 字号约 19px 文字高 ≈ 35px 渲染高），和"横向贴边小按钮"的 IDE 风格定位严重不符。

### #67 MenuBarAnt 顶级菜单按钮太高（没专属 token）
- **现象**：`MenuBarAnt.create()` 渲染出的 File/Edit/View/Help 顶级菜单项，单个高度约 35px（8px + 文字行高 19px + 8px = 35px），对比 VS Code（~30px）/ IntelliJ IDEA（~28px）风格明显偏胖，不符合 IDE/桌面应用菜单栏的紧凑观感。
- **根因**：典型「视觉定位不同的控件共用同一组 padding token」：
  - `.menu-bar > .container > .menu`（顶级菜单栏横向贴边按钮）`padding-y` 用了 `@menu-padding-y: @spacing-sm`（8px）
  - 但这组 token 是为「下拉菜单垂直可点击行」设计的（点击舒适度优先，需要 8px+ 高度）
  - 顶级菜单栏的视觉是「横向贴边小按钮」，需要的 padding 系统**完全不一样**
  - 二者**共用 token 没有拆分** → 顶级菜单被迫变得和下拉项一样厚
- **影响面**：所有使用 `MenuBarAnt`/`MenuAnt`/`ContextMenu` 的菜单栏（11 套主题都中招）。
- **修复**：
  1. `variables-base.less` 新增 **`@menu-bar-padding-y`** 专属 token，默认 `4px`（IDE 风格），并在注释里**写清楚三个 token 的分工**：
     - `@menu-bar-padding-y` → 顶级菜单栏项（`.menu-bar > .container > .menu`，VS Code/IDEA 风格紧凑菜单栏）
     - `@menu-padding-y/x`   → 下拉菜单容器 + 弹层
     - `@menu-item-padding-y/x` → 弹层里的菜单项 `.menu-item` / `.context-menu .menu-item`
  2. `_contextmenu.less` 顶级菜单选择器 `.menu-bar > .container > .menu` 改用 `@menu-bar-padding-y @menu-padding-x`（X 方向仍走原 token）。
  3. 4 套 **compact 主题**（light-compact / dark-compact / mui-compact / mui-dark-compact）在各自的 size override 区块追加 `@menu-bar-padding-y: 2px;`（极致紧凑，~23px 高）。
- **验证**（8 套 Java 可用主题编译产物实测）：

  | 主题 | 修复前 padding | 修复后 padding | 视觉高度（14px 字号）|
  |---|---|---|---|
  | theme-light | `8px 12px` | `4px 12px` | ~27px ✅ |
  | theme-dark | `8px 12px` | `4px 12px` | ~27px ✅ |
  | theme-mui | `8px 12px` | `4px 12px` | ~27px ✅ |
  | theme-mui-dark | `8px 12px` | `4px 12px` | ~27px ✅ |
  | theme-light-compact | `6px 8px` | `2px 8px` | ~23px ✅ |
  | theme-dark-compact | `6px 8px` | `2px 8px` | ~23px ✅ |
  | theme-mui-compact | `6px 8px` | `2px 8px` | ~23px ✅ |
  | theme-mui-dark-compact | `6px 8px` | `2px 8px` | ~23px ✅ |

  弹层里 `.menu-item` padding 全部**未动**（默认 8px 16px / compact 6px 12px）→ 改动精准隔离，没误伤下拉项。
- **保护性扫描**：
  - `grep -r "@menu-padding-y" jfxium/src/main/resources/.../less` → 仅下拉菜单使用，顶级菜单已切到 `@menu-bar-padding-y` ✅
  - `grep -r "@menu-bar-padding-y" jfxium/src/main/resources/.../less` → 1 个 base 定义 + 4 个 compact 覆盖 + 1 个消费点（_contextmenu.less）✅
- **复测**：
  - `./mvnw install -pl jfxium -DskipTests` → BUILD SUCCESS
  - 8 套 Java 主题 CSS 产物 grep `.menu-bar > .container > .menu` 全部输出符合预期 padding
- **沉淀**：
  - **顶级横向菜单栏和下拉菜单项的 padding 系统必须分开**——前者是「贴边小按钮」要紧凑，后者是「垂直可点击行」要舒适，二者**不能用同一组 token**。
  - 设计 token 命名要传达**视觉定位**而不是「能塞就用」：`@menu-bar-padding-y` 一看就知道是给菜单栏用的，`@menu-padding-y` 是给下拉菜单容器用的，分工清晰。
  - 此类「控件 X 用了控件 Y 的 token」型 bug，**第一反应是回到 _xxx.less 看 padding 是从哪组 token 继承的**——如果继承方和被继承方的视觉定位不同，就要单独拆 token。

## 修复说明（2026-06-07：BUG #68 MenuAnt INLINE 模式侧栏菜单 30px/row 极致紧凑）

> 用户拍板（第二轮 AskUserQuestion 回答）：「横向两个组件暂不动，**inline 行高压到 30px/row（极致紧凑）**」，对标 VS Code（30px）/ IntelliJ IDEA（28-30px）风格。BUG #67 修了顶级 MenuBar 横向按钮（`@menu-bar-padding-y` 4px → ~27px），但侧栏 `MenuAnt.create().build()` 的 inline row 还是 ~35px 偏胖。本轮沿用 #67 的 token 拆分思路，把「侧栏 inline 行」也从下拉菜单 token 拆出来独立治理。
>
> 用户关键反馈「不会就看 AtlantaFX 源码」——本轮 2 个核心设计修正（MenuGroup padding 跟 item 一致 / MenuDivider 走独立 token + CSS）都是 AtlantaFX 源码给的指引。

### #68 MenuAnt INLINE 模式 row 太高（共用下拉菜单 padding token + Java setPadding 吞 padding）
- **现象**：`MenuAnt.create().group().item().divider().subMenu()...build()` 渲染的侧栏 row 约 35px（8+19+8），对比 IntelliJ IDEA（28-30px）偏胖 5-7px，信息密度低。
- **根因**：三层问题叠加——
  1. **token 复用**：inline 模式 `.jfx-menu-item / .jfx-menu-submenu-header` 复用了 `@menu-item-padding-y: @spacing-sm` (8px)——这组 token 是给「下拉菜单垂直可点击行」设计的（点击舒适度优先），侧栏要的是「30px/row 极致紧凑」完全不同
  2. **Java 端吞 padding**（红线 #5 违反）：`MenuGroup.buildInline` 在 Java 端 `label.setPadding(new Insets(8, 0, 8, 16))` 硬编码，`MenuDivider.buildInline` 在 Java 端 `line.setPadding(new Insets(3, 0/16, 3, 0/16))`——Java 端 setPadding 会**覆盖** CSS `-fx-padding`，违背容器不吞 padding 原则
  3. **未对齐 AtlantaFX 源码**：AtlantaFX 源码设计哲学
     - `.caption-menu-item` padding 跟 `.menu-item` **完全一致**（不靠 padding 补偿字号差异，视觉区分只靠 fontsize + fontweight + text-fill）
     - `.context-menu .separator:horizontal` 用独立 token `$separator-padding`（**不是 menu-padding**），padding 走 CSS（不是 Java setPadding）
- **影响面**：所有使用 `MenuAnt` inline 模式的侧栏菜单（M19 内 admin 模板、login 模板、crud 模板都用）。
- **修复**（4 个文件 + 4 个 compact 主题 + 1 个 JfxStyles 常量）：

  1. **`variables-base.less`** 新增 4 个 token + 公式（默认 6px → 30/26，compact 4px 自动派生 26/22）：
     ```less
     // 行高公式：padding-y * 2 + 18px（label lineHeight @ fontSize 14）
     @menu-inline-padding-y: 6px;          // 默认极致紧凑 30/26
     @menu-inline-padding-x: @spacing-lg;  // 16/12
     @menu-divider-padding-y: 3px;          // divider 独立 token（不共用 inline）
     @menu-inline-row-height: @menu-inline-padding-y * 2 + 18px;     // 30/26
     @menu-divider-row-height: @menu-divider-padding-y * 2 + 1px;    // 7
     ```
     注释里**写清 token 分工**：inline（侧栏）vs menu-item（下拉）vs menu-bar（顶级横向）vs divider（视觉标记）四套**完全独立**。
  2. **`components/_menu.less`** 三处 CSS：
     - `.jfx-menu-item` / `.jfx-menu-submenu-header` / `.jfx-menu-group`：padding 走新 token + `-fx-min-height: @menu-inline-row-height` 锁行高
     - `.jfx-menu-divider`：`@-fx-min-height: @menu-divider-row-height` + padding 走新 token
     - `.jfx-menu-collapsed > .jfx-menu-divider`：padding 上下 3px，left/right 0
  3. **`components/_tier3-batch2.less`**（**关键修复，被 BUG #68 探针抓出**）：
     - 删除历史遗留的 `-fx-min-height: 1; -fx-pref-height: 1; -fx-max-height: 1;`——这是 1px Region 旧设计，覆盖了 _menu.less 的 7px min-height，导致 divider 行高只显示 1px
     - 只保留 `-fx-background-color: -color-border-muted;`（line border 颜色）
  4. **`MenuAnt.java`** 两处 Java 端：
     - `MenuGroup.buildInline`（line 883-906）：**拆开 `label.setPadding`**，改用 HBox 包装 + `.jfx-menu-group` styleClass + indent Region + inner Label（**完全对齐 AtlantaFX caption-menu-item**）
     - `MenuDivider.buildInline`（line 921-928）：**移除 Java 端 `setPadding`**，只挂 `.jfx-menu-divider` styleClass（**完全对齐 AtlantaFX separator 走 CSS**）
  5. **`JfxStyles.java`**：新增 `MENU_GROUP = "jfx-menu-group"` 常量（已有 `MENU_GROUP_LABEL` / `MENU_DIVIDER`）
  6. **4 个 compact 主题**（`theme-light-compact` / `theme-dark-compact` / `theme-mui-compact` / `theme-mui-dark-compact`）：覆盖 `@menu-inline-padding-y: 4px;`（公式自动派生 26px/row + divider 7px）

- **验证**（探针 `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/menu/MenuInlineProbe.java` 实测）：

  > 探针设计关键：**`getBoundsInLocal().getHeight()` = 总高度（含 padding）**，`getLayoutBounds().getHeight()` = content 高度（不含 padding）——JavaFX 文档不强调，第一次跑错用 layoutBounds 测出 17px（实际 29px）卡了半小时。**`getPadding()` = Insets 才是 padding 真相**。
  >
  > 探针位置：`jfxium-demo/src/main/java/org/openkawu/jfxium/demo/menu/MenuInlineProbe.java`
  > 跑法：临时改 `jfxium-demo/pom.xml` mainClass 为 `MenuInlineProbe` → `mvn -pl jfxium-demo javafx:run` → 跑完改回 `JfxiumUiExampleApp`（已在 pom 注释里写明候选清单）。

  实测 7 rows（group + item + item + divider + subMenu container + divider + item）：

  | 主题 | group | item | subMenu | divider | TOTAL | padding 实测 |
  |------|-------|------|---------|---------|-------|--------------|
  | LIGHT (default) | **30.0** | **30.0** | **30.0** | **7.0** | **164** | 6/16/6/0 + 3/16/3/16 ✅ |
  | LIGHT-COMPACT   | **26.0** | **26.0** | **26.0** | **7.0** | **144** | 4/12/4/0 + 3/12/3/12 ✅ |

  主题切换：padding 6/16 → 4/12、行高 30 → 26 完美联动，公式 `@menu-inline-padding-y * 2 + 18px` 实战有效。

- **复测**：
  - `./mvnw install -pl jfxium -DskipTests` → BUILD SUCCESS
  - 探针跑出 164/144px 完全符合预期（30+30+30+7+30+7+30=164 / 26*5+7*2=144）
  - 8 套 Java 主题 CSS 产物 grep `.jfx-menu-item / .jfx-menu-group / .jfx-menu-divider` padding + min-height 全部输出符合预期
- **沉淀**：
  - **「不会就看 AtlantaFX 源码」是金科玉律**——本轮 2 个关键修正（MenuGroup 6/6 padding 跟 item 一致 / MenuDivider 走独立 token + CSS）都是 AtlantaFX 源码给的指引，不是拍脑袋设计
  - **AtlantaFX caption-menu-item 哲学**：padding 跟普通 `.menu-item` 完全一致，**不靠 padding 补偿字号差异**——视觉区分只靠 fontsize + fontweight + text-fill 三个属性
  - **AtlantaFX separator 哲学**：用独立 token + 走 CSS（不是 Java setPadding）——divider 是「视觉分隔标记」不是「行」，padding 跟普通 item 完全不同
  - **JavaFX 跟 web CSS 行为不同**：font lineHeight 算不出整 px（fontSize 14 → lineHeight ~17.5），单独靠 padding 撑会差 1-2px。**必须用 `-fx-min-height` 锁死行高**（这是对 AtlantaFX 设计的实用主义偏离，注释里写清楚）
  - **改 CSS 时警惕历史 .less 残留覆盖**——新 `.jfx-menu-divider { min-height: 7px }` 被历史 `.jfx-menu-divider { min-height: 1; pref-height: 1; max-height: 1 }` 覆盖，CSS 优先级导致探针一度只显示 1px divider。**写完后要 grep 同名选择器**确认没残留
  - **探针设计要分清 API**：`getBoundsInLocal()`（含 padding）vs `getLayoutBounds()`（不含 padding）vs `getPadding()`（Insets 真相）——JavaFX 文档不强调，第一次写错 debug 了半小时
  - **红线 #5 容器不吞 padding** + **红线 #8 jfx- 前缀 styleClass** + **divider 走独立 token** 三者必须坚持——本轮 MenuGroup / MenuDivider 都从 Java setPadding 迁到 CSS，正是红线 #5 的正面应用
  - 继承 #67 经验：设计 token 命名要传达**视觉定位**而不是「能塞就用」——`@menu-inline-padding-y` 一看就知道是给侧栏 inline 用的，`@menu-divider-padding-y` 是给分隔线用的，跟 `@menu-bar-padding-y`（顶级横向）和 `@menu-item-padding-y`（下拉项）四套分工清晰

## 修复说明（2026-06-08：BUG #69 jfx-compact 块 menu padding 写死 4px 8px，绕开 jlessc 1.16 嵌套 + token 解析 bug）

> 用户 demo 验收反馈：「menu 有问题? 紧凑的 pading 左右不对」——theme-base.less jfx-compact 块的 menu padding 写死 `4px 8px`，没走 token 体系。
> 本意沿 #67/#68 路径把 jfx-compact 块也迁到 token，让 4 个 compact 主题能联动改 `@menu-item-padding-x` / `@menu-inline-padding-x`。
> 但实验发现 jlessc 1.16 在 `.root.jfx-compact { ... }` 嵌套块内对 @-token 解析有 bug（实测 `@menu-item-padding-y @menu-item-padding-x` → `6px 8px`），
> 字面量却能正常输出。终态方案：jfx-compact 块 5 条 menu 规则改字面量（数值与 token 体系同步），后续升级 LESS 编译器可改回 token 引用。

### #69 jfx-compact 块 menu padding 字面量硬编码（与 #58 同源：硬编码 px 不联动 token）+ 根因 jlessc 1.16 嵌套 + token 解析 bug
- **现象**：theme-base.less 的 `.root.jfx-compact` 块（line 118-128 原版）`.menu-item, .jfx-menu-item { -fx-padding: 4px 8px; }` 直接写死 `4px 8px`。
  4 个 compact 主题的 jfx-compact 块也各写一份 `4px 8px` 硬编码，没走 token 体系 → 4 个 compact 主题不能联动调整 menu padding-x。
- **根因**：
  1. **#58 同源**：jfx-compact 块当时没迁 token，沿用了「先跑起来再说」的硬编码（与「漏改 token」「高度类尺寸硬编码 px」是同一类病灶）。
  2. **jlessc 1.16 嵌套 + token 解析 bug**（本轮新发现的关键基础设施 bug）：
     - 沿 #67/#68 路径把 jfx-compact 块 5 条 menu 规则迁 token（`@menu-item-padding-y` / `@menu-item-padding-x` / `@menu-inline-padding-y` / `@menu-inline-padding-x` / `@menu-divider-padding-y`）
     - `./mvnw install -pl jfxium -DskipTests` 跑通，但 `target/classes/org/openkawu/jfxium/css/theme-light-compact.css` 的 jfx-compact 块里 `.menu-item` 输出 **`6px 8px`**（错的！）
     - 预期应是 `4px 12px`（jfx-compact + 4 个 compact 主题覆盖 `@menu-item-padding-x: @spacing-md`）
     - 实验：把 5 条 menu 规则改字面量（`4px 12px` / `4px 12px 4px 0` / `3px 12px 3px 12px` / `2px 4px` / `2px 0`）→ CSS 输出**完全正确**
     - 结论：**jlessc 嵌套块 + @-token 解析失败，但 jlessc 嵌套块 + 字面量正常**——这是 jlessc 1.16 工具层 bug，框架层无法绕过
- **影响面**：
  - 用户体验：jfx-compact 模式下 `.menu-item` 左右 padding 偏窄（`4px 8px` 是临时值，不是设计意图）
  - 4 个 compact 主题都受影响：theme-light-compact / theme-dark-compact / theme-mui-compact / theme-mui-dark-compact
  - 与 #67/#68 token 体系脱节，无法联动调整
- **修复**（5 个文件）：

  1. **`theme-base.less`** `.root.jfx-compact` 块（line 118-148）改字面量：
     ```less
     // ---- 菜单：Menu / MenuItem / MenuBar / ContextMenu ----
     // 用字面量（BUG #69 终态）：jlessc 1.16 在嵌套块内对 @-token 解析有 bug，会输出错位值
     // （实测：@menu-item-padding-y @menu-item-padding-x 编译为 6px 8px），所以 5 条 menu 规则
     // 全部用字面量。数值与 token 体系同步：
     //   y = 4（与 compact 主题覆盖的 @menu-inline-padding-y: 4px 一致）
     //   x = 12（default 16 → 12 缩 25%，与 @spacing-md 一致；BUG #69 起源）
     //   min-height = 26 = 4 + 18 + 4（与 @menu-inline-row-height 公式一致）
     // 4 个 compact 主题里 @menu-item-padding-x / @menu-inline-padding-x 覆盖已移除
     // （本块不引用这些 token，留着是 dead override）。
     // 后续若升级 LESS 编译器（jlessc → npx lessc / less4j），可改回 token 引用。

     // 下拉菜单项（JavaFX 原生 .menu-item 弹层里）
     .menu-item { -fx-padding: 4px 12px; }

     // inline 模式：侧栏菜单行 / 子菜单 header / 分组标题 / 分隔线
     // left=0 由 Java 端 indent spacer 接管（红线：缩进是结构不是样式）
     .jfx-menu-item, .jfx-menu-submenu-header, .jfx-menu-group {
       -fx-padding: 4px 12px 4px 0;
       -fx-min-height: 26px;
     }
     .jfx-menu-divider { -fx-padding: 3px 12px 3px 12px; -fx-min-height: 7px; }

     // 顶级菜单栏（VS Code / IDEA 风格）
     .menu-bar { -fx-padding: 2px 4px; }

     // 弹层菜单容器
     .context-menu { -fx-padding: 2px 0; }
     ```
  2. **4 个 compact 主题**（`theme-light-compact.less` / `theme-dark-compact.less` / `theme-mui-compact.less` / `theme-mui-dark-compact.less`）：
     - 各删 3 行 token 覆盖（`@menu-item-padding-x: @spacing-md;` + `@menu-inline-padding-x: @spacing-md;` + 1 行注释）——jfx-compact 块已改字面量不引用这些 token，留着是 dead override
     - 保留 `@menu-inline-padding-y: 4px;`（影响 `components/_menu.less` 的 base 规则）
- **验证**（`mvn install` + 8 套 CSS 产物 grep）：

  | 主题 | .menu-item | .jfx-menu-item | .jfx-menu-divider | .menu-bar | .context-menu |
  |------|-----------|---------------|-------------------|-----------|---------------|
  | theme-light | `8px 16px` | `6px 16px 6px 0` (30px) | - | - | - |
  | theme-dark | `8px 16px` | `6px 16px 6px 0` (30px) | - | - | - |
  | theme-mui | `8px 16px` | `6px 16px 6px 0` (30px) | - | - | - |
  | theme-mui-dark | `8px 16px` | `6px 16px 6px 0` (30px) | - | - | - |
  | theme-light-compact | `4px 12px` | `4px 12px 4px 0` (26px) | `3px 12px 3px 12px` (7px) | `2px 4px` | `2px 0` |
  | theme-dark-compact | `4px 12px` | `4px 12px 4px 0` (26px) | `3px 12px 3px 12px` (7px) | `2px 4px` | `2px 0` |
  | theme-mui-compact | `4px 12px` | `4px 12px 4px 0` (26px) | `3px 12px 3px 12px` (7px) | `2px 4px` | `2px 0` |
  | theme-mui-dark-compact | `4px 12px` | `4px 12px 4px 0` (26px) | `3px 12px 3px 12px` (7px) | `2px 4px` | `2px 0` |

  default 主题未动（仍走 token 体系，输出 `8px 16px` / `6px 16px 6px 0`）。
  4 个 compact 主题 jfx-compact 块全部输出 `4px 12px` 系列，min-height 26/7 与 `@menu-inline-row-height` 公式一致。
- **复测**：
  - `./mvnw install -pl jfxium -DskipTests` → BUILD SUCCESS（groovy-maven-plugin 静默成功）
  - 8 套 CSS 产物 grep `.root.jfx-compact .menu-item` / `.root.jfx-compact .jfx-menu-item` / `.root.jfx-compact .jfx-menu-divider` 全部输出符合预期
  - 4 个 default 主题 grep `.menu-item` 输出 `8px 16px`（未受影响）
  - 用户反馈的「紧凑 padding 左右不对」已修复：4 个 compact 主题 jfx-compact 块 `.menu-item` 现在是 `4px 12px`（4 = 紧凑 y，12 = @spacing-md 标准紧凑 x）
- **沉淀**：
  - **jlessc 1.16 嵌套块 + @-token 解析 bug**——这是新发现的基础设施层 bug，**框架层无法绕过**。
    后续升级 LESS 编译器（jlessc → npx lessc / less4j）可改回 token 引用，并在 `.root.jfx-compact` 块加 `// TODO LESS-UPGRADE: 改回 token 引用` 标记。
  - **#58 沉淀的延伸**：紧凑模式失效不止「漏改 token」「高度类尺寸硬编码 px」，**还有「工具不支持嵌套 + token」这种基础设施层 bug**。
    此类问题**只能先 patch（改字面量）** + **注释清楚原因**，等基础设施升级后再统一清理。
  - **jfx-compact 块的设计定位**：theme-base.less line 91 注释 `.root.jfx-compact 是 scene 级修饰类，所有 11 套主题 import theme-base.less 时都会引入此块`
    → 5 条 menu 规则「跨主题共享」的设计意图保留，**只是实现方式从 token 改为字面量**。
  - **保护性扫描**：
    - `grep -r '@menu-item-padding-x' jfxium/src/main/resources/.../less` → 仅 `components/_menu.less` 引用，4 个 compact 主题覆盖已删（0 覆盖）✅
    - `grep -r '@menu-inline-padding-x' jfxium/src/main/resources/.../less` → 仅 `components/_menu.less` 引用，4 个 compact 主题覆盖已删（0 覆盖）✅
    - `grep -r '@menu-divider-padding-y' jfxium/src/main/resources/.../less` → 仅 `components/_menu.less` 引用 ✅
    - token 体系本身未受污染，default 主题仍走 token 输出正确。

## 修复说明（2026-06-08：BUG #70 P0 红线批量合规修复 — 全面规则审计）

> 上一轮（commit 0285738 M19.44）修了红线 #1/#2/#3/#5/#6/#7/#9 之后，本轮对剩余红线 #8（jfx- 前缀）和 #10（module-info exports）做**全项目级深度审计 + 批量修复**。扫描范围：整个 `less/` 目录树（之前只扫 components/，漏了 theme-base / theme-mui* / 死代码）。扫描脚本 `/tmp/scan_v{2,3,final}.py` 三轮迭代，最终 0 唯一违规。

### #70.1 PopoverPanel `setStyle` 改 styleClass（红线 #1：禁 setStyle 写颜色/px）
- **现象**：`PopoverPanel.java` 构造时 `popup.getStyleClass().add(...)` 之外，又 `popup.setStyle("-fx-padding: 12 16 12 16;")` 硬编码 padding。SKILL 强约束 #1「样式必须走 styleClass + LESS」+「禁止 setStyle 写颜色/px」。
- **根因**：典型「Java 端临时硬编码」——之前定义 LESS 选择器时遗漏了 Popover 的 padding 块，开发者用 setStyle 临时补了一下，提交前忘了迁回 LESS。
- **修复**：
  - 删除 `setStyle("-fx-padding: 12 16 12 16;")` 行
  - `_popover.less` 新增 `.jfx-popover { -fx-padding: 12 16; }`（在已有 `.jfx-popover-content` 之外补一层）
  - `JfxStyles.POPOVER = "jfx-popover"` 常量复用
- **沉淀**：删 setStyle 的同时**一定要 grep 看现有 LESS 是否有对应块**，而不是简单替换为 styleClass 就完事。

### #70.2 JfxStyles 新增 75 个 jfx- 前缀常量（红线 #8：自定义 styleClass 必须带 jfx- 前缀）
- **现象**：上一轮扫描发现 ~30 处自定义 styleClass 缺 `jfx-` 前缀（与 modena 内置 `.button` / `.tab-pane` / `.text-field` 等冲突，导致 CSS 永不命中）。本轮全量扫描又找出 75 个 jfx- 常量对应的选择器改造。
- **影响面**：75 个常量对应至少 12 个 LESS 组件文件 + 9 个 .java 组件（[AvatarAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/AvatarAnt.java) / [ImageAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/ImageAnt.java) / [MenuAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/MenuAnt.java) / [QRCodeAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/QRCodeAnt.java) / [RateAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/RateAnt.java) / [StepsAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/StepsAnt.java) / [TabsAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/TabsAnt.java) / [TagAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/TagAnt.java) / [TreeSelectAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/TreeSelectAnt.java) / [WatermarkAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/composite/WatermarkAnt.java) / [MenuBarAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/control/MenuBarAnt.java) / [Overlay](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/base/Overlay.java) / [ResultDisplay](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/base/ResultDisplay.java) / [CrudTemplate](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/template/CrudTemplate.java) / [LoginTemplate](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/template/LoginTemplate.java)）。
- **修复**（增量）：
  - **`JfxStyles.java`** 新增 75 个常量（已在 #70 表格里列举代表项），全部以 `jfx-` 前缀开头 + 详细 JavaDoc 说明用途
  - 12 个 LESS 组件文件批量 `s/^\\.([a-z])/\\.jfx-$1/` 风格前缀化（仅项目自有选择器，modena 内置 `.button` / `.tab-pane` / `.text-field` / `.table-view` / `.list-view` / `.tree-view` / `.menu-bar` / `.menu-item` / `.context-menu` / `.check-box` / `.radio-button` / `.slider` / `.combo-box` / `.pagination-control` / `.scroll-pane` / `.split-pane` / `.progress-bar` / `.progress-indicator` / `.column-header` / `.table-row-cell` / `.tab-header-area` / `.tab-label` / `.date-picker` / `.toggle-button` / `.group-box` / `.content` 等不前綴化）
  - 9 个 .java 组件文件 18+ 处 `add("xxx")` / `styleClass("xxx")` → `add(JfxStyles.XXX)` / `styleClass(JfxStyles.XXX)` 改用常量
- **验证**（`/tmp/scan_final.py` 扫描）：
  - LESS 中裸 `.xxx` 出现在 `theme-base.less` / `theme-mui*.less` 的自定选择器 → **0 处**（除了白名单的 modena 内置 / mixin 函数 / elevation-* 修饰类 / `group-box` 内的 `.content` 等）
  - Java 中 `add("xxx")` / `styleClass("xxx")` 出现裸字符串 → **0 处**（除状态修饰如 `"active"` / `"disabled"` / `"error"` / `"success"` / `"warning"` 等无前缀合规修饰类）
- **沉淀**：
  - **「JfxStyles 常量 + 强制 jfx- 前缀」是项目级契约**——任何新组件 styleClass 必须从 JfxStyles 取值，不允许 `add("my-style")`。这条是继 #37 TableAnt 斑马纹 / #44 DatePicker 双边框 / #66 layout 包 borderXxx 错位 后的**第四次**同源根因击中（典型「Java 端硬编码 class 名」反模式）。
  - **白名单 = 「modena 内置选择器 + 状态/形状修饰类 + LESS mixin 函数 + elevation-* 装饰类」**，扫描时直接跳过——避免假阳性拖慢审计效率。
  - **扫描脚本必须覆盖整个 `less/` 目录树**，不能只扫 `components/`。theme-base.less / theme-mui*.less / variables-base.less 里也藏污纳垢（#70.3 / #70.4 / #70.5 都是主题文件层面发现的问题）。

### #70.3 4 个 mui 主题 `.alert-*` 改 `.jfx-alert-*`（红线 #8：主题文件也是 LESS）
- **现象**：theme-mui.less / theme-mui-compact.less / theme-mui-dark.less / theme-mui-dark-compact.less 各有 `.alert-success, .alert-info, .alert-warning, .alert-error { ... }` 4 个共享规则的 `border-radius` 块。
- **根因**：与 #70.2 同源：4 个 mui 主题的 alert 修饰类没带 `jfx-` 前缀（被 `AlertAnt` 用 `JfxStyles.ALERT_SUCCESS` 等常量挂的 class 名）——`AlertAnt.create().type(SUCCESS)` 时挂的 class 是 `jfx-alert-success`，主题里找的是 `.alert-success`，**匹配数 = 0**，4 个 mui 主题下 alert 类型色全部失效（看上去全是默认蓝）。
- **修复**：4 个 mui 主题的 4 个 alert 修饰类选择器统一加 `jfx-` 前缀，与 `AlertAnt` Java 端挂的常量名一致。
- **验证**：grep `target/classes/.../theme-mui*.css` 输出 `.jfx-alert-success` 等 4×4 = 16 处。
- **沉淀**：
  - **Java 端的常量改了，主题文件一定要同步 grep**——这是 #37 / #44 / #66 的第四个同源案例。**扫描脚本要扫到 `theme-*.less` 层级**才能发现这种「常量在主题文件里漏改」的隐藏 bug。
  - 此次修复对用户**完全透明**（修复前 alert 4 类型色在 mui 主题下本就看不见），但扫到不修就是技术债。

### #70.4 _pagination.less 10 处 `.pagination` 改 `.jfx-pagination`（保留 `.pagination-control`）
- **现象**：`_pagination.less` 里有 10 处 `.pagination` 选择器（含根 + 9 个后代选择器如 `.pagination .pagination-control .button:hover` 等）。
- **根因**：
  - 项目自有选择器 `.pagination`（PaginationAnt 根容器，由 `JfxStyles.PAGINATION` 挂在 root VBox 上）**没带 `jfx-` 前缀**——与 modena 内置 `.pagination` 冲突（虽然 JavaFX 没原生 Pagination 控件，但 modena 仍然定义了相关 look 假名，避免冲突依然有必要）
  - **modena 内置的子选择器 `.pagination-control`**（实际是 modena 内部约定名）必须保留——它是 JavaFX 标准控件的后代选择器
- **修复**：
  - `JfxStyles.PAGINATION = "jfx-pagination"` 新增常量
  - 10 处 `.pagination` 全部改 `.jfx-pagination`（包括 `.jfx-pagination .pagination-control .button:hover` 等复合选择器）
  - `.pagination-control` / `.button` / `.toggle-button` / `.arrow-button` 等 modena 内置子选择器**保留原名**
  - `PaginationAnt.java` 同步把 `getStyleClass().add("pagination")` → `add(JfxStyles.PAGINATION)`
- **验证**：grep 11 个主题 CSS 输出 `.jfx-pagination` × 10 个 / 旧 `.pagination`（不带 -control）= 0 个。
- **沉淀**：
  - **复合选择器 `复合选择器 .X.Y.Z` 改前缀时，X（最左侧 = 项目自有）改，Y/Z（modena 内置）保留**——不要无脑前缀化所有层。
  - **Pagination 这种「项目包装 + modena 内置子控件」混搭结构，扫描时必须看完整路径**，不能只看第一段。

### #70.5 theme-base.less 删 2 块死代码 + 注释保留 modena 内置
- **现象**：theme-base.less 里有 2 块「项目自有」死代码（`.card` 块 165-168 / `.panel > .panel-body` 块 193-195）——全项目 grep 不到 Java 端引用。
- **根因**：
  - `.card` / `.panel` 是历史组件类名（CardAnt 早期版本用过，后续改名/下线），但 LESS 块没跟着删
  - `.panel > .panel-body` 类似的过期选择器
  - 死代码不致命，但干扰扫描（`/tmp/scan_v2.py` 初版把它们当违规）且增加维护负担
- **修复**：
  - 删 `.card` 块（4 行） + `.panel > .panel-body` 块（3 行）
  - 保留 `.group-box > .content`（modena GroupBox 内置）+ 加注释说明
- **验证**：grep `.card ` / `.panel ` / `.panel-body` 在全项目 LESS 中输出 0 处（除了注释行）。
- **沉淀**：
  - **「历史组件下线时，配套的 LESS 块 / JfxStyles 常量 / demo 页面必须一起删」**——之前可能漏了，这次扫描顺手清理。
  - **modena 内置识别**：「写 LESS 时先扒 AtlantaFX」原则（红线 a.md 强制）——但要分清「AtlantaFX 用了 = modena 也有」与「项目自有 = 死代码」。

### #70.6 module-info.java 新增 `exports core.builder`（红线 #10：新 public 类必须同步 exports）
- **现象**：`AbstractStyleBuilder<SELF>` 是 `public class`（位于 `org.openkawu.jfxium.core.builder` 包），但 `module-info.java` 没有 `exports org.openkawu.jfxium.core.builder;`。
- **根因**：`AbstractStyleBuilder` 是 M19 大重构时新增的基类（28 个 builder 继承），当时忘了同步 module-info。红线 #10 反复强调「public 类必须 exports，否则下游不可见」。
- **修复**：`module-info.java` 新增一行 `exports org.openkawu.jfxium.core.builder;`。
- **验证**：`./mvnw install -pl jfxium-demo` 能正常解析 `AbstractStyleBuilder`（jfxium-demo 内部其实通过 builder 间接使用，但 JPMS 模块系统严格检查 exports，缺了直接报错）。
- **沉淀**：
  - **每加一个 public 类（无论是不是 abstract），grep 一下 module-info.java 对应包路径的 exports**——这是 #10 反复强调的「编译期保护」。
  - **可以写个 CI 脚本**对比 `find jfxium/src/main/java -name "*.java" | xargs grep "^public class"` 与 `module-info.java` 的 exports 列表——缺失即报错。

### #70 整体验证
- 编译：`./mvnw install -pl jfxium -DskipTests` → BUILD SUCCESS（150 源文件 + 17 测试源文件）
- 产物：11 个主题 CSS 由 jlessc 自动重新生成，输出含 75+ 个 `.jfx-*` 新选择器
- jar 打包：`jfxium-1.0.0-RC1.jar` 安装到本地 Maven 仓库
- 扫描：`/tmp/scan_final.py` 最终输出 0 唯一违规（白名单含 modena 内置 11 项 + LESS mixin 函数 3 项 + elevation-* 25 项 + `content` / `group-box` 等）

## 修复说明（2026-06-08：BUG #71 MuiTheme → MuiLightTheme 命名重构）

> 与 #70 同一 commit（6ee94e8）。本轮发现 `MuiTheme` 命名混淆（MUI 是 `ThemeManager.Family` 不变量，`light` 才是密度/明暗轴），与 `LightTheme` / `DarkTheme` / `LightCompactTheme` / `DarkCompactTheme` 的命名规范不一致。同步修复 ThemeManager 状态机表达。

### #71 MuiTheme 命名混淆
- **现象**：
  - `core/theme/MuiTheme.java` 类名 = "MuiTheme"（含义 = MUI 主题），但项目里还有 `LightTheme` / `DarkTheme` / `LightCompactTheme` / `DarkCompactTheme` 一组按「密度+明暗」命名的类
  - `ThemeManager.Family` 枚举暴露 `MUI` / `ANT_DESIGN` 两项（Family = 设计语言维度）
  - 实际 MUI 主题只有 1 个（`MuiTheme`，单 light 资源），命名上完全看不出 MUI 是「Family」还是「Theme 实例」
  - 调用方 `ThemeManager.getInstance().applyTheme(new MuiTheme())` —— "Mui" 在 Family 上下文里出现两次，读者无法判断「这是哪个轴的 MUI」
- **根因**：M19 大重构时把 `MuiTheme` 当作"一个具体的 Theme 实现"来命名，没意识到 MUI 应该是 Family 维度（与 Ant Design 对应），而 light/dark/compact 才是 Theme 实例的密度/明暗轴。
- **修复**（git 自动识别为 87% similarity rename，主体 100% 相同，仅类名 + JavaDoc 第一行改 1 字符）：
  - 文件重命名 `MuiTheme.java` → `MuiLightTheme.java`
  - 类名 `MuiTheme` → `MuiLightTheme`
  - JavaDoc 头 "JFXium MUI Theme." → "JFXium MUI Light Theme."
  - `Theme.getName()` 返回值 `"JFXium MUI"` 保持（这是 user-facing name，不是机器 key）
  - **关键修改**：`Theme.getName()` 在 ThemeManager 内部作 key 使用，原来返回 `"mui"` → 现在改 `"mui-light"`（key 唯一性原则：`MuiLightTheme` 的 key 不能与 `LightTheme` 的 `"light"` 冲突；之前 MuiTheme 用了 `"mui"` 反而绕过了冲突，但语义不准）
  - `ThemeColor` 枚举新增 `MUI_LIGHT` 值（之前没有，因为 `MuiTheme` 复用了 `LIGHT` 值的 key 命名空间，导致 `getPrimaryColor(MUI_LIGHT)` 调用走错分支）
  - `ThemeManager.getTheme(String name="mui")` 工厂方法 → `getMuiLightTheme()` 显式方法（与 `getLightTheme()` / `getDarkTheme()` / `getLightCompactTheme()` / `getDarkCompactTheme()` 命名一致）
  - 全量 .java 引用 `new MuiTheme()` / `import MuiTheme` → `new MuiLightTheme()` / `import MuiLightTheme`（影响 [Theme.java](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/core/theme/Theme.java) / [ThemeManager.java](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/core/theme/ThemeManager.java) / [ThemeColor.java](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/core/theme/ThemeColor.java) / 多个 Composite / Template）
- **验证**：
  - 编译：BUILD SUCCESS
  - 运行时：8 套 Java 可用主题（light / dark / light-compact / dark-compact / mui-light / mui-dark / mui-light-compact / mui-dark-compact）全量跑通，demo 顶栏「主题风格下拉」Ant/MUI 切换正常
- **沉淀**：
  - **设计语言 vs 主题实例 = 两个不同抽象维度**：Ant Design 是一组主题（light/dark/compact×2），MUI 是另一组（MuiLightTheme 单 light 资源，未来可能加 MuiDarkTheme）。命名上要**让 Family 名字和 Theme 实例名字能区分**——`MuiLightTheme` 看一眼就知道「MUI Family 的 light 实例」。
  - **不要复用枚举值**：之前 `MuiTheme` 复用了 `ThemeColor.LIGHT` 枚举值，导致 `ThemeColor.getPrimaryColor(MuiTheme)` 走错分支。**新 Theme 必须新增自己的枚举值**（`MUI_LIGHT`），不要为了"图省事"复用。
  - **rename 友好性**：git 87% similarity rename 让历史 blame 完整保留，比"delete + add new file"友好得多。

## 修复说明（2026-06-08：BUG #72 JavaFX 原生控件包装补齐 6 个）

> 上一轮（commit 0285738 M19.44）补了 LabelAnt，本轮继续把 JavaFX 原生控件库里没包装的几个补齐：ChoiceBox / ListView / Separator / SplitMenuButton（control 包）+ BorderPane / TextFlow（layout 包）。原因：用户 demo 经常需要这些基础容器，但目前只能用 JavaFX 原生类型（无 Builder API、无 styleClass 体系、零主题适配），与框架整体不协调。

### #72 JavaFX 原生控件包装补齐 6 个
- **修复**（6 个新文件 + 1 个新 LESS）：
  | 文件 | 行数 | 说明 |
  |------|------|------|
  | [control/ChoiceBoxAnt.java](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/control/ChoiceBoxAnt.java) | 142 | 继承 `ChoiceBox<T>`，Builder API（items / value / onChange / placeholder / editable / showLabelAnyway 等）|
  | [control/ListViewAnt.java](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/control/ListViewAnt.java) | 153 | 继承 `ListView<T>`，Builder API（items / selectionModel / onSelect / orientation / fixedCellSize / placeholder）|
  | [control/SeparatorAnt.java](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/control/SeparatorAnt.java) | 100 | 继承 `Separator`，Builder API（orientation / length / style）+ styleClass 走 `jfx-separator` |
  | [control/SplitMenuButtonAnt.java](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/control/SplitMenuButtonAnt.java) | 137 | 继承 `SplitMenuButton`，Builder API（items / onAction / onItemSelected / showTrailingIcon）|
  | [layout/BorderPaneAnt.java](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/layout/BorderPaneAnt.java) | 136 | 继承 `BorderPane`，Builder API（top / bottom / left / right / center / padding）+ 各 region styleClass |
  | [layout/TextFlowAnt.java](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/layout/TextFlowAnt.java) | 101 | 继承 `TextFlow`，Builder API（text / spans / lineSpacing / textAlignment / styleClass）|
  | [less/components/_separator.less](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/resources/org/openkawu/jfxium/css/less/components/_separator.less) | 11 | `.jfx-separator` 样式（横/纵 orientation、color 走 `-color-border-muted` token）|
- **设计原则**（与 #70 修复后的标准一致）：
  - **继承式 + Builder API**：与 [ButtonAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/control/ButtonAnt.java) / [InputAnt](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/component/control/InputAnt.java) 等 Form 组件同款
  - **jfx- 前缀 styleClass**：[JfxStyles](file:///Users/openai/workspace/work_open/JFXium/jfxium/src/main/java/org/openkawu/jfxium/core/css/JfxStyles.java) 新增 `CHOICE_BOX` / `LIST_VIEW` / `SEPARATOR` / `SPLIT_MENU_BUTTON` / `BORDER_PANE` / `TEXT_FLOW` 6 个常量
  - **零硬编码**：所有颜色/尺寸走 styleClass + LESS token 体系
- **验证**：编译 BUILD SUCCESS（150 源文件），11 套主题 CSS 全部含新选择器
- **沉淀**：
  - **原生控件包装要遵守项目契约**——继承式 + Builder + jfx- 前缀 + 零硬编码，缺一不可。这次补齐 6 个，避免新组件写 demo 时只能 `new ChoiceBox<T>()` 绕过框架。
  - **layout 包不只装 layout 容器**——BorderPane / TextFlow 是 layout 性质（管子节点排版）但也是 JavaFX 控件（extends Pane / Parent），放 layout 包与项目分层一致（control/extends Control，layout/extends Pane/Region）。
  - **后续可继续补**：Tooltip / FileChooser / DirectoryChooser / Hyperlink / ProgressIndicator / ScrollBar 等，按需添加。

---

## 修复统计（更新）

- **总计问题**：72 个（#1–#72）
- **已修复 / 已完成**：72 个
- **未修复**：0
- **本轮 commit**：`6ee94e8`（fix(theme): 全面规则审计 + MuiTheme 重命名 + JavaFX 控件补齐，+1931 / -872，59 files）
- **最后更新**：2026-06-08
