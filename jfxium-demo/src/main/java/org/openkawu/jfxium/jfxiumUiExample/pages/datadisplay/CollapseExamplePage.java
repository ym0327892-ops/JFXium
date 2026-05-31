package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CollapseAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Collapse 折叠面板 —— 基础 / 手风琴 / 禁用面板。
 */
public class CollapseExamplePage extends VBoxAnt {

    public CollapseExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Collapse 折叠面板")
                .description("可以折叠/展开的内容区域，用于将复杂内容分组收纳。")
                .sections(basicSection(), accordionSection(), disabledSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        VBox collapse = CollapseAnt.create()
                .panel("1", "面板一", new Label("这是面板一的内容。"))
                .panel("2", "面板二", new Label("这是面板二的内容。"))
                .panel("3", "面板三", new Label("这是面板三的内容。"))
                .activeKey("1")
                .build();
        String code = """
                CollapseAnt.create()
                        .panel("1", "面板一", new Label("这是面板一的内容。"))
                        .panel("2", "面板二", new Label("这是面板二的内容。"))
                        .panel("3", "面板三", new Label("这是面板三的内容。"))
                        .activeKey("1")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "多个面板可同时展开，activeKey 设置默认展开项。", code, collapse);
    }

    private Node accordionSection() {
        VBox collapse = CollapseAnt.create()
                .accordion()
                .panel("a", "手风琴 A", new Label("只能展开一个面板。"))
                .panel("b", "手风琴 B", new Label("展开 B 时 A 自动收起。"))
                .panel("c", "手风琴 C", new Label("互斥展开模式。"))
                .activeKey("a")
                .build();
        String code = """
                CollapseAnt.create()
                        .accordion()
                        .panel("a", "手风琴 A", new Label("只能展开一个面板。"))
                        .panel("b", "手风琴 B", new Label("展开 B 时 A 自动收起。"))
                        .panel("c", "手风琴 C", new Label("互斥展开模式。"))
                        .activeKey("a")
                        .build();
                """;
        return Demos.sectionWithCode("2. 手风琴模式",
                "accordion() 启用互斥展开，同一时间只有一个面板打开。", code, collapse);
    }

    private Node disabledSection() {
        VBox collapse = CollapseAnt.create()
                .panel("1", "可用面板", new Label("正常交互。"))
                .panel("2", "禁用面板", new Label("无法展开。"), true)
                .activeKey("1")
                .build();
        String code = """
                CollapseAnt.create()
                        .panel("1", "可用面板", new Label("正常交互。"))
                        .panel("2", "禁用面板", new Label("无法展开。"), true)
                        .activeKey("1")
                        .build();
                """;
        return Demos.sectionWithCode("3. 禁用面板",
                "panel 第四个参数 disabled=true 禁止该面板展开/收起。", code, collapse);
    }
}
