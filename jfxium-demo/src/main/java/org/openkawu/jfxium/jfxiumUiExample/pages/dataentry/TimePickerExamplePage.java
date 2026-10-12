package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import java.time.LocalTime;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.TimePickerAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * TimePicker 时间选择 —— 点击输入框弹出面板，时 / 分 / 秒各一条可滚动列表直接点选。
 *
 * <p>M21.29 重构后不再使用原生 {@code Spinner} 步进器；M21.37 起面板回到 Ant 的
 * <b>三列形态</b>：时 {@code 00-23} / 分 {@code 00-59} / 秒 {@code 00-59} 各一条可滚动列表，
 * 列高恒为 8 行、当前值自动滚进视野。format 控制出现哪些段（时 / 分 / 秒 / 上午下午），
 * 页脚「此刻 / 确定」提交。</p>
 */
public class TimePickerExamplePage extends VBoxAnt {

    public TimePickerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("TimePicker 时间选择")
                .description("点击输入框弹出的面板中选择时 / 分 / 秒，每段两列数字直接点选，format 控制显示精度。")
                .sections(basicSection(), keyboardSection(), formatSection(), valueSection(),
                        meridiemSection(), pickedValueSection(), controllerSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = TimePickerAnt.create()
                .onChange(time -> MessageAnt.info("当前时间：" + time))
                .build();
        String code = """
                TimePickerAnt.create()
                        .onChange(time -> MessageAnt.info("当前时间：" + time))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "默认 HH:mm:ss；点击输入框弹出面板，时 / 分 / 秒各两列（十位 / 个位）直接点选，底部「确定」提交。",
                code, demo);
    }

    private Node keyboardSection() {
        Node live = Demos.row(
                TimePickerAnt.create().format("HH:mm:ss").value(LocalTime.of(9, 30, 0)).build(),
                TimePickerAnt.create().format("HH:mm").value(LocalTime.of(9, 30)).build(),
                TimePickerAnt.create().format("hh:mm a").value(LocalTime.of(9, 30)).build(),
                TimePickerAnt.create().editable(false).value(LocalTime.of(9, 30)).build());
        String code = """
                TimePickerAnt.create().value(LocalTime.of(9, 30)).build()          // 可键盘直录
                TimePickerAnt.create().format("HH:mm").value(LocalTime.of(9, 30)).build()
                TimePickerAnt.create().format("hh:mm a").value(LocalTime.of(9, 30)).build()
                TimePickerAnt.create().editable(false).value(LocalTime.of(9, 30)).build()  // 只读
                """;
        return Demos.sectionWithCode("2. 键盘直接录入",
                "聚焦后直接敲数字：2345 即 23:45（段内首位即定值，整段替换——直接敲 2 得 2 时，"
                        + "再敲 3 成 23 时）。光标停在哪一段，敲下去就改那一段；段满后继续敲进位到下一段。"
                        + "←/→ 每按一次动一格，↑/↓ 步进（Shift 为 ±10），Backspace 逐位回退，"
                        + "Enter 提交，Esc 还原；粘贴 23:45:00 也能直接贴进来。"
                        + "面板开着时格子会跟着键盘录入同步高亮。12 小时制数字位不含「上午/下午」，"
                        + "末一个为 editable(false) 的只读样例。",
                code, live);
    }

    private Node formatSection() {
        Node demo = Demos.row(
                TimePickerAnt.create().format("HH:mm:ss").build(),
                TimePickerAnt.create().format("HH:mm").build(),
                TimePickerAnt.create().format("HH").build());
        String code = """
                TimePickerAnt.create().format("HH:mm:ss").build()   // 时:分:秒（默认）
                TimePickerAnt.create().format("HH:mm").build()      // 时:分
                TimePickerAnt.create().format("HH").build()         // 仅时
                """;
        return Demos.sectionWithCode("3. 显示精度",
                "format 控制出现哪些段：HH:mm:ss → 时/分/秒三段（6 列）、HH:mm → 两段（4 列）、HH → 一段（2 列）；每段都是十位 + 个位两列。",
                code, demo);
    }

    private Node valueSection() {
        Node demo = Demos.row(
                TimePickerAnt.create().value(LocalTime.of(9, 30)).build(),
                TimePickerAnt.create().value(LocalTime.of(18, 0, 45)).build(),
                TimePickerAnt.create().build());
        String code = """
                TimePickerAnt.create().value(LocalTime.of(9, 30)).build()
                TimePickerAnt.create().value(LocalTime.of(18, 0, 45)).build()
                TimePickerAnt.create().build()   // 未设值 → 显示 placeholder
                """;
        return Demos.sectionWithCode("4. 默认值与空值",
                "value(LocalTime) 指定初始时间；不设值时显示占位提示，鼠标悬停出现清除按钮。",
                code, demo);
    }

    private Node meridiemSection() {
        Label result = TypographyAnt.text("上午 09:30").build();
        Node demo = Demos.column(
                TimePickerAnt.create()
                        .format("HH:mm a")
                        .value(LocalTime.of(9, 30))
                        .onChange(t -> result.setText(t == null ? "未选择"
                                : (t.getHour() < 12 ? "上午 " : "下午 ") + t))
                        .build(),
                result);
        String code = """
                TimePickerAnt.create()
                        .format("HH:mm a")   // 追加上午/下午列，时列变为 1-12
                        .value(LocalTime.of(9, 30))
                        .build();
                """;
        return Demos.sectionWithCode("5. 12 小时制",
                "format 里带上 a 即出现「时段」列；值内部仍存 24 小时制，展示时自动换算。",
                code, demo);
    }

    private Node pickedValueSection() {
        Label result = TypographyAnt.text("当前时间：（未选择）").build();
        Node picker = TimePickerAnt.create()
                .value(LocalTime.of(9, 30))
                .onChange(time -> result.setText("当前时间：" + time))
                .build();
        Node demo = Demos.column(picker, result);
        String code = """
                Label result = TypographyAnt.text("当前时间：（未选择）").build();
                TimePickerAnt.create()
                        .value(LocalTime.of(9, 30))
                        .onChange(time -> result.setText("当前时间：" + time))
                        .build();
                """;
        return Demos.sectionWithCode("6. 获取选中时间",
                "onChange(time -> ...) 给出提交后的 LocalTime；点「确定」时触发，清除时回调 null。",
                code, demo);
    }

    private Node controllerSection() {
        Node picker = TimePickerAnt.create().value(LocalTime.of(9, 30)).build();
        TimePickerAnt.Controller ctrl = TimePickerAnt.controllerOf(picker);
        Node demo = Demos.column(picker, Demos.row(
                ButtonAnt.create("设为 08:00").onClick(e -> ctrl.setValue(LocalTime.of(8, 0))).build(),
                ButtonAnt.create("设为 18:30").onClick(e -> ctrl.setValue(LocalTime.of(18, 30))).build(),
                ButtonAnt.create("打开面板").onClick(e -> ctrl.showPanel()).build(),
                ButtonAnt.create("读取值").onClick(e -> MessageAnt.info("当前值：" + ctrl.getValue())).build()));
        String code = """
                Node picker = TimePickerAnt.create().value(LocalTime.of(9, 30)).build();
                TimePickerAnt.Controller ctrl = TimePickerAnt.controllerOf(picker);
                ctrl.setValue(LocalTime.of(8, 0));   // 构建后改值
                ctrl.showPanel();                    // 程序化展开面板
                ctrl.getValue();                     // 读取当前值
                """;
        return Demos.sectionWithCode("7. 构建后改值（Controller）",
                "controllerOf(node) 取回控制器，可在构建后改值 / 展开面板 / 读值，无需重建组件。",
                code, demo);
    }
}
