# JFXium vs JavaFX 原生控件对比

> 本文档用于快速对比 **JFXium 封装组件** 与 **JavaFX 原生控件** 的对应关系，
> 帮助开发者理解 JFXium 在原生基础上提供了哪些增强能力。

---

## 一、核心控件（control）

| JavaFX 原生 | JFXium 封装 | 作用简述 | JFXium 增强点 |
|---|---|---|---|
| `Button` | `ButtonAnt` | 按钮 | 类型（PRIMARY/DEFAULT/DASHED/DANGER/LINK）、尺寸、loading、图标、圆角 |
| `CheckBox` | `CheckBoxAnt` | 复选框 | 尺寸、indeterminate、回调 |
| `ComboBox` | `ComboBoxAnt` | 下拉选择框 | 占位符、禁用、回调 |
| `DatePicker` | `DatePickerAnt` | 日期选择器 | 占位符、回调、格式 |
| `Hyperlink` | `HyperlinkAnt` | 超链接 | 点击回调、禁用、下划线控制 |
| `Label` | `LabelAnt` | 文本标签 | 类型（PRIMARY/SUCCESS/WARNING/DANGER）、尺寸、粗体、截断、可复制 |
| `ListView` | `ListViewAnt` | 列表视图 | 选择回调、空状态 |
| `PasswordField` | `InputAnt.createPassword()` | 密码输入 | 密码模式 + 可见切换按钮 |
| `RadioButton` | `RadioAnt` | 单选按钮 | 组管理、尺寸、回调 |
| `Separator` | `DividerAnt` | 分割线 | 带文本、文本位置、垂直/水平 |
| `Slider` | `SliderAnt` | 滑块 | 范围、步长、刻度、回调 |
| `Spinner` | `SpinnerAnt` | 数值微调器 | 范围、步长、可编辑、回调 |
| `TableView` | `TableAnt` | 表格 | 列定义、分页、选择回调、空状态 |
| `TextArea` | `TextAreaAnt` | 多行文本输入 | 占位符、行数、只读、回调 |
| `TextField` | `InputAnt` | 单行文本输入 | 占位符、尺寸、前缀/后缀、回调、清空按钮 |
| `TitledPane` | `TitledPaneAnt` | 标题面板 | 可折叠、动画、回调 |
| `ToggleButton` | `ToggleButtonAnt` | 切换按钮 | 选中回调 |
| `ToolBar` | `ToolBarAnt` | 工具栏 | 图标按钮组、分隔线、弹性填充、垂直/水平 |
| `Tooltip` | `TooltipAnt` | 提示框 | 位置、延迟、富文本内容 |
| `TreeView` | `TreeAnt` | 树形控件 | 节点勾选、选择回调、搜索过滤 |
| `TreeTableView` | `TreeTableAnt` | 树形表格 | 列定义、层级展开/折叠、选择回调 |
| `MenuBar` | `MenuBarAnt` | 顶部系统菜单栏 | 多级菜单、快捷键、回调 |
| `ColorPicker` | `ColorPickerAnt` | 颜色选择器 | 回调、默认值、调色板 |
| `Pagination` | `PaginationAnt` | 分页控件 | 页码、总数、快速跳转、回调 |
| `Accordion` | `AccordionAnt` | 手风琴面板 | 多面板、展开回调 |
| `MenuButton` | `MenuButtonAnt` | 菜单按钮 | 弹出菜单、图标、回调 |
| `SplitMenuButton` | `SplitButtonAnt` | 分离式菜单按钮 | 默认操作 + 弹出菜单 |
| `TextField` | `MentionsAnt` | 提及输入框 | @提及、自动补全 |
| — | `IconAnt` | 图标 | 内置图标集、尺寸、颜色 |
| `Text` | `TypographyAnt` | 排版 | 标题/段落/文本样式、可复制、可编辑 |

---

## 二、布局容器（layout）

