package org.openkawu.jfxium.component.overlay;

import javafx.stage.Popup;
import javafx.scene.Node;

/**
 * Popup 主题样式注入工具（包内）。
 *
 * <p>{@link Popup} 拥有独立 Scene，不继承宿主节点的主题样式表。
 * 若不把宿主 Scene 的 stylesheets 复制进去，弹层里的 look-up 颜色
 * （如 {@code -color-bg-overlay}、{@code -color-fg-default}）无法解析，
 * 暗色 / 自定义主题下会出现背景透明、文字颜色错误。</p>
 *
 * <p>在 popup 显示时一次性同步宿主样式表；宿主与弹层任一 Scene 尚未就绪则跳过（下次 showing 再同步）。</p>
 */
final class PopupThemes {

    private PopupThemes() {
    }

    /** 绑定：popup 每次显示时复制 anchor 所在 Scene 的全部样式表。 */
    static void bind(Popup popup, Node anchor) {
        if (popup == null || anchor == null) {
            return;
        }
        popup.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing && popup.getScene() != null && anchor.getScene() != null) {
                popup.getScene().getStylesheets().setAll(anchor.getScene().getStylesheets());
            }
        });
    }
}
