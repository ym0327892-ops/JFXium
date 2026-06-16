package org.openkawu.jfxium.component.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SpaceAnt 单元测试")
class SpaceAntTest extends JfxTestBase {

    @Test
    @DisplayName("split(Node) 在多个分隔位时会为每个位置创建独立节点")
    void splitNodeCreatesIndependentCopies() {
        Pane pane = SpaceAnt.create()
                .split(new Label("/"))
                .children(new Label("A"), new Label("B"), new Label("C"))
                .build();

        assertInstanceOf(HBox.class, pane);
        assertEquals(5, pane.getChildren().size());
        assertInstanceOf(Label.class, pane.getChildren().get(1));
        assertInstanceOf(Label.class, pane.getChildren().get(3));
        assertNotSame(pane.getChildren().get(1), pane.getChildren().get(3));
        assertEquals("/", ((Label) pane.getChildren().get(1)).getText());
        assertEquals("/", ((Label) pane.getChildren().get(3)).getText());
    }

    @Test
    @DisplayName("split(true) 默认插入与容器方向垂直的 Separator")
    void splitTrueUsesDefaultSeparator() {
        Pane pane = SpaceAnt.createVertical()
                .split(true)
                .children(new Label("A"), new Label("B"))
                .build();

        Node split = pane.getChildren().get(1);
        assertInstanceOf(Separator.class, split);
        assertEquals(javafx.geometry.Orientation.HORIZONTAL, ((Separator) split).getOrientation());
    }

    @Test
    @DisplayName("复杂模板节点要求调用方改用 split(Supplier)，避免静默丢失结构")
    void complexSplitTemplateFailsFast() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
                SpaceAnt.create()
                        .split(new HBox(new Text("/")))
                        .children(new Label("A"), new Label("B"))
                        .build());
        assertTrue(error.getMessage().contains("split(Supplier"));
    }

    @Test
    @DisplayName("水平 Space 的 align 只影响交叉轴，不把整行推到右侧")
    void horizontalAlignMapsToCrossAxis() {
        HBox box = (HBox) SpaceAnt.create()
                .align(SpaceAnt.Align.END)
                .children(new Label("A"), new Label("B"))
                .build();
        assertEquals(javafx.geometry.Pos.BOTTOM_LEFT, box.getAlignment());
    }

    @Test
    @DisplayName("垂直 Space 的 align 只影响交叉轴，不把整列压到底部")
    void verticalAlignMapsToCrossAxis() {
        VBox box = (VBox) SpaceAnt.createVertical()
                .align(SpaceAnt.Align.END)
                .children(new Label("A"), new Label("B"))
                .build();
        assertEquals(javafx.geometry.Pos.TOP_RIGHT, box.getAlignment());
    }

    @Test
    @DisplayName("空方向和空对齐回落到默认水平布局")
    void nullDirectionAndAlignFallbackToDefaults() {
        HBox box = (HBox) SpaceAnt.create()
                .direction(null)
                .align(null)
                .children(new Label("A"), new Label("B"))
                .build();

        assertEquals(javafx.geometry.Pos.CENTER_LEFT, box.getAlignment());
    }

    @Test
    @DisplayName("负 size 钳制为 0，避免生成负 spacing")
    void negativeSizeIsClampedToZero() {
        HBox box = (HBox) SpaceAnt.create()
                .size(-12)
                .children(new Label("A"), new Label("B"))
                .build();

        assertEquals(0, box.getSpacing());
    }
}
