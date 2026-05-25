---
name: 项目约束与计划
description: 行为准则，用于减少常见的 LLM 编码错误。在编写、审查或重构代码时使用，避免过度复杂化、进行精准修改、暴露假设，并定义可验证的成功标准。
license: MIT
---
# JFXium 开发规范 (SKILL)

> 本文件定义 JFXium 项目开发过程中需要遵守的规范和行为准则。

***

## 核心原则

**不要只给一个颜色，要给一个色阶。** 所有颜色必须基于 Design Tokens 执行派生算法。

**设计参考哲学（M15 沉淀）**：吸取 Ant Design + Element Plus + AtlantaFX 三家优点，**不死磕单一来源**：
- **Ant Design**：色彩体系、组件 API 命名、整体视觉规范的主参考
- **Element Plus**：admin 实战细节（如 HORIZONTAL 菜单 hover 底部细线、Table 紧凑模式数值）
- **AtlantaFX**：JavaFX 内部控件 LESS 选择器层级和数值（CSS 标准答案，详见 SKILL 第 15 条）

实战决策时，按**用户体验 + 视觉一致**优先，三家有冲突时记录到 PLAN.md 当次里程碑里。

***

# 参考文档

```markdown
- Ant Design 色彩：https://ant.design/docs/spec/colors-cn
- Ant Design 布局：https://ant.design/docs/spec/layout-cn
- Element Plus 组件：https://element-plus.org/zh-CN/component/
- AtlantaFX 参考：[AtlantaFX 仓库](https://github.com/mkpaz/atlantafx)（本地路径 /Users/openai/workspace/work_open/atlantafx，按各自机器调整）
```

## 一、色板派生算法（Color Derivation Algorithm）

### 1.1 色阶结构（统一使用 0-9 级）

```less
// 色阶命名规则：
// 0: 最浅（浅色背景、悬停反馈）
// 5: Base (主色，标准状态)
// 6: Active (激活/按压状态)
// 9: 最深（最深强调）

// Accent Colors (Blue) - 完整 0-9 级色阶
@color-accent-0: #e6f4ff;  // 最浅 - 悬停背景
@color-accent-1: #bae0ff;
@color-accent-2: #91caff;   // Muted (边框/图标)
@color-accent-3: #69b1ff;
@color-accent-4: #4096ff;
@color-accent-5: #1677ff;  // Base (主色)
@color-accent-6: #0958d9;  // Active (按压状态)
@color-accent-7: #003eb3;
@color-accent-8: #002c8c;
@color-accent-9: #001d66;  // 最深
```

### 1.2 语义变量映射规则

```less
// 语义变量必须映射到色阶索引
@color-accent-emphasis: @color-accent-5;   // Base - 标准/选中状态
@color-accent-hover: @color-accent-0;      // Hover - 浅色背景
@color-accent-active: @color-accent-6;    // Active - 按压状态
@color-accent-muted: @color-accent-2;    // Muted - 边框/图标
@color-accent-subtle: @color-accent-0;    // Subtle - 极浅背景
```

### 1.3 色阶用途对照表

| 变量名         | 色阶索引     | 用途        | 示例                    |
| ----------- | -------- | --------- | --------------------- |
| `-emphasis` | 5 (Base) | 标准状态、选中状态 | 按钮背景、Switch 轨道        |
| `-hover`    | 0        | 悬停状态背景    | 按钮 hover、列表项 hover    |
| `-active`   | 6        | 按压/激活状态   | 按钮 pressed、Spinner 按压 |
| `-muted`    | 2        | 边框、图标     | 输入框边框 hover、图标颜色      |
| `-subtle`   | 0        | 极浅背景      | 表格选中行、Badge 背景        |

***

## 二、原子交互矩阵（Atomic Interaction Matrix）

> **重要**：所有组件的交互状态必须遵循此矩阵，保证视觉一致性。

### 2.1 交互状态规范表

| 状态                 | 触发条件      | 行为表现                              |
| ------------------ | --------- | --------------------------------- |
| **Normal**         | 默认状态      | 边框 `color-border`，背景 `bg-default` |
| **Hover**          | 鼠标悬停      | 边框变 `-muted`，背景微变或无变化             |
| **Focus**          | 键盘/点击获得焦点 | 边框变 `-emphasis` + Halo 外阴影        |
| **Active/Pressed** | 鼠标按下      | 背景变 `-active`                     |
| **Disabled**       | 禁用状态      | 透明度 0.4，背景变为禁用色                   |

