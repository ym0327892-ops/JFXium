---
name: design-reference
description: >
  JFXium 设计风格参考体系。在新建组件、调整样式、审查视觉一致性时使用。
  定义了风格来源权重、研究重点、禁止参考对象，以及已知的设计差距。
---

# JFXium 设计参考体系

> 核心定位：**专业桌面工具软件**（IDE / 数据库工具 / 监控系统 / 交易软件），不是 Web 后台管理系统。

## 一、风格来源权重

```
50% JetBrains New UI
25% Qt Widgets
10% DBeaver + Navicat + Wireshark + Beyond Compare
 5% Visual Studio
 5% Windows Terminal + Task Manager
 5% Windows 11 Desktop + macOS Desktop
```

## 二、第一梯队（必须参考，75%）

### JetBrains New UI — 权重 50%

**最成熟的桌面开发工具 UI 之一，是本项目的风格锚点。**

研究重点：

| 模块 | 具体参考 |
|------|---------|
| Toolbar | 紧凑按钮排列、图标+文字混合、分组分隔线 |
| Project Tree | 行高 28px、展开箭头、图标对齐、拖拽反馈 |
| Editor Tabs | 紧凑 Tab、关闭按钮位置、修改标记（圆点）、拖拽排序 |
| Settings Dialog | 左侧导航树 + 右侧内容面板、搜索过滤、面包屑 |
| Search Everywhere | 全局搜索弹层、分组结果、实时过滤 |
| Tool Window | 可停靠面板、标题栏折叠、工具栏按钮 |
| Dark Theme | 暗色调色板（`#1e1f22` 底色）、对比度、边框策略 |
| Dense Mode | 紧凑模式行高、间距收紧策略 |

### Qt 6 Widgets — 权重 25%

**桌面软件设计的教科书，控件风格标杆。参考 Qt Widgets，不是 Qt Quick / QML Mobile。**

研究重点：

| 控件 | 具体参考 |
|------|---------|
| QPushButton | 默认按钮高亮、图标+文字、尺寸档位 |
| QLineEdit | 输入框边框策略、focus 效果、placeholder |
| QComboBox | 下拉箭头、弹层样式、可编辑模式 |
| QTreeView | 展开/折叠、复选框节点、列宽调整 |
| QTableView | 表头样式、行选择、列排序、交替行色 |
| QToolBar | 工具栏布局、按钮间距、分隔线 |
| QMenuBar | 菜单栏紧凑度、下拉菜单宽度 |

## 三、第二梯队（辅助参考，15%）

### Visual Studio — 权重 5%

研究重点：
- Solution Explorer（树形导航、搜索过滤）
- Toolbar（多行工具栏、下拉按钮）
- Property Grid（属性面板布局、分组折叠）
- Dock Panel（可停靠窗口系统）

### Windows Terminal — 权重 5%

研究重点：
- Tab（多标签、标签配色、标签关闭）
- Search（命令面板、搜索 UI）
- Settings（JSON + GUI 双模式设置）

### Task Manager — 权重 5%

研究重点：
- Table（高密度数据表格、实时更新）
- Navigation（左侧 Tab 导航）
- Density（信息密度标杆）

## 四、第三梯队（数据软件，~10%）

### DBeaver

研究重点：Table、Tree、Toolbar、Dense Layout

数据库工具的密度标杆，一个屏幕要展示尽可能多的数据行。

### Navicat

研究重点：Data Grid、Dialogs、Forms

数据编辑表单、内联编辑、批量操作 UI。

### Wireshark

研究重点：High Density、Large Data、Filtering

大数据量表格渲染、过滤栏设计、实时数据流 UI。

### Beyond Compare

研究重点：Split View、Toolbar、Navigation

分屏对比、工具栏设计、文件树导航。

## 五、第四梯队（系统规范，~5%）

### Windows 11 Desktop

研究重点：
- Spacing（基础间距体系）
- Corner Radius（系统级圆角规范）
- Dialog（对话框布局、按钮排列）
- Context Menu（右键菜单层级）

