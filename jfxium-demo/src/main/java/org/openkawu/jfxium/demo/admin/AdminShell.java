package org.openkawu.jfxium.demo.admin;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.AppShellAnt;
import org.openkawu.jfxium.component.AvatarAnt;
import org.openkawu.jfxium.component.DropdownAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.MenuAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.util.Spacers;
import org.openkawu.jfxium.demo.admin.pages.DashboardPage;
import org.openkawu.jfxium.demo.admin.pages.PlaceholderPage;
import org.openkawu.jfxium.demo.admin.pages.UserListPage;

/**
 * Admin 主壳：登录成功后的主界面（独立大窗 Stage）。
 *
 * <pre>
 * ┌────────────────────────────────────────────────┐
 * │ Header  [Logo]               [theme] [avatar▼] │
 * ├──────────┬─────────────────────────────────────┤
 * │  Sider   │                                     │
 * │  (Menu)  │   Content (Router 切换)             │
 * │          │                                     │
 * └──────────┴─────────────────────────────────────┘
 * </pre>
 *
 * <p>设计取舍：</p>
 * <ul>
 *   <li>独立 Stage：与登录窗分离，避免 setScene + 调尺寸两套属性管理</li>
 *   <li>默认 1280×800：admin 黄金尺寸（Ant Pro / Element Admin 同款）</li>
 *   <li>最小 1024×640：保证侧边菜单 + 内容区不被挤碎</li>
 *   <li>可缩放 + 居中</li>
 * </ul>
 */
public class AdminShell {

    /** 主壳默认尺寸。 */
    public static final double DEFAULT_WIDTH  = 1280;
    public static final double DEFAULT_HEIGHT = 800;
    public static final double MIN_WIDTH      = 1024;
    public static final double MIN_HEIGHT     = 640;

    /** 自有 Stage。 */
    private final Stage stage = new Stage();

    private final Router router = new Router();
    private final ThemeManager themeManager = ThemeManager.getInstance();

    /** 登录后传入的当前用户名。 */
    private final String currentUser;

    /** 登出回调（点"退出登录"时触发）。由 AdminDemo 注入：关掉本壳 + 重新打开登录窗。 */
    private final Runnable onLogout;

    public AdminShell(String currentUser, Runnable onLogout) {
        this.currentUser = currentUser;
        this.onLogout = onLogout;
    }

