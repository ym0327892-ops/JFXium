package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HBoxAnt 单元测试 —— LayoutCommon<HBoxAnt> 接入后行为回归保护。
 *
 * <p>覆盖：Factory / Constructors / BusinessMethods(HBox 特有) /
 * StyleApi / Padding / Borders / Sizes / NodeProps / Inheritance / FullChain。
 * 业务继承 HBoxAnt 后构造里调用的所有 LayoutCommon default method
 * （padding / background / id / borderTop 等）必须生效，链式 API 必须返回子类类型。</p>
 */
@DisplayName("HBoxAnt 单元测试（接入 LayoutCommon<HBoxAnt>）")
class HBoxAntTest extends JfxTestBase {

    // ============================================================
    // Factory
    // ============================================================

    @Nested
    @DisplayName("工厂入口")
    class Factory {

        @Test
        @DisplayName("create() 返回非 null HBoxAnt 实例")
        void createReturnsInstance() {
            HBoxAnt ant = HBoxAnt.create();
            assertNotNull(ant);
            assertTrue(ant instanceof HBox, "HBoxAnt 必须是 HBox 子类");
            assertTrue(ant instanceof LayoutCommon, "HBoxAnt 必须实现 LayoutCommon");
        }

        @Test
        @DisplayName("create() 默认无子节点、styleClass 为空")
        void createEmptyDefaults() {
            HBoxAnt ant = HBoxAnt.create();
            assertEquals(0, ant.getChildren().size());
            assertTrue(ant.getStyleClass().isEmpty());
        }

        @Test
        @DisplayName("create(children...) 批量添加子节点")
        void createWithChildren() {
            Label a = new Label("A");
            Label b = new Label("B");
            HBoxAnt ant = HBoxAnt.create(a, b);
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
            HBoxAnt ant = new HBoxAnt();
            assertEquals(0, ant.getSpacing(), 0.0);
        }

        @Test
        @DisplayName("children 数组构造")
        void childrenConstructor() {
            Label a = new Label("A");
            HBoxAnt ant = new HBoxAnt(a);
            assertEquals(1, ant.getChildren().size());
        }

        @Test
        @DisplayName("spacing 构造")
        void spacingConstructor() {
            HBoxAnt ant = new HBoxAnt(12.5);
            assertEquals(12.5, ant.getSpacing(), 0.0);
        }

        @Test
        @DisplayName("负 spacing 构造钳制为 0")
        void negativeSpacingConstructorIsClamped() {
            HBoxAnt ant = new HBoxAnt(-12.5);
            assertEquals(0.0, ant.getSpacing(), 0.0);
        }

        @Test
        @DisplayName("spacing + children 构造")
        void spacingAndChildrenConstructor() {
            Label a = new Label("A");
            HBoxAnt ant = new HBoxAnt(8.0, a);
            assertEquals(8.0, ant.getSpacing(), 0.0);
            assertEquals(1, ant.getChildren().size());
        }
    }

    // ============================================================
    // BusinessMethods（HBox 特有）
    // ============================================================

    @Nested
    @DisplayName("业务方法（HBox 特有）")
    class BusinessMethods {

        @Test
        @DisplayName("spacing 设置后能读取")
        void spacingRoundTrip() {
            HBoxAnt ant = HBoxAnt.create().spacing(8);
            assertEquals(8.0, ant.getSpacing(), 0.0);
        }

        @Test
        @DisplayName("负 spacing 钳制为 0")
        void negativeSpacingIsClamped() {
            HBoxAnt ant = HBoxAnt.create().spacing(-8);
            assertEquals(0.0, ant.getSpacing(), 0.0);
        }

        @Test
        @DisplayName("align 设置后能读取")
        void alignRoundTrip() {
            HBoxAnt ant = HBoxAnt.create().align(Pos.CENTER_LEFT);
            assertEquals(Pos.CENTER_LEFT, ant.getAlignment());
        }

        @Test
        @DisplayName("children 批量添加，null 节点被过滤")
        void childrenBatchAdd() {
            Label a = new Label("A");
            Label b = new Label("B");
            HBoxAnt ant = HBoxAnt.create().children(a, null, b);
            assertEquals(2, ant.getChildren().size());
        }

