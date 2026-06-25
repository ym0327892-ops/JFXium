package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.IconPath;

/**
 * 内部基础组件：结果页显示面板（状态图标 + 标题 + 副标题 + 操作区）。
 *
 * <p>供 {@link org.openkawu.jfxium.component.composite.ResultAnt} 使用，不直接对外暴露。</p>
 */
public class ResultDisplay {

    public enum Status {
        SUCCESS, ERROR, INFO, WARNING, NOT_FOUND, FORBIDDEN, INTERNAL_ERROR
    }

    public static class Builder {
        private Status status = Status.INFO;
        private String title = "";
        private String subTitle = "";
        private Node extra = null;
        private double iconScale = 3.0;

        public Builder status(Status status) {
            this.status = status != null ? status : Status.INFO;
            return this;
        }

        public Builder title(String title) {
            this.title = title != null ? title : "";
            return this;
        }

        public Builder subTitle(String subTitle) {
            this.subTitle = subTitle != null ? subTitle : "";
            return this;
        }

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder iconScale(double scale) {
            this.iconScale = Double.isFinite(scale) ? Math.max(0, scale) : 3.0;
            return this;
        }

        public VBox build() {
            VBox result = new VBox();
            result.setAlignment(Pos.CENTER);
            result.getStyleClass().add(JfxStyles.RESULT);

            SVGPath icon = new SVGPath();
            icon.setContent(getIconPath(status));
            icon.setScaleX(iconScale);
            icon.setScaleY(iconScale);
            icon.getStyleClass().add(getIconStyleClass(status));

            VBox iconBox = new VBox(icon);
            iconBox.setAlignment(Pos.CENTER);
            iconBox.getStyleClass().add(JfxStyles.RESULT_ICON_BOX);
            result.getChildren().add(iconBox);

            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add(JfxStyles.RESULT_TITLE);
                result.getChildren().add(titleLabel);
            }

            if (!subTitle.isEmpty()) {
                Label subTitleLabel = new Label(subTitle);
                subTitleLabel.getStyleClass().add(JfxStyles.RESULT_SUBTITLE);
                subTitleLabel.setWrapText(true);
                subTitleLabel.setAlignment(Pos.CENTER);
                result.getChildren().add(subTitleLabel);
            }

            if (extra != null) {
                VBox extraBox = new VBox(extra);
                extraBox.setAlignment(Pos.CENTER);
                extraBox.getStyleClass().add(JfxStyles.RESULT_EXTRA);
                result.getChildren().add(extraBox);
            }

            return result;
        }

        private String getIconPath(Status status) {
            return switch (status) {
                case SUCCESS -> IconPath.ICON_SUCCESS;
                case ERROR -> IconPath.ICON_ERROR;
                case WARNING -> IconPath.WARNING_TRIANGLE;
                case INFO -> IconPath.ICON_INFO;
                case NOT_FOUND -> IconPath.ICON_NOT_FOUND;
                case FORBIDDEN -> IconPath.ICON_FORBIDDEN;
                case INTERNAL_ERROR -> IconPath.ICON_INTERNAL_ERROR;
            };
        }

        private String getIconStyleClass(Status status) {
            return switch (status) {
                case SUCCESS -> JfxStyles.ICON_SUCCESS;
                case ERROR, INTERNAL_ERROR -> JfxStyles.ICON_DANGER;
                case WARNING -> JfxStyles.ICON_WARNING;
                case INFO -> JfxStyles.ICON_INFO;
                case NOT_FOUND, FORBIDDEN -> JfxStyles.ICON_MUTED;
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
