package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;

/**
 * JFXium Badge Component - 对标 Ant Design Badge。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 数字徽标
 * StackPane badge = BadgeAnt.create()
 *     .content(new Button("Messages"))
 *     .count(5)
 *     .build();
 *
 * // 状态点
 * StackPane statusBadge = BadgeAnt.create()
 *     .content(new Label("Online"))
 *     .status(BadgeAnt.Status.SUCCESS)
 *     .build();
 * }</pre>
 */
public class BadgeAnt {

    public enum Status {
        SUCCESS, WARNING, ERROR, DEFAULT
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Node content;
        private int count = 0;
        private boolean dot = false;
        private Status status = null;

        private Builder() {}

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder dot(boolean dot) {
            this.dot = dot;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public StackPane build() {
            StackPane badge = new StackPane();
            badge.getStyleClass().add(JfxStyles.BADGE);

            if (content != null) {
                badge.getChildren().add(content);
            }

            // 三种 indicator 形态互斥：count > 0 优先，其次 dot，最后 status；任一未触发则不显示 indicator
            if (count > 0 || dot || status != null) {
                Label indicator = new Label();
                indicator.setAlignment(Pos.CENTER);
                indicator.getStyleClass().add(JfxStyles.BADGE_INDICATOR);

                if (count > 0) {
                    // count 形态：右上角小圆角矩形 + 数字文本
                    indicator.setText(String.valueOf(count));
                    indicator.getStyleClass().add(JfxStyles.BADGE_COUNT);
                    StackPane.setAlignment(indicator, Pos.TOP_RIGHT);
                } else if (dot) {
                    // dot 形态：右上角红色小圆点（默认 danger 色）
                    indicator.setPrefSize(8, 8);
                    indicator.getStyleClass().add(JfxStyles.BADGE_DOT);
                    StackPane.setAlignment(indicator, Pos.TOP_RIGHT);
                } else {
                    // status 形态：左侧偏移的小圆点 + 状态色
                    indicator.setPrefSize(8, 8);
                    indicator.getStyleClass().add(JfxStyles.BADGE_STATUS);
                    indicator.getStyleClass().add(statusClassFor(status));
                    StackPane.setAlignment(indicator, Pos.CENTER_LEFT);
                    indicator.setTranslateX(-12);
                }

                badge.getChildren().add(indicator);
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(badge);
            return badge;
        }

        /** 状态枚举到 styleClass 修饰类的映射 */
        private static String statusClassFor(Status status) {
            if (status == null) return JfxStyles.BADGE_STATUS_DEFAULT;
            return switch (status) {
                case SUCCESS -> JfxStyles.BADGE_STATUS_SUCCESS;
                case WARNING -> JfxStyles.BADGE_STATUS_WARNING;
                case ERROR -> JfxStyles.BADGE_STATUS_ERROR;
                case DEFAULT -> JfxStyles.BADGE_STATUS_DEFAULT;
            };
        }
    }
}
