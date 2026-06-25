package org.openkawu.jfxium.core.util;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.stage.Popup;

/**
 * PopupPositioner —— 统一 Popup 在 anchor 节点周围的弹出定位 + 主题样式表继承。
 *
 * <h2>解决的重复模式</h2>
 *
 * <h3>模式 E1：Popup 弹出定位（项目内 7+ 处）</h3>
 * <p>手工 localToScreen + show：</p>
 * <pre>{@code
 * // TreeSelectAnt / CascaderAnt / AutoCompleteAnt / MentionsAnt / MenuAnt / DropdownAnt / PopconfirmAnt
 * Bounds bounds = anchor.localToScreen(anchor.getBoundsInLocal());
 * popup.show(anchor, bounds.getMinX(), bounds.getMaxY() + 4);
 * }</pre>
 *
 * <h3>模式 E2：Popup 继承宿主 stylesheets（项目内 4 处）</h3>
 * <pre>{@code
 * // AutoCompleteAnt / DropdownAnt / MentionsAnt / PopconfirmAnt
 * popup.showingProperty().addListener((obs, wasShowing, isShowing) -> {
 *     if (isShowing && popup.getScene() != null && owner.getScene() != null) {
 *         popup.getScene().getStylesheets().setAll(owner.getScene().getStylesheets());
 *     }
 * });
 * }</pre>
 *
 * <p>用本工具类替换：</p>
 * <pre>{@code
 * PopupPositioner.showBelow(popup, anchor);            // 默认 4px gap
 * PopupPositioner.inheritStylesheets(popup, owner);    // 注入主题样式表
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>null 安全</b>：所有方法对入参 null 做防御——不抛 NPE，让外部代码零负担。</li>
 *   <li><b>无状态</b>：所有方法为 {@code static}，工具类不允许实例化。</li>
 *   <li><b>桥接 JavaFX API</b>：仅在 JavaFX API 之上做 null 防御 + 统一签名，不改变语义。</li>
 *   <li><b>约定对齐</b>：默认 gap = 4px 与 Ant Design / Element Plus / Naive UI 的 popup 默认偏移对齐。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 1) 字段下方（最常见，下拉/自动补全/级联）
 * Popup popup = new Popup();
 * popup.getContent().add(menuBox);
 * PopupPositioner.showBelow(popup, triggerField);
 * PopupPositioner.inheritStylesheets(popup, triggerField);
 *
 * // 2) 右键菜单（屏幕坐标）
 * target.setOnContextMenuRequested(e -> {
 *     PopupPositioner.showAtScreen(popup, target, e.getScreenX(), e.getScreenY());
 * });
 * }</pre>
 */
public final class PopupPositioner {

    /** 默认 gap（像素），与 Ant Design / Element Plus 的 popup 默认值对齐。 */
    public static final double DEFAULT_GAP = 4.0;

    private PopupPositioner() {
        // 工具类禁止实例化
    }

    // ============================================================
    // 围绕 anchor 四周的弹出（模式 E1）
    // ============================================================

    /**
     * 在 anchor 节点<b>正下方</b>显示 Popup，使用 {@link #DEFAULT_GAP}（4px）。
     *
     * <p>任一参数为 null 时静默跳过，不抛 NPE。</p>
     *
     * @param popup  要显示的 Popup（不可为 null）
     * @param anchor 锚点节点（不可为 null）
     */
    public static void showBelow(Popup popup, Node anchor) {
        showBelow(popup, anchor, DEFAULT_GAP);
    }

    /**
     * 在 anchor 节点<b>正下方</b>显示 Popup（可定制 gap）。
     *
     * <p>任一参数为 null 时静默跳过，不抛 NPE。</p>
     *
     * @param popup  要显示的 Popup（不可为 null）
     * @param anchor 锚点节点（不可为 null）
     * @param gap    与 anchor 边缘的间距（像素）；可为负值表示略微重叠
     */
    public static void showBelow(Popup popup, Node anchor, double gap) {
        showRelative(popup, anchor, PopupEdge.BOTTOM, gap);
    }

    /**
     * 在 anchor 节点<b>正上方</b>显示 Popup，使用 {@link #DEFAULT_GAP}（4px）。
     */
    public static void showAbove(Popup popup, Node anchor) {
        showAbove(popup, anchor, DEFAULT_GAP);
    }

