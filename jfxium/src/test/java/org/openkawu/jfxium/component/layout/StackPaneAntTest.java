package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StackPaneAnt 单元测试 —— LayoutCommon<StackPaneAnt> 接入后行为回归保护。
 *
 * <p>覆盖：Factory / Constructors / BusinessMethods(StackPane 特有) /
 * StyleApi / Padding / Borders / Sizes / NodeProps / Inheritance / FullChain。
 * 业务继承 StackPaneAnt 后构造里调用的所有 LayoutCommon default method
 * （padding / background / id / borderTop 等）必须生效，链式 API 必须返回子类类型。</p>
 */
@DisplayName("StackPaneAnt 单元测试（接入 LayoutCommon<StackPaneAnt>）")
class StackPaneAntTest extends JfxTestBase {

    // ============================================================
    // Factory
    // ============================================================

    @Nested
    @DisplayName("工厂入口")
    class Factory {

        @Test
        @DisplayName("create() 返回非 null StackPaneAnt 实例")
        void createReturnsInstance() {
            StackPaneAnt ant = StackPaneAnt.create();
            assertNotNull(ant);
            assertTrue(ant instanceof StackPane, "StackPaneAnt 必须是 StackPane 子类");
            assertTrue(ant instanceof LayoutCommon, "StackPaneAnt 必须实现 LayoutCommon");
        }

        @Test
        @DisplayName("create() 默认空 styleClass")
        void createEmptyDefaults() {
            StackPaneAnt ant = StackPaneAnt.create();
            assertEquals(0, ant.getChildren().size());
            // StackPane 不自动加任何 styleClass（与 TilePane/SplitPane 不同）
            assertTrue(ant.getStyleClass().isEmpty(),
                    "create() 默认应无 styleClass，实际: " + ant.getStyleClass());
        }

        @Test
        @DisplayName("create() 默认无子节点")
        void createNoChildren() {
            StackPaneAnt ant = StackPaneAnt.create();
            assertEquals(0, ant.getChildren().size());
        }

    }

    // ============================================================
    // Constructors
    // ============================================================

    @Nested
    @DisplayName("构造函数（公开，便于业务 extends）")
    class Constructors {

        @Test
        @DisplayName("无参构造")
        void noArg() {
            StackPaneAnt ant = new StackPaneAnt();
            assertEquals(0, ant.getChildren().size());
            assertTrue(ant.getStyleClass().isEmpty());
        }
    }

    // ============================================================
    // BusinessMethods（StackPane 特有）
    // ============================================================

    @Nested
    @DisplayName("业务方法（StackPane 特有）")
    class BusinessMethods {

        @Test
        @DisplayName("align 设置后能读取")
        void alignRoundTrip() {
            StackPaneAnt ant = StackPaneAnt.create().align(Pos.TOP_RIGHT);
            assertEquals(Pos.TOP_RIGHT, ant.getAlignment());
        }

        @Test
        @DisplayName("children 批量添加")
        void childrenBatchAdd() {
            Label a = new Label("A");
            Label b = new Label("B");
            StackPaneAnt ant = StackPaneAnt.create().children(a, b);
            assertEquals(2, ant.getChildren().size());
        }

        @Test
        @DisplayName("children 含 null 时只加非 null 节点")
        void childrenNullFilter() {
            Label a = new Label("A");
            Label b = new Label("B");
            StackPaneAnt ant = StackPaneAnt.create().children(a, null, b);
            assertEquals(2, ant.getChildren().size());
            assertTrue(ant.getChildren().contains(a));
            assertTrue(ant.getChildren().contains(b));
        }

        @Test
        @DisplayName("children((Node[]) null) 安全不抛异常")
        void childrenNullArraySafe() {
            StackPaneAnt ant = StackPaneAnt.create();
            assertDoesNotThrow(() -> ant.children((javafx.scene.Node[]) null));
            assertEquals(0, ant.getChildren().size());
        }

