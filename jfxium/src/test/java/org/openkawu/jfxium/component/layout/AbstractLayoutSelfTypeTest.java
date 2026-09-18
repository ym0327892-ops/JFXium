package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.Background;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Abstract layout 自类型基类测试")
class AbstractLayoutSelfTypeTest extends JfxTestBase {

    @Test
    @DisplayName("HBox/VBox 自类型基类允许 LayoutCommon 后继续链子类专属 API")
    void boxSelfTypesKeepSubclassChain() {
        CustomHBox hbox = new CustomHBox()
                .background(Background.SUBTLE)
                .spacing(8)
                .custom("h");

        CustomVBox vbox = new CustomVBox()
                .padding(8)
                .spacing(12)
                .custom("v");

        assertEquals("h", hbox.value);
        assertEquals("v", vbox.value);
        assertTrue(hbox.getStyleClass().contains(Background.SUBTLE.styleClass()));
        assertEquals(new Insets(8), vbox.getPadding());
    }

    @Test
    @DisplayName("Flow/Stack/Tile 自类型基类允许布局方法后继续链子类专属 API")
    void paneSelfTypesKeepSubclassChain() {
        CustomFlowPane flow = new CustomFlowPane()
                .gap(-1)
                .align(Pos.CENTER)
                .custom("flow");

        CustomStackPane stack = new CustomStackPane()
                .children(new Label("A"))
                .align(Pos.CENTER)
                .custom("stack");

        CustomTilePane tile = new CustomTilePane()
                .prefColumns(0)
                .gap(-1)
                .custom("tile");

        assertEquals("flow", flow.value);
        assertEquals(0, flow.getHgap(), 0.0);
        assertEquals("stack", stack.value);
        assertEquals(1, stack.getChildren().size());
        assertEquals("tile", tile.value);
        assertEquals(1, tile.getPrefColumns());
    }

    @Test
    @DisplayName("Border/Split/TextFlow 自类型基类允许布局方法后继续链子类专属 API")
    void structuralSelfTypesKeepSubclassChain() {
        Label center = new Label("center");
        CustomBorderPane border = new CustomBorderPane()
                .center(center)
                .borderTop()
                .custom("border");

        Label splitItem = new Label("split");
        CustomSplitPane split = new CustomSplitPane()
                .items(splitItem)
                .dividerPositions(Double.NaN, 2)
                .custom("split");

        Label text = new Label("text");
        CustomTextFlow textFlow = new CustomTextFlow()
                .children(text)
                .lineSpacing(-1)
                .textAlignment(TextAlignment.CENTER)
                .custom("text");

        assertSame(center, border.getCenter());
        assertEquals("border", border.value);
        assertEquals("split", split.value);
        assertEquals(1, split.getItems().size());
        assertEquals("text", textFlow.value);
        assertEquals(0, textFlow.getLineSpacing(), 0.0);
    }

    @Test
    @DisplayName("Anchor/Scroll 自类型基类保留特殊语义并允许继续链子类专属 API")
    void anchorAndScrollSelfTypesKeepSubclassChain() {
        Label anchored = new Label("anchor");
        CustomAnchorPane anchor = new CustomAnchorPane()
                .children(anchored)
                .fill(anchored)
                .custom("anchor");

        Label content = new Label("content");
        CustomScrollPane scroll = new CustomScrollPane()
                .padding(6)
                .content(content)
                .fitToWidth(true)
                .custom("scroll");

        assertEquals("anchor", anchor.value);
        assertEquals(0.0, AnchorPaneAnt.getTopAnchor(anchored));
        assertEquals("scroll", scroll.value);
        assertTrue(scroll.isFitToWidth());
        assertTrue(scroll.getContent().getStyleClass().contains("jfx-scroll-pane-viewport"));
    }

    private static final class CustomHBox extends AbstractHBoxAnt<CustomHBox> {
        private String value;
        private CustomHBox custom(String value) { this.value = value; return this; }
    }

    private static final class CustomVBox extends AbstractVBoxAnt<CustomVBox> {
        private String value;
        private CustomVBox custom(String value) { this.value = value; return this; }
    }

    private static final class CustomFlowPane extends AbstractFlowPaneAnt<CustomFlowPane> {
        private String value;
        private CustomFlowPane custom(String value) { this.value = value; return this; }
    }

    private static final class CustomStackPane extends AbstractStackPaneAnt<CustomStackPane> {
        private String value;
        private CustomStackPane custom(String value) { this.value = value; return this; }
    }

    private static final class CustomTilePane extends AbstractTilePaneAnt<CustomTilePane> {
        private String value;
        private CustomTilePane custom(String value) { this.value = value; return this; }
    }

    private static final class CustomBorderPane extends AbstractBorderPaneAnt<CustomBorderPane> {
        private String value;
        private CustomBorderPane custom(String value) { this.value = value; return this; }
    }

    private static final class CustomSplitPane extends AbstractSplitPaneAnt<CustomSplitPane> {
        private String value;
        private CustomSplitPane custom(String value) { this.value = value; return this; }
    }

    private static final class CustomTextFlow extends AbstractTextFlowAnt<CustomTextFlow> {
        private String value;
        private CustomTextFlow custom(String value) { this.value = value; return this; }
    }

    private static final class CustomAnchorPane extends AbstractAnchorPaneAnt<CustomAnchorPane> {
        private String value;
        private CustomAnchorPane custom(String value) { this.value = value; return this; }
    }

    private static final class CustomScrollPane extends AbstractScrollPaneAnt<CustomScrollPane> {
        private String value;
        private CustomScrollPane custom(String value) { this.value = value; return this; }
    }
}