不要全抄，只参考基础交互规范。

### macOS Desktop

研究重点：
- Toolbar（工具栏布局、分段控件）
- Sidebar（侧边栏导航、分组）
- SearchField（搜索框、清除按钮）

## 六、JavaFX 生态辅助参考

### AtlantaFX

项目内置参考（源码在 `ant-design-ref/AntLantaFx/`），必须研究：

| 主题 | 重点 |
|------|------|
| PrimerLight / PrimerDark | GitHub 风格的 JavaFX 实现，控件尺寸/间距/色彩 |
| NordLight / NordDark | 低对比度护眼主题，暗色模式色彩策略 |
| Cupertino | macOS 风格的 JavaFX 实现 |

重点关注 `src/main/resources/theme/` 中的 JavaFX 原生控件皮肤处理经验。

### ControlsFX

研究重点：
- PropertySheet（属性编辑器）
- Notifications（通知弹层）
- MasterDetailPane（主从面板布局）
- SearchableComboBox（可搜索下拉框）

## 七、禁止参考

以下会把 JavaFX 做成"网页套壳风"：

| 框架 | 禁止原因 |
|------|---------|
| Ant Design | 控件过高、留白过大、信息密度低、Web 后台管理系统思维 |
| Element Plus | 同上，Vue 生态的 Web 组件库 |
| Bootstrap | 同上，响应式 Web 框架 |
| Material Design Web | 同上，触摸优先、大留白、浮动按钮 |

这些框架适合网页和后台管理系统，不适合交易软件、数据库工具、监控平台、开发工具。

## 八、已知设计差距（待处理）

以下项目遗留问题与新设计方向存在差距，需要后续独立任务处理：

### 8.1 MUI 主题保留策略

- **现状**：ThemeManager 维护 `Family.ANT_DESIGN` 和 `Family.MUI` 两个族，共 4 个主题类（LightTheme / DarkTheme / MuiTheme / MuiDarkTheme）
- **差距**：Material Design 在"禁止参考"列表中
- **决策建议**：保留 MUI 主题作为"备选风格"（用户可能喜欢），但不再作为设计参考方向。`Family.ANT_DESIGN` 枚举名可考虑后续重命名为 `Family.DEFAULT` 或 `Family.STANDARD`
- **状态**：保留（M19 决定）；仅 AGENTS.md L360 增加注释说明 enum 为历史名称，主题本身是 JFXium 自研

### 8.2 深色侧栏调色板

- **现状**：`@color-menu-dark-bg: #001529`（Ant Design 标志性深蓝色侧栏）
- **差距**：与 JetBrains 暗色主题底色（`#1e1f22`）风格差异大，蓝色调过于明显
- **决策建议**：替换为 JetBrains 风格中性暗色（如 `#1e1f22` 或 `#2b2d30`），或保留蓝色调但降低饱和度
- **状态**：保留 `#001529`（M19 决定：色彩仍可参考 Ant Design）。注释已中性化为"深色侧栏调色板"，后续可考虑提供 `#1e1f22` 备选色板

### 8.3 组件命名 `*Ant` 后缀

- **现状**：所有组件类名带 Ant 后缀（ButtonAnt, InputAnt, TableAnt 等）
- **差距**：Ant 后缀来自 Ant Design，与新设计方向不一致
- **决策建议**：这是一个大规模重命名任务（100+ 类名），影响 module-info.java、demo、所有引用方。建议作为独立任务处理
- **状态**：保留（重命名为独立任务）

### 8.4 Ikonli AntDesignIcons 图标库

- **现状**：项目使用 Ikonli 的 `antdesignicons-outlined` 和 `antdesignicons-filled` 图标集
- **差距**：图标风格来自 Ant Design 体系
- **决策建议**：图标库替换影响面广（所有使用图标图标的组件），且 AntDesignIcons 本身设计质量高、适合桌面工具。建议保留图标库但不再强调其 Ant Design 来源
- **状态**：保留（图标库本身设计质量优秀，独立任务决策）

## 九、设计参考体系重构进展（M19 设计语言迁移）