        @Test
        @DisplayName("childAlign 覆盖容器级 align")
        void childAlignOverrides() {
            Label child = new Label("child");
            StackPaneAnt ant = StackPaneAnt.create()
                    .align(Pos.CENTER)
                    .childAlign(child, Pos.TOP_LEFT);
            assertEquals(Pos.CENTER, ant.getAlignment());
            assertEquals(Pos.TOP_LEFT, StackPane.getAlignment(child));
        }

        @Test
        @DisplayName("margin 设置后能读取")
        void marginRoundTrip() {
            Label child = new Label("child");
            Insets margin = new Insets(5, 10, 15, 20);
            StackPaneAnt ant = StackPaneAnt.create().margin(child, margin);
            assertEquals(margin, StackPane.getMargin(child));
        }

        @Test
        @DisplayName("业务方法链式调用返回 this")
        void businessChaining() {
            StackPaneAnt ant = StackPaneAnt.create();
            StackPaneAnt ret = ant.align(Pos.CENTER)
                    .children(new Label("x"))
                    .margin(new Label("y"), new Insets(1));
            assertSame(ant, ret);
        }

    }

    // ============================================================
    // StyleApi（继承自 LayoutCommon 的 default methods）
    // ============================================================

    @Nested
    @DisplayName("视觉钩子（继承 LayoutCommon）")
    class StyleApi {

        @Test
        @DisplayName("styleClass 单参数追加且幂等")
        void styleClassIdempotent() {
            StackPaneAnt ant = StackPaneAnt.create();
            StackPaneAnt ret = ant.styleClass("foo");
            assertSame(ant, ret);
            assertTrue(ant.getStyleClass().contains("foo"));
            ant.styleClass("foo");
            assertEquals(1, ant.getStyleClass().stream().filter("foo"::equals).count());
        }

        @Test
        @DisplayName("styleClass(null)/styleClass(\"\") 安全")
        void styleClassNullOrEmptySafe() {
            StackPaneAnt ant = StackPaneAnt.create();
            ant.styleClass((String) null);
            ant.styleClass("");
            assertTrue(ant.getStyleClass().isEmpty(),
                    "null 和空串应被忽略，实际 styleClass: " + ant.getStyleClass());
        }

        @Test
        @DisplayName("styleClass(String...) 批量挂载")
        void styleClassVarargs() {
            StackPaneAnt ant = StackPaneAnt.create().styleClass("a", "b", "c");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b", "c")));
        }

        @Test
        @DisplayName("styleClass(String...) 含 null 安全")
        void styleClassVarargsNullSafe() {
            StackPaneAnt ant = StackPaneAnt.create().styleClass("a", null, "b");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b")));
            assertEquals(2, ant.getStyleClass().size());
        }

        @Test
        @DisplayName("styleClass((String[]) null) 安全")
        void styleClassVarargsArrayNullSafe() {
            StackPaneAnt ant = StackPaneAnt.create().styleClass((String[]) null);
            assertTrue(ant.getStyleClass().isEmpty());
        }

        @Test
        @DisplayName("background(Background) 挂对应 styleClass")
        void backgroundSetsStyleClass() {
            StackPaneAnt ant = StackPaneAnt.create().background(Background.SUBTLE);
            assertTrue(ant.getStyleClass().stream()
                    .anyMatch(Background.SUBTLE.styleClass()::equals));
        }

        @Test
        @DisplayName("background(null) 安全不挂任何 styleClass")
        void backgroundNullSafe() {
            StackPaneAnt ant = StackPaneAnt.create().background(null);
            assertTrue(ant.getStyleClass().isEmpty());
        }

        @Test
        @DisplayName("style(inline) 设置后能读取")
        void styleInlineRoundTrip() {
            StackPaneAnt ant = StackPaneAnt.create().style("-fx-opacity: 0.5");
            assertEquals("-fx-opacity: 0.5", ant.getStyle());
        }

        @Test
        @DisplayName("style(null) 安全不抛异常")
        void styleNullSafe() {
            StackPaneAnt ant = StackPaneAnt.create().style(null);
            assertEquals("", ant.getStyle());
        }

