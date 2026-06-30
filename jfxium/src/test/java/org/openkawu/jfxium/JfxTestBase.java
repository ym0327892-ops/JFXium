package org.openkawu.jfxium;

import javafx.application.Platform;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JavaFX 单元测试基类 —— 在 {@code @BeforeAll} 中初始化 JavaFX 工具套件。
 *
 * <p>所有需要创建 JavaFX 控件（Button、TextField 等）的测试类都应继承此类。
 * JavaFX 工具套件只需初始化一次（JVM 级单例），本类通过 {@link AtomicBoolean} 保证幂等。</p>
 *
 * <p>在无图形后端的环境（如 CI 非 xvfb-run）里，{@code Platform.startup()} 会失败，
 * 通过 {@code Assumptions.assumeTrue} 自动跳过整个测试类的全部用例，
 * 而不是卡死在 JavaFX 启动阶段。</p>
 *
 * <p>CI 已配置 {@code xvfb-run} + {@code -Dprism.order=sw}（见 jfxium/pom.xml），
 * 确保全量测试在 GitHub Actions 上真实执行。</p>
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
    private static final AtomicBoolean FX_AVAILABLE = new AtomicBoolean(false);

    @BeforeAll
    static void initJavaFX() {
        if (FX_INITIALIZED.compareAndSet(false, true)) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(latch::countDown);
                latch.await();
                FX_AVAILABLE.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 中断 == 无法运行，标记不可用
            } catch (Throwable ignored) {
                // Platform.startup() 失败（无显示后端 / 缺 GL 库），测试跳过
            }
        }
        // 跳过整个测试类的全部用例（JUnit 5 语义：假设不满足 = 跳过）
        Assumptions.assumeTrue(FX_AVAILABLE.get(), "JavaFX not available in this environment");
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
}
