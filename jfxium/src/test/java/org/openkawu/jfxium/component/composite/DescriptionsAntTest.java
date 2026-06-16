package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("DescriptionsAnt")
class DescriptionsAntTest extends JfxTestBase {

    @Test
    @DisplayName("null 枚举、column(0)、null content 构建安全")
    void invalidValues_safe() {
        VBox descriptions = DescriptionsAnt.create()
                .title(null)
                .layout(null)
                .size(null)
                .column(0)
                .item(null, (javafx.scene.Node) null, -2)
                .build();

        assertNotNull(descriptions);
        assertEquals(1, descriptions.getChildren().size());
    }
}
