# JFXium

> 现代化 JavaFX UI 框架，对标 Ant Design 6.x 设计风格

[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.6-blue.svg)](https://openjfx.io/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

[完整使用指南](README_CN.md)

---

## 特性

- **组件齐全** — 覆盖控件、组合组件、浮层、布局与业务模板
- **Ant Design 6.x 风格** — 像素级还原 Ant Design 设计语言
- **内置主题入口** — Light/Dark/Tool + 自定义主题模板，均带紧凑密度变体
- **Builder 模式** — 全组件链式 API：`XxxAnt.create()...build()`
- **LESS 主题体系** — 只需改一个 LESS 文件即可生成自己的主题
- **国际化** — 内置中英文，`Messages.get(key)` API
- **零 FXML** — 纯代码构建 UI

---

## 快速开始

### 1. 引入依赖

> ⚠️ **JFXium 未发布到 Maven Central，也没有任何远程仓库。**
> 直接写 `org.openkawu:jfxium` 坐标必然失败：
> `Could not find artifact org.openkawu:jfxium:jar:1.34.0 in central`。
> **必须自己把 jar 提供给项目**，不能指望 Maven 远程下载。

**方式 A：本地构建安装（推荐）**

```bash
git clone https://github.com/ym0327892-ops/JFXium.git
cd JFXium
./mvnw install -pl jfxium -am -DskipTests -q   # -am 必须带：parent POM 也要装进本地仓库
```

> **为什么必须 `-am`**：`jfxium` 的 pom 里 `<parent>` 指向 `org.openkawu:jfxium-parent`。
> 只装 `-pl jfxium` 会让 **依赖它的项目解析失败**（报
> `Could not find artifact org.openkawu:jfxium-parent:pom:1.34.0`）——
> 因为本机仓库里没有 parent，远程仓库更没有。`-am`（also make）会一并安装 parent。

装完本机才能引用（**换台机器要重新装**，远程仓库里没有）：

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.34.0</version>
</dependency>
```

**方式 B：直接把 jar 放进项目（完全不依赖仓库）**

产物在 `jfxium/target/jfxium-1.34.0.jar`，拷进项目 `libs/`：

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.34.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/jfxium-1.34.0.jar</systemPath>
</dependency>
```

不写 `pom.xml` 也行——IDE 里把它挂到模块的 Libraries / classpath 即可。
（`system` scope 在 Maven 3.9+ 会告警且不参与依赖传递；要分发给别人请用方式 A 或 `mvn install:install-file`。）

**还需要哪些 jar**

用方式 A 时，下面这些由 Maven 从 Central 自动解析；用方式 B 要自己备齐。
**版本由你自己的项目决定**——本框架只依赖 `javafx-controls` 一个 JavaFX 模块
（**除 JavaFX 外零第三方运行时依赖**），
你用什么版本、要不要额外加 `javafx-fxml` / `javafx-media` / `javafx-web`，都按你项目的需要来。
你用什么版本的 JavaFX 都可以，想升级就升级，本框架不做版本锁死。
你自己 pom 里声明的 JavaFX 版本优先（Maven 就近原则）；没声明时才会用到本框架默认的 21.0.6。

| jar | 必需 | 说明 |
|---|---|---|
| `jfxium-1.34.0.jar` | ✅ | 主库；CSS 主题与 i18n 资源都已打进包内 |
| JavaFX `base` / `graphics` / `controls` | ✅ | 版本自选；需带平台分类器，如 `javafx-controls-<版本>-mac-aarch64.jar` |
| JavaFX `fxml` / `media` / `web` / `swing` | ❌ 本框架不涉及 | 按你自己的项目按需添加 |

若 JDK 自带 JavaFX（如 Liberica Full JDK），上表 JavaFX 各项可省。

> **升级 JavaFX 时别忘了平台分类器**：`javafx-controls` 主 jar **不含原生库**，
> 运行期必须带 `<classifier>`（`mac-aarch64` / `win` / `linux` 等）。
> 你若在自己 pom 里覆盖框架默认的 JavaFX 版本，请保持分类器不丢 ——
> 推荐用 `javafx-maven-plugin` 或 `os-maven-plugin` 自动注入，否则运行期才报原生库加载失败。

### 2. 引入主题并使用组件

```java
@Override
public void start(Stage stage) {
    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add(
        getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm()
    );

    // 按钮
    Button btn = ButtonAnt.create("点击我")
        .type(ButtonAnt.Type.PRIMARY)
        .onClick(e -> System.out.println("Hello JFXium!"))
        .build();

    // 输入框
    TextField input = InputAnt.create()
        .placeholder("请输入内容...")
        .build();

    // 模态框
    ModalAnt.create().title("确认").content("确定要继续吗？")
        .build().open(stage);

    stage.setScene(scene);
    stage.show();
}
```

### 3. 运行 Demo

```bash
# 在仓库根目录执行
./mvnw javafx:run -pl jfxium-demo
```

---

## 文档

| 文档 | 内容 |
|----------|-------------|
| [快速上手](docs/cn/快速上手.md) | 5 分钟跑通第一个 admin 应用 |
| [组件参考](docs/cn/组件参考.md) | 全量组件/模板参考 + bindValue |
| [主题系统](docs/cn/主题系统.md) | 主题切换、自定义主题、styleClass |
| [业务模板](docs/cn/业务模板.md) | CrudTemplate / LoginTemplate / DashboardTemplate |
| [最佳实践](docs/cn/最佳实践.md) | Builder 模式、EventBus、页面骨架 |

---

## 环境要求

- Java 21
- JavaFX 21+（本仓库构建用 21.0.6 验证；你项目声明的版本优先，不做版本锁死）
- Maven 3.8+（或直接用仓库自带的 `./mvnw`，无需预装 Maven）

---

## 许可证

MIT License
