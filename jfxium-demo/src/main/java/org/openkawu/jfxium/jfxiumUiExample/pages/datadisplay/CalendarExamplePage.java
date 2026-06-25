package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

import java.time.LocalDate;
import org.openkawu.jfxium.component.composite.CalendarAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * Calendar 日历 —— 基础月视图 / 选中回调 / 交互演示。
 */
public class CalendarExamplePage extends VBoxAnt {

    private static final LocalDate TODAY = LocalDate.now();

    public CalendarExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Calendar 日历")
                .description("按月展示日期面板，支持上下月导航与日期选择。")
                .sections(basicSection(), selectSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = CalendarAnt.create()
                .value(TODAY)
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
                .value(TODAY)
                .selectedDate(TODAY)
                .fullscreen(false)
                .onSelect(date -> MessageAnt.info("选中: " + date))
                .build();
        String code = """
                CalendarAnt.create()
                        .value(LocalDate.now())
                        .selectedDate(LocalDate.now())
                        .onSelect(date -> MessageAnt.info("选中: " + date))
                        .build();
                """;
        return Demos.sectionWithCode("2. 选中日期",
                "selectedDate 高亮初始选中项，onSelect 处理点击日期。", code, demo);
    }

    // ============================================================
    // 3. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Calendar 的 3 个维度：显示月份 / 选中日期 / 视图模式。
     *
     * <p>CalendarAnt 提供静态 {@code controllerOf(Node)}，Controller 暴露
     * {@code setValue / setSelectedDate / setMode}，三者的实现都调用 {@code Builder#rebuild()}
     * —— 即只重建 header+body 两个子节点，VBox 容器不重建，display 节点引用始终有效。
     * 所以采用 {@link PlayGround#rebindController}：apply Runnable 内部调 controller.setXxx，
     * 无重建、无闪烁。</p>
     *
     * <p>说明：日期输入若开放自由文本，解析成本高且易错；故 playground 用「相对偏移」
     * segmented（如 今天 / +1月 / -1月 / +1年 / -1年）而非自由日期文本框。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> value        = PlayGround.binder("today");
        Binder<String> selectedDate = PlayGround.binder("today");
        Binder<String> mode         = PlayGround.binder("month");

        // 2. 预 build display 并取 controller
        Node calendar = CalendarAnt.create()
                .value(parseDateOffset(value.get()))
                .selectedDate(parseDateOffset(selectedDate.get()))
                .mode(parseMode(mode.get()))
                .fullscreen(false)
                .build();
        CalendarAnt.Controller controller = CalendarAnt.controllerOf(calendar);

        // 3. apply Runnable —— 读 binder → 调 controller.setXxx（重建 header+body）
        Runnable apply = () -> {
            LocalDate newValue = parseDateOffset(value.get());
            LocalDate newSelected = parseDateOffset(selectedDate.get());
            controller.setValue(newValue);
            controller.setSelectedDate(newSelected);
            controller.setMode(parseMode(mode.get()));
        };

        // 4. 串起来
        return Demos.section("3. 交互演示",
                "通过左侧控件实时改变 Calendar 的显示月份 / 选中日期 / 视图模式 —— Controller 在原 VBox 内重建 header+body，容器不重建。",
                PlayGround.rebindController(calendar, apply, null,
                        PlayGround.row("显示月份", PlayGround.segmented(value,
                                PlayGround.entry("today",     "今天"),
                                PlayGround.entry("minus1m",   "−1 月"),
                                PlayGround.entry("plus1m",    "+1 月"),
                                PlayGround.entry("plus1y",    "+1 年"),
                                PlayGround.entry("minus1y",   "−1 年"))),
                        PlayGround.row("选中日期", PlayGround.segmented(selectedDate,
                                PlayGround.entry("today",  "今天"),
                                PlayGround.entry("plus7",  "+7 天"),
                                PlayGround.entry("plus30", "+30 天"),
                                PlayGround.entry("none",   "清空"))),
                        PlayGround.row("视图模式", PlayGround.segmented(mode,
                                PlayGround.entry("month", "月视图"),
                                PlayGround.entry("year",  "年视图")))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static LocalDate parseDateOffset(String v) {
        if (v == null) return TODAY;
        return switch (v) {
            case "today"   -> TODAY;
            case "minus1m" -> TODAY.minusMonths(1);
            case "plus1m"  -> TODAY.plusMonths(1);
            case "minus1y" -> TODAY.minusYears(1);
            case "plus1y"  -> TODAY.plusYears(1);
            case "plus7"   -> TODAY.plusDays(7);
            case "plus30"  -> TODAY.plusDays(30);
            default        -> TODAY;
        };
    }

    private static CalendarAnt.Mode parseMode(String v) {
        if (v == null) return CalendarAnt.Mode.MONTH;
        return "year".equals(v) ? CalendarAnt.Mode.YEAR : CalendarAnt.Mode.MONTH;
    }
}
