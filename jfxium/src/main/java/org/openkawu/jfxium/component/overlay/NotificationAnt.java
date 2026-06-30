package org.openkawu.jfxium.component.overlay;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.openkawu.jfxium.component.base.NotificationCard;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.util.AnimationDuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * JFXium 全局通知组件 - 对标 Ant Design Notification。
 *
 * <p><b>定位</b>：在窗口四个角落弹出通知卡片（标题 + 描述 + 可选自定义内容），
 * 支持自动消失或手动关闭。与 MessageAnt（短暂提示）严格区分。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>类型</b>：SUCCESS / ERROR / WARNING / INFO（带对应图标）</li>
 *   <li><b>位置</b>：TOP_LEFT / TOP_RIGHT（默认）/ BOTTOM_LEFT / BOTTOM_RIGHT</li>
 *   <li><b>动画</b>：300ms FadeIn + SlideIn，200ms FadeOut</li>
 *   <li><b>自动消失</b>：duration 秒后自动关闭（默认 4s，0 = 不自动关闭）</li>
 *   <li><b>自定义内容</b>：content(Node) 可放入任意节点</li>
 *   <li><b>窗口跟随</b>：窗口拖动/缩放时通知自动跟随重定位</li>
 *   <li><b>静态快捷方法</b>：success() / error() / warning() / info()</li>
 * </ul>
 *
 * <h2>关闭语义（与 Ant Design Notification 对齐）</h2>
 * <ul>
 *   <li><b>duration &gt; 0</b>：秒数到点自动关闭（hide）</li>
 *   <li><b>点 X 按钮</b>：触发 onClose 回调 + 关闭（X 按钮由 closable=true 渲染）</li>
 *   <li><b>点 body（非 X）</b>：仅触发 onClick 回调（若提供），<b>不关闭</b></li>
 * </ul>
 * 点 body 不关闭是核心约定 —— 用户可放心阅读长描述 / 点击 content(Hyperlink) /
 * 点 box 内空白;关闭必须显式（X 或 duration 到点）。修复点见 Bug 3（M19.51 同族）。
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 快捷调用：右上角成功通知
 * NotificationAnt.success("保存成功", "数据已保存到服务器");
 *
 * // Builder 用法：左下角错误通知，不自动消失
 * NotificationAnt.create()
 *     .title("连接失败")
 *     .description("无法连接到数据库，请检查配置")
 *     .type(NotificationAnt.Type.ERROR)
 *     .placement(NotificationAnt.Placement.BOTTOM_LEFT)
 *     .duration(0)  // 不自动消失
 *     .closable(true)
 *     .build()
 *     .show();
 *
 * // 带自定义内容节点
 * NotificationAnt.create()
 *     .title("系统更新")
 *     .description("新版本已就绪")
 *     .content(new Hyperlink("查看更新内容"))
 *     .build()
 *     .show();
 * }</pre>
 *
 * @see MessageAnt 全局短暂提示（自动消失，无标题）
 */
