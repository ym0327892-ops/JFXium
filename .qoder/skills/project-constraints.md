---
name: project-constraints
description: >
  JFXium 项目专属技术约束。在编写或审查组件代码、LESS 样式、主题文件时使用。
  涵盖色阶派生算法、交互状态矩阵、JavaFX CSS 强制规则、PC UI 标准。
  所有组件开发必须遵循本技能。
---

# JFXium 项目技术约束

> 核心原则：**不要只给一个颜色，要给一个色阶。**

## 一、色板派生算法

### 色阶结构（0-9 级，强制完整）

```less
@color-accent-0: #e6f4ff;  // 最浅 - 悬停背景
@color-accent-5: #1677ff;  // Base - 主色
@color-accent-6: #0958d9;  // Active - 按压
@color-accent-9: #001d66;  // 最深

// 语义变量必须映射到色阶索引
@color-accent-emphasis: @color-accent-5;   // 标准/选中
@color-accent-hover: @color-accent-0;      // 悬停背景
@color-accent-active: @color-accent-6;     // 按压状态
@color-accent-muted: @color-accent-2;      // 边框/图标
```

### 用途对照

| 语义 | 色阶 | 用途 |
|------|------|------|
| `-emphasis` | 5 | 按钮背景、Switch 轨道 |
| `-hover` | 0 | 按钮 hover、列表项 hover |
| `-active` | 6 | 按钮 pressed、Spinner 按压 |
| `-muted` | 2 | 输入框边框 hover、图标颜色 |
| `-subtle` | 0 | 表格选中行、Badge 背景 |

## 二、原子交互矩阵

| 状态 | 触发 | 表现 |
|------|------|------|
| **Normal** | 默认 | 边框 `color-border`，背景 `bg-default` |
| **Hover** | 鼠标悬停 | 边框变 `-muted`，背景微变 |
| **Focus** | 获焦点 | 边框变 `-emphasis` + dropshadow 外阴影 |
| **Active/Pressed** | 鼠标按下 | 背景变 `-active` |
| **Disabled** | 禁用 | 透明度 0.4 |

### Focus 焦点效果（统一实现）

```less
.input-component:focused {
  -fx-border-color: -color-accent-emphasis;
  -fx-effect: dropshadow(gaussian, -color-accent-emphasis, 2, 0, 0, 1);
}
```

## 三、PC UI 尺寸基准（强制）

| 维度 | PC admin 标准 | 项目落地 |
|------|--------------|---------|
| 控件高度 | 28-32px | default 32 / compact 28 |
| 行高 | 32-40px | Table default 48→收 |
| 卡片 padding | 12-16px | Card body 12 |
| 字号正文 | 13-14px | @font-size-md 14 |
| 间距梯度 | 4/8/12/16 | @spacing-xs/sm/md/lg |
| 圆角 | 4-6px | @border-radius-md 6 |

**密度定位**：default 是验收基准，面向桌面 admin 调优。large 仅兼容保留。

### PC UI 布局准则

1. **信息密度优先**：一屏尽量多内容，少滚动
2. **三段式无处不在**：header / toolbar / footer 都是「左信息 + 弹性 spacer + 右操作」——统一底层用 **BarAnt**（核心布局原子）
3. **组合容器只做壳**：Card / Modal / Drawer / Form 的 header/footer slot **CSS padding=0**，高度由传入的 BarAnt 自身 `.padding(...)` 自控
4. **最小组件组合、自下而上撑**：尺寸由叶子节点撑，不由外壳 CSS 钳死。新组件凡 padding / pref/min-height 一律走 token 或由传入节点自控，禁止硬编码 px
5. **固定 + 弹性混合**：sider 固定宽、content 弹性

### PC UI 视觉准则

