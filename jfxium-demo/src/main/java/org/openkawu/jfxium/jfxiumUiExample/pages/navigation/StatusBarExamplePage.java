package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.component.control.StatusBarAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * StatusBar 底部状态栏 —— 基础信息 / 进度条 / 可交互操作项 / 自定义节点。
 *
 * <p>默认基准是 28px，操作项使用 inline link 按钮，语义上按 ButtonAnt 的 LINK + XS 档落地。</p>
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
                        customSection(),
                        playgroundSection()
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
                .action("UTF-8", () -> MessageAnt.info("已点击: 切换编码 UTF-8"))
                .action("LF", () -> MessageAnt.info("已点击: 切换换行符 LF"))
                .action("Git: main", () -> MessageAnt.info("已点击: 切换分支 Git: main"))
                .status("行 42, 列 15")
                .build();
        String code = """
                StatusBarAnt.create()
                    .info("就绪")
                    .action("UTF-8", () -> MessageAnt.info("已点击: 切换编码 UTF-8"))
                    .action("LF", () -> MessageAnt.info("已点击: 切换换行符 LF"))
                    .action("Git: main", () -> MessageAnt.info("已点击: 切换分支 Git: main"))
                    .status("行 42, 列 15")
                    .build();
                """;
        return Demos.sectionWithCode("3. 可点击操作项",
                "action(text, onClick) 添加内联 link 按钮。CSS 剥掉按钮皮（透明背景、无边框），"
                        + "hover 高亮为 -color-accent-0，尺寸走 LINK + XS，契合 28px 底栏高度。",
                code, demo);
    }

    private Node inlineButtonSection() {
        ButtonAnt btn1 = ButtonAnt.create("UTF-8")
                .type(ButtonAnt.Type.LINK)
                .size(Size.XS)
                .styleClass(JfxStyles.BUTTON_INLINE)
                .build();
        btn1.setOnAction(e -> MessageAnt.info("已点击: UTF-8"));

        ButtonAnt btn2 = ButtonAnt.create("LF")
                .type(ButtonAnt.Type.LINK)
                .size(Size.XS)
                .styleClass(JfxStyles.BUTTON_INLINE)
                .build();
        btn2.setOnAction(e -> MessageAnt.info("已点击: LF"));

        ButtonAnt btn3 = ButtonAnt.create("Git: main")
                .type(ButtonAnt.Type.LINK)
                .size(Size.XS)
                .styleClass(JfxStyles.BUTTON_INLINE)
                .build();
        btn3.setOnAction(e -> MessageAnt.info("已点击: Git: main"));

        Node row = HBoxAnt.create().spacing(4).children(btn1, btn2, btn3);
        String code = """
                ButtonAnt btn = ButtonAnt.create("UTF-8")
                        .type(ButtonAnt.Type.LINK)
                        .size(Size.XS)
                        .styleClass(JfxStyles.BUTTON_INLINE)
                        .build();
                btn.setOnAction(e -> chooseEncoding());
                """;
        return Demos.sectionWithCode("4. 独立内联按钮",
                "不依赖 StatusBarAnt —— 任何 ButtonAnt + {} 就变成剥皮按钮（透明、无边框、微 padding、"
                        + "hover 高亮），正好适合 28px 状态栏，也可自由嵌入任意容器：HBox / FlowPane / 文本行等。",
                code.replace("{}", "JfxStyles.BUTTON_INLINE"),
                row);
    }

    private Node customSection() {
        Node demo = StatusBarAnt.create()
                .left(TypographyAnt.text("🔍").build())
                .info("3 个问题")
                .action("⚠ 2 警告", () -> MessageAnt.warning("查看 2 个警告"))
                .action("✉ 通知", () -> MessageAnt.info("查看通知"))
                .right(TypographyAnt.text("🔔").build())
                .status("v1.0-SNAPSHOT")
                .build();
        String code = """
                StatusBarAnt.create()
                    .left(TypographyAnt.text("🔍").build())
                    .info("3 个问题")
                    .action("⚠ 2 警告", () -> MessageAnt.warning("查看 2 个警告"))
                    .action("✉ 通知", () -> MessageAnt.info("查看通知"))
                    .right(TypographyAnt.text("🔔").build())
                    .status("v1.0-SNAPSHOT")
                    .build();
                """;
        return Demos.sectionWithCode("5. 自定义节点混排",
                "left(Node) / right(Node) 可插入任意节点（图标、分隔符等），"
                        + "与 info / action / status 自由混排。",
                code, demo);
    }

    /** 6. PlayGround：实时修改 info/status 文本 + 进度 + 显示/隐藏。 */
    private Node playgroundSection() {
        // 预先 build 一个 StatusBarAnt，初始 info + status + progress
        StatusBarAnt bar = StatusBarAnt.create()
                .info("就绪")
                .progress(0.5)
                .status("UTF-8 | LF")
                .build();

        Binder<String> infoText = PlayGround.binder("就绪");
        Binder<String> statusText = PlayGround.binder("UTF-8 | LF");
        Binder<String> progressVal = PlayGround.binder("0.5");
        Binder<String> showProgress = PlayGround.binder("on");

        Runnable apply = () -> {
            bar.updateInfo(infoText.get());
            bar.updateStatus(statusText.get());
            boolean on = "on".equals(showProgress.get());
            if (on) {
                double v;
                try { v = Double.parseDouble(progressVal.get()); }
                catch (NumberFormatException e) { v = 0.0; }
                v = Math.max(0.0, Math.min(1.0, v));
                bar.updateProgress(v);
            } else {
                bar.updateProgress(-1);
            }
        };

        return PlayGround.rebindModify(bar, apply, "信息 / 进度 / 状态",
                PlayGround.row("左侧信息", PlayGround.textField(infoText, "就绪", "左侧信息文本")),
                PlayGround.row("右侧状态", PlayGround.textField(statusText, "UTF-8 | LF", "右侧状态文本")),
                PlayGround.row("进度", PlayGround.textField(progressVal, "0.5", "0.0 ~ 1.0")),
                PlayGround.row("显示进度", PlayGround.segmented(showProgress,
                        PlayGround.entry("on", "显示"),
                        PlayGround.entry("off", "隐藏"))));
    }
}