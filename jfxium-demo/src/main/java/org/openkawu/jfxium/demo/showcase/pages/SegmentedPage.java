package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.SegmentedAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Segmented 分段控制器展示页（M19.14）。
 */
public class SegmentedPage implements ShowcasePage {

    @Override public String   key()      { return "segmented"; }
    @Override public String   title()    { return "Segmented 分段控制"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Segmented 分段控制");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("一组互斥按钮 —— 视图切换 / 类型筛选；与 RadioButton 同语义但视觉更紧凑。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithIcons(),
                        sectionSizes(),
                        sectionBlock()
                )
                .build();
    }

    private Node sectionBasic() {
        Node s = SegmentedAnt.create()
                .option("daily", "日")
                .option("weekly", "周")
                .option("monthly", "月")
                .option("yearly", "年")
                .selected("weekly")
                .onChange(v -> MessageAnt.info("切到：" + v))
                .build();
        return ShowcaseSection.create()
                .title("场景 1：基础（统计周期切换）")
                .description("admin dashboard 统计周期切换标配")
                .demo(s)
                .code("""
                        SegmentedAnt.create()
                            .option("daily", "日")
                            .option("weekly", "周")
                            .option("monthly", "月")
                            .selected("weekly")
                            .onChange(v -> reload(v))
                            .build();
                        """)
                .build();
    }

    private Node sectionWithIcons() {
        Node s = SegmentedAnt.create()
                .option("list", "列表", IconAnt.path(IconAnt.Path.FILE, 14))
                .option("card", "卡片", IconAnt.path(IconAnt.Path.DASHBOARD, 14))
                .option("tree", "树形", IconAnt.path(IconAnt.Path.CHART, 14))
                .selected("list")
                .build();
        return ShowcaseSection.create()
                .title("场景 2：带图标")
                .description(".option(value, label, icon) —— 视图切换最佳搭配")
                .demo(s)
                .code("""
                        SegmentedAnt.create()
                            .option("list", "列表", IconAnt.path(IconAnt.Path.FILE, 14))
                            .option("card", "卡片", IconAnt.path(IconAnt.Path.DASHBOARD, 14))
                            .selected("list")
                            .build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        Node small = SegmentedAnt.create().option("a", "Small").option("b", "Tab")
                .selected("a").size(SegmentedAnt.Size.SMALL).build();
        Node def = SegmentedAnt.create().option("a", "Default").option("b", "Tab")
                .selected("a").build();
        Node large = SegmentedAnt.create().option("a", "Large").option("b", "Tab")
                .selected("a").size(SegmentedAnt.Size.LARGE).build();

        HBox row = HBoxBuilder.create().spacing(8).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 3：三档尺寸")
                .description("SMALL / DEFAULT / LARGE")
                .demo(row)
                .code("""
                        SegmentedAnt.create().option(...).size(SegmentedAnt.Size.SMALL).build();
                        SegmentedAnt.create().option(...).build();
                        SegmentedAnt.create().option(...).size(SegmentedAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionBlock() {
        Node s = SegmentedAnt.create()
                .option("all", "全部")
                .option("active", "活跃")
                .option("archived", "已归档")
                .selected("active")
                .block()
                .build();
        return ShowcaseSection.create()
                .title("场景 4：Block 模式（占满父容器宽度）")
                .description(".block() —— 等宽分布，适合占满工具栏的过滤切换")
                .demo(s)
                .code("""
                        SegmentedAnt.create()
                            .option("all", "全部").option("active", "活跃").option("archived", "已归档")
                            .selected("active")
                            .block()
                            .build();
                        """)
                .build();
    }
}
