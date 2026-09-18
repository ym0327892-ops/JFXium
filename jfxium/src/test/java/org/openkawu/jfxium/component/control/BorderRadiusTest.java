package org.openkawu.jfxium.component.control;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.builder.Radius;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 7 个继承式 *Ant 控件的 borderRadius(Radius) 一致性测试（M25 任务）。
 *
 * <p>覆盖范围：</p>
 * <ul>
 *   <li>ButtonAnt / ChoiceBoxAnt&lt;T&gt; / ColorPickerAnt / ComboBoxAnt&lt;T&gt; / DatePickerAnt /
 *       InputAnt / TextAreaAnt —— 7 个控件（均实现 {@code LayoutCommon<SELF>}，默认方法契约）</li>
 *   <li>4 个 Radius 枚举值：NONE / SM / MD（不挂 class，走 LESS 默认） / LG</li>
 *   <li>通用契约：null 守卫、idempotent、fluent 返回 self、默认不挂任何 RADIUS_* class</li>
 * </ul>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li><b>7 × 4 = 28 个枚举矩阵用例</b>：每个组合独立测试，确保默认方法在各控件上行为一致</li>
 *   <li><b>MD 不挂 class</b>：MD 走 LESS 默认 {@code @border-radius-md}，不应挂任何 RADIUS_* class</li>
 *   <li><b>idempotent</b>：重复调只保留最后一次的 class，移除之前的</li>
 *   <li><b>null 守卫</b>：传 null 不抛异常，也不挂任何 RADIUS_* class</li>
 * </ul>
 *
 * <p><b>7 个 *Ant 全部存在测试类的状态</b>：</p>
 * <ul>
 *   <li>ButtonAntTest ✅ / InputAntTest ✅ / ComboBoxAntTest ✅ / DatePickerAntTest ✅ / CheckBoxAntTest ✅</li>
 *   <li>ChoiceBoxAntTest ❌ / ColorPickerAntTest ❌ / TextAreaAntTest ❌ —— 本文件也承担它们的回归</li>
 * </ul>
 */
@DisplayName("BorderRadius (7 *Ant × 4 枚举)")
class BorderRadiusTest extends JfxTestBase {

    // ============================================================
    // 通用契约（不依赖具体 *Ant 类型）
    // ============================================================

    @Nested
    @DisplayName("通用契约（基于 ButtonAnt）")
    class CommonContract {

        @Test
        @DisplayName("borderRadius(Radius.MD) 不挂任何 RADIUS_* class（走 LESS 默认）")
        void md_noClass() {
            ButtonAnt btn = ButtonAnt.create().borderRadius(Radius.MD).build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        @DisplayName("未调 borderRadius 时不挂任何 RADIUS_* class（默认 null 语义）")
        void default_null() {
            ButtonAnt btn = ButtonAnt.create().build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        @DisplayName("borderRadius(null) 不抛异常，也不挂任何 class")
        void nullGuard() {
            ButtonAnt btn = ButtonAnt.create().borderRadius(null).build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        @DisplayName("borderRadius 重复调用：清旧挂新（幂等性）")
        void idempotent() {
            ButtonAnt btn = ButtonAnt.create()
                    .borderRadius(Radius.SM)
                    .borderRadius(Radius.LG)
                    .build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_SM),
                    "重复调应清除 SM");
            assertTrue(btn.getStyleClass().contains(JfxStyles.RADIUS_LG),
                    "LG 应保留");
        }

        @Test
        @DisplayName("borderRadius 返回 self，支持链式")
        void fluent() {
            ButtonAnt btn = ButtonAnt.create();
            assertSame(btn, btn.borderRadius(Radius.MD));
        }

        @Test
        @DisplayName("NONE → SM 切换：清除 NONE，挂 SM")
        void switch_NONE_to_SM() {
            ButtonAnt btn = ButtonAnt.create()
                    .borderRadius(Radius.NONE)
                    .borderRadius(Radius.SM)
                    .build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertTrue(btn.getStyleClass().contains(JfxStyles.RADIUS_SM));
        }
    }

    // ============================================================
    // ButtonAnt
    // ============================================================

    @Nested
    @DisplayName("ButtonAnt")
    class ForButtonAnt {

        @Test
        void none() {
            ButtonAnt btn = ButtonAnt.create().borderRadius(Radius.NONE).build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.RADIUS_NONE));
        }

