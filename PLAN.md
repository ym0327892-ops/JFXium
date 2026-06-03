# JFXium 项目详细开发计划

> 目标：构建现代化 JavaFX UI 框架，对标 Ant Design 6.x / Material UI / MUI-like Style
> 技术栈：Java 21 + JavaFX 21.0.6 + Maven
> 核心原则：Design Token 驱动、Builder Pattern、完全代码构建 UI、CSS 变量体系
> 组件理念：微型化、封装、可组合性、可扩展性

---

## 一、项目开发约束

1. **开发记录**：完成的、修改的内容需记录到 PLAN.md 合适位置
2. **计划更新**：下一步计划、未完成的计划都需要记录
3. **计划变更**：任何计划改变需综合考虑并更新项目总计划
4. **什么是组件**：微型化、封装、可组合性，如 Modal、Drawer；小组件组装成大组件
5. **开发规范**：详见 [docs/SKILL.md](docs/SKILL.md)

---

## 二、Builder API 命名约定

- **组件入口类**：使用 `Ant` 后缀，如 `ButtonAnt`、`ModalAnt`、`DrawerAnt`
- **内部构造器**：`XxxAnt.Builder`
- **创建入口**：`XxxAnt.create(...)`
- **构建方法**：`.build()` 返回 JavaFX 原生控件
- **API 风格**：`XxxAnt.create(...).xxx().build()`

```java
Button btn = ButtonAnt.create("点击我")
    .type(ButtonAnt.Type.PRIMARY)
    .onClick(e -> System.out.println("点击"))
    .build();

HBox actions = BarAnt.create()
    .left(ButtonAnt.create("取消").build())
    .right(ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build())
    .build();
```

---

## 三、UI 参考资源

- Ant Design 色彩：https://ant.design/docs/spec/colors-cn
- Ant Design 布局：https://ant.design/docs/spec/layout-cn
- AtlantaFX 参考：[AtlantaFX 仓库](https://github.com/mkpaz/atlantafx)  本地 /Users/openai/workspace/work_open/atlantafx

---

## 四、项目进度跟踪

### ✅ 已完成工作

#### Phase 1: 基础设施
- [x] CSS 文件驱动的架构重构
- [x] Theme API 实现（ThemeManager）
- [x] CSS 类名常量化
- [x] 组件封装（Builder Pattern）
- [x] Playground 应用

#### Phase 2: 核心组件
- [x] ButtonAnt（按钮）
- [x] InputAnt（输入框）
- [x] CardAnt（卡片）
- [x] ModalAnt（对话框）
- [x] DrawerAnt（抽屉）
- [x] SelectAnt（选择器）
- [x] AutoCompleteAnt（自动补全）
- [x] MentionsAnt（提及）

#### Phase 3: 扩展组件
- [x] SpinnerAnt（计数器）
- [x] ComboBoxAnt（组合框）
- [x] DatePickerAnt（日期选择）
- [x] TableAnt（表格）
- [x] TreeAnt（树）
- [x] TabsAnt（标签页）
- [x] PaginationAnt（分页）
- [x] MenuAnt（菜单）

#### LESS 主题系统
- [x] 完整色阶定义（0-9 级）
- [x] 语义变量映射（Base/Hover/Active）
- [x] 主题切换机制
- [x] 统一焦点效果规范
- [x] 统一过渡动画规范
- [x] MUI 系列主题
- [x] Dark 主题适配

### 📝 进度时间线

> 按时间顺序记录里程碑。每个里程碑都有"动机 → 产出 → 关键改动"三个维度。

---

### 🎯 M0 历史阶段（~2026-05-13）

**最近一次旧版更新**：
- 统一了所有组件的交互状态（hover/armed/pressed/disabled）
- 修复了 MUI 系列主题输入框焦点边框宽度问题
- 调整了焦点外发光大小（4px → 2px）

---

### 🎯 M1 活跃 Bug 修复（2026-05-17）

**动机**：BUG.md 顶部存在 3 条未编号未关闭的活跃问题描述，与表格里"全部完成"自相矛盾，需要落地。

**产出**：3 条 bug 全部修复并补编号入表（详见 [BUG.md](BUG.md) #21/#22/#23）。

**关键改动**：
- [x] **#21 Table 选中色对比度**：`theme-base.less` 中 `-color-cell-bg-selected` 从 `@color-base-1` 改绑到 `@color-accent-subtle`（对齐 Ant Design `controlItemBgActive` token），新增 `:selected:hover` 规则
- [x] **#22 MUI 输入框文字硬编码**：删除 `theme-mui.less` 中遗漏的 `-fx-text-fill: rgba(0, 0, 0, 0.88)` 硬编码（其他 3 个 mui 子主题早已修对）
- [x] **#23 焦点效果统一**：4 个 mui 主题的 `:focused` 块从 `-fx-effect: none` 改回 `dropshadow(...)`，对齐 SKILL 强约束 #5

---

### 🎯 M2 项目摸底与文档修正（2026-05-17）

**动机**：开始大重构前，需要先把项目实情摸清楚，避免凭印象动手；同时清理已发现的文档与代码不一致。

**产出**：项目结构与约定全面对齐文档；BUG.md/PLAN.md/README_CN 三处一致性修正。

**关键改动**：
- [x] 项目实情摸底：多模块结构（`jfxium` + `jfxium-demo`）、Java 21 + JavaFX 21.0.6、8 个 lessc execution 在 `generate-resources` 阶段
- [x] 发现并记录隐性依赖：LESS 编译强依赖宿主机 Node.js
- [x] BUG.md 重整：顶部 3 条未编号描述移入表格，按编号重排，新增"修复说明"小节
- [x] PLAN.md 文件结构区从单模块视角扩展为多模块（补 `jfxium/` + `jfxium-demo/` 路径前缀）

---

### 🎯 M3 布局组件体检与重构（2026-05-17）

**动机**：用户希望"UI 更好扩展、优雅实现"，但布局组件存在多个隐藏问题。

**产出**：15 个布局组件完成体检；6 个重灾组件完全重写。

**关键改动**：
- [x] 完整体检 15 个布局组件（找到 6 重灾、9 中度、文档撒谎 1 处、孤儿类 1 个）
- [x] **LayoutAnt** → 重构为 AppShellAnt 的轻量委托（90% 重复代码消除）
- [x] **FlexAnt** → 重写：build() 返回 `Pane` 不再撒谎、wrap 真用 FlowPane、justify=BETWEEN/AROUND/EVENLY 用 Region spacer 实现 CSS 标准语义
- [x] **GridAnt** → 重写：用真 GridPane + percentWidth 实现 24 栅格（替代原"像素当百分比"的错误实现）
- [x] **SpaceAnt** → 修复 BASELINE+VBox 静默失效 bug、split=true 默认渲染原生 Separator
- [x] **DividerAnt** → 实现 text 支持（修复 README 撒谎问题，原代码不支持但文档说支持）
- [x] **FormAnt** → 删除全部 setStyle 拼字符串、状态色 styleClass、labelAlign 改枚举

---

### 🎯 M4 公共基础设施抽取（2026-05-17）

**动机**：M3 重写组件时发现"style + extraStyleClasses + styleClass()"三件套在 9+ 个 Builder 重复实现，应该抽公共基类。

**产出**：新建 `AbstractStyleBuilder<SELF>` 基类；28 个 *Ant 组件接入；通用工具类同步加 styleClass 钩子。

**关键改动**：
- [x] 新建 `core/builder/AbstractStyleBuilder.java`（self-bounded 泛型，支持链式 API 返回子类自身）
- [x] 提供 `applyStyles(Node)` 与 `applyStyles(Styleable)` 两个重载，覆盖 Tooltip 这类非 Node 的 Styleable
- [x] 接入 28 个 *Ant 组件（A 类 17 个完整三件套 + B 类 11 个仅 style 字段）
- [x] `core/layout/Layouts.java`（VBox/HBox/Grid 工厂）补 `styleClass()` 方法，对齐 *Ant API

---

### 🎯 M5 SKILL #1 全合规重构（2026-05-17）

**动机**：M3 重构时发现 6 个组件大量 inline `setStyle("-fx-...: -color-..."`)，违反 SKILL 强约束 #1（禁止硬编码）；进一步 grep 发现全项目共 29 个文件有此问题。

**产出**：项目级 inline color 注入**29 个文件 → 0 个文件**；6 个深重构组件 + 22 个批量清扫；激活了原本"死代码"的多段 LESS 选择器。

**关键改动**：

**子阶段 5.1：6 个深重构组件**
- [x] **SwitchAnt** → 删除死代码 textLabel、修复 disabled cursor 残留隐患
- [x] **BadgeAnt** → indicator 三种形态（count/dot/status）styleClass 化（激活原本死代码的 `.badge-count` LESS）
- [x] **AlertAnt** → 4 类型 × 4 inline = 16 处全部走 styleClass + LESS 状态机（激活 `.alert-success/info/warning/error` 死代码）
- [x] **ProgressAnt** → bar/circle 双 Builder 状态色走 LESS（激活 `.progress-bar.success` 死代码）
- [x] **SliderAnt** → tip/range/marks 全部 LESS 化，`-fx-accent` 搬到 `.slider` 选择器
- [x] **CodeBlockAnt** → 7 处 inline 全清；顺手修 spacer 死代码 bug

**子阶段 5.2：22 个批量清扫**
- [x] **Tier 1**（5 个）：IconAnt、EmptyAnt、BackTopAnt、SpinAnt、TimePickerAnt
- [x] **Batch 1**（6 个）：DropdownAnt、AnchorAnt、AutoCompleteAnt、ImageAnt、MentionsAnt、StatisticAnt
- [x] **Batch 2**（6 个）：TypographyAnt、CollapseAnt、TreeSelectAnt、SegmentedAnt、InputNumberAnt、CarouselAnt
- [x] **Batch 3**（5 个）：DrawerAnt、ModalAnt、CascaderAnt、TimelineAnt、TransferAnt（抽出通用 `overlay-*` 选择器）
- [x] **Batch 4**（5 个）：ListAnt、MenuAnt、UploadAnt、StepsAnt、BreadcrumbAnt
- [x] **Batch 5**（2 个）：DescriptionsAnt、CalendarAnt（最复杂，21 处 setStyle）

**抽出的可复用 LESS 选择器组**：
- `.jfx-popup-menu` / `.jfx-popup-menu-item` —— Dropdown / AutoComplete / Mentions / TreeSelect 共用
- `.jfx-overlay-mask` / `.jfx-overlay-panel` / `.jfx-overlay-header` —— Drawer / Modal 共用
- `.jfx-empty` / `.timeline-dot-*` / `.steps-*` 等状态机系列

---

### 🎯 M6 文档完善（2026-05-17）

**动机**：M1-M5 的工作量巨大但散落在对话历史中，需要落档让后续维护者可追溯。

**产出**：本次 PLAN.md 重组为时间线 + README_CN.md 同步重构成果。

**关键改动**：
- [x] PLAN.md：原"📝 当前状态"+ 单一里程碑展开为 M0-M6 六个里程碑
- [x] PLAN.md：第五章下一阶段计划重新分级为 P0/P1/P2/P3 路线图
- [x] BUG.md：末尾新增"项目级里程碑"小节，串联 #21/#22/#23 与全量重构
- [x] README_CN.md：新增"styleClass 体系"章节，主题文件清单补全到 8 个

---

### 📊 整体量化成果（M1-M19 累计）

- 已重构 *Ant 组件：**54 个**（M11 重写 TableAnt / M15 重写 MenuAnt / M16 重写 WatermarkAnt / M18 删 PageAnt 加 CrudTemplate / M19 加 BarAnt **+ 同时删除 ActionBarAnt + Headers 工厂** / M19.6 加 ToggleButtonAnt + MenuButtonAnt + SplitButtonAnt / M19.7 加 SelectableTextAnt）
- **业务模板（template/）**：**3 个**（CrudTemplate @ M18 / LoginTemplate @ M19.16 / DashboardTemplate @ M19.16）
- 总组件量：82 \*Ant + 3 \*Template = **85 个**
- 接入 AbstractStyleBuilder：**32 个**（M19.6 新增 3 个）
- **i18n 国际化**：默认 Locale = `Locale.SIMPLIFIED_CHINESE` / 3 份 properties / 9 个组件迁移 / Messages 静态门面 + ReadOnly localeProperty（M19.18）
- **三件套形状变体**（M19.20）：Switch / CheckBox / Radio 都有 Size + Shape 双维度，可组合 ~12 种视觉
- **响应式布局**（M19.21+M19.22）：GridAnt 6 档断点 + AppShellAnt Sider 折叠/breakpoint 自适应（共享 Breakpoint 枚举）
- 抽出通用 LESS 选择器：**~99 段**（M19.6 加 toggle-button 通用块 + 3 组按钮 size 规则）
- 新增 CssClasses 常量：**295+**（M19.6 复用现有 SIZE_SMALL/LARGE/SHAPE_*，无新增）
- 项目级 inline color 注入：**29 个文件 → 0 个文件**
- 编译诊断：**全部 0 报错**
- 顺手修复的隐性 bug：**4 处**
- 影响文件总数：**100+ 个**
- 新增组件：**7 个**（WatermarkAnt / FilterBarAnt / CrudTemplate / BarAnt / **ToggleButtonAnt** / **MenuButtonAnt** / **SplitButtonAnt**）
- 删除组件 / 工具类：**3 个**（PageAnt @ M18 / ActionBarAnt @ M19 / Headers @ M19）
- 增强组件：**5 个**
  - CardAnt：9 功能（M10）
  - TableAnt：列级+表级 + actionColumn + 4 边框模式 + 隐藏表头 + 双重对齐 + Size 三态 + AtlantaFX 视觉对齐（M11/M11.1/M11.2）
  - MenuAnt：4 模式正交（INLINE/HORIZONTAL × LIGHT/DARK × NORMAL/COLLAPSED + 选中态）（M15）
  - **CheckBoxAnt**：Size 三态 + allowIndeterminate（M19.6）
  - **RadioButtonAnt**：Size 三态（M19.6）
- **完整重写**：WatermarkAnt（M16，对齐 Element Plus，节点 80→1，零开销）
- 新增工具类：**3 个**（SceneLayout / OverlayManager / Spacers；Headers 已于 M19 删除）
- 新增容器 Builder：**8 个**
- 废弃/移除的类：**7 个**（含 M18 PageAnt + M19 ActionBarAnt + M19 Headers）
- 废弃的 API：**4 个**
- SKILL 强约束条数：**18 条**（M19.13 沉淀：项目约束 SKILL 加 #16/#17/#18 + 组件组合规范加 §7「包结构归约」+ 反模式 §4.9/4.10/4.11）
- IconAnt 业务图标：**13 个**
- Admin Demo 沉淀页面：**4 个**（参考实现保留 + selectedKey 接入）
- Showcase Demo 已实现页面：**13 个**（覆盖 80 个 Section / Ant Design 7 大类全覆盖 + 业务模板）
  - 通用：ButtonPage（8）、**ButtonGroupPage（8，M19.6 + M19.6.1，含箭头样式）**、SwitchPage（4）
  - 导航：MenuPage（7）
  - 布局：CardPage（9）、SplitBarPage（5，M19）、CrudTemplatePage（4，M18）
  - 数据录入：InputPage（5）
  - 数据展示：TablePage（10）、TagPage（5）
  - 反馈：ModalPage（5）、DrawerPage（5）
  - 其他：WatermarkPage（5）
- 第三方实现对齐：**2 处**
  - AtlantaFX：TableView 表头/箭头/分割线（M11.2）+ ComboBox/DatePicker/ColorPicker padding（M19.5）
  - Element Plus：Watermark Canvas snapshot + REPEAT 平铺（M16）
- **包结构演化（M18）**：`jfxium/component/` + `jfxium/template/`（新增）+ `jfxium/layout/`（预留）
- **Bar 类组件唯一真相源（M19）**：项目内布局原子层只剩 `BarAnt`，业务模板层 `FilterBarAnt`；ActionBarAnt + Headers 已删除，三段式重复实现 N→1
- **按钮族 API 全对齐（M19.6）**：Button / Toggle / Menu / Split / Radio / CheckBox 全部支持 Size 三态，与 InputAnt/ButtonAnt 视觉一致
- **内部消重（M19）**：CrudTemplate / CardAnt / SurfaceAnt 三处私有 / 工厂式 hbox 实现，全部统一到 BarAnt

---

### 🎯 M7 全局布局管理 + MessageAnt 位置功能（2026-05-21）

**动机**：用户需要更好的全局布局管理方案，以及 MessageAnt 需要支持多位置显示（顶部/底部/中间）。

**产出**：
1. 创建 SceneLayout（Scene 根布局管理器）和 OverlayManager（全局浮层管理器）
2. 独立 8 个容器 Builder 类（VBoxBuilder/HBoxBuilder 等）
3. MessageAnt 支持 TOP/BOTTOM/CENTER 三个位置
4. MyDemo 添加滚动布局

**关键改动**：

**子阶段 7.1：架构重构 - Builder 类独立 + 全局布局管理**
- [x] 创建 8 个独立 Builder 类（`core/container/`）：VBoxBuilder, HBoxBuilder, BorderPaneBuilder, StackPaneBuilder, FlowPaneBuilder, ScrollPaneBuilder, SplitPaneBuilder, GridPaneBuilder
- [x] 创建 `Spacers` 工具类（`core/util/Spacers.java`）：提供 `grow()` 和 `spacer()` 方法
- [x] 标记 `Layouts.java` 为废弃（`@Deprecated`）
- [x] 创建 `SceneLayout.java`（Scene 根布局管理器）- 只提供骨架（BorderPane），不限制内部布局
- [x] 创建 `OverlayManager.java`（全局浮层管理器）- 管理 4 层 z-index

**子阶段 7.2：Drawer/Modal Header 布局修复**
- [x] 修复 DrawerAnt 和 ModalAnt 的 Header 布局问题
- [x] 改用 Region spacer 模式（符合组件组合规范 3.1 Header 三段式）
- [x] 删除错误的 `HBox.setHgrow(titleLabel, Priority.ALWAYS)` 模式

