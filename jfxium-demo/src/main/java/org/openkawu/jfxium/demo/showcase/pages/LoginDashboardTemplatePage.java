package org.openkawu.jfxium.demo.showcase.pages;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.TimelineAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;
import org.openkawu.jfxium.template.DashboardTemplate;
import org.openkawu.jfxium.template.LoginTemplate;

/**
 * Login + Dashboard 业务模板展示页（M19.16，B 路线）。
 *
 * <p>合并展示两个新业务模板：</p>
 * <ul>
 *   <li>LoginTemplate —— 双栏 banner 登录页</li>
 *   <li>DashboardTemplate —— admin 概览首页（统计卡矩阵 + 双栏底部）</li>
 * </ul>
 */
public class LoginDashboardTemplatePage implements ShowcasePage {

    @Override public String   key()      { return "login-dashboard-template"; }
    @Override public String   title()    { return "Login / Dashboard 业务模板"; }
    @Override public Category category() { return Category.TEMPLATE; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Login / Dashboard 业务模板");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("admin 后台两个最高频整页骨架——一行调用替代 ~300 行手写。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionLogin(),
                        sectionLoginWithLinks(),
                        sectionDashboardBasic(),
                        sectionDashboardFull()
                )
                .build();
    }

    private Node sectionLogin() {
        BorderPane login = LoginTemplate.create()
                .brandName("JFXium Admin")
                .tagline("现代化 JavaFX UI 框架")
                .features("60+ 内置组件", "8 套主题运行时切换", "Builder Pattern API", "代码构建 UI")
                .copyright("© 2026 JFXium · MIT License")
                .onSubmit((u, p) -> MessageAnt.success("登录成功：" + u))
                .build();
        // 缩小到 Showcase 可视范围
        login.setPrefSize(620, 420);
        login.setMaxSize(620, 420);

        return ShowcaseSection.create()
                .title("场景 1：基础登录页（双栏 banner）")
                .description("左 banner 渐变 + 卖点 + 版权；右表单：用户名 + 密码 + 记住我 + 登录")
                .demo(login)
                .code("""
                        BorderPane login = LoginTemplate.create()
                            .brandName("JFXium Admin")
                            .tagline("现代化 JavaFX UI 框架")
                            .features("60+ 内置组件", "8 套主题", "Builder API")
                            .onSubmit((u, p) -> auth(u, p))
                            .build();

                        Scene scene = new Scene(login, 760, 520);
                        stage.setScene(scene);
                        stage.show();
                        """)
                .build();
    }

    private Node sectionLoginWithLinks() {
        BorderPane login = LoginTemplate.create()
                .brandName("企业管理系统")
                .tagline("更安全更高效的协作")
                .features("企业级权限", "审计日志", "SSO 单点登录")
                .formTitle("欢迎回来")
                .formSubtitle("请使用工作账号登录")
                .submitText("立即登录")
                .onSubmit((u, p) -> MessageAnt.success("登录：" + u))
                .onForgot(() -> MessageAnt.info("跳转到忘记密码页"))
                .onRegister(() -> MessageAnt.info("跳转到注册页"))
                .build();
        login.setPrefSize(620, 420);
        login.setMaxSize(620, 420);

        return ShowcaseSection.create()
                .title("场景 2：完整登录页（含忘记密码 + 注册链接）")
                .description(".onForgot / .onRegister 不为 null 时显示对应链接")
                .demo(login)
                .code("""
                        LoginTemplate.create()
                            .brandName("企业管理系统")
                            .formTitle("欢迎回来")
                            .submitText("立即登录")
                            .onSubmit((u, p) -> auth(u, p))
                            .onForgot(() -> openForgotPwd())
                            .onRegister(() -> openRegister())
                            .build();
                        """)
                .build();
    }

    private Node sectionDashboardBasic() {
        VBox dashboard = DashboardTemplate.create()
                .welcome("欢迎回来 👋")
                .stat(IconAnt.Path.USERS, "总用户", "1,234", "↑ 12.5%", true)
                .stat(IconAnt.Path.FILE, "今日订单", "89", "↓ 3.2%", false)
                .stat(IconAnt.Path.CHART, "月销售额", "¥125,890", "↑ 8.4%", true)
                .stat(IconAnt.Path.DASHBOARD, "转化率", "23.4%", "↑ 1.2%", true)
                .build();
        dashboard.setMaxWidth(720);

        return ShowcaseSection.create()
                .title("场景 3：基础 Dashboard（4 列统计卡）")
                .description("欢迎语 + 4 个统计卡 —— admin 首页核心")
                .demo(dashboard)
                .code("""
                        VBox dashboard = DashboardTemplate.create()
                            .welcome("欢迎回来 👋")
                            .stat(IconAnt.Path.USERS, "总用户", "1,234", "↑ 12.5%", true)
                            .stat(IconAnt.Path.FILE, "今日订单", "89", "↓ 3.2%", false)
                            .stat(IconAnt.Path.CHART, "月销售额", "¥125,890", "↑ 8.4%", true)
                            .stat(IconAnt.Path.DASHBOARD, "转化率", "23.4%", "↑ 1.2%", true)
                            .build();
                        """)
                .build();
    }

    private Node sectionDashboardFull() {
        // 底部左：Timeline
        VBox timeline = TimelineAnt.create()
                .item("张三 创建订单 #1024", "10:24", TimelineAnt.DotColor.BLUE)
                .item("李四 完成支付 ¥1,899", "09:58", TimelineAnt.DotColor.GREEN)
                .item("系统 自动备份完成", "09:30", TimelineAnt.DotColor.GRAY)
                .item("订单 #1019 退款成功", "昨天", TimelineAnt.DotColor.RED)
                .build();
        VBox.setMargin(timeline, new Insets(8, 0, 0, 8));
        VBox activityCard = CardAnt.create()
                .title("最近活动")
                .content(new VBox(timeline))
                .bordered(true).shadow(CardAnt.Shadow.SMALL)
                .build();

        // 底部右：待办
        VBox todoBox = new VBox(8);
        todoBox.setPadding(new Insets(8));
        todoBox.getChildren().addAll(
                new CheckBox("审核张三的注册申请"),
                new CheckBox("回复客户工单 #C-208"),
                new CheckBox("准备 Q2 产品规划"),
                new CheckBox("更新部署文档")
        );
        VBox todoCard = CardAnt.create()
                .title("待办任务")
                .content(todoBox)
                .bordered(true).shadow(CardAnt.Shadow.SMALL)
                .build();

        VBox dashboard = DashboardTemplate.create()
                .welcome("欢迎回来，张三")
                .stat(IconAnt.Path.USERS, "用户", "1,234", "↑ 12%", true)
                .stat(IconAnt.Path.FILE, "订单", "89", "↓ 3%", false)
                .stat(IconAnt.Path.CHART, "销售", "¥125k", "↑ 8%", true)
                .stat(IconAnt.Path.DASHBOARD, "转化", "23.4%", "↑ 1%", true)
                .bottomLeft(activityCard)
                .bottomRight(todoCard)
                .build();
        dashboard.setMaxWidth(720);

        return ShowcaseSection.create()
                .title("场景 4：完整 Dashboard（统计卡 + 活动 + 待办）")
                .description("admin 首页的完整骨架——统计卡 + 60% Timeline 活动 + 40% Todo 列表")
                .demo(dashboard)
                .code("""
                        VBox dashboard = DashboardTemplate.create()
                            .welcome("欢迎回来，张三")
                            .stat(...).stat(...).stat(...).stat(...)
                            .bottomLeft(activityCard)     // 60%
                            .bottomRight(todoCard)         // 40%
                            .build();
                        """)
                .build();
    }
}
