package org.openkawu.jfxium.component.overlay;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.openkawu.jfxium.component.overlay.DropdownAnt.DropdownResult;

@DisplayName("DropdownAnt")
class DropdownAntTest extends JfxTestBase {

    @Test
    @DisplayName("showArrow 时 trigger 包装节点挂 jfx-dropdown-trigger")
    void showArrow_wrapsTriggerWithStyleClass() throws Exception {
        DropdownResult result = DropdownAnt.create()
                .trigger(new Label("操作"))
                .showArrow()
                .item("edit", "编辑")
                .build();

        Field triggerField = result.getClass().getDeclaredField("trigger");
        triggerField.setAccessible(true);
        Node trigger = (Node) triggerField.get(result);

        assertInstanceOf(HBox.class, trigger);
        HBox wrapper = (HBox) trigger;
        assertTrue(wrapper.getStyleClass().contains(JfxStyles.DROPDOWN_TRIGGER));
        assertEquals(2, wrapper.getChildren().size());
        assertTrue(wrapper.getChildren().get(1).getStyleClass().contains(JfxStyles.ARROW_DROPDOWN));
    }
}
