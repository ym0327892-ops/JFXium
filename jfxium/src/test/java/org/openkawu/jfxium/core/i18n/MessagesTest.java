package org.openkawu.jfxium.core.i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Messages")
class MessagesTest extends JfxTestBase {

    @AfterEach
    void resetLocale() {
        runOnFxThreadAndWait(() -> Messages.setLocale(Locale.SIMPLIFIED_CHINESE));
        pumpFxEvents();
    }

    @Test
    @DisplayName("非 FX 线程切换 locale 后立即按新语言取 bundle")
    void setLocale_offFxThreadUpdatesBundleImmediately() {
        runOnFxThreadAndWait(() -> Messages.setLocale(Locale.SIMPLIFIED_CHINESE));
        pumpFxEvents();

        Messages.setLocale(Locale.ENGLISH);

        assertEquals("Copy", Messages.get("codeblock.copy"));
        pumpFxEvents();
        assertEquals(Locale.ENGLISH, Messages.localeProperty().get());
    }
}