| JavaFX 原生 | JFXium 封装 | 作用简述 | JFXium 增强点 |
|---|---|---|---|
| `BorderPane` | `BorderPaneAnt` | 五区位布局（上/下/左/右/中） | 双工厂模式（可继承）、链式 API、背景快捷设置 |
| `FlowPane` | `FlowPaneAnt` | 流式布局（自动换行） | 双工厂模式、链式 API |
| `GridPane` | `GridPaneAnt` | 二维网格布局 | 双工厂模式、cell(row,col) 快捷方法 |
| `HBox` | `HBoxAnt` | 水平布局 | 双工厂模式、链式 API、背景快捷设置 |
| `ScrollPane` | `ScrollPaneAnt` | 滚动面板 | 双工厂模式、fitToWidth/Height 链式设置 |
| `SplitPane` | `SplitPaneAnt` | 可拖拽分隔面板 | 双工厂模式、方向快捷设置 |
| `StackPane` | `StackPaneAnt` | 叠层布局 | 双工厂模式、对齐快捷设置 |
| `VBox` | `VBoxAnt` | 垂直布局 | 双工厂模式、链式 API、背景快捷设置 |
| `TilePane` | `TilePaneAnt` | 平铺布局（缩略图网格）| 行列数、方向、间距、链式添加子节点 |
| `AnchorPane` | `AnchorPaneAnt` | 绝对定位布局 | 四边锚定、居中、全填充、链式 API |
| — | `DividerAnt` | 分割线（增强版 Separator）| 带文本、文本位置、垂直/水平 |
| — | `FlexAnt` | 弹性布局（CSS Flexbox） | justify/align/wrap/gap、两端对齐 |
| — | `GridAnt` | 24 列栅格系统 | 响应式断点（xs/sm/md/lg/xl/xxl） |
| — | `ScrollContainerAnt` | 统一滚动容器 | 自动 viewport 包装、padding 控制 |
| — | `SpaceAnt` | 间距组件 | 水平/垂直间距、分隔线、对齐 |

---

## 三、复合组件（composite）

| JavaFX 原生 | JFXium 封装 | 作用简述 | JFXium 增强点 |
|---|---|---|---|
| — | `AlertAnt` | 嵌入式警告提示 | 类型（success/info/warning/error）、可关闭、描述、操作按钮 |
| — | `AnchorAnt` | 锚点导航 | 多级锚点、方向、选中高亮、滚动联动 |
| — | `AutoCompleteAnt` | 自动完成输入框 | 泛型选项、过滤、自定义渲染 |
| — | `AvatarAnt` | 头像 | 图片/文字/图标三种模式、多尺寸、圆形/方形 |
| — | `BackTopAnt` | 回到顶部 | 滚动监听、位置配置、动画 |
| — | `BadgeAnt` | 徽标数 | 数字/状态点、位置偏移 |
| — | `BarAnt` | 通用栏（标题栏/操作栏） | 标题 + extra + 按钮组 + 自适应布局 |
| — | `CalendarAnt` | 日历 | 月/年模式、选中回调、范围选择 |
| — | `CardAnt` | 卡片容器 | 标题、操作区、封面、悬浮效果、边框 |
| — | `CarouselAnt` | 轮播图 | 自动播放、切换效果、指示器 |
| — | `CascaderAnt` | 级联选择器 | 多级嵌套、搜索过滤 |
| — | `CodeBlockAnt` | 代码块 | 语法高亮、行号、复制按钮 |
| — | `CollapseAnt` | 折叠面板 | 多面板、手风琴模式、展开动画 |
| — | `DescriptionsAnt` | 描述列表 | 水平/垂直布局、多尺寸、带边框、多列 |
| — | `EmptyAnt` | 空状态 | 图标、描述、操作按钮 |
| — | `FloatButtonAnt` | 浮动按钮 | 位置、类型、图标、提示 |
| — | `FormAnt` | 表单 | 字段定义、校验、布局、提交回调 |
| — | `ImageAnt` | 图片 | 加载失败 fallback、占位符、圆角、预览 |
| — | `InputNumberAnt` | 数字输入框 | 范围、步长、精度、前缀/后缀 |
| — | `ListAnt` | 高级列表 | 头像+标题+描述+操作、可点击、分隔线 |
| — | `MenuAnt` | 侧边导航菜单 | 多级子菜单、图标、选中高亮、折叠 |
| — | `ProgressAnt` | 进度展示 | 线形/环形、状态色、尺寸 |
| — | `QRCodeAnt` | 二维码 | 内容、尺寸、颜色、容错级别 |
| — | `RateAnt` | 评分 | 半星、只读、回调、自定义字符 |
| — | `ResizablePanelAnt` | 可拖拽调整面板 | 水平/垂直/BOTH 拖拽、最小/最大尺寸约束 |
| — | `ResultAnt` | 结果页 | 状态图标、标题、副标题、操作按钮 |
| — | `SegmentedAnt` | 分段控制器 | 选项、尺寸、回调、默认选中 |
| — | `SelectableTextAnt` | 可选中只读文本 | 多行自动高度、无 input chrome |
| — | `SkeletonAnt` | 骨架屏 | 段落/头像/标题/按钮多种变体、动画 |
| — | `SpinAnt` | 加载中 | 三种动画样式、尺寸、提示文本、全屏/嵌入 |
| — | `StatisticAnt` | 统计数值 | 标题、数值、前缀/后缀、精度、趋势箭头 |
| — | `StepsAnt` | 步骤条 | 水平/垂直、尺寸、状态、当前步 |
| — | `SurfaceAnt` | 表面容器 | 背景、边框、阴影、圆角 |
| — | `SwitchAnt` | 开关 | 尺寸、文字标签、回调、禁用 |
| — | `TabsAnt` | 标签页 | 线形/卡片/胶囊类型、尺寸、关闭、回调 |
| — | `TagAnt` | 标签 | 颜色、可关闭、图标、边框 |
| — | `TimePickerAnt` | 时间选择器 | 时/分/秒、默认值、回调 |
| — | `TimelineAnt` | 时间线 | 左右/交替布局、圆点颜色、自定义节点 |
| — | `TransferAnt` | 穿梭框 | 左右两栏、搜索、选中、移动 |
| — | `TreeSelectAnt` | 树选择器 | 多级嵌套、搜索、回调 |
| — | `UploadAnt` | 文件上传 | 拖拽上传、多选、进度、回调 |
| — | `WatermarkAnt` | 水印 | 文字/图片、密度、旋转、透明度 |
| — | `BreadcrumbAnt` | 面包屑 | 路径导航、分隔符、点击回调 |

