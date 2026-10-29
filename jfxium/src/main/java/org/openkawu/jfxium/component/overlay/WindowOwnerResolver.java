package org.openkawu.jfxium.component.overlay;

import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * 覆盖层（Message / Notification 等）宿主窗口解析。
 *
 * <p>规则：显式 showing owner → 当前焦点 Stage → 首个 showing Stage。</p>
 */
final class WindowOwnerResolver {

    private WindowOwnerResolver() {
    }

    static Window resolve(Window requested) {
        if (requested != null && requested.isShowing()) {
            return requested;
        }
        Window first = null;
        for (Window w : Window.getWindows()) {
            if (!w.isShowing() || !(w instanceof Stage)) {
                continue;
            }
            if (w.isFocused()) {
                return w;
            }
            if (first == null) {
                first = w;
            }
        }
        return first;
    }
}