### 2.2 Focus 焦点效果规范（统一实现）

所有输入类组件焦点效果必须使用以下实现方式：

```less
// 焦点效果：边框变色 + 外发光
.input-component:focused {
  -fx-border-color: -color-accent-emphasis;
  -fx-effect: dropshadow(gaussian, -color-accent-emphasis, 2, 0, 0, 1);
}
```

**关键参数**：

- `border-color`: 变为主色 `-color-accent-emphasis`
- `dropshadow`: 2px 模糊外阴影，往下偏移 1px

**注意**：JavaFX CSS 不支持 CSS transition 动画，所有 `-fx-transition` 属性都会被忽略。

### 2.3 Spinner/InputNumber 特殊规范

Spinner 的上下按钮是**最复杂的交互组件**，必须严格遵循：

```less
.spinner .increment-arrow-button,
.spinner .decrement-arrow-button {
  -fx-background-color: transparent;
}

.spinner .increment-arrow-button:hover,
.spinner .decrement-arrow-button:hover {
  -fx-background-color: -color-accent-hover;    // Hover: 浅色背景
}

.spinner .increment-arrow-button:armed,
.spinner .decrement-arrow-button:pressed {
  -fx-background-color: -color-accent-active;    // Active: 深色背景
}

// 图标颜色变化
.spinner .increment-arrow-button:hover .increment-arrow {
  -fx-background-color: -color-accent-emphasis; // Hover: 图标变主色
}

.spinner .increment-arrow-button:armed .increment-arrow {
  -fx-background-color: -color-fg-on-emphasis;   // Active: 图标变白
}
```

**按钮区域背景变化**：透明 → `-accent-hover` → `-accent-active`
**图标颜色变化**：`-fg-muted` → `-accent-emphasis` → `-fg-on-emphasis`

***

## 三、JavaFX CSS 注意事项

### 3.1 组件交互状态（无 CSS Transition）

**重要**：JavaFX CSS **不支持**组件交互状态的 CSS transition（如 hover、focus、pressed 状态切换的平滑过渡）。

```less
// ✅ 正确：直接定义状态颜色（无 transition）
.button {
  -fx-background-color: -color-bg-default;
}

.button:hover {
  -fx-background-color: -color-accent-hover;
}
```

**例外**：JavaFX CSS 支持动画类（Animation Classes）的 transition，如 `.fade-in`、`.slide-up`、`.scale-in` 等，这些需要 Java 代码配合使用。

```less
/* ✅ JavaFX 动画类（支持 transition）*/
.fade-in {
  -fx-opacity: 0;
}

.fade-in.visible {
  -fx-opacity: 1;
  -fx-transition: opacity 200ms ease-out;
}
```

### 3.2 CSS 变量与运行时换肤

LESS 编译后的 CSS 中的变量（如 `-color-accent-5: #1677ff`）是 **JavaFX CSS 变量**，可以在运行时通过 Java 代码修改：

```java
// 运行时换肤示例
scene.getRoot().setStyle("-color-accent-5: #ff5722;");
```

***

## 四、Dark Mode 适配规范

### 4.1 核心原则

> **通过调节亮度（Brightness）而非饱和度来确保可读性。**

### 4.2 颜色调整规则

| 亮色模式      | 暗色模式      | 调整方式     |
| --------- | --------- | -------- |
| `#1677ff` | `#1668dc` | 亮度降低 10% |
| `#52c41a` | `#49a24a` | 亮度降低 10% |
| `#faad14` | `#d49907` | 亮度降低 10% |
| `#f5222d` | `#d43037` | 亮度降低 10% |

### 4.3 暗色主题语义变量

```less
// Dark 主题语义变量
@color-accent-emphasis: @color-accent-5;   // Base: 保持主色
@color-accent-hover: @color-accent-0;       // Hover: 使用浅色（高对比度）
@color-accent-active: @color-accent-6;       // Active: 使用深色
@color-accent-muted: @color-accent-2;       // Muted: 使用较亮颜色
```

