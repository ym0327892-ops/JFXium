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
| [主题系统](docs/cn/主题系统.md) | ThemeManager + 主题入口（Light/Dark/Tool + custom 模板 × 密度变体）+ 自定义主题 + styleClass 体系 |
| [快速上手](docs/cn/快速上手.md) | 完整入门教程 + 5 个可运行的业务场景示例 |
| [业务模板](docs/cn/业务模板.md) | PageTemplate / CrudTemplate / LoginTemplate / DashboardTemplate |
| [最佳实践](docs/cn/最佳实践.md) | Builder 规范 / EventBus + record / 页面骨架继承式写法 |

> **新人推荐阅读顺序**：本页「快速开始」→「按场景找组件」→ 「已知限制」→ 然后按需读分文档：[快速上手](docs/cn/快速上手.md)（学会写 CRUD 页）→ [组件参考](docs/cn/组件参考.md)（查具体组件）→ [最佳实践](docs/cn/最佳实践.md)（写出规范代码）

---

## 快速开始

### 1. 引入依赖

> ⚠️ **JFXium 未发布到 Maven Central，也没有任何远程仓库。**
> 直接写 `org.openkawu:jfxium` 坐标必然失败：
> `Could not find artifact org.openkawu:jfxium:jar:1.33.1 in central`。
> **必须自己把 jar 提供给项目**，不能指望 Maven 远程下载。

**方式 A：本地构建安装（推荐）**

```bash
git clone https://github.com/ym0327892-ops/JFXium.git
cd JFXium
./mvnw install -pl jfxium -DskipTests -q     # 装进本地 ~/.m2/repository
```

装完本机才能引用（**换台机器要重新装**，远程仓库里没有）：

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.33.1</version>
</dependency>
```

**方式 B：直接把 jar 放进项目（完全不依赖仓库）**

产物在 `jfxium/target/jfxium-1.33.1.jar`，拷进项目 `libs/`：

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.33.1</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/jfxium-1.33.1.jar</systemPath>
</dependency>
```

不写 `pom.xml` 也行——IDE 里把它挂到模块的 Libraries / classpath 即可。
（`system` scope 在 Maven 3.9+ 会告警且不参与依赖传递；要分发给别人请用方式 A 或 `mvn install:install-file`。）

**还需要哪些 jar**

用方式 A 时，下面这些由 Maven 从 Central 自动解析；用方式 B 要自己备齐。
**版本由你自己的项目决定**——本框架只依赖 `javafx-controls` 这一个 JavaFX 模块，
你用什么版本、要不要额外加 `javafx-fxml` / `javafx-media` / `javafx-web`，都按你项目的需要来。
你自己 pom 里声明的 JavaFX 版本优先（Maven 就近原则）；没声明时才会用到本框架验证过的 21.0.6。

| jar | 必需 | 说明 |
|---|---|---|
| `jfxium-1.33.1.jar` | ✅ | 主库；CSS 主题与 i18n 资源都已打进包内 |
| JavaFX `base` / `graphics` / `controls` | ✅ | 版本自选（本框架验证过 21.0.6）；需带平台分类器，如 `javafx-controls-<版本>-mac-aarch64.jar` |
| JavaFX `fxml` / `media` / `web` / `swing` | ❌ 本框架不涉及 | 按你自己的项目按需添加 |

若 JDK 自带 JavaFX（如 Liberica Full JDK），上表 JavaFX 各项可省。

### 2. 引入主题

```java
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.theme.LightTheme;

@Override
public void start(Stage stage) {
    Scene scene = new Scene(root, 800, 600);
    ThemeManager.getInstance().applyTheme(new LightTheme());
    // 要运行时切主题色才需要：
    // ThemeManager.getInstance().registerScene(scene);
    stage.setScene(scene);
    stage.show();
}
```

| API | 作用 | 何时调用 |
|---|---|---|
| `applyTheme(theme)` | 全局应用主题（切换亮/暗/紧凑） | 启动时调用一次 |
| `registerScene(scene)` | 注册 Scene，支持 `setPrimaryColor` 动态切色 | 需要运行时换主题色时 |

### 3. 三行示例

```java
Button btn = ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).onClick(e -> save()).build();
TextField input = InputAnt.create().placeholder("姓名").build();
VBox layout = VBoxAnt.create().spacing(16).padding(24).children(input, btn).build();
```

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

A: 支持 JavaFX 21+（项目使用 JavaFX 21.0.6）。

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

A: LESS 编译使用 jlessc（纯 Java），通过 Maven 插件自动执行，无需 Node.js。如遇构建问题请运行 `./mvnw clean install -pl jfxium -DskipTests`。

---

*文档版本: 1.3*
*更新日期: 2026-06-06*
*重大更新：文档拆分为总索引 + 5 个专题分文档（组件/主题/快速上手/业务模板/最佳实践）*
