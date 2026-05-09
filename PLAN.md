# JFXium 项目详细开发计划

> 目标：构建现代化 JavaFX UI 框架，对标 Ant Design 6.x / Material UI / MUI-like Style
> 技术栈：Java 21 + JavaFX 21.0.6 + Maven
> 核心原则：Design Token 驱动、Builder Pattern、完全代码构建 UI、CSS 变量体系

***

## 项目进度跟踪

### ✅ 已完成工作（2025-01-08 更新）

#### 1. CSS 文件驱动的架构重构（参考 AtlantaFX）

- ✅ 创建了 `theme-light.css` 和 `theme-dark.css` 主题文件
- ✅ 实现了三级别 Token 系统：
  - **Base Token**: 颜色梯度（color-base-0 到 color-base-10）
  - **Semantic Token**: 语义化颜色（color-fg-default, color-bg-default 等）
  - **Component Token**: 组件特定样式（button、input、card）
- ✅ 使用 JavaFX looked-up colors（`-color-*`）替代 CSS `var()` 函数
- ✅ 实现了类似 AtlantaFX 的简洁 CSS 类名（如 `primary`, `outlined` 而非 `jfx-button-primary`）

#### 2. Theme API 实现

- ✅ 创建了 `Theme` 接口定义主题契约
- ✅ 实现了 `LightTheme` 和 `DarkTheme` 类
- ✅ 实现了 `ThemeManager` 单例管理主题切换
- ✅ 使用 `Application.setUserAgentStylesheet()` 实现主题切换
- ✅ 支持运行时主题切换（Light ↔ Dark）

#### 3. CSS 类名常量化

- ✅ 创建了 `CssClasses.java` 集中管理 CSS 类名
- ✅ 定义了 Button、Input、Card 的所有样式类名常量
- ✅ 防止拼写错误，提供 IDE 自动补全支持

#### 4. 组件封装（Builder Pattern）

- ✅ **JFXButton**: 支持 6 种类型（DEFAULT, PRIMARY, OUTLINED, DASHED, TEXT, LINK）
- ✅ **JFXButton**: 支持 3 种尺寸（SMALL, DEFAULT, LARGE）
- ✅ **JFXButton**: 支持圆角形状（rounded）
- ✅ **JFXInput**: 支持尺寸、禁用状态
- ✅ **JFXCard**: 支持边框、阴影、悬浮效果
- ✅ 所有组件使用流畅的 Builder Pattern API

#### 5. Playground 应用

- ✅ 创建了 `JFXiumPlayground` 演示应用
- ✅ 实现了 Button 组件的所有变体演示
- ✅ 实现了 Input 组件演示
- ✅ 实现了 Card 组件演示
- ✅ 添加了主题切换按钮（实时切换 Light/Dark）
- ✅ 验证了 CSS 样式在 Light/Dark 主题下正确渲染

#### 6. 关键技术决策

- ✅ 采用 AtlantaFX 的 CSS First 理念
- ✅ 使用 JavaFX looked-up colors 解决 CSS 变量兼容性问题
- ✅ 参考 GitHub Primer 色彩系统设计颜色梯度
- ✅ 简化 CSS 类名，提升易用性

#### 7. 本次更新完成的工作（2025-01-08 第二轮更新）

- ✅ **完善 JFXButton 组件**:
  - 添加图标支持（`icon(Node)` 和 `loadingIcon(Node)`）
  - 添加加载状态（`loading(boolean)`）
  - 添加内容显示位置控制（`contentDisplay(ContentDisplay)`）
  - 优化 CSS 样式：字体加粗、对齐方式、图标间距
- ✅ **优化 CSS 样式，对标 Ant Design 6.x**:
  - 完善按钮交互状态（hover、focused、armed、disabled）
  - 添加按钮图标样式支持
  - 优化尺寸样式（small/large 的圆角适配）
  - 统一 disabled 状态透明度为 0.6
- ✅ **添加 JFXModal 对话框组件**:
  - 支持标题、内容、自定义尺寸
  - 支持确认/取消按钮自定义文本和类型
  - 支持点击遮罩关闭（maskClosable）
  - 支持关闭按钮和回调函数（onOk、onCancel、onClose）
  - 内置淡入淡出 + 缩放动画效果
- ✅ **添加 JFXAnimation 动画系统**:
  - 淡入淡出（fadeIn/fadeOut）
  - 缩放动画（scaleIn/scaleOut、popIn/popOut）
  - 滑入动画（slideInFromBottom/Top/Left/Right）
  - 自定义动画构建器（AnimationBuilder），支持链式组合
  - 对标 Ant Design 动画时长和缓动函数
- ✅ **更新 Playground 演示**:
  - 添加 Modal 对话框演示页面
  - 添加 Animation 动画效果演示页面
  - 演示所有新功能

### 📝 当前状态

- **阶段**: Phase 1 基础设施已完成，Phase 2 扩展组件进行中
- **进度**:
  - ✅ 步骤 1-5: 基础设施完成
  - ✅ 步骤 6-8: Button、Input、Card 组件完成
  - ✅ Modal 组件完成
  - ✅ 动画系统完成

#### 8. 本次更新完成的工作（2025-01-08 第三轮更新）

- ✅ **添加 JFXTable 表格组件**:
  - 支持文本列（`column()`）
  - 支持自定义节点列（`nodeColumn()`）
  - 支持数字列（`numberColumn()`，右对齐）
  - 支持布尔列（`booleanColumn()`，显示勾选框）
  - 支持斑马纹（`striped`）
  - 支持边框（`bordered`）
  - 支持选择列（`selectable`）
  - 支持紧凑模式（`compact`）
- ✅ **添加 JFXForm 表单系统**:
  - 支持三种布局：VERTICAL、HORIZONTAL、INLINE
  - 支持普通表单项和必填表单项
  - 支持帮助文本提示
  - 支持标签宽度自定义
  - 支持提交按钮和回调函数
  - 自动收集表单数据
