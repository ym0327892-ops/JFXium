---
name: jfxium
description: >
  Use when writing, reviewing, or fixing Java 21 + JavaFX 21 UI with the JFXium framework
  (org.openkawu.jfxium). Covers screens, forms, tables, dialogs, drawers, notifications, theming,
  layouts, and migrating native JavaFX controls to *Ant components. Also use when code contains
  `new Label(`, `new Button(`, `new Spinner(`, `setStyle("-fx-`, FXML, `box-shadow`, or
  `-fx-transition` and targets JFXium.
---

# JFXium UI Skill

Targets **JFXium 1.34.2** · Java 21 · JavaFX 21 · zero FXML · LESS theming.

本文件可**单独复制**为技能（Qoder: `.qoder/skills/jfxium.md`；Claude Code: `.claude/skills/jfxium/SKILL.md`）。
若同目录包含 `references/` 与 `assets/catalog/`，按需深读；只复制本文件时，按本文件规则执行即可。

## 触发场景

- 新增/修改 JavaFX 界面：页面、表单、表格、弹窗、抽屉、通知、主题、布局。
- 代码出现原生控件 `new Label/Button/TextField/ComboBox/Spinner/TableView/...`。
- 代码出现 `setStyle("-fx-...")`、`box-shadow`、`-fx-transition`、CSS `:active` / `:focus`。
- 把已有原生 JavaFX 页面迁移到 JFXium。

## 工作流（按序）

1. 查下方「控件速查」确定替代组件；若有 `assets/catalog/components.index.md`，grep 类名/能力。
2. 打开组件源码（`org/openkawu/jfxium/...`）读 Javadoc，确认工厂与 `build()` 返回类型。
3. 到 `jfxium-demo` 找同类页面，复用结构与交互。
4. 写 Builder 链；视觉全部走 styleClass → LESS。
5. 自检：`java skills/jfxium/scripts/JfxLint.java <改动目录>`，ERROR 必须为 0。

## 硬规则（违反即无效）

1. 不 `new` 原生 JavaFX 控件 → `XxxAnt.create()...build()`。
2. 不 `setStyle()` 写颜色/px → styleClass + LESS。
3. 不用 `box-shadow` / `-fx-transition` / `:active` / `:focus`
   （JavaFX 用 `-fx-effect: dropshadow(...)` / `:pressed` / `:armed` / `:focused`）。
4. 新 styleClass 必须 `jfx-` 前缀（避免与 modena 冲突）。
5. `build()` 返回类型不许撒谎；弹层 Result 必须 `.open(owner)` / `.show()`。
6. 不用 FXML；i18n 用 `Messages.get("key")`，字段初始化为 `null`，`build()` 内懒解析。
7. 异步回调触碰场景图前回到 FX 线程（`Platform.runLater`）。
8. 新 public 类必须同步 `module-info.java` exports。

## 控件速查

| 原生 | JFXium | 备注 |
|---|---|---|
| `Label` | `LabelAnt` / `TypographyAnt` | 标题/富文本用 TypographyAnt |
| `Button` | `ButtonAnt` | `type(Type.PRIMARY)` 等 |
| `ToggleButton` | `ToggleButtonAnt` | |
| `CheckBox` | `CheckBoxAnt` | |
| `RadioButton` | `RadioButtonAnt` | |
| `Hyperlink` | `HyperlinkAnt` | |
| `TextField` | `InputAnt` | 搜索清除用 `SearchInputAnt` |
| `PasswordField` | `InputAnt.createPassword()` | 返回 HBox |
| `TextArea` | `TextAreaAnt` | |
| `ComboBox` | `ComboBoxAnt` | |
| `ChoiceBox` | `ChoiceBoxAnt` | |
| `Spinner` | `SpinnerAnt` | 表单数字优先 `InputNumberAnt` |
| `Slider` | `SliderAnt` | |
| `ProgressBar` / `ProgressIndicator` | `ProgressAnt` / `SpinAnt` | 加载动画用 SpinAnt |
| `TableView` | `TableAnt` | `.column/.numberColumn/.data` |
| `TreeView` / `TreeTableView` | `TreeAnt` / `TreeTableAnt` | |
| `ListView` | `ListViewAnt` | |
| `MenuBar` / `MenuButton` / `SplitMenuButton` | `MenuBarAnt` / `MenuButtonAnt` / `SplitButtonAnt` | |
| `Menu` / `MenuItem` | `MenuAnt` | |
| `ContextMenu` / `Tooltip` | `ContextMenuAnt` / `TooltipAnt` | |
| `DatePicker` / `ColorPicker` | `DatePickerAnt` / `ColorPickerAnt` | 日期时间 `DateTimePickerAnt` |
| `TitledPane` / `Accordion` | `TitledPaneAnt` / `AccordionAnt` | |
| `Pagination` / `Separator` / `ToolBar` | `PaginationAnt` / `SeparatorAnt` / `ToolBarAnt` | |
| `TabPane` / `Tab` | `TabsAnt` | `TabsAnt.controllerOf(node)` |
| `ScrollPane` / `SplitPane` | `ScrollPaneAnt` / `SplitPaneAnt` | |
| `BorderPane` | `BorderPaneAnt` | |
| `VBox` / `HBox` | `VBoxAnt` / `HBoxAnt` | 条状用 `HBarAnt` / `VBarAnt` |
| `FlowPane` / `TilePane` / `StackPane` / `AnchorPane` / `TextFlow` | `FlowPaneAnt` / `TilePaneAnt` / `StackPaneAnt` / `AnchorPaneAnt` / `TextFlowAnt` | |
| `GridPane` | `GridAnt`（响应式 24 栅格）| 二维网格另有 `GridPaneAnt` |
| `Alert` | `AlertAnt` | |
| `ImageView` / 图表 / 文件上传 | `ImageAnt` / `ChartAnt` / `UploadAnt` | |
| `Canvas` | `CanvasAnt` | |
| `WebView` / `MediaView` | **无** | javafx-web/media 已移除 |

