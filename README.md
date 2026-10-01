# JFXium

> A modern JavaFX UI framework inspired by Ant Design 6.x

[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.6-blue.svg)](https://openjfx.io/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

[中文文档](README_CN.md)

---

## Features

- **Comprehensive UI Library** — Controls, composites, overlays, layouts, and business templates
- **Ant Design 6.x Style** — Pixel-perfect implementation of Ant Design's design language
- **Built-in Theme Entries** — Light/Dark/Tool + custom theme template, with compact density variants
- **Builder Pattern** — Fluent API for all components: `XxxAnt.create()...build()`
- **LESS-based Theming** — Modify one LESS file to generate your own theme
- **i18n** — Built-in Chinese/English with `Messages.get(key)` API
- **Zero FXML** — Pure code-based UI construction

---

## Quick Start

### 1. Add Dependency

> ⚠️ 该库**未发布到 Maven Central / 任何远程仓库**，`pom.xml` 里直接写坐标无法下载。必须先本地构建：

```bash
git clone https://github.com/ym0327892-ops/JFXium.git
cd JFXium
./mvnw install -pl jfxium -DskipTests -q     # 安装到本地 ~/.m2/repository
```

之后才能引用：

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.33.1</version>
</dependency>
```

### 2. Load Theme & Use Components

```java
@Override
public void start(Stage stage) {
    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add(
        getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm()
    );

    // Button
    Button btn = ButtonAnt.create("Click me")
        .type(ButtonAnt.Type.PRIMARY)
        .onClick(e -> System.out.println("Hello JFXium!"))
        .build();

    // Input
    TextField input = InputAnt.create()
        .placeholder("Enter something...")
        .build();

    // Modal
    ModalAnt.create().title("Confirm").content("Are you sure?")
        .build().open(stage);

    stage.setScene(scene);
    stage.show();
}
```

### 3. Run Demo

```bash
# 在仓库根目录执行
./mvnw javafx:run -pl jfxium-demo
```

---

## Documentation

| Document | Description |
|----------|-------------|
| [快速上手](docs/cn/快速上手.md) | 5 分钟跑通第一个 admin 应用 |
| [组件参考](docs/cn/组件参考.md) | 全量组件/模板参考 + bindValue |
| [主题系统](docs/cn/主题系统.md) | 主题切换、自定义主题、styleClass |
| [业务模板](docs/cn/业务模板.md) | CrudTemplate / LoginTemplate / DashboardTemplate |
| [最佳实践](docs/cn/最佳实践.md) | Builder 模式、EventBus、页面骨架 |

---

## Requirements

- Java 21
- JavaFX 21.0.6
- Maven 3.8+

---

## License

MIT License
