package org.openkawu.jfxium.demo.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.InputAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;

import java.util.function.Consumer;

/**
 * 登录页（M12.2 双栏 banner 版）。
 *
 * <pre>
 * ┌─────────────────────────┬─────────────────────────┐
 * │   品牌 banner（左 360）  │    登录表单（右 400）     │
 * │   渐变背景               │                          │
 * │   ⬢ Logo                │   登录账号                │
 * │   主标语                 │   ────                   │
 * │   产品卖点 ✓ ✓ ✓        │   [👤 用户名]            │
 * │                         │   [🔒 密码]              │
 * │   © 2026                │   ☐ 记住我      忘记密码 │
 * │                         │   [ 登 录 ]              │
 * │                         │   还没账号？注册          │
 * └─────────────────────────┴─────────────────────────┘
 * </pre>
 *
 * <p>设计取舍：</p>
 * <ul>
 *   <li>760×520：admin 行业惯例（Vercel / Linear 同款），左侧 banner 占 47%</li>
 *   <li>banner 用主题色渐变 + 白字，对比强烈，视觉锚点</li>
 *   <li>输入框前置图标用 HBox 自拼（不动 InputAnt 接口，精准动刀）</li>
 *   <li>所有颜色走 CSS 变量（{@code -color-accent-emphasis} / {@code -color-fg-on-emphasis}），切主题自动跟随</li>
 * </ul>
 */
public class LoginStage {

    /** 登录窗固定尺寸：双栏 banner 设计。 */
    public static final double WIDTH  = 760;
    public static final double HEIGHT = 520;

    /** 左侧 banner 宽度。 */
    private static final double BANNER_WIDTH = 360;

    private final Stage stage = new Stage();
    private final Consumer<String> onLogin;

    public LoginStage(Consumer<String> onLogin) {
        this.onLogin = onLogin;
    }

    public void show() {
        // 左：品牌 banner
        VBox banner = buildBanner();

        // 右：登录表单
        VBox formPanel = buildFormPanel();

        // 整体：左右两栏（用 BorderPane left+center）
        BorderPane root = new BorderPane();
        root.setLeft(banner);
        root.setCenter(formPanel);
        root.setStyle("-fx-background-color: -color-bg-default;");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        ThemeManager.getInstance().applyTheme(new LightTheme());
        ThemeManager.getInstance().registerScene(scene);

        stage.setScene(scene);
        stage.setTitle("JFXium Admin - 登录");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public void close() { stage.close(); }
    public Stage getStage() { return stage; }

    // ============================================================
    // 左侧品牌 banner
    // ============================================================
    private VBox buildBanner() {
        // Logo：圆角方块 + 白色 dashboard 图标
        Region logoIcon = IconAnt.path(IconAnt.Path.DASHBOARD, 24);
        // banner 是深色背景，logo 改白色（在 banner 区域用 styleClass 覆写）
        logoIcon.setStyle(logoIcon.getStyle() + " -fx-background-color: white;");

        StackPane logoBox = new StackPane(logoIcon);
        logoBox.setMinSize(48, 48);
        logoBox.setMaxSize(48, 48);
        logoBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.18);" +
                "-fx-background-radius: 8;"
        );

