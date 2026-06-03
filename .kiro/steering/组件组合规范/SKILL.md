---
inclusion: always
---

# JFXium 组件组合规范

> **核心目标**: 复杂组件 = 微组件组合。禁止重复造轮子，禁止直接操作 Node 属性。

## 关键决策树

**创建新组件前必须执行：**

```
1. 查阅"微组件清单"(第二章) → 能用原生 JavaFX Node 组合？
   ├─ 是 → 使用组合模式，跳到"标准模式"(第三章)
   └─ 否 → 继续

2. 检查项目内已有 *Ant 组件 → 能复用或扩展？
   ├─ 是 → 复用现有组件
   └─ 否 → 继续

3. 拆解 UI 稿 → 圈出"原子单位" → 每个单位对应一个微组件
   └─ 绘制微组件树：容器 > 子容器 > 叶子节点

4. 执行"检查清单"(第五章) → 设计阶段所有【必须】项
```

---

## 一、核心原则（强制执行）

### 1.1 组合优先原则

**复杂组件 = 微组件组合。微组件足够多以后，面向复杂场景就变成了简单的拼装。**

**AI 执行指令：**
- 写代码前先查阅"微组件清单"(第二章)
- 能用 JavaFX 原生 Node 组合的，禁止自己造新控件
- 复杂组件 Java 类中**严禁**调用 `setStyle()` / `setPadding()` 等 Node 属性方法
- 只负责实例化微组件 + 用布局容器串联

**违反后果：**
- 重复造轮子 → 多版本组件冲突
- 样式耦合 → 主题切换失败

### 1.2 六大铁律（绝对禁止违反）

1. **优先复用，禁止重复造轮子**
   - 写新组件前**必须**先查阅"微组件清单"(第二章)
   - 能用现有的就用现有的

2. **结构与视觉分层**
   - 结构（容器嵌套）→ Java 代码组装
   - 视觉（颜色/字号/边框）→ LESS 选择器控制
   - **禁止**在 Java 中写 `setStyle("-fx-background-color: ...")`

3. **单职责原则**
   - 一个微组件只做一件事
   - 组合时不互相耦合

4. **状态通过 styleClass 切换**
   - 基础类 + 状态修饰类
   - **禁止**在 Java 里拼字符串改样式
   - 示例：`.progress-bar` + `.progress-success`

5. **数据与微组件解耦**
   - 微组件通过 JavaFX `Property` 暴露输入（如 `valueProperty()`）
   - 微组件通过 `EventHandler` 暴露事件（如 `onActionProperty()`）
   - **禁止**微组件直接读写全局 Model / Service
   - 复杂组件通过 Binding 驱动微组件

6. **样式命名空间隔离**
   - 每个微组件**必须**有根 styleClass（如 `.jfx-avatar`）
   - LESS 中**必须**用后代选择器（`.jfx-avatar .avatar-image`）
   - **禁止**全局泛污染选择器（如裸 `.label { ... }`）

---

## 二、微组件清单

> **核心定义**：
> - **微组件（原子）** = JavaFX 自带的原生 Node，不可再拆，是最小单位。
> - **组合控件** = 由多个微组件拼装而成的 JFXium 项目内辅助层。
> 
> 写新 *Ant 组件前**先看这里**：能用原生 Node 拼出来的，就别造新控件；能用已有组合控件的，就别重复造轮子。

---

### 2.A JavaFX 原生微组件（最小控件，不可再拆）

#### 2.A.1 容器类（Container）

> 用于"放别的东西"，自身不展示内容。