1. **分隔线 > 留白**：密集界面靠 1px 线划分区域（`-color-border-muted`），不靠大空白
2. **阴影克制**：桌面用边框分隔多于阴影；阴影仅用于真正的浮层（Modal/Dropdown/Popover）
3. **圆角小**：4-6px，不用 Web 的 8-12px 大圆角
4. **hover 态必须有**：所有可交互组件定义 `:hover`（PC 必须，Web 移动端没有）
5. **不实现触摸特性**：手势 / 下拉刷新 / 触摸滑动一律不做

## 四、JavaFX CSS 强制规则

### 4.1 禁止项

| 禁止写法 | 正确替代 |
|---------|----------|
| `-fx-transition` 用于交互状态 | 直接定义状态颜色，用伪类切换（**动画类 `.fade-in` 等除外**） |
| `box-shadow` | `-fx-effect: dropshadow(...)` |
| `padding` / `border` | `-fx-padding` / `-fx-border-color` |
| `:active` | `:pressed` 或 `:armed` |
| `:focus` | `:focused` |
| `background-image` | `-fx-shape: "M..."` + `-fx-background-color` |
| `flex: 1` | `HBox.setHgrow(node, ALWAYS)` |

### 4.2 必须项

- 所有 CSS 属性必须以 `-fx-` 前缀开头
- **交互控件**边框/外发光用 **背景层叠**：`-fx-background-color: borderColor, fillColor; -fx-background-insets: -2, 0;`（布局容器层走原生 border，详见第九节）
- 所有高度/padding 必须用 token（`@control-height`），不硬编码 px
- `:armed` 和 `:pressed` 状态必须同时定义
- 复合选择器（`.A.B`）vs 后代选择器（`.A .B`）要严格按 styleClass 挂载方式选择
- **使用原生 `-fx-border-*` 时必须同时声明 `-fx-border-style: solid`**（JavaFX/modena 默认为 `none`，不声明则边框不渲染）。仅当选择器继承了 modena 已设 `border-style` 的原生控件（如 `.button`、`.text-field`）时可省略

### 4.3 组合控件「内部 padding 下放」

```less
/* 容器 padding=0，下放到子节点 */
.menu-button { -fx-padding: 0; }
.menu-button > .label { -fx-padding: @btn-padding-y @btn-padding-x; }
.menu-button > .arrow-button { -fx-padding: @btn-padding-y @spacing-sm @btn-padding-y 0; }
```

### 4.4 `.arrow` 节点必须显式设 shape

```less
.arrow {
  -fx-shape: "M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z";
  -fx-min-width: 10px; -fx-min-height: 6px;
  -fx-background-color: -color-fg-muted;
}
```

### 4.5 圆角必须双属性覆盖

```less
/* background-radius + border-radius 一起改 */
.shape-square .thumb {
  -fx-background-radius: 0;
  -fx-border-radius: 0;
}
```

### 4.6 `@border-radius-full`(9999px) 仅限尺寸钳死节点

- **安全**：thumb 14×14、badge-dot 8×8（width/height 都被钳死）
- **危险**：track、进度条等宽度靠父布局拉伸的节点

### 4.7 动画类例外

JavaFX CSS **不支持**交互状态的 transition，但**支持动画类的 transition**：
```less
/* ✅ 动画类（支持 transition） */
.fade-in { -fx-opacity: 0; }
.fade-in.visible {
  -fx-opacity: 1;
  -fx-transition: opacity 200ms ease-out;
}
```

## 五、Dark Mode 适配

通过调节亮度而非饱和度：亮色 → 暗色亮度降低约 10%。

## 六、i18n 规范

- 文案走 `Messages.get(key)`，禁止硬编码
- Builder 默认值用 `null` 占位，`build()` 时 lazy 取 i18n
- A 类（一次性弹窗）不挂 listener；B 类（常驻组件）监听 `localeProperty()`
- key 命名：`组件名.元素名`（如 `codeblock.copy`）

**Builder 默认值陷阱（关键）**：
```java
// ❌ 错：字段初始化时锁死 Locale
private String placeholder = Messages.get("treeselect.placeholder");

// ✅ 对：null 占位 + build() 时 lazy 求值
private String placeholder = null;
public Node build() {
    String effective = placeholder != null ? placeholder : Messages.get("treeselect.placeholder");
    // ...
}
```