        @Test
        @DisplayName("fillHeight 设置后能读取")
        void fillHeightRoundTrip() {
            HBoxAnt ant = HBoxAnt.create().fillHeight(false);
            assertFalse(ant.isFillHeight());
        }

        @Test
        @DisplayName("hgrow 给子节点设置拉伸优先级")
        void hgrowSetsPriority() {
            Label a = new Label("A");
            HBoxAnt ant = HBoxAnt.create().children(a).hgrow(a, Priority.ALWAYS);
            assertEquals(Priority.ALWAYS, HBox.getHgrow(a));
        }

        @Test
        @DisplayName("margin 给子节点设置外边距")
        void marginSetsInsets() {
            Label a = new Label("A");
            HBoxAnt ant = HBoxAnt.create().children(a).margin(a, new Insets(10));
            assertEquals(new Insets(10), HBox.getMargin(a));
        }

        @Test
        @DisplayName("业务方法链式调用返回 this")
        void businessChaining() {
            HBoxAnt ant = HBoxAnt.create();
            HBoxAnt ret = ant.spacing(8).align(Pos.CENTER).fillHeight(false);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("children(null) 安全，不抛异常")
        void childrenNullSafe() {
            HBoxAnt ant = HBoxAnt.create().children((javafx.scene.Node[]) null);
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
            HBoxAnt ant = HBoxAnt.create();
            HBoxAnt ret = ant.styleClass("foo");
            assertSame(ant, ret);
            assertTrue(ant.getStyleClass().contains("foo"));
            // 重复调用不再挂
            ant.styleClass("foo");
            assertEquals(1, ant.getStyleClass().stream().filter("foo"::equals).count());
        }

        @Test
        @DisplayName("styleClass(null)/styleClass(\"\") 安全")
        void styleClassNullOrEmptySafe() {
            HBoxAnt ant = HBoxAnt.create();
            ant.styleClass((String) null);
            ant.styleClass("");
            assertTrue(ant.getStyleClass().isEmpty());
        }

        @Test
        @DisplayName("styleClass(String...) 批量挂载")
        void styleClassVarargs() {
            HBoxAnt ant = HBoxAnt.create().styleClass("a", "b", "c");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b", "c")));
        }

        @Test
        @DisplayName("styleClass(String...) 含 null 安全")
        void styleClassVarargsNullSafe() {
            HBoxAnt ant = HBoxAnt.create().styleClass("a", null, "b");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b")));
        }

        @Test
        @DisplayName("styleClass((String[]) null) 安全")
        void styleClassVarargsArrayNullSafe() {
            HBoxAnt ant = HBoxAnt.create().styleClass((String[]) null);
            assertTrue(ant.getStyleClass().isEmpty());
        }

        @Test
        @DisplayName("background(Background) 挂对应 styleClass")
        void backgroundSetsStyleClass() {
            HBoxAnt ant = HBoxAnt.create().background(Background.SUBTLE);
            assertTrue(ant.getStyleClass().stream()
                    .anyMatch(Background.SUBTLE.styleClass()::equals));
        }

        @Test
        @DisplayName("background(null) 安全不挂任何 styleClass")
        void backgroundNullSafe() {
            HBoxAnt ant = HBoxAnt.create().background(null);
            assertTrue(ant.getStyleClass().isEmpty());
        }

        @Test
        @DisplayName("style(inline) 设置后能读取")
        void styleInlineRoundTrip() {
            HBoxAnt ant = HBoxAnt.create().style("-fx-opacity: 0.5");
            assertEquals("-fx-opacity: 0.5", ant.getStyle());
        }

        @Test
        @DisplayName("style(null) 安全不抛异常")
        void styleNullSafe() {
            HBoxAnt ant = HBoxAnt.create().style(null);
            assertEquals("", ant.getStyle());
        }

        @Test
        @DisplayName("style 链式返回 this")
        void styleChaining() {
            HBoxAnt ant = HBoxAnt.create();
            HBoxAnt ret = ant.style("-fx-foo: bar");
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("多次 styleClass 不重复挂载（幂等）")
        void styleClassMultipleCallsIdempotent() {
            HBoxAnt ant = HBoxAnt.create();
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
            HBoxAnt ant = HBoxAnt.create().padding(12);
            assertEquals(new Insets(12), ant.getPadding());
        }

        @Test
        @DisplayName("padding(top,right,bottom,left) 各边不同")
        void paddingFourSides() {
            HBoxAnt ant = HBoxAnt.create().padding(1, 2, 3, 4);
            assertEquals(new Insets(1, 2, 3, 4), ant.getPadding());
        }

        @Test
        @DisplayName("padding(Insets) 设置对象")
        void paddingInsets() {
            HBoxAnt ant = HBoxAnt.create().padding(new Insets(5, 10, 15, 20));
            assertEquals(new Insets(5, 10, 15, 20), ant.getPadding());
        }

        @Test
        @DisplayName("padding((Insets) null) 安全不抛异常")
        void paddingNullSafe() {
            HBoxAnt ant = HBoxAnt.create();
            assertDoesNotThrow(() -> ant.padding((Insets) null));
        }

        @Test
        @DisplayName("padding 链式返回 this")
        void paddingChaining() {
            HBoxAnt ant = HBoxAnt.create();
            HBoxAnt ret = ant.padding(8);
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
            HBoxAnt ant = HBoxAnt.create()
                    .borderTop().borderBottom().borderLeft().borderRight();
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderTop(true) 挂载，borderTop(false) 不挂")
        void borderTopSwitch() {
            HBoxAnt ant = HBoxAnt.create();
            ant.borderTop(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            ant.borderTop(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
        }

        @Test
        @DisplayName("borderBottom(true) 挂载")
        void borderBottomSwitch() {
            HBoxAnt ant = HBoxAnt.create();
            ant.borderBottom(false);
            ant.borderBottom(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        }

        @Test
        @DisplayName("borderLeft(true) 挂载")
        void borderLeftSwitch() {
            HBoxAnt ant = HBoxAnt.create();
            ant.borderLeft(false);
            ant.borderLeft(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
        }

        @Test
        @DisplayName("borderRight(true) 挂载")
        void borderRightSwitch() {
            HBoxAnt ant = HBoxAnt.create();
            ant.borderRight(false);
            ant.borderRight(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderXxx 链式返回 this")
        void borderChaining() {
            HBoxAnt ant = HBoxAnt.create();
            HBoxAnt ret = ant.borderTop();
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderTop(boolean) 链式返回 this")
        void borderSwitchChaining() {
            HBoxAnt ant = HBoxAnt.create();
            HBoxAnt ret = ant.borderTop(true);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderXxx(false) 不挂载对应 styleClass")
        void borderSwitchOffDoesNothing() {
            HBoxAnt ant = HBoxAnt.create();
            ant.borderTop(false).borderBottom(false).borderLeft(false).borderRight(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("多次 borderTop() 幂等不重复挂")
        void borderIdempotent() {
            HBoxAnt ant = HBoxAnt.create().borderTop().borderTop().borderTop();
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
            HBoxAnt ant = HBoxAnt.create()
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
            HBoxAnt ant = HBoxAnt.create()
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
            HBoxAnt ant = HBoxAnt.create();
            HBoxAnt ret = ant.maxW(100).prefSize(50, 60);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("尺寸 set 后再 set 能覆盖")
        void sizesOverridable() {
            HBoxAnt ant = HBoxAnt.create().maxW(100).maxW(200);
            assertEquals(200, ant.getMaxWidth(), 0.0);
        }

        @Test
        @DisplayName("prefSize(0,0) 合法不抛异常")
        void prefSizeZeroSafe() {
            HBoxAnt ant = HBoxAnt.create().prefSize(0, 0);
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
            HBoxAnt ant = HBoxAnt.create().visible(false);
            assertFalse(ant.isVisible());
        }

        @Test
        @DisplayName("disable 设置后能读取")
        void disableRoundTrip() {
            HBoxAnt ant = HBoxAnt.create().disable(true);
            assertTrue(ant.isDisabled());
        }

        @Test
        @DisplayName("managed 设置后能读取")
        void managedRoundTrip() {
            HBoxAnt ant = HBoxAnt.create().managed(false);
            assertFalse(ant.isManaged());
        }

        @Test
        @DisplayName("opacity 设置后能读取")
        void opacityRoundTrip() {
            HBoxAnt ant = HBoxAnt.create().opacity(0.42);
            assertEquals(0.42, ant.getOpacity(), 0.001);
        }

        @Test
        @DisplayName("cursor 设置后能读取")
        void cursorRoundTrip() {
            HBoxAnt ant = HBoxAnt.create().cursor(Cursor.HAND);
            assertEquals(Cursor.HAND, ant.getCursor());
        }

        @Test
        @DisplayName("id 设置后能读取")
        void idRoundTrip() {
            HBoxAnt ant = HBoxAnt.create().id("my-hbox");
            assertEquals("my-hbox", ant.getId());
        }

        @Test
        @DisplayName("id(null) 安全不抛异常（与原 HBoxAnt 行为一致）")
        void idNullSafe() {
            HBoxAnt ant = HBoxAnt.create();
            assertDoesNotThrow(() -> ant.id(null));
        }

        @Test
        @DisplayName("cursor(null) 安全不抛异常（与原 HBoxAnt 行为一致）")
        void cursorNullSafe() {
            HBoxAnt ant = HBoxAnt.create();
            assertDoesNotThrow(() -> ant.cursor(null));
        }

        @Test
        @DisplayName("NodeProps 链式返回 this")
        void nodePropsChaining() {
            HBoxAnt ant = HBoxAnt.create();
            HBoxAnt ret = ant.visible(true).id("x").opacity(1.0);
            assertSame(ant, ret);
        }
    }

    // ============================================================
    // Inheritance（业务继承 HBoxAnt 的核心场景）
    // ============================================================

    @Nested
    @DisplayName("业务继承")
    class Inheritance {

        /** 业务继承示例：模拟业务侧 extends HBoxAnt 写自定义 ToolBar。 */
        static class ToolBar extends HBoxAnt {
            ToolBar() {
                spacing(8);
                padding(8, 16, 8, 16);
                align(Pos.CENTER_LEFT);
                background(Background.SUBTLE);
                id("my-toolbar");
                borderBottom();
                children(new Label("A"), new Label("B"));
            }
        }

        @Test
        @DisplayName("业务继承后构造里 LayoutCommon default method 全部生效")
        void subclassInheritsAllDefaults() {
            ToolBar bar = new ToolBar();
            assertEquals(8.0, bar.getSpacing(), 0.0);
            assertEquals(Pos.CENTER_LEFT, bar.getAlignment());
            assertEquals(new Insets(8, 16, 8, 16), bar.getPadding());
            assertTrue(bar.getStyleClass().contains(Background.SUBTLE.styleClass()));
            assertEquals("my-toolbar", bar.getId());
            assertTrue(bar.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals(2, bar.getChildren().size());
        }

        @Test
        @DisplayName("业务继承后 instanceof LayoutCommon<HBoxAnt> 仍成立")
        void subclassIsLayoutCommon() {
            ToolBar bar = new ToolBar();
            assertTrue(bar instanceof LayoutCommon);
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
            HBoxAnt ant = HBoxAnt.create()
                    .spacing(8)
                    .align(Pos.CENTER_LEFT)
                    .padding(12)
                    .background(Background.LAYOUT)
                    .borderTop()
                    .borderBottom(true)
                    .id("chain-hbox")
                    .visible(true)
                    .managed(true)
                    .opacity(0.95)
                    .maxW(800)
                    .prefSize(400, 100)
                    .fillHeight(true)
                    .children(a, b)
                    .hgrow(a, Priority.ALWAYS)
                    .margin(b, new Insets(5))
                    .build();
            assertNotNull(ant);
            assertEquals(8.0, ant.getSpacing(), 0.0);
            assertEquals(Pos.CENTER_LEFT, ant.getAlignment());
            assertEquals(new Insets(12), ant.getPadding());
            assertTrue(ant.getStyleClass().contains(Background.LAYOUT.styleClass()));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals("chain-hbox", ant.getId());
            assertTrue(ant.isVisible());
            assertTrue(ant.isManaged());
            assertEquals(0.95, ant.getOpacity(), 0.001);
            assertEquals(800, ant.getMaxWidth(), 0.0);
            assertEquals(400, ant.getPrefWidth(), 0.0);
            assertEquals(100, ant.getPrefHeight(), 0.0);
            assertTrue(ant.isFillHeight());
            assertEquals(2, ant.getChildren().size());
            assertEquals(Priority.ALWAYS, HBox.getHgrow(a));
            assertEquals(new Insets(5), HBox.getMargin(b));
        }
    }
}
