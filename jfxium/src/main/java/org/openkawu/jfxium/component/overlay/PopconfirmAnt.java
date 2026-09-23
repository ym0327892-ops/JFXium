package org.openkawu.jfxium.component.overlay;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.component.base.PopconfirmPanel;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.AnimationDuration;
import javafx.util.Duration;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.function.Consumer;

/**
 * JFXium 气泡确认框组件 - 对标 Ant Design Popconfirm。
 *
 * <p><b>定位</b>：在目标元素旁弹出轻量确认面板，包含标题 + 描述 + 确认/取消按钮，
 * 用于二次确认危险操作（删除、提交等）。比 ModalAnt 更轻量，不打断用户操作流。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>标题 + 描述</b>：双行文案，描述可留空</li>
 *   <li><b>自定义按钮文案</b>：okText / cancelText（null 时走 i18n 默认值）</li>
 *   <li><b>回调</b>：onConfirm / onCancel，回传 boolean</li>
 *   <li><b>目标锚定</b>：target(Node) 指定弹出位置（目标元素正下方 +8px）</li>
 *   <li><b>动画</b>：150ms FadeIn</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 删除确认
 * PopconfirmAnt.create()
 *     .title("确定删除？")
 *     .description("删除后无法恢复")
 *     .target(deleteButton)
 *     .onConfirm(ok -> doDelete())
 *     .build()
 *     .show();
 *
 * // 自定义按钮文案
 * PopconfirmAnt.create()
 *     .title("提交审批？")
 *     .okText("提交")
 *     .cancelText("再想想")
 *     .target(submitBtn)
 *     .onConfirm(ok -> submitForApproval())
 *     .build()
 *     .show();
 * }</pre>
 *
 * @see ModalAnt 重量级对话框（适合复杂交互）
 * @see org.openkawu.jfxium.component.overlay.MessageAnt 全局消息提示（无需确认）
 */
public class PopconfirmAnt {

    public enum Placement {
        TOP, TOP_LEFT, TOP_RIGHT, BOTTOM, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private String description = "";
        // null = 用 i18n 默认值；非 null = 调用方显式指定
        private String okText = null;
        private String cancelText = null;
        private Consumer<Boolean> onConfirm = null;
        private Consumer<Boolean> onCancel = null;
        private Node target = null;
        private Placement placement = Placement.BOTTOM;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder okText(String okText) {
            this.okText = okText;
            return this;
        }

        public Builder cancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder onConfirm(Consumer<Boolean> onConfirm) {
            this.onConfirm = onConfirm;
            return this;
        }

        public Builder onCancel(Consumer<Boolean> onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public Builder target(Node target) {
            this.target = target;
            return this;
        }

        /** 气泡弹出位置，默认 BOTTOM（目标下方 +8px）。 */
        public Builder placement(Placement placement) {
            this.placement = placement;
            return this;
        }

        public Popconfirm build() {
            return new Popconfirm(this);
        }
    }

    public static class Popconfirm {
        private final Builder config;
        private Popup popup;
        private int positionAttempts = 0;

        private Popconfirm(Builder config) {
            this.config = config;
        }

        public void show() {
            if (config.target == null) return;

            popup = new Popup();
            PopupThemes.bind(popup, config.target);
            VBox panel = new PopconfirmPanel.Builder()
                .title(config.title)
                .description(config.description)
                .okText(TextUtils.safeText(config.okText, Messages.get("popconfirm.ok")))
                .cancelText(TextUtils.safeText(config.cancelText, Messages.get("popconfirm.cancel")))
                .onConfirm(() -> {
                    hide();
                    if (config.onConfirm != null) {
                        config.onConfirm.accept(true);
                    }
                })
                .onCancel(() -> {
                    hide();
                    if (config.onCancel != null) {
                        config.onCancel.accept(false);
                    }
                })
                .build();

            popup.getContent().add(panel);

            // 先以不可见状态 show，让 panel 完成布局得到真实尺寸，
            // 避免 TOP 位置首次 getHeight()=0 / 对齐量 getWidth()=0 算出错位的 x/y。
            popup.setOpacity(0);
            popup.setX(0);
            popup.setY(0);
            popup.show(config.target, 0, 0);
            positionAttempts = 0;
            schedulePosition(panel);
        }

        /** 有界等待 panel 布局完成后再定位并可见（无错位闪烁）。 */
        private void schedulePosition(javafx.scene.layout.Region panel) {
            PauseTransition retry = new PauseTransition(Duration.millis(16));
            retry.setOnFinished(e -> {
                double w = panel.getWidth();
                double h = panel.getHeight();
                if (w > 0 && h > 0 || ++positionAttempts >= 50) {
                    applyPosition(panel, w, h);
                } else {
                    retry.play();
                }
            });
            retry.play();
        }

        private void applyPosition(javafx.scene.layout.Region panel, double panelWidth, double panelHeight) {
            javafx.geometry.Bounds bounds = config.target.localToScreen(config.target.getBoundsInLocal());
            double x, y;
            switch (config.placement) {
                case TOP -> {
                    x = bounds.getMinX();
                    y = bounds.getMinY() - 8 - panelHeight;
                }
                case TOP_LEFT -> {
                    x = bounds.getMinX();
                    y = bounds.getMinY() - 8 - panelHeight;
                }
                case TOP_RIGHT -> {
                    x = bounds.getMaxX() - panelWidth;
                    y = bounds.getMinY() - 8 - panelHeight;
                }
                case BOTTOM_LEFT -> {
                    x = bounds.getMinX();
                    y = bounds.getMaxY() + 8;
                }
                case BOTTOM_RIGHT -> {
                    x = bounds.getMaxX() - panelWidth;
                    y = bounds.getMaxY() + 8;
                }
                default -> {
                    // BOTTOM（默认）
                    x = bounds.getMinX();
                    y = bounds.getMaxY() + 8;
                }
            }
            popup.setX(x);
            popup.setY(y);
            popup.setOpacity(1);

            FadeTransition fade = new FadeTransition(AnimationDuration.ULTRA_FAST, popup.getContent().get(0));
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }

        public void hide() {
            if (popup != null) {
                popup.hide();
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
