package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TitledPaneAnt 单元测试 —— 覆盖工厂创建、title/content、expanded/animated/collapsible 行为。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create + build 返回 TitledPane 且挂默认 styleClass</li>
 *   <li>构造重载：create() / create(title) / create(title, content)</li>
 *   <li>行为：expanded / animated / collapsible</li>
 *   <li>链式串联</li>
 *   <li>LayoutCommon 接口集成：disabled 来自接口默认方法</li>
 * </ul>
 *
 * <p><b>注意</b>：M19.x 继承式 + 双工厂模式重构后，{@code build()} 返回 {@link TitledPaneAnt}
 * 自身；老测试 / 业务代码可通过 {@link TitledPane} 父类型引用接收（多态兼容）。</p>
 */
@DisplayName("TitledPaneAnt")
class TitledPaneAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create() 默认空标题 + 挂默认 styleClass")
    void create_default() {
        TitledPaneAnt pane = TitledPaneAnt.create().build();
        assertNotNull(pane);
        assertEquals("", pane.getText());
        assertTrue(pane.getStyleClass().contains(JfxStyles.JFX_TITLED_PANE));
    }

    @Test
    @DisplayName("create(title) 设置标题")
    void create_withTitle() {
        TitledPaneAnt pane = TitledPaneAnt.create("高级设置").build();
        assertEquals("高级设置", pane.getText());
    }

    @Test
    @DisplayName("create(title, content) 同时设置标题 + 内容")
    void create_withTitleAndContent() {
        Label body = new Label("body content");
        TitledPaneAnt pane = TitledPaneAnt.create("折叠面板", body).build();
        assertEquals("折叠面板", pane.getText());
        assertEquals(body, pane.getContent());
    }

    @Test
    @DisplayName("null 标题兜底为空字符串，不抛 NPE")
    void create_nullTitle_safeText() {
        TitledPaneAnt pane = TitledPaneAnt.create(null).build();
        assertEquals("", pane.getText());
    }

    // ============================================================
    // title / content（流式）
    // ============================================================

    @Nested
    @DisplayName("title / content（流式 API）")
    class TitleContent {

        @Test
        @DisplayName("title(String) 链式设标题")
        void title_chained() {
            TitledPaneAnt pane = TitledPaneAnt.create()
                    .title("网络设置")
                    .build();
            assertEquals("网络设置", pane.getText());
        }

        @Test
        @DisplayName("content(Node) 链式设内容")
        void content_chained() {
            Label body = new Label("内容");
            TitledPaneAnt pane = TitledPaneAnt.create("标题")
                    .content(body)
                    .build();
            assertEquals(body, pane.getContent());
        }

        @Test
        @DisplayName("title(null) 不抛异常，文本置为空字符串")
        void title_nullSafe() {
            TitledPaneAnt pane = TitledPaneAnt.create("初始")
                    .title(null)
                    .build();
            assertEquals("", pane.getText());
        }

        @Test
        @DisplayName("content(null) 不抛异常，可清空内容")
        void content_nullSafe() {
            TitledPaneAnt pane = TitledPaneAnt.create("标题", new Label("init"))
                    .content(null)
                    .build();
            assertNull(pane.getContent());
        }
    }

    // ============================================================
    // expanded / animated / collapsible（行为）
    // ============================================================

    @Nested
    @DisplayName("expanded / animated / collapsible 行为")
    class Behavior {

        @Test
        @DisplayName("expanded(true) 展开面板")
        void expanded_true() {
            TitledPaneAnt pane = TitledPaneAnt.create("t")
                    .expanded(true)
                    .build();
            assertTrue(pane.isExpanded());
        }

        @Test
        @DisplayName("expanded(false) 折叠面板")
        void expanded_false() {
            TitledPaneAnt pane = TitledPaneAnt.create("t")
                    .expanded(true)
                    .expanded(false)
                    .build();
            assertFalse(pane.isExpanded());
        }

        @Test
        @DisplayName("默认 expanded 状态为 true（TitledPane 原生默认）")
        void expanded_default() {
            TitledPaneAnt pane = TitledPaneAnt.create("t").build();
            // TitledPane 原生默认展开，验证我们没在 init() 中错误地改默认值
            assertTrue(pane.isExpanded());
        }

        @Test
        @DisplayName("animated(true) 启用展开/折叠动画")
        void animated_true() {
            TitledPaneAnt pane = TitledPaneAnt.create("t")
                    .animated(true)
                    .build();
            assertTrue(pane.isAnimated());
        }

        @Test
        @DisplayName("animated(false) 关闭动画")
        void animated_false() {
            TitledPaneAnt pane = TitledPaneAnt.create("t")
                    .animated(true)
                    .animated(false)
                    .build();
            assertFalse(pane.isAnimated());
        }

        @Test
        @DisplayName("collapsible(false) 禁用折叠")
        void collapsible_false() {
            TitledPaneAnt pane = TitledPaneAnt.create("t")
                    .collapsible(false)
                    .build();
            assertFalse(pane.isCollapsible());
        }

        @Test
        @DisplayName("collapsible(true) 允许折叠（默认）")
        void collapsible_true() {
            TitledPaneAnt pane = TitledPaneAnt.create("t")
                    .collapsible(false)
                    .collapsible(true)
                    .build();
            assertTrue(pane.isCollapsible());
        }
    }

    // ============================================================
    // 链式串联 + 完整契约
    // ============================================================

    @Test
    @DisplayName("全链式串联 + 跨多次 setter 正确生效")
    void fullChain() {
        Label body = new Label("body");
        TitledPaneAnt pane = TitledPaneAnt.create("分组")
                .title("最终标题")
                .content(body)
                .expanded(true)
                .animated(true)
                .collapsible(true)
                .build();
        assertEquals("最终标题", pane.getText());
        assertEquals(body, pane.getContent());
        assertTrue(pane.isExpanded());
        assertTrue(pane.isAnimated());
        assertTrue(pane.isCollapsible());
    }

    @Test
    @DisplayName("build() 返回自身（继承式核心契约）")
    void build_returnsSelf() {
        TitledPaneAnt pane = TitledPaneAnt.create();
        // 链式 build 应等于原始引用（说明 build 返回 this 而非新对象）
        assertSame(pane, pane.build());
    }

    @Test
    @DisplayName("继承式：父类 TitledPane 引用可接收（多态兼容）")
    void parentReference_polymorphism() {
        TitledPane pane = TitledPaneAnt.create("通过父类接收").build();
        assertInstanceOf(TitledPaneAnt.class, pane);
        assertEquals("通过父类接收", pane.getText());
    }

    @Test
    @DisplayName("LayoutCommon 能力：disabled() / disabled(boolean) 通过接口可用")
    void layoutCommon_disabledViaInterface() {
        TitledPaneAnt pane = TitledPaneAnt.create("t").build();
        // DisabledSupport 接口默认提供 disabled(boolean) / disabled()
        pane.disabled(true);
        assertTrue(pane.isDisable());
        pane.disabled(false);
        assertFalse(pane.isDisable());
    }
}
