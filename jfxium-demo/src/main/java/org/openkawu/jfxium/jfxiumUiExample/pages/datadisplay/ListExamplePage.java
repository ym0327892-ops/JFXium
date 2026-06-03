package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.layout.VBox;


import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.ListAnt;

/**
 * List 列表 —— 基础 / 带边框 / 带操作。
 */
public class ListExamplePage extends VBoxAnt {

    public ListExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("List 列表")
                .description("最基础的列表展示，可承载文字、图片、段落。")
                .sections(
                        basicSection(),
                        borderedSection(),
                        actionSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        VBox list = ListAnt.create()
                .item("Ant Design", "蚂蚁金服体验技术部出品")
                .item("JFXium", "JavaFX 组件库")
                .item("Element Plus", "Vue 3 组件库")
                .build();
        String code = """
                VBox list = ListAnt.create()
                        .item("Ant Design", "蚂蚁金服体验技术部出品")
                        .item("JFXium", "JavaFX 组件库")
                        .item("Element Plus", "Vue 3 组件库")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础列表", "简单的列表展示。", code, list);
    }

    private Node borderedSection() {
        VBox list = ListAnt.create()
                .header("列表标题")
                .bordered()
                .item("列表项 1", "描述文字")
                .item("列表项 2", "描述文字")
                .item("列表项 3", "描述文字")
                .footer("共 3 项")
                .build();
        String code = """
                VBox list = ListAnt.create()
                        .header("列表标题")
                        .bordered()
                        .item("列表项 1", "描述文字")
                        .item("列表项 2", "描述文字")
                        .item("列表项 3", "描述文字")
                        .footer("共 3 项")
                        .build();
                """;
        return Demos.sectionWithCode("2. 带边框", "bordered() 添加边框，header/footer 添加头尾。", code, list);
    }

    private Node actionSection() {
        VBox list = ListAnt.create()
                .item(null, "任务一", "进行中",
                        ButtonAnt.create("编辑").type(ButtonAnt.Type.LINK).build())
                .item(null, "任务二", "已完成",
                        ButtonAnt.create("查看").type(ButtonAnt.Type.LINK).build())
                .item(null, "任务三", "待处理",
                        ButtonAnt.create("删除").type(ButtonAnt.Type.DANGER).build())
                .bordered()
                .build();
        String code = """
                VBox list = ListAnt.create()
                        .item(null, "任务一", "进行中",
                                ButtonAnt.create("编辑").type(ButtonAnt.Type.LINK).build())
                        .item(null, "任务二", "已完成",
                                ButtonAnt.create("查看").type(ButtonAnt.Type.LINK).build())
                        .item(null, "任务三", "待处理",
                                ButtonAnt.create("删除").type(ButtonAnt.Type.DANGER).build())
                        .bordered()
                        .build();
                """;
        return Demos.sectionWithCode("3. 带操作", "每行末尾可放置操作按钮。", code, list);
    }
}
