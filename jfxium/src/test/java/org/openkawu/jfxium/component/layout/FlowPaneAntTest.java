package org.openkawu.jfxium.component.layout;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FlowPaneAnt 单元测试 —— LayoutCommon<FlowPaneAnt> 接入后行为回归保护。
 *
 * <p>覆盖：Factory / Constructors / BusinessMethods(FlowPane 特有) /
 * StyleApi / Padding / Borders / Sizes / NodeProps / Inheritance / FullChain。
 * 业务继承 FlowPaneAnt 后构造里调用的所有 LayoutCommon default method
 * （padding / background / id / borderTop 等）必须生效，链式 API 必须返回子类类型。</p>
 */
@DisplayName("FlowPaneAnt 单元测试（接入 LayoutCommon<FlowPaneAnt>）")
class FlowPaneAntTest extends JfxTestBase {

    // ============================================================
    // Factory
    // ============================================================

    @Nested
    @DisplayName("工厂入口")
    class Factory {

        @Test
        @DisplayName("create() 返回非 null FlowPaneAnt 实例")
        void createReturnsInstance() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            assertNotNull(ant);
            assertTrue(ant instanceof FlowPane, "FlowPaneAnt 必须是 FlowPane 子类");
            assertTrue(ant instanceof LayoutCommon, "FlowPaneAnt 必须实现 LayoutCommon");
        }

