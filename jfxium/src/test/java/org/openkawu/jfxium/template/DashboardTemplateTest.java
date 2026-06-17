package org.openkawu.jfxium.template;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("DashboardTemplate")
class DashboardTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("stat(null) / leftRatio(NaN) / padding 非有限值构建安全")
    void invalidInputs_safe() {
        VBox root = DashboardTemplate.create()
                .stat(null, null, null, null, true)
                .bottomLeft(new VBox())
                .bottomRight(new VBox())
                .leftRatio(Double.NaN)
                .sectionGap(Double.NaN)
                .padding(Double.NaN)
                .build();

        assertNotNull(root);
        assertEquals(2, root.getChildren().size());
        assertEquals(0.0, root.getSpacing(), 0.001);

        GridPane bottom = (GridPane) root.getChildren().get(1);
        assertEquals(60.0, bottom.getColumnConstraints().get(0).getPercentWidth(), 0.001);
        assertEquals(40.0, bottom.getColumnConstraints().get(1).getPercentWidth(), 0.001);
    }
}