**子阶段 7.3：MessageAnt 位置功能**
- [x] 添加 `Position.TOP` / `Position.BOTTOM` / `Position.CENTER` 三个位置
- [x] 实现独立堆叠管理（顶部/底部/中间消息互不影响）
- [x] 中间位置只显示一个消息（新消息替换旧消息）
- [x] 动画差异：顶部/底部滑入，中间淡入
- [x] 支持手动关闭（`MessageResult.close()`）

**子阶段 7.4：MyDemo 完善**
- [x] 添加 ScrollPane 滚动布局（内容超出窗口高度时自动显示滚动条）
- [x] 添加 MessageAnt、NotificationAnt、AlertAnt 展示
- [x] 添加"中间 Info"按钮展示 CENTER 位置消息

**文档更新**：
- [x] README_CN.md：更新 MessageAnt 文档，添加详细的位置功能说明
- [x] README_CN.md：更新主题加载示例，改用 ThemeManager
- [x] PLAN.md：添加 M7 里程碑记录

**影响文件**：
- `core/container/` (8 个 Builder 类)
- `core/util/Spacers.java`
- `core/layout/SceneLayout.java`
- `core/layout/OverlayManager.java`
- `component/MessageAnt.java`
- `component/DrawerAnt.java`
- `component/ModalAnt.java`
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/MyDemo.java`
- `README_CN.md`

### 🎯 M8 WatermarkAnt 组件实现（2026-05-21）

**动机**：用户需要水印组件，用于在内容上叠加水印（文字或图片），常用于文档保护、版权声明等场景。

**产出**：
1. 创建 WatermarkAnt 组件（支持文字水印和图片水印）
2. 添加 CSS 样式定义（theme-base.less）
3. 添加 CSS 常量（CssClasses.java）
4. 更新文档（README_CN.md）

**关键改动**：

**子阶段 8.1：核心实现（P0）**
- [x] 创建 `WatermarkAnt.java`（文字水印 + 图片水印 + 平铺模式）
- [x] 添加 CSS 常量到 `CssClasses.java`（WATERMARK / WATERMARK_LAYER / WATERMARK_TEXT / WATERMARK_TEXT_GROUP / WATERMARK_IMAGE）
- [x] 添加 LESS 样式到 `theme-base.less`（`.jfx-watermark` 系列选择器）
- [x] 更新 `README_CN.md`（添加组件清单 + 详细使用说明）
- [x] 更新 `PLAN.md`（添加 M8 里程碑记录）

**子阶段 8.2：扩展功能（P2）**
- [x] 支持自定义水印内容（Node 类型，通过 Supplier 工厂方法）
- [x] 支持防删除保护（监听子节点列表变化，自动恢复水印层）
- [x] 添加 Demo 示例（自定义水印节点 + 防删除保护）
- [x] 更新 `README_CN.md`（添加新功能说明和 API 文档）

**核心特性**：
- ✅ 文字水印：支持单行/多行文字
- ✅ 图片水印：支持自定义图片
- ✅ 自定义水印：支持任意 Node 类型（通过工厂方法）
- ✅ 平铺模式：自动平铺填充整个容器
- ✅ 旋转角度：支持自定义旋转角度（默认 -22°）
- ✅ 间距控制：支持自定义水印间距（gapX / gapY）
- ✅ 透明度控制：支持自定义水印透明度（默认 0.15）
- ✅ 字体大小：支持自定义字体大小（默认 16px）
- ✅ 防删除保护：监听 DOM 变化，自动恢复水印层

**设计说明**：
- 使用 StackPane 叠层：底层是内容，顶层是水印层
- 水印层使用 Pane 容器，通过绝对定位平铺水印节点
- 水印节点使用 Rotate 变换实现旋转
- 监听容器尺寸变化，动态生成水印节点
- 所有颜色和样式通过 CSS 控制（`.jfx-watermark`）
- 防删除保护通过 ListChangeListener 监听子节点变化，自动恢复被移除的水印层
- 自定义水印节点通过 Supplier 工厂方法创建，避免节点克隆问题

**影响文件**：
- `component/WatermarkAnt.java`（新建，450+ 行）
- `core/css/CssClasses.java`（添加 5 个常量）
- `css/less/theme-base.less`（添加 `.jfx-watermark` 样式）
- `README_CN.md`（添加组件清单 + 详细说明 + P2 新功能）
- `PLAN.md`（添加 M8 里程碑）
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/MyDemo.java`（添加 5 个示例）

**待完成（可选）**：
- [ ] 支持 Canvas 渲染模式（性能优化，适用于大量水印节点的场景）
- [ ] 支持水印层 z-index 控制（允许用户调整水印层的层级）
- [ ] 支持水印节点缓存（避免重复创建相同的水印节点）

### 🎯 M9 P1 基础设施二期完成（2026-05-21）

**动机**：完成 P1 基础设施二期的所有任务，进一步提升代码复用性和 API 一致性。

**产出**：
1. 创建 Headers 工厂类（统一 title+extra Header 布局）
2. 弃用 Color-based API（AnchorAnt/StatisticAnt）
3. 完全移除 Layouts.java（已废弃）

**关键改动**：

**子阶段 9.1：抽公共 Header 工厂**
- [x] 创建 `Headers.java` 工厂类（`core/util/Headers.java`）
- [x] 实现"左 + 右"布局：左侧标题 + 中间弹性填充 + 右侧操作区
- [x] 使用 Region spacer 实现弹性填充（符合组件组合规范 3.1）
- [x] 重构 CardAnt 和 SurfaceAnt（从 20+ 行减少到 7 行）
- [x] PageAnt 因有 subtitle 特殊需求，暂时保留原实现

**子阶段 9.2：弃用 Color-based API**
- [x] 为 `AnchorAnt.inkColor(Color)` 添加 `@Deprecated` 注解
- [x] 为 `StatisticAnt.valueColor(Color)` 添加 `@Deprecated` 注解
- [x] 添加详细的废弃说明，引导用户改用 styleClass
- [x] 保留向下兼容性（旧代码仍可运行但会有编译警告）

**子阶段 9.3：完全移除 Layouts.java**
- [x] 删除已废弃的 `Layouts.java` 文件
- [x] 更新 JFXiumDemo.java 中唯一的使用处，改用 `VBoxBuilder.create()`
- [x] 移除 JFXiumDemo.java 中的 `import Layouts` 语句
- [x] 编译验证通过（整个项目编译成功）

**影响文件**：
- `core/util/Headers.java`（新建）
- `component/CardAnt.java`（重构）
- `component/SurfaceAnt.java`（重构）
- `component/AnchorAnt.java`（添加 @Deprecated）
- `component/StatisticAnt.java`（添加 @Deprecated）
- `core/layout/Layouts.java`（已删除）
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/JFXiumDemo.java`（更新）

**设计说明**：
- Headers 工厂实现了"左 + 右"布局，符合 Ant Design 规范
- 使用 Region spacer 实现弹性填充，避免 Label Hgrow 陷阱
- Color-based API 保留向下兼容，但引导用户改用 styleClass
- Layouts.java 完全移除，所有引用已迁移到独立 Builder 类

### 🎯 M10 CardAnt 功能补齐（2026-05-21）

**动机**：CardAnt 缺失 Ant Design Card 的多个核心功能（loading / cover / actions / tabList），需要补齐以提升实用性。

**产出**：
1. 重构 CardAnt 结构（清晰分离 Cover / Header / Body / Footer）
2. 新增 loading 状态（骨架屏）
3. 新增 cover 封面图片支持
4. 新增 actions 底部操作按钮
5. 新增 tabList 标签页支持
6. 新增 size / type 样式变体

**关键改动**：

**子阶段 10.1：结构重构**
- [x] 重构 build() 方法，清晰分离 Cover / Header / Body / Footer
- [x] 每个部分独立构建方法（buildCover / buildHeader / buildBody / buildFooter）
- [x] 符合 Ant Design 语义结构

**子阶段 10.2：新增功能**
- [x] **loading** - 加载状态，显示骨架屏
- [x] **cover** - 封面图片支持（支持路径和自定义 Node）
- [x] **actions** - 底部操作按钮列表
- [x] **tabList** - 标签页支持（多标签切换）
- [x] **activeTabKey / defaultActiveTabKey** - 标签页激活状态管理
- [x] **onTabChange** - 标签页切换回调
- [x] **tabBarExtraContent** - 标签栏额外内容
- [x] **size** - 卡片尺寸（MEDIUM / SMALL）
- [x] **type** - 卡片类型（DEFAULT / INNER）

**子阶段 10.3：样式和文档**
- [x] 添加 CSS 常量到 `CssClasses.java`（13 个新常量）
- [x] 添加 LESS 样式到 `theme-base.less`（完整样式定义）
- [x] 更新 `README_CN.md`（添加详细使用说明和 API 文档）
- [x] 编译验证通过

**核心特性**：
- ✅ Cover：封面图片支持（在 header 之前）
- ✅ Header：标题栏 + 标签页导航（可选）
- ✅ Body：内容区（必选，可包含标签页内容）
- ✅ Footer：操作按钮区（可选）
- ✅ Loading：骨架屏加载状态
- ✅ Tabs：多标签页切换支持
- ✅ Size：两种尺寸（medium / small）
- ✅ Type：内嵌卡片样式

**影响文件**：
- `component/CardAnt.java`（重构，新增 9 个功能）
- `core/css/CssClasses.java`（新增 13 个常量）
- `css/less/theme-base.less`（新增 80+ 行样式）
- `README_CN.md`（添加详细说明）

**设计说明**：
- 严格遵循 Ant Design Card 语义结构（Cover + Header + Body + Footer）
- 每个部分独立构建方法，职责单一
- 标签页通过 Label 实现轻量级切换
- 所有颜色和样式通过 CSS 控制
- 骨架屏复用 SkeletonAnt 组件

### 🎯 M11 TableAnt 高级化重构 + LESS 选择器陷阱修复（2026-05-23）

**动机**：`.column(...)` 系列只支持基础取值，列宽/排序/操作列等高级能力都要 build() 后用原生 API 救场，链式 API 断裂。同时 demo 验收时发现 `.align(...)` API 完全没生效。

**产出**：
1. TableAnt 路 B 重构：`.column(...)` 返回 `ColumnBuilder`，强制 `.end()` 回链
2. 新增 `ActionColumnBuilder` 操作列糖（admin 列表页高频场景）
3. 修复 LESS `align-*` 选择器陷阱（隐性 bug，影响所有列对齐）
4. SKILL.md 新增第 14 条"复合选择器 vs 后代选择器"约束

**关键改动**：

**子阶段 11.1：TableAnt 路 B 重构**
- [x] 列级 Builder（`ColumnBuilder<T, V>`）：`width / minWidth / maxWidth / resizable / sortable / sorter / align / visible / end`
- [x] 表级 API：`resizePolicy(CONSTRAINED/UNCONSTRAINED) / sortable / defaultSortBy(title, SortType)`
- [x] `.column / .numberColumn / .booleanColumn / .nodeColumn` 全部返回 `ColumnBuilder`
- [x] nodeColumn 默认 `sortable=false`（Node 不可比较）
- [x] 选择列默认禁用排序和拖宽

**子阶段 11.2：actionColumn 语法糖**
- [x] `.actionColumn(title).action(label, handler).type(...).danger().end()` 链式 API
- [x] 修饰方法（`.type / .danger`）作用于"最后一个 action"
- [x] 内部用 `nodeColumn` + `HBox` 实现，按钮垂直水平居中
- [x] 列宽默认 160，可 `.width(...)` 调整

**子阶段 11.3：LESS 选择器陷阱修复（顺手发现的隐性 bug）**
- [x] 旧：`.table-view .align-left .column-header`（**后代**选择器，**永远不匹配**）
- [x] 新：`.table-view .column-header.align-left`（**复合**选择器，正确语义）
- [x] 同步修 `.label` 子节点对齐、`booleanColumn` 居中、`actionColumn` `Pos.CENTER`
- [x] booleanColumn / 选择列默认挂 `align-center`

**子阶段 11.4：约束沉淀**
- [x] `项目约束与计划/SKILL.md` 第 14 条："复合选择器 vs 后代选择器"
- [x] 末尾"为什么这些很重要"新增第 3 条：复合选择器陷阱（编译/运行都不报错的隐性 bug）

**核心特性**：
- ✅ 列宽：`.width(pref)` / `.width(pref, min, max)` / `.minWidth / .maxWidth`
- ✅ 列排序：`.sortable(boolean)` / `.sorter(Comparator)` / 表级 `.defaultSortBy(...)`
- ✅ 列宽策略：`.resizePolicy(CONSTRAINED / UNCONSTRAINED)`
- ✅ 操作列糖：`actionColumn` 一行写多按钮，自带居中
- ✅ 列对齐：`.align(LEFT/CENTER/RIGHT)`（修复后真正生效）
- ✅ 列可见：`.visible(boolean)`（为后续"列管理"埋点）

**影响文件**：
- `component/TableAnt.java`（重写，~380 行；老代码 ~240 行）
- `css/less/theme-base.less`（修 align-* 选择器 + 新增 booleanColumn 居中）
- `jfxium-demo/.../JFXiumDemo.java`（适配路 B：补 `.end()`）
- `jfxium-demo/.../MyDemo.java`（新增 Table 高级示例：5 列 + actionColumn + UNCONSTRAINED + 默认排序）
- `.kiro/steering/项目约束与计划/SKILL.md`（新增第 14 条约束）

**设计说明**：
- 路 B 选择基于影响面评估：项目内只有 1 处真实调用（JFXiumDemo），破坏式重构成本可控
- ColumnBuilder 与 Builder 分离，避免"魔法"（持有 lastColumn 字段隐式修改）
- ActionColumnBuilder 按"最后一个 action"接受修饰，符合阅读直觉
- LESS bug 修复同时把所有列对齐相关样式串起来一致化（包括 .label 子节点）

**踩坑记录（沉淀进 SKILL）**：
- LESS 选择器组合方式：`.A .B`（后代，带空格）≠ `.A.B`（复合，无空格）
- JavaFX 把 styleClass 直接挂在 column-header / table-cell 节点上，**必须用复合选择器**
- 这类 bug 编译/运行都不报错，只能靠肉眼看截图发现样式没生效——已沉淀为 SKILL 第 14 条强约束

### 🎯 M11.1 TableAnt 表头/分割线/对齐增强（2026-05-23）

**动机**：M12.4 用户列表页验收时发现 4 个真实痛点：表头无上下边距/无分割线、内容贴顶不居中、排序箭头不可见、列宽拖拽点不可见、表头与内容对齐不能拆分。

**产出**：

**子阶段 11.1.1：表头视觉修复**
- [x] 修对表头高度选择器：`.column-header-background -fx-pref-height: 48px`（之前错挂在 `.column-header` 不生效）
- [x] 列分割线：默认显示淡淡的右竖线，让用户找到拖拽点
- [x] 表头 hover 高亮 + 手型光标（提示"可点击排序"）
- [x] 排序箭头显式着色（默认 `-color-fg-muted`，激活时 `-color-accent-emphasis`）
- [x] 列宽拖拽时显示主题色 `.column-resize-line`

**子阶段 11.1.2：双重对齐 API**
- [x] 拆分 `align()` 为 `headerAlign()` + `contentAlign()`
- [x] 老 `.align(...)` 保留兼容，等价"同时设两者"
- [x] LESS 新增 `.align-header-*` 和 `.align-content-*` 6 个 styleClass

**子阶段 11.1.3：内容区分割线 4 种模式**
- [x] 新增 `Border` 枚举：`NONE / HORIZONTAL / VERTICAL / BOTH`
- [x] `.borders(Border)` 替代老 `.bordered(boolean)`（后者标 `@Deprecated`）
- [x] LESS 4 套样式 `.jfx-table-border-{none,h,v,both}`

**子阶段 11.1.4：隐藏表头**
- [x] 新增 `.showHeader(false)` API
- [x] LESS `.jfx-table-no-header` 把 `.column-header-background` 高度设 0

**子阶段 11.1.5：行高保护**
- [x] 撤销错误的 `-fx-cell-size: 54px`（强制裁切高内容）
- [x] 改用 `.table-row-cell -fx-min-height: 48px`（自适应但有下限）

**关键改动**：
- `component/TableAnt.java`：`Border` 枚举 + `borders/showHeader/headerAlign/contentAlign` 4 个新方法
- `core/css/CssClasses.java`：新增 `TABLE_BORDER_*` 4 个 + `TABLE_NO_HEADER` 共 5 个常量
- `css/less/theme-base.less`：~120 行新样式（4 边框模式 + 隐藏表头 + 双重对齐 + 排序箭头 + 列分割线 + 表头 hover）
- `jfxium-demo/.../UserListPage.java`：换用新 API（`.borders(HORIZONTAL)` + 创建时间列双重对齐）

**踩坑实证**：
- ❌ `-fx-min-height` 设在 `.column-header`（错误：仅控制单列文字框，不控整体表头高度）
- ✅ `-fx-pref-height` 设在 `.column-header-background`（外层容器，整行表头）
- ❌ `-fx-cell-size` 强制裁切（导致 actionColumn 按钮变 "..."）
- ✅ `.table-row-cell -fx-min-height` 自适应（容纳高内容，纯文字行也有下限）

**API 兼容性**：老代码 `.bordered(true)` / `.bordered(false)` / `.align(...)` 全部继续可用（`@Deprecated` 注解但保留实现）。

### 🎯 M11.2 TableAnt size 三态 + 表头视觉对齐 AtlantaFX（2026-05-23）

**动机**：M11.1 之后 TablePage 验收时发现：
1. `.compact(true)` 只改了行高，没改表头高度和 padding，半残实现
2. 排序箭头时大时小、位置错乱、消失/重叠等多次回归
3. 表头右上角"色块不协调"、最后一列右分割线问题

直接对照 AtlantaFX 源码（`/Users/openai/workspace/work_open/atlantafx/styles/src/components/_data.scss`）一次性修干净。

**产出**：

**子阶段 11.2.1：Size 三态**
- [x] 新增 `Size` 枚举（SMALL / MIDDLE / LARGE）+ `.size(Size)` API
- [x] LESS 三档样式：表头高 / 行高 / padding / 字号联动
  - SMALL：40 / 36 / 6×8 / 13px
  - MIDDLE（默认）：48 / 48 / 8×12 / 14px
  - LARGE：56 / 56 / 14×16 / 15px
- [x] 老 `.compact(boolean)` 保留兼容，标 `@Deprecated`
- [x] 删除硬编码 `setPrefHeight(300)`（让父容器决定）

**子阶段 11.2.2：表头视觉完全对齐 AtlantaFX**
- [x] **`.column-header` padding=0**，padding 下放到内部 `.label`（修过去贴顶/排序按钮重叠的根因）
- [x] **`.column-header > GridPane`** 是排序区容器（之前不知道这一层）
- [x] **`.arrow` 用 AtlantaFX 标准**：padding `3 4 3 4` + shape `"M 0 0 h 7 l -3.5 4 z"`
- [x] **`.sort-order-dots-container` 保留默认 padding**（之前压成 0 导致重叠）
- [x] **`.column-header-background` 染色 + 去内阴影**（之前的"右上角色块"根因）
- [x] **`.filler / .show-hide-columns-button`** 透明背景 + 底部 1px 边框（AtlantaFX 同款）

**关键改动**：
- `component/TableAnt.java`：新增 `Size` 枚举 + `.size(Size)` 方法
- `core/css/CssClasses.java`：新增 `TABLE_SIZE_SMALL/MIDDLE/LARGE` 常量
- `css/less/theme-base.less`：完全重写表头/箭头/分割线样式，对齐 AtlantaFX
- `jfxium-demo/.../showcase/pages/TablePage.java`：新增 sectionSizes 三档对比展示

**踩坑实证（5 次）**：
1. ❌ `-fx-min-height` 设在 `.column-header`（不影响整体表头高度）
2. ❌ `-fx-cell-size` 强制裁切高内容（actionColumn 按钮变 "..."）
3. ❌ `.arrow` 自定义 padding=0 + 自定义 shape（导致箭头消失或重叠）
4. ❌ `.sort-order-dots-container` 压成 0（导致箭头与文字重叠）
5. ❌ `.column-header` 自身设 padding（导致排序区无法正确布局）

**正解（AtlantaFX 同款）**：
- ✅ `.column-header-background -fx-pref-height: 48px` 控整体表头高度
- ✅ `.table-row-cell -fx-min-height: 48px` 弹性行高
- ✅ `.arrow padding 3×4` + shape `"M 0 0 h 7 l -3.5 4 z"` 是 modena 默认值
- ✅ `.sort-order-dots-container padding: 2px 0` 保留
- ✅ `.column-header padding: 0`，padding 下放到 `.label` 和 `> GridPane`

**已知不可完美解决**：JavaFX 表头右上角 `show-hide-columns-button` 区域 + 滚动条交叉点，连 AtlantaFX 也只能做到"染色 + 加底线"——这是 JavaFX TableView 的固有限制，不是 LESS 能完美修的。

### 🎯 M14 ShowcaseDemo 控件展示骨架（2026-05-23）

**动机**：原 P2.1 Web Admin demo 做到 M12.4 后，发现"业务 demo 工程量大、复用价值低"，转向"控件 Showcase Demo"——每个组件一页，源码即文档。

**产出**：
- [x] 删除老 demo：`JFXiumDemo.java` / `MyDemo.java`
- [x] 新增 `showcase/` 包：4 个骨架文件（ShowcaseDemo / ShowcaseFrame / ShowcasePage / ShowcaseSection）
- [x] 新增 `pages/TablePage.java`：M11 + M11.1 + M11.2 全功能展示，10 个 Section
- [x] 切换 mainClass 到 `ShowcaseDemo`
- [x] AtlantaFX 源码作为"复杂 JavaFX 控件 LESS 标准答案"参考路径

**Showcase 设计要点**：
- 左侧菜单按 Ant Design 7 类分组（通用/布局/导航/数据录入/数据展示/反馈/其他）
- 每个 Section = CardAnt + 标题 + 描述 + 演示节点 + 折叠代码块（CodeBlockAnt）
- 切换组件 = 替换 ScrollPane content（无路由参数，简单直接）
- 沿用 AdminShell 的 IconAnt SVG path 体系

**保留的 admin demo**：作为业务模板参考实现保留（M12.1-M12.4），后续 P2.2 模板沉淀的参考。

### 🎯 M15 MenuAnt 增强：4 模式正交（2026-05-23）

**动机**：原 MenuAnt 只支持 INLINE 单模式，缺横向导航 / 选中态 / 暗色 / 折叠四种 admin 高频场景。一次性补齐，对齐 Ant Design Menu 全功能。

**产出**：4 个正交特性（任意组合），`Pane build()` 替代 `VBox build()` 不再撒谎。

**子阶段 15.1：横向模式（Mode）**
- [x] 新增 `Mode` 枚举：INLINE（默认）/ HORIZONTAL
- [x] HORIZONTAL 模式 build() 返回 `HBox`，顶级横排，subMenu Popup 下拉
- [x] `MenuItem` 拆出 `buildInline()` 和 `buildHorizontal()` 两套渲染
- [x] `Builder.build()` 返回类型从 `VBox` 改为 `Pane`（不再撒谎）

**子阶段 15.2：选中态（selectedKey）**
- [x] `MenuItem.key` 字段 + 所有 `item / subMenu` 加带 key 的重载
- [x] `Builder.selectedKey(String)` + `onSelect(Consumer<String>)` API
- [x] LESS 选中态视觉：
  - INLINE：背景 `-color-accent-subtle` + 左侧 3px 主题色竖线 + 文字主题色加粗
  - HORIZONTAL：底部 3px 主题色横线 + 文字主题色加粗
- [x] `BuildContext` 类传递全菜单共享状态（mode/theme/collapsed/selectedKey/onSelect），解耦内部类

**子阶段 15.3：暗色主题（Theme.DARK）**
- [x] 新增 `Theme` 枚举：LIGHT（默认）/ DARK
- [x] LESS `.menu-dark` 样式：
  - 背景 `#001529`（Ant Design 默认侧栏深色）
  - 文字 `rgba(255,255,255,0.85)`
  - hover `rgba(255,255,255,0.08)`
  - 选中态主题色填充
  - subMenu body 更深一档 `#000c17`
  - `.jfx-icon-path` 自动变白色（无需用户改 styleClass）