2026-06 完成。本节记录设计参考体系从 Ant Design 转向桌面工具风格的工作内容与决策。

### 9.1 已完成

| Task | 内容 | 文件 |
|------|------|------|
| 1 | 创建本文件作为设计参考技能 | `.qoder/skills/design-reference.md` |
| 2 | 更新 AGENTS.md Project Overview + Skills 表 + ThemeManager 注释 | `AGENTS.md` |
| 3 | 更新 pc-ui.md §2 参考对象表 + §14 风格目标权重 | `.qoder/skills/pc-ui.md` |
| 4.1 | 清理 variables-base.less 中 8 处 Ant Design 注释 | `less/variables-base.less` |
| 4.2 | 清理 variables.less 中 2 处 Ant Design 注释 | `less/variables.less` |
| 4.3 | 清理 15 个组件 LESS 文件中 ~16 处 Ant Design 注释 | `less/components/*.less` |
| 4.3' | 清理 theme-light-compact.less + theme-dark-compact.less 中尺寸相关 Ant Design 引用（4 + 2 = 6 处） | `less/theme-{light,dark}-compact.less` |
| 5 | Token 值审查：ctrl-padding 数值保留、注释已对齐桌面工具；`@color-menu-dark-bg: #001529` 保留 | `less/variables-base.less` |
| 6 | 本节 | 本文件 §9 |

### 9.2 设计决策记录

#### D1：色彩 token 仍参考 Ant Design（M19 决定）

**决定**：color-accent 0-10 色阶、`@color-menu-dark-bg: #001529` 等色彩 token 保留 Ant Design 影响。

**理由**：
- 色彩是视觉品牌的一部分，蓝/绿/红 accent scale 在桌面工具与 Web 后台是共通的成熟体系
- 替换整套色阶影响面广（94+ 组件、11 个主题、20+ 业务模板），且替换后不一定更"桌面"
- 桌面工具（JetBrains / Qt）也使用蓝色作为 accent（如 IDEA 蓝），与 Ant Design blue-5 (`#1677ff`) 不冲突
- 用户 M19 表态："实际色彩好需要参考 Ant Design 了吧"

**做法**：
- 不再在 AGENTS.md / LESS 注释 / 文档中强调"色彩来自 Ant Design"
- 色彩选择作为"成熟色阶参考"使用，组件层面继续以桌面工具为锚
- 后续如需调整，参见 §8.2 菜单暗色板决策项

#### D2：theme-mui*.less 保留 "Ant Design MUI" 引用

**决定**：`theme-mui.less` / `theme-mui-dark.less` / `theme-mui-compact.less` 中保留 ~17 处 "Ant Design MUI" / "Ant Design MUI token" 字样。

**理由**：
- 这三个文件本身就是 Material Design 主题，明确声明"基于 Ant Design 官方 MUI 配置"
- MUI 主题与色彩紧密绑定（Material 色阶、阴影、圆角），保留引用让 token 对应关系可追溯
- 与 D1 一致：色彩参考保留

**后续**：如 D1 决策变化，MUI 主题需先重新设计色彩基础

#### D3：AtlantaFX antdesign-light.css 引用保留

**决定**：`components/_button.less` L160 `对标 AtlantaFX antdesign-light.css 行 1259-1306（.button.success / .danger）` 保留。

**理由**：
- AtlantaFX 已经是项目 CSS 选择器层级的参考基线（见 `.qoder/rules/a.md`）
- 该引用是技术参照（行号定位），不是设计语言参考
- 与设计参考体系重构目标不冲突

### 9.3 影响面 & 验收

- **代码**：22 个 LESS 文件、AGENTS.md、pc-ui.md、design-reference.md
- **公开 API**：无破坏性变更（`Family.ANT_DESIGN` 枚举保留，仅注释说明）
- **运行时**：无影响（纯注释 + 文档 + 设计语言描述调整）
- **验收**：Task 7 构建验证 `./mvnw install -pl jfxium -DskipTests -q` + `./mvnw test -pl jfxium` 通过

### 9.4 后续待办

