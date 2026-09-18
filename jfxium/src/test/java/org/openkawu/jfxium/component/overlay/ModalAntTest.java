package org.openkawu.jfxium.component.overlay;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ModalAnt")
class ModalAntTest extends JfxTestBase {

    @Test
    @DisplayName("默认 footer 使用 jfx-overlay-footer 且不在 Java 里写死 spacing")
    void defaultFooterUsesStyleClassSpacing() throws Exception {
        ModalAnt.ModalResult result = ModalAnt.create()
                .title("提示")
                .content(new Label("内容"))
                .build();

        Method method = result.getClass().getDeclaredMethod("createDefaultFooter");
        method.setAccessible(true);
        HBox footer = (HBox) method.invoke(result);

        assertTrue(footer.getStyleClass().contains(JfxStyles.OVERLAY_FOOTER));
        assertEquals(2, footer.getChildren().size());
    }
}
