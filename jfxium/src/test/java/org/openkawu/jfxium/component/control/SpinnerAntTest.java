package org.openkawu.jfxium.component.control;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SpinnerAnt 文本/光标行为测试。
 *
 * <p>Spinner 在 value 变化时会用 converter 重写 editor 文本，TextField 会把 caret 重置到 0。
 * 箭头步进后 caret 必须落在末尾（包含到达 min/max、值未变化的情况）。</p>
 */
@DisplayName("SpinnerAnt caret")
class SpinnerAntTest extends JfxTestBase {

    @Test
    @DisplayName("value() 重写文本后 caret 在末尾")
    void value_programmatic_caretAtEnd() {
        SpinnerAnt spinner = SpinnerAnt.create(-90, 90, 0, 0.01).build();
        spinner.value(42);

        assertEquals(spinner.getEditor().getLength(), spinner.getEditor().getCaretPosition());
    }

    @Test
    @DisplayName("increment() 后 caret 在末尾")
    void increment_caretAtEnd() {
        SpinnerAnt spinner = SpinnerAnt.create(-90, 90, 0, 0.01).build();
        spinner.getEditor().positionCaret(0);

        spinner.increment();

        assertEquals(spinner.getEditor().getLength(), spinner.getEditor().getCaretPosition());
    }

    @Test
    @DisplayName("decrement() 后 caret 在末尾")
    void decrement_caretAtEnd() {
        SpinnerAnt spinner = SpinnerAnt.create(-90, 90, 0, 0.01).build();
        spinner.getEditor().positionCaret(0);

        spinner.decrement();

        assertEquals(spinner.getEditor().getLength(), spinner.getEditor().getCaretPosition());
    }

    @Test
    @DisplayName("到达 min/max 后继续点击，caret 仍在末尾")
    void atBound_caretStillAtEnd() {
        SpinnerAnt spinner = SpinnerAnt.create(0, 10, 10, 1).build();
        spinner.getEditor().positionCaret(0);
        spinner.increment();   // 已在 max，值不变

        assertEquals(spinner.getEditor().getLength(), spinner.getEditor().getCaretPosition());

        spinner.value(0);
        spinner.getEditor().positionCaret(0);
        spinner.decrement();   // 已在 min，值不变

        assertEquals(spinner.getEditor().getLength(), spinner.getEditor().getCaretPosition());
    }
}