| 任务 | 范围 | 优先级 |
|------|------|--------|
| 菜单暗色板备选色 | 提供 `#1e1f22` JetBrains 风格备选菜单色板 | 低 |
| `*Ant` 后缀重命名 | 100+ 类名，影响 module-info.java + demo | 低（独立任务） |
| Ikonli 图标库评估 | 是否替换为更中性的图标库 | 低（独立任务） |
| MUI 主题重设计 | 替换 Material Design 为桌面工具配色 | 待用户决策 |

## 十、弹层交互阻断契约

> **目的**:定义所有 overlay 组件(Modal / Drawer / Dropdown / Toast / Tooltip / ContextMenu / DatePicker / TimePicker / ColorPicker / 自定义弹层)在打开时**阻断下层页面交互**的硬约束,避免事件穿透、焦点盗窃、滚动穿透。借鉴 EUI-NEO `docs/组件.md` §"基本约定" 第 7 条 + §"dialog" L620 + §"sidebar" L649。

### 契约条文(强制)

所有 overlay 组件**必须**满足以下三点,缺一即视为实现缺陷:

1. **scrim/backdrop 阻断下层 hover / click / scroll / focus**
   - Modal / Drawer / ContextMenu:全屏半透明 scrim + **透明 hit rect** 吃掉后内容的 hover、click 和 scroll。
   - Drawer:左/右侧 scrim 负责点击关闭 + 阻断背景 click/scroll/focus;面板背景吃掉空白区域的 click/scroll/focus。
   - Dropdown / DatePicker / TimePicker / ColorPicker:打开时,在下拉面板/对话框外层加全屏透明 dismiss 层(覆盖整个 Scene 而非面板本身),点击触发 `onDismiss`。
2. **焦点命中阻断**
   - overlay 打开时,下层输入框(`TextField` / `TextArea` / `ComboBox` 等可获焦组件)**不得**被点击获焦;即使用户点 scrim,焦点也应保持在 overlay 内或转移到关闭按钮,而不是穿透到底层。
3. **scrim 关闭语义统一**
   - 点击 scrim → 触发 `onClose`(Modal/Drawer)/ `onDismiss`(Dropdown/Toast/Menu),不直接销毁弹层状态。
   - **例外**:带 `onMaskClick` 自定义回调的 Modal,按回调决定是否关闭;**不允许** 完全没有关闭路径的弹层。

### JFXium 实现要点

| 场景 | 实现机制 | 参考实现 |
|------|---------|---------|
| **半透明 scrim 阻断** | `StackPane` 装透明 `Region`(全屏),`setOnMouseClicked(...)` + `setPickOnBounds(true)` | `ModalAnt` / `DrawerAnt` 现有 scrim |
| **透明 dismiss 层** | `Region` 覆盖整个 Scene,`setMouseTransparent(false)`,`setOnMouseClicked(e -> onDismiss.accept())` | `DropdownAnt` / `ContextMenuAnt` 现有 dismiss 层 |
| **focus 阻断** | overlay 打开时把焦点抢到自己的 first focusable 子节点(优先 close 按钮 / 第一个输入框),关闭时归还到原 owner | 参见 `ModalAnt.requestFocus()` 现有逻辑 |
| **键盘 Escape 关闭** | overlay 注册 `SceneAccelerator(Escape)`,触发 `onClose` | 多数 overlay 已有,统一验过即可 |
| **滚动阻断** | 弹层打开时,如果页面有 `ScrollPane`,临时禁用其滚动(`setPannable(false)`);关闭时还原 | 容易遗漏,**作为后续 feature 候选** |

### 反模式(本节禁止)

