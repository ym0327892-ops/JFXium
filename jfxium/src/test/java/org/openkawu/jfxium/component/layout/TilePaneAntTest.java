package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TilePaneAnt 单元测试 —— LayoutCommon<TilePaneAnt> 接入后行为回归保护。
 *
 * <p>覆盖：Factory / Constructors / BusinessMethods(TilePane 特有) /
 * StyleApi / Padding / Borders / Sizes / NodeProps / Inheritance / FullChain。
 * 业务继承 TilePaneAnt 后构造里调用的所有 LayoutCommon default method
 * （padding / background / id / borderTop 等）必须生效，链式 API 必须返回子类类型。</p>
 */
@DisplayName("TilePaneAnt 单元测试（接入 LayoutCommon<TilePaneAnt>）")
class TilePaneAntTest extends JfxTestBase {

    // ============================================================
    // Factory
    // ============================================================

    @Nested
    @DisplayName("工厂入口")
    class Factory {

        @Test
        @DisplayName("create() 返回非 null TilePaneAnt 实例")
        void createReturnsInstance() {
            TilePaneAnt ant = TilePaneAnt.create();
            assertNotNull(ant);
            assertTrue(ant instanceof TilePane, "TilePaneAnt 必须是 TilePane 子类");
            assertTrue(ant instanceof LayoutCommon, "TilePaneAnt 必须实现 LayoutCommon");
        }

        @Test
        @DisplayName("create() 默认带 TILE_PANE styleClass")
        void createEmptyDefaults() {
            TilePaneAnt ant = TilePaneAnt.create();
            assertEquals(0, ant.getChildren().size());
            // 构造里强制加了 TILE_PANE
            assertTrue(ant.getStyleClass().contains(JfxStyles.TILE_PANE));
            assertEquals(5, ant.getPrefColumns());
            assertEquals(5, ant.getPrefRows());
            assertEquals(Orientation.HORIZONTAL, ant.getOrientation());
        }

        @Test
        @DisplayName("create() 默认无子节点")
        void createNoChildren() {
            TilePaneAnt ant = TilePaneAnt.create();
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
        @DisplayName("无参构造（自动挂 TILE_PANE）")
        void noArg() {
            TilePaneAnt ant = new TilePaneAnt();
            assertEquals(0, ant.getChildren().size());
            assertTrue(ant.getStyleClass().contains(JfxStyles.TILE_PANE));
        }
    }

    // ============================================================
    // BusinessMethods（TilePane 特有）
    // ============================================================

    @Nested
    @DisplayName("业务方法（TilePane 特有）")
    class BusinessMethods {

        @Test
        @DisplayName("prefColumns 设置后能读取")
        void prefColumnsRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().prefColumns(4);
            assertEquals(4, ant.getPrefColumns());
        }

        @Test
        @DisplayName("prefRows 设置后能读取")
        void prefRowsRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().prefRows(3);
            assertEquals(3, ant.getPrefRows());
        }

        @Test
        @DisplayName("非法行列数钳制到 1")
        void invalidPrefRowsAndColumnsAreClamped() {
            TilePaneAnt ant = TilePaneAnt.create()
                    .prefColumns(0)
                    .prefRows(-3);
            assertEquals(1, ant.getPrefColumns());
            assertEquals(1, ant.getPrefRows());
        }

