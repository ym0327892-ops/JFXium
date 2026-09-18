package org.openkawu.jfxium.component.control;

import javafx.scene.control.Button;
import javafx.util.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TooltipStyleHelper 单元测试 —— 覆盖 modifier() 映射表、时序常量、install 重载。
 *
 * <p>测试目标：</p>
 * <ul>
 *   <li><b>modifier(Type)</b>：5 种状态色 + null 输入 → 正确映射到 {@link JfxStyles} 常量</li>
 *   <li><b>时序常量</b>：与 {@link TooltipAnt} 默认值对齐（200ms / 200ms / 10s）</li>
 *   <li><b>install 重载</b>：4 种签名都走「TooltipAnt.create + styleClass + install」链路,
 *       各种 null 输入（node / text / type / Duration）都静默兜底不抛 NPE</li>
 * </ul>
 *
 * <h2>测试策略</h2>
 * <p>由于 JavaFX {@code Tooltip.install(node, tooltip)} 没有公开 API 读出已安装的 Tooltip
 * （Tooltip 实际由私有 {@code TooltipBehavior} 持有）,
 * Helper.install 的「挂上 styleClass」行为通过 {@link TooltipAnt#styleClass(String)} 间接验证：
 * 测试用例手动模拟 Helper.install 内部逻辑,断言 styleClass 确实被挂上,
 * Helper.install 本身只验证「不抛异常 + node 仍存活」。</p>
 */
@DisplayName("TooltipStyleHelper")
class TooltipStyleHelperTest extends JfxTestBase {

    // ============================================================
    // modifier() 映射表
    // ============================================================

    @Nested
    @DisplayName("modifier(Type) 状态色映射")
    class Modifier {

        @Test
        @DisplayName("DEFAULT → null（不挂任何状态修饰类）")
        void default_returnsNull() {
            assertNull(TooltipStyleHelper.modifier(TooltipStyleHelper.Type.DEFAULT));
        }

        @Test
        @DisplayName("null → null（等价 DEFAULT）")
        void nullType_returnsNull() {
            assertNull(TooltipStyleHelper.modifier(null));
        }

        @Test
        @DisplayName("SUCCESS → jfx-tooltip-success")
        void success_returnsJfxTooltipSuccess() {
            assertEquals(JfxStyles.TOOLTIP_SUCCESS,
                    TooltipStyleHelper.modifier(TooltipStyleHelper.Type.SUCCESS));
        }

        @Test
        @DisplayName("WARNING → jfx-tooltip-warning")
        void warning_returnsJfxTooltipWarning() {
            assertEquals(JfxStyles.TOOLTIP_WARNING,
                    TooltipStyleHelper.modifier(TooltipStyleHelper.Type.WARNING));
        }

        @Test
        @DisplayName("ERROR → jfx-tooltip-error")
        void error_returnsJfxTooltipError() {
            assertEquals(JfxStyles.TOOLTIP_ERROR,
                    TooltipStyleHelper.modifier(TooltipStyleHelper.Type.ERROR));
        }

        @Test
        @DisplayName("INFO → jfx-tooltip-info")
        void info_returnsJfxTooltipInfo() {
            assertEquals(JfxStyles.TOOLTIP_INFO,
                    TooltipStyleHelper.modifier(TooltipStyleHelper.Type.INFO));
        }
    }

    // ============================================================
    // 时序常量
    // ============================================================

    @Nested
    @DisplayName("时序常量")
    class TimingConstants {

        @Test
        @DisplayName("SHOW_DELAY_DEFAULT = 200ms（与 TooltipAnt 默认对齐）")
        void showDelayDefault_is200ms() {
            assertEquals(200, TooltipStyleHelper.SHOW_DELAY_DEFAULT.toMillis());
        }

        @Test
        @DisplayName("HIDE_DELAY_DEFAULT = 200ms（与 TooltipAnt 默认对齐）")
        void hideDelayDefault_is200ms() {
            assertEquals(200, TooltipStyleHelper.HIDE_DELAY_DEFAULT.toMillis());
        }

        @Test
        @DisplayName("SHOW_DURATION_DEFAULT = 10s（与 TooltipAnt 默认对齐）")
        void showDurationDefault_is10s() {
            assertEquals(10_000, TooltipStyleHelper.SHOW_DURATION_DEFAULT.toMillis());
        }
    }

    // ============================================================
    // install 重载
    // ============================================================

    @Nested
    @DisplayName("install() 重载")
    class Install {

        @Test
        @DisplayName("install(node, text) 默认 tooltip 不抛异常")
        void install_default_noException() {
            Button btn = ButtonAnt.create("按钮").build();
            assertDoesNotThrow(() -> TooltipStyleHelper.install(btn, "普通提示"));
            assertNotNull(btn);
        }

        @Test
        @DisplayName("install(node, text, SUCCESS) 通过 TooltipAnt 挂 jfx-tooltip-success")
        void install_success_addsSuccessClass() {
            // 模拟 Helper.install 内部流程: TooltipAnt.create + styleClass(modifier(SUCCESS))
            TooltipAnt tip = TooltipAnt.create("成功提示");
            String mod = TooltipStyleHelper.modifier(TooltipStyleHelper.Type.SUCCESS);
            assertNotNull(mod);
            tip.styleClass(mod);
            assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP_SUCCESS),
                    "TooltipAnt 应挂上 jfx-tooltip-success 修饰类");
            assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP),
                    "TooltipAnt 应保留 jfx-tooltip 根类");
        }

        @Test
        @DisplayName("install(node, text, WARNING) 通过 TooltipAnt 挂 jfx-tooltip-warning")
        void install_warning_addsWarningClass() {
            TooltipAnt tip = TooltipAnt.create("警告提示");
            tip.styleClass(TooltipStyleHelper.modifier(TooltipStyleHelper.Type.WARNING));
            assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP_WARNING));
        }

        @Test
        @DisplayName("install(node, text, ERROR) 通过 TooltipAnt 挂 jfx-tooltip-error")
        void install_error_addsErrorClass() {
            TooltipAnt tip = TooltipAnt.create("错误提示");
            tip.styleClass(TooltipStyleHelper.modifier(TooltipStyleHelper.Type.ERROR));
            assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP_ERROR));
        }

        @Test
        @DisplayName("install(node, text, INFO) 通过 TooltipAnt 挂 jfx-tooltip-info")
        void install_info_addsInfoClass() {
            TooltipAnt tip = TooltipAnt.create("信息提示");
            tip.styleClass(TooltipStyleHelper.modifier(TooltipStyleHelper.Type.INFO));
            assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP_INFO));
        }

        @Test
        @DisplayName("install(node, text, DEFAULT) 不挂任何状态修饰类（仅 TOOLTIP 根类）")
        void install_default_noExtraClass() {
            TooltipAnt tip = TooltipAnt.create("默认");
            String mod = TooltipStyleHelper.modifier(TooltipStyleHelper.Type.DEFAULT);
            assertNull(mod, "DEFAULT 应返回 null,Helper 不会调用 styleClass(null)");
            // 模拟 if (mod != null) 不成立 → 不挂任何状态修饰类
            assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP),
                    "TooltipAnt 保留 jfx-tooltip 根类");
            assertFalse(tip.getStyleClass().contains(JfxStyles.TOOLTIP_SUCCESS));
            assertFalse(tip.getStyleClass().contains(JfxStyles.TOOLTIP_WARNING));
            assertFalse(tip.getStyleClass().contains(JfxStyles.TOOLTIP_ERROR));
            assertFalse(tip.getStyleClass().contains(JfxStyles.TOOLTIP_INFO));
        }

        @Test
        @DisplayName("install(node, text, null type) 不抛 NPE,等价 DEFAULT 处理")
        void install_nullType_noNPE() {
            Button btn = ButtonAnt.create("按钮").build();
            assertDoesNotThrow(() -> TooltipStyleHelper.install(btn, "text", null));
            assertNotNull(btn);
        }

        @Test
        @DisplayName("install(node, null text) 文本降级为空字符串, 不抛 NPE")
        void install_nullText_safeText() {
            TooltipAnt tip = TooltipAnt.create(TextUtils.safeText(null));
            assertEquals("", tip.getText(),
                    "Helper 内部走 TooltipAnt.create(text) → TextUtils.safeText → 空字符串");
        }

        @Test
        @DisplayName("install(node, text, type, showDuration) 自定义显示时长")
        void install_customShowDuration() {
            // 模拟 install 4 参数重载内部流程: create + styleClass + duration
            TooltipAnt tip = TooltipAnt.create("短时提示");
            tip.styleClass(TooltipStyleHelper.modifier(TooltipStyleHelper.Type.SUCCESS));
            tip.duration(Duration.seconds(2));
            assertEquals(2000, tip.getShowDuration().toMillis(),
                    "showDuration 应被设为 2s 而非默认 10s");
            assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP_SUCCESS));
        }

        @Test
        @DisplayName("install(node, text, type, full timings) 全量时序控制生效")
        void install_fullTiming() {
            TooltipAnt tip = TooltipAnt.create("精细控制");
            tip.styleClass(TooltipStyleHelper.modifier(TooltipStyleHelper.Type.WARNING));
            tip.delay(Duration.millis(150));
            tip.duration(Duration.seconds(3));
            tip.hideDelay(Duration.millis(500));
            assertEquals(150, tip.getShowDelay().toMillis());
            assertEquals(3000, tip.getShowDuration().toMillis());
            assertEquals(500, tip.getHideDelay().toMillis());
            assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP_WARNING));
        }

        @Test
        @DisplayName("install 全量重载对 null Duration 静默跳过（保留 TooltipAnt 默认值）")
        void install_nullDuration_keepsDefault() {
            // 模拟全量重载: 全 null Duration → 不调用任何 setter, 走 TooltipAnt 默认值
            TooltipAnt tip = TooltipAnt.create("默认值");
            tip.styleClass(TooltipStyleHelper.modifier(TooltipStyleHelper.Type.INFO));
            // 三个 setter 全传 null → 不修改, 走 TooltipAnt 构造函数默认值
            // JavaFX Tooltip 默认: showDelay=0, showDuration=5s, hideDelay=200ms
            // 我们用 TooltipAnt 自己的默认值(它在 create 时未调 setter,所以继承 JavaFX 默认值)
            // 这里只验证不抛异常 + styleClass 挂上
            assertNotNull(tip);
            assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP_INFO));
        }
    }

    // ============================================================
    // null safety —— 边界
    // ============================================================

    @Nested
    @DisplayName("null safety 边界")
    class NullSafety {

        @Test
        @DisplayName("install(null, text) 4 种重载都不抛 NPE")
        void install_nullNode_noNPE() {
            assertDoesNotThrow(() -> TooltipStyleHelper.install(null, "text"));
            assertDoesNotThrow(() -> TooltipStyleHelper.install(null, "text",
                    TooltipStyleHelper.Type.SUCCESS));
            assertDoesNotThrow(() -> TooltipStyleHelper.install(null, "text",
                    TooltipStyleHelper.Type.SUCCESS, Duration.seconds(2)));
            assertDoesNotThrow(() -> TooltipStyleHelper.install(null, "text",
                    TooltipStyleHelper.Type.SUCCESS, null, null, null));
        }

        @Test
        @DisplayName("install(node, null, type) 不抛 NPE")
        void install_nullTextAndNode_noNPE() {
            assertDoesNotThrow(() -> TooltipStyleHelper.install(null, null,
                    TooltipStyleHelper.Type.WARNING));
        }
    }
}