# JFXium 布局 DSL 文档

## 概述

JFXium 提供了一套流式 API 用于创建常见布局，简化 JavaFX 布局代码，提高可读性和开发效率。

## VBox 垂直布局

### 基本用法

```java
VBox vbox = Layouts.vbox()
    .spacing(16)
    .padding(20)
    .align(Pos.CENTER)
    .children(
        new Label("Title"),
        new Button("Button 1"),
        new Button("Button 2")
    )
    .build();
```

### 配置方法

| 方法 | 说明 | 默认值 |
|------|------|--------|
| `spacing(double)` | 子节点间距 | 8 |
| `padding(double)` | 四边内边距 | 0 |
| `padding(double, double, double, double)` | 自定义内边距 | 0 |
| `align(Pos)` | 对齐方式 | `Pos.TOP_LEFT` |
| `children(Node...)` | 添加子节点 | - |
| `style(String)` | 内联样式 | - |

### 示例

```java
// 垂直表单布局
VBox form = Layouts.vbox()
    .spacing(12)
    .padding(16)
    .align(Pos.CENTER_LEFT)
    .children(
        new Label("Username"),
        new TextField(),
        new Label("Password"),
        new PasswordField(),
        JFXButton.create("Submit").type(JFXButton.Type.PRIMARY).build()
    )
    .build();
```

## HBox 水平布局

### 基本用法

```java
HBox hbox = Layouts.hbox()
    .spacing(12)
    .padding(16)
    .align(Pos.CENTER_LEFT)
    .children(
        new Label("Left"),
        Layouts.grow(),  // 弹性占位
        new Button("Right")
    )
    .build();
```

### 配置方法

| 方法 | 说明 | 默认值 |
|------|------|--------|
| `spacing(double)` | 子节点间距 | 8 |
| `padding(double)` | 四边内边距 | 0 |
| `padding(double, double, double, double)` | 自定义内边距 | 0 |
| `align(Pos)` | 对齐方式 | `Pos.CENTER_LEFT` |
| `children(Node...)` | 添加子节点 | - |
| `style(String)` | 内联样式 | - |

### 示例

```java
// 工具栏布局
HBox toolbar = Layouts.hbox()
    .spacing(8)
    .padding(8, 16, 8, 16)
    .align(Pos.CENTER_LEFT)
    .children(
        new Label("Toolbar"),
        Layouts.grow(),
        JFXButton.create("New").type(JFXButton.Type.PRIMARY).build(),
        JFXButton.create("Edit").type(JFXButton.Type.DEFAULT).build(),
        JFXButton.create("Delete").type(JFXButton.Type.TEXT).build()
    )
    .build();
```

## GridPane 网格布局

### 基本用法

```java
GridPane grid = Layouts.grid()
    .cols(3)
    .gap(8)
    .padding(16)
    .children(
        new Button("1"), new Button("2"), new Button("3"),
        new Button("4"), new Button("5"), new Button("6")
    )
    .build();
```

### 配置方法

| 方法 | 说明 | 默认值 |
|------|------|--------|
| `cols(int)` | 列数 | 2 |
| `gap(double)` | 行列间距 | 8 |
| `gap(double, double)` | 水平/垂直间距 | 8, 8 |
| `padding(double)` | 四边内边距 | 0 |
| `padding(double, double, double, double)` | 自定义内边距 | 0 |
| `children(Node...)` | 添加子节点 | - |
| `style(String)` | 内联样式 | - |

### 示例

```java
// 表单网格布局
GridPane formGrid = Layouts.grid()
    .cols(2)
    .gap(12, 8)
    .padding(16)
    .children(
        new Label("Name:"), new TextField(),
        new Label("Email:"), new TextField(),
        new Label("Phone:"), new TextField(),
        new Label("Address:"), new TextField()
    )
    .build();
```

## 辅助方法

### grow - 弹性占位

在 HBox 或 VBox 中自动填充剩余空间：

```java
HBox hbox = Layouts.hbox()
    .children(
        new Label("Left"),
        Layouts.grow(),  // 填充中间空间
        new Button("Right")
    )
    .build();
```

### spacer - 固定占位

创建固定尺寸的占位区域：

```java
HBox hbox = Layouts.hbox()
    .children(
        new Button("Left"),
        Layouts.spacer(20, 0),  // 20像素宽的水平间距
        new Button("Right")
    )
    .build();
```

## 实际示例

### 登录页面布局

```java
VBox loginPage = Layouts.vbox()
    .spacing(16)
    .padding(40)
    .align(Pos.CENTER)
    .children(
        new Label("Login") {{ setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); }},
        JFXInput.create().placeholder("Username").build(),
        JFXInput.create().password().placeholder("Password").build(),
        JFXButton.create("Login").type(JFXButton.Type.PRIMARY).size(JFXButton.Size.LARGE).build(),
        new Label("Forgot password?") {{ setStyle("-fx-text-fill: -color-accent-emphasis;"); }}
    )
    .build();
```

### 卡片列表布局

```java
VBox cardList = Layouts.vbox()
    .spacing(16)
    .padding(16)
    .children(
        JFXCard.create()
            .title("Card 1")
            .content("Content of card 1")
            .build(),
        JFXCard.create()
            .title("Card 2")
            .content("Content of card 2")
            .build(),
        JFXCard.create()
            .title("Card 3")
            .content("Content of card 3")
            .build()
    )
    .build();
```

### 响应式工具栏

```java
HBox toolbar = Layouts.hbox()
    .spacing(8)
    .padding(8, 16, 8, 16)
    .align(Pos.CENTER_LEFT)
    .children(
        new Label("Application"),
        Layouts.grow(),
        JFXButton.create("Settings").type(JFXButton.Type.TEXT).build(),
        JFXButton.create("Help").type(JFXButton.Type.TEXT).build(),
        JFXButton.create("Logout").type(JFXButton.Type.TEXT).build()
    )
    .build();
```

### 仪表盘网格

```java
GridPane dashboard = Layouts.grid()
    .cols(3)
    .gap(16)
    .padding(16)
    .children(
        createStatCard("Users", "1,234", "+12%"),
        createStatCard("Revenue", "$56,789", "+8%"),
        createStatCard("Orders", "892", "+15%"),
        createStatCard("Products", "456", "+3%"),
        createStatCard("Reviews", "1,567", "+22%"),
        createStatCard("Visitors", "12,345", "+18%")
    )
    .build();
```

## 最佳实践

1. **保持间距一致**: 使用统一的间距值（如 8, 12, 16, 24）
2. **合理使用 grow**: 在需要填充剩余空间时使用 `grow()`
3. **避免嵌套过深**: 布局嵌套不要超过 3-4 层
4. **使用 padding**: 为容器添加适当的内边距，避免内容贴边
5. **对齐方式**: 根据内容选择合适的对齐方式

## 与组件结合使用

```java
// 创建带布局的表单
VBox form = Layouts.vbox()
    .spacing(16)
    .padding(24)
    .align(Pos.CENTER_LEFT)
    .children(
        JFXForm.create()
            .item("Name", JFXInput.create().placeholder("Enter name").build())
            .item("Email", JFXInput.create().placeholder("Enter email").build())
            .onSubmit(data -> handleSubmit(data))
            .build(),
        Layouts.hbox()
            .spacing(8)
            .children(
                JFXButton.create("Cancel").type(JFXButton.Type.DEFAULT).build(),
                Layouts.grow(),
                JFXButton.create("Submit").type(JFXButton.Type.PRIMARY).build()
            )
            .build()
    )
    .build();
```