***

## 五、开发规范

### 5.1 变量优先原则

> **所有颜色必须在** **`:root`** **样式中定义，严禁在组件中硬编码。**

```less
// ✅ 正确：使用语义变量
.button {
  -fx-background-color: -color-accent-emphasis;
}

// ❌ 错误：硬编码颜色
.button {
  -fx-background-color: #1677ff;
}
```

### 5.2 状态联动原则

> **相同状态的组件必须表现完全一致（阴影大小、过渡时间、颜色深浅）。**

### 5.3 细节修正原则

| 组件        | 修正要点                                              |
| --------- | ------------------------------------------------- |
| Input 类   | 焦点时边框平滑过渡，禁止瞬间变色                                  |
| Spinner 类 | 点击按钮时背景变为 `-accent-hover`，图标变为 `-accent-emphasis` |
| 所有组件      | `:armed` 和 `:pressed` 状态必须同时定义                    |

***

## 六、文件位置与规范

| 文件类型              | 路径          | 职责           |
| ----------------- | ----------- | ------------ |
| LESS 源文件          | `css/less/` | 定义变量和组件样式    |
| variables-\*.less | `css/less/` | 颜色值和语义变量     |
| theme-base.less   | `css/less/` | 所有组件的 CSS 样式 |
| theme-\*.less     | `css/less/` | 各主题入口文件      |
| 生成的 CSS           | `css/`      | 编译后的 CSS 文件  |

***

## 七、强制约束

1. **禁止硬编码**：组件 CSS 中必须使用语义变量
2. **色阶完整**：每个主题色必须定义 0-9 级色阶
3. **状态完整**：每个组件必须定义 hover/armed/pressed/disabled 状态
4. **动画统一**：所有交互使用 `@motion-duration-mid`
5. **焦点一致**：所有焦点效果使用边框变色 + 外阴影实现
6. **严禁使用** **`-fx-transition`**：JavaFX 原生 CSS 不支持 transition 属性。请将动画逻辑剥离，仅通过 CSS 伪类（`:hover`, `:focused`, `:pressed`）控制颜色切换。
7. **原生变量输出**：LESS 编译时，必须在 `:root` 下输出 JavaFX 支持的自定义属性（如 `-color-accent-5: #1677ff;`），以便于我们在 Java 代码中动态更换主题色。
8. 属性前缀与命名规范
   - **严禁省略前缀**：所有 JavaFX 特有的属性必须以 `-fx-` 开头。
     - ❌ 错误：`background-color: red;` / `border-radius: 4px;`
     - ✅ 正确：`-fx-background-color: red;` / `-fx-background-radius: 4px;`
   - **盒模型差异**：JavaFX 使用 `-fx-padding` 而非 `padding`。且属性值通常不带单位（默认为像素），如 `-fx-padding: 10 15 10 15;`。
9. 边框与阴影的“叠加逻辑” (The Stacking Rule)
   - **外阴影 (Box Shadow) 实现**：JavaFX 不支持 `box-shadow`。必须使用 `-fx-effect` 属性。
     - 示例：`-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);`
   - **外发光/焦点环**：JavaFX 无法通过 `outline` 实现。请利用 **背景层叠 (Background Stacking)**。
     - 技巧：通过 `-fx-background-color` 设置多层颜色，配合 `-fx-background-insets`（内切）来实现边框和焦点光晕。
     - 示例：`-fx-background-color: -color-accent-emphasis, white; -fx-background-insets: -2, 0;`（这会创建一个 2px 的外扩边框）。
10. 伪类状态映射 (Pseudo-class Mapping)

    AI 经常混淆 Web 和 JavaFX 的交互状态，请严格映射：
    - **点击态**：Web 是 `:active`，JavaFX 按钮类组件是 **`:pressed`** 或 **`:armed`**。
    - **焦点态**：Web 是 `:focus`，JavaFX 是 **`:focused`**。
    - **选择态**：对于 CheckBox/RadioButton，使用 **`:selected`**。