- ✅ **添加 Layouts DSL 布局系统**:
  - VBox 布局构建器（`Layouts.vbox()`）
  - HBox 布局构建器（`Layouts.hbox()`）
  - GridPane 布局构建器（`Layouts.grid()`）
  - 弹性占位（`Layouts.grow()`）
  - 固定尺寸占位（`Layouts.spacer()`）
  - 支持间距、边距、对齐方式链式设置
- ✅ **更新 Playground 演示**:
  - 添加 Table 表格演示页面
  - 添加 Form 表单演示页面（垂直和水平布局）
  - 添加 Layout DSL 布局演示页面

### 📝 当前状态

- **阶段**: Phase 1 基础设施已完成，Phase 2 扩展组件基本完成
- **进度**:
  - ✅ 步骤 1-5: 基础设施完成
  - ✅ 步骤 6-8: Button、Input、Card 组件完成
  - ✅ Modal 组件完成
  - ✅ 动画系统完成
  - ✅ Table 表格组件完成
  - ✅ Form 表单系统完成
  - ✅ Layout DSL 布局系统完成

#### 9. 本次更新完成的工作（2025-01-08 第四轮更新）

- ✅ **优化 CSS 样式细节**:
  - 添加 Table 表格组件样式（表头、行、单元格、斑马纹、边框、紧凑模式）
  - 添加 Form 表单组件样式
  - 添加 Menu 菜单组件样式（菜单栏、菜单项、上下文菜单）
  - 添加 Tabs 标签页组件样式（选中状态、悬停状态）
  - 添加 Tree 树形组件样式（节点、选中状态、展开图标）
- ✅ **添加 JFXMenu 菜单组件**:
  - 支持菜单项（`item()`）
  - 支持带图标的菜单项
  - 支持带快捷键的菜单项
  - 支持分隔线（`separator()`）
  - 支持子菜单（`subMenu()`）
- ✅ **添加 JFXTabs 标签页组件**:
  - 支持添加标签页（`tab()`）
  - 支持带提示文本的标签页
  - 支持可关闭标签页（`closable()`）
  - 支持关闭策略和拖拽策略
- ✅ **添加 JFXTree 树形组件**:
  - 支持叶子节点（`leaf()`）
  - 支持父节点（`node()`）
  - 支持带图标的节点
  - 支持选择回调（`onSelect()`）
  - 支持显示/隐藏根节点
- ✅ **更新 Playground 演示**:
  - 添加 Menu 菜单演示页面
  - 添加 Tabs 标签页演示页面
  - 添加 Tree 树形演示页面

### 📝 当前状态

- **阶段**: Phase 1 基础设施已完成，Phase 2 扩展组件基本完成
- **进度**:
  - ✅ 步骤 1-5: 基础设施完成
  - ✅ 步骤 6-8: Button、Input、Card 组件完成
  - ✅ Modal 组件完成
  - ✅ 动画系统完成
  - ✅ Table 表格组件完成
  - ✅ Form 表单系统完成
  - ✅ Layout DSL 布局系统完成
  - ✅ Menu 菜单组件完成
  - ✅ Tabs 标签页组件完成
  - ✅ Tree 树形组件完成

#### 10. 本次更新完成的工作（2025-01-08 第五轮更新）

- ✅ **添加 JFXDatePicker 日期选择组件**:
  - 支持占位文本（`placeholder()`）
  - 支持默认值（`value()`）
  - 支持可编辑控制（`editable()`）
  - 支持显示周数（`showWeekNumbers()`）
  - 支持值变化回调（`onChange()`）
- ✅ **添加 JFXSlider 滑块组件**:
  - 支持最小值、最大值、当前值（`min()`、`max()`、`value()`）
  - 支持步长（`step()`）
  - 支持刻度标签和刻度线（`showTickLabels()`、`showTickMarks()`）
  - 支持垂直/水平方向（`vertical()`）
  - 支持值变化回调（`onChange()`）
- ✅ **添加 JFXProgress 进度条组件**:
  - 支持进度条（`JFXProgress.bar()`）
  - 支持环形进度（`JFXProgress.circle()`）
  - 支持多种尺寸（SMALL、DEFAULT、LARGE）
  - 支持多种状态（NORMAL、SUCCESS、WARNING、ERROR）
  - 支持显示进度信息
- ✅ **添加 JFXAlert 警告提示组件**:
  - 支持四种类型：success、info、warning、error
  - 支持标题和消息内容
  - 支持可关闭（`closable()`）
  - 支持关闭回调（`onClose()`）
  - 支持显示/隐藏图标
  - 内置淡入淡出动画
- ✅ **更新 Playground 演示**:
  - 添加 DatePicker 日期选择演示页面
  - 添加 Slider 滑块演示页面
  - 添加 Progress 进度条演示页面
  - 添加 Alert 警告提示演示页面

### 📝 当前状态

- **阶段**: Phase 1 基础设施已完成，Phase 2 扩展组件基本完成
- **进度**:
  - ✅ 步骤 1-5: 基础设施完成
  - ✅ 步骤 6-8: Button、Input、Card 组件完成
  - ✅ Modal 组件完成
  - ✅ 动画系统完成
  - ✅ Table 表格组件完成
  - ✅ Form 表单系统完成
  - ✅ Layout DSL 布局系统完成
  - ✅ Menu 菜单组件完成
  - ✅ Tabs 标签页组件完成
  - ✅ Tree 树形组件完成
  - ✅ DatePicker 日期选择组件完成
  - ✅ Slider 滑块组件完成
  - ✅ Progress 进度条组件完成
  - ✅ Alert 警告提示组件完成
#### 11. 本次更新完成的工作（2025-01-08 第六轮更新）

- ✅ **创建组件使用文档** (`docs/COMPONENTS.md`):
  - JFXButton、JFXInput、JFXCard 文档
  - JFXTable、JFXForm、JFXModal 文档
  - JFXMenu、JFXTabs、JFXTree 文档
  - JFXDatePicker、JFXSlider、JFXProgress、JFXAlert 文档
  - 包含基本用法、配置方法、完整示例
  - 快速参考章节

- ✅ **创建动画系统文档** (`docs/ANIMATION.md`):
  - 动画时长和缓动函数说明
  - 淡入淡出、缩放、弹出、滑入动画
  - 自定义动画构建器
  - 实际示例（模态框、列表项、页面切换、提示信息）
  - 最佳实践和性能优化

