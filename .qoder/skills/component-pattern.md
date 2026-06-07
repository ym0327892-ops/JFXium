---
name: component-pattern
description: >
  JFXium 组件设计模式与组合规范。在创建新组件、重构现有组件、或审查组件代码时使用。
  涵盖微组件清单、标准模板、反模式红线、包结构归约和检查清单。
---

# JFXium 组件组合规范

> **核心目标**: 复杂组件 = 微组件组合。禁止重复造轮子，禁止直接操作 Node 属性。

## 创建新组件决策树

```
1. 查阅"微组件清单" → 能用原生 JavaFX Node 组合？
   ├─ 是 → 使用组合模式
   └─ 否 → 继续

2. 检查项目内已有 *Ant 组件 → 能复用或扩展？
   ├─ 是 → 复用
   └─ 否 → 拆解 UI 稿 → 圈出原子单位 → 每个对应一个微组件
```

## 六大铁律

1. **优先复用** — 写新组件前先查微组件清单
2. **结构与视觉分层** — 结构用 Java 组装，视觉走 LESS
3. **单职责** — 一个微组件只做一件事
4. **状态通过 styleClass 切换** — 基础类 + 状态修饰类，禁止拼字符串
5. **数据与微组件解耦** — 用 Property + EventHandler，不直接读写全局 Model
6. **样式命名空间隔离** — 每个微组件有根 styleClass，LESS 用后代选择器

## 微组件清单（速查）

### 容器类

| Node | 用途 |
|------|------|
| `HBox` | 横向布局 |
| `VBox` | 纵向布局 |
| `StackPane` | 叠层（z-axis） |
| `BorderPane` | 五区位（top/right/bottom/left/center） |
| `GridPane` | 二维网格 |
| `FlowPane` | 流式换行 |
| `Region` | **弹性 spacer**（配 Hgrow=ALWAYS + maxWidth=MAX） |
| `ScrollPane` | 滚动容器 |

### 控件类

| Control | JFXium 封装 |
|---------|-------------|
| `Button` | ButtonAnt |
| `TextField` | InputAnt |
| `ComboBox` | ComboBoxAnt |
| `TableView` | TableAnt |
| `TreeView` | TreeAnt |
| `MenuButton` | DropdownAnt 底层 |

## 标准模板

### Header 三段式

```java
HBox header = new HBox(8);
header.setAlignment(Pos.CENTER_LEFT);
// 1. 左 slot（可选）
// 2. 标题 Label
// 3. 弹性 Region spacer（Hgrow=ALWAYS + maxWidth=MAX）
// 4. extra 节点（可选）
// 5. 右 slot（可选）
```

### 状态机

```java
node.getStyleClass().add(JfxStyles.PROGRESS_BAR);
node.getStyleClass().add(switch (status) {
    case SUCCESS -> JfxStyles.PROGRESS_SUCCESS;
    case ERROR   -> JfxStyles.PROGRESS_ERROR;
    default      -> null;
});
```

## 包结构归约

### 顶层三层

| 包 | 后缀 | 定位 |
|---|---|---|
| `component/` | `*Ant` | 原子控件 + 组合容器 + 浮层 |
| `template/` | `*Template` | 业务模板（整页骨架） |
| `layout/` | `*Ant` | 应用骨架（跨页面） |

### component 内四子包

| 子包 | 判定 | 示例 |
|------|------|------|
| `control/` | build() 返回原生控件 | ButtonAnt / InputAnt |
| `composite/` | build() 返回容器 | CardAnt / BarAnt / FormAnt |
| `overlay/` | build() 返回 Result | ModalAnt / DrawerAnt |
| `layout/` | 容器封装 | VBoxAnt / HBoxAnt |

## 反模式红线（绝对禁止）

| # | 反模式 | 正确做法 |
|---|--------|---------|
| 1 | Label 用 Hgrow=ALWAYS 当填充 | 用独立 Region + Hgrow=ALWAYS + maxWidth=MAX |
| 2 | 创建 Region 但忘了 add 到容器 | setHgrow 只对 children 里的节点有效 |
| 3 | Java 里拼字符串实现 hover | 让 LESS `:hover` 伪类接管 |
| 4 | build() 类型撒谎 | 诚实声明返回类型 |
| 5 | `setStyle(getStyle() + ...)` 累加 | 挂 styleClass，让 LESS 处理 |
| 6 | 组件中硬编码颜色 | 用 LESS 变量或 styleClass |
| 7 | 微组件侵入业务模型 | 通过 Property + 事件回调暴露 |
| 8 | 全局泛污染 LESS 选择器 | 挂在微组件根 styleClass 后代 |
| 9 | 容器吞 padding 导致布局错乱 | 容器 padding=0，下放到子节点 |
| 10 | `.arrow` 只设颜色不设 shape | 显式 `-fx-shape` + min/pref 尺寸 |
| 11 | 未钳制尺寸节点用 9999px 圆角 | 用 `@border-radius-md` |
| 12 | 字符串模板里嵌套未转义双引号 | 用「」或转义 `\"` |

## 布局约束陷阱

| 陷阱 | 说明 |
|------|------|
| Label 默认 maxWidth=USE_PREF_SIZE | 给 Label 设 Hgrow 也不会拉伸，必须用 Region |
| Region 默认 maxWidth=USE_PREF_SIZE | 需要 `setMaxWidth(MAX)` + Hgrow=ALWAYS 双保险 |
| HBox/VBox 默认 max=MAX | 塞进 StackPane 会被拉伸到撑满，用 `setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE)` 收缩 |
| StackPane z-order | 用 `setViewOrder()` 控制，数值越小越靠前 |

## 新组件检查清单

### 设计阶段
- [ ] 能用现有微组件组合出来？
- [ ] 拆解成微组件树：容器 > 子容器 > 叶子
- [ ] 状态机列表完整（hover/selected/disabled）
- [ ] build() 返回类型诚实

### 实现阶段
- [ ] Builder 继承 AbstractStyleBuilder
- [ ] 视觉样式 100% 走 LESS
- [ ] 状态切换用 styleClass，不用事件回调
- [ ] 用独立 Region 做填充
- [ ] 微组件挂根 styleClass + LESS 后代选择器

### 验收阶段
- [ ] 切换全部 8 套主题，颜色正常跟随
- [ ] 极小/极大宽度下布局不破
- [ ] 禁用状态视觉一致
- [ ] 无 inline `setStyle("-fx-...: -color-...")` 残留
