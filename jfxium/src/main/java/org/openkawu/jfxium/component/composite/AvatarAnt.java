package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

/**
 * JFXium 头像组件 - 对标 Ant Design Avatar。
 *
 * <p><b>定位</b>：圆形/圆角方形头像容器，支持图片、文字首字母、自定义节点三种模式。
 * 常用于用户头像、团队成员展示、评论者标识等场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>类型</b>：src(url) 图片 / text(“张三”) 文字首字母 / icon(Node) 自定义节点</li>
 *   <li><b>尺寸</b>：SMALL(24) / DEFAULT(32) / LARGE(40) / XL(64)</li>
 *   <li><b>形状</b>：CIRCLE（圆形，默认）/ SQUARE（圆角方形）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 图片头像
 * StackPane avatar = AvatarAnt.create()
 *     .src("https://example.com/avatar.jpg")
 *     .size(AvatarAnt.Size.LARGE)
 *     .build();
 *
 * // 文字头像（取首字母）
 * StackPane textAvatar = AvatarAnt.create()
 *     .text("张三")
 *     .size(AvatarAnt.Size.DEFAULT)
 *     .build();
 * }</pre>
 */
public class AvatarAnt {

    public enum Size {
        SMALL(24), DEFAULT(32), LARGE(40), XL(64);

        private final int value;

        Size(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Shape {
        CIRCLE, SQUARE
    }

    public enum Type {
        IMAGE, TEXT, ICON
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Size size = Size.DEFAULT;
        private Shape shape = Shape.CIRCLE;
        private String text = "";
        private Image image = null;
        private String icon = null;
        private String backgroundColor = null;
        private String textColor = "-color-fg-on-emphasis";
        private int customSize = 0;

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder size(int pixels) {
            this.customSize = pixels;
            return this;
        }

        public Builder shape(Shape shape) {
            this.shape = shape;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder image(Image image) {
            this.image = image;
            return this;
        }

        public Builder image(String url) {
            this.image = new Image(url, true);
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder backgroundColor(String color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder textColor(String color) {
            this.textColor = color;
            return this;
        }

        public StackPane build() {
            int s = customSize > 0 ? customSize : size.getValue();

            StackPane avatar = new StackPane();
            avatar.getStyleClass().add("avatar");
            avatar.setPrefSize(s, s);
            avatar.setMinSize(s, s);
            avatar.setMaxSize(s, s);

            // Background
            String bg = backgroundColor != null ? backgroundColor : "-color-accent-emphasis";
            avatar.setStyle("-fx-background-color: " + bg + ";");

            // Clip shape
            if (shape == Shape.CIRCLE) {
                Circle clip = new Circle(s / 2.0);
                clip.setCenterX(s / 2.0);
                clip.setCenterY(s / 2.0);
                avatar.setClip(clip);
            } else {
                Rectangle clip = new Rectangle(s, s);
                clip.setArcWidth(s * 0.2);
                clip.setArcHeight(s * 0.2);
                avatar.setClip(clip);
            }

            // Content
            if (image != null) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(s);
                imageView.setFitHeight(s);
                imageView.setPreserveRatio(true);
                avatar.getChildren().add(imageView);
            } else if (text != null && !text.isEmpty()) {
                String displayText = text.length() > 2 ? text.substring(0, 2) : text;
                Label label = new Label(displayText);
                label.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: " + (s * 0.4) + "px; -fx-font-weight: 600;");
                avatar.getChildren().add(label);
            }

            avatar.setAlignment(Pos.CENTER);
            return avatar;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder create(String text) {
        return new Builder().text(text);
    }
}
