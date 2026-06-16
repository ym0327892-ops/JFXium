package org.openkawu.jfxium.component.layout;

import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TextFlowAnt 单元测试")
class TextFlowAntTest extends JfxTestBase {

    @Test
    @DisplayName("children 过滤 null 节点")
    void childrenFiltersNullNodes() {
        Label a = new Label("A");
        Label b = new Label("B");

        TextFlowAnt flow = TextFlowAnt.create().children(a, null, b);

        assertEquals(2, flow.getChildren().size());
        assertSame(a, flow.getChildren().get(0));
        assertSame(b, flow.getChildren().get(1));
    }

    @Test
    @DisplayName("textAlignment(null) 安全且不改已有值")
    void textAlignmentNullIsIgnored() {
        TextFlowAnt flow = TextFlowAnt.create().textAlignment(TextAlignment.CENTER);

        TextFlowAnt returned = assertDoesNotThrow(() -> flow.textAlignment(null));

        assertSame(flow, returned);
        assertEquals(TextAlignment.CENTER, flow.getTextAlignment());
    }

    @Test
    @DisplayName("负 lineSpacing 钳制为 0")
    void negativeLineSpacingIsClamped() {
        TextFlowAnt flow = TextFlowAnt.create().lineSpacing(-6);

        assertEquals(0, flow.getLineSpacing(), 0.0);
    }
}
