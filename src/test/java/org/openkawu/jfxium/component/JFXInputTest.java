package org.openkawu.jfxium.component;

import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JFXInput 组件测试
 */
public class JFXInputTest extends org.openkawu.jfxium.JavaFXTestBase {

    @Test
    public void testBasicInput() {
        TextField input = JFXInput.create().placeholder("Enter text").build();
        assertNotNull(input);
        assertEquals("Enter text", input.getPromptText());
    }

    @Test
    public void testInputValue() {
        TextField input = JFXInput.create().text("test value").build();
        assertEquals("test value", input.getText());
    }

    @Test
    public void testInputDisabled() {
        TextField input = JFXInput.create().disabled(true).build();
        assertTrue(input.isDisabled());
    }

    @Test
    public void testInputReadonly() {
        TextField input = JFXInput.create().readOnly(true).build();
        assertFalse(input.isEditable());
    }
}