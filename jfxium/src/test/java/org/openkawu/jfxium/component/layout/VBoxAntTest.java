package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VBoxAnt 单元测试 —— 验证「VBoxAnt implements LayoutCommon<VBoxAnt>」重构后行为 100% 等价。
 *
 * <p>覆盖范围：</p>
 * <ul>
 *   <li>工厂创建 / 构造函数（4 个）/ build() 返回 this</li>
 *   <li>业务特定方法：spacing / align / children / fillWidth / vgrow / margin</li>
 *   <li>继承自 {@link LayoutCommon} 的所有 default methods：
 *       styleClass / style / background / padding / borderXxx /
 *       maxW/H/minW/H/prefW/H / prefSize/maxSize/minSize /
 *       visible / disable / managed / opacity / cursor / id</li>
 *   <li>业务继承链：子类继承 VBoxAnt 后流式 API 仍可用</li>
 * </ul>
 */
@DisplayName("VBoxAnt")
class VBoxAntTest extends JfxTestBase {

    // ============================================================
    // 工厂创建 & build()
    // ============================================================

    @Nested
    @DisplayName("工厂创建")
    class Factory {

        @Test
        @DisplayName("create() 返回非空 VBoxAnt")
        void create_returnsNonNull() {
            VBoxAnt box = VBoxAnt.create();
            assertNotNull(box);
            assertTrue(box.getChildren().isEmpty());
        }

        @Test
        @DisplayName("create() 产物是 VBox（保持 VBoxAnt extends VBox 兼容性）")
        void create_isVBox() {
            VBoxAnt box = VBoxAnt.create();
            assertTrue(box instanceof VBox,
                    "VBoxAnt 必须仍是 VBox，场景代码 container.getChildren().add(box) 才能编译");
        }

        @Test
        @DisplayName("create(Node...) 把子节点都挂上")
        void createWithChildren() {
            Label a = new Label("A");
            Label b = new Label("B");
            VBoxAnt box = VBoxAnt.create(a, b);
            assertEquals(2, box.getChildren().size());
            assertTrue(box.getChildren().contains(a));
            assertTrue(box.getChildren().contains(b));
        }

        @Test
        @DisplayName("create() 配合 build() 拿到自身引用（链式 API 终结）")
        void build_returnsThis() {
            VBoxAnt box = VBoxAnt.create();
            assertSame(box, box.build(),
                    "build() 必须返回 this —— 跟 VBoxBuilder.build() 风格对齐");
        }
    }

    // ============================================================
    // 构造函数（4 个）
    // ============================================================

    @Nested
    @DisplayName("构造函数")
    class Constructors {

        @Test
        @DisplayName("VBoxAnt() 默认无子节点")
        void noArg() {
            VBoxAnt box = new VBoxAnt();
            assertNotNull(box);
            assertTrue(box.getChildren().isEmpty());
        }

        @Test
        @DisplayName("VBoxAnt(Node...) 子节点")
        void varargsChildren() {
            Label a = new Label("a");
            Label b = new Label("b");
            VBoxAnt box = new VBoxAnt(a, b);
            assertEquals(2, box.getChildren().size());
        }

        @Test
        @DisplayName("VBoxAnt(double) 设置 spacing")
        void spacingOnly() {
            VBoxAnt box = new VBoxAnt(16);
            assertEquals(16, box.getSpacing(), 0.01);
        }

        @Test
        @DisplayName("负 spacing 构造钳制为 0")
        void negativeSpacingConstructorIsClamped() {
            VBoxAnt box = new VBoxAnt(-16);
            assertEquals(0, box.getSpacing(), 0.01);
        }

        @Test
        @DisplayName("VBoxAnt(double, Node...) spacing + 子节点")
        void spacingAndChildren() {
            Label a = new Label("a");
            VBoxAnt box = new VBoxAnt(8, a);
            assertEquals(8, box.getSpacing(), 0.01);
            assertEquals(1, box.getChildren().size());
            assertEquals(a, box.getChildren().get(0));
        }
    }

