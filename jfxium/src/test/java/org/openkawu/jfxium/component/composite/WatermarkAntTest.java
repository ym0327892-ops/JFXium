package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WatermarkAnt 单元测试 —— 覆盖运行时 Controller 与 Builder 样式接线。
 */
@DisplayName("WatermarkAnt")
class WatermarkAntTest extends JfxTestBase {

    @Test
    @DisplayName("build() 返回 StackPane 并挂默认样式")
    void build_returnsStackPane() {
        StackPane watermark = WatermarkAnt.create()
                .content(new Label("content"))
                .text("JFXium")
                .build();

        assertNotNull(watermark);
        assertTrue(watermark.getStyleClass().contains(JfxStyles.WATERMARK));
        assertEquals(2, watermark.getChildren().size());
    }

    @Test
    @DisplayName("styleClass / prefWidth 应用到返回容器")
    void builderStyles_applied() {
        StackPane watermark = WatermarkAnt.create()
                .content(new Label("content"))
                .text("JFXium")
                .styleClass("wm-extra")
                .prefWidth(320)
                .build();

        assertTrue(watermark.getStyleClass().contains("wm-extra"));
        assertEquals(320, watermark.getPrefWidth());
    }

    @Test
    @DisplayName("controllerOf 获取运行时控制器")
    void controllerOf_returnsController() {
        StackPane watermark = WatermarkAnt.create()
                .content(new Label("content"))
                .text("initial")
                .rotate(-30)
                .opacity(0.2)
                .build();

        WatermarkAnt.Controller controller = WatermarkAnt.controllerOf(watermark);
        assertArrayEquals(new String[]{"initial"}, controller.getTextLines());
        assertEquals(-30, controller.getRotate());
        assertEquals(0.2, controller.getOpacity());

        controller.setText("updated");
        controller.setColor(Color.RED);
        controller.setGap(80, 90);

        assertArrayEquals(new String[]{"updated"}, controller.getTextLines());
        assertEquals(Color.RED, controller.getColor());
        assertEquals(80, controller.getGapX());
        assertEquals(90, controller.getGapY());
    }

    @Test
    @DisplayName("controllerOf 非 WatermarkAnt 节点抛出明确异常")
    void controllerOf_invalidNode() {
        assertThrows(IllegalArgumentException.class, () -> WatermarkAnt.controllerOf(new StackPane()));
    }
}
