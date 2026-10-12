package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import java.time.LocalDateTime;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.composite.DateTimePickerAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * DateTimePicker 日期时间选择 —— 左侧日历 + 右侧时间面板，共用一个页脚。
 */
public class DateTimePickerExamplePage extends VBoxAnt {

    public DateTimePickerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("DateTimePicker 日期时间")
                .description("日期面板与时间面板并排，选中日期 + 时刻后点「确定」提交。")
                .sections(basicSection(), keyboardSection(), formatSection(), valueSection(),
                        pickedValueSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = DateTimePickerAnt.create().build();
        String code = """
                DateTimePickerAnt.create().build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "默认 yyyy-MM-dd HH:mm:ss；点击输入框弹出「日历 + 时/分/秒」组合面板。",
                code, demo);
    }

    private Node keyboardSection() {
        Node live = Demos.row(
                DateTimePickerAnt.create()
                        .value(LocalDateTime.of(2026, 10, 11, 9, 30))
                        .format("yyyy-MM-dd HH:mm:ss").build(),
                DateTimePickerAnt.create()
                        .value(LocalDateTime.of(2026, 10, 11, 9, 30))
                        .format("yyyy-MM-dd HH:mm").build());
        String code = """
                DateTimePickerAnt.create()
                        .value(LocalDateTime.of(2026, 10, 11, 9, 30))
                        .format("yyyy-MM-dd HH:mm:ss")
                        .build();
                """;
        return Demos.sectionWithCode("2. 键盘直接录入时间",
                "聚焦后直接敲数字即可改时间部分：日期前缀只读、原样保留，"
                        + "Enter 提交（日期取已选日期，未选过则取今天）。日期仍由左侧日历负责，"
                        + "editable(false) 可关闭键盘录入。",
                code, live);
    }

    private Node formatSection() {
        Node demo = Demos.row(
                DateTimePickerAnt.create().format("yyyy-MM-dd HH:mm:ss").build(),
                DateTimePickerAnt.create().format("yyyy-MM-dd HH:mm").build());
        String code = """
                DateTimePickerAnt.create().format("yyyy-MM-dd HH:mm:ss").build()
                DateTimePickerAnt.create().format("yyyy-MM-dd HH:mm").build()
                """;
        return Demos.sectionWithCode("3. 显示精度",
                "format 同时控制日期与时间两部分的精度；时间部分无秒时，面板只出现时、分两列。",
                code, demo);
    }

    private Node valueSection() {
        Node demo = Demos.row(
                DateTimePickerAnt.create().value(LocalDateTime.of(2026, 10, 11, 9, 30)).build(),
                DateTimePickerAnt.create().build());
        String code = """
                DateTimePickerAnt.create()
                        .value(LocalDateTime.of(2026, 10, 11, 9, 30))
                        .build()
                DateTimePickerAnt.create().build()   // 未设值 → 显示 placeholder
                """;
        return Demos.sectionWithCode("4. 默认值与空值",
                "value(LocalDateTime) 指定初始值；不设值时显示占位提示。",
                code, demo);
    }

    private Node pickedValueSection() {
        Label result = TypographyAnt.text("选中：（未选择）").build();
        Node picker = DateTimePickerAnt.create()
                .value(LocalDateTime.of(2026, 10, 11, 9, 30))
                .format("yyyy-MM-dd HH:mm")
                .onChange(dt -> result.setText("选中：" + dt))
                .build();
        Node demo = Demos.column(picker, result);
        String code = """
                Label result = TypographyAnt.text("选中：（未选择）").build();
                DateTimePickerAnt.create()
                        .value(LocalDateTime.of(2026, 10, 11, 9, 30))
                        .format("yyyy-MM-dd HH:mm")
                        .onChange(dt -> result.setText("选中：" + dt))
                        .build();
                """;
        return Demos.sectionWithCode("5. 获取选中值",
                "onChange(dt -> ...) 在点「确定」时给出 LocalDateTime；清除时回调 null。",
                code, demo);
    }
}