    // ============================================================
    // 业务特定方法
    // ============================================================

    @Nested
    @DisplayName("业务方法：spacing / align / children / fillWidth / vgrow / margin")
    class BusinessMethods {

        @Test
        @DisplayName("spacing(double) 设置 + 返回 this")
        void spacing() {
            VBoxAnt box = VBoxAnt.create();
            assertSame(box, box.spacing(12), "spacing 必须返回 this 保留链式");
            assertEquals(12, box.getSpacing(), 0.01);
        }

        @Test
        @DisplayName("负 spacing 钳制为 0")
        void negativeSpacingIsClamped() {
            VBoxAnt box = VBoxAnt.create().spacing(-12);
            assertEquals(0, box.getSpacing(), 0.01);
        }

        @Test
        @DisplayName("align(Pos) 设置对齐 + 返回 this")
        void align() {
            VBoxAnt box = VBoxAnt.create();
            assertSame(box, box.align(Pos.CENTER));
            assertEquals(Pos.CENTER, box.getAlignment());
        }

        @Test
        @DisplayName("align(Pos.TOP_LEFT) 等任意 Pos 都生效")
        void align_topleft() {
            VBoxAnt box = VBoxAnt.create().align(Pos.TOP_LEFT);
            assertEquals(Pos.TOP_LEFT, box.getAlignment());
        }

        @Test
        @DisplayName("children(Node...) 追加到末尾，保留旧子节点")
        void children_appends() {
            Label a = new Label("a");
            VBoxAnt box = VBoxAnt.create(a);
            Label b = new Label("b");
            box.children(b);
            assertEquals(2, box.getChildren().size());
            assertEquals(a, box.getChildren().get(0));
            assertEquals(b, box.getChildren().get(1));
        }

        @Test
        @DisplayName("children(null) 不抛异常")
        void children_nullSafe() {
            VBoxAnt box = VBoxAnt.create();
            assertDoesNotThrow(() -> box.children((Node[]) null));
        }

        @Test
        @DisplayName("children(...) 过滤 null 子节点（不挂 null）")
        void children_filtersNull() {
            Label a = new Label("a");
            VBoxAnt box = VBoxAnt.create().children(a, null, new Label("b"));
            assertEquals(2, box.getChildren().size(), "null 节点应被过滤");
            assertTrue(box.getChildren().contains(a));
        }

        @Test
        @DisplayName("fillWidth(boolean) 切换 fillWidth + 返回 this")
        void fillWidth() {
            VBoxAnt box = VBoxAnt.create();
            // VBox 默认 fillWidth = true
            assertTrue(box.isFillWidth());
            assertSame(box, box.fillWidth(false));
            assertFalse(box.isFillWidth());
            assertSame(box, box.fillWidth(true));
            assertTrue(box.isFillWidth());
        }

        @Test
        @DisplayName("vgrow(Node, Priority) 设置垂直拉伸优先级")
        void vgrow() {
            Label a = new Label("a");
            VBoxAnt box = VBoxAnt.create(a);
            box.vgrow(a, Priority.ALWAYS);
            assertEquals(Priority.ALWAYS, VBox.getVgrow(a));
        }

        @Test
        @DisplayName("margin(Node, Insets) 设置外边距")
        void margin() {
            Label a = new Label("a");
            VBoxAnt box = VBoxAnt.create(a);
            Insets m = new Insets(5, 10, 15, 20);
            box.margin(a, m);
            assertEquals(m, VBox.getMargin(a));
        }
    }

    // ============================================================
    // 继承自 LayoutCommon：styleClass / style / background
    // ============================================================

    @Nested
    @DisplayName("styleClass / style / background（继承自 LayoutCommon）")
    class StyleApi {

        @Test
        @DisplayName("styleClass(String) 幂等追加")
        void styleClass_idempotent() {
            VBoxAnt box = VBoxAnt.create();
            box.styleClass("jfx-cls-a");
            box.styleClass("jfx-cls-a");
            long count = box.getStyleClass().stream().filter("jfx-cls-a"::equals).count();
            assertEquals(1, count, "重复 styleClass 不应重复挂");
        }