        @Test
        @DisplayName("orientation 设置后能读取")
        void orientationRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().orientation(Orientation.VERTICAL);
            assertEquals(Orientation.VERTICAL, ant.getOrientation());
        }

        @Test
        @DisplayName("hgap 设置后能读取")
        void hgapRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().hgap(8);
            assertEquals(8.0, ant.getHgap(), 0.0);
        }

        @Test
        @DisplayName("vgap 设置后能读取")
        void vgapRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().vgap(12);
            assertEquals(12.0, ant.getVgap(), 0.0);
        }

        @Test
        @DisplayName("gap 同时设置 hgap 和 vgap")
        void gapSetsBoth() {
            TilePaneAnt ant = TilePaneAnt.create().gap(10);
            assertEquals(10.0, ant.getHgap(), 0.0);
            assertEquals(10.0, ant.getVgap(), 0.0);
        }

        @Test
        @DisplayName("负间距钳制为 0")
        void negativeGapsAreClamped() {
            TilePaneAnt ant = TilePaneAnt.create()
                    .hgap(-8)
                    .vgap(-12);
            assertEquals(0.0, ant.getHgap(), 0.0);
            assertEquals(0.0, ant.getVgap(), 0.0);
        }

        @Test
        @DisplayName("alignment 设置后能读取")
        void alignmentRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().alignment(Pos.CENTER);
            assertEquals(Pos.CENTER, ant.getAlignment());
        }

        @Test
        @DisplayName("children 批量添加")
        void childrenBatchAdd() {
            Label a = new Label("A");
            Label b = new Label("B");
            TilePaneAnt ant = TilePaneAnt.create().children(a, b);
            assertEquals(2, ant.getChildren().size());
        }

        @Test
        @DisplayName("children(null) 安全不抛异常")
        void childrenNullSafe() {
            TilePaneAnt ant = TilePaneAnt.create();
            assertDoesNotThrow(() -> TilePaneAnt.create().children((javafx.scene.Node[]) null));
            assertEquals(0, ant.getChildren().size());
        }

        @Test
        @DisplayName("add(null) 安全不抛异常")
        void addNullSafe() {
            TilePaneAnt ant = TilePaneAnt.create();
            assertDoesNotThrow(() -> ant.add(null));
            assertEquals(0, ant.getChildren().size());
        }

        @Test
        @DisplayName("add(node) 添加单个子节点")
        void addAddsNode() {
            Label a = new Label("A");
            TilePaneAnt ant = TilePaneAnt.create().add(a);
            assertEquals(1, ant.getChildren().size());
            assertTrue(ant.getChildren().contains(a));
        }

        @Test
        @DisplayName("业务方法链式调用返回 this")
        void businessChaining() {
            TilePaneAnt ant = TilePaneAnt.create();
            TilePaneAnt ret = ant.prefColumns(4).hgap(8).alignment(Pos.CENTER);
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
            TilePaneAnt ant = TilePaneAnt.create();
            TilePaneAnt ret = ant.styleClass("foo");
            assertSame(ant, ret);
            assertTrue(ant.getStyleClass().contains("foo"));
            ant.styleClass("foo");
            assertEquals(1, ant.getStyleClass().stream().filter("foo"::equals).count());
        }

        @Test
        @DisplayName("styleClass(null)/styleClass(\"\") 安全")
        void styleClassNullOrEmptySafe() {
            TilePaneAnt ant = TilePaneAnt.create();
            ant.styleClass((String) null);
            ant.styleClass("");
            assertTrue(ant.getStyleClass().contains(JfxStyles.TILE_PANE));
            assertFalse(ant.getStyleClass().contains("foo"));
            assertFalse(ant.getStyleClass().contains(""));
        }

        @Test
        @DisplayName("styleClass(String...) 批量挂载")
        void styleClassVarargs() {
            TilePaneAnt ant = TilePaneAnt.create().styleClass("a", "b", "c");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b", "c")));
        }

        @Test
        @DisplayName("styleClass(String...) 含 null 安全")
        void styleClassVarargsNullSafe() {
            TilePaneAnt ant = TilePaneAnt.create().styleClass("a", null, "b");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b")));
        }

        @Test
        @DisplayName("styleClass((String[]) null) 安全")
        void styleClassVarargsArrayNullSafe() {
            TilePaneAnt ant = TilePaneAnt.create().styleClass((String[]) null);
            assertTrue(ant.getStyleClass().contains(JfxStyles.TILE_PANE));
        }

        @Test
        @DisplayName("background(Background) 挂对应 styleClass")
        void backgroundSetsStyleClass() {
            TilePaneAnt ant = TilePaneAnt.create().background(Background.SUBTLE);
            assertTrue(ant.getStyleClass().stream()
                    .anyMatch(Background.SUBTLE.styleClass()::equals));
        }

        @Test
        @DisplayName("background(null) 安全不挂任何 styleClass（除 TILE_PANE）")
        void backgroundNullSafe() {
            TilePaneAnt ant = TilePaneAnt.create().background(null);
            assertTrue(ant.getStyleClass().contains(JfxStyles.TILE_PANE));
            assertFalse(ant.getStyleClass().stream()
                    .anyMatch(Background.SUBTLE.styleClass()::equals));
        }

        @Test
        @DisplayName("style(inline) 设置后能读取")
        void styleInlineRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().style("-fx-opacity: 0.5");
            assertEquals("-fx-opacity: 0.5", ant.getStyle());
        }

        @Test
        @DisplayName("style(null) 安全不抛异常")
        void styleNullSafe() {
            TilePaneAnt ant = TilePaneAnt.create().style(null);
            assertEquals("", ant.getStyle());
        }

        @Test
        @DisplayName("style 链式返回 this")
        void styleChaining() {
            TilePaneAnt ant = TilePaneAnt.create();
            TilePaneAnt ret = ant.style("-fx-foo: bar");
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("多次 styleClass 不重复挂载（幂等）")
        void styleClassMultipleCallsIdempotent() {
            TilePaneAnt ant = TilePaneAnt.create();
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
            TilePaneAnt ant = TilePaneAnt.create().padding(12);
            assertEquals(new Insets(12), ant.getPadding());
        }

        @Test
        @DisplayName("padding(top,right,bottom,left) 各边不同")
        void paddingFourSides() {
            TilePaneAnt ant = TilePaneAnt.create().padding(1, 2, 3, 4);
            assertEquals(new Insets(1, 2, 3, 4), ant.getPadding());
        }

        @Test
        @DisplayName("padding(Insets) 设置对象")
        void paddingInsets() {
            TilePaneAnt ant = TilePaneAnt.create().padding(new Insets(5, 10, 15, 20));
            assertEquals(new Insets(5, 10, 15, 20), ant.getPadding());
        }

        @Test
        @DisplayName("padding((Insets) null) 安全不抛异常")
        void paddingNullSafe() {
            TilePaneAnt ant = TilePaneAnt.create();
            assertDoesNotThrow(() -> ant.padding((Insets) null));
        }

        @Test
        @DisplayName("padding 链式返回 this")
        void paddingChaining() {
            TilePaneAnt ant = TilePaneAnt.create();
            TilePaneAnt ret = ant.padding(8);
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
            TilePaneAnt ant = TilePaneAnt.create()
                    .borderTop().borderBottom().borderLeft().borderRight();
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderTop(true) 挂载，borderTop(false) 不挂")
        void borderTopSwitch() {
            TilePaneAnt ant = TilePaneAnt.create();
            ant.borderTop(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            ant.borderTop(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
        }

        @Test
        @DisplayName("borderBottom(true) 挂载")
        void borderBottomSwitch() {
            TilePaneAnt ant = TilePaneAnt.create();
            ant.borderBottom(false);
            ant.borderBottom(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        }

        @Test
        @DisplayName("borderLeft(true) 挂载")
        void borderLeftSwitch() {
            TilePaneAnt ant = TilePaneAnt.create();
            ant.borderLeft(false);
            ant.borderLeft(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
        }

        @Test
        @DisplayName("borderRight(true) 挂载")
        void borderRightSwitch() {
            TilePaneAnt ant = TilePaneAnt.create();
            ant.borderRight(false);
            ant.borderRight(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderXxx 链式返回 this")
        void borderChaining() {
            TilePaneAnt ant = TilePaneAnt.create();
            TilePaneAnt ret = ant.borderTop();
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderTop(boolean) 链式返回 this")
        void borderSwitchChaining() {
            TilePaneAnt ant = TilePaneAnt.create();
            TilePaneAnt ret = ant.borderTop(true);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderXxx(false) 不挂载对应 styleClass")
        void borderSwitchOffDoesNothing() {
            TilePaneAnt ant = TilePaneAnt.create();
            ant.borderTop(false).borderBottom(false).borderLeft(false).borderRight(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("多次 borderTop() 幂等不重复挂")
        void borderIdempotent() {
            TilePaneAnt ant = TilePaneAnt.create().borderTop().borderTop().borderTop();
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
            TilePaneAnt ant = TilePaneAnt.create()
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
            TilePaneAnt ant = TilePaneAnt.create()
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
            TilePaneAnt ant = TilePaneAnt.create();
            TilePaneAnt ret = ant.maxW(100).prefSize(50, 60);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("尺寸 set 后再 set 能覆盖")
        void sizesOverridable() {
            TilePaneAnt ant = TilePaneAnt.create().maxW(100).maxW(200);
            assertEquals(200, ant.getMaxWidth(), 0.0);
        }

        @Test
        @DisplayName("prefSize(0,0) 合法不抛异常")
        void prefSizeZeroSafe() {
            TilePaneAnt ant = TilePaneAnt.create().prefSize(0, 0);
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
            TilePaneAnt ant = TilePaneAnt.create().visible(false);
            assertFalse(ant.isVisible());
        }

        @Test
        @DisplayName("disable 设置后能读取")
        void disableRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().disable(true);
            assertTrue(ant.isDisabled());
        }

        @Test
        @DisplayName("managed 设置后能读取")
        void managedRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().managed(false);
            assertFalse(ant.isManaged());
        }

        @Test
        @DisplayName("opacity 设置后能读取")
        void opacityRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().opacity(0.42);
            assertEquals(0.42, ant.getOpacity(), 0.001);
        }

        @Test
        @DisplayName("cursor 设置后能读取")
        void cursorRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().cursor(Cursor.HAND);
            assertEquals(Cursor.HAND, ant.getCursor());
        }

        @Test
        @DisplayName("id 设置后能读取")
        void idRoundTrip() {
            TilePaneAnt ant = TilePaneAnt.create().id("my-tilepane");
            assertEquals("my-tilepane", ant.getId());
        }

        @Test
        @DisplayName("id(null) 安全不抛异常（与原 TilePaneAnt 行为一致）")
        void idNullSafe() {
            TilePaneAnt ant = TilePaneAnt.create();
            assertDoesNotThrow(() -> ant.id(null));
        }

        @Test
        @DisplayName("cursor(null) 安全不抛异常（与原 TilePaneAnt 行为一致）")
        void cursorNullSafe() {
            TilePaneAnt ant = TilePaneAnt.create();
            assertDoesNotThrow(() -> ant.cursor(null));
        }

        @Test
        @DisplayName("NodeProps 链式返回 this")
        void nodePropsChaining() {
            TilePaneAnt ant = TilePaneAnt.create();
            TilePaneAnt ret = ant.visible(true).id("x").opacity(1.0);
            assertSame(ant, ret);
        }
    }

    // ============================================================
    // Inheritance（业务继承 TilePaneAnt 的核心场景）
    // ============================================================

    @Nested
    @DisplayName("业务继承")
    class Inheritance {

        /** 业务继承示例：模拟业务侧 extends TilePaneAnt 写缩略图网格。 */
        static class ThumbnailGrid extends TilePaneAnt {
            ThumbnailGrid() {
                prefColumns(4);
                hgap(10);
                vgap(10);
                padding(8);
                background(Background.SUBTLE);
                id("my-thumbs");
                borderBottom();
                add(new Label("thumb1"));
                add(new Label("thumb2"));
            }
        }

        @Test
        @DisplayName("业务继承后构造里 LayoutCommon default method 全部生效")
        void subclassInheritsAllDefaults() {
            ThumbnailGrid grid = new ThumbnailGrid();
            assertEquals(4, grid.getPrefColumns());
            assertEquals(10.0, grid.getHgap(), 0.0);
            assertEquals(10.0, grid.getVgap(), 0.0);
            assertEquals(new Insets(8), grid.getPadding());
            assertTrue(grid.getStyleClass().contains(Background.SUBTLE.styleClass()));
            assertEquals("my-thumbs", grid.getId());
            assertTrue(grid.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals(2, grid.getChildren().size());
        }

        @Test
        @DisplayName("业务继承后 instanceof LayoutCommon<TilePaneAnt> 仍成立")
        void subclassIsLayoutCommon() {
            ThumbnailGrid grid = new ThumbnailGrid();
            assertTrue(grid instanceof LayoutCommon);
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
            TilePaneAnt ant = TilePaneAnt.create()
                    .prefColumns(4)
                    .hgap(10)
                    .vgap(12)
                    .orientation(Orientation.HORIZONTAL)
                    .alignment(Pos.CENTER)
                    .padding(16)
                    .background(Background.LAYOUT)
                    .borderTop()
                    .borderBottom(true)
                    .id("chain-tilepane")
                    .visible(true)
                    .managed(true)
                    .opacity(0.95)
                    .maxW(800)
                    .prefSize(400, 100)
                    .children(a, b)
                    .add(new Label("C"))
                    .build();
            assertNotNull(ant);
            assertEquals(4, ant.getPrefColumns());
            assertEquals(10.0, ant.getHgap(), 0.0);
            assertEquals(12.0, ant.getVgap(), 0.0);
            assertEquals(Orientation.HORIZONTAL, ant.getOrientation());
            assertEquals(Pos.CENTER, ant.getAlignment());
            assertEquals(new Insets(16), ant.getPadding());
            assertTrue(ant.getStyleClass().contains(Background.LAYOUT.styleClass()));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals("chain-tilepane", ant.getId());
            assertTrue(ant.isVisible());
            assertTrue(ant.isManaged());
            assertEquals(0.95, ant.getOpacity(), 0.001);
            assertEquals(800, ant.getMaxWidth(), 0.0);
            assertEquals(400, ant.getPrefWidth(), 0.0);
            assertEquals(100, ant.getPrefHeight(), 0.0);
            assertEquals(3, ant.getChildren().size());
        }
    }
}
