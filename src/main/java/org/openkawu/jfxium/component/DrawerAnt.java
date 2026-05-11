package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * JFXium Drawer Component
 * Inspired by Ant Design Drawer
 * A panel which slides in from the edge of the screen.
 */
public class DrawerAnt {

    public enum Placement {
        LEFT, RIGHT, TOP, BOTTOM
    }

    public enum Size {
        DEFAULT, LARGE
    }

    public static class Builder {
        private String title = "";
        private Node content = null;
        private Placement placement = Placement.RIGHT;
        private int width = 378;
        private int height = 378;
        private boolean mask = true;
        private boolean maskClosable = true;
        private Consumer<Boolean> onClose = null;
        private Node footer = null;
        private Size size = Size.DEFAULT;
        private Node extra = null;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder placement(Placement placement) {
            this.placement = placement;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder mask(boolean mask) {
            this.mask = mask;
            return this;
        }

        public Builder noMask() {
            return mask(false);
        }

        public Builder maskClosable(boolean maskClosable) {
            this.maskClosable = maskClosable;
            return this;
        }

        public Builder onClose(Consumer<Boolean> onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        /**
         * 预设抽屉宽度/高度
         * 对标 Ant Design size 属性
         * DEFAULT = 378px, LARGE = 736px
         */
        public Builder size(Size size) {
            this.size = size;
            if (size == Size.LARGE) {
                this.width = 736;
                this.height = 736;
            }
            return this;
        }

        /**
         * 抽屉右上角的操作区域
         * 对标 Ant Design extra 属性
         */
        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Drawer build() {
            return new Drawer(this);
        }
    }

    public static class Drawer {
        private final Builder config;
        private Popup popup;
        private StackPane overlay;
        private VBox drawerPanel;
        private boolean isOpen = false;

        private Drawer(Builder config) {
            this.config = config;
        }

        public void open(Node owner) {
            if (isOpen) return;
            isOpen = true;

            // Create overlay
            overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
            overlay.setPrefSize(owner.getScene().getWindow().getWidth(), owner.getScene().getWindow().getHeight());

            // Create drawer panel
            drawerPanel = createDrawerPanel();

            // Position panel based on placement
            positionPanel();

            overlay.getChildren().add(drawerPanel);
            StackPane.setAlignment(drawerPanel, getAlignment());

            // Mask click to close
            if (config.maskClosable) {
                overlay.setOnMouseClicked(e -> {
                    if (e.getTarget() == overlay) {
                        close();
                    }
                });
            }

            // Show popup
            popup = new Popup();
            popup.getContent().add(overlay);
            popup.show(owner.getScene().getWindow());

            // Animate in
            animateIn();
        }

        public void close() {
            if (!isOpen) return;
            isOpen = false;

            animateOut(() -> {
                if (popup != null) {
                    popup.hide();
                }
                if (config.onClose != null) {
                    config.onClose.accept(false);
                }
            });
        }

        private VBox createDrawerPanel() {
            VBox panel = new VBox(0);
            panel.getStyleClass().add("drawer");
            panel.setStyle(
                "-fx-background-color: -color-bg-overlay;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 16, 0, 0, 4);"
            );

            // Header
            if (!config.title.isEmpty()) {
                HBox header = new HBox(8);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setStyle("-fx-padding: 16px 24px; -fx-border-color: transparent transparent -color-border-muted transparent; -fx-border-width: 0 0 1px 0;");

                // Title - 左侧
                javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(config.title);
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default;");
                HBox.setHgrow(titleLabel, Priority.ALWAYS);
                header.getChildren().add(titleLabel);

                // Extra 操作区 - 右侧，对标 Ant Design extra 属性
                if (config.extra != null) {
                    header.getChildren().add(config.extra);
                }

                // Close button - 右上角，对标 Ant Design
                javafx.scene.control.Button closeBtn = new javafx.scene.control.Button("×");
                closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: -color-fg-muted; -fx-font-size: 20px; -fx-cursor: hand; -fx-padding: 0 4px;");
                closeBtn.setOnAction(e -> close());
                header.getChildren().add(closeBtn);

                panel.getChildren().add(header);
            }

            // Content
            if (config.content != null) {
                VBox contentBox = new VBox(config.content);
                contentBox.setStyle("-fx-padding: 24px;");
                VBox.setVgrow(contentBox, Priority.ALWAYS);
                panel.getChildren().add(contentBox);
            }

            // Footer
            if (config.footer != null) {
                HBox footerBox = new HBox(config.footer);
                footerBox.setAlignment(Pos.CENTER_RIGHT);
                footerBox.setStyle("-fx-padding: 16px 24px; -fx-border-color: -color-border-muted transparent transparent transparent; -fx-border-width: 1px 0 0 0;");
                panel.getChildren().add(footerBox);
            }

            return panel;
        }

        private void positionPanel() {
            switch (config.placement) {
                case LEFT, RIGHT -> {
                    drawerPanel.setPrefWidth(config.width);
                    drawerPanel.setPrefHeight(overlay.getPrefHeight());
                }
                case TOP, BOTTOM -> {
                    drawerPanel.setPrefWidth(overlay.getPrefWidth());
                    drawerPanel.setPrefHeight(config.height);
                }
            }
        }

        private Pos getAlignment() {
            return switch (config.placement) {
                case LEFT -> Pos.CENTER_LEFT;
                case RIGHT -> Pos.CENTER_RIGHT;
                case TOP -> Pos.TOP_CENTER;
                case BOTTOM -> Pos.BOTTOM_CENTER;
            };
        }

        private void animateIn() {
            drawerPanel.setOpacity(0);

            FadeTransition fade = new FadeTransition(Duration.millis(250), drawerPanel);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setInterpolator(Interpolator.EASE_OUT);

            TranslateTransition slide = new TranslateTransition(Duration.millis(250), drawerPanel);
            switch (config.placement) {
                case LEFT -> {
                    slide.setFromX(-config.width);
                    slide.setToX(0);
                }
                case RIGHT -> {
                    slide.setFromX(config.width);
                    slide.setToX(0);
                }
                case TOP -> {
                    slide.setFromY(-config.height);
                    slide.setToY(0);
                }
                case BOTTOM -> {
                    slide.setFromY(config.height);
                    slide.setToY(0);
                }
            }
            slide.setInterpolator(Interpolator.EASE_OUT);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, slide);
            pt.play();
        }

        private void animateOut(Runnable onFinished) {
            FadeTransition fade = new FadeTransition(Duration.millis(200), drawerPanel);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setInterpolator(Interpolator.EASE_IN);

            TranslateTransition slide = new TranslateTransition(Duration.millis(200), drawerPanel);
            switch (config.placement) {
                case LEFT -> slide.setToX(-config.width);
                case RIGHT -> slide.setToX(config.width);
                case TOP -> slide.setToY(-config.height);
                case BOTTOM -> slide.setToY(config.height);
            }
            slide.setInterpolator(Interpolator.EASE_IN);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, slide);
            pt.setOnFinished(e -> onFinished.run());
            pt.play();
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
