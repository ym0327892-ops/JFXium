# JavaFX Desktop Design System

## 设计目标

面向 Windows / macOS / Linux 桌面软件的 JavaFX UI 设计系统。

核心目标：

* 高信息密度
* 长时间办公使用
* 数据录入效率优先
* 专业工具软件风格（IDE / 数据库工具 / 监控系统 / 交易软件）

---

## 参考对象（优先级从高到低）

| 级别 | 对象 | 重点 |
|------|------|------|
| 1 | JetBrains IDE（IDEA / DataGrip / Rider） | 控件尺寸、工具栏、Tab、Tree、Table、间距 |
| 2 | Qt 6 Widgets（QPushButton / QLineEdit / QTableView） | 控件风格，不参考 Qt Quick Mobile |
| 3 | Windows 11（File Explorer / Task Manager / Visual Studio） | 控件高度、间距、圆角、状态栏 |
| 4 | 专业桌面软件（DBeaver / Navicat / Wireshark / Beyond Compare） | 数据表格密度、工具栏、菜单 |

不参考 Web UI 框架（Ant Design Web / Element Plus / Material Design Web / Naive UI / Bootstrap）的尺寸体系——控件过高、信息密度过低，不适合桌面软件。

---

## 字体系统

### 字号梯度

| Token | 值 | 用途 |
|-------|------|------|
| `@font-size-xs` | 10px | 角标、极小辅助文字 |
| `@font-size-sm` | 12px | 辅助文字、标签、Tooltip |
| `@font-size-md` | **14px** | **全局基准字号**，正文、控件、菜单 |
| `@font-size-lg` | 16px | 小标题、Section Header |
| `@font-size-xl` | 20px | 页面标题 |

### 字体族

```
-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif
```

等宽字体：`SFMono-Regular, Consolas, 'Liberation Mono', Menlo, Courier, monospace`

### 字重

| 用途 | 字重 |
|------|------|
| 按钮、标题 | 500（medium） |
| 正文、控件 | 400（regular，默认） |

---

## 色彩体系

### 色阶结构

每个语义色定义 0-9 色阶（0 最浅 → 9 最深），通过语义变量映射到具体用途：

| 语义变量 | 色阶索引 | 用途 |
|----------|----------|------|
| `*-emphasis` | 5 | 主色（按钮填充、链接） |
| `*-hover` | 0 | Hover 浅底色 |
| `*-active` | 6 | 按压深色 |
| `*-muted` | 2 | 边框、图标辅助色 |
| `*-subtle` | 0 | 背景底色 |

### 四组语义色

| 色组 | 变量前缀 | 用途 | Light 主色 | Dark 主色 |
|------|----------|------|-----------|-----------|
| Accent（蓝） | `@color-accent-*` | 主操作、链接、选中 | `#1677ff` | `#1668dc` |
| Success（绿） | `@color-success-*` | 成功、通过 | `#52c41a` | `#49aa19` |
| Warning（橙） | `@color-warning-*` | 警告、注意 | `#faad14` | `#d87a16` |
| Danger（红） | `@color-danger-*` | 错误、危险、删除 | `#f5222d` | `#d84a45` |

### 中性色（base 色阶 0-10）

| 用途 | Light | Dark |
|------|-------|------|
| 页面底色 `@color-bg-default` | `#ffffff` | `#0d1117` |
| 次级底色 `@color-bg-subtle` | `#f6f8fa` | `#161b22` |
| 嵌套底色 `@color-bg-inset` | `#f3f4f6` | `#010409` |
| 弹层底色 `@color-bg-overlay` | `#ffffff` | `#161b22` |

### 前景色

| 用途 | Light | Dark |
|------|-------|------|
| 主文字 `@color-fg-default` | `rgba(0,0,0,0.88)` | `rgba(255,255,255,0.85)` |
| 次级文字 `@color-fg-muted` | `rgba(0,0,0,0.65)` | `rgba(255,255,255,0.65)` |
| 辅助文字 `@color-fg-subtle` | `rgba(0,0,0,0.45)` | `rgba(255,255,255,0.45)` |
| 反色文字 `@color-fg-on-emphasis` | `#ffffff` | `#ffffff` |

### 边框色

| 用途 | Light | Dark |
|------|-------|------|
| 默认边框 `@color-border-default` | `#d9d9d9` | `#30363d` |
| 弱化边框 `@color-border-muted` | `#f0f0f0` | `#21262d` |
| 极淡边框 `@color-border-subtle` | `rgba(0,0,0,0.06)` | `rgba(255,255,255,0.1)` |

### 阴影色

| 用途 | Light | Dark |
|------|-------|------|
| `@color-shadow-default` | `rgba(0,0,0,0.15)` | `rgba(0,0,0,0.4)` |

