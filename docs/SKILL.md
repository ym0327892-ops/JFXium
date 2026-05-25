# JFXium 开发规范 (SKILL)

> 本文件定义 JFXium 项目开发过程中需要遵守的规范和行为准则。
> 本规范基于 Design Tokens 进行派生，确保所有颜色和交互元素符合设计规范。
> jfxium UI

***

## 核心原则

**不要只给一个颜色，要给一个色阶。** 所有颜色必须基于 Design Tokens 执行派生算法。

***

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

### 3.1 CSS Transition 不支持

**重要**：JavaFX CSS **不支持** CSS 的 `transition` 属性。所有 `-fx-transition` 都会被忽略。

```less
// ❌ 错误：JavaFX 不支持
.button {
  -fx-transition: background-color 0.2s;
}

// ✅ 正确：直接定义状态颜色
.button {
  -fx-background-color: -color-bg-default;
}

.button:hover {
  -fx-background-color: -color-accent-hover;
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

***

为什么这些很重要？

1\. **`-fx-background-insets`** **是神技**：Web 开发者习惯用 `border`，但在 JavaFX 里，用背景层叠 + Insets 能做出比原生边框更丝滑、不抖动的焦点效果。

2\. **避免渲染报错**：如果你不告诉它避开 `box-shadow`，Minimax 可能会生成一堆虽然在 IDE 里不报错，但程序运行起来后台日志全是 `CSS Syntax Error` 的代码。

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

