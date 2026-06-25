package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Grid 栅格 —— 对齐 Ant Design Grid 文档风格（蓝色交替色块 + 多行对比）。
 */
public class GridExamplePage extends VBoxAnt {

    public GridExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Grid 栅格")
                .description("24 栅格系统，先看响应式再看固定布局，拖动窗口能直接看到列数变化。")
                .sections(
                        responsiveSection(),
                        basicSection(),
                        offsetSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 响应式栅格：缩放窗口直接看 4 列 / 2 列 / 1 列变化。 */
    private Node responsiveSection() {
        Node hint = VBoxAnt.create()
                .spacing(4)
                .padding(0)
                .children(
                        TypographyAnt.text("把窗口从大拖到小，下面三组卡片会跟着换列。")
                                .type(TypographyAnt.TextColor.SECONDARY)
                                .build(),
                        TypographyAnt.text("重点看 `GridAnt.col(node).responsive(24, 12, 12, 6, 6, 6)` 这一组快捷写法。")
                                .type(TypographyAnt.TextColor.SECONDARY)
                                .build()
                )
                .build();
        hint.getStyleClass().add("jfx-demo-grid-hint");

        Node demo = GridAnt.create()
                .gutter(16)
                .responsive()
                .row(GridAnt.row()
                        .col(GridAnt.col(responsiveCard("订单中心", "XS 24 | SM 12 | MD 12 | LG 6", true)).responsive(24, 12, 12, 6, 6, 6))
                        .col(GridAnt.col(responsiveCard("统计看板", "XS 24 | SM 12 | MD 12 | LG 6", false)).responsive(24, 12, 12, 6, 6, 6))
                        .col(GridAnt.col(responsiveCard("审批任务", "XS 24 | SM 12 | MD 12 | LG 6", true)).responsive(24, 12, 12, 6, 6, 6))
                        .col(GridAnt.col(responsiveCard("最近活动", "XS 24 | SM 12 | MD 12 | LG 6", false)).responsive(24, 12, 12, 6, 6, 6)))
                .row(GridAnt.row()
                        .col(GridAnt.col(responsiveCard("详情主区", "XS 24 | SM 24 | MD 12 | LG 8", true)).responsive(24, 24, 12, 8, 8, 8))
                        .col(GridAnt.col(responsiveCard("辅助侧栏 A", "XS 24 | SM 24 | MD 12 | LG 8", false)).responsive(24, 24, 12, 8, 8, 8))
                        .col(GridAnt.col(responsiveCard("辅助侧栏 B", "XS 24 | SM 24 | MD 12 | LG 8", true)).responsive(24, 24, 12, 8, 8, 8)))
                .row(GridAnt.row()
                        .col(GridAnt.col(responsiveCard("单栏表单 A", "XS 24 | SM 24 | MD 24 | LG 12", true)).responsive(24, 24, 24, 12, 12, 12))
                        .col(GridAnt.col(responsiveCard("单栏表单 B", "XS 24 | SM 24 | MD 24 | LG 12", false)).responsive(24, 24, 24, 12, 12, 12)))
                .build();
        String code = """
                GridAnt.create()
                        .gutter(16)
                        .responsive()
                        .row(GridAnt.row()
                                .col(GridAnt.col(orderCard).responsive(24, 12, 12, 6, 6, 6))
                                .col(GridAnt.col(statsCard).responsive(24, 12, 12, 6, 6, 6))
                                .col(GridAnt.col(taskCard).responsive(24, 12, 12, 6, 6, 6))
                                .col(GridAnt.col(activityCard).responsive(24, 12, 12, 6, 6, 6)))
                        .row(GridAnt.row()
                                .col(GridAnt.col(mainSurface).responsive(24, 24, 12, 8, 8, 8))
                                .col(GridAnt.col(sideA).responsive(24, 24, 12, 8, 8, 8))
                                .col(GridAnt.col(sideB).responsive(24, 24, 12, 8, 8, 8)))
                        .row(GridAnt.row()
                                .col(GridAnt.col(formA).responsive(24, 24, 24, 12, 12, 12))
                                .col(GridAnt.col(formB).responsive(24, 24, 24, 12, 12, 12)))
                        .build();
                """;
        return Demos.sectionWithCode("1. 响应式栅格（缩放窗口看变化）",
                "这一节最重要：窄屏自动单列，中屏双列，桌面再回到多列。你缩小窗口时变化会很直观。",
                code,
                hint,
                demo);
    }

    /** 2. 基础栅格：多行对比（2 列 / 3 列 / 4 列 / 不等分）。 */
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
        return Demos.sectionWithCode("2. 基础栅格（固定等分）",
                "从上到下：2 等分（col-12）、3 等分（col-8）、4 等分（col-6）。",
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

    /** 4. PlayGround：实时调整列间距 + 切换常用 row 布局（2/3/4 等分、带偏移）。 */
    private Node playgroundSection() {
        Binder<String> gutter = PlayGround.binder("16");
        Binder<String> layout = PlayGround.binder("2col");
        return PlayGround.rebindRebuild(
                () -> buildGrid(gutter.get(), layout.get()),
                "列间距 / 行布局",
                PlayGround.row("列间距 gutter", PlayGround.textField(gutter, "16", "列与列之间 px")),
                PlayGround.row("行布局", PlayGround.segmented(layout,
                        PlayGround.entry("2col",   "2 等分"),
                        PlayGround.entry("3col",   "3 等分"),
                        PlayGround.entry("4col",   "4 等分"),
                        PlayGround.entry("offset", "偏移 8"))));
    }

    private Node responsiveCard(String title, String detail, boolean dark) {
        Label titleLabel = TypographyAnt.text(title).build();
        Label detailLabel = TypographyAnt.text(detail)
                .type(TypographyAnt.TextColor.SECONDARY)
                .build();
        titleLabel.getStyleClass().add("jfx-demo-grid-card-title");
        detailLabel.getStyleClass().add("jfx-demo-grid-card-detail");

        VBoxAnt pane = VBoxAnt.create()
                .spacing(6)
                .padding(16)
                .align(Pos.CENTER)
                .children(titleLabel, detailLabel);
        pane.getStyleClass().add("jfx-demo-grid-card");
        pane.getStyleClass().add("jfx-demo-col-block");
        pane.getStyleClass().add(dark ? "jfx-demo-col-dark" : "jfx-demo-col-light");
        pane.setMinHeight(92);
        pane.setMaxWidth(Double.MAX_VALUE);
        return pane;
    }

    private Node buildGrid(String gutterStr, String layout) {
        double g;
        try { g = Math.max(0, Double.parseDouble(gutterStr)); }
        catch (NumberFormatException e) { g = 16.0; }
        GridAnt.Builder b = GridAnt.create().gutter(g);
        switch (layout) {
            case "3col" -> b.row(GridAnt.row()
                    .col(8, Demos.colBlock("col-8", true))
                    .col(8, Demos.colBlock("col-8", false))
                    .col(8, Demos.colBlock("col-8", true)));
            case "4col" -> b.row(GridAnt.row()
                    .col(6, Demos.colBlock("col-6", true))
                    .col(6, Demos.colBlock("col-6", false))
                    .col(6, Demos.colBlock("col-6", true))
                    .col(6, Demos.colBlock("col-6", false)));
            case "offset" -> b.row(GridAnt.row()
                    .col(8, Demos.colBlock("col-8", true))
                    .col(8, 8, Demos.colBlock("col-8 offset-8", false)));
            default -> b.row(GridAnt.row()
                    .col(12, Demos.colBlock("col-12", true))
                    .col(12, Demos.colBlock("col-12", false)));
        }
        return b.build();
    }
}
