package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GroupBoxAnt 单元测试 —— 覆盖 Builder 创建、title/extra/content/bordered/size/type、
 * actions 变长、tab 切换、header 渲染开关、hoverable 与 build() 返回 VBox 类型契约。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本结构：create + build 返回 VBox + 默认 GROUP_BOX class</li>
 *   <li>header：title / extra / content / bordered</li>
 *   <li>size / type / hoverable：styleClass 显式追加</li>
 *   <li>actions 变长：footer 渲染</li>
 *   <li>tabs：tab() + defaultActiveTabKey + activeTabKey 受控模式</li>
 *   <li>header 渲染开关：headerBackground / headerBorder</li>
 *   <li>链式：全链式串联不抛异常</li>
 * </ul>
 */
@DisplayName("GroupBoxAnt")
class GroupBoxAntTest extends JfxTestBase {

    // ============================================================
    // 基本结构
    // ============================================================

    @Test
    @DisplayName("create().build() 返回 VBox 并挂 GROUP_BOX")
    void build_returnsVBoxWithBaseClass() {
        VBox box = GroupBoxAnt.create().build();
        assertNotNull(box);
        assertTrue(box.getStyleClass().contains(JfxStyles.GROUP_BOX),
                "默认应挂 jfx-group-box");
    }

    @Test
    @DisplayName("无 title / extra / tab 时不渲染 header")
    void noHeader_whenAllEmpty() {
        VBox box = GroupBoxAnt.create().build();
        // 唯一子节点应是 body
        assertEquals(1, box.getChildren().size(),
                "无 title/extra/tab 时只有 body 一个子节点");
    }

    // ============================================================
    // header
    // ============================================================

    @Nested
    @DisplayName("header（title / extra / content）")
    class Header {

        @Test
        @DisplayName("title 设置后渲染 GROUP_BOX_HEADER 子节点")
        void title_rendersHeader() {
            VBox box = GroupBoxAnt.create()
                    .title("基本信息")
                    .build();
            assertEquals(2, box.getChildren().size(), "应渲染 header + body");
            // header 是 VBox
            assertTrue(box.getChildren().get(0) instanceof VBox);
            assertTrue(box.getChildren().get(0).getStyleClass()
                    .contains(JfxStyles.GROUP_BOX_HEADER));
        }

        @Test
        @DisplayName("title Label 内容正确")
        void title_labelText() {
            VBox box = GroupBoxAnt.create()
                    .title("账户信息")
                    .build();
            // header -> titleRow(HBox) -> title(Label)
            VBox header = (VBox) box.getChildren().get(0);
            HBox titleRow = (HBox) header.getChildren().get(0);
            // left side 第一个节点
            Node left = titleRow.getChildren().get(0);
            assertTrue(left instanceof Label);
            assertEquals("账户信息", ((Label) left).getText());
            assertTrue(left.getStyleClass().contains(JfxStyles.GROUP_BOX_TITLE));
        }

        @Test
        @DisplayName("extra 节点挂到 titleRow 右侧（HBarAnt 二段布局：left + spacer + right）")
        void extra_attachedToTitleRow() {
            Label extra = new Label("更多");
            VBox box = GroupBoxAnt.create()
                    .title("标题")
                    .extra(extra)
                    .build();
            VBox header = (VBox) box.getChildren().get(0);
            HBox titleRow = (HBox) header.getChildren().get(0);
            // HBarAnt 二段布局：left(标题) + spacer(grow) + right(extra)
            // —— 最后一项是 extra
            assertEquals(extra, titleRow.getChildren().get(titleRow.getChildren().size() - 1));
        }

        @Test
        @DisplayName("content 注入 body 且挂 GROUP_BOX_CONTENT")
        void content_injectedToBody() {
            Region content = new Region();
            VBox box = GroupBoxAnt.create()
                    .content(content)
                    .build();
            // body 是 VBox
            VBox body = (VBox) box.getChildren().get(0);
            assertEquals(1, body.getChildren().size());
            assertEquals(content, body.getChildren().get(0));
            assertTrue(content.getStyleClass().contains(JfxStyles.GROUP_BOX_CONTENT));
        }
    }

    // ============================================================
    // bordered / size / type / hoverable
    // ============================================================

    @Nested
    @DisplayName("视觉开关（bordered / size / type / hoverable）")
    class VisualFlags {

        @Test
        @DisplayName("bordered(true) 挂 GROUP_BOX_BORDERED")
        void bordered_true() {
            VBox box = GroupBoxAnt.create().bordered(true).build();
            assertTrue(box.getStyleClass().contains(JfxStyles.GROUP_BOX_BORDERED));
        }

