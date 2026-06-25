package org.openkawu.jfxium.component.control;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MenuButtonAnt 单元测试 —— 覆盖 Builder 创建、text/size/shape、arrowStyle（含 NONE）、
 * icon/contentDisplay、disabled、菜单项 item / item(item,icon,handler) / itemDisabled / separator / add。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create + build 返回 MenuButton 且挂 jfx-menu-button class</li>
 *   <li>text：链式设文本 + null 安全</li>
 *   <li>size：SMALL/LARGE/DEFAULT</li>
 *   <li>shape：rounded/square 互斥</li>
 *   <li>arrowStyle：CHEVRON / TRIANGLE / NONE（含 noArrow 语法糖）</li>
 *   <li>disabled / icon / contentDisplay / focusTraversable</li>
 *   <li>items：item / itemDisabled / separator / add</li>
 *   <li>链式串联 + 继承式核心契约（build returnsSelf / 多态）</li>
 * </ul>
 */
@DisplayName("MenuButtonAnt")
class MenuButtonAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create(text).build() 返回 MenuButton 且挂 jfx-menu-button")
    void build_returnsMenuButton() {
        MenuButton btn = MenuButtonAnt.create("批量操作").build();
        assertNotNull(btn);
        assertEquals("批量操作", btn.getText());
        assertTrue(btn.getStyleClass().contains(JfxStyles.JFX_MENU_BUTTON));
    }

    @Test
    @DisplayName("create() 无参默认空文本")
    void create_emptyText() {
        MenuButton btn = MenuButtonAnt.create().build();
        assertEquals("", btn.getText());
    }

    @Test
    @DisplayName("create(null) 兜底空字符串，不抛 NPE")
    void create_nullText_safeText() {
        MenuButton btn = MenuButtonAnt.create(null).build();
        assertEquals("", btn.getText());
    }

    @Test
    @DisplayName("默认 items 列表为空")
    void defaultItemsEmpty() {
        MenuButton btn = MenuButtonAnt.create("菜单").build();
        assertEquals(0, btn.getItems().size());
    }

    // ============================================================
    // text 流式 API
    // ============================================================

    @Nested
    @DisplayName("text 流式 API")
    class TextApi {

        @Test
        @DisplayName("text(String) 链式设文本（覆盖初始值）")
        void text_chained() {
            MenuButton btn = MenuButtonAnt.create("初始")
                    .text("最终")
                    .build();
            assertEquals("最终", btn.getText());
        }

        @Test
        @DisplayName("text(null) 兜底空字符串，不抛 NPE")
        void text_nullSafe() {
            MenuButton btn = MenuButtonAnt.create("初始")
                    .text(null)
                    .build();
            assertEquals("", btn.getText());
        }
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
            MenuButton btn = MenuButtonAnt.create().size(Size.SMALL).build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_SMALL));
        }

        @Test
        @DisplayName("size(LARGE) 挂 SIZE_LARGE")
        void size_large() {
            MenuButton btn = MenuButtonAnt.create().size(Size.LARGE).build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_LARGE));
        }

        @Test
        @DisplayName("size(DEFAULT) 不挂 SMALL/LARGE")
        void size_default() {
            MenuButton btn = MenuButtonAnt.create().build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.SIZE_SMALL));
            assertFalse(btn.getStyleClass().contains(JfxStyles.SIZE_LARGE));
        }

        @Test
        @DisplayName("size(null) 等价 DEFAULT，不抛 NPE")
        void size_null_safe() {
            assertDoesNotThrow(() -> MenuButtonAnt.create().size(null).build());
        }

        @Test
        @DisplayName("size 互斥：SMALL → LARGE 仅保留 LARGE")
        void size_mutuallyExclusive() {
            MenuButton btn = MenuButtonAnt.create()
                    .size(Size.SMALL)
                    .size(Size.LARGE)
                    .build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.SIZE_SMALL));
            assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_LARGE));
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
            MenuButton btn = MenuButtonAnt.create().rounded().build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED));
        }

        @Test
        @DisplayName("square() 挂 SHAPE_SQUARE")
        void square() {
            MenuButton btn = MenuButtonAnt.create().square().build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_SQUARE));
        }

        @Test
        @DisplayName("rounded() → square() 互斥切换：仅保留 SQUARE")
        void roundedThenSquare_mutuallyExclusive() {
            MenuButton btn = MenuButtonAnt.create()
                    .rounded().square().build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED));
            assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_SQUARE));
        }

        @Test
        @DisplayName("square() → rounded() 互斥切换：仅保留 ROUNDED")
        void squareThenRounded_mutuallyExclusive() {
            MenuButton btn = MenuButtonAnt.create()
                    .square().rounded().build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.SHAPE_SQUARE));
            assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED));
        }
    }

    // ============================================================
    // arrowStyle
    // ============================================================

    @Nested
    @DisplayName("arrowStyle（CHEVRON / TRIANGLE / NONE，含 noArrow 语法糖）")
    class Arrow {

        @Test
        @DisplayName("arrowStyle(CHEVRON) 默认不挂额外箭头 class")
        void chevron_default() {
            MenuButton btn = MenuButtonAnt.create().build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.JFX_ARROW_TRIANGLE));
            assertFalse(btn.getStyleClass().contains(JfxStyles.JFX_NO_ARROW));
        }

        @Test
        @DisplayName("arrowStyle(TRIANGLE) 挂 JFX_ARROW_TRIANGLE")
        void triangle() {
            MenuButton btn = MenuButtonAnt.create()
                    .arrowStyle(MenuButtonAnt.ArrowStyle.TRIANGLE).build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.JFX_ARROW_TRIANGLE));
            assertFalse(btn.getStyleClass().contains(JfxStyles.JFX_NO_ARROW));
        }

        @Test
        @DisplayName("arrowStyle(NONE) 挂 JFX_NO_ARROW")
        void none() {
            MenuButton btn = MenuButtonAnt.create()
                    .arrowStyle(MenuButtonAnt.ArrowStyle.NONE).build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.JFX_ARROW_TRIANGLE));
            assertTrue(btn.getStyleClass().contains(JfxStyles.JFX_NO_ARROW));
        }

        @Test
        @DisplayName("noArrow() 等价 arrowStyle(NONE)")
        void noArrow_sugar() {
            MenuButton btn = MenuButtonAnt.create().noArrow().build();
            assertTrue(btn.getStyleClass().contains(JfxStyles.JFX_NO_ARROW));
        }

        @Test
        @DisplayName("arrowStyle 三态互斥：TRIANGLE → NONE 仅保留 JFX_NO_ARROW")
        void arrowStyle_mutuallyExclusive() {
            MenuButton btn = MenuButtonAnt.create()
                    .arrowStyle(MenuButtonAnt.ArrowStyle.TRIANGLE)
                    .arrowStyle(MenuButtonAnt.ArrowStyle.NONE)
                    .build();
            assertFalse(btn.getStyleClass().contains(JfxStyles.JFX_ARROW_TRIANGLE));
            assertTrue(btn.getStyleClass().contains(JfxStyles.JFX_NO_ARROW));
        }
    }

    // ============================================================
    // 状态
    // ============================================================

    @Test
    @DisplayName("disabled(true) 设置 isDisable")
    void disabled_true() {
        MenuButton btn = MenuButtonAnt.create().disabled(true).build();
        assertTrue(btn.isDisable());
    }

    @Test
    @DisplayName("disabled(false) 默认不禁用")
    void disabled_defaultFalse() {
        MenuButton btn = MenuButtonAnt.create().build();
        assertFalse(btn.isDisable());
    }

    // ============================================================
    // icon / contentDisplay
    // ============================================================

    @Test
    @DisplayName("icon 设置 graphic")
    void icon_setsGraphic() {
        Rectangle icon = new Rectangle(16, 16);
        MenuButton btn = MenuButtonAnt.create().icon(icon).build();
        assertEquals(icon, btn.getGraphic());
    }

    @Test
    @DisplayName("icon(null) 不抛 NPE，不修改 graphic")
    void icon_nullSafe() {
        MenuButton btn = MenuButtonAnt.create().icon(null).build();
        assertNull(btn.getGraphic());
    }

    @Test
    @DisplayName("contentDisplay 设置图标位置（仅 icon != null 时生效）")
    void contentDisplay_sets() {
        Rectangle icon = new Rectangle(12, 12);
        MenuButton btn = MenuButtonAnt.create()
                .icon(icon)
                .contentDisplay(ContentDisplay.RIGHT)
                .build();
        assertEquals(ContentDisplay.RIGHT, btn.getContentDisplay());
    }

    @Test
    @DisplayName("contentDisplay(null) 不抛 NPE，保持原值")
    void contentDisplay_nullSafe() {
        MenuButton btn = MenuButtonAnt.create().build();
        assertEquals(ContentDisplay.LEFT, btn.getContentDisplay());
    }

    // ============================================================
    // focusTraversable
    // ============================================================

    @Test
    @DisplayName("focusTraversable() 设置可聚焦（键盘 Enter/Space 触发下拉）")
    void focusTraversable_sets() {
        MenuButton btn = MenuButtonAnt.create("菜单").focusTraversable().build();
        assertTrue(btn.isFocusTraversable());
    }

    // ============================================================
    // items
    // ============================================================

    @Nested
    @DisplayName("下拉菜单项（item / item(item,icon,handler) / itemDisabled / separator / add）")
    class Items {

        @Test
        @DisplayName("item(label, handler) 单个追加")
        void item_single() {
            MenuButton btn = MenuButtonAnt.create()
                    .item("导出", e -> {})
                    .build();
            assertEquals(1, btn.getItems().size());
            assertEquals("导出", btn.getItems().get(0).getText());
        }

        @Test
        @DisplayName("item(label, icon, handler) 带图标追加")
        void item_withIcon() {
            Rectangle icon = new Rectangle(12, 12);
            MenuButton btn = MenuButtonAnt.create()
                    .item("导入", icon, e -> {})
                    .build();
            MenuItem mi = btn.getItems().get(0);
            assertEquals("导入", mi.getText());
            assertEquals(icon, mi.getGraphic());
        }

        @Test
        @DisplayName("item(label, null, handler) icon 为 null 不抛异常")
        void item_nullIcon() {
            MenuButton btn = MenuButtonAnt.create()
                    .item("选项", null, e -> {})
                    .build();
            assertEquals(1, btn.getItems().size());
            assertNull(btn.getItems().get(0).getGraphic());
        }

        @Test
        @DisplayName("item(label, null handler) onAction 为 null 不挂 handler")
        void item_nullHandler() {
            MenuButton btn = MenuButtonAnt.create()
                    .item("选项", null)
                    .build();
            assertNull(btn.getItems().get(0).getOnAction());
        }

        @Test
        @DisplayName("item(label, handler) 多次追加保持顺序")
        void item_multiple() {
            MenuButton btn = MenuButtonAnt.create()
                    .item("a", e -> {})
                    .item("b", e -> {})
                    .item("c", e -> {})
                    .build();
            assertEquals(3, btn.getItems().size());
            assertEquals("a", btn.getItems().get(0).getText());
            assertEquals("b", btn.getItems().get(1).getText());
            assertEquals("c", btn.getItems().get(2).getText());
        }

        @Test
        @DisplayName("itemDisabled(label) 追加禁用菜单项")
        void itemDisabled() {
            MenuButton btn = MenuButtonAnt.create()
                    .itemDisabled("不可用")
                    .build();
            MenuItem mi = btn.getItems().get(0);
            assertEquals("不可用", mi.getText());
            assertTrue(mi.isDisable());
        }

        @Test
        @DisplayName("separator() 追加 SeparatorMenuItem")
        void separator() {
            MenuButton btn = MenuButtonAnt.create()
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
            MenuButton btn = MenuButtonAnt.create()
                    .add(custom)
                    .build();
            assertEquals(1, btn.getItems().size());
            assertEquals("自定义", btn.getItems().get(0).getText());
        }

        @Test
        @DisplayName("add(null) 静默忽略")
        void add_nullIgnored() {
            MenuButton btn = MenuButtonAnt.create()
                    .add((MenuItem) null)
                    .build();
            assertEquals(0, btn.getItems().size());
        }
    }

    // ============================================================
    // 链式串联 + 继承式核心契约
    // ============================================================

    @Test
    @DisplayName("全链式串联不抛异常，所有配置生效")
    void fullChain_noException() {
        Rectangle icon = new Rectangle(14, 14);
        MenuButton btn = MenuButtonAnt.create("批量操作")
                .size(Size.LARGE)
                .rounded()
                .arrowStyle(MenuButtonAnt.ArrowStyle.TRIANGLE)
                .icon(icon)
                .contentDisplay(ContentDisplay.LEFT)
                .focusTraversable()
                .disabled(false)
                .item("导出", e -> {})
                .item("删除", new Rectangle(8, 8), e -> {})
                .itemDisabled("不可用")
                .separator()
                .add(new MenuItem("自定义"))
                .build();

        assertNotNull(btn);
        assertEquals("批量操作", btn.getText());
        assertTrue(btn.getStyleClass().contains(JfxStyles.SIZE_LARGE));
        assertTrue(btn.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED));
        assertTrue(btn.getStyleClass().contains(JfxStyles.JFX_ARROW_TRIANGLE));
        assertEquals(icon, btn.getGraphic());
        assertTrue(btn.isFocusTraversable());
        assertEquals(5, btn.getItems().size());
    }

    @Test
    @DisplayName("build() 返回自身（继承式核心契约）")
    void build_returnsSelf() {
        MenuButtonAnt btn = MenuButtonAnt.create();
        assertSame(btn, btn.build());
    }

    @Test
    @DisplayName("继承式：父类 MenuButton 引用可接收（多态兼容）")
    void parentReference_polymorphism() {
        MenuButton btn = MenuButtonAnt.create("通过父类接收").build();
        assertInstanceOf(MenuButtonAnt.class, btn);
        assertEquals("通过父类接收", btn.getText());
    }
}