        @Test
        @DisplayName("styleClass(String...) 批量追加")
        void styleClass_varargs() {
            VBoxAnt box = VBoxAnt.create();
            box.styleClass("jfx-a", "jfx-b", "jfx-c");
            assertTrue(box.getStyleClass().contains("jfx-a"));
            assertTrue(box.getStyleClass().contains("jfx-b"));
            assertTrue(box.getStyleClass().contains("jfx-c"));
        }

        @Test
        @DisplayName("styleClass(null) 不抛异常也不挂 null")
        void styleClass_nullSafe() {
            VBoxAnt box = VBoxAnt.create();
            assertDoesNotThrow(() -> box.styleClass((String) null));
            assertFalse(box.getStyleClass().contains(null));
        }

        @Test
        @DisplayName("styleClass(\"\") 空字符串被过滤")
        void styleClass_emptyFiltered() {
            VBoxAnt box = VBoxAnt.create();
            box.styleClass("");
            assertFalse(box.getStyleClass().contains(""));
        }

        @Test
        @DisplayName("styleClass 链式返回 SELF 类型（VBoxAnt）")
        void styleClass_returnsSelf() {
            VBoxAnt box = VBoxAnt.create();
            VBoxAnt ret = box.styleClass("jfx-x");
            assertSame(box, ret, "链式必须返回 this");
        }

        @Test
        @DisplayName("style(String) 设置 inline style")
        void style_setInline() {
            VBoxAnt box = VBoxAnt.create();
            box.style("-fx-background-color: red;");
            assertEquals("-fx-background-color: red;", box.getStyle());
        }

        @Test
        @DisplayName("style(null) 不抛异常也不改 style")
        void style_nullSafe() {
            VBoxAnt box = VBoxAnt.create().style("-fx-background-color: red;");
            box.style(null);
            assertEquals("-fx-background-color: red;", box.getStyle(),
                    "style(null) 不应清空已有 style");
        }

        @Test
        @DisplayName("background(Background.LAYOUT) 挂 jfx-bg-layout")
        void background_layout() {
            VBoxAnt box = VBoxAnt.create().background(Background.LAYOUT);
            assertTrue(box.getStyleClass().contains(JfxStyles.BG_LAYOUT));
        }

        @Test
        @DisplayName("background(Background.SUBTLE) 挂 jfx-bg-subtle")
        void background_subtle() {
            VBoxAnt box = VBoxAnt.create().background(Background.SUBTLE);
            assertTrue(box.getStyleClass().contains(JfxStyles.BG_SUBTLE));
        }

        @Test
        @DisplayName("background(null) 不抛异常也不挂任何 bg-*")
        void background_nullSafe() {
            VBoxAnt box = VBoxAnt.create();
            assertDoesNotThrow(() -> box.background(null));
            assertFalse(box.getStyleClass().stream().anyMatch(c -> c != null && c.startsWith("jfx-bg-")));
        }

        @Test
        @DisplayName("background 幂等：多次调同一枚举不重复挂")
        void background_idempotent() {
            VBoxAnt box = VBoxAnt.create()
                    .background(Background.LAYOUT)
                    .background(Background.LAYOUT);
            long count = box.getStyleClass().stream().filter(JfxStyles.BG_LAYOUT::equals).count();
            assertEquals(1, count);
        }
    }

    // ============================================================
    // 继承自 LayoutCommon：padding（3 重载）
    // ============================================================

    @Nested
    @DisplayName("padding（继承自 LayoutCommon）")
    class Padding {

        @Test
        @DisplayName("padding(double) 四边相同")
        void padding_uniform() {
            VBoxAnt box = VBoxAnt.create().padding(12);
            assertEquals(new Insets(12), box.getPadding());
        }

