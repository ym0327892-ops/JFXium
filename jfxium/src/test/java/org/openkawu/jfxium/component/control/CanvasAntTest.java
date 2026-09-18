package org.openkawu.jfxium.component.control;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CanvasAnt 单元测试 —— 覆盖工厂创建 / size / onDraw 重绘 / clear-fill-stroke 快捷绘图 / 继承式契约。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本：create() / 构造重载 / 默认 styleClass</li>
 *   <li>size：setWidth / setHeight / 链式</li>
 *   <li>onDraw：null 不挂监听 / 非 null 注册监听 / redraw 触发 handler / widthProperty 变化触发 redraw</li>
 *   <li>快捷绘图：clear() / fill() / stroke() / gc()</li>
 *   <li>链式串联 + 继承式核心契约（build() 返回 this + 多态兼容）</li>
 * </ul>
 *
 * <p><b>FX 线程注意</b>：{@code onDraw(handler)} 内部用 {@code Platform.runLater(this::redraw)} 触发首次绘制，
 * 断言 handler 被调用需要走 {@link JfxTestBase#runOnFxThreadAndWait(Runnable)} 等到 FX 线程执行完毕。</p>
 */
@DisplayName("CanvasAnt")
class CanvasAntTest extends JfxTestBase {

    // ============================================================
    // 基本
    // ============================================================

    @Test
    @DisplayName("create() 默认 0 尺寸 + 挂 jfx-canvas")
    void create_default() {
        CanvasAnt canvas = CanvasAnt.create().build();
        assertNotNull(canvas);
        assertTrue(canvas.getStyleClass().contains(JfxStyles.CANVAS));
        // JavaFX Canvas 默认 width=0, height=0
        assertEquals(0.0, canvas.getWidth());
        assertEquals(0.0, canvas.getHeight());
    }

    @Test
    @DisplayName("构造 CanvasAnt(w, h) 直接设尺寸 + 挂样式")
    void constructor_withSize() {
        CanvasAnt canvas = new CanvasAnt(200, 150);
        assertEquals(200.0, canvas.getWidth());
        assertEquals(150.0, canvas.getHeight());
        assertTrue(canvas.getStyleClass().contains(JfxStyles.CANVAS));
    }

    // ============================================================
    // size
    // ============================================================

    @Test
    @DisplayName("size(w, h) 设宽高")
    void size_normal() {
        CanvasAnt canvas = CanvasAnt.create().size(400, 300).build();
        assertEquals(400.0, canvas.getWidth());
        assertEquals(300.0, canvas.getHeight());
    }

    @Test
    @DisplayName("size 链式：连续多次覆盖生效")
    void size_chainedOverride() {
        CanvasAnt canvas = CanvasAnt.create().size(100, 100).size(200, 250).build();
        assertEquals(200.0, canvas.getWidth());
        assertEquals(250.0, canvas.getHeight());
    }

    // ============================================================
    // onDraw
    // ============================================================

    @Nested
    @DisplayName("onDraw —— 自定义绘制回调")
    class OnDraw {

        @Test
        @DisplayName("onDraw(null) 不抛 NPE + 不挂监听")
        void onDraw_null_noListener() {
            CanvasAnt canvas = CanvasAnt.create().size(50, 50);
            assertDoesNotThrow(() -> canvas.onDraw(null));
            // 改尺寸不应触发任何回调（因为没注册监听）
            assertDoesNotThrow(() -> canvas.size(80, 80));
        }

        @Test
        @DisplayName("onDraw(handler) 后改 width 触发 redraw → 调用 handler")
        void onDraw_widthChangeFiresHandler() {
            AtomicInteger callCount = new AtomicInteger(0);
            CanvasAnt canvas = CanvasAnt.create()
                    .size(100, 100)
                    .onDraw(gc -> callCount.incrementAndGet())
                    .build();

            // 改 width 触发监听器 → redraw → handler +1
            canvas.size(200, 200);
            // 首次 onDraw 时还有 Platform.runLater(initial redraw)，改 width 也走 redraw
            // 这里断言至少被调用过 1 次即可，不依赖具体次数
            assertTrue(callCount.get() >= 1,
                    "onDraw handler 应至少被调用 1 次,实际 " + callCount.get());
        }

        @Test
        @DisplayName("onDraw(handler) 拿到的 GraphicsContext 是有效 Canvas 的")
        void onDraw_handlerReceivesValidGc() {
            AtomicReference<GraphicsContext> captured = new AtomicReference<>();
            CanvasAnt canvas = CanvasAnt.create()
                    .size(60, 40)
                    .onDraw(captured::set)
                    .build();

            // 改 height 触发 redraw，确保 handler 至少跑 1 次
            canvas.size(80, 60);

            GraphicsContext gc = captured.get();
            assertNotNull(gc, "handler 应被调用且收到非 null GraphicsContext");
            assertSame(canvas.getGraphicsContext2D(), gc,
                    "handler 收到的 GraphicsContext 应等同 canvas.gc()");
        }

        @Test
        @DisplayName("redraw() 即使没设 onDraw 也不崩")
        void redraw_withoutHandlerSafe() {
            CanvasAnt canvas = CanvasAnt.create().size(20, 20).build();
            assertDoesNotThrow(canvas::redraw);
        }
    }

    // ============================================================
    // 快捷绘图
    // ============================================================

    @Test
    @DisplayName("gc() 返回非空 GraphicsContext")
    void gc_returnsNonNull() {
        CanvasAnt canvas = CanvasAnt.create().size(50, 50).build();
        assertNotNull(canvas.gc());
    }

    @Test
    @DisplayName("clear() 在有尺寸时不抛异常")
    void clear_withSizeSafe() {
        CanvasAnt canvas = CanvasAnt.create()
                .size(100, 100)
                .fill(Color.RED)
                .clear()
                .build();
        // fill + clear 后像素已变更，但断言不崩即可（GraphicsContext 内部状态）
        assertEquals(100.0, canvas.getWidth());
    }

    @Test
    @DisplayName("fill(Color) 全画布填色不抛异常")
    void fill_appliesColor() {
        CanvasAnt canvas = CanvasAnt.create()
                .size(120, 80)
                .fill(Color.BLUE)
                .build();
        // fill 后 gc.getFill() 应回到默认（fillRect 不改变当前 fill 颜色设置外的状态）
        // 这里仅断言不崩 + gc 仍可访问
        assertNotNull(canvas.gc());
    }

    @Test
    @DisplayName("stroke(Color) 设描边色不抛异常")
    void stroke_appliesColor() {
        CanvasAnt canvas = CanvasAnt.create()
                .size(50, 50)
                .stroke(Color.GREEN)
                .build();
        assertNotNull(canvas.gc());
    }

    // ============================================================
    // 链式串联 + 继承式核心契约
    // ============================================================

    @Test
    @DisplayName("全链式：create → size → fill → stroke → clear → build 全部生效")
    void fullChain_noException() {
        AtomicInteger drawCount = new AtomicInteger(0);
        CanvasAnt canvas = CanvasAnt.create()
                .size(200, 100)
                .onDraw(gc -> drawCount.incrementAndGet())
                .fill(Color.RED)
                .stroke(Color.BLACK)
                .clear()
                .build();

        assertEquals(200.0, canvas.getWidth());
        assertEquals(100.0, canvas.getHeight());
        assertTrue(canvas.getStyleClass().contains(JfxStyles.CANVAS));
        // onDraw 注册后,首次 Platform.runLater(redraw) 是异步的 —— 抽干 FX 事件队列再断言
        pumpFxEvents();
        assertTrue(drawCount.get() >= 1, "onDraw 应被至少调用 1 次");
    }

    @Test
    @DisplayName("build() 返回自身（继承式核心契约）")
    void build_returnsSelf() {
        CanvasAnt canvas = CanvasAnt.create();
        assertSame(canvas, canvas.build());
    }

    @Test
    @DisplayName("继承式：父类 Canvas 引用可接收（多态兼容）")
    void parentReference_polymorphism() {
        Canvas canvas = CanvasAnt.create().size(60, 60).build();
        assertInstanceOf(CanvasAnt.class, canvas);
        assertEquals(60.0, canvas.getWidth());
        assertTrue(canvas.getStyleClass().contains(JfxStyles.CANVAS));
    }
}