        @Test
        void sm() {
            ButtonAnt btn = ButtonAnt.create().borderRadius(Radius.SM).build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.RADIUS_SM));
        }

        @Test
        void md() {
            ButtonAnt btn = ButtonAnt.create().borderRadius(Radius.MD).build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(btn.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        void lg() {
            ButtonAnt btn = ButtonAnt.create().borderRadius(Radius.LG).build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }
    }

    // ============================================================
    // ChoiceBoxAnt<T>
    // ============================================================

    @Nested
    @DisplayName("ChoiceBoxAnt<String>")
    class ForChoiceBoxAnt {

        @Test
        void none() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create().borderRadius(Radius.NONE);
            assertTrue(cb.getStyleClass().contains(JfxStyles.RADIUS_NONE));
        }

        @Test
        void sm() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create().borderRadius(Radius.SM);
            assertTrue(cb.getStyleClass().contains(JfxStyles.RADIUS_SM));
        }

        @Test
        void md() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create().borderRadius(Radius.MD);
            assertFalse(cb.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(cb.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(cb.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        void lg() {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create().borderRadius(Radius.LG);
            assertTrue(cb.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }
    }

    // ============================================================
    // ColorPickerAnt
    // ============================================================

    @Nested
    @DisplayName("ColorPickerAnt")
    class ForColorPickerAnt {

        @Test
        void none() {
            ColorPickerAnt cp = ColorPickerAnt.create().borderRadius(Radius.NONE);
            assertTrue(cp.getStyleClass().contains(JfxStyles.RADIUS_NONE));
        }

        @Test
        void sm() {
            ColorPickerAnt cp = ColorPickerAnt.create().borderRadius(Radius.SM);
            assertTrue(cp.getStyleClass().contains(JfxStyles.RADIUS_SM));
        }

        @Test
        void md() {
            ColorPickerAnt cp = ColorPickerAnt.create().borderRadius(Radius.MD);
            assertFalse(cp.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(cp.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(cp.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        void lg() {
            ColorPickerAnt cp = ColorPickerAnt.create().borderRadius(Radius.LG);
            assertTrue(cp.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }
    }

    // ============================================================
    // ComboBoxAnt<T>
    // ============================================================

    @Nested
    @DisplayName("ComboBoxAnt<String>")
    class ForComboBoxAnt {

        @Test
        void none() {
            ComboBoxAnt<String> cb = ComboBoxAnt.<String>create().borderRadius(Radius.NONE);
            assertTrue(cb.getStyleClass().contains(JfxStyles.RADIUS_NONE));
        }

        @Test
        void sm() {
            ComboBoxAnt<String> cb = ComboBoxAnt.<String>create().borderRadius(Radius.SM);
            assertTrue(cb.getStyleClass().contains(JfxStyles.RADIUS_SM));
        }

        @Test
        void md() {
            ComboBoxAnt<String> cb = ComboBoxAnt.<String>create().borderRadius(Radius.MD);
            assertFalse(cb.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(cb.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(cb.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        void lg() {
            ComboBoxAnt<String> cb = ComboBoxAnt.<String>create().borderRadius(Radius.LG);
            assertTrue(cb.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }
    }

    // ============================================================
    // DatePickerAnt
    // ============================================================

    @Nested
    @DisplayName("DatePickerAnt")
    class ForDatePickerAnt {

        @Test
        void none() {
            DatePickerAnt dp = DatePickerAnt.create().borderRadius(Radius.NONE);
            assertTrue(dp.getStyleClass().contains(JfxStyles.RADIUS_NONE));
        }

        @Test
        void sm() {
            DatePickerAnt dp = DatePickerAnt.create().borderRadius(Radius.SM);
            assertTrue(dp.getStyleClass().contains(JfxStyles.RADIUS_SM));
        }

        @Test
        void md() {
            DatePickerAnt dp = DatePickerAnt.create().borderRadius(Radius.MD);
            assertFalse(dp.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(dp.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(dp.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        void lg() {
            DatePickerAnt dp = DatePickerAnt.create().borderRadius(Radius.LG);
            assertTrue(dp.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }
    }

    // ============================================================
    // InputAnt
    // ============================================================

    @Nested
    @DisplayName("InputAnt")
    class ForInputAnt {

        @Test
        void none() {
            InputAnt input = InputAnt.create().borderRadius(Radius.NONE);
            assertTrue(input.getStyleClass().contains(JfxStyles.RADIUS_NONE));
        }

        @Test
        void sm() {
            InputAnt input = InputAnt.create().borderRadius(Radius.SM);
            assertTrue(input.getStyleClass().contains(JfxStyles.RADIUS_SM));
        }

        @Test
        void md() {
            InputAnt input = InputAnt.create().borderRadius(Radius.MD);
            assertFalse(input.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(input.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(input.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        void lg() {
            InputAnt input = InputAnt.create().borderRadius(Radius.LG);
            assertTrue(input.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }
    }

    // ============================================================
    // TextAreaAnt
    // ============================================================

    @Nested
    @DisplayName("TextAreaAnt")
    class ForTextAreaAnt {

        @Test
        void none() {
            TextAreaAnt ta = TextAreaAnt.create().borderRadius(Radius.NONE);
            assertTrue(ta.getStyleClass().contains(JfxStyles.RADIUS_NONE));
        }

        @Test
        void sm() {
            TextAreaAnt ta = TextAreaAnt.create().borderRadius(Radius.SM);
            assertTrue(ta.getStyleClass().contains(JfxStyles.RADIUS_SM));
        }

        @Test
        void md() {
            TextAreaAnt ta = TextAreaAnt.create().borderRadius(Radius.MD);
            assertFalse(ta.getStyleClass().contains(JfxStyles.RADIUS_NONE));
            assertFalse(ta.getStyleClass().contains(JfxStyles.RADIUS_SM));
            assertFalse(ta.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }

        @Test
        void lg() {
            TextAreaAnt ta = TextAreaAnt.create().borderRadius(Radius.LG);
            assertTrue(ta.getStyleClass().contains(JfxStyles.RADIUS_LG));
        }
    }
}
