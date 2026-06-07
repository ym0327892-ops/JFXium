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
- 边框/外发光用 **背景层叠**：`-fx-background-color: borderColor, fillColor; -fx-background-insets: -2, 0;`
- 所有高度/padding 必须用 token（`@control-height`），不硬编码 px
- `:armed` 和 `:pressed` 状态必须同时定义
- 复合选择器（`.A.B`）vs 后代选择器（`.A .B`）要严格按 styleClass 挂载方式选择

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

### 4.6 动画类例外

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

## 九、复杂控件查 AtlantaFX 源码

调 TableView / TreeView / ComboBox 弹层等复杂控件样式时，**先读 AtlantaFX 源码**，再改 LESS。不凭印象猜选择器层级。

**常用文件**（路径：`/Users/openai/workspace/work_open/atlantafx/styles/src/components/`）：
- `_data.scss` — TableView / TreeView / ListView
- `_combo-box.scss` — ComboBox / DatePicker / ChoiceBox
- `_text-input.scss` — TextField / TextArea
- `_button.scss` / `_menu.scss` / `_scroll-bar.scss`

### 复合选择器优先级陷阱

给 `.button` 节点挂额外 styleClass 覆盖默认样式时，**必须用 `.button.xxx` 复合选择器**（特异性=2），不要写裸 `.xxx`：
```less
// ❌ 错：hover/armed 时被 .button:hover 覆盖
.carousel-arrow-btn { -fx-background-color: rgba(0,0,0,0.4); }

// ✅ 对：复合选择器 + 补全所有伪类
.button.carousel-arrow-btn { -fx-background-color: rgba(0,0,0,0.4); }
.button.carousel-arrow-btn:hover { -fx-background-color: rgba(0,0,0,0.6); }
```
