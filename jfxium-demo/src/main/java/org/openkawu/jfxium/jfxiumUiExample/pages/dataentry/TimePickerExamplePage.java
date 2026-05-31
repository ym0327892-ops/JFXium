package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import java.time.LocalTime;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.TimePickerAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * TimePicker 时间选择 —— 基础 / 时分格式 / 默认值。
 */
public class TimePickerExamplePage extends VBoxAnt {

    public TimePickerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("TimePicker 时间选择")
                .description("用步进器选择时 / 分 / 秒，format 控制显示精度。")
                .sections(basicSection(), formatSection(), valueSection(), pickedValueSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = TimePickerAnt.create()
                .onChange(time -> {})
                .build();
        String code = """
                TimePickerAnt.create()
                        .onChange(time -> System.out.println(time))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "默认 HH:mm:ss，三个步进器分别调整时 / 分 / 秒。",
                code, demo);
    }

    private Node formatSection() {
        Node demo = TimePickerAnt.create()
                .format("HH:mm")
                .build();
        String code = """
                TimePickerAnt.create()
                        .format("HH:mm")   // 只显示时:分
                        .build();
                """;
        return Demos.sectionWithCode("2. 时分格式",
                "format(\"HH:mm\") 隐藏秒，只保留时与分两段步进器。",
                code, demo);
    }

    private Node valueSection() {
        Node demo = TimePickerAnt.create()
                .value(LocalTime.of(9, 30, 0))
                .build();
        String code = """
                TimePickerAnt.create()
                        .value(LocalTime.of(9, 30, 0))
                        .build();
                """;
        return Demos.sectionWithCode("3. 默认值",
                "value(LocalTime) 指定初始时间。",
                code, demo);
    }

    /**
     * 4. 获取选中时间 —— onChange 回调拿到用户调整后的 LocalTime。
     *
     * <p>注：此前 TimePickerAnt 的 onChange 字段从未被任何 spinner 触发（死回调），
     * M19.42 已修复为「时/分/秒任一 spinner 变化都汇聚成 LocalTime 回调」。
     * 这里拨动任意步进器，结果 Label 即实时显示当前时间。</p>
     */
    private Node pickedValueSection() {
        Label result = new Label("当前时间：09:30:00");
        Node picker = TimePickerAnt.create()
                .value(LocalTime.of(9, 30, 0))
                .onChange(time -> result.setText("当前时间：" + time))
                .build();
        Node demo = Demos.column(picker, result);
        String code = """
                Label result = new Label("当前时间：09:30:00");
                TimePickerAnt.create()
                        .value(LocalTime.of(9, 30, 0))
                        .onChange(time -> result.setText("当前时间：" + time))
                        .build();
                """;
        return Demos.sectionWithCode("4. 获取选中时间",
                "onChange(time -> ...) 给出当前 LocalTime；拨动任意时/分/秒步进器即更新结果 Label。",
                code, demo);
    }
}
