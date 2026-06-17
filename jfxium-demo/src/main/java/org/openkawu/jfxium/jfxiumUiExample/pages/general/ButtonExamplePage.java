package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * Button 按钮 —— 类型 / 四档尺寸 / 形状 / 状态 / 块级。
 *
 * <p>对标 Ant Design Button：10 种 Type × 4 档 Size × 多种 Shape，可叠加 ghost / block / disabled。</p>
 */
public class ButtonExamplePage extends VBoxAnt {

    public ButtonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Button 按钮")
                .description("可点击的交互元素，支持多种 type / size / shape，组合非常灵活。默认尺寸是 middle；布局模板里常把按钮再压一档，常见是 small 或 xs。")
                .sections(
                        typeSection(),
                        sizeSection(),
                        shapeSection(),
                        stateSection(),
                        blockSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 类型 Type —— 通过 type(...) 切换语义颜色。 */
    private Node typeSection() {
        Node row = Demos.row(
                ButtonAnt.create("默认").build(),
                ButtonAnt.create("主按钮").type(ButtonAnt.Type.PRIMARY).build(),
                ButtonAnt.create("强调").type(ButtonAnt.Type.ACCENT).build(),
                ButtonAnt.create("成功").type(ButtonAnt.Type.SUCCESS).build(),
                ButtonAnt.create("警告").type(ButtonAnt.Type.WARNING).build(),
                ButtonAnt.create("危险").type(ButtonAnt.Type.DANGER).build(),
                ButtonAnt.create("虚线").type(ButtonAnt.Type.DASHED).build(),
                ButtonAnt.create("文字").type(ButtonAnt.Type.TEXT).build(),
                ButtonAnt.create("链接").type(ButtonAnt.Type.LINK).build()
        );
        String code = """
                // 默认按钮
                ButtonAnt.create("默认").build();

                // 通过 .type(...) 切换 9 种语义颜色
                ButtonAnt.create("主按钮").type(ButtonAnt.Type.PRIMARY).build();
                ButtonAnt.create("强调").type(ButtonAnt.Type.ACCENT).build();
                ButtonAnt.create("成功").type(ButtonAnt.Type.SUCCESS).build();
                ButtonAnt.create("警告").type(ButtonAnt.Type.WARNING).build();
                ButtonAnt.create("危险").type(ButtonAnt.Type.DANGER).build();
                ButtonAnt.create("虚线").type(ButtonAnt.Type.DASHED).build();
                ButtonAnt.create("文字").type(ButtonAnt.Type.TEXT).build();
                ButtonAnt.create("链接").type(ButtonAnt.Type.LINK).build();
                """;
        return Demos.sectionWithCode("1. 类型 Type",
                "DEFAULT / PRIMARY / ACCENT / SUCCESS / WARNING / DANGER / DASHED / TEXT / LINK 等。",
                code, row);
    }

    /** 2. 尺寸 Size —— 四档大小。 */
    private Node sizeSection() {
        Node row = Demos.column(
                Demos.row(
                        ButtonAnt.create("Large").size(ButtonAnt.Size.LARGE).type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("Middle").size(ButtonAnt.Size.MIDDLE).type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("Small").size(ButtonAnt.Size.SMALL).type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("XS").size(ButtonAnt.Size.XS).type(ButtonAnt.Type.PRIMARY).build()
                ),
                Demos.row(
                        ButtonAnt.create("Text Large").type(ButtonAnt.Type.TEXT).size(ButtonAnt.Size.LARGE).build(),
                        ButtonAnt.create("Text Middle").type(ButtonAnt.Type.TEXT).size(ButtonAnt.Size.MIDDLE).build(),
                        ButtonAnt.create("Text Small").type(ButtonAnt.Type.TEXT).size(ButtonAnt.Size.SMALL).build(),
                        ButtonAnt.create("Text XS").type(ButtonAnt.Type.TEXT).size(ButtonAnt.Size.XS).build()
                ),
                Demos.row(
                        ButtonAnt.create("Link Large").type(ButtonAnt.Type.LINK).size(ButtonAnt.Size.LARGE).build(),
                        ButtonAnt.create("Link Middle").type(ButtonAnt.Type.LINK).size(ButtonAnt.Size.MIDDLE).build(),
                        ButtonAnt.create("Link Small").type(ButtonAnt.Type.LINK).size(ButtonAnt.Size.SMALL).build(),
                        ButtonAnt.create("Link XS").type(ButtonAnt.Type.LINK).size(ButtonAnt.Size.XS).build()
                )
        );
        String code = """
                // 四档：LARGE / MIDDLE / SMALL / XS
                ButtonAnt.create("Large").size(ButtonAnt.Size.LARGE).type(ButtonAnt.Type.PRIMARY).build();
                ButtonAnt.create("Middle").size(ButtonAnt.Size.MIDDLE).type(ButtonAnt.Type.PRIMARY).build();
                ButtonAnt.create("Small").size(ButtonAnt.Size.SMALL).type(ButtonAnt.Type.PRIMARY).build();
                ButtonAnt.create("XS").size(ButtonAnt.Size.XS).type(ButtonAnt.Type.PRIMARY).build();

                // TEXT / LINK 也沿用同一套四档 size
                ButtonAnt.create("Text XS").type(ButtonAnt.Type.TEXT).size(ButtonAnt.Size.XS).build();
                ButtonAnt.create("Link XS").type(ButtonAnt.Type.LINK).size(ButtonAnt.Size.XS).build();
                """;
        return Demos.sectionWithCode("2. 尺寸 Size",
                "四档：LARGE / MIDDLE / SMALL / XS。TEXT 和 LINK 也直接复用同一套 size。",
                code, row);
    }

    /** 3. 形状 Shape —— 圆角程度。 */
    private Node shapeSection() {
        Node row = Demos.row(
                ButtonAnt.create("默认形状").type(ButtonAnt.Type.PRIMARY).build(),
                ButtonAnt.create("圆角 ROUNDED").type(ButtonAnt.Type.PRIMARY).shape(ButtonAnt.Shape.ROUNDED).build(),
                ButtonAnt.create("方角 SQUARE").type(ButtonAnt.Type.PRIMARY).shape(ButtonAnt.Shape.SQUARE).build()
        );
        String code = """
                // shape 控制圆角程度
                ButtonAnt.create("默认形状").type(ButtonAnt.Type.PRIMARY).build();
                ButtonAnt.create("圆角 ROUNDED")
                        .type(ButtonAnt.Type.PRIMARY)
                        .shape(ButtonAnt.Shape.ROUNDED)
                        .build();
                ButtonAnt.create("方角 SQUARE")
                        .type(ButtonAnt.Type.PRIMARY)
                        .shape(ButtonAnt.Shape.SQUARE)
                        .build();
                """;
        return Demos.sectionWithCode("3. 形状 Shape",
                "DEFAULT（小圆角）/ ROUNDED（pill 胶囊）/ SQUARE（直角）。",
                code, row);
    }

    /** 4. 状态 —— 禁用 / Ghost / 点击。 */
    private Node stateSection() {
        Node row = Demos.row(
                ButtonAnt.create("可点击").type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> MessageAnt.success("点击成功")).build(),
                ButtonAnt.create("禁用").type(ButtonAnt.Type.PRIMARY).disabled(true).build(),
                ButtonAnt.create("Ghost").type(ButtonAnt.Type.PRIMARY).ghost(true).build()
        );
        String code = """
                // 点击事件用 .onClick(EventHandler<ActionEvent>)
                ButtonAnt.create("可点击")
                        .type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> MessageAnt.success("点击成功"))
                        .build();

                // disabled(true) 禁用
                ButtonAnt.create("禁用").type(ButtonAnt.Type.PRIMARY).disabled(true).build();

                // ghost(true) 透明背景描边按钮（适合彩色背景上）
                ButtonAnt.create("Ghost").type(ButtonAnt.Type.PRIMARY).ghost(true).build();
                """;
        return Demos.sectionWithCode("4. 状态",
                "disabled(true) 禁用；ghost(true) 透明背景描边按钮（适合彩色背景上）。",
                code, row);
    }

    /** 5. 块级 —— 撑满父宽度。 */
    private Node blockSection() {
        Node btn = ButtonAnt.create("块级按钮（撑满父宽度）")
                .type(ButtonAnt.Type.PRIMARY).block(true).build();
        String code = """
                // block(true) —— 撑满父容器宽度
                ButtonAnt.create("块级按钮（撑满父宽度）")
                        .type(ButtonAnt.Type.PRIMARY)
                        .block(true)
                        .build();
                """;
        return Demos.sectionWithCode("5. 块级 block",
                "block(true) —— 撑满父容器宽度，常用于移动端 / 表单提交按钮。",
                code, btn);
    }
}
