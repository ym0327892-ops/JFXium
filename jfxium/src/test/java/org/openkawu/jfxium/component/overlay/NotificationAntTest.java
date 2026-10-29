package org.openkawu.jfxium.component.overlay;

import javafx.application.Platform;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * NotificationAnt 多窗口 owner 隔离测试。
 *
 * <p>通知容器按 owner Window 分桶：两个窗口各自一个 popup；同一窗口同 placement 共用一个 popup。</p>
 */
@DisplayName("NotificationAnt owner 隔离")
class NotificationAntTest extends JfxTestBase {

    @Test
    @DisplayName("不同 owner → 各自独立 popup")
    void differentOwners_separatePopups() throws Exception {
        Stage a = showStage();
        Stage b = showStage();
        try {
            NotificationAnt.create().owner(a)
                    .title("A").description("通知 A").duration(0).build().show();
            NotificationAnt.create().owner(b)
                    .title("B").description("通知 B").duration(0).build().show();
            drainFxEvents();
            assertEquals(2, showingPopups());
        } finally {
            hideStages(a, b);
        }
    }

    @Test
    @DisplayName("同一 owner / 同一 placement → 共用一个 popup")
    void sameOwnerAndPlacement_singlePopup() throws Exception {
        Stage owner = showStage();
        try {
            NotificationAnt.create().owner(owner)
                    .title("1").description("第一条").duration(0).build().show();
            NotificationAnt.create().owner(owner)
                    .title("2").description("第二条").duration(0).build().show();
            drainFxEvents();
            assertEquals(1, showingPopups());
        } finally {
            hideStages(owner);
        }
    }

    private static Stage showStage() {
        Stage[] ref = new Stage[1];
        runOnFxThreadAndWait(() -> {
            Stage stage = new Stage();
            stage.setX(80);
            stage.setY(80);
            stage.setWidth(360);
            stage.setHeight(240);
            stage.show();
            ref[0] = stage;
        });
        return ref[0];
    }

    private static void hideStages(Stage... stages) {
        runOnFxThreadAndWait(() -> {
            for (Stage stage : stages) {
                stage.hide();
            }
        });
    }

    private static int showingPopups() {
        AtomicInteger count = new AtomicInteger();
        runOnFxThreadAndWait(() -> count.set((int) Window.getWindows().stream()
                .filter(w -> w.isShowing() && w instanceof Popup)
                .filter(w -> ((Popup) w).getContent().stream()
                        .anyMatch(n -> n.getStyleClass().contains(JfxStyles.NOTIFICATION_CONTAINER)))
                .count()));
        return count.get();
    }

    /** 让 {@code NotificationAnt.show()} 内部排队的 runLater 先执行。 */
    private static void drainFxEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        assertTrue(latch.await(3, TimeUnit.SECONDS), "FX event queue timeout");
    }
}
