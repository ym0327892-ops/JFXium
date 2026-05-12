package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.openkawu.jfxium.component.base.PopconfirmPanel;

import java.util.function.Consumer;

public class PopconfirmAnt {

    public static class Builder {
        private String title = "";
        private String description = "";
        private String okText = "Yes";
        private String cancelText = "No";
        private Consumer<Boolean> onConfirm = null;
        private Consumer<Boolean> onCancel = null;
        private Node target = null;

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

        public Popconfirm build() {
            return new Popconfirm(this);
        }
    }

    public static class Popconfirm {
        private final Builder config;
        private Popup popup;

        private Popconfirm(Builder config) {
            this.config = config;
        }

        public void show() {
            if (config.target == null) return;

            popup = new Popup();

            VBox panel = new PopconfirmPanel.Builder()
                .title(config.title)
                .description(config.description)
                .okText(config.okText)
                .cancelText(config.cancelText)
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

            javafx.geometry.Bounds bounds = config.target.localToScreen(config.target.getBoundsInLocal());
            popup.show(config.target, bounds.getMinX(), bounds.getMaxY() + 8);

            FadeTransition fade = new FadeTransition(Duration.millis(150), popup.getContent().get(0));
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
