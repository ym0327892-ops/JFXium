package org.openkawu.jfxium.component.composite;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BackTopAnt 单元测试")
class BackTopAntTest extends JfxTestBase {

    @Test
    @DisplayName("bottom/right 配置应用到 StackPane margin")
    void bottomAndRightApplyToMargin() {
        StackPane backTop = BackTopAnt.create()
                .bottom(60)
                .right(24)
                .build();

        assertEquals(new Insets(0, 24, 60, 0), StackPane.getMargin(backTop));
    }

    @Test
    @DisplayName("空 duration 和无 content target 不导致运行时异常")
    void nullDurationAndEmptyTargetAreSafe() {
        ScrollPane scrollPane = new ScrollPane();
        StackPane backTop = assertDoesNotThrow(() -> BackTopAnt.create()
                .target(scrollPane)
                .duration(null)
                .visibilityHeight(-1)
                .bottom(-10)
                .right(-20)
                .build());

        assertEquals(new Insets(0, 0, 0, 0), StackPane.getMargin(backTop));
        assertDoesNotThrow(() -> scrollPane.setVvalue(1));
    }

    @Test
    @DisplayName("非空 duration 保持可配置")
    void durationIsConfigurable() {
        assertDoesNotThrow(() -> BackTopAnt.create()
                .duration(Duration.millis(1))
                .build());
    }
}
