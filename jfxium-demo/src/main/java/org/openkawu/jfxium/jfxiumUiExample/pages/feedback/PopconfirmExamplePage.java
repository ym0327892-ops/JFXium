package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.control.Button;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.PopconfirmAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * Popconfirm 气泡确认框 —— 基础 / 自定义文案 / 回调。
 */
public class PopconfirmExamplePage extends VBoxAnt {

    public PopconfirmExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Popconfirm 气泡确认框")
                .description("点击元素弹出气泡式确认框，适合轻量级的二次确认场景。")
                .sections(basicSection(), customTextSection(), callbackSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Button trigger = ButtonAnt.create("删除").type(ButtonAnt.Type.DANGER).build();
        PopconfirmAnt.Popconfirm popconfirm = PopconfirmAnt.create()
                .title("确认删除？")
                .description("删除后不可恢复。")
                .target(trigger)
                .build();
        trigger.setOnAction(e -> popconfirm.show());
        String code = """
                Button trigger = ButtonAnt.create("删除").type(ButtonAnt.Type.DANGER).build();
                PopconfirmAnt.Popconfirm popconfirm = PopconfirmAnt.create()
                        .title("确认删除？")
                        .description("删除后不可恢复。")
                        .target(trigger)
                        .build();
                trigger.setOnAction(e -> popconfirm.show());
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "点击按钮弹出确认气泡，target 指定锚点元素。", code, trigger);
    }

    private Node customTextSection() {
        Button trigger = ButtonAnt.create("提交").type(ButtonAnt.Type.PRIMARY).build();
        PopconfirmAnt.Popconfirm popconfirm = PopconfirmAnt.create()
                .title("确认提交？")
                .okText("是的")
                .cancelText("再想想")
                .target(trigger)
                .build();
        trigger.setOnAction(e -> popconfirm.show());
        String code = """
                PopconfirmAnt.create()
                        .title("确认提交？")
                        .okText("是的")
                        .cancelText("再想想")
                        .target(trigger)
                        .build();
                """;
        return Demos.sectionWithCode("2. 自定义按钮文案",
                "okText / cancelText 自定义确认和取消按钮的文字。", code, trigger);
    }

    private Node callbackSection() {
        Button trigger = ButtonAnt.create("操作").build();
        PopconfirmAnt.Popconfirm popconfirm = PopconfirmAnt.create()
                .title("执行此操作？")
                .onConfirm(result -> MessageAnt.success("已确认"))
                .onCancel(result -> MessageAnt.info("已取消"))
                .target(trigger)
                .build();
        trigger.setOnAction(e -> popconfirm.show());
        String code = """
                PopconfirmAnt.create()
                        .title("执行此操作？")
                        .onConfirm(result -> MessageAnt.success("已确认"))
                        .onCancel(result -> MessageAnt.info("已取消"))
                        .target(trigger)
                        .build();
                """;
        return Demos.sectionWithCode("3. 确认/取消回调",
                "onConfirm / onCancel 处理用户选择。", code, trigger);
    }
}