| 原生 Node | 用途 | 何时用 |
|-----------|------|--------|
| `HBox` | 水平排列子节点，子节点尺寸由 `Hgrow` 决定 | 横向布局：toolbar、按钮组、header 三段式 |
| `VBox` | 垂直排列子节点，子节点尺寸由 `Vgrow` 决定 | 纵向布局：表单、列表、卡片内容区 |
| `StackPane` | 子节点叠层（z-axis），后加的在上 | 徽标覆盖 Avatar、遮罩盖 panel、loading 遮 content |
| `FlowPane` | 流式排列，超出自动换行 | 标签云、自适应按钮组、wrap 场景 |
| `GridPane` | 二维网格，每格独立 row/col 约束 | 24 栅格、表单 label-input 对齐 |
| `BorderPane` | 五区位（top/right/bottom/left/center） | 整页骨架（AppShell） |
| `AnchorPane` | 子节点锚定到父容器某边 | 绝对定位场景（少用） |
| `Pane` | 不做任何布局，纯坐标定位 | 自定义绘制底板 |
| `Region` | 最基础的可设尺寸节点，无内容 | **弹性 spacer**（配 `Hgrow=ALWAYS, maxWidth=MAX`） |
| `ScrollPane` | 给内容加滚动条 | 长列表、长内容、Drawer body |
| `SplitPane` | 拖拽分隔的多窗格 | IDE 风格分屏 |
| `TabPane` | 标签页容器 | TabsAnt 底层 |
| `TitledPane` | 可折叠的标题面板 | CollapseAnt 底层 |
| `Accordion` | 多个 TitledPane 的互斥容器 | 手风琴组件 |

#### 2.A.2 文本与图形类（Leaf）

> 显示具体内容的叶子节点。

| 原生 Node | 用途 | 何时用 |
|-----------|------|--------|
| `Label` | 不可编辑的文本（可带 graphic） | 标题、说明文字、Form label |
| `Text` | 纯文本节点（Shape 子类，可参与图形效果） | 富文本片段、特殊渲染 |
| `Hyperlink` | 链接样式的按钮 | 行内链接 |
| `ImageView` | 显示图片 | 头像、图标位图 |
| `SVGPath` | 矢量路径节点 | 自定义图标 |
| `Circle` / `Rectangle` / `Polygon` / `Arc` | 基础几何形状 | 自定义绘制、图表 |
| `Canvas` | 像素级绘制画布 | 复杂图形、游戏渲染 |
| `Region` + `-fx-shape` | CSS 控制的矢量形状 | 主题化图标（推荐） |

#### 2.A.3 控件类（Control）

> 自带交互逻辑的原生控件。

| 原生 Control | 用途 | JFXium 封装 |
|--------------|------|-------------|
| `Button` | 按钮 | ButtonAnt |
| `TextField` | 单行文本输入 | InputAnt |
| `PasswordField` | 密码输入 | InputAnt (password mode) |
| `TextArea` | 多行文本输入 | InputAnt (textarea mode) |
| `CheckBox` | 复选框 | CheckBoxAnt |
| `RadioButton` | 单选按钮 | RadioAnt |
| `ToggleButton` | 切换按钮 | SwitchAnt 底层 |
| `ChoiceBox` | 简单下拉框 | SelectAnt 底层 |
| `ComboBox` | 可编辑下拉框 | ComboBoxAnt |
| `Spinner` | 数字步进器 | SpinnerAnt / InputNumberAnt |
| `Slider` | 滑块 | SliderAnt |
| `ProgressBar` | 进度条 | ProgressAnt |
| `ProgressIndicator` | 进度圆环 | SpinAnt |
| `ListView` | 列表视图 | ListAnt |
| `TableView` | 表格视图 | TableAnt |
| `TreeView` | 树形视图 | TreeAnt |
| `TreeTableView` | 树形表格 | TreeTableAnt |
| `MenuBar` | 菜单栏 | MenuAnt |
| `MenuButton` | 菜单按钮 | DropdownAnt 底层 |
| `ContextMenu` | 右键菜单 | DropdownAnt 内部 |
| `Tooltip` | 悬浮提示 | TooltipAnt |
| `Popup` / `PopupControl` | 自定义浮层 | DropdownAnt / DatePickerAnt 内部 |
| `DatePicker` | 日期选择器 | DatePickerAnt 底层 |
| `ColorPicker` | 颜色选择器 | ColorPickerAnt 底层 |
| `Pagination` | 分页器 | PaginationAnt 底层 |
| `Separator` | 分隔线 | DividerAnt 底层 |

#### 2.A.4 窗口类（Window）

| 原生类 | 用途 | JFXium 封装 |
|--------|------|-------------|
| `Stage` | 顶层窗口 | DrawerAnt / ModalAnt 用 UTILITY/TRANSPARENT 模式 |
| `Scene` | 场景容器 | 每个 Stage 必须有一个 Scene |

