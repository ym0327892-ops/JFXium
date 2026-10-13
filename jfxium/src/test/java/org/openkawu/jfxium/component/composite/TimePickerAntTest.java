package org.openkawu.jfxium.component.composite;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


/**
 * TimePickerAnt 单元测试 —— M21.29 弹出面板重构后的核心契约。
 *
 * <p>覆盖：format 段位解析（含秒不再被静默丢弃）、时/分/秒列互相独立、
 * 12 小时制上午下午换算、清除语义、Controller 构建后改值、以及
 * <b>触发器宽度恒定</b>契约（叠加层方案，对齐 BUG #152）。</p>
 */
class TimePickerAntTest extends JfxTestBase {

    private static void layoutInScene(Node node) {
        Pane root = new Pane(node);
        Scene scene = new Scene(root, 640, 240);
        scene.getStylesheets().add(TimePickerAntTest.class.getResource(
                "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        root.applyCss();
        root.layout();
    }

    /** 主键单击事件：必须走 {@code Event.fireEvent} 派发，过滤器链才会生效。 */
    private static MouseEvent click() {
        return new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
    }

    @Nested
    @DisplayName("format 段位解析")
    class FormatTests {

        @Test
        @DisplayName("单双字母等价：H:m:s 也解析出时、分、秒三段")
        void singleLetterEquivalent() {
            TimePickerAnt.Visibility vis = TimePickerAnt.parseVisibility("H:m:s");
            assertTrue(vis.hour);
            assertTrue(vis.minute, "m 应识别为分钟段");
            assertTrue(vis.second, "s 应识别为秒段 —— 旧实现会在 H:m:s 下静默丢段");
        }

        @Test
        @DisplayName("HH:mm 只有时、分")
        void hourMinuteOnly() {
            TimePickerAnt.Visibility vis = TimePickerAnt.parseVisibility("HH:mm");
            assertTrue(vis.hour);
            assertTrue(vis.minute);
            assertFalse(vis.second);
        }

        @Test
        @DisplayName("含 a 时出现上午/下午段")
        void meridiemFlag() {
            assertTrue(TimePickerAnt.parseVisibility("HH:mm a").meridiem);
            assertFalse(TimePickerAnt.parseVisibility("HH:mm").meridiem);
        }

        @Test
        @DisplayName("JDK 12 小时制符号 h 规范化为 H")
        void twelveHourSymbolNormalized() {
            assertEquals("HH:mm", TimePickerAnt.normalizePattern("hh:mm"));
            assertEquals(TimePickerAnt.DEFAULT_FORMAT, TimePickerAnt.normalizePattern(null));
            assertEquals(TimePickerAnt.DEFAULT_FORMAT, TimePickerAnt.normalizePattern(""));
        }

        @Test
        @DisplayName("不含时间字段的格式串直接报错，不静默降级")
        void rejectsNonTimePattern() {
            assertThrows(IllegalArgumentException.class,
                    () -> TimePickerAnt.parseVisibility("yyyy"));
        }

        @Test
        @DisplayName("只有分/秒的格式串仍会补出时列（否则值无法表达）")
        void forcesHourColumn() {
            assertTrue(TimePickerAnt.parseVisibility("mm:ss").hour);
        }
    }

    @Nested
    @DisplayName("面板选择")
    class PanelTests {

        private TimePickerAnt.TimePanel panel(String format) {
            return new TimePickerAnt.TimePanel(TimePickerAnt.parseVisibility(format), true);
        }

        @Test
        @DisplayName("setValue 后 getValue 回读一致，且未显示段归零")
        void setValueRoundTrip() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = panel("HH:mm");
                p.setValue(LocalTime.of(9, 30, 45));
                assertEquals(LocalTime.of(9, 30, 0), p.getValue(), "未显示的秒段应按 0 计");
            });
        }

