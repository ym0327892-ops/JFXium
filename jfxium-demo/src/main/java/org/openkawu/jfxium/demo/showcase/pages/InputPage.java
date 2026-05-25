package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.InputAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * InputAnt 组件展示页。
 */
public class InputPage implements ShowcasePage {

    @Override public String   key()      { return "input"; }
    @Override public String   title()    { return "Input 输入框"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Input 输入框");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("文本输入控件，支持多种尺寸 + 状态");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionSizes(),
                        sectionStates(),
                        sectionWithLabel(),
                        sectionForm()
                )
                .build();
    }

    private Node sectionBasic() {
        VBox col = new VBox(8);
        col.setMaxWidth(360);
        col.getChildren().addAll(
                InputAnt.create().placeholder("请输入用户名").build(),
                InputAnt.create().text("已经有内容").build()
        );
        return ShowcaseSection.create()
                .title("基础用法")
                .description("placeholder 占位提示 / text 默认值")
                .demo(col)
                .code("""
                        TextField field = InputAnt.create()
                            .placeholder("请输入用户名")
                            .build();

                        TextField filled = InputAnt.create()
                            .text("已经有内容")
                            .build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        VBox col = new VBox(8);
        col.setMaxWidth(360);
        col.getChildren().addAll(
                InputAnt.create().placeholder("Small 紧凑").size(InputAnt.Size.SMALL).build(),
                InputAnt.create().placeholder("Default 默认").size(InputAnt.Size.DEFAULT).build(),
                InputAnt.create().placeholder("Large 宽松").size(InputAnt.Size.LARGE).build()
        );
        return ShowcaseSection.create()
                .title("3 种尺寸")
                .description("Size.SMALL / Size.DEFAULT / Size.LARGE，匹配密度场景")
                .demo(col)
                .code("""
                        InputAnt.create().placeholder("Small") .size(InputAnt.Size.SMALL) .build();
                        InputAnt.create().placeholder("Default").size(InputAnt.Size.DEFAULT).build();
                        InputAnt.create().placeholder("Large") .size(InputAnt.Size.LARGE) .build();
                        """)
                .build();
    }

    private Node sectionStates() {
        VBox col = new VBox(8);
        col.setMaxWidth(360);
        col.getChildren().addAll(
                InputAnt.create().placeholder("正常").build(),
                InputAnt.create().text("禁用状态").disabled(true).build(),
                InputAnt.create().text("只读状态").readOnly(true).build()
        );
        return ShowcaseSection.create()
                .title("状态")
                .description("disabled(true) 禁用 / readOnly(true) 只读（可选中复制但不可编辑）")
                .demo(col)
                .code("""
                        InputAnt.create().text("禁用状态").disabled(true).build();
                        InputAnt.create().text("只读状态").readOnly(true).build();
                        """)
                .build();
    }

    private Node sectionWithLabel() {
        Label l1 = new Label("用户名：");
        l1.setMinWidth(64);
        TextField f1 = InputAnt.create().placeholder("请输入").build();
        f1.setPrefWidth(220);
        HBox row1 = new HBox(8, l1, f1);
        row1.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label l2 = new Label("邮箱：");
        l2.setMinWidth(64);
        TextField f2 = InputAnt.create().placeholder("name@example.com").build();
        f2.setPrefWidth(220);
        HBox row2 = new HBox(8, l2, f2);
        row2.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox col = new VBox(8, row1, row2);

        return ShowcaseSection.create()
                .title("配 Label 表单")
                .description("InputAnt 直接返回 TextField，可与任意 Label/HBox 组合成表单行")
                .demo(col)
                .code("""
                        Label label = new Label("用户名：");
                        TextField field = InputAnt.create().placeholder("请输入").build();
                        HBox row = new HBox(8, label, field);
                        """)
                .build();
    }

    private Node sectionForm() {
        TextField nameField = InputAnt.create().placeholder("用户名").build();
        TextField emailField = InputAnt.create().placeholder("邮箱").build();
        TextField phoneField = InputAnt.create().placeholder("手机号（选填）").build();
        nameField.setPrefWidth(280);
        emailField.setPrefWidth(280);
        phoneField.setPrefWidth(280);

        VBox col = new VBox(12, nameField, emailField, phoneField);
        col.setMaxWidth(280);

        return ShowcaseSection.create()
                .title("竖向表单组合")
                .description("InputAnt 是 admin 表单页基础组件，配合 VBox spacing 即可构成简单表单")
                .demo(col)
                .code("""
                        VBox form = VBoxBuilder.create()
                            .spacing(12)
                            .children(
                                InputAnt.create().placeholder("用户名").build(),
                                InputAnt.create().placeholder("邮箱").build(),
                                InputAnt.create().placeholder("手机号（选填）").build()
                            )
                            .build();
                        """)
                .build();
    }
}
