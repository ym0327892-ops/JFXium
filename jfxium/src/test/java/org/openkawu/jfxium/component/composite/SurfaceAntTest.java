package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SurfaceAnt 单元测试")
class SurfaceAntTest extends JfxTestBase {

    @Test
    @DisplayName("title(null)、shadow(null)、负 gap 安全回落默认")
    void nullAndNegativeOptionsAreSafe() {
        VBox surface = assertDoesNotThrow(() -> SurfaceAnt.create()
                .title(null)
                .shadow(null)
                .gap(-12)
                .content(new Label("content"))
                .build());

        assertEquals(0, surface.getSpacing(), 0.0);
        assertTrue(surface.getStyleClass().contains(JfxStyles.SURFACE));
        assertFalse(surface.getStyleClass().contains(JfxStyles.SURFACE_SHADOW_SM));
        assertFalse(surface.getStyleClass().contains(JfxStyles.SURFACE_SHADOW_MD));
        assertFalse(surface.getStyleClass().contains(JfxStyles.SURFACE_SHADOW_LG));
    }

    @Test
    @DisplayName("有标题时创建 header")
    void titleCreatesHeader() {
        VBox surface = SurfaceAnt.create()
                .title("Filters")
                .content(new Label("content"))
                .build();

        assertEquals(2, surface.getChildren().size());
        assertTrue(surface.getChildren().get(0).getStyleClass()
                .contains(JfxStyles.SURFACE_HEADER));
    }
}
