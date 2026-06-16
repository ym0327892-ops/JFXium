package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("SpinAnt")
class SpinAntTest extends JfxTestBase {

    @Test
    @DisplayName("size(null) / indicator(null) 回退默认值")
    void nullEnums_fallbackToDefaults() {
        VBox spin = SpinAnt.create()
                .size(null)
                .indicator(null)
                .build();

        assertNotNull(spin);
        assertEquals(1, spin.getChildren().size());
    }
}