弹层：`ModalAnt` / `PromptDialogAnt` / `DrawerAnt` → `.build().open(owner)`；
`DropdownAnt` / `PopoverAnt` / `PopconfirmAnt` → `.build().show()`；
`MessageAnt.success("...")` 直接调用；`NotificationAnt` → `.build().show()`。

组合/模板：`PageTemplate`、`FormAnt`、`CrudTemplate`、`LoginTemplate`、`AppShellAnt`。
完整对照见 `references/native-map.md`。

## 关键契约

- **build 返回两类**：常驻 Node → 直接挂载；弹层 Result → `.open(owner)` / `.show()`。
- **数字输入**：表单/金额/百分比优先 `InputNumberAnt`（FormAnt 校验/reset 已接线）；
  需要 `Spinner` 语义（`valueProperty`、`increment()`、第三方 API）用 `SpinnerAnt`
  （默认可键入，`editable(false)` 只读；进 FormAnt 需自行 `bindValue`）。
- **加载动画**：`SpinAnt`（SPINNER / DOTS / BARS），不是 `ProgressIndicator`。
- **主题**：只用 `ThemeManager`：`applyTheme(new LightTheme())` / `registerScene(scene)` /
  `setDensity(ThemeDensity.COMPACT)` / `setPrimaryColor("#ff5722")`；Fluent 主题固定品牌色。
- **控制器**：`TabsAnt` / `StepsAnt` / `PaginationAnt` 等用 `controllerOf(node)`，不重建树。
- **尺寸**：`size(Size.SMALL/DEFAULT/LARGE)`；间距用 `LayoutCommon`（`spacing/padding`），
  不写 `new HBox(8)`。

## 常用片段

页面骨架：

```java
VBox page = VBoxAnt.create()
        .spacing(0)
        .children(PageTemplate.create()
                .title("用户管理").description("查询、创建与维护用户。")
                .sections(filterSection(), tableSection())
                .padding(24).build())
        .build();
```

表单 + 校验：

```java
FormAnt.Result form = FormAnt.create()
        .layout(FormAnt.Layout.HORIZONTAL)
        .item("用户名", InputAnt.create().placeholder("至少 3 个字符").build(), "username")
            .required().rule(Rule.minLength(3, "用户名至少 3 个字符")).end()
        .footer(ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> { if (form.validate()) MessageAnt.success("保存成功"); }).build())
        .buildResult();
VBox formNode = form.getRoot();
```

表格：

```java
TableAnt<Person> table = TableAnt.<Person>create()
        .column("姓名", Person::name)
        .numberColumn("年龄", Person::age)
        .column("邮箱", Person::email)
        .data(rows)
        .build();
```

弹窗 / 消息：

```java
ModalAnt.create().title("删除确认").content("删除后不可恢复，确定继续？")
        .build().open(triggerButton);

MessageAnt.success("保存成功");
```

数字输入：

```java
// 表单首选
Node qty = InputNumberAnt.create().value(1).min(0).max(999).step(1).build();

// 需要 Spinner 语义
SpinnerAnt latitude = SpinnerAnt.create(-90, 90, 0, 0.01)
        .onChange(v -> save(v))
        .build();
```

更多片段见 `references/recipes.md`。

## 输出要求

- 可直接编译的 Java（含 import），无占位符、无 `TODO`、无 FXML。
- 结构与行为用 Java；视觉只用 styleClass → LESS。
- 数值/集合参数做防御式钳制；`null` 回调不绑定。
- 缺少所需组件时：**停止并说明缺哪个 wrapper**，不要用原生控件凑合。

## 可选深度资料（仅完整 Skill 包内可用）

| 文件 | 何时读 |
|---|---|
| `references/rules.md` | 需要完整 11 条红线与 API 契约 |
| `references/native-map.md` | 控件/弹层完整对照、无包装控件说明 |
| `references/recipes.md` | 表单/表格/弹窗/主题/i18n/线程等完整片段 |
| `references/agent-prompt.md` | 不是 Skill 型工具时，粘贴到 AGENTS.md |
| `assets/catalog/components.md` | 全量组件：extends / 工厂 / build / 关键 API / 源文件 |
| `assets/catalog/components.json` | 工具链检索 |
