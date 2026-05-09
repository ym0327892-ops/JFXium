package org.openkawu.jfxium.component;

import javafx.scene.control.ComboBox;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JFXComboBox 组件测试
 */
public class JFXComboBoxTest extends org.openkawu.jfxium.JavaFXTestBase {

    @Test
    public void testBasicComboBox() {
        ComboBox<String> comboBox = JFXComboBox.<String>create()
            .items("Option 1", "Option 2", "Option 3")
            .build();
        
        assertNotNull(comboBox);
        assertEquals(3, comboBox.getItems().size());
        assertEquals("Option 1", comboBox.getItems().get(0));
    }

    @Test
    public void testComboBoxValue() {
        ComboBox<String> comboBox = JFXComboBox.<String>create()
            .items("A", "B", "C")
            .value("B")
            .build();
        
        assertEquals("B", comboBox.getValue());
    }

    @Test
    public void testComboBoxPlaceholder() {
        ComboBox<String> comboBox = JFXComboBox.<String>create()
            .placeholder("Select option")
            .build();
        
        assertEquals("Select option", comboBox.getPromptText());
    }

    @Test
    public void testComboBoxDisabled() {
        ComboBox<String> comboBox = JFXComboBox.<String>create()
            .disabled(true)
            .build();
        
        assertTrue(comboBox.isDisabled());
    }
}