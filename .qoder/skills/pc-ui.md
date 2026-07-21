# JavaFX Desktop Design System

## 1. 设计目标

面向 Windows / macOS / Linux 桌面软件的 JavaFX UI 设计系统。

核心目标：

* 高信息密度
* 长时间办公使用
* 数据录入效率优先
* 专业工具软件风格（IDE / 数据库工具 / 监控系统 / 交易软件）

---

## 2. 参考对象（按权重从高到低）

| 梯队 | 对象 | 权重 | 重点研究 |
|------|------|------|----------|
| 1 | JetBrains New UI | **50%** | Toolbar / Project Tree / Editor Tabs / Settings Dialog / Search Everywhere / Tool Window / Dark Theme / Dense Mode |
| 1 | Qt 6 Widgets | **25%** | QPushButton / QLineEdit / QComboBox / QTreeView / QTableView / QToolBar / QMenuBar |
| 2 | Visual Studio | ~5% | Solution Explorer / Toolbar / Property Grid / Dock Panel |
| 2 | Windows Terminal | ~5% | Tab / Search / Settings |
| 2 | Task Manager | ~5% | Table / Navigation / Density |
| 3 | DBeaver | ~3% | Table / Tree / Toolbar / Dense Layout |
| 3 | Navicat | ~3% | Data Grid / Dialogs / Forms |
| 3 | Wireshark | ~2% | High Density / Large Data / Filtering |
| 3 | Beyond Compare | ~2% | Split View / Toolbar / Navigation |
| 4 | Windows 11 Desktop | ~3% | Spacing / Corner Radius / Dialog / Context Menu |
| 4 | macOS Desktop | ~2% | Toolbar / Sidebar / SearchField |
| 辅助 | AtlantaFX | — | PrimerLight / PrimerDark / NordLight / NordDark / Cupertino（JavaFX 原生控件皮肤经验） |
| 辅助 | ControlsFX | — | PropertySheet / Notifications / MasterDetailPane / SearchableComboBox |

不参考 Web UI 框架（Ant Design Web / Element Plus / Material Design Web / Bootstrap）的尺寸体系——控件过高、留白过大、信息密度低、移动端思维，不适合桌面软件（交易软件 / 数据库工具 / 监控平台 / 开发工具）。

详细参考体系见 `.qoder/skills/design-reference.md`。

---

## 3. 核心数字体系

> 整个 JavaFX Design System 只围绕 **5 组数字 + 1 个维度** 展开。

| 维度 | 数值 | 数量 | Token 前缀 |
|------|------|------|------------|
| Font 字号 | 12 / 14 / 16 / 20 | 4 档 | `@font-size-*` |
| Radius 圆角 | 4 / 6 / 8 | 3 档 | `@border-radius-*` |
| Height 控件高度 | 22 / 28 / 32 / 40 | 4 档 | `@control-height-*` |
| Spacing 间距 | 4 / 8 / 12 / 16 / 24 | 5 档 | `@spacing-*` |
| Icon 图标 | 16 / 18 / 20 / 24 | 4 档 | `@icon-size-*` |
| Density 密度 | Default / Compact | 1 维 | `Density.DEFAULT` / `Density.COMPACT` |

**绝对禁止**：在上述数字之外出现任意尺寸。常见反例（24 / 26 / 30 / 34 / 36 / 38 / 10 / 22 / 28 / 32 / 40 之间的"中间值"）一律不允许——任何"看起来差不多"的尺寸都是噪音，会让体系碎裂。

**比例推导**：14px 字体 + 32px 控件高度 ≈ JetBrains New UI / VS Code / Qt Creator 的桌面工具主流比例，不是 Ant Design / Fluent UI 那种 Web 思路。整组数字围绕这个比例自然展开：

- 14px 文字 + 6px 上下 padding = 26px → 取整 28（sm）
- 14px 文字 + 9px 上下 padding = 32px → md 标准
- 14px 文字 + 13px 上下 padding = 40px → lg 大按钮

---

## 4. 字体系统

### 4.1 字号梯度

| Token | 值 | 用途 |
|-------|------|------|
| `@font-size-sm` | 12px | 辅助文字、标签、Tooltip、Badge 数字 |
| `@font-size-md` | **14px** | **全局基准字号**，正文、控件、菜单 |
| `@font-size-lg` | 16px | 小标题、Section Header |
| `@font-size-xl` | 20px | 页面标题 |

