---
name: demo-discipline
description: >
  jfxium-demo 工程项目展示纪律。在编写、修改、审查 jfxium-demo 代码时使用。
  强制 JFXium 控件优先、util 孵化机制、以及通用性评审整合流程。
---

# jfxium-demo 工程项目展示纪律

> **核心原则**: jfxium-demo 是 JFXium 的「活文档」和「回归测试场」，所有 UI 必须优先使用 JFXium 自带控件和布局。

## 1) 触发条件

在以下场景启用本技能：

- 编写或修改 `jfxium-demo/` 下的任何 Java 文件
- 新增示例页面（ExamplePage）
- 调整 MainView / 导航结构
- 审查 demo 代码质量

## 2) 三条铁律

### 铁律一：JFXium 控件优先（默认强制）

所有 UI 构建**必须使用 JFXium 提供的控件和布局**，不得直接使用原生 JavaFX 节点。

**控件映射表（常用对照）：**

| 原生 JavaFX | JFXium 替代 |
|-------------|-------------|
| `new HBox(...)` | `HBoxAnt.create().children(...).build()` |
| `new VBox(...)` | `VBoxAnt.create().children(...).build()` |
| `new StackPane(...)` | `StackPaneAnt.create().children(...).build()` |
| `new BorderPane(...)` | `BorderPaneAnt.create().center(...).build()` |
| `new GridPane(...)` | `GridAnt.create().add(...).build()` |
| `new FlowPane(...)` | `FlowPaneAnt.create().children(...).build()` |
| `new AnchorPane(...)` | `AnchorPaneAnt.create().children(...).build()` |
| `new ScrollPane(...)` | `ScrollPaneAnt.create().content(...).build()` |
| `new SplitPane(...)` | `SplitPaneAnt.create().items(...).build()` |
| `new Label(...)` | `TypographyAnt.text(...).build()` |
| `new Button(...)` | `ButtonAnt.create().text(...).build()` |
| `new TextField(...)` | `InputAnt.create().build()` |
| `new ComboBox(...)` | `ComboBoxAnt.create().build()` |

**检查方法**：在 `jfxium-demo/` 中搜索 `new HBox(`、`new VBox(`、`new StackPane(`、`new BorderPane(` 等原生构造器，应全部替换为 `*Ant` 版本。

### 铁律二：缺失能力 → util 孵化（不准绕过）

若 JFXium **确实没有**所需的控件或布局能力：

1. **先在 `jfxium-demo/.../jfxiumUiExample/util/` 中实现**，作为临时工具类
2. 代码质量与 JFXium 主体一致：走 `styleClass + LESS`、Builder 模式、`jfx-` 前缀
3. 加清晰 Javadoc 注释，说明「这个工具做了什么、为什么 JFXium 现有控件无法满足」
4. **禁止**：为了绕过限制而直接用 `setStyle()` 拼字符串、或硬编码原生 JavaFX 节点

**当前 util 文件**：

| 文件 | 职责 |
|------|------|
| `util/Demos.java` | 示例页通用工具（section/row/column/labeled/placeholder/colBlock） |

### 铁律三：定期评审 → 整合入 JFXium（闭环）

util 中积累的工具类/控件，需按以下维度评审是否整合入 JFXium 主体：

**评审清单**：

| 维度 | 判断标准 | 整合动作 |
|------|----------|----------|
| **通用性** | 该控件/布局在 3+ 个不同场景有复用价值？ | 移入 `component/control/` 或 `component/composite/` |
| **使用频率** | 多个 ExamplePage 都在调用同一 util 方法？ | 考虑抽象为通用组件 |
| **功能完整性** | 方法签名稳定、参数防御完善、有 Builder API？ | 按新组件 checklist 6 步走 |
| **与现有组件重叠** | 与已有 `*Ant` 功能重叠 ≥ 60%？ | 合并到现有组件，而非新建 |

**评审时机**：

- 每新增 3+ 个 ExamplePage 后扫一遍 `util/` 目录
- util 文件超过 300 行时触发拆分或整合
- 每次大版本功能完成后主动评审

## 3) 编写新 ExamplePage 的检查清单

新建示例页时必须逐项确认：

- [ ] 页面类放在对应分类包下（`pages/datadisplay/`、`pages/dataentry/`、`pages/feedback/`、`pages/navigation/`、`pages/general/`）
- [ ] 所有布局容器使用 `*Ant` 版本（HBoxAnt/VBoxAnt/StackPaneAnt 等）
- [ ] 所有控件使用 `*Ant` 版本（ButtonAnt/InputAnt/TypographyAnt 等）
- [ ] 页面结构使用 `Demos.section()` / `Demos.sectionWithCode()` 包装
- [ ] 演示控件横排用 `Demos.row()`，纵排用 `Demos.column()`
- [ ] 无 `setStyle()` 硬编码颜色/px
- [ ] 无 `new HBox(`、`new VBox(` 等原生构造器（除非已确认 JFXium 无对应 *Ant）
- [ ] 若使用了原生 JavaFX 节点，已在 `util/` 中创建对应包装或写了注释说明原因

## 4) util 孵化代码模板

当需要在 util 中创建新的辅助工具时，遵循以下模板：

```java
/**
 * [功能描述]。
 *
 * <p><b>孵化原因</b>：[为什么 JFXium 现有控件无法满足]</p>
 * <p><b>整合评估</b>：[预计使用场景和通用性判断]</p>
 */
public static Node myHelper(String param, Node... children) {
    // 1. 使用 *Ant 布局组装
    // 2. 样式走 styleClass（jfx- 前缀）
    // 3. 参数防御（null → 默认值）
    // 4. 返回 Node
}
```

## 5) 违规信号（发现以下模式需立即修正）

| 违规模式 | 正确做法 |
|----------|----------|
| `new HBox(12, a, b)` | `HBoxAnt.create().spacing(12).children(a, b).build()` |
| `new VBox(content)` | `VBoxAnt.create().children(content).build()` |
| `new StackPane(overlay, base)` | `StackPaneAnt.create().children(base, overlay).build()` |
| `new BorderPane(center)` | `BorderPaneAnt.create().center(center).build()` |
| `label.setStyle("-fx-text-fill: ...")` | 使用 `TypographyAnt` + `type()` |
| `pane.setStyle("-fx-background-color: ...")` | 使用 `Background` enum 或 styleClass |
| `new Label("标题")` | `TypographyAnt.text("标题").build()` |
| util 方法超过 50 行且无 Javadoc | 补文档 + 评估整合 |

## 6) 与现有规范的关系

本 Skill 是对以下规范的 **demo 层补充**：

- `red-lines.md` — 全局致命红线（setStyle/box-shadow 等），demo 同样适用
- `component-pattern.md` — 组件设计模式，util 孵化时遵循
- `code-standard.md` — 通用编码质量准则
- `project-constraints.md` — LESS/CSS 技术约束

**冲突优先级**：`red-lines.md` > 本 Skill > `component-pattern.md` > `code-standard.md`
