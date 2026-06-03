package org.openkawu.jfxium.core.layout;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.component.overlay.ModalAnt;
import org.openkawu.jfxium.component.overlay.DrawerAnt;

/**
 * 全局浮层管理器（单例）。
 * 统一管理 Modal / Drawer / Notification / Message 等浮层组件的 z-index 层级。
 *
 * <h2>层级规范（从下到上）</h2>
 * <pre>
 * 0. 主内容层（mainLayout）
 * 1. Drawer 层（z-index: 1000）
 * 2. Modal 层（z-index: 2000）
 * 3. Notification 层（z-index: 3000）
 * 4. Message 层（z-index: 4000）
 * </pre>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 1. 在 Application.start() 中注册浮层容器
 * SceneLayout sceneLayout = SceneLayout.create()
 *     .content(mainContent)
 *     .buildLayout();
 *
 * OverlayManager.getInstance().registerOverlayLayer(sceneLayout.getOverlayLayer());
 *
 * // 2. 在组件中使用（未来可选，当前 DrawerAnt/ModalAnt 仍使用独立 Stage）
 * OverlayManager.getInstance().showInDrawerLayer(drawerNode);
 * OverlayManager.getInstance().showInModalLayer(modalNode);
 * }</pre>
 *
 * <h2>设计说明</h2>
 * <ul>
 *   <li><b>当前版本</b>：DrawerAnt / ModalAnt 使用独立 Stage（TRANSPARENT + APPLICATION_MODAL），
 *       不依赖 OverlayManager。这是为了保证遮罩层覆盖整个窗口，且支持跨窗口弹出。</li>
 *   <li><b>未来扩展</b>：Notification / Message 等"非模态浮层"可直接挂载到 overlayLayer，
 *       避免创建额外 Stage，提升性能。</li>
 *   <li><b>迁移路径</b>：如果未来需要"内嵌式 Modal"（不覆盖整个窗口，只覆盖某个容器），
 *       可通过 OverlayManager 提供 showInModalLayer(node, container) 方法实现。</li>
 * </ul>
 */
public class OverlayManager {
    private static final OverlayManager INSTANCE = new OverlayManager();

    // z-index 层级常量
    public static final int Z_INDEX_DRAWER = 1000;
    public static final int Z_INDEX_MODAL = 2000;
    public static final int Z_INDEX_NOTIFICATION = 3000;
    public static final int Z_INDEX_MESSAGE = 4000;

    private StackPane overlayLayer;

    private OverlayManager() {
    }

    /**
     * 获取单例实例。
     */
    public static OverlayManager getInstance() {
        return INSTANCE;
    }

    /**
     * 注册全局浮层容器（由 SceneLayout 提供）。
     * 必须在 Application.start() 中调用一次。
     *
     * @param overlayLayer SceneLayout.getOverlayLayer() 返回的 StackPane
     */
    public void registerOverlayLayer(StackPane overlayLayer) {
        this.overlayLayer = overlayLayer;
        // 初始化时设置鼠标穿透（子节点会覆盖此属性）
        overlayLayer.setMouseTransparent(true);
        overlayLayer.setPickOnBounds(false);
    }

    /**
     * 获取浮层容器（供内部使用）。
     */
    public StackPane getOverlayLayer() {
        if (overlayLayer == null) {
            throw new IllegalStateException(
                    "OverlayManager: overlayLayer 未注册。"
                            + "请在 Application.start() 中调用 registerOverlayLayer()。");
        }
        return overlayLayer;
    }

    /**
     * 在 Drawer 层显示节点（z-index: 1000）。
     * 适用于非模态抽屉、侧边栏等。
     *
     * @param node 要显示的节点
     */
    public void showInDrawerLayer(Node node) {
        StackPane layer = getOverlayLayer();
        node.setViewOrder(-Z_INDEX_DRAWER);  // viewOrder 越小越靠前
        layer.getChildren().add(node);
    }

    /**
     * 在 Modal 层显示节点（z-index: 2000）。
     * 适用于对话框、确认框等模态浮层。
     *
     * @param node 要显示的节点
     */
    public void showInModalLayer(Node node) {
        StackPane layer = getOverlayLayer();
        node.setViewOrder(-Z_INDEX_MODAL);
        layer.getChildren().add(node);
    }

    /**
     * 在 Notification 层显示节点（z-index: 3000）。
     * 适用于全局通知、Toast 等。
     *
     * @param node 要显示的节点
     */
    public void showInNotificationLayer(Node node) {
        StackPane layer = getOverlayLayer();
        node.setViewOrder(-Z_INDEX_NOTIFICATION);
        layer.getChildren().add(node);
    }

    /**
     * 在 Message 层显示节点（z-index: 4000，最高层级）。
     * 适用于全局消息提示、加载指示器等。
     *
     * @param node 要显示的节点
     */
    public void showInMessageLayer(Node node) {
        StackPane layer = getOverlayLayer();
        node.setViewOrder(-Z_INDEX_MESSAGE);
        layer.getChildren().add(node);
    }

    /**
     * 从浮层容器中移除节点。
     *
     * @param node 要移除的节点
     */
    public void remove(Node node) {
        if (overlayLayer != null) {
            overlayLayer.getChildren().remove(node);
        }
    }

    /**
     * 清空所有浮层节点（慎用，通常用于场景切换）。
     */
    public void clearAll() {
        if (overlayLayer != null) {
            overlayLayer.getChildren().clear();
        }
    }

    /**
     * 检查浮层容器是否已注册。
     */
    public boolean isRegistered() {
        return overlayLayer != null;
    }
}