        @Test
        @DisplayName("padding(top, right, bottom, left) 四边独立")
        void padding_directional() {
            VBoxAnt box = VBoxAnt.create().padding(1, 2, 3, 4);
            assertEquals(1, box.getPadding().getTop(), 0.01);
            assertEquals(2, box.getPadding().getRight(), 0.01);
            assertEquals(3, box.getPadding().getBottom(), 0.01);
            assertEquals(4, box.getPadding().getLeft(), 0.01);
        }

        @Test
        @DisplayName("padding(Insets) Insets 对象")
        void padding_insets() {
            Insets ins = new Insets(5, 10, 15, 20);
            VBoxAnt box = VBoxAnt.create().padding(ins);
            assertEquals(ins, box.getPadding());
        }

        @Test
        @DisplayName("padding(Insets null) 不抛异常也不改 padding")
        void padding_nullSafe() {
            VBoxAnt box = VBoxAnt.create().padding(10);
            box.padding((Insets) null);
            assertEquals(new Insets(10), box.getPadding());
        }

        @Test
        @DisplayName("padding 链式返回 SELF")
        void padding_returnsSelf() {
            VBoxAnt box = VBoxAnt.create();
            assertSame(box, box.padding(5));
        }
    }

    // ============================================================
    // 继承自 LayoutCommon：borderXxx（8 个）
    // ============================================================

    @Nested
    @DisplayName("borderXxx（继承自 LayoutCommon）")
    class Borders {

