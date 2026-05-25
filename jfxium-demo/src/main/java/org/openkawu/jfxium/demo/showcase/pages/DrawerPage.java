package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.DrawerAnt;
import org.openkawu.jfxium.component.InputAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * DrawerAnt 组件展示页。
 */
public class DrawerPage implements ShowcasePage {

    @Override public String   key()      { return "drawer"; }
    @Override public String   title()    { return "Drawer 抽屉"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Drawer 抽屉");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("从屏幕侧边滑出的浮层面板，常用于详情查看、表单编辑等长内容场景");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionPlacements(),
                        sectionSizes(),
                        sectionFormDrawer(),
                        sectionExtra(),
                        sectionMaskClosable()
                )
                .build();
    }

    private Node sectionPlacements() {
        HBox row = new HBox(8);
        row.getChildren().addAll(
                openButton("从左滑出", DrawerAnt.Placement.LEFT),
                openButton("从右滑出", DrawerAnt.Placement.RIGHT),
                openButton("从顶滑出", DrawerAnt.Placement.TOP),
                openButton("从底滑出", DrawerAnt.Placement.BOTTOM)
        );
        return ShowcaseSection.create()
                .title("4 个方向")
                .description("Placement.LEFT / RIGHT / TOP / BOTTOM。RIGHT 是最常用（admin 详情面板）")
                .demo(row)
                .code("""
                        DrawerAnt.create()
                            .title("详情")
                            .content("从右侧滑出")
                            .placement(DrawerAnt.Placement.RIGHT)
                            .open(triggerNode);
                        """)
                .build();
    }

    private Button openButton(String label, DrawerAnt.Placement p) {
        return ButtonAnt.create(label)
                .onClick(e -> DrawerAnt.create()
                        .title(label + " - " + p.name())
                        .content("Drawer 内容区域。点击遮罩或 ESC 可关闭。")
                        .placement(p)
                        .build().open((Node) e.getSource()))
                .build();
    }

    private Node sectionSizes() {
        HBox row = new HBox(8);
        row.getChildren().addAll(
                ButtonAnt.create("默认尺寸（378px）")
                        .onClick(e -> DrawerAnt.create()
                                .title("Default Size")
                                .content("默认 378px 宽")
                                .size(DrawerAnt.Size.DEFAULT)
                                .build().open((Node) e.getSource()))
                        .build(),
                ButtonAnt.create("大尺寸（736px）")
                        .onClick(e -> DrawerAnt.create()
                                .title("Large Size")
                                .content("大尺寸 736px 宽，适合详情/表单页")
                                .size(DrawerAnt.Size.LARGE)
                                .build().open((Node) e.getSource()))
                        .build(),
                ButtonAnt.create("自定义 width=500")
                        .onClick(e -> DrawerAnt.create()
                                .title("Custom Width")
                                .content("自定义 width=500")
                                .width(500)
                                .build().open((Node) e.getSource()))
                        .build()
        );
        return ShowcaseSection.create()
                .title("尺寸")
                .description("Size.DEFAULT 378px / Size.LARGE 736px / 或用 width(int) / height(int) 完全自定义")
                .demo(row)
                .code("""
                        .size(DrawerAnt.Size.DEFAULT)   // 378
                        .size(DrawerAnt.Size.LARGE)     // 736
                        .width(500)                     // 自定义
                        """)
                .build();
    }

    private Node sectionFormDrawer() {
        Button trigger = ButtonAnt.create("打开表单 Drawer")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    VBox form = VBoxBuilder.create()
                            .spacing(12)
                            .children(
                                    new Label("用户名"),
                                    InputAnt.create().placeholder("请输入用户名").build(),
                                    new Label("邮箱"),
                                    InputAnt.create().placeholder("name@example.com").build(),
                                    new Label("手机号"),
                                    InputAnt.create().placeholder("可选").build()
                            )
                            .build();
                    DrawerAnt.create()
                            .title("新增用户")
                            .content(form)
                            .placement(DrawerAnt.Placement.RIGHT)
                            .size(DrawerAnt.Size.DEFAULT)
                            .build().open((Node) e.getSource());
                })
                .build();

        return ShowcaseSection.create()
                .title("典型场景：表单 Drawer")
                .description("admin 列表页常见模式：点击「新增」从右滑出表单，提交后关闭")
                .demo(trigger)
                .code("""
                        VBox form = VBoxBuilder.create()
                            .spacing(12)
                            .children(
                                new Label("用户名"),
                                InputAnt.create().placeholder("请输入").build(),
                                ...
                            )
                            .build();

                        DrawerAnt.create()
                            .title("新增用户")
                            .content(form)
                            .placement(DrawerAnt.Placement.RIGHT)
                            .open(triggerNode);
                        """)
                .build();
    }

    private Node sectionExtra() {
        Button trigger = ButtonAnt.create("带 Extra 操作")
                .onClick(e -> {
                    Button save = ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build();
                    DrawerAnt.create()
                            .title("订单 #1024")
                            .extra(save)        // 标题栏右侧 extra 区
                            .content("订单详细信息内容...")
                            .placement(DrawerAnt.Placement.RIGHT)
                            .build().open((Node) e.getSource());
                })
                .build();

        return ShowcaseSection.create()
                .title("Extra 标题栏右侧操作")
                .description("extra(Node) 在标题栏右侧加按钮（不是底部 Footer），常用于「保存/编辑」入口")
                .demo(trigger)
                .code("""
                        Button saveBtn = ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build();
                        DrawerAnt.create()
                            .title("订单 #1024")
                            .extra(saveBtn)
                            .content(orderDetail)
                            .open(triggerNode);
                        """)
                .build();
    }

    private Node sectionMaskClosable() {
        Button trigger = ButtonAnt.create("禁止遮罩关闭")
                .onClick(e -> DrawerAnt.create()
                        .title("严肃编辑")
                        .content("点击遮罩不会关闭；只能用左上角 X 或 Esc 退出")
                        .placement(DrawerAnt.Placement.RIGHT)
                        .maskClosable(false)
                        .build().open((Node) e.getSource()))
                .build();

        return ShowcaseSection.create()
                .title("禁止遮罩关闭")
                .description("maskClosable(false) 用于编辑表单防止误点丢失；保留 X 按钮和 Esc")
                .demo(trigger)
                .code("""
                        DrawerAnt.create()
                            .title("严肃编辑")
                            .maskClosable(false)
                            .open(triggerNode);
                        """)
                .build();
    }
}
