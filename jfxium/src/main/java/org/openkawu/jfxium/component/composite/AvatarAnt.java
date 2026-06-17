package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

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
        private Node iconNode = null;
        private String iconText = null;
        private String backgroundColor = null;
        private String textColor = "-color-fg-on-emphasis";
        private int customSize = 0;

        public Builder size(Size size) {
            this.size = size != null ? size : Size.DEFAULT;
            return this;
        }

        public Builder size(int pixels) {
            this.customSize = pixels;
            return this;
        }

        public Builder shape(Shape shape) {
            this.shape = shape != null ? shape : Shape.CIRCLE;
            return this;
        }

        public Builder text(String text) {
            this.text = text != null ? text : "";
            this.image = null;
            this.iconNode = null;
            this.iconText = null;
            return this;
        }

        public Builder image(Image image) {
            this.image = image;
            this.iconNode = null;
            this.iconText = null;
            return this;
        }

        public Builder src(Image image) {
            return image(image);
        }

        public Builder image(String url) {
            this.image = new Image(url, true);
            this.iconNode = null;
            this.iconText = null;
            return this;
        }

        public Builder src(String url) {
            return image(url);
        }

        public Builder icon(Node icon) {
            this.iconNode = icon;
            this.iconText = null;
            this.image = null;
            this.text = "";
            return this;
        }

        public Builder icon(String icon) {
            this.iconText = icon;
            this.iconNode = null;
            this.image = null;
            this.text = "";
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
            avatar.getStyleClass().add(JfxStyles.AVATAR);
            if (shape == Shape.SQUARE) {
                avatar.getStyleClass().add(JfxStyles.AVATAR_SQUARE);
            }
            avatar.setPrefSize(s, s);
            avatar.setMinSize(s, s);
            avatar.setMaxSize(s, s);

            String bg = backgroundColor != null ? backgroundColor : null;
            if (bg == null || isCssVar(bg)) {
                avatar.getStyleClass().add(JfxStyles.AVATAR_BG_DEFAULT);
            } else {
                avatar.setBackground(new Background(new BackgroundFill(
                        Paint.valueOf(bg),
                        CornerRadii.EMPTY,
                        javafx.geometry.Insets.EMPTY)));
            }

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
            } else if (iconNode != null) {
                avatar.getChildren().add(iconNode);
            } else if (iconText != null && !iconText.isEmpty()) {
                Label label = new Label(iconText);
                label.getStyleClass().add(JfxStyles.AVATAR_TEXT);
                if (!isCssVar(textColor)) {
                    label.setTextFill(Paint.valueOf(textColor));
                } else {
                    label.getStyleClass().add(JfxStyles.AVATAR_FG_DEFAULT);
                }
                avatar.getChildren().add(label);
            } else if (text != null && !text.isEmpty()) {
                String displayText = text.length() > 2 ? text.substring(0, 2) : text;
                Label label = new Label(displayText);
                // 文字根类：weight 600 + font-size 走 LESS 修饰类
                label.getStyleClass().add(JfxStyles.AVATAR_TEXT);
                if (customSize > 0) {
                    label.setFont(Font.font(label.getFont().getFamily(), FontWeight.SEMI_BOLD, s * 0.4));
                } else {
                    // enum 尺寸走 LESS 修饰类（避免 setStyle 拼 -fx-font-size）
                    label.getStyleClass().add(textSizeClass(size));
                }
                if (!isCssVar(textColor)) {
                    label.setTextFill(Paint.valueOf(textColor));
                } else {
                    label.getStyleClass().add(JfxStyles.AVATAR_FG_DEFAULT);
                }
                avatar.getChildren().add(label);
            }

            avatar.setAlignment(Pos.CENTER);
            applyStyles(avatar);
            return avatar;
        }

        /** 判断颜色是否为 CSS 变量（以 "-" 开头） */
        private static boolean isCssVar(String color) {
            return color != null && color.startsWith("-");
        }

        /** enum 尺寸 → 字体大小修饰类（s*0.4 px 在 LESS 预定义） */
        private static String textSizeClass(Size size) {
            return switch (size) {
                case SMALL -> JfxStyles.AVATAR_TEXT_24;
                case LARGE -> JfxStyles.AVATAR_TEXT_40;
                case XL    -> JfxStyles.AVATAR_TEXT_64;
                default    -> JfxStyles.AVATAR_TEXT_32;
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder create(String text) {
        return new Builder().text(text);
    }
}