#### 2.A.5 布局约束（Layout Hints）

> 这些不是 Node，是给容器看的"约束信息"。**容易被遗漏，导致 spacer 不生效**。

| 约束 | 设置位置 | 作用 |
|------|---------|------|
| `HBox.setHgrow(node, ALWAYS)` | 在 HBox 子节点上 | 该子节点水平拉伸 |
| `VBox.setVgrow(node, ALWAYS)` | 在 VBox 子节点上 | 该子节点垂直拉伸 |
| `GridPane.setConstraints(...)` | 在 GridPane 子节点上 | 指定行列位置、跨度 |
| `node.setMaxWidth(Double.MAX_VALUE)` | 在节点本身 | **必须配合 Hgrow，否则不会实际拉伸** |
| `node.setMaxHeight(Double.MAX_VALUE)` | 同上 | 配合 Vgrow |
| `Pos.*` (`setAlignment`) | 在容器上 | 子节点的对齐方式 |
| `Priority.ALWAYS / SOMETIMES / NEVER` | 配合 Hgrow/Vgrow | 拉伸优先级 |

> ⚠️ **关键陷阱**：`Label` 默认 `maxWidth = USE_PREF_SIZE`，给它设 `Hgrow=ALWAYS` 也**不会**拉伸。
> 要做弹性填充，**必须用独立 `Region` 节点**，并同时设 `Hgrow=ALWAYS` + `setMaxWidth(MAX)`。

---

### 2.B JFXium 项目内组合控件（由原生 Node 组合而成）

#### 2.B.1 布局组合控件

> 这些是对原生容器的"语义化封装"或"增强版"，底层仍是 JavaFX 原生 Node。

| 组合控件 | 底层实现 | 用途 |
|----------|---------|------|
| `FlexAnt` | `HBox` / `VBox` / `FlowPane` | CSS Flexbox 风格弹性布局，支持 wrap / justify |
| `GridAnt` | `GridPane` + percentWidth 计算 | 真正的 24 栅格系统 |
| `SpaceAnt` | `Region` / `Separator` | 间距组件，支持 split 分隔线 |
| `DividerAnt` | `Separator` + `Label` | 带文字的分隔线 |
| `AppShellAnt` / `LayoutAnt` | `BorderPane` | 整页骨架（header/sidebar/content/footer） |

#### 2.B.2 工厂方法 / 帮助类

> 这些不"扩展" JavaFX，只是把"反复用的组合"包装成快捷方法，避免每次手写 5 行 boilerplate。

| 辅助类 | 路径 | 用途 |
|--------|------|------|
| `Layouts.grow()` | `core/layout/Layouts.java` | 一行创建 `Region` + `Hgrow=ALWAYS` + `maxWidth=MAX` |
| `Layouts.spacer(w, h)` | 同上 | 一行创建固定尺寸 `Region` |
| `Layouts.hbox/vbox/grid()` | 同上 | 带默认间距/对齐的容器工厂 |
| `AbstractStyleBuilder<SELF>` | `core/builder/` | 所有 *Ant Builder 父类，统一 `style()` / `styleClass()` |
| `CssClasses` | `core/css/CssClasses.java` | 集中管理所有 styleClass 字符串常量 |

#### 2.B.3 通用 styleClass 命名空间

> styleClass 是"虚拟微组件"——用 LESS 把一组样式打包成一个名字，谁挂上谁就有那套外观。

| 命名空间 | 适用场景 |
|---------|---------|
| `jfx-popup-menu` + `.jfx-popup-menu-item` + `-divider` + `-disabled` | 任何下拉菜单浮层（Dropdown / AutoComplete / Mentions / TreeSelect） |
| `jfx-overlay-mask` + `-panel` + `-header` + `-body` + `-footer` + `-close-btn` + `-title` | 任何遮罩弹窗（Drawer / Modal） |
| 状态修饰类（如 `alert-success` / `progress-error` / `steps-finished`）| 状态机组件，挂在基础类之后切换颜色 |

---

## 三、标准模式（拷贝即用）

### 3.1 Header 三段式：[左 slot] [文本] [填充] [extra] [右 slot]