- ✅ **创建布局 DSL 文档** (`docs/LAYOUT.md`):
  - VBox、HBox、GridPane 布局构建器
  - 辅助方法（grow、spacer）
  - 实际示例（登录页面、卡片列表、工具栏、仪表盘）
  - 最佳实践

- ✅ **创建主题系统文档** (`docs/THEME.md`):
  - 三层 Token 体系说明
  - 主题切换方法
  - 创建自定义主题
  - CSS 类名常量
  - 最佳实践

- ✅ **更新 Playground 实际示例**:
  - 添加 Examples 标签页
  - 登录表单示例
  - 仪表盘示例（统计卡片、活动列表）
  - 设置面板示例

### 📝 当前状态

- **阶段**: Phase 1 基础设施已完成，Phase 2 扩展组件和文档已完成
- **进度**:
  - ✅ 步骤 1-5: 基础设施完成
  - ✅ 步骤 6-8: Button、Input、Card 组件完成
  - ✅ Modal 组件完成
  - ✅ 动画系统完成
  - ✅ Table 表格组件完成
  - ✅ Form 表单系统完成
  - ✅ Layout DSL 布局系统完成
  - ✅ Menu 菜单组件完成
  - ✅ Tabs 标签页组件完成
  - ✅ Tree 树形组件完成
  - ✅ DatePicker 日期选择组件完成
  - ✅ Slider 滑块组件完成
  - ✅ Progress 进度条组件完成
  - ✅ Alert 警告提示组件完成
  - ✅ 组件文档完成
  - ✅ 动画文档完成
  - ✅ 布局文档完成
  - ✅ 主题文档完成
#### 12. 本次更新完成的工作（2025-01-08 第七轮更新）

- ✅ **分析 JavaFX 原生控件缺失情况**:
  - 识别出 8 个缺失的常用原生控件
  - 制定补充计划

- ✅ **添加 JFXCheckBox 复选框组件**:
  - 支持选中状态（`selected()`）
  - 支持禁用状态（`disabled()`）
  - 支持不确定状态（`indeterminate()`）
  - 支持值变化回调（`onChange()`）

- ✅ **添加 JFXRadioButton 单选按钮组件**:
  - 支持选中状态（`selected()`）
  - 支持禁用状态（`disabled()`）
  - 支持 ToggleGroup（`toggleGroup()`）
  - 支持值变化回调（`onChange()`）

- ✅ **添加 JFXComboBox 下拉框组件**:
  - 支持选项列表（`items()`）
  - 支持默认值（`value()`）
  - 支持占位文本（`placeholder()`）
  - 支持可编辑（`editable()`）
  - 支持值变化回调（`onChange()`）

- ✅ **添加 JFXListView 列表组件**:
  - 支持数据列表（`items()`）
  - 支持选择回调（`onSelect()`）
  - 支持禁用状态（`disabled()`）

- ✅ **添加 JFXTextArea 多行文本组件**:
  - 支持占位文本（`placeholder()`）
  - 支持行数（`rows()`）
  - 支持自动换行（`wrapText()`）
  - 支持值变化回调（`onChange()`）

- ✅ **添加 JFXSpinner 加载中组件**:
  - 支持自定义大小（`size()`）

- ✅ **添加 JFXBadge 徽标组件**:
  - 支持数字徽标（`count()`）
  - 支持圆点徽标（`dot()`）
  - 支持状态点（`status()`）
  - 支持四种状态（SUCCESS, WARNING, ERROR, DEFAULT）

- ✅ **添加 JFXDivider 分割线组件**:
  - 支持水平/垂直方向（`vertical()`）

- ✅ **更新 Playground 演示**:
  - 添加 CheckBox 演示页面
  - 添加 RadioButton 演示页面
  - 添加 ComboBox 演示页面
  - 添加 ListView 演示页面
  - 添加 TextArea 演示页面
  - 添加 Spinner 演示页面
  - 添加 Badge 演示页面
  - 添加 Divider 演示页面

- ✅ **创建单元测试**:
  - JFXButtonTest（5 个测试用例）
  - JFXInputTest（4 个测试用例）
  - JFXCheckBoxTest（4 个测试用例）
  - JFXComboBoxTest（4 个测试用例）
  - LayoutsTest（5 个测试用例）
  - JavaFXTestBase（测试基类）
  - 所有测试通过

### 📝 当前状态

- **阶段**: Phase 1 基础设施已完成，Phase 2 扩展组件和测试已完成
- **进度**:
  - ✅ 步骤 1-5: 基础设施完成
  - ✅ 步骤 6-8: Button、Input、Card 组件完成
  - ✅ Modal 组件完成
  - ✅ 动画系统完成
  - ✅ Table 表格组件完成
  - ✅ Form 表单系统完成
  - ✅ Layout DSL 布局系统完成
  - ✅ Menu 菜单组件完成
  - ✅ Tabs 标签页组件完成
  - ✅ Tree 树形组件完成
  - ✅ DatePicker 日期选择组件完成
  - ✅ Slider 滑块组件完成
  - ✅ Progress 进度条组件完成
  - ✅ Alert 警告提示组件完成
  - ✅ CheckBox 复选框组件完成
  - ✅ RadioButton 单选按钮组件完成
  - ✅ ComboBox 下拉框组件完成
  - ✅ ListView 列表组件完成
  - ✅ TextArea 多行文本组件完成
  - ✅ Spinner 加载中组件完成
  - ✅ Badge 徽标组件完成
  - ✅ Divider 分割线组件完成
  - ✅ 组件文档完成
  - ✅ 动画文档完成
  - ✅ 布局文档完成
  - ✅ 主题文档完成
  - ✅ 单元测试完成
#### 13. 本次更新完成的工作（2025-01-08 第八轮更新）

- ✅ **JavaFX 原生控件覆盖完成**:
  - JFXTooltip 工具提示组件
  - JFXSwitch 开关组件（ToggleButton 封装）
  - JFXPagination 分页组件
  - JFXAccordion 折叠面板组件
  - JFXTitledPane 标题面板组件
  - JFXColorPicker 颜色选择组件

