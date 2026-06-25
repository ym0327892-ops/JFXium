package org.openkawu.jfxium.core.util;

import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本 / 节点容器的 null 安全辅助工具。
 * 集中替代散落在各 composite 组件私有方法中的 {@code safeText / safeLabel / safeContent}。
 *
 * <h2>设计动机</h2>
 * <p>原 {@code CascaderAnt.safeText}、{@code TabsAnt.safeLabel / safeContent}、
 * {@code TransferAnt.safeText} 都是同一段 null→空字符串 / null→空数组兜底逻辑的副本。
 * 本类收口后：</p>
 * <ul>
 *   <li>调用方意图更明确（{@code safeText} / {@code safeLabel} / {@code safeContent} 一目了然）</li>
 *   <li>后续要给"空内容"加 sentinel（如灰色"无"占位）只改一处</li>
 *   <li>新人 review 时能直接搜到所有调用点，不用逐个文件找私有方法</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 文本：null → ""
 * String label = TextUtils.safeText(rawLabel);
 *
 * // Label：null → 空 Label（避免空标题渲染异常）
 * Label title = TextUtils.safeLabel(rawTitle, JfxStyles.MY_TITLE);
 *
 * // 节点数组：null → []，并过滤掉数组中的 null 元素
 * List<Node> kids = TextUtils.safeContent(rawChildren);
 * }</pre>
 */
public final class TextUtils {

    private TextUtils() {
        // 工具类禁止实例化
    }

    /**
     * 文本 null 安全：{@code null} → {@code ""}。
     *
     * <p>替代散落的 {@code text != null ? text : ""} 三元模式。
     * 比 {@code Objects.requireNonNullElse(text, "")} 性能略好（不创建 Optional / 包装对象）。</p>
     */
    public static String safeText(String text) {
        return text != null ? text : "";
    }

    /**
     * 文本 null 安全（带 fallback）：{@code null} → {@code fallback}。
     *
     * <p>替代散落在 {@code ProjectDashboardTemplate} / {@code ProjectOverviewTemplate} 等
     * {@code Builder} 中的 {@code value != null ? value : DEFAULT_X} 模式，把"非空即用，否则降级"
     * 语义集中收口。{@code fallback} 也可传 {@code null}（返回 {@code null}），满足"显式清空"场景。</p>
     *
     * @param text     原始文本（{@code null} 视为缺失）
     * @param fallback 缺失时的回退值（可空）
     * @return {@code text} 非 {@code null} 则原样返回，否则返回 {@code fallback}
     */
    public static String safeText(String text, String fallback) {
        return text != null ? text : fallback;
    }

    /**
     * 浮点型尺寸 null/异常安全（非负语义）：{@code value} 为非有限数或负数时返回 {@code fallback}。
     *
     * <p>替代散落在 {@code WorkspaceTemplate} / {@code PageTemplate} / {@code CrudTemplate} /
     * {@code FilterBarAnt} / {@code DashboardTemplate} / {@code PanelHeader} 中的
     * {@code private safe* / safeWidth} 模式，集中收口为一种行为。</p>
     *
     * <p><b>为什么允许 {@code fallback} 传 {@code 0}</b>：很多场景下"非法输入 → 0"是更安全的默认
     * （不会偷偷变成负数让布局崩坏），同时调用方可以传更合适的非零 fallback（如 {@code 360}）。</p>
     *
     * @param value    原始数值
     * @param fallback 非法输入时（{@code NaN} / {@code Infinity} / 负数）的回退值
     * @return 合法且非负时返回原值，否则返回 fallback
     */
    public static double safeNonNegative(double value, double fallback) {
        return Double.isFinite(value) && value >= 0 ? value : fallback;
    }

    /**
     * 浮点型尺寸 null/异常安全（正数语义）：{@code value} 为非有限数或非正数时返回 {@code fallback}。
     *
     * <p>适用于"宽度 / 高度"类必须为正的场景（{@code 0} 同样视为非法，因为布局上不可见）。</p>
     *
     * @param value    原始数值
     * @param fallback 非法输入时（{@code NaN} / {@code Infinity} / {@code <= 0}）的回退值
     */
    public static double safePositive(double value, double fallback) {
        return Double.isFinite(value) && value > 0 ? value : fallback;
    }

