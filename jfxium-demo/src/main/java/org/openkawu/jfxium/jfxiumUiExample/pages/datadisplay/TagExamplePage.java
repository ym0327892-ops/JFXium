package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * Tag 标签 —— 类型 / 尺寸 / 形状 / 可关闭。
 */
public class TagExamplePage extends VBoxAnt {

    public TagExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Tag 标签")
                .description("用于标记和分类的小标签组件。")
                .sections(
                        typeSection(),
                        sizeSection(),
                        shapeSection(),
                        closableSection()
                )
                .padding(24)
                .build());
    }

    private Node typeSection() {
        Node demo = Demos.row(
                TagAnt.create().text("Default").build(),
                TagAnt.create().text("Success").type(TagAnt.Type.SUCCESS).build(),
                TagAnt.create().text("Warning").type(TagAnt.Type.WARNING).build(),
                TagAnt.create().text("Error").type(TagAnt.Type.ERROR).build(),
                TagAnt.create().text("Processing").type(TagAnt.Type.PROCESSING).build()
        );
        String code = """
                TagAnt.create().text("Default").build();
                TagAnt.create().text("Success").type(TagAnt.Type.SUCCESS).build();
                TagAnt.create().text("Warning").type(TagAnt.Type.WARNING).build();
                TagAnt.create().text("Error").type(TagAnt.Type.ERROR).build();
                TagAnt.create().text("Processing").type(TagAnt.Type.PROCESSING).build();
                """;
        return Demos.sectionWithCode("1. 类型",
                "不同语义类型对应不同颜色。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                TagAnt.create().text("Small").size(TagAnt.Size.SMALL).build(),
                TagAnt.create().text("Default").build(),
                TagAnt.create().text("Large").size(TagAnt.Size.LARGE).build()
        );
        String code = """
                TagAnt.create().text("Small").size(TagAnt.Size.SMALL).build();
                TagAnt.create().text("Default").build();
                TagAnt.create().text("Large").size(TagAnt.Size.LARGE).build();
                """;
        return Demos.sectionWithCode("2. 尺寸", "SMALL / DEFAULT / LARGE。", code, demo);
    }

    private Node shapeSection() {
        Node demo = Demos.row(
                TagAnt.create().text("Default").build(),
                TagAnt.create().text("Round").shape(TagAnt.Shape.ROUND).build()
        );
        String code = """
                TagAnt.create().text("Default").build();
                TagAnt.create().text("Round").shape(TagAnt.Shape.ROUND).build();
                """;
        return Demos.sectionWithCode("3. 形状",
                "DEFAULT（小圆角）/ ROUND（全圆角胶囊）。",
                code, demo);
    }

    private Node closableSection() {
        Node demo = Demos.row(
                TagAnt.create().text("可关闭").closable(true).onClose(() -> MessageAnt.info("Tag 已关闭")).build(),
                TagAnt.create().text("Success 可关闭")
                        .type(TagAnt.Type.SUCCESS).closable(true).onClose(() -> MessageAnt.success("Success Tag 已关闭")).build()
        );
        String code = """
                TagAnt.create().text("可关闭")
                        .closable(true)
                        .onClose(() -> System.out.println("closed"))
                        .build();
                """;
        return Demos.sectionWithCode("4. 可关闭",
                "closable(true) 显示关闭按钮；onClose 回调处理关闭逻辑。",
                code, demo);
    }
}
