package org.openkawu.jfxium.component.overlay;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.openkawu.jfxium.component.base.MessageCard;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.util.AnimationDuration;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 全局消息提示组件 - 对标 Ant Design Message（组合式，静态方法调用）。
 *
 * <p><b>定位</b>：全局顶部 / 底部 / 中心短暂提示，自动消失，
 * 与 NotificationAnt（右下角通知）严格区分。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>类型</b>：SUCCESS / ERROR / WARNING / INFO / LOADING</li>
 *   <li><b>位置</b>：TOP（默认）/ BOTTOM / CENTER（中间只显示一个）</li>
 *   <li><b>动画</b>：顶部/底部滑入，中间淡入</li>
 *   <li><b>手动关闭</b>：返回 MessageResult 可主动 close()</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * MessageAnt.success("保存成功");
 * MessageAnt.error("网络错误", 5);
 * MessageAnt.loading("正在处理...").close();   // loading 返回句柄
 * }</pre>
 */
public class MessageAnt {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO, LOADING
    }

    public enum Position {
        TOP,     // 顶部居中（默认）
        BOTTOM,  // 底部居中
        CENTER   // 屏幕中间（只显示一个，新消息替换旧消息）
    }

    private static final int MAX_MESSAGES = 5;
    private static final List<MessageEntry> activeMessages = new ArrayList<>();
    private static volatile Position defaultPosition = Position.TOP;  // 默认位置
    private static volatile double defaultEdgeOffset = 24;  // 顶部/底部距窗口边缘距离（px）
    private static final double STACK_GAP = 8;  // 多条堆叠间距

    /** 设置顶部/底部提示相对窗口边缘的距离（像素），默认 24。非法值回退默认。 */
    public static void setEdgeOffset(double offset) {
        defaultEdgeOffset = Double.isFinite(offset) ? Math.max(0, offset) : 24;
    }

    /** @deprecated 命名含糊，请用 {@link #setEdgeOffset(double)}。 */
    @Deprecated
    public static void setTopOffset(double offset) {
        setEdgeOffset(offset);
    }

    private static class MessageEntry {
        final Popup popup;
        final HBox box;
        final Window owner;
        final Position position;
        PauseTransition timer;
        boolean closing = false;

        MessageEntry(Popup popup, HBox box, Window owner, Position position) {
            this.popup = popup;
            this.box = box;
            this.owner = owner;
            this.position = position;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String content = "";
        private Type type = Type.INFO;
        private int durationSeconds = 3;
        private Position position = null;  // null = build 时取 defaultPosition

        public Builder content(String content) {
            this.content = content == null ? "" : content;
            return this;
        }

        public Builder type(Type type) {
            this.type = type == null ? Type.INFO : type;
            return this;
        }

        public Builder duration(int seconds) {
            this.durationSeconds = Math.max(0, seconds);
            return this;
        }

        public Builder position(Position position) {
            this.position = position;  // null 合法 → build 时取 defaultPosition
            return this;
        }

        public MessageResult build() {
            Position eff = position != null ? position : defaultPosition;
            return new MessageResult(content, type, durationSeconds, eff);
        }
    }

    public static class MessageResult {
        private final String content;
        private final Type type;
        private final int durationSeconds;
        private final Position position;
        private MessageEntry entry;
        private boolean closeRequested = false;

        MessageResult(String content, Type type, int durationSeconds, Position position) {
            this.content = content;
            this.type = type;
            this.durationSeconds = durationSeconds;
            this.position = position;
        }

        /** 显示。FX 线程上同步完成（entry 立即就绪）；其他线程异步显示。 */
        public MessageResult show() {
            if (javafx.application.Platform.isFxApplicationThread()) {
                this.entry = showAndReturn(content, type, durationSeconds, position);
                if (closeRequested) doClose();
            } else {
                javafx.application.Platform.runLater(() -> {
                    this.entry = showAndReturn(content, type, durationSeconds, position);
                    if (closeRequested) doClose();
                });
            }
            return this;
        }

        /** 关闭（幂等，可跨线程调用）。若消息尚未显示，登记为显示后立即关闭。 */
        public void close() {
            closeRequested = true;
            if (entry != null) {
                if (javafx.application.Platform.isFxApplicationThread()) {
                    doClose();
                } else {
                    javafx.application.Platform.runLater(this::doClose);
                }
            }
        }

        private void doClose() {
            MessageEntry e = entry;
            if (e == null || e.closing) return;
            activeMessages.remove(e);
            hideMessage(e);
            repositionMessages(e.owner);
            entry = null;
        }
    }

    // ============================================================
    // 静态快捷方法
    // ============================================================

    public static void show(String content) {
        show(content, Type.INFO, 3, defaultPosition);
    }

    public static void success(String content) {
        show(content, Type.SUCCESS, 3, defaultPosition);
    }

    public static void error(String content) {
        show(content, Type.ERROR, 3, defaultPosition);
    }

    public static void warning(String content) {
        show(content, Type.WARNING, 3, defaultPosition);
    }

    public static void info(String content) {
        show(content, Type.INFO, 3, defaultPosition);
    }

    /** Loading（默认不自动消失），返回句柄供手动 close()。 */
    public static MessageResult loading(String content) {
        return showResult(content, Type.LOADING, 0, defaultPosition);
    }

    /** {@link #success(String)} 指定秒数。 */
    public static void success(String content, int durationSeconds) {
        show(content, Type.SUCCESS, durationSeconds, defaultPosition);
    }

    /** {@link #error(String)} 指定秒数。 */
    public static void error(String content, int durationSeconds) {
        show(content, Type.ERROR, durationSeconds, defaultPosition);
    }

    /** {@link #warning(String)} 指定秒数。 */
    public static void warning(String content, int durationSeconds) {
        show(content, Type.WARNING, durationSeconds, defaultPosition);
    }

    /** {@link #info(String)} 指定秒数。 */
    public static void info(String content, int durationSeconds) {
        show(content, Type.INFO, durationSeconds, defaultPosition);
    }

    /** {@link #loading(String)} 指定超时秒数（默认 0 = 永不自动消失）。 */
    public static MessageResult loading(String content, int durationSeconds) {
        return showResult(content, Type.LOADING, durationSeconds, defaultPosition);
    }

    public static void show(String content, Type type, int durationSeconds) {
        show(content, type, durationSeconds, defaultPosition);
    }

    /** 精确 Duration 重载，null 走默认 3 秒。 */
    public static void show(String content, Type type, Duration duration) {
        double seconds = duration == null ? 3.0 : Math.max(0.0, duration.toSeconds());
        int secondsInt = (int) Math.ceil(seconds);
        show(content, type, secondsInt, defaultPosition);
    }

    public static void show(String content, Type type, int durationSeconds, Position position) {
        showResult(content, type, durationSeconds, position);
    }

    private static MessageResult showResult(String content, Type type, int durationSeconds, Position position) {
        MessageResult result = new MessageResult(
                content == null ? "" : content,
                type == null ? Type.INFO : type,
                Math.max(0, durationSeconds),
                position == null ? defaultPosition : position);
        result.show();
        return result;
    }

    /**
     * 显示消息。调用方须在 FX 线程。返回的 MessageEntry 在 popup 定位完成后就绪。
     */
    private static MessageEntry showAndReturn(String content, Type type, int durationSeconds, Position position) {
        Window window = Window.getWindows().stream()
            .filter(Window::isShowing)
            .filter(w -> w instanceof javafx.stage.Stage)
            .findFirst()
            .orElse(null);

        if (window == null) return null;

        // 同位置桶的容量上限（CENTER 固定 1，单独处理）
        if (position != Position.CENTER) {
            long sameBucket = activeMessages.stream()
                .filter(e -> e.owner == window && e.position == position)
                .count();
            if (sameBucket >= MAX_MESSAGES) {
                activeMessages.stream()
                    .filter(e -> e.owner == window && e.position == position)
                    .findFirst()
                    .ifPresent(old -> {
                        activeMessages.remove(old);
                        hideMessage(old);
                    });
            }
        }

        HBox messageBox = new MessageCard.Builder()
            .content(content)
            .type(convertType(type))
            .build();

        Popup popup = new Popup();
        popup.getContent().add(messageBox);

        // 不可见显示，等布局取真实尺寸，避免量到 0 用占位尺寸导致错位。
        popup.setOpacity(0);
        popup.show(window);

        MessageEntry entry = new MessageEntry(popup, messageBox, window, position);

        // owner 关闭 → 清理僵尸 entry（popup 随 owner 隐藏但列表项不会自动移除）。
        window.showingProperty().addListener(new javafx.beans.value.ChangeListener<Boolean>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Boolean> obs,
                                Boolean ov, Boolean nv) {
                if (!nv && activeMessages.remove(entry)) {
                    if (entry.timer != null) entry.timer.stop();
                }
                window.showingProperty().removeListener(this);
            }
        });

        waitForLayout(messageBox, popup, window, position, durationSeconds, entry, 0);
        return entry;
    }

    /** 有界等待布局完成（计数为局部参数，跨消息不互相干扰），再定位与动画。 */
    private static void waitForLayout(HBox messageBox, Popup popup, Window window,
                                      Position position, int durationSeconds,
                                      MessageEntry entry, int attempts) {
        double w = messageBox.getWidth();
        double h = messageBox.getHeight();
        if ((w > 0 && h > 0) || attempts >= 50) {
            double ew = w > 0 ? w : 300;
            double eh = h > 0 ? h : 40;
            placeAndAnimate(messageBox, popup, window, position, durationSeconds, entry, ew, eh);
            return;
        }
        PauseTransition retry = new PauseTransition(Duration.millis(16));
        retry.setOnFinished(e -> waitForLayout(messageBox, popup, window, position,
                durationSeconds, entry, attempts + 1));
        retry.play();
    }

    private static void placeAndAnimate(HBox messageBox, Popup popup, Window window,
                                        Position position, int durationSeconds,
                                        MessageEntry entry, double messageWidth, double messageHeight) {
        double x = window.getX() + (window.getWidth() - messageWidth) / 2;
        double y;

        if (position == Position.CENTER) {
            // CENTER 只显示一个：替换同 owner 旧 CENTER。
            activeMessages.stream()
                .filter(e -> e.owner == window && e.position == Position.CENTER)
                .findFirst()
                .ifPresent(old -> {
                    activeMessages.remove(old);
                    hideMessage(old);
                });
            y = window.getY() + (window.getHeight() - messageHeight) / 2;
        } else {
            int stackIndex = (int) activeMessages.stream()
                .filter(e -> e.owner == window && e.position == position)
                .count();
            double used = stackIndex * (messageHeight + STACK_GAP);
            if (position == Position.BOTTOM) {
                y = window.getY() + window.getHeight() - defaultEdgeOffset - messageHeight - used;
            } else {
                y = window.getY() + defaultEdgeOffset + used;
            }
        }

        popup.setX(x);
        popup.setY(y);
        // 恢复 popup 窗口透明度：定位前为取尺寸临时置 0，不恢复会让 messageBox
        // 的淡入被窗口整体 0 透明度乘掉 —— Windows 下消息完全不显示。
        popup.setOpacity(1);

        activeMessages.add(entry);

        messageBox.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(AnimationDuration.FAST, messageBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setInterpolator(Interpolator.EASE_OUT);

        if (position == Position.CENTER) {
            fadeIn.play();
        } else {
            TranslateTransition slideIn = new TranslateTransition(AnimationDuration.FAST, messageBox);
            slideIn.setFromY(position == Position.BOTTOM ? 20 : -20);
            slideIn.setToY(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);
            new javafx.animation.ParallelTransition(fadeIn, slideIn).play();
        }

        if (durationSeconds > 0) {
            PauseTransition delay = new PauseTransition(Duration.seconds(durationSeconds));
            delay.setOnFinished(e -> {
                if (activeMessages.remove(entry) && !entry.closing) {
                    hideMessage(entry);
                    repositionMessages(window);
                }
            });
            delay.play();
            entry.timer = delay;
        }
    }

    private static void hideMessage(MessageEntry entry) {
        if (entry.closing) return;
        entry.closing = true;
        if (entry.timer != null) entry.timer.stop();
        FadeTransition fadeOut = new FadeTransition(AnimationDuration.FAST, entry.box);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> entry.popup.hide());
        fadeOut.play();
    }

    /** 重定位指定 owner 的全部消息（按真实卡片高度堆叠）。 */
    private static void repositionMessages(Window owner) {
        int topIndex = 0;
        int bottomIndex = 0;
        for (MessageEntry entry : activeMessages) {
            if (entry.owner != owner) continue;
            double w = entry.box.getWidth() > 0 ? entry.box.getWidth() : 300;
            double h = entry.box.getHeight() > 0 ? entry.box.getHeight() : 40;
            entry.popup.setX(owner.getX() + (owner.getWidth() - w) / 2);

            if (entry.position == Position.CENTER) {
                entry.popup.setY(owner.getY() + (owner.getHeight() - h) / 2);
            } else if (entry.position == Position.BOTTOM) {
                double used = bottomIndex * (h + STACK_GAP);
                entry.popup.setY(owner.getY() + owner.getHeight() - defaultEdgeOffset - h - used);
                bottomIndex++;
            } else {
                double used = topIndex * (h + STACK_GAP);
                entry.popup.setY(owner.getY() + defaultEdgeOffset + used);
                topIndex++;
            }
        }
    }

    private static MessageCard.Type convertType(Type type) {
        return switch (type) {
            case SUCCESS -> MessageCard.Type.SUCCESS;
            case ERROR -> MessageCard.Type.ERROR;
            case WARNING -> MessageCard.Type.WARNING;
            case INFO -> MessageCard.Type.INFO;
            case LOADING -> MessageCard.Type.LOADING;
        };
    }
}
