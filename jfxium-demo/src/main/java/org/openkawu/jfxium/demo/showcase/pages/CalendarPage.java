package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CalendarAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.time.LocalDate;

/**
 * Calendar 日历展示页（M19.11）。
 */
public class CalendarPage implements ShowcasePage {

    @Override public String   key()      { return "calendar"; }
    @Override public String   title()    { return "Calendar 日历"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Calendar 日历");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("整月日历视图 —— 与 DatePicker 区别：DatePicker 是输入控件（小弹层），Calendar 是大块视图（行程/排班）。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionFullscreen(),
                        sectionCard(),
                        sectionWithCallback(),
                        sectionYearMode()
                )
                .build();
    }

    private Node sectionFullscreen() {
        Node c = CalendarAnt.create()
                .value(LocalDate.now())
                .build();
        return ShowcaseSection.create()
                .title("场景 1：整页日历（默认 fullscreen=true）")
                .description("适合作为页面主内容 —— 日程管理 / 排班视图")
                .demo(c)
                .code("""
                        VBox cal = CalendarAnt.create()
                            .value(LocalDate.now())
                            .build();
                        """)
                .build();
    }

    private Node sectionCard() {
        Node c = CalendarAnt.create()
                .value(LocalDate.now())
                .fullscreen(false)
                .build();
        ((javafx.scene.layout.Region) c).setMaxWidth(360);

        return ShowcaseSection.create()
                .title("场景 2：紧凑卡片（fullscreen=false）")
                .description(".fullscreen(false) —— 嵌入侧边栏 / 仪表盘的小日历")
                .demo(c)
                .code("""
                        CalendarAnt.create()
                            .value(LocalDate.now())
                            .fullscreen(false)
                            .build();
                        """)
                .build();
    }

    private Node sectionWithCallback() {
        Node c = CalendarAnt.create()
                .value(LocalDate.now())
                .fullscreen(false)
                .onSelect(date -> MessageAnt.info("选中：" + date))
                .onPanelChange(date -> MessageAnt.info("面板切换：" + date))
                .build();
        ((javafx.scene.layout.Region) c).setMaxWidth(360);

        return ShowcaseSection.create()
                .title("场景 3：选择 / 面板切换回调")
                .description(".onSelect / .onPanelChange —— 联动右侧详情或加载该日数据")
                .demo(c)
                .code("""
                        CalendarAnt.create()
                            .value(LocalDate.now())
                            .onSelect(date -> reloadEvents(date))
                            .onPanelChange(month -> reloadMonthSummary(month))
                            .build();
                        """)
                .build();
    }

    private Node sectionYearMode() {
        Node c = CalendarAnt.create()
                .value(LocalDate.now())
                .mode(CalendarAnt.Mode.YEAR)
                .fullscreen(false)
                .build();
        ((javafx.scene.layout.Region) c).setMaxWidth(360);

        return ShowcaseSection.create()
                .title("场景 4：年视图（Mode.YEAR）")
                .description(".mode(YEAR) —— 一次显示 12 个月，适合年度概览")
                .demo(c)
                .code("""
                        CalendarAnt.create()
                            .value(LocalDate.now())
                            .mode(CalendarAnt.Mode.YEAR)
                            .build();
                        """)
                .build();
    }
}
