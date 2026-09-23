package org.openkawu.jfxium.component.overlay;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.openkawu.jfxium.component.base.NotificationCard;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.AnimationDuration;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * JFXium 桌面级通知组件 —— 锚定操作系统屏幕（{@link Screen} 坐标）四角，默认右下角。
 *
 * <p><b>定位</b>：与 {@link NotificationAnt}（锚定应用窗口的 {@link javafx.stage.Popup}）
 * 严格区分。本组件用独立的透明、置顶 {@link Stage} 承载，坐标基于屏幕可视区域
 * （{@link Screen#getVisualBounds()}，已排除任务栏 / Dock），因此：</p>
 * <ul>
 *   <li>贴屏幕角落，不跟随主窗口移动 / 缩放；</li>
 *   <li>主窗口<b>最小化后仍能弹出</b>；</li>
 *   <li>{@code alwaysOnTop} 盖在其它程序之上，类似系统通知。</li>
 * </ul>
 *
 * <p><b>生命周期</b>：要求至少有一个可见的主 {@link Stage} 存在（作为"应用存活"前提与焦点归还目标，
 * <b>但不做 owner 父子绑定</b>——避免 toast 随主窗口移动 / 最小化）；找不到主窗口时静默 no-op。
 * 纯无窗口后台场景本期不支持。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>类型</b>：SUCCESS / ERROR / WARNING / INFO（带对应图标，复用 NotificationCard）</li>
 *   <li><b>位置</b>：TOP_LEFT / TOP_CENTER / TOP_RIGHT / BOTTOM_LEFT / BOTTOM_RIGHT（默认 BOTTOM_RIGHT）</li>
 *   <li><b>动画</b>：FadeIn + SlideIn（四角横向滑入，TOP_CENTER 从上方滑入），FadeOut</li>
 *   <li><b>自动消失</b>：duration 秒后自动关闭（默认 5s，0 = 不自动关闭）</li>
 *   <li><b>关闭方式</b>：点 X（closable=true）关闭并触发 onClose；duration 到点自动关闭；
 *       closeOnClick=true 时点卡片本体关闭。默认点 body 仅触发 onClick、不关闭</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 快捷调用：右下角成功通知
 * DesktopNotificationAnt.success("保存成功", "数据已保存到服务器");
 *
 * // Builder 用法：左上角错误通知，不自动消失
 * DesktopNotificationAnt.create()
 *     .title("连接失败")
 *     .description("无法连接到数据库")
 *     .type(DesktopNotificationAnt.Type.ERROR)
 *     .placement(DesktopNotificationAnt.Placement.TOP_LEFT)
 *     .duration(0)
 *     .build()
 *     .show();
 * }</pre>
 *
 * @see NotificationAnt 锚定应用窗口的通知（Popup 实现）
 */
