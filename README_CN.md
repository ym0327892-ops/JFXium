# JFXium 使用指南

> 现代化 JavaFX UI 框架，对标 Ant Design 6.x 设计风格

---

## 目录

1. [快速开始](#快速开始)
2. [主题系统](#主题系统)
3. [组件分类](#组件分类)
4. [自定义主题](#自定义主题)
5. [styleClass 体系](#styleclass-体系)
6. [自定义组件](#自定义组件)
7. [最佳实践](#最佳实践)

---

## 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 引入主题

在你的 JavaFX Application 启动时加载主题：

```java
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.theme.LightTheme;

@Override
public void start(Stage stage) {
    Scene scene = new Scene(root, 800, 600);
    
    // 方式一：使用 ThemeManager（推荐）
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.applyTheme(new LightTheme());
    themeManager.registerScene(scene);  // 注册 Scene 以支持动态主题切换
    
    stage.setScene(scene);
    stage.show();
}
```

**为什么推荐用 ThemeManager？**
- 支持运行时动态切换主题（`toggleTheme()` / `switchToMui()` 等）
- 支持动态修改主题色（`setPrimaryColor("#ff6b6b")`）
- 统一管理多个 Scene/Region 的主题状态
- 避免硬编码 CSS 路径

### 3. 使用组件

```java
import org.openkawu.jfxium.component.*;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.core.theme.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

// 创建一个主按钮
Button btn = ButtonAnt.create("点击我")
    .type(ButtonAnt.Type.PRIMARY)
    .onClick(e -> System.out.println("Hello JFXium!"))
    .build();

// 创建一个输入框
TextField input = InputAnt.create()
    .placeholder("请输入内容")
    .build();

// 使用 VBoxBuilder 组合（推荐）
VBox layout = VBoxBuilder.create()
    .spacing(16)
    .padding(24)
    .align(Pos.CENTER)
    .children(input, btn)
    .build();

// 应用主题
ThemeManager themeManager = ThemeManager.getInstance();
themeManager.applyTheme(new LightTheme());
themeManager.registerScene(scene);  // 注册以支持动态切换
```

### 4. 写一个完整 admin 页（5 分钟）

把上面的零件拼成"用户管理"页 — 顶部筛选 + 中间表格 + 底部分页：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.*;
import org.openkawu.jfxium.core.theme.*;
import org.openkawu.jfxium.template.CrudTemplate;

public class MyAdmin extends Application {
    @Override
    public void start(Stage stage) {
        // 1. 顶部工具栏：搜索 + 新增按钮
        TextField search = InputAnt.create().placeholder("搜索用户").build();
        Node addBtn = ButtonAnt.create("新增")
            .type(ButtonAnt.Type.PRIMARY)
            .onClick(e -> openAddModal())
            .build();

        // 2. 中间表格
        TableView<User> table = TableAnt.<User>create()
            .data(loadUsers())                                  // 你的业务数据
            .column("姓名", User::getName).end()
            .column("邮箱", User::getEmail).end()
            .actionColumn("操作")
                .action("编辑", u -> openEdit(u))
                .action("删除", u -> doDelete(u)).type(ButtonAnt.Type.LINK).danger()
                .end()
            .build();

        // 3. 底部分页
        Pagination pager = PaginationAnt.create()
            .pageCount(10)
            .currentPage(0)
            .onChange(p -> reload(p))
            .build();

        // 4. 用 CrudTemplate 拼整页
        BorderPane page = CrudTemplate.create()
            .title("用户管理")
            .topRight(SplitBarAnt.create().left(search).right(addBtn).build())
            .body(table)
            .bottomRight(pager)
            .build();

        // 5. Scene + 主题
        Scene scene = new Scene(page, 1280, 800);
        ThemeManager.getInstance().applyTheme(new LightTheme());
        ThemeManager.getInstance().registerScene(scene);

        stage.setTitle("用户管理");
        stage.setScene(scene);
        stage.show();
    }

    private void openAddModal() {
        ModalAnt.create()
            .title("新增用户")
            .content("...")
            .onOk(() -> { /* 提交 */ })
            .build()
            .open(stage.getScene().getRoot());
    }

    public static void main(String[] args) { launch(args); }
}
```

要再上一层 admin shell（顶栏 + 侧栏菜单 + 路由），见后面 `AppShellAnt 详细说明` 和 `LoginTemplate / DashboardTemplate / CrudTemplate 业务模板`。

---

## 主题系统

JFXium 使用 LESS 预处理器管理主题，**内置 8 套主题**，覆盖亮/暗、Material、shadcn、cyberpunk 等多种风格。

### 主题文件位置

```
src/main/resources/org/openkawu/jfxium/css/
├── less/                          # LESS 源文件
│   ├── variables-base.less        # 共享尺寸/间距/字体/mixin
│   ├── variables.less             # 亮色色阶定义
│   ├── variables-dark.less        # 暗色色阶定义
│   ├── theme-base.less            # 全部组件样式（主题无关）
│   ├── theme-light.less           # 亮色主题入口
│   ├── theme-dark.less            # 暗色主题入口
│   ├── theme-mui.less             # Material UI 风格（亮）
│   ├── theme-mui-dark.less        # Material UI 风格（暗）
│   ├── theme-mui-compact.less     # Material UI 紧凑（亮）
│   ├── theme-mui-dark-compact.less# Material UI 紧凑（暗）
│   ├── theme-shadcn.less          # shadcn/ui 风格
│   ├── theme-cyberpunk.less       # 赛博朋克风格
│   └── theme-custom.less          # 自定义主题示例
└── theme-*.css                    # 编译产物（generate-resources 阶段由 npx lessc 生成）
```

> **构建依赖**：LESS 编译用 `npx lessc`，需要宿主机有 Node.js。新机器先确认 `node -v && npx -v` 可用。

### 内置主题清单

| 主题 | Java 类 | 适用场景 |
|------|---------|---------|
| **light** | `LightTheme` | 默认亮色主题，类 GitHub 风格 |
| **dark** | `DarkTheme` | 默认暗色主题 |
| **light-compact** | `LightCompactTheme` | 紧凑亮色（减小间距和控件尺寸） |
| **dark-compact** | `DarkCompactTheme` | 紧凑暗色 |
| **mui** | `MuiTheme` | Material UI 亮色 |
| **mui-dark** | `MuiDarkTheme` | Material UI 暗色 |
| **mui-compact** | `MuiCompactTheme` | Material UI 紧凑亮色 |
| **mui-dark-compact** | `MuiDarkCompactTheme` | Material UI 紧凑暗色 |

> **注意**：shadcn 和 cyberpunk 主题的 CSS 文件已生成，但对应的 Java Theme 类尚未实现。如需使用，可通过 `Application.setUserAgentStylesheet()` 手动加载 CSS。

### 切换主题

```java
import org.openkawu.jfxium.core.theme.*;

ThemeManager themeManager = ThemeManager.getInstance();

// 方式一：切换到指定主题
themeManager.applyTheme(new DarkTheme());           // 深色主题
themeManager.applyTheme(new MuiTheme());            // Material UI 亮色
themeManager.applyTheme(new MuiDarkTheme());        // Material UI 暗色
themeManager.applyTheme(new LightCompactTheme());   // 紧凑亮色
themeManager.applyTheme(new DarkCompactTheme());    // 紧凑暗色
themeManager.applyTheme(new MuiCompactTheme());     // MUI 紧凑亮色
themeManager.applyTheme(new MuiDarkCompactTheme()); // MUI 紧凑暗色

// 方式二：快捷切换
themeManager.toggleTheme();        // 亮/暗切换
themeManager.toggleCompact();      // 默认/紧凑切换
themeManager.switchToMui();        // 切换到 MUI 亮色
themeManager.switchToMuiDark();    // 切换到 MUI 暗色

// 方式三：动态修改主题色（无需重新编译 CSS）
themeManager.setPrimaryColor("#ff6b6b");                    // 自定义颜色
themeManager.setPrimaryColor(ThemeColor.Preset.PURPLE);     // 预设颜色
```

**ThemeManager 核心 API：**

| 方法 | 说明 |
|------|------|
| `applyTheme(Theme)` | 应用指定主题 |
| `toggleTheme()` | 亮/暗主题切换 |
| `toggleCompact()` | 默认/紧凑密度切换 |
| `setPrimaryColor(String)` | 动态修改主题色（运行时生效） |
| `registerScene(Scene)` | 注册 Scene 以支持动态主题切换 |
| `getCurrentTheme()` | 获取当前主题对象 |
| `isCompact()` | 判断是否为紧凑模式 |

---

## 组件分类

> 共 **82 个 \*Ant 组件**（`component/`）+ **3 个 \*Template 业务模板**（`template/`）= **85 个组件**，参考 Ant Design 设计

### 通用组件 (General)

| 组件 | 说明 | 布局结构 | 示例 |
|------|------|----------|------|
| **ButtonAnt** | 按钮 | `Button` + 图标/文字 | `ButtonAnt.create("提交").type(Type.PRIMARY).build()` |
| **ToggleButtonAnt** | 切换按钮（M19.6）| `ToggleButton` + 选中态 + 可加 ToggleGroup 互斥 | `ToggleButtonAnt.create("加粗").selected(true).build()` |
| **MenuButtonAnt** | 菜单按钮（M19.6）| `MenuButton` 整体点击弹下拉 | `MenuButtonAnt.create("批量操作").item("导出", e->{}).item("删除", e->{}).build()` |
| **SplitButtonAnt** | 分割按钮（M19.6）| `SplitMenuButton` 主按钮 + 右下拉箭头 | `SplitButtonAnt.create("保存").onClick(e->{}).item("保存并新建", e->{}).build()` |
| **InputAnt** | 输入框 | `VBox` > `TextField` + 前/后缀图标 | `InputAnt.create().placeholder("请输入").build()` |
| **TextAreaAnt** | 文本域 | `VBox` > `TextArea` | `TextAreaAnt.create().rows(4).build()` |
| **CheckBoxAnt** | 复选框 | `HBox` > `CheckBox` + 文字 | `CheckBoxAnt.create("记住我").shape(Shape.CIRCLE).build()` |
| **RadioButtonAnt** | 单选框 | `VBox` > `RadioButton` 列表 | `RadioButtonAnt.create("选项A").shape(Shape.SQUARE).build()` |
| **SwitchAnt** | 开关 | `HBox` > 轨道 + 滑块（StackPane）+ 状态文本 | `SwitchAnt.create().shape(Shape.ROUNDED).checkedText("开").build()` |
| **SliderAnt** | 滑块 | `VBox` > `Slider` + 数值标签 | `SliderAnt.create().min(0).max(100).build()` |
| **SpinnerAnt** | 计数器 | `HBox` > `TextField` + 上下按钮 | `SpinnerAnt.create().min(0).max(100).build()` |

#### ButtonAnt 详细说明

最高频组件。支持 10 种 Type / 3 档 Size / 3 种 Shape，可叠加 ghost / block / loading / icon。

```java
import org.openkawu.jfxium.component.ButtonAnt;

// 主按钮
Button save = ButtonAnt.create("保存")
    .type(ButtonAnt.Type.PRIMARY)
    .onClick(e -> doSave())
    .build();

// 危险按钮 + 大尺寸
Button delete = ButtonAnt.create("删除")
    .type(ButtonAnt.Type.DANGER)
    .size(ButtonAnt.Size.LARGE)
    .build();

// 链接按钮（看起来像超链接）
Button link = ButtonAnt.create("查看详情")
    .type(ButtonAnt.Type.LINK)
    .build();

// 加载状态（提交时按钮转圈不可点）
Button submit = ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY).build();
submit.setOnAction(e -> {
    submit.setDisable(true);
    // 业务逻辑...
});

// Block 撑满父容器宽度（移动端常见）
Button login = ButtonAnt.create("登录")
    .type(ButtonAnt.Type.PRIMARY)
    .block(true)
    .build();

// Ghost 幽灵按钮（透明背景 + 边框/文字主题色，常用于彩色背景）
Button ghost = ButtonAnt.create("订阅")
    .type(ButtonAnt.Type.PRIMARY)
    .ghost(true)
    .build();
```

**Type 全档**：DEFAULT / PRIMARY / DASHED / TEXT / LINK / DANGER / SUCCESS / WARNING / INFO / ACCENT
**Size 全档**：SMALL / DEFAULT / LARGE
**Shape 全档**：DEFAULT（小圆角）/ ROUNDED（大圆角）/ SQUARE（直角）/ CIRCLE（圆形，仅图标按钮）

**特殊点**：
- `onClick` 接收 `EventHandler<ActionEvent>`，写法是 `e -> {...}`
- 按钮状态变化（loading / disabled）通过 `setDisable(true)` 或自定义 styleClass 切换，**不要 setStyle 拼字符串**
- 想做"图标 + 文字"按钮：`.icon(IconAnt.path(IconAnt.Path.SAVE, 14))`

---

#### InputAnt 详细说明

文本输入框。支持前/后缀图标、状态、密码模式、清空按钮。

```java
import org.openkawu.jfxium.component.InputAnt;
import javafx.scene.control.TextField;

// 基础输入
TextField name = InputAnt.create()
    .placeholder("请输入姓名")
    .build();

// 带前后缀图标
TextField search = InputAnt.create()
    .placeholder("搜索...")
    .prefixIcon(IconAnt.path(IconAnt.Path.SEARCH, 14))
    .build();

// 带后缀单位
TextField money = InputAnt.create()
    .placeholder("0.00")
    .suffix("元")
    .build();

// 密码输入（自动用 PasswordField）
TextField password = InputAnt.create()
    .placeholder("密码")
    .password(true)
    .build();

// 三档尺寸
TextField small = InputAnt.create().size(InputAnt.Size.SMALL).build();
TextField large = InputAnt.create().size(InputAnt.Size.LARGE).build();

// 错误态（红边）
TextField err = InputAnt.create()
    .placeholder("用户名已被占用")
    .status(InputAnt.Status.ERROR)
    .build();
```

**特殊点**：
- `.password(true)` 内部会用 `PasswordField` 替换 `TextField`，**返回类型仍是 TextField**（兼容性）
- 监听值变化用 `field.textProperty().addListener(...)` —— 跟 JavaFX 原生一致
- 想配合 FormAnt 校验：见 FormAnt + Rule（M19.23）

---

#### SwitchAnt 详细说明

开关。3 种 Shape（PILL / ROUNDED / SQUARE），支持双态文字。

```java
import org.openkawu.jfxium.component.SwitchAnt;

// 最简
HBox toggle = SwitchAnt.create().build();

// 带文字 + 监听变化
HBox notify = SwitchAnt.create()
    .checkedText("开")
    .uncheckedText("关")
    .selected(true)
    .onChange(checked -> System.out.println("通知: " + checked))
    .build();

// 形状变体
HBox pill = SwitchAnt.create().shape(SwitchAnt.Shape.PILL).build();      // 胶囊（默认）
HBox roundd = SwitchAnt.create().shape(SwitchAnt.Shape.ROUNDED).build();  // 圆角矩形
HBox sqr = SwitchAnt.create().shape(SwitchAnt.Shape.SQUARE).build();      // 直角矩形
```

**特殊点**：
- `.build()` 返回的是 `HBox`（容器 + 状态文字），不是单独的开关节点。如果需要拿到内部开关本身，需要从 children 取
- `onChange` 在状态变化时立即触发，包括程序调 `setSelected`

---

#### CheckBoxAnt / RadioButtonAnt 详细说明

复选框 / 单选框。3 档 Size + 3-4 种 Shape，CheckBox 还支持三态（半选）。

```java
// CheckBox - 基础
CheckBox remember = CheckBoxAnt.create("记住我")
    .selected(true)
    .onChange(checked -> saveRemember(checked))
    .build();

// CheckBox - 三态半选（用于"全选"场景）
CheckBox triState = CheckBoxAnt.create("全选")
    .allowIndeterminate(true)   // 允许 selected ↔ indeterminate ↔ unselected 循环
    .build();

// CheckBox - 形状变体（默认方角，可选 CIRCLE 圆形外观但仍是多选语义）
CheckBox circular = CheckBoxAnt.create("订阅")
    .shape(CheckBoxAnt.Shape.CIRCLE)
    .build();

// RadioButton - 必须配 ToggleGroup 互斥
ToggleGroup gender = new ToggleGroup();
RadioButton male = RadioButtonAnt.create("男").toggleGroup(gender).selected(true).build();
RadioButton female = RadioButtonAnt.create("女").toggleGroup(gender).build();

// RadioButton - 形状变体（默认圆形，SQUARE 看起来像 CheckBox 但仍是单选语义）
RadioButton square = RadioButtonAnt.create("选项")
    .shape(RadioButtonAnt.Shape.SQUARE)
    .toggleGroup(gender)
    .build();
```

**特殊点**：
- RadioButton **必须** 配 `ToggleGroup` 才能互斥，单独用 = 它自己一个组（点了就选中，无法取消）
- CheckBox `allowIndeterminate(true)` 后用户点击会在三态间循环；不开则只有 selected ↔ unselected 两态
- 形状是**视觉**层面的 — Shape.CIRCLE 的 CheckBox 仍然是多选语义（点击多个能同时选中）

---

#### SliderAnt / SpinnerAnt 详细说明

```java
// Slider 基础
Slider volume = SliderAnt.create()
    .min(0).max(100).value(40)
    .onChange(v -> setVolume(v.intValue()))
    .build();

// Slider 带步进点 + 刻度
Slider quality = SliderAnt.create()
    .min(0).max(100).value(50)
    .step(10).dots(true)
    .build();

// Slider 范围选择（双滑块）
Slider price = SliderAnt.create()
    .min(0).max(1000)
    .range()                              // 启用 range 模式
    .defaultValue(new double[]{200, 800})
    .build();

// Spinner 数字步进
Spinner age = SpinnerAnt.create()
    .min(0).max(120).value(18)
    .step(1)
    .build();
```

**特殊点**：
- Slider 的 `onChange` 接收 `Double`，要 cast 才能拿 int
- Spinner 内部是 `TextField + 上下按钮`，不是 JavaFX 原生 `Spinner`（更可控）
- 想要带千分位的 InputNumberAnt 用 `InputNumberAnt`（独立组件，对标 Ant Design InputNumber）

---

### 布局组件 (Layout)

> **容器 Builder**：对于简单的 VBox/HBox/BorderPane 等布局，推荐使用独立的 Builder 类，避免手动 `new` 和 setter 调用。

**容器 Builder 示例：**

```java
import org.openkawu.jfxium.core.container.*;
import org.openkawu.jfxium.core.util.Spacers;
import javafx.geometry.Pos;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;

// VBox 垂直布局
VBox vbox = VBoxBuilder.create()
    .spacing(16)
    .padding(24)
    .align(Pos.CENTER)
    .styleClass("my-panel")
    .children(node1, node2, node3)
    .build();

// HBox 水平布局
HBox hbox = HBoxBuilder.create()
    .spacing(8)
    .padding(12)
    .align(Pos.CENTER_LEFT)
    .children(label, input, button)
    .build();

// BorderPane 五区位布局
BorderPane border = BorderPaneBuilder.create()
    .top(header)
    .left(sidebar)
    .center(content)
    .right(aside)
    .bottom(footer)
    .build();

// StackPane 叠层布局
StackPane stack = StackPaneBuilder.create()
    .align(Pos.CENTER)
    .children(background, content, overlay)
    .build();

// FlowPane 流式布局（自动换行）
FlowPane flow = FlowPaneBuilder.create()
    .gap(8)
    .align(Pos.CENTER)
    .children(tag1, tag2, tag3, tag4)
    .build();

// ScrollPane 滚动容器
ScrollPane scroll = ScrollPaneBuilder.create()
    .content(longContent)
    .fitToWidth(true)
    .vbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED)
    .hbarPolicy(ScrollPane.ScrollBarPolicy.NEVER)
    .build();

// SplitPane 可拖拽分栏
SplitPane split = SplitPaneBuilder.create()
    .orientation(Orientation.HORIZONTAL)
    .items(leftPanel, rightPanel)
    .dividerPositions(0.3)
    .build();

// GridPane 网格布局
GridPane grid = GridPaneBuilder.create()
    .cols(2)
    .gap(8, 16)  // hgap, vgap
    .padding(20)
    .children(label1, input1, label2, input2)
    .build();

// 弹性占位（自动填充剩余空间）
Region spacer = Spacers.grow();

// 固定尺寸占位
Region spacer = Spacers.spacer(100, 20);
```

**容器 Builder API：**

| Builder 类 | 说明 |
|-----------|------|
| `VBoxBuilder` | 创建 VBox（垂直布局） |
| `HBoxBuilder` | 创建 HBox（水平布局） |
| `BorderPaneBuilder` | 创建 BorderPane（五区位布局） |
| `StackPaneBuilder` | 创建 StackPane（叠层布局） |
| `FlowPaneBuilder` | 创建 FlowPane（流式布局，自动换行） |
| `ScrollPaneBuilder` | 创建 ScrollPane（滚动容器） |
| `SplitPaneBuilder` | 创建 SplitPane（可拖拽分栏） |
| `GridPaneBuilder` | 创建 GridPane（网格布局） |
| `Spacers.grow()` | 创建弹性占位 Region |
| `Spacers.spacer(w, h)` | 创建固定尺寸占位 Region |

**Builder 通用方法：**

| 方法 | 适用 Builder | 说明 |
|------|-------------|------|
| `.spacing(double)` | VBox, HBox | 设置子节点间距 |
| `.padding(double)` | 全部 | 设置内边距（四边相同） |
| `.padding(top, right, bottom, left)` | 全部 | 设置内边距（四边独立） |
| `.align(Pos)` | VBox, HBox, StackPane, FlowPane | 设置对齐方式 |
| `.children(Node...)` | VBox, HBox, StackPane, FlowPane, GridPane | 添加子节点 |
| `.top(Node)` | BorderPane | 设置顶部区域 |
| `.right(Node)` | BorderPane | 设置右侧区域 |
| `.bottom(Node)` | BorderPane | 设置底部区域 |
| `.left(Node)` | BorderPane | 设置左侧区域 |
| `.center(Node)` | BorderPane | 设置中心区域 |
| `.orientation(Orientation)` | FlowPane, SplitPane | 设置方向（HORIZONTAL/VERTICAL） |
| `.hgap(double)` | FlowPane, GridPane | 设置水平间距 |
| `.vgap(double)` | FlowPane, GridPane | 设置垂直间距 |
| `.gap(double)` | FlowPane, GridPane | 设置水平和垂直间距（相同） |
| `.gap(hgap, vgap)` | FlowPane, GridPane | 设置水平和垂直间距（独立） |
| `.cols(int)` | GridPane | 设置列数 |
| `.content(Node)` | ScrollPane | 设置滚动内容 |
| `.fitToWidth(boolean)` | ScrollPane | 内容宽度适应容器 |
| `.fitToHeight(boolean)` | ScrollPane | 内容高度适应容器 |
| `.hbarPolicy(Policy)` | ScrollPane | 水平滚动条策略 |
| `.vbarPolicy(Policy)` | ScrollPane | 垂直滚动条策略 |
| `.items(Node...)` | SplitPane | 添加分栏项 |
| `.dividerPositions(double...)` | SplitPane | 设置分隔线位置（0.0-1.0） |
| `.styleClass(String)` | 全部 | 追加 styleClass |
| `.style(String)` | 全部 | 设置 inline 样式（慎用） |
| `.build()` | 全部 | 构建容器 |

> **注意**：`Layouts` 工具类已废弃，请使用独立的 Builder 类（如 `VBoxBuilder.create()`）。

---

### 全局布局管理

JFXium 提供 **SceneLayout** 和 **OverlayManager** 两个核心类，用于管理应用的根布局和浮层系统。

#### SceneLayout - Scene 根布局管理器

`SceneLayout` 提供标准的应用骨架：AppBar + Content + OverlayLayer。

**重要说明：** SceneLayout 只提供**骨架**（BorderPane 三段式：top/center/bottom），具体每个区域里用什么布局（VBox/HBox/BorderPane/GridPane）由用户自己决定。

**架构：**

```
Scene
  └─ StackPane (root)
      ├─ BorderPane (mainLayout) - 骨架，固定为 BorderPane
      │   ├─ top: appBar      - 用户自定义（可以是 HBox/VBox/BorderPane 等）
      │   ├─ center: content  - 用户自定义（可以是任何布局）
      │   └─ bottom: footer   - 用户自定义（可以是 HBox/VBox 等）
      └─ StackPane (overlayLayer) - 全局浮层容器
```

**使用示例：**

```java
import org.openkawu.jfxium.core.layout.SceneLayout;
import org.openkawu.jfxium.core.layout.OverlayManager;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// 1. 创建顶部应用栏（用户自己决定用什么布局，这里用 HBox）
HBox appBar = HBoxBuilder.create()
    .spacing(16)
    .padding(16)
    .align(Pos.CENTER_LEFT)
    .children(
        new Label("我的应用"),
        Spacers.grow(),  // 弹性占位，把右侧按钮推到最右
        themeToggleButton
    )
    .styleClass("app-bar")
    .build();

// 2. 创建主内容区域（用户自己决定用什么布局，这里用 VBox）
VBox mainContent = VBoxBuilder.create()
    .spacing(24)
    .padding(24)
    .align(Pos.TOP_LEFT)
    .children(
        sectionTitle,
        buttonGroup,
        inputForm
    )
    .build();

// 3. 创建底部状态栏（可选）
HBox footer = HBoxBuilder.create()
    .spacing(8)
    .padding(8, 16, 8, 16)
    .align(Pos.CENTER_LEFT)
    .children(statusLabel)
    .build();

// 4. 使用 SceneLayout 创建应用骨架
SceneLayout sceneLayout = SceneLayout.create()
    .appBar(appBar)      // 顶部应用栏（可选）
    .content(mainContent) // 主内容区域（必须）
    .footer(footer)      // 底部区域（可选）
    .buildLayout();

// 5. 注册全局浮层管理器（用于 Message/Notification 等浮层组件）
OverlayManager.getInstance().registerOverlayLayer(sceneLayout.getOverlayLayer());

// 6. 创建 Scene 并应用主题
Scene scene = sceneLayout.createScene(1200, 800);
ThemeManager.getInstance().applyTheme(new LightTheme());
ThemeManager.getInstance().registerScene(scene);

// 7. 显示窗口
stage.setScene(scene);
stage.show();
```

**SceneLayout API：**

| 方法 | 说明 |
|------|------|
| `SceneLayout.create()` | 创建 Builder |
| `.appBar(Node)` | 设置顶部应用栏（可选） |
| `.content(Node)` | 设置主内容区域（必须） |
| `.footer(Node)` | 设置底部区域（可选） |
| `.buildLayout()` | 构建布局，返回 SceneLayout 对象 |
| `.createScene(width, height)` | 创建 Scene |
| `.getRoot()` | 获取根 StackPane |
| `.getMainLayout()` | 获取主布局 BorderPane |
| `.getOverlayLayer()` | 获取浮层容器 StackPane |

**设计理念：**

1. **只提供骨架**：SceneLayout 只负责提供 BorderPane 三段式骨架（top/center/bottom）
2. **不限制内部布局**：每个区域里用什么布局（VBox/HBox/BorderPane/GridPane）由用户自己决定
3. **浮层分离**：浮层容器（overlayLayer）独立于主布局，用于 Message/Notification 等全局浮层组件

---

#### OverlayManager - 全局浮层管理器

`OverlayManager` 管理全局浮层的 z-index 层级，确保不同类型的浮层按正确顺序显示。

**四层 z-index 结构：**

| 层级 | z-index | 用途 | 示例组件 |
|------|---------|------|---------|
| **BASE** | 1000 | 基础浮层 | Tooltip, Popover |
| **DROPDOWN** | 2000 | 下拉菜单 | Dropdown, Select, AutoComplete |
| **MODAL** | 3000 | 模态对话框 | Modal, Drawer |
| **NOTIFICATION** | 4000 | 全局通知 | Message, Notification |

**使用示例：**

```java
import org.openkawu.jfxium.core.layout.OverlayManager;
import javafx.scene.layout.StackPane;

// 1. 注册浮层容器（通常在应用启动时注册一次）
StackPane overlayLayer = sceneLayout.getOverlayLayer();
OverlayManager.getInstance().registerOverlayLayer(overlayLayer);

// 2. 添加浮层节点到指定层级
OverlayManager.getInstance().addOverlay(myPopup, OverlayManager.ZIndex.DROPDOWN);

// 3. 移除浮层节点
OverlayManager.getInstance().removeOverlay(myPopup);

// 4. 清空指定层级的所有浮层
OverlayManager.getInstance().clearLayer(OverlayManager.ZIndex.MODAL);
```

**OverlayManager API：**

| 方法 | 说明 |
|------|------|
| `getInstance()` | 获取单例实例 |
| `registerOverlayLayer(StackPane)` | 注册浮层容器 |
| `addOverlay(Node, ZIndex)` | 添加浮层节点到指定层级 |
| `removeOverlay(Node)` | 移除浮层节点 |
| `clearLayer(ZIndex)` | 清空指定层级的所有浮层 |
| `clearAll()` | 清空所有浮层 |

**注意事项：**

1. **必须先注册**：使用 OverlayManager 前必须先调用 `registerOverlayLayer()`
2. **单例模式**：OverlayManager 是全局单例，整个应用共享一个实例
3. **自动管理**：大部分浮层组件（Message/Notification/Modal/Drawer）会自动使用 OverlayManager，无需手动调用

---

**完整示例（MyDemo）：**

```java
@Override
public void start(Stage stage) {
    // 1. 创建顶部应用栏
    HBox appBar = createAppBar();

    // 2. 创建主内容区域
    VBox mainContent = createMainContent();

    // 3. 创建底部状态栏
    HBox footer = createFooter();

    // 4. 使用 SceneLayout 创建应用骨架
    SceneLayout sceneLayout = SceneLayout.create()
            .appBar(appBar)
            .content(mainContent)
            .footer(footer)
            .buildLayout();

    // 5. 注册全局浮层管理器
    OverlayManager.getInstance().registerOverlayLayer(sceneLayout.getOverlayLayer());

    // 6. 创建 Scene 并应用主题
    Scene scene = sceneLayout.createScene(1200, 800);
    
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.applyTheme(new LightTheme());
    themeManager.registerScene(scene);

    // 7. 显示窗口
    stage.setScene(scene);
    stage.setTitle("JFXium Demo");
    stage.show();
}
```

**使用示例：**

```java
import org.openkawu.jfxium.core.layout.SceneLayout;
import org.openkawu.jfxium.core.layout.OverlayManager;
import org.openkawu.jfxium.core.container.*;

// 1. 用户自己决定每个区域的布局
HBox appBar = HBoxBuilder.create()
    .spacing(16)
    .padding(16)
    .children(title, Spacers.grow(), buttons)
    .build();

VBox content = VBoxBuilder.create()
    .spacing(24)
    .padding(24)
    .children(form, table)
    .build();

HBox footer = HBoxBuilder.create()
    .padding(8, 16, 8, 16)
    .children(statusLabel)
    .build();

// 2. SceneLayout 只提供骨架，不限制内部布局
SceneLayout sceneLayout = SceneLayout.create()
    .appBar(appBar)      // 可选，用户自己决定用什么布局
    .content(content)    // 必选，用户自己决定用什么布局
    .footer(footer)      // 可选，用户自己决定用什么布局
    .buildLayout();

// 3. 创建 Scene
Scene scene = sceneLayout.createScene(1200, 800);

// 4. 注册浮层管理器（支持 Notification/Message 等组件）
OverlayManager.getInstance().registerOverlayLayer(sceneLayout.getOverlayLayer());

// 5. 应用主题
ThemeManager.getInstance().applyTheme(new LightTheme());
ThemeManager.getInstance().registerScene(scene);

// 6. 显示窗口
stage.setScene(scene);
stage.show();
```

**SceneLayout API：**

| 方法 | 说明 |
|------|------|
| `SceneLayout.create()` | 创建 Builder |
| `.appBar(Node)` | 设置顶部应用栏 |
| `.content(Node)` | 设置主内容区域 |
| `.footer(Node)` | 设置底部区域 |
| `.buildLayout()` | 构建 SceneLayout |
| `.createScene(width, height)` | 创建 Scene |
| `.getRoot()` | 获取根容器（StackPane） |
| `.getMainLayout()` | 获取主布局（BorderPane） |
| `.getOverlayLayer()` | 获取浮层容器（StackPane） |

#### OverlayManager - 全局浮层管理器

`OverlayManager` 统一管理 Modal / Drawer / Notification / Message 等浮层组件的 z-index 层级。

**层级规范（从下到上）：**

```
0. 主内容层（mainLayout）
1. Drawer 层（z-index: 1000）
2. Modal 层（z-index: 2000）
3. Notification 层（z-index: 3000）
4. Message 层（z-index: 4000）
```

**使用示例：**

```java
import org.openkawu.jfxium.core.layout.OverlayManager;

// 在 Notification 层显示节点
OverlayManager.getInstance().showInNotificationLayer(notificationNode);

// 在 Message 层显示节点（最高层级）
OverlayManager.getInstance().showInMessageLayer(messageNode);

// 移除节点
OverlayManager.getInstance().remove(notificationNode);

// 清空所有浮层（慎用，通常用于场景切换）
OverlayManager.getInstance().clearAll();
```

**OverlayManager API：**

| 方法 | 说明 |
|------|------|
| `getInstance()` | 获取单例实例 |
| `registerOverlayLayer(StackPane)` | 注册浮层容器（必须在 Application.start() 中调用一次） |
| `showInDrawerLayer(Node)` | 在 Drawer 层显示节点（z-index: 1000） |
| `showInModalLayer(Node)` | 在 Modal 层显示节点（z-index: 2000） |
| `showInNotificationLayer(Node)` | 在 Notification 层显示节点（z-index: 3000） |
| `showInMessageLayer(Node)` | 在 Message 层显示节点（z-index: 4000） |
| `remove(Node)` | 从浮层容器中移除节点 |
| `clearAll()` | 清空所有浮层节点 |
| `isRegistered()` | 检查浮层容器是否已注册 |

**设计说明：**

- **当前版本**：DrawerAnt / ModalAnt 使用独立 Stage（TRANSPARENT + APPLICATION_MODAL），不依赖 OverlayManager。这是为了保证遮罩层覆盖整个窗口，且支持跨窗口弹出。
- **未来扩展**：Notification / Message 等"非模态浮层"可直接挂载到 overlayLayer，避免创建额外 Stage，提升性能。
- **迁移路径**：如果未来需要"内嵌式 Modal"（不覆盖整个窗口，只覆盖某个容器），可通过 OverlayManager 提供 `showInModalLayer(node, container)` 方法实现。

---

| 组件 | 说明 | 布局结构 | 示例 |
|------|------|----------|------|
| **CardAnt** | 卡片 | `VBox` > Cover + Header + Body + Footer | `CardAnt.create().title("标题").content(node).cover("/img.jpg").actions(btn1,btn2).build()` |
| **DividerAnt** | 分割线 | `Separator` 或 `HBox`（带文本时：线-文本-线） | `DividerAnt.create().text("或").build()` |
| **FormAnt** | 表单容器 | `VBox` > `FormItem` 列表 | `FormAnt.create().label("姓名", input).build()` |
| **TableAnt** | 表格（M11 高级化）| `TableView` + 列级/表级 Builder + actionColumn 糖 | `TableAnt.<Person>create().column("姓名", Person::getName).end().data(list).build()` |
| **LayoutAnt** | 页面布局 | `VBox` > Header + `HBox`(Sider+Content) + Footer | `LayoutAnt.create().header(h).sider(s,200).content(c).build()` |
| **CrudTemplate** | 业务模板（M18 新增）| `BorderPane` > 顶工具栏 + body + 底工具栏 | `CrudTemplate.create().title("用户管理").topRight(addBtn).body(table).bottomRight(pagination).build()` |
| **SurfaceAnt** | 内容承载面 | `VBox` > 标题区 + 白色内容面板 | `SurfaceAnt.create().title("筛选条件").content(form).build()` |
| **AppShellAnt** | 应用骨架 | `BorderPane` > Header/Sider/Content/Footer | `AppShellAnt.create().header(h).sider(s).content(page).build()` |
| **SplitBarAnt** | 三段式横向布局（M19）| `HBox` 左 + spacer + 中 + spacer + 右（center 可选，省略即退化为二段，覆盖 ActionBar/Header 全部场景）| `SplitBarAnt.create().left(back).center(title).right(save,cancel).build()` |
| **SplitPaneAnt** | 分割面板 | `SplitPane` 水平/垂直分栏 | `SplitPaneAnt.create().items(left,right).dividerPositions(0.3).build()` |
| **ResizablePanelAnt** | 可调整尺寸面板 | `StackPane` + 拖拽手柄 | `ResizablePanelAnt.create().content(details).prefWidth(320).build()` |
| **ScrollContainerAnt** | 滚动容器 | `ScrollPane` + 统一视口背景 | `ScrollContainerAnt.create().content(page).fitToWidth(true).build()` |
| **FlexAnt** | 弹性布局 | `Pane`（HBox/VBox/FlowPane 之一） | `FlexAnt.create().gap(16).children(a, b, c).build()` |
| **GridAnt** | 24 栅格系统 | `VBox` > GridPane（24 列 percentWidth）| `GridAnt.create().row(GridAnt.row().col(12, a).col(12, b)).build()` |
| **SpaceAnt** | 间距组件 | `HBox/VBox` + spacer 间隔 | `SpaceAnt.create().size(16).children(ns).build()` |

#### AppShellAnt 详细说明（M19.22 Sider 折叠 + breakpoint）

整个 admin 应用的"骨架"组件 —— 顶部 Header / 左侧 Sider / 中间 Content / 底部 Footer 五区位。

```java
import org.openkawu.jfxium.component.AppShellAnt;
import org.openkawu.jfxium.component.GridAnt;

// 基础用法
BorderPane shell = AppShellAnt.create()
    .header(headerNode)                         // 顶部应用栏
    .sider(menuNode, 240)                       // 左侧菜单 + 宽度
    .content(routerOutletNode)                  // 主内容区（路由切换替换这里）
    .footer(footerNode)                          // 底部状态栏（可选）
    .build();
Scene scene = new Scene(shell, 1280, 800);

// 可折叠 Sider（M19.22）
BorderPane shell2 = AppShellAnt.create()
    .header(headerNode)
    .sider(menuNode, 240)
    .collapsible(true)                          // 启用折叠
    .collapsedWidth(64)                          // 折叠后宽度（默认 64）
    .trigger(true)                               // 显示底部 ‹/› 按钮（默认 true）
    .onCollapseChange(c -> log.info("collapsed=" + c))
    .content(pageNode)
    .build();

// 响应式自适应（窄屏自动折叠）
BorderPane shell3 = AppShellAnt.create()
    .sider(menuNode, 240)
    .collapsible(true)
    .breakpoint(GridAnt.Breakpoint.LG)          // < 992px 自动折叠
    .content(pageNode)
    .build();

// 外部控制折叠（自定义按钮触发）
AppShellAnt.Result result = AppShellAnt.create()
    .sider(menuNode, 240)
    .collapsible(true)
    .trigger(false)                              // 隐藏内置按钮
    .content(pageNode)
    .buildResult();

myHamburgerBtn.setOnAction(e -> result.toggle());
BorderPane shell4 = result.getRoot();
```

**特殊点**：
- `.build()` 直接返回 `BorderPane`（向下兼容老 API）
- `.buildResult()` 返回 `Result`，含 `toggle()` / `setCollapsed(b)` / `collapsedProperty()` 用于外部控制
- `breakpoint` 复用 GridAnt.Breakpoint 枚举（XS/SM/MD/LG/XL/XXL）—— 共用一套标准
- 折叠时 sider 节点自动挂 `.app-shell-sider-collapsed` styleClass，业务方可在 LESS 里自定义"折叠时隐藏文字只剩图标"等细节
- Header / Footer 区域是任意 Node —— 通常用 SplitBarAnt 拼三段式（左 logo + 中间空 + 右用户菜单）

---

### 导航组件 (Navigation)

| 组件 | 说明 | 布局结构 | 示例 |
|------|------|----------|------|
| **AnchorAnt** | 锚点导航 | `VBox` > AnchorLink 列表 | `AnchorAnt.create().item("basic","基础").build()` |
| **BreadcrumbAnt** | 面包屑 | `HBox` > Hyperlink 分隔符链接 | `BreadcrumbAnt.create().item("首页").item("分类").build()` |
| **DropdownAnt** | 下拉菜单 | `HBox` > 触发器 + `ContextMenu` | `DropdownAnt.create().trigger(btn).item("编辑").build()` |
| **MenuAnt** | 菜单栏 | `VBox/HBox` > MenuItem + 子菜单 | `MenuAnt.create().menu("文件",m->m.item("新建")).build()` |
| **PaginationAnt** | 分页器 | `HBox` > 上一页 + 页码 + 下一页 + 每页条数 | `PaginationAnt.create().total(100).pageSize(10).build()` |
| **StepsAnt** | 步骤条 | `HBox` > StepItem + 连接线 | `StepsAnt.create().step("下单").step("支付").build()` |
| **TabsAnt** | 标签页 | `VBox` > [HBox:标签] + [指示条] + StackPane:内容 | `TabsAnt.create().tab("Tab1", content).build()` |

#### MenuAnt 详细说明（M15 4 模式正交）

admin 后台侧栏的核心组件。支持 INLINE / HORIZONTAL × LIGHT / DARK × 折叠 × 选中态 4 个维度正交组合。

```java
import org.openkawu.jfxium.component.MenuAnt;

// 基础侧栏菜单
Pane sider = MenuAnt.create()
    .item("dashboard", "首页", () -> router.go("dashboard"))
    .item("users", "用户管理", () -> router.go("users"))
    .selectedKey("dashboard")               // 当前选中（高亮 + 左侧主题色竖线）
    .onSelect(key -> log.info("选中: " + key))
    .build();

// 多级嵌套（subMenu 必须 .endSubMenu() 收口）
Pane nested = MenuAnt.create()
    .item("dashboard", "首页", () -> {})
    .subMenu("user", "用户管理")
        .item("user.list", "列表", () -> {})
        .item("user.add", "新增", () -> {})
        .endSubMenu()                        // 必须！否则后续 item 还会塞到 subMenu 里
    .item("settings", "设置", () -> {})
    .build();

// 顶部横向导航（HORIZONTAL）
Pane topNav = MenuAnt.create()
    .mode(MenuAnt.Mode.HORIZONTAL)
    .item("home", "首页", () -> {})
    .subMenu("products", "产品")            // 横向时 subMenu 改为下拉 Popup
        .item("p1", "产品 A", () -> {})
        .endSubMenu()
    .build();

// 暗色侧栏（admin Pro 经典）
Pane darkSider = MenuAnt.create()
    .theme(MenuAnt.Theme.DARK)               // 深色背景 + 白字
    .item(...).build();

// 折叠模式（仅 INLINE）
Pane collapsed = MenuAnt.create()
    .collapsed(true)                         // 缩到 64px，只显示图标
    .item("home", "首页",
          IconAnt.path(IconAnt.Path.HOME, 16),    // 提供图标
          () -> {})
    .build();

// 手风琴展开模式（M19.19）
Pane accordion = MenuAnt.create()
    .expandMode(MenuAnt.ExpandMode.EXCLUSIVE)  // 展开 A 时其他自动收起
    .subMenu("a", "A").item(...).endSubMenu()
    .subMenu("b", "B").item(...).endSubMenu()
    .build();
```

**特殊点**：
- `build()` 返回 `Pane`（不是 `VBox`）—— 因为 HORIZONTAL 模式返回 HBox。需要持有引用时用 `Pane sider = ...`
- `subMenu(...)` **必须** 跟 `.endSubMenu()`，否则后续 item 全塞进 subMenu
- `selectedKey` 是受控的 —— 路由切换时调 `MenuAnt` 重新 build 并传新 selectedKey；内部不会自动跟随用户点击（要监听 `onSelect` 自己更新业务路由）
- 折叠 + 暗色 + 选中态可以**同时叠加**（M15 4 模式正交）

---

### 数据录入 (Data Entry)

| 组件 | 说明 | 布局结构 | 示例 |
|------|------|----------|------|
| **AutoCompleteAnt** | 自动完成 | `VBox` > `TextField` + 下拉 `ListView` | `AutoCompleteAnt.<String>create().options(list).build()` |
| **CascaderAnt** | 级联选择 | `HBox` > 显示框 + `VBox` 多级面板 | `CascaderAnt.create().options(options).build()` |
| **ComboBoxAnt** | 下拉框 | `VBox` > `ComboBox` | `ComboBoxAnt.create().items(list).build()` |
| **DatePickerAnt** | 日期选择 | `VBox` > 显示框 + 弹出日历面板 | `DatePickerAnt.create().placeholder("选择日期").build()` |
| **TimePickerAnt** | 时间选择 | `HBox` > 时:分:秒 `ComboBox` | `TimePickerAnt.create().format("HH:mm:ss").build()` |
| **ColorPickerAnt** | 颜色选择 | `VBox` > `ColorPicker` + 预览色块 | `ColorPickerAnt.create().value(Color.BLUE).build()` |
| **InputNumberAnt** | 数字输入 | `HBox` > 减按钮 + `TextField` + 加按钮 | `InputNumberAnt.create().min(0).max(100).build()` |
| **MentionsAnt** | @提及 | `VBox` > `TextArea` + 提及建议弹窗 | `MentionsAnt.create().option("user1","张三").build()` |
| **UploadAnt** | 文件上传 | `VBox` > 拖拽区 + 文件列表 | `UploadAnt.create().multiple(true).build()` |
| **TransferAnt** | 穿梭框 | `HBox` > 源列表 + 操作按钮 + 目标列表 | `TransferAnt.create().source(l1).target(l2).build()` |

#### FormAnt 详细说明（M19.23 校验 + 联动 + Result 句柄）

最重要的数据录入组件。3 种 Layout / 3 档 Size / 完整校验规则系统 + 字段联动。

```java
import org.openkawu.jfxium.component.FormAnt;
import org.openkawu.jfxium.core.form.Rule;

// 基础（向下兼容）
VBox form = FormAnt.create()
    .layout(FormAnt.Layout.HORIZONTAL)
    .labelCol(6).wrapperCol(18)              // label 占 6/24，控件占 18/24
    .item("用户名", usernameField, true)      // 第 3 参数 true = 必填（红 *）
    .item("邮箱", emailField, true, "请输入有效邮箱")  // 第 4 参数 = helpText
    .footer(submitBtn)
    .build();

// 完整校验（M19.23 新增）
FormAnt.Result result = FormAnt.create()
    .item("用户名", usernameField, "username")          // 第 3 参数 name = 进入 FormContext
        .required()                                     // 自动加 required Rule
        .rule(Rule.minLength(3, "至少 3 个字符"))
        .rule(Rule.maxLength(16, "至多 16 个字符"))
        .rule(Rule.pattern("^[a-zA-Z0-9_]+$", "仅字母数字下划线"))
        .end()                                          // 必须！回链 Builder
    .item("邮箱", emailField, "email")
        .required().rule(Rule.email()).end()
    .item("年龄", ageField, "age")
        .required().rule(Rule.range(18, 120, "请输入 18-120")).end()
    .buildResult();

// 提交校验
submitBtn.setOnAction(e -> {
    if (result.validate()) {
        Map<String, Object> values = result.getValues();   // {username=..., email=..., age=...}
        doSubmit(values);
    }
    // 校验失败时每个字段下方自动显示红色错误消息
});

// 字段联动（密码 / 确认密码）
FormAnt.Result pwd = FormAnt.create()
    .item("密码", passwordField, "password")
        .required().rule(Rule.minLength(6, "至少 6 位")).end()
    .item("确认密码", confirmField, "confirm")
        .required()
        .rule(Rule.custom(
            v -> Objects.equals(v, passwordField.getText()),
            "两次输入不一致"))
        .end()
    .buildResult();

// 联动：password 变化时自动重新校验 confirm
pwd.onChange("password", (val, ctx) -> ctx.validateField("confirm"));
pwd.onChange("confirm", (val, ctx) -> ctx.validateField("confirm"));
```

**Rule 全档**：required / minLength / maxLength / lengthBetween / pattern / email / range / custom

**特殊点**：
- 老 `.item(label, control, required)` API 完全保留，**未命名（不传 name）的 item 不进 FormContext，零开销**
- `.item(label, control, name).required()...rule(...).end()` 是新链式 API，**.end() 必须** 回链
- `result.getValues()` 自动从 6 类常用控件提取值（TextField / CheckBox / Radio / Toggle / ComboBox / DatePicker）
- 嵌套表单：item 的 control 可以是另一个 FormAnt 的 build 产物 —— 零特殊代码
- 想要"重置错误显示但保留输入值"：`result.context().clearErrors()`

---

### 数据展示 (Data Display)

| 组件 | 说明 | 布局结构 | 示例 |
|------|------|----------|------|
| **AvatarAnt** | 头像 | `Circle/Region` > 头像内容 | `AvatarAnt.create().text("JD").size(Size.LARGE).build()` |
| **BadgeAnt** | 徽标 | `StackPane` > 关联内容 + 红色小圆点 | `BadgeAnt.create().count(5).build()` |
| **CalendarAnt** | 日历 | `VBox` > 月份导航 + 星期标题 + 日期网格 | `CalendarAnt.create().value(LocalDate.now()).build()` |
| **CarouselAnt** | 轮播 | `StackPane` > 图片列表 + 指示点 + 箭头 | `CarouselAnt.create().image("/1.png").autoplay(true).build()` |
| **CollapseAnt** | 折叠面板 | `VBox` > `TitledPane` 列表 | `CollapseAnt.create().panel("1","标题",content).build()` |
| **DescriptionsAnt** | 描述列表 | `VBox` > 标题行 + `GridPane` 键值对 | `DescriptionsAnt.create().item("姓名","张三").build()` |
| **EmptyAnt** | 空状态 | `VBox` > 图标 + 提示文字 + 操作按钮 | `EmptyAnt.create().description("暂无数据").build()` |
| **ImageAnt** | 图片 | `ImageView` + 占位/加载中/错误状态 | `ImageAnt.create().src("/photo.png").width(200).build()` |
| **ListAnt** | 高级列表 | `VBox` > 标题 + `ListView` + 加载更多 | `ListAnt.create().item(avatar,"标题","描述").build()` |
| **ListViewAnt** | 列表控件 | `ListView` > 自定义 Cell | `ListViewAnt.create().items(list).build()` |
| **PopoverAnt** | 气泡卡片 | `VBox` > 箭头 + 内容面板 | `PopoverAnt.create().title("提示").content(node).build()` |
| **QRCodeAnt** | 二维码 | `ImageView` + 描述文字 | `QRCodeAnt.create().value("https://...").size(160).build()` |
| **SegmentedAnt** | 分段控制器 | `HBox` > `ToggleButton` 等分 | `SegmentedAnt.create().option("day","日").option("week","周").build()` |
| **StatisticAnt** | 统计数值 | `VBox` > 前缀 + 大数字 + 后缀/描述 | `StatisticAnt.create().title("用户数").value(112893).build()` |
| **TagAnt** | 标签 | `HBox` > 标签文字 + 可选关闭图标 | `TagAnt.create().text("标签").color(Color.BLUE).build()` |
| **SelectableTextAnt** | 可复制文本（M19.7）| `TextField/TextArea` 去 chrome —— 看似 Label 但能拖选+Ctrl+C | `SelectableTextAnt.create("can copy").multiline(true).wrap(true).build()` |
| **TimelineAnt** | 时间轴 | `VBox` > TimelineItem(圆点+内容) 列表 | `TimelineAnt.create().item("创建","2024-01-01").build()` |
| **TooltipAnt** | 文字提示 | `Tooltip` 提示框 | `TooltipAnt.create().text("提示信息").build()` |
| **TreeAnt** | 树形控件 | `VBox` > `TreeView` 可嵌套节点 | `TreeAnt.create().root("根",r->r.child("子")).build()` |
| **TreeSelectAnt** | 树形选择 | `HBox` > 显示框 + 弹出 `TreeView` | `TreeSelectAnt.create().treeNode("1","根",children).build()` |
| **TypographyAnt** | 排版 | `VBox` > 标题 + 正文 + 辅助文字 | `TypographyAnt.title("标题").level(1).build()` |
| **WatermarkAnt** | 水印 | `StackPane` > 内容 + 水印层 | `WatermarkAnt.create().content(node).text("机密").build()` |

### 反馈组件 (Feedback)

| 组件 | 说明 | 布局结构 | 示例 |
|------|------|----------|------|
| **AlertAnt** | 警告提示 | `HBox` > 图标 + 消息 + 关闭按钮 | `AlertAnt.create().type(Type.SUCCESS).message("成功").build()` |
| **DrawerAnt** | 抽屉面板 | `StackPane` > 遮罩 + 从侧边滑出的面板 | `DrawerAnt.create().title("设置").open(owner)` |
| **MessageAnt** | 全局消息 | 全局 `Popup` > 支持顶部/底部/中间位置 | `MessageAnt.success("操作成功！")` 或 `MessageAnt.create().position(Position.CENTER).build().show()` |
| **ModalAnt** | 对话框 | `StackPane` > 遮罩 + 居中模态框 | `ModalAnt.create().title("确认").open(owner)` |
| **NotificationAnt** | 通知提醒 | `VBox` > 固定位置的通知卡片列表 | `NotificationAnt.info("标题","内容")` |
| **PopconfirmAnt** | 气泡确认框 | `VBox` > 提示文字 + 确认/取消按钮 | `PopconfirmAnt.create().title("删除？").onConfirm(()->{}).build()` |
| **ProgressAnt** | 进度条 | `ProgressBar` / `ProgressIndicator` | `ProgressAnt.create().value(0.5).build()` |
| **ResultAnt** | 结果页 | `VBox` > 图标 + 标题 + 副标题 + 操作 | `ResultAnt.create().status(SUCCESS).title("成功").build()` |
| **SkeletonAnt** | 骨架屏 | `VBox` > 骨架形状占位 | `SkeletonAnt.create().rows(3).animated(true).build()` |
| **SpinAnt** | 加载中 | `Region` CSS 旋转动画 | `SpinAnt.create().indicator(SPINNER).tip("加载中").build()` |

#### ModalAnt 详细说明

模态对话框。**注意：build() 返回的是 `ModalResult`，不是 Node。需要调 `.open(owner)` 才会显示。**

```java
import org.openkawu.jfxium.component.ModalAnt;

// 基础确认对话框
ButtonAnt.create("删除").onClick(e -> {
    ModalAnt.create()
        .title("确认删除")
        .content("此操作不可撤销，确定要删除该条记录吗？")
        .onOk(() -> doDelete())                       // OK 回调
        .build()
        .open(deleteBtn);                              // open(任意 Node 作为 owner)
}).build();

// 自定义 footer
Button cancel = ButtonAnt.create("取消").build();
Button confirm = ButtonAnt.create("我已知晓").type(ButtonAnt.Type.PRIMARY).build();
HBox footer = new HBox(8, cancel, confirm);

ModalAnt.ModalResult result = ModalAnt.create()
    .title("用户协议")
    .content(scrollableContentNode)                   // 任意 Node
    .footer(footer)
    .width(640)                                        // 自定义宽度（默认 520）
    .closePlacement(ModalAnt.ClosePlacement.RIGHT)    // RIGHT/LEFT/NONE
    .maskClosable(true)                                // 点击遮罩可关闭（默认 true）
    .keyboard(true)                                    // ESC 可关闭（默认 true）
    .build();

confirm.setOnAction(e -> {
    saveAcknowledge();
    result.close();                                    // 程序关闭
});

result.open(triggerBtn);

// 静态便捷方法
ModalAnt.info("标题", "提示内容", ownerNode);
ModalAnt.confirm("删除？", "不可恢复", ownerNode, () -> doDelete());
```

**特殊点**：
- `build()` 返回 `ModalResult`（不是 Node！）—— SKILL #18 中的「Result 包装型 API」实证
- `.open(owner)` 中的 owner 可以是触发按钮 / 当前页面任意 Node —— Modal 会从 owner.getScene().getWindow() 取根 Stage
- `.content(...)` 接受 String 或 Node 两种重载 —— Node 时可以放任何复杂布局
- 默认按钮文字「确定/取消」走 i18n —— 切到英文 Locale 时自动变 OK/Cancel（M19.18）
- `closePlacement(NONE)` 时 maskClosable + keyboard + 默认 footer 至少保留一个，否则 build() 抛 IllegalStateException

---

#### DrawerAnt 详细说明

抽屉面板。从屏幕一侧滑出，常用于详情查看 / 表单编辑。同样是 Result 包装型，需要 `.open(owner)`。

```java
import org.openkawu.jfxium.component.DrawerAnt;

// 右侧抽屉（默认）
DrawerAnt.create()
    .title("用户详情")
    .content(detailNode)                              // 任意 Node
    .placement(DrawerAnt.Placement.RIGHT)             // LEFT/RIGHT/TOP/BOTTOM
    .size(480)                                         // 宽度（左右）或高度（上下）
    .build()
    .open(triggerBtn);

// 左侧抽屉 + 自定义 footer
DrawerAnt.DrawerResult drawer = DrawerAnt.create()
    .title("筛选条件")
    .content(filterForm)
    .placement(DrawerAnt.Placement.LEFT)
    .size(360)
    .footer(new HBox(8, resetBtn, applyBtn))
    .build();

applyBtn.setOnAction(e -> {
    applyFilters();
    drawer.close();
});

drawer.open(filterBtn);
```

**特殊点**：
- 跟 ModalAnt 同样 Result 包装型，`build()` 后必须 `.open(owner)`
- `placement` 决定动画方向（LEFT 从左滑入 / RIGHT 从右滑入 / TOP 从上滑入 / BOTTOM 从下滑入）
- `size` 在 LEFT/RIGHT 是宽度，TOP/BOTTOM 是高度
- 内容超长时自动出滚动条（内部已包了 ScrollPane）

---

#### TableAnt 详细说明（M11 高级化）

TableAnt 是表格组件，**M11 重构**为列级/表级两层链式 API，支持列宽、排序、自定义比较器、操作列糖等高级能力。

**核心特性：**
- ✅ **5 种列类型**：`column`（文本）/ `numberColumn`（数字右对齐）/ `booleanColumn`（勾选框）/ `nodeColumn`（任意节点）/ `actionColumn`（操作按钮糖）
- ✅ **列级配置**：宽度、对齐、排序、自定义比较器、可见性、是否可拖宽
- ✅ **表级配置**：列宽策略（CONSTRAINED / UNCONSTRAINED）、默认排序列、全局排序开关
- ✅ **多选 + 斑马纹 + 边框 + 紧凑模式**

**最简使用：**

```java
record Person(String name, int age) {}

TableView<Person> table = TableAnt.<Person>create()
    .column("姓名", Person::name).end()
    .numberColumn("年龄", Person::age).end()
    .data(people)
    .build();
```

> ⚠️ **路 B API 注意**：每个列配置必须以 `.end()` 结束才能继续链式调用下一列或表级方法。

**列级链式配置：**

```java
TableView<Person> table = TableAnt.<Person>create()
    // 文本列：固定宽度 + 右对齐
    .column("ID", p -> String.valueOf(p.id()))
        .width(60).align(TableAnt.Align.RIGHT).end()

    // 文本列：限定 min/max 宽度
    .column("姓名", Person::name)
        .width(120, 80, 200).end()

    // 数字列：自定义比较器
    .numberColumn("年龄", Person::age)
        .sorter(Comparator.comparingInt(Number::intValue)).end()

    // 邮箱列：禁止排序、禁止拖宽
    .column("邮箱", Person::email)
        .sortable(false).resizable(false).end()

    // 布尔列：默认渲染勾选框 + 居中
    .booleanColumn("启用", Person::active).end()

    // 隐藏列（为后续"列管理"功能埋点）
    .column("内部 ID", Person::internalId)
        .visible(false).end()

    .data(people)
    .build();
```

**操作列糖（admin 高频）：**

```java
TableView<User> table = TableAnt.<User>create()
    .column("姓名", User::name).end()

    .actionColumn("操作")
        .action("编辑", u -> openEditModal(u))
        .action("详情", u -> openDetail(u))
        .action("删除", u -> doDelete(u))
            .danger()                    // 红色文字
        .width(180)                      // 列宽
        .spacing(12)                     // 按钮间距
        .end()

    .data(users)
    .build();
```

> 💡 `.danger()` 和 `.type(...)` 修饰**最后一个** `.action(...)`。

**表级配置：**

```java
TableView<Order> table = TableAnt.<Order>create()
    .column("订单号", Order::orderNo).end()
    .numberColumn("金额", Order::amount).end()

    .data(orders)
    .striped(true)                                            // 斑马纹
    .bordered(true)                                           // 边框
    .selectable(true)                                         // 首列勾选框（多选）
    .compact(true)                                            // 紧凑模式
    .resizePolicy(TableAnt.Resize.UNCONSTRAINED)              // 列宽自由，超出滚动
    .defaultSortBy("金额", TableColumn.SortType.DESCENDING)    // 启动时按金额倒序
    .sortable(true)                                           // 全局排序开关
    .build();
```

**API 参考：**

| 列级方法（ColumnBuilder）| 说明 |
|---|---|
| `.width(pref)` | 首选宽度 |
| `.width(pref, min, max)` | 三参版（首选/最小/最大） |
| `.minWidth(min)` / `.maxWidth(max)` | 单独设最小/最大 |
| `.resizable(bool)` | 是否允许拖动列头改宽 |
| `.sortable(bool)` | 是否允许排序（nodeColumn 默认 false）|
| `.sorter(Comparator<V>)` | 自定义比较器（按列值类型）|
| `.align(Align)` | 对齐（LEFT / CENTER / RIGHT）|
| `.visible(bool)` | 列可见性 |
| `.end()` | 结束列配置，回到表 Builder |

| 表级方法（Builder）| 说明 | 默认 |
|---|---|---|
| `.data(List<T>)` / `.data(ObservableList<T>)` | 数据 | 空 |
| `.striped(bool)` | 斑马纹 | false |
| `.bordered(bool)` | 边框 | false |
| `.selectable(bool)` | 首列勾选框（多选）| false |
| `.compact(bool)` | 紧凑模式 | false |
| `.resizePolicy(Resize)` | CONSTRAINED / UNCONSTRAINED | CONSTRAINED |
| `.sortable(bool)` | 全局排序开关 | true |
| `.defaultSortBy(title, sortType)` | 启动默认排序列 | 无 |

| ActionColumnBuilder 方法 | 说明 |
|---|---|
| `.action(label, handler)` | 添加一个操作按钮 |
| `.type(ButtonAnt.Type)` | 修饰最后一个 action 的按钮类型（默认 LINK） |
| `.danger()` | 修饰最后一个 action 为红色 |
| `.width(double)` | 列宽（默认 160） |
| `.spacing(double)` | 按钮间距（默认 8） |
| `.end()` | 结束，回到表 Builder |

**已知限制：**
- 取值是"快照"：内部 cell value 是 `SimpleObjectProperty`，不会自动响应 JavaFX `Property` 变化。如需实时刷新，请重新 `setItems`。
- nodeColumn 默认不可排序（Node 不可比较），需要排序时使用 `column` + `nodeColumn` 组合或自定义 `sorter`。
- 内置分页未实现，请配合外部 `PaginationAnt` 使用。

#### CardAnt 详细说明

CardAnt 是卡片容器组件，用于展示单个主题的相关内容。支持封面图片、标签页、加载状态、操作按钮等功能。

**核心特性：**
- ✅ **Cover** - 封面图片支持
- ✅ **Header + Body + Footer** - 清晰的三段式结构
- ✅ **Actions** - 底部操作按钮列表
- ✅ **Tabs** - 标签页支持
- ✅ **Loading** - 加载状态（骨架屏）
- ✅ **Size** - 两种尺寸（MEDIUM / SMALL）
- ✅ **Type** - 内嵌卡片样式

**使用示例：**

```java
// 1. 基础卡片
VBox card = CardAnt.create()
    .title("卡片标题")
    .content(new Label("卡片内容"))
    .bordered(true)
    .build();

// 2. 带封面和操作按钮的卡片
VBox card = CardAnt.create()
    .title("卡片标题")
    .cover("/images/cover.jpg")
    .content(new Label("卡片内容"))
    .actions(editBtn, shareBtn, deleteBtn)
    .build();

// 3. 加载状态卡片
VBox card = CardAnt.create()
    .title("加载中")
    .loading(true)
    .build();

// 4. 带标签页的卡片
VBox card = CardAnt.create()
    .title("标签页卡片")
    .tab("tab1", "标签1", content1)
    .tab("tab2", "标签2", content2)
    .defaultActiveTabKey("tab1")
    .onTabChange(e -> System.out.println("切换到: " + e.getSource()))
    .build();

// 5. 小尺寸内嵌卡片
VBox card = CardAnt.create()
    .title("内嵌卡片")
    .content(new Label("内容"))
    .type(CardAnt.Type.INNER)
    .size(CardAnt.Size.SMALL)
    .build();
```

**API 参考：**

| 方法 | 说明 | 默认值 |
|------|------|--------|
| `title(String)` | 卡片标题 | - |
| `extra(Node)` | 标题栏右侧额外内容 | - |
| `content(Node)` | 卡片主体内容 | - |
| `cover(String)` | 封面图片路径 | - |
| `cover(Node)` | 自定义封面节点 | - |
| `actions(Node...)` | 底部操作按钮列表 | - |
| `tab(String, String, Node)` | 添加标签页（key, label, content）| - |
| `activeTabKey(String)` | 当前激活的标签页 key | - |
| `defaultActiveTabKey(String)` | 默认激活的标签页 key | 第一个 tab |
| `onTabChange(EventHandler)` | 标签页切换回调 | - |
| `tabBarExtraContent(Node)` | 标签栏右侧额外内容 | - |
| `loading(boolean)` | 加载状态（骨架屏）| false |
| `size(Size)` | 卡片尺寸（MEDIUM / SMALL）| MEDIUM |
| `type(Type)` | 卡片类型（DEFAULT / INNER）| DEFAULT |
| `bordered(boolean)` | 是否显示边框 | false |
| `hoverable(boolean)` | 是否悬停效果 | false |
| `shadow(Shadow)` | 阴影大小（NONE / SMALL / MEDIUM / LARGE）| NONE |

**语义结构：**

```
Card (VBox)
  ├─ Cover（封面，可选）
  ├─ Header（标题栏 + 标签页导航，可选）
  ├─ Body（内容区，必选）
  └─ Footer（操作区，可选）
```

#### MessageAnt 详细说明

MessageAnt 是全局消息反馈组件，支持**顶部/底部/中间**三个位置，适用于操作反馈、加载提示等场景。

**核心特性：**
- ✅ **三个位置**：TOP（顶部）、BOTTOM（底部）、CENTER（中间）
- ✅ **自动消失**：默认 3 秒后自动关闭（可自定义）
- ✅ **手动关闭**：Loading 类型支持手动关闭
- ✅ **独立堆叠**：顶部/底部/中间消息互不影响，各自独立管理
- ✅ **动画差异**：顶部/底部滑入，中间淡入

**使用示例：**

```java
// 1. 快捷方法（顶部，3秒后消失）
MessageAnt.success("操作成功！");
MessageAnt.error("操作失败！");
MessageAnt.warning("请注意！");
MessageAnt.info("提示信息");

// 2. 自定义位置和持续时间
MessageAnt.create()
    .content("这是底部消息")
    .type(MessageAnt.Type.ERROR)
    .duration(5)  // 5秒后消失
    .position(MessageAnt.Position.BOTTOM)  // 底部显示
    .build()
    .show();

// 3. 中间位置（只显示一个，新消息替换旧消息）
MessageAnt.create()
    .content("重要提示")
    .type(MessageAnt.Type.INFO)
    .duration(3)
    .position(MessageAnt.Position.CENTER)  // 中间显示
    .build()
    .show();

// 4. Loading 消息（手动关闭）
MessageAnt.MessageResult loading = MessageAnt.create()
    .content("加载中...")
    .type(MessageAnt.Type.LOADING)
    .duration(0)  // 不自动消失
    .build();
loading.show();

// ... 操作完成后手动关闭
loading.close();
MessageAnt.success("加载完成！");
```

**位置对照表：**

| 位置 | 枚举值 | 特点 | 动画 | 堆叠规则 |
|------|--------|------|------|---------|
| 顶部 | `Position.TOP` | 默认位置 | 从上滑入 + 淡入 | 多个消息从上往下排列 |
| 底部 | `Position.BOTTOM` | 底部居中 | 从下滑入 + 淡入 | 多个消息从下往上排列 |
| 中间 | `Position.CENTER` | 屏幕正中央 | 纯淡入（不滑动）| **只显示一个**，新消息替换旧消息 |

**API 参考：**

| 方法 | 说明 | 默认值 |
|------|------|--------|
| `content(String)` | 消息内容 | - |
| `type(Type)` | 消息类型（SUCCESS/ERROR/WARNING/INFO/LOADING）| INFO |
| `duration(int)` | 持续时间（秒），0 表示不自动消失 | 3 |
| `position(Position)` | 显示位置（TOP/BOTTOM/CENTER）| TOP |
| `show()` | 显示消息 | - |
| `close()` | 手动关闭消息（仅 MessageResult 对象）| - |

### 其他组件 (Other)

| 组件 | 说明 | 布局结构 | 示例 |
|------|------|----------|------|
| **AccordionAnt** | 手风琴 | `VBox` > 互斥展开的 `TitledPane` | `AccordionAnt.create().pane("标题",content).build()` |
| **AnimationAnt** | 动画工具 | 静态工具类 | `AnimationAnt.fadeIn(node, 300)` |
| **BackTopAnt** | 回到顶部 | 悬浮在右下角的按钮 | `BackTopAnt.create().target(scrollPane).build()` |
| **FloatButtonAnt** | 悬浮按钮 | 圆形 `Button` 悬浮在角落 | `FloatButtonAnt.create().icon(icon).build()` |
| **IconAnt** | 图标 | `Region/SVGPath` | `IconAnt.symbol(IconAnt.Symbol.CLOSE)` |
| **RateAnt** | 星级评分 | `HBox` > 5 个星形图标 | `RateAnt.create().value(3.5).allowHalf(true).build()` |
| **CodeBlockAnt** | 代码块 | `VBox` > 工具栏 + 代码 `TextArea` | `CodeBlockAnt.create().code("public class...").language("java").build()` |

#### WatermarkAnt 详细说明

WatermarkAnt 是水印组件，用于在内容上叠加水印，支持文字水印和图片水印。

**核心特性：**
- ✅ **文字水印**：支持单行/多行文字
- ✅ **图片水印**：支持自定义图片
- ✅ **自定义水印**：支持任意 Node 类型（通过工厂方法）
- ✅ **平铺模式**：自动平铺填充整个容器
- ✅ **旋转角度**：支持自定义旋转角度（默认 -22°）
- ✅ **间距控制**：支持自定义水印间距
- ✅ **透明度控制**：支持自定义水印透明度
- ✅ **字体大小**：支持自定义字体大小
- ✅ **防删除保护**：支持监听 DOM 变化，自动恢复水印层

**使用示例：**

```java
// 1. 文字水印（单行）
StackPane watermarked = WatermarkAnt.create()
    .content(myContent)
    .text("机密文档")
    .build();

// 2. 文字水印（多行）
StackPane watermarked = WatermarkAnt.create()
    .content(myContent)
    .text("机密文档", "请勿外传")
    .build();

// 3. 图片水印
StackPane watermarked = WatermarkAnt.create()
    .content(myContent)
    .image("/logo.png")
    .build();

// 4. 自定义水印节点（使用工厂方法）
StackPane watermarked = WatermarkAnt.create()
    .content(myContent)
    .customNode(() -> {
        // 创建自定义水印节点
        VBox custom = new VBox(4);
        custom.setAlignment(Pos.CENTER);
        custom.getChildren().addAll(
            new Label("自定义水印"),
            new Label("可以是任意 Node")
        );
        return custom;
    })
    .build();

// 5. 启用防删除保护
StackPane watermarked = WatermarkAnt.create()
    .content(myContent)
    .text("重要文档")
    .preventRemoval()  // 启用防删除保护
    .build();

// 6. 自定义样式
StackPane watermarked = WatermarkAnt.create()
    .content(myContent)
    .text("内部资料")
    .rotate(-45)           // 旋转角度
    .opacity(0.1)          // 透明度
    .fontSize(20)          // 字体大小
    .gapX(150)             // 水平间距
    .gapY(100)             // 垂直间距
    .build();
```

**API 参考：**

| 方法 | 说明 | 默认值 |
|------|------|--------|
| `content(Node)` | 被水印覆盖的内容节点（必须）| - |
| `text(String)` | 文字水印（单行）| - |
| `text(String...)` | 文字水印（多行）| - |
| `image(String)` | 图片水印路径 | - |
| `customNode(Supplier<Node>)` | 自定义水印节点工厂方法 | - |
| `preventRemoval()` | 启用防删除保护 | false |
| `preventRemoval(boolean)` | 设置是否启用防删除保护 | false |
| `rotate(double)` | 旋转角度（度数）| -22 |
| `opacity(double)` | 透明度（0.0-1.0）| 0.15 |
| `fontSize(double)` | 字体大小（px）| 16 |
| `gapX(double)` | 水平间距（px）| 100 |
| `gapY(double)` | 垂直间距（px）| 100 |
| `imageWidth(double)` | 图片宽度（px）| 120 |
| `imageHeight(double)` | 图片高度（px）| 64 |

**设计说明：**
- 使用 StackPane 叠层：底层是内容，顶层是水印层
- 水印层使用 Pane 容器，通过绝对定位平铺水印节点
- 水印节点使用 Rotate 变换实现旋转
- 所有颜色和样式通过 CSS 控制（`.jfx-watermark`）
- 防删除保护通过监听子节点列表变化实现，自动恢复被移除的水印层

---

## 业务模板（Templates）

> M18 起，JFXium 把"针对业务场景的整页骨架"独立到 `jfxium/template/` 包，命名后缀 `*Template`。
> 与 `*Ant` 区别：`*Ant` 是原子控件可自由组合，`*Template` 是整页骨架装载多个原子控件 —— admin 后台 90% 业务页直接用模板替代手写。

### 业务模板清单

| 模板 | 说明 | 抽象自 | 示例 |
|------|------|--------|------|
| **CrudTemplate** | 通用三段式业务页（顶工具栏 + body + 底工具栏，M18）| admin demo 列表页/表单页/详情页/仪表盘的共同骨架 | `CrudTemplate.create().title("用户管理").topRight(addBtn).body(table).bottomRight(pagination).build()` |
| **LoginTemplate** | 双栏 banner 登录页（M19.16）| admin demo LoginStage —— 行业标准 760×520 双栏布局 | `LoginTemplate.create().brandName("My Admin").features("...").onSubmit((u,p)->auth(u,p)).build()` |
| **DashboardTemplate** | 概览首页骨架（欢迎 + N 列统计卡 + 双栏底部，M19.16）| admin demo DashboardPage 的标准结构 | `DashboardTemplate.create().welcome("...").stat(...).bottomLeft(...).bottomRight(...).build()` |

### CrudTemplate 详细说明

CrudTemplate 不只适用 CRUD —— **任何「上工具栏 + 中间主内容 + 下分页/状态栏」形态都能用**：表单页 / 详情页 / 仪表盘 / 极简内容页。

```java
// 列表页（最经典）
BorderPane page = CrudTemplate.create()
    .title("用户管理")
    .topLeft(searchField, roleCombo, statusCombo)   // 左：筛选
    .topRight(refreshBtn, addBtn)                   // 右：操作
    .body(table)                                    // 中：表格
    .bottomLeft(totalLabel)                         // 左：总条数
    .bottomRight(pagination, pageSizeCombo)         // 右：分页
    .bordered(true)
    .build();

// 表单页（仅 body + 底部右侧提交）
BorderPane form = CrudTemplate.create()
    .title("新增用户")
    .body(formContent)
    .bottomRight(cancelBtn, submitBtn)
    .build();

// 仪表盘（顶部刷新+导出 + body 是统计卡矩阵）
BorderPane dash = CrudTemplate.create()
    .title("数据概览")
    .topRight(refreshBtn, exportBtn)
    .body(statsGrid)
    .build();
```

### LoginTemplate 详细说明

```java
BorderPane login = LoginTemplate.create()
    .brandName("My Admin")
    .tagline("企业管理系统")
    .features("60+ 内置组件", "8 套主题", "Builder API")
    .copyright("© 2026 · MIT License")
    .formTitle("欢迎回来")
    .submitText("立即登录")
    .onSubmit((username, password) -> auth(username, password))
    .onForgot(() -> openForgotPwd())       // 不调即不显示链接
    .onRegister(() -> openRegister())      // 不调即不显示注册区
    .build();

Scene scene = new Scene(login, 760, 520);
stage.setScene(scene);
stage.show();
```

### DashboardTemplate 详细说明

```java
VBox dashboard = DashboardTemplate.create()
    .welcome("欢迎回来，张三 👋")
    .stat(IconAnt.Path.USERS,    "总用户",   "1,234",   "↑ 12.5%", true)
    .stat(IconAnt.Path.FILE,     "今日订单", "89",      "↓ 3.2%",  false)
    .stat(IconAnt.Path.CHART,    "月销售额", "¥125k",   "↑ 8.4%",  true)
    .stat(IconAnt.Path.DASHBOARD,"转化率",   "23.4%",   "↑ 1.2%",  true)
    .bottomLeft(activityCard)               // 占 60%
    .bottomRight(todoCard)                   // 占 40%
    .leftRatio(60)                           // 可调比例
    .build();
```

> 详见 `jfxium/template/` 源码 + Showcase Demo「业务模板」分类下的实战示例。

---

## 国际化（i18n）

JFXium 内置 i18n 国际化机制（M19.18），**默认 Locale = 简体中文（`zh_CN`）**，支持运行时切换语言、UI 文案自动刷新。

### 核心 API

```java
import org.openkawu.jfxium.core.i18n.Messages;
import java.util.Locale;

// 取一段固定文案（默认 zh_CN）
String copy = Messages.get("codeblock.copy");        // "复制"

// 参数化（{0}/{1} 占位符替换）
String items = Messages.get("transfer.items", 5);    // "5 项"

// 切换 Locale（运行时立即生效）
Messages.setLocale(Locale.ENGLISH);
Messages.get("codeblock.copy");                      // "Copy"

// 当前 Locale
Locale current = Messages.getLocale();

// 监听变化（自定义组件随 locale 自动刷新）
Messages.localeProperty().addListener((obs, ov, nv) -> {
    myButton.setText(Messages.get("my.custom.key"));
});
```

### 内置覆盖的组件

`CodeBlockAnt` / `TreeSelectAnt` / `EmptyAnt` / `ModalAnt` / `PopconfirmAnt` / `UploadAnt` / `TransferAnt` / `LoginTemplate` / `DashboardTemplate` —— 默认文案随 Locale 自动切换。

### 资源文件位置

```
jfxium/src/main/resources/org/openkawu/jfxium/i18n/
├── messages.properties        # fallback（与 zh_CN 同内容）
├── messages_zh_CN.properties  # 默认中文
└── messages_en.properties     # 英文
```

key 命名约定：`组件名小写.元素名`，如 `codeblock.copy` / `treeselect.placeholder` / `modal.ok`。

### 扩展自定义 key

业务项目添加自己的 i18n key：在你的项目 classpath 下覆盖同名 properties 即可（按 ResourceBundle 标准 fallback 链生效）。

### 注意事项

- 默认 Locale 在类加载时锁定为 `Locale.SIMPLIFIED_CHINESE`，符合项目主用户群体
- `Messages.get` 永不返回 null：缺失 key 时返回 key 本身 + WARNING 日志（不会崩溃 UI）
- 0 第三方依赖（仅 JDK `ResourceBundle` / `MessageFormat` / `Locale`）
- Showcase Demo「其他」分类下有 `I18n 国际化` 演示页

---

## 自定义主题

JFXium 的主题系统参考 Ant Design 设计，**只需修改一个 LESS 文件即可生成整套 UI 主题**。

### 主题文件结构

```
css/less/
├── variables-base.less      # 基础尺寸、间距（一般不需要修改）
├── variables.less           # 默认浅色主题颜色变量
├── variables-dark.less      # 深色主题颜色变量
├── theme-base.less          # 组件样式（使用变量，不需要修改）
├── theme-light.less         # 浅色主题入口
├── theme-dark.less          # 深色主题入口
└── theme-custom.less        # ← 自定义主题示例
```

### 方式一：复制修改 theme-custom.less（推荐）

**只需 3 步：**

#### 第 1 步：复制主题文件

```bash
# 复制自定义主题示例
cp css/less/theme-custom.less css/less/theme-mytheme.less
```

#### 第 2 步：修改颜色变量

编辑 `theme-mytheme.less`，修改颜色值：

```less
// ============================================
// 我的自定义主题 - 企业红
// ============================================

@import "variables-base.less";

// 主题色 - 改为红色
@color-accent-5: #f5222d;
@color-accent-6: #cf1322;

// 成功色 - 改为绿色
@color-success-5: #52c41a;

// 背景色 - 改为浅灰
@color-bg-subtle: #f5f5f5;

// 边框色
@color-border-default: #d9d9d9;

// ... 其他变量

// 导入组件样式（必须保留）
@import "theme-base.less";
```

#### 第 3 步：编译生成 CSS

**手动编译：**
```bash
npx lessc css/less/theme-mytheme.less css/theme-mytheme.css
```

**自动编译（推荐）：**
编辑 `pom.xml`，取消注释自定义主题编译配置：

```xml
<execution>
    <id>compile-less-themes-custom</id>
    <phase>generate-resources</phase>
    <goals>
        <goal>exec</goal>
    </goals>
    <configuration>
        <executable>npx</executable>
        <arguments>
            <argument>lessc</argument>
            <argument>src/main/resources/org/openkawu/jfxium/css/less/theme-mytheme.less</argument>
            <argument>src/main/resources/org/openkawu/jfxium/css/theme-mytheme.css</argument>
        </arguments>
    </configuration>
</execution>
```

然后运行：
```bash
mvn compile
```

#### 使用自定义主题

```java
// 加载自定义主题
scene.getStylesheets().add(
    getClass().getResource("/org/openkawu/jfxium/css/theme-mytheme.css").toExternalForm()
);
```

### 方式二：运行时动态修改主题色

使用 ThemeManager 动态修改主题色，无需重新编译 CSS：

```java
ThemeManager themeManager = ThemeManager.getInstance();

// 修改主题色为自定义颜色
themeManager.setPrimaryColor("#ff6b6b");

// 或使用预设颜色
themeManager.setPrimaryColor(ThemeColor.Preset.RED);
themeManager.setPrimaryColor(ThemeColor.Preset.PURPLE);
themeManager.setPrimaryColor(ThemeColor.Preset.GREEN);
```

**ThemeColor.Preset 预设颜色（Ant Design 色板）：**

| 预设 | 颜色值 | 说明 |
|------|--------|------|
| `BLUE` | `#1677ff` | 默认蓝色 |
| `PURPLE` | `#722ed1` | 紫色 |
| `CYAN` | `#13c2c2` | 青色 |
| `GREEN` | `#52c41a` | 绿色 |
| `MAGENTA` | `#eb2f96` | 品红 |
| `RED` | `#f5222d` | 红色 |
| `ORANGE` | `#fa8c16` | 橙色 |
| `GOLD` | `#faad14` | 金色 |
| `LIME` | `#a0d911` | 青柠 |
| `GEEKBLUE` | `#2f54eb` | 极客蓝 |
| `VOLCANO` | `#fa541c` | 火山橙 |

### 方式三：覆盖特定组件样式

```java
Button btn = ButtonAnt.create("自定义")
    .style("-fx-background-color: #ff6b6b; -fx-text-fill: white;")
    .build();
```

### 可修改的变量清单

| 变量 | 说明 | 示例 |
|------|------|------|
| `@color-accent-5` | 主题主色 | `#1677ff` (蓝), `#f5222d` (红) |
| `@color-success-5` | 成功色 | `#52c41a` |
| `@color-warning-5` | 警告色 | `#faad14` |
| `@color-danger-5` | 危险色 | `#f5222d` |
| `@color-bg-default` | 背景色 | `#ffffff` |
| `@color-bg-subtle` | 次要背景 | `#f6f8fa` |
| `@color-fg-default` | 文字颜色 | `rgba(0,0,0,0.88)` |
| `@color-border-default` | 边框颜色 | `#d9d9d9` |
| `@font-size-md` | 基础字体 | `14px` |
| `@font-family` | 字体家族 | `-apple-system, ...` |
| `@spacing-unit` | 间距单位 | `4px` |
| `@border-radius-md` | 圆角大小 | `6px` |
| `@control-height` | 控件高度 | `32px` |
| `@motion` | 动画开关 | `true` / `false` |
| `@wireframe` | 线框模式 | `true` / `false` |

### 与 Ant Design 主题编辑器对比

| 功能 | Ant Design | JFXium | 说明 |
|------|-----------|--------|------|
| **品牌色** | colorPrimary | @color-accent-5 | ✅ 支持 |
| **成功色** | colorSuccess | @color-success-5 | ✅ 支持 |
| **警告色** | colorWarning | @color-warning-5 | ✅ 支持 |
| **错误色** | colorError | @color-danger-5 | ✅ 支持 |
| **信息色** | colorInfo | @color-accent-5 | ✅ 支持 |
| **链接色** | colorLink | @color-accent-5 | ✅ 支持 |
| **基础文本色** | colorTextBase | @color-fg-default | ✅ 支持 |
| **基础背景色** | colorBgBase | @color-bg-default | ✅ 支持 |
| **字体大小** | fontSize | @font-size-md | ✅ 支持 |
| **字体家族** | fontFamily | @font-family | ✅ 支持 |
| **圆角** | borderRadius | @border-radius-md | ✅ 支持 |
| **尺寸步长** | sizeStep | @spacing-unit | ✅ 支持 |
| **控件高度** | controlHeight | @control-height | ✅ 支持 |
| **线框模式** | wireframe | @wireframe | ✅ 支持 |
| **动画开关** | motion | @motion | ✅ 支持 |
| **组件级主题** | components.Button | ❌ | 暂不支持 |
| **算法派生** | algorithm | ❌ | 暂不支持 |
| **动态切换** | ConfigProvider | scene.setStylesheets | ✅ 支持 |
| **主题编辑器** | 在线可视化 | ❌ | 暂不支持 |

**总结**：JFXium 支持 Ant Design 主题编辑器中约 85% 的核心功能（颜色、尺寸、字体、圆角、动画等），但暂不支持组件级主题定制和算法派生。对于大部分业务场景，通过修改 LESS 变量已足够满足自定义需求。

### 完整示例：创建紫色主题

```less
// theme-purple.less
@import "variables-base.less";

// 紫色主题
@color-accent-0: #f3e8ff;
@color-accent-1: #e4ccff;
@color-accent-2: #d1aaff;
@color-accent-3: #bc87ff;
@color-accent-4: #a564ff;
@color-accent-5: #8b41f5;    // 主色
@color-accent-6: #7228dc;
@color-accent-7: #5910c2;

// 语义变量
@color-bg-subtle: #f9f8ff;
@color-border-default: #ddd8f5;

@import "theme-base.less";
```

编译后所有组件会自动使用紫色主题，无需修改任何 Java 代码！

---

## styleClass 体系

> 自 2026-05-17 重构后，JFXium 全部组件采用"styleClass + LESS"模式管理视觉样式，
> **不再使用 inline `setStyle("-fx-...")` 拼字符串**。本节说明如何利用这套体系做自定义。

### 设计原则

1. **Java 端只挂 styleClass，不写 inline 样式**——颜色、字号、字重、边框全部交给 LESS 选择器
2. **每个组件都有基础类 + 修饰类**——状态/尺寸/类型通过修饰类切换，不在 Java 里拼字符串
3. **通用模式抽公共选择器**——例如所有"浮窗菜单"（Dropdown / AutoComplete / Mentions / TreeSelect）共用 `.jfx-popup-menu`，所有"遮罩面板"（Drawer / Modal）共用 `.jfx-overlay-*`

### 使用方法

#### 1. 给组件挂自定义 styleClass

所有 *Ant 组件都提供 `.styleClass(String)` 方法（基于 [`AbstractStyleBuilder`](#自定义组件)）：

```java
Button btn = ButtonAnt.create("自定义")
    .type(ButtonAnt.Type.PRIMARY)
    .styleClass("my-action-btn")        // 挂自定义类
    .styleClass("compact")              // 可多次叠加
    .build();
```

然后在你的 LESS / CSS 里写规则：

```less
.my-action-btn {
  -fx-pref-width: 120px;
  -fx-font-weight: 600;
}

.my-action-btn.compact {
  -fx-padding: 4 8 4 8;
}
```

#### 2. 利用内置 styleClass 自定义状态

JFXium 组件的状态都通过修饰类暴露。你可以用 LESS 覆盖：

```less
/* 自定义"危险按钮"hover 时的颜色 */
.button.danger:hover {
  -fx-background-color: #ff4d4f;  /* 覆盖默认的 -color-danger-hover */
}

/* 自定义"选中行"背景 */
.table-row-cell:selected {
  -fx-background-color: -color-warning-subtle;  /* 改成黄色调 */
}

/* 给所有 Alert 加阴影 */
.jfx-alert {
  -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);
}
```

### 常见 styleClass 命名约定

| 前缀模式 | 含义 | 示例 |
|---------|------|------|
| `jfx-<component>` | 组件根容器 | `.jfx-alert`、`.jfx-popup-menu` |
| `<component>-<part>` | 组件子节点 | `.alert-title`、`.collapse-arrow` |
| `<component>-<state>` | 状态修饰类 | `.alert-success`、`.steps-current` |
| `<size>` | 尺寸修饰类 | `.size-small`、`.size-large` |
| `:hover` / `:focused` / `:armed` / `:pressed` | JavaFX 伪类 | `.button:hover` |

完整常量定义见 `org.openkawu.jfxium.core.css.CssClasses`。

---

## 自定义组件

> 想写自己的 \*Ant 组件？继承 `AbstractStyleBuilder` 即可享受统一的 style/styleClass API。

### AbstractStyleBuilder 公共基类

每个 *Ant Builder 都有 `.style(String)` 和 `.styleClass(String)` 两个钩子。
这些行为统一在 `core/builder/AbstractStyleBuilder<SELF>`，使用 self-bounded 泛型保证链式 API 类型正确：

```java
package org.openkawu.jfxium.component;

import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

public class MyPanelAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public VBox build() {
            VBox panel = new VBox();
            panel.getStyleClass().add("my-panel");      // 挂内置类
            // ... 组装内部节点 ...

            applyStyles(panel);                          // 应用用户的 style/styleClass
            return panel;
        }
    }
}
```

继承后你的 Builder 自动拥有：
- `style(String)` —— 设置 inline 样式（慎用）
- `styleClass(String)` —— 追加额外 styleClass（推荐）
- `applyStyles(Node)` —— 在 build() 末尾调用，把累积的钩子应用到目标节点

### 关键约定

1. **build() 末尾调用 `applyStyles(node)`**：让用户的 `.style/.styleClass` 在内置 styleClass **之后** 应用，确保用户能覆盖默认样式
2. **结构性属性留 Java**：例如 `setPrefSize(...)`、padding 与 level 联动这种动态值，无法在 LESS 表达
3. **视觉性属性走 LESS**：颜色、字号、字重、边框、圆角、阴影一律下沉到 LESS 选择器
4. **状态机用修饰类**：例如 `success/warning/error` 切换不要在 Java 里拼颜色，用 styleClass 切换让 LESS 选择器接管

---

## 完整使用示例

以下是一个完整的 JFXium 应用示例，展示了 SceneLayout、OverlayManager、主题切换、基础组件和浮层组件的综合使用。

### 示例代码

```java
package org.openkawu.jfxium.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.*;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.core.layout.OverlayManager;
import org.openkawu.jfxium.core.layout.SceneLayout;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.util.Spacers;

import java.time.LocalDate;

public class MyDemo extends Application {

    private ThemeManager themeManager;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        // 1. 用户自己决定每个区域的布局（这里选择 HBox/VBox）
        HBox appBar = createAppBar();
        VBox mainContent = createMainContent();
        HBox footer = createFooter();

        // 2. SceneLayout 只提供骨架（BorderPane），不限制内部布局
        SceneLayout sceneLayout = SceneLayout.create()
                .appBar(appBar)      // 可选，这里传入 HBox
                .content(mainContent) // 必选，这里传入 VBox
                .footer(footer)      // 可选，这里传入 HBox
                .buildLayout();

        // 3. 注册全局浮层管理器
        OverlayManager.getInstance().registerOverlayLayer(sceneLayout.getOverlayLayer());

        // 4. 创建 Scene 并应用主题
        Scene scene = sceneLayout.createScene(1200, 800);
        
        themeManager = ThemeManager.getInstance();
        themeManager.applyTheme(new LightTheme());
        themeManager.registerScene(scene);

        // 5. 显示窗口
        stage.setScene(scene);
        stage.setTitle("JFXium Demo");
        stage.show();
    }

    private HBox createAppBar() {
        Label title = new Label("JFXium Demo");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button toggleThemeBtn = ButtonAnt.create("切换主题")
                .type(ButtonAnt.Type.DEFAULT)
                .onClick(e -> themeManager.toggleTheme())
                .build();

        // 使用 Spacers.grow() 实现左右布局
        return HBoxBuilder.create()
                .spacing(16)
                .padding(16)
                .align(Pos.CENTER_LEFT)
                .children(title, Spacers.grow(), toggleThemeBtn)
                .style("-fx-background-color: -color-bg-container; -fx-border-color: -color-border; -fx-border-width: 0 0 1 0;")
                .build();
    }

    private VBox createMainContent() {
        // 输入框
        TextField input = InputAnt.create()
                .placeholder("请输入内容")
                .build();

        // 按钮组
        HBox buttonGroup = HBoxBuilder.create()
                .spacing(8)
                .children(
                        ButtonAnt.create("主按钮").type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("默认按钮").type(ButtonAnt.Type.DEFAULT).build(),
                        ButtonAnt.create("虚线按钮").type(ButtonAnt.Type.DASHED).build()
                )
                .build();

        // 浮层按钮
        HBox overlayButtons = HBoxBuilder.create()
                .spacing(8)
                .children(
                        ButtonAnt.create("打开 Modal")
                                .type(ButtonAnt.Type.PRIMARY)
                                .onClick(e -> openModal(input))
                                .build(),
                        ButtonAnt.create("打开 Drawer")
                                .type(ButtonAnt.Type.DEFAULT)
                                .onClick(e -> openDrawer())
                                .build()
                )
                .build();

        // Calendar 组件
        VBox calendar = CalendarAnt.create()
                .value(LocalDate.now())
                .selectedDate(LocalDate.now())
                .onSelect(date -> System.out.println("选中日期：" + date))
                .fullscreen(false)
                .build();
        calendar.setMaxWidth(600);

        return VBoxBuilder.create()
                .spacing(16)
                .padding(24)
                .align(Pos.TOP_LEFT)
                .children(input, buttonGroup, overlayButtons, calendar)
                .style("-fx-background-color: -color-bg-layout;")
                .build();
    }

    private HBox createFooter() {
        statusLabel = new Label("就绪");
        return HBoxBuilder.create()
                .padding(8, 16, 8, 16)
                .children(statusLabel)
                .style("-fx-background-color: -color-bg-container; -fx-border-color: -color-border; -fx-border-width: 1 0 0 0;")
                .build();
    }

    private void openModal(TextField input) {
        ModalAnt.create()
                .title("Modal 示例")
                .content(new Label("输入框内容：" + input.getText()))
                .width(520)
                .centered(true)
                .build()
                .open(input);
    }

    private void openDrawer() {
        DrawerAnt.create()
                .title("Drawer 示例")
                .content(new Label("这是一个抽屉"))
                .placement(DrawerAnt.Placement.RIGHT)
                .build()
                .open(statusLabel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

### 示例说明

**1. SceneLayout 只提供骨架**
- SceneLayout 固定使用 BorderPane 作为骨架（top/center/bottom）
- 每个区域里用什么布局（VBox/HBox/BorderPane/GridPane）由用户自己决定
- 本示例中：appBar 用 HBox，content 用 VBox，footer 用 HBox
- 用户也可以选择其他布局，比如 appBar 用 BorderPane，content 用 GridPane

**2. OverlayManager 浮层管理**
- 通过 `registerOverlayLayer()` 注册浮层容器
- 未来 Notification/Message 组件可直接挂载到 overlayLayer

**3. 主题管理**
- 使用 `ThemeManager.getInstance()` 单例管理主题
- `applyTheme()` 应用主题，`registerScene()` 注册 Scene 支持动态切换
- `toggleTheme()` 实现亮色/暗色一键切换

**4. 容器 Builder**
- 使用 `VBoxBuilder` / `HBoxBuilder` 创建布局
- 使用 `Spacers.grow()` 创建弹性占位，实现左右布局

**5. 组件 Builder**
- 所有组件使用 `XxxAnt.create()` Builder 模式
- 链式调用设置属性，最后 `.build()` 构建

**6. 浮层组件**
- Modal/Drawer 使用独立 Stage（TRANSPARENT + APPLICATION_MODAL）
- 遮罩层覆盖整个窗口，支持 ESC 和点击遮罩关闭

### 运行效果

- **顶部应用栏**：标题 + 主题切换按钮（右对齐）
- **主内容区域**：输入框 + 按钮组 + 浮层按钮 + Calendar
- **底部状态栏**：显示当前状态
- **主题切换**：点击按钮即可在亮色/暗色之间切换
- **浮层组件**：点击按钮打开 Modal 或 Drawer

---

## 最佳实践

### 1. 使用 Builder 模式

**所有组件和布局都使用 Builder 模式，避免手动 `new` 和 setter 调用：**

```java
// ✅ 推荐：使用 Builder
Button btn = ButtonAnt.create("保存")
    .type(ButtonAnt.Type.PRIMARY)
    .size(ButtonAnt.Size.LARGE)
    .rounded()
    .onClick(e -> save())
    .build();

VBox layout = VBoxBuilder.create()
    .spacing(16)
    .padding(24)
    .children(input, btn)
    .build();

// ❌ 不推荐：手动 new + setter
VBox layout = new VBox(16);
layout.setPadding(new Insets(24));
layout.getChildren().addAll(input, btn);
```

**为什么推荐 Builder？**
- 链式调用更简洁
- 统一的 API 风格（`XxxBuilder.create()` / `XxxAnt.create()`）
- 支持 `.styleClass()` 钩子
- 避免遗漏必要的设置

### 2. 尺寸规范

| 尺寸 | 适用场景 |
|------|----------|
| **SMALL** | 表格内操作按钮、紧凑布局 |
| **DEFAULT** | 大多数场景 |
| **LARGE** | 重要操作、独立按钮 |

### 3. 类型选择

| 类型 | 适用场景 |
|------|----------|
| **PRIMARY** | 主操作，一个页面最多一个 |
| **DEFAULT** | 次要操作 |
| **SUCCESS** | 正向操作（保存、提交） |
| **WARNING** | 警告操作（需谨慎） |
| **DANGER** | 危险操作（删除、清空） |
| **TEXT** | 低优先级操作 |
| **LINK** | 导航、跳转 |

### 4. 无障碍支持

```java
// 为按钮添加无障碍信息
AccessibilityUtils.configureButton(
    button,
    "提交表单",           // 标签
    "点击后将保存所有修改"  // 描述
);

// 为输入框添加无障碍信息
AccessibilityUtils.configureTextInput(
    input,
    "用户名",             // 标签
    "请输入您的用户名"      // 占位提示
);
```

---

## 常见问题

### Q: 如何修改主题色？

A: 有两种方式：

**方式一：运行时动态修改（推荐）**
```java
ThemeManager.getInstance().setPrimaryColor("#ff6b6b");
// 或使用预设
ThemeManager.getInstance().setPrimaryColor(ThemeColor.Preset.PURPLE);
```

**方式二：编译时修改**
编辑 `src/main/resources/org/openkawu/jfxium/css/less/variables.less` 中的 `@color-accent-5` 变量，然后运行 `mvn -pl jfxium compile` 重新编译。

### Q: 支持 JavaFX 哪些版本？

A: 支持 JavaFX 17+，推荐 JavaFX 21。

### Q: 如何添加自定义样式类？

A: 使用 `.styleClass("my-class")` 方法：

```java
Button btn = ButtonAnt.create("自定义")
    .styleClass("my-custom-button")
    .build();
```

详见 [styleClass 体系](#styleclass-体系) 章节。

### Q: 想自己写 \*Ant 组件怎么办？

A: 继承 `AbstractStyleBuilder<SELF>` 即可，详见 [自定义组件](#自定义组件) 章节。

### Q: 组件是否支持响应式布局？

A: 所有组件都基于 JavaFX 布局系统，可与 VBox、HBox、GridPane 等标准布局容器配合使用。GridAnt 24 栅格目前不带断点，xs/sm/md/lg/xl/xxl 响应式断点在 [PLAN.md](PLAN.md) P2 计划中。

### Q: 构建报错说找不到 `npx` 怎么办？

A: LESS 编译用 `npx lessc`，需要宿主机有 Node.js。安装方法：

```bash
# macOS（Homebrew）
brew install node

# 验证
node -v && npx -v
```

---

*文档版本: 1.1*
*更新日期: 2026-05-17*
*重大更新：styleClass 体系、AbstractStyleBuilder 公共基类、8 套主题清单同步*