**已删除 `@font-size-xs: 10px`**：在 Windows 缩放、Linux DPI、Retina 屏下渲染过小、不可读。Badge 数字等极小场景用 12px 替代。

### 4.2 字体族

```
-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif
```

等宽字体：`SFMono-Regular, Consolas, 'Liberation Mono', Menlo, Courier, monospace`

### 4.3 字重

| 用途 | 字重 |
|------|------|
| 按钮、标题 | 500（medium） |
| 正文、控件 | 400（regular，默认） |

---

## 5. 控件高度体系

### 5.1 4 档模式

| 档位 | Token | 高度 | 典型控件 |
|------|-------|------|----------|
| xs | `@control-height-xs` | **22px** | CheckBox / Radio / Switch（原生命令型） |
| sm | `@control-height-sm` | **28px** | Toolbar 按钮、MenuItem、Breadcrumb、TreeRow、Hyperlink、StatusBar、MenuBar |
| md | `@control-height` | **32px** | **标准控件**：Button / Input / ComboBox / DatePicker / TableHeader / TableRow |
| lg | `@control-height-lg` | **40px** | 大操作按钮：开始 / 停止 / 部署 / 导出 / 执行 |

**绝对禁止 24 / 26 / 30 / 34 / 36 / 38**：所有"看起来差不多"的中间值都是噪音。紧凑模式只动 Density，不动档位命名。

> 注：现 LESS 仍保留 `@control-height-sm: 24px` 的旧值（来自 Ant Web compact 算法），需在落地阶段改为 28px 同步到本规范。

### 5.2 紧凑联动（Density 维度）

紧凑模式按 **"减 4 缩一档"** 算法自动联动：

| 档位 | Default | Compact | 差值 |
|------|---------|---------|------|
| xs | 22 | 22 | 0（原生尺寸不变） |
| sm | 28 | 24 | -4 |
| md | 32 | 28 | -4 |
| lg | 40 | 36 | -4 |

特殊控件高度（xs 档）不参与密度联动，原生尺寸天然紧凑。

---

## 6. 间距系统

五档梯度，通过 Density 维度联动：

| Token | Default | Compact | 用途 |
|-------|---------|---------|------|
| `@spacing-xs` | 4px | 2px | 极小间距（图标与文字间） |
| `@spacing-sm` | 8px | 6px | 小间距（cell padding、菜单项） |
| `@spacing-md` | 12px | 8px | 中间距（表单、卡片） |
| `@spacing-lg` | 16px | 12px | 大间距（对话框、区块间） |
| `@spacing-xl` | 24px | 16px | 超大间距（页面级留白） |

禁止任意间距。

---

## 7. 圆角系统

| Token | 值 | 用途 |
|-------|------|------|
| `@border-radius-sm` | 4px | 小控件（Tag、Badge） |
| `@border-radius-md` | **6px** | **标准控件**（Button、Input、Card） |
| `@border-radius-lg` | 8px | 大容器（Modal、Drawer） |

**已删除 `@border-radius-xl: 12px`**：

- 桌面端 12px 圆角偏大，会破坏信息密度
- 开发者极易在所有地方贴 12px 圆角（"看起来更现代"），破坏体系简洁性
- 3 档（4 / 6 / 8）足够覆盖全部场景

`@border-radius-full: 9999px` 仅用于尺寸钳制的节点（Switch thumb、Badge）。

---

## 8. 阴影系统

极弱阴影，参考 JetBrains New UI，不用 Material Design 浮层阴影。

| 级别 | 实现 | 用途 |
|------|------|------|
| sm | `dropshadow(gaussian, shadow-color, 4, 0, 0, 1)` | Tooltip、小弹层 |
| md | `dropshadow(gaussian, shadow-color, 8, 0, 0, 2)` | ComboBox 弹层、Popover |
| lg | `dropshadow(gaussian, shadow-color, 16, 0, 0, 4)` | Modal、Drawer |

---

## 9. 图标系统

统一采用四档尺寸：**16px / 18px / 20px / 24px**

**绝对禁止**：14 / 17 / 19 / 21 / 22 / 23 / 28 等奇数或中间尺寸。

当前图标库：Ikonli（AntDesignIconsOutlined / AntDesignIconsFilled）

---

## 10. 色彩体系

### 10.1 色阶结构

每个语义色定义 0-9 色阶（0 最浅 → 9 最深），通过语义变量映射到具体用途：

