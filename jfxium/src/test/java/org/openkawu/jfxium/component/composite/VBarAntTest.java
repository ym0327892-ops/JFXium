package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("VBarAnt")
class VBarAntTest extends JfxTestBase {

    @Test
    @DisplayName("默认模式保留三段式 spacer")
    void build_defaultUsesSpacer() {
        VBarAnt bar = VBarAnt.create()
                .top(new Label("顶部"))
                .bottom(new Label("底部"))
                .build();

        assertEquals(3, bar.getChildren().size());
    }

    @Test
    @DisplayName("compact() 退化为普通紧凑纵向堆叠")
    void build_compactSkipsSpacer() {
        VBarAnt bar = VBarAnt.create()
                .compact()
                .gap(8)
                .top(new Label("标题"))
                .bottom(new Label("内容"))
                .build();

        assertEquals(2, bar.getChildren().size());
        assertTrue(bar.getChildren().get(0) instanceof Label);
        assertTrue(bar.getChildren().get(1) instanceof Label);
    }
}
