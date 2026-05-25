package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.TagAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * TagAnt 组件展示页。
 */
public class TagPage implements ShowcasePage {

    @Override public String   key()      { return "tag"; }
    @Override public String   title()    { return "Tag 标签"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Tag 标签");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("标记和分类，常用于状态、分组、过滤条件等场景");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionTypes(),
                        sectionSizes(),
                        sectionShapes(),
                        sectionBordered(),
                        sectionClosable()
                )
                .build();
    }

    private Node sectionTypes() {
        FlowPane row = new FlowPane(8, 8);
        row.getChildren().addAll(
                TagAnt.create("Default").type(TagAnt.Type.DEFAULT).build(),
                TagAnt.create("Primary").type(TagAnt.Type.PRIMARY).build(),
                TagAnt.create("Success").type(TagAnt.Type.SUCCESS).build(),
                TagAnt.create("Warning").type(TagAnt.Type.WARNING).build(),
                TagAnt.create("Error").type(TagAnt.Type.ERROR).build(),
                TagAnt.create("Processing").type(TagAnt.Type.PROCESSING).build()
        );
        return ShowcaseSection.create()
                .title("6 种类型")
                .description("DEFAULT / PRIMARY / SUCCESS / WARNING / ERROR / PROCESSING")
                .demo(row)
                .code("""
                        TagAnt.create("Success").type(TagAnt.Type.SUCCESS).build();
                        TagAnt.create("Error")  .type(TagAnt.Type.ERROR)  .build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getChildren().addAll(
                TagAnt.create("Small").type(TagAnt.Type.PRIMARY).size(TagAnt.Size.SMALL).build(),
                TagAnt.create("Default").type(TagAnt.Type.PRIMARY).size(TagAnt.Size.DEFAULT).build(),
                TagAnt.create("Large").type(TagAnt.Type.PRIMARY).size(TagAnt.Size.LARGE).build()
        );
        return ShowcaseSection.create()
                .title("3 种尺寸")
                .description("Size.SMALL / Size.DEFAULT / Size.LARGE")
                .demo(row)
                .code("""
                        TagAnt.create("Small").size(TagAnt.Size.SMALL).build();
                        TagAnt.create("Large").size(TagAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionShapes() {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getChildren().addAll(
                TagAnt.create("默认").type(TagAnt.Type.PRIMARY).shape(TagAnt.Shape.DEFAULT).build(),
                TagAnt.create("圆角").type(TagAnt.Type.PRIMARY).shape(TagAnt.Shape.ROUND).build(),
                TagAnt.create("方形").type(TagAnt.Type.PRIMARY).shape(TagAnt.Shape.SQUARE).build()
        );
        return ShowcaseSection.create()
                .title("3 种形状")
                .description("Shape.DEFAULT / Shape.ROUND（圆角胶囊）/ Shape.SQUARE（直角）")
                .demo(row)
                .code("""
                        TagAnt.create("圆角").shape(TagAnt.Shape.ROUND).build();
                        TagAnt.create("方形").shape(TagAnt.Shape.SQUARE).build();
                        """)
                .build();
    }

    private Node sectionBordered() {
        HBox row = new HBox(8);
        row.getChildren().addAll(
                TagAnt.create("有边框").type(TagAnt.Type.PRIMARY).bordered(true).build(),
                TagAnt.create("无边框").type(TagAnt.Type.PRIMARY).noBorder().build(),
                TagAnt.create("Success").type(TagAnt.Type.SUCCESS).noBorder().build(),
                TagAnt.create("Warning").type(TagAnt.Type.WARNING).noBorder().build()
        );
        return ShowcaseSection.create()
                .title("边框开关")
                .description("bordered(true) 默认有边框；.noBorder() 去除边框（背景填充更柔和）")
                .demo(row)
                .code("""
                        TagAnt.create("无边框").type(TagAnt.Type.PRIMARY).noBorder().build();
                        """)
                .build();
    }

    private Node sectionClosable() {
        HBox row = new HBox(8);
        row.getChildren().addAll(
                TagAnt.create("可关闭")
                        .type(TagAnt.Type.PRIMARY)
                        .closable()
                        .onClose(() -> System.out.println("[Demo] 关闭 Tag"))
                        .build(),
                TagAnt.create("React")
                        .type(TagAnt.Type.PROCESSING)
                        .closable()
                        .build(),
                TagAnt.create("Vue")
                        .type(TagAnt.Type.SUCCESS)
                        .closable()
                        .build(),
                TagAnt.create("Angular")
                        .type(TagAnt.Type.ERROR)
                        .closable()
                        .build()
        );
        return ShowcaseSection.create()
                .title("可关闭 Tag")
                .description("closable() 显示关闭按钮；onClose 监听关闭事件（适合标签筛选场景）")
                .demo(row)
                .code("""
                        TagAnt.create("可关闭")
                            .type(TagAnt.Type.PRIMARY)
                            .closable()
                            .onClose(() -> System.out.println("关闭"))
                            .build();
                        """)
                .build();
    }
}
