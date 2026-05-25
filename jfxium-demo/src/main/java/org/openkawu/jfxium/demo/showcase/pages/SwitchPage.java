package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.SwitchAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * SwitchAnt 组件展示页。
 */
public class SwitchPage implements ShowcasePage {

    @Override public String   key()      { return "switch"; }
    @Override public String   title()    { return "Switch 开关"; }
    @Override public Category category() { return Category.GENERAL; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Switch 开关");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("二态切换开关，常用于设置项的启用/禁用");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithText(),
                        sectionStates(),
                        sectionShapes(),
                        sectionWithCallback()
                )
                .build();
    }

    private Node sectionShapes() {
        VBox col = new VBox(12);

        HBox r1 = new HBox(16);
        r1.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        r1.getChildren().addAll(
                new Label("PILL（默认）"),
                SwitchAnt.create().shape(SwitchAnt.Shape.PILL).build(),
                SwitchAnt.create().shape(SwitchAnt.Shape.PILL).selected(true).build()
        );
        HBox r2 = new HBox(16);
        r2.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        r2.getChildren().addAll(
                new Label("ROUNDED   "),
                SwitchAnt.create().shape(SwitchAnt.Shape.ROUNDED).build(),
                SwitchAnt.create().shape(SwitchAnt.Shape.ROUNDED).selected(true).build()
        );
        HBox r3 = new HBox(16);
        r3.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        r3.getChildren().addAll(
                new Label("SQUARE    "),
                SwitchAnt.create().shape(SwitchAnt.Shape.SQUARE).build(),
                SwitchAnt.create().shape(SwitchAnt.Shape.SQUARE).selected(true).build()
        );
        col.getChildren().addAll(r1, r2, r3);

        return ShowcaseSection.create()
                .title("形状变体（M19.20）")
                .description(".shape(Shape.PILL/ROUNDED/SQUARE) 切换轨道与滑块的圆角")
                .demo(col)
                .code("""
                        SwitchAnt.create().shape(SwitchAnt.Shape.PILL).build();      // 胶囊（默认）
                        SwitchAnt.create().shape(SwitchAnt.Shape.ROUNDED).build();   // 圆角矩形
                        SwitchAnt.create().shape(SwitchAnt.Shape.SQUARE).build();    // 直角矩形
                        """)
                .build();
    }

    private Node sectionBasic() {
        HBox row = new HBox(16);
        row.getChildren().addAll(
                SwitchAnt.create().build(),
                SwitchAnt.create().selected(true).build()
        );
        return ShowcaseSection.create()
                .title("基础用法")
                .description("默认状态 + 选中状态。点击切换。")
                .demo(row)
                .code("""
                        SwitchAnt.create().build();                 // 默认关闭
                        SwitchAnt.create().selected(true).build();  // 默认开启
                        """)
                .build();
    }

    private Node sectionWithText() {
        HBox row = new HBox(16);
        row.getChildren().addAll(
                SwitchAnt.create()
                        .checkedText("开")
                        .uncheckedText("关")
                        .selected(true)
                        .build(),
                SwitchAnt.create()
                        .checkedText("ON")
                        .uncheckedText("OFF")
                        .build(),
                SwitchAnt.create()
                        .checkedText("启用通知")
                        .uncheckedText("已禁用")
                        .selected(true)
                        .build()
        );
        return ShowcaseSection.create()
                .title("带文字标签")
                .description("checkedText / uncheckedText 在开关右侧显示当前状态文字")
                .demo(row)
                .code("""
                        SwitchAnt.create()
                            .checkedText("开")
                            .uncheckedText("关")
                            .selected(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionStates() {
        HBox row = new HBox(16);
        row.getChildren().addAll(
                SwitchAnt.create().selected(true).build(),
                SwitchAnt.create().selected(false).build(),
                SwitchAnt.create().selected(true).disabled(true).build(),
                SwitchAnt.create().selected(false).disabled(true).build()
        );
        return ShowcaseSection.create()
                .title("启用 / 禁用")
                .description("disabled(true) 禁止交互；保留视觉状态")
                .demo(row)
                .code("""
                        SwitchAnt.create().selected(true).disabled(true).build();
                        SwitchAnt.create().selected(false).disabled(true).build();
                        """)
                .build();
    }

    private Node sectionWithCallback() {
        Label statusLabel = new Label("当前：关闭");
        statusLabel.setStyle("-fx-text-fill: -color-fg-muted;");

        HBox demo = new HBox(12);
        demo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        demo.getChildren().addAll(
                SwitchAnt.create()
                        .checkedText("开")
                        .uncheckedText("关")
                        .onChange(checked -> statusLabel.setText("当前：" + (checked ? "开启" : "关闭")))
                        .build(),
                statusLabel
        );

        return ShowcaseSection.create()
                .title("状态变化回调")
                .description("onChange(Consumer<Boolean>) 监听切换；右侧 Label 显示实时状态")
                .demo(demo)
                .code("""
                        SwitchAnt.create()
                            .checkedText("开")
                            .uncheckedText("关")
                            .onChange(checked -> {
                                statusLabel.setText("当前：" + (checked ? "开启" : "关闭"));
                            })
                            .build();
                        """)
                .build();
    }
}
