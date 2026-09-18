package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UploadAnt")
class UploadAntTest extends JfxTestBase {

    @Test
    @DisplayName("UploadFile(null) 构建安全")
    void uploadFile_nullSafe() {
        UploadAnt.UploadFile file = new UploadAnt.UploadFile(null);

        assertEquals("", file.name);
        assertEquals(0.0, file.size, 0.001);
        assertEquals("done", file.status);
        assertEquals(100.0, file.percent, 0.001);
    }

    @Test
    @DisplayName("null 文案 / accept 构建安全")
    void build_nullValues_safe() {
        VBox upload = UploadAnt.create()
                .buttonText(null)
                .dragText(null)
                .hintText(null)
                .accept(null)
                .build();

        assertNotNull(upload);
        assertTrue(upload.getStyleClass().contains(JfxStyles.UPLOAD));
    }
}
