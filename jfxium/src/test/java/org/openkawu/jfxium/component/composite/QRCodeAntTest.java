package org.openkawu.jfxium.component.composite;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QRCodeAnt 单元测试 —— 覆盖 Builder 样式接线与运行时 Controller。
 */
@DisplayName("QRCodeAnt")
class QRCodeAntTest extends JfxTestBase {

    @Test
    @DisplayName("build() 返回 StackPane 并挂默认样式")
    void build_returnsStackPane() {
        StackPane qr = QRCodeAnt.create()
                .value("JFXium")
                .build();

        assertNotNull(qr);
        assertTrue(qr.getStyleClass().contains(JfxStyles.QR_CODE));
        assertTrue(qr.getStyleClass().contains(JfxStyles.QR_CODE_BORDERED));
        assertEquals(1, qr.getChildren().size());
        assertInstanceOf(Canvas.class, qr.getChildren().getFirst());
    }

    @Test
    @DisplayName("styleClass / prefWidth 应用到返回容器")
    void builderStyles_applied() {
        StackPane qr = QRCodeAnt.create()
                .styleClass("qr-extra")
                .prefWidth(220)
                .build();

        assertTrue(qr.getStyleClass().contains("qr-extra"));
        assertEquals(220, qr.getPrefWidth());
    }

    @Test
    @DisplayName("controllerOf 获取运行时控制器")
    void controllerOf_returnsController() {
        StackPane qr = QRCodeAnt.create()
                .value("initial")
                .size(128)
                .color(Color.BLUE)
                .bgColor(Color.WHITE)
                .build();

        QRCodeAnt.Controller controller = QRCodeAnt.controllerOf(qr);
        assertEquals("initial", controller.getValue());
        assertEquals(128, controller.getSize());
        assertEquals(Color.BLUE, controller.getColor());

        controller.setValue("updated");
        controller.setSize(96);

        assertEquals("updated", controller.getValue());
        assertEquals(96, controller.getSize());
    }

    @Test
    @DisplayName("controllerOf 非 QRCodeAnt 节点抛出明确异常")
    void controllerOf_invalidNode() {
        assertThrows(IllegalArgumentException.class, () -> QRCodeAnt.controllerOf(new StackPane()));
    }
}