---

## 四、浮层/弹窗（overlay）

| JavaFX 原生 | JFXium 封装 | 作用简述 | JFXium 增强点 |
|---|---|---|---|
| `Popup` | `DropdownAnt` | 下拉菜单 | 图标、禁用、分隔线、位置、回调 |
| `Popup` | `MessageAnt` | 全局消息提示 | 类型、位置、自动消失、手动关闭 |
| `Stage`+`Popup` | `ModalAnt` | 模态对话框 | 标题、内容、footer、动画、ESC/遮罩关闭、快捷方法 |
| `Stage`+`Popup` | `DrawerAnt` | 抽屉面板 | 四方向滑入、尺寸、footer、动画、窗口跟随 |
| `Popup` | `NotificationAnt` | 全局通知 | 四角落、类型、自动消失、窗口跟随 |
| `Popup` | `PopoverAnt` | 气泡卡片 | CLICK/HOVER 触发、位置、自定义内容 |
| `Popup` | `PopconfirmAnt` | 气泡确认框 | 标题+描述+确认/取消、回调、轻量确认 |
| `ContextMenu` | `ContextMenuAnt` | 右键菜单 | 分组、图标、快捷键提示、回调 |

---

## 五、JFXium 独有（JavaFX 完全没有对应）

| JFXium 组件 | 作用简述 | 典型场景 |
|---|---|---|
| `AlertAnt` | 嵌入式警告提示 | 表单校验提示、操作结果提示 |
| `AnchorAnt` | 锚点导航 | 长页面目录导航、文档页 |
| `AutoCompleteAnt` | 自动完成 | 搜索框、城市选择 |
| `AvatarAnt` | 头像 | 用户头像、团队成员展示 |
| `BackTopAnt` | 回到顶部 | 长列表/长页面 |
| `BadgeAnt` | 徽标数 | 消息未读数、状态标记 |
| `BarAnt` | 通用栏 | 页面标题栏、操作栏 |
| `CalendarAnt` | 日历 | 日期选择、日程展示 |
| `CardAnt` | 卡片 | 信息卡片、商品卡片 |
| `CarouselAnt` | 轮播图 | 首页 Banner、图片展示 |
| `CascaderAnt` | 级联选择 | 省市区三级联动 |
| `CodeBlockAnt` | 代码块 | 文档、代码展示 |
| `CollapseAnt` | 折叠面板 | FAQ、设置分组 |
| `DescriptionsAnt` | 描述列表 | 详情页、用户资料 |
| `EmptyAnt` | 空状态 | 无数据页面 |
| `FlexAnt` | 弹性布局 | 自适应布局、导航栏 |
| `FloatButtonAnt` | 浮动按钮 | 回到顶部、快捷操作 |
| `FormAnt` | 表单 | 数据录入、配置页面 |
| `GridAnt` | 24 列栅格 | 响应式页面布局 |
| `ImageAnt` | 图片 | 带 fallback、圆角 |
| `InputNumberAnt` | 数字输入 | 数值录入、百分比 |
| `ListAnt` | 高级列表 | 用户列表、消息列表 |
| `MenuAnt` | 侧边导航 | 后台管理系统菜单 |
| `ProgressAnt` | 进度展示 | 上传进度、任务进度 |
| `QRCodeAnt` | 二维码 | 扫码登录、支付码 |
| `RateAnt` | 评分 | 商品评分、满意度评价 |
| `ResizablePanelAnt` | 可拖拽面板 | IDE 分屏、详情面板 |
| `ResultAnt` | 结果页 | 提交成功、404/500 页面 |
| `SegmentedAnt` | 分段控制 | 视图切换、筛选条件 |
| `SelectableTextAnt` | 可选中只读文本 | 日志展示、代码片段 |
| `SkeletonAnt` | 骨架屏 | 数据加载占位 |
| `SpaceAnt` | 间距 | 元素间距控制 |
| `SpinAnt` | 加载中 | 数据加载、提交等待 |
| `StatisticAnt` | 统计数值 | Dashboard 数据展示 |
| `StatusBarAnt` | 状态栏 | 底部状态栏，多区域文本/进度显示 |
| `StepsAnt` | 步骤条 | 注册流程、订单状态 |
| `SurfaceAnt` | 表面容器 | 卡片背景、面板背景 |
| `SwitchAnt` | 开关 | 功能开关、状态切换 |
| `TabsAnt` | 标签页 | 内容切换、配置分组 |
| `TagAnt` | 标签 | 分类标签、状态标签 |
| `TimePickerAnt` | 时间选择 | 日程安排、提醒设置 |
| `TimelineAnt` | 时间线 | 操作日志、版本历史 |
| `TransferAnt` | 穿梭框 | 权限分配、数据迁移 |
| `TreeSelectAnt` | 树选择器 | 组织架构选择 |
| `UploadAnt` | 文件上传 | 头像上传、附件上传 |
| `WatermarkAnt` | 水印 | 敏感信息防泄露 |
| `CanvasAnt` | 自绘图形 | 图表、游戏、自定义绘制 |
| `PromptDialogAnt` | 快速输入弹框 | 确认输入、轻量弹窗 |

---

## 六、对比总结

| 维度 | JavaFX 原生 | JFXium |
|---|---|---|
| **控件数量** | ~30 个基础控件 | **94+** 个组件（含布局/复合/浮层）|
| **API 风格** | 命令式 / 属性绑定 | Builder 流式链式 API |
| **主题系统** | 需手动写 CSS | LESS + CSS 变量 + 主题切换 |
| **响应式** | 无 | GridAnt 24 列栅格 + 断点系统 |
| **国际化** | 需自行实现 | 内置 i18n 框架 |
| **动画** | 需手动实现 | 内置 Fade/Slide/Scale 动画 |
| **PC 软件刚需** | MenuBar/ToolBar/StatusBar 弱 | ✅ MenuBarAnt/ToolBarAnt/StatusBarAnt 已补齐 |
| **Web 风格组件** | 无 | Alert/Empty/Skeleton/Result 等 |