11. 图标实现：SVG 路径 (SVG Path)
    - 不要尝试用 `background-image` 加载外部图片作为图标。
    - **首选方案**：使用 `-fx-shape` 属性直接在 CSS 中通过 SVG Path 字符串绘制矢量图标。
      - 示例：`.ant-icon { -fx-shape: "M10 20... "; -fx-background-color: -fx-primary; }`
12. 布局容器的“约束继承”
    - AI 容易混淆 `Pane` 的种类。
    - **VBox/HBox**：通过 `VBox.setVgrow()` 显式声明拉伸逻辑，而不是在 CSS 中写 `flex: 1`（JavaFX 不支持）。
    - **StackPane**：用于组件叠层（如 Badge 徽标覆盖在 Avatar 上）。
13. 字体引用 (Font Rendering)
    - JavaFX 对系统字体的引用不带引号。
    - ❌ 错误：`-fx-font-family: "Microsoft YaHei";`
    - ✅ 正确：`-fx-font-family: "Microsoft YaHei";`（虽然带引号能识别，但建议使用标准名称或逻辑名称如 `System`）。
14. **选择器组合方式：复合选择器（`.class.class`）≠ 后代选择器（`.class .class`）**

    **背景**：JavaFX CSS 跟 Web CSS 一样有"复合"和"后代"两种组合方式，但容易混淆。**M11 修 TableAnt 对齐 bug 的根因就是这里写错了。**

    - **复合选择器**（同一节点同时具备多个类）：类与类之间**没有空格**

      ```less
      // ✅ 正确：column-header 节点自身同时挂了 .align-right
      .table-view .column-header.align-right {
        -fx-alignment: center-right;
      }
      ```

    - **后代选择器**（一个类的元素**内部**的另一个类元素）：类与类之间**有空格**

      ```less
      // ⚠️ 这条规则的语义是：".align-right 元素内部" 的 ".column-header"
      .table-view .align-right .column-header {  // ← 完全不同的意思
        -fx-alignment: center-right;
      }
      ```

    **如何判断**：在 Java 端看 styleClass 是怎么挂的——

    ```java
    // 这种情况：column 节点自身挂了两个类 → 用复合选择器（同节点）
    column.getStyleClass().add("align-right");           // 加在已有的 column-header 上

    // 这种情况：parent 容器有类 A，child 节点有类 B → 用后代选择器
    parent.getStyleClass().add("dark-mode");
    child.getStyleClass().add("title");
    // 对应 LESS: .dark-mode .title { ... }（带空格）
    ```

    **常见现象**：用户调了 `.align(...)` 等 API 但没生效、调试时改 inline style 又能生效——很可能就是 LESS 选择器写错了组合方式。
    **排查方法**：编译后 grep 一下 `target/classes/.../theme-*.css`，看选择器结构是不是 `.A.B`（同节点）还是 `.A .B`（后代）。