适用于 Drawer / Modal / Card / Page / Surface 等所有"带标题"的组件。

```java
HBox header = new HBox(8);
header.setAlignment(Pos.CENTER_LEFT);

// 1. 左侧 slot（可选：关闭按钮 / 返回按钮 / 图标）
if (leftSlot != null) {
    header.getChildren().add(leftSlot);
}

// 2. 标题文本（默认占自己宽度，不抢空间）
Label titleLabel = new Label(title);
titleLabel.getStyleClass().add(CssClasses.OVERLAY_TITLE);
header.getChildren().add(titleLabel);

// 3. 弹性填充（关键：把右侧推到最右）
Region spacer = new Region();
HBox.setHgrow(spacer, Priority.ALWAYS);
spacer.setMaxWidth(Double.MAX_VALUE);
header.getChildren().add(spacer);

// 4. extra 节点（用户自定义按钮组）
if (extra != null) {
    header.getChildren().add(extra);
}

// 5. 右侧 slot（可选：关闭按钮 / 更多操作）
if (rightSlot != null) {
    header.getChildren().add(rightSlot);
}
```

**关键点**：
- 用**独立 Region 当 spacer**，不要给 Label 设 `Hgrow=ALWAYS`（Label 默认 `maxWidth=USE_PREF_SIZE` 不会拉伸）
- spacer 必须 `setMaxWidth(MAX)` 双重保险

### 3.2 Footer 右对齐按钮组

适用于 Drawer / Modal / Form 提交区。

```java
HBox footer = new HBox(8);
footer.setAlignment(Pos.CENTER_RIGHT);   // 整体右对齐
footer.getStyleClass().add(CssClasses.OVERLAY_FOOTER);
footer.getChildren().addAll(cancelBtn, okBtn);
```

**关键点**：
- 默认右对齐（`Pos.CENTER_RIGHT`），符合 Ant Design 习惯。
- Footer 内容**仅约束容器对齐方式**，不限制内容类型——按钮组、链接、文字都可放。
- 如需"两端对齐"（左侧辅助文字 + 右侧主操作），用 `Region` spacer 撑开。

### 3.3 状态机：基础类 + 状态修饰类

```java
// Java 端：挂语义类，不写颜色
node.getStyleClass().add(CssClasses.PROGRESS_BAR);
node.getStyleClass().add(switch (status) {
    case SUCCESS -> CssClasses.PROGRESS_SUCCESS;
    case WARNING -> CssClasses.PROGRESS_WARNING;
    case ERROR   -> CssClasses.PROGRESS_ERROR;
    case NORMAL  -> null;
});
```

```less
/* LESS 端：颜色随状态切换 */
.progress-bar.success .bar { -fx-background-color: -color-success-emphasis; }
.progress-bar.warning .bar { -fx-background-color: -color-warning-emphasis; }
.progress-bar.error   .bar { -fx-background-color: -color-danger-emphasis; }
```

**命名约定**：
- 状态枚举统一使用 `PRIMARY / SUCCESS / WARNING / ERROR / INFO / NORMAL`。
- 修饰类前缀使用"组件名 + 状态"格式（`progress-success` 而非裸 `success`），避免选择器互相污染。

### 3.4 弹窗 popup-menu（dropdown 风格）

> 已有：`CssClasses.POPUP_MENU` + `POPUP_MENU_ITEM` + `POPUP_MENU_DIVIDER` + `POPUP_MENU_ITEM_DISABLED`

```java
VBox panel = new VBox(0);
panel.getStyleClass().add(CssClasses.POPUP_MENU);

for (Item item : items) {
    HBox row = new HBox(8);
    row.getStyleClass().add(CssClasses.POPUP_MENU_ITEM);
    if (item.isDisabled()) {
        row.getStyleClass().add(CssClasses.POPUP_MENU_ITEM_DISABLED);
    }
    // ... 内容
    panel.getChildren().add(row);
}
```

---

## 四、反模式 / 红线（绝对禁止）

> 这些是**已经踩过的坑**，命名记下来防止重复。所有红线均为**绝对禁止**，PR 中出现一律打回。

### 4.1 Label 用 Hgrow=ALWAYS 当填充

