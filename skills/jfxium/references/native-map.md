# 原生 JavaFX → JFXium 对照表

> 业务代码禁止 `new` 左列控件。右列不存在时：先确认 catalog，仍无则**停下提需求**，不要绕过。

## 控件

| 原生 | JFXium |
|---|---|
| `Label` | `LabelAnt`（富文本/标题用 `TypographyAnt`） |
| `Button` | `ButtonAnt` |
| `ToggleButton` | `ToggleButtonAnt` |
| `CheckBox` | `CheckBoxAnt` |
| `RadioButton` | `RadioButtonAnt`（单选组用 `RadioButtonAnt` group） |
| `Hyperlink` | `HyperlinkAnt` |
| `TextField` | `InputAnt`（搜索带清除用 `SearchInputAnt`） |
| `PasswordField` | `InputAnt.createPassword()` |
| `TextArea` | `TextAreaAnt` |
| `ComboBox` | `ComboBoxAnt` |
| `ChoiceBox` | `ChoiceBoxAnt` |
| `Spinner` | `SpinnerAnt`（表单数字优先 `InputNumberAnt`） |
| `Slider` | `SliderAnt` |
| `ProgressBar` / `ProgressIndicator` | `ProgressAnt` / 加载动画 `SpinAnt` |
| `TableView` | `TableAnt` |
| `TreeView` | `TreeAnt` |
| `TreeTableView` | `TreeTableAnt` |
| `ListView` | `ListViewAnt` |
| `MenuBar` | `MenuBarAnt` |
| `MenuButton` | `MenuButtonAnt` |
| `SplitMenuButton` | `SplitButtonAnt` |
| `Menu` / `MenuItem` | `MenuAnt`（Builder 内声明菜单项） |
| `ContextMenu` | `ContextMenuAnt` |
| `Tooltip` | `TooltipAnt` |
| `DatePicker` | `DatePickerAnt`（日期时间用 `DateTimePickerAnt`，时间用 `TimePickerAnt`） |
| `ColorPicker` | `ColorPickerAnt` |
| `TitledPane` | `TitledPaneAnt` |
| `Accordion` | `AccordionAnt` |
| `Pagination` | `PaginationAnt` |
| `Separator` | `SeparatorAnt` |
| `Canvas` | `CanvasAnt` |
| `ToolBar` | `ToolBarAnt` |
| `TabPane` / `Tab` | `TabsAnt`（`TabsAnt.controllerOf(...)` 切页） |
| `ScrollPane` | `ScrollPaneAnt` |
| `SplitPane` | `SplitPaneAnt` |
| `BorderPane` | `BorderPaneAnt` |
| `VBox` / `HBox` | `VBoxAnt` / `HBoxAnt`（条状用 `HBarAnt` / `VBarAnt`） |
| `FlowPane` / `TilePane` | `FlowPaneAnt` / `TilePaneAnt` |
| `StackPane` | `StackPaneAnt` |
| `AnchorPane` | `AnchorPaneAnt` |
| `TextFlow` | `TextFlowAnt` |
| `GridPane` | `GridAnt` |
| `Alert` | `AlertAnt` |
| `ImageView` | `ImageAnt` |
| 图表 | `ChartAnt` |
| 文件选择 | `UploadAnt`（拖拽/校验/进度） |
| Web 内容 | **无**：`javafx-web` 已移除，用宿主浏览器或图片替代 |
| 视频 | **无**：`javafx-media` 已移除 |

## 弹层

| 需求 | JFXium | 调用 |
|---|---|---|
| 模态对话框 | `ModalAnt` | `.build().open(owner)` |
| 输入弹框 | `PromptDialogAnt` | `.build().open(owner)` |
| 侧边抽屉 | `DrawerAnt` | `.build().open(owner)` |
| 下拉菜单 | `DropdownAnt` | `.build().show()` |
| 气泡卡片 | `PopoverAnt` | `.build().show()` |
| 气泡确认 | `PopconfirmAnt` | `.build().show()` |
| 全局轻提示 | `MessageAnt` | `MessageAnt.success("已保存")` |
| 右上角通知 | `NotificationAnt` | `.build().show()` |
| 桌面通知 | `DesktopNotificationAnt` | `.build().show()` |

> 弹层具体方法名以 `catalog/components.md` 的 `api` 列与源码 Javadoc 为准。

## 组合 / 模板

| 需求 | JFXium |
|---|---|
| 页面骨架（标题 + 描述 + 分区） | `PageTemplate` |
| 表单 + 校验 | `FormAnt`（`.item(...).required().rule(...).end()` → `.buildResult()`） |
| 增删改查页面 | `CrudTemplate` |
| 登录页 | `LoginTemplate` |
| 设置面板 | `ResizablePanelAnt` / `WorkspaceSettingsTemplate` |
| 页面外壳 | `AppShellAnt` / `LayoutAnt` |
| 状态栏 | `StatusBarAnt` |

## 允许的例外

- 框架内部实现（`component.*` 下的 `extends XxxAnt` 类、CSS 内部 helper）可以 `new` 原生控件。
- 非视觉 Node（`Group` 仅作纯变换容器时可由框架确认后使用；业务优先 `StackPaneAnt`）。
- `FileChooser`、`Clipboard`、`Robot` 等**非 Node** API 不受红线 #11 约束。
