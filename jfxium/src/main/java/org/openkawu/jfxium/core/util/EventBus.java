package org.openkawu.jfxium.core.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 轻量级跨模块事件总线（M19.32 引入）。
 *
 * <p>主要解决「跨 Stage 通信」「跨模块解耦」场景——发布者不关心谁在监听、
 * 监听者不依赖发布者。基于 {@link ConcurrentHashMap} + {@link CopyOnWriteArrayList}，
 * 线程安全，可在任意线程 publish/subscribe。</p>
 *
 * <h2>线程模型</h2>
 * <p><b>不自动派发到 JavaFX Application Thread</b>。listener 被调用时
 * 处于 publish 调用方的线程——UI listener 内部需要自己 {@code Platform.runLater}
 * 切回 FX Thread。这样设计更通用：</p>
 * <ul>
 *   <li>worker 线程订阅 worker 线程发布的事件无需绕道 FX Thread</li>
 *   <li>UI listener 显式 runLater 比框架隐式派发更易追踪</li>
 *   <li>跟 Guava EventBus / Greenrobot 行为一致</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 1. 定义事件（任意 class/record，无需继承基类）
 * public record UserLoggedIn(String username) {}
 *
 * // 2. 订阅，保存 Subscription 句柄
 * Subscription sub = EventBus.getDefault().subscribe(UserLoggedIn.class, e -> {
 *     Platform.runLater(() -> welcomeLabel.setText("欢迎，" + e.username()));
 * });
 *
 * // 3. 发布（任意线程都行）
 * EventBus.getDefault().publish(new UserLoggedIn("alice"));
 *
 * // 4. 不再需要时取消订阅（防内存泄漏）
 * sub.unsubscribe();
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>类型精确匹配</b>：订阅 {@code Foo.class} 不会收到 {@code FooSub extends Foo}
 *       事件——KISS 原则，避免类型层级带来的隐式行为</li>
 *   <li><b>同 listener 多次订阅 = 多次回调</b>：Java method ref 每次返回新 lambda 实例,
 *       天然允许重复订阅；语义上视为「同一处理器执行 N 次」</li>
 *   <li><b>listener 异常隔离</b>：单个 listener 抛 {@link Throwable} 只记 WARNING 日志，
 *       不影响后续 listener 派发——遵循 Guava EventBus 设计准则</li>
 *   <li><b>派发顺序 = 订阅顺序</b>：{@link CopyOnWriteArrayList} 保证 FIFO</li>
 *   <li><b>多实例支持</b>：默认共用 {@link #getDefault()}；需要模块隔离时 {@code new EventBus()}</li>
 * </ul>
 *
 * <h2>事件设计推荐</h2>
 *
 * <p><b>用 record 定义事件，不要用基类</b>。Java record 是最适合事件数据的载体——
 * 不可变、自动 equals/hashCode/toString、定义只要 1 行。本框架**不提供** {@code BaseEvent}
 * 基类或 {@code Event<T>} 通用封装，因为：</p>
 * <ul>
 *   <li>record 不能 {@code extends} 类，强加基类会迫使用户放弃 record</li>
 *   <li>{@code Event<T>(String type, T data)} 信封类是反模式：丢失类型安全 + 字符串路由 + 强转</li>
 *   <li>业界共识：Guava EventBus / Greenrobot EventBus 都是裸 Object 事件，不强制信封</li>
 * </ul>
 *
 * <p>需要"统一标记"或"命名空间"时，<b>推荐用 sealed interface 而非基类</b>：</p>
 * <pre>{@code
 * // 推荐：sealed interface 做事件命名空间
 * public sealed interface AppEvent
 *         permits UserLoggedIn, UserLoggedOut, OrderCreated {
 *     // 可选公共方法，子类自动实现
 *     default long timestamp() { return System.currentTimeMillis(); }
 * }
 *
 * public record UserLoggedIn(String username) implements AppEvent {}
 * public record UserLoggedOut(String username) implements AppEvent {}
 * public record OrderCreated(long orderId, BigDecimal amount) implements AppEvent {}
 *
 * // 订阅仍然按精确类型（注意 EventBus 不支持订阅 sealed interface 收到所有子类）
 * bus.subscribe(UserLoggedIn.class, e -> ...);
 * bus.subscribe(OrderCreated.class, e -> ...);
 *
 * // 在订阅闭包里用 switch + 模式匹配处理多种事件
 * Consumer<AppEvent> dispatcher = e -> {
 *     switch (e) {
 *         case UserLoggedIn(var user) -> welcomeUser(user);
 *         case UserLoggedOut(var user) -> goodbyeUser(user);
 *         case OrderCreated(var id, var amount) -> notifyOrder(id, amount);
 *     }
 * };
 * }</pre>
 *
 * <p><b>请求-响应（Request/Reply）超出 EventBus 职责</b>。EventBus 是单向 PubSub，
 * 不要用它实现 RPC。需要请求-响应时使用 {@link java.util.concurrent.CompletableFuture}
 * 或直接接口注入。</p>
 *
 * <h2>设计参考</h2>
 * <p>Subscription 句柄返回模式参考 RxJava / reactive-streams；
 * listener 异常隔离参考
 * <a href="https://guava.dev/releases/30.0-android/api/docs/com/google/common/eventbus/EventBus.html">Guava EventBus</a>。</p>
 */
public final class EventBus {

    private static final Logger LOGGER = Logger.getLogger(EventBus.class.getName());

    /** 默认全局实例。需要模块隔离时自己 {@code new EventBus()}。 */
    private static final EventBus DEFAULT = new EventBus();

    /** 获取默认全局实例。命名跟 {@link java.util.Locale#getDefault()} 对齐。 */
    public static EventBus getDefault() {
        return DEFAULT;
    }

    /**
     * 订阅句柄。调 {@link #unsubscribe()} 取消订阅。
     *
     * <p>幂等：多次调用 unsubscribe 是安全的（第二次开始 no-op）。</p>
     */
    public interface Subscription {
        void unsubscribe();
    }

    private final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

    /** public 构造：业务侧可以创建独立 bus 实例做模块隔离。 */
    public EventBus() {
    }

    /**
     * 订阅指定类型事件。
     *
     * @param eventType 事件类型 Class（不能为 null）
     * @param listener  事件处理器（不能为 null）
     * @return 订阅句柄。**业务侧需要保存并在不再需要时调 {@code unsubscribe()}**，
     *         否则 listener 持有的外部对象（如 {@code this}）永远不会被 GC
     * @throws NullPointerException 任一参数为 null
     */
    public <T> Subscription subscribe(Class<T> eventType, Consumer<T> listener) {
        Objects.requireNonNull(eventType, "eventType 不能为 null");
        Objects.requireNonNull(listener, "listener 不能为 null");

        // computeIfAbsent 是原子操作，多线程并发订阅同类型时不会重复创建 list
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                 .add(listener);

        // 闭包捕获 eventType + listener 引用，unsubscribe 时精确移除这一个
        // CopyOnWriteArrayList.remove(Object) 用 .equals 比对，对 lambda 是引用相等——
        // 所以 unsub 必须传同一个 listener 实例（这正是用 Subscription 句柄的原因）
        return () -> {
            List<Consumer<?>> list = listeners.get(eventType);
            if (list != null) {
                list.remove(listener);
            }
        };
    }

    /**
     * 发布事件。所有匹配 {@code event.getClass()} 类型的订阅者会被同步回调。
     *
     * <p>单个 listener 抛 {@link Throwable} 不影响其他 listener 派发——
     * 异常会被捕获并以 WARNING 级别记录。</p>
     *
     * @param event 事件实例（不能为 null）
     * @throws NullPointerException event 为 null
     */
    public void publish(Object event) {
        Objects.requireNonNull(event, "event 不能为 null");

        List<Consumer<?>> subs = listeners.get(event.getClass());
        if (subs == null || subs.isEmpty()) {
            return;
        }

        for (Consumer<?> consumer : subs) {
            try {
                // 类型擦除后 Consumer<?> 实际就是 Consumer<Object>，强转无运行时风险；
                // 真正的类型安全由 subscribe 签名 Class<T> + Consumer<T> 静态保证
                @SuppressWarnings("unchecked")
                Consumer<Object> typed = (Consumer<Object>) consumer;
                typed.accept(event);
            } catch (Throwable t) {
                // 异常隔离：单个 listener 抛错不能影响后续派发（业界共识，Guava 也这么做）
                // 用 Throwable 而非 Exception，防止 listener 抛 Error 时整条派发链断掉
                LOGGER.log(Level.WARNING,
                        "EventBus listener for " + event.getClass().getName()
                                + " threw exception (其他 listener 仍会被派发)",
                        t);
            }
        }
    }

    /**
     * 清空所有订阅。
     *
     * <p>主要用于单元测试隔离和应用关闭时的资源清理。
     * 业务运行期不应调用此方法——会清掉其他模块的订阅。</p>
     */
    public void clear() {
        listeners.clear();
    }
}
