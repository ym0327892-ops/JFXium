# JFXium 组件目录（v1.34.2）

> 由 `skills/jfxium/scripts/CatalogGen.java` 生成，**禁止手改**。
> 改动组件公开 API 后重跑：`java skills/jfxium/scripts/CatalogGen.java`

> AI 使用顺序：本目录定位类名 → 打开 `source` 指向的类 Javadoc → `jfxium-demo` 找对应示例。


## org.openkawu.jfxium.component.composite

| 组件 | extends | 工厂 | build() | 关键 API | 定位 |
|---|---|---|---|---|---|
| **AnchorAnt** | — | `create() -> Builder` | `VBox` | item, items, direction, offsetTop, activeKey, onChange, controller | 页内锚点导航，点击链接滚动到对应区域， |
| **AutoCompleteAnt** | — | `create() -> <T> Builder<T>` | `HBox` | value, options, optionToString, filter, onChange, onSelect, maxSuggestions, bindValue | 输入框 + 智能建议下拉，常用于搜索框、城市选择、 |
| **AvatarAnt** | — | `create() -> Builder`<br>`create(String text) -> Builder` | `StackPane` | size, shape, text, image, src, icon, backgroundColor, textColor | 圆形/圆角方形头像容器，支持图片、文字首字母、自定义节点三种模式。 |
| **BackTopAnt** | — | `create() -> Builder` | `StackPane` | visibilityHeight, duration, target, bottom, right, install | 固定在右下角的浮动按钮，滚动到一定高度后显示， |
| **BadgeAnt** | — | `create() -> Builder` | `StackPane` | content, count, dot, status | JFXium Badge Component - 对标 Ant Design Badge。 |
| **BreadcrumbAnt** | — | `create() -> Builder` | `HBox` | separator, item, items | 显示当前页面在导航层级中的位置，常用于顶部导航路径展示。 |
| **CalendarAnt** | — | `create() -> Builder` | `VBox` | value, selectedDate, mode, onSelect, onPanelChange, fullscreen, controllerOf | JFXium 日历组件 —— 对标 Ant Design Calendar。 |
| **CarouselAnt** | — | `create() -> Builder` | `StackPane` | item, items, autoplay, autoplayInterval, dots, noDots, arrows, noArrows, effect, initialIndex, dotPosition | 轮播图控件，支持自动播放、手动切换、指示点导航， |
| **CascaderAnt** | — | `create() -> Builder` | `HBox` | hasChildren, options, allowClear, showSearch, onChange, value, bindValue | 多级下拉选择器，常用于省市区三级联动、分类目录选择等场景。 |
| **ChartAnt** | — | `create() -> Builder` | `Chart` | line, bar, area, stackedBar, stackedArea, scatter, bubble, data, slice, point, pointSeries, typeOf, pie, controllerOf, type, title, xAxisLabel, yAxisLabel | JFXium Chart 图表组件 —— 封装 javafx.scene.chart.* 的 Builder 入口。 |
| **CodeBlockAnt** | — | `create() -> Builder` | `BorderPane` | main, language, code, showLineNumbers, theme, maxHeight, showCopyButton, title, selectable | CodeBlockAnt - 代码块显示组件（不可编辑，可复制）。 |
| **CollapseAnt** | — | `create() -> Builder` | `VBox` | disabled, controllerOf, panel, panels, accordion, activeKey, activeKeys, expandIcon, controller, expand, collapse, toggle, expandAll, collapseAll | 可展开/收缩的内容面板，支持多个 Panel，常用于 FAQ、详情分组、 |
| **DateTimePickerAnt** | — | `create() -> Builder` | `StackPane` | controllerOf, value, editable, format, allowClear, size, onChange, bindValue, showPanel, hidePanel | 只读输入框 + 弹出面板（左侧日期面板 + 右侧时间面板并排）， |
| **DescriptionsAnt** | — | `create() -> Builder` | `VBox` | title, size, column, bordered, item | 以列表形式展示多个字段的详情（标签 + 值对），常用于详情页、 |
| **EmptyAnt** | — | `create() -> Builder`<br>`create(String description) -> Builder` | `VBox` | description, image, extra, extraButton | 空数据占位提示，用于列表 / 表格 / 搜索无结果时展示友好提示。 |
| **FloatButtonAnt** | — | `create() -> Builder` | `StackPane` | icon, tooltip, type, onClick, size | JFXium 悬浮按钮组件 - 对标 Ant Design FloatButton |
| **FormAnt** | — | `create() -> Builder` | `VBox` | required, rule, rules, helpText, validateStatus, labelAlign, labelWidth, hidden, hide, tooltip, end, size, colon, labelCol, wrapperCol, rowGap, columnGap, validateTrigger | JFXium 表单组件 - 对标 Ant Design Form。 |
| **GroupBoxAnt** | — | `create() -> Builder` | `VBox` | title, extra, content, bordered, size, type, actions, tab, activeTabKey, defaultActiveTabKey, onTabChange, tabBarExtraContent, headerBackground, headerBorder, hoverable | 带标题边框的内容容器，用于将逻辑相关的控件（输入框、复选框等） |
| **HBarAnt** | `AbstractHBoxAnt` | `create() -> HBarAnt` | `HBarAnt` | left, center, right, gap, alignment, borderTop, borderBottom, borderLeft, borderRight, background, padding, borderRadius, styleClass, style, visible, managed, opacity, id | 项目最底层的「横向条状容器」。header / toolbar / footer / appbar |
| **ImageAnt** | — | `create() -> Builder` | `StackPane` | src, width, height, alt, preview, fallback, placeholder, objectFit, borderRadius, controllerOf | 增强型 ImageView，支持加载失败 fallback、加载中 placeholder、 |
| **InputNumberAnt** | — | `create() -> Builder` | `HBox` | value, min, max, step, readOnly, size, onChange, precision, prefix, suffix, bindValue | 带 +/- 按钮的数字输入控件，支持范围限制、步长、精度控制， |
| **ListAnt** | — | `create() -> Builder` | `VBox` | item, items, bordered, split, header, footer, loading | 通用列表容器，支持头像 + 标题 + 描述 + 操作按钮的列表项， |
| **MenuAnt** | — | `create() -> Builder` | `Pane` | controllerOf, childLevel, addItem, subMenu, mode, theme, collapsed, selectedKey, onSelect, expandMode, expandedKeys, onExpandChange, endSubMenu, controller, toggleCollapsed, expandKey, collapseKey, defaultExpanded | JFXium 高级菜单组件 - 对标 Ant Design Menu。 |
| **ProgressAnt** | — | — | `HBox` | bar, circle, progress, size, status, showInfo, controllerOf | JFXium Progress 进度组件 - 对标 Ant Design Progress。 |
| **QRCodeAnt** | — | `create() -> Builder` | `StackPane` | value, size, color, bgColor, icon, bordered, controllerOf | JFXium 二维码组件 - 对标 Ant Design QRCode |
| **RateAnt** | — | `create() -> Builder` | `HBox` | count, value, defaultValue, allowHalf, size, onChange, onHoverChange, bindValue | 星级评分控件，用多个图标（默认星星）表示 0-N 分， |
| **ResizablePanelAnt** | — | `create() -> Builder` | `StackPane` | content, mode, minWidth, minHeight, prefWidth, prefHeight, maxWidth, maxHeight, grow, growX, growY | 可拖拽边缘调整大小的容器，包装 StackPane， |
| **ResultAnt** | — | `create() -> Builder` | `VBox` | status, title, subTitle, extra, extraButton, success, error, info, warning | 操作结果反馈页，包含状态图标 + 标题 + 副标题 + 可选操作按钮， |
| **SegmentedAnt** | — | `create() -> Builder` | `HBox` | option, options, selected, size, block, onChange, bindValue, controllerOf | 轻量级切换控件，类似 Tabs 但更紧凑，常用于视图切换、 |
| **SelectableTextAnt** | — | `create(String text) -> Builder`<br>`create() -> Builder` | `Node` | multiline, wrap, maxWidth, prefWidth, type, bordered, focusHalo | 看起来像一段普通 Label 文本，但用户可以拖动选中、Ctrl+C 复制。 |
| **SkeletonAnt** | — | `create() -> Builder` | `StackPane` | variant, width, height, animated, noAnimation, paragraph, avatarText | 数据加载前的占位动画，模拟真实内容的形状， |
| **SliderAnt** | — | `create() -> Builder` | `Node` | min, max, value, defaultValue, step, range, vertical, reverse, dots, marks, tipFormatter, tooltipVisible, onChange, onChangeComplete, bindValue | JFXium Slider 组件 - 全面对标 Ant Design Slider。 |
| **SpinAnt** | — | `create() -> Builder` | `VBox` | tip, size, indicator, fullscreen, overlayOpacity, overlay, show, hide | 加载状态指示器，支持三种动画样式（spinner/dots/bars）， |
| **StatisticAnt** | — | `create() -> Builder` | `VBox` | title, value, prefix, suffix, precision, size, controllerOf | 展示统计数字（带标题、前缀、后缀、精度控制）， |
| **StepsAnt** | — | `create() -> Builder` | `Node` | step, current, direction, size, controller, next, prev | 引导用户按流程完成任务的导航条，常用于注册流程、订单状态、 |
| **SurfaceAnt** | — | `create() -> Builder` | `VBox` | title, extra, content, children, bordered, shadow, gap | 轻量的内容承载面板，带可选标题 + 额外操作区， |
| **SwitchAnt** | — | `create() -> Builder` | `HBox` | selected, checkedText, uncheckedText, shape, onChange, bindValue | 二态切换开关（开/关），用于切换某个配置项的启用状态， |
| **TabsAnt** | — | `create() -> Builder` | `Node` | controllerOf, tab, type, size, tabPlacement, centered, extraLeft, extraRight, onChange, activeIndex, activeKey, controller, selectByKey, next, prev | 内容切换标签页,支持多个标签页之间的内容切换, |
| **TagAnt** | — | `create(String text) -> Builder`<br>`create() -> Builder` | `HBox` | modify, text, type, size, shape, closable, bordered, noBorder, color, onClose, apply | 小型语义化标签，用于标记状态、分类、属性等， |
| **TimePickerAnt** | — | `create() -> Builder` | `StackPane` | controllerOf, value, format, allowClear, size, onChange, bindValue, editable, showPanel, hidePanel | 只读输入框 + 弹出时间面板，用于日程安排、提醒设置、营业时间配置等场景。 |
| **TimelineAnt** | — | `create() -> Builder` | `VBox` | mode, pending, item | 垂直时间线展示事件序列，常用于操作日志、订单流程、 |
| **TitleBarAnt** | `AbstractHBoxAnt` | `create() -> TitleBarAnt` | `TitleBarAnt` | title, icon, stage, left, center, right, windowControls, controls, draggable | StageStyle.UNDECORATED 自绘外壳的顶部条 —— |
| **TransferAnt** | — | `create() -> <T> Builder<T>` | `HBox` | dataSource, targetKeys, titles, render, onChange, onSelectChange, showSearch, bindValue | 双列列表控件，用于在两个列表之间移动数据项， |
| **TreeSelectAnt** | — | `create() -> Builder` | `HBox` | disabled, expanded, hasChildren, tree, multiple, onSelect, onMultipleSelect, bindValue | 下拉树形选择器，支持多级嵌套节点，常用于组织架构选择、 |
| **UploadAnt** | — | `create() -> Builder` | `VBox` | type, listType, multiple, directory, showUploadList, noUploadList, accept, buttonText, dragText, hintText, onChange, onRemove | 文件上传控件，支持点击选择或拖拽上传， |
| **VBarAnt** | `AbstractVBoxAnt` | `create() -> VBarAnt` | `VBarAnt` | top, center, bottom, gap, alignment, compact | VBarAnt - 竖向条状容器（上 / 中 / 下 三段式）。 |
| **WatermarkAnt** | — | `create() -> Builder` | `StackPane` | content, text, image, customNode, rotate, opacity, fontSize, gapX, gapY, imageWidth, imageHeight, color, fontFamily, fontWeight, fontGap, preventRemoval, wrap, controllerOf | WatermarkAnt - 对标 Ant Design / Element Plus Watermark。 |
| **WindowControlAnt** | `AbstractHBoxAnt` | `create() -> WindowControlAnt` | `WindowControlAnt` | target, minimizeVisible, maximizeVisible, closeVisible, maximized, requestClose | 自绘窗口外壳（StageStyle.UNDECORATED / TRANSPARENT）右侧的 |

