package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AccordionAnt 单元测试 —— 覆盖 Builder 创建、pane 重载（含 disabled）、
 * activeKey/activeKeys 默认展开、null 防御、LayoutCommon 集成与链式串联。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create + build 返回 VBox + 挂 jfx-collapse styleClass</li>
 *   <li>pane：单个 / 多个 / null 兜底</li>
 *   <li>pane 重载：disabled 单面板禁用（挂 jfx-collapse-disabled）</li>
 *   <li>activeKey / activeKeys：默认展开 + accordion 互斥 + null/空串忽略</li>
 *   <li>LayoutCommon 集成：padding / disabled（来自 AbstractStyleBuilder）</li>
 *   <li>链式串联：pane().pane().activeKey().padding() 全部生效</li>
 * </ul>
 *
 * <p><b>背景</b>：P2-S12 修复 —— 原 AccordionAnt 仅暴露 {@code pane(title, content)}，
 * 与 {@link org.openkawu.jfxium.component.composite.CollapseAnt} 相比缺少 disabled 重载与
 * activeKey 入口。本次新增对应 API 并补齐单测，确保委托入口与底层组件的能力对齐。</p>
 */
@DisplayName("AccordionAnt")
class AccordionAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create() 默认空面板 + build 返回 VBox + 挂 jfx-collapse styleClass")
    void build_returnsVBoxWithCollapseClass() {
        VBox accordion = AccordionAnt.create().build();
        assertNotNull(accordion);
        assertTrue(accordion.getStyleClass().contains(JfxStyles.COLLAPSE),
                "AccordionAnt 委托 CollapseAnt，应挂 jfx-collapse styleClass");
    }

    @Test
    @DisplayName("pane(title, content) 单个面板：children 包含 1 个 panel")
    void pane_single() {
        Label body = new Label("body");
        VBox accordion = AccordionAnt.create()
                .pane("面板一", body)
                .build();
        // 1 个 panel（无 divider，最后一个 panel 不挂 divider）
        assertEquals(1, accordion.getChildren().size(),
                "单个面板时 VBox 应只含 1 个 panel 子节点");
    }

    @Test
    @DisplayName("pane() 多个面板：children 包含 N 个 panel（divider 在 panelBox 内部）")
    void pane_multiple() {
        VBox accordion = AccordionAnt.create()
                .pane("A", new Label("a"))
                .pane("B", new Label("b"))
                .pane("C", new Label("c"))
                .build();
        // 3 个 panel 子节点（divider 由 CollapseAnt 嵌入到 panelBox 内部，不直接挂在 VBox 上）
        assertEquals(3, accordion.getChildren().size(),
                "3 个面板应有 3 个 panelBox 子节点");
    }

    // ============================================================
    // pane 重载（disabled）
    // ============================================================

    @Nested
    @DisplayName("pane(title, content, disabled) — 单面板禁用")
    class PaneDisabled {

        @Test
        @DisplayName("disabled=true 时该面板 header 挂 jfx-collapse-disabled")
        void pane_disabled_true() {
            VBox accordion = AccordionAnt.create()
                    .pane("禁用项", new Label("x"), true)
                    .build();
            // VBox > PanelVBox > HeaderHBox（挂 COLLAPSE_DISABLED）
            VBox panelBox = (VBox) accordion.getChildren().get(0);
            Node header = panelBox.getChildren().get(0);
            assertTrue(header.getStyleClass().contains(JfxStyles.COLLAPSE_DISABLED),
                    "disabled=true 的面板 header 应挂 jfx-collapse-disabled");
        }

        @Test
        @DisplayName("disabled=false 等价于 pane(title, content)，不挂 disabled class")
        void pane_disabled_false_equivalentToNoArg() {
            VBox accordion = AccordionAnt.create()
                    .pane("正常项", new Label("x"), false)
                    .build();
            VBox panelBox = (VBox) accordion.getChildren().get(0);
            Node header = panelBox.getChildren().get(0);
            assertFalse(header.getStyleClass().contains(JfxStyles.COLLAPSE_DISABLED),
                    "disabled=false 的面板 header 不应挂 disabled class");
        }

        @Test
        @DisplayName("混用：第一个 disabled、第二个正常")
        void pane_mixedDisabled() {
            VBox accordion = AccordionAnt.create()
                    .pane("锁定项", new Label("x"), true)
                    .pane("可选项", new Label("y"))
                    .build();

            VBox first = (VBox) accordion.getChildren().get(0);
            Node firstHeader = first.getChildren().get(0);
            assertTrue(firstHeader.getStyleClass().contains(JfxStyles.COLLAPSE_DISABLED));

            VBox second = (VBox) accordion.getChildren().get(1);
            Node secondHeader = second.getChildren().get(0);
            assertFalse(secondHeader.getStyleClass().contains(JfxStyles.COLLAPSE_DISABLED));
        }
    }

    // ============================================================
    // activeKey / activeKeys
    // ============================================================

    @Nested
    @DisplayName("activeKey / activeKeys — 默认展开")
    class ActiveKey {

        @Test
        @DisplayName("activeKey(key) 让对应面板默认可见（accordion 互斥，只展开一个）")
        void activeKey_single() {
            VBox accordion = AccordionAnt.create()
                    .pane("p1", new Label("1"))
                    .pane("p2", new Label("2"))
                    .activeKey("accordion-2")
                    .build();

            VBox firstPanel = (VBox) accordion.getChildren().get(0);
            VBox firstContent = (VBox) firstPanel.getChildren().get(1);
            assertFalse(firstContent.isVisible(),
                    "未指定 activeKey 的面板默认折叠");

            VBox secondPanel = (VBox) accordion.getChildren().get(1);
            VBox secondContent = (VBox) secondPanel.getChildren().get(1);
            assertTrue(secondContent.isVisible(),
                    "activeKey=\"accordion-2\" 对应的面板应默认展开");
        }

        @Test
        @DisplayName("activeKey 传 null 静默忽略，不抛 NPE")
        void activeKey_null_safe() {
            assertDoesNotThrow(() ->
                    AccordionAnt.create()
                            .pane("p1", new Label("1"))
                            .activeKey(null)
                            .build());
        }

        @Test
        @DisplayName("activeKey 传 \"\" 静默忽略，不抛异常")
        void activeKey_empty_safe() {
            assertDoesNotThrow(() ->
                    AccordionAnt.create()
                            .pane("p1", new Label("1"))
                            .activeKey("")
                            .build());
        }

        @Test
        @DisplayName("activeKeys(List) 批量设置：所有匹配 key 的面板可见（accordion 互斥仅在点击时生效）")
        void activeKeys_list() {
            VBox accordion = AccordionAnt.create()
                    .pane("p1", new Label("1"))
                    .pane("p2", new Label("2"))
                    .pane("p3", new Label("3"))
                    .activeKeys(List.of("accordion-1", "accordion-2", "accordion-3"))
                    .build();

            // CollapseAnt.build() 仅按 activeKeys.contains(key) 判定初始可见性，
            // 不做 accordion 互斥（互斥逻辑只在点击 header 时触发）。
            // 因此 activeKeys 中包含的 key 对应的面板都会默认展开。
            VBox firstPanel = (VBox) accordion.getChildren().get(0);
            VBox firstContent = (VBox) firstPanel.getChildren().get(1);
            assertTrue(firstContent.isVisible(),
                    "activeKeys 包含 accordion-1，panel_1 默认可见");

            VBox secondPanel = (VBox) accordion.getChildren().get(1);
            VBox secondContent = (VBox) secondPanel.getChildren().get(1);
            assertTrue(secondContent.isVisible(),
                    "activeKeys 包含 accordion-2，panel_2 默认可见（CollapseAnt 不做 build-time 互斥）");
        }

        @Test
        @DisplayName("activeKeys(null) 静默忽略")
        void activeKeys_nullList_safe() {
            assertDoesNotThrow(() ->
                    AccordionAnt.create()
                            .pane("p1", new Label("1"))
                            .activeKeys(null)
                            .build());
        }

        @Test
        @DisplayName("activeKey 指向不存在的 key —— 该面板不展开，其他默认折叠")
        void activeKey_unknownKey() {
            VBox accordion = AccordionAnt.create()
                    .pane("p1", new Label("1"))
                    .activeKey("does-not-exist")
                    .build();

            VBox firstPanel = (VBox) accordion.getChildren().get(0);
            VBox firstContent = (VBox) firstPanel.getChildren().get(1);
            assertFalse(firstContent.isVisible(),
                    "activeKey 指向不存在的 key 时不应展开任何面板");
        }
    }

    // ============================================================
    // null 防御
    // ============================================================

    @Test
    @DisplayName("pane(title, null) —— 内容为 null 不抛 NPE（CollapseAnt 内部已防御）")
    void pane_nullContent_safe() {
        assertDoesNotThrow(() ->
                AccordionAnt.create()
                        .pane("p1", null)
                        .build());
    }

    @Test
    @DisplayName("pane(null, content) —— 标题为 null 不抛 NPE")
    void pane_nullTitle_safe() {
        assertDoesNotThrow(() ->
                AccordionAnt.create()
                        .pane(null, new Label("body"))
                        .build());
    }

    // ============================================================
    // LayoutCommon 集成（来自 AbstractStyleBuilder）
    // ============================================================

    @Nested
    @DisplayName("LayoutCommon 接口集成")
    class LayoutCommonIntegration {

        @Test
        @DisplayName("padding(double) 透传到 VBox")
        void padding_applied() {
            VBox accordion = AccordionAnt.create()
                    .pane("p1", new Label("x"))
                    .padding(16)
                    .build();
            assertEquals(16, accordion.getPadding().getTop(), 0.001);
            assertEquals(16, accordion.getPadding().getRight(), 0.001);
            assertEquals(16, accordion.getPadding().getBottom(), 0.001);
            assertEquals(16, accordion.getPadding().getLeft(), 0.001);
        }

        @Test
        @DisplayName("styleClass(\"my-class\") 追加到 VBox")
        void styleClass_added() {
            VBox accordion = AccordionAnt.create()
                    .pane("p1", new Label("x"))
                    .styleClass("my-custom-class")
                    .build();
            assertTrue(accordion.getStyleClass().contains("my-custom-class"));
            assertTrue(accordion.getStyleClass().contains(JfxStyles.COLLAPSE),
                    "内置 jfx-collapse 与用户追加的类应共存");
        }

        @Test
        @DisplayName("disabled(true) 透传到 VBox.setDisable")
        void disabled_propagated() {
            VBox accordion = AccordionAnt.create()
                    .pane("p1", new Label("x"))
                    .disabled(true)
                    .build();
            assertTrue(accordion.isDisable());
        }
    }

    // ============================================================
    // 链式串联
    // ============================================================

    @Test
    @DisplayName("链式串联：pane().pane().disabled().activeKey().padding() 全部生效")
    void chained_allApis() {
        VBox accordion = AccordionAnt.create()
                .pane("面板 A", new Label("a"))
                .pane("面板 B", new Label("b"), true) // 禁用
                .pane("面板 C", new Label("c"))
                .activeKey("accordion-1")
                .padding(8)
                .styleClass("custom")
                .build();

        assertEquals(8, accordion.getPadding().getTop(), 0.001);
        assertTrue(accordion.getStyleClass().contains("custom"));

        // panel A 应展开
        VBox panelA = (VBox) accordion.getChildren().get(0);
        VBox contentA = (VBox) panelA.getChildren().get(1);
        assertTrue(contentA.isVisible(), "activeKey=\"accordion-1\" 对应的 panel A 应默认展开");

        // panel B 应禁用（index 1：VBox.children 仅有 3 个 panelBox，没有 divider 在 VBox 直接子节点）
        VBox panelB = (VBox) accordion.getChildren().get(1);
        Node headerB = panelB.getChildren().get(0);
        assertTrue(headerB.getStyleClass().contains(JfxStyles.COLLAPSE_DISABLED));
    }
}