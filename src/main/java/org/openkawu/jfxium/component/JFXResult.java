package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

/**
 * JFXium Result Component
 * Inspired by Ant Design Result
 * Used to feed back the results of a series of operational tasks.
 */
public class JFXResult {

    public enum Status {
        SUCCESS, ERROR, INFO, WARNING, 404, 403, 500
    }

    public static class Builder {
        private Status status = Status.INFO;
        private String title = "";
        private String subTitle = "";
        private javafx.scene.Node extra = null;

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder subTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public Builder extra(javafx.scene.Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder extraButton(String text, Runnable action) {
            this.extra = JFXButton.create(text)
                .type(JFXButton.Type.PRIMARY)
                .onClick(e -> action.run())
                .build();
            return this;
        }

        public VBox build() {
            VBox result = new VBox(16);
            result.setAlignment(Pos.CENTER);
            result.getStyleClass().add("result");
            result.setStyle("-fx-padding: 48px 32px;");

            // Icon
            SVGPath icon = new SVGPath();
            icon.setContent(getIconPath(status));
            icon.setScaleX(3);
            icon.setScaleY(3);
            icon.setStyle("-fx-fill: " + getIconColor(status) + ";");

            VBox iconBox = new VBox(icon);
            iconBox.setAlignment(Pos.CENTER);
            iconBox.setStyle("-fx-padding: 0 0 16px 0;");
            result.getChildren().add(iconBox);

            // Title
            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default;");
                result.getChildren().add(titleLabel);
            }

            // SubTitle
            if (!subTitle.isEmpty()) {
                Label subTitleLabel = new Label(subTitle);
                subTitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-muted;");
                subTitleLabel.setWrapText(true);
                subTitleLabel.setAlignment(Pos.CENTER);
                result.getChildren().add(subTitleLabel);
            }

            // Extra
            if (extra != null) {
                VBox extraBox = new VBox(extra);
                extraBox.setAlignment(Pos.CENTER);
                extraBox.setStyle("-fx-padding: 16px 0 0 0;");
                result.getChildren().add(extraBox);
            }

            return result;
        }

        private String getIconPath(Status status) {
            return switch (status) {
                case SUCCESS -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";
                case ERROR -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";
                case WARNING -> "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
                case INFO -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";
                case 404 -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.