- ❌ overlay 打开时,下层 TextField 仍能获焦(光标闪)→ 焦点穿透,严重 UX bug。
- ❌ scrim 只画半透明背景,不挂 hit rect → 用户点 scrim 无反应,误以为应用卡死。
- ❌ scrim 用 `setMouseTransparent(true)` → 反向穿透,下层仍可点击。
- ❌ `onClose` 路径缺失,弹层只能通过右上角 X 关闭 → 桌面工具不允许,必须有 scrim/Escape 关闭路径。
- ❌ Dropdown / DatePicker 关闭后,旧 panel 仍保留 hover 高亮 → 命中区未清,旧 panel 仍能触发原回调。必须 `setMouseTransparent(true)` 或 `setVisible(false)`。
- ❌ overlay 嵌套时,内层 modal 的 scrim 没盖到外层 modal → 内层点击会穿透到外层 scrim,误触关闭外层。

### 验收清单(每个 overlay 组件创建/修改时必跑)

- [ ] 打开 overlay → 点击 scrim 区域,弹层正确关闭
- [ ] 打开 overlay → 点击 scrim,下层 TextField 不会获焦
- [ ] 打开 overlay → 按 Escape 键,弹层正确关闭
- [ ] 打开 overlay → 滚动下层 ScrollPane,不会带动底层内容滚动
- [ ] 关闭 overlay → 焦点正确归还到打开前 owner
- [ ] overlay 嵌套时,内层 modal 的 scrim 覆盖整个内层区域(不穿透到外层)

## 十一、外部框架借鉴索引

> **目的**:集中记录本文件借鉴的外部框架来源,便于审计设计来源权重与回溯原始依据。

| 借鉴内容 | 来源 | 借鉴章节 | JFXium 落地位置 |
|---------|------|---------|----------------|
| **弹层交互阻断契约**(scrim + 透明 hit rect + focus 阻断 + topmost hit-test) | [EUI-NEO docs/组件.md](https://github.com/sudoevolve/EUI-NEO/blob/main/docs/%E7%BB%84%E4%BB%B6.md) | §"基本约定" 第 7 条 + §"dialog" L620 + §"sidebar" L649 + §"tooltip" L727 | 本文件 §十 |
| **风格来源权重**(JetBrains 50% / Qt 25% / ...) | 自创 | — | 本文件 §一 |
| **设计语言迁移决策**(从 Ant Design 转向桌面工具) | 自创 | — | 本文件 §九 |

### 后续候选借鉴(待评估)

以下 EUI-NEO 设计点已识别为有借鉴价值,但本次未落地,登记为后续 feature 候选:

| 候选借鉴 | EUI-NEO 来源 | 评估维度 |
|---------|------------|---------|
| `visualStateFrom(id, scale)` 共享视觉态(按钮按压时外层 Stack 跟随缩放) | `docs/组件.md` §"写新组件时的底线" | JFXium 用 `PseudoClass` + `setScaleX/Y` 已能部分表达,需评估是否值得抽象 |
| 8 个命名缓动函数 + 全局速率缩放 | `docs/动画.md`(未在本次审计范围) | JFXium 动画系统已用 `Interpolator` 子类,需评估命名映射 |
| `ignoreLayout()` 装饰背景不入布局 | `docs/组件.md` §"card / layoutDebugOverlay" | JFXium 用 `StackPane.getChildren().add(...)` + `setMouseTransparent(true)` 已能部分表达 |
| `runtimePointerTransformFrom` 高频指针跟随 | `docs/组件.md` §"workshop" L697 | JFXium 用 `Timeline` 驱动 `setTranslateX/Y` 性能足够,除非出现真实性能瓶颈 |
| `MouseArea` 通用输入热区 | `docs/组件.md` §"mouseArea" | JFXium 用 `Region` + 手写事件监听已能实现,**不重复造轮子** |
| `layoutDebugOverlay` 显式绘制 frame/padding/content 边界 | `docs/组件.md` §"card / layoutDebugOverlay" | 调试辅助工具,需评估是否值得加到 `jfxium-debug` 模块 |
| `workshop/` 命名空间放高定组件 | `docs/组件.md` §"workshop" | JFXium 暂不需要,业务模板已用 `*Template` 后缀划分 |

> **审计周期**:每次新增/重大修改组件前,扫一遍本表确认是否还有未评估的借鉴项;**每季度** 复核一次 EUI-NEO 主仓库是否有新的设计模式值得借鉴。