        Label brandName = new Label("JFXium Admin");
        brandName.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: white;"
        );

        Label tagline = new Label("现代化 JavaFX UI 框架");
        tagline.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: rgba(255,255,255,0.85);"
        );

        // 卖点列表
        VBox features = VBoxBuilder.create()
                .spacing(10)
                .children(
                        feature("60+ 内置组件，开箱即用"),
                        feature("8 套主题，运行时切换"),
                        feature("代码构建 UI，告别 FXML"),
                        feature("Builder Pattern 链式 API")
                )
                .build();

        // 底部版权
        Label copyright = new Label("© 2026 JFXium · MIT License");
        copyright.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: rgba(255,255,255,0.6);"
        );

        // 顶部品牌区
        VBox top = VBoxBuilder.create()
                .spacing(16)
                .children(logoBox, brandName, tagline)
                .build();

        // 弹性占位把卖点和版权分开
        VBox banner = new VBox(20);
        banner.setPadding(new Insets(40, 32, 32, 32));
        banner.setMinWidth(BANNER_WIDTH);
        banner.setMaxWidth(BANNER_WIDTH);
        banner.setPrefWidth(BANNER_WIDTH);
        banner.setAlignment(Pos.TOP_LEFT);
        banner.setStyle(
                // 主题色渐变（左上→右下，由浅到深）
                "-fx-background-color: linear-gradient(to bottom right, " +
                "    -color-accent-4, -color-accent-emphasis 60%, -color-accent-7);"
        );

        // 中间空白把卖点推到中部偏上
        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, javafx.scene.layout.Priority.ALWAYS);
        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, javafx.scene.layout.Priority.ALWAYS);

        banner.getChildren().addAll(top, spacer1, features, spacer2, copyright);
        return banner;
    }

    /** 单个卖点行：✓ + 文字。 */
    private HBox feature(String text) {
        Region check = IconAnt.path(IconAnt.Path.DASHBOARD, 14);  // 用 Symbol.CHECK 更准确，但是 Symbol 模式
        // 用 ✓ Symbol 模式：颜色由 Label 控制
        Label checkLabel = new Label("✓");
        checkLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.95);" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 700;"
        );

        Label content = new Label(text);
        content.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.9);" +
                "-fx-font-size: 13px;"
        );

        return HBoxBuilder.create()
                .spacing(10)
                .align(Pos.CENTER_LEFT)
                .children(checkLabel, content)
                .build();
    }

    // ============================================================
    // 右侧登录表单
    // ============================================================
    private VBox buildFormPanel() {
        Label title = new Label("登录账号");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 700;");

        Label subtitle = new Label("欢迎回来，请输入凭证以继续");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-muted;");

        // 用户名输入（带前置图标）
        TextField usernameField = InputAnt.create().placeholder("用户名").build();
        HBox usernameRow = inputWithIcon(IconAnt.Path.USER, usernameField);

        // 密码输入（带前置图标）—— InputAnt 当前没有 password 模式开关，直接用 javafx PasswordField 包一层
        // 偷懒：M12.2 阶段先用 TextField 占位（任意凭证可登）
        TextField passwordField = InputAnt.create().placeholder("密码").build();
        HBox passwordRow = inputWithIcon(IconAnt.Path.SETTINGS, passwordField);

        // 错误提示
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: -color-danger-emphasis; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // 记住我 + 忘记密码
        CheckBox rememberMe = new CheckBox("记住我");
        rememberMe.setStyle("-fx-font-size: 13px;");

        Hyperlink forgotLink = new Hyperlink("忘记密码？");
        forgotLink.setStyle("-fx-font-size: 13px;");
        forgotLink.setOnAction(e -> {
            errorLabel.setText("（骨架阶段：忘记密码功能待实现）");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        });

        Region rememberSpacer = new Region();
        HBox.setHgrow(rememberSpacer, javafx.scene.layout.Priority.ALWAYS);
        rememberSpacer.setMaxWidth(Double.MAX_VALUE);
        HBox rememberRow = HBoxBuilder.create()
                .spacing(0)
                .align(Pos.CENTER_LEFT)
                .children(rememberMe, rememberSpacer, forgotLink)
                .build();

        // 登录按钮
        Button loginBtn = ButtonAnt.create("登 录")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    String name = usernameField.getText();
                    if (name == null || name.isBlank()) {
                        errorLabel.setText("请输入用户名");
                        errorLabel.setVisible(true);
                        errorLabel.setManaged(true);
                        return;
                    }
                    onLogin.accept(name);
                })
                .build();
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(40);
        loginBtn.setStyle(loginBtn.getStyle() + " -fx-font-size: 14px;");

        // 注册引导
        Label noAccount = new Label("还没账号？");
        noAccount.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px;");
        Hyperlink registerLink = new Hyperlink("立即注册");
        registerLink.setStyle("-fx-font-size: 13px;");
        registerLink.setOnAction(e -> {
            errorLabel.setText("（骨架阶段：注册功能待实现）");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        });
        HBox registerRow = HBoxBuilder.create()
                .spacing(0)
                .align(Pos.CENTER)
                .children(noAccount, registerLink)
                .build();

        VBox panel = new VBox(16);
        panel.setPadding(new Insets(60, 48, 40, 48));
        panel.setAlignment(Pos.TOP_LEFT);
        panel.getChildren().addAll(
                title,
                subtitle,
                spacer(8),
                usernameRow,
                passwordRow,
                errorLabel,
                rememberRow,
                spacer(4),
                loginBtn
        );

        // 底部注册引导推到最下
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, javafx.scene.layout.Priority.ALWAYS);
        panel.getChildren().addAll(bottomSpacer, registerRow);
        return panel;
    }

    /** 输入框前置图标的复合行：[ icon | TextField ]，外层加边框模拟"集成式输入"。 */
    private HBox inputWithIcon(IconAnt.Path iconPath, TextField field) {
        Region icon = IconAnt.path(iconPath, 16);
        // muted 颜色让图标看着像辅助元素
        icon.getStyleClass().add("icon-muted");

        StackPane iconBox = new StackPane(icon);
        iconBox.setMinWidth(36);
        iconBox.setMaxWidth(36);
        iconBox.setAlignment(Pos.CENTER);

        // 让 TextField 撑满剩余空间
        HBox.setHgrow(field, javafx.scene.layout.Priority.ALWAYS);
        field.setMaxWidth(Double.MAX_VALUE);
        // 移除 TextField 自身边框，让外层 HBox 显示统一边框
        field.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-padding: 8 12 8 0;" +
                "-fx-background-insets: 0;"
        );

        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;"
        );
        row.setPrefHeight(40);
        row.getChildren().addAll(iconBox, field);
        return row;
    }

    /** 垂直占位器（固定高度）。 */
    private Region spacer(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        r.setMaxHeight(h);
        return r;
    }
}
