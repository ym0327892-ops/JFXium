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

2. 检索已有 *Ant 组件 → 功能重叠 ≥ 60%？
   ├─ 是 → 提取基类 / 委托包装（禁止独立实现）
   └─ 否 → 继续

3. 拆解 UI 稿 → 圈出原子单位 → 每个对应一个微组件
```

## 六大铁律

1. **优先复用** — 写新组件前先查微组件清单
2. **组件去重** — 新增前检索已有 `*Ant` 类，若大部分功能重叠（≥60%），禁止独立实现。须采用以下策略之一：
   - **提取基类** — 共同能力上提到抽象基类，双方继承共享
   - **委托包装** — 功能少的一方委托功能全的一方（参见 #93：SpinnerAnt 委托 SpinAnt）
3. **结构与视觉分层** — 结构用 Java 组装，视觉走 LESS
4. **单职责** — 一个微组件只做一件事
5. **状态通过 styleClass 切换** — 基础类 + 状态修饰类，禁止拼字符串
6. **数据与微组件解耦** — 用 Property + EventHandler，不直接读写全局 Model
7. **样式命名空间隔离** — 每个微组件有根 styleClass，LESS 用后代选择器

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
| 13 | 创建与已有组件功能大幅重叠的新组件 | 提取基类或委托已有组件 |
| 14 | 便捷方法硬编码关键参数（duration / position 等），调用方必须走 Builder 才能定制 | 提供参数重载：便捷版用默认值，完整版暴露全参数；阻塞+非阻塞两种调用路径共存 |
| 15 | 只提供阻塞式 API 无非阻塞回调 | 同时提供 show()+回调 和 showAndWait() 两种风格，让业务方自由选择 |
| 16 | 尺寸 API 只覆盖 width，遗漏 height/maxHeight/minHeight | 尺寸四元组：width + height + maxHeight + minHeight 全部提供（-1=未设，内容撑开；maxHeight 启用时自动加 ScrollPane） |

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
- [ ] **已检索所有 `*Ant` 类，确认无功能重叠 ≥ 60%？若重叠，已采用基类/委托策略？**
- [ ] 拆解成微组件树：容器 > 子容器 > 叶子
- [ ] 状态机列表完整（hover/selected/disabled）
- [ ] build() 返回类型诚实
- [ ] **便捷方法是否暴露了 Builder 中的关键参数？**（参见反模式 #14：`success(String)` 必须配套 `success(String, int duration)` 重载）
- [ ] **是否有阻塞 + 非阻塞两种调用路径？**（参见反模式 #15：`.show() + onOk/onCancel/onResult` 与 `.showAndWait()` 共存）
- [ ] **尺寸 API 是否四元组齐全？**（参见反模式 #16：width + height + maxHeight + minHeight，-1=未设/内容撑开）

### 实现阶段
- [ ] Builder 继承 AbstractStyleBuilder
- [ ] 视觉样式 100% 走 LESS
- [ ] 状态切换用 styleClass，不用事件回调
- [ ] 用独立 Region 做填充
- [ ] 微组件挂根 styleClass + LESS 后代选择器
- [ ] **全部关键参数都有对应 setter**（禁止只有 Builder 深参无缝式暴露）
- [ ] **maxHeight 启用时必须给 body 自动加 ScrollPane**（内容超出出现滚动条）

### 验收阶段
- [ ] 切换全部 8 套主题，颜色正常跟随
- [ ] 极小/极大宽度下布局不破
- [ ] 禁用状态视觉一致
- [ ] 无 inline `setStyle("-fx-...: -color-...")` 残留
- [ ] **所有便捷方法既有零参默认版，也有关键参数重载版**
- [ ] **阻塞和非阻塞两种调用路径均可正常工作**

## 组件命名约定表（受控属性标准）

> **目的**:统一所有 `*Ant` 组件的 Builder 方法命名,避免同名不同语义。借鉴 EUI-NEO `docs/组件.md` §"API 标准"的"组件公共属性标准"表。新建/重构组件时按本节口径收敛;不一致项登记到 INTERNAL/BUILDER_API_AUDIT.md。

| 类别 | 标准方法名 | 适用范围 | JFXium 现状 |
|------|----------|---------|-------------|
| **身份** | 构造参数 `id` (String) | 所有组件 | 未统一(部分组件 id 可选) |
| **尺寸** | `size(width, height)` / `width(...)` / `height(...)` | 有明确外框的控件/容器/弹层/图表 | 已用 `width/height/prefWidth/...`(语义更细) |
| **内容** | `text(...)` / `title(...)` / `message(...)` / `content(...)` / `placeholder(...)` | 文本/弹层/容器按语义选一种 | 已有,但部分组件混用(如 Modal 同时有 title + content) |
| **受控值** | `value(...)` / `checked(...)` / `selected(...)` / `index(...)` / `open(...)` / `visible(...)` / `expanded(...)` | 业务状态由调用方持有,组件只回调 next value | 已有 `value/checked/selected/open/visible`,**缺 `expanded` 标准名**(Collapse/Tree 等子用) |
| **变更事件** | `onChange(...)` / `onOpenChange(...)` / `onClose(...)` / `onDismiss(...)` / `onOk(...)` / `onCancel(...)` | 值变化统一 `onChange`,开关态 `onOpenChange`,关闭语义按场景区分 | 已有 `onChange/onClose/onOpen`,**缺 `onDismiss`(Toast/Message 应统一)** |
| **可用状态** | `disabled(boolean)` | 可交互组件必须支持禁用态 | 已有 `disable()/enabled()`,**建议同时支持 `disabled(boolean)` 短名** |
| **视觉** | `theme(tokens)` / `style(style)` / `transition(duration, easing)` | theme 按主题重建;style 完整覆盖;动画走 transition | 已有 `styleClass/style`,**缺 `transition` 命名(目前用 `duration` + easing)**,**无 `theme(tokens)` 入口** |
| **层级** | `zIndex(int)` | 仅 overlay 组件(Modal/Drawer/Dropdown/Toast/Tooltip) | **未提供 zIndex setter**,由调用方 `node.setViewOrder()` 手动管理,**容易踩反模式** |
| **坐标** | `position(x, y)` / `anchor(...)` / `screen(width, height)` | 浮层/菜单/tooltip/dialog/picker | **未提供,需手动 `layoutX/layoutY`**,容易出现主题切换后位置漂移 |

### 命名收敛规则(强制)

1. **状态属性名固定**:布尔态用 `checked` / `open` / `visible` / `selected`,不要发明 `isXxx` / `xxxOn`。
2. **变更事件固定**:值变化 → `onChange`;开关/可见性 → `onOpenChange` 或 `onClose`;主动关闭 → `onDismiss`(Toast/Message)。
3. **弹层固定 zIndex**:所有 overlay 组件必须暴露 `zIndex(int)` 链式方法,内部调 `setViewOrder()`。
4. **弹层固定 screen/anchor**:Dialog/Drawer/Picker 必须暴露 `screen(int, int)` 链式方法,内部用绑定到 Scene 宽高属性,避免主题/窗口缩放后位置漂移。
5. **content 唯一语义**:`content(...)` 统一指"塞入子节点"或"面板主内容回调",不要同时表示 "value content(富文本内容)"。

### 反模式(本节禁止)

- 同一概念用不同名:`isOpen` / `opened` / `show` / `visible` 同时存在 → 收敛为 `open`。
- 关闭事件混用 `onClose` / `onDismiss` / `onHidden` → 弹层主动消失用 `onClose`,Toast/Message 自动消失用 `onDismiss`。
- 弹层不暴露 `zIndex` / `screen`,让调用方去 `node.setViewOrder(...)` → 禁止,必须封装在 Builder 内。

## 组件 ID 命名空间

> **目的**:避免同级组件子节点互相污染,保证 `lookup(id)` / CSS 后代选择器 / 调试时能稳定定位。借鉴 EUI-NEO `docs/组件.md` §"写新组件时的底线"第 3 条:组件 id 必须稳定,内部子节点使用 `id + ".name"`。

### 强制规则

1. **每个组件根节点必须有稳定 id** — Builder 必须暴露 `id(String)` 链式方法,未设时在 `build()` 内用 `getClass().getSimpleName() + "@" + System.identityHashCode(this)` 兜底,但**禁止裸用 `setId(class.getSimpleName())` 同名复用**。
2. **内部子节点用 `id + ".role"` 命名** — 角色名用小写单词,描述视觉/语义角色,不用外观描述:
   - ✅ 正确:`"save.dialog"` → 子节点 `"save.dialog.scrim"` / `"save.dialog.panel"` / `"save.dialog.title"` / `"save.dialog.close"`
   - ❌ 错误:`"save.dialog"` → 子节点 `"panel1"` / `"rectBg"` / `"labelTop"`
3. **角色名禁止带数字后缀**:`"row1"` / `"col2"` 是从 0/1 索引算出来的,删除中间项会全部错位。改用 `id + "." + dataKey`(如 `"userTable.row.userId_42"`)或 `id + "." + index`(且 index 必须从数据绑定而非循环下标取)。
4. **id 命名走 lowerCamelCase,带 `.` 路径分隔** — `save.btn` / `nav.menu.item.dark` / `table.cell.userId.col1`。保留 `.` 作为路径分隔,便于将来 `lookup(".title")` 在子树内递归找。
5. **id 全局唯一** — 同一 Scene 内不允许两个不同组件用同一 id。复杂场景(动态表行)用 `idPrefix` + `dataKey` 拼出唯一 id。

### 子节点 ID 与 CSS 后代选择器协同

- 子节点 id 主要用于**调试定位 + 程序化查找**,不是给 LESS 用的。LESS 仍走 `根 styleClass + 后代选择器`(参见六大铁律 #7)。
- 例外:**当需要从外部 JS 风格代码 / CSS query 直接锁定某子节点时**,子节点 id 可作为锚点(如 `lookup("#save.dialog.title")`),但默认不依赖。

### 反模式(本节禁止)

- 多个子节点共用同一 id(如 Modal 内 close 按钮和 backdrop 都叫 `"close"`)→ 违反节点唯一性。
- 子节点 id 用 `auto-generated-uuid` → 失去稳定锚点,主题切换后所有引用失效。
- 子节点 id 含组件类名( `"SaveButton.btn"` )→ 冗余,只需 `"save.btn"`。

## 外部借鉴来源

本文件 §"组件命名约定表" 与 §"组件 ID 命名空间" 借鉴自 [EUI-NEO docs/组件.md](https://github.com/sudoevolve/EUI-NEO/blob/main/docs/%E7%BB%84%E4%BB%B6.md) §"API 标准" 与 §"写新组件时的底线"。JFXium 收敛时遵循:**取其标准名,留其组合套路,不照搬 DSL 风格**(我们走 Java 链式 API,不走 C++ builder 链)。