## org.openkawu.jfxium.component.control

| 组件 | extends | 工厂 | build() | 关键 API | 定位 |
|---|---|---|---|---|---|
| **AccordionAnt** | — | `create() -> Builder` | `VBox` | pane, activeKey, activeKeys | CollapseAnt 的互斥折叠快捷入口。始终以 accordion 模式运行 |
| **ButtonAnt** | `Button` | `create() -> ButtonAnt`<br>`create(String text) -> ButtonAnt`<br>`create(String text, Node graphic) -> ButtonAnt` | `ButtonAnt` | link, compactLink, iconOnly, type, size, shape, rounded, square, loading, ghost, block, text, icon, contentDisplay, onClick, command | 项目最基础的交互控件，继承自 Button， |
| **CanvasAnt** | `Canvas` | `create() -> CanvasAnt` | `CanvasAnt` | size, onDraw, gc, clear, fill, stroke, redraw | 2D 绘图容器，继承自 JavaFX Canvas。 |
| **CheckBoxAnt** | `CheckBox` | `create() -> CheckBoxAnt`<br>`create(String text) -> CheckBoxAnt` | `CheckBoxAnt` | text, selected, indeterminate, allowIndeterminate, size, shape, onChange, bindValue | 多选控件，继承自 CheckBox， |
| **ChoiceBoxAnt** | `ChoiceBox` | `create() -> <T> ChoiceBoxAnt<T>` | `ChoiceBoxAnt<T>` | items, value, onSelect, size, bindValue | 比 ComboBoxAnt 更轻量的下拉选择控件，继承自 ChoiceBox， |
| **ColorPickerAnt** | `ColorPicker` | `create() -> ColorPickerAnt`<br>`create(Color value) -> ColorPickerAnt` | `ColorPickerAnt` | value, onChange, size, bindValue, promptText | 颜色选择控件，支持色板选择、十六进制输入、透明度调节。 |
| **ComboBoxAnt** | `ComboBox` | `create() -> <T> ComboBoxAnt<T>` | `ComboBoxAnt<T>` | items, value, placeholder, editable, size, onChange, bindValue | 下拉选择控件，继承自 ComboBox， |
| **DatePickerAnt** | `DatePicker` | `create() -> DatePickerAnt` | `DatePickerAnt` | placeholder, value, editable, showWeekNumbers, size, onChange, bindValue | 日期选择控件，继承自 DatePicker， |
| **HyperlinkAnt** | `Hyperlink` | `create(String text) -> HyperlinkAnt`<br>`create() -> HyperlinkAnt` | `HyperlinkAnt` | text, onClick, underline, visited | 文本超链接，支持点击跳转、回调、禁用状态。 |
| **IconAnt** | — | — | — | symbol, path | JFXium 极简内置图标。两种渲染模式： |
| **InputAnt** | `TextField` | `create() -> InputAnt`<br>`create(String text) -> InputAnt`<br>`createPassword() -> PasswordBuilder` | `HBox` | placeholder, text, size, status, readOnly, bindValue, createPassword, onChange, disabled | 项目最基础的文本输入控件，继承自 TextField， |
| **LabelAnt** | `Label` | `create() -> LabelAnt`<br>`create(String text) -> LabelAnt`<br>`create(String text, Node graphic) -> LabelAnt` | `LabelAnt` | text, type, secondary, success, warning, danger, disabledColor, wrap, graphic, contentDisplay, align, strong, italic, underline, delete, code, mark, copyable | 项目最基础的文本控件，补齐「连最简单的 Label 都没有封装」的缺口。 |
| **ListViewAnt** | `ListView` | `create() -> <T> ListViewAnt<T>` | `ListViewAnt<T>` | items, placeholder, selectionMode, onSelect, onDoubleClick, fixedCellSize, editable | 列表选择控件，继承自 ListView， |
| **MentionsAnt** | `TextArea` | `create() -> MentionsAnt` | `MentionsAnt` | option, options, prefix, placeholder, rows, onSelect, onChange, bindValue | 输入时 @ 提及用户/对象的文本框，输入触发符（默认 @）后弹出候选列表， |
| **MenuBarAnt** | `MenuBar` | `create() -> MenuBarAnt` | — | menu, clearMenus, item, disabled, accelerator, divider, subMenu, endMenu, endSubMenu | 窗口顶部系统菜单栏（File / Edit / View / Help...）， |
| **MenuButtonAnt** | `MenuButton` | `create() -> MenuButtonAnt`<br>`create(String text) -> MenuButtonAnt` | `MenuButtonAnt` | text, size, rounded, square, icon, contentDisplay, arrowStyle, noArrow, item, itemDisabled, separator, add, focusTraversable | 外观像普通按钮，点击后弹出下拉菜单。在一组动作中做出选择。 |
| **PaginationAnt** | `Pagination` | `create() -> PaginationAnt`<br>`create(int pageCount) -> PaginationAnt` | `PaginationAnt` | controllerOf, next, prev, first, last, pageCount, currentPage, maxPageIndicatorCount, onChange | 页码导航控件，用于对大量数据分页展示。对标 Ant Design Pagination。 |
| **RadioButtonAnt** | `RadioButton` | `create() -> RadioButtonAnt`<br>`create(String text) -> RadioButtonAnt` | `RadioButtonAnt` | text, selected, size, shape, toggleGroup, onChange, bindValue | 单选控件，继承自 RadioButton， |
| **SearchInputAnt** | `AbstractStackPaneAnt` | `create() -> SearchInputAnt` | — | placeholder, text, size, allowClear, onChange, onSearch, bindValue, clear, input, clearButton | 文本输入的「可清除」变体。InputAnt 继承自 javafx.scene.text.TextField |
| **SeparatorAnt** | `Separator` | `create() -> SeparatorAnt`<br>`create(Orientation orientation) -> SeparatorAnt` | `SeparatorAnt` | orientation, halignment, valignment | 视觉分隔线控件，继承自 Separator， |
| **SpinnerAnt** | `Spinner` | `create() -> SpinnerAnt`<br>`create(double min, double max, double initialValue, double stepSize) -> SpinnerAnt` | `SpinnerAnt` | value, min, max, step, precision, editable, placeholder, onChange, bindValue, size, fromString | SpinnerAnt 就是 Spinner<Double>， |
| **SplitButtonAnt** | `SplitMenuButton` | `create() -> SplitButtonAnt`<br>`create(String text) -> SplitButtonAnt` | `SplitButtonAnt` | text, size, arrowStyle, rounded, square, icon, contentDisplay, onClick, item, itemDisabled, separator, add, focusTraversable | 组合按钮——左侧主体是普通按钮（触发默认动作），右侧带小箭头点击弹出下拉菜单 |
| **StatusBarAnt** | `HBox` | `create() -> StatusBarAnt`<br>`create(String info, String status) -> StatusBarAnt` | `StatusBarAnt` | info, status, progress, action, left, center, right, updateInfo, updateProgress, updateStatus | 窗口底部信息与操作栏，左侧信息 + 中间进度 + 右侧状态/操作。 |
| **TableAnt** | `TableView` | `create() -> <T> TableAnt<T>` | `TableAnt<T>` | column, numberColumn, booleanColumn, nodeColumn, actionColumn, columnWidth, columnMinWidth, columnMaxWidth, columnResizable, columnSortable, columnSorter, columnAlign, headerAlign, contentAlign, columnVisible, action, type, danger | 数据表格控件，继承自 TableView， |
| **TextAreaAnt** | `TextArea` | `create() -> TextAreaAnt`<br>`create(String text) -> TextAreaAnt` | `TextAreaAnt` | placeholder, text, rows, wrapText, editable, onChange, bindValue, readOnly | 多行文本输入控件，继承自 TextArea， |
| **TitledPaneAnt** | `TitledPane` | `create() -> TitledPaneAnt`<br>`create(String title) -> TitledPaneAnt`<br>`create(String title, Node content) -> TitledPaneAnt` | `TitledPaneAnt` | title, content, expanded, animated, collapsible | 可折叠的标题 + 内容面板，继承自 TitledPane， |
| **ToggleButtonAnt** | `ToggleButton` | `create() -> ToggleButtonAnt`<br>`create(String text) -> ToggleButtonAnt` | `ToggleButtonAnt` | mandatoryGroup, text, icon, contentDisplay, size, shape, rounded, square, selected, toggleGroup, onAction, onChange, bindValue | 具有「按下 / 弹起」两态切换的按钮（独立或加入 ToggleGroup 形成互斥组）。 |
| **ToolBarAnt** | `ToolBar` | `create() -> ToolBarAnt`<br>`create(Orientation orientation) -> ToolBarAnt` | `ToolBarAnt` | orientation, button, item, divider, spacer, addButton, addItem, addDivider, addSpacer, clear | 窗口顶部可定制工具栏，支持图标按钮组、溢出菜单、可拖拽自定义。 |
| **TooltipAnt** | `Tooltip` | `create(String text) -> TooltipAnt` | `TooltipAnt` | text, styleClass, style, delay, duration, hideDelay, install | 悬浮提示气泡，继承自 Tooltip， |
| **TreeAnt** | `TreeView` | `create() -> <T> TreeAnt<T>`<br>`create(T rootValue, TreeItem<T>... children) -> <T> TreeAnt<T>` | `TreeAnt<T>` | leaf, node, root, showRoot, valueExtractor, onSelect | 层级数据展示控件，提供节点工厂方法（#leaf / #node）实现声明式树结构构建。 |
| **TreeTableAnt** | `TreeTableView` | `create() -> <T> TreeTableAnt<T>` | `TreeTableAnt<T>` | column, root, onSelect, multiSelect, expandAll, collapseAll, expandToLevel, child, endChild, endRoot | 表格 + 树形结构的结合体，支持层级展开/折叠， |
| **TypographyAnt** | — | — | `Label` | ellipsis, rows, type, copyable, strong, italic, underline, delete, code, mark, title, paragraph, text | 富文本排版工厂,提供三种 Builder 入口(TitleBuilder / ParagraphBuilder / TextBuilder), |

