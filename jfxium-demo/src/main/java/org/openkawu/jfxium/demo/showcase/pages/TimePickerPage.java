package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.TimePickerAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.time.LocalTime;

/**
 * TimePicker 时间选择展示页（M19.12）。
 */
public class TimePickerPage implements ShowcasePage {

    @Override public String   key()      { return "time-picker"; }
    @Override public String   title()    { return "TimePicker 时间选择"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("TimePicker 时间选择");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("挑时间 —— 排班 / 营业时间 / 提醒时间。支持 24h / 12h 多种格式。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionDefaultValue(),
                        sectionFormat(),
                        sectionDisabled()
                )
                .build();
    }

    private Node sectionBasic() {
        Node t = TimePickerAnt.create()
                .onChange(time -> MessageAnt.info("选中时间：" + time))
                .build();
        return ShowcaseSection.create()
                .title("场景 1：基础（默认 24h 格式）")
                .description("默认 HH:mm:ss")
                .demo(t)
                .code("""
                        TimePickerAnt.create()
                            .onChange(time -> handle(time))
                            .build();
                        """)
                .build();
    }

    private Node sectionDefaultValue() {
        Node t = TimePickerAnt.create()
                .value(LocalTime.of(9, 30))
                .build();
        return ShowcaseSection.create()
                .title("场景 2：默认值（9:30）")
                .description(".value(LocalTime) —— 表单回显已有值")
                .demo(t)
                .code("""
                        TimePickerAnt.create()
                            .value(LocalTime.of(9, 30))
                            .build();
                        """)
                .build();
    }

    private Node sectionFormat() {
        Node hm = TimePickerAnt.create()
                .value(LocalTime.of(14, 30))
                .format("HH:mm")
                .build();
        Node hms = TimePickerAnt.create()
                .value(LocalTime.of(14, 30, 45))
                .format("HH:mm:ss")
                .build();
        Node h12 = TimePickerAnt.create()
                .value(LocalTime.of(14, 30))
                .format("hh:mm a")
                .build();

        HBox row = HBoxBuilder.create().spacing(12).children(hm, hms, h12).build();

        return ShowcaseSection.create()
                .title("场景 3：自定义格式")
                .description(".format(\"HH:mm\") / \"HH:mm:ss\" / \"hh:mm a\" — 12h 制带 AM/PM")
                .demo(row)
                .code("""
                        TimePickerAnt.create().format("HH:mm").build();
                        TimePickerAnt.create().format("HH:mm:ss").build();
                        TimePickerAnt.create().format("hh:mm a").build();
                        """)
                .build();
    }

    private Node sectionDisabled() {
        Node t = TimePickerAnt.create()
                .value(LocalTime.of(18, 0))
                .disabled(true)
                .build();
        return ShowcaseSection.create()
                .title("场景 4：禁用态")
                .description("disabled(true) —— 仅展示，不可修改")
                .demo(t)
                .code("""
                        TimePickerAnt.create()
                            .value(LocalTime.of(18, 0))
                            .disabled(true)
                            .build();
                        """)
                .build();
    }
}
