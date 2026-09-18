package org.openkawu.jfxium.component.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SplitButtonAnt 单元测试 —— 覆盖 Builder 创建、size/shape、arrowStyle、icon、
 * onClick 事件、item 变长/带图标/禁用、separator 分隔、add 原生 MenuItem。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create + build 返回 SplitMenuButton + 默认 jfx-split-menu-button class</li>
 *   <li>size / shape：SMALL/LARGE/ROUNDED/SQUARE 互斥</li>
 *   <li>arrowStyle：CHEVRON 默认 / TRIANGLE 追加 JfxStyles.JFX_ARROW_TRIANGLE class</li>
 *   <li>disabled / icon / contentDisplay</li>
 *   <li>onClick：主按钮点击事件</li>
 *   <li>items：item(item,item,item,itemDisabled) / separator / add(MenuItem)</li>
 *   <li>链式串联</li>
 * </ul>
 */
@DisplayName("SplitButtonAnt")
class SplitButtonAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create(text).build() 返回 SplitMenuButton 且挂 jfx-split-menu-button")
    void build_returnsSplitMenuButton() {
        SplitMenuButton btn = SplitButtonAnt.create("保存").build();
        assertNotNull(btn);
        assertEquals("保存", btn.getText());
        assertTrue(btn.getStyleClass().contains(JfxStyles.JFX_SPLIT_MENU_BUTTON));
    }

    @Test
    @DisplayName("create() 无参默认空文本")
    void create_emptyText() {
        SplitMenuButton btn = SplitButtonAnt.create().build();
        assertEquals("", btn.getText());
    }

    @Test
    @DisplayName("默认 items 列表为空")
    void defaultItemsEmpty() {
        SplitMenuButton btn = SplitButtonAnt.create("主操作").build();
        assertEquals(0, btn.getItems().size());
    }

    // ============================================================
    // size
    // ============================================================

    @Nested
    @DisplayName("size（SMALL / LARGE / DEFAULT）")
    class SizeTests {

        @Test
        @DisplayName("size(SMALL) 挂 SIZE_SMALL")
        void size_small() {
            SplitMenuButton btn = SplitButtonAnt.create().size(Size.SMALL).build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_SMALL));
        }

        @Test
        @DisplayName("size(LARGE) 挂 SIZE_LARGE")
        void size_large() {
            SplitMenuButton btn = SplitButtonAnt.create().size(Size.LARGE).build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_LARGE));
        }

        @Test
        @DisplayName("size(DEFAULT) 不挂 SMALL/LARGE")
        void size_default() {
            SplitMenuButton btn = SplitButtonAnt.create().build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.SIZE_SMALL));
            assertFalse(btn.getStyleClass().contains(JfxStyles.SIZE_LARGE));
        }
    }

    // ============================================================
    // shape（rounded / square 互斥）
    // ============================================================

    @Nested
    @DisplayName("shape（rounded / square 互斥）")
    class Shape {

        @Test
        @DisplayName("rounded() 挂 SHAPE_ROUNDED")
        void rounded() {
            SplitMenuButton btn = SplitButtonAnt.create().rounded().build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED));
        }

        @Test
        @DisplayName("square() 挂 SHAPE_SQUARE")
        void square() {
            SplitMenuButton btn = SplitButtonAnt.create().square().build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_SQUARE));
        }

        @Test
        @DisplayName("rounded() → square() 互斥切换：仅保留 SQUARE")
        void roundedThenSquare_mutuallyExclusive() {
            SplitMenuButton btn = SplitButtonAnt.create()
                    .rounded().square().build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED));
            assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_SQUARE));
        }
    }

    // ============================================================
    // arrowStyle
    // ============================================================

    @Nested
    @DisplayName("arrowStyle（CHEVRON / TRIANGLE）")
    class Arrow {

        @Test
        @DisplayName("arrowStyle(CHEVRON) 默认不挂 JfxStyles.JFX_ARROW_TRIANGLE")
        void chevron_default() {
            SplitMenuButton btn = SplitButtonAnt.create().build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.JFX_ARROW_TRIANGLE));
        }

        @Test
        @DisplayName("arrowStyle(TRIANGLE) 挂 JfxStyles.JFX_ARROW_TRIANGLE class")
        void triangle() {
            SplitMenuButton btn = SplitButtonAnt.create()
                    .arrowStyle(SplitButtonAnt.ArrowStyle.TRIANGLE).build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.JFX_ARROW_TRIANGLE));
        }
    }

    // ============================================================
    // 状态
    // ============================================================

    @Test
    @DisplayName("disabled(true) 设置 isDisable")
    void disabled_true() {
        SplitMenuButton btn = SplitButtonAnt.create().disabled(true).build();
        assertTrue(btn.isDisable());
    }

    @Test
    @DisplayName("disabled(false) 默认不禁用")
    void disabled_defaultFalse() {
        SplitMenuButton btn = SplitButtonAnt.create().build();
        assertFalse(btn.isDisable());
    }

    // ============================================================
    // icon / contentDisplay
    // ============================================================

    @Test
    @DisplayName("icon 设置 graphic")
    void icon_setsGraphic() {
        Rectangle icon = new Rectangle(16, 16);
        SplitMenuButton btn = SplitButtonAnt.create().icon(icon).build();
        assertEquals(icon, btn.getGraphic());
    }

    @Test
    @DisplayName("contentDisplay 设置图标位置（仅 icon != null 时生效）")
    void contentDisplay_sets() {
        Rectangle icon = new Rectangle(12, 12);
        SplitMenuButton btn = SplitButtonAnt.create()
                .icon(icon)
                .contentDisplay(ContentDisplay.RIGHT)
                .build();
        assertEquals(ContentDisplay.RIGHT, btn.getContentDisplay());
    }

    @Test
    @DisplayName("contentDisplay 默认 LEFT")
    void contentDisplay_defaultLeft() {
        SplitMenuButton btn = SplitButtonAnt.create().build();
        assertEquals(ContentDisplay.LEFT, btn.getContentDisplay());
    }

    // ============================================================
    // onClick 事件
    // ============================================================

    @Test
    @DisplayName("onClick 注册 ActionEvent handler（主按钮点击）")
    void onClick_registersHandler() {
        boolean[] fired = {false};
        SplitMenuButton btn = SplitButtonAnt.create()
                .onClick(e -> fired[0] = true)
                .build();
        assertNotNull(btn.getOnAction());
        btn.getOnAction().handle(new ActionEvent());
        assertTrue(fired[0]);
    }

    @Test
    @DisplayName("onClick(null) 不抛异常，且不再有 handler")
    void onClick_null() {
        SplitMenuButton btn = SplitButtonAnt.create()
                .onClick(null)
                .build();
        assertNull(btn.getOnAction());
    }

    // ============================================================
    // items
    // ============================================================

    @Nested
    @DisplayName("下拉菜单项（item / itemDisabled / separator / add）")
    class Items {

        @Test
        @DisplayName("item(label, handler) 单个追加")
        void item_single() {
            SplitMenuButton btn = SplitButtonAnt.create()
                    .item("保存并新建", e -> {})
                    .build();
            assertEquals(1, btn.getItems().size());
            assertEquals("保存并新建", btn.getItems().get(0).getText());
        }

        @Test
        @DisplayName("item(label, icon, handler) 带图标追加")
        void item_withIcon() {
            Rectangle icon = new Rectangle(12, 12);
            SplitMenuButton btn = SplitButtonAnt.create()
                    .item("选项", icon, e -> {})
                    .build();
            MenuItem mi = btn.getItems().get(0);
            assertEquals("选项", mi.getText());
            assertEquals(icon, mi.getGraphic());
        }

        @Test
        @DisplayName("item(label, null, handler) icon 为 null 不抛异常")
        void item_nullIcon() {
            SplitMenuButton btn = SplitButtonAnt.create()
                    .item("选项", null, e -> {})
                    .build();
            assertEquals(1, btn.getItems().size());
        }

        @Test
        @DisplayName("item(label, null handler) onAction 为 null 不挂 handler")
        void item_nullHandler() {
            SplitMenuButton btn = SplitButtonAnt.create()
                    .item("选项", null)
                    .build();
            assertNull(btn.getItems().get(0).getOnAction());
        }

        @Test
        @DisplayName("item(label, handler) 多次追加保持顺序")
        void item_multiple() {
            SplitMenuButton btn = SplitButtonAnt.create()
                    .item("a", e -> {})
                    .item("b", e -> {})
                    .item("c", e -> {})
                    .build();
            assertEquals(3, btn.getItems().size());
            assertEquals("a", btn.getItems().get(0).getText());
            assertEquals("c", btn.getItems().get(2).getText());
        }

        @Test
        @DisplayName("itemDisabled(label) 追加禁用菜单项")
        void itemDisabled() {
            SplitMenuButton btn = SplitButtonAnt.create()
                    .itemDisabled("不可用")
                    .build();
            MenuItem mi = btn.getItems().get(0);
            assertEquals("不可用", mi.getText());
            assertTrue(mi.isDisable());
        }

        @Test
        @DisplayName("separator() 追加 SeparatorMenuItem")
        void separator() {
            SplitMenuButton btn = SplitButtonAnt.create()
                    .item("a", e -> {})
                    .separator()
                    .item("b", e -> {})
                    .build();
            assertEquals(3, btn.getItems().size());
            assertTrue(btn.getItems().get(1) instanceof SeparatorMenuItem);
        }

        @Test
        @DisplayName("add(MenuItem) 直接追加原生项")
        void add_menuItem() {
            MenuItem custom = new MenuItem("自定义");
            SplitMenuButton btn = SplitButtonAnt.create()
                    .add(custom)
                    .build();
            assertEquals(1, btn.getItems().size());
            assertEquals("自定义", btn.getItems().get(0).getText());
        }

        @Test
        @DisplayName("add(null) 静默忽略")
        void add_nullIgnored() {
            SplitMenuButton btn = SplitButtonAnt.create()
                    .add((MenuItem) null)
                    .build();
            assertEquals(0, btn.getItems().size());
        }
    }

    // ============================================================
    // 链式串联
    // ============================================================

    @Test
    @DisplayName("全链式串联不抛异常")
    void fullChain_noException() {
        Rectangle icon = new Rectangle(14, 14);
        EventHandler<ActionEvent> handler = e -> {};
        SplitMenuButton btn = SplitButtonAnt.create("保存")
                .size(Size.LARGE)
                .arrowStyle(SplitButtonAnt.ArrowStyle.TRIANGLE)
                .disabled(false)
                .rounded()
                .icon(icon)
                .contentDisplay(ContentDisplay.LEFT)
                .onClick(handler)
                .item("保存并新建", e -> {})
                .item("保存并退出", new Rectangle(8, 8), e -> {})
                .itemDisabled("不可用")
                .separator()
                .add(new MenuItem("自定义"))
                .build();
        assertNotNull(btn);
        assertEquals("保存", btn.getText());
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_LARGE));
        assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED));
        assertTrue(btn.getStyleClass().contains(JfxStyles.JFX_ARROW_TRIANGLE));
        assertEquals(5, btn.getItems().size());
    }
}
