package org.openkawu.jfxium.core.util;

import java.util.function.Consumer;

/**
 * 回调函数触发辅助工具。
 * 集中替代散落在各 template/component 私有方法中的 {@code fire / fireIfPresent} 样板。
 *
 * <h2>设计动机</h2>
 * <p>原 {@code ProjectDashboardTemplate.fire}、{@code ProjectOverviewTemplate.fire}、
 * {@code ProjectReleaseTemplate.fire}、{@code LaunchPadTemplate.fire} 都是同一段
 * {@code if (onAction != null) onAction.accept(key);} 的副本。本类收口后：</p>
 * <ul>
 *   <li>调用方意图更明确（{@code Callbacks.fire(consumer, key)} 一目了然）</li>
 *   <li>空值保护在工具层完成（{@code consumer} / {@code value} 任一为 {@code null} 时静默跳过）</li>
 *   <li>后续要加日志埋点、性能监控或异步分发只改一处</li>
 *   <li>新人 review 时能直接搜到所有触发点</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 安全触发单参回调（consumer / value 任一为 null 则跳过）
 * Callbacks.fire(onAction, key);
 *
 * // 触发无参回调（Runnable）
 * Callbacks.fire(onConfirm);
 * }</pre>
 */
public final class Callbacks {

    private Callbacks() {
        // 工具类禁止实例化
    }

    /**
     * 安全触发 {@link Consumer} 回调：{@code consumer} 或 {@code value} 为 {@code null} 时静默跳过。
     *
     * <p>替代散落在各 template/component 私有方法中的 {@code fire(String)} 模式。
     * 关键差异：本方法对 {@code value} 也做 null 检查，避免调用方忘记 null-guard 导致回调
     * 收到 null key（消费方往往直接 {@code String.valueOf} 出现 "null" 字符串）。</p>
     *
     * @param consumer 回调（{@code null} 时静默跳过）
     * @param value    回调参数（{@code null} 时静默跳过，避免回调消费方收到 null）
     */
    public static void fire(Consumer<String> consumer, String value) {
        if (consumer != null && value != null) {
            consumer.accept(value);
        }
    }

    /**
     * 安全触发 {@link Runnable} 回调：{@code runnable} 为 {@code null} 时静默跳过。
     *
     * <p>对应无参事件（如确认、取消、关闭）的统一触发入口。</p>
     */
    public static void fire(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }
}
