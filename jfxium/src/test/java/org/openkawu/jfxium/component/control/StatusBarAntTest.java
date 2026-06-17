package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatusBarAnt")
class StatusBarAntTest extends JfxTestBase {

    @Test
    @DisplayName("action 的 Runnable 可为 null，且不会绑定空回调")
    void action_allowsNullRunnable() {
        StatusBarAnt bar = StatusBarAnt.create()
                .action("UTF-8", null)
                .build();

        assertNotNull(bar);
        Node rightBox = bar.getChildren().get(2);
        assertInstanceOf(HBox.class, rightBox);

        HBox right = (HBox) rightBox;
        assertEquals(1, right.getChildren().size());
        assertInstanceOf(Button.class, right.getChildren().get(0));
        Button btn = (Button) right.getChildren().get(0);
        assertNull(btn.getOnAction());
    }
}
