package org.openkawu.jfxium.component.control;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HyperlinkAnt")
class HyperlinkAntTest extends JfxTestBase {

    @Test
    @DisplayName("onClick 的 Runnable / Consumer 可为 null")
    void onClick_allowsNullCallbacks() {
        HyperlinkAnt link = new HyperlinkAnt("帮助")
                .onClick((Runnable) null)
                .onClick((java.util.function.Consumer<HyperlinkAnt>) null);

        assertNotNull(link);
        assertNull(link.getOnAction());
    }
}