| 语义变量 | 色阶索引 | 用途 |
|----------|----------|------|
| `*-emphasis` | 5 | 主色（按钮填充、链接） |
| `*-hover` | 0 | Hover 浅底色 |
| `*-active` | 6 | 按压深色 |
| `*-muted` | 2 | 边框、图标辅助色 |
| `*-subtle` | 0 | 背景底色 |

### 10.2 四组语义色

| 色组 | 变量前缀 | 用途 | Light 主色 | Dark 主色 |
|------|----------|------|-----------|-----------|
| Accent（蓝） | `@color-accent-*` | 主操作、链接、选中 | `#1677ff` | `#1668dc` |
| Success（绿） | `@color-success-*` | 成功、通过 | `#52c41a` | `#49aa19` |
| Warning（橙） | `@color-warning-*` | 警告、注意 | `#faad14` | `#d87a16` |
| Danger（红） | `@color-danger-*` | 错误、危险、删除 | `#f5222d` | `#d84a45` |

### 10.3 中性色（base 色阶 0-10）

| 用途 | Light | Dark |
|------|-------|------|
| 页面底色 `@color-bg-default` | `#ffffff` | `#0d1117` |
| 次级底色 `@color-bg-subtle` | `#f6f8fa` | `#161b22` |
| 嵌套底色 `@color-bg-inset` | `#f3f4f6` | `#010409` |
| 弹层底色 `@color-bg-overlay` | `#ffffff` | `#161b22` |

### 10.4 前景色

| 用途 | Light | Dark |
|------|-------|------|
| 主文字 `@color-fg-default` | `rgba(0,0,0,0.88)` | `rgba(255,255,255,0.85)` |
| 次级文字 `@color-fg-muted` | `rgba(0,0,0,0.65)` | `rgba(255,255,255,0.65)` |
| 辅助文字 `@color-fg-subtle` | `rgba(0,0,0,0.45)` | `rgba(255,255,255,0.45)` |
| 反色文字 `@color-fg-on-emphasis` | `#ffffff` | `#ffffff` |

### 10.5 边框色

| 用途 | Light | Dark |
|------|-------|------|
| 默认边框 `@color-border-default` | `#d9d9d9` | `#30363d` |
| 弱化边框 `@color-border-muted` | `#f0f0f0` | `#21262d` |
| 极淡边框 `@color-border-subtle` | `rgba(0,0,0,0.06)` | `rgba(255,255,255,0.1)` |

### 10.6 阴影色

| 用途 | Light | Dark |
|------|-------|------|
| `@color-shadow-default` | `rgba(0,0,0,0.15)` | `rgba(0,0,0,0.4)` |

---

## 11. 控件规范速查

### 11.1 输入类控件（md 32 / 紧凑 28）

走 `@control-height` + `@ctrl-padding-y/x` token：

InputAnt（TextField）、PasswordAnt、ComboBoxAnt、DatePickerAnt、TimePickerAnt、ColorPickerAnt、ChoiceBoxAnt、SpinnerAnt、InputNumberAnt、AutoCompleteAnt、CascaderAnt、TreeSelectAnt、MentionsAnt、TextAreaAnt

### 11.2 按钮类控件（md 32 / 紧凑 28 / lg 40）

走 `@control-height` + `@btn-padding-y/x` token：

ButtonAnt、MenuButtonAnt、SplitMenuButtonAnt、ToggleButtonAnt

Large 档（40px / 紧凑 36px）仅用于：开始 / 停止 / 部署 / 导出 / 执行

### 11.3 选择类控件（xs 22）

| 控件 | 高度 | Token | 说明 |
|------|------|-------|------|
| CheckBoxAnt | 22px | 原生 | 勾选框 + 文字 |
| RadioAnt | 22px | 原生 | 单选圆点 + 文字 |
| SwitchAnt | 22px | `@switch-padding: 2px` | toggle 轨道 |
| RateAnt | 20px 星 | 原生 | 评分星星（独立尺寸） |
| SliderAnt | track 4px | 原生 | 滑块轨道（独立尺寸） |

### 11.4 文本与标签

| 控件 | 规格 | Token | 说明 |
|------|------|-------|------|
| LabelAnt | 14px 字号 | `@font-size-md` | 文本标签 |
| HyperlinkAnt | sm 28px | 文字型控件 | 链接 |
| TagAnt | sm/md/lg 三档 | `@border-radius-sm` | 标签/标记 |
| BadgeAnt | 圆点/数字 | `@border-radius-full` | 尺寸钳制节点 |
| SelectableTextAnt | 14px 字号 | — | 可选中文本 |
| CodeBlockAnt | 等宽字体 | `@font-family-code` | 代码块 |

