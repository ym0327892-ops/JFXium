package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.PromptDialogAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * PromptDialog 输入弹框 —— 基础 prompt / 默认值 / 自定义按钮 / 取消回调。
 */
public class PromptDialogExamplePage extends VBoxAnt {

    public PromptDialogExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("PromptDialog 输入弹框")
                .description("轻量级输入弹框，用于快速获取用户输入（文本），支持确认/取消回调、默认值、自定义按钮文字。")
                .sections(
                        basicSection(),
                        defaultValueSection(),
                        customButtonSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node trigger = ButtonAnt.create("弹出输入框")
                .type(ButtonAnt.Type.ACCENT)
                .onClick(e -> PromptDialogAnt.create()
                        .title("请输入名称")
                        .message("输入后点击确认保存")
                        .placeholder("例如：张三")
                        .onConfirm(name -> MessageAnt.success("输入：" + name))
                        .build()
                        .open((Node) e.getSource()))
                .build();

        String code = """
                PromptDialogAnt.create()
                    .title("请输入名称")
                    .message("输入后点击确认保存")
                    .placeholder("例如：张三")
                    .onConfirm(name -> {
                        MessageAnt.success("输入：" + name);
                    })
                    .build()
                    .open(ownerNode);
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "create() 构建；title() 设置标题；message() 设置提示文本；placeholder() 设置占位符；onConfirm() 获取输入值；build().open(node) 显式打开。",
                code, Demos.row(trigger, new javafx.scene.control.Label("（点击按钮弹出对话框）")));
    }

    private Node defaultValueSection() {
        Node trigger = ButtonAnt.create("弹出带默认值的输入框")
                .type(ButtonAnt.Type.ACCENT)
                .onClick(e -> PromptDialogAnt.create()
                        .title("修改备注")
                        .message("当前备注内容如下，可直接修改")
                        .defaultValue("原始备注信息")
                        .onConfirm(text -> MessageAnt.success("修改为：" + text))
                        .build()
                        .open((Node) e.getSource()))
                .build();

        String code = """
                PromptDialogAnt.create()
                    .title("修改备注")
                    .message("当前备注内容如下")
                    .defaultValue("原始备注信息")
                    .onConfirm(text -> assert text.startsWith("原始"))
                    .build()
                    .open(ownerNode);
                """;
        return Demos.sectionWithCode("2. 带默认值",
                "defaultValue(String) 为输入框预填默认值，适合编辑已有内容的场景。",
                code, trigger);
    }

    private Node customButtonSection() {
        Node demo = Demos.row(
                ButtonAnt.create("自定义按钮文字")
                        .type(ButtonAnt.Type.ACCENT)
                        .onClick(e -> PromptDialogAnt.create()
                                .title("确认删除")
                                .message("此操作不可撤销，请输入确认信息")
                                .placeholder("输入 DELETE 确认")
                                .okText("确认删除")
                                .cancelText("我再想想")
                                .onConfirm(text -> MessageAnt.warning("确认：" + text))
                                .onCancel(() -> MessageAnt.info("取消删除"))
                                .build()
                                .open((Node) e.getSource()))
                        .build(),
                ButtonAnt.create("仅确认按钮")
                        .type(ButtonAnt.Type.ACCENT)
                        .onClick(e -> PromptDialogAnt.create()
                                .title("温馨提示")
                                .message("这是一个只有确认按钮的弹框")
                                .okText("我知道了")
                                .onConfirm(text -> MessageAnt.success("收到"))
                                .build()
                                .open((Node) e.getSource()))
                        .build()
        );
        String code = """
                PromptDialogAnt.create()
                    .title("确认删除")
                    .message("此操作不可撤销")
                    .okText("确认删除")
                    .cancelText("我再想想")
                    .onConfirm(...)
                    .onCancel(() -> MessageAnt.info("已取消"))
                    .build()
                    .open(ownerNode);
                """;
        return Demos.sectionWithCode("3. 自定义按钮文字与取消回调",
                "okText()/cancelText() 自定义按钮文字；onCancel() 设置取消回调。",
                code, demo);
    }
}
