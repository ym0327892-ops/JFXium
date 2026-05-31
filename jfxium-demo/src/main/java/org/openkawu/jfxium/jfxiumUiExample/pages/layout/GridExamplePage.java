package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Grid 栅格 —— 对齐 Ant Design Grid 文档风格（蓝色交替色块 + 多行对比）。
 */
public class GridExamplePage extends VBoxAnt {

    public GridExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Grid 栅格")
                .description("24 栅格系统，通过 row + col 快速搭建响应式布局。")
                .sections(
                        basicSection(),
                        gutterSection(),
                        offsetSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 基础栅格：多行对比（2 列 / 3 列 / 4 列 / 不等分）。 */
    private Node basicSection() {
        Node demo = GridAnt.create()
                .row(GridAnt.row()
                        .col(12, Demos.colBlock("col-12", true))
                        .col(12, Demos.colBlock("col-12", false)))
                .row(GridAnt.row()
                        .col(8, Demos.colBlock("col-8", true))
                        .col(8, Demos.colBlock("col-8", false))
                        .col(8, Demos.colBlock("col-8", true)))
                .row(GridAnt.row()
                        .col(6, Demos.colBlock("col-6", true))
                        .col(6, Demos.colBlock("col-6", false))
                        .col(6, Demos.colBlock("col-6", true))
                        .col(6, Demos.colBlock("col-6", false)))
                .build();
        String code = """
                GridAnt.create()
                        .row(GridAnt.row()
                                .col(12, node1).col(12, node2))
                        .row(GridAnt.row()
                                .col(8, node1).col(8, node2).col(8, node3))
                        .row(GridAnt.row()
                                .col(6, node1).col(6, node2).col(6, node3).col(6, node4))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础栅格",
                "从上到下：2 等分（col-12）、3 等分（col-8）、4 等分（col-6）。",
                code, demo);
    }

    /** 2. 列间距（Gutter）。 */
    private Node gutterSection() {
        Node demo = GridAnt.create()
                .gutter(16)
                .row(GridAnt.row()
                        .col(6, Demos.colBlock("col-6", true))
                        .col(6, Demos.colBlock("col-6", false))
                        .col(6, Demos.colBlock("col-6", true))
                        .col(6, Demos.colBlock("col-6", false)))
                .build();
        String code = """
                GridAnt.create()
                        .gutter(16)   // 列间距 16px
                        .row(GridAnt.row()
                                .col(6, node1)
                                .col(6, node2)
                                .col(6, node3)
                                .col(6, node4))
                        .build();
                """;
        return Demos.sectionWithCode("2. 列间距（Gutter）",
                "gutter(16) 设置列与列之间 16px 间距。推荐 (16 + 8n) px。",
                code, demo);
    }

    /** 3. 列偏移（Offset）。 */
    private Node offsetSection() {
        Node demo = GridAnt.create()
                .row(GridAnt.row()
                        .col(8, Demos.colBlock("col-8", true))
                        .col(8, 8, Demos.colBlock("col-8 offset-8", false)))
                .row(GridAnt.row()
                        .col(6, 6, Demos.colBlock("col-6 offset-6", true))
                        .col(6, 6, Demos.colBlock("col-6 offset-6", false)))
                .build();
        String code = """
                GridAnt.create()
                        .row(GridAnt.row()
                                .col(8, node1)
                                .col(8, 8, node2))   // span=8, offset=8
                        .row(GridAnt.row()
                                .col(6, 6, node1)    // span=6, offset=6
                                .col(6, 6, node2))
                        .build();
                """;
        return Demos.sectionWithCode("3. 列偏移（Offset）",
                "col(span, offset, node) —— offset 向右偏移指定栅格数。",
                code, demo);
    }
}
