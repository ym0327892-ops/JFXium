package org.openkawu.jfxium.component.composite;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.AnimationDuration;
import org.openkawu.jfxium.core.util.IconPath;

/**
 * JFXium 回到顶部组件 - 对标 Ant Design BackTop（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：固定在右下角的浮动按钮，滚动到一定高度后显示，
 * 点击平滑滚动回顶部。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>可设触发显示高度（{@code visibilityHeight}，默认 400px）</li>
 *   <li>可设右下角位置（bottom + right）</li>
 *   <li>平滑滚动动画（可设 duration）</li>
 *   <li>可指定滚动目标容器（默认滚动父 ScrollPane）</li>
 *   <li>hover 效果走 LESS（{@code .jfx-back-top:hover}）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node backTop = BackTopAnt.create()
 *     .visibilityHeight(300)
 *     .bottom(60).right(40)
 *     .build();
 * // 添加到 Scene 根层（与 ScrollPane 同级）
 * }</pre>
 */
public class BackTopAnt {

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private int visibilityHeight = 400;
        private Duration duration = AnimationDuration.BACK_TOP;
        private Node target = null;
        private double bottom = 40;
        private double right = 40;

        public Builder visibilityHeight(int visibilityHeight) {
            this.visibilityHeight = Math.max(0, visibilityHeight);
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration != null ? duration : AnimationDuration.BACK_TOP;
            return this;
        }

        public Builder target(Node target) {
            this.target = target;
            return this;
        }

        public Builder bottom(double bottom) {
            this.bottom = Math.max(0, bottom);
            return this;
        }

        public Builder right(double right) {
            this.right = Math.max(0, right);
            return this;
        }

        public StackPane build() {
            StackPane backTop = new StackPane();
            backTop.getStyleClass().add(JfxStyles.BACK_TOP);
            backTop.setPrefSize(44, 44);
            backTop.setMaxSize(44, 44);
            backTop.setVisible(false);
            backTop.setManaged(false);
            backTop.setOpacity(0);
            StackPane.setAlignment(backTop, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(backTop, new Insets(0, right, bottom, 0));

            // 箭头图标，颜色随主题切换
            SVGPath arrow = IconPath.arrowUpScaled();
            arrow.getStyleClass().add(JfxStyles.BACK_TOP_ARROW);
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
                    if (scrollPane.getContent() == null) {
                        updateVisibility(backTop, 0);
                        return;
                    }
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
                FadeTransition fadeIn = new FadeTransition(AnimationDuration.FAST, backTop);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.setInterpolator(Interpolator.EASE_OUT);
                fadeIn.play();
            } else if (!shouldShow && backTop.isVisible()) {
                FadeTransition fadeOut = new FadeTransition(AnimationDuration.FAST, backTop);
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
            parent.getChildren().add(backTop);
        }
        return backTop;
    }
}
