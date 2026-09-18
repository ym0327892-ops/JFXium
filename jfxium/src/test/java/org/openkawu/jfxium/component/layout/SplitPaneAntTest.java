package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SplitPaneAnt 单元测试 —— LayoutCommon<SplitPaneAnt> 接入后行为回归保护。
 *
 * <p>覆盖：Factory / Constructors / BusinessMethods(SplitPane 特有) /
 * StyleApi / Padding / Borders / Sizes / NodeProps / Inheritance / FullChain。
 * 业务继承 SplitPaneAnt 后构造里调用的所有 LayoutCommon default method
 * （padding / background / id / borderTop 等）必须生效，链式 API 必须返回子类类型。</p>
 */
@DisplayName("SplitPaneAnt 单元测试（接入 LayoutCommon<SplitPaneAnt>）")
class SplitPaneAntTest extends JfxTestBase {

    // ============================================================
    // Factory
    // ============================================================

    @Nested
    @DisplayName("工厂入口")
    class Factory {

        @Test
        @DisplayName("create() 返回非 null SplitPaneAnt 实例")
        void createReturnsInstance() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertNotNull(ant);
            assertTrue(ant instanceof SplitPane, "SplitPaneAnt 必须是 SplitPane 子类");
            assertTrue(ant instanceof LayoutCommon, "SplitPaneAnt 必须实现 LayoutCommon");
        }

