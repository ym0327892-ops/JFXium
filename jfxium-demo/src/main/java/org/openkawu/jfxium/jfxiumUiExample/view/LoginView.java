package org.openkawu.jfxium.jfxiumUiExample.view;

import javafx.scene.layout.BorderPane;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.template.LoginTemplate;

import java.util.function.Consumer;

/**
 * 登录视图 —— 只负责构建登录页 UI，不碰 Stage / Scene。
 *
 * <h2>职责</h2>
 * <ul>
 *   <li>{@link #build()} 返回一个根 {@link BorderPane}（直接来自 LoginTemplate）</li>
 *   <li>用户点登录 → 简单校验 → 通过 {@code onLoginSuccess} 回调通知 App</li>
 *   <li>窗口生命周期 / 主题 / Scene 切换由 {@code JfxiumUiExampleApp} 统一管理</li>
 * </ul>
 *
 * <h2>典型用法</h2>
 * <pre>{@code
 * Region root = new LoginView(username -> showMain(username)).build();
 * stage.setScene(new Scene(root, 760, 520));
 * }</pre>
 */
public class LoginView {

    private final Consumer<String> onLoginSuccess;

    public LoginView(Consumer<String> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    /** 构建登录页 root —— App 拿去 setScene。 */
    public BorderPane build() {
        return LoginTemplate.create()
                .brandName("JFXium UI Example")
                .tagline("基于 JFXium UI 库的示例项目")
                .features("现代化 JavaFX 组件库", "对标 Ant Design 设计", "8 套主题随心切")
                .copyright("© 2026 JFXium")
                .formTitle("登录账号")
                .formSubtitle("欢迎回来，请输入账号密码")
                .usernamePlaceholder("用户名（任意非空）")
                .passwordPlaceholder("密码（任意非空）")
                .submitText("登 录")
                .onSubmit(this::handleLogin)
                .onForgot(() -> MessageAnt.info("演示项目暂未实现"))
                .onRegister(() -> MessageAnt.info("演示项目暂未实现"))
                .build();
    }

    /** 演示用的登录处理：非空校验通过即视为成功。 */
    private void handleLogin(String username, String password) {
        if (username == null || username.isBlank()) {
            MessageAnt.error("请输入用户名");
            return;
        }
        if (password == null || password.isBlank()) {
            MessageAnt.error("请输入密码");
            return;
        }

        MessageAnt.success("欢迎，" + username);
        if (onLoginSuccess != null) {
            onLoginSuccess.accept(username);
        }
    }
}
