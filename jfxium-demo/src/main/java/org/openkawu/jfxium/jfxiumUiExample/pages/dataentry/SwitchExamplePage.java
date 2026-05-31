package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import org.openkawu.jfxium.component.SwitchAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Switch 开关 —— 形状 / 文字 / 禁用。
 */
public class SwitchExamplePage extends VBoxAnt {

    public SwitchExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Switch 开关")
                .description("用于切换两种状态（开/关）的开关组件。")
                .sections(
                        basicSection(),
                        shapeSection(),
                        disabledTextSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                SwitchAnt.create().build(),
                SwitchAnt.create().selected(true).build()
        );
        String code = """
                SwitchAnt.create().build();
                SwitchAnt.create().selected(true).build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "默认关闭和默认开启。", code, demo);
    }

    private Node shapeSection() {
        Node demo = Demos.row(
                SwitchAnt.create().shape(SwitchAnt.Shape.PILL).selected(true).build(),
                SwitchAnt.create().shape(SwitchAnt.Shape.ROUNDED).selected(true).build(),
                SwitchAnt.create().shape(SwitchAnt.Shape.SQUARE).selected(true).build()
        );
        String code = """
                SwitchAnt.create().shape(SwitchAnt.Shape.PILL).selected(true).build();
                SwitchAnt.create().shape(SwitchAnt.Shape.ROUNDED).selected(true).build();
                SwitchAnt.create().shape(SwitchAnt.Shape.SQUARE).selected(true).build();
                """;
        return Demos.sectionWithCode("2. 形状",
                "PILL（胶囊，默认）/ ROUNDED（圆角）/ SQUARE（方角）。",
                code, demo);
    }

    private Node disabledTextSection() {
        Node demo = Demos.row(
                SwitchAnt.create().disabled(true).selected(true).build(),
                SwitchAnt.create().checkedText("开").uncheckedText("关").selected(true).build(),
                SwitchAnt.create().checkedText("开").uncheckedText("关").build()
        );
        String code = """
                SwitchAnt.create().disabled(true).selected(true).build();
                SwitchAnt.create().checkedText("开").uncheckedText("关").selected(true).build();
                SwitchAnt.create().checkedText("开").uncheckedText("关").build();
                """;
        return Demos.sectionWithCode("3. 禁用与文字",
                "disabled(true) 禁用；checkedText/uncheckedText 显示开关状态文字。",
                code, demo);
    }
}
