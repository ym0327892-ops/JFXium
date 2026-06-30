package org.openkawu.jfxium.component.control;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ToolBarAnt 单元测试 —— 覆盖工厂创建、orientation、button(icon,tooltip,action) /
 * button(text,icon,tooltip,action)、item/divider/spacer、运行时 addButton/addItem/addDivider/addSpacer/clear。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create + build 返回 ToolBar 且挂 jfx-tool-bar</li>
 *   <li>orientation：create(Orientation) + orientation 流式 + 默认 HORIZONTAL</li>
 *   <li>button / item / divider / spacer（构建时）</li>
 *   <li>运行时：addButton / addItem / addDivider / addSpacer / clear</li>
 *   <li>disabled</li>
 *   <li>链式串联 + 继承式核心契约</li>
 * </ul>
 *
 * <p><b>设计要点</b>：ToolBarAnt 提供运行时 + 构建时双 API —— {@code button/divider/spacer} 返回
 * this 用于链式；{@code addButton/addDivider/addSpacer} 无返回值用于运行时追加；{@code clear}
 * 清空所有项。</p>
 */
@DisplayName("ToolBarAnt")
class ToolBarAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create().build() 返回 ToolBar 且挂 jfx-tool-bar")
    void build_returnsToolBar() {
        ToolBarAnt bar = ToolBarAnt.create().build();
        assertNotNull(bar);
        assertTrue(bar.getStyleClass().contains(JfxStyles.TOOL_BAR));
    }

    @Test
    @DisplayName("create() 默认 orientation = HORIZONTAL")
    void orientation_defaultHorizontal() {
        ToolBarAnt bar = ToolBarAnt.create().build();
        assertEquals(Orientation.HORIZONTAL, bar.getOrientation());
    }

    @Test
    @DisplayName("create(Orientation.VERTICAL) 设置垂直工具栏")
    void create_verticalOrientation() {
        ToolBarAnt bar = ToolBarAnt.create(Orientation.VERTICAL).build();
        assertEquals(Orientation.VERTICAL, bar.getOrientation());
    }

    @Test
    @DisplayName("create(Orientation.HORIZONTAL) 显式水平")
    void create_horizontalOrientation() {
        ToolBarAnt bar = ToolBarAnt.create(Orientation.HORIZONTAL).build();
        assertEquals(Orientation.HORIZONTAL, bar.getOrientation());
    }

    @Test
    @DisplayName("create(null orientation) 兜底 HORIZONTAL，不抛 NPE")
    void create_nullOrientation() {
        ToolBarAnt bar = ToolBarAnt.create(null).build();
        assertEquals(Orientation.HORIZONTAL, bar.getOrientation());
    }

    // ============================================================
    // orientation 流式
    // ============================================================

    @Test
    @DisplayName("orientation(VERTICAL) 流式切换方向")
    void orientation_chained() {
        ToolBarAnt bar = ToolBarAnt.create()
                .orientation(Orientation.VERTICAL)
                .build();
        assertEquals(Orientation.VERTICAL, bar.getOrientation());
    }

    @Test
    @DisplayName("orientation(null) 不修改原值")
    void orientation_nullSafe() {
        ToolBarAnt bar = ToolBarAnt.create()
                .orientation(Orientation.VERTICAL)
                .orientation(null)
                .build();
        assertEquals(Orientation.VERTICAL, bar.getOrientation(),
                "orientation(null) 应保持原值");
    }

    // ============================================================
    // 构建时 button / item / divider / spacer
    // ============================================================

    @Nested
    @DisplayName("构建时 API（button / item / divider / spacer）")
    class BuilderApi {

        @Test
        @DisplayName("button(icon, tooltip, action) 追加图标按钮")
        void button_icon() {
            Rectangle icon = new Rectangle(14, 14);
            boolean[] fired = {false};
            ToolBarAnt bar = ToolBarAnt.create()
                    .button(icon, "新建", () -> fired[0] = true)
                    .build();
            assertEquals(1, bar.getItems().size());
            Node item = bar.getItems().get(0);
            assertInstanceOf(Button.class, item);
            Button btn = (Button) item;
            assertEquals(icon, btn.getGraphic());
            assertNotNull(btn.getTooltip());
            assertEquals("新建", btn.getTooltip().getText());
            assertNotNull(btn.getOnAction());
            btn.getOnAction().handle(new javafx.event.ActionEvent());
            assertTrue(fired[0]);
        }

        @Test
        @DisplayName("button(text, icon, tooltip, action) 追加带文本按钮")
        void button_textIcon() {
            Rectangle icon = new Rectangle(12, 12);
            ToolBarAnt bar = ToolBarAnt.create()
                    .button("保存", icon, "保存文件", () -> {})
                    .build();
            Button btn = (Button) bar.getItems().get(0);
            assertEquals("保存", btn.getText());
            assertEquals(icon, btn.getGraphic());
        }

        @Test
        @DisplayName("button(icon, null tooltip, null action) 不挂 tooltip 不挂 handler")
        void button_nullSafety() {
            Rectangle icon = new Rectangle(8, 8);
            ToolBarAnt bar = ToolBarAnt.create()
                    .button(icon, null, null)
                    .build();
            Button btn = (Button) bar.getItems().get(0);
            assertNull(btn.getTooltip());
            assertNull(btn.getOnAction());
        }

        @Test
        @DisplayName("button(icon, empty tooltip, null action) 空 tooltip 不挂")
        void button_emptyTooltip() {
            Rectangle icon = new Rectangle(8, 8);
            ToolBarAnt bar = ToolBarAnt.create()
                    .button(icon, "", null)
                    .build();
            Button btn = (Button) bar.getItems().get(0);
            assertNull(btn.getTooltip(), "空字符串 tooltip 应被识别为不挂");
        }

        @Test
        @DisplayName("item(Node) 追加任意节点")
        void item_node() {
            Rectangle node = new Rectangle(10, 10);
            ToolBarAnt bar = ToolBarAnt.create()
                    .item(node)
                    .build();
            assertEquals(1, bar.getItems().size());
            assertEquals(node, bar.getItems().get(0));
        }

        @Test
        @DisplayName("item(null) 静默忽略")
        void item_nullIgnored() {
            ToolBarAnt bar = ToolBarAnt.create()
                    .item(null)
                    .build();
            assertEquals(0, bar.getItems().size());
        }

        @Test
        @DisplayName("divider() HORIZONTAL 时插入 1x24 Region")
        void divider_horizontal() {
            ToolBarAnt bar = ToolBarAnt.create()
                    .divider()
                    .build();
            Region div = (Region) bar.getItems().get(0);
            assertEquals(1, div.getPrefWidth(), 0.001);
            assertEquals(24, div.getPrefHeight(), 0.001);
            assertTrue(div.getStyleClass().contains(JfxStyles.TOOL_BAR_ITEM));
        }

        @Test
        @DisplayName("divider() VERTICAL 时插入 24x1 Region")
        void divider_vertical() {
            ToolBarAnt bar = ToolBarAnt.create(Orientation.VERTICAL)
                    .divider()
                    .build();
            Region div = (Region) bar.getItems().get(0);
            assertEquals(24, div.getPrefWidth(), 0.001);
            assertEquals(1, div.getPrefHeight(), 0.001);
        }

        @Test
        @DisplayName("spacer() HORIZONTAL 时插入 HBox.setHgrow(ALWAYS) 的 Region")
        void spacer_horizontal() {
            ToolBarAnt bar = ToolBarAnt.create()
                    .spacer()
                    .build();
            Region sp = (Region) bar.getItems().get(0);
            assertEquals(Region.USE_COMPUTED_SIZE, sp.getPrefWidth(), 0.001);
            assertEquals(HBox.getHgrow(sp), javafx.scene.layout.Priority.ALWAYS,
                    "HORIZONTAL spacer 应设置 HBox.setHgrow(ALWAYS)");
        }

        @Test
        @DisplayName("spacer() VERTICAL 时插入 VBox.setVgrow(ALWAYS) 的 Region")
        void spacer_vertical() {
            ToolBarAnt bar = ToolBarAnt.create(Orientation.VERTICAL)
                    .spacer()
                    .build();
            Region sp = (Region) bar.getItems().get(0);
            assertEquals(Region.USE_COMPUTED_SIZE, sp.getPrefHeight(), 0.001);
            assertEquals(VBox.getVgrow(sp), javafx.scene.layout.Priority.ALWAYS,
                    "VERTICAL spacer 应设置 VBox.setVgrow(ALWAYS)");
        }
    }

    // ============================================================
    // 运行时 addXxx / clear
    // ============================================================

    @Nested
    @DisplayName("运行时 API（addButton / addItem / addDivider / addSpacer / clear）")
    class RuntimeApi {

        @Test
        @DisplayName("addButton(icon, tooltip, action) 运行时追加")
        void addButton_icon() {
            Rectangle icon = new Rectangle(14, 14);
            ToolBarAnt bar = ToolBarAnt.create().build();
            assertEquals(0, bar.getItems().size());
            bar.addButton(icon, "新建", () -> {});
            assertEquals(1, bar.getItems().size());
            assertInstanceOf(Button.class, bar.getItems().get(0));
        }

        @Test
        @DisplayName("addButton(text, icon, tooltip, action) 运行时追加带文本按钮")
        void addButton_text() {
            Rectangle icon = new Rectangle(12, 12);
            ToolBarAnt bar = ToolBarAnt.create().build();
            bar.addButton("打开", icon, "打开文件", () -> {});
            Button btn = (Button) bar.getItems().get(0);
            assertEquals("打开", btn.getText());
            assertEquals(icon, btn.getGraphic());
        }

        @Test
        @DisplayName("addItem(Node) 运行时追加任意节点")
        void addItem_node() {
            Rectangle node = new Rectangle(10, 10);
            ToolBarAnt bar = ToolBarAnt.create().build();
            bar.addItem(node);
            assertEquals(1, bar.getItems().size());
            assertEquals(node, bar.getItems().get(0));
        }

        @Test
        @DisplayName("addDivider() 运行时插入分隔线")
        void addDivider_default() {
            ToolBarAnt bar = ToolBarAnt.create().build();
            bar.addDivider();
            assertEquals(1, bar.getItems().size());
            Region div = (Region) bar.getItems().get(0);
            assertEquals(1, div.getPrefWidth(), 0.001);
        }

        @Test
        @DisplayName("addSpacer() 运行时插入弹性填充")
        void addSpacer_default() {
            ToolBarAnt bar = ToolBarAnt.create().build();
            bar.addSpacer();
            assertEquals(1, bar.getItems().size());
        }

        @Test
        @DisplayName("clear() 清空所有项")
        void clear() {
            ToolBarAnt bar = ToolBarAnt.create()
                    .button(new Rectangle(10, 10), "a", null)
                    .button(new Rectangle(10, 10), "b", null)
                    .button(new Rectangle(10, 10), "c", null)
                    .build();
            assertEquals(3, bar.getItems().size());
            bar.clear();
            assertEquals(0, bar.getItems().size());
        }

        @Test
        @DisplayName("clear() 空工具栏不抛异常")
        void clear_empty() {
            ToolBarAnt bar = ToolBarAnt.create().build();
            assertDoesNotThrow(bar::clear);
            assertEquals(0, bar.getItems().size());
        }
    }

    // ============================================================
    // 状态
    // ============================================================

    @Test
    @DisplayName("disabled(true) 设置 isDisable")
    void disabled_true() {
        ToolBarAnt bar = ToolBarAnt.create().disabled(true).build();
        assertTrue(bar.isDisable());
    }

    @Test
    @DisplayName("disabled(false) 默认不禁用")
    void disabled_defaultFalse() {
        ToolBarAnt bar = ToolBarAnt.create().build();
        assertFalse(bar.isDisable());
    }

    // ============================================================
    // 链式串联 + 继承式核心契约
    // ============================================================

    @Test
    @DisplayName("全链式串联：create → orientation → button × N → divider → spacer 全部生效")
    void fullChain_noException() {
        Rectangle icon1 = new Rectangle(14, 14);
        Rectangle icon2 = new Rectangle(14, 14);
        ToolBarAnt bar = ToolBarAnt.create(Orientation.HORIZONTAL)
                .button(icon1, "新建", () -> {})
                .button("打开", icon2, "打开", () -> {})
                .item(new Rectangle(8, 8))
                .divider()
                .spacer()
                .button(new Rectangle(8, 8), "保存", () -> {})
                .disabled(false)
                .build();

        assertEquals(6, bar.getItems().size());
        assertEquals(Orientation.HORIZONTAL, bar.getOrientation());
    }

    @Test
    @DisplayName("构建后 addXxx + clear 组合：先构建 3 项，运行时追加 1 项后清空")
    void build_thenRuntimeAddAndClear() {
        ToolBarAnt bar = ToolBarAnt.create()
                .button(new Rectangle(10, 10), "a", null)
                .button(new Rectangle(10, 10), "b", null)
                .build();
        assertEquals(2, bar.getItems().size());
        bar.addButton(new Rectangle(10, 10), "c", null);
        assertEquals(3, bar.getItems().size());
        bar.clear();
        assertEquals(0, bar.getItems().size());
    }

    @Test
    @DisplayName("build() 返回自身（继承式核心契约）")
    void build_returnsSelf() {
        ToolBarAnt bar = ToolBarAnt.create();
        assertSame(bar, bar.build());
    }

    @Test
    @DisplayName("继承式：父类 ToolBar 引用可接收（多态兼容）")
    void parentReference_polymorphism() {
        ToolBar bar = ToolBarAnt.create().build();
        assertInstanceOf(ToolBarAnt.class, bar);
        assertTrue(bar.getStyleClass().contains(JfxStyles.TOOL_BAR));
    }
}