### 11.5 数据展示

| 控件 | 默认 | 紧凑 | Token | 说明 |
|------|------|------|-------|------|
| TableAnt Header | **32px** | 28px | `@table-header-height` | 表头容器高，与行高**统一** |
| TableAnt Row | **32px** | 28px | `@table-row-height` | min-height |
| TreeTableAnt | 同 Table | 同 Table | 同上 | 树表格 |
| TreeAnt Cell | sm 28px | sm 24px | `@tree-cell-padding-y` (`@spacing-sm`) | 随紧凑联动 |
| ListAnt Cell | sm 28px | sm 24px | `@list-cell-padding-y` (`@spacing-sm`) | 随紧凑联动 |
| DescriptionsAnt | — | — | `@spacing-md` / `@spacing-lg` | 描述列表 |
| StatisticAnt | — | — | — | 统计数值 |
| TimelineAnt | — | — | — | 时间线 |
| CalendarAnt | — | — | — | 日历面板 |
| SkeletonAnt | — | — | — | 骨架屏占位 |
| EmptyAnt | — | — | — | 空状态占位 |
| ResultAnt | — | — | — | 结果反馈页 |

**表头与行高统一 32px**：IDEA / DBeaver / DataGrip 等成熟软件均采用 Header=Row。34/32 这种 2px 差异视觉上几乎看不出，但会破坏体系简洁性。

### 11.6 导航

| 控件 | 规格 | Token | 说明 |
|------|------|-------|------|
| TabsAnt Header | — | `@tab-padding-y` (`@spacing-sm`) + `@tab-padding-x` (`@spacing-md`) | 参考 IDEA |
| MenuAnt (inline) | sm 28px/行 | `@menu-inline-padding-y` | 侧栏菜单 |
| MenuAnt (dropdown) | — | `@menu-item-padding-y` (`@spacing-sm`) | 下拉菜单项 |
| MenuBarAnt | sm 28px | `@menu-bar-padding-y: 4px` | 顶部菜单栏 |
| BreadcrumbAnt | sm 28px | — | 面包屑 |
| StepsAnt | — | — | 步骤条 |
| AnchorAnt | — | — | 锚点导航 |
| PaginationAnt | — | `@pagination-padding-y` (`@spacing-xs`) | 分页 |
| SegmentedAnt | — | — | 分段控件 |

### 11.7 反馈与弹层

| 控件 | 规格 | Token | 说明 |
|------|------|-------|------|
| AlertAnt | — | `@alert-padding-y` (`@spacing-md`) | 警告提示 |
| ModalAnt | — | `@dialog-padding` (`@spacing-lg`) | 模态对话框 |
| DrawerAnt | — | `@dialog-padding` (`@spacing-lg`) | 抽屉 |
| TooltipAnt | — | `@tooltip-padding-y` (`@spacing-sm`) | 提示气泡，sm 阴影 |
| PopoverAnt | — | — | 气泡卡片 |
| PopconfirmAnt | — | — | 气泡确认框 |
| MessageAnt | — | — | 全局消息提示 |
| NotificationAnt | — | — | 通知提醒框 |
| SpinAnt | — | — | 加载中 |
| ProgressAnt | — | — | 进度条 |

### 11.8 表单与容器

| 控件 | 规格 | Token | 说明 |
|------|------|-------|------|
| FormAnt | — | `@form-item-padding-y` (`@spacing-sm`) | 表单布局 |
| GroupBoxAnt (Card) | — | `@card-padding` (`@spacing-md`) | 卡片容器 |
| CollapseAnt / AccordionAnt | — | `@accordion-title-padding-y` (`@spacing-md`) | 折叠面板 |
| SurfaceAnt | — | — | 表面容器 |
| ResizablePanelAnt | — | — | 可调整面板 |

### 11.9 媒体与杂项

| 控件 | 规格 | 说明 |
|------|------|------|
| AvatarAnt | — | 头像 |
| ImageAnt | — | 图片预览 |
| CarouselAnt | — | 轮播图 |
| QRCodeAnt | — | 二维码 |
| WatermarkAnt | — | 水印 |
| FloatButtonAnt | — | 悬浮按钮 |
| BackTopAnt | — | 回到顶部 |
| UploadAnt | — | 文件上传 |
| TransferAnt | — | 穿梭框 |
| IconAnt | 16/18/20/24px | 图标 |
| CanvasAnt | — | 画布 |