        @Test
        @DisplayName("style 链式返回 this")
        void styleChaining() {
            StackPaneAnt ant = StackPaneAnt.create();
            StackPaneAnt ret = ant.style("-fx-foo: bar");
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("多次 styleClass 不重复挂载（幂等）")
        void styleClassMultipleCallsIdempotent() {
            StackPaneAnt ant = StackPaneAnt.create();
            ant.styleClass("a").styleClass("a").styleClass("a");
            assertEquals(1, ant.getStyleClass().stream().filter("a"::equals).count());
        }
    }

    // ============================================================
    // Padding（继承自 LayoutCommon）
    // ============================================================

    @Nested
    @DisplayName("padding（继承 LayoutCommon）")
    class Padding {

        @Test
        @DisplayName("padding(double) 4 边相同")
        void paddingUniform() {
            StackPaneAnt ant = StackPaneAnt.create().padding(12);
            assertEquals(new Insets(12), ant.getPadding());
        }

        @Test
        @DisplayName("padding(top,right,bottom,left) 各边不同")
        void paddingFourSides() {
            StackPaneAnt ant = StackPaneAnt.create().padding(1, 2, 3, 4);
            assertEquals(new Insets(1, 2, 3, 4), ant.getPadding());
        }

        @Test
        @DisplayName("padding(Insets) 设置对象")
        void paddingInsets() {
            StackPaneAnt ant = StackPaneAnt.create().padding(new Insets(5, 10, 15, 20));
            assertEquals(new Insets(5, 10, 15, 20), ant.getPadding());
        }

        @Test
        @DisplayName("padding((Insets) null) 安全不抛异常")
        void paddingNullSafe() {
            StackPaneAnt ant = StackPaneAnt.create();
            assertDoesNotThrow(() -> ant.padding((Insets) null));
        }

        @Test
        @DisplayName("padding 链式返回 this")
        void paddingChaining() {
            StackPaneAnt ant = StackPaneAnt.create();
            StackPaneAnt ret = ant.padding(8);
            assertSame(ant, ret);
        }
    }

    // ============================================================
    // Borders（继承自 LayoutCommon）
    // ============================================================

    @Nested
    @DisplayName("方向性边框线（继承 LayoutCommon）")
    class Borders {

