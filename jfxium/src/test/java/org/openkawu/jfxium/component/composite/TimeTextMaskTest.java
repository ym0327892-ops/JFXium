package org.openkawu.jfxium.component.composite;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 时间键盘录入掩码（M21.33）行为测试。
 *
 * <p>这里断言的是<b>用户可见的输入结果</b>（文本渲染 + 提交值），而不是内部字段：
 * 掩码的价值全在「敲几个键之后框里是什么、提交出来是什么」，只有真实走
 * {@link KeyEvent} 派发才能覆盖（直接调私有方法会把事件过滤器、KEY_TYPED 拦截、
 * 光标同步这几条最容易出错的链路全绕过去）。</p>
 */
class TimeTextMaskTest extends JfxTestBase {

    /** 把控件放进真实场景：光标/焦点语义需要 Scene 才成立。 */
    private static TextField fieldOf(StackPane node) {
        Pane root = new Pane(node);
        Scene scene = new Scene(root, 480, 160);
        scene.getStylesheets().add(TimePickerAntTest.class.getResource(
                "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
        node.applyCss();
        Node field = node.lookup("." + JfxStyles.TIME_PICKER_FIELD);
        assertNotNull(field, "触发器输入框应存在");
        return (TextField) field;
    }

    private static void press(TextField field, KeyCode code) {
        Event.fireEvent(field, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code,
                false, false, false, false));
    }

    private static void pressShift(TextField field, KeyCode code) {
        Event.fireEvent(field, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code,
                true, false, false, false));
    }

    /** 敲一位数字（KEY_PRESSED 的 text 即为该数字，与真实键盘一致）。 */
    private static void type(TextField field, String digit) {
        Event.fireEvent(field, new KeyEvent(KeyEvent.KEY_PRESSED, digit, digit,
                KeyCode.valueOf("DIGIT" + digit), false, false, false, false));
    }