**子阶段 15.4：折叠模式（collapsed）**
- [x] `Builder.collapsed(boolean)` API（仅 INLINE 有效，HORIZONTAL 自动忽略）
- [x] LESS `.menu-collapsed` 样式：宽度 64px、padding=0
- [x] `MenuItem.buildInline` 在 collapsed 下隐藏文字、改居中对齐
- [x] `SubMenuBuilder` 折叠时改用 `buildInlineCollapsed`：subMenu 改 Popup 从右侧弹出
- [x] `MenuGroup` / `MenuDivider` 在 collapsed 下隐藏标题/收窄
- [x] 与 DARK 主题正交（可叠加 `.collapsed(true).theme(DARK)` = Ant Pro 经典样式）

**关键改动**：
- `component/MenuAnt.java`：完整重写，~370 行 → ~470 行，加 4 个枚举/API
- `core/css/CssClasses.java`：新增 `MENU_DARK / MENU_COLLAPSED / MENU_INLINE / MENU_HORIZONTAL / MENU_ITEM_SELECTED / MENU_SUBMENU_ARROW_BOX` 6 个常量
- `css/less/theme-base.less`：新增 ~80 行（INLINE/HORIZONTAL/DARK/COLLAPSED/选中态）
- `jfxium-demo/.../showcase/pages/MenuPage.java`：新建，7 个 Section 全功能展示

**老调用方迁移**：
- `MenuAnt.create().build()` 返回类型从 `VBox` 改为 `Pane`
- `AdminShell` / `ShowcaseFrame` 加 `(VBox)` cast 兼容（仅这两处）

**API 兼容性**：
- 老 `.item(text, onClick)` / `.item(text, icon, onClick)` 全部继续可用
- `.subMenu(text)` / `.subMenu(text, icon)` 继续可用
- 新加 key 的重载是新增方法，不破坏老 API

**设计要点**：
- **4 个特性正交**：Mode / Theme / Collapsed / SelectedKey 任意组合
- **collapsed 仅 INLINE 有效**：HORIZONTAL 没"折叠"语义，build() 自动 `effectiveCollapsed = false`
- **BuildContext 模式**：全菜单共享状态打包，避免 MenuItem 持有 Builder 引用
- **subMenu 双 Popup 来源**：HORIZONTAL 从下方 / collapsed 从右侧，复用同一套 Popup 逻辑

### 🎯 M16 Showcase 5 个组件页 + WatermarkAnt 重写（2026-05-23）

**动机**：M14 ShowcaseDemo 骨架完成后只有 1 个 Table 页，需要快速覆盖更多组件。同时 WatermarkAnt 老实现性能差（80+ 节点 + 反复重建），需要按 Element Plus 标准做法重写。

**子阶段 16.1：4 个 Showcase 页快速覆盖**
- [x] **CardPage**（9 Section）：M10 9 个新功能全展示（基础/4阴影/2尺寸/2类型/Hoverable/Actions/Tabs/Loading/Cover）
- [x] **ButtonPage**（8 Section）：10 type / 3 size / 2 shape / disabled / ghost / block / icon / loading
- [x] **WatermarkPage**（5 Section）：单行/多行文字 / 自定义样式 / 自定义 Node / 防删除保护
- [x] **MenuPage**（7 Section，M15 同步做）：INLINE/HORIZONTAL × LIGHT/DARK × 折叠 × 选中态全组合

**子阶段 16.2：admin demo 接 selectedKey**
- [x] AdminShell sider 给 menu item 加 key
- [x] `router.onChange()` 触发 `rebuildSider()` 让选中态跟随当前路由
- [x] 验证 admin demo 实战体验，确认 M15 API 好用

**子阶段 16.3：WatermarkAnt 重写（性能 + 视觉双优化）**

**触发原因**：WatermarkPage 验收时发现 5 个 Section 都不渲染水印——根因是 `getBoundsInParent()` 在节点未入场景图时返回 0，而 ScrollPane 嵌套场景下时序更复杂。

**根本改造**：放弃"循环创建 80+ 节点"的方案，对齐 Element Plus useClips.ts 的 Canvas snapshot + BackgroundImage 平铺方案。

**实现对照**：

| 步骤 | 我们的实现 | Element Plus useClips.ts |
|---|---|---|
| 测文字尺寸 | `Text.getLayoutBounds()` | `ctx.measureText()` |
| 画 contentCanvas | `gc.fillText()` 多行 | `ctx.fillText()` 多行 |
| 旋转到 rCanvas | `gc.rotate(rotate) + drawImage` | `rCtx.rotate(angle) + drawImage` |
| 算紧凑边界 | 4 角点 × 旋转矩阵 min/max | 同 |
| 错位 3 次 | `drawClip()` × 3（中/上/下） | `drawImg()` × 3 |
| 输出 | `Canvas.snapshot()` → WritableImage | `toDataURL()` |
| 平铺 | `BackgroundImage(REPEAT, REPEAT)` | `background-image + repeat` |

**性能对比**：

| 项 | 老实现 | M16.3 新实现 |
|---|---|---|
| 节点数 | 80+ Label/HBox | **1 Region + 1 Image** |
| 滚动/缩放 | clear + 重建 80+ 节点 | **零开销**（CSS 自动平铺） |
| 错位 | 网格对齐 | 对角错位 |
| DPR | 不处理 | `Screen.getOutputScaleX()` 适配 |
| 代码 | ~470 行 | ~360 行 |

**新增 API**：
- `.color(Color)` —— 文字颜色（Element Plus 的 `font.color` 对应）
- `.fontFamily(String)` —— 字体族
- `.fontWeight(FontWeight)` —— 字重
- `.fontGap(double)` —— 多行文字行间距（默认 3）

**关键改动**：
- `component/WatermarkAnt.java`：完整重写（~470 → ~360 行）
- `core/css/CssClasses.java`：保留旧常量（向下兼容）
- `jfxium-demo/.../showcase/pages/`：新增 ButtonPage / CardPage / WatermarkPage（MenuPage 在 M15 已加）

**API 兼容性**：所有原 API（content / text / image / customNode / preventRemoval / rotate / opacity / fontSize / gapX / gapY / imageWidth / imageHeight）100% 保留。

**踩坑实证**：
- ❌ 老 WatermarkAnt 用 `getBoundsInParent()` 测量节点尺寸：节点未入场景图时返回 0，导致整个水印不渲染（ScrollPane 嵌套场景必发）
- ✅ 正解：用 `Text.getLayoutBounds()` 不依赖场景图，可立即测量；自定义 Node 用 `snapshot()` 后从 Image 拿尺寸
- ⚠️ Canvas snapshot 时要 `SnapshotParameters.setFill(Color.TRANSPARENT)`，否则默认白底会污染水印

**对齐设计哲学**：M15 SKILL 核心原则提到的"吸取 Ant + Element Plus + AtlantaFX 三家优点"——M16 是第一次明确按 Element Plus 源码实现的组件。

### 🎯 M17 Showcase 5 个新页（admin 高频四大件 + Switch）（2026-05-23）

**动机**：M16 完成 5 页后，admin 高频组件还差 Input/Switch/Tag/Modal/Drawer 五个，一次性补齐覆盖 admin 全栈。

**产出**：

**Showcase 新增 5 页 / 24 Section**：
- [x] **SwitchPage**（4 Section）：基础 / 文字标签 / 启用禁用 / 状态变化回调
- [x] **TagPage**（5 Section）：6 type / 3 size / 3 shape / 边框开关 / 可关闭
- [x] **InputPage**（5 Section）：基础 / 3 size / 状态 / 配 Label 表单 / 竖向表单组合
- [x] **ModalPage**（5 Section）：基础 / 确认对话框 / 关闭回调 / 禁止遮罩 / 自定义 Footer
- [x] **DrawerPage**（5 Section）：4 方向 / 尺寸 / 表单 Drawer / Extra 标题区 / 禁止遮罩

**ShowcaseDemo 总览**：
```
通用：     Button(8) + Switch(4)
导航：     Menu(7)
布局：     Card(9)
数据录入： Input(5)
数据展示： Table(10) + Tag(5)
反馈：     Modal(5) + Drawer(5)
其他：     Watermark(5)
=========================================
共 10 页 / 63 Section，覆盖 Ant Design 7 大类
```

**踩坑实证（API 不一致）**：
- ❌ 写 ModalPage / DrawerPage 时凭印象 `ModalAnt.create().xxx().open(node)`，编译失败
- ✅ 实际 API：`build()` 返回 `ModalResult` / `DrawerResult`，`open()` 在 Result 上
- 📝 与 Button/Card/Table 等 `build()` 直接返回最终节点的模式**不一致**

**API 不一致归类**：
| 类型 | 代表 | `build()` 返回 |
|---|---|---|
| 直接节点型 | Button / Input / Card / Table / Watermark | 直接可用的 Node |
| Result 包装型 | Modal / Drawer / Dropdown | Result 对象，需要 `.open()` 触发 |

**关键改动**：
- `jfxium-demo/.../showcase/pages/`：新增 5 个页
- `jfxium-demo/.../showcase/ShowcaseDemo.java`：注册 5 个新页

**新增 SKILL 候选条目**：建议补 SKILL 第 16 条"返回类型契约：直接节点型 vs Result 包装型"，但本轮先不动 SKILL，等下次 API 不一致再次踩坑时再沉淀。

### 🎯 M18 包结构归约 + CrudTemplate 业务模板沉淀（2026-05-24）

**动机**：M12.4 沉淀了 FilterBarAnt 业务级组件，做完 ShowcaseDemo 后用户提出真实痛点——

> "card 是高频组件，要功能丰富，类似 BorderPane 顶部+中部+底部，可包容 TableView 模板，顶部塞删改，中部表格，底部分页"

进一步讨论发现两个深层问题：
1. CardAnt 是装饰容器（带边框/阴影/cover/actions），扩展为"业务页骨架"会污染语义；
2. 老 PageAnt 是早期产物，命名含糊，subtitle/actions/content/children API 拼凑式，**已被 CrudTemplate 完全覆盖**；
3. 整个 `jfxium/component/` 包名"组件"过度泛化，把"原子控件"和"业务模板"塞在一起，规模化后命名空间紊乱。

**讨论决策**（按用户选择 A/B 顺序）：
- **B 方案**：新增独立组件而非扩展 CardAnt
- **A 方案**：删除老 PageAnt（推翻重来）
- **A 方案**：新组件命名 `CrudTemplate`（不带 Ant 后缀，明确"业务模板"语义）
- **A 方案**：放在新建 `jfxium/template/` 包下（与 `component/` 严格区分）

**产出**：

**子阶段 18.1：删除老 PageAnt**
- [x] 删除 `component/PageAnt.java`
- [x] 删除 CssClasses 中 5 个 `PAGE_*` 常量（PAGE / PAGE_HEADER / PAGE_TITLE / PAGE_SUBTITLE / PAGE_ACTIONS）
- [x] 删除 `theme-base.less` 中 `.page / .page-header / .page-title / .page-subtitle / .page-actions` 整段
- [x] 编译产物 8 套主题 CSS 全部重新生成（`mvn install -pl jfxium`）

**子阶段 18.2：新建 CrudTemplate**
- [x] 新建 `jfxium/template/` 包（项目内首个非 component 子包）
- [x] 实现 `template/CrudTemplate.java`（~270 行 + AbstractStyleBuilder 继承）
- [x] CssClasses 新增 5 个常量：`CRUD_TEMPLATE / _TITLE / _TOPBAR / _BODY / _BOTTOMBAR`
- [x] LESS 新增 `.crud-template` 系列样式（默认无边框 + .bordered 修饰类支持）
- [x] 装饰能力：bordered / topbarSpacing / bottombarSpacing / sectionGap / padding（继承自 AbstractStyleBuilder）

**子阶段 18.3：API 设计要点**
- [x] 底层 BorderPane（top/center/bottom）：顶/底自适应内容高度，center 撑满
- [x] topbar / bottombar 各为 HBox 三段式：`[ left... ] spacer [ right... ]`（用独立 Region 做 spacer，符合组件组合规范 3.1）
- [x] title 可选：null 不渲染，topbar 顶到顶部
- [x] 任一边没节点不渲染该侧（避免空 HBox 占位）
- [x] `build()` 诚实返回 `BorderPane`（不撒谎）

