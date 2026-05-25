package org.openkawu.jfxium.demo.admin;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Admin 后台 Demo 主入口。
 *
 * <p>启动流程（M12.1 骨架，双 Stage）：</p>
 * <pre>
 *   start()
 *     └─ new LoginStage().show()           [400×520, 不可缩放]
 *           └─ 用户点登录
 *                 └─ loginStage.close()
 *                 └─ new AdminShell(name).show()   [1280×800, 可缩放]
 *                       └─ 用户点"退出登录"
 *                             └─ adminShell.close()
 *                             └─ new LoginStage().show()  // 回到登录
 * </pre>
 *
 * <p>设计取舍：登录窗与主壳是两个独立 Stage，每次切换都关掉旧的、打开新的。
 * 优点：每个 Stage 自己管理尺寸/可缩放等属性，不会互相污染。
 * Application 入口的 Stage 不用（关掉），由 LoginStage / AdminShell 自己创建。</p>
 */
public class AdminDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 不用 primary stage，登录用自己的小 Stage
        primaryStage.close();
        showLogin();
    }

    private void showLogin() {
        LoginStage[] holder = new LoginStage[1];   // 闭包引用
        holder[0] = new LoginStage(username -> {
            holder[0].close();
            showAdmin(username);
        });
        holder[0].show();
    }

    private void showAdmin(String username) {
        AdminShell[] holder = new AdminShell[1];
        holder[0] = new AdminShell(username, () -> {
            holder[0].close();
            showLogin();
        });
        holder[0].show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
