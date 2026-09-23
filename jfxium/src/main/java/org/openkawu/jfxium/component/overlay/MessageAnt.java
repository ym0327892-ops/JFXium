package org.openkawu.jfxium.component.overlay;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
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
 * // 顶部成功提示（默认 3 秒自动消失）
 * MessageAnt.success("保存成功");
 *
 * // 底部错误提示（指定 5 秒）
 * MessageAnt.error("网络错误", 5, MessageAnt.Position.BOTTOM);
 *
 * // 精确 Duration（支持亚秒级，如 500ms / 2.5s）
 * MessageAnt.show("提示", MessageAnt.Type.INFO, Duration.millis(500));
 *
 * // Loading（默认不自动消失，需手动 close）
 * MessageResult loading = MessageAnt.loading("正在处理...");
 * // ... 异步完成后
 * loading.close();
 *
 * // Loading 指定超时（6 秒后自动消失）
 * MessageAnt.loading("正在处理...", 6);
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
    private static Position defaultPosition = Position.TOP;  // 默认位置
    private static double defaultTopOffset = 24;  // 顶部提示距离窗口上边缘的距离（px）

    /** 有界布局等待的失败计数（防止极端情况下重试循环永不退出）。 */
    private static int layoutWaitAttempts = 0;
    /**
     * 设置顶部/底部提示相对窗口边缘的距离（像素）。默认 24。
     * 传入负数或 NaN/Infinity 时回退为默认 24。
     */
    public static void setTopOffset(double offset) {
        defaultTopOffset = Double.isFinite(offset) ? Math.max(0, offset) : 24;
    }

    private static class MessageEntry {
        final Popup popup;
        final HBox box;
        final long showTime;
        final Position position;

        MessageEntry(Popup popup, HBox box, Position position) {
            this.popup = popup;
            this.box = box;
            this.showTime = System.currentTimeMillis();
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
        private Position position = defaultPosition;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder duration(int seconds) {
            this.durationSeconds = seconds;
            return this;
        }

        public Builder position(Position position) {
            this.position = position;
            return this;
        }

        public MessageResult build() {
            return new MessageResult(content, type, durationSeconds, position);
        }
    }

    public static class MessageResult {
        private final String content;
        private final Type type;
        private final int durationSeconds;
        private final Position position;
        private MessageEntry entry;

        MessageResult(String content, Type type, int durationSeconds, Position position) {
            this.content = content;
            this.type = type;
            this.durationSeconds = durationSeconds;
            this.position = position;
        }

        public void show() {
            this.entry = MessageAnt.showAndReturn(content, type, durationSeconds, position);
        }

        public void close() {
            if (entry != null) {
                activeMessages.remove(entry);
                hideMessage(entry);
                // 重新定位剩余消息
                javafx.stage.Window window = javafx.stage.Window.getWindows().stream()
                    .filter(javafx.stage.Window::isShowing)
                    .filter(w -> w instanceof javafx.stage.Stage)
                    .findFirst()
                    .orElse(null);
                if (window != null) {
                    repositionMessages(window);
                }
            }
        }
    }

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

    public static void loading(String content) {
        show(content, Type.LOADING, 0, defaultPosition);
    }

    // ============================================================
    // durationSeconds 重载（让业务方自由控制自动消失时间）
    // - durationSeconds > 0：N 秒后自动消失
    // - durationSeconds == 0：不自动消失（Loading 默认行为）
    // - durationSeconds < 0：按 0 处理
    // ============================================================

    /** {@link #success(String)} 指定 durationSeconds 秒（覆盖默认 3 秒）。 */
    public static void success(String content, int durationSeconds) {
        show(content, Type.SUCCESS, durationSeconds, defaultPosition);
    }

    /** {@link #error(String)} 指定 durationSeconds 秒（覆盖默认 3 秒）。 */
    public static void error(String content, int durationSeconds) {
        show(content, Type.ERROR, durationSeconds, defaultPosition);
    }

    /** {@link #warning(String)} 指定 durationSeconds 秒（覆盖默认 3 秒）。 */
    public static void warning(String content, int durationSeconds) {
        show(content, Type.WARNING, durationSeconds, defaultPosition);
    }

    /** {@link #info(String)} 指定 durationSeconds 秒（覆盖默认 3 秒）。 */
    public static void info(String content, int durationSeconds) {
        show(content, Type.INFO, durationSeconds, defaultPosition);
    }

    /** {@link #loading(String)} 指定 durationSeconds 秒后自动消失（默认 0 = 永不自动消失）。 */
    public static void loading(String content, int durationSeconds) {
        show(content, Type.LOADING, durationSeconds, defaultPosition);
    }

    public static void show(String content, Type type, int durationSeconds) {
        show(content, type, durationSeconds, defaultPosition);
    }

    /**
     * 精确 Duration 重载（支持亚秒级，如 {@code Duration.millis(500)} / {@code Duration.seconds(2.5)}）。
     * null 走默认 3 秒。
     */
    public static void show(String content, Type type, Duration duration) {
        double seconds = duration == null ? 3.0 : Math.max(0.0, duration.toSeconds());
        // 亚秒级向上取整（如 0.5s -> 1s，保证不立即消失）
        int secondsInt = (int) Math.ceil(seconds);
        show(content, type, secondsInt, defaultPosition);
    }

    /**
     * 显示消息并返回 MessageEntry（供 MessageResult.close() 使用）
     */
    private static MessageEntry showAndReturn(String content, Type type, int durationSeconds, Position position) {
        final MessageEntry[] resultEntry = new MessageEntry[1];
        
        javafx.application.Platform.runLater(() -> {
            javafx.stage.Window window = javafx.stage.Window.getWindows().stream()
                .filter(javafx.stage.Window::isShowing)
                .filter(w -> w instanceof javafx.stage.Stage)
                .findFirst()
                .orElse(null);

            if (window == null) return;

            while (activeMessages.size() >= MAX_MESSAGES) {
                MessageEntry oldest = activeMessages.remove(0);
                hideMessage(oldest);
            }

            HBox messageBox = new MessageCard.Builder()
                .content(content)
                .type(convertType(type))
                .build();

            Popup popup = new Popup();
            popup.getContent().add(messageBox);

            // 先显示 popup（不可见），让 messageBox 完成布局计算实际宽度
            popup.setOpacity(0);
            popup.show(window);

            // 等待布局完成后获取实际宽度/高度。单个 runLater 可能在 pulse 前触发，
            // 量到的是 0 而回退成 300/40 占位尺寸，导致 popup 被摆到错误位置，
            // 之后再跳到正确位置 —— 这就是「先闪烁一下再居中」的根因。
            // 这里用有界轮询：等真实尺寸就绪后再定位，绝不闪现错误位置。
            waitForLayout(messageBox, popup, window, position, durationSeconds, resultEntry);
        });
        
        return resultEntry[0];
    }

    /** 有界等待 messageBox 完成布局，取到真实尺寸后一次性定位并播放入场动画。 */
    private static void waitForLayout(HBox messageBox, Popup popup, javafx.stage.Window window,
                                      Position position, int durationSeconds, MessageEntry[] resultEntry) {
        double w = messageBox.getWidth();
        double h = messageBox.getHeight();
        if (w > 0 && h > 0) {
            layoutWaitAttempts = 0;
            placeAndAnimate(messageBox, popup, window, position, durationSeconds, resultEntry, w, h);
            return;
        }

        PauseTransition retry = new PauseTransition(Duration.millis(16));
        retry.setOnFinished(e -> waitForLayout(messageBox, popup, window, position, durationSeconds, resultEntry));
        // 有界：留 50 次（约 800ms）作为兜底，避免极端情况下永远不返回
        if (++layoutWaitAttempts < 50) {
            retry.play();
        } else {
            placeAndAnimate(messageBox, popup, window, position, durationSeconds, resultEntry, 300, 40);
        }
    }

    private static void placeAndAnimate(HBox messageBox, Popup popup, javafx.stage.Window window,
                                        Position position, int durationSeconds, MessageEntry[] resultEntry,
                                        double messageWidth, double messageHeight) {
        // 根据位置计算 X 和 Y 坐标
        double x = window.getX() + (window.getWidth() - messageWidth) / 2;
        double y;

        if (position == Position.CENTER) {
            // 中间：屏幕正中央（只显示一个，新消息替换旧消息）
            activeMessages.stream()
                .filter(e -> e.position == Position.CENTER)
                .findFirst()
                .ifPresent(oldEntry -> {
                    activeMessages.remove(oldEntry);
                    hideMessage(oldEntry);
                });

            y = window.getY() + (window.getHeight() - messageHeight) / 2;
        } else if (position == Position.BOTTOM) {
            // 底部：从窗口底部往上计算
            int bottomIndex = (int) activeMessages.stream()
                .filter(e -> e.position == Position.BOTTOM)
                .count();
            y = window.getY() + window.getHeight() - defaultTopOffset - ((bottomIndex + 1) * 50);
        } else {
            // 顶部：从窗口顶部往下计算
            int topIndex = (int) activeMessages.stream()
                .filter(e -> e.position == Position.TOP)
                .count();
            y = window.getY() + defaultTopOffset + (topIndex * 50);
        }

        popup.setX(x);
        popup.setY(y);

        MessageEntry entry = new MessageEntry(popup, messageBox, position);
        activeMessages.add(entry);
        resultEntry[0] = entry;

        messageBox.setOpacity(0);
        // 中间位置淡入淡出，顶部/底部滑入
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

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fadeIn, slideIn);
            pt.play();
        }

        if (durationSeconds > 0) {
            PauseTransition delay = new PauseTransition(Duration.seconds(durationSeconds));
            delay.setOnFinished(e -> {
                activeMessages.remove(entry);
                hideMessage(entry);
                repositionMessages(window);
            });
            delay.play();
        }
    }


    public static void show(String content, Type type, int durationSeconds, Position position) {
        showAndReturn(content, type, durationSeconds, position);
    }

    private static void hideMessage(MessageEntry entry) {
        FadeTransition fadeOut = new FadeTransition(AnimationDuration.FAST, entry.box);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> entry.popup.hide());
        fadeOut.play();
    }

    private static void repositionMessages(javafx.stage.Window window) {
        // 分别处理顶部、底部和中间的消息
        int topIndex = 0;
        int bottomIndex = 0;
        
        for (MessageEntry entry : activeMessages) {
            double messageWidth = entry.box.getWidth();
            if (messageWidth == 0) {
                messageWidth = 300;
            }
            double messageHeight = entry.box.getHeight();
            if (messageHeight == 0) {
                messageHeight = 40;
            }
            double baseX = window.getX() + (window.getWidth() - messageWidth) / 2;
            
            if (entry.position == Position.CENTER) {
                // 中间：屏幕正中央
                entry.popup.setX(baseX);
                entry.popup.setY(window.getY() + (window.getHeight() - messageHeight) / 2);
            } else if (entry.position == Position.BOTTOM) {
                // 底部消息：从下往上排列
                double baseY = window.getY() + window.getHeight() - defaultTopOffset;
                entry.popup.setX(baseX);
                entry.popup.setY(baseY - ((bottomIndex + 1) * 50));
                bottomIndex++;
            } else {
                // 顶部消息：从上往下排列
                double baseY = window.getY() + defaultTopOffset;
                entry.popup.setX(baseX);
                entry.popup.setY(baseY + (topIndex * 50));
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