**子阶段 18.4：CrudTemplatePage 验收**
- [x] 新建 `showcase/pages/CrudTemplatePage.java`（4 Section）
  - Section 1：经典 CRUD 列表页（搜索 + 筛选 + 新增 + 表格 + 分页）
  - Section 2：表单页（仅 body + 底部右侧提交按钮）
  - Section 3：仪表盘（顶部刷新/导出 + 4 列统计卡矩阵）
  - Section 4：极简（只有 title + body）
- [x] ShowcaseDemo 注册 CrudTemplatePage（归类到"布局"分类）

**关键改动**：
- `jfxium/template/CrudTemplate.java`（新建，~270 行）
- `core/css/CssClasses.java`（删 5 个 PAGE_* 加 5 个 CRUD_TEMPLATE_*）
- `css/less/theme-base.less`（删 .page-* 系列加 .crud-template 系列）
- `component/PageAnt.java`（删除）
- `jfxium-demo/.../showcase/pages/CrudTemplatePage.java`（新建）
- `jfxium-demo/.../showcase/ShowcaseDemo.java`（注册）
- 文档同步：`README_CN.md` / `README.md` / `docs/COMPONENTS.md` / `docs/LAYOUT.md` 中 PageAnt 引用替换为 CrudTemplate（保留迁移说明）

**API 兼容性破坏**：
- ❌ `PageAnt.create()` 不存在 → 用 `CrudTemplate.create()`
- ❌ `.subtitle(...)` API 已删除 → CrudTemplate 不支持副标题（admin 后台实际用得很少）
- ❌ `.actions(...)` → `.topRight(...)`
- ❌ `.content(...)` → `.body(...)`
- 项目内仅 `docs/BUILDER_API_AUDIT.md` 残留 1 处（审计文档，已是历史快照，不修）

**设计哲学（落档）**：

**包分类规范（P3 SKILL 候选条目"组件分类归约"）**：

| 包路径 | 后缀 | 定位 | 示例 |
|---|---|---|---|
| `jfxium/component/` | `*Ant` | 原子控件 + 装饰容器 + 浮层（粒度小，可独立使用） | ButtonAnt / CardAnt / DrawerAnt / FilterBarAnt |
| `jfxium/template/` | `*Template` | **业务模板**（粒度大，针对业务场景的整页骨架） | **CrudTemplate** |
| `jfxium/layout/` | `*Layout`（预留） | 应用骨架（粒度最大，跨页面架构） | AppShell / 未来的 SidebarLayout 等 |

**为什么这个分类重要**：
1. 命名后缀（Ant / Template / Layout）让用户**一眼看出粒度**
2. 业务模板天然耦合多个原子控件，**不能塞回 component/ 让用户误以为它是原子控件**
3. CrudTemplate 是 template/ 首个成员，未来 LoginTemplate / DetailTemplate / DashboardTemplate 都进这里
4. 与组件组合规范 SKILL 第二章"微组件清单"打通：原子（component/）→ 业务模板（template/）→ 应用骨架（layout/）三层依赖单向递增

**下次写新组件前问自己**：
- 这玩意儿是"原子控件 / 装饰容器 / 浮层"？→ `component/`，加 `*Ant` 后缀
- 这玩意儿是"针对业务场景的整页骨架"？→ `template/`，加 `*Template` 后缀
- 这玩意儿是"应用级跨页面架构"？→ `layout/`，加 `*Layout` 后缀（暂未启用）

**踩坑实证**：
- ❌ 早期 PageAnt 命名歧义：PaginationAnt / PageHeader 等都可能被误读为相关
- ❌ 早期把 FilterBarAnt 放 component/：实际上 FilterBarAnt 已经是"工具栏业务模板"，应该放 template/——这是 M18 之后**待迁移**的历史债（暂不动，避免连带打破调用方）
- ✅ CrudTemplate 直接进 template/，避免重蹈覆辙

**API 一致性**（提醒）：CrudTemplate `build()` 返回直接节点（BorderPane），不是 Result 包装型——延续 ButtonAnt / TableAnt 的"直接节点型"契约，与 ModalAnt / DrawerAnt 的"Result 包装型"做语义区分。

### 🎯 M19 BarAnt 三段式横向布局沉淀（2026-05-24）

**动机**：M18 落地 CrudTemplate 后，用户提出真实痛点——

> "有那个 hbox 分为左中右布局的控件没有？这也是一个高频使用，可以用在模态窗口、card 顶部使用，有很多场景的"

回查项目里这个模式的覆盖情况：

| 现状 | 模型 | 问题 |
|---|---|---|
| `ActionBarAnt` | 顺序 add + 手动 spacer | 二段勉强能用，三段写不出，spacer 顺序难记 |
| `Headers` 工厂 | title + spacer + extra | 仅二段，名字限定 Header 场景 |
| `CrudTemplate.buildBar(...)` | 三段（私有方法） | 已识别但写死给自己用，未对外开放 |
| 组件组合规范 SKILL 3.1 | 标准模式已识别 | **要求用户手写**，未抽成组件 |

**结论**：是个"已识别为高频模式、却没抽出可复用组件"的真空区。SKILL 已经把它定为标准 pattern，CrudTemplate 已复刻了一遍，到了正式抽出来的时机。

**讨论决策**（按用户选择）：
- **A 方案**：新建独立组件 `BarAnt`（推荐方案）
- center 用 **多节点 Node...**，与 CrudTemplate 的 topLeft/topRight 一致
- center **真正居中**（左 + spacer + 中 + spacer + 右），受挤压时自然偏移——flex 标准行为

**产出**：

**子阶段 19.1：核心组件实现**
- [x] 新建 `component/BarAnt.java`（~180 行 + AbstractStyleBuilder 继承）
- [x] API：`.left(Node...)` / `.center(Node...)` / `.right(Node...)` / `.gap(double)` / `.alignment(Pos)`
- [x] **center 自动退化**：不调用或传空数组 → 二段（左 + spacer + 右）；非空 → 三段（左 + spacer + 中 + spacer + 右）
- [x] 每段允许多节点累加（多次调用 `.left(a).left(b)` 累加，不覆盖）
- [x] `build()` 诚实返回 `HBox`（不撒谎）
- [x] CssClasses 新增 2 个常量：`SPLIT_BAR / SPLIT_BAR_SPACER`
- [x] LESS 新增 `.split-bar / .split-bar-spacer`（自身仅承担布局，无视觉装饰，spacer 透明）

**子阶段 19.2：消除内部重复（精准动刀）**
- [x] CrudTemplate 内部私有 `buildBar(left, right, spacing)` 方法**改为复用 BarAnt**
- [x] 删除 CrudTemplate 中 import `Priority` / `Region`（不再直接拼 spacer）
- [x] CrudTemplate 公开 API 完全不变，调用方零感知

**子阶段 19.3：SplitBarPage 验收**
- [x] 新建 `showcase/pages/SplitBarPage.java`（5 Section）
  - Section 1：三段式（左+中+右）—— 页面/Modal 顶部栏标题居中
  - Section 2：二段式（不传 center 自动退化）—— 等价 ActionBar 但语义清晰
  - Section 3：每段多节点 —— 图标+标题+Tag / Tab 组 / 图标按钮组
  - Section 4：Modal Header + Footer 拼装 —— 一个 Modal 用 2 个 SplitBar
  - Section 5：App Header —— Logo + 全局搜索框 + 消息+用户
- [x] ShowcaseDemo 注册 SplitBarPage（归类到"布局"）

**子阶段 19.4：彻底删除 ActionBarAnt + Headers（一次性清干净）**
- [x] 全项目 grep 调用方：CardAnt / SurfaceAnt 各 1 处使用 `Headers.create()`，ActionBarAnt 在源码与 demo 内**零调用**
- [x] CardAnt.buildHeader：`Headers.create()...build()` → `BarAnt.create().left(titleLabel).right(extra).build()`
- [x] SurfaceAnt.build：`Headers.create()...build()` → `BarAnt.create().left(titleLabel).right(extra).build()`，header styleClass 在 build 后挂回
- [x] 删除 `component/ActionBarAnt.java`
- [x] 删除 `core/util/Headers.java`
- [x] 删除 CssClasses 中 `ACTION_BAR / ACTION_BAR_SPACER` 2 个常量
- [x] 删除 `theme-base.less` 中 `.action-bar { ... }` 选择器（spacer 选择器从未单独使用，一并清掉）
- [x] 8 套主题 CSS 重新生成，确认无残留
- [x] 文档同步：README_CN.md / README.md / docs/COMPONENTS.md / docs/LAYOUT.md / PLAN.md（"二、Builder 命名约定"示例）所有 ActionBarAnt 调用替换为 BarAnt

**子阶段 19.5：input-base 家族高度对齐 + Size API 补齐（CrudTemplate 验收触发）**

**触发现场**：CrudTemplatePage 列表页验收时用户发现"管理员/启用"两个 ComboBox 比 Input/Button 矮一截，明显不齐。

**根因分析**：
1. **默认 padding bug**：`theme-base.less` 中 `.combo-box / .date-picker / .color-picker` 三个选择器 `.input-base()` mixin 之后又 override `-fx-padding: @spacing-xs(4px) @input-padding-x` —— 把 padding-y 从默认 8px 砍到 4px，导致比 `.text-field` 矮 8px。**对照 AtlantaFX `_combo-box.scss`**（line 95/134）发现 ComboBox/ColorPicker/DatePicker 应该用**完整 `padding-y padding-x`**，与 text-field 完全一致——验证为 bug。
2. **Java 端 Size API 缺失**：`ComboBoxAnt / DatePickerAnt / ColorPickerAnt` 三个 Builder 都没有 `Size` 枚举和 `.size()` 方法。但 LESS 端 `.combo-box.small / .choice-box.small` 选择器**早就写好**——这是死代码，用户永远拿不到 small/large 形态。
3. **Size LESS 规则不统一**：现存 `.combo-box.small` 用 `@spacing-xs / @spacing-sm`，与 `.text-field.small` 用 `@input-padding-y-sm / @input-padding-y-lg` 不一致——同一行 Input + ComboBox 设 SMALL 后还是不齐。

**修复**：

- [x] **默认 padding 修对**：删除 `.combo-box / .date-picker / .color-picker` 三处错误的 `-fx-padding: @spacing-xs @input-padding-x`，让它们继承 `.input-base()` 默认 `@input-padding-y @input-padding-x`（8px / 12px）
- [x] **Size LESS 规则统一**：4 个组件全部改用 `@input-padding-y-sm/lg @input-padding-x-sm/lg + @font-size-sm/lg`，与 `.text-field.small / .large` 完全一致
  - `.combo-box.small / .large`（修正）
  - `.choice-box.small / .large`（修正）
  - `.date-picker.small / .large`（**新增**，原本没有）
  - `.color-picker.small / .large`（**新增**，原本没有）
- [x] **Java 端 Size API 补齐**：
  - `ComboBoxAnt`：新增 `Size` 枚举 + `.size(Size)` 方法（复用 `CssClasses.SIZE_SMALL/LARGE` 通用常量）
  - `DatePickerAnt`：同上
  - `ColorPickerAnt`：同上
- [x] **ChoiceBoxAnt** 不存在 Java 类（仅 LESS 选择器），无需补 API

**验收**：CrudTemplatePage 中 search/role/status 三个组件高度完全一致；`.size(Size.SMALL/LARGE)` 三档高度联动正常。

**关键改动**：
- `component/ComboBoxAnt.java`（加 `Size` enum + `.size()` 方法 + build 时挂 styleClass）
- `component/DatePickerAnt.java`（同上）
- `component/ColorPickerAnt.java`（同上）
- `css/less/theme-base.less`（删 3 处错误 padding 覆盖；统一 4 个组件 Size 规则；新增 date-picker / color-picker `.small/.large`）

**踩坑实证**：
- ❌ "微调"心态在底层 mixin 之后 override padding，只盯着自己组件看显得"挺好"，但与同家族其他组件并排就破功——SKILL 5.2 状态联动原则（"相同状态的组件必须表现完全一致"）就是为防这个
- ✅ 写 LESS 默认值时，**优先继承 mixin**，需要 override 时**必须对照同家族其他成员**确认值能对齐
- ✅ AtlantaFX 又一次成为 JavaFX CSS 标准答案——遇到"我们的 ComboBox/DatePicker padding 选什么"时直接扒 AtlantaFX `_combo-box.scss`（M11.2 已经验证过这条经验，M19.5 再次印证）
- ❌ 早期"LESS 写好但 Java 不挂 styleClass" → 用户永远拿不到那个形态。验证规则：**新增 LESS 选择器时同步检查 Java 端是否真的挂得上**（不然就是死代码）

### 🎯 M19.6 按钮族补全：Toggle / Menu / Split + Radio/CheckBox 配套（2026-05-24）

**动机**：用户参照"按钮类组件 + 下拉与列表选择类"清单全量体检，发现项目缺 3 个 JavaFX 标准按钮族成员。

**审计结论**：

| 你列的需求 | 我们的实现 | 状态 |
|---|---|---|
| 普通按钮 | ButtonAnt（10 type / 3 size / 2 shape / icon / loading） | ✅ |
| 切换按钮（ToggleButton） | **缺 ToggleButtonAnt** | ❌ |
| 单选按钮（RadioButton） | RadioButtonAnt 有，但缺 Size API | ⚠️ |
| 复选框（CheckBox） | CheckBoxAnt 有 indeterminate，缺 Size + allowIndeterminate | ⚠️ |
| 菜单按钮（MenuButton） | **缺 MenuButtonAnt**（DropdownAnt 是 trigger+Popup，结构不同） | ❌ |
| 分割按钮（SplitMenuButton） | **缺 SplitButtonAnt** | ❌ |
| 下拉框（ComboBox） | ComboBoxAnt（M19.5 加了 Size） | ✅ |
| 选择框（ChoiceBox） | **缺 ChoiceBoxAnt**（LESS 早写好，Java 端没人挂） | ⚠️ |

**产出**：

**子阶段 19.6.1：3 个新按钮族组件**
- [x] **ToggleButtonAnt**：包装 JavaFX ToggleButton，API 镜像 ButtonAnt（size / shape / icon / disabled / toggleGroup / onChange / onAction）
- [x] **MenuButtonAnt**：包装 JavaFX MenuButton，链式 `.item / .itemDisabled / .separator / .add` 添加菜单项 + size / shape / icon
- [x] **SplitButtonAnt**：包装 JavaFX SplitMenuButton，多了 `.onClick`（主按钮）API
- 三者 LESS 视觉走 `.toggle-button / .menu-button / .split-menu-button` 系列（已有完整规则；新增三档 size 规则；新增独立 `.toggle-button` 通用视觉块）

**子阶段 19.6.2：Radio / CheckBox 配套补齐**
- [x] **RadioButtonAnt**：新增 `Size` 枚举 + `.size(Size)` 方法（与 ButtonAnt/InputAnt 一致）
- [x] **CheckBoxAnt**：
  - 新增 `Size` 枚举 + `.size(Size)`
  - 新增 `.allowIndeterminate(boolean)` 方法（让用户能在 selected ↔ indeterminate ↔ unselected 三态间循环点击，对应 Ant Design Checkbox 三态语义）

**子阶段 19.6.3：ChoiceBoxAnt 历史债处理**
- 决策：**不补 Java 类**。理由：
  - ChoiceBox 是 ComboBox 的"轻量简化版"，能力被 ComboBox 完全覆盖
  - 项目内零调用方
  - 但 LESS 端 `.choice-box / .choice-box.small / .large` 等规则**保留**——它们对裸 `new ChoiceBox()` 仍生效，删掉是"过度极简"
- 已记账：用户若需要轻量下拉直接用 ComboBoxAnt（语义无差别）

**子阶段 19.6.4：ButtonGroupPage 验收**
- [x] 新建 `showcase/pages/ButtonGroupPage.java`（7 Section）
  - Section 1：ToggleButton 独立切换（加粗/斜体/下划线）
  - Section 2：ToggleButton 互斥组（ToggleGroup）—— admin 视图切换
  - Section 3：ToggleButton 三档尺寸
  - Section 4：MenuButton 基础（"批量操作"+ 子项 + 分隔线 + 禁用项）
  - Section 5：MenuButton 三档尺寸
  - Section 6：SplitButton 基础（"保存 / 保存并新建 / 保存并退出"）
  - Section 7：SplitButton 三档尺寸
- [x] ShowcaseDemo 注册 ButtonGroupPage（归类到"通用"）

**关键改动**：
- `component/ToggleButtonAnt.java`（新建）
- `component/MenuButtonAnt.java`（新建）
- `component/SplitButtonAnt.java`（新建）
- `component/RadioButtonAnt.java`（加 Size enum + .size()）
- `component/CheckBoxAnt.java`（加 Size enum + .size() + allowIndeterminate(boolean)）
- `css/less/theme-base.less`：
  - 新增独立 `.toggle-button` 通用块（hover / armed / selected / disabled）
  - 新增 `.toggle-button.small/.large / .menu-button.small/.large / .split-menu-button.small/.large` 三档 size 规则
  - 注：现有 `.pagination .pagination-control .toggle-button` 和 `.toggle-button.switch` 因选择器更具体（后代/复合）不受影响
- `jfxium-demo/.../showcase/pages/ButtonGroupPage.java`（新建，7 Section）
- `jfxium-demo/.../showcase/ShowcaseDemo.java`（注册）
- `README_CN.md`（组件清单加 3 行）

**API 一致性结果**：

| 组件 | Size API | 与 ButtonAnt 一致 |
|---|---|---|
| ButtonAnt | ✅ | — |
| InputAnt | ✅ | ✅ |
| ComboBoxAnt | ✅（M19.5）| ✅ |
| DatePickerAnt | ✅（M19.5）| ✅ |
| ColorPickerAnt | ✅（M19.5）| ✅ |
| **RadioButtonAnt** | ✅（M19.6）| ✅ |
| **CheckBoxAnt** | ✅（M19.6）| ✅ |
| **ToggleButtonAnt** | ✅（M19.6 新建即带）| ✅ |
| **MenuButtonAnt** | ✅（M19.6 新建即带）| ✅ |
| **SplitButtonAnt** | ✅（M19.6 新建即带）| ✅ |