public class NotificationAnt {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }

    public enum Placement {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    private static final Map<Placement, NotificationContainer> containers = new HashMap<>();

    static {
        for (Placement p : Placement.values()) {
            containers.put(p, new NotificationContainer(p));
        }
    }

    private static class NotificationEntry {
        final Popup popup;
        final VBox box;

        NotificationEntry(Popup popup, VBox box) {
            this.popup = popup;
            this.box = box;
        }
    }

    private static class NotificationContainer {
        final Placement placement;
        final VBox container;
        final Popup popup;
        final List<NotificationEntry> entries = new ArrayList<>();
        Window window;

        /**
         * 窗口位置 / 尺寸变化都重新定位 —— 让通知始终贴着对应角落。
         * 用同一个 listener 实例挂到多个 property 上，便于统一拆除（避免泄漏）。
         */
        private final javafx.beans.InvalidationListener windowChangeListener = obs -> updatePosition();

        NotificationContainer(Placement placement) {
            this.placement = placement;
            this.container = new VBox();
            this.container.getStyleClass().add(org.openkawu.jfxium.core.css.JfxStyles.NOTIFICATION_CONTAINER);
            this.container.setAlignment(Pos.TOP_LEFT);
            this.container.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

            this.popup = new Popup();
            this.popup.getContent().add(container);
            this.popup.setAutoHide(false);
            this.popup.setHideOnEscape(false);

            // BOTTOM_* placement 的 y = 窗口底 - margin - container.height，
            // addEntry 时容器还未 layout，getHeight() 仍是 0 会算出错位的 y。
            // 监听 height 变化，layout 完成后自动 reposition。
            // TOP_* 不需要：y 固定为 window.top + margin，与容器高度无关。
            if (placement == Placement.BOTTOM_LEFT || placement == Placement.BOTTOM_RIGHT) {
                this.container.heightProperty().addListener((obs, ov, nv) -> updatePosition());
            }
        }

        /**
         * 把 container 关联到 window —— 同时挂位置 / 尺寸 listener，
         * 窗口被拖动 / 缩放时通知卡片自动跟随。
         *
         * <p>如果换了 window（多 Stage 应用），先拆旧的 listener 再装新的，避免泄漏。</p>
         */
        void attachWindow(Window newWindow) {
            if (this.window == newWindow) return;
            detachWindowListeners();
            this.window = newWindow;
            if (newWindow != null) {
                newWindow.xProperty().addListener(windowChangeListener);
                newWindow.yProperty().addListener(windowChangeListener);
                newWindow.widthProperty().addListener(windowChangeListener);
                newWindow.heightProperty().addListener(windowChangeListener);
            }
        }

        private void detachWindowListeners() {
            if (this.window != null) {
                this.window.xProperty().removeListener(windowChangeListener);
                this.window.yProperty().removeListener(windowChangeListener);
                this.window.widthProperty().removeListener(windowChangeListener);
                this.window.heightProperty().removeListener(windowChangeListener);
            }
        }

        void addEntry(NotificationEntry entry) {
            entries.add(entry);
            container.getChildren().add(entry.box);

            // 必须先把 popup show 出来，container 才进入 scene 树，
            // 后面的 applyCss / layout 才有意义（否则 NotificationCard 的
            // inline CSS "-fx-min-width: 384px" 不会被解析）。
            if (!popup.isShowing() && window != null && window.isShowing()) {
                popup.show(window);
            }
            // 强制同步 layout —— 让 container.height 立刻有真值，
            // 避免 BOTTOM_* 在第一次添加时算出 height=0 的错位 y。
            container.applyCss();
            container.layout();

            updatePosition();
        }

        void removeEntry(NotificationEntry entry) {
            entries.remove(entry);
            container.getChildren().remove(entry.box);
            updatePosition();
        }

        void updatePosition() {
            if (window == null || !window.isShowing()) return;

            double x, y;
            double margin = 24;
            double width = 384;

            Bounds bounds = window.getScene().getRoot().getLayoutBounds();
            switch (placement) {
                case TOP_LEFT -> {
                    x = window.getX() + margin;
                    y = window.getY() + margin;
                }
                case TOP_RIGHT -> {
                    x = window.getX() + window.getWidth() - width - margin;
                    y = window.getY() + margin;
                }
                case BOTTOM_LEFT -> {
                    x = window.getX() + margin;
                    y = window.getY() + window.getHeight() - margin - container.getHeight();
                }
                case BOTTOM_RIGHT -> {
                    x = window.getX() + window.getWidth() - width - margin;
                    y = window.getY() + window.getHeight() - margin - container.getHeight();
                }
                default -> {
                    x = window.getX() + window.getWidth() - width - margin;
                    y = window.getY() + margin;
                }
            }

            if (!popup.isShowing()) {
                popup.show(window);
            }
            popup.setX(x);
            popup.setY(y);
        }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private String description = "";
        private Node content = null;
        private Type type = Type.INFO;
        private Placement placement = Placement.TOP_RIGHT;
        private int durationSeconds = 4;
        private boolean closable = true;
        private Consumer<Void> onClose = null;
        private Consumer<Void> onClick = null;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder placement(Placement placement) {
            this.placement = placement;
            return this;
        }

        public Builder duration(int seconds) {
            this.durationSeconds = seconds;
            return this;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder onClose(Consumer<Void> onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder onClick(Consumer<Void> onClick) {
            this.onClick = onClick;
            return this;
        }

        public NotificationResult build() {
            return new NotificationResult(this);
        }
    }

    public static class NotificationResult {
        private final Builder config;

        NotificationResult(Builder config) {
            this.config = config;
        }

        public void show() {
            NotificationAnt.show(config);
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static void success(String title, String description) {
        showStatic(title, description, Type.SUCCESS, 4, Placement.TOP_RIGHT);
    }

    public static void error(String title, String description) {
        showStatic(title, description, Type.ERROR, 4, Placement.TOP_RIGHT);
    }

    public static void warning(String title, String description) {
        showStatic(title, description, Type.WARNING, 4, Placement.TOP_RIGHT);
    }

    public static void info(String title, String description) {
        showStatic(title, description, Type.INFO, 4, Placement.TOP_RIGHT);
    }

    // ============================================================
    // duration / placement 重载（与 MessageAnt 一致：便捷入口不锁死参数）
    // ============================================================

    /** {@link #success(String, String)} 指定 duration（秒），0 = 不自动消失。 */
    public static void success(String title, String description, int durationSeconds) {
        showStatic(title, description, Type.SUCCESS, durationSeconds, Placement.TOP_RIGHT);
    }

    /** {@link #success(String, String)} 指定 placement。 */
    public static void success(String title, String description, Placement placement) {
        showStatic(title, description, Type.SUCCESS, 4, placement);
    }

    /** {@link #error(String, String)} 指定 duration（秒），0 = 不自动消失。 */
    public static void error(String title, String description, int durationSeconds) {
        showStatic(title, description, Type.ERROR, durationSeconds, Placement.TOP_RIGHT);
    }

    /** {@link #error(String, String)} 指定 placement。 */
    public static void error(String title, String description, Placement placement) {
        showStatic(title, description, Type.ERROR, 4, placement);
    }

    /** {@link #warning(String, String)} 指定 duration（秒），0 = 不自动消失。 */
    public static void warning(String title, String description, int durationSeconds) {
        showStatic(title, description, Type.WARNING, durationSeconds, Placement.TOP_RIGHT);
    }

    /** {@link #warning(String, String)} 指定 placement。 */
    public static void warning(String title, String description, Placement placement) {
        showStatic(title, description, Type.WARNING, 4, placement);
    }

    /** {@link #info(String, String)} 指定 duration（秒），0 = 不自动消失。 */
    public static void info(String title, String description, int durationSeconds) {
        showStatic(title, description, Type.INFO, durationSeconds, Placement.TOP_RIGHT);
    }

    /** {@link #info(String, String)} 指定 placement。 */
    public static void info(String title, String description, Placement placement) {
        showStatic(title, description, Type.INFO, 4, placement);
    }

    private static void showStatic(String title, String description, Type type, int durationSeconds, Placement placement) {
        create()
            .title(title).description(description)
            .type(type)
            .duration(durationSeconds)
            .placement(placement)
            .build().show();
    }

    private static void show(Builder config) {
        javafx.application.Platform.runLater(() -> {
            Window window = Window.getWindows().stream()
                .filter(Window::isShowing)
                .filter(w -> w instanceof javafx.stage.Stage)
                .findFirst()
                .orElse(null);

            if (window == null) return;

            NotificationContainer container = containers.get(config.placement);
            container.attachWindow(window);

            NotificationCard.Builder cardBuilder = new NotificationCard.Builder()
                .title(config.title)
                .description(config.description)
                .type(convertType(config.type))
                .closable(config.closable)
                // 透传 onClose：NotificationCard.onClose 收 Runnable,此处收 Consumer<Void>,包一层适配
                // 修 Bug（M19.51）：之前漏传,导致 NotificationCard.L117 if (closable && onClose != null) 永远为 false,X 按钮从未渲染
                .onClose(config.onClose == null ? null : () -> config.onClose.accept(null))
                .content(config.content);

            VBox notificationBox = cardBuilder.build();

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.getContent().add(notificationBox);

            NotificationEntry entry = new NotificationEntry(popup, notificationBox);

            notificationBox.setOpacity(0);
            boolean fromLeft = config.placement == Placement.TOP_LEFT || config.placement == Placement.BOTTOM_LEFT;
            notificationBox.setTranslateX(fromLeft ? -20 : 20);

            FadeTransition fadeIn = new FadeTransition(AnimationDuration.SLOW, notificationBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition slideIn = new TranslateTransition(AnimationDuration.SLOW, notificationBox);
            slideIn.setFromX(fromLeft ? -20 : 20);
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fadeIn, slideIn);
            pt.play();

            if (config.durationSeconds > 0) {
                PauseTransition delay = new PauseTransition(Duration.seconds(config.durationSeconds));
                delay.setOnFinished(e -> hide(entry, config));
                delay.play();
            }

            // 关闭路径 3 条:
            //   1) duration 计时器到点 → hide()       (L415)
            //   2) NotificationCard 内部的 X 按钮 onAction → hide() (内嵌于 cardBuilder 内)
            //   3) onClick 回调 —— 不触发 hide,只通知业务方用户点了 box(见下方 setOnMouseClicked)
            //
            // 修复 Bug 3 (M19.51 同族): 之前 box click handler 末尾也调用 hide(),
            // 导致: ① 点 body 任何位置都关掉通知;② 点 X 时事件 bubble-up 重复触发 hide;
            // ③ content(Hyperlink) 等自定义节点被 bubble-up 截胡,根本无法点击。
            // 现仅触发 onClick,不再 hide —— 与 Ant Design Notification 默认语义对齐。
            if (config.closable && config.onClick != null) {
                notificationBox.setOnMouseClicked(e -> config.onClick.accept(null));
            }

            container.addEntry(entry);
        });
    }

    private static void hide(NotificationEntry entry, Builder config) {
        FadeTransition fadeOut = new FadeTransition(AnimationDuration.FAST, entry.box);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            entry.popup.hide();
            NotificationContainer container = containers.get(config.placement);
            container.removeEntry(entry);
        });
        fadeOut.play();
    }

    private static NotificationCard.Type convertType(Type type) {
        return switch (type) {
            case SUCCESS -> NotificationCard.Type.SUCCESS;
            case ERROR -> NotificationCard.Type.ERROR;
            case WARNING -> NotificationCard.Type.WARNING;
            case INFO -> NotificationCard.Type.INFO;
        };
    }
}
