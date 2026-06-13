# JFXium 快速开始

> 5 分钟搞起一个完整的 admin 后台：登录页 + 主页（含统计卡） + 列表页（含表格/筛选/分页）。
> 直接复制粘贴 → 改 brandName/字段 → 跑起来。

---

## 一、依赖（pom.xml）

```xml
<dependency>
    <groupId>org.openkawu</groupId>
    <artifactId>jfxium</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

---

## 二、最简启动：用 LoginTemplate 起一个登录页

`MyAdminApp.java`（约 30 行）：

```java
package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.template.LoginTemplate;

public class MyAdminApp extends Application {
    @Override
    public void start(Stage stage) {
        var login = LoginTemplate.create()
                .brandName("My Admin")
                .tagline("企业管理系统")
                .features("102+ 内置组件", "11 套主题", "Builder API")
                .onSubmit((u, p) -> {
                    if ("admin".equals(u) && "1234".equals(p)) {
                        showMainStage(stage);
                    } else {
                        MessageAnt.show("用户名或密码错误", MessageAnt.Type.ERROR, 3);
                    }
                })
                .build();

        var scene = new Scene(login, 760, 520);
        ThemeManager.getInstance().applyTheme(new LightTheme());
        ThemeManager.getInstance().registerScene(scene);
        stage.setScene(scene);
        stage.setTitle("My Admin - 登录");
        stage.setResizable(false);
        stage.show();
    }

    private void showMainStage(Stage stage) {
        // 见下一节
    }

    public static void main(String[] args) { launch(args); }
}
```

**就这些**。运行后 760×520 窗口出现双栏 banner 登录页，输入 `admin / 1234` 跳到主页。

---

## 三、主页：DashboardTemplate（约 30 行）

把 `showMainStage()` 补全：

```java
private void showMainStage(Stage stage) {
    var dashboard = DashboardTemplate.create()
            .welcome("欢迎回来 👋")
            .stat(IconAnt.Path.USERS,     "总用户",   "1,234",   "↑ 12.5%", true)
            .stat(IconAnt.Path.FILE,      "今日订单", "89",      "↓ 3.2%",  false)
            .stat(IconAnt.Path.CHART,     "月销售额", "¥125k",   "↑ 8.4%",  true)
            .stat(IconAnt.Path.DASHBOARD, "转化率",   "23.4%",   "↑ 1.2%",  true)
            .build();

    var scene = new Scene(dashboard, 1280, 800);
    ThemeManager.getInstance().registerScene(scene);
    stage.setScene(scene);
    stage.setTitle("My Admin - 概览");
    stage.setResizable(true);
    stage.centerOnScreen();
}
```

---

## 四、列表页：CrudTemplate + TableAnt + FilterBarAnt

```java
public Node buildUserListPage() {
    // 1. 表格
    TableView<User> table = TableAnt.<User>create()
            .column("ID", u -> String.valueOf(u.id())).width(60).align(TableAnt.Align.RIGHT).end()
            .column("姓名", User::name).width(120).end()
            .column("邮箱", User::email).minWidth(220).end()
            .column("角色", User::role).width(100).end()
            .actionColumn("操作")
                .action("编辑", u -> editUser(u))
                .action("删除", u -> deleteUser(u)).danger()
                .end()
            .data(users)
            .build();

    // 2. 筛选 + 操作组合
    TextField search   = InputAnt.create().placeholder("搜索用户").build();
    ComboBox<String> roleCombo = ComboBoxAnt.<String>create()
            .items("全部", "管理员", "编辑", "访客").value("全部").build();
    Button addBtn = ButtonAnt.create("新增用户").type(ButtonAnt.Type.PRIMARY).build();

    // 3. 分页
    Label total = new Label("共 57 条");
    Pagination pagination = PaginationAnt.create().pageCount(6).build();

    // 4. CrudTemplate 一键拼装
    return CrudTemplate.create()
            .title("用户管理")
            .topLeft(search, roleCombo)
            .topRight(addBtn)
            .body(table)
            .bottomLeft(total)
            .bottomRight(pagination)
            .bordered(true)
            .build();
}
```

—— 替代约 200 行手写 BorderPane + GridPane + ChangeListener 的样板代码。

---

## 五、运行 Showcase Demo（学习用）

```bash
git clone <repo>
cd JFXium
mvn install -pl jfxium -DskipTests -q
mvn javafx:run -pl jfxium-demo -q
```

打开后左侧菜单按分类浏览所有组件，每个 Section 内有「查看代码」可复制。

---

## 六、下一步

- **想换主题**：见 [README_CN.md > 主题系统](../README_CN.md#主题系统)（11 套内置主题，运行时切换）
- **想自定义颜色**：见 [README_CN.md > 自定义主题](../README_CN.md#自定义主题)
- **想造组件**：见 [docs/cn/主题系统.md > 自定义组件](../docs/cn/主题系统.md#自定义组件)