**踩坑提示**：
- 新增独立 `.toggle-button` 通用块时一度担心污染 SwitchAnt（`.toggle-button.switch`）和 PaginationAnt（`.pagination .pagination-control .toggle-button`）—— 实际安全：CSS 选择器特异性规则（compound > generic、descendant > generic）保证后两者优先级更高
- ChoiceBox 不补 Java 类反而是"精准动刀"——它的能力 100% 被 ComboBox 覆盖，加一个就是为重复造轮子

### 🎯 M19.6.1 MenuButton/SplitButton 视觉打磨：箭头三件套（2026-05-24）

**触发现场**：M19.6 上线 ButtonGroupPage 后用户连续指出 3 个视觉问题。

**问题 1：箭头不显示（▼ 字符 fallback）**

- 现象：MenuButton 渲染成 "批量操作▼"——文字带 Unicode 箭头字符，不是图形 arrow
- 根因：modena 默认对 `.combo-box .arrow` 设了 shape，但 **MenuButton/SplitMenuButton 的 `.arrow` 没有默认 shape**。我们的 LESS 只覆盖 `-fx-background-color`，shape 缺失导致 `.arrow` 节点 0 尺寸不可见，JavaFX fallback 到文字层渲染了 ▼ 字符
- 修复：显式设 `-fx-shape` + `min/pref-width/height`（参考 AtlantaFX `_icons.scss`）
- **沉淀经验**：JavaFX 内部控件的 `.arrow` 节点遇到默认 shape 不存在的情况，必须显式设 shape + 尺寸；只设颜色会变成"有色无形"

**问题 2：箭头位置错乱（左贴文字、右大空白）**

- 现象：场景 4 截图显示 "批量操作▼" 后右侧大段空白；MenuButton 整体 padding 与子节点 padding 双层叠加
- 根因：原写法 `.menu-button { .button-base(); }` 让容器自己拿了 16px padding，**同时** `.label` 和 `.arrow-button` 又各自带 padding——modena 默认布局错位
- 修复：对齐 AtlantaFX `_menu-button.scss` 标准模式
  - **容器 `-fx-padding: 0`**
  - padding 全部下放到 `> .label`（左右等距）和 `> .arrow-button`（仅垂直 + 按需水平）
- **沉淀经验**：JavaFX 组合控件（容器+`.label`+`.arrow-button`）的 padding 必须遵循 AtlantaFX 模式——**容器 padding=0，padding 下放到 label/arrow-button**。给容器设 padding 会与子节点 padding 叠加，造成箭头位置错乱
- 这是第三次踩到（M11.2 TableView 表头 / 现在 MenuButton），下次再踩沉淀到 SKILL 第 16 条「内部 padding 下放原则」

**问题 3：箭头样式不符合 Ant Design + 缺关闭/切换 API**

- 现象：原箭头是实心三角 `M7 10l5 5 5-5z`（AtlantaFX 风格），但用户截图对比 Ant Design 是细 V 形 chevron——视觉风格不统一
- 修复：
  - **默认 shape 改为 Material Icons keyboard_arrow_down 的 chevron 路径**：`M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z`
  - 新增 `ArrowStyle` 枚举：`CHEVRON`（默认，Ant Design 风格）/ `TRIANGLE`（AtlantaFX 风格）/ `NONE`（无箭头，仅 MenuButton 支持，SplitButton 没箭头会退化无意义）
  - 新增 `.arrowStyle(ArrowStyle)` API + `.noArrow()` 语法糖
  - LESS 实现 `.arrow-triangle` 和 `.no-arrow`（对齐 AtlantaFX `&.no-arrow` 模式）

**问题 4：箭头区域过宽**

- 现象：场景 7 SplitButton 截图，箭头区水平宽度 ≈ 50px（比 Ant Design 截图宽近一倍）
- 根因：arrow-button 沿用了 `.label` 的水平 padding (16px×2)
- 修复：对齐 AtlantaFX `_menu-button.scss:174` 的 `cfg.$padding-x / 2` —— **arrow-button 水平 padding 减半**
  - DEFAULT：16px → **8px**（用 `@spacing-sm`）
  - SMALL：8px → **4px**（用 `@spacing-xs`）
  - LARGE：24px → **12px**（用 `@spacing-md`）
- 视觉宽度：50px → **34px**，与 Ant Design (~30-36px) 基本对齐

**关键改动**：
- `component/MenuButtonAnt.java`（新增 `ArrowStyle` 枚举 + `.arrowStyle()` + `.noArrow()`）
- `component/SplitButtonAnt.java`（新增 `ArrowStyle` 枚举 + `.arrowStyle()`，不支持 NONE）
- `css/less/theme-base.less`：
  - 容器 padding=0，padding 下放到 `.label / .arrow-button`
  - 默认 shape 改为 chevron（Ant Design 风格）
  - 新增 `.arrow-triangle / .no-arrow` 修饰类
  - arrow-button 水平 padding 全档位减半（DEFAULT/SMALL/LARGE）
- `jfxium-demo/.../showcase/pages/ButtonGroupPage.java`（新增"场景 6：MenuButton 箭头样式"，原 6/7 顺延为 7/8，共 8 Section）

**调整后 Showcase 总览（M19.6.1 收尾）**：
```
通用：     Button(8) + Toggle/Menu/SplitButton(8) + Switch(4)
导航：     Menu(7)
布局：     Card(9) + SplitBar(5) + CrudTemplate(4)
数据录入： Input(5)
数据展示： Table(10) + Tag(5)
反馈：     Modal(5) + Drawer(5)
其他：     Watermark(5)
=========================================
共 13 页 / 80 Section
```

**沉淀经验汇总（M19.6 + M19.6.1 共 4 条）**：
1. 内部 padding 下放原则：JavaFX 组合控件（带 `.label` + `.arrow-button` 的）必须容器 padding=0
2. 箭头节点必须显式设 shape：MenuButton/SplitMenuButton 的 `.arrow` 没默认 shape
3. arrow-button 水平 padding 减半：对齐 AtlantaFX 标准
4. 箭头视觉风格优先 Ant Design chevron，TRIANGLE/NONE 作为 opt-in 选项


**关键改动**：
- `component/BarAnt.java`（新建）
- `core/css/CssClasses.java`（加 2 个 SPLIT_BAR_* 常量；删除 ACTION_BAR / ACTION_BAR_SPACER 2 个常量）
- `css/less/theme-base.less`（加 `.split-bar / .split-bar-spacer` 选择器；删除 `.action-bar` 选择器）
- `template/CrudTemplate.java`（私有 buildBar 改用 BarAnt，删 spacer 自拼代码）
- `component/CardAnt.java`（buildHeader 内部从 Headers 工厂迁到 BarAnt）
- `component/SurfaceAnt.java`（build 内部从 Headers 工厂迁到 BarAnt）
- **删除** `component/ActionBarAnt.java`（M19 子阶段 19.4）
- **删除** `core/util/Headers.java`（M19 子阶段 19.4）
- `jfxium-demo/.../showcase/pages/SplitBarPage.java`（新建，5 Section）
- `jfxium-demo/.../showcase/ShowcaseDemo.java`（注册）
- `README_CN.md` / `README.md` / `docs/COMPONENTS.md` / `docs/LAYOUT.md` / `PLAN.md`（ActionBarAnt 引用全部替换）

**与既有组件的关系**：

| 场景 | 选谁 | 理由 |
|---|---|---|
| 简单顺序排列（仅"左+右"或"左+中+右"） | **`BarAnt`** | 唯一布局原子，已覆盖 ActionBar/Header 全部场景 |
| 仅 title + extra 的 Header | **`BarAnt`**（二段模式） | 老 Headers 工厂已删除 |
| 真三段（左+中+右） | **`BarAnt`** | 语义明确 |
| admin 列表页带 search/filter/action 业务 API | `FilterBarAnt` | L2 业务模板，内部用 BarAnt 二段 |
| 整页业务骨架 | `CrudTemplate`（内部已用 BarAnt） | 三段工具栏是 CrudTemplate 的子能力 |

**设计要点**：
- **center 真正居中**：不是"靠左+紧跟"，而是左/右两侧用独立 spacer 撑开做对称分配——admin 顶部栏的"中间标题"是高频诉求
- **center 受挤压偏移**：当 left/right 内容宽度不对称时，center 会向较窄的一侧偏移——这是 flex 标准行为，不是 bug
- **退化为二段**：center 为空时只有一个 spacer，等价于 `[left ─── right]`，与 ActionBar 的 spacer 模式语义一致

**踩坑实证**：
- M18 写 CrudTemplate 时 buildBar 是私有方法，本质上已经写过一遍三段式逻辑——M19 之前是"项目内重复实现 N 次"的反例
- SKILL 3.1 一直把三段式列为"标准模式"但只给用户示范代码，没抽成组件——M19 才补全这个标准模式的"组件层"
- 单元测试性优势：以前每次用户手写 `[left, spacer, right]` 都得重新检查 spacer 是否设了 `Hgrow + maxWidth`，现在只需要相信 BarAnt 即可

**Showcase 总览更新**：
```
布局：     Card(9) + SplitBar(5) + CrudTemplate(4)        ← 3 个布局组件
```
共 11 → **12 页 / 67 → 72 Section**。


### 🎯 M19.13 SKILL 沉淀：踩坑实证 → 强约束（2026-05-24，B 路线）

**动机**：M11.2 / M19.5 / M19.6 / M19.7 等里程碑积累了多次踩坑实证，但都散落在 PLAN.md 单个里程碑里，未提炼为 SKILL 强约束。本轮一次性沉淀。

**产出**：

**子阶段 19.13.1：项目约束 SKILL 新增 3 条强约束（#16 / #17 / #18）**
- [x] **#16 JavaFX 组合控件「内部 padding 下放原则」**（M11.2 + M19.5 + M19.6 三次实证）
  - 容器 `-fx-padding: 0`，padding 全部下放到 `> .label` 和 `> .arrow-button`
  - arrow-button 水平 padding 减半（用 `@spacing-sm`）
  - 适用：ComboBox / MenuButton / SplitMenuButton / DatePicker / ColorPicker / TableView 表头
- [x] **#17 JavaFX 内部 `.arrow` 节点必须显式设 shape**（M19.6 实证）
  - modena 默认对部分 `.arrow`（MenuButton / SplitMenuButton）没设 shape
  - 必须显式 `-fx-shape` + min/pref-width/height
  - 提供两种标准 SVG path：Chevron（Ant 风格）/ Triangle（AtlantaFX 风格）
- [x] **#18 \*Ant Builder `build()` 返回类型契约**（M17 实证）
  - 直接节点型 vs Result 包装型 —— 一表对比
  - 命名约定：build() 诚实返回（不撒谎）/ Result 类公开 open()/show()/close() 触发方法

**子阶段 19.13.2：项目约束 SKILL「为什么这些很重要」补 3 条**
- [x] 第 5 条：内部 padding 下放原则（统一组合控件视觉的钥匙）
- [x] 第 6 条：`.arrow` 没默认 shape 时只设颜色 = 看不见
- [x] 第 7 条：Builder 返回类型契约要诚实

**子阶段 19.13.3：组件组合规范 SKILL 新增第七章「包结构归约」**
- [x] 三层包结构表（component/template/layout）+ 命名后缀强制对应
- [x] 「写新组件前问自己」三连问决策树
- [x] `*Ant` vs `*Template` 的判别标准
- [x] 反例记录：FilterBarAnt 历史债（实际是业务模板，应迁 template/，暂不迁）
- [x] 与第二章「微组件清单」的衔接：四层依赖单向递增（微组件 → 原子控件 → 业务模板 → 应用骨架）

**子阶段 19.13.4：组件组合规范 SKILL 反模式新增 3 条（§4.9 / §4.10 / §4.11）**
- [x] **§4.9 容器吞 padding 导致内部布局错乱**（与项目约束 #16 互引）
- [x] **§4.10 `.arrow` 节点只设颜色不设 shape**（与项目约束 #17 互引）
- [x] **§4.11 字符串模板里嵌套未转义的双引号**（M19.7 + M19.10 多次踩到，写 Showcase description 时反复编译失败）

**关键改动**：
- `.kiro/steering/项目约束与计划/SKILL.md`（加 #16/#17/#18 + 「为什么重要」补 3 条；版本号未必单独标注）
- `.kiro/steering/组件组合规范/SKILL.md`（加 §7 + §4.9/4.10/4.11；版本 1.0 → 1.1）
- `PLAN.md`（累计成果 SKILL 强约束条数 15 → 18）

**统计**：
- 项目约束 SKILL：原 15 条 → 现 18 条（+3）
- 组件组合规范 SKILL：原 8 条反模式 → 现 11 条（+3）；新增第七章 4 节
- 「为什么这些很重要」补充 4→7 条

**B 路线意义**：
1. **降低未来重复踩坑概率** —— 这些 bug 都不是「编译报错」型，而是「视觉效果不对」「Modal 不弹出」型，没有沉淀就会反复踩
2. **缩短新协作者上手时间** —— 直接读 SKILL 即可知道 JavaFX 组合控件的 padding 规则
3. **PLAN.md 减负** —— PLAN.md 是日志，SKILL 是规约，两者不应混用——本轮把 PLAN 里的「踩坑实证」提炼到 SKILL，PLAN.md 只保留时间线

### 🎯 M19.14-M19.15 Showcase 第三/四/五/六批 + 收尾两页（2026-05-24，A 路线）

**动机**：完成 admin 高频组件的 Showcase 全覆盖；admin 项目能直接照着 Showcase 抄。

**产出**：

**M19.14**（第六批 + 第七批 + 收尾两页）：
- 第三批（DropdownPage / ListPage / TabsPage / StepsPage / TimelinePage / DescriptionsPage / SkeletonPage）—— 7 页 / 32 Section
- 第四批（DatePicker / Slider / InputNumber / Upload / Breadcrumb / Carousel / Calendar）—— 7 页 / 33 Section
- 第五批（CodeBlock / Empty / Result / Spin / Cascader / TimePicker / Transfer）—— 7 页 / 27 Section
- 第六批（Anchor / AutoComplete / Mentions / Segmented / Spinner / TextArea / TreeSelect）—— 7 页 / 27 Section

**M19.15**（A 路线收尾）：
- CollapsePage（5 Section，含 AccordionAnt 对比）
- ImagePage（4 Section，src/fallback/borderRadius/objectFit）
- **A 路线正式封盘**：剩余 ~15 个组件（IconAnt/DividerAnt/Space/Flex/Grid/AppShell/Surface/SplitPane 等）按需补，不再做单独 Showcase

**Showcase 总览（M19.15 后）**：57 页 / **273 Section**，覆盖 admin 后台 90% 业务场景。

**踩坑实证**：
- 字符串模板里嵌套未转义双引号（中英文场景）多次踩坑——已沉淀到 SKILL §4.11
- TreeAnt 的 `.root(value, graphic, ...)` 不支持 graphic 参数；正解：用 `TreeAnt.node(value, graphic, ...)` 包出 TreeItem 后传 `.root(TreeItem)`

### 🎯 M19.16 业务模板沉淀：LoginTemplate + DashboardTemplate（2026-05-24，B 路线）

**动机**：A 路线 57 页 Showcase 已经覆盖单组件全集，但用户仍要把组件拼成完整业务页 —— admin 后台 90% 业务页（登录/概览/列表）的骨架是固定的，应抽成模板复用。

**产出**：

**子阶段 19.16.1：LoginTemplate**
- [x] 新建 `template/LoginTemplate.java`（~330 行）—— 双栏 banner 登录页
- [x] API：brandName / tagline / features(...) / copyright / formTitle / formSubtitle / submitText / showRememberMe / onSubmit(BiConsumer<u,p>) / onForgot(Runnable) / onRegister(Runnable)
- [x] 抽自 admin demo `LoginStage`（~300 行手写 → 一行 builder 调用）
- [x] PasswordField + 前置图标的复合输入框（HBox 自拼边框，模拟集成式输入）
- [x] 渐变 banner 用 CSS 变量（`-color-accent-4 → emphasis → -color-accent-7`），切主题自动跟随
- [x] 可选回调 default null —— 不调即不显示对应链接

**子阶段 19.16.2：DashboardTemplate**
- [x] 新建 `template/DashboardTemplate.java`（~210 行）—— admin 概览首页骨架
- [x] API：welcome / stat(icon, title, value, trend, up) / statColumns / bottomLeft / bottomRight / leftRatio / sectionGap
- [x] 抽自 admin demo `DashboardPage` —— 欢迎语 + N 列统计卡 + 底部双栏
- [x] 自动按 stat 数量决定列数（也可强制 statColumns）
- [x] 底部双栏比例可调（默认 60:40，admin 行业惯例）

**子阶段 19.16.3：Showcase 整合 + 菜单分组**
- [x] 新建 `LoginDashboardTemplatePage.java`（4 Section）—— 基础登录 + 完整登录 + 基础 Dashboard + 完整 Dashboard
- [x] ShowcasePage 新增 `Category.TEMPLATE("业务模板")` 枚举
- [x] CrudTemplatePage 从 LAYOUT → TEMPLATE 分类
- [x] LoginDashboardTemplatePage 注册到 TEMPLATE
- [x] SplitBarPage 保留在 LAYOUT（BarAnt 是布局原子，不是业务模板）
- [x] 左侧菜单底部新增「业务模板」独立分组

**关键改动**：
- `template/LoginTemplate.java`（新建）
- `template/DashboardTemplate.java`（新建）
- `showcase/pages/LoginDashboardTemplatePage.java`（新建）
- `showcase/ShowcasePage.java`（加 TEMPLATE 枚举值）
- `showcase/ShowcaseDemo.java`（按业务模板分类重新组织注册块）

**template/ 包现状（M19.16 后）**：
- CrudTemplate（M18，业务页三段式）
- LoginTemplate（M19.16，登录页双栏）
- DashboardTemplate（M19.16，概览首页）

**Showcase 总览（M19.16 后）**：57 页 / 273 Section（其中「业务模板」分类 2 页 / 8 Section）

**设计取舍**：
- 模板的 API 优先暴露「常见配置」（brandName/welcome/stat），罕见配置走 styleClass + LESS 覆盖
- 所有可选项 default null —— 不调用即不渲染该区，避免空 `null` 占位
- build() 诚实返回容器（BorderPane / VBox），调用方自己包 Scene/Stage