        @Test
        @DisplayName("bordered 默认 false 不挂 BORDERED")
        void bordered_defaultFalse() {
            VBox box = GroupBoxAnt.create().build();
            assertFalse(box.getStyleClass().contains(JfxStyles.GROUP_BOX_BORDERED));
        }

        @Test
        @DisplayName("size(SMALL) 挂 GROUP_BOX_SMALL")
        void size_small() {
            VBox box = GroupBoxAnt.create()
                    .size(Size.SMALL).build();
            assertTrue(box.getStyleClass().contains(JfxStyles.GROUP_BOX_SMALL));
        }

        @Test
        @DisplayName("type(INNER) 挂 GROUP_BOX_INNER")
        void type_inner() {
            VBox box = GroupBoxAnt.create()
                    .type(GroupBoxAnt.Type.INNER).build();
            assertTrue(box.getStyleClass().contains(JfxStyles.GROUP_BOX_INNER));
        }

        @Test
        @DisplayName("hoverable(true) 默认挂 GROUP_BOX_HOVERABLE")
        void hoverable_defaultTrue() {
            VBox box = GroupBoxAnt.create().build();
            assertTrue(box.getStyleClass().contains(JfxStyles.GROUP_BOX_HOVERABLE));
        }

        @Test
        @DisplayName("hoverable(false) 不挂 GROUP_BOX_HOVERABLE")
        void hoverable_false() {
            VBox box = GroupBoxAnt.create()
                    .hoverable(false).build();
            assertFalse(box.getStyleClass().contains(JfxStyles.GROUP_BOX_HOVERABLE));
        }
    }

    // ============================================================
    // actions 变长
    // ============================================================

    @Nested
    @DisplayName("底部操作区（actions 变长）")
    class Actions {

        @Test
        @DisplayName("actions(0) 不渲染 footer")
        void actions_empty_noFooter() {
            VBox box = GroupBoxAnt.create().title("标题").build();
            // 1 个 header + 1 个 body
            assertEquals(2, box.getChildren().size());
        }

        @Test
        @DisplayName("actions 单个 → 渲染 footer（1 项）")
        void actions_single() {
            Region btn = new Region();
            VBox box = GroupBoxAnt.create()
                    .title("标题")
                    .actions(btn)
                    .build();
            // header + body + footer = 3
            assertEquals(3, box.getChildren().size());
            Node footer = box.getChildren().get(2);
            assertTrue(footer instanceof HBox);
            assertTrue(footer.getStyleClass().contains(JfxStyles.GROUP_BOX_ACTIONS));
            assertEquals(1, ((HBox) footer).getChildren().size());
        }

        @Test
        @DisplayName("actions 变长多个 → footer 全部装载")
        void actions_varargs() {
            Region a = new Region();
            Region b = new Region();
            Region c = new Region();
            VBox box = GroupBoxAnt.create()
                    .actions(a, b, c)
                    .build();
            HBox footer = (HBox) box.getChildren().get(1);
            assertEquals(3, footer.getChildren().size());
        }

        @Test
        @DisplayName("actions(null) 静默忽略")
        void actions_nullIgnored() {
            VBox box = GroupBoxAnt.create()
                    .actions((Node[]) null)
                    .build();
            // 不渲染 footer
            assertEquals(1, box.getChildren().size());
        }
    }

    // ============================================================
    // tabs
    // ============================================================

    @Nested
    @DisplayName("标签页（tab / activeTabKey / defaultActiveTabKey）")
    class Tabs {

        @Test
        @DisplayName("tab() 单个 → 渲染 tab bar")
        void tab_single() {
            Region content = new Region();
            VBox box = GroupBoxAnt.create()
                    .tab("a", "标签 A", content)
                    .build();
            // header + body
            VBox header = (VBox) box.getChildren().get(0);
            // 第一行 titleRow 为空（无 title/extra），第二行是 tabBar
            HBox tabBar = (HBox) header.getChildren().get(0);
            assertTrue(tabBar.getStyleClass().contains(JfxStyles.GROUP_BOX_TAB_BAR));
        }

        @Test
        @DisplayName("多个 tab 顺序排列，激活项挂 ACTIVE class")
        void tabs_multipleWithDefaultActive() {
            Region a = new Region();
            Region b = new Region();
            VBox box = GroupBoxAnt.create()
                    .tab("a", "A", a)
                    .tab("b", "B", b)
                    .defaultActiveTabKey("b")
                    .build();
            VBox header = (VBox) box.getChildren().get(0);
            HBox tabBar = (HBox) header.getChildren().get(0);
            HBox tabButtons = (HBox) tabBar.getChildren().get(0);
            assertEquals(2, tabButtons.getChildren().size());
            Label btnA = (Label) tabButtons.getChildren().get(0);
            Label btnB = (Label) tabButtons.getChildren().get(1);
            assertFalse(btnA.getStyleClass().contains(JfxStyles.GROUP_BOX_TAB_ITEM_ACTIVE));
            assertTrue(btnB.getStyleClass().contains(JfxStyles.GROUP_BOX_TAB_ITEM_ACTIVE));
        }

