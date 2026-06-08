package org.openkawu.jfxium.component.composite;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 骨架屏组件 - 对标 Ant Design Skeleton（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：数据加载前的占位动画，模拟真实内容的形状，
 * 提升用户体验（感知加载速度更快）。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>形态</b>：TEXT（文本行）/ CIRCULAR（圆形头像）/ RECTANGULAR（矩形图片）/ ROUNDED（圆角矩形）</li>
 *   <li><b>尺寸</b>：自定义 width / height</li>
 *   <li><b>动画</b>：可开关的脉冲动画（animated）</li>
 *   <li>可组合多个骨架构成完整页面占位</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>列表页加载中（多行 TEXT + 一个 CIRCULAR 头像）</li>
 *   <li>卡片加载中（RECTANGULAR 封面 + 多行 TEXT）</li>
 *   <li>详情页加载中（组合多形态）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 文本骨架行
 * Node textLine = SkeletonAnt.create()
 *     .variant(SkeletonAnt.Variant.TEXT)
 *     .width(200).height(16)
 *     .build();
 *
 * // 圆形头像骨架
 * Node avatar = SkeletonAnt.create()
 *     .variant(SkeletonAnt.Variant.CIRCULAR)
 *     .width(48).height(48)
 *     .build();
 * }</pre>
 */
public class SkeletonAnt {

    public enum Variant {
        TEXT, CIRCULAR, RECTANGULAR, ROUNDED
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Variant variant = Variant.TEXT;
        private double width = 200;
        private double height = 16;
        private boolean animated = true;

        public Builder variant(Variant variant) {
            this.variant = variant;
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

        public Builder animated(boolean animated) {
            this.animated = animated;
            return this;
        }

        public Builder noAnimation() {
            return animated(false);
        }

        public StackPane build() {
            StackPane skeleton = new StackPane();
            skeleton.getStyleClass().add(JfxStyles.SKELETON);
            skeleton.setPrefSize(width, height);
            skeleton.setMaxSize(width, height);

            Rectangle rect = new Rectangle(width, height);
            // 填充颜色通过 styleClass 在 LESS 中定义（避免 setStyle 中 CSS 变量导致 ClassCastException）
            rect.getStyleClass().add(JfxStyles.SKELETON_RECT);

            switch (variant) {
                case CIRCULAR -> {
                    double size = Math.min(width, height);
                    rect.setWidth(size);
                    rect.setHeight(size);
                    rect.setArcWidth(size);
                    rect.setArcHeight(size);
                }
                case ROUNDED -> {
                    rect.setArcWidth(8);
                    rect.setArcHeight(8);
                }
                case RECTANGULAR -> {
                    rect.setArcWidth(0);
                    rect.setArcHeight(0);
                }
                default -> {
                    rect.setArcWidth(4);
                    rect.setArcHeight(4);
                }
            }

            skeleton.getChildren().add(rect);

            if (animated) {
                Rectangle shimmer = new Rectangle(width, height);
                shimmer.getStyleClass().add(JfxStyles.SKELETON_SHIMMER);
                shimmer.setTranslateX(-width);

                switch (variant) {
                    case CIRCULAR -> {
                        double size = Math.min(width, height);
                        shimmer.setWidth(size);
                        shimmer.setHeight(size);
                        shimmer.setArcWidth(size);
                        shimmer.setArcHeight(size);
                    }
                    case ROUNDED -> {
                        shimmer.setArcWidth(8);
                        shimmer.setArcHeight(8);
                    }
                    default -> {
                        shimmer.setArcWidth(4);
                        shimmer.setArcHeight(4);
                    }
                }

                Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(shimmer.translateXProperty(), -width)),
                    new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(shimmer.translateXProperty(), width * 2))
                );
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();

                skeleton.getChildren().add(shimmer);
            }

            return skeleton;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    /**
     * Create a skeleton paragraph with multiple lines
     */
    public static VBox paragraph(int lines) {
        return paragraph(lines, 200, 16);
    }

    public static VBox paragraph(int lines, double width, double lineHeight) {
        VBox container = new VBox(8);
        for (int i = 0; i < lines; i++) {
            double lineWidth = (i == lines - 1) ? width * 0.6 : width;
            container.getChildren().add(
                SkeletonAnt.create()
                    .width(lineWidth)
                    .height(lineHeight)
                    .build()
            );
        }
        return container;
    }

    /**
     * Create a skeleton avatar + text combination
     */
    public static HBox avatarText() {
        HBox container = new HBox(12);
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        container.getChildren().addAll(
            SkeletonAnt.create()
                .variant(Variant.CIRCULAR)
                .width(40)
                .height(40)
                .build(),
            SkeletonAnt.paragraph(2, 160, 12)
        );
        return container;
    }
}