### 🎯 M19.17 文档加固：README + QUICKSTART（2026-05-24，C 路线）

**动机**：项目已经发展到 82 个 *Ant + 3 个 *Template，但 README_CN 还停在 M7 时代「74 个组件」；新协作者读完 README 也不知道怎么 5 分钟搭起一个 admin 后台。本轮一次性补齐文档。

**产出**：

**子阶段 19.17.1：README_CN.md 加固**
- [x] 「组件分类」头部 "74 个组件" → "82 个 *Ant + 3 个 *Template = 85 个"
- [x] 数据展示组件表加 SelectableTextAnt 行（M19.7）
- [x] 新增「业务模板（Templates）」整章 —— CrudTemplate / LoginTemplate / DashboardTemplate 三个完整 API 示例 + 区别说明（*Ant vs *Template）
- [x] M19.6 按钮族（Toggle/Menu/Split）已经在「通用组件」表里

**子阶段 19.17.2：README.md（英文）同步**
- [x] Features：74 → 82 *Ant + 3 Templates
- [x] Data Display 节加 SelectableTextAnt
- [x] 新增「Business Templates (3)」章，对外讲清楚 template/ 包的存在
- [x] 末尾指向 docs/QUICKSTART.md

**子阶段 19.17.3：新建 docs/QUICKSTART.md**
- [x] 5 分钟搞起一个完整 admin 后台 —— 登录页 + 主页 + 列表页
- [x] 三个示例完整代码（LoginTemplate ~30 行 / DashboardTemplate ~30 行 / CrudTemplate + TableAnt + FilterBarAnt 列表页）
- [x] 末尾指引到主题/自定义/SKILL 文档

**关键改动**：
- `README_CN.md`（+ 业务模板整章 ~70 行 + 数据展示一行 + 头部数字）
- `README.md`（+ Business Templates 表 ~10 行 + 数据展示一行 + 头部数字）
- `docs/QUICKSTART.md`（新建，~140 行）

**为什么这一步重要**：
1. **降低用户上手门槛** —— 之前用户读完 README 还不知道怎么从 0 起一个项目，QUICKSTART 给「复制粘贴可运行」的最小代码
2. **凸显业务模板价值** —— 新加的 template/ 包不在 README 里就等于不存在；现在三个模板都有 API 示例
3. **README 与项目同步** —— "74 个组件" 这种过期数字会让用户怀疑项目已停更

### 🎯 M19.18 i18n 国际化（默认中文）+ Carousel 修复（2026-05-24）

**动机**：项目原本所有用户可见文案都硬编码在 *.java 里（CodeBlock 的「复制」、TreeSelect 的「请选择」、Modal/Popconfirm 的 OK/Cancel/Yes/No、Upload 的英文拖拽提示、Transfer 的「Source/Target」、LoginTemplate 中文表单等），无法切换语言。用户路线 4 选择 i18n 国际化，明确「默认中文」。

**产出**：

**子阶段 19.18.1：i18n 基础设施**
- [x] 新建 `jfxium/core/i18n/Messages.java`（~150 行）
  - 静态门面：`get(String) / get(String, Object...) / setLocale(Locale) / getLocale() / localeProperty()`
  - 默认 Locale = `Locale.SIMPLIFIED_CHINESE`（用户明确「默认中文」）
  - 三级 fallback：current locale → ROOT bundle (messages.properties) → key 自身 + WARNING 日志
  - 线程安全：synchronized + Platform.runLater 把 localeProperty 写入推到 FX 线程
  - 0 第三方依赖（仅 JDK ResourceBundle/MessageFormat/Locale）
- [x] 新建 3 份 properties（`src/main/resources/org/openkawu/jfxium/i18n/`）
  - `messages.properties`（fallback，与 zh_CN 同内容）
  - `messages_zh_CN.properties`（默认中文）
  - `messages_en.properties`（英文）
  - 每份 28 个 key，按组件分组用 `#` 注释
- [x] `module-info.java` 新增 `exports org.openkawu.jfxium.core.i18n` + `requires java.logging`

**子阶段 19.18.2：组件迁移（9 个）**
- [x] **CodeBlockAnt**：「复制」/「已复制!」 → `Messages.get("codeblock.copy" / "codeblock.copied")` + 监听 localeProperty 自动刷新
- [x] **TreeSelectAnt**：placeholder「请选择」 → null + build 时 i18n + 监听刷新
- [x] **EmptyAnt**：description「No Data」 → null + i18n + 监听刷新
- [x] **ModalAnt**：okText「OK」/ cancelText「Cancel」 → null + 取 i18n + 监听刷新
- [x] **PopconfirmAnt**：okText「Yes」/ cancelText「No」 → null + show 时取 i18n
- [x] **UploadAnt**：buttonText / dragText / hintText / 「Error」 → 全部 i18n
- [x] **TransferAnt**：titles「Source;Target」 → null + i18n；count "items" 用 `transfer.items` 参数化
- [x] **LoginTemplate**：8 个 banner/表单字段 + 错误前缀 + 「记住我」/「忘记密码？」/「还没账号？」/「立即注册」 → null + i18n
- [x] **DashboardTemplate**：「 较上周」 → `Messages.get("dashboard.compared_to_last_week")`

**迁移策略**（待沉淀到 SKILL）：
- **Builder 默认值用 null + build() 时 lazy 求值**（不要写 `private String x = Messages.get(...)`，会在 Builder 实例化时锁死 Locale）
- **A 类（一次性弹窗 / 短生命周期）**：构造时取一次 `Messages.get(...)`，不监听 localeProperty，避免 listener 泄漏
- **B 类（常驻 UI 容器树）**：监听 localeProperty 自动刷新，且仅当用户没显式覆盖时才挂 listener

**子阶段 19.18.3：I18nPage Showcase**
- [x] 新建 `jfxium-demo/.../showcase/pages/I18nPage.java`（~250 行 / 8 Section）
  - Locale 切换器（中文 / English 按钮 + 当前 Locale 标签实时刷新）
  - EmptyAnt / TreeSelectAnt / CodeBlockAnt / ModalAnt + PopconfirmAnt / UploadAnt / TransferAnt 各自演示
  - API 速查
- [x] ShowcaseDemo 注册（「其他」分类，与 Watermark 同组）

**子阶段 19.18.4：CarouselAnt 顺手修 4 个 UI bug**

验收 demo 时用户发现 Carousel 多个问题，一并修了：

| 问题 | 根因 | 修法 |
|---|---|---|
| 箭头按钮难看（白底无视觉） | `.carousel-arrow-btn` 被全局 `.button` / `:hover` / `:armed` 通用规则覆盖（同特异性，但通用规则有伪类优先） | 改用复合选择器 `.button.carousel-arrow-btn`（特异性=2），补全 `:hover/:armed/:pressed` 全状态。Ant 风格 40×40 圆形 `rgba(0,0,0,0.4)` 半透明黑底 + 白字 + 阴影 |
| dots 飘在 carousel 中心（不在底部） | `HBox` 默认 `maxWidth/maxHeight = MAX_VALUE`，被 StackPane 拉伸撑满 → `setAlignment(BOTTOM_CENTER)` 失效 | `dotsBox.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE)` 强制收缩到内容尺寸 |
| dots 被 slide 遮住看不见 | StackPane 子节点默认按添加顺序叠层，slide 在 dotsBox 之前 | `dotsBox.setViewOrder(-100)` + `arrowBtn.setViewOrder(-100)` 强制浮在最上层 |
| SCROLL 切换无方向感（prev/next 都从右滑入，旧 slide 瞬间消失） | navigateTo 单方向 + 旧 slide 直接 `setVisible(false)` | 加 `direction` 参数：next 旧左滑出 + 新右滑入，prev 旧右滑出 + 新左滑入。两 TranslateTransition 并行播放。`contentPane.setClip(Rectangle)` 裁剪边界防越界 |

**新增 API**：
- `CarouselAnt.dotPosition(DotPosition)` —— TOP / CENTER / BOTTOM 三档（默认 BOTTOM）
- CarouselPage 加场景 5 演示三种位置

**关键改动**：
- `jfxium/src/main/java/org/openkawu/jfxium/core/i18n/Messages.java`（新建，~150 行）
- `jfxium/src/main/resources/org/openkawu/jfxium/i18n/`（新建 3 份 properties，每份 28 keys）
- `jfxium/src/main/java/org/openkawu/jfxium/module-info.java`（exports + requires java.logging）
- 9 个 *Ant / *Template 迁移
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/showcase/pages/I18nPage.java`（新建）
- `ShowcaseDemo.java`（注册 I18nPage）
- `jfxium/src/main/java/org/openkawu/jfxium/component/CarouselAnt.java`（修 4 处 bug + dotPosition API）
- `css/less/theme-base.less`（carousel-arrow-btn 复合选择器 + dots 样式重写）
- `CarouselPage.java`（新增场景 5）

**踩坑实证（待沉淀到 SKILL）**：
1. **JavaFX CSS 复合选择器优先级陷阱**：裸 `.carousel-arrow-btn` 被 `.button` 通用规则覆盖。M11 的 SKILL #14 是「复合 vs 后代」，本轮补的是「**复合优先级提升**」—— 同特异性下，需要主动写出 `.button.carousel-arrow-btn` 这种带具体场景标识的复合选择器，否则被通用 `.button:hover` 等伪类规则压制
2. **HBox 在 StackPane 内默认拉伸到 MAX**：`setAlignment` 看似失效是因为节点已被拉伸到撑满父容器；必须配合 `setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE)` 才能真正按 alignment 摆位
3. **viewOrder 是控 z 序的标准答案**：StackPane 子节点叠层默认按 children 顺序，但 `setViewOrder` 优先级最高（数值越小越靠前），用来让 dots/箭头浮在 slide 之上不被遮挡
4. **i18n Builder 默认值不要在字段初始化时取 Messages.get**：会在 Builder 实例化那一刻锁死 Locale，正确做法是 null + build() 时 lazy 取

**为什么这一步重要**：
1. **i18n 是 0 → 1 的能力**：之前所有面向用户字符串都硬编码，海外用户 / 双语项目根本没法用；现在 `Messages.setLocale(Locale.ENGLISH)` 一行切换
2. **CarouselAnt 修 4 个 bug**：UI 组件的「能用」和「好用」差距巨大，验收 demo 是发现真实痛点的最好时机
3. **viewOrder + setMaxSize(USE_PREF_SIZE)**：JavaFX 布局两个鲜为人知但极其关键的 API，沉淀进 SKILL 后续高频用得上

---

### 🎯 M19.19 Showcase 顶栏 + 菜单分组 + ThemeColor 色阶 bug（2026-05-24）

**动机**：用户体验路线 4（i18n）后顺手提了多个真实痛点：
1. Showcase 左侧菜单 60+ 项全平铺，没分组超难找；
2. 想要在顶部直观切换主题、主题色、紧凑度；
3. 切换主题色时菜单选中态背景变成纯色实块，遮住文字（截图证据：紫色实色把"SplitBar 三段式布局"那一行吞了）；
4. 菜单分类的展开 / 折叠行为应该可配置（手风琴 vs 多展开）；
5. SubMenu 箭头紧贴文字、没顶到行尾。

**产出**：

**子阶段 19.19.1：Showcase 菜单改成 subMenu 折叠分组**
- [x] 7 大分类（通用 / 布局 / 导航 / 数据录入 / 数据展示 / 反馈 / 其他 / 业务模板）每个独立 subMenu
- [x] 当前路由所在分类**自动展开**（让用户随时知道自己在哪）
- [x] MenuAnt 加 `SubMenuBuilder.defaultExpanded(boolean)` 公开 API
- [x] MenuAnt 加 `containsKey(selectedKey)` 自动展开判断逻辑

**子阶段 19.19.2：Showcase 顶栏全套主题工具栏**
- [x] 主题家族 Segmented：Ant / MUI 切换
- [x] 紧凑度 Segmented：默认 / 紧凑
- [x] 主题色 11 个色点（ThemeColor.Preset 全列）：Blue / Purple / Cyan / Green / Magenta / Red / Orange / Gold / Lime / GeekBlue / Volcano，hover 显示 Tooltip
- [x] 🌗 / ☀ 切亮暗
- [x] 切换时 `refreshAccent()` 把当前主题色重新应用到新主题，避免切到 Dark 丢色

**子阶段 19.19.3：修 ThemeColor.generateColorScale 算法 bug（隐性 + 高影响）**

**根因**（`ThemeColor.java`）：
```java
// 旧算法：i=0 时 factor = 1，1-factor = 0
double factor = 1 - (i * 0.18);
Color c = base.interpolate(Color.WHITE, 1 - factor);
// → interpolate(white, 0) = base 本色（紫色实色）
```

i=0 时算出的 `accent-0` 直接 = 主色实色，而 `-color-accent-subtle = -color-accent-0`，导致 `.menu-item-selected` 选中行背景变实色把文字吞掉。**这个 bug 一直没暴露**：默认 LightTheme / MuiTheme 的色阶是 LESS 静态写死的；只有点了主题色 dot（触发 `setPrimaryColor` → `applyPrimaryColorToAll`）后才会用 Java 端动态注入 inline，覆盖 LESS。

**修法**：用显式 mix 数组替代 off-by-one 错误的乘法。
- 亮色色阶：i=0 → 5% base + 95% white（极浅）→ i=5 base → i=9 70% black 混色
- 暗色色阶同理对称翻转
- `lightMix = {0.05, 0.20, 0.35, 0.50, 0.70}` / `darkMix = {0.15, 0.30, 0.50, 0.70}`

**子阶段 19.19.4：MenuAnt 加 ExpandMode（手风琴 vs 多展开）**

**动机**：用户反馈「展开 A 分类后展开 B，再点 B 下的 item，A 被折叠 — 应该要么手风琴互斥，要么多展开互不影响」。原 bug 根因：`navigateTo → rebuildSider` 重建菜单时只保留"含 selectedKey 的分类自动展开"，用户手动展开的状态全丢。

**修法**：
- [x] MenuAnt 新增 `ExpandMode { MULTIPLE, EXCLUSIVE }` 枚举
- [x] Builder 加 `expandMode(ExpandMode)` / `expandedKeys(Collection<String>)` / `onExpandChange(Consumer<Set<String>>)` 三个 API
- [x] BuildContext 持有 `expandedKeys` Set + 顶级 SubMenu 列表，互斥时通知兄弟节点收起
- [x] ShowcaseFrame 用 `expandedCategoryKeys` 持久化跨 rebuildSider 的展开状态
- [x] 顶栏加「多展开 / 手风琴」Segmented 让用户实时切换

**子阶段 19.19.5：subMenu 箭头推到最右**

**根因**：MenuAnt `createInlineHeader` 给 Label 设 `Hgrow=ALWAYS` 想撑开右侧空间——但 **Label 默认 maxWidth=USE_PREF_SIZE，Hgrow 不会让它拉伸**（SKILL §4.1 / §20.1 反复实证）。结果箭头紧贴文字，右侧大片空白。

**修法**：用独立 Region spacer 替代给 Label 设 Hgrow。

**这是 MenuAnt 自身的实证**：之前 SKILL §4.1 / §20.1 总在讲外部业务代码，**框架内部组件自己也踩同样的坑**，说明这个反模式真的极其隐蔽，每次都要靠肉眼看截图发现。

**关键改动**：
- `jfxium/src/main/java/org/openkawu/jfxium/component/MenuAnt.java`：新增 `ExpandMode` 枚举 + `defaultExpanded` / `expandMode` / `expandedKeys` / `onExpandChange` 4 个 API + `containsKey` 递归判断 + `SubMenuRenderInfo` 互斥 + spacer 修复
- `jfxium/src/main/java/org/openkawu/jfxium/core/theme/ThemeColor.java`：色阶算法整段重写（`generateColorScale` + `generateDarkColorScale` 都修了相同 off-by-one bug）
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/showcase/ShowcaseFrame.java`：顶栏全套主题工具栏 + 菜单分组渲染 + 展开状态持久化 + 菜单模式切换

**踩坑实证（沉淀）**：
1. **Label Hgrow 反模式 — 框架内部第二次实证**：M19.18 Carousel dots 是第一次（HBox 撑满 StackPane），本轮 MenuAnt subMenu header 是第二次（Label 不拉伸）。**SKILL 有写，但代码里还是漏点位**。下次写新组件时 grep 一遍 `HBox.setHgrow(label`、`HBox.setHgrow(.*Label`、`setHgrow(.*new Label` 类似模式，提前清掉。
2. **Java 端动态色阶 bug 隐藏 N 个版本**：因为默认主题的色阶来自 LESS 静态写死，只有调 `setPrimaryColor` 时才会触发动态算法。**任何"只有特定路径才执行的代码"都要单独验证**。
3. **rebuild 模式下要做状态持久化**：菜单 / Tabs / Tree 等组件如果用 rebuild 重建，必须把交互态（展开 / 选中 / 滚动位置）跨实例持久化，否则用户看上去就是"点击导致状态丢失"。

**为什么这一步重要**：
1. **菜单分组 + 顶栏工具栏让 Showcase 真正可用**：60+ 组件全平铺找不到，做完后用户找组件几秒到位
2. **ThemeColor 色阶算法是项目级地基**：所有"动态切主题色"功能都依赖它；之前 bug 没暴露纯属侥幸
3. **MenuAnt ExpandMode 是 admin 通用能力**：手风琴 vs 多展开两种 UX 选择都很常见，业务方应该可配

---

### 🎯 M19.20 Switch / CheckBox / Radio 三件套加 Shape API（2026-05-24）

**动机**：用户提出三个选择类组件能不能支持多形态——圆形 / 方形 / 圆角等。M19.6 已经给三个组件加了 Size API，本轮补 Shape API 完成"全维度变体"。

**产出**：

**子阶段 19.20.1：三个组件的 Shape 枚举**
- [x] **SwitchAnt**：`Shape { PILL, ROUNDED, SQUARE }` —— PILL 默认胶囊；ROUNDED 圆角矩形；SQUARE 直角矩形
- [x] **CheckBoxAnt**：`Shape { DEFAULT, CIRCLE, SQUARE, ROUNDED }` —— DEFAULT 方形小圆角；**CIRCLE 圆形**（仍是多选语义）；SQUARE 直角；ROUNDED 大圆角
- [x] **RadioButtonAnt**：`Shape { DEFAULT, SQUARE, ROUNDED }` —— DEFAULT 圆形；SQUARE 方形（仍是单选语义）；ROUNDED 圆角方形

