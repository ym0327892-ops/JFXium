package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResizablePanelAnt 单元测试")
class ResizablePanelAntTest extends JfxTestBase {

    @Test
    @DisplayName("mode(null) 回落默认水平模式，不在 build 阶段 NPE")
    void nullModeFallsBackToDefault() {
        StackPane panel = assertDoesNotThrow(() -> ResizablePanelAnt.create()
                .mode(null)
                .content(new Label("content"))
                .build());

        assertEquals(2, panel.getChildren().size());
        assertTrue(panel.getChildren().get(1).getStyleClass()
                .contains(JfxStyles.RESIZABLE_PANEL_HANDLE_HORIZONTAL));
    }

    @Test
    @DisplayName("非法尺寸会钳制，max 小于 min 时上调到 min")
    void invalidSizesAreClamped() {
        StackPane panel = ResizablePanelAnt.create()
                .minWidth(-10)
                .minHeight(-20)
                .maxWidth(-1)
                .maxHeight(-2)
                .build();

        assertEquals(0, panel.getMinWidth(), 0.0);
        assertEquals(0, panel.getMinHeight(), 0.0);
        assertEquals(0, panel.getMaxWidth(), 0.0);
        assertEquals(0, panel.getMaxHeight(), 0.0);
    }
}
