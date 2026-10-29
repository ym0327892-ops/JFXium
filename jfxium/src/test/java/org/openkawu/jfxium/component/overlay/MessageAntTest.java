package org.openkawu.jfxium.component.overlay;

import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageAnt 多窗口 owner 解析测试。
 *
 * <p>多窗口 bug 的根因是“永远取第一个 Stage”，这里锁定：显式 showing owner 必须原样返回，
 * 未 showing 的 owner 回退到当前 showing Stage。</p>
 */
@DisplayName("MessageAnt owner 解析")
class MessageAntTest extends JfxTestBase {

    @Test
    @DisplayName("显式 showing Stage 原样返回")
    void resolveOwner_explicitShowingStage() {
        runOnFxThreadAndWait(() -> {
            Stage owner = new Stage();
            owner.show();
            try {
                assertSame(owner, MessageAnt.resolveOwner(owner));
            } finally {
                owner.hide();
            }
        });
    }

    @Test
    @DisplayName("未 showing / null owner 回退到 showing Stage")
    void resolveOwner_hiddenOrNull_fallsBackToShowing() {
        runOnFxThreadAndWait(() -> {
            Stage showing = new Stage();
            Stage hidden = new Stage();
            showing.show();
            try {
                Window fallback = MessageAnt.resolveOwner(hidden);
                assertNotNull(fallback);
                assertTrue(fallback.isShowing());
                assertNotNull(MessageAnt.resolveOwner(null));
            } finally {
                hidden.hide();
                showing.hide();
            }
        });
    }
}