## org.openkawu.jfxium.component.layout

| 组件 | extends | 工厂 | build() | 关键 API | 定位 |
|---|---|---|---|---|---|
| **AnchorPaneAnt** | `AbstractAnchorPaneAnt` | `create() -> AnchorPaneAnt` | — | — | 绝对定位容器，继承自 JavaFX AnchorPane。 |
| **BorderPaneAnt** | `AbstractBorderPaneAnt` | `create() -> BorderPaneAnt` | — | — | BorderPaneAnt - 继承式 BorderPane 容器。 |
| **DividerAnt** | — | `create() -> Builder` | `Node` | text, orientation, vertical, position | JFXium 分割线组件 - 对标 Ant Design Divider。 |
| **FlexAnt** | — | `create() -> Builder`<br>`createVertical() -> Builder` | `Pane` | createVertical, direction, justify, align, wrap, gap, rowGap, columnGap, children, child | JFXium 弹性布局组件 - 对标 Ant Design Flex / CSS Flexbox |
| **FlowPaneAnt** | `AbstractFlowPaneAnt` | `create() -> FlowPaneAnt`<br>`create(Node... children) -> FlowPaneAnt` | — | — | FlowPaneAnt - 继承式 FlowPane 容器（M19.36 引入）。 |
| **GridAnt** | — | `create() -> Builder` | `VBox` | of, row, col, span, offset, xs, sm, md, lg, xl, xxl, xsOffset, smOffset, mdOffset, lgOffset, xlOffset, xxlOffset, responsive | JFXium 24 列栅格系统组件 - 对标 Ant Design Grid (Row/Col)。 |
| **HBoxAnt** | `AbstractHBoxAnt` | `create() -> HBoxAnt`<br>`create(Node... children) -> HBoxAnt` | — | — | HBoxAnt - 继承式 HBox 容器（M19.36 引入）。 |
| **ScrollPaneAnt** | `AbstractScrollPaneAnt` | `create() -> ScrollPaneAnt`<br>`create(Node content) -> ScrollPaneAnt` | — | padding | ScrollPaneAnt - 继承式滚动容器（M19.36 升级为双工厂模式 + 改名自 ScrollContainerAnt）。 |
| **SpaceAnt** | — | `create() -> Builder`<br>`createVertical() -> Builder` | `Pane` | createVertical, direction, size, align, split, children | JFXium 间距组件 - 对标 Ant Design Space。 |
| **SplitPaneAnt** | `AbstractSplitPaneAnt` | `create() -> SplitPaneAnt` | — | — | SplitPaneAnt - 继承式 SplitPane 容器（M19.36 升级为双工厂模式）。 |
| **StackPaneAnt** | `AbstractStackPaneAnt` | `create() -> StackPaneAnt`<br>`create(Node... children) -> StackPaneAnt` | — | — | StackPaneAnt - 继承式 StackPane 容器（M19.36 引入）。 |
| **TextFlowAnt** | `AbstractTextFlowAnt` | `create() -> TextFlowAnt`<br>`create(Node... children) -> TextFlowAnt` | — | — | TextFlowAnt - 继承式 TextFlow 容器。 |
| **TilePaneAnt** | `AbstractTilePaneAnt` | `create() -> TilePaneAnt` | — | — | 平铺排列容器，继承自 JavaFX TilePane。 |
| **VBoxAnt** | `AbstractVBoxAnt` | `create() -> VBoxAnt`<br>`create(Node... children) -> VBoxAnt` | — | — | 垂直布局容器的"双工厂模式"——既能当工厂用，也能被业务继承。 |

