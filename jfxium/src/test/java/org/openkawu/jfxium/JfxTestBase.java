package org.openkawu.jfxium;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JavaFX 单元测试基类 —— 在 @BeforeAll 中初始化 JavaFX 工具套件。
 *
 * <p>所有需要创建 JavaFX 控件（Button、TextField 等）的测试类都应继承此类。
 * JavaFX 工具套件只需初始化一次（JVM 级单例），本类通过 {@link AtomicBoolean} 保证幂等。</p>
 *
 * <p>在无图形后端的环境里，这些测试会被自动跳过，而不是卡在 JavaFX 启动阶段。</p>
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
@ExtendWith(JfxTestBase.HeadlessCondition.class)
public abstract class JfxTestBase {

    private static final AtomicBoolean FX_INITIALIZED = new AtomicBoolean(false);
    private static final AtomicBoolean FX_AVAILABLE = new AtomicBoolean(false);

    @BeforeAll
    static void initJavaFX() {
        if (FX_INITIALIZED.compareAndSet(false, true)) {
            if (isHeadlessEnvironment()) {
                FX_AVAILABLE.set(false);
                return;
            }

            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(latch::countDown);
                latch.await();
                FX_AVAILABLE.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("JavaFX toolkit init interrupted", e);
            } catch (Throwable error) {
                FX_AVAILABLE.set(false);
            }
        }
    }

    protected static void runOnFxThreadAndWait(Runnable action) {
        if (!FX_AVAILABLE.get() || Platform.isFxApplicationThread()) {
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
        if (!FX_AVAILABLE.get()) {
            return;
        }
        runOnFxThreadAndWait(() -> {});
    }

    private static boolean isHeadlessEnvironment() {
        String display = System.getenv("DISPLAY");
        return display == null || display.isBlank();
    }

    static final class HeadlessCondition implements ExecutionCondition {
        @Override
        public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
            if (isHeadlessEnvironment()) {
                return ConditionEvaluationResult.disabled("JavaFX tests skipped in headless environment");
            }
            return ConditionEvaluationResult.enabled("JavaFX available");
        }
    }
}