❌ **错误**：
```java
HBox.setHgrow(titleLabel, Priority.ALWAYS);  // Label 默认 maxWidth=USE_PREF_SIZE，不会拉伸
```

✅ **正确**：用独立 Region 做 spacer（见 3.1）。

> 📝 **历史教训**：Drawer/Modal 的 close 按钮位置 bug 就是这么来的。

### 4.2 创建 Region 但忘了 add 到容器

❌ **错误**：
```java
HBox.setHgrow(new Region(), Priority.ALWAYS);  // 创建后没加进 children，等于死代码
```

> 📝 **历史教训**：CodeBlockAnt 原代码就是这样。`setHgrow` 是给容器看 layout hint 的，不在 layout 树里就完全无效。

### 4.3 在 Java 里拼字符串实现 hover

❌ **错误**：
```java
btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: -color-bg-subtle; ..."));
btn.setOnMouseExited(e  -> btn.setStyle("-fx-background-color: transparent; ..."));
```

✅ **正确**：让 LESS `:hover` 伪类接管。

```less
.my-btn:hover { -fx-background-color: -color-bg-subtle; }
```

> 📝 **历史教训**：BackTopAnt / Carousel arrow / Cascader item 等 6+ 处都犯过。

### 4.4 build() 类型撒谎

❌ **错误**：
```java
public HBox build() {
    if (vertical) {
        return new HBox(new VBox(...));  // 声明 HBox 实际包了 VBox 进去骗调用方
    }
    ...
}
```

✅ **正确**：诚实声明返回类型（`Pane` / `Node`），用户拿到什么就是什么。

> 📝 **历史教训**：FlexAnt 老版本 build() 永远返回 HBox，但 COLUMN 方向时实际是 HBox(VBox)，用户调 `setOrientation(VERTICAL)` 永远不生效。

### 4.5 用 `setStyle(getStyle() + ...)` 累加样式

❌ **错误**：
```java
btn.setStyle(btn.getStyle() + "-fx-background-color: red;");
```

风险：
- inline 样式覆盖 LESS（破坏主题切换）
- 字符串拼接没去重，多次调用会越拼越长
- 没法用 `:hover` 等伪类响应

✅ **正确**：挂 styleClass，让 LESS 处理。

### 4.6 在 component 中硬编码颜色

❌ **错误**：
```java
node.setStyle("-fx-fill: #1677ff;");
arc.setStroke(Color.web("#1677ff"));
```

✅ **正确**：用 LESS 变量 `-color-accent-emphasis`，或挂 styleClass。

> 📝 **历史教训**：SpinAnt 的 `Color.web("#1677ff")` 至今没修干净（JavaFX Shape API 限制，已标 TODO）。

### 4.7 微组件侵入业务模型

❌ **错误**：微组件内部直接读写全局 Model / Service。
```java
class TagItem extends HBox {
    void onClose() { GlobalStore.removeTag(this.id); }  // 越层修改外部数据
}
```

✅ **正确**：通过 Property + 事件回调暴露能力，由复杂组件决定如何响应。
```java
class TagItem extends HBox {
    private final EventHandler<ActionEvent> onClose;
    // 复杂组件订阅 onClose，在自己的 handler 里改数据
}
```

### 4.8 全局泛污染 LESS 选择器

❌ **错误**：
```less
/* 影响整个项目所有 Label */
.label { -fx-text-fill: red; }
```

✅ **正确**：所有规则必须挂在微组件根 styleClass 后代选择器上。
```less
.jfx-avatar .avatar-label { -fx-text-fill: red; }
```

### 4.9 容器吞 padding 导致内部布局错乱（M11.2 + M19.5 + M19.6 实证）

❌ **错误**：在「容器 + 内部 .label + 内部 .arrow-button」结构的组合控件容器上设 padding。
```less
.menu-button {
  .button-base();   /* 包含 -fx-padding: ... 把容器自己撑了 */
}
```

✅ **正确**：容器 padding=0，padding 全部下放到子节点（参见项目约束 SKILL #16）。
```less
.menu-button {
  -fx-padding: 0;
  -fx-alignment: CENTER_LEFT;
}
.menu-button > .label { -fx-padding: ...; }
.menu-button > .arrow-button { -fx-padding: ...; }   /* 水平减半 */
```