        @Test
        @DisplayName("borderTop/Bottom/Left/Right 各自挂对应 styleClass")
        void allBorders() {
            StackPaneAnt ant = StackPaneAnt.create()
                    .borderTop().borderBottom().borderLeft().borderRight();
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderTop(true) 挂载，borderTop(false) 不挂")
        void borderTopSwitch() {
            StackPaneAnt ant = StackPaneAnt.create();
            ant.borderTop(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            ant.borderTop(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
        }

        @Test
        @DisplayName("borderBottom(true) 挂载")
        void borderBottomSwitch() {
            StackPaneAnt ant = StackPaneAnt.create();
            ant.borderBottom(false);
            ant.borderBottom(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        }

        @Test
        @DisplayName("borderLeft(true) 挂载")
        void borderLeftSwitch() {
            StackPaneAnt ant = StackPaneAnt.create();
            ant.borderLeft(false);
            ant.borderLeft(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
        }

        @Test
        @DisplayName("borderRight(true) 挂载")
        void borderRightSwitch() {
            StackPaneAnt ant = StackPaneAnt.create();
            ant.borderRight(false);
            ant.borderRight(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderXxx 链式返回 this")
        void borderChaining() {
            StackPaneAnt ant = StackPaneAnt.create();
            StackPaneAnt ret = ant.borderTop();
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderTop(boolean) 链式返回 this")
        void borderSwitchChaining() {
            StackPaneAnt ant = StackPaneAnt.create();
            StackPaneAnt ret = ant.borderTop(true);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderXxx(false) 不挂载对应 styleClass")
        void borderSwitchOffDoesNothing() {
            StackPaneAnt ant = StackPaneAnt.create();
            ant.borderTop(false).borderBottom(false).borderLeft(false).borderRight(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("多次 borderTop() 幂等不重复挂")
        void borderIdempotent() {
            StackPaneAnt ant = StackPaneAnt.create().borderTop().borderTop().borderTop();
            assertEquals(1, ant.getStyleClass().stream()
                    .filter(JfxStyles.BORDER_TOP::equals).count());
        }
    }

    // ============================================================
    // Sizes（继承自 LayoutCommon）
    // ============================================================

    @Nested
    @DisplayName("尺寸（继承 LayoutCommon）")
    class Sizes {

        @Test
        @DisplayName("maxW/H, minW/H, prefW/H 各自 round-trip")
        void singleAxisSizes() {
            StackPaneAnt ant = StackPaneAnt.create()
                    .maxW(100).maxH(200)
                    .minW(50).minH(60)
                    .prefW(300).prefH(400);
            assertEquals(100, ant.getMaxWidth(), 0.0);
            assertEquals(200, ant.getMaxHeight(), 0.0);
            assertEquals(50, ant.getMinWidth(), 0.0);
            assertEquals(60, ant.getMinHeight(), 0.0);
            assertEquals(300, ant.getPrefWidth(), 0.0);
            assertEquals(400, ant.getPrefHeight(), 0.0);
        }

        @Test
        @DisplayName("prefSize/maxSize/minSize 同时设置宽高")
        void combinedSizes() {
            StackPaneAnt ant = StackPaneAnt.create()
                    .prefSize(150, 250)
                    .maxSize(800, 600)
                    .minSize(20, 30);
            assertEquals(150, ant.getPrefWidth(), 0.0);
            assertEquals(250, ant.getPrefHeight(), 0.0);
            assertEquals(800, ant.getMaxWidth(), 0.0);
            assertEquals(600, ant.getMaxHeight(), 0.0);
            assertEquals(20, ant.getMinWidth(), 0.0);
            assertEquals(30, ant.getMinHeight(), 0.0);
        }

        @Test
        @DisplayName("尺寸链式返回 this")
        void sizesChaining() {
            StackPaneAnt ant = StackPaneAnt.create();
            StackPaneAnt ret = ant.maxW(100).prefSize(50, 60);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("尺寸 set 后再 set 能覆盖")
        void sizesOverridable() {
            StackPaneAnt ant = StackPaneAnt.create().maxW(100).maxW(200);
            assertEquals(200, ant.getMaxWidth(), 0.0);
        }

        @Test
        @DisplayName("prefSize(0,0) 合法不抛异常")
        void prefSizeZeroSafe() {
            StackPaneAnt ant = StackPaneAnt.create().prefSize(0, 0);
            assertEquals(0, ant.getPrefWidth(), 0.0);
            assertEquals(0, ant.getPrefHeight(), 0.0);
        }
    }

    // ============================================================
    // NodeProps（继承自 LayoutCommon）
    // ============================================================

    @Nested
    @DisplayName("高频节点属性（继承 LayoutCommon）")
    class NodeProps {

        @Test
        @DisplayName("visible 设置后能读取")
        void visibleRoundTrip() {
            StackPaneAnt ant = StackPaneAnt.create().visible(false);
            assertFalse(ant.isVisible());
        }

        @Test
        @DisplayName("disable 设置后能读取")
        void disableRoundTrip() {
            StackPaneAnt ant = StackPaneAnt.create().disable(true);
            assertTrue(ant.isDisabled());
        }

        @Test
        @DisplayName("managed 设置后能读取")
        void managedRoundTrip() {
            StackPaneAnt ant = StackPaneAnt.create().managed(false);
            assertFalse(ant.isManaged());
        }

        @Test
        @DisplayName("opacity 设置后能读取")
        void opacityRoundTrip() {
            StackPaneAnt ant = StackPaneAnt.create().opacity(0.42);
            assertEquals(0.42, ant.getOpacity(), 0.001);
        }

        @Test
        @DisplayName("cursor 设置后能读取")
        void cursorRoundTrip() {
            StackPaneAnt ant = StackPaneAnt.create().cursor(Cursor.HAND);
            assertEquals(Cursor.HAND, ant.getCursor());
        }

        @Test
        @DisplayName("id 设置后能读取")
        void idRoundTrip() {
            StackPaneAnt ant = StackPaneAnt.create().id("my-stackpane");
            assertEquals("my-stackpane", ant.getId());
        }

        @Test
        @DisplayName("id(null) 安全不抛异常（与原 StackPaneAnt 行为一致）")
        void idNullSafe() {
            StackPaneAnt ant = StackPaneAnt.create();
            assertDoesNotThrow(() -> ant.id(null));
        }

        @Test
        @DisplayName("cursor(null) 安全不抛异常（与原 StackPaneAnt 行为一致）")
        void cursorNullSafe() {
            StackPaneAnt ant = StackPaneAnt.create();
            assertDoesNotThrow(() -> ant.cursor(null));
        }

        @Test
        @DisplayName("NodeProps 链式返回 this")
        void nodePropsChaining() {
            StackPaneAnt ant = StackPaneAnt.create();
            StackPaneAnt ret = ant.visible(true).id("x").opacity(1.0);
            assertSame(ant, ret);
        }
    }

    // ============================================================
    // Inheritance（业务继承 StackPaneAnt 的核心场景）
    // ============================================================

    @Nested
    @DisplayName("业务继承")
    class Inheritance {

        /** 业务继承示例：模拟业务侧 extends StackPaneAnt 写 loading 浮层。 */
        static class LoadingOverlay extends StackPaneAnt {
            LoadingOverlay() {
                align(Pos.CENTER);
                padding(8);
                background(Background.SUBTLE);
                id("loading");
                borderTop();
                children(new Label("content"), new Label("mask"));
            }
        }

        @Test
        @DisplayName("业务继承后构造里 LayoutCommon default method 全部生效")
        void subclassInheritsAllDefaults() {
            LoadingOverlay overlay = new LoadingOverlay();
            assertEquals(Pos.CENTER, overlay.getAlignment());
            assertEquals(new Insets(8), overlay.getPadding());
            assertTrue(overlay.getStyleClass().contains(Background.SUBTLE.styleClass()));
            assertEquals("loading", overlay.getId());
            assertTrue(overlay.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertEquals(2, overlay.getChildren().size());
        }

        @Test
        @DisplayName("业务继承后 instanceof LayoutCommon<StackPaneAnt> 仍成立")
        void subclassIsLayoutCommon() {
            LoadingOverlay overlay = new LoadingOverlay();
            assertTrue(overlay instanceof LayoutCommon);
        }
    }

    // ============================================================
    // FullChain
    // ============================================================

    @Nested
    @DisplayName("全链路长链式")
    class FullChain {

        @Test
        @DisplayName("一长串链式调用不抛异常且状态正确")
        void fullChain() {
            Label a = new Label("A");
            Label b = new Label("B");
            StackPaneAnt ant = StackPaneAnt.create()
                    .align(Pos.CENTER)
                    .padding(16)
                    .background(Background.LAYOUT)
                    .borderTop()
                    .borderBottom(true)
                    .id("chain-stackpane")
                    .visible(true)
                    .managed(true)
                    .opacity(0.95)
                    .maxW(800)
                    .prefSize(400, 100)
                    .children(a, b)
                    .childAlign(a, Pos.TOP_LEFT)
                    .margin(b, new Insets(5))
                    .build();
            assertNotNull(ant);
            assertEquals(Pos.CENTER, ant.getAlignment());
            assertEquals(new Insets(16), ant.getPadding());
            assertTrue(ant.getStyleClass().contains(Background.LAYOUT.styleClass()));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals("chain-stackpane", ant.getId());
            assertTrue(ant.isVisible());
            assertTrue(ant.isManaged());
            assertEquals(0.95, ant.getOpacity(), 0.001);
            assertEquals(800, ant.getMaxWidth(), 0.0);
            assertEquals(400, ant.getPrefWidth(), 0.0);
            assertEquals(100, ant.getPrefHeight(), 0.0);
            assertEquals(2, ant.getChildren().size());
            assertEquals(Pos.TOP_LEFT, StackPane.getAlignment(a));
            assertEquals(new Insets(5), StackPane.getMargin(b));
        }
    }
}
