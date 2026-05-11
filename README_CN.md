# JFXium 使用指南

> 现代化 JavaFX UI 框架，对标 Ant Design 6.x 设计风格

---

## 目录

1. [快速开始](#快速开始)
2. [主题系统](#主题系统)
3. [组件分类](#组件分类)
4. [自定义主题](#自定义主题)
5. [最佳实践](#最佳实践)

---

## 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 引入主题 CSS

在你的 JavaFX Application 启动时加载主题：

```java
@Override
public void start(Stage stage) {
    Scene scene = new Scene(root, 800, 600);
    // 加载浅色主题
    scene.getStylesheets().add(
        getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm()
    );
    stage.setScene(scene);
    stage.show();
}
```

### 3. 使用组件

```java
// 创建一个主按钮
Button btn = ButtonAnt.create("点击我")
    .type(ButtonAnt.Type.PRIMARY)
    .onClick(e -> System.out.println("Hello JFXium!"))
    .build();

// 创建一个输入框
TextField input = InputAnt.create()
    .placeholder("请输入内容")
    .build();

// 组合使用
VBox layout = new VBox(16);
layout.setPadding(new Insets(24));
layout.getChildren().addAll(input, btn);
```

---

## 主题系统

JFXium 使用 LESS 预处理器管理主题，支持 Light/Dark 两套主题。

### 主题文件位置

```
src/main/resources/org/openkawu/jfxium/css/
├── less/
│   ├── theme-base.less      # 基础样式（组件样式）
│   ├── theme-light.less     # 浅色主题变量
│   ├── theme-dark.less      # 深色主题变量
│   └── _variables.less      # 共享变量（颜色、间距、字体等）
├── theme-light.css          # 编译后的浅色主题
└── theme-dark.css           # 编译后的深色主题
```

### 切换主题

```java
// 切换到深色主题
scene.getStylesheets().clear();
scene.getStylesheets().add(
    getClass().getResource("/org/openkawu/jfxium/css/theme-dark.css").toExternalForm()
);
```

---

## 组件分类

### 通用组件 (General)

| 组件 | 说明 | 示例 |
|------|------|------|
| **ButtonAnt** | 按钮 | `ButtonAnt.create("提交").type(Type.PRIMARY).build()` |
| **InputAnt** | 输入框 | `InputAnt.create().placeholder("请输入").build()` |
| **TextAreaAnt** | 文本域 | `TextAreaAnt.create().rows(4).build()` |
| **CheckBoxAnt** | 复选框 | `CheckBoxAnt.create().text("记住我").build()` |
| **RadioButtonAnt** | 单选框 | `RadioButtonAnt.create().text("选项A").build()` |
| **SwitchAnt** | 开关 | `SwitchAnt.create().text("启用").build()` |
| **SliderAnt** | 滑块 | `SliderAnt.create().min(0).max(100).build()` |
| **SpinnerAnt** | 计数器 | `SpinnerAnt.create().min(0).max(100).build()` |

### 布局组件 (Layout)

| 组件 | 说明 | 示例 |
|------|------|------|
| **CardAnt** | 卡片 | `CardAnt.create().title("标题").content(node).build()` |
| **DividerAnt** | 分割线 | `DividerAnt.create().text("或").build()` |
| **FormAnt** | 表单 | `FormAnt.create().label("姓名", input).build()` |
| **TableAnt** | 表格 | `TableAnt.create(Person.class).column("姓名", "name").build()` |
| **LayoutAnt** | 页面布局 | `LayoutAnt.create().header(header).sider(sider, 200).content(content).build()` |
| **FlexAnt** | 弹性布局 | `FlexAnt.create().direction(ROW).gap(16).children(nodes).build()` |
| **GridAnt** | 栅格系统 | `GridAnt.create().col(6, node).col(6, node).build()` |
| **SpaceAnt** | 间距 | `SpaceAnt.create().size(16).children(nodes).build()` |

### 导航组件 (Navigation)

| 组件 | 说明 | 示例 |
|------|------|------|
| **AnchorAnt** | 锚点 | `AnchorAnt.create().item("basic", "基础", "#basic").build()` |
| **BreadcrumbAnt** | 面包屑 | `BreadcrumbAnt.create().item("首页", () -> {}).build()` |
| **DropdownAnt** | 下拉菜单 | `DropdownAnt.create().trigger(btn).item("1", "编辑").build()` |
| **MenuAnt** | 菜单栏 | `MenuAnt.create().menu("文件", m -> m.item("新建", () -> {})).build()` |
| **PaginationAnt** | 分页 | `PaginationAnt.create().total(100).pageSize(10).build()` |
| **StepsAnt** | 步骤条 | `StepsAnt.create().step("下单", "1").step("支付", "2").build()` |
| **TabsAnt** | 标签页 | `TabsAnt.create().tab("Tab1", content).build()` |

### 数据录入 (Data Entry)

| 组件 | 说明 | 示例 |
|------|------|------|
| **AutoCompleteAnt** | 自动完成 | `AutoCompleteAnt.<String>create().options(list).build()` |
| **CascaderAnt** | 级联选择 | `CascaderAnt.create().options(options).build()` |
| **ComboBoxAnt** | 下拉框 | `ComboBoxAnt.create().items(list).build()` |
| **DatePickerAnt** | 日期选择 | `DatePickerAnt.create().placeholder("选择日期").build()` |
| **TimePickerAnt** | 时间选择 | `TimePickerAnt.create().format("HH:mm:ss").build()` |
| **ColorPickerAnt** | 颜色选择 | `ColorPickerAnt.create().value(Color.BLUE).build()` |
| **InputNumberAnt** | 数字输入 | `InputNumberAnt.create().min(0).max(100).build()` |
| **MentionsAnt** | 提及 | `MentionsAnt.create().option("user1", "张三").build()` |
| **UploadAnt** | 文件上传 | `UploadAnt.create().multiple(true).build()` |
| **TransferAnt** | 穿梭框 | `TransferAnt.create().source(list1).target(list2).build()` |

### 数据展示 (Data Display)

| 组件 | 说明 | 示例 |
|------|------|------|
| **AvatarAnt** | 头像 | `AvatarAnt.create().text("JD").size(Size.LARGE).build()` |
| **BadgeAnt** | 徽标 | `BadgeAnt.create().count(5).build()` |
| **CalendarAnt** | 日历 | `CalendarAnt.create().value(LocalDate.now()).build()` |
| **CarouselAnt** | 轮播 | `CarouselAnt.create().image("/1.png").autoplay(true).build()` |
| **CollapseAnt** | 折叠面板 | `CollapseAnt.create().panel("1", "标题", content).build()` |
| **DescriptionsAnt** | 描述列表 | `DescriptionsAnt.create().item("姓名", "张三").build()` |
| **EmptyAnt** | 空状态 | `EmptyAnt.create().description("暂无数据").build()` |
| **ImageAnt** | 图片 | `ImageAnt.create().src("/photo.png").width(200).build()` |
| **ListAnt** | 高级列表 | `ListAnt.create().item(avatar, "标题", "描述").build()` |
| **ListViewAnt** | 列表 | `ListViewAnt.create().items(list).build()` |
| **PopoverAnt** | 气泡卡片 | `PopoverAnt.create().title("提示").content(node).build()` |
| **QRCodeAnt** | 二维码 | `QRCodeAnt.create().value("https://...").size(160).build()` |
| **SegmentedAnt** | 分段控制器 | `SegmentedAnt.create().option("day", "日").option("week", "周").build()` |
| **StatisticAnt** | 统计数值 | `StatisticAnt.create().title("用户数").value(112893).build()` |
| **TagAnt** | 标签 | `TagAnt.create().text("标签").color(Color.BLUE).build()` |
| **TimelineAnt** | 时间轴 | `TimelineAnt.create().item("创建", "2024-01-01", Color.BLUE).build()` |
| **TooltipAnt** | 文字提示 | `TooltipAnt.create().text("提示信息").build()` |
| **TreeAnt** | 树形控件 | `TreeAnt.create().root("根", r -> r.child("子")).build()` |
| **TreeSelectAnt** | 树形选择 | `TreeSelectAnt.create().treeNode("1", "根", children).build()` |
| **TypographyAnt** | 排版 | `TypographyAnt.title("标题").level(1).build()` |

### 反馈组件 (Feedback)

| 组件 | 说明 | 示例 |
|------|------|------|
| **AlertAnt** | 警告提示 | `AlertAnt.create().type(Type.SUCCESS).message("成功").build()` |
| **DrawerAnt** | 抽屉 | `DrawerAnt.create().title("设置").build().open(owner)` |
| **MessageAnt** | 全局提示 | `MessageAnt.success("操作成功！")` |
| **ModalAnt** | 对话框 | `ModalAnt.create().title("确认").build().open(owner)` |
| **NotificationAnt** | 通知提醒框 | `NotificationAnt.info("标题", "内容")` |
| **PopconfirmAnt** | 气泡确认框 | `PopconfirmAnt.create().title("删除？").onConfirm(() -> {}).build()` |
| **ProgressAnt** | 进度条 | `ProgressAnt.create().value(0.5).build()` |
| **ResultAnt** | 结果页 | `ResultAnt.create().status(Status.SUCCESS).title("成功").build()` |
| **SkeletonAnt** | 骨架屏 | `SkeletonAnt.create().rows(3).animated(true).build()` |
| **SpinAnt** | 加载中 | `SpinAnt.create().indicator(Indicator.SPINNER).tip("加载中...").build()` |

### 其他组件 (Other)

| 组件 | 说明 | 示例 |
|------|------|------|
| **AccordionAnt** | 手风琴 | `AccordionAnt.create().pane("标题", content).build()` |
| **AnimationAnt** | 动画工具 | `AnimationAnt.fadeIn(node, 300)` |
| **BackTopAnt** | 回到顶部 | `BackTopAnt.create().target(scrollPane).build()` |
| **FloatButtonAnt** | 悬浮按钮 | `FloatButtonAnt.create().icon(icon).onClick(() -> {}).build()` |
| **IconAnt** | 图标 | `IconAnt.symbol(IconAnt.Symbol.CLOSE)` |
| **RateAnt** | 评分 | `RateAnt.create().value(3.5).allowHalf(true).build()` |

---

## 自定义主题

JFXium 的主题系统参考 Ant Design 设计，**只需修改一个 LESS 文件即可生成整套 UI 主题**。

### 主题文件结构

```
css/less/
├── variables-base.less      # 基础尺寸、间距（一般不需要修改）
├── variables.less           # 默认浅色主题颜色变量
├── variables-dark.less      # 深色主题颜色变量
├── theme-base.less          # 组件样式（使用变量，不需要修改）
├── theme-light.less         # 浅色主题入口
├── theme-dark.less          # 深色主题入口
└── theme-custom.less        # ← 自定义主题示例
```

### 方式一：复制修改 theme-custom.less（推荐）

**只需 3 步：**

#### 第 1 步：复制主题文件

```bash
# 复制自定义主题示例
cp css/less/theme-custom.less css/less/theme-mytheme.less
```

#### 第 2 步：修改颜色变量

编辑 `theme-mytheme.less`，修改颜色值：

```less
// ============================================
// 我的自定义主题 - 企业红
// ============================================

@import "variables-base.less";

// 主题色 - 改为红色
@color-accent-5: #f5222d;
@color-accent-6: #cf1322;

// 成功色 - 改为绿色
@color-success-5: #52c41a;

// 背景色 - 改为浅灰
@color-bg-subtle: #f5f5f5;

// 边框色
@color-border-default: #d9d9d9;

// ... 其他变量

// 导入组件样式（必须保留）
@import "theme-base.less";
```

#### 第 3 步：编译生成 CSS

**手动编译：**
```bash
npx lessc css/less/theme-mytheme.less css/theme-mytheme.css
```

**自动编译（推荐）：**
编辑 `pom.xml`，取消注释自定义主题编译配置：

```xml
<execution>
    <id>compile-less-themes-custom</id>
    <phase>generate-resources</phase>
    <goals>
        <goal>exec</goal>
    </goals>
    <configuration>
        <executable>npx</executable>
        <arguments>
            <argument>lessc</argument>
            <argument>src/main/resources/org/openkawu/jfxium/css/less/theme-mytheme.less</argument>
            <argument>src/main/resources/org/openkawu/jfxium/css/theme-mytheme.css</argument>
        </arguments>
    </configuration>
</execution>
```

然后运行：
```bash
mvn compile
```

#### 使用自定义主题

```java
// 加载自定义主题
scene.getStylesheets().add(
    getClass().getResource("/org/openkawu/jfxium/css/theme-mytheme.css").toExternalForm()
);
```

### 方式二：运行时切换 CSS 变量

不需要重新编译，直接在运行时修改：

```java
// 修改主题色
root.setStyle(
    "-color-accent-emphasis: #ff6b6b;" +
    "-color-bg-default: #f0f2f5;" +
    "-color-border-default: #d9d9d9;"
);
```

### 方式三：覆盖特定组件样式

```java
Button btn = ButtonAnt.create("自定义")
    .style("-fx-background-color: #ff6b6b; -fx-text-fill: white;")
    .build();
```

### 可修改的变量清单

| 变量 | 说明 | 示例 |
|------|------|------|
| `@color-accent-5` | 主题主色 | `#1677ff` (蓝), `#f5222d` (红) |
| `@color-success-5` | 成功色 | `#52c41a` |
| `@color-warning-5` | 警告色 | `#faad14` |
| `@color-danger-5` | 危险色 | `#f5222d` |
| `@color-bg-default` | 背景色 | `#ffffff` |
| `@color-bg-subtle` | 次要背景 | `#f6f8fa` |
| `@color-fg-default` | 文字颜色 | `rgba(0,0,0,0.88)` |
| `@color-border-default` | 边框颜色 | `#d9d9d9` |
| `@font-size-md` | 基础字体 | `14px` |
| `@font-family` | 字体家族 | `-apple-system, ...` |
| `@spacing-unit` | 间距单位 | `4px` |
| `@border-radius-md` | 圆角大小 | `6px` |
| `@control-height` | 控件高度 | `32px` |
| `@motion` | 动画开关 | `true` / `false` |
| `@wireframe` | 线框模式 | `true` / `false` |

### 与 Ant Design 主题编辑器对比

| 功能 | Ant Design | JFXium | 说明 |
|------|-----------|--------|------|
| **品牌色** | colorPrimary | @color-accent-5 | ✅ 支持 |
| **成功色** | colorSuccess | @color-success-5 | ✅ 支持 |
| **警告色** | colorWarning | @color-warning-5 | ✅ 支持 |
| **错误色** | colorError | @color-danger-5 | ✅ 支持 |
| **信息色** | colorInfo | @color-accent-5 | ✅ 支持 |
| **链接色** | colorLink | @color-accent-5 | ✅ 支持 |
| **基础文本色** | colorTextBase | @color-fg-default | ✅ 支持 |
| **基础背景色** | colorBgBase | @color-bg-default | ✅ 支持 |
| **字体大小** | fontSize | @font-size-md | ✅ 支持 |
| **字体家族** | fontFamily | @font-family | ✅ 支持 |
| **圆角** | borderRadius | @border-radius-md | ✅ 支持 |
| **尺寸步长** | sizeStep | @spacing-unit | ✅ 支持 |
| **控件高度** | controlHeight | @control-height | ✅ 支持 |
| **线框模式** | wireframe | @wireframe | ✅ 支持 |
| **动画开关** | motion | @motion | ✅ 支持 |
| **组件级主题** | components.Button | ❌ | 暂不支持 |
| **算法派生** | algorithm | ❌ | 暂不支持 |
| **动态切换** | ConfigProvider | scene.setStylesheets | ✅ 支持 |
| **主题编辑器** | 在线可视化 | ❌ | 暂不支持 |

**总结**：JFXium 支持 Ant Design 主题编辑器中约 85% 的核心功能（颜色、尺寸、字体、圆角、动画等），但暂不支持组件级主题定制和算法派生。对于大部分业务场景，通过修改 LESS 变量已足够满足自定义需求。

### 完整示例：创建紫色主题

```less
// theme-purple.less
@import "variables-base.less";

// 紫色主题
@color-accent-0: #f3e8ff;
@color-accent-1: #e4ccff;
@color-accent-2: #d1aaff;
@color-accent-3: #bc87ff;
@color-accent-4: #a564ff;
@color-accent-5: #8b41f5;    // 主色
@color-accent-6: #7228dc;
@color-accent-7: #5910c2;

// 语义变量
@color-bg-subtle: #f9f8ff;
@color-border-default: #ddd8f5;

@import "theme-base.less";
```

编译后所有组件会自动使用紫色主题，无需修改任何 Java 代码！

---

## 最佳实践

### 1. 使用 Builder 模式

所有组件都使用 Builder 模式，链式调用：

```java
Button btn = ButtonAnt.create("保存")
    .type(ButtonAnt.Type.PRIMARY)
    .size(ButtonAnt.Size.LARGE)
    .rounded()
    .onClick(e -> save())
    .build();
```

### 2. 尺寸规范

| 尺寸 | 适用场景 |
|------|----------|
| **SMALL** | 表格内操作按钮、紧凑布局 |
| **DEFAULT** | 大多数场景 |
| **LARGE** | 重要操作、独立按钮 |

### 3. 类型选择

| 类型 | 适用场景 |
|------|----------|
| **PRIMARY** | 主操作，一个页面最多一个 |
| **DEFAULT** | 次要操作 |
| **SUCCESS** | 正向操作（保存、提交） |
| **WARNING** | 警告操作（需谨慎） |
| **DANGER** | 危险操作（删除、清空） |
| **TEXT** | 低优先级操作 |
| **LINK** | 导航、跳转 |

### 4. 无障碍支持

```java
// 为按钮添加无障碍信息
AccessibilityUtils.configureButton(
    button,
    "提交表单",           // 标签
    "点击后将保存所有修改"  // 描述
);

// 为输入框添加无障碍信息
AccessibilityUtils.configureTextInput(
    input,
    "用户名",             // 标签
    "请输入您的用户名"      // 占位提示
);
```

---

## 常见问题

### Q: 如何修改主题色？

A: 编辑 `src/main/resources/org/openkawu/jfxium/css/less/_variables.less` 中的 `@color-accent-5` 变量，然后运行 `mvn compile` 重新编译。

### Q: 支持 JavaFX 哪些版本？

A: 支持 JavaFX 17+，推荐 JavaFX 21。

### Q: 如何添加自定义样式类？

A: 使用 `.styleClass("my-class")` 方法：

```java
Button btn = ButtonAnt.create("自定义")
    .styleClass("my-custom-button")
    .build();
```

### Q: 组件是否支持响应式布局？

A: 所有组件都基于 JavaFX 布局系统，可与 VBox、HBox、GridPane 等标准布局容器配合使用。

---

*文档版本: 1.0*
*更新日期: 2026-05-09*
