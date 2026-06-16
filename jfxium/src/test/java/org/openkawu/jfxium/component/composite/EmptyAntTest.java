package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("EmptyAnt")
class EmptyAntTest extends JfxTestBase {

    @Test
    @DisplayName("extraButton(null action) 构建和点击不抛异常")
    void extraButton_nullAction_safe() {
        VBox empty = EmptyAnt.create()
                .extraButton("新增", null)
                .build();

        Button button = assertInstanceOf(Button.class, empty.getChildren().get(2));
        assertDoesNotThrow(button::fire);
    }
}
