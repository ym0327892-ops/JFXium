package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.openkawu.jfxium.component.base.PopoverPanel;

public class PopoverAnt {

    public enum Trigger {
        CLICK, HOVER
    }

    public static class Builder {
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