- ✅ **更新 Playground 演示**:
  - 添加 Tooltip 演示页面
  - 添加 Switch 演示页面
  - 添加 Pagination 演示页面
  - 添加 Accordion 演示页面
  - 添加 ColorPicker 演示页面

#### 14. 本次更新完成的工作（2026-05-09 - LESS 主题系统）

- ✅ **LESS 主题变量系统**:
  - 创建 `variables-base.less`：共享的尺寸、间距、圆角、Mixin（与主题无关）
  - 创建 `variables.less`：Light 主题颜色变量
  - 创建 `variables-dark.less`：Dark 主题颜色变量
  - 颜色体系：Base Token（11级灰度 + 10级彩色）→ Semantic Token → Component Token

- ✅ **共享组件样式**:
  - 创建 `theme-base.less`：所有组件样式（Button、Input、Card、Table、Menu、Tabs、Tree）
  - 样式与变量分离，通过 LESS 变量引用实现主题无关

- ✅ **主题入口文件**:
  - `theme-light.less` = `@import "variables.less"` + `@import "theme-base.less"`
  - `theme-dark.less` = `@import "variables-dark.less"` + `@import "theme-base.less"`
  - 同一套组件样式，两套颜色变量，生成两套 CSS

- ✅ **Node.js LESS 编译**:
  - 使用本机 Node.js + less@4.6.4 编译
  - 编译命令：`npx lessc theme-light.less theme-light.css`
  - 创建 `build-themes.js` 脚本一键编译所有主题
  - 输出到 `src/main/resources/org/openkawu/jfxium/css/` 供 Java 加载

- ✅ **验证**:
  - Light CSS 生成正确（白色背景 `#ffffff`，黑色文字）
  - Dark CSS 生成正确（深色背景 `#0d1117`，白色文字）
  - 所有单元测试通过
  - Playground 主题切换功能正常

### 📝 当前状态

- **阶段**: Phase 1 基础设施已完成，Phase 2 JavaFX 原生控件覆盖已完成
- **JavaFX 原生控件覆盖**: 27 个组件
  - ✅ 基础组件：Button、Input、Card、Modal
  - ✅ 表单组件：CheckBox、RadioButton、ComboBox、TextArea、DatePicker、Slider、ColorPicker
  - ✅ 数据展示：Table、ListView、Tree、Progress、Badge、Pagination
  - ✅ 导航：Menu、Tabs、Accordion、TitledPane
  - ✅ 反馈：Alert、Spinner、Tooltip
  - ✅ 布局：Divider、Layouts DSL
  - ✅ 其他：Switch

#### 15. 本次更新完成的工作（2026-05-09 第二轮 - CSS 补全 + Phase 3 组件）

- ✅ **补全 theme-base.less CSS 样式覆盖**（所有27个组件）：
  - CheckBox、RadioButton、ComboBox、ListView、TextArea
  - Slider、ProgressBar、ProgressIndicator
  - Alert（success/info/warning/error 四色）
  - Badge（数字徽标、圆点、状态点）
  - Divider/Separator
  - DatePicker（含弹出面板日历样式）
  - ColorPicker（含弹出调色板样式）
  - Accordion、TitledPane
  - Tooltip、Pagination
  - ScrollBar、ScrollPane、ContextMenu、MenuBar 子菜单
  - Switch（ToggleButton）
  - Label、Hyperlink、ToolBar、ChoiceBox、Spinner
  - SplitPane、Dialog/Modal 遮罩
  - Table 增强（行、单元格、斑马纹、边框）
  - Tab 增强（标签、关闭按钮）
  - Tree 增强（单元格、展开图标）
  - Form 辅助样式（label、required、help-text）
  - 全局禁用状态、焦点遍历清除

- ✅ **参考 AtlantaFX 完善 CSS 细节**：
  - 对照 AtlantaFX 本地代码完善组件选择器
  - 添加阴影、悬停、选中状态细节
  - 统一圆角和间距规范

- ✅ **Maven 集成 LESS 编译**：
  - 配置 exec-maven-plugin 在 generate-resources 阶段自动编译
  - `mvn compile` 自动生成 theme-light.css 和 theme-dark.css
  - 无需手动执行 npx lessc

- ✅ **Phase 3 高级组件**：
  - JFXTag：6种类型（Default/Primary/Success/Warning/Error/Processing）、3种尺寸、3种形状、可关闭、无边框
  - JFXAvatar：4种尺寸（SM/DEFAULT/LG/XL）、2种形状（Circle/Square）、自定义背景色、图片/文字支持
  - JFXRate：5星评分、半星支持、禁用状态、3种尺寸、hover/click 回调
  - JFXEmpty：空状态图标、描述文字、操作按钮
  - JFXSteps：水平/垂直方向、步骤标题+描述、当前步骤高亮、完成/等待状态
  - JFXSkeleton：4种变体（Text/Circular/Rectangular/Rounded）、动画 shimmer、段落/头像组合辅助方法

- ✅ **更新 Playground 演示**：
  - 添加 Tag、Avatar、Rate、Empty、Steps、Skeleton 演示页面
  - 每个组件展示所有变体和配置选项

- ✅ **Padding 变量统一化**：
  - `variables-base.less` 新增组件级 padding 变量（Button/Input/Card/Table/Menu/Tab/Tree/Dialog 等）
  - `theme-base.less` 所有组件 padding 从写死值改为引用变量
  - 为后续 Small/Medium/Large 主题尺寸变体做准备

- ✅ **UI Bug 修复**：
  - 修复按钮 hover 文字消失问题（双层背景改为单层+边框）
  - 修复 Menu Bar 菜单项间距（添加 padding）
  - 修复 Tab 关闭按钮默认红色问题（改为灰色，hover 才变红）

- ✅ **验证**：
  - LESS 编译成功
  - mvn compile 成功（含自动 LESS 编译）
  - mvn test 全部通过
  - Light/Dark 主题切换截图验证通过

### 📝 当前状态