public class DesktopNotificationAnt {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }

    public enum Placement {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    /** 卡片宽度 / 屏幕边距 —— 与 NotificationAnt 视觉一致。 */
    private static final double CARD_WIDTH = 384;
    private static final double MARGIN = 24;

    /** 每个 Placement 一个持久 container（懒创建，复用同一个透明 Stage）。 */
    private static final Map<Placement, DesktopContainer> containers = new EnumMap<>(Placement.class);

    private DesktopNotificationAnt() {
        // 静态工具入口，禁止实例化
    }

    // ============================================================
    // Builder / Result
    // ============================================================

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private String description = "";
        private Node content = null;
        private Type type = Type.INFO;
        private Placement placement = Placement.BOTTOM_RIGHT;
        private int durationSeconds = 5;
        private boolean closable = true;
        private boolean closeOnClick = false;
        private Consumer<Void> onClose = null;
        private Consumer<Void> onClick = null;

        public Builder title(String title) {
            this.title = title == null ? "" : title;
            return this;
        }

        public Builder description(String description) {
            this.description = description == null ? "" : description;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder type(Type type) {
            this.type = type == null ? Type.INFO : type;
            return this;
        }

        public Builder placement(Placement placement) {
            this.placement = placement == null ? Placement.BOTTOM_RIGHT : placement;
            return this;
        }

        public Builder duration(int seconds) {
            this.durationSeconds = Math.max(0, seconds);
            return this;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        /**
         * 点击卡片本体是否关闭通知。
         * <ul>
         *   <li>{@code true}：点卡片任意位置（X 按钮除外）先触发 onClick（若有）再关闭。</li>
         *   <li>{@code false}（默认）：点 body 仅触发 onClick、不关闭 —— 沿用 NotificationAnt 语义，
         *       便于放 content(Node) 交互。</li>
         * </ul>
         */
        public Builder closeOnClick(boolean closeOnClick) {
            this.closeOnClick = closeOnClick;
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

        public DesktopResult build() {
            return new DesktopResult(this);
        }
    }

    public static class DesktopResult {
        private final Builder config;

        DesktopResult(Builder config) {
            this.config = config;
        }

        public void show() {
            DesktopNotificationAnt.show(config);
        }
    }

    // ============================================================
    // 静态便捷方法（与 NotificationAnt 对齐：便捷入口不锁死参数）
    // ============================================================

    public static void success(String title, String description) {
        showStatic(title, description, Type.SUCCESS, 5, Placement.BOTTOM_RIGHT);
    }

    public static void error(String title, String description) {
        showStatic(title, description, Type.ERROR, 5, Placement.BOTTOM_RIGHT);
    }

    public static void warning(String title, String description) {
        showStatic(title, description, Type.WARNING, 5, Placement.BOTTOM_RIGHT);
    }

    public static void info(String title, String description) {
        showStatic(title, description, Type.INFO, 5, Placement.BOTTOM_RIGHT);
    }

    /** {@link #success(String, String)} 指定 duration（秒），0 = 不自动消失。 */
    public static void success(String title, String description, int durationSeconds) {
        showStatic(title, description, Type.SUCCESS, durationSeconds, Placement.BOTTOM_RIGHT);
    }

    /** {@link #success(String, String)} 指定 placement。 */
    public static void success(String title, String description, Placement placement) {
        showStatic(title, description, Type.SUCCESS, 5, placement);
    }

    /** {@link #error(String, String)} 指定 duration（秒），0 = 不自动消失。 */
    public static void error(String title, String description, int durationSeconds) {
        showStatic(title, description, Type.ERROR, durationSeconds, Placement.BOTTOM_RIGHT);
    }

    /** {@link #error(String, String)} 指定 placement。 */
    public static void error(String title, String description, Placement placement) {
        showStatic(title, description, Type.ERROR, 5, placement);
    }

    /** {@link #warning(String, String)} 指定 duration（秒），0 = 不自动消失。 */
    public static void warning(String title, String description, int durationSeconds) {
        showStatic(title, description, Type.WARNING, durationSeconds, Placement.BOTTOM_RIGHT);
    }

    /** {@link #warning(String, String)} 指定 placement。 */
    public static void warning(String title, String description, Placement placement) {
        showStatic(title, description, Type.WARNING, 5, placement);
    }

    /** {@link #info(String, String)} 指定 duration（秒），0 = 不自动消失。 */
    public static void info(String title, String description, int durationSeconds) {
        showStatic(title, description, Type.INFO, durationSeconds, Placement.BOTTOM_RIGHT);
    }

    /** {@link #info(String, String)} 指定 placement。 */
    public static void info(String title, String description, Placement placement) {
        showStatic(title, description, Type.INFO, 5, placement);
    }

    private static void showStatic(String title, String description, Type type, int durationSeconds, Placement placement) {
        create()
            .title(title).description(description)
            .type(type)
            .duration(durationSeconds)
            .placement(placement)
            .build().show();
    }

    // ============================================================
    // 核心显示逻辑
    // ============================================================

    private static void show(Builder config) {
        javafx.application.Platform.runLater(() -> {
            Stage owner = findOwnerStage();
            if (owner == null) return;  // 需有主窗口，否则静默 no-op

            DesktopContainer container = containers.computeIfAbsent(
                config.placement, p -> new DesktopContainer(p, owner));

            // entry 需要在 onClose 里被引用，而 onClose 又要在 build() 前设定 ——
            // 用 1 元素持有器打破先有鸡还是先有蛋：onClose 触发时 ref[0] 早已就绪。
            final DesktopEntry[] ref = new DesktopEntry[1];

            // X 按钮点击：先移除卡片，再触发业务 onClose。closable=false 时 NotificationCard 不渲染 X，此回调不触发。
            Runnable closeAction = () -> {
                container.removeEntry(ref[0]);
                if (config.onClose != null) config.onClose.accept(null);
            };

            VBox card = new NotificationCard.Builder()
                .title(config.title)
                .description(config.description)
                .type(convertType(config.type))
                .closable(config.closable)
                .onClose(closeAction)
                .content(config.content)
                .build();

            DesktopEntry entry = new DesktopEntry(card, config);
            ref[0] = entry;

            // 点击行为：
            //   closeOnClick=true  → 点 body 先触发 onClick（若有）再关闭
            //   closeOnClick=false → 点 body 仅触发 onClick，不关闭（沿用 NotificationAnt 语义）
            if (config.closeOnClick) {
                card.setOnMouseClicked(e -> {
                    if (config.onClick != null) config.onClick.accept(null);
                    container.removeEntry(ref[0]);
                });
            } else if (config.onClick != null) {
                card.setOnMouseClicked(e -> config.onClick.accept(null));
            }

            // 入场动画：四角从对应水平方向滑入；TOP_CENTER 从上方垂直滑入。
            boolean center = config.placement == Placement.TOP_CENTER;
            boolean fromLeft = config.placement == Placement.TOP_LEFT
                || config.placement == Placement.BOTTOM_LEFT;
            card.setOpacity(0);

            FadeTransition fadeIn = new FadeTransition(AnimationDuration.SLOW, card);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition slideIn = new TranslateTransition(AnimationDuration.SLOW, card);
            slideIn.setInterpolator(Interpolator.EASE_OUT);
            if (center) {
                card.setTranslateY(-20);
                slideIn.setFromY(-20);
                slideIn.setToY(0);
            } else {
                card.setTranslateX(fromLeft ? -20 : 20);
                slideIn.setFromX(fromLeft ? -20 : 20);
                slideIn.setToX(0);
            }

            new ParallelTransition(fadeIn, slideIn).play();

            container.addEntry(entry);

            // 自动消失
            if (config.durationSeconds > 0) {
                final DesktopEntry timerEntry = entry;
                PauseTransition delay = new PauseTransition(Duration.seconds(config.durationSeconds));
                delay.setOnFinished(e -> hide(container, timerEntry));
                delay.play();
            }
        });
    }

    private static void hide(DesktopContainer container, DesktopEntry entry) {
        FadeTransition fadeOut = new FadeTransition(AnimationDuration.FAST, entry.card);
        fadeOut.setFromValue(entry.card.getOpacity());
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> container.removeEntry(entry));
        fadeOut.play();
    }

    /** 取第一个可见的主 Stage 作 owner（同 NotificationAnt 的做法），排除本组件自己的透明 Stage。 */
    private static Stage findOwnerStage() {
        return Window.getWindows().stream()
            .filter(Window::isShowing)
            .filter(w -> w instanceof Stage)
            .map(w -> (Stage) w)
            .filter(s -> !isDesktopStage(s))
            .findFirst()
            .orElse(null);
    }

    private static boolean isDesktopStage(Stage stage) {
        for (DesktopContainer c : containers.values()) {
            if (c.stage == stage) return true;
        }
        return false;
    }

    private static NotificationCard.Type convertType(Type type) {
        return switch (type) {
            case SUCCESS -> NotificationCard.Type.SUCCESS;
            case ERROR -> NotificationCard.Type.ERROR;
            case WARNING -> NotificationCard.Type.WARNING;
            case INFO -> NotificationCard.Type.INFO;
        };
    }

    // ============================================================
    // 内部结构：Entry / Container
    // ============================================================

    private static class DesktopEntry {
        final VBox card;
        final Builder config;

        DesktopEntry(VBox card, Builder config) {
            this.card = card;
            this.config = config;
        }
    }

    /**
     * 每个屏幕角落一个持久载体：透明、置顶、owner 绑定主 Stage 的 {@link Stage}，
     * 内含一个 {@link VBox} 堆叠多条通知卡片。
     */
    private static class DesktopContainer {
        final Placement placement;
        final Stage stage;
        final VBox root;
        final Stage owner;

        DesktopContainer(Placement placement, Stage owner) {
            this.placement = placement;
            this.owner = owner;

            this.root = new VBox();
            this.root.getStyleClass().add(JfxStyles.DESKTOP_NOTIFICATION_CONTAINER);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            // 复用主 Stage 的样式表，保证卡片主题一致。
            if (owner.getScene() != null) {
                scene.getStylesheets().addAll(owner.getScene().getStylesheets());
            }

            this.stage = new Stage();
            this.stage.initStyle(StageStyle.TRANSPARENT);
            // 刻意不 initOwner(owner):owned 子窗口会随主窗口移动/最小化,且部分平台会把子窗口
            // 位置约束在 owner 附近 —— 这正是"跟随窗口/不独立"的根因。桌面 toast 要独立于主窗口,
            // 只把 owner 当作"应用存活"的前提(findOwnerStage 判空)与焦点归还目标,不做父子绑定。
            this.stage.setAlwaysOnTop(true);
            this.stage.setResizable(false);
            this.stage.setScene(scene);

            // BOTTOM_* 的 y 依赖容器高度，layout 完成后重算；TOP_* 的 y 固定，无需监听。
            if (placement == Placement.BOTTOM_LEFT || placement == Placement.BOTTOM_RIGHT) {
                this.root.heightProperty().addListener((obs, ov, nv) -> updatePosition());
            }

            // 主窗口关闭(showing→false)时收起 toast 并把 container 从静态 map 移除：
            // ① 独立 Stage 不拖住 JVM 退出；② owner 死后下次同 placement 通知需用新 owner
            // 重建 container，否则 owner 永久固化首个 Stage，新主窗不弹通知、焦点归还到死窗口。
            javafx.beans.value.ChangeListener<Boolean> ownerCloseListener =
                new javafx.beans.value.ChangeListener<>() {
                    @Override
                    public void changed(javafx.beans.value.ObservableValue<? extends Boolean> obs,
                                        Boolean was, Boolean showing) {
                        if (!showing) {
                            owner.showingProperty().removeListener(this);
                            root.getChildren().clear();
                            stage.hide();
                            containers.remove(placement);
                        }
                    }
                };
            owner.showingProperty().addListener(ownerCloseListener);
        }

        void addEntry(DesktopEntry entry) {
            root.getChildren().add(entry.card);
            if (!stage.isShowing()) {
                stage.show();
                // Stage 显示会抢焦点 —— 立即把焦点还给主窗口（桌面 toast 不应夺焦）。
                // 个别平台可能有一次轻微闪烁，可接受。
                owner.requestFocus();
            }
            // 强制同步 layout，让容器高度立刻有真值，避免 BOTTOM_* 首次算出 height=0 的错位 y。
            root.applyCss();
            root.layout();
            stage.sizeToScene();
            updatePosition();
        }

        void removeEntry(DesktopEntry entry) {
            root.getChildren().remove(entry.card);
            if (root.getChildren().isEmpty()) {
                stage.hide();  // 空了就藏起来（不销毁，复用）
            } else {
                stage.sizeToScene();
                updatePosition();
            }
        }

        void updatePosition() {
            // 本期仅主屏定位；未来可扩展为鼠标所在屏 Screen.getScreensForRectangle(...)。
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            double stageWidth = Math.max(stage.getWidth(), CARD_WIDTH);
            double stageHeight = stage.getHeight();
            if (Double.isNaN(stageHeight)) stageHeight = 0;

            double x;
            double y;
            switch (placement) {
                case TOP_LEFT -> {
                    x = bounds.getMinX() + MARGIN;
                    y = bounds.getMinY() + MARGIN;
                }
                case TOP_CENTER -> {
                    x = bounds.getMinX() + (bounds.getWidth() - stageWidth) / 2;
                    y = bounds.getMinY() + MARGIN;
                }
                case TOP_RIGHT -> {
                    x = bounds.getMaxX() - stageWidth - MARGIN;
                    y = bounds.getMinY() + MARGIN;
                }
                case BOTTOM_LEFT -> {
                    x = bounds.getMinX() + MARGIN;
                    y = bounds.getMaxY() - MARGIN - stageHeight;
                }
                case BOTTOM_RIGHT -> {
                    x = bounds.getMaxX() - stageWidth - MARGIN;
                    y = bounds.getMaxY() - MARGIN - stageHeight;
                }
                default -> {
                    x = bounds.getMaxX() - stageWidth - MARGIN;
                    y = bounds.getMaxY() - MARGIN - stageHeight;
                }
            }
            stage.setX(x);
            stage.setY(y);
        }
    }
}
