# 最容易出错 / 最容易懵的地方

按“新人实际踩坑频率”排序。每条都是：**现象 → 原因 → 正确写法**。

## 1. `build()` 返回的不是你以为的东西

```java
// ❌ ModalAnt 返回 ModalResult，加到场景图里什么都不会显示
root.getChildren().add(ModalAnt.create().title("确认").content("...").build());

// ✅ 弹层必须 open(owner)，owner 是任意 Node（不是 Stage）
ModalAnt.create().title("确认").content("...").build().open(triggerButton);
```

| 类型 | 例子 | `build()` 之后 |
|---|---|---|
| 继承式控件 | `ButtonAnt` / `InputAnt` / `TableAnt` / `SpinnerAnt` | 返回自身，直接挂载 |
| Builder 组合 | `FormAnt.build()` / `PageTemplate.build()` | 返回 Node（`VBox` 等） |
| 需要校验的表单 | `FormAnt.buildResult()` | 返回 `Result`，用 `getRoot()` 取 Node |
| 弹层 | `ModalAnt` / `DrawerAnt` / `PromptDialogAnt` | `Result`，必须 `.open(owner)` |
| 浮层 | `DropdownAnt` / `PopoverAnt` / `PopconfirmAnt` | `Result`，必须 `.show()` |
| 轻提示 | `MessageAnt` / `NotificationAnt` | 静态 `MessageAnt.success(...)` 或 `.build().show()` |

> 拿不准就看 `assets/catalog/components.md` 的 `build()` 列。

## 2. `Background` 有两个，导错包就炸

```java
// ❌ 这是 JavaFX 的画布背景对象，不是 JFXium 的背景层级
import javafx.scene.layout.Background;

// ✅ JFXium 背景层级（映射到 -color-bg-* token）
import org.openkawu.jfxium.core.style.Background;

VBoxAnt.create().background(Background.DEFAULT);   // 内容白底
VBoxAnt.create().background(Background.LAYOUT);    // 页面灰底
Backgrounds.apply(node, Background.SUBTLE);        // 任意 Node
```

## 3. 自定义背景色 → 暗色主题白块

```java
// ❌ 亮色正常，Dark/FluentDark 下固定白块
box.setBackground(new Background(new BackgroundFill(Color.web("#ffffff"), null, null)));
box.setStyle("-fx-background-color: #ffffff;");
```

```less
/* ❌ 所有主题都固定浅灰 */
.jfx-card { -fx-background-color: #f5f5f5; }

/* ✅ 用语义 token */
.jfx-card { -fx-background-color: -color-bg-default; }
```

规则与 token 表见 `theme-colors.md`；`JfxLint` 会报 `HARDCODED_BACKGROUND`。

## 4. `SpinnerAnt` / `InputNumberAnt` / `SpinAnt` 三个名字

| 类 | 是什么 | 何时用 |
|---|---|---|
| `InputNumberAnt` | 自绘数字输入（输入框 + 上下按钮，支持 prefix/suffix） | **表单数字输入首选**；FormAnt 校验/reset 已接线 |
| `SpinnerAnt` | 原生 `Spinner<Double>` 包装（右侧三角步进） | 需要 `valueProperty()` / `increment()` / 第三方 API 要求 `Spinner` |
| `SpinAnt` | 加载动画（SPINNER / DOTS / BARS） | loading 状态，不是数字输入 |

`SpinnerAnt` 现在**默认可键盘输入**；旧版本默认不可编辑，`editable(false)` 才是只读。

## 5. 主题不是加 stylesheet，而是 `ThemeManager`

```java
// ❌ 模块化项目里 getResource 读不到 jfxium 内部 CSS → NPE
scene.getStylesheets().add(getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm());

// ✅ 唯一入口
ThemeManager tm = ThemeManager.getInstance();
tm.applyTheme(new LightTheme());
tm.registerScene(scene);                 // 之后切主题自动跟随
tm.setDensity(ThemeDensity.COMPACT);     // 紧凑
tm.setPrimaryColor("#ff5722");           // Fluent 主题会跳过（固定品牌色）
```

## 6. `size(Size)` 和 `size(double)` 是两个方法