**B 类常驻组件响应式刷新**：
```java
// 仅当用户没显式覆盖时才挂 listener
if (placeholder == null) {
    Messages.localeProperty().addListener((obs, ov, nv) ->
            field.setPromptText(Messages.get("treeselect.placeholder")));
}
```

## 七、Controller 模式

组件 `build()` 后需改状态时，用 Controller 而非 rebuild：

```java
MenuAnt.Controller ctrl = MenuAnt.controllerOf(menu);
ctrl.setSelectedKey("file");   // 改已渲染节点的 styleClass，不重建
```

已有 Controller：MenuAnt / StepsAnt / AnchorAnt。

## 八、Builder 返回类型契约

| 类型 | 代表 | `build()` 返回 | 用法 |
|------|------|---------------|------|
| **直接节点型** | Button/Card/Table | Node | 直接 `.add()` 到容器 |
| **Result 包装型** | Modal/Drawer/Dropdown | XxxResult | 必须 `.open(owner)` |

判断：持续显示 → 直接节点；按需弹出 → Result。

## 九、边框技术分层策略（border vs background-insets）

> 参考 AtlantaFX（`_config.scss` 注释）：**"Most components use background insets to draw its borders due to performance reasons"**——但这是针对**交互控件**而言的。布局容器应走原生 border。

### 分层决策表

| 层级 | 技术选型 | 适用场景 | 代表组件 |
|------|---------|---------|----------|
| **交互控件层** | `background-insets` 背景层叠 | 多状态切换（normal/hover/focus/pressed）、圆角边框、焦点环 | ButtonAnt, InputAnt, ComboBoxAnt |
| **布局容器层** | 原生 `-fx-border-*` | 1px 直线分割线、无圆角、无焦点环、无多状态切换 | BarAnt, GroupBoxAnt, AppShellAnt |
| **纯分割线** | 原生 `-fx-border-*` | 需要极细线条、不干扰布局占位 | Separator, Divider |

### 布局层走原生 border 的理由

1. **4 条规则全是 1px 直线**：无圆角、无焦点环，原生 border 在此场景下**没有锯齿问题**（锯齿只在圆角/小尺寸时才出现）
2. **占位与可见区对齐**：`background-insets` 使用负 inset 时，「组件占位空间」和「可见区域」不对齐，对布局容器反而是坑
3. **border 占外空间 1px**：这是已知且可接受的代价，布局组件靠这 1px 划出清晰边界

### 交互控件层走 background-insets 的理由

1. **状态切换高效**：hover/focus/pressed 只需换第一层 `background-color` 值，无需重绘 border 区域
2. **圆角完美**：内外层 `-fx-background-radius` 配合，圆角过渡无锯齿
3. **不占外部空间**：边框效果完全在节点 padding 区域内绘制，不影响布局计算

### 写法对照

```less
/* ✅ 交互控件（Button/Input）：background-insets 层叠 */
.jfx-button {
  -fx-background-color: -color-border-default, -color-bg-default;
  -fx-background-insets: 0, 1;
  -fx-background-radius: @border-radius-md, calc(@border-radius-md - 1px);
}
.jfx-button:focused {
  -fx-background-color: -color-accent-emphasis, -color-bg-default;  /* 只换颜色 */
}

/* ✅ 布局容器（BarAnt/GroupBoxAnt）：原生 border */
.jfx-bar {
  -fx-border-color: transparent transparent -color-border-muted transparent;
  -fx-border-width: 0 0 1 0;
}

/* ✅ 纯分割线（Separator）：原生 border */
.separator:horizontal > .line {
  -fx-border-color: -color-border-muted transparent transparent transparent;
  -fx-border-insets: 1 0 0 0;
}
```

### AtlantaFX 源码中的实证