- **阶段**: Phase 1-2 完成，Phase 3 进行中
- **JavaFX 原生控件覆盖**: 27 个组件 ✅
- **Phase 3 高级组件**: 6 个已完成（Tag、Avatar、Rate、Empty、Steps、Skeleton）
- **组件总数**: 33 个

- **下一步（Phase 3 继续）**:
  - Result 结果页组件
  - BackTop 回到顶部组件
  - Descriptions 描述列表组件
  - Timeline 时间轴组件
  - Carousel 轮播组件
  - Calendar 日历组件
  - Upload 上传组件
  - Transfer 穿梭框组件

- **其他计划**:
  1. 优化性能和可访问性
  2. 添加更多高级功能（数据绑定、主题定制等）

---

## 下一阶段准备（Phase 3 继续 + 优化）

> **新对话接入指南**：直接阅读本 PLAN.md 即可了解项目完整状态，无需重复询问历史。

### 已完成（无需重复）
- ✅ LESS 主题系统（Light/Dark 两套 CSS）
- ✅ 27 个 JavaFX 原生控件样式覆盖
- ✅ 6 个 Phase 3 高级组件（Tag、Avatar、Rate、Empty、Steps、Skeleton）
- ✅ Padding 变量统一化
- ✅ Maven 自动编译 LESS
- ✅ Playground 演示应用

### 待做任务（按优先级）

**高优先级：**
1. Phase 3 剩余组件：Result、BackTop、Descriptions、Timeline、Carousel、Calendar、Upload、Transfer
2. 参考 AtlantaFX 完善缺失的 CSS（ScrollPane、ToolBar、ChoiceBox 等细节）
3. 对标 Ant Design 6.x 组件列表，检查缺失项

**中优先级：**
4. 组件尺寸变体（Small/Medium/Large）- padding 变量已准备好
5. 更多主题色（Success/Warning/Danger 主题的按钮变体）
6. 动画效果增强（组件出现/消失动画）

**低优先级：**
7. 性能优化
8. 可访问性（键盘导航、屏幕阅读器）
9. 文档完善

### 技术要点（新对话需知）
- **LESS 编译**：`mvn compile` 自动生成 CSS，或 `npx lessc` 手动编译
- **主题切换**：`Application.setUserAgentStylesheet()` + `ThemeManager`
- **组件模式**：Builder Pattern（`JFXButton.create().text("OK").build()`）
- **CSS 变量**：`-color-*` 格式（JavaFX looked-up colors）
- **文件位置**：
  - LESS 源文件：`src/main/resources/org/openkawu/jfxium/css/less/`
  - 生成 CSS：`src/main/resources/org/openkawu/jfxium/css/`
  - 组件代码：`src/main/java/org/openkawu/jfxium/component/`
  - Playground：`src/main/java/org/openkawu/jfxium/playground/JFXiumPlayground.java`

***

## 项目信息

- **JavaFX 版本**: 21.0.6（不升级）
- **构建工具**: Maven
- **FXML**: 完全移除，纯代码构建 UI
- **CSS 策略**: CSS 文件为主，CSS 变量映射 Token，参考 Ant Design 6.x Token 规范
- **模块系统**: module-info.java 随包结构扩展更新
- **开发节奏**: 先设计 Token + Theme + CSS，用原生控件验证样式，后期封装自定义控件
- <br />

## 项目参考 UI实现思路 ;([atlantafx 开源项目](https://github.com/mkpaz/atlantafx))

