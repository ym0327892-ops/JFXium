# JFXium 常用片段（可直接抄）

> 所有片段都来自本仓库可编译的 demo / 组件 Javadoc。方法不存在于源码时不要臆造。

## 1. 页面骨架

```java
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.template.PageTemplate;

VBox page = VBoxAnt.create()
        .spacing(0)
        .children(PageTemplate.create()
                .title("用户管理")
                .description("查询、创建与维护用户。")
                .sections(filterSection(), tableSection())
                .padding(24)
                .build())
        .build();
```

## 2. 表单 + 校验 + 提交

```java
import org.openkawu.jfxium.component.composite.FormAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.core.form.Rule;

FormAnt.Result form = FormAnt.create()
        .layout(FormAnt.Layout.HORIZONTAL)
        .item("用户名", InputAnt.create().placeholder("至少 3 个字符").build(), "username")
            .required()
            .rule(Rule.minLength(3, "用户名至少 3 个字符"))
            .end()
        .item("邮箱", InputAnt.create().placeholder("name@example.com").build(), "email")
            .required()
            .rule(Rule.pattern(".*@.*", "请输入有效邮箱"))
            .end()
        .footer(ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    if (form.validate()) {
                        MessageAnt.success("保存成功");
                    }
                }).build())
        .buildResult();

VBox formNode = form.getRoot();          // 场景图节点
Map<String, Object> values = form.getValues();   // 字段值
// form.reset() / form.resetField("email") / form.submit() / form.context()
```

> `buildResult()` 返回 `FormAnt.Result`；`getRoot()` 取 `VBox`，`validate()` 校验，
> `getValues()` 取字段值，`reset()` 复位。只读展示场景可改用 `build()` 直接拿 `VBox`。

## 3. 表格

```java
import org.openkawu.jfxium.component.control.TableAnt;

TableAnt<Person> table = TableAnt.<Person>create()
        .column("姓名", Person::name)
        .numberColumn("年龄", Person::age)
        .column("邮箱", Person::email)
        .data(rows)
        .striped(true)
        .borders(TableAnt.Border.BOTH)
        .build();
```

## 4. 模态框 / 抽屉 / 输入弹框

```java
// 常驻 Node？不，这里 build() 返回 Result，必须 open(owner)
ModalAnt.create()
        .title("删除确认")
        .content("删除后不可恢复，确定继续？")
        .build()
        .open(triggerButton);

DrawerAnt.create()
        .title("编辑用户")
        .content(formNode)
        .width(420)
        .build()
        .open(triggerButton);

PromptDialogAnt.create()
        .title("重命名")
        .build()
        .open(triggerButton);
```

## 5. 全局消息 / 通知

```java
MessageAnt.success("保存成功");
MessageAnt.error("保存失败，请重试");

NotificationAnt.create()
        .title("部署完成")
        .description("v1.34.1 已发布到生产环境")
        .build()
        .show();
```

## 6. 加载状态

```java
Node loading = SpinAnt.create()
        .indicator(SpinAnt.Indicator.SPINNER)   // 或 DOTS / BARS
        .size(Size.LARGE)
        .tip("加载中…")
        .build();
```

## 7. 数字输入（选型）

```java
// 表单/金额/百分比：推荐 InputNumberAnt
Node qty = InputNumberAnt.create()
        .value(1).min(0).max(999)
        .step(1).precision(0)
        .onChange(v -> save(v))
        .build();

// 需要 JavaFX Spinner 语义（valueProperty / increment / 第三方要求 Spinner）
SpinnerAnt latitude = SpinnerAnt.create(-90, 90, 0, 0.01)
        .onChange(v -> save(v))
        .build();                 // 默认可键入；.editable(false) 只读
```

## 8. 布局

```java
HBox toolbar = HBoxAnt.create()
        .spacing(8)
        .padding(12)
        .children(ButtonAnt.create("新建").type(ButtonAnt.Type.PRIMARY).build(),
                  ButtonAnt.create("刷新").build())
        .build();

VBox page = VBoxAnt.create()
        .spacing(16)
        .padding(24)
        .children(toolbar, table)
        .build();

// 自适应：GridAnt（断点/列数）、FlexAnt（换行弹性）
```

## 9. 主题 / 密度 / 主色

```java
ThemeManager tm = ThemeManager.getInstance();
tm.applyTheme(new LightTheme());          // 或 DarkTheme / FluentTheme / FluentDarkTheme
tm.registerScene(scene);                  // 场景后续跟随主题切换
tm.setDensity(ThemeDensity.COMPACT);      // theme-*-compact.css
tm.setPrimaryColor("#ff5722");            // Fluent 主题有固定品牌色，会跳过注入
```

## 10. i18n

```java
// 字段不要初始化时锁死 Locale
private String placeholder = null;        // ❌ = Messages.get("user.name")

// build() 内懒解析
String effective = placeholder != null ? placeholder : Messages.get("user.name.placeholder");

// 持久组件跟随语言切换
Messages.localeProperty().addListener((obs, old, locale) -> {
    if (placeholder == null) {
        field.setPromptText(Messages.get("user.name.placeholder"));
    }
});
```

## 11. 异步 / 线程

```java
CompletableFuture.supplyAsync(this::loadUsers)
        .thenAccept(rows -> Platform.runLater(() -> table.setItems(rows)));
```

## 12. 模块化消费者

```java
module com.example.app {
    requires javafx.controls;
    requires org.openkawu.jfxium;   // jfxium 的自动模块名/显式模块名
}
```

> 非模块化项目（classpath）直接依赖 jar 即可。样式表由 `ThemeManager` 注入，不要手写 import。