| 文件 | 组件类型 | 使用技术 | 原文注释 |
|------|---------|---------|----------|
| `_button.scss` | 交互控件 | `background-insets: 0, $border-width` | "performance reasons" |
| `_text-input.scss` | 交互控件 | `background-insets: 0, $border-width` | 同上 |
| `_card.scss` | 布局容器 | `-fx-border-color` + `-fx-border-width` | 无圆角状态切换 |
| `_separator.scss` | 分割线 | `-fx-border-color` | **"using border instead of insets to get thinner line"** |
| `_toolbar.scss` | `background-insets` 层叠 | `-fx-background-insets: 0, 0 0 $border-width 0` | 方向灵活（horizontal/vertical/bottom 切换 inset 方向），ToolBar 是控件而非布局容器 |

## 十、控件查 AtlantaFX 源码

调 TableView / TreeView / ComboBox 弹层等复杂或者简单控件样式时(它每个css样式都考虑到了,用的过程中没见过哪个样式缺失的)，**先读 AtlantaFX 源码**，再改 LESS。不凭印象猜选择器层级。

**本地源码路径**：`ant-design-ref/AntLantaFx/src/`

| 文件 | 覆盖控件 |
|------|----------|
| `components/_data.scss` | TableView / TreeView / ListView |
| `components/_combo-box.scss` | ComboBox / DatePicker / ChoiceBox |
| `components/_text-input.scss` | TextField / TextArea |
| `components/_button.scss` | Button / ToggleButton |
| `components/_menu.scss` | Menu / ContextMenu / MenuBar |
| `components/_tab-pane.scss` | TabPane（三种风格：普通/floating/classic） |
| `components/_scrolling.scss` | ScrollBar / ScrollPane |
| `components/_card.scss` | Card（原生 border 写法参考） |
| `components/_toolbar.scss` | ToolBar（background-insets 层叠写法参考） |
| `components/_dialog.scss` | DialogPane |
| `settings/_config.scss` | 全局 token（间距/圆角/阴影/elevation） |
| `settings/_color-scale.scss` | 色阶定义（base/accent/success/warning/danger 0-9） |
| `settings/_color-vars.scss` | 语义变量映射（fg/bg/border/neutral/accent/success/warning/danger） |
| `settings/_effects.scss` | 阴影 mixin（dropshadow） |
| `settings/_icons.scss` | SVG 图标 path 库 |

**编译好的 CSS 输出**（用于直接查看最终效果）：`ant-design-ref/AntLantaFx/dist/`
- `antdesign-light.css` / `antdesign-dark.css` — Ant Design 风格
- `primer-light.css` / `primer-dark.css` — GitHub Primer 风格
- `cupertino-light.css` / `cupertino-dark.css` — macOS 风格

### 复合选择器优先级陷阱

给 `.button` 节点挂额外 styleClass 覆盖默认样式时，**必须用 `.button.xxx` 复合选择器**（特异性=2），不要写裸 `.xxx`：
```less
// ❌ 错：hover/armed 时被 .button:hover 覆盖
.carousel-arrow-btn { -fx-background-color: rgba(0,0,0,0.4); }

// ✅ 对：复合选择器 + 补全所有伪类
.button.carousel-arrow-btn { -fx-background-color: rgba(0,0,0,0.4); }
.button.carousel-arrow-btn:hover { -fx-background-color: rgba(0,0,0,0.6); }
```

## 十一、布局组件 API 边界与防复发清单

> 来自 `component/layout` 多轮审计（BUG #120-#122、#128）：layout 包是业务最常直接拼装的基础设施，必须把“外部输入不可信”作为默认前提。

### 11.1 Builder/链式 API 入参归一化

