package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import org.openkawu.jfxium.component.DatePickerAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

import java.time.LocalDate;

/**
 * DatePicker 日期选择 —— 基础 / 禁用 / 占位文字。
 */
public class DatePickerExamplePage extends VBoxAnt {

    public DatePickerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("DatePicker 日期选择器")
                .description("点击输入框弹出日历面板，选择日期。支持尺寸、占位文字等配置。")
                .sections(basicSection(), disabledSection(), placeholderSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        DatePicker dp = DatePickerAnt.create()
                .value(LocalDate.now())
                .build();
        String code = """
                DatePickerAnt.create()
                        .value(LocalDate.now())
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "默认展示当前日期。", code, dp);
    }

    private Node disabledSection() {
        DatePicker dp = DatePickerAnt.create()
                .value(LocalDate.of(2025, 1, 1))
                .editable(false)
                .build();
        dp.setDisable(true);
        String code = """
                DatePicker dp = DatePickerAnt.create()
                        .value(LocalDate.of(2025, 1, 1))
                        .editable(false)
                        .build();
                dp.setDisable(true);
                """;
        return Demos.sectionWithCode("2. 禁用状态", "setDisable(true) 禁止交互。", code, dp);
    }

    private Node placeholderSection() {
        DatePicker small = DatePickerAnt.create()
                .placeholder("选择日期")
                .size(DatePickerAnt.Size.SMALL)
                .build();
        DatePicker normal = DatePickerAnt.create()
                .placeholder("请选择日期")
                .build();
        DatePicker large = DatePickerAnt.create()
                .placeholder("Pick a date")
                .size(DatePickerAnt.Size.LARGE)
                .build();
        Node demo = Demos.row(small, normal, large);
        String code = """
                DatePickerAnt.create().placeholder("选择日期").size(DatePickerAnt.Size.SMALL).build();
                DatePickerAnt.create().placeholder("请选择日期").build();
                DatePickerAnt.create().placeholder("Pick a date").size(DatePickerAnt.Size.LARGE).build();
                """;
        return Demos.sectionWithCode("3. 占位文字与尺寸",
                "placeholder 设置提示文字；size 支持 SMALL / DEFAULT / LARGE 三档。", code, demo);
    }
}