    /**
     * 浮点下界钳制：把非有限数视为 {@code min}，再钳到 {@code [min, +∞)}。
     *
     * <p>替代散落的 {@code Double.isFinite(v) ? Math.max(min, v) : min} 模式。
     * 典型场景：列数、页码、评分等"至少为 1"的物理量。</p>
     *
     * @param value 原始数值
     * @param min   允许的最小值（{@code NaN} 时回退到 {@code min}）
     */
    public static double ensureAtLeast(double value, double min) {
        return Double.isFinite(value) ? Math.max(min, value) : min;
    }

    /** {@link #ensureAtLeast(double, double)} 的 {@code min = 0} 特化。 */
    public static double ensureAtLeastZero(double value) {
        return ensureAtLeast(value, 0);
    }

    /** {@link #ensureAtLeast(double, double)} 的 {@code min = 1} 特化 —— 用于列数、页码、评分等。 */
    public static double ensureAtLeastOne(double value) {
        return ensureAtLeast(value, 1);
    }

    /**
     * {@link #ensureAtLeastOne(double)} 的 {@code int} 重载。
     *
     * <p>用于 {@code span / rowSpan / colSpan / rows} 等网格语义参数：调用方写
     * {@code TextUtils.ensureAtLeastOne(span)} 比 {@code (int) TextUtils.ensureAtLeastOne((double) span)}
     * 更简洁,且避免一次隐式窄化转换。</p>
     */
    public static int ensureAtLeastOne(int value) {
        return value >= 1 ? value : 1;
    }

    /**
     * Label 节点 null 安全：{@code text == null} → 空 Label。
     *
     * <p>对应之前 {@code TabsAnt.safeLabel(String, String)} 私有方法。
     * 同时挂上 styleClass（调用方负责传 JfxStyles 常量）。</p>
     *
     * @param text       文本内容（{@code null} 视为空字符串）
     * @param styleClass 要挂到 Label 的 styleClass（{@code null} 不挂任何类）
     */
    public static Label safeLabel(String text, String styleClass) {
        Label label = new Label(safeText(text));
        if (styleClass != null) {
            label.getStyleClass().add(styleClass);
        }
        return label;
    }

    /**
     * 节点数组 null 安全：{@code nodes == null} → 空 {@code List}，并过滤掉数组中的 null 元素。
     *
     * <p>替代 {@code TabsAnt.safeContent} / {@code CascaderAnt} 等组件中的
     * {@code nodes == null ? List.of() : Arrays.asList(nodes)} 模式。
     * <b>为什么过滤 null</b>：JavaFX 单节点只能挂一个 parent，传入含 null 的数组会让
     * 后续 {@code container.getChildren().addAll(list)} 抛 NPE，统一在工具层过滤更省事。</p>
     *
     * @param nodes 原始节点数组（可为 {@code null}，元素可含 {@code null}）
     * @return 非空、null 元素已过滤的不可变节点列表
     */
    public static List<Node> safeContent(Node[] nodes) {
        if (nodes == null || nodes.length == 0) {
            return List.of();
        }
        List<Node> result = new ArrayList<>(nodes.length);
        for (Node n : nodes) {
            if (n != null) {
                result.add(n);
            }
        }
        return result;
    }

    /**
     * List 节点 null 安全：{@code list == null} → 空 {@code List}，并过滤掉 list 中的 null 元素。
     *
     * <p>对应 {@code List<Node>} 入参的版本。</p>
     */
    public static List<Node> safeContent(List<? extends Node> list) {
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        List<Node> result = new ArrayList<>(list.size());
        for (Node n : list) {
            if (n != null) {
                result.add(n);
            }
        }
        return result;
    }

    /**
     * 把非空节点追加到目标列表：{@code nodes} 为 {@code null} / 空 数组跳过；逐个过滤 {@code null} 元素。
     *
     * <p>替代散落在 {@code WorkspaceTemplate} / {@code ProjectConsoleTemplate} 中的私有 {@code addNodes} 方法。
     * 原代码采用 {@code for} 循环逐个 null-guard，意图完全相同，本方法把语义提升为「追加非空节点」。</p>
     *
     * <p><b>为什么不在调用方用 {@code safeContent} + {@code addAll}</b>：{@code safeContent} 内部创建新的
     * {@code ArrayList}（隔离可变状态），而追加场景下复用调用方的列表更省内存。这里直接复用同一目标容器。</p>
     *
     * @param target 目标列表（{@code null} 静默跳过）
     * @param nodes  节点数组（{@code null} / 空 / 含 {@code null} 元素都安全处理）
     */
    public static void addNonNull(List<Node> target, Node... nodes) {
        if (target == null || nodes == null || nodes.length == 0) {
            return;
        }
        for (Node node : nodes) {
            if (node != null) {
                target.add(node);
            }
        }
    }
}
