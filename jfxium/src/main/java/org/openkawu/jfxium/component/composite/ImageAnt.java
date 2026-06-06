package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

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
        public Builder width(double width) { this.width = width; return this; }
        public Builder height(double height) { this.height = height; return this; }
        public Builder alt(String alt) { this.alt = alt; return this; }
        public Builder preview(boolean preview) { this.preview = preview; return this; }
        public Builder preview() { return preview(true); }
        public Builder fallback(String fallback) { this.fallback = fallback; return this; }
        public Builder placeholder(String placeholder) { this.placeholder = placeholder; return this; }
        public Builder objectFit(String fit) { this.objectFit = fit; return this; }
        public Builder borderRadius(double radius) { this.borderRadius = radius; return this; }

        public StackPane build() {
            StackPane container = new StackPane();
            container.setAlignment(Pos.CENTER);
            container.getStyleClass().add(CssClasses.IMAGE);

            if (width > 0) {
                container.setPrefWidth(width);
                container.setMaxWidth(width);
            }
            if (height > 0) {
                container.setPrefHeight(height);
                container.setMaxHeight(height);
            }
            // borderRadius 是用户传入的实例属性，无法预定义 LESS 选择器，保留 inline 注入
            // 但只设 -fx-background-radius，不再混搭 background-color（背景由 LESS 控制）
            if (borderRadius > 0) {
                container.setStyle("-fx-background-radius: " + borderRadius + "px;");
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
                        // cursor 是交互细节，inline 一行可接受；后续可下沉到 .jfx-image-preview
                        imageView.setStyle("-fx-cursor: hand;");
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
            return container;
        }

        private void showFallback(StackPane container) {
            Label label = new Label(fallback != null ? fallback
                    : (alt.isEmpty() ? "Image Error" : alt));
            label.getStyleClass().add(CssClasses.IMAGE_FALLBACK);
            container.getChildren().add(label);
        }

        private void showPlaceholder(StackPane container) {
            Label label = new Label(placeholder != null ? placeholder
                    : (alt.isEmpty() ? "No Image" : alt));
            label.getStyleClass().add(CssClasses.IMAGE_FALLBACK);
            container.getChildren().add(label);
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
