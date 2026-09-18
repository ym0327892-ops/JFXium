package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BreadcrumbAnt 单元测试 —— 覆盖 item/href/onClick/separator/最后项高亮/AbstractStyleBuilder 继承。
 */
@DisplayName("BreadcrumbAnt")
class BreadcrumbAntTest extends JfxTestBase {

    @Test
    @DisplayName("空 build() 返回 HBox + 根 BREADCRUMB 修饰类")
    void build_empty() {
        HBox bc = BreadcrumbAnt.create().build();
        assertNotNull(bc);
        assertTrue(bc.getStyleClass().contains(JfxStyles.BREADCRUMB));
    }

    @Test
    @DisplayName("item(title) 单个文本项 = 普通 BREADCRUMB_ITEM + BREADCRUMB_LAST")
    void item_single() {
        HBox bc = BreadcrumbAnt.create().item("首页").build();
        // 单项:既是普通项,也是最后项
        Node node = bc.getChildren().get(0);
        assertTrue(node.getStyleClass().contains(JfxStyles.BREADCRUMB_ITEM));
        assertTrue(node.getStyleClass().contains(JfxStyles.BREADCRUMB_LAST));
        assertInstanceOf(Label.class, node);
        assertEquals("首页", ((Label) node).getText());
    }

    @Test
    @DisplayName("items(a,b,c) 三项:前两项普通,最后一项高亮 + 默认分隔符 /")
    void items_three() {
        HBox bc = BreadcrumbAnt.create().items("首页", "用户管理", "张三").build();
        // 3 items + 2 separators = 5 children
        assertEquals(5, bc.getChildren().size());

        // 第一项:Label + BREADCRUMB_ITEM
        Node first = bc.getChildren().get(0);
        assertTrue(first.getStyleClass().contains(JfxStyles.BREADCRUMB_ITEM));
        assertFalse(first.getStyleClass().contains(JfxStyles.BREADCRUMB_LAST));
        assertInstanceOf(Label.class, first);

        // 第一分隔符:Label + BREADCRUMB_SEPARATOR + "/"
        Node sep1 = bc.getChildren().get(1);
        assertTrue(sep1.getStyleClass().contains(JfxStyles.BREADCRUMB_SEPARATOR));
        assertEquals("/", ((Label) sep1).getText());

        // 第三项:Label + BREADCRUMB_ITEM + BREADCRUMB_LAST
        Node last = bc.getChildren().get(4);
        assertTrue(last.getStyleClass().contains(JfxStyles.BREADCRUMB_ITEM));
        assertTrue(last.getStyleClass().contains(JfxStyles.BREADCRUMB_LAST));
    }

    @Test
    @DisplayName("item(title, href) 是 Hyperlink + BREADCRUMB_LINK 修饰类")
    void item_withHref() {
        HBox bc = BreadcrumbAnt.create()
                .item("首页", "/home")
                .item("当前页")
                .build();
        Node link = bc.getChildren().get(0);
        assertInstanceOf(Hyperlink.class, link);
        assertTrue(link.getStyleClass().contains(JfxStyles.BREADCRUMB_LINK));
        assertEquals("首页", ((Hyperlink) link).getText());
    }

    @Test
    @DisplayName("item(title, onClick) 是 Hyperlink 且 onAction 触发回调")
    void item_withOnClick() {
        AtomicReference<BreadcrumbAnt.Item> captured = new AtomicReference<>();
        HBox bc = BreadcrumbAnt.create()
                .item("点击我", item -> captured.set(item))
                .item("当前页")
                .build();
        Node link = bc.getChildren().get(0);
        assertInstanceOf(Hyperlink.class, link);

        // 触发 Hyperlink 的 onAction
        ((Hyperlink) link).fire();
        assertNotNull(captured.get());
        assertEquals("点击我", captured.get().title);
    }

    @Test
    @DisplayName("separator(\">\") 自定义分隔符文本")
    void separator_custom() {
        HBox bc = BreadcrumbAnt.create()
                .separator(">")
                .item("a")
                .item("b")
                .build();
        // 2 items + 1 separator
        Node sep = bc.getChildren().get(1);
        assertTrue(sep.getStyleClass().contains(JfxStyles.BREADCRUMB_SEPARATOR));
        assertEquals(">", ((Label) sep).getText());
    }

    // ---------- AbstractStyleBuilder 继承 ----------

    @Test
    @DisplayName("styleClass 追加到根容器")
    void styleClass_applied() {
        HBox bc = BreadcrumbAnt.create()
                .item("a")
                .styleClass("my-breadcrumb")
                .build();
        assertTrue(bc.getStyleClass().contains("my-breadcrumb"));
        assertTrue(bc.getStyleClass().contains(JfxStyles.BREADCRUMB));
    }

    @Test
    @DisplayName("padding 应用到根容器")
    void padding_applied() {
        HBox bc = BreadcrumbAnt.create()
                .item("a")
                .padding(8)
                .build();
        assertEquals(8, bc.getPadding().getTop());
        assertEquals(8, bc.getPadding().getRight());
        assertEquals(8, bc.getPadding().getBottom());
        assertEquals(8, bc.getPadding().getLeft());
    }

    @Test
    @DisplayName("prefHeight 应用到根容器")
    void prefHeight_applied() {
        HBox bc = BreadcrumbAnt.create()
                .item("a")
                .prefHeight(40)
                .build();
        assertEquals(40, bc.getPrefHeight(), 0.01);
    }

    // ---------- 链式 ----------

    @Test
    @DisplayName("全链式串联:item/onClick/separator/styleClass/padding")
    void fullChain() {
        HBox bc = BreadcrumbAnt.create()
                .separator("·")
                .item("首页", "/home")
                .item("列表", item -> {})
                .item("详情")
                .styleClass("app-breadcrumb")
                .padding(6)
                .build();
        assertNotNull(bc);
        assertTrue(bc.getStyleClass().contains(JfxStyles.BREADCRUMB));
        assertTrue(bc.getStyleClass().contains("app-breadcrumb"));
        // 3 items + 2 separators = 5 children
        assertEquals(5, bc.getChildren().size());
        // 分隔符是 "·"
        assertEquals("·", ((Label) bc.getChildren().get(1)).getText());
    }
}
