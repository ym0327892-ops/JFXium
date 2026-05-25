package org.openkawu.jfxium.template;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.InputAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.i18n.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * LoginTemplate - 双栏 banner 登录页模板（M19.16）。
 *
 * <p><b>定位</b>：admin 后台最常见的登录页骨架——左侧品牌 banner（渐变+卖点），
 * 右侧账号密码表单。所有 admin 项目都用这个，抽成模板避免每次手写 ~300 行。</p>
 *
 * <h2>整体结构</h2>
 * <pre>
 * ┌──────────────────────┬──────────────────────┐
 * │   左 Banner（渐变）    │   右 表单             │
 * │   ⬢ Logo             │   登录账号            │
 * │   品牌名 / 标语        │   ────                │
 * │   卖点 ✓ ✓ ✓         │   [👤 用户名]         │
 * │                      │   [🔒 密码]           │
 * │   © 2026             │   ☐ 记住我  忘记密码   │
 * │                      │   [ 登 录 ]           │
 * │                      │   还没账号？注册       │
 * └──────────────────────┴──────────────────────┘
 * </pre>
 *
 * <h2>使用示例</h2>
 *
 * <h3>最简（默认 banner + 表单）</h3>
 * <pre>{@code
 * BorderPane login = LoginTemplate.create()
 *     .brandName("My Admin")
 *     .tagline("企业管理系统")
 *     .features("功能 1", "功能 2", "功能 3")
 *     .onSubmit((username, password) -> {
 *         if (auth(username, password)) {
 *             goMainPage();
 *         } else {
 *             // 显示错误
 *         }
 *     })
 *     .build();
 *
 * Scene scene = new Scene(login, 760, 520);
 * stage.setScene(scene);
 * }</pre>
 *
 * <h3>带"忘记密码 / 注册"链接</h3>
 * <pre>{@code
 * BorderPane login = LoginTemplate.create()
 *     .brandName("My Admin")
 *     .features("..."、"..."、"...")
 *     .showRememberMe(true)
 *     .onForgot(() -> openForgotPwdDialog())
 *     .onRegister(() -> openRegisterPage())
 *     .onSubmit((u, p) -> auth(u, p))
 *     .build();
 * }</pre>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>底层 BorderPane —— left 是 banner，center 是 form</li>
 *   <li>banner 用 CSS 变量渐变（{@code -color-accent-4 → emphasis → -color-accent-7}），切主题自动跟随</li>
 *   <li>表单用 PasswordField + 前置图标的复合输入框（HBox 自拼）</li>
 *   <li>所有可选回调（onForgot/onRegister）默认 null —— 不调用即不显示对应链接</li>
 *   <li>build() 诚实返回 BorderPane，调用方自己包 Scene/Stage</li>
 * </ul>
 */
public class LoginTemplate {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        // Banner 配置 —— null 表示用 i18n 默认值
        private String brandName = null;
        private String tagline = null;
        private final List<String> features = new ArrayList<>();
        private String copyright = null;
        private double bannerWidth = 360;

        // 表单配置 —— null 表示用 i18n 默认值
        private String formTitle = null;
        private String formSubtitle = null;
        private String usernamePlaceholder = null;
        private String passwordPlaceholder = null;
        private String submitText = null;
        private boolean showRememberMe = true;

        // 回调
        private BiConsumer<String, String> onSubmit;     // (username, password)
        private Runnable onForgot;
        private Runnable onRegister;

        private Builder() {}

        // ============================================================
        // Banner 配置
        // ============================================================

        public Builder brandName(String name) { this.brandName = name; return this; }
        public Builder tagline(String tagline) { this.tagline = tagline; return this; }
        public Builder feature(String text) { this.features.add(text); return this; }
        public Builder features(String... texts) {
            if (texts != null) for (String t : texts) features.add(t);
            return this;
        }
        public Builder copyright(String text) { this.copyright = text; return this; }
        public Builder bannerWidth(double width) { this.bannerWidth = width; return this; }

        // ============================================================
        // 表单配置
        // ============================================================

        public Builder formTitle(String title) { this.formTitle = title; return this; }
        public Builder formSubtitle(String subtitle) { this.formSubtitle = subtitle; return this; }
        public Builder usernamePlaceholder(String text) { this.usernamePlaceholder = text; return this; }
        public Builder passwordPlaceholder(String text) { this.passwordPlaceholder = text; return this; }
        public Builder submitText(String text) { this.submitText = text; return this; }
        public Builder showRememberMe(boolean show) { this.showRememberMe = show; return this; }

        // ============================================================
        // 回调
        // ============================================================

