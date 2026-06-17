package org.openkawu.jfxium.template;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LoginTemplate")
class LoginTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("feature(null) / bannerWidth 非有限值 / 输入图标盒接线安全")
    void nullAndInvalidValues_safe() {
        BorderPane root = LoginTemplate.create()
                .feature(null)
                .bannerWidth(Double.NaN)
                .build();

        assertNotNull(root);

        VBox banner = (VBox) root.getLeft();
        assertEquals(360.0, banner.getPrefWidth(), 0.001);
        VBox featuresBox = (VBox) banner.getChildren().get(2);
        assertTrue(featuresBox.getChildren().isEmpty());

        VBox form = (VBox) root.getCenter();
        HBox usernameRow = null;
        for (var child : form.getChildren()) {
            if (child instanceof HBox hBox) {
                usernameRow = hBox;
                break;
            }
        }
        assertNotNull(usernameRow);

        StackPane iconBox = (StackPane) usernameRow.getChildren().get(0);
        assertTrue(iconBox.getStyleClass().contains(JfxStyles.LOGIN_FORM_INPUT_ICON_BOX));
    }
}
