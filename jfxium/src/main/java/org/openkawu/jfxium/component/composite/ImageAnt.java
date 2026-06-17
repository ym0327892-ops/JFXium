package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 图片组件 - 对标 Ant Design Image。
 *
 * <p><b>定位</b>：增强型 ImageView，支持加载失败 fallback、加载中 placeholder、
 * 圆角、预览、objectFit 等能力。视觉走 LESS。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>图片源</b>：src(url/path)</li>
 *   <li><b>尺寸</b>：width/height 控制显示尺寸</li>
 *   <li><b>圆角</b>：borderRadius(px)</li>
 *   <li><b>Fallback</b>：加载失败时显示 fallback URL</li>
 *   <li><b>Placeholder</b>：加载中显示占位图</li>
 *   <li><b>Alt</b>：无图时显示文本</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * StackPane img = ImageAnt.create()
 *     .src("https://example.com/photo.jpg")
 *     .width(200).height(150)
 *     .borderRadius(8)
 *     .alt("加载失败")
 *     .build();
 * }</pre>
 */
public class ImageAnt {
    private static final String CONTROLLER_KEY = ImageAnt.class.getName() + ".controller";

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String src = null;
        private double width = 0;
        private double height = 0;
        private String alt = "";
        private boolean preview = false;
        private String fallback = null;
        private String placeholder = null;
        private String objectFit = "cover";
        private double borderRadius = 0;

        public Builder src(String src) { this.src = src; return this; }
        public Builder width(double width) { this.width = Double.isFinite(width) && width >= 0 ? width : 0; return this; }
        public Builder height(double height) { this.height = Double.isFinite(height) && height >= 0 ? height : 0; return this; }
        public Builder alt(String alt) { this.alt = alt != null ? alt : ""; return this; }
        public Builder preview(boolean preview) { this.preview = preview; return this; }
        public Builder preview() { return preview(true); }
        public Builder fallback(String fallback) { this.fallback = fallback; return this; }
        public Builder placeholder(String placeholder) { this.placeholder = placeholder; return this; }
        public Builder objectFit(String fit) { this.objectFit = fit != null ? fit : "cover"; return this; }
        public Builder borderRadius(double radius) { this.borderRadius = Double.isFinite(radius) && radius >= 0 ? radius : 0; return this; }

        public StackPane build() {
            StackPane container = new StackPane();
            container.setAlignment(Pos.CENTER);
            container.getStyleClass().add(JfxStyles.IMAGE);
            render(container);
            container.getProperties().put(CONTROLLER_KEY, new Controller(this, container));
            applyStyles(container);
            return container;
        }

        private void render(StackPane container) {
            container.getChildren().clear();
            container.getStyleClass().removeAll(JfxStyles.IMAGE_ROUNDED, JfxStyles.IMAGE_CIRCLE);
            container.setClip(null);

            if (width > 0) {
                container.setPrefWidth(width);
                container.setMaxWidth(width);
            }
            if (height > 0) {
                container.setPrefHeight(height);
                container.setMaxHeight(height);
            }
            if (borderRadius > 0) {
                container.getStyleClass().add(JfxStyles.IMAGE_ROUNDED);
                Rectangle clip = new Rectangle();
                clip.widthProperty().bind(container.widthProperty());
                clip.heightProperty().bind(container.heightProperty());
                clip.setArcWidth(borderRadius * 2);
                clip.setArcHeight(borderRadius * 2);
                container.setClip(clip);
            }

            if (src != null && !src.isEmpty()) {
                try {
                    Image image = new Image(src,
                            width > 0 ? width : 0, height > 0 ? height : 0, true, true);
                    ImageView imageView = new ImageView(image);
                    if (width > 0) imageView.setFitWidth(width);
                    if (height > 0) imageView.setFitHeight(height);
                    imageView.setPreserveRatio(true);

                    if (objectFit.equals("cover")) {
                        imageView.setPreserveRatio(false);
                        if (width > 0) imageView.setFitWidth(width);
                        if (height > 0) imageView.setFitHeight(height);
                    }

                    if (borderRadius > 0) {
                        Rectangle clip = new Rectangle(
                                width > 0 ? width : image.getWidth(),
                                height > 0 ? height : image.getHeight());
                        clip.setArcWidth(borderRadius * 2);
                        clip.setArcHeight(borderRadius * 2);
                        imageView.setClip(clip);
                    }

                    if (preview) {
                        // cursor 下沉到 .jfx-image-preview（LESS 提供 -fx-cursor: hand），避免红线 #1
                        imageView.getStyleClass().add(JfxStyles.IMAGE_PREVIEW);
                        imageView.setOnMouseClicked(e -> {
                            // Simple preview - could be enhanced with a modal
                            System.out.println("Preview: " + src);
                        });
                    }
                    container.getChildren().add(imageView);
                } catch (Exception e) {
                    showFallback(container);
                }
            } else {
                showPlaceholder(container);
            }
        }

        private void showFallback(StackPane container) {
            Label label = new Label(fallback != null ? fallback
                    : (alt.isEmpty() ? "Image Error" : alt));
            label.getStyleClass().add(JfxStyles.IMAGE_FALLBACK);
            container.getChildren().add(label);
        }

        private void showPlaceholder(StackPane container) {
            Label label = new Label(placeholder != null ? placeholder
                    : (alt.isEmpty() ? "No Image" : alt));
            label.getStyleClass().add(JfxStyles.IMAGE_FALLBACK);
            container.getChildren().add(label);
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Controller controllerOf(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("ImageAnt.controllerOf(node) 的 node 不能为 null");
        }
        Object controller = node.getProperties().get(CONTROLLER_KEY);
        if (controller instanceof Controller imageController) {
            return imageController;
        }
        throw new IllegalArgumentException("node 不是 ImageAnt.build() 返回的图片组件");
    }

    public static class Controller {
        private final Builder builder;
        private final StackPane container;

        private Controller(Builder builder, StackPane container) {
            this.builder = builder;
            this.container = container;
        }

        public String getSrc() {
            return builder.src;
        }

        public void setSrc(String src) {
            builder.src = src;
            refresh();
        }

        public String getPlaceholder() {
            return builder.placeholder;
        }

        public void setPlaceholder(String placeholder) {
            builder.placeholder = placeholder;
            refresh();
        }

        public String getAlt() {
            return builder.alt;
        }

        public void setAlt(String alt) {
            builder.alt = alt != null ? alt : "";
            refresh();
        }

        public double getWidth() {
            return builder.width;
        }

        public double getHeight() {
            return builder.height;
        }

        public void setSize(double width, double height) {
            builder.width = width;
            builder.height = height;
            refresh();
        }

        public double getBorderRadius() {
            return builder.borderRadius;
        }

        public void setBorderRadius(double radius) {
            builder.borderRadius = radius;
            refresh();
        }

        public boolean isPreview() {
            return builder.preview;
        }

        public void setPreview(boolean preview) {
            builder.preview = preview;
            refresh();
        }

        private void refresh() {
            builder.render(container);
        }
    }
}
