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
- **11 Built-in Theme Entries** — Light/Dark/MUI + shadcn/Cyberpunk/Custom, with compact density variants
- **Builder Pattern** — Fluent API for all components: `XxxAnt.create()...build()`
- **LESS-based Theming** — Modify one LESS file to generate your own theme
- **i18n** — Built-in Chinese/English with `Messages.get(key)` API
- **Zero FXML** — Pure code-based UI construction

---

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.0-SNAPSHOT</version>
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

### AI 开发者

| Document | Description |
|----------|-------------|
| [AGENTS.md](AGENTS.md) | AI 工作指南（红线 + 架构 + 索引） |
| [.qoder/rules/red-lines.md](.qoder/rules/red-lines.md) | 致命红线（always-on） |
| [.qoder/skills/project-constraints.md](.qoder/skills/project-constraints.md) | 项目技术约束 |
| [.qoder/skills/component-pattern.md](.qoder/skills/component-pattern.md) | 组件设计模式 |
| [PROJECT_PLAN.md](PROJECT_PLAN.md) | 开发计划与进度 |

> 内部知识沉淀：[INTERNAL/](INTERNAL/) 包含设计决策、审计报告、API 速查（对内文档）。

---

## Requirements

- Java 21
- JavaFX 21.0.6
- Maven 3.8+

---

## License

MIT License
