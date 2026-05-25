package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.base.ResultDisplay;

public class ResultAnt {

    public enum Status {
        SUCCESS, ERROR, INFO, WARNING, NOT_FOUND, FORBIDDEN, INTERNAL_ERROR
    }

    public static class Builder {
        private Status status = Status.INFO;
        private String title = "";
        private String subTitle = "";
        private Node extra = null;

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

        public Builder extra(Node extra) {
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
            return new ResultDisplay.Builder()
                .status(convertStatus(status))
                .title(title)
                .subTitle(subTitle)
                .extra(extra)
                .build();
        }

        private ResultDisplay.Status convertStatus(Status status) {
            return switch (status) {
                case SUCCESS -> ResultDisplay.Status.SUCCESS;
                case ERROR -> ResultDisplay.Status.ERROR;
                case INFO -> ResultDisplay.Status.INFO;
                case WARNING -> ResultDisplay.Status.WARNING;
                case NOT_FOUND -> ResultDisplay.Status.NOT_FOUND;
                case FORBIDDEN -> ResultDisplay.Status.FORBIDDEN;
                case INTERNAL_ERROR -> ResultDisplay.Status.INTERNAL_ERROR;
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
