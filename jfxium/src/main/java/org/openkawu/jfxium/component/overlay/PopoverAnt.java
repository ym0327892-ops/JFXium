package org.openkawu.jfxium.component.overlay;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.openkawu.jfxium.component.base.PopoverPanel;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

/**
 * JFXium 气泡卡片组件 - 对标 Ant Design Popover。
 *
 * <p><b>定位</b>：在目标元素旁弹出内容卡片（标题 + 自定义内容），
 * 比 TooltipAnt 更重（可放任意节点），比 ModalAnt 更轻（不打断操作流）。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>触发方式</b>：CLICK（默认）/ HOVER（鼠标悬停显示，移出 100ms 后关闭）</li>
 *   <li><b>弹出位置</b>：BOTTOM_CENTER（默认）/ TOP_CENTER / TOP_LEFT / TOP_RIGHT</li>
 *   <li><b>自定义内容</b>：content(Node) 可放入任意节点（表格、表单、图片等）</li>
 *   <li><b>可关闭</b>：卡片内带 × 关闭按钮</li>
 *   <li><b>动画</b>：150ms FadeIn</li>
 *   <li><b>点击外部关闭</b>：setAutoHide(true)</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 点击触发：显示用户详情
 * PopoverAnt.create()
 *     .title("用户信息")
 *     .content(new VBox(8,
 *         new Label("姓名：张三"),
 *         new Label("邮箱：zhangsan@example.com")))
 *     .target(avatarNode)
 *     .build();  // build() 时自动绑定触发事件
 *
 * // Hover 触发：显示图表详情
 * PopoverAnt.create()
 *     .title("数据详情")
 *     .content(chartDetailNode)
 *     .target(chartNode)
 *     .trigger(PopoverAnt.Trigger.HOVER)
 *     .placement(Pos.TOP_CENTER)
 *     .build();
 * }</pre>
 *
 * @see org.openkawu.jfxium.component.control.TooltipAnt 轻量文本提示（仅文本）
 * @see PopconfirmAnt 带确认/取消按钮的确认框
 */
public class PopoverAnt {

    public enum Trigger {
        CLICK, HOVER
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private Node content = null;
        private Node target = null;
        private Pos placement = Pos.BOTTOM_CENTER;
        private Trigger trigger = Trigger.CLICK;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder target(Node target) {
            this.target = target;
            return this;
        }

        public Builder placement(Pos placement) {
            this.placement = placement;
            return this;
        }

        public Builder trigger(Trigger trigger) {
            this.trigger = trigger;
            return this;
        }

        public Popover build() {
            return new Popover(this);
        }
    }

    public static class Popover {
        private final Builder config;
        private Popup popup;
        private VBox panel;

        private Popover(Builder config) {
            this.config = config;
            setupTrigger();
        }

        private void setupTrigger() {
            if (config.target == null) return;

            if (config.trigger == Trigger.HOVER) {
                config.target.setOnMouseEntered(e -> show());
                config.target.setOnMouseExited(e -> {
                    javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(100));
                    delay.setOnFinished(ev -> hide());
                    delay.play();
                });
            } else {
                config.target.setOnMouseClicked(e -> {
                    if (popup != null && popup.isShowing()) {
                        hide();
                    } else {
                        show();
                    }
                });
            }
        }

        public void show() {
            if (config.target == null) return;
            if (popup != null && popup.isShowing()) return;

            popup = new Popup();
            popup.setAutoHide(true);
            popup.setAutoFix(true);

            panel = new PopoverPanel.Builder()
                .title(config.title)
                .content(config.content)
                .closable(true)
                .onClose(() -> hide())
                .build();

            panel.setOnMouseClicked(e -> {
                hide();
            });

            popup.getContent().add(panel);

            javafx.geometry.Bounds bounds = config.target.localToScreen(config.target.getBoundsInLocal());
            double x, y;

            switch (config.placement) {
                case TOP_CENTER -> {
                    x = bounds.getMinX() + (bounds.getWidth() / 2) - 100;
                    y = bounds.getMinY() - 8;
                }
                case BOTTOM_CENTER -> {
                    x = bounds.getMinX() + (bounds.getWidth() / 2) - 100;
                    y = bounds.getMaxY() + 8;
                }
                case TOP_LEFT -> {
                    x = bounds.getMinX();
                    y = bounds.getMinY() - 8;
                }
                case TOP_RIGHT -> {
                    x = bounds.getMaxX() - 200;
                    y = bounds.getMinY() - 8;
                }
                default -> {
                    x = bounds.getMinX();
                    y = bounds.getMaxY() + 8;
                }
            }

            popup.show(config.target, x, y);

            FadeTransition fade = new FadeTransition(Duration.millis(150), panel);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }

        public void hide() {
            if (popup != null && popup.isShowing()) {
                popup.hide();
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