```java
spinner.size(Size.LARGE);   // 控件尺寸档位（.small/.large styleClass）
spinner.size(48);           // LayoutCommon：把 min/pref/max 全设成 48（固定尺寸）
```

`LayoutCommon` 的间距同理：用 `.spacing(8)` / `.padding(16)`，不要 `new HBox(8)` / `setPadding(...)`，
否则 compact 密度不生效。

## 7. 表单校验链路

```java
FormAnt.Result form = FormAnt.create()
        .item("用户名", InputAnt.create().build(), "username")
            .required()
            .rule(Rule.minLength(3, "至少 3 个字符"))
            .end()                          // 回到 Form Builder，漏了会编译不过
        .footer(submitButton)
        .buildResult();

VBox node = form.getRoot();
if (form.validate()) { ... }               // 校验在 Result 上，不在 Builder 上
```

- `build()` → 只有 UI；`buildResult()` → 可 `validate()` / `getValues()` / `reset()`。
- 依赖联动：`form.onChange("password", (v, ctx) -> ctx.validateField("confirm"))`。

## 8. Controller 不是重建组件树

```java
TabsAnt.Controller ctrl = TabsAnt.controllerOf(tabsNode);
ctrl.select("tab-2");        // ✅ 运行时切状态
```

需要改状态时先找 `controllerOf`；没有 Controller 的组件才考虑重建。

## 9. i18n 字段不要锁死 Locale

```java
// ❌ 类加载时锁死当前语言
private String placeholder = Messages.get("user.name");

// ✅ 懒解析 + 监听语言切换
private String placeholder = null;
String text = placeholder != null ? placeholder : Messages.get("user.name");
```

## 10. 伪类 / CSS 属性名与 Web 不同

| Web | JavaFX |
|---|---|
| `:active` | `:pressed` / `:armed` |
| `:focus` | `:focused` / `:focus-within` |
| `box-shadow` | `-fx-effect: dropshadow(...)` |
| `transition` | 不支持：直接定义状态颜色 |

## 11. 构建 / 依赖

```bash
# ❌ 只装模块，父 POM 没装，下游解析 jfxium-parent 失败
./mvnw install -pl jfxium -DskipTests

# ✅ 带上 -am
./mvnw clean install -pl jfxium -am -DskipTests
```

- `javafx-controls` 是 `compile + optional`：你的应用自己声明 JavaFX 版本。
- 模块化项目：`requires org.openkawu.jfxium;`
- 不要 FXML；不要 `javafx-web` / `javafx-media`（已移除依赖）。

## 12. 线程

所有场景图操作在 FX 线程；异步回调里先 `Platform.runLater(...)`。
`snapshot()` / Canvas 绘制同样要检查 `Platform.isFxApplicationThread()`。

## 13. 升级版本时三件事

1. `pom.xml` 的 jfxium 版本与 `assets/catalog` 的 `version` 对齐（AI 依赖它判断 API）。
2. 重新读 `references/rules.md` / `pitfalls.md`（可能有破坏性变更）。
3. 跑 `JfxLint` + 切 Light/Dark/Fluent/FluentDark/compact 各看一遍。

## 14. 多窗口：MessageAnt 显示到主窗口

```java
// ❌ 默认解析是“焦点 Stage → 第一个 showing Stage”，副窗口里调用可能显示到主窗口
MessageAnt.success("已保存");

// ✅ 显式 owner（Window / Stage）
MessageAnt.success("已保存", secondaryStage);
MessageAnt.create().owner(secondaryStage).content("已保存").build().show();
```

`ModalAnt` / `DrawerAnt` 本来就要求 `open(owner)`；`NotificationAnt` 目前仍取首个 Stage，多窗口下需注意。

## 15. 生成代码后的 30 秒自检

```bash
java skills/jfxium/scripts/JfxLint.java src/main/java
```

必查：`NATIVE_NEW`、`INLINE_STYLE`、`HARDCODED_BACKGROUND`、`MODULE_EXPORT` 为 0；
再确认弹层用了 `.open(owner)`、表单用了 `buildResult()`、自定义背景走了 token。