**子阶段 19.20.2：LESS 复合选择器修饰类**
- [x] `.jfx-switch.shape-rounded/.shape-square` + 嵌套到 `.jfx-switch-track` / `.jfx-switch-thumb`
- [x] `.check-box.shape-circle/.shape-square/.shape-rounded` + 嵌套到 `.box`
- [x] `.radio-button.shape-square/.shape-rounded` + 嵌套到 `.radio` / `.dot`

**子阶段 19.20.3：踩坑实证 —— Switch thumb radius 必须同时盖 background + border**

**第一次提交**：Switch ROUNDED/SQUARE thumb 视觉不对——thumb 看起来还是圆形贴在方角轨道上，特别违和。

**根因**：thumb 节点 modena 默认有 **两个** radius：
```less
.jfx-switch-thumb {
  -fx-background-radius: 9px;
  -fx-border-radius: 9px;       /* 我只覆盖了 background-radius，border-radius 漏了 */
}
```

JavaFX CSS 里 `-fx-background-radius` 控背景圆角、`-fx-border-radius` 控**边框圆角**，两者独立。只覆盖一个时另一个保留 modena 默认值 9px → 边框还是圆形 → 视觉违和。

**修法**：所有形状变体规则**同时**覆盖两个 radius。

**这是 SKILL 候选条目**（待沉淀）：「JavaFX CSS 圆角必须 `-fx-background-radius` + `-fx-border-radius` 两个一起覆盖」。

**子阶段 19.20.4：Showcase 三页加 Shape Section**
- [x] SwitchPage：新增「形状变体」section，三种形状 × 选中/未选中 = 6 个开关
- [x] CheckBoxPage：新增「场景 6：形状变体」section，DEFAULT/CIRCLE/SQUARE/ROUNDED 四种
- [x] RadioPage：新增「场景 5：形状变体」section，DEFAULT/SQUARE/ROUNDED 三种

**关键改动**：
- `jfxium/src/main/java/org/openkawu/jfxium/component/{SwitchAnt,CheckBoxAnt,RadioButtonAnt}.java`：各加 `Shape` 枚举 + `.shape(Shape)` API + 挂修饰类
- `jfxium/src/main/resources/org/openkawu/jfxium/css/less/theme-base.less`：新增 ~50 行形状变体 LESS
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/showcase/pages/{SwitchPage,CheckBoxPage,RadioPage}.java`：各加 1 个 Shape Section
- `README_CN.md` / `README.md`：组件表更新示例代码体现 `.shape(...)` API

**设计哲学（M15 落档）**：再次实证「吸取三家优点」原则——
- Ant Design 没有 Switch 形状 API，但 CheckBox/Radio 默认就是方/圆
- Element Plus 有 size 但没 shape API
- AtlantaFX 完全无形状变体
- 我们综合：**Size + Shape 双维度变体**，业务方能拼出 12+ 种不同视觉的同语义控件

**为什么这一步重要**：
1. **同语义不同外观是 admin 实战常见需求**：合规系统要求"必勾选"的复选框做圆形以提示重要性、设置页面想用方形 Switch 显示"硬开关"风格，这些都需要形状自由
2. **`-fx-background-radius` + `-fx-border-radius` 双覆盖**：JavaFX CSS 圆角的隐藏陷阱，下次修组件圆角先 grep 一遍 LESS 看俩属性都覆盖没

---

### 🎯 M19.21 GridAnt 响应式断点（2026-05-24）

**动机**：用户提议把 P2.3「GridAnt 二期：xs/sm/md/lg/xl/xxl 响应式断点」做掉。原 GridAnt（M3）只支持固定 span，admin 后台在不同窗口宽度下无法自动重排。

**产出**：

**子阶段 19.21.1：Breakpoint 枚举 + 响应式监听**
- [x] 新增 `Breakpoint { XS, SM, MD, LG, XL, XXL }`，阈值对齐 Ant Design / Bootstrap：0/576/768/992/1200/1600
- [x] `Breakpoint.of(double width)` 静态方法，根据宽度返回所属断点
- [x] `Builder.responsive()` 启用 Scene 宽度监听（用 `sceneProperty()` 链 + `widthProperty()` 链，跨断点重建所有行）
- [x] **不调 .responsive() 时所有断点配置退化为默认 span**，老 API 完全兼容

**子阶段 19.21.2：ColBuilder 链式响应式 API**
- [x] 新增 `ColBuilder` 链式构造器：`.span(int) / .offset(int)` + 6 档 `.xs/.sm/.md/.lg/.xl/.xxl(int)` + 6 档 `.xsOffset(...)`
- [x] 静态工厂 `GridAnt.col(node)` → `ColBuilder`
- [x] `Row.col(ColBuilder)` 重载收纳新 API
- [x] **回退规则**：未设的断点向下查找最近有效值（lg → md → sm → xs → 默认 span），都没设用 24

**子阶段 19.21.3：兼容性**
- [x] 老 `Col(span, node)` / `Col(span, offset, node)` 构造器保留
- [x] 老 `Row.col(int span, Node)` / `Row.col(int span, int offset, Node)` 保留
- [x] `placeNode` 加去重：rebuild 场景下同一节点会被多次挂 styleClass，需要 `if (!contains)` 兜底

**子阶段 19.21.4：GridPage Showcase**

迭代了两版：
- **v1（鸡肋版）**：5 个 section 演示 24 列等分 / gutter 0/16/32 对比 / offset / 6 卡响应式 / 断点表 —— 用户反馈「展示的全是变体语法，没有真实业务场景」
- **v2（业务版）**：3 个 admin 真实场景 + API 速查 —— 每个 section 代码可直接复制粘贴
  1. **admin 表单两栏布局**（4 字段，xs 单栏 / sm+ 双栏，备注独占一行）
  2. **Dashboard 统计卡矩阵**（4 卡，xs 1 列 / sm 2 列 / lg 4 列）
  3. **内容 + 辅助侧栏**（左 16 列 + 右 8 列，xs 时辅助下沉，详情页骨架）
  4. **自动 wrap（FlowPane）**（用户提需求："不用 24 列语义，N 个等宽卡片自动 wrap"）—— **诚实告诉用户 GridAnt 不天然支持，给出 JavaFX 原生 FlowPane 替代方案**

**关键改动**：
- `jfxium/src/main/java/org/openkawu/jfxium/component/GridAnt.java`：~340 行（M3 原版 ~150 行）
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/showcase/pages/GridPage.java`（新建）
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/showcase/ShowcaseDemo.java`（注册 GridPage）

**踩坑实证（沉淀）**：
1. **Showcase v1 鸡肋的根因**：纯展示「API 怎么写」，没展示「什么时候用」。用户拿到不知道怎么照搬。**Showcase 应该是"业务模板复制源"，不是"API 文档可视化"**。
2. **响应式不一定要复杂栅格**：用户提的"窄 1 排 2、宽 1 排 4 自动 wrap"原生 FlowPane 就能做。**不要为了用栅格而用栅格** —— 加场景 4 给出 FlowPane 替代方案，避免用户被 24 列模型困住。

**为什么这一步重要**：
1. **响应式是 admin 后台基本能力**：用户切窗口大小（特别是从全屏切到分屏），布局应该跟着变
2. **复用给 AppShellAnt（M19.22）**：Breakpoint 枚举建好后下个里程碑直接用，不重复造轮子
3. **Showcase 哲学的转折点**：从 v1 → v2 的迭代是"Showcase 是业务模板而不是 API 文档"的明确实证，沉淀进 PLAN 后续每次新建 Page 都要回想这个原则

---

### 🎯 M19.22 AppShellAnt 增强：Sider 折叠 + 响应式自适应（2026-05-24）

**动机**：M19.21 做完 GridAnt 响应式后，admin AppShellAnt 是下一个需要响应式能力的组件 —— 窄屏时左侧菜单应该能折叠成图标条。

**产出**：

**子阶段 19.22.1：Sider 折叠**
- [x] `Builder.collapsible(boolean)` 启用折叠能力
- [x] `Builder.collapsed(boolean)` 初始折叠状态
- [x] `Builder.collapsedWidth(double)` 折叠后宽度（默认 64px，admin 行业惯例）
- [x] `Builder.trigger(boolean)` 是否显示 sider 底部内置 ‹/› 触发按钮（默认 true）
- [x] `Builder.onCollapseChange(Consumer<Boolean>)` 状态变化回调
- [x] 折叠态在 sider 节点上挂 `.app-shell-sider-collapsed` styleClass，方便业务用 LESS 切换内部细节（如菜单文字隐藏）
- [x] 用 `BooleanProperty` 持有折叠状态，所有相关组件（trigger 按钮文字 / sider 宽度 / onCollapseChange / styleClass）都订阅它

**子阶段 19.22.2：响应式断点自适应**
- [x] `Builder.breakpoint(GridAnt.Breakpoint)` —— Scene 宽度小于此断点的 minWidth 时自动折叠
- [x] **复用 GridAnt.Breakpoint 枚举**（M19.21），不重复造轮子
- [x] 用 `sceneProperty()` 链 + `widthProperty()` 链监听，跨阈值时自动 toggle
- [x] 入场景图当下立即按当前宽度判断一次（避免初次渲染状态错）

**子阶段 19.22.3：Result 外部控制句柄**
- [x] 新增 `Result` 类：包含 root BorderPane + collapsed BooleanProperty
- [x] `result.toggle()` / `result.setCollapsed(b)` / `result.collapsedProperty()`
- [x] `Builder.buildResult()` 返回 `Result`；`Builder.build()` 仍返回 `BorderPane`（向下兼容）
- [x] 适用场景：让 Header 上的汉堡按钮 / 自定义触发器控制折叠（用 `.trigger(false)` 隐藏内置按钮）

**子阶段 19.22.4：AppShellPage Showcase**
- [x] 4 个 section：基础 / 可折叠 / 外部控制 / 响应式断点自适应
- [x] 每个都是真实可用的代码（吸取 M19.21 v2 教训：Showcase 是业务模板）

**LESS 新增**：
- `.app-shell-sider-collapsed` 占位钩子
- `.button.app-shell-sider-trigger` 内置触发按钮（hover/armed 状态全覆盖）

**关键改动**：
- `jfxium/src/main/java/org/openkawu/jfxium/component/AppShellAnt.java`：~140 行 → ~280 行
- `jfxium/src/main/resources/org/openkawu/jfxium/css/less/theme-base.less`：新增 ~20 行
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/showcase/pages/AppShellPage.java`（新建）
- `jfxium-demo/src/main/java/org/openkawu/jfxium/demo/showcase/ShowcaseDemo.java`（注册 AppShellPage）

**为什么这一步重要**：
1. **Sider 折叠是 admin 后台必备**：缩小窗口看正文 / 移动办公场景必须能折菜单
2. **Result 包装型 API 模式实证**：M19.13 SKILL #18 讲过「直接节点型 vs Result 包装型」契约，AppShellAnt 这次实证 — 想让外部控制内部状态时，**明确返回 Result 比返回 BorderPane 加无数 setter 更清晰**
3. **复用 GridAnt.Breakpoint 是好示范**：跨组件共享枚举，避免每个组件都定义自己的 Breakpoint

---


- [x] **SpinAnt `Color.web("#1677ff")` 硬编码**：已修复（M7），改用 Region + CSS 变量替代 Shape
- [x] **AlertBanner 孤儿类**（`component/base/AlertBanner.java`）：已删除（M7），功能被 AlertAnt 完全覆盖
- [x] **AnchorAnt / StatisticAnt 的 `Color`-based API**：已弃用（M9），添加 @Deprecated 注解，引导用户改用 styleClass
- [x] **SceneLayout/OverlayManager 使用文档**：已补充（M7），README_CN.md 中添加完整使用示例
- [x] **Layouts.java 废弃迁移**：已完全移除（M9），所有引用已迁移到独立 Builder 类

---

### 🎯 M19.38 双向溯源：MenuAnt 加 runtime Controller（2026-05-27）

**动机**：用户报「ShowcaseDemo 切下方菜单项后侧栏滚动条跳回顶部」。最初当作 demo 局部 bug 处理（把 ScrollPane 字段化复用），但顺手按「示例项目即回归测试」的视角溯源——根因是 **MenuAnt 没 runtime API**，逼调用方 rebuild 整个 menu。

**沉淀**：
- 项目约束 SKILL.md 新增第 22 条「**示例项目即回归测试 / 双向溯源**」：用户报 demo 问题时禁止只修示例，必须同时追问源头是否有 API 缺失
- 「为什么这些很重要」加第 8 条 demo 即免费回归测试

**产出**：

**子阶段 38.1：源头修复（jfxium）**
- [x] `MenuAnt.Controller` 类：runtime 修改菜单状态的句柄
- [x] `Builder.controller()` —— `build()` 后取控制器（推荐）
- [x] `MenuAnt.controllerOf(Pane)` —— 从已构造产物里反查（兼容老代码）
- [x] 控制器 API：`setSelectedKey / expandKey / collapseKey / setExpandedKeys / getSelectedKey / getExpandedKeys`
- [x] `BuildContext` 扩展：`itemRows`（key→row 节点索引）+ `expandHandles`（key→展开/收起闭包对）
- [x] `SubMenuBuilder.buildInline` 把 `doExpand` / `doCollapse` 闭包对登记到 ctx，互斥模式 / 嵌套子菜单都能控

**子阶段 38.2：示例验证（jfxium-demo）**
- [x] `ShowcaseFrame.navigateTo()` 用 `menuController.setSelectedKey()` + `expandKey()` 替代 `rebuildSider()`
- [x] `rebuildSider()` 仅保留给 expandMode 切换（MULTIPLE/EXCLUSIVE 是 build-time 配置，必须重建）
- [x] sider 外层 `ScrollPane siderScroll` 字段化复用，runtime 切菜单不动这一层

**子阶段 38.3：文档同步**
- [x] BUG.md #30 立项 + 修复说明
- [x] SKILL.md §22 沉淀双向溯源原则

**关键改动**：
- `jfxium/component/MenuAnt.java`：~470 → ~570 行
  - 加 Controller 类（~70 行）+ ExpandHandle 类
  - BuildContext 加 itemRows / expandHandles 双索引
  - SubMenuBuilder 拆出 doExpand 闭包（原本只有 doCollapse 给互斥用）
  - applyItemStyles 把 row 注册进 itemRows
  - selectedKey 从 final → 可写（Controller 修改）
- `jfxium-demo/.../ShowcaseFrame.java`：navigateTo 改 controller API；rebuildSider 仅 expandMode 切换时调
- `BUG.md`：新增 #30
- `.kiro/steering/项目约束与计划/SKILL.md`：新增第 22 条 + 「为什么这些很重要」第 8 条

**踩坑实证**：
- 表层修法（demo 字段化 ScrollPane）虽然修了滚动条，但根因没动，**下次换一个用法（例如想要 runtime 切高亮 + 保留子菜单展开动画）仍会触发同根因 bug**
- 真正的源头修法是给框架加缺失的 runtime API；表层修法是辅助保险

**对齐原则**：JFXium 是 UI 库，`jfxium-demo` 是回归测试用例集；demo 里写代码不顺手 = 框架 API 有缺口

---

### 🎯 M19.42 数据输入控件取值 + runtime API 补齐（2026-05-31）

**动机**：用户报 BUG #5——Dropdown/MenuButton/ComboBox/InputNumber/Cascader/TreeSelect/ColorPicker/TimePicker 一族「数据输入」控件「点了之后取不到选中的 value」（表面是 label，要的是底层 value）。先全控件审计再按 SKILL §22 双向修源头 + demo。

**审计结论**：8 个控件里 6 个本来就能取到 value（ComboBox/InputNumber/ColorPicker/TimePicker/Cascader/MenuButton）。真正缺口只有 2 个：
- **#54 DropdownAnt**：`onSelect(key)` 只回 key → 新增 `onSelectItem(Consumer<MenuItem>)` 回传完整对象（含 key+label）
- **#53 TreeSelectAnt 多选**：`onMultipleSelect` 死回调 → 点击切换选中态 + 输入框回填 + 触发回调 + 选中行高亮（新增 `.tree-select-selected`）

**顺带补齐 runtime API（仿 M19.38 MenuAnt.Controller 模式）**：
- **#51 StepsAnt.Controller**：`setCurrent/next/prev`，不重建节点切步骤
- **#52 AnchorAnt.Controller**：`setActiveKey`，点击锚点自动移高亮

**沉淀**：项目约束 SKILL 新增 **#24「runtime 修改一律走 Controller 模式」**——build() 装配 Controller 持有已渲染节点引用，setter 直接改 styleClass 不重建。Menu/Steps/Anchor 三个组件已统一此范式。

**关键改动**：`DropdownAnt` / `TreeSelectAnt` / `StepsAnt` / `AnchorAnt` + `CssClasses`（TREE_SELECT_SELECTED）+ `theme-base.less` + 4 个示例页删 workaround + BUG.md #51-54。

---

### 🎯 M19.43 范围 Slider 假溢出真因：9999px 圆角泄出（2026-05-31）

**动机**：用户报 BUG #6 范围 Slider 溢出容器，连改两轮 `maxWidth` 都没用。

**真因（探针实测，非猜）**：`.slider .track` 用了 `@border-radius-full`(9999px)，JavaFX SliderSkin 不裁切 track 圆角 → track **视觉 bounds** 宽达 20144px（布局盒其实正常 160px），形成「一根线横穿窗口」的假溢出。改 `@border-radius-md` 后 track 恢复 158px。

**关键方法论**：写临时探针 dump `getBoundsInLocal()` 拿运行时真值，发现「布局盒正常但视觉 bounds 爆炸」，瞬间定位到圆角而非宽度。**「溢出」症状 ≠ 根因是「宽度约束」**。

**全项目排查**：grep 出 13 处 `@border-radius-full`，写探针扫描所有可疑示例页（Progress/Switch/Badge/List/Slider），实测**只有 slider track 一处中招**——其余都因「尺寸被钳死」或「两轴都有约束」而安全。确认不是 StackPane 通病，是「9999 圆角 + 尺寸未钳制」的特定组合。