        /** 登录提交：{@code (username, password) -> ...}。必须设置。 */
        public Builder onSubmit(BiConsumer<String, String> onSubmit) {
            this.onSubmit = onSubmit;
            return this;
        }

        /** "忘记密码"链接点击回调；不设置则不显示该链接。 */
        public Builder onForgot(Runnable onForgot) {
            this.onForgot = onForgot;
            return this;
        }

        /** "立即注册"链接点击回调；不设置则不显示底部注册区域。 */
        public Builder onRegister(Runnable onRegister) {
            this.onRegister = onRegister;
            return this;
        }

        // ============================================================
        // 构建
        // ============================================================

        public BorderPane build() {
            BorderPane root = new BorderPane();
            root.setLeft(buildBanner());
            root.setCenter(buildForm());
            root.setStyle("-fx-background-color: -color-bg-default;");
            applyStyles(root);
            return root;
        }

        // ----------------- Banner -----------------

        private VBox buildBanner() {
            // 解析当前 Locale 文案（null 走 i18n 默认）
            String resolvedBrand = brandName != null ? brandName : Messages.get("login.brand_name");
            String resolvedTagline = tagline != null ? tagline : Messages.get("login.tagline");
            String resolvedCopyright = copyright != null ? copyright : Messages.get("login.copyright");

            // Logo box
            Region logoIcon = IconAnt.path(IconAnt.Path.DASHBOARD, 24);
            logoIcon.setStyle(logoIcon.getStyle() + " -fx-background-color: white;");
            StackPane logoBox = new StackPane(logoIcon);
            logoBox.setMinSize(48, 48);
            logoBox.setMaxSize(48, 48);
            logoBox.setStyle("-fx-background-color: rgba(255,255,255,0.18); -fx-background-radius: 8;");

            // 品牌名 / 标语
            Label name = new Label(resolvedBrand);
            name.setStyle("-fx-font-size: 22px; -fx-font-weight: 700; -fx-text-fill: white;");
            Label sub = new Label(resolvedTagline);
            sub.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.85);");

            VBox top = new VBox(16, logoBox, name, sub);
            top.setAlignment(Pos.TOP_LEFT);

            // 卖点
            VBox featuresBox = new VBox(10);
            for (String f : features) {
                featuresBox.getChildren().add(buildFeature(f));
            }