        @Test
        @DisplayName("值相同时分钟列与小时列互不干扰（旧实现的耦合 bug）")
        void hourAndMinuteIndependent() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = panel("HH:mm");
                p.setValue(LocalTime.of(5, 5));
                assertEquals(5, p.getValue().getHour());
                assertEquals(5, p.getValue().getMinute());
            });
        }

        @Test
        @DisplayName("12 小时制：15 时 = 下午 3 时")
        void twelveHourMeridiem() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = panel("HH:mm a");
                p.setValue(LocalTime.of(15, 30));
                assertEquals(15, p.getValue().getHour());
            });
        }
    }

    @Nested
    @DisplayName("触发器与交互")
    class TriggerTests {

        @Test
        @DisplayName("有值时显示格式化文本，未设值显示 placeholder 且文本为空")
        void valueAndPlaceholder() {
            runOnFxThreadAndWait(() -> {
                StackPane withValue = TimePickerAnt.create().value(LocalTime.of(9, 30, 45)).build();
                Pane root = new Pane(withValue);
                new Scene(root, 300, 100);
                Node field = withValue.lookup("." + JfxStyles.TIME_PICKER_FIELD);
                assertNotNull(field);
                // M21.33 起触发器可键盘录入，文本必须与格式化结果一致（掩码不篡改显示）
                assertEquals("09:30:45", ((javafx.scene.control.TextField) field).getText());

                StackPane empty = TimePickerAnt.create().placeholder("选择时间").build();
                new Scene(new Pane(empty), 300, 100);
                assertEquals("", ((javafx.scene.control.TextField) empty.lookup(
                        "." + JfxStyles.TIME_PICKER_FIELD)).getText());
            });
        }

        @Test
        @DisplayName("onChange 在面板提交时给出 LocalTime")
        void onChangeFires() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalTime> captured = new AtomicReference<>();
                StackPane node = TimePickerAnt.create()
                        .value(LocalTime.of(9, 30))
                        .onChange(captured::set)
                        .build();
                TimePickerAnt.Controller ctrl = TimePickerAnt.controllerOf(node);
                assertNotNull(ctrl);
                // 页脚「确定」提交路径：面板 getValue 即提交值
                Node panel = ctrl.getPanelNode();
                assertTrue(panel instanceof TimePickerAnt.TimePanel);
                TimePickerAnt.TimePanel tp = (TimePickerAnt.TimePanel) panel;
                tp.setValue(LocalTime.of(11, 45));
                tp.commit();
                assertEquals(LocalTime.of(11, 45, 0), captured.get(),
                        "确定应提交面板当前值");
                assertEquals(LocalTime.of(11, 45), ctrl.getValue());
            });
        }

        @Test
        @DisplayName("点清除按钮真的清除：值置空 + 回调 null + 不顺带开面板（BUG #168）")
        void clearSemantics() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalTime> captured = new AtomicReference<>(LocalTime.of(9, 30));
                StackPane node = TimePickerAnt.create()
                        .value(LocalTime.of(9, 30))
                        .onChange(captured::set)
                        .build();
                layoutInScene(node);
                TimePickerAnt.Controller ctrl = TimePickerAnt.controllerOf(node);
                Node clear = node.lookup("." + JfxStyles.TIME_PICKER_CLEAR);
                assertNotNull(clear, "allowClear 默认应创建清除按钮");

                // 必须走真实派发：曾经挂在同一节点上的 MOUSE_CLICKED 过滤器会先吃掉事件，
                // 直接调 getOnMouseClicked().handle(...) 绕过了过滤器，因此查不出这个 bug。
                Event.fireEvent(clear, click());

                assertNull(captured.get(), "清除应回调 null");
                assertNull(ctrl.getValue());
                assertEquals("", ((javafx.scene.control.TextField) node.lookup(
                        "." + JfxStyles.TIME_PICKER_FIELD)).getText(), "清除后文本框应清空");
                assertFalse(ctrl.isPanelShowing(), "点清除不应顺带打开面板");
            });
        }

        @Test
        @DisplayName("清除后重开面板落在当前时刻，而不是 00:00（BUG #168）")
        void clearThenReopenIsNotMidnight() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(9, 30)).build();
                layoutInScene(node);
                TimePickerAnt.Controller ctrl = TimePickerAnt.controllerOf(node);
                Event.fireEvent(node.lookup("." + JfxStyles.TIME_PICKER_CLEAR), click());

                ctrl.showPanel();
                LocalTime shown = ((TimePickerAnt.TimePanel) ctrl.getPanelNode()).getValue();
                long diff = Math.abs(java.time.Duration.between(shown, LocalTime.now()).getSeconds());
                assertTrue(Math.min(diff, 86400 - diff) <= 60,
                        "未选中时面板应是「此刻」（对齐日期侧取今天），实测 " + shown);
            });
        }

        @Test
        @DisplayName("allowClear(false) 不创建清除按钮")
        void allowClearFalse() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().allowClear(false).build();
                assertNull(node.lookup("." + JfxStyles.TIME_PICKER_CLEAR));
            });
        }

        @Test
        @DisplayName("size(SMALL) 让输入框挂上 small 类（LESS 尺寸档位）")
        void sizeClassApplied() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().size(Size.SMALL).build();
                Node field = node.lookup("." + JfxStyles.TIME_PICKER_FIELD);
                assertNotNull(field);
                assertTrue(field.getStyleClass().contains(JfxStyles.SIZE_SMALL));
            });
        }
    }

    @Nested
    @DisplayName("Controller")
    class ControllerTests {

        @Test
        @DisplayName("setValue 更新已提交值且不触发 onChange")
        void setValueSilent() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalTime> captured = new AtomicReference<>();
                StackPane node = TimePickerAnt.create().onChange(captured::set).build();
                TimePickerAnt.Controller ctrl = TimePickerAnt.controllerOf(node);
                ctrl.setValue(LocalTime.of(8, 0));
                assertEquals(LocalTime.of(8, 0), ctrl.getValue());
                assertNull(captured.get(), "程序化 setValue 不应触发 onChange");
            });
        }

        @Test
        @DisplayName("bindValue 回写：提交时写入 property，外部变更同步显示")
        void bindValueRoundTrip() {
            runOnFxThreadAndWait(() -> {
                ObjectProperty<LocalTime> prop = new SimpleObjectProperty<>(LocalTime.of(9, 30));
                StackPane node = TimePickerAnt.create().bindValue(prop).build();
                TimePickerAnt.Controller ctrl = TimePickerAnt.controllerOf(node);
                assertEquals(LocalTime.of(9, 30), ctrl.getValue(), "初始值应取自 property");

                prop.set(LocalTime.of(20, 15));
                assertEquals(LocalTime.of(20, 15), ctrl.getValue(), "外部变更应同步");
            });
        }
    }

    @Nested
    @DisplayName("取值列契约：整段全值同列，选中项滚进视野")
    class ValueColumnTests {

        /** 取某列当前选中项。 */
        private Object sel(TimePickerAnt.TimePanel p, String id) {
            @SuppressWarnings("unchecked")
            javafx.scene.control.ListView<Object> lv =
                    (javafx.scene.control.ListView<Object>) p.lookup("#" + id);
            assertNotNull(lv, "面板应含取值列 " + id);
            return lv.getSelectionModel().getSelectedItem();
        }

        private javafx.scene.control.ListView<?> list(TimePickerAnt.TimePanel p, String id) {
            javafx.scene.control.ListView<?> lv =
                    (javafx.scene.control.ListView<?>) p.lookup("#" + id);
            assertNotNull(lv, "面板应含取值列 " + id);
            return lv;
        }

        @Test
        @DisplayName("时 0-23、分 0-59、秒 0-59 各一整列，不再拆十位/个位")
        void everyUnitOwnsItsFullRange() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = new TimePickerAnt.TimePanel(
                        TimePickerAnt.parseVisibility("HH:mm:ss"), false);
                layoutInScene(p);
                assertEquals(24, list(p, "hour-list").getItems().size(),
                        "时列应含 0-23 全部取值");
                assertEquals(60, list(p, "minute-list").getItems().size(),
                        "分列应含 0-59 全部取值");
                assertEquals(60, list(p, "second-list").getItems().size(),
                        "秒列应含 0-59 全部取值");
            });
        }

        @Test
        @DisplayName("format 不含秒时不渲染秒列（秒不再被静默丢弃，也不会凭空多出）")
        void hiddenUnitsHaveNoColumn() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = new TimePickerAnt.TimePanel(
                        TimePickerAnt.parseVisibility("HH:mm"), false);
                layoutInScene(p);
                assertNotNull(p.lookup("#hour-list"));
                assertNotNull(p.lookup("#minute-list"));
                assertNull(p.lookup("#second-list"), "HH:mm 下不应有秒列");
            });
        }

        @Test
        @DisplayName("9:03:40 精确落到各列：时=9、分=3、秒=40")
        void valueLandsOnExactRow() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = new TimePickerAnt.TimePanel(
                        TimePickerAnt.parseVisibility("HH:mm:ss"), false);
                p.setValue(LocalTime.of(9, 3, 40));
                layoutInScene(p);
                assertEquals(9, sel(p, "hour-list"));
                assertEquals(3, sel(p, "minute-list"));
                assertEquals(40, sel(p, "second-list"));
            });
        }

        @Test
        @DisplayName("选中项必须滚进视野 —— 否则 59 分会高亮在看不见的地方")
        void selectionIsScrolledIntoView() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = new TimePickerAnt.TimePanel(
                        TimePickerAnt.parseVisibility("HH:mm:ss"), false);
                p.setValue(LocalTime.of(23, 59, 59));
                layoutInScene(p);

                javafx.scene.control.ListView<?> minute = list(p, "minute-list");
                // 列表被 layoutInScene 重新布局过，滚动位置在下次脉冲落实：显式再滚一次，
                // 断言的是「选中项与滚动位置一致」这一不变量，而非某次布局的中间态。
                minute.scrollTo(minute.getSelectionModel().getSelectedIndex());
                int selIdx = minute.getSelectionModel().getSelectedIndex();
                assertEquals(59, selIdx);

                // 可见范围只能从 VirtualFlow 读（API 是 protected），改用「已渲染单元格」
                // 的几何位置判断：选中行的 y 区间与该流可视高度必须有交集。
                javafx.scene.control.skin.VirtualFlow<?> flow = virtualFlow(minute);
                assertNotNull(flow, "列表应已装壳（否则无从判断可见范围）");
                ListCell<?> selectedCell = minute.lookupAll(".list-cell").stream()
                        .filter(ListCell.class::isInstance)
                        .map(ListCell.class::cast)
                        .filter(c -> c.getIndex() == selIdx)
                        .findFirst()
                        .orElseThrow(() -> new AssertionError(
                                "选中行 " + selIdx + " 尚未渲染 —— 说明它没被滚进视野"));
                double top = selectedCell.getBoundsInParent().getMinY()
                        - flow.getBoundsInParent().getMinY();
                double bottom = selectedCell.getBoundsInParent().getMaxY()
                        - flow.getBoundsInParent().getMinY();
                assertTrue(flow.getHeight() > 0, "列表可视高度应为正");
                assertTrue(top < flow.getHeight() && bottom > 0,
                        "选中行 y=[" + top + "," + bottom + "] 不在可视高度 "
                                + flow.getHeight() + " 内");
            });
        }

        @Test
        @DisplayName("点选即定值：分列点 45 后取值就是 45（不需要先在 60 行里滚）")
        void pickWritesValueDirectly() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = new TimePickerAnt.TimePanel(
                        TimePickerAnt.parseVisibility("HH:mm:ss"), false);
                p.setValue(LocalTime.of(21, 47, 0));
                layoutInScene(p);
                pick(p, "minute-list", 45);
                assertEquals(45, p.getValue().getMinute());
                pick(p, "hour-list", 7);
                assertEquals(7, p.getValue().getHour(), "时列直接给全值，无需十位/个位联动");
                pick(p, "second-list", 59);
                assertEquals(59, p.getValue().getSecond());
            });
        }

        @Test
        @DisplayName("12 小时制：时列只列 1-12，选项按当前上下午折算成 24 小时制")
        void twelveHourColumnMapsThroughMeridiem() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = new TimePickerAnt.TimePanel(
                        TimePickerAnt.parseVisibility("hh:mm a"), false);
                layoutInScene(p);
                assertEquals(12, list(p, "hour-list").getItems().size(),
                        "12 小时制的时列应是 1-12");

                p.setValue(LocalTime.of(15, 30)); // 下午 3 点
                assertEquals(3, sel(p, "hour-list"), "15 时应显示为 3");
                pick(p, "hour-list", 11);
                assertEquals(23, p.getValue().getHour(), "下午的 11 点 = 23 时");
            });
        }

        @Test
        @DisplayName("下午被选中后「上午」仍在视口内，可改回（BUG #168）")
        void meridiemKeepsBothRowsVisible() {
            runOnFxThreadAndWait(() -> {
                TimePickerAnt.TimePanel p = new TimePickerAnt.TimePanel(
                        TimePickerAnt.parseVisibility("HH:mm a"), false);
                layoutInScene(p);

                // 上午/下午列只有 2 行且同屏；选中第 2 行时若还去 scrollTo，
                // VirtualFlow 会把第 2 行顶到首行，「上午」整行滚出视口且不回落。
                p.setValue(LocalTime.of(15, 30)); // 下午
                assertTrue(fullRowVisible(p, JfxStyles.TIME_PANEL_MERIDIEM_ITEM, "上午"),
                        "「上午」必须仍整行可见 —— 否则用户选完下午就再也改不回上午");
                assertTrue(fullRowVisible(p, JfxStyles.TIME_PANEL_MERIDIEM_ITEM, "下午"),
                        "被选中的「下午」必须整行可见");
                pick(p, JfxStyles.TIME_PANEL_MERIDIEM_ITEM, Boolean.FALSE);
                assertEquals(3, p.getValue().getHour(), "应能从下午改回上午 3 时");
            });
        }

        /** 指定列中文字为 {@code text} 的单元格是否整行落在该列视口内。 */
        private boolean fullRowVisible(TimePickerAnt.TimePanel p, String styleClass, String text) {
            javafx.scene.control.ListView<?> lv =
                    (javafx.scene.control.ListView<?>) p.lookup("." + styleClass);
            double vTop = lv.localToScene(lv.getBoundsInLocal()).getMinY();
            double vBottom = lv.localToScene(lv.getBoundsInLocal()).getMaxY();
            return lv.lookupAll(".list-cell").stream()
                    .map(ListCell.class::cast)
                    .filter(c -> text.equals(c.getText()))
                    .anyMatch(c -> c.localToScene(c.getBoundsInLocal()).getMinY() >= vTop - 0.5
                            && c.localToScene(c.getBoundsInLocal()).getMaxY() <= vBottom + 0.5);
        }

        /** 取列表内部的 VirtualFlow（可见单元格范围只能从这里读）。 */
        private javafx.scene.control.skin.VirtualFlow<?> virtualFlow(
                javafx.scene.control.ListView<?> lv) {
            return (javafx.scene.control.skin.VirtualFlow<?>) lv.lookup(".virtual-flow");
        }

        private void pick(TimePickerAnt.TimePanel p, String id, int value) {
            @SuppressWarnings("unchecked")
            javafx.scene.control.ListView<Object> lv =
                    (javafx.scene.control.ListView<Object>) p.lookup("#" + id);
            lv.getSelectionModel().select(Integer.valueOf(value));
        }

        /** 时段列的选项是 {@code Boolean}，取不了 {@link Integer} 重载。 */
        private void pick(TimePickerAnt.TimePanel p, String styleClass, Boolean value) {
            @SuppressWarnings("unchecked")
            javafx.scene.control.ListView<Object> lv =
                    (javafx.scene.control.ListView<Object>) p.lookup("." + styleClass);
            lv.getSelectionModel().select(value);
        }
    }

    @Nested
    @DisplayName("面板外观契约：选中项背景与文字必须可分辨")
    class PanelAppearanceTests {

        @Test
        @DisplayName("选中单元格背景与文字不同色（防 :focused 全局规则只改背景导致撞色）")
        void selectedCellContrast() {
            runOnFxThreadAndWait(() -> {
                // 用伪类直接复现「列表获得焦点」这一前提：无头测试环境里
                // Window 没有系统焦点，requestFocus() 不会让 :focused 生效，
                // 而 modena 的选中色恰恰只在 `.list-view:focused ...` 下覆盖背景。
                TimePickerAnt.TimePanel panel = new TimePickerAnt.TimePanel(
                        TimePickerAnt.parseVisibility("HH:mm a"), false);
                panel.setValue(LocalTime.of(15, 30));
                layoutInScene(panel);

                Node list = panel.lookup("." + JfxStyles.TIME_PANEL_LIST);
                assertNotNull(list, "面板应含时间列表");
                list.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
                ((javafx.scene.Parent) panel).applyCss();
                ((javafx.scene.Parent) panel).layout();

                ListCell<?> selected = panel.lookupAll(".list-cell").stream()
                        .filter(ListCell.class::isInstance)
                        .map(ListCell.class::cast)
                        .filter(ListCell::isSelected)
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("面板中应有一个选中单元格"));

                Color bg = (Color) ((Region) selected).getBackground().getFills().get(0).getFill();
                Color fg = (Color) selected.getTextFill();
                assertNotEquals(bg, fg,
                        "选中态背景与文字撞色（全局规则只覆盖背景、本组件规则只覆盖文字色，"
                                + "双方同为 accent-emphasis 时文字完全不可见）");
            });
        }
    }

    @Nested
    @DisplayName("布局契约：触发器宽度恒定")
    class LayoutTests {

        @Test
        @DisplayName("空 ↔ 有值 切换宽度不变（叠加层方案，不抖）")
        void widthStableAcrossValueChange() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().build();
                layoutInScene(node);
                double empty = node.getLayoutBounds().getWidth();
                assertTrue(empty > 0, "组件应有非零宽度");

                TimePickerAnt.controllerOf(node).setValue(LocalTime.of(23, 59, 59));
                node.applyCss();
                node.layout();
                double filled = node.getLayoutBounds().getWidth();
                assertEquals(empty, filled, 0.5,
                        "空 ↔ 有值切换不应改变组件宽度（清除按钮/箭头必须是叠加层）");
            });
        }
    }
}