| 入参类型 | 必须行为 | 典型组件 |
|----------|----------|----------|
| `gap/spacing/gutter/lineSpacing` | 负数、`NaN`、`Infinity` 归 0 | HBox/VBox/FlowPane/TilePane/Flex/Space/Grid/TextFlow |
| `size/width/height/prefRows/prefColumns` | 非有限数回默认；计数类最小为 1；尺寸类按组件语义钳制 | TilePane/Grid/Flex/Divider |
| 枚举参数 | `null` 回默认值，不允许拖到 `switch` 阶段 NPE | Flex/Space/SplitPane/Divider |
| 文案参数 | `null` 转空字符串，或 build 时 lazy 取 i18n | Divider/Empty/Result/Timeline |
| 回调参数 | 允许为空；为空时不绑定 handler | Empty/Result/按钮类 extra action |
| `Node...` / 集合参数 | 容器参数允许为空；逐个过滤 null 节点 | 所有 layout 容器 |

### 11.2 Node parent 生命周期

- JavaFX 同一个 `Node` 只能有一个 parent；任何 `content(...)`、`children(...)`、响应式重建、跨容器移动逻辑，都要先释放旧内部容器里的 children，再重新挂载。
- 包了内部 viewport/wrapper 的组件（如 `ScrollPaneAnt`）必须把 wrapper 当实现细节管理，`content(null)` 和重复 `content(sameNode)` 都要可安全调用。
- 响应式重建时不能直接把已有业务节点 add 到新容器；先清旧 `GridPane/VBox/HBox` 的 children，避免 parent 冲突。

### 11.3 响应式与离屏构建

- 响应式组件不能假设首次构建就是桌面宽屏。离屏 build、snapshot、打印、测试测量时没有 Scene 宽度，默认断点应保守，优先用 `XS`，入场景后再根据 `Scene.widthProperty()` 刷新。
- Grid 类布局超过 24 列时应换到下一条物理行，而不是截断后续节点；`rowGutter` 应同步作用到多行 `GridPane.vgap` 和总高度。

### 11.4 Java 结构与 LESS 视觉分工

- Java 端只负责节点结构、约束关系和必要的布局优先级；视觉间距、padding、线宽、字号必须走 styleClass + LESS token。
- 禁止在 Java 构造参数或 setter 中写固定视觉 px，例如 `new HBox(8)`、`setMinWidth(8)`、`setMaxWidth(24)` 用来表达组件视觉间距。应改为 `-fx-spacing: @spacing-*`、`-fx-padding: @spacing-*`，或用 `HBox.setHgrow(...)` 表达结构关系。
- compact 主题只会影响引用 token 的样式；任何写死在 Java 或 CSS 字面 px 的尺寸，都不会自动随密度收紧。

### 11.5 layout 包回归测试最低要求

- 新增或修改 layout 组件时，至少覆盖：null 枚举、负数/非有限数、null 节点、重复 build、重复 content、跨容器移动、响应式断点切换中的相关项。
- 修复 parent 生命周期问题时，测试必须断言旧 parent 已释放，并验证同一业务节点可再次挂载。
- 修复主题/密度问题时，测试至少断言 Java 端不再写固定尺寸；视觉 token 是否生效由 LESS 编译和人工验收确认。

## 十二、组合组件 API 边界与防复发清单

> 来自 `component/composite` 多轮审计（BUG #123-#127）：这类组件是业务层最常直接拼装的展示/交互容器，必须把“外部输入不可信”作为默认前提，不能把空值、异常数值或旧字节码兼容问题留到运行时。

### 12.1 Builder/链式 API 入参归一化

| 入参类型 | 必须行为 | 典型组件 |
|----------|----------|----------|
| `count/value/size/scale` | 非有限数、负数按语义钳制；计数类最小为 1 | `RateAnt` / `ProgressAnt` / `FloatButtonAnt` |
| `status/type/mode/layout/shape` 枚举 | `null` 回默认值，不允许拖到 `switch` 阶段 NPE | `ResultAnt` / `FloatButtonAnt` / `RateAnt` / `TimelineAnt` / `DescriptionsAnt` / `TagAnt` |
| `title/subTitle/text/pending` 文案 | `null` 转空字符串，或 build 时 lazy 取默认文案 | `ResultAnt` / `ResultDisplay` / `TimelineAnt` / `TagAnt` / `EmptyAnt` |
| 回调参数 | 允许为空；为空时不绑定 handler | `EmptyAnt.extraButton` / `ResultAnt.extraButton` / `FloatButtonAnt.onClick` |
| `Node...` / 集合参数 | 容器参数允许为空；逐个过滤 null 节点或 null item | `ListAnt` / `DescriptionsAnt` / `TimelineAnt` |
| `column/span/prefRows/prefColumns` | 计数类最小为 1；span 需限制在合法范围内 | `DescriptionsAnt` / `GridAnt` / `TilePaneAnt` |

