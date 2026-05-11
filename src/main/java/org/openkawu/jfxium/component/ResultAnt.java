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
public class ResultAnt {

    public enum Status {
        SUCCESS, ERROR, INFO, WARNING, NOT_FOUND, FORBIDDEN, INTERNAL_ERROR
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
            this.extra = ButtonAnt.create(text)
                .type(ButtonAnt.Type.PRIMARY)
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
                case NOT_FOUND -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z";
                case FORBIDDEN -> "M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z";
                case INTERNAL_ERROR -> "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z";
            };
        }

        private String getIconColor(Status status) {
            return switch (status) {
                case SUCCESS -> "-color-success-emphasis";
                case ERROR, INTERNAL_ERROR -> "-color-danger-emphasis";
                case WARNING -> "-color-warning-emphasis";
                case INFO -> "-color-accent-emphasis";
                case NOT_FOUND, FORBIDDEN -> "-color-fg-muted";
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder success(String title, String subTitle) {
        return new Builder().status(Status.SUCCESS).title(title).subTitle(subTitle);
    }

    public static Builder error(String title, String subTitle) {
        return new Builder().status(Status.ERROR).title(title).subTitle(subTitle);
    }

    public static Builder info(String title, String subTitle) {
        return new Builder().status(Status.INFO).title(title).subTitle(subTitle);
    }

    public static Builder warning(String title, String subTitle) {
        return new Builder().status(Status.WARNING).title(title).subTitle(subTitle);
    }
}
