package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import org.openkawu.jfxium.component.control.StatusBarAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * StatusBar 底部状态栏 —— 基础信息 / 进度条 / 可交互操作项 / 自定义节点。
 */
public class StatusBarExamplePage extends VBoxAnt {

    public StatusBarExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("StatusBar 状态栏")
                .description("底部状态栏，左侧信息 + 中间进度 + 右侧状态/操作项。"
                        + "对标 VS Code / IDEA 底栏，支持只读文本和可点击操作按钮。")
                .sections(
                        basicSection(),
                        progressSection(),
                        actionSection(),
                        inlineButtonSection(),
                        customSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = StatusBarAnt.create()
                .info("就绪")
                .status("UTF-8 | LF | Java")
                .build();
        String code = """
                StatusBarAnt.create()
                    .info("就绪")
                    .status("UTF-8 | LF | Java")
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础信息",
                "info(text) 左侧信息 + status(text) 右侧状态。系统化高度 28px，比工具栏（32px）矮 4px。",
                code, demo);
    }

    private Node progressSection() {
        Node demo = StatusBarAnt.create()
                .info("正在上传...")
                .progress(0.65)
                .status("65%")
                .build();
        String code = """
                StatusBarAnt.create()
                    .info("正在上传...")
                    .progress(0.65)
                    .status("65%")
                    .build();
                """;
        return Demos.sectionWithCode("2. 带进度条",
                "progress(value) 在中间栏插入原生 ProgressBar。传 -1 则隐藏。",
                code, demo);
    }

    private Node actionSection() {
        Node demo = StatusBarAnt.create()
                .info("就绪")
                .action("UTF-8", () -> System.out.println("切换编码"))
                .action("LF", () -> System.out.println("切换换行符"))
                .action("Git: main", () -> System.out.println("切换分支"))
                .status("行 42, 列 15")
                .build();
        String code = """
                StatusBarAnt.create()
                    .info("就绪")
                    .action("UTF-8", () -> chooseEncoding())
                    .action("LF", () -> changeLineEnding())
                    .action("Git: main", () -> switchBranch())
                    .status("行 42, 列 15")
                    .build();
                """;
        return Demos.sectionWithCode("3. 可点击操作项",
                "action(text, onClick) 添加内联文字按钮。CSS 剥掉按钮皮（透明背景、无边框），"
                        + "hover 高亮为 -color-accent-0，契合 28px 底栏高度。",
                code, demo);
    }

    private Node inlineButtonSection() {
        Button btn1 = new Button("UTF-8");
        btn1.getStyleClass().add(JfxStyles.BUTTON_INLINE);
        btn1.setOnAction(e -> System.out.println("UTF-8"));

        Button btn2 = new Button("LF");
        btn2.getStyleClass().add(JfxStyles.BUTTON_INLINE);
        btn2.setOnAction(e -> System.out.println("LF"));

        Button btn3 = new Button("Git: main");
        btn3.getStyleClass().add(JfxStyles.BUTTON_INLINE);
        btn3.setOnAction(e -> System.out.println("Git: main"));

        HBox row = new HBox(4, btn1, btn2, btn3);
        String code = """
                Button btn = new Button("UTF-8");
                btn.getStyleClass().add(JfxStyles.BUTTON_INLINE);
                btn.setOnAction(e -> chooseEncoding());
                """;
        return Demos.sectionWithCode("4. 独立内联按钮",
                "不依赖 StatusBarAnt —— 任何原生 Button 加 {} 就变成剥皮按钮（透明、无边框、微 padding、"
                        + "hover 高亮），可自由嵌入任意容器：HBox / FlowPane / 文本行等。",
                code.replace("{}", "JfxStyles.BUTTON_INLINE"),
                row);
    }

    private Node customSection() {
        Node demo = StatusBarAnt.create()
                .left(new Label("🔍"))
                .info("3 个问题")
                .action("⚠ 2 警告", () -> System.out.println("查看警告"))
                .action("✉ 通知", () -> System.out.println("查看通知"))
                .right(new Label("🔔"))
                .status("v1.0-SNAPSHOT")
                .build();
        String code = """
                StatusBarAnt.create()
                    .left(new Label("🔍"))
                    .info("3 个问题")
                    .action("⚠ 2 警告", () -> showWarnings())
                    .action("✉ 通知", () -> showNotifications())
                    .right(new Label("🔔"))
                    .status("v1.0-SNAPSHOT")
                    .build();
                """;
        return Demos.sectionWithCode("5. 自定义节点混排",
                "left(Node) / right(Node) 可插入任意节点（图标、分隔符等），"
                        + "与 info / action / status 自由混排。",
                code, demo);
    }
}
