package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ResultAnt")
class ResultAntTest extends JfxTestBase {

    @Test
    @DisplayName("null 状态和文案回退默认值")
    void nullValues_fallbackToDefaults() {
        VBox result = ResultAnt.create()
                .status(null)
                .title(null)
                .subTitle(null)
                .build();

        assertNotNull(result);
    }

    @Test
    @DisplayName("extraButton(null action) 点击不抛异常")
    void extraButton_nullAction_safe() {
        VBox result = ResultAnt.create()
                .extraButton("返回", null)
                .build();

        VBox extraBox = assertInstanceOf(VBox.class, result.getChildren().get(result.getChildren().size() - 1));
        Button button = assertInstanceOf(Button.class, extraBox.getChildren().get(0));
        assertDoesNotThrow(button::fire);
    }
}