    /**
     * 在 anchor 节点<b>正上方</b>显示 Popup（可定制 gap）。
     */
    public static void showAbove(Popup popup, Node anchor, double gap) {
        showRelative(popup, anchor, PopupEdge.TOP, gap);
    }

    /**
     * 在 anchor 节点<b>右侧</b>显示 Popup，使用 {@link #DEFAULT_GAP}（4px）。
     */
    public static void showRightOf(Popup popup, Node anchor) {
        showRightOf(popup, anchor, DEFAULT_GAP);
    }

    /**
     * 在 anchor 节点<b>右侧</b>显示 Popup（可定制 gap）。
     */
    public static void showRightOf(Popup popup, Node anchor, double gap) {
        showRelative(popup, anchor, PopupEdge.RIGHT, gap);
    }

    /**
     * 在 anchor 节点<b>左侧</b>显示 Popup，使用 {@link #DEFAULT_GAP}（4px）。
     */
    public static void showLeftOf(Popup popup, Node anchor) {
        showLeftOf(popup, anchor, DEFAULT_GAP);
    }

    /**
     * 在 anchor 节点<b>左侧</b>显示 Popup（可定制 gap）。
     */
    public static void showLeftOf(Popup popup, Node anchor, double gap) {
        showRelative(popup, anchor, PopupEdge.LEFT, gap);
    }

    /**
     * 在屏幕坐标 (screenX, screenY) 处显示 Popup（右键菜单场景）。
     *
     * <p>任一参数为 null 时静默跳过，不抛 NPE。</p>
     */
    public static void showAtScreen(Popup popup, Node anchor, double screenX, double screenY) {
        if (popup != null && anchor != null) {
            popup.show(anchor, screenX, screenY);
        }
    }

    // ============================================================
    // 主题样式表继承（模式 E2）
    // ============================================================

    /**
     * 把 owner 的 stylesheets 注入 popup 的 Scene，确保暗色主题等正确。
     *
     * <p>通常在构造 Popup 后调用一次即可——后续 popup 每次 show/hide 都会触发 listener 同步。</p>
     *
     * <p>任一参数为 null 时静默跳过，不抛 NPE。</p>
     *
     * <p><b>为何必要</b>：JavaFX 的 {@link Popup} 在 show 时会创建独立 {@link javafx.scene.Scene Scene}，
     * 新 Scene 默认无样式表——若不注入宿主样式表，popup 内的控件会丢失主题（暗色主题下出现亮底白字等
     * 反白问题）。</p>
     *
     * @param popup  要补全样式表的 Popup（不可为 null）
     * @param owner  宿主节点（不可为 null）——通常就是 trigger field
     */
    public static void inheritStylesheets(Popup popup, Node owner) {
        if (popup == null || owner == null) {
            return;
        }
        popup.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing && popup.getScene() != null && owner.getScene() != null) {
                popup.getScene().getStylesheets().setAll(owner.getScene().getStylesheets());
            }
        });
    }

    // ============================================================
    // 内部实现
    // ============================================================

    /** Popup 相对于 anchor 的方位枚举（私有，避免外部依赖）。 */
    private enum PopupEdge {
        TOP, BOTTOM, LEFT, RIGHT
    }

    /**
     * 统一的「计算 Bounds → 调用 show」内部入口。任一参数为 null 时静默跳过。
     */
    private static void showRelative(Popup popup, Node anchor, PopupEdge edge, double gap) {
        if (popup == null || anchor == null) {
            return;
        }
        Bounds bounds = anchor.localToScreen(anchor.getBoundsInLocal());
        if (bounds == null) {
            return;
        }
        double x;
        double y;
        switch (edge) {
            case TOP -> {
                x = bounds.getMinX();
                y = bounds.getMinY() - gap;
            }
            case BOTTOM -> {
                x = bounds.getMinX();
                y = bounds.getMaxY() + gap;
            }
            case LEFT -> {
                x = bounds.getMinX() - gap;
                y = bounds.getMinY();
            }
            case RIGHT -> {
                x = bounds.getMaxX() + gap;
                y = bounds.getMinY();
            }
            default -> {
                x = bounds.getMinX();
                y = bounds.getMaxY() + gap;
            }
        }
        popup.show(anchor, x, y);
    }
}