    /**
     * 把光标点到渲染文本的第 {@code displayIndex} 个字符处，模拟「用户点了某一段」。
     *
     * <p>真实点击里把 caret 摆到按下位置是 JavaFX 皮肤自己的活（掩码只读结果），
     * 合成 MouseEvent 不会触发那段皮肤逻辑，因此这里直接产出同样的结果：
     * 派发按下事件（掩码据此认定「接下来的 caret 是用户意图」）→ 移动 caret。
     * 掩码只认「用户亲手放过光标」后的 caret 变化，这条路径与真实点击一致。</p>
     */
    private static void clickAt(TextField field, int displayIndex) {
        pumpFxEvents();
        field.requestFocus();
        pumpFxEvents();
        // 鼠标按下：掩码据此判定「接下来这个 caret 是用户意图」
        Event.fireEvent(field, new MouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0,
                MouseButton.PRIMARY, 1, false, false, false, false,
                true, false, false, true, false, false, null));
        // 平台负责把 caret 摆到按下位置（合成事件不走皮肤那一步，这里补上）。
        // 先摆到末尾再摆到目标位：位置没变时不会触发 caret 监听器，
        // 那样就测不到「把光标放到某一段 → 键盘归属该段」这条链路
        field.positionCaret(field.getText().length());
        field.positionCaret(displayIndex);
        pumpFxEvents();
    }

    private static void typeAll(TextField field, String digits) {
        for (char c : digits.toCharArray()) {
            type(field, String.valueOf(c));
        }
    }

    @Nested
    @DisplayName("Esc 取消录入")
    class EscapeCancels {

        @Test
        @DisplayName("Esc 收起面板并丢弃未提交数字：文本还原，值不变")
        void escapeRevertsAndCloses() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(9, 30)).build();
                Pane root = new Pane(node);
                Scene scene = new Scene(root, 480, 160);
                scene.getStylesheets().add(TimePickerAntTest.class.getResource(
                        "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
                root.applyCss();
                root.layout();

                TextField field = (TextField) node.lookup("." + JfxStyles.TIME_PICKER_FIELD);
                TimePickerAnt.Controller controller = TimePickerAnt.controllerOf(node);
                controller.showPanel();
                field.requestFocus();

                typeAll(field, "23");
                assertEquals("23:30:00", field.getText(), "录入后文本应反映未提交的输入");

                press(field, KeyCode.ESCAPE);
                assertEquals(LocalTime.of(9, 30), controller.getValue(), "Esc 不得提交未录入完成的值");
                assertEquals("09:30:00", field.getText(), "Esc 应把文本还原为已提交值");
                assertFalse(controller.isPanelShowing(), "Esc 应顺带收起面板");

                // Esc 之后失焦不得静默提交那串被丢弃的数字
                field.getParent().requestFocus();
                assertEquals(LocalTime.of(9, 30), controller.getValue(),
                        "Esc 之后失焦仍应是已提交值（修复前会把 23:30 静默提交）");
                stage.close();
            });
        }

        @Test
        @DisplayName("DateTimePicker：Esc 只丢弃时间后缀，日期前缀与值都不受影响")
        void escapeOnDateTimePicker() {
            runOnFxThreadAndWait(() -> {
                StackPane node = DateTimePickerAnt.create()
                        .value(LocalDateTime.of(2026, 10, 11, 9, 30)).build();
                Pane root = new Pane(node);
                Scene scene = new Scene(root, 520, 160);
                scene.getStylesheets().add(TimePickerAntTest.class.getResource(
                        "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
                root.applyCss();
                root.layout();

                TextField field = (TextField) node.lookup("." + JfxStyles.TIME_PICKER_FIELD);
                DateTimePickerAnt.Controller controller = DateTimePickerAnt.controllerOf(node);
                controller.showPanel();
                field.requestFocus();

                typeAll(field, "23");
                assertTrue(field.getText().contains("23:30"), "录入应改时间部分");
                press(field, KeyCode.ESCAPE);

                assertEquals(LocalDateTime.of(2026, 10, 11, 9, 30), controller.getValue(),
                        "Esc 不得提交");
                assertEquals("2026-10-11 09:30:00", field.getText(),
                        "Esc 后文本还原，日期前缀保持");
                assertFalse(controller.isPanelShowing(), "Esc 应收起面板");
                stage.close();
            });
        }
    }

    @Nested
    @DisplayName("旁路写入：有数字就采纳，没数字就丢弃")
    class BypassWrites {

        @Test
        @DisplayName("程序化 setText 的整串时间会被采纳（与粘贴同一条路）")
        void externalSetTextIsAdopted() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(7, 15)).build();
                TextField field = fieldOf(node);
                field.setText("23:45:00");
                assertEquals("23:45:00", field.getText(), "带数字的外部写入按数字折入模型");
                press(field, KeyCode.ENTER);
                assertEquals(LocalTime.of(23, 45), TimePickerAnt.controllerOf(node).getValue());
            });
        }

        @Test
        @DisplayName("没有数字的外部写入被丢弃：文本不会与值各说各话")
        void digitlessWriteIsDiscarded() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(7, 15)).build();
                TextField field = fieldOf(node);
                field.setText("下午三点");
                assertEquals("07:15:00", field.getText(), "纯文本写入应被按模型重绘掉");
                assertEquals(LocalTime.of(7, 15), TimePickerAnt.controllerOf(node).getValue());
            });
        }
    }

    @Nested
    @DisplayName("显示与值一致（多段时分隔符计数）")
    class DisplayConsistency {

        @Test
        @DisplayName("提交后显示由值重绘：12 小时制敲 12（上午）显示 12 时而非 00 时")
        void commitRendersFromValue() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().format("hh:mm a").build();
                TextField field = fieldOf(node);
                typeAll(field, "12");
                press(field, KeyCode.ENTER);
                assertEquals(LocalTime.of(0, 0), TimePickerAnt.controllerOf(node).getValue(),
                        "12 时（上午）就是 0 点");
                assertEquals("12:00 上午", field.getText(),
                        "显示必须由提交后的值重绘：12 时→12，而不是 00");
            });
        }

        @Test
        @DisplayName("DateTimePicker：录入时间后日期前缀保持模式长度，不被挤掉")
        void datePrefixSurvivesTyping() {
            runOnFxThreadAndWait(() -> {
                StackPane node = DateTimePickerAnt.create()
                        .value(LocalDateTime.of(2026, 10, 11, 9, 30)).build();
                TextField field = fieldOf(node);
                assertEquals(19, field.getText().length(), "yyyy-MM-dd HH:mm:ss 渲染 19 字符");
                typeAll(field, "2345");
                press(field, KeyCode.ENTER);
                assertEquals(19, field.getText().length(),
                        "录入后仍应占满模式长度（修复前前缀少补一个分隔符 → 18）");
                assertTrue(field.getText().startsWith("2026-10-11"), "日期前缀必须保留");
                assertTrue(field.getText().contains("23:45"), "时间部分应为 23:45");
            });
        }
    }

    @Nested
    @DisplayName("TimePickerAnt：键盘直录")
    class SegmentedEntry {

        @Test
        @DisplayName("连续敲 4 位：2 → 23 → 2345，全程无需移动光标")
        void sequentialEntry() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(7, 15)).build();
                TextField field = fieldOf(node);

                assertEquals("07:15:00", field.getText(), "初始显示已提交值");
                assertEquals(0, field.getCaretPosition(), "光标落在第 1 段第 1 位");
                assertEquals(0, field.getSelection().getLength(),
                        "聚焦后不留整串选中 —— 全选会让人以为「敲下去整串替换」");

                type(field, "2");
                assertEquals("2:15:00", field.getText(), "第 1 位：整段替换为 2 时");
                type(field, "3");
                assertEquals("23:15:00", field.getText(), "第 2 位：补成 23 时");
                type(field, "4");
                type(field, "5");
                assertEquals("23:45:00", field.getText());

                press(field, KeyCode.ENTER);
                assertEquals(LocalTime.of(23, 45), TimePickerAnt.controllerOf(node).getValue());
                assertEquals(6, field.getCaretPosition(),
                        "回车只是确认，光标停在原处（秒段第一个数字之前）");
            });
        }

        @Test
        @DisplayName("只敲 3 位即可提交：930 = 09:30（未敲的段不占位）")
        void threeDigitsIsEnough() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().build();
                TextField field = fieldOf(node);
                typeAll(field, "930");
                assertEquals("09:30", field.getText(), "未敲的段既不占位也不画悬空冒号");
                press(field, KeyCode.ENTER);
                assertEquals(LocalTime.of(9, 30), TimePickerAnt.controllerOf(node).getValue());
                assertEquals("09:30:00", field.getText(), "提交后补齐各段");
            });
        }

        @Test
        @DisplayName("段内首位即定值：敲 9 得 9 时（而不是 90 时）")
        void firstDigitIsValueNotTens() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().build();
                TextField field = fieldOf(node);
                type(field, "9");
                assertEquals("09", field.getText(), "9 立即定型为 9 时");
                press(field, KeyCode.ENTER);
                assertEquals(LocalTime.of(9, 0), TimePickerAnt.controllerOf(node).getValue());
            });
        }

        @Test
        @DisplayName("段满后再敲：溢出到下一段（07:15 + 3 → 07:15:03）")
        void overflowFallsThroughToNextSegment() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(7, 15)).build();
                TextField field = fieldOf(node);
                // 光标停在分钟段里、且分钟段已满：下一位数字属于秒段
                for (int i = 0; i < 4; i++) {
                    press(field, KeyCode.RIGHT);
                }
                assertEquals(6, field.getCaretPosition(), "插入符落在秒段第一个数字之前");
                type(field, "3");
                assertEquals("07:15:3", field.getText(),
                        "段末边界属于下一段：数字进秒段而不是把分钟挤掉一位");
                press(field, KeyCode.ENTER);
                assertEquals(LocalTime.of(7, 15, 3), TimePickerAnt.controllerOf(node).getValue());

                // 段还有空位时不会溢出
                StackPane node2 = TimePickerAnt.create().value(LocalTime.of(7, 15)).build();
                TextField field2 = fieldOf(node2);
                press(field2, KeyCode.RIGHT);
                press(field2, KeyCode.RIGHT);
                press(field2, KeyCode.RIGHT);
                assertEquals(4, field2.getCaretPosition(), "分钟段内（第二个数字之前）");
                type(field2, "3");
                assertEquals("07:13:00", field2.getText(),
                        "插入点右侧被替换：1 + 3 得 13");
                press(field2, KeyCode.ENTER);
                assertEquals(LocalTime.of(7, 13), TimePickerAnt.controllerOf(node2).getValue());
            });
        }

        @Test
        @DisplayName("退格逐位回退：2345 → 234 → 23 → 2 → 空")
        void backspaceStepsBack() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().build();
                TextField field = fieldOf(node);
                typeAll(field, "2345");
                assertEquals("23:45", field.getText());

                press(field, KeyCode.BACK_SPACE);
                assertEquals("23:4", field.getText(), "删掉最后一位");
                press(field, KeyCode.BACK_SPACE);
                assertEquals("23", field.getText(), "分钟段空了不留下悬空冒号");
                press(field, KeyCode.BACK_SPACE);
                assertEquals("2", field.getText(), "继续退到小时段的一位");
                press(field, KeyCode.BACK_SPACE);
                assertEquals("", field.getText(), "退空");
                press(field, KeyCode.BACK_SPACE);
                assertEquals("", field.getText(), "退空后不再有副作用");
            });
        }

        @Test
        @DisplayName("字母键不污染文本（掩码只吃数字）")
        void lettersAreIgnored() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(7, 15)).build();
                TextField field = fieldOf(node);
                String before = field.getText();
                Event.fireEvent(field, new KeyEvent(KeyEvent.KEY_PRESSED, "a", "a",
                        KeyCode.A, false, false, false, false));
                assertEquals(before, field.getText(), "字母不应进入时间文本");
                assertEquals(0, field.getCaretPosition(), "字母也不该推动光标");
            });
        }

        @Test
        @DisplayName("粘贴整串时间按数字折入模型（而不是被丢弃）")
        void pasteFillsField() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(7, 15)).build();
                TextField field = fieldOf(node);
                ClipboardContent content = new ClipboardContent();
                content.putString("23:45:00");
                Clipboard.getSystemClipboard().setContent(content);

                field.paste();
                assertEquals("23:45:00", field.getText(), "复制来的时间应当能贴进去");
                press(field, KeyCode.ENTER);
                assertEquals(LocalTime.of(23, 45), TimePickerAnt.controllerOf(node).getValue());
            });
        }

        @Test
        @DisplayName("粘贴不带分隔符的 2345 同样有效；没有数字的粘贴不改动内容")
        void pasteStripsSeparators() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(7, 15)).build();
                TextField field = fieldOf(node);
                ClipboardContent digits = new ClipboardContent();
                digits.putString("2345");
                Clipboard.getSystemClipboard().setContent(digits);
                field.paste();
                assertEquals("23:45:00", field.getText());

                ClipboardContent words = new ClipboardContent();
                words.putString("明天下午三点");
                Clipboard.getSystemClipboard().setContent(words);
                field.paste();
                assertEquals("23:45:00", field.getText(), "没有数字的粘贴不应改动内容");
            });
        }

        @Test
        @DisplayName("← → 每次动一格；段内第二位敲 9 → 29 越界则单独成立")
        void arrowMovesByDigit() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().build();
                TextField field = fieldOf(node);
                typeAll(field, "2345");          // "23:45"
                assertEquals(5, field.getCaretPosition(), "4 位敲完，插入符在文本末尾");
                press(field, KeyCode.LEFT);
                assertEquals(4, field.getCaretPosition(), "← 每按一次动一格");
                press(field, KeyCode.LEFT);
                assertEquals(3, field.getCaretPosition(), "再 ← 跨过分隔符");
                press(field, KeyCode.LEFT);
                assertEquals(1, field.getCaretPosition(), "再 ← 落在小时段第二位之前");
                press(field, KeyCode.LEFT);
                assertEquals(0, field.getCaretPosition(), "← 到底停在小时段第一位");
                press(field, KeyCode.RIGHT);
                assertEquals(1, field.getCaretPosition(), "→ 回到小时段第二位");

                type(field, "9");
                assertEquals("09:45", field.getText(),
                        "2、9 组成 29 时越界 → 该位单独成立（整段替换为 9 时，而不是钳成 23 时）");
                press(field, KeyCode.ENTER);
                assertEquals(LocalTime.of(9, 45), TimePickerAnt.controllerOf(node).getValue());
            });
        }

        @Test
        @DisplayName("↑↓ 步进当前段，Shift 为 ±10，不闪出多余数字")
        void arrowsStepSegment() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().value(LocalTime.of(9, 30)).build();
                TextField field = fieldOf(node);
                press(field, KeyCode.UP);
                assertEquals("10:30:00", field.getText(), "↑ = 小时 +1");
                press(field, KeyCode.DOWN);
                assertEquals("09:30:00", field.getText(),
                        "↓ 仍作用在同一段（不因光标贴边界而跳到分钟段）");

                press(field, KeyCode.RIGHT);
                press(field, KeyCode.RIGHT);
                pressShift(field, KeyCode.UP);
                assertEquals("09:40:00", field.getText(), "→ 到分钟段后 Shift+↑ = +10");

                press(field, KeyCode.HOME);
                pressShift(field, KeyCode.DOWN);
                assertEquals("23:40:00", field.getText(), "Shift+↓ = 小时 -10 并回绕（9 → 23）");
            });
        }

        @Test
        @DisplayName("12 小时制：钟点段按 1-12 录入，12 上午 = 0 点")
        void twelveHourEntry() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().format("hh:mm a").build();
                TextField field = fieldOf(node);
                typeAll(field, "1230");
                assertEquals("12:30 上午", field.getText(), "钟点段宽 2，后缀由格式化器给出");
                press(field, KeyCode.ENTER);
                assertEquals(LocalTime.of(0, 30), TimePickerAnt.controllerOf(node).getValue());
            });
        }
    }

    @Nested
    @DisplayName("TimePickerAnt：editable(false) 保持只读")
    class ReadOnlyMode {

        @Test
        @DisplayName("editable(false) 时输入框不可编辑，击键不改文本")
        void readOnlyIgnoresKeys() {
            runOnFxThreadAndWait(() -> {
                StackPane node = TimePickerAnt.create().editable(false).build();
                TextField field = fieldOf(node);
                assertFalse(field.isEditable(), "editable(false) 应把输入框置为只读");
                typeAll(field, "123456");
                assertEquals("", field.getText(), "只读模式下击键不应改变文本");
            });
        }
    }

    @Nested
    @DisplayName("DateTimePickerAnt：日期与时间整串可键盘录入")
    class DateTimeEntry {

        @Test
        @DisplayName("录入时间不改变日期部分，提交值沿用面板选中的日期")
        void timeEntryKeepsDate() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured = new AtomicReference<>();
                StackPane node = DateTimePickerAnt.create()
                        .value(LocalDateTime.of(2026, 3, 14, 7, 15))
                        .format("yyyy-MM-dd HH:mm:ss")
                        .onChange(captured::set)
                        .build();
                TextField field = fieldOf(node);
                assertEquals("2026-03-14 07:15:00", field.getText());
                typeAll(field, "2345");
                press(field, KeyCode.ENTER);
                assertEquals(LocalDateTime.of(2026, 3, 14, 23, 45, 0), captured.get(),
                        "只改时间，日期沿用已选中的 2026-03-14");
            });
        }

        @Test
        @DisplayName("时间在前、日期在后（HH:mm yyyy-MM-dd）同样可键盘录入，且只改时间")
        void timeFirstLayoutIsEditable() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured = new AtomicReference<>();
                StackPane node = DateTimePickerAnt.create()
                        .format("HH:mm yyyy-MM-dd")
                        .value(LocalDateTime.of(2026, 3, 14, 7, 15))
                        .onChange(captured::set)
                        .build();
                TextField field = fieldOf(node);
                assertTrue(field.isEditable(), "整串分段后不再要求时间必须在末尾");
                assertEquals("07:15 2026-03-14", field.getText());
                typeAll(field, "23");
                press(field, KeyCode.ENTER);
                assertEquals(LocalDateTime.of(2026, 3, 14, 23, 15), captured.get(),
                        "改的是小时，日期保持 2026-03-14");
            });
        }

        @Test
        @DisplayName("含无法分段的字段（毫秒 SSS）时退化为只读，而不是给出错位录入")
        void unsupportedFieldFallsBackToReadOnly() {
            runOnFxThreadAndWait(() -> {
                StackPane node = DateTimePickerAnt.create()
                        .format("yyyy-MM-dd HH:mm:ss.SSS")
                        .value(LocalDateTime.of(2026, 3, 14, 7, 15))
                        .build();
                TextField field = fieldOf(node);
                assertFalse(field.isEditable(),
                        "毫秒段无法用「数字段」表达（渲染出第 7 段），应保持只读而不是错位录入");
                assertEquals("2026-03-14 07:15:00.000", field.getText());
            });
        }
    }

    @Nested
    @DisplayName("点哪一段就改哪一段（键盘归属跟随点击）")
    class ClickSelectsSegment {

        /** yyyy-MM-dd HH:mm:ss 的渲染文本：0-3 年、5-6 月、8-9 日、11-12 时、14-15 分、17-18 秒。 */
        private static final int TEXT_LEN = "2026-10-11 09:30:00".length();

        private StackPane build(AtomicReference<LocalDateTime> captured) {
            StackPane node = DateTimePickerAnt.create()
                    .value(LocalDateTime.of(2026, 10, 11, 9, 30))
                    .format("yyyy-MM-dd HH:mm:ss")
                    .onChange(captured::set)
                    .build();
            return node;
        }

        @Test
        @DisplayName("点到月份上敲 11：改的是月份，年与时间都不动")
        void clickMonthEditsMonth() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured = new AtomicReference<>();
                TextField field = fieldOf(build(captured));
                clickAt(field, TEXT_LEN - "2026-10-11 09:30:00".length() + 5);
                typeAll(field, "11");
                press(field, KeyCode.ENTER);
                assertEquals(LocalDateTime.of(2026, 11, 11, 9, 30), captured.get(),
                        "点月份敲 11 → 11 月（修复前会落到小时段，把 09:30 改成 11:30）");
                assertEquals("2026-11-11 09:30:00", field.getText(), "只有月份那两位变了");
            });
        }

        @Test
        @DisplayName("点到日上敲 05：改的是日")
        void clickDayEditsDay() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured = new AtomicReference<>();
                TextField field = fieldOf(build(captured));
                clickAt(field, 8);
                typeAll(field, "05");
                press(field, KeyCode.ENTER);
                assertEquals(LocalDateTime.of(2026, 10, 5, 9, 30), captured.get(),
                        "点日敲 05 → 5 日");
            });
        }

        @Test
        @DisplayName("点到年份上敲 2030：改的是年（日期段时间段共用同一套语义）")
        void clickYearEditsYear() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured = new AtomicReference<>();
                TextField field = fieldOf(build(captured));
                clickAt(field, 0);
                typeAll(field, "2030");
                press(field, KeyCode.ENTER);
                assertEquals(LocalDateTime.of(2030, 10, 11, 9, 30), captured.get(),
                        "点年敲 2030 → 2030 年");
            });
        }

        @Test
        @DisplayName("点到小时上敲 23：改的是小时（回归：默认光标与点击位置一致）")
        void clickHourEditsHour() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured = new AtomicReference<>();
                TextField field = fieldOf(build(captured));
                clickAt(field, 11);
                typeAll(field, "23");
                press(field, KeyCode.ENTER);
                assertEquals(LocalDateTime.of(2026, 10, 11, 23, 30), captured.get(),
                        "点小时敲 23 → 23 时，分钟仍是 30");
            });
        }

        @Test
        @DisplayName("敲了数字直接点面板「确定」：采纳字段里的编辑（含日期段）")
        void confirmButtonAdoptsPendingEdit() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured = new AtomicReference<>();
                StackPane node = DateTimePickerAnt.create()
                        .value(LocalDateTime.of(2026, 10, 11, 9, 30))
                        .format("yyyy-MM-dd HH:mm:ss")
                        .onChange(captured::set)
                        .build();
                TextField field = fieldOf(node);
                DateTimePickerAnt.Controller ctrl = DateTimePickerAnt.controllerOf(node);
                ctrl.showPanel();
                field.requestFocus();
                pumpFxEvents();
                clickAt(field, 8);   // 点到「日」上
                typeAll(field, "25");
                // 点面板页脚的「确定」（用户不按 Enter 也能提交）
                // 时间面板 → body(HBox) → shell(VBox：主体 + 页脚)
                Node shell = ctrl.getTimePanelNode().getParent().getParent();
                assertTrue(shell.getStyleClass().contains(JfxStyles.DATE_TIME_PICKER_SHELL),
                        "页脚与时间面板应在同一个外壳里");
                shell.applyCss();
                Button ok = shell.lookupAll(".button").stream()
                        .filter(n -> n instanceof Button b
                                && Messages.get("timepicker.ok").equals(b.getText()))
                        .map(n -> (Button) n)
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("面板应有「确定」按钮"));
                ok.fire();
                assertEquals(LocalDateTime.of(2026, 10, 25, 9, 30), captured.get(),
                        "未按 Enter 的编辑也必须被采纳，且改的是被点到的那一段（日）");
                assertEquals("2026-10-25 09:30:00", field.getText());
            });
        }

        @Test
        @DisplayName("获得焦点时默认停在第一个时间段：不点任何地方直接敲数字就是改时间")
        void defaultCaretSitsOnFirstTimeSegment() {
            runOnFxThreadAndWait(() -> {
                AtomicReference<LocalDateTime> captured = new AtomicReference<>();
                TextField field = fieldOf(build(captured));
                field.requestFocus();
                pumpFxEvents();
                typeAll(field, "23");
                press(field, KeyCode.ENTER);
                assertEquals(LocalDateTime.of(2026, 10, 11, 23, 30), captured.get(),
                        "默认光标在时上，不是年上");
            });
        }
    }
}