> 📝 **历史教训**：MenuButton/SplitMenuButton 的箭头位置错乱、ComboBox 比 Input 矮 8px、TableView 表头行高失效——都是这个根因。

### 4.10 `.arrow` 节点只设颜色不设 shape（M19.6 实证）

❌ **错误**：以为 modena 默认 shape 一定存在。
```less
.menu-button > .arrow-button > .arrow {
  -fx-background-color: -color-fg-muted;   /* shape 缺失 → 0 尺寸不可见 */
}
```

✅ **正确**：显式设 shape + min/pref 尺寸（参见项目约束 SKILL #17）。
```less
.menu-button > .arrow-button > .arrow {
  -fx-shape: "M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z";   /* Material chevron */
  -fx-min-width: 10px; -fx-min-height: 6px;
  -fx-pref-width: 10px; -fx-pref-height: 6px;
  -fx-background-color: -color-fg-muted;
}
```

> 📝 **历史教训**：MenuButton 弹层的下拉箭头变成 `▼` 字符，就是 fallback 到 Unicode 渲染的信号。

### 4.11 字符串模板里嵌套未转义的中英文双引号（M19.7 + M19.10 多次踩到）

❌ **错误**：在 Java 文本块（text block）里直接用中文场景下的双引号包裹概念。
```java
String desc = "数据为空时的占位 —— 比"白屏"友好";   // 编译错误
```

✅ **正确**：用「」或转义。
```java
String desc = "数据为空时的占位 —— 比「白屏」友好";   // 中文引号
String desc = "数据为空时的占位 —— 比\"白屏\"友好";  // 转义
```

> 📝 **历史教训**：Showcase Section 的 description 里多次踩到（M19.7 SelectableText / M19.10 Descriptions 等）；Java 不识别中文场景下的"花引号"是字符串边界。

### 4.12 给「尺寸未钳制」的节点用 `@border-radius-full`(9999px)（M19.43 Slider track 实证）

❌ **错误**：在宽/高靠父布局拉伸、没有 min/max 钳死的节点上用 9999px 圆角。
```less
.slider .track {
  -fx-background-radius: @border-radius-full;   /* 9999px */
  -fx-pref-height: 4px;                          /* 高固定，但宽不固定 */
}
```
JavaFX 的 SliderSkin 不裁切 track 圆角 → 9999px 圆角把 track 视觉 bounds 向左右各撑出 ~9999px（探针实测 track 宽 20144px），形成「一根线横穿整个窗口」的假溢出。

✅ **正确**：圆头只要 ≥ 自身厚度一半即可；细条用普通 radius。
```less
.slider .track {
  -fx-background-radius: @border-radius-md;   /* track 4px 高，小圆角就完全圆头 */
  -fx-pref-height: 4px;
}
```

**判断**：9999px 圆角只能用在 width/height **都被钳死**的节点（thumb 14×14、badge-dot 8×8）。详见项目约束 SKILL #23。

> 📝 **历史教训**：范围 Slider 溢出，连改两轮 `maxWidth` 都没中（根因不是宽度约束，是圆角泄出）。**「溢出」症状 ≠ 根因是「宽度」**——反复修不好就写探针 dump `getBoundsInLocal()`，别猜。clip 会掩盖这类 bug（单滑块有 clip 没暴露，范围滑块没 clip 才暴露）。

---

## 五、写新复杂组件的检查清单

> 写新 *Ant 组件 / 重构旧组件前，逐项过一遍。**【必须】**项缺一不可，**【建议】**项视场景执行。

### 5.1 设计阶段

- [ ] **【必须】** 能不能用现有微组件组合出来？翻第二章清单，能复用就复用
- [ ] **【必须】** 拆解成"微组件树"：画出 `容器 > 子容器 > 叶子节点` 的层级，每层职责单一
- [ ] **【必须】** 状态机列表：列出所有状态（hover / selected / disabled / current / ...），每个状态对应一个修饰类
- [ ] **【必须】** 接口"诚实"：`build()` 返回什么类型就是什么类型，不撒谎
- [ ] **【建议】** 数据契约：输入用 `Property`，事件用 `EventHandler`，避免回调地狱

