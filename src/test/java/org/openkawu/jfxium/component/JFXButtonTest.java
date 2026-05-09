package org.openkawu.jfxium.component;

import javafx.scene.control.Button;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JFXButton 组件测试
 */
public class JFXButtonTest extends org.openkawu.jfxium.JavaFXTestBase {

    @Test
    public void testBasicButton() {
        Button button = JFXButton.create("Test").build();
        assertNotNull(button);
        assertEquals("Test", button.getText());
    }

    @Test
    public void testButtonTypes() {
        Button defaultBtn = JFXButton.create("Default").type(JFXButton.Type.DEFAULT).build();
        assertTrue(defaultBtn.getStyleClass().contains("default"));

        Button primaryBtn = JFXButton.create("Primary").type(JFXButton.Type.PRIMARY).build();
        assertTrue(primaryBtn.getStyleClass().contains("accent"));

        Button outlinedBtn = JFXButton.create("Outlined").type(JFXButton.Type.OUTLINED).build();
        assertTrue(outlinedBtn.getStyleClass().contains("outlined"));
    }

    @Test
    public void testButtonSizes() {
        Button smallBtn = JFXButton.create("Small").size(JFXButton.Size.SMALL).build();
        assertTrue(smallBtn.getStyleClass().contains("small"));

        Button largeBtn = JFXButton.create("Large").size(JFXButton.Size.LARGE).build();
        assertTrue(largeBtn.getStyleClass().contains("large"));
    }

    @Test
    public void testButtonShapes() {
        Button roundedBtn = JFXButton.create("Rounded").rounded().build();
        assertTrue(roundedBtn.getStyleClass().contains("rounded"));

        Button squareBtn = JFXButton.create("Square").square().build();
        assertTrue(squareBtn.getStyleClass().contains("square"));
    }

    @Test
    public void testButtonDisabled() {
        Button disabledBtn = JFXButton.create("Disabled").disabled(true).build();
        assertTrue(disabledBtn.isDisabled());
    }

    @Test
    public void testButtonStyleClass() {
        Button btn = JFXButton.create("Styled").styleClass("custom-class").build();
        assertTrue(btn.getStyleClass().contains("custom-class"));
    }
}