        @Test
        @DisplayName("create() 默认无子节点、styleClass 为空")
        void createEmptyDefaults() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            assertEquals(0, ant.getChildren().size());
            assertTrue(ant.getStyleClass().isEmpty());
            assertEquals(0.0, ant.getHgap(), 0.0);
            assertEquals(0.0, ant.getVgap(), 0.0);
        }

        @Test
        @DisplayName("create(children...) 批量添加子节点")
        void createWithChildren() {
            Label a = new Label("A");
            Label b = new Label("B");
            FlowPaneAnt ant = FlowPaneAnt.create(a, b);
            assertEquals(2, ant.getChildren().size());
            assertTrue(ant.getChildren().containsAll(java.util.List.of(a, b)));
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
            FlowPaneAnt ant = new FlowPaneAnt();
            assertEquals(0.0, ant.getHgap(), 0.0);
            assertEquals(0.0, ant.getVgap(), 0.0);
            assertEquals(Orientation.HORIZONTAL, ant.getOrientation());
        }

        @Test
        @DisplayName("children 数组构造")
        void childrenConstructor() {
            Label a = new Label("A");
            FlowPaneAnt ant = new FlowPaneAnt(a);
            assertEquals(1, ant.getChildren().size());
        }

        @Test
        @DisplayName("orientation 构造")
        void orientationConstructor() {
            FlowPaneAnt ant = new FlowPaneAnt(Orientation.VERTICAL);
            assertEquals(Orientation.VERTICAL, ant.getOrientation());
        }

        @Test
        @DisplayName("hgap+vgap 构造")
        void hgapVgapConstructor() {
            FlowPaneAnt ant = new FlowPaneAnt(8.0, 12.0);
            assertEquals(8.0, ant.getHgap(), 0.0);
            assertEquals(12.0, ant.getVgap(), 0.0);
        }

        @Test
        @DisplayName("构造函数负间距钳制为 0")
        void constructorNegativeGapsAreClamped() {
            FlowPaneAnt ant = new FlowPaneAnt(-8.0, -12.0);
            assertEquals(0.0, ant.getHgap(), 0.0);
            assertEquals(0.0, ant.getVgap(), 0.0);
        }
    }

    // ============================================================
    // BusinessMethods（FlowPane 特有）
    // ============================================================

    @Nested
    @DisplayName("业务方法（FlowPane 特有）")
    class BusinessMethods {

        @Test
        @DisplayName("hgap 设置后能读取")
        void hgapRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().hgap(8);
            assertEquals(8.0, ant.getHgap(), 0.0);
        }

        @Test
        @DisplayName("vgap 设置后能读取")
        void vgapRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().vgap(12);
            assertEquals(12.0, ant.getVgap(), 0.0);
        }

        @Test
        @DisplayName("gap 设置后 hgap 和 vgap 同时生效")
        void gapSetsBoth() {
            FlowPaneAnt ant = FlowPaneAnt.create().gap(10);
            assertEquals(10.0, ant.getHgap(), 0.0);
            assertEquals(10.0, ant.getVgap(), 0.0);
        }

        @Test
        @DisplayName("spacing 等同 gap 语义，hgap 和 vgap 同时生效")
        void spacingSetsBoth() {
            FlowPaneAnt ant = FlowPaneAnt.create().spacing(6);
            assertEquals(6.0, ant.getHgap(), 0.0);
            assertEquals(6.0, ant.getVgap(), 0.0);
        }

        @Test
        @DisplayName("负间距钳制为 0")
        void negativeGapsAreClamped() {
            FlowPaneAnt ant = FlowPaneAnt.create()
                    .hgap(-8)
                    .vgap(-12);
            assertEquals(0.0, ant.getHgap(), 0.0);
            assertEquals(0.0, ant.getVgap(), 0.0);
        }

        @Test
        @DisplayName("orientation 设置后能读取")
        void orientationRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().orientation(Orientation.VERTICAL);
            assertEquals(Orientation.VERTICAL, ant.getOrientation());
        }

        @Test
        @DisplayName("align 设置后能读取")
        void alignRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().align(Pos.CENTER);
            assertEquals(Pos.CENTER, ant.getAlignment());
        }

        @Test
        @DisplayName("children 批量添加，null 节点被过滤")
        void childrenBatchAdd() {
            Label a = new Label("A");
            Label b = new Label("B");
            FlowPaneAnt ant = FlowPaneAnt.create().children(a, null, b);
            assertEquals(2, ant.getChildren().size());
        }

        @Test
        @DisplayName("prefWrapLength 设置后能读取")
        void prefWrapLengthRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().prefWrapLength(300);
            assertEquals(300.0, ant.getPrefWrapLength(), 0.0);
        }

        @Test
        @DisplayName("负 prefWrapLength 钳制为 0")
        void negativePrefWrapLengthIsClamped() {
            FlowPaneAnt ant = FlowPaneAnt.create().prefWrapLength(-300);
            assertEquals(0.0, ant.getPrefWrapLength(), 0.0);
        }

        @Test
        @DisplayName("rowValignment 设置后能读取")
        void rowValignmentRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().rowValignment(VPos.CENTER);
            assertEquals(VPos.CENTER, ant.getRowValignment());
        }

        @Test
        @DisplayName("columnHalignment 设置后能读取")
        void columnHalignmentRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().columnHalignment(HPos.CENTER);
            assertEquals(HPos.CENTER, ant.getColumnHalignment());
        }

        @Test
        @DisplayName("margin 给子节点设置外边距")
        void marginSetsInsets() {
            Label a = new Label("A");
            FlowPaneAnt ant = FlowPaneAnt.create().children(a).margin(a, new Insets(10));
            assertEquals(new Insets(10), FlowPane.getMargin(a));
        }

        @Test
        @DisplayName("业务方法链式调用返回 this")
        void businessChaining() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            FlowPaneAnt ret = ant.hgap(8).vgap(12).orientation(Orientation.HORIZONTAL);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("children(null) 安全，不抛异常")
        void childrenNullSafe() {
            FlowPaneAnt ant = FlowPaneAnt.create().children((javafx.scene.Node[]) null);
            assertEquals(0, ant.getChildren().size());
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
            FlowPaneAnt ant = FlowPaneAnt.create();
            FlowPaneAnt ret = ant.styleClass("foo");
            assertSame(ant, ret);
            assertTrue(ant.getStyleClass().contains("foo"));
            // 重复调用不再挂
            ant.styleClass("foo");
            assertEquals(1, ant.getStyleClass().stream().filter("foo"::equals).count());
        }

        @Test
        @DisplayName("styleClass(null)/styleClass(\"\") 安全")
        void styleClassNullOrEmptySafe() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            ant.styleClass((String) null);
            ant.styleClass("");
            assertTrue(ant.getStyleClass().isEmpty());
        }

        @Test
        @DisplayName("styleClass(String...) 批量挂载")
        void styleClassVarargs() {
            FlowPaneAnt ant = FlowPaneAnt.create().styleClass("a", "b", "c");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b", "c")));
        }

        @Test
        @DisplayName("styleClass(String...) 含 null 安全")
        void styleClassVarargsNullSafe() {
            FlowPaneAnt ant = FlowPaneAnt.create().styleClass("a", null, "b");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b")));
        }

        @Test
        @DisplayName("styleClass((String[]) null) 安全")
        void styleClassVarargsArrayNullSafe() {
            FlowPaneAnt ant = FlowPaneAnt.create().styleClass((String[]) null);
            assertTrue(ant.getStyleClass().isEmpty());
        }

        @Test
        @DisplayName("background(Background) 挂对应 styleClass")
        void backgroundSetsStyleClass() {
            FlowPaneAnt ant = FlowPaneAnt.create().background(Background.SUBTLE);
            assertTrue(ant.getStyleClass().stream()
                    .anyMatch(Background.SUBTLE.styleClass()::equals));
        }

        @Test
        @DisplayName("background(null) 安全不挂任何 styleClass")
        void backgroundNullSafe() {
            FlowPaneAnt ant = FlowPaneAnt.create().background(null);
            assertTrue(ant.getStyleClass().isEmpty());
        }

        @Test
        @DisplayName("style(inline) 设置后能读取")
        void styleInlineRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().style("-fx-opacity: 0.5");
            assertEquals("-fx-opacity: 0.5", ant.getStyle());
        }

        @Test
        @DisplayName("style(null) 安全不抛异常")
        void styleNullSafe() {
            FlowPaneAnt ant = FlowPaneAnt.create().style(null);
            assertEquals("", ant.getStyle());
        }

        @Test
        @DisplayName("style 链式返回 this")
        void styleChaining() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            FlowPaneAnt ret = ant.style("-fx-foo: bar");
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("多次 styleClass 不重复挂载（幂等）")
        void styleClassMultipleCallsIdempotent() {
            FlowPaneAnt ant = FlowPaneAnt.create();
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
            FlowPaneAnt ant = FlowPaneAnt.create().padding(12);
            assertEquals(new Insets(12), ant.getPadding());
        }

        @Test
        @DisplayName("padding(top,right,bottom,left) 各边不同")
        void paddingFourSides() {
            FlowPaneAnt ant = FlowPaneAnt.create().padding(1, 2, 3, 4);
            assertEquals(new Insets(1, 2, 3, 4), ant.getPadding());
        }

        @Test
        @DisplayName("padding(Insets) 设置对象")
        void paddingInsets() {
            FlowPaneAnt ant = FlowPaneAnt.create().padding(new Insets(5, 10, 15, 20));
            assertEquals(new Insets(5, 10, 15, 20), ant.getPadding());
        }

        @Test
        @DisplayName("padding((Insets) null) 安全不抛异常")
        void paddingNullSafe() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            assertDoesNotThrow(() -> ant.padding((Insets) null));
        }

        @Test
        @DisplayName("padding 链式返回 this")
        void paddingChaining() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            FlowPaneAnt ret = ant.padding(8);
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
            FlowPaneAnt ant = FlowPaneAnt.create()
                    .borderTop().borderBottom().borderLeft().borderRight();
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderTop(true) 挂载，borderTop(false) 不挂")
        void borderTopSwitch() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            ant.borderTop(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            ant.borderTop(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
        }

        @Test
        @DisplayName("borderBottom(true) 挂载")
        void borderBottomSwitch() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            ant.borderBottom(false);
            ant.borderBottom(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        }

        @Test
        @DisplayName("borderLeft(true) 挂载")
        void borderLeftSwitch() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            ant.borderLeft(false);
            ant.borderLeft(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
        }

        @Test
        @DisplayName("borderRight(true) 挂载")
        void borderRightSwitch() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            ant.borderRight(false);
            ant.borderRight(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderXxx 链式返回 this")
        void borderChaining() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            FlowPaneAnt ret = ant.borderTop();
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderTop(boolean) 链式返回 this")
        void borderSwitchChaining() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            FlowPaneAnt ret = ant.borderTop(true);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderXxx(false) 不挂载对应 styleClass")
        void borderSwitchOffDoesNothing() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            ant.borderTop(false).borderBottom(false).borderLeft(false).borderRight(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("多次 borderTop() 幂等不重复挂")
        void borderIdempotent() {
            FlowPaneAnt ant = FlowPaneAnt.create().borderTop().borderTop().borderTop();
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
            FlowPaneAnt ant = FlowPaneAnt.create()
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
            FlowPaneAnt ant = FlowPaneAnt.create()
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
            FlowPaneAnt ant = FlowPaneAnt.create();
            FlowPaneAnt ret = ant.maxW(100).prefSize(50, 60);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("尺寸 set 后再 set 能覆盖")
        void sizesOverridable() {
            FlowPaneAnt ant = FlowPaneAnt.create().maxW(100).maxW(200);
            assertEquals(200, ant.getMaxWidth(), 0.0);
        }

        @Test
        @DisplayName("prefSize(0,0) 合法不抛异常")
        void prefSizeZeroSafe() {
            FlowPaneAnt ant = FlowPaneAnt.create().prefSize(0, 0);
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
            FlowPaneAnt ant = FlowPaneAnt.create().visible(false);
            assertFalse(ant.isVisible());
        }

        @Test
        @DisplayName("disable 设置后能读取")
        void disableRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().disable(true);
            assertTrue(ant.isDisabled());
        }

        @Test
        @DisplayName("managed 设置后能读取")
        void managedRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().managed(false);
            assertFalse(ant.isManaged());
        }

        @Test
        @DisplayName("opacity 设置后能读取")
        void opacityRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().opacity(0.42);
            assertEquals(0.42, ant.getOpacity(), 0.001);
        }

        @Test
        @DisplayName("cursor 设置后能读取")
        void cursorRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().cursor(Cursor.HAND);
            assertEquals(Cursor.HAND, ant.getCursor());
        }

        @Test
        @DisplayName("id 设置后能读取")
        void idRoundTrip() {
            FlowPaneAnt ant = FlowPaneAnt.create().id("my-flowpane");
            assertEquals("my-flowpane", ant.getId());
        }

        @Test
        @DisplayName("id(null) 安全不抛异常（与原 FlowPaneAnt 行为一致）")
        void idNullSafe() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            assertDoesNotThrow(() -> ant.id(null));
        }

        @Test
        @DisplayName("cursor(null) 安全不抛异常（与原 FlowPaneAnt 行为一致）")
        void cursorNullSafe() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            assertDoesNotThrow(() -> ant.cursor(null));
        }

        @Test
        @DisplayName("NodeProps 链式返回 this")
        void nodePropsChaining() {
            FlowPaneAnt ant = FlowPaneAnt.create();
            FlowPaneAnt ret = ant.visible(true).id("x").opacity(1.0);
            assertSame(ant, ret);
        }
    }

    // ============================================================
    // Inheritance（业务继承 FlowPaneAnt 的核心场景）
    // ============================================================

    @Nested
    @DisplayName("业务继承")
    class Inheritance {

        /** 业务继承示例：模拟业务侧 extends FlowPaneAnt 写自定义 TagCloud。 */
        static class TagCloud extends FlowPaneAnt {
            TagCloud() {
                hgap(8);
                vgap(8);
                padding(16);
                background(Background.SUBTLE);
                id("my-tagcloud");
                borderBottom();
                children(new Label("Java"), new Label("Kotlin"), new Label("JavaFX"));
            }
        }

        @Test
        @DisplayName("业务继承后构造里 LayoutCommon default method 全部生效")
        void subclassInheritsAllDefaults() {
            TagCloud cloud = new TagCloud();
            assertEquals(8.0, cloud.getHgap(), 0.0);
            assertEquals(8.0, cloud.getVgap(), 0.0);
            assertEquals(new Insets(16), cloud.getPadding());
            assertTrue(cloud.getStyleClass().contains(Background.SUBTLE.styleClass()));
            assertEquals("my-tagcloud", cloud.getId());
            assertTrue(cloud.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals(3, cloud.getChildren().size());
        }

        @Test
        @DisplayName("业务继承后 instanceof LayoutCommon<FlowPaneAnt> 仍成立")
        void subclassIsLayoutCommon() {
            TagCloud cloud = new TagCloud();
            assertTrue(cloud instanceof LayoutCommon);
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
            FlowPaneAnt ant = FlowPaneAnt.create()
                    .hgap(8)
                    .vgap(12)
                    .orientation(Orientation.HORIZONTAL)
                    .align(Pos.CENTER)
                    .padding(16)
                    .background(Background.LAYOUT)
                    .borderTop()
                    .borderBottom(true)
                    .id("chain-flowpane")
                    .visible(true)
                    .managed(true)
                    .opacity(0.95)
                    .maxW(800)
                    .prefSize(400, 100)
                    .prefWrapLength(300)
                    .rowValignment(VPos.CENTER)
                    .columnHalignment(HPos.CENTER)
                    .children(a, b)
                    .margin(b, new Insets(5))
                    .build();
            assertNotNull(ant);
            assertEquals(8.0, ant.getHgap(), 0.0);
            assertEquals(12.0, ant.getVgap(), 0.0);
            assertEquals(Orientation.HORIZONTAL, ant.getOrientation());
            assertEquals(Pos.CENTER, ant.getAlignment());
            assertEquals(new Insets(16), ant.getPadding());
            assertTrue(ant.getStyleClass().contains(Background.LAYOUT.styleClass()));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals("chain-flowpane", ant.getId());
            assertTrue(ant.isVisible());
            assertTrue(ant.isManaged());
            assertEquals(0.95, ant.getOpacity(), 0.001);
            assertEquals(800, ant.getMaxWidth(), 0.0);
            assertEquals(400, ant.getPrefWidth(), 0.0);
            assertEquals(100, ant.getPrefHeight(), 0.0);
            assertEquals(300.0, ant.getPrefWrapLength(), 0.0);
            assertEquals(VPos.CENTER, ant.getRowValignment());
            assertEquals(HPos.CENTER, ant.getColumnHalignment());
            assertEquals(2, ant.getChildren().size());
            assertEquals(new Insets(5), FlowPane.getMargin(b));
        }
    }
}
