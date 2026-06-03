package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.SelectableTextAnt;

/**
 * SelectableText 可选文本 —— 基础 / 多行折行 / 语义类型。
 */
public class SelectableTextExamplePage extends VBoxAnt {

    public SelectableTextExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("SelectableText 可选文本")
                .description("看起来像普通文本，但可拖动选中并 Ctrl+C 复制，用于展示可复制的数据。")
                .sections(basicSection(), multilineSection(), typeSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.column(
                SelectableTextAnt.create("zhangsan@example.com").build(),
                SelectableTextAnt.create("订单号：JFX-2024-000123").build()
        );
        String code = """
                SelectableTextAnt.create("zhangsan@example.com").build();
                SelectableTextAnt.create("订单号：JFX-2024-000123").build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "单行只读文本，拖选后可复制。", code, demo);
    }

    private Node multilineSection() {
        Node demo = SelectableTextAnt.create(
                        "java.lang.NullPointerException: Cannot invoke method on null object\n"
                        + "    at com.example.Service.handle(Service.java:42)\n"
                        + "    at com.example.Controller.run(Controller.java:18)")
                .multiline(true)
                .wrap(true)
                .bordered(true)
                .maxWidth(480)
                .build();
        String code = """
                SelectableTextAnt.create(stackTrace)
                        .multiline(true)
                        .wrap(true)
                        .bordered(true)
                        .maxWidth(480)
                        .build();
                """;
        return Demos.sectionWithCode("2. 多行 + 折行",
                "multiline + wrap 展示多行内容，bordered 给出卡片式边界。", code, demo);
    }

    private Node typeSection() {
        Node demo = Demos.column(
                SelectableTextAnt.create("仅前端可见，不会上报")
                        .type(SelectableTextAnt.Type.SECONDARY).build(),
                SelectableTextAnt.create("校验通过")
                        .type(SelectableTextAnt.Type.SUCCESS).build(),
                SelectableTextAnt.create("Token 已过期，请重新登录")
                        .type(SelectableTextAnt.Type.ERROR).build()
        );
        String code = """
                SelectableTextAnt.create("仅前端可见，不会上报")
                        .type(SelectableTextAnt.Type.SECONDARY).build();
                SelectableTextAnt.create("校验通过")
                        .type(SelectableTextAnt.Type.SUCCESS).build();
                SelectableTextAnt.create("Token 已过期，请重新登录")
                        .type(SelectableTextAnt.Type.ERROR).build();
                """;
        return Demos.sectionWithCode("3. 语义类型",
                "type 控制文字颜色：SECONDARY / SUCCESS / WARNING / ERROR。", code, demo);
    }
}
