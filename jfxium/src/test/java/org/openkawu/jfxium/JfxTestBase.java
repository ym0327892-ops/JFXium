package org.openkawu.jfxium;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JavaFX 单元测试基类 —— 在 @BeforeAll 中初始化 JavaFX 工具套件。
 *
 * <p>所有需要创建 JavaFX 控件（Button、TextField 等）的测试类都应继承此类。
 * JavaFX 工具套件只需初始化一次（JVM 级单例），本类通过 {@link AtomicBoolean} 保证幂等。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * class MyControlTest extends JfxTestBase {
 *     @Test
 *     void testCreate() {
 *         ButtonAnt btn = ButtonAnt.create("OK").build();
 *         assertEquals("OK", btn.getText());
 *     }
 * }
 * }</pre>
 */
public abstract class JfxTestBase {

    private static final AtomicBoolean FX_INITIALIZED = new AtomicBoolean(false);

    @BeforeAll
    static void initJavaFX() {
        if (FX_INITIALIZED.compareAndSet(false, true)) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("JavaFX toolkit init interrupted", e);
            }
        }
    }

    protected static void runOnFxThreadAndWait(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable error) {
                errorRef.set(error);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("JavaFX action interrupted", e);
        }

        Throwable error = errorRef.get();
        if (error != null) {
            if (error instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (error instanceof Error fatalError) {
                throw fatalError;
            }
            throw new RuntimeException(error);
        }
    }

    protected static void pumpFxEvents() {
        runOnFxThreadAndWait(() -> {});
    }
}
