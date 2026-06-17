package org.openkawu.jfxium.template;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * LoginTemplate - 双栏 banner 登录页模板（M19.16，M19.39 LESS 化）。
 *
 * <p><b>定位</b>：admin 后台最常见的登录页骨架——左侧品牌 banner（渐变 + 卖点），
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
 * <pre>{@code
 * BorderPane login = LoginTemplate.create()
 *     .brandName("My Admin")
 *     .features("企业级权限", "审计日志", "SSO")
 *     .onSubmit((username, password) -> auth(username, password))
 *     .onForgot(() -> openForgotPwdDialog())   // 不调用 → 不显示
 *     .onRegister(() -> openRegisterPage())    // 不调用 → 不显示
 *     .build();
 *
 * Scene scene = new Scene(login, 760, 520);
 * stage.setScene(scene);
 * }</pre>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>底层 BorderPane —— left 是 banner，center 是 form</li>
 *   <li>所有视觉样式（颜色 / 字号 / 边框 / 渐变）100% 走 LESS（{@code .login-template-*} 选择器）</li>
 *   <li>banner 用 CSS 变量渐变，切主题自动跟随</li>
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
        public Builder feature(String text) {
            if (text != null) {
                this.features.add(text);
            }
            return this;
        }
        public Builder features(String... texts) {
            if (texts != null) {
                for (String t : texts) {
                    if (t != null) {
                        features.add(t);
                    }
                }
            }
            return this;
        }
        public Builder copyright(String text) { this.copyright = text; return this; }
        public Builder bannerWidth(double width) {
            this.bannerWidth = Double.isFinite(width) && width > 0 ? width : 360;
            return this;
        }

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
            root.getStyleClass().add(JfxStyles.LOGIN_ROOT);
            root.setLeft(buildBanner());
            root.setCenter(buildForm());
            applyStyles(root);
            return root;
        }

        // ----------------- Banner -----------------

        private VBox buildBanner() {
            // 解析当前 Locale 文案（null 走 i18n 默认）
            String resolvedBrand = brandName != null ? brandName : Messages.get("login.brand_name");
            String resolvedTagline = tagline != null ? tagline : Messages.get("login.tagline");
            String resolvedCopyright = copyright != null ? copyright : Messages.get("login.copyright");
            double resolvedBannerWidth = Double.isFinite(bannerWidth) && bannerWidth > 0 ? bannerWidth : 360;

            // Logo box（图标白色，背景半透明白）
            Region logoIcon = IconAnt.path(IconAnt.Path.DASHBOARD, 24);
            // 图标颜色由 LESS 控制（.login-template-banner-logo .jfx-icon-path）
            StackPane logoBox = new StackPane(logoIcon);
            logoBox.getStyleClass().add(JfxStyles.LOGIN_BANNER_LOGO_BOX);
            logoBox.setMinSize(48, 48);
            logoBox.setMaxSize(48, 48);

            // 品牌名 / 标语
            Label name = new Label(resolvedBrand);
            name.getStyleClass().add(JfxStyles.LOGIN_BANNER_BRAND);

            Label sub = new Label(resolvedTagline);
            sub.getStyleClass().add(JfxStyles.LOGIN_BANNER_TAGLINE);

            VBox top = new VBox(logoBox, name, sub);
            top.getStyleClass().add(JfxStyles.LOGIN_BANNER_BRAND_BOX);
            top.setAlignment(Pos.TOP_LEFT);

            // 卖点
            VBox featuresBox = new VBox();
            featuresBox.getStyleClass().add(JfxStyles.LOGIN_BANNER_FEATURES);
            for (String f : features) {
                featuresBox.getChildren().add(buildFeature(f));
            }

            // 版权
            Label copyrightLabel = new Label(resolvedCopyright);
            copyrightLabel.getStyleClass().add(JfxStyles.LOGIN_BANNER_COPYRIGHT);

            // 弹性占位
            Region s1 = new Region(); VBox.setVgrow(s1, Priority.ALWAYS);
            Region s2 = new Region(); VBox.setVgrow(s2, Priority.ALWAYS);

            VBox banner = new VBox(top, s1, featuresBox, s2, copyrightLabel);
            banner.getStyleClass().add(JfxStyles.LOGIN_BANNER);
            banner.setMinWidth(resolvedBannerWidth);
            banner.setMaxWidth(resolvedBannerWidth);
            banner.setPrefWidth(resolvedBannerWidth);
            banner.setAlignment(Pos.TOP_LEFT);
            return banner;
        }

        private HBox buildFeature(String text) {
            // BUG #132 修复：原来 new Label("✓") 是裸 Unicode 字符当图标，现走 IconAnt.symbol 统一收口
            Node check = IconAnt.symbol(IconAnt.Symbol.CHECK, 14);
            // 给 Node 套个 Class，让 LESS 走 LOGIN_BANNER_FEATURE_CHECK 样式
            check.getStyleClass().add(JfxStyles.LOGIN_BANNER_FEATURE_CHECK);

            Label content = new Label(text != null ? text : "");
            content.getStyleClass().add(JfxStyles.LOGIN_BANNER_FEATURE_TEXT);

            HBox row = new HBox(check, content);
            row.getStyleClass().add(JfxStyles.LOGIN_BANNER_FEATURE_ROW);
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
            title.getStyleClass().add(JfxStyles.LOGIN_FORM_TITLE);

            Label subtitle = new Label(resolvedFormSubtitle);
            subtitle.getStyleClass().add(JfxStyles.LOGIN_FORM_SUBTITLE);

            // 输入框（前置图标 + 文本框，看起来像「集成式输入」）
            TextField usernameField = InputAnt.create().placeholder(resolvedUsername).build();
            HBox usernameRow = inputWithIcon(IconAnt.Path.USER, usernameField);

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText(resolvedPassword);
            HBox passwordRow = inputWithIcon(IconAnt.Path.SETTINGS, passwordField);

            // 错误提示（默认隐藏）
            Label errorLabel = new Label("");
            errorLabel.getStyleClass().add(JfxStyles.LOGIN_FORM_ERROR);
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            // 记住我 + 忘记密码
            HBox rememberRow = null;
            if (showRememberMe || onForgot != null) {
                CheckBox rememberMe = new CheckBox(Messages.get("login.remember_me"));
                rememberMe.getStyleClass().add(JfxStyles.LOGIN_FORM_REMEMBER);
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
                    forgotLink.getStyleClass().addAll(JfxStyles.LOGIN_FORM_LINK_SMALL, JfxStyles.HYPERLINK);
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
            loginBtn.getStyleClass().add(JfxStyles.LOGIN_FORM_SUBMIT);
            loginBtn.setMaxWidth(Double.MAX_VALUE);

            // 组装
            VBox panel = new VBox();
            panel.getStyleClass().add(JfxStyles.LOGIN_FORM);
            panel.setAlignment(Pos.TOP_LEFT);
            panel.getChildren().addAll(title, subtitle, vSpacer(8), usernameRow, passwordRow, errorLabel);
            if (rememberRow != null) panel.getChildren().add(rememberRow);
            panel.getChildren().addAll(vSpacer(4), loginBtn);

            // 底部注册（可选）
            if (onRegister != null) {
                Region bottomSpacer = new Region();
                VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

                Label noAccount = new Label(Messages.get("login.no_account"));
                noAccount.getStyleClass().add(JfxStyles.LOGIN_FORM_NO_ACCOUNT);

                Hyperlink registerLink = new Hyperlink(Messages.get("login.register"));
                registerLink.getStyleClass().addAll(JfxStyles.LOGIN_FORM_LINK_SMALL, JfxStyles.HYPERLINK);
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
            icon.getStyleClass().add(JfxStyles.ICON_MUTED);
            StackPane iconBox = new StackPane(icon);
            iconBox.getStyleClass().add(JfxStyles.LOGIN_FORM_INPUT_ICON_BOX);
            iconBox.setAlignment(Pos.CENTER);

            HBox.setHgrow(field, Priority.ALWAYS);
            field.setMaxWidth(Double.MAX_VALUE);
            // 字段挂 styleClass，由 LESS 把背景 / 边框抹掉（看起来像无边框输入）
            field.getStyleClass().add(JfxStyles.LOGIN_FORM_INPUT_FIELD);

            HBox row = new HBox(0, iconBox, field);
            row.getStyleClass().add(JfxStyles.LOGIN_FORM_INPUT_ROW);
            row.setAlignment(Pos.CENTER_LEFT);
            return row;
        }

        private static Region vSpacer(double h) {
            double size = Double.isFinite(h) ? Math.max(0, h) : 0;
            Region r = new Region();
            r.setMinHeight(size);
            r.setMaxHeight(size);
            r.setPrefHeight(size);
            return r;
        }
    }
}
