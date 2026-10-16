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

| jar | 必需 | 说明 |
|---|---|---|
| `jfxium-1.34.1.jar` | ✅ | 主库；CSS 主题与 i18n 资源都已打进包内 |
| JavaFX `base` / `graphics` / `controls` | ✅ **需你显式声明**（框架不传递） | 版本自选；需带平台分类器，如 `javafx-controls-<版本>-mac-aarch64.jar` |
| JavaFX `fxml` / `media` / `web` / `swing` | ❌ 本框架不涉及 | 按你自己的项目按需添加 |

> 💡 **就是这么设计的**：桌面应用的 JavaFX 版本应该由应用自己定 —— 由
> `javafx-maven-plugin` / `jlink` / 启动脚本挂到 module-path 上。框架只负责「我编译时需要它」，
> 不替你做这个决定。（`javafx-controls` 也**不在** `jfxium.jar` 里：jar 只有 1.2 MB、
> 全是 JFXium 自己的 class 与资源，0 个 JavaFX 类。）

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

三个容易踩的坑，示例里已经避开：

| 坑 | 说明 |
|---|---|
| **别用 `getClass().getResource("/org/openkawu/jfxium/css/theme-light.css")`** | 模块化项目（module-path）下该资源在**另一个模块**里，本模块读不到 → 运行期 NPE。用 `ThemeManager.applyTheme(new LightTheme())`，由框架自己定位 |
| **别再写 `new Button()`** | 原生控件没有 `jfx-` styleClass 与主题接线，长得和你选的主题不一样。统一走 `ButtonAnt.create(...).build()` |
| **`build()` 的返回值必须进场景图** | `VBoxAnt...children(input, btn).build()` 的返回值才是根节点；把 `btn` 直接丢给 `Scene` 而不经容器，控件不会显示 |

> 主题色、暗色切换、运行时改色：见 [主题系统](docs/cn/主题系统.md)。
> 弹窗（`ModalAnt` / `DrawerAnt` 等）是 `Result` 包装，`build()` 后要调 `.open(owner)` 才显示，且 `owner` 是 **Node** 不是 `Stage` —— 见 [组件参考](docs/cn/组件参考.md)。

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