        @Test
        @DisplayName("activeTabKey 受控模式覆盖 defaultActiveTabKey")
        void tabs_activeKeyOverDefault() {
            Region a = new Region();
            Region b = new Region();
            VBox box = GroupBoxAnt.create()
                    .tab("a", "A", a)
                    .tab("b", "B", b)
                    .defaultActiveTabKey("a")
                    .activeTabKey("b")        // 受控
                    .build();
            VBox header = (VBox) box.getChildren().get(0);
            HBox tabBar = (HBox) header.getChildren().get(0);
            HBox tabButtons = (HBox) tabBar.getChildren().get(0);
            Label btnB = (Label) tabButtons.getChildren().get(1);
            assertTrue(btnB.getStyleClass().contains(JfxStyles.GROUP_BOX_TAB_ITEM_ACTIVE));
        }

        @Test
        @DisplayName("tab 存在时 body 是 StackPane（用于切换内容）")
        void tabBodyIsStackPane() {
            Region a = new Region();
            VBox box = GroupBoxAnt.create()
                    .tab("a", "A", a)
                    .build();
            // 第 2 个子节点是 body
            assertTrue(box.getChildren().get(1) instanceof StackPane);
        }
    }

    // ============================================================
    // header 渲染开关
    // ============================================================

    @Nested
    @DisplayName("header 渲染开关（headerBackground / headerBorder）")
    class HeaderSwitches {

        @Test
        @DisplayName("headerBackground(true) 默认挂 GROUP_BOX_HEADER_BG")
        void headerBackground_default() {
            VBox box = GroupBoxAnt.create().title("t").build();
            VBox header = (VBox) box.getChildren().get(0);
            assertTrue(header.getStyleClass().contains(JfxStyles.GROUP_BOX_HEADER_BG));
        }

        @Test
        @DisplayName("headerBackground(false) 不挂 BG class")
        void headerBackground_false() {
            VBox box = GroupBoxAnt.create()
                    .title("t")
                    .headerBackground(false)
                    .build();
            VBox header = (VBox) box.getChildren().get(0);
            assertFalse(header.getStyleClass().contains(JfxStyles.GROUP_BOX_HEADER_BG));
        }

        @Test
        @DisplayName("headerBorder(false) 不挂 BORDER class")
        void headerBorder_false() {
            VBox box = GroupBoxAnt.create()
                    .title("t")
                    .headerBorder(false)
                    .build();
            VBox header = (VBox) box.getChildren().get(0);
            assertFalse(header.getStyleClass().contains(JfxStyles.GROUP_BOX_HEADER_BORDER));
        }
    }

    // ============================================================
    // tabBarExtraContent
    // ============================================================

    @Test
    @DisplayName("tabBarExtraContent 挂到 tabBar 右侧")
    void tabBarExtraContent_attached() {
        Label extra = new Label("更多");
        VBox box = GroupBoxAnt.create()
                .tab("a", "A", new Region())
                .tabBarExtraContent(extra)
                .build();
        VBox header = (VBox) box.getChildren().get(0);
        HBox tabBar = (HBox) header.getChildren().get(0);
        // 顺序：tabButtons + spacer + extra → 最后一项是 extra
        assertEquals(extra, tabBar.getChildren().get(tabBar.getChildren().size() - 1));
    }

    // ============================================================
    // 链式串联
    // ============================================================

    @Test
    @DisplayName("全链式串联不抛异常")
    void fullChain_noException() {
        VBox box = GroupBoxAnt.create()
                .title("账户信息")
                .extra(new Label("+"))
                .content(new Region())
                .bordered(true)
                .size(Size.SMALL)
                .type(GroupBoxAnt.Type.DEFAULT)
                .actions(new Region(), new Region())
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .defaultActiveTabKey("a")
                .tabBarExtraContent(new Label("···"))
                .headerBackground(true)
                .headerBorder(true)
                .hoverable(true)
                .build();
        assertNotNull(box);
        assertTrue(box.getStyleClass().contains(JfxStyles.GROUP_BOX));
        assertTrue(box.getStyleClass().contains(JfxStyles.GROUP_BOX_BORDERED));
        assertTrue(box.getStyleClass().contains(JfxStyles.GROUP_BOX_SMALL));
    }
}
