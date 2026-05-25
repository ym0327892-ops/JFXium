package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.DatePickerAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.time.LocalDate;

/**
 * DatePicker 展示页（M19.11）。
 */
public class DatePickerPage implements ShowcasePage {

    @Override public String   key()      { return "date-picker"; }
    @Override public String   title()    { return "DatePicker 日期选择"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("DatePicker 日期选择");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("挑日期 —— 表单 / 筛选器高频。M19.5 加 Size + 修对默认 padding。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionPlaceholder(),
                        sectionSizes(),
                        sectionEditableToggle(),
                        sectionWeekNumbers()
                )
                .build();
    }

    private Node sectionBasic() {
        DatePicker dp = DatePickerAnt.create()
                .value(LocalDate.now())
                .onChange(date -> MessageAnt.info("选中：" + date))
                .build();
        return ShowcaseSection.create()
                .title("场景 1：基础用法（默认今天）")
                .description("初始 .value(today) + .onChange 回调")
                .demo(dp)
                .code("""
                        DatePicker dp = DatePickerAnt.create()
                            .value(LocalDate.now())
                            .onChange(date -> reload(date))
                            .build();
                        """)
                .build();
    }

    private Node sectionPlaceholder() {
        DatePicker dp = DatePickerAnt.create()
                .placeholder("请选择日期")
                .build();
        return ShowcaseSection.create()
                .title("场景 2：占位符")
                .description("未选中时显示提示文字")
                .demo(dp)
                .code("""
                        DatePickerAnt.create().placeholder("请选择日期").build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        DatePicker small = DatePickerAnt.create().placeholder("Small").size(DatePickerAnt.Size.SMALL).build();
        DatePicker def = DatePickerAnt.create().placeholder("Default").build();
        DatePicker large = DatePickerAnt.create().placeholder("Large").size(DatePickerAnt.Size.LARGE).build();

        HBox row = HBoxBuilder.create().spacing(8).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 3：三档尺寸（M19.5 与 Input/Combo 完全对齐）")
                .description("混排时高度齐平 —— 同一行 Input + DatePicker + ComboBox 不会跑位")
                .demo(row)
                .code("""
                        DatePickerAnt.create().size(DatePickerAnt.Size.SMALL).build();
                        DatePickerAnt.create().build();
                        DatePickerAnt.create().size(DatePickerAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionEditableToggle() {
        DatePicker editable = DatePickerAnt.create().placeholder("可手动输入").editable(true).build();
        DatePicker readonly = DatePickerAnt.create().placeholder("仅可点选").editable(false).build();

        HBox row = HBoxBuilder.create().spacing(8).children(editable, readonly).build();

        return ShowcaseSection.create()
                .title("场景 4：editable 开关")
                .description("editable(true) 允许用户手动输入；false 时仅能从面板选择")
                .demo(row)
                .code("""
                        DatePickerAnt.create().editable(true).build();   // 默认
                        DatePickerAnt.create().editable(false).build();  // 仅面板选择
                        """)
                .build();
    }

    private Node sectionWeekNumbers() {
        DatePicker dp = DatePickerAnt.create()
                .placeholder("带周数")
                .showWeekNumbers(true)
                .build();
        return ShowcaseSection.create()
                .title("场景 5：显示周数")
                .description(".showWeekNumbers(true) —— 排班/财务等按周计算的场景")
                .demo(dp)
                .code("""
                        DatePickerAnt.create().showWeekNumbers(true).build();
                        """)
                .build();
    }
}
