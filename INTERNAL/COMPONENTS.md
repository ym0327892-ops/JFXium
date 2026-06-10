# JFXium 组件文档

> ⚠️ **文档归档说明**：本文档为 M18 早期版本（命名沿用初版 demo 的 `JFX*` 前缀：JFXButton / JFXInput / JFXCard / JFXForm / JFXMenu / JFXTabs / JFXTree / JFXDatePicker / JFXSlider / JFXProgress / JFXAlert）。
>
> **当前主命名已统一为 `*Ant` 后缀**（ButtonAnt / InputAnt / CardAnt / FormAnt / MenuAnt / TabsAnt / TreeAnt / DatePickerAnt / SliderAnt / ProgressAnt / AlertAnt 等），请以最新文档为准：
> - 对外参考：[docs/cn/组件参考.md](../docs/cn/组件参考.md)
> - 主跟踪文档：[PROJECT_PLAN.md](../PROJECT_PLAN.md)（含 M19/M20/M21 全部里程碑）
> - 内部速查：[SKILL.md](./SKILL.md)
>
> 本文件保留作为历史快照，M19.46 之后的新功能 / API 变更 **不在此处追踪**。

## 目录

- [JFXButton](#jfxbutton) - 按钮组件
- [JFXInput](#jfxinput) - 输入框组件
- [JFXCard](#jfxcard) - 卡片组件
- [JFXModal](#jfxmodal) - 模态框组件
- [JFXTable](#jfxtable) - 表格组件
- [JFXForm](#jfxform) - 表单组件
- [JFXMenu](#jfxmenu) - 菜单组件
- [JFXTabs](#jfxtabs) - 标签页组件
- [JFXTree](#jfxtree) - 树形组件
- [JFXDatePicker](#jfxdatepicker) - 日期选择组件
- [JFXSlider](#jfxslider) - 滑块组件
- [JFXProgress](#jfxprogress) - 进度条组件
- [JFXAlert](#jfxalert) - 警告提示组件
- [页面语义布局组件](#页面语义布局组件) - CrudTemplate / Surface / AppShell / SplitBar

---

## 页面语义布局组件

页面级布局组件用于建立清晰的背景层级：页面背景使用 `color-bg-layout`，内容面板使用 `color-bg-container`。

### CrudTemplate（M18 新增，业务模板）

> `org.openkawu.jfxium.template.CrudTemplate` —— admin 后台 90% 业务页通用三段式骨架。
> body 装什么都行：TableView / Form / Detail / Chart 等。

```java
BorderPane page = CrudTemplate.create()
    .title("用户管理")
    .topLeft(searchField, roleCombo)        // 左：筛选/搜索
    .topRight(refreshBtn, addBtn)            // 右：操作按钮
    .body(table)                             // 中：主内容
    .bottomLeft(totalLabel)                  // 左：统计文字
    .bottomRight(pagination, pageSizeCombo)  // 右：分页器
    .bordered(true)
    .build();
```

> 老 `PageAnt` 已在 M18 删除，使用 `CrudTemplate` 替代。

### SurfaceAnt

```java
VBox surface = SurfaceAnt.create()
    .title("筛选条件")
    .extra(resetButton)
    .content(form)
    .bordered(true)
    .shadow(SurfaceAnt.Shadow.SMALL)
    .build();
```

### AppShellAnt

```java
BorderPane shell = AppShellAnt.create()
    .header(header)
    .sider(menu, 240)
    .content(page)
    .footer(footer)
    .build();
```

### BarAnt（M19，横向 左/中/右 三段式）

> `BarAnt` 取代了原 `ActionBarAnt` 与 `Headers` 工厂——一个组件同时覆盖
> "左+右"、"左+中+右"两种 hbox 布局，center 不传即自动退化为二段。

```java
HBox actions = BarAnt.create()
    .left(ButtonAnt.create("刷新").build())
    .right(ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build())
    .build();

HBox header = BarAnt.create()
    .left(closeBtn)
    .center(titleLabel)         // 三段式：center 真正居中
    .right(saveBtn, cancelBtn)
    .build();
```

> 老 `ActionBarAnt` 已在 M19 删除，请用 BarAnt 替代。

### SplitPaneAnt / ResizablePanelAnt / ScrollContainerAnt

```java
SplitPane split = SplitPaneAnt.create()
    .items(leftPanel, rightPanel)
    .dividerPositions(0.3)
    .build();

StackPane panel = ResizablePanelAnt.create()
    .content(details)
    .mode(ResizablePanelAnt.Mode.HORIZONTAL)
    .prefWidth(320)
    .build();

ScrollPane scroll = ScrollContainerAnt.create()
    .content(page)
    .fitToWidth(true)
    .build();
```

> 不新增 `ToolbarAnt`，页面和面板操作区统一使用 `BarAnt`（M19 之前为 `ActionBarAnt`，已删除）。

---

## JFXButton

按钮组件，支持多种类型、尺寸和形状。

### 基本用法

```java
// 默认按钮
Button btn = JFXButton.create("Click me").build();

// 主要按钮
Button primaryBtn = JFXButton.create("Primary")
    .type(JFXButton.Type.PRIMARY)
    .build();

// 带点击事件
Button clickBtn = JFXButton.create("Click me")
    .onClick(e -> System.out.println("Clicked!"))
    .build();
```

### 按钮类型

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| `DEFAULT` | 默认样式 | 一般操作 |
| `PRIMARY` | 主要样式（蓝色） | 主要操作 |
| `OUTLINED` | 边框样式 | 次要操作 |
| `DASHED` | 虚线边框 | 添加、上传 |
| `TEXT` | 文本样式 | 链接、次要操作 |
| `LINK` | 链接样式 | 跳转、外链 |

### 按钮尺寸

| 尺寸 | 说明 |
|------|------|
| `SMALL` | 小尺寸 |
| `DEFAULT` | 默认尺寸 |
| `LARGE` | 大尺寸 |

### 按钮形状

| 方法 | 说明 |
|------|------|
| `rounded()` | 圆角按钮 |
| `square()` | 方形按钮 |

### 完整示例

```java
Button btn = JFXButton.create("Submit")
    .type(JFXButton.Type.PRIMARY)
    .size(JFXButton.Size.LARGE)
    .rounded()
    .onClick(e -> handleSubmit())
    .build();
```

---

## JFXInput

输入框组件，支持多种类型和状态。

### 基本用法

```java
// 基础输入框
TextField input = JFXInput.create()
    .placeholder("Enter text...")
    .build();

// 密码输入框
TextField password = JFXInput.create()
    .password()
    .placeholder("Enter password...")
    .build();

// 带值变化监听
TextField textInput = JFXInput.create()
    .placeholder("Enter text...")
    .onChange((oldVal, newVal) -> System.out.println("Changed: " + newVal))
    .build();
```

### 输入框类型

| 方法 | 说明 |
|------|------|
| `password()` | 密码输入框 |
| `multiline()` | 多行文本框 |

### 输入框状态

| 方法 | 说明 |
|------|------|
| `disabled(boolean)` | 禁用状态 |
| `readonly(boolean)` | 只读状态 |

---

## JFXCard

卡片组件，用于展示信息。

### 基本用法

```java
// 基础卡片
VBox card = JFXCard.create()
    .title("Card Title")
    .content("Card content goes here...")
    .build();

// 带操作的卡片
VBox actionCard = JFXCard.create()
    .title("Card with Actions")
    .content("This card has action buttons.")
    .actions(
        JFXButton.create("Edit").type(JFXButton.Type.TEXT).build(),
        JFXButton.create("Delete").type(JFXButton.Type.TEXT).build()
    )
    .build();
```

### 卡片样式

| 方法 | 说明 |
|------|------|
| `bordered(boolean)` | 显示边框 |
| `shadow(Shadow)` | 阴影效果（NONE, SM, MD, LG, XL） |
| `hoverable(boolean)` | 悬停效果 |

---

## JFXModal

模态框组件，用于显示对话框。

### 基本用法

```java
// 基础模态框
JFXModal.create()
    .title("确认删除")
    .content("确定要删除这条记录吗？")
    .onOk(() -> System.out.println("Confirmed!"))
    .show();

// 自定义按钮文本
JFXModal.create()
    .title("提示")
    .content("操作成功！")
    .okText("知道了")
    .showCancel(false)
    .show();
```

### 模态框配置

| 方法 | 说明 |
|------|------|
| `title(String)` | 设置标题 |
| `content(String)` | 设置内容文本 |
| `content(Node)` | 设置自定义内容 |
| `width(double)` | 设置宽度 |
| `okText(String)` | 确认按钮文本 |
| `cancelText(String)` | 取消按钮文本 |
| `showCancel(boolean)` | 是否显示取消按钮 |
| `closable(boolean)` | 是否显示关闭按钮 |
| `maskClosable(boolean)` | 点击遮罩是否关闭 |
| `onOk(Runnable)` | 确认回调 |
| `onCancel(Runnable)` | 取消回调 |
| `onClose(Runnable)` | 关闭回调 |

---

## JFXTable

表格组件，用于展示数据。

### 基本用法

```java
// 基础表格
TableView<Person> table = JFXTable.<Person>create()
    .column("Name", Person::getName)
    .column("Email", Person::getEmail)
    .numberColumn("Age", Person::getAge)
    .data(personList)
    .build();

// 样式表格
TableView<Person> styledTable = JFXTable.<Person>create()
    .column("Name", Person::getName)
    .column("Email", Person::getEmail)
    .booleanColumn("Active", Person::isActive)
    .nodeColumn("Action", person -> 
        JFXButton.create("Edit").type(JFXButton.Type.TEXT).build()
    )
    .data(personList)
    .striped(true)
    .bordered(true)
    .selectable(true)
    .build();
```

### 表格配置

| 方法 | 说明 |
|------|------|
| `column(String, Function)` | 添加文本列 |
| `numberColumn(String, Function)` | 添加数字列（右对齐） |
| `booleanColumn(String, Function)` | 添加布尔列（勾选框） |
| `nodeColumn(String, Function)` | 添加自定义节点列 |
| `data(List)` | 设置表格数据 |
| `striped(boolean)` | 斑马纹 |
| `bordered(boolean)` | 边框 |
| `selectable(boolean)` | 选择列 |
| `compact(boolean)` | 紧凑模式 |

---

## JFXForm

表单组件，用于收集用户输入。

### 基本用法

```java
// 垂直布局表单
VBox form = JFXForm.create()
    .item("Username", JFXInput.create().placeholder("Enter username").build())
    .item("Email", JFXInput.create().placeholder("Enter email").build())
    .itemRequired("Password", JFXInput.create().password().placeholder("Enter password").build())
    .onSubmit(data -> System.out.println("Submitted: " + data))
    .build();

// 水平布局表单
VBox horizontalForm = JFXForm.create()
    .item("Username", JFXInput.create().build())
    .item("Email", JFXInput.create().build())
    .layout(JFXForm.Layout.HORIZONTAL)
    .labelWidth(100)
    .build();
```

### 表单布局

| 布局 | 说明 |
|------|------|
| `VERTICAL` | 标签在上方 |
| `HORIZONTAL` | 标签在左侧 |
| `INLINE` | 行内排列 |

### 表单项方法

| 方法 | 说明 |
|------|------|
| `item(String, Node)` | 添加表单项 |
| `itemRequired(String, Node)` | 添加必填项 |
| `item(String, Node, String)` | 添加带帮助文本的项 |

---

## JFXMenu

菜单组件，用于创建菜单栏。

### 基本用法

```java
MenuBar menuBar = JFXMenu.create()
    .menu("File",
        JFXMenu.item("New", e -> System.out.println("New")),
        JFXMenu.item("Open", e -> System.out.println("Open")),
        JFXMenu.separator(),
        JFXMenu.item("Exit", e -> System.exit(0))
    )
    .menu("Edit",
        JFXMenu.item("Cut", e -> System.out.println("Cut")),
        JFXMenu.item("Copy", e -> System.out.println("Copy"))
    )
    .build();
```

### 菜单项类型

| 方法 | 说明 |
|------|------|
| `item(String, Consumer)` | 普通菜单项 |
| `item(String, Node, Consumer)` | 带图标的菜单项 |
| `item(String, KeyCombination, Consumer)` | 带快捷键的菜单项 |
| `separator()` | 分隔线 |
| `subMenu(String, MenuItem...)` | 子菜单 |

---

## JFXTabs

标签页组件，用于切换内容。

### 基本用法

```java
TabPane tabs = JFXTabs.create()
    .tab("Tab 1", new Label("Content 1"))
    .tab("Tab 2", new Label("Content 2"))
    .tab("Tab 3", new Label("Content 3"))
    .build();

// 可关闭标签页
TabPane closableTabs = JFXTabs.create()
    .tab("Tab 1", new Label("Content 1"))
    .tab("Tab 2", new Label("Content 2"))
    .closable(true)
    .build();
```

### 标签页配置

| 方法 | 说明 |
|------|------|
| `tab(String, Node)` | 添加标签页 |
| `tab(String, Node, String)` | 添加带提示的标签页 |
| `closable(boolean)` | 是否可关闭 |
| `closingPolicy(TabClosingPolicy)` | 关闭策略 |
| `dragPolicy(TabDragPolicy)` | 拖拽策略 |

---

## JFXTree

树形组件，用于展示层级数据。

### 基本用法

```java
TreeView<String> tree = JFXTree.<String>create()
    .root("Root",
        JFXTree.node("Folder 1",
            JFXTree.leaf("File 1-1"),
            JFXTree.leaf("File 1-2")
        ),
        JFXTree.node("Folder 2",
            JFXTree.leaf("File 2-1"),
            JFXTree.leaf("File 2-2")
        )
    )
    .onSelect(item -> System.out.println("Selected: " + item))
    .build();
```

### 树节点类型

| 方法 | 说明 |
|------|------|
| `leaf(T)` | 叶子节点 |
| `leaf(T, Node)` | 带图标的叶子节点 |
| `node(T, TreeItem...)` | 父节点 |
| `node(T, Node, TreeItem...)` | 带图标的父节点 |

### 树配置

| 方法 | 说明 |
|------|------|
| `root(T, TreeItem...)` | 设置根节点 |
| `showRoot(boolean)` | 是否显示根节点 |
| `onSelect(Consumer)` | 选择回调 |

---

## JFXDatePicker

日期选择组件，用于选择日期。

### 基本用法

```java
DatePicker datePicker = JFXDatePicker.create()
    .placeholder("Select date")
    .value(LocalDate.now())
    .onChange(date -> System.out.println("Selected: " + date))
    .build();
```

### 配置

| 方法 | 说明 |
|------|------|
| `placeholder(String)` | 占位文本 |
| `value(LocalDate)` | 默认值 |
| `editable(boolean)` | 是否可编辑 |
| `showWeekNumbers(boolean)` | 显示周数 |
| `onChange(Consumer)` | 值变化回调 |

---

## JFXSlider

滑块组件，用于选择数值。

### 基本用法

```java
Slider slider = JFXSlider.create()
    .min(0)
    .max(100)
    .value(50)
    .step(10)
    .onChange(value -> System.out.println("Value: " + value))
    .build();
```

### 配置

| 方法 | 说明 |
|------|------|
| `min(double)` | 最小值 |
| `max(double)` | 最大值 |
| `value(double)` | 当前值 |
| `step(double)` | 步长 |
| `showTickLabels(boolean)` | 显示刻度标签 |
| `showTickMarks(boolean)` | 显示刻度线 |
| `vertical(boolean)` | 垂直方向 |
| `onChange(Consumer)` | 值变化回调 |

---

## JFXProgress

进度条组件，用于展示进度。

### 基本用法

```java
// 进度条
HBox progressBar = JFXProgress.bar()
    .progress(0.75)
    .size(JFXProgress.Size.LARGE)
    .status(JFXProgress.Status.SUCCESS)
    .build();

// 环形进度
VBox progressCircle = JFXProgress.circle()
    .progress(0.6)
    .size(80)
    .build();
```

### 进度条配置

| 方法 | 说明 |
|------|------|
| `progress(double)` | 进度值（0-1） |
| `size(Size)` | 尺寸（SMALL, DEFAULT, LARGE） |
| `status(Status)` | 状态（NORMAL, SUCCESS, WARNING, ERROR） |
| `showInfo(boolean)` | 显示进度信息 |

---

## JFXAlert

警告提示组件，用于显示提示信息。

### 基本用法

```java
// 成功提示
VBox successAlert = JFXAlert.success("Success", "Operation completed!")
    .build();

// 错误提示（可关闭）
VBox errorAlert = JFXAlert.error("Error", "Something went wrong!")
    .closable(true)
    .onClose(() -> System.out.println("Alert closed"))
    .build();

// 信息提示
VBox infoAlert = JFXAlert.info("Info", "This is an informational message.")
    .build();

// 警告提示
VBox warningAlert = JFXAlert.warning("Warning", "Please be careful.")
    .build();
```

### 配置

| 方法 | 说明 |
|------|------|
| `closable(boolean)` | 是否可关闭 |
| `showIcon(boolean)` | 显示图标 |
| `onClose(Runnable)` | 关闭回调 |

---

## 通用方法

所有组件都支持以下通用方法：

| 方法 | 说明 |
|------|------|
| `style(String)` | 设置内联样式 |
| `styleClass(String)` | 添加样式类 |

---

## 快速参考

### 创建按钮
```java
JFXButton.create("Text").type(JFXButton.Type.PRIMARY).build();
```

### 创建输入框
```java
JFXInput.create().placeholder("Placeholder").build();
```

### 创建卡片
```java
JFXCard.create().title("Title").content("Content").build();
```

### 创建模态框
```java
JFXModal.create().title("Title").content("Content").show();
```

### 创建表格
```java
JFXTable.<Type>create().column("Name", Type::getName).data(list).build();
```

### 创建表单
```java
JFXForm.create().item("Label", control).onSubmit(data -> {}).build();
```

---

## M19.46 之后的新功能追踪

> 本节为 M19.46（jlessc 替换 npx lessc）之后的**关键里程碑**快速指针，详细内容请见 [PROJECT_PLAN.md](../PROJECT_PLAN.md)。

### M19.46 — LESS 编译迁移
- LESS 编译从 `npx lessc` 改为 `jlessc`（纯 Java），**不再依赖 Node.js**
- 修复 BUG #64：groovy-maven-plugin "假成功" 问题

### M19.53 — 组件按类型分包
- `component/control`（23 原子型，继承 JavaFX 原生控件）
- `component/composite`（42 组合型，自定义容器）
- `component/overlay`（7 浮层型，Popup/Stage/ContextMenu）
- `component/layout`（13）+ `component/base`（9）

### M20 — 测试覆盖增强（2026-06-08）
- **M20.1** BorderRadius 下沉到 `AbstractStyleBuilder<SELF>`：`BorderRadiusTest` 28 用例 / 6 @Nested
- **M20.2** 三个核心组件单测骨架：
  - `GroupBoxAntTest`（391 行 / 6 @Nested）
  - `ToggleButtonAntTest`（329 行 / 6 @Nested，含 BUG #48 mandatoryGroup）
  - `SplitButtonAntTest`（358 行 / 6 @Nested，含 BUG #43 边线连续性）

### M21 — FormAnt 增强 + bindValue API 全补完（2026-06-09）
- **M21.1** `FormAnt.Builder` 单测 `FormAntTest`（1071 行 / 66 用例 / 9 @Nested）
  - 覆盖 FormContext 21 / Result 5 / Rule 9 / Named 11 / Legacy 4 / Section 3 / Footer 6 / Header 3 / Layout 4
- **M21.2** bindValue API 补完 ChoiceBox：`ChoiceBoxAnt<T>.bindValue(Property<T>)`
  - **21 个数据输入控件**（control 11 + composite 10）全支持
  - 4 个 bindValue 系列单测：InputNumberAntTest（141 行）/ SliderAntTest（174 行）/ SwitchAntTest（134 行）/ ChoiceBoxAntTest（329 行），共 778 行
- **M21.3** 测试矩阵：23 个测试文件 / 766 用例 / 0 失败


---

## M19.46 之后的新功能追踪

> 本节为 M19.46（jlessc 替换 npx lessc）之后的**关键里程碑**快速指针，详细内容请见 [PROJECT_PLAN.md](../PROJECT_PLAN.md)。

### M19.46 — LESS 编译迁移
- LESS 编译从 `npx lessc` 改为 `jlessc`（纯 Java），**不再依赖 Node.js**
- 修复 BUG #64：groovy-maven-plugin "假成功" 问题

### M19.53 — 组件按类型分包
- `component/control`（23 原子型，继承 JavaFX 原生控件）
- `component/composite`（42 组合型，自定义容器）
- `component/overlay`（7 浮层型，Popup/Stage/ContextMenu）
- `component/layout`（13）+ `component/base`（9）

### M20 — 测试覆盖增强（2026-06-08）
- **M20.1** BorderRadius 下沉到 `AbstractStyleBuilder<SELF>`：`BorderRadiusTest` 28 用例 / 6 @Nested
- **M20.2** 三个核心组件单测骨架：
  - `GroupBoxAntTest`（391 行 / 6 @Nested）
  - `ToggleButtonAntTest`（329 行 / 6 @Nested，含 BUG #48 mandatoryGroup）
  - `SplitButtonAntTest`（358 行 / 6 @Nested，含 BUG #43 边线连续性）

### M21 — FormAnt 增强 + bindValue API 全补完（2026-06-09）
- **M21.1** `FormAnt.Builder` 单测 `FormAntTest`（1071 行 / 66 用例 / 9 @Nested）
  - 覆盖 FormContext 21 / Result 5 / Rule 9 / Named 11 / Legacy 4 / Section 3 / Footer 6 / Header 3 / Layout 4
- **M21.2** bindValue API 补完 ChoiceBox：`ChoiceBoxAnt<T>.bindValue(Property<T>)`
  - **21 个数据输入控件**（control 11 + composite 10）全支持
  - 4 个 bindValue 系列单测：InputNumberAntTest（141 行）/ SliderAntTest（174 行）/ SwitchAntTest（134 行）/ ChoiceBoxAntTest（329 行），共 778 行
- **M21.3** 测试矩阵：23 个测试文件 / 766 用例 / 0 失败
