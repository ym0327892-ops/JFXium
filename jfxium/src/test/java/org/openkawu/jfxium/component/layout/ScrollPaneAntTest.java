package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ScrollPaneAnt 单元测试")
class ScrollPaneAntTest extends JfxTestBase {

    @Test
    @DisplayName("content(Node) 会用 TOP_LEFT 对齐的 viewport 包装内容")
    void contentUsesTopLeftViewport() {
        ScrollPaneAnt ant = ScrollPaneAnt.create().content(new Label("Hello"));

        StackPane viewport = assertInstanceOf(StackPane.class, ant.getContent());
        assertEquals(Pos.TOP_LEFT, viewport.getAlignment());
        assertTrue(viewport.getStyleClass().contains(JfxStyles.SCROLL_PANE_VIEWPORT));
    }

    @Test
    @DisplayName("padding 先设置后 content 也会正确下放到 viewport")
    void paddingBeforeContentAppliesToViewport() {
        ScrollPaneAnt ant = ScrollPaneAnt.create()
                .padding(new Insets(8, 12, 16, 20))
                .content(new Label("Hello"));

        StackPane viewport = assertInstanceOf(StackPane.class, ant.getContent());
        assertEquals(new Insets(8, 12, 16, 20), viewport.getPadding());
    }

    @Test
    @DisplayName("content(null) 会释放旧 viewport 子节点，允许复用同一内容节点")
    void contentNullReleasesPreviousViewportChild() {
        Label content = new Label("Hello");
        ScrollPaneAnt ant = ScrollPaneAnt.create().content(content);

        ant.content(null);

        assertNull(ant.getContent());
        assertNull(content.getParent());
        assertDoesNotThrow(() -> ant.content(content));
        StackPane viewport = assertInstanceOf(StackPane.class, ant.getContent());
        assertSame(content, viewport.getChildren().get(0));
    }

    @Test
    @DisplayName("重复 content(同一节点) 按 setter 语义替换 viewport，不因旧 parent 抛异常")
    void repeatedContentWithSameNodeReplacesViewportSafely() {
        Label content = new Label("Hello");
        ScrollPaneAnt ant = ScrollPaneAnt.create().content(content);
        StackPane firstViewport = assertInstanceOf(StackPane.class, ant.getContent());

        assertDoesNotThrow(() -> ant.content(content));

        StackPane secondViewport = assertInstanceOf(StackPane.class, ant.getContent());
        assertNotSame(firstViewport, secondViewport);
        assertTrue(firstViewport.getChildren().isEmpty());
        assertSame(content, secondViewport.getChildren().get(0));
    }
}
