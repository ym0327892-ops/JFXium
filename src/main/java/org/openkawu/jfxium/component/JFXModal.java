package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Modal Dialog Component
 * Inspired by Ant Design Modal
 *
 * Usage:
 * <pre>{@code
 * JFXModal.create()
 *     .title("确认删除")
 *     .content("确定要删除这条记录吗？")
 *     .width(400)
 *     .okText("确认")
 *     .cancelText("取消")
 *     .onOk(() -> System.out.println("Confirmed!"))
 *     .show();
 * }</pre>
 */
public class JFXModal {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String title = "";
        private Node content;
        private String contentText = "";
        private double width = 420;
        private double height = -1;
        private boolean closable = true;
        private boolean maskClosable = true;
        private String okText = "确定";
        private String cancelText = "取消";
        private boolean showCancel = true;
        private JFXButton.Type okType = JFXButton.Type.PRIMARY;
        private JFXButton.Type cancelType = JFXButton.Type.DEFAULT;
        private Runnable onOk;
        private Runnable onCancel;
        private Runnable onClose;
        private Stage owner;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder content(String text) {
            this.contentText = text;
            return this;
        }

        public Builder width(double width) {
            this.width = width;
            return this;
        }

        public Builder height(double height) {
            this.height = height;
            return this;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder maskClosable(boolean maskClosable) {
            this.maskClosable = maskClosable;
            return this;
        }

        public Builder okText(String text) {
            this.okText = text;
            return this;
        }

        public Builder cancelText(String text) {
            this.cancelText = text;
            return this;
        }

        public Builder showCancel(boolean show) {
            this.showCancel = show;
            return this;
        }

        public Builder okType(JFXButton.Type type) {
            this.okType = type;
            return this;
        }

        public Builder cancelType(JFXButton.Type type) {
            this.cancelType = type;
            return this;
        }

        public Builder onOk(Runnable handler) {
            this.onOk = handler;
            return this;
        }

        public Builder onCancel(Runnable handler) {
            this.onCancel = handler;
            return this;
        }

        public Builder onClose(Runnable handler) {
            this.onClose = handler;
            return this;
        }

        public Builder owner(Stage owner) {
            this.owner = owner;
            return this;
        }

        public void show() {
            // Create modal stage
            Stage modalStage = new Stage();
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) {
                modalStage.initOwner(owner);
            }

            // Create mask (background overlay)
            StackPane mask = new StackPane();
            mask.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
            mask.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

            // Create modal content container
            VBox modalContent = new VBox();
            modalContent.getStyleClass().add("modal-content");
            modalContent.setMaxWidth(width);
            if (height > 0) {
                modalContent.setMaxHeight(height);
            }
            modalContent.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-radius: 8px;"
            );

            // Add shadow effect
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.15));
            shadow.setRadius(20);
            shadow.setSpread(0.1);
            modalContent.setEffect(shadow);

            // Title
            if (!title.isEmpty()) {
                HBox titleBar = new HBox();
                titleBar.setAlignment(Pos.CENTER_LEFT);
                titleBar.setPadding(new Insets(16, 16, 12, 16));
                titleBar.setStyle("-fx-border-color: transparent transparent #f0f0f0 transparent; -fx-border-width: 0 0 1px 0;");

                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: rgba(0, 0, 0, 0.88);");
                titleBar.getChildren().add(titleLabel);

                // Close button
                if (closable) {
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button closeBtn = new Button("✕");
                    closeBtn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: rgba(0, 0, 0, 0.45);" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 0;" +
                        "-fx-min-width: 24px;" +
                        "-fx-min-height: 24px;" +
                        "-fx-cursor: hand;"
                    );
                    closeBtn.setOnAction(e -> {
                        closeModal(modalStage, mask);
                        if (onClose != null) onClose.run();
                    });

                    titleBar.getChildren().addAll(spacer, closeBtn);
                }

                modalContent.getChildren().add(titleBar);
            }

            // Content area
            Node contentNode;
            if (content != null) {
                contentNode = content;
            } else {
                Label contentLabel = new Label(contentText);
                contentLabel.setWrapText(true);
                contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(0, 0, 0, 0.65);");
                contentNode = contentLabel;
            }

            StackPane contentPane = new StackPane(contentNode);
            contentPane.setPadding(new Insets(16));
            modalContent.getChildren().add(contentPane);

            // Footer with buttons
            HBox footer = new HBox(8);
            footer.setAlignment(Pos.CENTER_RIGHT);
            footer.setPadding(new Insets(12, 16, 16, 16));
            footer.setStyle("-fx-border-color: #f0f0f0 transparent transparent transparent; -fx-border-width: 1px 0 0 0;");

            if (showCancel) {
                Button cancelBtn = JFXButton.create(cancelText)
                    .type(cancelType)
                    .onClick(e -> {
                        closeModal(modalStage, mask);
                        if (onCancel != null) onCancel.run();
                    })
                    .build();
                footer.getChildren().add(cancelBtn);
            }

            Button okBtn = JFXButton.create(okText)
                .type(okType)
                .onClick(e -> {
                    closeModal(modalStage, mask);
                    if (onOk != null) onOk.run();
                })
                .build();
            footer.getChildren().add(okBtn);

            modalContent.getChildren().add(footer);

            // Add content to mask
            mask.getChildren().add(modalContent);
            StackPane.setAlignment(modalContent, Pos.CENTER);

            // Mask click to close
            if (maskClosable) {
                mask.setOnMouseClicked(e -> {
                    if (e.getTarget() == mask) {
                        closeModal(modalStage, mask);
                        if (onClose != null) onClose.run();
                    }
                });
            }

            // Create scene
            Scene scene = new Scene(mask);
            scene.setFill(Color.TRANSPARENT);
            modalStage.setScene(scene);

            // Show with animation
            modalStage.show();
            animateShow(mask, modalContent);
        }

        private void closeModal(Stage stage, StackPane mask) {
            // Fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), mask);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> stage.close());
            fadeOut.play();
        }

        private void animateShow(StackPane mask, VBox content) {
            // Initial state
            mask.setOpacity(0);
            content.setScaleX(0.9);
            content.setScaleY(0.9);

            // Fade in mask
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), mask);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            // Scale in content
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), content);
            scaleIn.setFromX(0.9);
            scaleIn.setFromY(0.9);
            scaleIn.setToX(1);
            scaleIn.setToY(1);

            fadeIn.play();
            scaleIn.play();
        }
    }
}