    public void show() {
        registerPages();

        HBox header = buildHeader();
        VBox sider = buildSider();

        StackPane content = router.getOutlet();
        content.setStyle("-fx-background-color: -color-bg-layout;");

        BorderPane shell = AppShellAnt.create()
                .header(header)
                .sider(sider, 220)
                .content(content)
                .build();

        // 持有 sider 容器引用：路由切换后重建菜单时替换 children[0]
        if (shell.getCenter() instanceof HBox center) {
            this.siderContainer = center;
        }

        // 路由切换 → 重建菜单（让 selectedKey 跟随当前路由）
        router.onChange(key -> rebuildSider());

        // 默认导航到首页
        router.go("dashboard");

        Scene scene = new Scene(shell, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        themeManager.applyTheme(new LightTheme());
        themeManager.registerScene(scene);

        stage.setScene(scene);
        stage.setTitle("JFXium Admin Demo - " + currentUser);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.centerOnScreen();
        stage.show();
    }

    /** 路由切换时调用：重建 sider 让 selectedKey 跟随当前路由。 */
    private void rebuildSider() {
        if (siderContainer == null) return;
        VBox newSider = buildSider();
        // 同步宽度（AppShellAnt 在初次 build 时设的 220 不会自动应用到新节点）
        newSider.setPrefWidth(220);
        newSider.setMinWidth(220);
        newSider.getStyleClass().add(org.openkawu.jfxium.core.css.CssClasses.APP_SHELL_SIDER);
        // 替换原 sider（它一定是 children[0]）
        if (!siderContainer.getChildren().isEmpty()) {
            siderContainer.getChildren().set(0, newSider);
        }
    }

    public void close() {
        stage.close();
    }

    public Stage getStage() {
        return stage;
    }

    // ============================================================
    // 注册所有页面（M12.1 全部用 Placeholder，M12.2+ 逐步替换）
    // ============================================================
    private void registerPages() {
        router.register(new DashboardPage(currentUser));                       // ✅ M12.3 真页面
        router.register(new UserListPage());                                   // ✅ M12.4 真页面
        router.register(new PlaceholderPage("user.form",  "用户编辑"));
        router.register(new PlaceholderPage("profile",    "个人中心"));
        router.register(new PlaceholderPage("404",        "页面未找到"));
    }

    // ============================================================
    // 顶栏：[Logo]   spacer   [主题]   [头像 + 用户名 ▾]
    // ============================================================
    private HBox buildHeader() {
        Label logo = new Label("⬢ JFXium Admin");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Label themeBtn = new Label("🌙");
        themeBtn.setStyle("-fx-font-size: 16px; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
        themeBtn.setOnMouseClicked(e -> themeManager.toggleTheme());

        // 头像 + 用户名 + 小箭头作为下拉触发器
        StackPane avatar = AvatarAnt.create(initials(currentUser))
                .size(32)
                .build();
        Label userLabel = new Label(currentUser);
        userLabel.setStyle("-fx-padding: 0 0 0 8;");
        Label caret = new Label("▾");
        caret.setStyle("-fx-padding: 0 0 0 4; -fx-text-fill: -color-fg-muted;");

        HBox userTrigger = HBoxBuilder.create()
                .spacing(0)
                .align(Pos.CENTER)
                .padding(4, 12, 4, 8)
                .children(avatar, userLabel, caret)
                .build();
        userTrigger.setStyle("-fx-cursor: hand;");

        DropdownAnt.create()
                .trigger(userTrigger)
                .item("profile",  "个人中心", IconAnt.path(IconAnt.Path.USER))
                .item("settings", "账号设置", IconAnt.path(IconAnt.Path.SETTINGS))
                .divider()
                .item("logout",   "退出登录", IconAnt.path(IconAnt.Path.LOGOUT))
                .onSelect(key -> {
                    switch (key) {
                        case "profile"  -> router.go("profile");
                        case "settings" -> router.go("profile");  // 暂时复用
                        case "logout"   -> onLogout.run();
                    }
                })
                .build();   // DropdownAnt 内部已自动绑定 trigger 点击事件

        return HBoxBuilder.create()
                .spacing(0)
                .padding(8, 16, 8, 16)
                .align(Pos.CENTER_LEFT)
                .children(logo, Spacers.grow(), themeBtn, userTrigger)
                .build();
    }

    // ============================================================
    // 侧边菜单（M15：用 selectedKey + onSelect 让菜单跟随当前路由高亮）
    // ============================================================

    /** Sider 容器，引用持有以便切换路由后重建菜单。 */
    private javafx.scene.layout.HBox siderContainer;

    private VBox buildSider() {
        return (VBox) MenuAnt.create()
                .item("dashboard", "首页",     IconAnt.path(IconAnt.Path.DASHBOARD), () -> router.go("dashboard"))
                .item("user.list", "用户管理", IconAnt.path(IconAnt.Path.USERS),     () -> router.go("user.list"))
                .item("profile",   "个人中心", IconAnt.path(IconAnt.Path.USER),      () -> router.go("profile"))
                .divider()
                .item("404",       "404 演示", IconAnt.path(IconAnt.Path.FILE),      () -> router.go("404"))
                .selectedKey(router.currentKey())   // 当前路由高亮
                .build();
    }

    // ============================================================
    // 私有工具：取用户名前 1~2 个字符作为头像文字
    // ============================================================
    private static String initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String trimmed = name.trim();
        if (trimmed.codePointAt(0) > 127) {
            return trimmed.substring(0, 1);  // 中文取 1 字
        }
        return trimmed.length() >= 2 ? trimmed.substring(0, 2).toUpperCase()
                                     : trimmed.toUpperCase();
    }
}
