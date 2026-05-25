package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.ModalAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * ModalAnt 组件展示页。
 *
 * <p>Modal 是覆盖式弹窗，需要 owner Node 触发，所以每个 Section 用 Button 演示。</p>
 */
public class ModalPage implements ShowcasePage {

    @Override public String   key()      { return "modal"; }
    @Override public String   title()    { return "Modal 对话框"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Modal 对话框");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("覆盖式弹窗，用于需要用户决策的强交互场景。点击下方按钮打开。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionConfirm(),
                        sectionWithCallback(),
                        sectionMaskClosable(),
                        sectionCustomFooter()
                )
                .build();
    }

    private Node sectionBasic() {
        Button trigger = ButtonAnt.create("打开 Modal")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> ModalAnt.create()
                        .title("基础对话框")
                        .content("这是 Modal 的内容区域。点击右上角 X / 点击遮罩 / 按 Esc 都可关闭。")
                        .build().open((Node) e.getSource()))
                .build();

        return ShowcaseSection.create()
                .title("基础用法")
                .description("最简弹窗：title + content + 默认 OK/Cancel footer")
                .demo(trigger)
                .code("""
                        ModalAnt.create()
                            .title("基础对话框")
                            .content("这是 Modal 的内容区域。")
                            .open(triggerNode);
                        """)
                .build();
    }

    private Node sectionConfirm() {
        Button trigger = ButtonAnt.create("删除（带确认）")
                .type(ButtonAnt.Type.DANGER)
                .onClick(e -> ModalAnt.create()
                        .title("确认删除？")
                        .content("此操作不可撤销，请谨慎！")
                        .okText("确认删除")
                        .cancelText("取消")
                        .onOk(() -> System.out.println("[Demo] 已删除"))
                        .build().open((Node) e.getSource()))
                .build();

        return ShowcaseSection.create()
                .title("确认对话框")
                .description("用 okText / cancelText 自定义按钮文字；onOk 监听确认事件")
                .demo(trigger)
                .code("""
                        ModalAnt.create()
                            .title("确认删除？")
                            .content("此操作不可撤销，请谨慎！")
                            .okText("确认删除")
                            .cancelText("取消")
                            .onOk(() -> doDelete())
                            .open(triggerNode);
                        """)
                .build();
    }

    private Node sectionWithCallback() {
        Label result = new Label("（未操作）");
        result.setStyle("-fx-text-fill: -color-fg-muted;");

        Button trigger = ButtonAnt.create("打开（带回调）")
                .onClick(e -> ModalAnt.create()
                        .title("操作结果回调")
                        .content("点击 OK 或 Cancel 看下方文字变化")
                        .onClose(confirmed -> result.setText(
                                "结果：" + (confirmed ? "✅ 已确认" : "❌ 已取消")))
                        .build().open((Node) e.getSource()))
                .build();

        HBox demo = new HBox(16, trigger, result);
        demo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        return ShowcaseSection.create()
                .title("关闭回调（区分 OK / Cancel）")
                .description("onClose(Consumer<Boolean>) 在弹窗关闭时触发；参数 true=点了 OK，false=取消/X/Esc")
                .demo(demo)
                .code("""
                        ModalAnt.create()
                            .title("操作结果")
                            .content("点击 OK 或 Cancel")
                            .onClose(confirmed -> {
                                if (confirmed) saveData();
                                else discardChanges();
                            })
                            .open(triggerNode);
                        """)
                .build();
    }

    private Node sectionMaskClosable() {
        Button trigger = ButtonAnt.create("不可点击遮罩关闭")
                .onClick(e -> ModalAnt.create()
                        .title("严肃操作")
                        .content("此 Modal 关闭遮罩点击；只能点 OK/Cancel/X 关闭")
                        .maskClosable(false)
                        .keyboard(false)        // 同时禁用 Esc 键关闭
                        .build().open((Node) e.getSource()))
                .build();

        return ShowcaseSection.create()
                .title("禁止遮罩关闭 + 禁用 Esc")
                .description("maskClosable(false) + keyboard(false)；常用于需要用户明确决策的关键操作")
                .demo(trigger)
                .code("""
                        ModalAnt.create()
                            .title("严肃操作")
                            .content("...")
                            .maskClosable(false)
                            .keyboard(false)        // 禁用 Esc 关闭
                            .open(triggerNode);
                        """)
                .build();
    }

    private Node sectionCustomFooter() {
        Button trigger = ButtonAnt.create("自定义 Footer")
                .onClick(e -> {
                    Button extra = ButtonAnt.create("第三方登录").type(ButtonAnt.Type.LINK).build();
                    Button cancel = ButtonAnt.create("取消").build();
                    Button submit = ButtonAnt.create("立即登录").type(ButtonAnt.Type.PRIMARY).build();
                    HBox footer = new HBox(8, extra,
                            new HBox() {{ setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(this, javafx.scene.layout.Priority.ALWAYS); }},
                            cancel, submit);
                    footer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    ModalAnt.create()
                            .title("登录")
                            .content("点击下方按钮看自定义 Footer 布局（左 link + 右两个按钮）")
                            .footer(footer)
                            .build().open((Node) e.getSource());
                })
                .build();

        return ShowcaseSection.create()
                .title("自定义 Footer")
                .description("footer(Node) 完全替换默认 OK/Cancel；适合复杂业务（多按钮 / 第三方登录入口等）")
                .demo(trigger)
                .code("""
                        HBox footer = new HBox(8,
                            ButtonAnt.create("第三方登录").type(ButtonAnt.Type.LINK).build(),
                            Spacers.grow(),
                            ButtonAnt.create("取消").build(),
                            ButtonAnt.create("立即登录").type(ButtonAnt.Type.PRIMARY).build()
                        );

                        ModalAnt.create()
                            .title("登录")
                            .content("...")
                            .footer(footer)
                            .open(triggerNode);
                        """)
                .build();
    }
}
