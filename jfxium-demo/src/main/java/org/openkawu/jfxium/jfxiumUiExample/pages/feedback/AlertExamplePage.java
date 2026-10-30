package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Window;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.overlay.AlertAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Alert 模态弹窗 —— 4 种类型 / 阻塞确认 / 非阻塞回调 / 自定义按钮文案。
 *
 * <p>AlertAnt = JavaFX Alert + JFXium 主题（.jfx-alert-dialog）；原生 {@code new Alert}
 * 只命中通用 .dialog-pane 基础主题，类型配色与图标需要 AlertAnt。</p>
 */
public class AlertExamplePage extends VBoxAnt {

    public AlertExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Alert 模态弹窗")
                .description("独立 Stage 模态对话框：信息/警告/错误/确认，支持阻塞等待与异步回调。")
                .sections(
                        typesSection(),
                        confirmSection(),
                        callbackSection(),
                        labelSection()
                )
                .padding(24)
                .build());
    }

    private Node typesSection() {
        Node demo = Demos.row(
                ButtonAnt.create("信息")
                        .onClick(e -> AlertAnt.info("信息", "这是一条信息提示。", ownerOf(e)).show())
                        .build(),
                ButtonAnt.create("警告").type(ButtonAnt.Type.WARNING)
                        .onClick(e -> AlertAnt.warning("警告", "请检查输入内容。", ownerOf(e)).show())
                        .build(),
                ButtonAnt.create("错误").type(ButtonAnt.Type.DANGER)
                        .onClick(e -> AlertAnt.error("错误", "保存失败，请重试。", ownerOf(e)).show())
                        .build(),
                ButtonAnt.create("确认").type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> AlertAnt.confirm("确认", "确定继续吗？", ownerOf(e)).show())
                        .build()
        );
        String code = """
                AlertAnt.info("信息", "内容", ownerWindow).show();
                AlertAnt.warning("警告", "内容", ownerWindow).show();
                AlertAnt.error("错误", "内容", ownerWindow).show();
                AlertAnt.confirm("确认", "内容", ownerWindow).show();
                """;
        return Demos.sectionWithCode("1. 四种类型",
                "INFORMATION / WARNING / ERROR / CONFIRMATION，header 与图标按类型配色。",
                code, demo);
    }

    private Node confirmSection() {
        Label result = TypographyAnt.text("等待选择…").build();
        ButtonAnt open = ButtonAnt.create("删除确认（阻塞）")
                .type(ButtonAnt.Type.DANGER)
                .onClick(e -> {
                    boolean ok = AlertAnt.confirm("删除确认", "此操作不可撤销。", ownerOf(e))
                            .okText("删除")
                            .cancelText("再想想")
                            .showAndWaitForOk();
                    result.setText(ok ? "结果：已确认删除" : "结果：已取消");
                })
                .build();
        Node demo = Demos.column(open, result);
        String code = """
                boolean ok = AlertAnt.confirm("删除确认", "此操作不可撤销。", ownerWindow)
                        .okText("删除")
                        .cancelText("再想想")
                        .showAndWaitForOk();
                if (ok) doDelete();
                """;
        return Demos.sectionWithCode("2. 阻塞确认",
                "showAndWaitForOk() 返回是否点击 OK；okText/cancelText 覆盖默认 i18n 文案。",
                code, demo);
    }

    private Node callbackSection() {
        Label result = TypographyAnt.text("等待选择…").build();
        ButtonAnt open = ButtonAnt.create("异步回调")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> AlertAnt.confirm("退出登录", "确定要退出吗？", ownerOf(e))
                        .onOk(() -> result.setText("onOk：准备退出"))
                        .onCancel(() -> result.setText("onCancel：继续留在当前页"))
                        .onResult(btn -> System.out.println("result=" + btn))
                        .show())
                .build();
        Node demo = Demos.column(open, result);
        String code = """
                AlertAnt.confirm("退出登录", "确定要退出吗？", ownerWindow)
                        .onOk(() -> logout())
                        .onCancel(() -> stay())
                        .onResult(btn -> log(btn))
                        .show();   // 非阻塞，弹窗立即返回
                """;
        return Demos.sectionWithCode("3. 非阻塞回调",
                "onOk / onCancel / onResult 在关闭时触发；show() 不阻塞当前线程。",
                code, demo);
    }

    private Node labelSection() {
        ButtonAnt open = ButtonAnt.create("自定义按钮文案")
                .onClick(e -> AlertAnt.info("提示", "按钮文案由调用方指定。", ownerOf(e))
                        .okText("知道了")
                        .show())
                .build();
        String code = """
                AlertAnt.info("提示", "按钮文案由调用方指定。", ownerWindow)
                        .okText("知道了")
                        .show();
                """;
        return Demos.sectionWithCode("4. 自定义按钮文案",
                "okText / cancelText 只影响当前弹窗；未设置时走 Messages i18n。",
                code, open);
    }

    private static Window ownerOf(javafx.event.Event event) {
        if (event != null && event.getSource() instanceof Node node && node.getScene() != null) {
            return node.getScene().getWindow();
        }
        return null;
    }
}