---

## 尺寸体系

### 控件高度（两档模式）

| 档位 | 默认模式 | 紧凑模式 | 用途 |
|------|----------|----------|------|
| Small | 24px | 20px | 工具栏按钮、紧凑型输入 |
| Default | 32px | 28px | **标准控件**（Button / Input / ComboBox / DatePicker / Spinner） |
| Large | 40px | 36px | 大操作按钮（开始 / 停止 / 部署 / 导出） |

Token：`@control-height` / `@control-height-sm` / `@control-height-lg`

禁止出现 31 / 33 / 37 / 42 等随意尺寸。

### 特殊控件高度

| 控件 | 高度 | 说明 |
|------|------|------|
| CheckBox | 22px | 原生控件尺寸 |
| RadioButton | 22px | 原生控件尺寸 |
| Switch | 22px | toggle 轨道高度 |
| Hyperlink | 24px | 文字型控件 |
| StatusBar | 26px | 底部状态栏 |
| Breadcrumb | 28px | 面包屑导航 |

---

## 间距系统

五档梯度，默认 / 紧凑两档联动：

| Token | 默认 | 紧凑 | 用途 |
|-------|------|------|------|
| `@spacing-xs` | 4px | 2px | 极小间距（图标与文字间） |
| `@spacing-sm` | 8px | 6px | 小间距（cell padding、菜单项） |
| `@spacing-md` | 12px | 8px | 中间距（表单、卡片） |
| `@spacing-lg` | 16px | 12px | 大间距（对话框、区块间） |
| `@spacing-xl` | 24px | 16px | 超大间距（页面级留白） |

禁止任意间距。

---

## 圆角系统

| Token | 值 | 用途 |
|-------|------|------|
| `@border-radius-sm` | 4px | 小控件（Tag、Badge） |
| `@border-radius-md` | **6px** | **标准控件**（Button、Input、Card） |
| `@border-radius-lg` | 8px | 大容器（Modal、Drawer） |
| `@border-radius-xl` | 12px | 特殊场景 |

桌面软件禁止超过 10px 圆角（`@border-radius-full: 9999px` 仅用于尺寸钳制的节点如 Switch thumb、Badge）。

---

## 阴影系统

极弱阴影，参考 JetBrains New UI，不用 Material Design 浮层阴影。

| 级别 | 实现 | 用途 |
|------|------|------|
| sm | `dropshadow(gaussian, shadow-color, 4, 0, 0, 1)` | Tooltip、小弹层 |
| md | `dropshadow(gaussian, shadow-color, 8, 0, 0, 2)` | ComboBox 弹层、Popover |
| lg | `dropshadow(gaussian, shadow-color, 16, 0, 0, 4)` | Modal、Drawer |

---

## 图标系统

统一采用四档尺寸：16px / 18px / 20px / 24px

禁止 17 / 19 / 21 / 23 等奇数尺寸。

当前图标库：Ikonli（AntDesignIconsOutlined / AntDesignIconsFilled）

---

## 控件规范速查

### 输入类控件（32px / 紧凑 28px）

走 `@control-height` + `@ctrl-padding-y/x` token：

InputAnt（TextField）、PasswordAnt、ComboBoxAnt、DatePickerAnt、TimePickerAnt、ColorPickerAnt、ChoiceBoxAnt、SpinnerAnt、InputNumberAnt、AutoCompleteAnt、CascaderAnt、TreeSelectAnt、MentionsAnt、TextAreaAnt

### 按钮类控件（32px / 紧凑 28px）

走 `@control-height` + `@btn-padding-y/x` token：

ButtonAnt、MenuButtonAnt、SplitMenuButtonAnt、ToggleButtonAnt

Large 档（40px / 紧凑 36px）仅用于：开始 / 停止 / 部署 / 导出 / 执行

### 选择类控件

| 控件 | 高度 | Token | 说明 |
|------|------|-------|------|
| CheckBoxAnt | 22px | 原生 | 勾选框 + 文字 |
| RadioAnt | 22px | 原生 | 单选圆点 + 文字 |
| SwitchAnt | 22px | `@switch-padding: 2px` | toggle 轨道 |
| RateAnt | 20px 星 | 原生 | 评分星星 |
| SliderAnt | track 4px | 原生 | 滑块轨道 |

### 文本与标签

| 控件 | 规格 | Token | 说明 |
|------|------|-------|------|
| LabelAnt | 14px 字号 | `@font-size-md` | 文本标签 |
| HyperlinkAnt | 24px | 文字型控件 | 链接 |
| TagAnt | sm/md/lg 三档 | `@border-radius-sm` | 标签/标记 |
| BadgeAnt | 圆点/数字 | `@border-radius-full` | 尺寸钳制节点 |
| SelectableTextAnt | 14px 字号 | — | 可选中文本 |
| CodeBlockAnt | 等宽字体 | `@font-family-code` | 代码块 |