### 11.10 布局容器

| 控件 | 规格 | Token | 说明 |
|------|------|-------|------|
| ToolBarAnt | — | `@toolbar-padding-y` (`@spacing-sm`) / `@toolbar-padding-x` (`@spacing-md`) | 内部按钮走 sm 档（28px） |
| ScrollBar | **8px / hover 10px** | `@scrollbar-size` / `@scrollbar-size-hover` | JetBrains 风格，紧贴手感 |
| ScrollPane | — | — | 滚动容器 |
| SplitPane Divider | 2px padding | `@split-divider-padding` | 分割面板 |

---

## 12. 主题与密度

### 12.1 主题（Theme）维度

| 维度 | 取值 |
|------|------|
| 风格（Family） | Ant / Mui |
| 模式（Mode） | Light / Dark |

通过 ThemeManager 切换：`ThemeManager.getInstance().applyTheme(new LightTheme())` / `MuiDarkTheme` 等。

### 12.2 密度（Density）维度

| 取值 | 用途 |
|------|------|
| `Density.DEFAULT` | 标准密度（普通数据展示） |
| `Density.COMPACT` | 高密度（专业工具、长时间使用） |

通过 ThemeManager 切换：`ThemeManager.getInstance().setDensity(Density.COMPACT)`。

### 12.3 二维矩阵

最终 ThemeManager 维护 **Theme × Density = 2 维状态机**：

| | Light | Dark |
|---|-------|------|
| **Default** | AntLight | AntDark |
| **Compact** | AntLight + COMPACT | AntDark + COMPACT |

紧凑模式从独立主题类（LightCompact / DarkCompact）**降级为密度属性**：

| 维度 | 旧设计 | 新设计 |
|------|--------|--------|
| 主题类数量 | 2 × 2 × 2 = 8（Ant/Mui × Light/Dark × Default/Compact） | 2 × 2 = 4（Ant/Mui × Light/Dark） |
| 紧凑控制 | 独立 *CompactTheme 类、*Compact.less 文件 | `ThemeManager.setDensity(Density.COMPACT)` 切换 |
| Token 覆盖 | theme-*-compact.less 覆盖所有 token | ThemeManager 注入 CSS 变量覆盖 |

### 12.4 主题现状

当前 LESS 已编译主题 CSS：

- **4 套 ThemeManager 主题**：theme-light / theme-dark / theme-light-compact / theme-dark-compact
- **2 套独立加载主题**：theme-shadcn / theme-custom（设计探索，非密度控制）

新设计**不强制清理** shadcn / custom 两套独立主题。

---

## 13. CSS 编写约束

* 调 JavaFX 原生控件（TableView / TreeView / ComboBox 弹层等）样式时，**先读 AtlantaFX 源码**，再改 LESS，不凭印象猜选择器层级
* 所有颜色走 `@color-*` token，禁止硬编码色值
* 所有尺寸走 `@spacing-*` / `@control-height-*` token，禁止硬编码 px（Density 才能联动）
* 阴影用 `-fx-effect: dropshadow(...)`，禁止 CSS `box-shadow`
* 伪类映射：Web `:active` → JavaFX `:pressed`/`:armed`；Web `:focus` → JavaFX `:focused`

---

## 14. 最终风格目标

**50% JetBrains New UI + 25% Qt Widgets + 10% 数据软件 + 5% Visual Studio + 5% Windows Terminal / Task Manager + 5% Windows 11 / macOS**

围绕 5 组数字（12/14/16/20 + 4/6/8 + 22/28/32/40 + 4/8/12/16/24 + 16/18/20/24）+ 1 个 Density 维度（Default / Compact）展开。

专业、紧凑、高密度、长时间使用不疲劳的 JavaFX Desktop Design System。风格锚点是专业桌面工具（IDE / 数据库工具 / 监控系统 / 交易软件），不是 Web 后台管理系统。

---

## 15. 落地路线图（Density 重构 + 数字体系同步）

> 本节是把 §3 / §12 的设计目标**真正落到代码**的执行计划。每一步都给出**改哪些文件、怎么改、怎么验**。

### 15.1 阶段总览

