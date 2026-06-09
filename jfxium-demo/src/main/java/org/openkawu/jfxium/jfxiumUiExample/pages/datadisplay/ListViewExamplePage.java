package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.SelectionMode;

import org.openkawu.jfxium.component.control.ListViewAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * ListView 列表视图 —— 基础 / 多选 / 双击 / 禁用。
 */
public class ListViewExamplePage extends VBoxAnt {

    public ListViewExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ListView 列表视图")
                .description("列表选择控件，支持单选/多选、双击回调、固定行高等，用于文件列表、选项列表等场景。")
                .sections(
                        basicSection(),
                        multiSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        ListViewAnt<String> demo = ListViewAnt.<String>create()
                .items("项目 Alpha", "项目 Beta", "项目 Gamma", "项目 Delta", "项目 Epsilon")
                .selectionMode(SelectionMode.SINGLE)
                .onSelect(item -> System.out.println("选中: " + item))
                .onDoubleClick(item -> System.out.println("双击: " + item))
                .fixedCellSize(32)
                .build();
        demo.setPrefHeight(170);

        String code = """
                ListViewAnt.<String>create()
                    .items("项目 Alpha", "项目 Beta", "项目 Gamma")
                    .selectionMode(SelectionMode.SINGLE)
                    .onSelect(item -> System.out.println("选中: " + item))
                    .onDoubleClick(item -> System.out.println("双击: " + item))
                    .fixedCellSize(32)
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "items() 设置选项；onSelect() 单选回调；onDoubleClick() 双击回调；fixedCellSize() 优化虚拟化性能。",
                code, demo);
    }

    private Node multiSection() {
        Node demo = Demos.row(
                ListViewAnt.<String>create()
                        .items("文件 A.txt", "文件 B.docx", "文件 C.pdf",
                               "文件 D.xlsx", "文件 E.pptx", "文件 F.jpg")
                        .selectionMode(SelectionMode.MULTIPLE)
                        .fixedCellSize(32)
                        .build(),
                ListViewAnt.<String>create()
                        .items("选项一", "选项二", "选项三")
                        .selectionMode(SelectionMode.SINGLE)
                        .disabled(true)
                        .fixedCellSize(32)
                        .build()
        );
        demo.setStyle("-fx-spacing: 20;");
        String code = """
                // 多选模式
                ListViewAnt.<String>create()
                    .items("文件 A.txt", ...)
                    .selectionMode(SelectionMode.MULTIPLE)
                    .build();

                // 禁用模式
                ListViewAnt.<String>create()
                    .items("选项一", ...)
                    .disabled(true)
                    .build();
                """;
        return Demos.sectionWithCode("2. 多选与禁用",
                "selectionMode(MULTIPLE) 开启多选（Ctrl/Shift+Click）；disabled(true) 禁用列表。",
                code, demo);
    }
}