### 数据展示

| 控件 | 默认 | 紧凑 | Token | 说明 |
|------|------|------|-------|------|
| TableAnt Header | 48px | 36px | `@table-header-height` | 表头容器高 |
| TableAnt Row | 48px | 36px | `@table-row-height` | min-height |
| TreeTableAnt | 同 Table | 同 Table | 同上 | 树表格 |
| TreeAnt Cell | — | — | `@tree-cell-padding-y` (`@spacing-sm`) | 随紧凑联动 |
| ListAnt Cell | — | — | `@list-cell-padding-y` (`@spacing-sm`) | 随紧凑联动 |
| DescriptionsAnt | — | — | `@spacing-md` / `@spacing-lg` | 描述列表 |
| StatisticAnt | — | — | — | 统计数值 |
| TimelineAnt | — | — | — | 时间线 |
| CalendarAnt | — | — | — | 日历面板 |
| SkeletonAnt | — | — | — | 骨架屏占位 |
| EmptyAnt | — | — | — | 空状态占位 |
| ResultAnt | — | — | — | 结果反馈页 |

### 导航

| 控件 | 规格 | Token | 说明 |
|------|------|-------|------|
| TabsAnt Header | — | `@tab-padding-y` (`@spacing-sm`) + `@tab-padding-x` (`@spacing-md`) | 参考 IDEA |
| MenuAnt (inline) | 30px/行 | `@menu-inline-padding-y: 6px` | 侧栏菜单 |
| MenuAnt (dropdown) | — | `@menu-item-padding-y` (`@spacing-sm`) | 下拉菜单项 |
| MenuBarAnt | 28-30px | `@menu-bar-padding-y: 4px` | 顶部菜单栏 |
| BreadcrumbAnt | 28px | — | 面包屑 |
| StepsAnt | — | — | 步骤条 |
| AnchorAnt | — | — | 锚点导航 |
| PaginationAnt | — | `@pagination-padding-y` (`@spacing-xs`) | 分页 |
| SegmentedAnt | — | — | 分段控件 |

### 反馈与弹层

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

### 表单与容器

| 控件 | 规格 | Token | 说明 |
|------|------|-------|------|
| FormAnt | — | `@form-item-padding-y` (`@spacing-sm`) | 表单布局 |
| GroupBoxAnt (Card) | — | `@card-padding` (`@spacing-md`) | 卡片容器 |
| CollapseAnt / AccordionAnt | — | `@accordion-title-padding-y` (`@spacing-md`) | 折叠面板 |
| SurfaceAnt | — | — | 表面容器 |
| ResizablePanelAnt | — | — | 可调整面板 |

### 媒体与杂项

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

### 布局容器

| 控件 | 规格 | Token | 说明 |
|------|------|-------|------|
| ToolBarAnt | — | `@toolbar-padding-y` (`@spacing-sm`) / `@toolbar-padding-x` (`@spacing-md`) | 内部按钮走 sm 档 |
| ScrollBar | 8px 宽 | `@scrollbar-size` | 滚动条 |
| ScrollPane | — | — | 滚动容器 |
| SplitPane Divider | 2px padding | `@split-divider-padding` | 分割面板 |

---

## 主题

必须同时支持 Light Theme 和 Dark Theme，通过 ThemeManager 切换。

色彩体系通过 LESS 变量文件分治：`variables.less`（Light）/ `variables-dark.less`（Dark），组件 CSS 共享同一套 `theme-base.less`，只换色不改动布局。

紧凑模式额外提供 Light Compact / Dark Compact 两档。

---

## CSS 编写约束

* 调 JavaFX 原生控件（TableView / TreeView / ComboBox 弹层等）样式时，**先读 AtlantaFX 源码**，再改 LESS，不凭印象猜选择器层级
* 所有颜色走 `@color-*` token，禁止硬编码色值
* 所有尺寸走 `@spacing-*` / `@control-height` token，禁止硬编码 px（紧凑模式才能联动）
* 阴影用 `-fx-effect: dropshadow(...)`，禁止 CSS `box-shadow`
* 伪类映射：Web `:active` → JavaFX `:pressed`/`:armed`；Web `:focus` → JavaFX `:focused`

---

## 最终风格目标

70% JetBrains IDE + 20% Qt Widgets + 10% Windows 11

专业、紧凑、高密度、长时间使用不疲劳的 JavaFX Desktop Design System。