        @Test
        @DisplayName("borderTop() 挂 jfx-border-top")
        void borderTop() {
            VBoxAnt box = VBoxAnt.create().borderTop();
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_TOP));
        }

        @Test
        @DisplayName("borderBottom() 挂 jfx-border-bottom")
        void borderBottom() {
            VBoxAnt box = VBoxAnt.create().borderBottom();
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        }

        @Test
        @DisplayName("borderLeft() 挂 jfx-border-left")
        void borderLeft() {
            VBoxAnt box = VBoxAnt.create().borderLeft();
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_LEFT));
        }

        @Test
        @DisplayName("borderRight() 挂 jfx-border-right")
        void borderRight() {
            VBoxAnt box = VBoxAnt.create().borderRight();
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("borderTop(false) 不挂")
        void borderTop_false() {
            VBoxAnt box = VBoxAnt.create().borderTop(false);
            assertFalse(box.getStyleClass().contains(JfxStyles.BORDER_TOP));
        }

        @Test
        @DisplayName("borderBottom(false) 不挂")
        void borderBottom_false() {
            VBoxAnt box = VBoxAnt.create().borderBottom(false);
            assertFalse(box.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        }

        @Test
        @DisplayName("borderLeft(false) 不挂")
        void borderLeft_false() {
            VBoxAnt box = VBoxAnt.create().borderLeft(false);
            assertFalse(box.getStyleClass().contains(JfxStyles.BORDER_LEFT));
        }

        @Test
        @DisplayName("borderRight(false) 不挂")
        void borderRight_false() {
            VBoxAnt box = VBoxAnt.create().borderRight(false);
            assertFalse(box.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }

        @Test
        @DisplayName("4 个 borderXxx() 同时挂互不影响")
        void allFourBorders() {
            VBoxAnt box = VBoxAnt.create()
                    .borderTop().borderBottom().borderLeft().borderRight();
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_LEFT));
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
        }
    }

    // ============================================================
    // 继承自 LayoutCommon：尺寸（6 + 3）
    // ============================================================

    @Nested
    @DisplayName("尺寸属性（继承自 LayoutCommon）")
    class Sizes {

        @Test
        @DisplayName("maxW / minW / prefW 设置宽度")
        void widths() {
            VBoxAnt box = VBoxAnt.create()
                    .maxW(500).minW(100).prefW(250);
            assertEquals(500, box.getMaxWidth(), 0.01);
            assertEquals(100, box.getMinWidth(), 0.01);
            assertEquals(250, box.getPrefWidth(), 0.01);
        }

        @Test
        @DisplayName("maxH / minH / prefH 设置高度")
        void heights() {
            VBoxAnt box = VBoxAnt.create()
                    .maxH(400).minH(50).prefH(200);
            assertEquals(400, box.getMaxHeight(), 0.01);
            assertEquals(50, box.getMinHeight(), 0.01);
            assertEquals(200, box.getPrefHeight(), 0.01);
        }

        @Test
        @DisplayName("prefSize(w, h) 同时设置首选宽高")
        void prefSize() {
            VBoxAnt box = VBoxAnt.create().prefSize(300, 200);
            assertEquals(300, box.getPrefWidth(), 0.01);
            assertEquals(200, box.getPrefHeight(), 0.01);
        }

        @Test
        @DisplayName("maxSize(w, h) 同时设置最大宽高")
        void maxSize() {
            VBoxAnt box = VBoxAnt.create().maxSize(600, 500);
            assertEquals(600, box.getMaxWidth(), 0.01);
            assertEquals(500, box.getMaxHeight(), 0.01);
        }

        @Test
        @DisplayName("minSize(w, h) 同时设置最小宽高")
        void minSize() {
            VBoxAnt box = VBoxAnt.create().minSize(80, 60);
            assertEquals(80, box.getMinWidth(), 0.01);
            assertEquals(60, box.getMinHeight(), 0.01);
        }
    }

    // ============================================================
    // 继承自 LayoutCommon：高频节点属性（6 个）
    // ============================================================

    @Nested
    @DisplayName("高频节点属性（继承自 LayoutCommon）")
    class NodeProps {

        @Test
        @DisplayName("visible(false) 隐藏")
        void visible_false() {
            VBoxAnt box = VBoxAnt.create().visible(false);
            assertFalse(box.isVisible());
        }

        @Test
        @DisplayName("visible(true) 显示")
        void visible_true() {
            VBoxAnt box = VBoxAnt.create();
            box.setVisible(false);
            box.visible(true);
            assertTrue(box.isVisible());
        }

        @Test
        @DisplayName("disable(true) 禁用")
        void disable_true() {
            VBoxAnt box = VBoxAnt.create().disable(true);
            assertTrue(box.isDisable());
        }

        @Test
        @DisplayName("disable(false) 启用")
        void disable_false() {
            VBoxAnt box = VBoxAnt.create().disable(true).disable(false);
            assertFalse(box.isDisable());
        }

        @Test
        @DisplayName("managed(false) 不受布局管理")
        void managed_false() {
            VBoxAnt box = VBoxAnt.create().managed(false);
            assertFalse(box.isManaged());
        }

        @Test
        @DisplayName("opacity(0.5) 半透明")
        void opacity() {
            VBoxAnt box = VBoxAnt.create().opacity(0.5);
            assertEquals(0.5, box.getOpacity(), 0.01);
        }

        @Test
        @DisplayName("cursor(Cursor.HAND) 设置鼠标光标")
        void cursor_hand() {
            VBoxAnt box = VBoxAnt.create().cursor(Cursor.HAND);
            assertEquals(Cursor.HAND, box.getCursor());
        }

        @Test
        @DisplayName("id(String) 设置节点 ID")
        void id_set() {
            VBoxAnt box = VBoxAnt.create().id("my-vbox");
            assertEquals("my-vbox", box.getId());
        }

        @Test
        @DisplayName("id 链式返回 SELF（VBoxAnt）")
        void id_returnsSelf() {
            VBoxAnt box = VBoxAnt.create();
            assertSame(box, box.id("x"));
        }
    }

    // ============================================================
    // 业务继承场景
    // ============================================================

    @Nested
    @DisplayName("业务继承场景")
    class Inheritance {

        /** 业务用法：class HomeView extends VBoxAnt —— 构造里直接用链式 API。 */
        static class HomeView extends VBoxAnt {
            HomeView() {
                spacing(16);
                padding(24);
                background(Background.LAYOUT);
                children(new Label("欢迎，alice"), new Label("dashboard"));
            }
        }

        @Test
        @DisplayName("业务继承 VBoxAnt，构造里调用继承自 LayoutCommon 的 API 全部生效")
        void subclass_constructorUsesInheritedApi() {
            HomeView view = new HomeView();
            assertEquals(16, view.getSpacing(), 0.01);
            assertEquals(new Insets(24), view.getPadding());
            assertTrue(view.getStyleClass().contains(JfxStyles.BG_LAYOUT));
            assertEquals(2, view.getChildren().size());
        }

        @Test
        @DisplayName("业务继承后，继续调 LayoutCommon default method（id/borderTop/opacity）")
        void subclass_postConstructChain() {
            HomeView view = new HomeView();
            VBoxAnt ret = view
                    .id("home-root")
                    .borderTop()
                    .borderBottom()
                    .opacity(0.9)
                    .cursor(Cursor.HAND)
                    .maxW(1200)
                    .align(Pos.CENTER);
            assertSame(view, ret, "链式必须仍返回 this（继承时类型一致）");
            assertEquals("home-root", view.getId());
            assertTrue(view.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(view.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertEquals(0.9, view.getOpacity(), 0.01);
            assertEquals(Cursor.HAND, view.getCursor());
            assertEquals(1200, view.getMaxWidth(), 0.01);
            assertEquals(Pos.CENTER, view.getAlignment());
        }
    }

    // ============================================================
    // 全部方法串联（业务 + LayoutCommon 混合链式）
    // ============================================================

    @Nested
    @DisplayName("全链路")
    class FullChain {

        @Test
        @DisplayName("业务方法 + 继承自 LayoutCommon 的方法全部串联不抛异常")
        void fullChain_noException() {
            VBoxAnt box = VBoxAnt.create(new Label("a"))
                    .spacing(16)
                    .padding(24)
                    .background(Background.LAYOUT)
                    .align(Pos.CENTER)
                    .fillWidth(true)
                    .maxW(1200).minW(200).prefW(800)
                    .maxH(900).minH(100).prefH(600)
                    .prefSize(800, 600)
                    .maxSize(1200, 900)
                    .minSize(200, 100)
                    .borderTop().borderBottom()
                    .visible(true)
                    .disable(false)
                    .managed(true)
                    .opacity(1.0)
                    .cursor(Cursor.DEFAULT)
                    .id("root")
                    .styleClass("jfx-cls-x", "jfx-cls-y")
                    .style("-fx-background-radius: 4;")
                    .children(new Label("b"), new Label("c"))
                    .build();

            assertNotNull(box);
            assertEquals(16, box.getSpacing(), 0.01);
            assertEquals(new Insets(24), box.getPadding());
            assertTrue(box.getStyleClass().contains(JfxStyles.BG_LAYOUT));
            assertEquals(Pos.CENTER, box.getAlignment());
            assertTrue(box.isFillWidth());
            assertEquals(1200, box.getMaxWidth(), 0.01);
            assertEquals(200, box.getMinWidth(), 0.01);
            assertEquals(800, box.getPrefWidth(), 0.01);
            assertEquals(900, box.getMaxHeight(), 0.01);
            assertEquals(100, box.getMinHeight(), 0.01);
            assertEquals(600, box.getPrefHeight(), 0.01);
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_TOP));
            assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
            assertTrue(box.isVisible());
            assertFalse(box.isDisable());
            assertTrue(box.isManaged());
            assertEquals(1.0, box.getOpacity(), 0.01);
            assertEquals(Cursor.DEFAULT, box.getCursor());
            assertEquals("root", box.getId());
            assertTrue(box.getStyleClass().contains("jfx-cls-x"));
            assertTrue(box.getStyleClass().contains("jfx-cls-y"));
            assertEquals("-fx-background-radius: 4;", box.getStyle());
            // 1 initial + 2 appended = 3
            assertEquals(3, box.getChildren().size());
        }
    }
}