| 阶段 | 目标 | 状态 |
|------|------|------|
| **P1 数字体系同步** | variables-base.less 与 5 组数字对齐 | ✅ 已完成 |
| **P2 Density 拆维** | ThemeManager 从 3D 状态机降为 2D + 注入式 | ⬜ 待实施 |
| **P3 主题类瘦身** | 8 个 `*Theme` 类 → 4 个，删除 4 个 `*CompactTheme` | ⬜ 待实施 |
| **P4 LESS 收敛** | 4 个 `theme-*-compact.less` 由「独立主题」降级为「变量覆盖源」 | ⬜ 待实施 |
| **P5 Demo 验证** | jfxium-demo 跑通 Default ↔ Compact 热切换 | ⬜ 待实施 |

---

### 15.2 P1 数字体系同步（✅ 已完成）

落地文件清单（共 17 个）：

| 文件 | 改动 |
|------|------|
| `css/less/variables-base.less` | 删 `@font-size-xs` / `@border-radius-xl`；加 `@control-height-xs: 22px` / `@scrollbar-size-hover: 10px`；`@control-height-sm` 24→28；`@table-header-height` / `@table-row-height` 48→`@control-height` |
| `css/less/theme-light-compact.less` | `sm` 20→24；`table` 36→28 |
| `css/less/theme-dark-compact.less` | 同上 |
| `css/less/theme-mui-compact.less` | 同上 + 删 `@border-radius-xl` + `.badge-count` 字号 sm |
| `css/less/theme-mui-dark-compact.less` | 同上 |
| `css/less/theme-mui.less` / `theme-mui-dark.less` | 删 `@border-radius-xl` + `.badge-count` 字号 sm |
| `css/less/theme-shadcn.less` | 删 `@border-radius-xl` |
| `css/less/components/_badge.less` | `.badge` 字号 sm |
| `css/less/components/_badge-enhance.less` | 2 处 `font-size-xs` → sm |
| `css/less/components/_codeblock.less` | 2 处同上 |
| `css/less/components/_sizes.less` | `.tag.small` 字号 sm |
| `css/less/components/_datepicker-popup.less` | `.week-number-cell` 字号 sm |
| `css/less/components/_tier4.less` | `.jfx-descriptions-small` 字号 sm |
| `css/less/components/_scrollbar.less` | 加 `.scroll-bar:hover` 走 `@scrollbar-size-hover` |

**验证**：
- `./mvnw generate-resources -pl jfxium -DskipTests` → 11 主题 CSS 全部成功编译
- `./mvnw compile -pl jfxium -DskipTests` → 148 个 Java 文件全部成功编译
- `grep -r "font-size-xs" jfxium/src/main/resources/org/openkawu/jfxium/css/less/` → **0 匹配**
- `grep -r "border-radius-xl" jfxium/src/main/resources/org/openkawu/jfxium/css/less/` → **0 匹配**

---

### 15.3 P2 Density 拆维（⬜ 待实施）

**目标**：把 Density 从「boolean compact flag」改为「枚举状态 + CSS 变量注入」。

#### 15.3.1 `ThemeManager` 字段调整

```java
// 当前（3D 状态机：family × dark × compact boolean）
private boolean compact = false;
public void setCompactDensity(boolean compact) { this.compact = compact; ... }
public boolean isCompact() { return compact; }

// 重构后（2D 状态机：family × dark + density 独立维度）
private ThemeDensity density = ThemeDensity.DEFAULT;
public void setDensity(ThemeDensity density) { this.density = density; ... }
public ThemeDensity getDensity() { return density; }

// 兼容旧 API（标记 @Deprecated，2 个版本后删除）
@Deprecated
public void setCompactDensity(boolean compact) {
    setDensity(compact ? ThemeDensity.COMPACT : ThemeDensity.DEFAULT);
}
@Deprecated
public boolean isCompact() { return density == ThemeDensity.COMPACT; }
@Deprecated
public void toggleCompact() { setDensity(isCompact() ? DEFAULT : COMPACT); }
```

#### 15.3.2 移除 `applyComposite()` 中的 compact 分支

```java
// 当前：compact 是主题类的决定因素之一
private void applyComposite() {
    Theme theme = switch (currentFamily) {
        case ANT_DESIGN -> dark
                ? (compact ? new DarkCompactTheme() : new DarkTheme())
                : (compact ? new LightCompactTheme() : new LightTheme());
        case MUI -> dark
                ? (compact ? new MuiDarkCompactTheme() : new MuiDarkTheme())
                : (compact ? new MuiCompactTheme() : new MuiTheme());
    };
    applyTheme(theme);
}

// 重构后：compact 由 CSS 变量注入控制，不参与 Theme 类选择
private void applyComposite() {
    Theme theme = switch (currentFamily) {
        case ANT_DESIGN -> dark ? new DarkTheme() : new LightTheme();
        case MUI        -> dark ? new MuiDarkTheme() : new MuiTheme();
    };
    applyTheme(theme);  // 这一步内部会重新注入 density stylesheet
}
```

