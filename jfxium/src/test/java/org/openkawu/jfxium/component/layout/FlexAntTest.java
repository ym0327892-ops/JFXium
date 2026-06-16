package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FlexAnt 单元测试")
class FlexAntTest extends JfxTestBase {

    @Test
    @DisplayName("纵向 wrap 时 justify 映射到垂直轴，align 映射到水平轴")
    void verticalWrapMapsAxesCorrectly() {
        Pane pane = FlexAnt.createVertical()
                .wrap(true)
                .justify(FlexAnt.Justify.END)
                .align(FlexAnt.Align.CENTER)
                .children(new Label("A"), new Label("B"))
                .build();

        FlowPane flow = assertInstanceOf(FlowPane.class, pane);
        assertEquals(Orientation.VERTICAL, flow.getOrientation());
        assertEquals(Pos.BOTTOM_CENTER, flow.getAlignment());
    }

    @Test
    @DisplayName("wrap(true) 与 grow 子节点组合时快速失败，避免静默失效")
    void wrapWithGrowFailsFast() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () ->
                FlexAnt.create()
                        .wrap(true)
                        .child(new Label("Grow"), true)
                        .build());
        assertTrue(error.getMessage().contains("wrap(true)"));
    }

    @Test
    @DisplayName("空枚举参数回落到默认布局，避免 build 阶段 NPE")
    void nullEnumOptionsFallbackToDefaults() {
        Pane pane = FlexAnt.create()
                .direction(null)
                .justify(null)
                .align(null)
                .children(new Label("A"))
                .build();

        HBox box = assertInstanceOf(HBox.class, pane);
        assertEquals(Pos.CENTER_LEFT, box.getAlignment());
    }

    @Test
    @DisplayName("负间距钳制为 0，避免产生反常布局")
    void negativeGapsAreClampedToZero() {
        Pane pane = FlexAnt.create()
                .wrap(true)
                .gap(-8)
                .rowGap(-4)
                .columnGap(-6)
                .children(new Label("A"), new Label("B"))
                .build();

        FlowPane flow = assertInstanceOf(FlowPane.class, pane);
        assertEquals(0, flow.getHgap());
        assertEquals(0, flow.getVgap());
    }
}