        @Test
        @DisplayName("create() 默认带 SPLIT_PANE styleClass")
        void createEmptyDefaults() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertEquals(0, ant.getItems().size());
            // 构造里强制加了 SPLIT_PANE
            assertTrue(ant.getStyleClass().contains(JfxStyles.SPLIT_PANE));
            assertEquals(Orientation.HORIZONTAL, ant.getOrientation());
        }

        @Test
        @DisplayName("create(items...) 批量添加窗格")
        void createWithItems() {
            Label a = new Label("A");
            Label b = new Label("B");
            // SplitPane 没有 create(items...) 工厂，先用构造函数
            SplitPaneAnt ant = new SplitPaneAnt(a, b);
            assertEquals(2, ant.getItems().size());
            assertTrue(ant.getItems().containsAll(java.util.List.of(a, b)));
            assertTrue(ant.getStyleClass().contains(JfxStyles.SPLIT_PANE));
        }

    }

    // ============================================================
    // Constructors
    // ============================================================

    @Nested
    @DisplayName("构造函数（公开，便于业务 extends）")
    class Constructors {

        @Test
        @DisplayName("无参构造（默认 HORIZONTAL，自动挂 SPLIT_PANE）")
        void noArg() {
            SplitPaneAnt ant = new SplitPaneAnt();
            assertEquals(0, ant.getItems().size());
            assertEquals(Orientation.HORIZONTAL, ant.getOrientation());
            assertTrue(ant.getStyleClass().contains(JfxStyles.SPLIT_PANE));
        }

        @Test
        @DisplayName("items 数组构造（自动挂 SPLIT_PANE）")
        void itemsConstructor() {
            Label a = new Label("A");
            SplitPaneAnt ant = new SplitPaneAnt(a);
            assertEquals(1, ant.getItems().size());
            assertTrue(ant.getStyleClass().contains(JfxStyles.SPLIT_PANE));
        }
    }

    // ============================================================
    // BusinessMethods（SplitPane 特有）
    // ============================================================

    @Nested
    @DisplayName("业务方法（SplitPane 特有）")
    class BusinessMethods {

        @Test
        @DisplayName("direction(HORIZONTAL/VERTICAL) 切换 orientation")
        void directionSwitch() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            ant.direction(SplitPaneAnt.Direction.HORIZONTAL);
            assertEquals(Orientation.HORIZONTAL, ant.getOrientation());
            ant.direction(SplitPaneAnt.Direction.VERTICAL);
            assertEquals(Orientation.VERTICAL, ant.getOrientation());
        }

        @Test
        @DisplayName("item(null) 安全不抛异常")
        void itemNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertDoesNotThrow(() -> ant.item(null));
            assertEquals(0, ant.getItems().size());
        }

        @Test
        @DisplayName("item(node) 添加单个窗格")
        void itemAddsNode() {
            Label a = new Label("A");
            SplitPaneAnt ant = SplitPaneAnt.create().item(a);
            assertEquals(1, ant.getItems().size());
            assertTrue(ant.getItems().contains(a));
        }

        @Test
        @DisplayName("items 批量添加，null 节点被过滤")
        void itemsBatchAdd() {
            Label a = new Label("A");
            Label b = new Label("B");
            SplitPaneAnt ant = SplitPaneAnt.create().items(a, null, b);
            assertEquals(2, ant.getItems().size());
        }

        @Test
        @DisplayName("items(null) 安全不抛异常")
        void itemsNullArraySafe() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertDoesNotThrow(() -> ant.items((javafx.scene.Node[]) null));
            assertEquals(0, ant.getItems().size());
        }

        @Test
        @DisplayName("dividerPositions(0.3) 在 items 设置后能正常生效")
        void dividerPositionsRoundTrip() {
            Label a = new Label("A");
            Label b = new Label("B");
            SplitPaneAnt ant = SplitPaneAnt.create().items(a, b).dividerPositions(0.3);
            assertEquals(0.3, ant.getDividerPositions()[0], 0.001);
        }

        @Test
        @DisplayName("dividerPositions 多个位置（items 数 - 1）")
        void dividerPositionsMultiple() {
            Label a = new Label("A");
            Label b = new Label("B");
            Label c = new Label("C");
            SplitPaneAnt ant = SplitPaneAnt.create()
                    .items(a, b, c)
                    .dividerPositions(0.2, 0.7);
            double[] pos = ant.getDividerPositions();
            assertEquals(2, pos.length);
            assertEquals(0.2, pos[0], 0.001);
            assertEquals(0.7, pos[1], 0.001);
        }

        @Test
        @DisplayName("dividerPositions 越界值钳制到 0..1")
        void dividerPositionsOutOfRangeValuesAreClamped() {
            Label a = new Label("A");
            Label b = new Label("B");
            Label c = new Label("C");
            SplitPaneAnt ant = SplitPaneAnt.create()
                    .items(a, b, c)
                    .dividerPositions(-0.5, 1.5);

            double[] pos = ant.getDividerPositions();
            assertEquals(2, pos.length);
            assertEquals(0.0, pos[0], 0.001);
            assertEquals(1.0, pos[1], 0.001);
        }

        @Test
        @DisplayName("dividerPositions 忽略 NaN 和无穷大")
        void dividerPositionsIgnoreNonFiniteValues() {
            Label a = new Label("A");
            Label b = new Label("B");
            SplitPaneAnt ant = SplitPaneAnt.create()
                    .items(a, b)
                    .dividerPositions(Double.NaN, Double.POSITIVE_INFINITY, 0.4);

            double[] pos = ant.getDividerPositions();
            assertEquals(1, pos.length);
            assertEquals(0.4, pos[0], 0.001);
        }

        @Test
        @DisplayName("dividerPositions(null) 安全不抛异常")
        void dividerPositionsNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertDoesNotThrow(() -> ant.dividerPositions((double[]) null));
        }

        @Test
        @DisplayName("dividerPositions() 空数组安全不抛异常")
        void dividerPositionsEmptySafe() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertDoesNotThrow(() -> ant.dividerPositions());
        }

        @Test
        @DisplayName("resizableWithParent 设置后能读取")
        void resizableWithParentRoundTrip() {
            Label a = new Label("A");
            SplitPaneAnt ant = SplitPaneAnt.create().item(a);
            ant.resizableWithParent(a, false);
            assertFalse(SplitPane.isResizableWithParent(a));
            ant.resizableWithParent(a, true);
            assertTrue(SplitPane.isResizableWithParent(a));
        }

        @Test
        @DisplayName("resizableWithParent(null) 安全不抛异常")
        void resizableWithParentNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertDoesNotThrow(() -> ant.resizableWithParent(null, true));
        }

        @Test
        @DisplayName("业务方法链式调用返回 this")
        void businessChaining() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            SplitPaneAnt ret = ant.direction(SplitPaneAnt.Direction.VERTICAL)
                    .item(new Label("X"));
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
            SplitPaneAnt ant = SplitPaneAnt.create();
            SplitPaneAnt ret = ant.styleClass("foo");
            assertSame(ant, ret);
            assertTrue(ant.getStyleClass().contains("foo"));
            ant.styleClass("foo");
            assertEquals(1, ant.getStyleClass().stream().filter("foo"::equals).count());
        }

        @Test
        @DisplayName("styleClass(null)/styleClass(\"\") 安全")
        void styleClassNullOrEmptySafe() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            ant.styleClass((String) null);
            ant.styleClass("");
            // SPLIT_PANE 还在
            assertTrue(ant.getStyleClass().contains(JfxStyles.SPLIT_PANE));
            assertFalse(ant.getStyleClass().contains("foo"));
            assertFalse(ant.getStyleClass().contains(""));
        }

        @Test
        @DisplayName("styleClass(String...) 批量挂载")
        void styleClassVarargs() {
            SplitPaneAnt ant = SplitPaneAnt.create().styleClass("a", "b", "c");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b", "c")));
        }

        @Test
        @DisplayName("styleClass(String...) 含 null 安全")
        void styleClassVarargsNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create().styleClass("a", null, "b");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b")));
        }

        @Test
        @DisplayName("styleClass((String[]) null) 安全")
        void styleClassVarargsArrayNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create().styleClass((String[]) null);
            assertTrue(ant.getStyleClass().contains(JfxStyles.SPLIT_PANE));
        }

        @Test
        @DisplayName("background(Background) 挂对应 styleClass")
        void backgroundSetsStyleClass() {
            SplitPaneAnt ant = SplitPaneAnt.create().background(Background.SUBTLE);
            assertTrue(ant.getStyleClass().stream()
                    .anyMatch(Background.SUBTLE.styleClass()::equals));
        }

        @Test
        @DisplayName("background(null) 安全不挂任何 styleClass（除 SPLIT_PANE）")
        void backgroundNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create().background(null);
            assertTrue(ant.getStyleClass().contains(JfxStyles.SPLIT_PANE));
            assertFalse(ant.getStyleClass().stream()
                    .anyMatch(Background.SUBTLE.styleClass()::equals));
        }

        @Test
        @DisplayName("style(inline) 设置后能读取")
        void styleInlineRoundTrip() {
            SplitPaneAnt ant = SplitPaneAnt.create().style("-fx-opacity: 0.5");
            assertEquals("-fx-opacity: 0.5", ant.getStyle());
        }

        @Test
        @DisplayName("style(null) 安全不抛异常")
        void styleNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create().style(null);
            assertEquals("", ant.getStyle());
        }

        @Test
        @DisplayName("style 链式返回 this")
        void styleChaining() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            SplitPaneAnt ret = ant.style("-fx-foo: bar");
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("多次 styleClass 不重复挂载（幂等）")
        void styleClassMultipleCallsIdempotent() {
            SplitPaneAnt ant = SplitPaneAnt.create();
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
            SplitPaneAnt ant = SplitPaneAnt.create().padding(12);
            assertEquals(new Insets(12), ant.getPadding());
        }

        @Test
        @DisplayName("padding(top,right,bottom,left) 各边不同")
        void paddingFourSides() {
            SplitPaneAnt ant = SplitPaneAnt.create().padding(1, 2, 3, 4);
            assertEquals(new Insets(1, 2, 3, 4), ant.getPadding());
        }

        @Test
        @DisplayName("padding(Insets) 设置对象")
        void paddingInsets() {
            SplitPaneAnt ant = SplitPaneAnt.create().padding(new Insets(5, 10, 15, 20));
            assertEquals(new Insets(5, 10, 15, 20), ant.getPadding());
        }

        @Test
        @DisplayName("padding((Insets) null) 安全不抛异常")
        void paddingNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertDoesNotThrow(() -> ant.padding((Insets) null));
        }

        @Test
        @DisplayName("padding 链式返回 this")
        void paddingChaining() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            SplitPaneAnt ret = ant.padding(8);
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
            SplitPaneAnt ant = SplitPaneAnt.create()
                    .borderTop().borderBottom().borderLeft().borderRight();
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderTop(true) 挂载，borderTop(false) 不挂")
        void borderTopSwitch() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            ant.borderTop(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            ant.borderTop(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
        }

        @Test
        @DisplayName("borderBottom(true) 挂载")
        void borderBottomSwitch() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            ant.borderBottom(false);
            ant.borderBottom(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        }

        @Test
        @DisplayName("borderLeft(true) 挂载")
        void borderLeftSwitch() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            ant.borderLeft(false);
            ant.borderLeft(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
        }

        @Test
        @DisplayName("borderRight(true) 挂载")
        void borderRightSwitch() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            ant.borderRight(false);
            ant.borderRight(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderXxx 链式返回 this")
        void borderChaining() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            SplitPaneAnt ret = ant.borderTop();
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderTop(boolean) 链式返回 this")
        void borderSwitchChaining() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            SplitPaneAnt ret = ant.borderTop(true);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderXxx(false) 不挂载对应 styleClass")
        void borderSwitchOffDoesNothing() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            ant.borderTop(false).borderBottom(false).borderLeft(false).borderRight(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("多次 borderTop() 幂等不重复挂")
        void borderIdempotent() {
            SplitPaneAnt ant = SplitPaneAnt.create().borderTop().borderTop().borderTop();
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
            SplitPaneAnt ant = SplitPaneAnt.create()
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
            SplitPaneAnt ant = SplitPaneAnt.create()
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
            SplitPaneAnt ant = SplitPaneAnt.create();
            SplitPaneAnt ret = ant.maxW(100).prefSize(50, 60);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("尺寸 set 后再 set 能覆盖")
        void sizesOverridable() {
            SplitPaneAnt ant = SplitPaneAnt.create().maxW(100).maxW(200);
            assertEquals(200, ant.getMaxWidth(), 0.0);
        }

        @Test
        @DisplayName("prefSize(0,0) 合法不抛异常")
        void prefSizeZeroSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create().prefSize(0, 0);
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
            SplitPaneAnt ant = SplitPaneAnt.create().visible(false);
            assertFalse(ant.isVisible());
        }

        @Test
        @DisplayName("disable 设置后能读取")
        void disableRoundTrip() {
            SplitPaneAnt ant = SplitPaneAnt.create().disable(true);
            assertTrue(ant.isDisabled());
        }

        @Test
        @DisplayName("managed 设置后能读取")
        void managedRoundTrip() {
            SplitPaneAnt ant = SplitPaneAnt.create().managed(false);
            assertFalse(ant.isManaged());
        }

        @Test
        @DisplayName("opacity 设置后能读取")
        void opacityRoundTrip() {
            SplitPaneAnt ant = SplitPaneAnt.create().opacity(0.42);
            assertEquals(0.42, ant.getOpacity(), 0.001);
        }

        @Test
        @DisplayName("cursor 设置后能读取")
        void cursorRoundTrip() {
            SplitPaneAnt ant = SplitPaneAnt.create().cursor(Cursor.HAND);
            assertEquals(Cursor.HAND, ant.getCursor());
        }

        @Test
        @DisplayName("id 设置后能读取")
        void idRoundTrip() {
            SplitPaneAnt ant = SplitPaneAnt.create().id("my-splitpane");
            assertEquals("my-splitpane", ant.getId());
        }

        @Test
        @DisplayName("id(null) 安全不抛异常（与原 SplitPaneAnt 行为一致）")
        void idNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertDoesNotThrow(() -> ant.id(null));
        }

        @Test
        @DisplayName("cursor(null) 安全不抛异常（与原 SplitPaneAnt 行为一致）")
        void cursorNullSafe() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            assertDoesNotThrow(() -> ant.cursor(null));
        }

        @Test
        @DisplayName("NodeProps 链式返回 this")
        void nodePropsChaining() {
            SplitPaneAnt ant = SplitPaneAnt.create();
            SplitPaneAnt ret = ant.visible(true).id("x").opacity(1.0);
            assertSame(ant, ret);
        }
    }

    // ============================================================
    // Inheritance（业务继承 SplitPaneAnt 的核心场景）
    // ============================================================

    @Nested
    @DisplayName("业务继承")
    class Inheritance {

        /** 业务继承示例：模拟业务侧 extends SplitPaneAnt 写 IDE 风格分屏页。 */
        static class IdePage extends SplitPaneAnt {
            IdePage() {
                direction(Direction.HORIZONTAL);
                items(new Label("Sidebar"), new Label("Editor"));
                padding(8);
                background(Background.SUBTLE);
                id("my-ide");
                borderBottom();
            }
        }

        @Test
        @DisplayName("业务继承后构造里 LayoutCommon default method 全部生效")
        void subclassInheritsAllDefaults() {
            IdePage ide = new IdePage();
            assertEquals(Orientation.HORIZONTAL, ide.getOrientation());
            assertEquals(2, ide.getItems().size());
            assertEquals(new Insets(8), ide.getPadding());
            assertTrue(ide.getStyleClass().contains(Background.SUBTLE.styleClass()));
            assertEquals("my-ide", ide.getId());
            assertTrue(ide.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        }

        @Test
        @DisplayName("业务继承后 instanceof LayoutCommon<SplitPaneAnt> 仍成立")
        void subclassIsLayoutCommon() {
            IdePage ide = new IdePage();
            assertTrue(ide instanceof LayoutCommon);
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
            SplitPaneAnt ant = SplitPaneAnt.create()
                    .direction(SplitPaneAnt.Direction.VERTICAL)
                    .items(a, b)
                    .dividerPositions(0.4)
                    .padding(12)
                    .background(Background.LAYOUT)
                    .borderTop()
                    .borderBottom(true)
                    .id("chain-splitpane")
                    .visible(true)
                    .managed(true)
                    .opacity(0.95)
                    .maxW(800)
                    .prefSize(400, 600)
                    .resizableWithParent(a, false)
                    .build();
            assertNotNull(ant);
            assertEquals(Orientation.VERTICAL, ant.getOrientation());
            assertEquals(2, ant.getItems().size());
            assertEquals(0.4, ant.getDividerPositions()[0], 0.001);
            assertEquals(new Insets(12), ant.getPadding());
            assertTrue(ant.getStyleClass().contains(Background.LAYOUT.styleClass()));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals("chain-splitpane", ant.getId());
            assertTrue(ant.isVisible());
            assertTrue(ant.isManaged());
            assertEquals(0.95, ant.getOpacity(), 0.001);
            assertEquals(800, ant.getMaxWidth(), 0.0);
            assertEquals(400, ant.getPrefWidth(), 0.0);
            assertEquals(600, ant.getPrefHeight(), 0.0);
            assertFalse(SplitPane.isResizableWithParent(a));
        }
    }
}