## org.openkawu.jfxium.component.overlay

| 组件 | extends | 工厂 | build() | 关键 API | 定位 |
|---|---|---|---|---|---|
| **AlertAnt** | `Alert` | — | — | info, warning, error, confirm, title, content, okText, cancelText, onOk, onCancel, onResult, graphic, showAndWaitForOk | 独立 Stage 模态对话框。调用方可自由选择两种风格： |
| **ContextMenuAnt** | — | `create() -> Builder` | `ContextMenuResult` | divider, target, item, disabled, accelerator, onSelect, onSelectItem, show, hide | 在目标元素上右键弹出的上下文菜单，支持分组、图标、快捷键提示、 |
| **DesktopNotificationAnt** | — | `create() -> Builder` | `DesktopResult` | title, description, content, type, placement, duration, closable, closeOnClick, onClose, onClick, show, success, error, warning, info, changed | 与 NotificationAnt（锚定应用窗口的 javafx.stage.Popup） |
| **DrawerAnt** | — | `create() -> Builder` | `DrawerResult` | title, content, placement, width, height, maskClosable, onClose, footer, size, extra, keyboard, closePlacement, open, close | 从屏幕边缘滑入的覆盖式面板，适合详情展示、表单编辑、筛选面板等场景。 |
| **DropdownAnt** | — | `create() -> Builder` | `DropdownResult` | divider, trigger, showArrow, item, items, onSelect, onSelectItem, placement, show, hide | 点击触发节点弹出菜单列表，支持图标、禁用、分隔线。 |
| **MessageAnt** | — | `create() -> Builder` | `MessageResult` | content, type, duration, position, show, close, success, error, warning, info, loading, changed | 全局顶部 / 底部 / 中心短暂提示，自动消失， |
| **ModalAnt** | — | `create() -> Builder` | `ModalResult` | title, content, maskClosable, width, height, maxHeight, minHeight, onClose, footer, noFooter, centered, keyboard, closePlacement, confirmLoading, okText, cancelText, onOk, bindToLocale | 屏幕中央弹出的模态对话框，适合需要用户明确响应的场景（确认、警告、表单输入）。 |
| **NotificationAnt** | — | `create() -> Builder` | `NotificationResult` | title, description, content, type, placement, duration, closable, onClose, onClick, show, success, error, warning, info | 在窗口四个角落弹出通知卡片（标题 + 描述 + 可选自定义内容）， |
| **PopconfirmAnt** | — | `create() -> Builder` | `Popconfirm` | title, description, okText, cancelText, onConfirm, onCancel, target, placement, show, hide | 在目标元素旁弹出轻量确认面板，包含标题 + 描述 + 确认/取消按钮， |
| **PopoverAnt** | — | `create() -> Builder` | `Popover` | title, content, target, placement, trigger, onOpenChange, show, hide, close | 在目标元素旁弹出内容卡片（标题 + 自定义内容）， |
| **PromptDialogAnt** | — | `create() -> PromptDialogAnt` | `PromptDialogResult` | title, message, defaultValue, placeholder, okText, cancelText, onConfirm, onCancel, width, open, close | 轻量级弹框，用于快速获取用户输入（文本）。 |

