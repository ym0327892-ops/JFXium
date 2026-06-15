package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AnchorPaneAnt 单元测试 —— LayoutCommon<AnchorPaneAnt> 接入后行为回归保护。
 *
 * <p>覆盖：Factory / Constructors / BusinessMethods(AnchorPane 特有) /
 * StyleApi / Padding / Borders / Sizes / NodeProps / Inheritance / FullChain。
 * 业务继承 AnchorPaneAnt 后构造里调用的所有 LayoutCommon default method
 * （padding / background / id / borderTop 等）必须生效，链式 API 必须返回子类类型。</p>
 */
@DisplayName("AnchorPaneAnt 单元测试（接入 LayoutCommon<AnchorPaneAnt>）")
class AnchorPaneAntTest extends JfxTestBase {

    // ============================================================
    // Factory
    // ============================================================

    @Nested
    @DisplayName("工厂入口")
    class Factory {

        @Test
        @DisplayName("create() 返回非 null AnchorPaneAnt 实例")
        void createReturnsInstance() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            assertNotNull(ant);
            assertTrue(ant instanceof AnchorPane, "AnchorPaneAnt 必须是 AnchorPane 子类");
            assertTrue(ant instanceof LayoutCommon, "AnchorPaneAnt 必须实现 LayoutCommon");
        }

