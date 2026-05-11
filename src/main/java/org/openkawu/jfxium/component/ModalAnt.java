package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * JFXium Modal Component
 * Inspired by Ant Design Modal
 * A dialog box for displaying important information or collecting user input.
 */
public class ModalAnt {

    public static class Builder {
        private String title = "";
        private Node content = null;
        private boolean mask = true;
        private boolean maskClosable = true;
        private int width = 520;
        private Consumer<Boolean> onClose = null;
        private Node footer = null;
        private boolean centered = false;
        private boolean keyboard = true;
        private boolean confirmLoading = false;
        private String okText = "OK";
        private String cancelText = "Cancel";
        private Runnable onOk = null;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder content(String text) {
            this.content = new Label(text);
            return this;
        }

        public Builder mask(boolean mask) {
            this.mask = mask;
            return this;
        }

        public Builder maskClosable(boolean maskClosable) {
            this.maskClosable = maskClosable;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
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

        public Builder centered(boolean centered) {
            this.centered = centered;
            return this;
        }

        public Builder centered() {
            return centered(true);
        }

        /**
         * 是否支持键盘 ESC 关闭
         * 对标 Ant Design keyboard 属性
         */
        public Builder keyboard(boolean keyboard) {
            this.keyboard = keyboard;
            return this;
        }

        /**
         * 确定按钮 loading 状态
         * 对标 Ant Design confirmLoading 属性
         */
        public Builder confirmLoading(boolean confirmLoading) {
            this.confirmLoading = confirmLoading;
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

        public Builder onOk(Runnable onOk) {
            this.onOk = onOk;
            return this;
        }

        public Modal build() {
            return new Modal(this);
        }
    }

    public static class Modal {
        private final Builder config;
        private Popup popup;
        private StackPane overlay;
        private VBox modalPanel;
        private boolean isOpen = false;

        private Modal(Builder config) {
            this.config = config;
        }

        public void open(Node owner) {
            if (isOpen) return;
            isOpen = true;

            javafx.stage.Window window = owner.getScene().getWindow();

            // Create overlay - cover entire window
            overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
            // Set size to match window
            overlay.setPrefSize(window.getWidth(), window.getHeight());

            // Create modal panel
            modalPanel = createModalPanel();
            modalPanel.setMaxWidth(config.width);
            modalPanel.setMinWidth(config.width);

            overlay.getChildren().add(modalPanel);
            if (config.centered) {
                StackPane.setAlignment(modalPanel, Pos.CENTER);
            } else {
                StackPane.setAlignment(modalPanel, Pos.TOP_CENTER);
                StackPane.setMargin(modalPanel, new javafx.geometry.Insets(100, 0, 0, 0));
            }

            // Mask click to close
            if (config.maskClosable) {
                overlay.setOnMouseClicked(e -> {
                    if (e.getTarget() == overlay) {
                        close();
                    }
                });
            }

            // Show popup positioned at window location
            popup = new Popup();
            popup.getContent().add(overlay);

            // Position popup to cover the entire window
            popup.setX(window.getX());
            popup.setY(window.getY());
            popup.setWidth(window.getWidth());
            popup.setHeight(window.getHeight());

            popup.show(window);

            // 键盘 ESC 关闭 - 对标 Ant Design keyboard 属性
            if (config.keyboard) {
                overlay.setOnKeyPressed(e -> {
                    if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                        close();
                        e.consume();
                    }
                });
                overlay.setFocusTraversable(true);
                overlay.requestFocus();
            }

            // Update overlay size when window resizes
            javafx.beans.value.ChangeListener<Number> widthListener = (obs, old, val) -> {
                overlay.setPrefSize(val.doubleValue(), window.getHeight());
                popup.setX(window.getX());
                popup.setWidth(val.doubleValue());
            };
            javafx.beans.value.ChangeListener<Number> heightListener = (obs, old, val) -> {
                overlay.setPrefSize(window.getWidth(), val.doubleValue());
                popup.setY(window.getY());
                popup.setHeight(val.doubleValue());
            };
            javafx.beans.value.ChangeListener<Number> xListener = (obs, old, val) -> popup.setX(val.doubleValue());
            javafx.beans.value.ChangeListener<Number> yListener = (obs, old, val) -> popup.setY(val.doubleValue());

            window.widthProperty().addListener(widthListener);
            window.heightProperty().addListener(heightListener);
            window.xProperty().addListener(xListener);
            window.yProperty().addListener(yListener);

            // Store listeners to remove them later
            popup.setOnHidden(e -> {
                window.widthProperty().removeListener(widthListener);
                window.heightProperty().removeListener(heightListener);
                window.xProperty().removeListener(xListener);
                window.yProperty().removeListener(yListener);
            });

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

        private VBox createModalPanel() {
            VBox panel = new VBox(0);
            panel.getStyleClass().add("modal");
            panel.setStyle(
                "-fx-background-color: -color-bg-overlay;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 16, 0, 0, 4);"
            );

            // Header
            if (!config.title.isEmpty()) {
                HBox header = new HBox(8);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setStyle("-fx-padding: 16px 24px; -fx-border-color: transparent transparent -color-border-muted transparent; -fx-border-width: 0 0 1px 0;");

                Label titleLabel = new Label(config.title);
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default;");
                HBox.setHgrow(titleLabel, Priority.ALWAYS);
                header.getChildren().add(titleLabel);

                // Close button - right aligned
                javafx.scene.control.Button closeBtn = new javafx.scene.control.Button("\u00d7");
                closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: -color-fg-muted; -fx-font-size: 20px; -fx-cursor: hand; -fx-padding: 0 4px;");
                closeBtn.setOnAction(e -> close());
                header.getChildren().add(closeBtn);

                panel.getChildren().add(header);
            }

            // Content
            if (config.content != null) {
                VBox contentBox = new VBox(config.content);
                contentBox.setStyle("-fx-padding: 24px;");
                panel.getChildren().add(contentBox);
            }

            // Footer
            if (config.footer != null) {
                HBox footerBox = new HBox(config.footer);
                footerBox.setAlignment(Pos.CENTER_RIGHT);
                footerBox.setStyle("-fx-padding: 16px 24px; -fx-border-color: -color-border-muted transparent transparent transparent; -fx-border-width: 1px 0 0 0;");
                panel.getChildren().add(footerBox);
            } else {
                // Default footer with OK/Cancel
                HBox defaultFooter = new HBox(8);
                defaultFooter.setAlignment(Pos.CENTER_RIGHT);
                defaultFooter.setStyle("-fx-padding: 16px 24px; -fx-border-color: -color-border-muted transparent transparent transparent; -fx-border-width: 1px 0 0 0;");

                javafx.scene.control.Button cancelBtn = ButtonAnt.create(config.cancelText)
                    .type(ButtonAnt.Type.DEFAULT)
                    .onClick(e -> close())
                    .build();

                // 确定按钮支持 loading 状态 - 对标 Ant Design confirmLoading
                javafx.scene.control.Button okBtn = ButtonAnt.create(config.okText)
                    .type(ButtonAnt.Type.PRIMARY)
                    .loading(config.confirmLoading)
                    .onClick(e -> {
                        if (config.onOk != null) {
                            config.onOk.run();
                        }
                        close();
                    })
                    .build();

                defaultFooter.getChildren().addAll(cancelBtn, okBtn);
                panel.getChildren().add(defaultFooter);
            }

            return panel;
        }

        private void animateIn() {
            modalPanel.setOpacity(0);
            modalPanel.setScaleX(0.9);
            modalPanel.setScaleY(0.9);

            FadeTransition fade = new FadeTransition(Duration.millis(200), modalPanel);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setInterpolator(Interpolator.EASE_OUT);

            javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(Duration.millis(200), modalPanel);
            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1);
            scale.setToY(1);
            scale.setInterpolator(Interpolator.EASE_OUT);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, scale);
            pt.play();
        }

        private void animateOut(Runnable onFinished) {
            FadeTransition fade = new FadeTransition(Duration.millis(150), modalPanel);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setInterpolator(Interpolator.EASE_IN);

            javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(Duration.millis(150), modalPanel);
            scale.setFromX(1);
            scale.setFromY(1);
            scale.setToX(0.95);
            scale.setToY(0.95);
            scale.setInterpolator(Interpolator.EASE_IN);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, scale);
            pt.setOnFinished(e -> onFinished.run());
            pt.play();
        }
    }

    public static Builder create() {
        return new Builder();
    }

    // Convenience methods
    public static void info(String title, String content, Node owner) {
        create().title(title).content(content).build().open(owner);
    }

    public static void confirm(String title, String content, Node owner, Runnable onOk) {
        Modal modal = create()
            .title(title)
            .content(content)
            .build();
        modal.open(owner);
    }
}