            // 版权
            Label copyrightLabel = new Label(resolvedCopyright);
            copyrightLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.6);");

            // 弹性占位
            Region s1 = new Region(); VBox.setVgrow(s1, Priority.ALWAYS);
            Region s2 = new Region(); VBox.setVgrow(s2, Priority.ALWAYS);

            VBox banner = new VBox(20, top, s1, featuresBox, s2, copyrightLabel);
            banner.setPadding(new Insets(40, 32, 32, 32));
            banner.setMinWidth(bannerWidth);
            banner.setMaxWidth(bannerWidth);
            banner.setPrefWidth(bannerWidth);
            banner.setAlignment(Pos.TOP_LEFT);
            banner.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, " +
                            "    -color-accent-4, -color-accent-emphasis 60%, -color-accent-7);"
            );
            return banner;
        }

        private HBox buildFeature(String text) {
            Label check = new Label("✓");
            check.setStyle("-fx-text-fill: rgba(255,255,255,0.95); -fx-font-size: 14px; -fx-font-weight: 700;");
            Label content = new Label(text);
            content.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 13px;");
            HBox row = new HBox(10, check, content);
            row.setAlignment(Pos.CENTER_LEFT);
            return row;
        }

        // ----------------- Form -----------------

        private VBox buildForm() {
            // 解析当前 Locale 文案
            String resolvedFormTitle = formTitle != null ? formTitle : Messages.get("login.form_title");
            String resolvedFormSubtitle = formSubtitle != null ? formSubtitle : Messages.get("login.form_subtitle");
            String resolvedUsername = usernamePlaceholder != null ? usernamePlaceholder : Messages.get("login.username_placeholder");
            String resolvedPassword = passwordPlaceholder != null ? passwordPlaceholder : Messages.get("login.password_placeholder");
            String resolvedSubmit = submitText != null ? submitText : Messages.get("login.submit");

            Label title = new Label(resolvedFormTitle);
            title.setStyle("-fx-font-size: 22px; -fx-font-weight: 700;");
            Label subtitle = new Label(resolvedFormSubtitle);
            subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-muted;");

            // 输入框
            TextField usernameField = InputAnt.create().placeholder(resolvedUsername).build();
            HBox usernameRow = inputWithIcon(IconAnt.Path.USER, usernameField);

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText(resolvedPassword);
            HBox passwordRow = inputWithIcon(IconAnt.Path.SETTINGS, passwordField);

            // 错误提示
            Label errorLabel = new Label("");
            errorLabel.setStyle("-fx-text-fill: -color-danger-emphasis; -fx-font-size: 12px;");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            // 记住我 + 忘记密码
            HBox rememberRow = null;
            CheckBox rememberMe = null;
            if (showRememberMe || onForgot != null) {
                rememberMe = new CheckBox(Messages.get("login.remember_me"));
                rememberMe.setStyle("-fx-font-size: 13px;");
                if (!showRememberMe) {
                    rememberMe.setVisible(false);
                    rememberMe.setManaged(false);
                }

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                spacer.setMaxWidth(Double.MAX_VALUE);

                rememberRow = new HBox(0);
                rememberRow.setAlignment(Pos.CENTER_LEFT);
                rememberRow.getChildren().addAll(rememberMe, spacer);

                if (onForgot != null) {
                    Hyperlink forgotLink = new Hyperlink(Messages.get("login.forgot"));
                    forgotLink.setStyle("-fx-font-size: 13px;");
                    forgotLink.setOnAction(e -> onForgot.run());
                    rememberRow.getChildren().add(forgotLink);
                }
            }

            // 登录按钮
            Button loginBtn = ButtonAnt.create(resolvedSubmit)
                    .type(ButtonAnt.Type.PRIMARY)
                    .onClick(e -> {
                        String u = usernameField.getText();
                        String p = passwordField.getText();
                        if (u == null || u.isBlank()) {
                            // 错误前缀 + 字段名走 i18n（resolvedUsername 已经是当前 Locale 文案）
                            errorLabel.setText(Messages.get("login.error_required_prefix") + resolvedUsername);
                            errorLabel.setVisible(true);
                            errorLabel.setManaged(true);
                            return;
                        }
                        errorLabel.setVisible(false);
                        errorLabel.setManaged(false);
                        if (onSubmit != null) {
                            onSubmit.accept(u, p);
                        }
                    })
                    .build();
            loginBtn.setMaxWidth(Double.MAX_VALUE);
            loginBtn.setPrefHeight(40);
            loginBtn.setStyle(loginBtn.getStyle() + " -fx-font-size: 14px;");

            // 组装
            VBox panel = new VBox(16);
            panel.setPadding(new Insets(60, 48, 40, 48));
            panel.setAlignment(Pos.TOP_LEFT);
            panel.getChildren().addAll(title, subtitle, vSpacer(8), usernameRow, passwordRow, errorLabel);
            if (rememberRow != null) panel.getChildren().add(rememberRow);
            panel.getChildren().addAll(vSpacer(4), loginBtn);

            // 底部注册（可选）
            if (onRegister != null) {
                Region bottomSpacer = new Region();
                VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
                Label noAccount = new Label(Messages.get("login.no_account"));
                noAccount.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px;");
                Hyperlink registerLink = new Hyperlink(Messages.get("login.register"));
                registerLink.setStyle("-fx-font-size: 13px;");
                registerLink.setOnAction(e -> onRegister.run());
                HBox registerRow = new HBox(0, noAccount, registerLink);
                registerRow.setAlignment(Pos.CENTER);
                panel.getChildren().addAll(bottomSpacer, registerRow);
            }

            return panel;
        }

        /** 输入框 + 前置图标的复合行（HBox 自拼边框，看起来像「集成式输入」）。 */
        private static HBox inputWithIcon(IconAnt.Path iconPath, javafx.scene.control.TextInputControl field) {
            Region icon = IconAnt.path(iconPath, 16);
            icon.getStyleClass().add("icon-muted");
            StackPane iconBox = new StackPane(icon);
            iconBox.setMinWidth(36);
            iconBox.setMaxWidth(36);
            iconBox.setAlignment(Pos.CENTER);

            HBox.setHgrow(field, Priority.ALWAYS);
            field.setMaxWidth(Double.MAX_VALUE);
            field.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;" +
                            "-fx-padding: 8 12 8 0;" +
                            "-fx-background-insets: 0;"
            );

            HBox row = new HBox(0, iconBox, field);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle(
                    "-fx-background-color: -color-bg-default;" +
                            "-fx-border-color: -color-border-default;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 6;" +
                            "-fx-background-radius: 6;"
            );
            row.setPrefHeight(40);
            return row;
        }

        private static Region vSpacer(double h) {
            Region r = new Region();
            r.setMinHeight(h);
            r.setMaxHeight(h);
            r.setPrefHeight(h);
            return r;
        }
    }
}