### 5.2 实现阶段

- [ ] **【必须】** Builder 继承 `AbstractStyleBuilder<Builder>`
- [ ] **【必须】** 视觉样式（颜色/字号/边框/圆角/阴影）100% 走 LESS
- [ ] **【必须】** hover/selected 等状态切换用 styleClass，不用事件回调拼字符串
- [ ] **【必须】** 用独立 Region 做填充，不要给 Label 设 Hgrow
- [ ] **【必须】** 微组件挂根 styleClass，LESS 写后代选择器（防污染）
- [ ] **【建议】** 结构样式（padding 与变量联动 / 尺寸 / 对齐）才允许在 Java 设

### 5.3 验收阶段

- [ ] **【必须】** 切换全部 8 套主题，组件颜色正常跟随
- [ ] **【必须】** 极小宽度 / 极大宽度下布局不破
- [ ] **【必须】** 禁用状态视觉与交互一致
- [ ] **【必须】** 无 inline `setStyle("-fx-...: -color-...")` 残留（grep 验证）
- [ ] **【建议】** 微组件嵌套到不同复杂组件中，样式不互相污染

---

## 六、和其他 SKILL 的关系

| 文档 | 定位 | 与本文件的关系 |
|------|------|--------------|
| `项目约束与计划/SKILL.md` | 项目级约束（色阶/原子交互/JavaFX CSS 注意事项）| **本文件是它的"组件层"补充** |
| `BaseCode/SKILL.md` | Karpathy 准则（谋定而后动 / 极简 / 精准动刀）| 本文件遵循 BaseCode 准则 |

---

## 七、包结构归约（M18 + M19 + M19.53 沉淀）

> **核心问题**：项目早期所有组件都堆在 `jfxium/component/`，规模化后命名空间紊乱——
> 「原子控件」（ButtonAnt）和「业务模板」（CrudTemplate）混在同一个包里，用户分不清粒度。
> M19.53 进一步发现：`component/` 顶层 73 个 *Ant 平铺，原子型 / 组合型 / 浮层型混在一起，
> 用户分不清哪个是「薄封装原生控件」哪个是「微组件拼装的容器」。
>
> **决策**：① 按粒度分三层包（M18）；② component 内再按类型分三子包（M19.53）。

### 7.1 顶层三层包结构（按粒度）

| 包路径 | 命名后缀 | 定位 | 粒度 | 示例 |
|---|---|---|---|---|
| `jfxium/component/` | `*Ant` | **原子控件 + 组合容器 + 浮层** | 小（独立可用，组合自由） | ButtonAnt / CardAnt / ModalAnt |
| `jfxium/template/` | `*Template`（+ 历史 `*Ant`） | **业务模板**（针对场景的整页骨架/工具栏） | 中（耦合多个原子控件） | CrudTemplate / LoginTemplate / FilterBarAnt |
| `jfxium/layout/` | `*Ant` / `*Layout` | **应用骨架**（跨页面架构） | 大（仅 1 个应用 1 个） | AppShellAnt / LayoutAnt |

### 7.2 component 内三子包结构（按类型，M19.53）

> **判定标准**：看 `build()` 返回类型——返回原生控件 = 原子；返回容器（VBox/HBox/StackPane/Pane）= 组合；返回 Result 包装 = 浮层。

| 子包 | 类型 | 判定 | 数量 | 示例 |
|---|---|---|---|---|
| `component/control/` | **原子型** | build() 返回原生 JavaFX 控件（Button/TextField/ComboBox/TableView…）薄封装 | 23 | ButtonAnt / InputAnt / ComboBoxAnt / TableAnt / TreeAnt / CheckBoxAnt / LabelAnt / IconAnt |
| `component/composite/` | **组合型** | build() 返回容器，由微组件拼装而成 | 42 | BarAnt / CardAnt / FormAnt / SurfaceAnt / AlertAnt / AvatarAnt / BadgeAnt / SwitchAnt / TabsAnt |
| `component/overlay/` | **浮层型** | build() 返回 Result 包装，需 `.open()`/`.show()` 触发 | 7 | ModalAnt / DrawerAnt / DropdownAnt / MessageAnt / NotificationAnt / PopconfirmAnt / PopoverAnt |
| `component/layout/` | **布局容器** | 容器原子（VBox/HBox/Grid 等封装） | 13 | VBoxAnt / HBoxAnt / FlowPaneAnt / GridAnt / SplitPaneAnt |
| `component/base/` | **内部基类** | 非公开 API，复杂组件的内部零件 | 9 | CloseButton / Overlay / PanelHeader / PopoverPanel |

