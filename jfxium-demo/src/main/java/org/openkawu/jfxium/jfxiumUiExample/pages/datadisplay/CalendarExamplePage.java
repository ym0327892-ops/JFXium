package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import org.openkawu.jfxium.component.CalendarAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

import java.time.LocalDate;

/**
 * Calendar 日历 —— 基础月视图 / 选中回调。
 */
public class CalendarExamplePage extends VBoxAnt {

    public CalendarExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Calendar 日历")
                .description("按月展示日期面板，支持上下月导航与日期选择。")
                .sections(basicSection(), selectSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = CalendarAnt.create()
                .value(LocalDate.now())
                .fullscreen(false)
                .build();
        String code = """
                CalendarAnt.create()
                        .value(LocalDate.now())
                        .fullscreen(false)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础月视图",
                "value 指定显示月份，header 的 < / > 切换月份。", code, demo);
    }

    private Node selectSection() {
        Node demo = CalendarAnt.create()
                .value(LocalDate.now())
                .selectedDate(LocalDate.now())
                .fullscreen(false)
                .onSelect(date -> System.out.println("选中: " + date))
                .build();
        String code = """
                CalendarAnt.create()
                        .value(LocalDate.now())
                        .selectedDate(LocalDate.now())
                        .onSelect(date -> System.out.println("选中: " + date))
                        .build();
                """;
        return Demos.sectionWithCode("2. 选中日期",
                "selectedDate 高亮初始选中项，onSelect 处理点击日期。", code, demo);
    }
}
