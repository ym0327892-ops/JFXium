package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium BackTop Component - 对标 Ant Design BackTop。
 *
 * <h2>修复说明</h2>
 * 原实现存在 5 处 inline {@code setStyle}：
 * <ul>
 *   <li>容器初始样式：背景/圆角/边框/padding/cursor/dropshadow 拼一长串</li>
 *   <li>arrow 图标 fill</li>
 *   <li>{@code setOnMouseEntered} / {@code setOnMouseExited} 各自重新拼整段 setStyle 实现 hover</li>
 * </ul>
 * 这是典型"用 Java 事件回调实现 hover"的反模式：当 LESS 早就支持 {@code :hover} 伪类时，
 * 完全没必要走 Java。
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>容器样式搬到 LESS {@code .jfx-back-top}，hover 走 LESS {@code :hover} 伪类</li>
 *   <li>删除 {@code setOnMouseEntered} / {@code setOnMouseExited} 两段 inline 注入</li>
 *   <li>arrow 颜色走 LESS {@code .jfx-back-top-arrow}</li>
 *   <li>接入 {@link AbstractStyleBuilder}</li>
 * </ul>
 */
public class BackTopAnt {

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private int visibilityHeight = 400;
        private Duration duration = Duration.millis(450);
        private Node target = null;
        private double bottom = 40;
        private double right = 40;

        public Builder visibilityHeight(int visibilityHeight) {
            this.visibilityHeight = visibilityHeight;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder target(Node target) {
            this.target = target;
            return this;
        }

        public Builder bottom(double bottom) {
            this.bottom = bottom;
            return this;
        }

        public Builder right(double right) {
            this.right = right;
            return this;
        }

        public StackPane build() {
            StackPane backTop = new StackPane();
            backTop.getStyleClass().add(CssClasses.BACK_TOP);
            backTop.setPrefSize(44, 44);
            backTop.setMaxSize(44, 44);
            backTop.setVisible(false);
            backTop.setManaged(false);
            backTop.setOpacity(0);

            // 箭头图标，颜色随主题切换
            SVGPath arrow = new SVGPath();
            arrow.setContent("M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6z");
            arrow.setScaleX(1.5);
            arrow.setScaleY(1.5);
            arrow.getStyleClass().add(CssClasses.BACK_TOP_ARROW);
            backTop.getChildren().add(arrow);

            // hover 由 LESS .jfx-back-top:hover 控制，不再通过 Java 事件回调
            // 仅保留点击回调（业务逻辑非视觉）
            backTop.setOnMouseClicked(e -> scrollToTop());

            setupScrollListener(backTop);
            applyStyles(backTop);
            return backTop;
        }

        private void setupScrollListener(StackPane backTop) {
            if (target instanceof ScrollPane scrollPane) {
                scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
                    double scrollY = newVal.doubleValue()
                            * (scrollPane.getContent().getBoundsInLocal().getHeight()
                            - scrollPane.getViewportBounds().getHeight());
                    updateVisibility(backTop, scrollY);
                });
            }
        }

        private void updateVisibility(StackPane backTop, double scrollY) {
            boolean shouldShow = scrollY > visibilityHeight;
            if (shouldShow && !backTop.isVisible()) {
                backTop.setVisible(true);
                backTop.setManaged(true);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), backTop);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.setInterpolator(Interpolator.EASE_OUT);
                fadeIn.play();
            } else if (!shouldShow && backTop.isVisible()) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), backTop);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setInterpolator(Interpolator.EASE_IN);
                fadeOut.setOnFinished(e -> {
                    backTop.setVisible(false);
                    backTop.setManaged(false);
                });
                fadeOut.play();
            }
        }

        private void scrollToTop() {
            if (target instanceof ScrollPane scrollPane) {
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(scrollPane.vvalueProperty(), scrollPane.getVvalue())),
                        new KeyFrame(duration,
                                new KeyValue(scrollPane.vvalueProperty(), 0, Interpolator.EASE_BOTH))
                );
                timeline.play();
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }

    /** Install BackTop on a ScrollPane */
    public static StackPane install(ScrollPane scrollPane) {
        StackPane backTop = create().target(scrollPane).build();
        if (scrollPane.getParent() instanceof StackPane parent) {
            StackPane.setAlignment(backTop, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(backTop, new Insets(0, 40, 40, 0));
            parent.getChildren().add(backTop);
        }
        return backTop;
    }
}
