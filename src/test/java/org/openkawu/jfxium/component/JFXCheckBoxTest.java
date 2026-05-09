package org.openkawu.jfxium.component;

import javafx.scene.control.CheckBox;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JFXCheckBox 组件测试
 */
public class JFXCheckBoxTest extends org.openkawu.jfxium.JavaFXTestBase {

    @Test
    public void testBasicCheckBox() {
        CheckBox checkBox = JFXCheckBox.create("Test").build();
        assertNotNull(checkBox);
        assertEquals("Test", checkBox.getText());
        assertFalse(checkBox.isSelected());
    }

    @Test
    public void testSelectedCheckBox() {
        CheckBox checkBox = JFXCheckBox.create("Selected").selected(true).build();
        assertTrue(checkBox.isSelected());
    }

    @Test
    public void testDisabledCheckBox() {
        CheckBox checkBox = JFXCheckBox.create("Disabled").disabled(true).build();
        assertTrue(checkBox.isDisabled());
    }

    @Test
    public void testIndeterminateCheckBox() {
        CheckBox checkBox = JFXCheckBox.create("Indeterminate").indeterminate(true).build();
        assertTrue(checkBox.isIndeterminate());
    }
}