### 12.2 运行时状态与 build() 合同

- `build()` 必须返回真实类型，不能“看起来能用但运行时签名不对”。
- 组合组件如果会在 build 后变更状态，必须提供 Controller 或等价的稳定入口，不能让 demo 通过重建整个节点树来绕过 API 缺口。
- `build()` 如果可能被重复调用，必须幂等，不能重复追加 spacer、监听器或临时节点。
- 运行时修改 UI 时，要同时更新已构建节点树和内部状态缓存，不能只改 Builder 字段。

### 12.3 节点生命周期与线程边界

- 同一个 `Node` 只能有一个 parent；所有 content 替换、重复挂载、跨容器移动前都要释放旧容器中的引用。
- 包了内部 wrapper / viewport / overlay 的组件，`content(null)` 和 `content(sameNode)` 都要安全。
- 涉及 `snapshot()`、`Canvas`、`Node` 渲染或任何 JavaFX 场景对象的代码，必须明确 FX 线程边界；非 FX 线程要切回 JavaFX Application Thread。

### 12.4 二进制兼容与 API 演化

- 当把旧的具体方法签名改成默认方法、父类实现或泛型链式 API 时，如果外部模块可能还带着旧字节码，必须补桥接方法，避免 `NoSuchMethodError`。
- 新增 public 方法或 public 类时，要同步检查 `module-info.java` exports，避免 demo 或下游模块看不到新 API。
- 重构 Builder 继承层次时，要优先验证旧调用点还能编译、还能运行、还能反射到同名方法。

### 12.5 回归测试最低要求

- 新增或修改 composite 组件时，至少覆盖：`null` 枚举、`null` 文案、负数/非有限数、空 action、重复 build、重复 content、跨容器移动、FX 线程、桥接签名。
- 对于有 controller 的组件，测试必须覆盖 build 后的状态更新，不许只测初始渲染。
- 对于对外展示组件，测试必须断言“异常输入不抛异常”只是底线，最好再断言默认值和 styleClass 真的落到了返回节点上。

## 十三、全项目高频复发点

> 这些问题不只出现在 `layout` 或 `composite`，而是这个仓库里最容易在新组件、新主题、新 demo 里重新长出来的坑。

### 13.1 module / API 同步

- 新增 `public` 类、`public` 方法或新 package 时，必须同步检查 `module-info.java` exports。
- 重构 Builder 继承层次、改父类实现、改返回类型时，要先确认外部模块旧字节码是否还会直接调用旧签名；必要时补桥接方法。
- 任何“源码能编译、旧 demo 跑不起来”的情况，都优先怀疑二进制兼容，而不是先怀疑调用方。

### 13.2 线程与生命周期

- 涉及 JavaFX 场景对象、`snapshot()`、`Canvas`、`Node` 图形计算的逻辑，必须明确 FX 线程边界。
- 绑定监听器、动画、时间线、定时任务时，要有释放或重建策略，不能让重复 build / 重复挂载产生泄漏。
- Controller / 监听器 / 回调如果依赖节点生命周期，要保证节点被替换、隐藏、移除后不会继续驱动旧对象。

### 13.3 主题与 token

- 新组件先补 `JfxStyles` 常量，再补 LESS，再注册到 `components/_index.less`；缺一步都算“只做了一半”。
- 颜色必须走 0-9 色阶和语义映射，不允许新开一把孤立颜色。
- 尺寸、间距、圆角、阴影优先用 token，不要把视觉值写死在 Java 构造参数、setter 或 CSS 字面量里。

### 13.4 构建与验证