#### 15.3.3 新增 density stylesheet 注入机制

参考现有 `applyPrimaryColorToAll()` 的 data-URI 套路（M19.51），新增 `applyDensityToAll()`：

```java
private String densityStylesheet;  // 上一次注入的 density data-URI

private void applyDensityToAll() {
    // 1. 生成 density CSS 规则
    String css = buildDensityCss(density);

    // 2. base64 打包成 data-URI
    String dataUri = "data:text/css;base64,"
            + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));

    // 3. 替换每个 scene 上一次注入的 density stylesheet
    for (Scene scene : registeredScenes) {
        if (densityStylesheet != null) {
            scene.getStylesheets().remove(densityStylesheet);
        }
        scene.getStylesheets().add(dataUri);
    }
    densityStylesheet = dataUri;
}

private String buildDensityCss(ThemeDensity d) {
    return switch (d) {
        case DEFAULT -> "";  // 默认密度不注入，走 LESS 默认值
        case COMPACT -> """
                .root {
                    -control-height: 28px;
                    -control-height-sm: 24px;
                    -control-height-lg: 36px;
                    -control-height-xs: 18px;
                    -spacing-xs: 2px;
                    -spacing-sm: 6px;
                    -spacing-md: 8px;
                    -spacing-lg: 12px;
                    -spacing-xl: 16px;
                    -table-header-height: 28px;
                    -table-row-height: 28px;
                }
                """;
    };
}
```

#### 15.3.4 关键决策点

| 决策 | 选择 | 理由 |
|------|------|------|
| Density 注入位置 | `.root` 上覆盖 `-fx-` 变量 | 复用 M19.51 的 data-URI 套路，与 accent 注入同位置 |
| 默认密度 | 不注入 stylesheet（`""`） | 避免给「什么都不做」的状态加一份空文件 |
| CSS 变量名规范 | `-control-height` / `-spacing-sm`（无 `fx-` 前缀） | 与 `variables-base.less` 的 LESS 变量名保持一致，便于一一映射 |
| 旧 API 兼容 | 保留 `setCompactDensity(boolean)` / `isCompact()` / `toggleCompact()` 3 个方法 | 标记 `@Deprecated` 不删除，避免破坏现有 demo |

---

### 15.4 P3 主题类瘦身（⬜ 待实施）

**目标**：8 个 `*Theme` 类 → 4 个。

#### 15.4.1 删除清单

| 删除类 | 路径 |
|--------|------|
| `LightCompactTheme` | `core/theme/LightCompactTheme.java` |
| `DarkCompactTheme` | `core/theme/DarkCompactTheme.java` |
| `MuiCompactTheme` | `core/theme/MuiCompactTheme.java` |
| `MuiDarkCompactTheme` | `core/theme/MuiDarkCompactTheme.java` |

#### 15.4.2 保留类

| 保留类 | 路径 | 对应 CSS |
|--------|------|----------|
| `LightTheme` | `core/theme/LightTheme.java` | `theme-light.css` |
| `DarkTheme` | `core/theme/DarkTheme.java` | `theme-dark.css` |
| `MuiTheme` | `core/theme/MuiTheme.java` | `theme-mui.css` |
| `MuiDarkTheme` | `core/theme/MuiDarkTheme.java` | `theme-mui-dark.css` |

#### 15.4.3 残留引用清理

`grep -rn "CompactTheme" jfxium-demo/src` 找出 demo 里的所有引用，逐个替换为：

```java
// 旧
ThemeManager.getInstance().applyTheme(new LightCompactTheme());
// 新
ThemeManager.getInstance().applyTheme(new LightTheme());
ThemeManager.getInstance().setDensity(ThemeDensity.COMPACT);
```

---

### 15.5 P4 LESS 收敛（⬜ 待实施）

**目标**：4 个 `theme-*-compact.less` 不再作为「独立主题」编译，而是作为「变量覆盖源」被 P2 的 `buildDensityCss()` 替代。

#### 15.5.1 短期方案（推荐先做）