15. **复杂 JavaFX 控件 LESS 调样式：先查 AtlantaFX 源码（M11.2 实证）**

    **背景**：JavaFX 内置控件（特别是 TableView / TreeView / TreeTableView / ComboBox 弹层）有大量隐藏的内部子节点（`.column-header-background`、`.nested-column-header`、`.show-hide-columns-button`、`.sort-order-dots-container`、`> GridPane` 等），文档不齐、靠猜会反复踩坑。

    **黄金参考**：[AtlantaFX](https://github.com/mkpaz/atlantafx) 是上手扒过 JavaFX 源码、跑过几年实战的成熟主题。
    **本地路径**：`/Users/openai/workspace/work_open/atlantafx/styles/src/components/`（按机器调整）
    **常用文件**：
    - `_data.scss` —— TableView / TreeTableView / TreeView / ListView
    - `_combo-box.scss` —— ComboBox / DatePicker / ChoiceBox
    - `_text-input.scss` —— TextField / TextArea / PasswordField
    - `_button.scss` / `_menu.scss` / `_scroll-bar.scss` 等

    **遵守规则**：
    - 调 TableView / Tree* / ListView 等"列表型"控件样式，**先去 `_data.scss` 读完一遍**，再动手改我们的 LESS
    - 不要凭印象猜选择器层级（M11 + M11.1 + M11.2 三轮回归就是反面教材）
    - AtlantaFX 的写法可以**直接照抄到我们 LESS**（替换变量名即可，比如 `-color-cell-border` → `-color-border-muted`）
    - 改完后 grep 一下 `target/classes/.../theme-*.css` 验证选择器实际生成的样子

    **典型踩坑（M11.2 5 次回归）**：
    - 把 `min-height` 设在 `.column-header`（错，应该在 `.column-header-background`）
    - 用 `-fx-cell-size` 强制行高（错，会裁切高内容；应该用 `-fx-min-height`）
    - 自定义 `.arrow` shape/padding（错，AtlantaFX 早就给出最优值 `padding 3×4 + shape "M 0 0 h 7 l -3.5 4 z"`）
    - 把 `.sort-order-dots-container` 压成 0（错，会让箭头和文字重叠）
    - `.column-header` 自身留 padding（错，应该 padding=0，下放到 `.label` 和 `> GridPane`）
16. **JavaFX 组合控件「内部 padding 下放原则」（M11.2 + M19.5 + M19.6 三次实证）**

    **背景**：JavaFX 「容器 + 内部 `.label` + 内部 `.arrow-button`」结构的组合控件（ComboBox / MenuButton / SplitMenuButton / DatePicker / ColorPicker / TableView 表头），如果在容器自身设了 `-fx-padding`，会与子节点 padding 叠加，导致内部布局错乱：
    - 子节点位置抖动 / 偏移
    - 箭头被挤到边缘
    - 整体高度比同家族控件不一致

    **正解（对齐 AtlantaFX `_menu-button.scss` / `_combo-box.scss`）**：
    - **容器自身 `-fx-padding: 0`**
    - padding **全部下放**到 `> .label`（左右等距）和 `> .arrow-button`（仅垂直 + 按需水平）
    - **arrow-button 水平 padding 减半**（用 `@spacing-sm` / `@btn-padding-x / 2`），避免箭头区过宽

    **示例对照**：
    ```less
    /* ❌ 错：容器吞 padding */
    .menu-button {
      .button-base();   /* 包含 -fx-padding: btn-padding-y btn-padding-x */
    }

    /* ✅ 对：容器 padding=0，下放到 label/arrow-button */
    .menu-button {
      -fx-padding: 0;
      -fx-alignment: CENTER_LEFT;
    }
    .menu-button > .label {
      -fx-padding: @btn-padding-y @btn-padding-x;
    }
    .menu-button > .arrow-button {
      -fx-padding: @btn-padding-y @spacing-sm @btn-padding-y 0;  /* 水平减半 */
    }
    ```

    **常见现象**：箭头位置错乱、文字与箭头不对齐、高度比 Button 矮一截、size 三档高度不一致。
    **排查方法**：grep `target/classes/.../theme-*.css`，看容器自身有没有 padding；同时对照 AtlantaFX 同名 SCSS。
17. **JavaFX 内部 `.arrow` 节点必须显式设 shape（M19.6 实证）**

    **背景**：modena 默认给 ComboBox / DatePicker 的 `.arrow` 设了 shape，但 **MenuButton / SplitMenuButton / Dropdown 弹出菜单等的 `.arrow` 没有默认 shape**。如果只覆盖 `-fx-background-color` 不设 shape，会变成「有色无形」（节点 0 尺寸不可见），JavaFX 可能 fallback 到 Unicode 字符（▼）渲染，但位置/对齐都错。

    **正解（AtlantaFX 标准）**：
    ```less
    /* ❌ 错：只设颜色，shape 缺失 */
    .menu-button > .arrow-button > .arrow {
      -fx-background-color: -color-fg-muted;
    }

    /* ✅ 对：显式 shape + min/pref 尺寸 */
    .menu-button > .arrow-button > .arrow {
      -fx-shape: "M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z";  /* Material chevron-down */
      -fx-min-width: 10px; -fx-min-height: 6px;
      -fx-pref-width: 10px; -fx-pref-height: 6px;
      -fx-background-color: -color-fg-muted;
    }
    ```

    **箭头形状对照**：
    - **Chevron**（细 V 形，Ant Design 风格）：`"M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z"` —— Material `keyboard_arrow_down`，10×6
    - **Triangle**（实心三角，AtlantaFX 风格）：`"M7 10l5 5 5-5z"` —— Material `arrow_drop_down`，8×8

    **支持 NONE 关闭箭头**：`.no-arrow > .arrow-button > .arrow { -fx-shape: null; ... }`

    **典型现象**：弹层组件的下拉箭头看起来是 ▼ 字符（说明 fallback 了），位置不对齐、大小诡异。
18. **\*Ant Builder `build()` 返回类型契约：直接节点型 vs Result 包装型（M17 实证）**

    **背景**：项目里 `*Ant.create()...build()` 返回值有两种类型，混用容易写错：

    | 类型 | 代表组件 | `build()` 返回 | 用法 |
    |---|---|---|---|
    | **直接节点型** | Button / Input / Card / Table / Watermark / SplitBar / CrudTemplate | 直接可用的 Node（Button/HBox/VBox/BorderPane 等） | 拿到直接 `.add(...)` 或 `setCenter(...)` |
    | **Result 包装型** | Modal / Drawer / Dropdown | XxxResult 对象（不是 Node） | 拿到后必须 `.open(owner)` / `.show()` 才显示 |

    **判断标准**：
    - 「持续显示在容器树里」的组件 → 直接节点型
    - 「按需弹出 / 浮层 / 一次性触发」的组件 → Result 包装型（需要 owner Stage 上下文，不能预先放进容器）

    **示例对照**：
    ```java
    // ✅ 直接节点型：build() 即可用
    Button btn = ButtonAnt.create("Save").build();
    container.getChildren().add(btn);

    // ✅ Result 包装型：build() 后还需 open
    ModalAnt.create()
        .title("Confirm")
        .content("Are you sure?")
        .build()           // 返回 ModalResult
        .open(ownerStage); // 触发显示

    // ❌ 错：忘了 .open()
    ModalAnt.create()...build();   // 编译能过，但 Modal 不会出现
    ```

    **新组件命名约定**：
    - 直接节点型：`build()` 返回类型应**诚实**（VBox/HBox/Pane/BorderPane 等，不撒谎）
    - Result 包装型：`build()` 返回 `XxxResult`，Result 类公开 `open()` / `show()` / `close()` 等触发方法

19. **i18n 文案走 `Messages.get(key)`，禁止硬编码（M19.18 实证）**

    **背景**：项目里所有面向最终用户的字符串（按钮文字 / placeholder / 默认提示 / 错误信息）必须走 `org.openkawu.jfxium.core.i18n.Messages`，不能硬编码到 `*.java`。

    **强约束**：
    - ❌ 错：`new Button("复制")` / `setPromptText("请选择")` / `private String okText = "OK"`
    - ✅ 对：`new Button(Messages.get("codeblock.copy"))` / 默认值 `null` + `build()` 时 lazy 取 i18n

    **Builder 默认值的正确写法（关键陷阱）**：
    ```java
    // ❌ 错：字段初始化时锁死 Locale —— Builder 实例化那一刻取的是当时的 Locale，
    //   之后用户调 Messages.setLocale(en) 也不影响这个 Builder
    private String placeholder = Messages.get("treeselect.placeholder");

    // ✅ 对：null 占位 + build() 时 lazy 求值
    private String placeholder = null;
    public Builder placeholder(String s) { this.placeholder = s; return this; }
    public Node build() {
        String effective = placeholder != null ? placeholder : Messages.get("treeselect.placeholder");
        // ...
    }
    ```

    **响应式刷新（B 类常驻组件）**：
    ```java
    // 仅当用户没显式覆盖时才挂 listener，避免覆盖用户传入的业务文案
    if (placeholder == null) {
        Messages.localeProperty().addListener((obs, ov, nv) ->
                field.setPromptText(Messages.get("treeselect.placeholder")));
    }
    ```

    **A 类（一次性弹窗 / 短生命周期，如 Modal/Popconfirm/LoginTemplate）vs B 类（常驻 UI 容器树）**：
    - A 类：构造时取一次 `Messages.get(...)`，**不挂 listener**（避免 listener 泄漏）
    - B 类：监听 `Messages.localeProperty()` 自动刷新文案

    **i18n key 命名约定**：`组件名小写.元素名`，如 `codeblock.copy` / `treeselect.placeholder` / `modal.ok` / `dashboard.compared_to_last_week`。

    **资源文件位置**：`jfxium/src/main/resources/org/openkawu/jfxium/i18n/messages{,_zh_CN,_en}.properties`，UTF-8，按组件分组用 `#` 注释。

20. **JavaFX 布局两个鲜为人知的 API：`setMaxSize(USE_PREF_SIZE)` 与 `setViewOrder()`（M19.18 Carousel 修复实证）**

    **背景**：Carousel 修 4 个 bug 时连续踩到 JavaFX 容器的两个非常不直观的特性，沉淀如下。

    **20.1 `HBox / VBox` 在 `StackPane` 内默认拉伸到 MAX**

    HBox / VBox 的 `maxWidth = maxHeight = Double.MAX_VALUE`（默认值）。塞进 StackPane 后会被拉伸到撑满父容器，导致 `StackPane.setAlignment(child, BOTTOM_CENTER)` 看似失效（其实是 child 已经撑满了父容器，"贴底"对一个撑满的容器没有视觉差异）。

    ```java
    // ❌ 错：dotsBox 撑满整个 carousel，alignment 失效
    HBox dotsBox = new HBox(8);
    StackPane.setAlignment(dotsBox, Pos.BOTTOM_CENTER);

    // ✅ 对：强制 dotsBox 收缩到内容尺寸，alignment 才真正生效
    HBox dotsBox = new HBox(8);
    dotsBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    StackPane.setAlignment(dotsBox, Pos.BOTTOM_CENTER);
    ```

    **同理**：HBox 内部用 spacer Region 撑开时，`Region` 默认 `maxWidth = USE_PREF_SIZE`，反而需要 `setMaxWidth(MAX_VALUE)` + `Hgrow=ALWAYS` 双保险才能拉伸（详见组件组合规范 SKILL §3.1）。**HBox/VBox 容器是 MAX，Region/Label 默认是 PREF —— 方向相反，记牢这个反直觉的差异**。

    **20.2 StackPane 子节点 z-order 用 `setViewOrder()` 控制**

    StackPane 默认按 children 添加顺序叠层（后加的在上），但中后期想强制某个子节点浮在最上层（如箭头按钮 / 浮层 dots），改 children 顺序又会破坏其他逻辑。**正确做法是用 `setViewOrder()`：数值越小越靠前（在用户视野最前）**。

    ```java
    // ✅ 让箭头按钮和 dots 永远浮在 slide 之上
    arrowBtn.setViewOrder(-100);  // 浮在最前
    dotsBox.setViewOrder(-100);
    contentPane.setViewOrder(0);  // 默认值，在 slide 层
    ```

    **典型现象**：dots / 箭头按钮被 slide 撑满后遮住"看不见"——大概率是 z-order 问题，不是 CSS 问题。

    **20.3 CSS 复合选择器优先级提升（M11 #14 的姐妹篇）**

    SKILL #14 讲了「复合选择器（`.A.B`）vs 后代选择器（`.A .B`）」的语义差异。本轮补一条**优先级**陷阱：

    JavaFX 把 `.button` 这种通用规则定义在前面，把 `.carousel-arrow-btn` 等场景规则定义在后面，**同特异性**下后定义胜出 —— 看似没问题。但 `.button:hover` / `.button:armed` 等带伪类的规则在 hover/armed 时**优先级高于裸 `.carousel-arrow-btn`**，导致场景样式被通用样式覆盖。

    ```less
    // ❌ 错：carousel-arrow-btn 在 hover/armed 状态下被 .button:hover 覆盖
    .carousel-arrow-btn { -fx-background-color: rgba(0,0,0,0.4); }

    // ✅ 对：复合选择器（特异性=2）+ 补全所有伪类状态
    .button.carousel-arrow-btn { -fx-background-color: rgba(0,0,0,0.4); }
    .button.carousel-arrow-btn:hover { -fx-background-color: rgba(0,0,0,0.6); }
    .button.carousel-arrow-btn:armed,
    .button.carousel-arrow-btn:pressed { -fx-background-color: rgba(0,0,0,0.75); }
    ```

    **判断规则**：当你给 `.button` 节点挂额外 styleClass `.carousel-arrow-btn` 想覆盖默认样式时，**必须用 `.button.carousel-arrow-btn` 复合选择器**，不要写裸 `.carousel-arrow-btn`。

21. **JavaFX CSS 圆角必须 `-fx-background-radius` + `-fx-border-radius` 两个一起覆盖（M19.20 Switch thumb 实证）**

    **背景**：JavaFX 把"背景圆角"和"边框圆角"分成两个独立属性。modena 默认很多组件两个都设了相同值，但**改写时必须两个一起改**，否则只覆盖一个时另一个残留默认值。

    **典型现象**：组件外观一半圆角一半直角，看起来违和（如 Switch ROUNDED 模式下 thumb 视觉还是圆形）。

    ```less
    /* ❌ 错：只覆盖 background-radius，border 还是圆形 */
    .jfx-switch.shape-square .jfx-switch-thumb {
      -fx-background-radius: 0;   /* 背景变方了 */
      /* border 还是 modena 默认 9px → 边框还是圆形 → 视觉违和 */
    }

    /* ✅ 对：两个 radius 一起覆盖 */
    .jfx-switch.shape-square .jfx-switch-thumb {
      -fx-background-radius: 0;
      -fx-border-radius: 0;
    }
    ```

    **类似陷阱**：`-fx-background-color`（背景色）和 `-fx-border-color`（边框色）也是独立属性，**改一个不会自动改另一个**。如果想让节点完全无边框，单独写 `-fx-border-color: transparent;` 不够，还要确认 `-fx-border-width` 没被默认值撑出占位。

    **排查方法**：grep 编译产物 `target/classes/.../theme-*.css`，看节点上 `-fx-border-radius` / `-fx-border-color` / `-fx-border-width` 三个属性的实际值。

***

为什么这些很重要？

1\. **`-fx-background-insets`** **是神技**：Web 开发者习惯用 `border`，但在 JavaFX 里，用背景层叠 + Insets 能做出比原生边框更丝滑、不抖动的焦点效果。

2\. **避免渲染报错**：如果你不告诉它避开 `box-shadow`，Minimax 可能会生成一堆虽然在 IDE 里不报错，但程序运行起来后台日志全是 `CSS Syntax Error` 的代码。

3\. **复合选择器陷阱**：写错一个空格，整条规则永远匹配不到，但**编译不报错、运行不报错**——只能靠肉眼看截图发现样式没生效。这是最隐蔽的一类 bug（M11 实证）。

4\. **AtlantaFX 是 JavaFX 主题的标准答案**：自己猜 TableView / Tree* / ComboBox 弹层等复杂控件的内部结构会反复回归（M11.2 一周内踩了 5 次）。AtlantaFX 源码 5 分钟读一段就能少走数小时弯路。**优先扒 AtlantaFX 源码，再改我们的 LESS**。

5\. **「内部 padding 下放原则」是组合控件统一视觉的钥匙**：容器吞 padding 是 ComboBox/MenuButton/DatePicker 这一族控件「视觉不一致 + 子节点错位」的通用根因（已在 M11.2/M19.5/M19.6 三次实证）。**记住「容器 padding=0，padding 下放」即可避免**。

6\. **`.arrow` 没默认 shape 时只设颜色 = 看不见**：MenuButton/SplitMenuButton 的 `.arrow` 没有 modena 默认 shape，必须显式 `-fx-shape` + min/pref 尺寸；Chevron `M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z` 是 Ant Design 风格的标准答案。

7\. **Builder 返回类型契约要诚实**：直接节点型（Button/Card 等）`build()` 返回真实容器；Result 包装型（Modal/Drawer/Dropdown 等）返回 Result 后还要 `.open()`。混用会写出能编译但 Modal 不弹出的隐性 bug。

***

## 八、组件分类批次（Component Batch Strategy）

### 第一批：架构底层（已完成）

- [x] 全局 CSS 变量声明 (`:root` 样式)
- [x] 核心按钮 (ButtonAnt)
- [x] 输入框 (InputAnt)
- [x] 主题切换机制

### 第二批：打样评审（已完成）

- [x] SpinnerAnt / InputNumberAnt
- [x] ComboBoxAnt
- [x] 所有交互细节统一验证

### 第三批：批量平移（已完成）

- [x] 剩余组件已按统一规范实现