- LESS 编译必须实跑验证，不能只看日志“compiled successfully”；遇到样式不落盘，优先检查构建脚本输出是否真的写入目标文件。
- 新改动要至少跑对应包的单测，再跑一轮模块级安装/编译，避免本地 `target` 里残留旧产物。
- demo 的“跑得起来”不等于框架正确，看到 rebuild / replace / 手写样式表 / 手工同步状态时要反向追框架 API 缺口。

### 13.5 审查顺序

- 先看 Builder 入参是否边界归一化，再看 build() 是否幂等，再看节点是否真的落样式和布局，最后才看 demo 层怎么用。
- 任何“看起来只是 UI 细节”的改动，如果会影响 parent、listener、controller、线程或返回签名，都要按运行时缺陷处理。

## 十四、审查速查清单

> 适合扫任何 `*Ant` 组件时直接过一遍。

- [ ] `public` 新类 / 新方法是否同步 `module-info.java`
- [ ] Builder 入参是否 `null` 回默认值
- [ ] 数值入参是否钳制负数、`NaN`、`Infinity`
- [ ] 文案入参是否 `null` 转空字符串或延迟 i18n
- [ ] `Node` / 集合参数是否过滤 `null`
- [ ] 会替换 content 的组件是否释放旧 parent
- [ ] `build()` 是否幂等，重复调用不叠加 spacer / listener / 临时节点
- [ ] 有运行时状态变化时，是否提供 Controller 或等价 API
- [ ] 是否存在旧字节码兼容风险，必要时补桥接方法
- [ ] 是否调用 `applyStyles(...)`，并且 styleClass 是否落到返回节点
- [ ] 视觉值是否走 token / LESS，而不是 Java 写死 px
- [ ] 是否有对应回归测试覆盖边界输入和运行时更新

## 十五、应用壳与模板层

> 来自根包 `org.openkawu.jfxium`、`layout/`、`template/` 的宽扫描结果：这层不是单个控件，而是整页骨架、应用启动、主题装配和业务模板的入口，最容易把底层的坏习惯放大成“全页都坏”。

### 15.1 模板层入参必须先收口

- `Node...`、`List<Node>`、`String...`、`Collection<Node>` 这类入口必须逐个过滤 `null`，不能直接 `addAll(...)`。
- 公开 Javadoc 如果写了“必须设置”的回调，`build()` 就要 fail fast；如果逻辑上允许为空，就必须把文档改成“可选”，并让 UI 表现与之相符。
- 对外模板的 `null` 处理要一致：文本转空字符串，节点过滤，集合视为空，回调为空时不绑定。

### 15.2 模板层的尺寸与间距

- 模板层可以承载少量结构尺寸，但不能把大量视觉像素散落在多个 setter 里。
- `padding`、`bannerWidth`、`iconBox`、`rowHeight`、`spacing` 这类值如果是页面骨架的一部分，要集中管理并优先用 token 或统一常量，不要在多个方法里各写一份。
- 任何暴露给调用方的尺寸入口都必须做非负和非有限数保护。

### 15.3 应用壳与主题装配

- `Scene` 创建、`ThemeManager.applyTheme(...)`、`ThemeManager.registerScene(...)`、`stage.setScene(...)` 要作为一个闭环处理，不要分散到多个调用点。
- 切换场景或重建主窗口时，必须按新的 `Scene` 重新注册主题，不能默认旧 scene 还有效。
- 启动壳层如果持有生命周期资源或监听器，要有清理策略，不能让旧 stage / scene 的引用继续存活。

### 15.4 模板层回归测试最低要求

- 至少补一组 `template/` 的 smoke test，覆盖 `null` 节点、`null` 文案、空集合、重复 build、必需回调缺失、尺寸边界。
- `AppShellAnt` 一类壳层要补场景注册、折叠切换、宽度断点、非 `Region` sider 兼容等回归。
- 任何模板改动如果引入新的固定尺寸，都必须同步检查 compact 主题和桌面密度是否还能一致。