[参考它 : ](https://github.com/mkpaz/atlantafx-sample-theme)[https://github.com/mkpaz/atlantafx ;    本地代码在 F:\workspace-open-code\atlantafx\sampler ; ](https://github.com/mkpaz/atlantafx)

本地[atlantafx 生成的 UI 主题 文件 里面非常全 完全的覆盖了  javafx 已有 控件](https://github.com/mkpaz/atlantafx)  F:\workspace-open-code\atlantafx\styles\dist

还有 看人家的思路;  直接用 css 就能 展示出一套 ui

```
public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // find more themes in 'atlantafx.base.theme' package
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // the rest of the code ...
    }
}
```

最好 是 less 来组装 css 等, 别人一运行  就能生成一套 UI css;  别人稍微更改写 就又是一套 UI;  如果java也能实现 类似 less 那样  生成 一套 或者多套  UI  css 主题 也可以用Java 来写?  

 .calss 常用的名称 改成 java 常量 ,其他  个性化 UI ,通用组件 都能用;  

剩下就是 参考 Ant Design 6.x Token  美化样式; 

## 验证代码与UI 补充建议

AI去运行示例项目Demo,   截图 , 你自己分析 是否达到了 Ant Design 6.x Token 美化

***

##  一、Phase 1: 基础设施 + 核心组件

### 步骤 1: 项目结构重构

**目标**: 建立清晰的包结构和模块划分

**具体操作**:

1. 删除 `hello-view.fxml`、`HelloController.java`、`HelloApplication.java`
2. 创建新的包结构：
   ```
   org.openkawu.jfxium/
   ├── core/
   │   ├── token/         # Design Token 定义
   │   ├── theme/         # Theme Engine
   │   └── css/           # CSS 资源管理
   ├── component/
   │   └── (预留，Phase 1 后期使用)
   ├── playground/        # Demo / 样式验证
   └── JFXiumApp.java     # 新的入口类
   ```
3. 更新 `module-info.java` 导出新的包
4. 更新 `pom.xml`（如有需要）
   **验收标准**:

- 项目能正常编译运行
- 新的入口类能启动一个空窗口
  \*\* CSS 参考 \*\*:
  <https://github.com/mkpaz/atlantafx> 现代 JavaFX CSS 主题合集，包含更多控制选项。
  This is an example of creating custom JavaFX CSS theme based on AtlantaFX stylesheet. Just clone the repository and use it as a starting point for creating your own theme.
  <https://github.com/mkpaz/atlantafx-sample-theme>

***

### 步骤 2: Design Token System 设计

**目标**: 建立对标 Ant Design 的 Token 分层体系

**Token 三层架构**:

```
Base Token (基础/原子级)
  └── 颜色板、字体尺寸、间距步长、圆角基数、阴影层级
      ↓
Semantic Token (语义级)
  └── 主色、成功色、警告色、错误色、文本色、背景色、边框色
      ↓
Component Token (组件级)
  └── Button Token、Input Token、Card Token...
```

**具体 Token 分类**:

#### 2.1 颜色 Token (Colors)

- 主色板：Primary (品牌色，6-10 个梯度)
- 功能色：Success、Warning、Error、Info (各 6-10 个梯度)
- 中性色：Gray (13 个梯度，从白到黑)
- 语义映射：
  - `color-primary` → Primary-600
  - `color-success` → Success-600
  - `color-warning` → Warning-600
  - `color-error` → Error-600
  - `color-text-primary` → Gray-900
  - `color-text-secondary` → Gray-600
  - `color-text-disabled` → Gray-400
  - `color-bg-container` → White
  - `color-bg-secondary` → Gray-100
  - `color-border` → Gray-300

#### 2.2 间距 Token (Spacing)

- 基础步长：2px、4px、8px、12px、16px、20px、24px、32px、40px、48px
- 语义映射：
  - `spacing-xs` → 4px
  - `spacing-sm` → 8px
  - `spacing-md` → 16px
  - `spacing-lg` → 24px
  - `spacing-xl` → 32px

#### 2.3 圆角 Token (Radius)

- `radius-sm` → 2px
- `radius-md` → 4px
- `radius-lg` → 8px
- `radius-xl` → 16px
- `radius-full` → 9999px (pill 形状)

#### 2.4 字体 Token (Typography)

- 字体族：`font-family` → "Inter", "Segoe UI", system-ui, sans-serif
- 字号：
  - `font-size-xs` → 12px
  - `font-size-sm` → 14px
  - `font-size-md` → 16px
  - `font-size-lg` → 18px
  - `font-size-xl` → 20px
  - `font-size-xxl` → 24px
- 字重：
  - `font-weight-regular` → 400
  - `font-weight-medium` → 500
  - `font-weight-semibold` → 600
  - `font-weight-bold` → 700
- 行高：
  - `line-height-tight` → 1.25
  - `line-height-normal` → 1.5
  - `line-height-relaxed` → 1.75

#### 2.5 阴影 Token (Shadow)

- `shadow-sm` → 0 1px 2px rgba(0,0,0,0.05)
- `shadow-md` → 0 4px 6px rgba(0,0,0,0.07)
- `shadow-lg` → 0 10px 15px rgba(0,0,0,0.1)
- `shadow-xl` → 0 20px 25px rgba(0,0,0,0.1)

#### 2.6 动画 Token (Animation)

- `duration-fast` → 150ms
- `duration-normal` → 250ms
- `duration-slow` → 350ms
- `ease-in-out` → cubic-bezier(0.4, 0, 0.2, 1)
- `ease-out` → cubic-bezier(0, 0, 0.2, 1)

**Java 实现方式**:

- 使用 Java Records 定义 Token 结构
- 使用不可变 Map 存储 Token 值
- 提供 Token 覆盖机制（用户可自定义 Token）

**验收标准**:

- Token 定义完整，覆盖颜色、间距、圆角、字体、阴影、动画
- 能通过 Java API 访问任意 Token 值
- 支持 Token 值的覆盖和扩展

***

### 步骤 3: Theme Engine 设计

**目标**: 实现 Light/Dark 主题切换、Token 动态覆盖

**核心组件**:

#### 3.1 Theme 接口/类

```java
public interface Theme {
    String getName();                    // "light" 或 "dark"
    Color getColor(String token);        // 获取颜色 Token
    double getSpacing(String token);     // 获取间距 Token
    double getRadius(String token);      // 获取圆角 Token
    // ... 其他 Token 获取方法
}
```

#### 3.2 LightTheme / DarkTheme 实现

- `LightTheme`: 默认亮色主题，基于 Ant Design 默认色板
- `DarkTheme`: 暗色主题，颜色值反转/调整

#### 3.3 ThemeContext（全局主题上下文）

- 单例模式管理当前主题
- 支持主题切换事件监听
- 提供 `current()` 方法获取当前主题

#### 3.4 ThemeManager

- 注册/切换主题
- 应用主题到 Scene（注入 CSS 变量）
- 监听主题变化，动态更新样式

**主题切换机制**:

1. ThemeManager 维护当前 Theme 实例
2. 切换主题时，重新生成 CSS 变量字符串
3. 将 CSS 变量注入到 Scene 的 stylesheet
4. 所有使用 CSS 变量的控件自动更新

**验收标准**:

- 能切换 Light/Dark 主题
- 切换后所有使用 CSS 变量的控件自动更新颜色
- 支持运行时 Token 覆盖

***

### 步骤 4: CSS 文件体系设计

**目标**: 建立完整的 CSS 变量体系，映射 Token 到 CSS

**文件结构**:

```
resources/org/openkawu/jfxium/css/
├── base/                    # 基础 CSS
│   ├── variables-light.css  # Light 主题 CSS 变量
│   ├── variables-dark.css   # Dark 主题 CSS 变量
│   └── reset.css            # 基础重置样式
├── components/              # 组件样式（Phase 1 后期开始）
│   ├── button.css
│   ├── input.css
│   └── card.css
└── theme.css                # 主题入口（动态加载）
```

**CSS 变量命名规范**（对标 Ant Design CSS 变量）:

```css
/* variables-light.css */
.root {
  /* 主色 */
  --jfx-primary: #1677ff;
  --jfx-primary-hover: #4096ff;
  --jfx-primary-active: #0958d9;
  --jfx-primary-disabled: #bae0ff;
  
  /* 功能色 */
  --jfx-success: #52c41a;
  --jfx-warning: #faad14;
  --jfx-error: #f5222d;
  --jfx-info: #1677ff;
  
  /* 文本色 */
  --jfx-text-primary: rgba(0, 0, 0, 0.88);
  --jfx-text-secondary: rgba(0, 0, 0, 0.65);
  --jfx-text-disabled: rgba(0, 0, 0, 0.25);
  
  /* 背景色 */
  --jfx-bg-container: #ffffff;
  --jfx-bg-secondary: #f5f5f5;
  --jfx-bg-tertiary: #f0f0f0;
  
  /* 边框色 */
  --jfx-border: #d9d9d9;
  --jfx-border-secondary: #f0f0f0;
  
  /* 间距 */
  --jfx-spacing-xs: 4px;
  --jfx-spacing-sm: 8px;
  --jfx-spacing-md: 16px;
  --jfx-spacing-lg: 24px;
  --jfx-spacing-xl: 32px;
  
  /* 圆角 */
  --jfx-radius-sm: 2px;
  --jfx-radius-md: 4px;
  --jfx-radius-lg: 8px;
  --jfx-radius-xl: 16px;
  
  /* 阴影 */
  --jfx-shadow-sm: 0 1px 2px rgba(0,0,0,0.05);
  --jfx-shadow-md: 0 4px 6px rgba(0,0,0,0.07);
  --jfx-shadow-lg: 0 10px 15px rgba(0,0,0,0.1);
  
  /* 字体 */
  --jfx-font-family: "Inter", "Segoe UI", system-ui, sans-serif;
  --jfx-font-size-sm: 14px;
  --jfx-font-size-md: 16px;
}
```

**Dark 主题变量**:

- 颜色反转：背景变暗、文本变亮
- 边框调整：使用 rgba 透明度
- 阴影调整：降低透明度

**动态加载机制**:

- ThemeManager 根据当前主题加载对应的 variables-xxx.css
- 支持运行时切换（移除旧样式表，添加新样式表）

**验收标准**:

- CSS 变量完整覆盖所有 Token
- Light/Dark 两套变量文件完整
- 能动态加载和切换 CSS 文件
- 原生 JavaFX 控件应用 CSS 变量后显示正确

***

### 步骤 5: Playground / Demo 搭建

**目标**: 创建样式验证环境，用原生控件测试 CSS

**内容**:

1. 创建 `JFXiumPlayground` 入口类
2. 创建多个 Demo 页面：
   - **Colors Demo**: 展示所有颜色 Token 和色板
   - **Typography Demo**: 展示字体、字号、字重
   - **Spacing Demo**: 展示间距体系
   - **Shadow Demo**: 展示阴影效果
   - **Components Demo**: 用原生 Button、TextField、Label 等测试 CSS 样式
3. 添加主题切换按钮（Light ↔ Dark）

**原生控件测试策略**:

- 用 `javafx.scene.control.Button` 测试按钮样式
- 用 `javafx.scene.control.TextField` 测试输入框样式
- 用 `javafx.scene.layout.VBox/HBox` 测试间距和布局
- 所有控件通过 CSS 类名应用样式

**验收标准**:

- Playground 能正常启动
- 能展示所有 Token 的视觉效果
- 主题切换按钮能实时切换 Light/Dark
- 所有原生控件应用 CSS 后视觉效果符合 Ant Design 风格

***

### 步骤 6: Button 组件封装（Phase 1 后期）

**目标**: 封装第一个自定义组件，验证完整架构

**组件结构**:

```
component/button/
  ├── JFXButton.java          # API 类（Builder Pattern）
  ├── JFXButtonSkin.java      # Skin 类（视觉渲染）
  ├── JFXButtonTheme.java     # 组件主题配置
  └── JFXButtonTokens.java    # 组件 Token 默认值
```

**API 设计**:

```java
JFXButton button = UI.button("Click Me")
    .type(ButtonType.PRIMARY)    // PRIMARY, DEFAULT, DASHED, TEXT, LINK
    .size(ButtonSize.LARGE)       // SMALL, MEDIUM, LARGE
    .shape(ButtonShape.ROUNDED)   // SQUARE, ROUNDED, CIRCLE
    .icon(Icons.PLUS)             # 图标
    .disabled(true)               # 禁用状态
    .loading(true)                # 加载状态
    .onClick(event -> {...})      # 点击事件
    .build();
```

**样式实现**:

- 优先使用 CSS 类名控制样式
- 复杂状态（loading、disabled）通过 Skin 类处理
- 颜色、间距、圆角全部使用 CSS 变量

**验收标准**:

- Button 组件能正常显示和使用
- 支持所有类型、尺寸、形状
- 支持禁用和加载状态
- 主题切换时 Button 样式自动更新

***

### 步骤 7: Input 组件封装

**目标**: 封装文本输入组件

**API 设计**:

```java
JFXInput input = UI.input()
    .placeholder("请输入内容")
    .size(InputSize.LARGE)
    .prefix(Icons.USER)           # 前缀图标
    .suffix(Icons.EYE)            # 后缀图标
    .clearable(true)              # 可清空
    .disabled(true)
    .onChange(value -> {...})
    .build();
```

**验收标准**:

- 支持普通文本、密码输入
- 支持前缀/后缀图标
- 支持清空按钮
- 支持禁用状态
- 主题切换时样式自动更新

***

### 步骤 8: Card 组件封装

**目标**: 封装卡片容器组件

**API 设计**:

```java
JFXCard card = UI.card()
    .title("Card Title")
    .extra(UI.button("More").link().build())
    .bordered(true)
    .hoverable(true)
    .shadow(ShadowSize.MD)
    .padding(Spacing.MD)
    .content(...)
    .build();
```

**验收标准**:

- 支持标题、额外操作区
- 支持边框、阴影、悬浮效果
- 支持自定义内容
- 主题切换时样式自动更新

***

## 二、Phase 2: 扩展组件 + 布局系统

### 步骤 9: Modal / Dialog 组件

**目标**: 封装对话框组件

**API 设计**:

```java
JFXModal modal = UI.modal()
    .title("确认删除")
    .content("确定要删除这条记录吗？")
    .width(400)
    .closable(true)
    .maskClosable(true)
    .okText("确认")
    .cancelText("取消")
    .onOk(() -> {...})
    .onCancel(() -> {...})
    .show();
```

***

### 步骤 10: Table 组件

**目标**: 封装数据表格组件

**API 设计**:

```java
JFXTable<Person> table = UI.table(Person.class)
    .column("Name", Person::getName)
    .column("Age", Person::getAge)
    .column("Action", person -> UI.button("Edit").build())
    .data(personList)
    .striped(true)
    .bordered(true)
    .pagination(10)
    .build();
```

***

### 步骤 11: Form System

**目标**: 封装表单系统

**API 设计**:

```java
JFXForm form = UI.form()
    .item("用户名", UI.input().placeholder("请输入用户名").build())
    .item("密码", UI.input().password().placeholder("请输入密码").build())
    .item("性别", UI.select().options("男", "女").build())
    .layout(FormLayout.HORIZONTAL)
    .onSubmit(data -> {...})
    .build();
```

***

### 步骤 12: Layout DSL

**目标**: 创建流式布局 DSL

**API 设计**:

```java
// 垂直布局
VBox vbox = UI.vbox()
    .spacing(Spacing.MD)
    .align(Align.CENTER)
    .children(
        UI.button("A").build(),
        UI.button("B").build(),
        UI.button("C").build()
    )
    .build();

// 水平布局
HBox hbox = UI.hbox()
    .spacing(Spacing.MD)
    .justify(Justify.SPACE_BETWEEN)
    .children(...)
    .build();

// 网格布局
Grid grid = UI.grid()
    .cols(3)
    .gap(Spacing.MD)
    .children(...)
    .build();

// 弹性布局
Flex flex = UI.flex()
    .wrap(true)
    .gap(Spacing.MD)
    .children(...)
    .build();
```

***

## 三、Phase 3: 动画 + 工具链

### 步骤 13: Animation System

**目标**: 封装常用动画效果

**API 设计**:

```java
// 淡入
Animation.fadeIn(node, Duration.millis(250));

// 淡出
Animation.fadeOut(node, Duration.millis(250));

// 缩放
Animation.scaleIn(node, Duration.millis(200));

// 滑动
Animation.slideInFromBottom(node, Duration.millis(300));

// 自定义过渡
Animation.builder(node)
    .fromOpacity(0)
    .toOpacity(1)
    .fromTranslateY(20)
    .toTranslateY(0)
    .duration(Duration.millis(250))
    .play();
```

***

### 步骤 14: Playground 完善

**目标**: 完善 Demo 系统

**内容**:

1. 所有组件的独立 Demo 页面
2. 组件 API 文档展示
3. 代码示例展示
4. 主题实时切换
5. Token 值实时查看

***

### 步骤 15: CLI Tooling（可选）

**目标**: 提供脚手架工具

**内容**:

1. 组件生成器（自动生成组件模板）
2. 主题生成器（根据 Token 生成 CSS）
3. 文档生成器（从代码生成文档）

***

## 四、CSS 开发详细计划

### CSS 文件清单

| 文件                    | 说明              | 优先级 |
| --------------------- | --------------- | --- |
| `variables-light.css` | Light 主题 CSS 变量 | P0  |
| `variables-dark.css`  | Dark 主题 CSS 变量  | P0  |
| `reset.css`           | 基础重置样式          | P0  |
| `button.css`          | 按钮样式            | P1  |
| `input.css`           | 输入框样式           | P1  |
| `card.css`            | 卡片样式            | P1  |
| `modal.css`           | 对话框样式           | P2  |
| `table.css`           | 表格样式            | P2  |
| `form.css`            | 表单样式            | P2  |
| `layout.css`          | 布局辅助样式          | P2  |

### CSS 变量命名规范

- 前缀：`--jfx-`（避免与项目其他 CSS 冲突）
- 层级：`--jfx-{category}-{name}-{state}`
- 示例：
  - `--jfx-color-primary`
  - `--jfx-color-primary-hover`
  - `--jfx-button-bg-primary`
  - `--jfx-button-border-radius`

### 原生控件 CSS 映射

| 原生控件      | CSS 类名                | 说明     |
| --------- | --------------------- | ------ |
| Button    | `.jfx-button`         | 基础按钮样式 |
| Button    | `.jfx-button-primary` | 主按钮    |
| Button    | `.jfx-button-large`   | 大尺寸    |
| TextField | `.jfx-input`          | 输入框样式  |
| Label     | `.jfx-text`           | 文本样式   |
| VBox/HBox | `.jfx-layout`         | 布局辅助   |

***

## 五、开发顺序总结

```
Phase 1（当前阶段）:
  步骤 1: 项目结构重构
  步骤 2: Design Token System
  步骤 3: Theme Engine
  步骤 4: CSS 文件体系
  步骤 5: Playground / Demo
  步骤 6: Button 组件
  步骤 7: Input 组件
  步骤 8: Card 组件

Phase 2:
  步骤 9: Modal 组件
  步骤 10: Table 组件
  步骤 11: Form System
  步骤 12: Layout DSL

Phase 3:
  步骤 13: Animation System
  步骤 14: Playground 完善
  步骤 15: CLI Tooling
```

***

## 六、技术决策记录

| 决策              | 选择              | 理由                     |
| --------------- | --------------- | ---------------------- |
| FXML            | 删除              | 完全代码构建，符合现代 UI 框架趋势    |
| CSS 策略          | CSS 文件 + CSS 变量 | 对标 Ant Design，支持动态主题切换 |
| JavaFX 版本       | 21.0.6          | 稳定，无需升级                |
| 控件封装顺序          | 先原生验证，后自定义封装    | 降低风险，先验证 CSS 设计        |
| Token 规范        | Ant Design 6.x  | 成熟、文档完善、社区认可           |
| Builder Pattern | 链式调用            | 现代化 API 风格，对标前端框架      |

***

## 七、下一步行动

等待审核确认后，按以下顺序执行：

1. **开始步骤 1**: 重构项目结构，删除 FXML，建立新包结构
2. **开始步骤 2**: 设计 Token System Java API
3. **开始步骤 3**: 实现 Theme Engine
4. **开始步骤 4**: 编写 CSS 变量文件（Light + Dark）
5. **开始步骤 5**: 搭建 Playground，用原生控件验证 CSS

***

*计划版本: v1.0*
*创建日期: 2026-05-08*
*状态: 待审核*