**保留** 4 个 `theme-*-compact.less` 文件原状（避免破坏 P1 已验证的编译），但**修改 `build-themes.js` 编译脚本**：

- `theme-light-compact.less` / `theme-dark-compact.less` / `theme-mui-compact.less` / `theme-mui-dark-compact.less` 这 4 个文件**继续编译成 CSS 兜底**
- 但 `ThemeManager` 不再 `applyTheme(new LightCompactTheme())`，而是统一 `applyTheme(new LightTheme()) + setDensity(COMPACT)`
- 兜底 CSS 仅作为「万一 P2 注入失败时降级」的安全网

#### 15.5.2 长期方案（P2 稳定后做）

等 P2 的 `applyDensityToAll()` 经过 1~2 个版本验证稳定后：

1. 删除 4 个 `theme-*-compact.less` 源文件
2. 删除 4 个对应的 `theme-*-compact.css` 编译产物
3. 修改 `build-themes.js` 不再编译这 4 个文件
4. 单一注入源：`ThemeManager.buildDensityCss(COMPACT)` 字符串

#### 15.5.3 `build-themes.js` 同步

```js
// 当前
const themes = ['light', 'dark', 'light-compact', 'dark-compact',
                'shadcn', 'custom'];

// 短期保留 compact 兜底
// 长期改为
const themes = ['light', 'dark', 'shadcn', 'custom'];
```

---

### 15.6 P5 Demo 验证（⬜ 待实施）

**目标**：在 `jfxium-demo` 里跑通 Default ↔ Compact 的热切换。

#### 15.6.1 Demo 改动

```java
// jfxium-demo/src/main/java/.../ShowcaseDemo.java
// 在顶部加一个 Toggle Density 按钮
Button densityBtn = ButtonAnt.create()
    .label("切换密度: Default / Compact")
    .onAction(e -> {
        ThemeDensity d = ThemeManager.getInstance().getDensity();
        ThemeManager.getInstance().setDensity(
            d == ThemeDensity.DEFAULT ? ThemeDensity.COMPACT : ThemeDensity.DEFAULT);
        densityBtn.label("当前: " + ThemeManager.getInstance().getDensity());
    })
    .build();
```

#### 15.6.2 验证清单

- [ ] 启动 demo，所有 Table / Form / Tree 走 Default（32px 行高）
- [ ] 点击切换密度，所有行高立即变为 28px（无残留 default 样式）
- [ ] 切换暗色模式后密度不丢
- [ ] 切换 family（Ant → MUI）后密度不丢
- [ ] 切换主色（accent）后密度不丢
- [ ] 切回 Default 密度，所有 token 恢复到 LESS 默认值

#### 15.6.3 验收截图

在 `docs/cn/主题系统.md` 中追加「密度切换」一节，配 2 张截图：
- Default 密度下的 Table（行高 32px）
- Compact 密度下的 Table（行高 28px）

---

### 15.7 风险与回退

| 风险 | 触发条件 | 回退方案 |
|------|----------|----------|
| P2 注入导致 popup / TableView 的 `LookedUpColor` 解析失败 | 复现 M19.51 的 "Could not resolve" 问题 | 把 `applyDensityToAll()` 的 `data-URI` 改为「先于 scene 启动时注入」而非运行时 |
| P3 删除 `*CompactTheme` 类导致 demo 编译失败 | 漏改某处引用 | 在 P3 之前先 `grep -rn "CompactTheme" jfxium-demo/src` 全部替换为新 API |
| P4 删除 `theme-*-compact.less` 后 CI 报缺文件 | `build-themes.js` 漏改 | 短期方案先不动 LESS，只删 Java 类 |
| Density 切换有视觉闪烁 | data-URI 注入是异步的 | 改为同步：先 `scene.getStylesheets().remove(0, N)` 再 add |

---

### 15.8 工期估算

| 阶段 | 代码改动量 | 验证时长 | 风险 |
|------|----------|----------|------|
| P1 | 17 个 LESS 文件 | 1 轮 build | 已完成 |
| P2 | 1 个 Java 文件 + 1 个新方法 | 3 个 demo 场景 | 中（注入机制） |
| P3 | 删除 4 个类 + grep 清理 demo | 1 轮 build + demo 跑通 | 低 |
| P4 | 1 个 `build-themes.js` + 4 个文件删除 | 1 轮 build | 低 |
| P5 | demo 1 处 + 截图 | 手动验证 | 低 |
| **合计** | ~25 个文件 | **约 1~2 个工作日** | 中 |
