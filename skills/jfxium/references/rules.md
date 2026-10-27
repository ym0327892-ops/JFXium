# JFXium 硬规则（AI 必读）

> 权威来源：`.qoder/rules/red-lines.md`。本文件是给下游 AI 的精简版，**改一处必须同步三处**
> （red-lines / 本文件 / `AGENT_PROMPT.md`）。

## 一、11 条致命红线

1. **禁止 `setStyle()` 写颜色/px** → `styleClass` + LESS。
2. **禁止 CSS `box-shadow`** → `-fx-effect: dropshadow(...)`。
3. **禁止 CSS `:active` / `:focus`** → `:pressed`（`:armed`）/ `:focused`。
4. **禁止 `-fx-transition` 用于交互状态**（`.fade-in` 等动画类除外）→ 直接定义状态色。
5. **禁止容器吞 padding** → 组合控件根容器 `-fx-padding: 0`，下放到子节点。
6. **禁止 `@border-radius-full`(9999px) 用于尺寸未钳制的节点** → 进度条/轨道用 `@border-radius-md`。
7. **禁止 `build()` 返回类型撒谎** → 返回什么就是什么。
8. **禁止新建 styleClass 不带 `jfx-` 前缀** → 避免与 modena 冲突。
9. **禁止 `.arrow` 只设颜色不设 shape** → 显式 `-fx-shape` + min/pref 尺寸。
10. **禁止新增 public 类不同步 `module-info.java` exports**。
11. **禁止业务代码 `new` 原生 JavaFX 控件** → `XxxAnt.create(...)`。框架内部 `extends XxxAnt`
    的实现类与临时 helper 除外。

## 二、API 契约（AI 最常错的地方）

| 契约 | 说明 | 例子 |
|---|---|---|
| **`build()` 两类返回** | 常驻场景图 → 直接 Node；按需弹层 → Result（必须 `open(owner)` / `show()`） | `ModalAnt...build().open(owner)`；`MessageAnt.success("已保存")` |
| **Builder vs 继承式** | 控制类多为 `extends Native`（`InputAnt`、`ButtonAnt`、`SpinnerAnt`），`build()` 返回自身；组合类 `create()` 返回 Builder，`build()` 返回 Node | `TableAnt.<T>create()...build()` 返回 `TableAnt<T>` |
| **Controller** | 构建后要改运行时状态，用 `XxxAnt.controllerOf(node)`；不要整棵树重建 | `TabsAnt.controllerOf(tabs).select("tab2")` |
| **尺寸** | `size(Size)` 走 `.small/.large` 修饰类；间距/内边距走 token，不写 `new HBox(8)` | `HBoxAnt.create().spacing(8)` |
| **布局** | 页面/表单用 `VBoxAnt`/`HBoxAnt`/`GridAnt`/`PageTemplate`，别退回原生 `VBox` | `VBoxAnt.create().spacing(12).padding(16)` |
| **主题** | `ThemeManager.getInstance()`：`applyTheme` / `registerScene` / `setDensity` / `setPrimaryColor` | Fluent 主题禁运行时注入主色（`hasFixedAccent`） |
| **i18n** | 字段初始化为 `null`，`build()` 内懒解析；持久组件监听 `Messages.localeProperty()` | 不写 `private String x = Messages.get("k")` |
| **防御式输入** | 枚举 null → 默认；文本 null → `""`；数值 NaN/Infinity/负数 → clamp；`Node...` 过滤 null | `Double.isFinite(v) ? Math.max(0, v) : 0` |
| **FX 线程** | 触碰 `snapshot()`/Canvas/场景图渲染前检查 `Platform.isFxApplicationThread()` | 异步回调里 `Platform.runLater` |
| **幂等** | `build()` 可重复调用，不累积子节点/监听器 | 替换内容前先解绑旧 parent |
| **模块** | 新 public 类必须同步 `module-info.java` exports；新增依赖同步 `jfxium/pom.xml` | `exports org.openkawu.jfxium.component.control;` |

## 三、数字输入选型（容易选错）

| 场景 | 用法 |
|---|---|
| 常规表单 / 金额 / 百分比 / 需要 prefix·suffix | `InputNumberAnt`（推荐；FormAnt 校验 / reset 已接线） |
| 需要 JavaFX `Spinner` 语义（`valueProperty`、`increment()`、第三方 API 要求 `Spinner`） | `SpinnerAnt`（默认可键入，`editable(false)` 只读；进 FormAnt 需自行 `bindValue`） |
| 加载动画 | `SpinAnt`（SPINNER / DOTS / BARS），不是 `ProgressIndicator` |

## 四、常见“看起来正确”的错误

- `ModalAnt.create()...build()` 直接 `add(node)` → 不会显示，必须 `.open(owner)`。
- `TableAnt` 每列重建 `TableColumn` 而不走 `.column(...)` → 丢失主题与排序封装。
- `new Spinner<>(...)` 后只设 `setEditable(true)` → 绕过 JFXium 的精度取整、非法输入回滚与主题。
- 在 Java 里 `new HBox(8)` / `setPadding(...)` 控制视觉间距 → 紧凑主题失效；走 `spacing()` + token。
- 给组件加 `"card"`、`"menu"` 这类裸 styleClass → 与 modena 冲突，必须 `jfx-` 前缀。
- 用 `box-shadow` / `-fx-transition` / `:focus` → JavaFX 不支持或伪类名不同，检查器会直接报错。

## 五、验收

```bash
java skills/jfxium/scripts/JfxLint.java <改动目录>    # ERROR 必须清零
./mvnw install -pl jfxium -am -DskipTests
./mvnw compile -pl jfxium-demo -am -DskipTests
```