## org.openkawu.jfxium.layout

| 组件 | extends | 工厂 | build() | 关键 API | 定位 |
|---|---|---|---|---|---|
| **AppShellAnt** | — | `create() -> Builder` | `BorderPane` | header, sider, content, footer, collapsible, collapsed, collapsedWidth, breakpoint, trigger, headerBackground, siderBackground, contentBackground, footerBackground, onCollapseChange, buildResult, toggle, collapsedProperty | JFXium 应用骨架组件。 |
| **LayoutAnt** | — | `create() -> Builder` | `BorderPane` | header, sider, content, footer | JFXium 页面布局组件 - 对标 Ant Design Layout |

## org.openkawu.jfxium.template

| 组件 | extends | 工厂 | build() | 关键 API | 定位 |
|---|---|---|---|---|---|
| **CrudTemplate** | — | `create() -> Builder` | `BorderPane` | title, topLeft, topRight, body, bottomLeft, bottomRight, bordered, background, topbarSpacing, bottombarSpacing, sectionGap | admin 后台 90% 业务页的通用模板。 |
| **DashboardTemplate** | — | `create() -> Builder` | `VBox` | welcome, stat, statColumns, bottomLeft, bottomRight, leftRatio, sectionGap, padding | 标准 dashboard 布局——欢迎语 + N 列统计卡矩阵 + 底部多区域内容。 |
| **FilterBarAnt** | — | `create() -> Builder` | `HBox` | spacing, padding, search, filter, action, actionPrimary, actionNode | FilterBarAnt - admin 列表页"上栏工具条"标准化组件。 |
| **LaunchPadTemplate** | — | `create() -> Builder` | `VBox` | title, description, columns, gap, onAction, item | LaunchPadTemplate - 首页/控制台常用快捷入口模板。 |
| **LoginTemplate** | — | `create() -> Builder` | `BorderPane` | brandName, tagline, feature, features, copyright, bannerWidth, formTitle, formSubtitle, usernamePlaceholder, passwordPlaceholder, submitText, showRememberMe, onSubmit, onForgot, onRegister | admin 后台最常见的登录页骨架——左侧品牌 banner（渐变 + 卖点）， |
| **PageTemplate** | — | `create() -> Builder` | `VBox` | title, description, body, section, sections, headerGap, sectionGap, headerToBodyGap, padding, background | 「大标题 + 描述 + 内容区」最简模板。覆盖 admin / showcase / 文档 / 设置等 |
| **ProjectChangelogTemplate** | — | `create() -> Builder` | `VBox` | changelog, ChangeEntry, title, description, onAction, version | ProjectChangelogTemplate - 项目变更日志模板。 |
| **ProjectConsoleTemplate** | — | `create() -> Builder` | `BorderPane` | userMenu, headerActions, statusBar, standardMenuBar, SidebarLink, SidebarSection, SidebarResult, sidebarLink, sidebarSection, sidebarPane, brand, brandSubtitle, brandIcon, headerTop, headerCenter, headerRight, menuBar, headerLeft | ProjectConsoleTemplate - 工程项目控制台壳模板。 |
| **ProjectDashboardTemplate** | — | `create() -> Snapshot`<br>`create() -> Builder` | `VBox` | demo, pageTitle, pageSubtitle, pageDescription, currentUser, version, branch, buildStatus, mode, templateCount, componentCount, updatedAt, completion, icon, mergeFrom, title, description, onAction | ProjectDashboardTemplate - 工程项目首页成品模板。 |
| **ProjectFeatureTemplate** | — | `create() -> Builder` | `VBox` | title, description, columns, gap, onAction, feature | ProjectFeatureTemplate - 项目特性/亮点网格展示模板。 |
| **ProjectHeroTemplate** | — | `create() -> Builder` | `VBox` | userMenu, title, subtitle, description, icon, status, meta, action, actions | ProjectHeroTemplate - 工程项目展示首屏门面模板。 |
| **ProjectOverviewTemplate** | — | `create() -> Builder` | `VBox` | title, description, status, tech, metric, meta, milestone, activity, progress, onAction | ProjectOverviewTemplate - 工程项目展示模板。 |
| **ProjectQuickStartTemplate** | — | `create() -> Builder` | `VBox` | title, description, step | ProjectQuickStartTemplate - 快速上手步骤模板。 |
| **ProjectReleaseTemplate** | — | `create() -> Builder` | `VBox` | title, description, status, meta, release, change, readiness, onAction | ProjectReleaseTemplate - 工程发布节奏模板。 |
| **ProjectShowcaseTemplate** | — | `create() -> Builder` | `VBox` | title, description, hero, section, sections | ProjectShowcaseTemplate - 工程项目展示首页模板。 |
| **ShowcaseSectionTemplate** | — | `create() -> Builder` | `VBox` | title, description, body | ShowcaseSectionTemplate - 示例页/文档页的通用 section 壳。 |
| **WorkspaceSettingsTemplate** | — | `create() -> Builder` | `VBox` | drawer, themeManager, watermarkVisibleSupplier, onWatermarkVisibleChanged, title, description, fromString | WorkspaceSettingsTemplate - 工作台外观设置抽屉内容模板。 |
| **WorkspaceTemplate** | — | `create() -> Builder` | `BorderPane` | userMenu, brand, brandSubtitle, brandIcon, header, headerLeft, headerTop, headerCenter, headerRight, headerGap, sider, siderWidth, content, footer, collapsible, collapsed, collapsedWidth, trigger | 在 AppShellAnt 这种低层应用骨架上，再补一层“可直接拿去做生产系统”的 |
