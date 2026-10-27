# JFXium 使用指南

> 现代化 JavaFX UI 框架，对标 Ant Design 6.x 设计风格

---

## 目录

1. [快速开始](#快速开始)
2. [按场景找组件](#按场景找组件) ⭐ **快速查找组件**
3. [已知限制 / 绕行方案](#已知限制--绕行方案) ⭐ **避免撞墙**
4. [国际化（i18n）](#国际化i18n)
5. [常见问题](#常见问题)

**分文档导航**（更多详情见独立文档）：

| 文档 | 内容 |
|------|------|
| [组件参考](docs/cn/组件参考.md) | 全量组件/模板参考 + bindValue 声明式绑定专题 + 布局/全局浮层管理 |
| [主题系统](docs/cn/主题系统.md) | ThemeManager + 主题入口（Ant Design 系 Light/Dark/Tool + custom 模板；Fluent 2 系 Fluent/Fluent Dark × 密度变体）+ 自定义主题 + styleClass 体系 |
| [快速上手](docs/cn/快速上手.md) | 完整入门教程 + 5 个可运行的业务场景示例 |
| [业务模板](docs/cn/业务模板.md) | PageTemplate / CrudTemplate / LoginTemplate / DashboardTemplate |
| [最佳实践](docs/cn/最佳实践.md) | Builder 规范 / EventBus + record / 页面骨架继承式写法 |

> **新人推荐阅读顺序**：本页「快速开始」→「按场景找组件」→ 「已知限制」→ 然后按需读分文档：[快速上手](docs/cn/快速上手.md)（学会写 CRUD 页）→ [组件参考](docs/cn/组件参考.md)（查具体组件）→ [最佳实践](docs/cn/最佳实践.md)（写出规范代码）

---

## 快速开始

### 1. 引入依赖

> ⚠️ **JFXium 未发布到 Maven Central，也没有任何远程仓库。**
> 直接写 `org.openkawu:jfxium` 坐标必然失败：
> `Could not find artifact org.openkawu:jfxium:jar:1.34.1 in central`。
> **必须自己把 jar 提供给项目**，不能指望 Maven 远程下载。

**方式 A：从 GitHub Releases 下载（最省事）**

[Releases](https://github.com/ym0327892-ops/JFXium/releases) 页面每个版本都挂着现成产物：
`jfxium-<版本>.jar`、`-sources.jar`、`-javadoc.jar`，外加两个 pom（由 CI 在推 `v*` tag 时自动发布）。
下载后装进本地仓库：

```bash
V=1.34.1   # 换成你下载的版本号
mvn install:install-file -Dfile=jfxium-parent-$V.pom -DpomFile=jfxium-parent-$V.pom
mvn install:install-file -Dfile=jfxium-$V.jar -DpomFile=jfxium-$V.pom
# 可选：源码/文档 jar，装了 IDE 里能直接跳进源码
mvn install:install-file -Dfile=jfxium-$V-sources.jar -DpomFile=jfxium-$V.pom -Dclassifier=sources
```

> **两个 pom 一个都不能少**：`jfxium.jar` 内的 pom 带着 `<parent>jfxium-parent</parent>`，
> 只跑 `install-file -Dfile=jfxium-$V.jar`（不给 `-DpomFile`）会报
> `Could not find artifact org.openkawu:jfxium-parent:pom:<版本>`。

**方式 B：本地构建安装（需 JDK 21 + Maven）**

```bash
git clone https://github.com/ym0327892-ops/JFXium.git
cd JFXium
./mvnw install -pl jfxium -am -DskipTests -q   # -am 必须带：parent POM 也要装进本地仓库
```

> **为什么必须 `-am`**：`jfxium` 的 pom 里 `<parent>` 指向 `org.openkawu:jfxium-parent`。
> 只装 `-pl jfxium` 会让 **依赖它的项目解析失败**（报
> `Could not find artifact org.openkawu:jfxium-parent:pom:1.34.1`）——
> 因为本机仓库里没有 parent，远程仓库更没有。`-am`（also make）会一并安装 parent。

装完本机才能引用（**换台机器要重新装**，远程仓库里没有）：

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.34.1</version>
</dependency>
```

**方式 C：直接把 jar 放进项目（完全不依赖仓库）**

产物在 `jfxium/target/jfxium-1.34.1.jar`，拷进项目 `libs/`：

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.34.1</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/jfxium-1.34.1.jar</systemPath>
</dependency>
```

不写 `pom.xml` 也行——IDE 里把它挂到模块的 Libraries / classpath 即可。
（`system` scope 在 Maven 3.9+ 会告警且不参与依赖传递；要分发给别人请用方式 A、B 或 `mvn install:install-file`。）

**还需要哪些 jar**

用方式 A、B 时，下面这些由 Maven 从 Central 自动解析 —— 但 **JavaFX 不会随本框架自动带进来**：
主库把 `javafx-controls` 声明为 `optional`，**不向下游传递**（这正是你要的行为）。所以
**JavaFX 得由你自己的项目显式声明**；用方式 C 还要自己备齐 jar。
本框架除 JavaFX 外零第三方运行时依赖。
你用什么版本、要不要额外加 `javafx-fxml` / `javafx-media` / `javafx-web`，都按你项目的需要来；
本框架不做版本锁死，也不会拿自己的 JavaFX 版本顶掉你的。

> 💡 **就是这么设计的**：桌面应用的 JavaFX 版本应该由应用自己定 —— 由
> `javafx-maven-plugin` / `jlink` / 启动脚本挂到 module-path 上。框架只负责「我编译时需要它」，
> 不替你做这个决定。（`javafx-controls` 也**不在** `jfxium.jar` 里：jar 只有 1.2 MB、
> 全是 JFXium 自己的 class 与资源，0 个 JavaFX 类。）

| jar | 必需 | 说明 |
| `jfxium-1.34.1.jar` | ✅ | 主库；CSS 主题与 i18n 资源都已打进包内 |
| JavaFX `base` / `graphics` / `controls` | ✅ **需你显式声明**（框架不传递） | 版本自选；需带平台分类器，如 `javafx-controls-<版本>-mac-aarch64.jar` |
| JavaFX `fxml` / `media` / `web` / `swing` | ❌ 本框架不涉及 | 按你自己的项目按需添加 |

若 JDK 自带 JavaFX（如 Liberica Full JDK），上表 JavaFX 各项可省。

> **升级 JavaFX 时别忘了平台分类器**：`javafx-controls` 主 jar **不含原生库**，
> 运行期必须带 `<classifier>`（`mac-aarch64` / `win` / `linux` 等）。
> 你若在自己 pom 里覆盖框架默认的 JavaFX 版本，请保持分类器不丢 ——
> 推荐用 `javafx-maven-plugin` 或 `os-maven-plugin` 自动注入，否则运行期才报原生库加载失败。

### 2. 引入主题并使用组件

一个完整可运行的最小程序（不是片段 —— 照着贴就能跑）：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;

public class HelloJfxium extends Application {
    @Override
    public void start(Stage stage) {
        // 1) 加载主题（全局一次；换 DarkTheme 即切暗色）
        ThemeManager.getInstance().applyTheme(new LightTheme());

        // 2) Builder 造组件
        Button btn = ButtonAnt.create("点击我")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> System.out.println("Hello JFXium!"))
                .build();
        TextField input = InputAnt.create()
                .placeholder("请输入内容...")
                .build();

        // 3) 组装布局（container → children → build）
        VBox root = VBoxAnt.create()
                .spacing(16)
                .padding(24)
                .children(input, btn)
                .build();

        // 4) 进场景
        stage.setScene(new Scene(root, 360, 200));
        stage.setTitle("Hello JFXium");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

主题相关的两个 API：

| API | 作用 | 何时调用 |
|---|---|---|
| `applyTheme(theme)` | **全局**应用主题（`Application.setUserAgentStylesheet`），所有 Scene 自动生效 | 启动时调用一次 |
| `registerScene(scene)` | 注册单个 Scene，供 `setPrimaryColor` 运行时改主题色注入变量 | 需要运行时换主题色时 |

换个密度用 `setDensity(ThemeDensity.COMPACT)`；切亮暗用 `setDark(true)`（或 `applyTheme(new DarkTheme())`）。

三个容易踩的坑，示例里已经避开：

| 坑 | 说明 |
|---|---|
| **别用 `scene.getStylesheets().add(getClass().getResource("…/theme-light.css"))` 引入主题** | 那是 **per-scene** 挂载，且模块化项目（module-path）下该资源在**框架模块**里、你的模块读不到 → 运行期 NPE（实测：classpath 侥幸能跑，module-path 必挂）。统一用 `ThemeManager.applyTheme(new LightTheme())`，由框架自己定位 CSS |
| **别再写 `new Button()`** | 原生控件没有 `jfx-` styleClass 与主题接线，长得和你选的主题不一样。统一走 `ButtonAnt.create(...).build()` |
| **`build()` 的返回值必须进场景图** | `VBoxAnt...children(input, btn).build()` 的返回值才是根节点；把 `btn` 直接丢给 `Scene` 而不经容器，控件不会显示 |

> 弹窗（`ModalAnt` / `DrawerAnt` 等）是 `Result` 包装：`build()` 后要调 `.open(owner)` 才显示，
> 且 `owner` 是 **Node** 不是 `Stage` —— 见 [组件参考](docs/cn/组件参考.md)。

> 完整 CRUD 页写法、5 个业务场景示例见 **[快速上手](docs/cn/快速上手.md)**。

---

## 按场景找组件

> 业务进来直接查这张表——比翻整个组件分类章节快。

| 我要做... | 用什么 |
|---|---|
| **登录页** | LoginTemplate（最快）/ FormAnt + VBoxAnt（自由）|
| **admin 列表页** | CrudTemplate + TableAnt + PaginationAnt |
| **数据概览页** | DashboardTemplate + StatisticAnt |
| **简单展示页** | PageTemplate（title + description + body）|
| **应用骨架**（顶栏 + 侧栏 + 内容）| AppShellAnt |
| **表单 + 校验** | FormAnt + Rule.required() + Rule.email() |
| **跨窗口通信** | EventBus + record 事件 |
| **多窗口管理** | WindowManager.getDefault().register(stage) |
| **页面骨架基类**（业务继承）| extends VBoxAnt / BorderPaneAnt |
| **响应式 24 栅格** | GridAnt（不是 GridPaneAnt！）|
| **简单二维网格** | GridPaneAnt（cell/row/column 链式 API）|
| **垂直滚动长内容** | ScrollPaneAnt |
| **拖拽分屏 IDE 风格** | SplitPaneAnt |
| **顶部三段式工具栏**（左/中/右）| BarAnt |
| **水平工具条**（图标按钮 + 分隔线 + 弹性填充）| ToolBarAnt |
| **底部状态栏**（信息 + 进度 + 操作项）| StatusBarAnt |
| **背景色分层**（容器 vs 内容）| Background.TRANSPARENT / LAYOUT / SUBTLE / DEFAULT |
| **输入限制**（数字/手机号/邮箱）| TextFormatters.integerOnly() 等 12 种 |
| **跨平台 OS 判断** | PlatformUtils.isMac() / isWindows() |

> 全部组件/模板详情见 **[组件参考](docs/cn/组件参考.md)**。

---

## AI 辅助开发

`skills/jfxium/` 是标准 Agent Skill 包（Claude Code / Qoder / Cursor / Copilot / Codex / 任意聊天 AI），
目标：AI 生成 UI 时优先且正确地使用 JFXium，而不是 `new Label()` / `setStyle()` / FXML。

- `SKILL.md`：自包含技能入口，可直接复制为 `.qoder/skills/jfxium.md` 或 `.claude/skills/jfxium/SKILL.md`
- `skills/jfxium/dist/jfxium-skill.md`：单文件技能包，直接粘贴给任意 AI 即可
- `assets/catalog/`：全量组件的工厂、`build()` 返回类型、关键 API（自动生成）
- `references/`：11 条红线、原生→Ant 完整对照、可复制片段、system prompt
- 自检：`java skills/jfxium/scripts/JfxLint.java src/main/java`
- 维护：`java skills/jfxium/scripts/CatalogGen.java && java skills/jfxium/scripts/Bundle.java`

接入步骤见 [skills/jfxium/README.md](skills/jfxium/README.md)。

---

## 已知限制 / 绕行方案

> 业务侧实测「想这么干但当前 API 不够顺」的场景。**这些不是 bug**，是 API 演进期的边界，列在这避免你撞墙。

| 想做的事 | 当前限制 | 绕行方案 | 待优化 |
|---|---|---|---|
| **FormAnt 按钮居中** | `footer(Node)` 写死 CENTER_RIGHT | 用 VBoxAnt/HBoxAnt 包一层自己控制 align | 待加 `footerAlign(Pos)` API |
| **FormAnt 多按钮** | `footer` 只接 1 个节点 | 用 HBoxAnt/VBoxAnt 把多个按钮打包成 1 节点 | 待加 `footer(Node...)` 变长重载 |
| **FormAnt 顶部 banner** | 没有 `header` 区 | 在 form 外层用 VBoxAnt 包，banner 放上面 | 待加 `header(Node)` API |
| **FormAnt 分段标题** | 没有 section API | 自己用 `Label + Divider` 拼，插在 items 之间 | 待加 `section(String)` API |
| **InputAnt 密码模式** | 没暴露 password 开关 | 直接用 `new PasswordField()` + 加 styleClass `text-field` | 待加 `.password(true)` |
| **InputAnt focus 时显隐密码** | 没内置 | 用 `PasswordTextFormatter`（参考 AtlantaFX）业务自拼 | 可考虑加 |
| **图标颜色** | `IconAnt.path()` 默认主题色，反色场景需 inline style | `iconNode.setStyle("-fx-background-color: white;")` | 可加 `.color(Color)` |

---

## 国际化（i18n）

JFXium 内置 i18n 国际化机制（M19.18），**默认 Locale = 简体中文（`zh_CN`）**，支持运行时切换语言、UI 文案自动刷新。

```java
import org.openkawu.jfxium.core.i18n.Messages;
import java.util.Locale;

String copy = Messages.get("codeblock.copy");        // "复制"
String items = Messages.get("transfer.items", 5);    // "5 项"（参数化）
Messages.setLocale(Locale.ENGLISH);
Messages.get("codeblock.copy");                      // "Copy"

// 监听变化
Messages.localeProperty().addListener((obs, ov, nv) -> {
    myButton.setText(Messages.get("my.custom.key"));
});
```

**内置覆盖组件**：CodeBlockAnt / TreeSelectAnt / EmptyAnt / ModalAnt / PopconfirmAnt / UploadAnt / TransferAnt / LoginTemplate / DashboardTemplate。

资源文件：`jfxium/src/main/resources/org/openkawu/jfxium/i18n/messages*.properties`。key 命名：`组件名小写.元素名`（如 `codeblock.copy` / `modal.ok`）。

### 注意事项

- 默认 Locale 锁定为 `Locale.SIMPLIFIED_CHINESE`
- `Messages.get` 永不返回 null（缺失 key 时返回 key 本身 + WARNING 日志）
- 0 第三方依赖（仅 JDK `ResourceBundle` / `MessageFormat` / `Locale`）

---

## 常见问题

### Q: 如何修改主题色？

A: 两种方式：

**运行时动态修改（推荐）**：
```java
ThemeManager.getInstance().setPrimaryColor("#ff6b6b");
ThemeManager.getInstance().setPrimaryColor(ThemeColor.Preset.PURPLE);
```

**编译时修改**：编辑 `src/main/resources/org/openkawu/jfxium/css/less/variables.less` 中的 `@color-accent-5` 变量，然后 `./mvnw compile -pl jfxium`。

详见 **[主题系统](docs/cn/主题系统.md)**。

### Q: 支持 JavaFX 哪些版本？

A: 支持 JavaFX 21+。本仓库构建时用 21.0.6 验证；你项目里声明的版本优先（Maven 就近原则），不锁死版本。除 JavaFX 外本框架零第三方运行时依赖。

### Q: 如何添加自定义样式类？

A: 使用 `.styleClass("my-class")` 方法：

```java
Button btn = ButtonAnt.create("自定义").styleClass("my-custom-button").build();
```

详见 **[主题系统 → styleClass 体系](docs/cn/主题系统.md#styleclass-体系)**。

### Q: 想自己写 \*Ant 组件怎么办？

A: 继承 `AbstractStyleBuilder<SELF>`，详见 **[主题系统 → 自定义组件](docs/cn/主题系统.md#自定义组件)**。

### Q: 组件是否支持响应式布局？

A: 所有组件都基于 JavaFX 布局系统。GridAnt 24 栅格已支持响应式断点（xs/sm/md/lg/xl/xxl），详见 [组件参考](docs/cn/组件参考.md)。

### Q: 构建报错找不到 LESS 编译产物怎么办？

A: LESS 编译使用 jlessc（纯 Java），通过 Maven 插件自动执行，无需 Node.js。如遇构建问题请运行 `./mvnw clean install -pl jfxium -am -DskipTests`。

---

*文档版本: 1.3*
*更新日期: 2026-06-06*
*重大更新：文档拆分为总索引 + 5 个专题分文档（组件/主题/快速上手/业务模板/最佳实践）*
