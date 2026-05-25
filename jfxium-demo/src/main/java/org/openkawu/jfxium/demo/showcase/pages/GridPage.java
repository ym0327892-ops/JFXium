package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Grid 栅格 / 自动 wrap 展示页（M19.21 简化版）。
 *
 * <p><b>桌面应用响应式实战说明</b>：</p>
 * <p>Web 上常见的 xs/sm/md/lg/xl/xxl 6 档断点在 JavaFX 桌面应用里几乎用不到 ——
 * 桌面窗口 1280×800 起步，跑到 lg 阈值（992px）以下的概率极低；真正有用的是
 * <b>"卡片数量不定、宽度固定，自动 wrap"</b> 这种模式。</p>
 *
 * <p>本页因此只展示一个高频场景：用 JavaFX 原生 <b>FlowPane</b> 实现 dashboard 风格自动 wrap。
 * 如果你确实需要 24 列栅格 + 响应式断点，{@code GridAnt.create().responsive()} 完整 API 仍然可用，
 * 详见 GridAnt 类 javadoc。</p>
 */
public class GridPage implements ShowcasePage {

    @Override public String   key()      { return "grid"; }
    @Override public String   title()    { return "Grid 栅格 / Wrap"; }
    @Override public Category category() { return Category.LAYOUT; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Grid 栅格 / Wrap");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label(
                "桌面应用响应式实战 —— 卡片数量不定、宽度固定、容器变窄自动换行。" +
                "用 JavaFX 原生 FlowPane 实现，比 24 列栅格更直接。"
        );
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");
        pageDesc.setWrapText(true);

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionAutoWrap(),
                        sectionAutoWrapManyCards(),
                        sectionApiNote()
                )
                .build();
    }

    /** 4 张卡 + FlowPane 自动 wrap —— 拖窗口看效果 */
    private Node sectionAutoWrap() {
        FlowPane flow = new FlowPane();
        flow.setHgap(16);
        flow.setVgap(16);
        flow.setPrefWrapLength(0);
        flow.getChildren().addAll(
                statCard("总用户", "8,492", "↑ 12.5%", true),
                statCard("今日订单", "234", "↓ 3.2%", false),
                statCard("月销售额", "¥125k", "↑ 8.4%", true),
                statCard("转化率", "23.4%", "↑ 1.2%", true)
        );

        return ShowcaseSection.create()
                .title("Dashboard 4 张统计卡 —— 拖窗口看 wrap 效果")
                .description("窗口宽时一排 4 张；变窄时自动 2 张 / 1 张。卡片本身固定 220px 不变形")
                .demo(flow)
                .code("""
                        // 不需要任何栅格组件，JavaFX 原生 FlowPane 即可
                        FlowPane flow = new FlowPane();
                        flow.setHgap(16);
                        flow.setVgap(16);
                        flow.getChildren().addAll(
                            statCard("总用户", "8,492", "↑ 12.5%", true),
                            statCard("今日订单", "234", "↓ 3.2%", false),
                            statCard("月销售额", "¥125k", "↑ 8.4%", true),
                            statCard("转化率", "23.4%", "↑ 1.2%", true)
                        );
                        // 卡片自身设固定 prefWidth；FlowPane 自动按容器宽度 wrap
                        """)
                .build();
    }

    /** 12 张卡的更密集场景 —— 同样一行/两行/三行自动 wrap */
    private Node sectionAutoWrapManyCards() {
        FlowPane flow = new FlowPane();
        flow.setHgap(12);
        flow.setVgap(12);
        flow.setPrefWrapLength(0);
        for (int i = 1; i <= 12; i++) {
            flow.getChildren().add(simpleTile("Tile " + i));
        }

        return ShowcaseSection.create()
                .title("12 张瓦片 —— 数量不定时的标准模式")
                .description("拖窗口宽度看 wrap 效果。瓦片宽度固定 140px，容器宽度自动决定每排几张")
                .demo(flow)
                .code("""
                        FlowPane flow = new FlowPane();
                        flow.setHgap(12);
                        flow.setVgap(12);
                        for (Item item : items) {
                            flow.getChildren().add(buildTile(item));
                        }
                        """)
                .build();
    }

    private Node sectionApiNote() {
        StringBuilder note = new StringBuilder();
        note.append("// 桌面应用建议\n");
        note.append("//   - 大多数场景用 FlowPane（自动 wrap）/ HBox+VBox（固定布局）就够\n");
        note.append("//   - GridAnt 24 列栅格仅在你确实需要 \"label-input 表单 6+18 比例\"\n");
        note.append("//     或 \"详情页 16+8 主辅栏\" 这种精确比例时才用\n\n");
        note.append("// GridAnt 24 列基础用法（响应式断点桌面应用一般用不到）\n");
        note.append("VBox grid = GridAnt.create()\n");
        note.append("    .gutter(16)\n");
        note.append("    .row(GridAnt.row()\n");
        note.append("        .col(16, mainContent)        // 16/24 = 66.7%\n");
        note.append("        .col(8, asidePanel))         //  8/24 = 33.3%\n");
        note.append("    .build();\n\n");
        note.append("// 完整响应式 API（GridAnt.create().responsive() + xs/sm/md/lg/xl/xxl）\n");
        note.append("// 仍然保留，详见 GridAnt javadoc，需要时再用");

        return ShowcaseSection.create()
                .title("API 说明")
                .description("GridAnt 与 FlowPane 各自适用场景")
                .demo(new Label(""))
                .code(note.toString())
                .build();
    }

    // ============================================================
    // 内部工具
    // ============================================================

    private Node statCard(String title, String value, String trend, boolean up) {
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-fg-muted;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 24px; -fx-font-weight: 700;");
        Label tr = new Label(trend);
        tr.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                (up ? "-color-success-emphasis" : "-color-danger-emphasis") + ";");
        VBox body = new VBox(6, t, v, tr);
        body.setStyle("-fx-padding: 16;");
        Node card = CardAnt.create().bordered(true).content(body).build();
        if (card instanceof Region r) {
            r.setMinWidth(220);
            r.setPrefWidth(220);
        }
        return card;
    }

    private Node simpleTile(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: -color-fg-default;");
        VBox body = new VBox(l);
        body.setStyle("-fx-padding: 24; -fx-alignment: center;");
        Node card = CardAnt.create().bordered(true).content(body).build();
        if (card instanceof Region r) {
            r.setMinWidth(140);
            r.setPrefWidth(140);
        }
        return card;
    }
}
