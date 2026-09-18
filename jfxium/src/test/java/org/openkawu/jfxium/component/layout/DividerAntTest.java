package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DividerAnt 单元测试")
class DividerAntTest extends JfxTestBase {

    @Test
    @DisplayName("默认 build 返回水平 Separator")
    void defaultBuildReturnsHorizontalSeparator() {
        Node node = DividerAnt.create().build();

        Separator separator = assertInstanceOf(Separator.class, node);
        assertEquals(Orientation.HORIZONTAL, separator.getOrientation());
        assertTrue(separator.getStyleClass().contains(JfxStyles.DIVIDER_HORIZONTAL));
    }

    @Test
    @DisplayName("orientation(null) 和 position(null) 安全回退默认值")
    void nullConfigurationFallsBackToDefaults() {
        Node node = DividerAnt.create()
                .orientation(null)
                .position(null)
                .text("OR")
                .build();

        HBox box = assertInstanceOf(HBox.class, node);
        assertEquals(3, box.getChildren().size());
        assertInstanceOf(Label.class, box.getChildren().get(1));
        assertEquals("OR", ((Label) box.getChildren().get(1)).getText());
    }

    @Test
    @DisplayName("vertical + text 会降级为垂直 Separator，而不是返回错误结构")
    void verticalWithTextFallsBackToPlainSeparator() {
        Node node = DividerAnt.create()
                .vertical()
                .text("ignored")
                .build();

        Separator separator = assertInstanceOf(Separator.class, node);
        assertEquals(Orientation.VERTICAL, separator.getOrientation());
        assertTrue(separator.getStyleClass().contains(JfxStyles.DIVIDER_VERTICAL));
    }

    @Test
    @DisplayName("带文本 Divider 通过 Hgrow 表达位置，不在 Java 中硬编码短线像素")
    void textPositionUsesGrowthPriorityInsteadOfFixedLinePixels() {
        HBox leftBox = assertInstanceOf(HBox.class, DividerAnt.create()
                .text("Left")
                .position(DividerAnt.Position.LEFT)
                .build());
        Separator leftLine = assertInstanceOf(Separator.class, leftBox.getChildren().get(0));
        Separator rightLine = assertInstanceOf(Separator.class, leftBox.getChildren().get(2));

        assertEquals(Priority.SOMETIMES, HBox.getHgrow(leftLine));
        assertEquals(Priority.ALWAYS, HBox.getHgrow(rightLine));
        assertEquals(Region.USE_COMPUTED_SIZE, leftLine.getMinWidth());
        assertEquals(Region.USE_COMPUTED_SIZE, leftLine.getMaxWidth());

        HBox rightBox = assertInstanceOf(HBox.class, DividerAnt.create()
                .text("Right")
                .position(DividerAnt.Position.RIGHT)
                .build());
        Separator rightShortLine = assertInstanceOf(Separator.class, rightBox.getChildren().get(2));
        assertEquals(Priority.SOMETIMES, HBox.getHgrow(rightShortLine));
        assertEquals(Region.USE_COMPUTED_SIZE, rightShortLine.getMaxWidth());
    }
}