**附带修复**：#57 DatePicker 基础用法日期文字显示不全 → `.date-picker` 加 `min-width:130 / pref-width:160`。

**沉淀**：
- 项目约束 SKILL **#23「@border-radius-full 只能用在尺寸被硬钳制的节点」**
- 组件组合规范 SKILL **§4.12 反模式**（含探针排查法 + clip 会掩盖此类 bug）

**关键改动**：`theme-base.less`（`.slider .track` / `.colored-track` / `.date-picker` 圆角与宽度）+ `SliderAnt.java`（范围模式 slider 限宽，虽非根因但顺手收敛布局）+ BUG.md #55-57。

---

### 🎯 M19.44 验收准备：文档账面对齐 + 验收清单（2026-06-01）

**动机**：进度走到 M19.43、BUG 表 #1–#57 全闭环、双模块编译零报错，进入「可验收」节点。但有两处账面与实际对不上，先抹平再验收。

**产出**：
- [x] **BUG.md 顶部清理**：把置顶的未编号原始反馈（第 1–9 条）折叠成「已归档对照表」，逐条标注对应修复编号（#41–#57），并修正自相矛盾的标题日期（原「2026-05-17 全部完成」）。
- [x] **form-ant-enhance spec 收尾**：该 spec 此前只有 requirements.md，但 4 个新 API（header/footer 变长/footerAlign/section）+ FormExamplePage 实际已在 M19.39 落地。补回 `design.md`（实现实况）+ `acceptance.md`（验收清单），机器可验证项 9 条全部实测通过。
- [x] **新增 `ACCEPTANCE.md`**（项目根）：全局人工验收清单——11 套主题 + 重点回归页（对应 #41–#57）+ 66 个示例页逐分类勾选，验收基准锁定 default 尺寸（SKILL 密度约束）。

**实测核对**（机器项）：
- FormAnt / FormExamplePage `grep setStyle("-fx-` 均 0 命中
- CssClasses 3 常量（FORM_HEADER/FORM_SECTION_TITLE/FORM_FOOTER）+ theme-base.less 3 选择器齐全
- `./mvnw install -pl jfxium` + `compile -pl jfxium-demo` 双零报错

**待办（移交人工）**：UI 层验收按 `ACCEPTANCE.md` 跑 `./mvnw javafx:run -pl jfxium-demo` 逐项勾选；新发现问题从 BUG #58 续编号，按 SKILL §22 双向溯源。

---

### 🎯 M19.45 紧凑模式尺寸体系修复（2026-06-01）

**动机**：用户验收紧凑模式时发现 Table「只字变小、行距没收紧」。

**根因（两层系统性缺陷）**：
1. **高度硬编码**：`.column-header-background`/`.table-row-cell` 的 48px 写死，不走 token。紧凑模式靠「覆盖 token 重新生成」工作，硬编码值它管不到。
2. **compact spacing 没真收窄**：light/dark-compact 的 `@spacing-xs/sm` 还是默认 4/8（只有 mui-compact 改对成 2/6），导致走 `@spacing-sm` 的组件（Table cell / List / Menu / Tab 等）紧凑 padding 没变。

**修复**：
- `variables-base.less` 新增 `@table-header-height` / `@table-row-height` 高度 token
- `theme-base.less` Table 默认表头高/行高改引用 token（SMALL/LARGE 显式档不动）
- light/dark-compact 修 `@spacing-xs:2 / @spacing-sm:6` + 覆盖 table 高度 token 48→36
- mui-compact/mui-dark-compact 补覆盖 table 高度 token 48→36

**验证（grep 4 套 compact CSS）**：default 48px → compact 36px；cell padding 8×12 → 6×8。连带 List/Menu/Tab/Tree/Tooltip/Form 紧凑也真生效。

**沉淀**：紧凑模式只对「引用了 compact 覆盖的 token」的属性生效，硬编码 px 一律失效。新组件 pref/min-height、padding 一律走 token，不写死 px。详见 BUG #58。

---

### 🎯 M19.46-M19.50 主题系统 + 构建链 + 控件高度（2026-06-02）

> demo 验收主题/紧凑时一路双向溯源，揪出 6 个框架 bug + 补 1 组件 + 加主题选择器。详见 BUG #59-#64。

**M19.46 去 Node 依赖**：LESS 编译 `exec-maven-plugin(npx lessc)` → `groovy-maven-plugin + jlessc 1.16`（纯 Java）。新机器只需 JDK。

**M19.47 主题选择器 + ThemeManager 三维状态机**：重构成 Family × dark × compact 正交组合，新增 `setFamily/setDark/setCompactDensity` + 切换后自动重应用 accent。demo 顶栏加「风格 / 明暗 / 紧凑 / 主题色」4 控件。

**M19.48 LabelAnt**：补最基础的 Label 封装（继承式 + 链式，仿 VBoxAnt 双工厂）。demo 加 LabelExamplePage。

**M19.49 ThemeManager inline style bug**：主题色注入误用 `.root{}` 选择器塞 setStyle → ClassCastException + 切换丢色。去选择器 + applyTheme 后自动重应用。

**M19.50 控件全家族高度对齐 + 构建假成功 bug**：
- Button/ComboBox/Input/Select/DatePicker 在 small/large 下高度不齐 → 统一 padding token + 补 min-height 钳到 controlHeight。探针实测 default 31 / small 24 / large 40 全对齐。
- 揪出 groovy-maven-plugin 下 `Files.writeString`/`File.text` 静默不落盘的「假成功」bug → 改 `OutputStreamWriter`+flush+写入校验。
- 顺手清理 4 处无效 `-fx-transition`；demo 菜单「Select 选择器」→「ComboBox 下拉框」。

**沉淀**：
- 构建期写文件别用 `Files.writeString`/`File.text`（某些插件 classloader 静默失败），用显式 stream+flush+写入校验。
- 控件跨族等高：光对齐 padding 不够（skin 盒模型差异），需 min-height 钳到 controlHeight。
- `Node.setStyle()` 只接受属性声明，禁止带选择器。

---

### 🎯 M19.51-M19.53 PC UI 标准固化 + Bar 改名 + 组件分包（2026-06-03）

**M19.51 组合容器壳化**：Card/Modal/Drawer/Form 的 header/footer slot CSS padding 归零，高度由传入的 BarAnt 自身 `.padding()` 自控。容器只做「壳 + 分隔线」。Card body padding 16→12（再收紧）。SplitBarAnt 增强：padding/borderBottom/borderTop/minHeight/prefHeight/maxWidth。布局类全家族补齐原生属性（maxW/minW/prefW/fill/grow/margin 等 ~80 方法）。

**M19.52 PC UI 标准固化**：项目约束 SKILL 新增「PC UI 实现标准（强制）」——尺寸基准（控件 28-32 / 卡片 12-16 / 字号 13-14 / 间距 4/8/12/16 / 图标 16 / 圆角 4-6）+ 布局/交互/视觉准则。明确「桌面 admin 思维，不用 Web 思维」。SplitBarAnt → **BarAnt** 改名（Split 与 SplitPane 语义冲突）。

**M19.53 组件按类型分包**：`component/` 顶层 73 个 *Ant 平铺 → 按 build() 返回类型分三子包：
- `component/control/`（23）原子型——薄封装原生控件
- `component/composite/`（42）组合型——微组件拼装容器
- `component/overlay/`（7）浮层型——Result 包装
- `component/layout/`（13）/ `base/`（9）原有不动
- FilterBarAnt 迁入 `template/`

映射表脚本一次性完成：移文件 + 改 package + 全局修 import + module-info exports。jfxium + demo 编译通过、demo 运行时干净、clean install BUILD SUCCESS。组件组合规范 SKILL 第七章同步更新（新增 7.2 三子包按类型归约）。

---

## 五、下一阶段计划

### 🔴 P0：本次重构遗留收尾（短期）
- [x] 跑 demo 全面验收（待 JDK 就绪）：`mvn -pl jfxium compile` + 启动 demo，覆盖所有主题
- [x] 修 SpinAnt 硬编码主题色 TODO（用 Region 替代 Shape 或 Looked-up colors API）
- [x] 决定 AlertBanner 孤儿类去留
- [x] 补充 SceneLayout/OverlayManager 完整使用文档（README_CN.md）

### 🆕 P0+：文档与 API 可用性收尾（M19.37 实战反馈）
> **动机**：业务实测「用着吃力，写个东西想老半天」——补足新人配方手册 + 暴露当前 API 限制，避免业务侧撞墙。

**文档侧**（已完成 M19.37）：
- [x] README 顶部加「5 分钟配方手册」5 配方（登录页 / admin 列表 / 表单提交 / 继承式页面 / 跨窗口通信）
- [x] README 加「按场景找组件」检索表（17 行映射表）
- [x] README 加「已知限制 / 绕行方案」表（暴露 API 边界，避免撞墙）
- [x] 目录扩充至 14 节，新人推荐阅读顺序

**API 侧**（待办）：
- [ ] **FormAnt 增强**（核心）—— 业务高频痛点
  - `header(Node)` 顶部 banner 区
  - `footer(Node...)` 变长重载，支持多按钮
  - `footerAlign(Pos)` 对齐方式（默认 CENTER_RIGHT）
  - `section(String)` 分段标题
- [ ] InputAnt 加 `.password(boolean)` 模式开关
- [ ] InputAnt 考虑加密码可见切换（参考 AtlantaFX PasswordTextFormatter）
- [ ] IconAnt 加 `.color(Color)` 直接设图标颜色（避免 inline style）

### 🟠 P1：基础设施二期（中期）
- [x] 抽公共 padding(Insets) Builder 钩子（5+ 组件重复）
- [x] 抽公共 title+extra Header 工厂（CardAnt/PageAnt/SurfaceAnt 重复）
- [x] 弃用 `Color`-based API（AnchorAnt.inkColor / StatisticAnt.valueColor），引导用户改用 styleClass
- [x] 完全移除 Layouts.java（已标记 @Deprecated）

### 🟡 P2：从"造控件"到"用好控件"（下一阶段重点）

> **核心思路**：从造控件转向用好控件。
> **方向调整（M12.4 后）**：原计划做完整 Web Admin demo，做到 M12.4 用户列表页时发现：
> - 控件层痛点已基本暴露（M11 + M11.1 集中修完 TableAnt）
> - 完整业务 demo 工程量大、复用价值低（每个项目业务不同）
> - **转向"控件 Showcase Demo"**：每个组件展示完整的"姿态、API、变体、最佳实践"，源码即文档（类似 AtlantaFX Sampler）

#### P2.1 Web Admin 后台 Demo（已转向，里程碑已达成）

**已完成的部分**（沉淀到主框架）：
- ✅ M12.1 应用骨架：双 Stage（Login 400×520 / Main 1280×800）+ Router + IconAnt SVG path 13 个业务图标
- ✅ M12.2 登录页双栏 banner：760×520，主题色渐变
- ✅ M12.3 Dashboard 首页：4 列统计卡 + Timeline + 待办
- ✅ M12.4 用户列表页：FilterBarAnt（新通用组件）+ TableAnt 三段式模板（驱动出 M11.1 5 项增强）

**沉淀产物**（保留，作为"参考实现 + 模板代码"）：
- `jfxium/component/FilterBarAnt.java`：筛选+搜索+操作工具栏（admin 通用）
- `jfxium/component/IconAnt.Path` 枚举：13 个业务 SVG 图标
- `jfxium-demo/admin/`：完整骨架代码可复制粘贴

**已停止的部分**：404 异常页、表单页、个人中心、注册等单价值低的页面（M12.5+ 不再做）

#### P2.1' 控件 Showcase Demo（NEW，下一阶段主线）

**核心思路**：类似 AtlantaFX Sampler，每个组件一页，展示其全部"姿态、API 变体、典型用法、复制粘贴片段"。源码本身就是该组件的活文档。

**目标**：
1. 让用户**5 分钟内**找到任何组件的所有用法
2. 源码可直接复制粘贴到自己项目
3. 反向驱动控件 API 不一致、文档不全的问题暴露

**结构（参考 AtlantaFX Sampler）**：
```
ShowcaseDemo
├─ 左侧组件列表（按分类：通用/布局/导航/数据录入/数据展示/反馈/其他）
├─ 中间组件展示区（卡片化分块：基础用法/变体/状态/事件/最佳实践）
└─ 右侧侧边（可选）：API 速查、当前主题切换器
```

**实施策略（按需迭代，不一次性做完）**：
- [ ] **基础设施**：ShowcaseFrame（左侧菜单 + 主内容区路由）
- [ ] **第一批组件**（已重写过的 / 高频）：
  - ButtonAnt（10+ 变体已覆盖最完整）
  - InputAnt（基础/前后缀/校验态/disabled/password 等）
  - **TableAnt**（M11 + M11.1 全部新功能展示）
  - CardAnt（M10 9 个新功能展示）
  - WatermarkAnt（M8 P0+P2 全功能展示）
- [ ] **第二批组件**：DatePicker / Select / Modal / Drawer / ...
- [ ] **第三批**：剩余 50 个组件（按字母序或分类批量推进）

**预期产出**：
- 一个独立的 `jfxium-showcase` 模块（或 jfxium-demo 内子包）
- 每个组件一个独立 ShowcasePage，~80-150 行
- 源码可直接拷贝复用

#### P2.2 业务模板沉淀（保留，按需推进）

> 部分模板已在 P2.1 中沉淀（FilterBarAnt 等）；后续按真实需求继续。

- [x] **FilterBarAnt** —— 筛选+搜索+操作工具栏（M12.4 已沉淀）
- [x] **CrudTemplate** —— admin 通用三段式业务页骨架（M18 已沉淀，覆盖 CRUD 列表/表单/仪表盘/详情）
- [ ] **LoginPageTemplate** —— LoginStage 已是参考实现
- [ ] **DetailPageTemplate** —— 详情/编辑页模板（左表单 + 右辅助信息）
- [ ] **DashboardTemplate** —— 仪表盘网格模板（DashboardPage 已是参考实现）

#### P2.3 控件层查漏补缺（被 P2.1' Showcase 倒逼，按需推进）

- [x] 数据表格高级功能：排序、列宽、对齐、操作列、边框模式、隐藏表头（M11 + M11.1 完成）
- [x] GridAnt 二期：xs/sm/md/lg/xl/xxl 响应式断点（M19.21 完成）
- [x] AppShellAnt 增强：Sider 折叠 / breakpoint（M19.22 完成）
- [ ] FormAnt 增强：校验规则、字段联动、嵌套表单
- [ ] **bindValue API（声明式数据绑定）**：所有数据输入控件（InputAnt / CheckBoxAnt / ComboBoxAnt / DatePickerAnt / RadioButtonAnt 等）新增 `bindValue(Property)` 方法，build 时自动双向绑定（`bindBidirectional`）。用户声明 Property 即可取值/监听，无需持有控件引用。只读场景通过 `.disabled(true)` 控制，不引入 BindMode 枚举。后续考虑 FormModel（表单级数据容器）做批量取值/重置/回填。
- [x] CardAnt 缺失 props（M10 完成）

#### P2.4 元工具（次优先级）

- [ ] 主题预览器（在线切换主题、调色、复制 LESS）
- [ ] 组件文档自动生成（扫描 Builder API → Markdown）

### 🟢 P3：长期愿景
- [x] 国际化（i18n）支持（按钮文字、复制提示等硬编码字符串外置）—— M19.18 完成
- [ ] 主题色板在线编辑器
- [ ] 单元测试覆盖（核心 Builder API）
- [ ] 发布到 Maven Central
- [ ] Figma 设计稿导入
- [ ] 组件市场

---

## 六、文件结构

```
JFXium/                                # 多模块 Maven 项目（parent）
├── pom.xml                           # 父 POM（modules: jfxium, jfxium-demo）
├── docs/
│   ├── SKILL.md                      # 开发规范（主题色阶、交互规范）
│   ├── API.md                        # API 文档
│   └── COMPONENTS.md                 # 组件清单
│
├── jfxium/                           # 主框架模块（发布产物）
│   ├── pom.xml                       # 含 8 个 lessc execution
│   └── src/main/
│       ├── java/org/openkawu/jfxium/
│       │   ├── JFXiumApp.java        # 框架入口
│       │   ├── module-info.java      # JPMS 模块声明
│       │   ├── component/            # 原子控件 + 装饰容器 + 浮层（*Ant 后缀）
│       │   │   ├── ButtonAnt.java
│       │   │   ├── InputAnt.java
│       │   │   ├── ModalAnt.java
│       │   │   └── ...               # 详见 README_CN 组件清单
│       │   ├── template/             # 业务模板（M18 新增，*Template 后缀）
│       │   │   └── CrudTemplate.java # 通用三段式业务页骨架
│       │   └── core/                 # 内核：CssClasses、Theme API 等
│       └── resources/org/openkawu/jfxium/css/
│           ├── less/                 # LESS 源
│           │   ├── variables-base.less   # 共享尺寸/间距/mixin
│           │   ├── variables.less        # 亮色色阶
│           │   ├── variables-dark.less   # 暗色色阶
│           │   ├── theme-base.less       # 所有组件样式（主题无关）
│           │   ├── theme-light.less      # 亮色主题入口
│           │   ├── theme-dark.less       # 暗色主题入口
│           │   ├── theme-mui*.less       # MUI 系列（4 套）
│           │   ├── theme-shadcn.less     # shadcn 主题
│           │   ├── theme-cyberpunk.less  # cyberpunk 主题
│           │   └── theme-custom.less     # 自定义主题示例
│           └── theme-*.css           # 编译产物（generate-resources 阶段由 npx lessc 生成）
│
└── jfxium-demo/                      # Demo / Playground 模块（不发布）
    ├── pom.xml                       # 含 javafx-maven-plugin 运行配置
    └── src/main/java/org/openkawu/jfxium/demo/
        └── JFXiumDemo.java           # mainClass，演示所有组件
```

> **构建约束**：LESS 编译强依赖宿主机 Node.js（pom 中 8 个 execution 调 `npx lessc`），新机器需先确保 `node -v && npx -v` 可用，否则 `mvn compile` 会在 `generate-resources` 阶段失败。
