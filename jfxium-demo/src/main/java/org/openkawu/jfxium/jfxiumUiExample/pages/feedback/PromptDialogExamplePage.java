package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.PromptDialogAnt;
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
                .onClick(e -> PromptDialogAnt.show(null)
                        .title("请输入名称")
                        .message("输入后点击确认保存")
                        .placeholder("例如：张三")
                        .onConfirm(name -> System.out.println("输入：" + name))
                        .build())
                .build();

        String code = """
                PromptDialogAnt.show(stage)
                    .title("请输入名称")
                    .message("输入后点击确认保存")
                    .placeholder("例如：张三")
                    .onConfirm(name -> {
                        System.out.println("输入：" + name);
                    })
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "show(stage) 创建；title() 设置标题；message() 设置提示文本；placeholder() 设置占位符；onConfirm() 获取输入值。",
                code, Demos.row(trigger, new javafx.scene.control.Label("（点击按钮弹出对话框）")));
    }

    private Node defaultValueSection() {
        Node trigger = ButtonAnt.create("弹出带默认值的输入框")
                .type(ButtonAnt.Type.ACCENT)
                .onClick(e -> PromptDialogAnt.show(null)
                        .title("修改备注")
                        .message("当前备注内容如下，可直接修改")
                        .defaultValue("原始备注信息")
                        .onConfirm(text -> System.out.println("修改为：" + text))
                        .build())
                .build();

        String code = """
                PromptDialogAnt.show(stage)
                    .title("修改备注")
                    .message("当前备注内容如下")
                    .defaultValue("原始备注信息")
                    .onConfirm(text -> assert text.startsWith("原始"))
                    .build();
                """;
        return Demos.sectionWithCode("2. 带默认值",
                "defaultValue(String) 为输入框预填默认值，适合编辑已有内容的场景。",
                code, trigger);
    }

    private Node customButtonSection() {
        Node demo = Demos.row(
                ButtonAnt.create("自定义按钮文字")
                        .type(ButtonAnt.Type.ACCENT)
                        .onClick(e -> PromptDialogAnt.show(null)
                                .title("确认删除")
                                .message("此操作不可撤销，请输入确认信息")
                                .placeholder("输入 DELETE 确认")
                                .okText("确认删除")
                                .cancelText("我再想想")
                                .onConfirm(text -> System.out.println("确认：" + text))
                                .onCancel(() -> System.out.println("取消删除"))
                                .build())
                        .build(),
                ButtonAnt.create("仅确认按钮")
                        .type(ButtonAnt.Type.ACCENT)
                        .onClick(e -> PromptDialogAnt.show(null)
                                .title("温馨提示")
                                .message("这是一个只有确认按钮的弹框")
                                .okText("我知道了")
                                .onConfirm(text -> System.out.println("收到"))
                                .build())
                        .build()
        );
        String code = """
                PromptDialogAnt.show(stage)
                    .title("确认删除")
                    .message("此操作不可撤销")
                    .okText("确认删除")
                    .cancelText("我再想想")
                    .onConfirm(...)
                    .onCancel(() -> System.out.println("取消"))
                    .build();
                """;
        return Demos.sectionWithCode("3. 自定义按钮文字与取消回调",
                "okText()/cancelText() 自定义按钮文字；onCancel() 设置取消回调。",
                code, demo);
    }
}
