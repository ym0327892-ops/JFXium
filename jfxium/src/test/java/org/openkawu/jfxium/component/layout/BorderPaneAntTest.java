package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BorderPaneAnt 单元测试")
class BorderPaneAntTest extends JfxTestBase {

    @Test
    @DisplayName("children(单节点) 仍然兼容为 center")
    void childrenSingleNodeMapsToCenter() {
        Label content = new Label("content");

        BorderPaneAnt ant = BorderPaneAnt.create().children(content);

        assertSame(content, ant.getCenter());
    }

    @Test
    @DisplayName("children(多节点) 不再抛异常，而是打包进 center 的 VBox")
    void childrenMultipleNodesPackIntoCenterVBox() {
        Label a = new Label("A");
        Label b = new Label("B");
        Label c = new Label("C");

        BorderPaneAnt ant = assertDoesNotThrow(() -> BorderPaneAnt.create().children(a, b, c));

        VBox center = assertInstanceOf(VBox.class, ant.getCenter());
        assertEquals(3, center.getChildren().size());
        assertSame(a, center.getChildren().get(0));
        assertSame(b, center.getChildren().get(1));
        assertSame(c, center.getChildren().get(2));
        assertEquals(0.0, center.getSpacing(), 0.0);
    }

    @Test
    @DisplayName("children(含 null) 只打包非 null 节点")
    void childrenIgnoresNullNodes() {
        Label a = new Label("A");
        Label b = new Label("B");

        BorderPaneAnt ant = BorderPaneAnt.create().children(a, null, b);

        VBox center = assertInstanceOf(VBox.class, ant.getCenter());
        assertEquals(2, center.getChildren().size());
        assertSame(a, center.getChildren().get(0));
        assertSame(b, center.getChildren().get(1));
    }

    @Test
    @DisplayName("top/left/center/right/bottom 仍按命名区域工作")
    void namedRegionsStillWork() {
        Label top = new Label("top");
        Label left = new Label("left");
        Label center = new Label("center");
        Label right = new Label("right");
        Label bottom = new Label("bottom");

        BorderPaneAnt ant = BorderPaneAnt.create()
                .top(top)
                .left(left)
                .center(center)
                .right(right)
                .bottom(bottom);

        assertSame(top, ant.getTop());
        assertSame(left, ant.getLeft());
        assertSame(center, ant.getCenter());
        assertSame(right, ant.getRight());
        assertSame(bottom, ant.getBottom());
    }

    @Test
    @DisplayName("align/margin 链式 API 仍可用")
    void alignAndMarginStillWork() {
        Label content = new Label("content");
        BorderPaneAnt ant = BorderPaneAnt.create().center(content);

        BorderPaneAnt returned = ant
                .align(content, Pos.BOTTOM_RIGHT)
                .margin(content, new Insets(1, 2, 3, 4));

        assertSame(ant, returned);
        assertEquals(Pos.BOTTOM_RIGHT, BorderPane.getAlignment(content));
        assertEquals(new Insets(1, 2, 3, 4), BorderPane.getMargin(content));
    }
}
