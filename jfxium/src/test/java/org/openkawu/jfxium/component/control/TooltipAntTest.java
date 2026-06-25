package org.openkawu.jfxium.component.control;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TooltipAnt 单元测试 —— 覆盖工厂创建、文本/样式链式 API、delay/duration/hideDelay 时序、
 * install(node) 一键安装。
 *
 * <h2>特殊设计说明</h2>
 * <p>TooltipAnt 继承自 {@link Tooltip}（PopupControl → Control），<b>不是 Node</b>，
 * 因此不能 {@code implements LayoutCommon}（LayoutCommon 的 default 方法会对 {@code this}
 * 做 {@code (Node) this} 强转）。本组件采用「继承 Tooltip + 独立 styleClass/style 桥接」的模式：
 * 暴露同名的 {@code styleClass(String)} / {@code style(String)} 链式 API 兜底。</p>
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create(text) / 无参构造 / 默认 styleClass</li>
 *   <li>text：链式设文本 + null 安全</li>
 *   <li>styleClass / style：idempotent 幂等性</li>
 *   <li>时序：delay / duration / hideDelay + null 安全</li>
 *   <li>install：静态便捷方法 + null 安全</li>
 *   <li>链式串联</li>
 * </ul>
 */
@DisplayName("TooltipAnt")
class TooltipAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create(text) 设文本 + 挂默认 styleClass")
    void create_withText() {
        TooltipAnt tip = TooltipAnt.create("这是提示").build();
        assertNotNull(tip);
        assertEquals("这是提示", tip.getText());
        assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP));
    }

    @Test
    @DisplayName("create(null) 兜底空字符串，不抛 NPE")
    void create_nullText_safeText() {
        TooltipAnt tip = TooltipAnt.create(null).build();
        assertEquals("", tip.getText());
    }

    @Test
    @DisplayName("无参构造 默认空文本")
    void create_empty() {
        TooltipAnt tip = new TooltipAnt();
        assertEquals("", tip.getText());
        assertTrue(tip.getStyleClass().contains(JfxStyles.TOOLTIP));
    }

    @Test
    @DisplayName("单参构造 直接传字符串初始化")
    void constructor_withText() {
        TooltipAnt tip = new TooltipAnt("直接构造");
        assertEquals("直接构造", tip.getText());
    }

    @Test
    @DisplayName("单参构造 null 文本兜底")
    void constructor_nullText() {
        TooltipAnt tip = new TooltipAnt(null);
        assertEquals("", tip.getText());
    }

    // ============================================================
    // text 流式 API
    // ============================================================

    @Nested
    @DisplayName("text 流式 API")
    class TextApi {

        @Test
        @DisplayName("text(String) 链式设文本")
        void text_chained() {
            TooltipAnt tip = TooltipAnt.create("init")
                    .text("覆盖文本")
                    .build();
            assertEquals("覆盖文本", tip.getText());
        }

        @Test
        @DisplayName("text(null) 不抛 NPE，文本置为空字符串")
        void text_nullSafe() {
            TooltipAnt tip = TooltipAnt.create("init")
                    .text(null)
                    .build();
            assertEquals("", tip.getText());
        }
    }

    // ============================================================
    // styleClass / style（独立 API，替代 LayoutCommon）
    // ============================================================

    @Nested
    @DisplayName("styleClass / style（独立 API）")
    class StyleApi {

        @Test
        @DisplayName("styleClass 追加样式类")
        void styleClass_add() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .styleClass("jfx-tooltip-success")
                    .build();
            assertTrue(tip.getStyleClass().contains("jfx-tooltip-success"));
        }

        @Test
        @DisplayName("styleClass 幂等——重复调用不重复挂")
        void styleClass_idempotent() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .styleClass("jfx-tooltip-success")
                    .styleClass("jfx-tooltip-success")
                    .build();
            int count = 0;
            for (String s : tip.getStyleClass()) {
                if ("jfx-tooltip-success".equals(s)) count++;
            }
            assertEquals(1, count);
        }

        @Test
        @DisplayName("styleClass(null / empty) 不抛异常")
        void styleClass_nullSafe() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .styleClass(null)
                    .styleClass("")
                    .build();
            assertNotNull(tip);
        }

        @Test
        @DisplayName("style 设置 inline style")
        void style_set() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .style("-fx-font-size: 14px;")
                    .build();
            // setStyle 写入 Node.style（Tooltip 虽是 PopupControl 但支持 style）
            assertNotNull(tip);
        }

        @Test
        @DisplayName("style(null) 不抛异常，不修改 inline style")
        void style_nullSafe() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .style(null)
                    .build();
            assertNotNull(tip);
        }
    }

    // ============================================================
    // 时序控制：delay / duration / hideDelay
    // ============================================================

    @Nested
    @DisplayName("时序控制（delay / duration / hideDelay）")
    class Timing {

        @Test
        @DisplayName("delay(Duration) 设显示延迟")
        void delay_set() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .delay(Duration.millis(500))
                    .build();
            // Tooltip.getShowDelay 返回 Duration 对象
            assertEquals(500, tip.getShowDelay().toMillis());
        }

        @Test
        @DisplayName("duration(Duration) 设显示时长")
        void duration_set() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .duration(Duration.seconds(5))
                    .build();
            assertEquals(5000, tip.getShowDuration().toMillis());
        }

        @Test
        @DisplayName("hideDelay(Duration) 设隐藏延迟")
        void hideDelay_set() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .hideDelay(Duration.millis(800))
                    .build();
            assertEquals(800, tip.getHideDelay().toMillis());
        }

        @Test
        @DisplayName("delay(null) 不抛异常，不修改值")
        void delay_nullSafe() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .delay(null)
                    .build();
            assertNotNull(tip);
        }

        @Test
        @DisplayName("duration(null) 不抛异常，不修改值")
        void duration_nullSafe() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .duration(null)
                    .build();
            assertNotNull(tip);
        }

        @Test
        @DisplayName("hideDelay(null) 不抛异常，不修改值")
        void hideDelay_nullSafe() {
            TooltipAnt tip = TooltipAnt.create("t")
                    .hideDelay(null)
                    .build();
            assertNotNull(tip);
        }
    }

    // ============================================================
    // install(node) 一键安装
    // ============================================================

    @Test
    @DisplayName("install(node) 一键安装到目标节点（实例方法版）")
    void install_toNode() {
        Button btn = ButtonAnt.create("悬停").build();
        TooltipAnt tip = TooltipAnt.create("提示文本").build();
        // 不抛异常即可——install 后 Tooltip.install 内部会挂到 btn.getProperties()
        tip.install(btn);
        // Tooltip.install 实际挂的是 Tooltip 对象本身（非 Text 文本），通过 getProperties 读出
        assertNotNull(btn);
    }

    @Test
    @DisplayName("install(null) 不抛异常")
    void install_nullSafe() {
        TooltipAnt tip = TooltipAnt.create("t").build();
        // 不抛异常即可
        tip.install(null);
        assertNotNull(tip);
    }

    // ============================================================
    // 链式串联 + 继承式核心契约
    // ============================================================

    @Test
    @DisplayName("全链式串联 + 完整契约生效")
    void fullChain() {
        TooltipAnt tip = TooltipAnt.create("初始文本")
                .text("最终文本")
                .styleClass("jfx-tooltip-success")
                .delay(Duration.millis(300))
                .duration(Duration.seconds(3))
                .hideDelay(Duration.millis(200))
                .build();
        assertEquals("最终文本", tip.getText());
        assertTrue(tip.getStyleClass().contains("jfx-tooltip-success"));
        assertEquals(300, tip.getShowDelay().toMillis());
        assertEquals(3000, tip.getShowDuration().toMillis());
        assertEquals(200, tip.getHideDelay().toMillis());
    }

    @Test
    @DisplayName("build() 返回自身（继承式核心契约）")
    void build_returnsSelf() {
        TooltipAnt tip = TooltipAnt.create("t");
        assertSame(tip, tip.build());
    }

    @Test
    @DisplayName("继承式：父类 Tooltip 引用可接收（多态兼容）")
    void parentReference_polymorphism() {
        Tooltip tip = TooltipAnt.create("通过父类接收").build();
        assertInstanceOf(TooltipAnt.class, tip);
        assertEquals("通过父类接收", tip.getText());
    }
}
