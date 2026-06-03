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
彻底消除 inline `setStyle("-fx-...: -color-...")` 硬编码注入。详见 [PLAN.md](PLAN.md) 第四章里程碑。

**核心数据**：
- 已重构 *Ant 组件：48 个
- 接入公共 `AbstractStyleBuilder<SELF>` 基类：28 个
- 项目级 inline color 注入：**29 个文件 → 0 个文件**
- 顺手修复 3 处隐性 bug：SwitchAnt cursor 残留、CodeBlockAnt spacer 死代码、DividerAnt 文档撒谎

**已知遗留**（详见 PLAN.md P0/P1 计划）：
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