### 7.3 写新组件前问自己

```
1. 这玩意儿是「薄封装某个 JavaFX 原生控件」（build 返回原生控件）？
   → component/control/，加 *Ant 后缀

2. 这玩意儿是「微组件拼装的容器」（build 返回 VBox/HBox/StackPane/Pane）？
   → component/composite/，加 *Ant 后缀

3. 这玩意儿是「按需弹出的浮层」（build 返回 Result，需 .open()/.show()）？
   → component/overlay/，加 *Ant 后缀

4. 这玩意儿是「容器布局原子」（VBox/Grid 等封装）？
   → component/layout/，加 *Ant 后缀

5. 这玩意儿是「针对业务场景的整页骨架/工具栏」？
   → template/，加 *Template 后缀（FilterBarAnt 是历史遗留例外）

6. 这玩意儿是「应用级跨页面架构」？
   → layout/
```

### 7.4 关键判别

**原子 vs 组合 vs 浮层**（看 build() 返回类型，最客观）：
- 原子：`Button build()` / `TextField build()` / `ComboBox<T> build()`——返回原生控件
- 组合：`VBox build()` / `HBox build()` / `StackPane build()` / `Pane build()`——返回容器
- 浮层：`ModalResult build()` / `DrawerResult build()`——返回 Result，需 `.open()`

**`*Ant` vs `*Template`**：
- `*Ant` 应该可以**单独使用、自由组合**——比如 `ButtonAnt` 哪里都能塞
- `*Template` 应该是**整页/整段骨架**——比如 `CrudTemplate` 是一个「topbar + body + bottombar」的页骨架，里头要装 TableAnt / FormAnt / CardAnt 等原子控件

**历史遗留**：
- `FilterBarAnt` 是「业务工具栏」，M19.53 已迁到 `template/`（但保留 `*Ant` 后缀避免破坏调用方 import 之外的认知）。

### 7.5 与其他规范的衔接

- 第二章「微组件清单」里 **2.A**（JavaFX 原生）+ **2.B**（项目内辅助）—— 都是粒度更小的「砖块层」
- 本节的 component（control/composite/overlay/layout）/ template / layout —— 是「砖块组装出来的成品层」
- 整体依赖单向递增：**微组件 → control（原子控件）→ composite（组合容器）→ overlay（浮层）→ template（业务模板）→ layout（应用骨架）**

### 7.6 跨子包引用注意

- 分子包后，组件间互相引用需要 `import`（同包不需要）。例如 composite 的 CardAnt 用 control 的 ButtonAnt，要 `import org.openkawu.jfxium.component.control.ButtonAnt;`。
- `module-info.java` 必须 `exports` 全部子包（control/composite/overlay/layout/base）。新增子包记得同步加 exports。

---

## 八、活的文档（维护规则）

> 这份文档**应该随项目演进**，每发现一个新坑/抽出一个新微组件，都要回来更新。

更新规则：
- 新增微组件 → 更新第二章清单
- 新踩坑 → 更新第四章反模式
- 新模式抽象 → 更新第三章标准模式
- 检查清单不完备 → 更新第五章
- **新建组件时违反包归约 → 更新第七章**（注意按 7.2 选对 control/composite/overlay 子包）

维护责任：
- 任何 PR 涉及新增 *Ant 组件 / 修复反模式 bug，必须同步更新本文档对应章节。
- Reviewer 审查 PR 时主动检查"是否需要更新本 SKILL"。

---

*版本: 1.2*
*创建日期: 2026-05-19*
*M18 更新（2026-05-24）：新增第七章「包结构归约」*
*M19.53 更新（2026-06-03）：第七章新增 component 三子包（control/composite/overlay）按类型归约*