        @Test
        @DisplayName("create() 默认带 ANCHOR_PANE styleClass")
        void createEmptyDefaults() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            assertEquals(0, ant.getChildren().size());
            // 构造里强制加了 ANCHOR_PANE
            assertTrue(ant.getStyleClass().contains(JfxStyles.ANCHOR_PANE));
        }

        @Test
        @DisplayName("create() 默认无子节点")
        void createNoChildren() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
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
        @DisplayName("无参构造（自动挂 ANCHOR_PANE）")
        void noArg() {
            AnchorPaneAnt ant = new AnchorPaneAnt();
            assertEquals(0, ant.getChildren().size());
            assertTrue(ant.getStyleClass().contains(JfxStyles.ANCHOR_PANE));
        }
    }

    // ============================================================
    // BusinessMethods（AnchorPane 特有）
    // ============================================================

    @Nested
    @DisplayName("业务方法（AnchorPane 特有）")
    class BusinessMethods {

        @Test
        @DisplayName("anchor 4 边锚定后能读取")
        void anchorFourSides() {
            Label node = new Label("X");
            AnchorPaneAnt ant = AnchorPaneAnt.create().anchor(node, 10.0, 20.0, 30.0, 40.0);
            assertEquals(10.0, AnchorPane.getTopAnchor(node), 0.0);
            assertEquals(20.0, AnchorPane.getRightAnchor(node), 0.0);
            assertEquals(30.0, AnchorPane.getBottomAnchor(node), 0.0);
            assertEquals(40.0, AnchorPane.getLeftAnchor(node), 0.0);
        }

        @Test
        @DisplayName("anchor 含 null 边跳过该边")
        void anchorPartialNullSafe() {
            Label node = new Label("X");
            AnchorPaneAnt ant = AnchorPaneAnt.create().anchor(node, 10.0, null, null, 40.0);
            assertEquals(10.0, AnchorPane.getTopAnchor(node), 0.0);
            assertNull(AnchorPane.getRightAnchor(node));
            assertNull(AnchorPane.getBottomAnchor(node));
            assertEquals(40.0, AnchorPane.getLeftAnchor(node), 0.0);
        }

        @Test
        @DisplayName("anchor(null node) 安全不抛异常")
        void anchorNullNodeSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            assertDoesNotThrow(() -> ant.anchor(null, 10.0, 20.0, 30.0, 40.0));
        }

        @Test
        @DisplayName("topAnchor 设置后能读取")
        void topAnchorRoundTrip() {
            Label node = new Label("X");
            AnchorPaneAnt ant = AnchorPaneAnt.create().topAnchor(node, 5.0);
            assertEquals(5.0, AnchorPane.getTopAnchor(node), 0.0);
        }

        @Test
        @DisplayName("bottomAnchor 设置后能读取")
        void bottomAnchorRoundTrip() {
            Label node = new Label("X");
            AnchorPaneAnt ant = AnchorPaneAnt.create().bottomAnchor(node, 7.0);
            assertEquals(7.0, AnchorPane.getBottomAnchor(node), 0.0);
        }

        @Test
        @DisplayName("leftAnchor 设置后能读取")
        void leftAnchorRoundTrip() {
            Label node = new Label("X");
            AnchorPaneAnt ant = AnchorPaneAnt.create().leftAnchor(node, 11.0);
            assertEquals(11.0, AnchorPane.getLeftAnchor(node), 0.0);
        }

        @Test
        @DisplayName("rightAnchor 设置后能读取")
        void rightAnchorRoundTrip() {
            Label node = new Label("X");
            AnchorPaneAnt ant = AnchorPaneAnt.create().rightAnchor(node, 13.0);
            assertEquals(13.0, AnchorPane.getRightAnchor(node), 0.0);
        }

        @Test
        @DisplayName("center 清除四边锚点，改为运行时居中定位")
        void centerClearsAnchors() {
            Label node = new Label("X");
            AnchorPaneAnt ant = AnchorPaneAnt.create().center(node);
            assertNull(AnchorPane.getTopAnchor(node));
            assertNull(AnchorPane.getRightAnchor(node));
            assertNull(AnchorPane.getBottomAnchor(node));
            assertNull(AnchorPane.getLeftAnchor(node));
        }

        @Test
        @DisplayName("fill 等价 center")
        void fillEqualsCenter() {
            Label node = new Label("X");
            AnchorPaneAnt ant = AnchorPaneAnt.create().fill(node);
            assertEquals(0.0, AnchorPane.getTopAnchor(node), 0.0);
            assertEquals(0.0, AnchorPane.getRightAnchor(node), 0.0);
            assertEquals(0.0, AnchorPane.getBottomAnchor(node), 0.0);
            assertEquals(0.0, AnchorPane.getLeftAnchor(node), 0.0);
        }

        @Test
        @DisplayName("center 后再 children 也会在节点入树后补做一次定位")
        void centerBeforeChildrenStillUpdatesPosition() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            javafx.scene.layout.Region node = new javafx.scene.layout.Region();
            node.setPrefSize(40, 20);

            runOnFxThreadAndWait(() -> {
                ant.resize(200, 100);
                ant.center(node);
                ant.children(node);
            });
            pumpFxEvents();

            runOnFxThreadAndWait(() -> {
                assertEquals(80.0, node.getLayoutX(), 0.001);
                assertEquals(40.0, node.getLayoutY(), 0.001);
            });
        }

        @Test
        @DisplayName("children 批量添加")
        void childrenBatchAdd() {
            Label a = new Label("A");
            Label b = new Label("B");
            AnchorPaneAnt ant = AnchorPaneAnt.create().children(a, b);
            assertEquals(2, ant.getChildren().size());
        }

        @Test
        @DisplayName("children(null) 安全不抛异常（用 addAll，children((Node[]) null)）")
        void childrenNullArraySafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            assertDoesNotThrow(() -> AnchorPaneAnt.create().children((javafx.scene.Node[]) null));
            assertEquals(0, ant.getChildren().size());
        }

        @Test
        @DisplayName("add(null) 安全不抛异常")
        void addNullSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            assertDoesNotThrow(() -> ant.add(null));
            assertEquals(0, ant.getChildren().size());
        }

        @Test
        @DisplayName("add(node) 添加单个子节点")
        void addAddsNode() {
            Label a = new Label("A");
            AnchorPaneAnt ant = AnchorPaneAnt.create().add(a);
            assertEquals(1, ant.getChildren().size());
            assertTrue(ant.getChildren().contains(a));
        }

        @Test
        @DisplayName("业务方法链式调用返回 this")
        void businessChaining() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            Label a = new Label("A");
            AnchorPaneAnt ret = ant.add(a).topAnchor(a, 5.0).center(a).fill(a);
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
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            AnchorPaneAnt ret = ant.styleClass("foo");
            assertSame(ant, ret);
            assertTrue(ant.getStyleClass().contains("foo"));
            ant.styleClass("foo");
            assertEquals(1, ant.getStyleClass().stream().filter("foo"::equals).count());
        }

        @Test
        @DisplayName("styleClass(null)/styleClass(\"\") 安全")
        void styleClassNullOrEmptySafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            ant.styleClass((String) null);
            ant.styleClass("");
            assertTrue(ant.getStyleClass().contains(JfxStyles.ANCHOR_PANE));
            assertFalse(ant.getStyleClass().contains("foo"));
            assertFalse(ant.getStyleClass().contains(""));
        }

        @Test
        @DisplayName("styleClass(String...) 批量挂载")
        void styleClassVarargs() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().styleClass("a", "b", "c");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b", "c")));
        }

        @Test
        @DisplayName("styleClass(String...) 含 null 安全")
        void styleClassVarargsNullSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().styleClass("a", null, "b");
            assertTrue(ant.getStyleClass().containsAll(java.util.List.of("a", "b")));
        }

        @Test
        @DisplayName("styleClass((String[]) null) 安全")
        void styleClassVarargsArrayNullSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().styleClass((String[]) null);
            assertTrue(ant.getStyleClass().contains(JfxStyles.ANCHOR_PANE));
        }

        @Test
        @DisplayName("background(Background) 挂对应 styleClass")
        void backgroundSetsStyleClass() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().background(Background.SUBTLE);
            assertTrue(ant.getStyleClass().stream()
                    .anyMatch(Background.SUBTLE.styleClass()::equals));
        }

        @Test
        @DisplayName("background(null) 安全不挂 SUBTLE styleClass")
        void backgroundNullSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().background(null);
            assertTrue(ant.getStyleClass().contains(JfxStyles.ANCHOR_PANE));
            assertFalse(ant.getStyleClass().stream()
                    .anyMatch(Background.SUBTLE.styleClass()::equals));
        }

        @Test
        @DisplayName("style(inline) 设置后能读取")
        void styleInlineRoundTrip() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().style("-fx-opacity: 0.5");
            assertEquals("-fx-opacity: 0.5", ant.getStyle());
        }

        @Test
        @DisplayName("style(null) 安全不抛异常")
        void styleNullSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().style(null);
            assertEquals("", ant.getStyle());
        }

        @Test
        @DisplayName("style 链式返回 this")
        void styleChaining() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            AnchorPaneAnt ret = ant.style("-fx-foo: bar");
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("多次 styleClass 不重复挂载（幂等）")
        void styleClassMultipleCallsIdempotent() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
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
            AnchorPaneAnt ant = AnchorPaneAnt.create().padding(12);
            assertEquals(new Insets(12), ant.getPadding());
        }

        @Test
        @DisplayName("padding(top,right,bottom,left) 各边不同")
        void paddingFourSides() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().padding(1, 2, 3, 4);
            assertEquals(new Insets(1, 2, 3, 4), ant.getPadding());
        }

        @Test
        @DisplayName("padding(Insets) 设置对象")
        void paddingInsets() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().padding(new Insets(5, 10, 15, 20));
            assertEquals(new Insets(5, 10, 15, 20), ant.getPadding());
        }

        @Test
        @DisplayName("padding((Insets) null) 安全不抛异常")
        void paddingNullSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            assertDoesNotThrow(() -> ant.padding((Insets) null));
        }

        @Test
        @DisplayName("padding 链式返回 this")
        void paddingChaining() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            AnchorPaneAnt ret = ant.padding(8);
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
            AnchorPaneAnt ant = AnchorPaneAnt.create()
                    .borderTop().borderBottom().borderLeft().borderRight();
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderTop(true) 挂载，borderTop(false) 不挂")
        void borderTopSwitch() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            ant.borderTop(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            ant.borderTop(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
        }

        @Test
        @DisplayName("borderBottom(true) 挂载")
        void borderBottomSwitch() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            ant.borderBottom(false);
            ant.borderBottom(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        }

        @Test
        @DisplayName("borderLeft(true) 挂载")
        void borderLeftSwitch() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            ant.borderLeft(false);
            ant.borderLeft(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
        }

        @Test
        @DisplayName("borderRight(true) 挂载")
        void borderRightSwitch() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            ant.borderRight(false);
            ant.borderRight(true);
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderXxx 链式返回 this")
        void borderChaining() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            AnchorPaneAnt ret = ant.borderTop();
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderTop(boolean) 链式返回 this")
        void borderSwitchChaining() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            AnchorPaneAnt ret = ant.borderTop(true);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("borderXxx(false) 不挂载对应 styleClass")
        void borderSwitchOffDoesNothing() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            ant.borderTop(false).borderBottom(false).borderLeft(false).borderRight(false);
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertFalse(ant.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("多次 borderTop() 幂等不重复挂")
        void borderIdempotent() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().borderTop().borderTop().borderTop();
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
            AnchorPaneAnt ant = AnchorPaneAnt.create()
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
            AnchorPaneAnt ant = AnchorPaneAnt.create()
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
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            AnchorPaneAnt ret = ant.maxW(100).prefSize(50, 60);
            assertSame(ant, ret);
        }

        @Test
        @DisplayName("尺寸 set 后再 set 能覆盖")
        void sizesOverridable() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().maxW(100).maxW(200);
            assertEquals(200, ant.getMaxWidth(), 0.0);
        }

        @Test
        @DisplayName("prefSize(0,0) 合法不抛异常")
        void prefSizeZeroSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().prefSize(0, 0);
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
            AnchorPaneAnt ant = AnchorPaneAnt.create().visible(false);
            assertFalse(ant.isVisible());
        }

        @Test
        @DisplayName("disable 设置后能读取")
        void disableRoundTrip() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().disable(true);
            assertTrue(ant.isDisabled());
        }

        @Test
        @DisplayName("managed 设置后能读取")
        void managedRoundTrip() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().managed(false);
            assertFalse(ant.isManaged());
        }

        @Test
        @DisplayName("opacity 设置后能读取")
        void opacityRoundTrip() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().opacity(0.42);
            assertEquals(0.42, ant.getOpacity(), 0.001);
        }

        @Test
        @DisplayName("cursor 设置后能读取")
        void cursorRoundTrip() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().cursor(Cursor.HAND);
            assertEquals(Cursor.HAND, ant.getCursor());
        }

        @Test
        @DisplayName("id 设置后能读取")
        void idRoundTrip() {
            AnchorPaneAnt ant = AnchorPaneAnt.create().id("my-anchorpane");
            assertEquals("my-anchorpane", ant.getId());
        }

        @Test
        @DisplayName("id(null) 安全不抛异常（与原 AnchorPaneAnt 行为一致）")
        void idNullSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            assertDoesNotThrow(() -> ant.id(null));
        }

        @Test
        @DisplayName("cursor(null) 安全不抛异常（与原 AnchorPaneAnt 行为一致）")
        void cursorNullSafe() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            assertDoesNotThrow(() -> ant.cursor(null));
        }

        @Test
        @DisplayName("NodeProps 链式返回 this")
        void nodePropsChaining() {
            AnchorPaneAnt ant = AnchorPaneAnt.create();
            AnchorPaneAnt ret = ant.visible(true).id("x").opacity(1.0);
            assertSame(ant, ret);
        }
    }

    // ============================================================
    // Inheritance（业务继承 AnchorPaneAnt 的核心场景）
    // ============================================================

    @Nested
    @DisplayName("业务继承")
    class Inheritance {

        /** 业务继承示例：模拟业务侧 extends AnchorPaneAnt 写 dashboard。 */
        static class Dashboard extends AnchorPaneAnt {
            Dashboard() {
                padding(8);
                background(Background.SUBTLE);
                id("dashboard");
                borderBottom();
                add(new Label("A"));
                add(new Label("B"));
            }
        }

        @Test
        @DisplayName("业务继承后构造里 LayoutCommon default method 全部生效")
        void subclassInheritsAllDefaults() {
            Dashboard d = new Dashboard();
            assertEquals(new Insets(8), d.getPadding());
            assertTrue(d.getStyleClass().contains(Background.SUBTLE.styleClass()));
            assertEquals("dashboard", d.getId());
            assertTrue(d.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals(2, d.getChildren().size());
        }

        @Test
        @DisplayName("业务继承后 instanceof LayoutCommon<AnchorPaneAnt> 仍成立")
        void subclassIsLayoutCommon() {
            Dashboard d = new Dashboard();
            assertTrue(d instanceof LayoutCommon);
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
            AnchorPaneAnt ant = AnchorPaneAnt.create()
                    .children(a, b)
                    .topAnchor(a, 5.0)
                    .leftAnchor(a, 10.0)
                    .rightAnchor(b, 20.0)
                    .bottomAnchor(b, 30.0)
                    .padding(16)
                    .background(Background.LAYOUT)
                    .borderTop()
                    .borderBottom(true)
                    .id("chain-anchorpane")
                    .visible(true)
                    .managed(true)
                    .opacity(0.95)
                    .maxW(800)
                    .prefSize(400, 100)
                    .build();
            assertNotNull(ant);
            assertEquals(5.0, AnchorPane.getTopAnchor(a), 0.0);
            assertEquals(10.0, AnchorPane.getLeftAnchor(a), 0.0);
            assertEquals(20.0, AnchorPane.getRightAnchor(b), 0.0);
            assertEquals(30.0, AnchorPane.getBottomAnchor(b), 0.0);
            assertEquals(new Insets(16), ant.getPadding());
            assertTrue(ant.getStyleClass().contains(Background.LAYOUT.styleClass()));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(ant.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals("chain-anchorpane", ant.getId());
            assertTrue(ant.isVisible());
            assertTrue(ant.isManaged());
            assertEquals(0.95, ant.getOpacity(), 0.001);
            assertEquals(800, ant.getMaxWidth(), 0.0);
            assertEquals(400, ant.getPrefWidth(), 0.0);
            assertEquals(100, ant.getPrefHeight(), 0.0);
            assertEquals(2, ant.getChildren().size());
        }
    }
}
