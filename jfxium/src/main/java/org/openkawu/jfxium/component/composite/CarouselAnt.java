package org.openkawu.jfxium.component.composite;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.AnimationDuration;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 走马灯组件 - 对标 Ant Design Carousel（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：轮播图控件，支持自动播放、手动切换、指示点导航，
 * 用于展示图片或内容轮播。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>切换效果</b>：SCROLL（滚动）/ FADE（淡入淡出）</li>
 *   <li><b>自动播放</b>：autoplay + autoplayInterval（默认 3s）</li>
 *   <li><b>指示点位置</b>：TOP / CENTER / BOTTOM</li>
 *   <li><b>箭头按钮</b>：hover 显示左右切换箭头</li>
 *   <li>视觉样式走 LESS（{@code .jfx-carousel} 系列）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node carousel = CarouselAnt.create()
 *     .item(image1)
 *     .item(image2)
 *     .item(image3)
 *     .autoplay(true)
 *     .effect(CarouselAnt.Effect.SCROLL)
 *     .build();
 * }</pre>
 */
public class CarouselAnt {

    public enum Effect {
        SCROLL, FADE
    }

    /** dots 圆点位置 —— 默认 BOTTOM（Ant Design 标准）。 */
    public enum DotPosition {
        TOP, CENTER, BOTTOM
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private List<Node> items = new ArrayList<>();
        private boolean autoplay = false;
        private Duration autoplayInterval = Duration.seconds(3);
        private boolean dots = true;
        private boolean arrows = true;
        private Effect effect = Effect.SCROLL;
        private int initialIndex = 0;
        private DotPosition dotPosition = DotPosition.BOTTOM;

        public Builder item(Node item) { this.items.add(item); return this; }
        public Builder items(Node... items) {
            if (items != null) {
                for (Node item : items) {
                    if (item != null) this.items.add(item);
                }
            }
            return this;
        }
        public Builder autoplay(boolean autoplay) { this.autoplay = autoplay; return this; }
        public Builder autoplay() { return autoplay(true); }
        public Builder autoplayInterval(Duration interval) { this.autoplayInterval = interval; return this; }
        public Builder dots(boolean dots) { this.dots = dots; return this; }
        public Builder noDots() { return dots(false); }
        public Builder arrows(boolean arrows) { this.arrows = arrows; return this; }
        public Builder noArrows() { return arrows(false); }
        public Builder effect(Effect effect) { this.effect = effect != null ? effect : Effect.SCROLL; return this; }
        public Builder initialIndex(int index) { this.initialIndex = index; return this; }
        /** dots 位置：TOP / CENTER / BOTTOM（默认 BOTTOM）。 */
        public Builder dotPosition(DotPosition position) { this.dotPosition = position != null ? position : DotPosition.BOTTOM; return this; }

        public StackPane build() {
            if (items.isEmpty()) {
                StackPane empty = new StackPane(new Label("No items"));
                empty.getStyleClass().add(JfxStyles.CAROUSEL);
                empty.setMinHeight(200);
                applyStyles(empty);
                return empty;
            }

            StackPane carousel = new StackPane();
            carousel.getStyleClass().add(JfxStyles.CAROUSEL);
            carousel.setPrefHeight(300);

            StackPane contentPane = new StackPane();
            contentPane.getStyleClass().add(JfxStyles.CAROUSEL_CONTENT);
            // 关键：裁剪到自身边界，否则 SCROLL 动画时滑出/滑入的 slide 会越界
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
            clip.widthProperty().bind(contentPane.widthProperty());
            clip.heightProperty().bind(contentPane.heightProperty());
            contentPane.setClip(clip);

            for (Node item : items) {
                item.setVisible(false);
                contentPane.getChildren().add(item);
            }

            int safeInitial = (initialIndex >= 0 && initialIndex < items.size()) ? initialIndex : 0;
            items.get(safeInitial).setVisible(true);
            carousel.getChildren().add(contentPane);

            final int[] currentIndex = {safeInitial};

            if (arrows && items.size() > 1) {
                Button prevBtn = createArrowButton("<");
                Button nextBtn = createArrowButton(">");
                StackPane.setAlignment(prevBtn, Pos.CENTER_LEFT);
                StackPane.setAlignment(nextBtn, Pos.CENTER_RIGHT);
                StackPane.setMargin(prevBtn, new Insets(0, 0, 0, 8));
                StackPane.setMargin(nextBtn, new Insets(0, 8, 0, 0));
                // 关键：把 button 的 viewOrder 调到最前（数值越小越靠前），
                // 防止 contentPane / dotsBox 在 z-order 上盖住按钮的可点击区域
                prevBtn.setViewOrder(-100);
                nextBtn.setViewOrder(-100);

                prevBtn.setOnAction(e -> {
                    int newIndex = currentIndex[0] - 1;
                    if (newIndex < 0) newIndex = items.size() - 1;
                    // 上一张：方向 = -1（从左滑入）
                    navigateTo(items, currentIndex, newIndex, effect, contentPane, carousel, -1);
                });
                nextBtn.setOnAction(e -> {
                    int newIndex = (currentIndex[0] + 1) % items.size();
                    // 下一张：方向 = +1（从右滑入）
                    navigateTo(items, currentIndex, newIndex, effect, contentPane, carousel, 1);
                });
                carousel.getChildren().addAll(prevBtn, nextBtn);
            }

            if (dots && items.size() > 1) {
                HBox dotsBox = new HBox();
                dotsBox.setAlignment(Pos.CENTER);
                dotsBox.getStyleClass().add(JfxStyles.CAROUSEL_DOTS);
                // 根据 dotPosition 决定对齐方式与边距（与箭头按钮的 viewOrder 相同，浮在 slide 之上）
                Pos align;
                Insets margin;
                switch (dotPosition) {
                    case TOP    -> { align = Pos.TOP_CENTER;    margin = new Insets(16, 0, 0, 0); }
                    case CENTER -> { align = Pos.CENTER;        margin = Insets.EMPTY; }
                    default     -> { align = Pos.BOTTOM_CENTER; margin = new Insets(0, 0, 16, 0); }
                }
                StackPane.setAlignment(dotsBox, align);
                StackPane.setMargin(dotsBox, margin);
                // 关键 1：HBox 默认 maxWidth/maxHeight = MAX_VALUE，会被 StackPane 拉伸撑满父容器，
                // 导致 setAlignment 失效（dots 永远飘在中心）。限定为 USE_PREF_SIZE 才能真正按 alignment 摆。
                dotsBox.setMaxSize(javafx.scene.layout.Region.USE_PREF_SIZE, javafx.scene.layout.Region.USE_PREF_SIZE);
                // 关键 2：让 dotsBox 浮在 slide 之上，否则被 slide 撑满后挡住
                dotsBox.setViewOrder(-100);
                dotsBox.setPickOnBounds(true);

                List<Circle> dotCircles = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                    Circle dot = new Circle(4);
                    dot.getStyleClass().add(JfxStyles.CAROUSEL_DOT);
                    final int index = i;
                    dot.setOnMouseClicked(e -> {
                        // dot 跳转方向 = 目标索引相对当前索引（>0 从右滑入，<0 从左滑入）
                        int dir = index > currentIndex[0] ? 1 : -1;
                        navigateTo(items, currentIndex, index, effect, contentPane, carousel, dir);
                    });
                    dotCircles.add(dot);
                    dotsBox.getChildren().add(dot);
                }
                updateDots(dotCircles, currentIndex[0]);

                // 把 dots 列表挂到 properties 上，autoplay/navigateTo 后能找到它们更新 active
                carousel.getProperties().put("dotCircles", dotCircles);
                carousel.getChildren().add(dotsBox);
            }

            if (autoplay && items.size() > 1) {
                Timeline timeline = new Timeline(
                        new KeyFrame(autoplayInterval, e -> {
                            int newIndex = (currentIndex[0] + 1) % items.size();
                            navigateTo(items, currentIndex, newIndex, effect, contentPane, carousel, 1);
                        })
                );
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
                carousel.setOnMouseEntered(e -> timeline.pause());
                carousel.setOnMouseExited(e -> timeline.play());
            }
            applyStyles(carousel);
            return carousel;
        }

        /** 箭头按钮：视觉样式（背景、圆角、字色、cursor）由 LESS .carousel-arrow-btn 控制，包含 hover */
        private Button createArrowButton(String text) {
            Button btn = new Button(text);
            btn.getStyleClass().add(JfxStyles.CAROUSEL_ARROW_BTN);
            return btn;
        }

        private void navigateTo(List<Node> items, int[] currentIndex, int newIndex, Effect effect, StackPane contentPane, StackPane carousel) {
            // 兼容旧调用：默认按"向右滑入"处理
            navigateTo(items, currentIndex, newIndex, effect, contentPane, carousel, 1);
        }

        /**
         * 切换到指定索引；direction 决定 SCROLL 模式下的滑入方向：
         * <ul>
         *   <li>+1 = 从右侧滑入（next 行为）：旧 slide 向左滑出，新 slide 从右滑入</li>
         *   <li>-1 = 从左侧滑入（prev 行为）：旧 slide 向右滑出，新 slide 从左滑入</li>
         * </ul>
         * SCROLL 用「旧滑出 + 新滑入」并行动画，视觉连续，避免老版「旧瞬间消失」的突兀感。
         */
        private void navigateTo(List<Node> items, int[] currentIndex, int newIndex, Effect effect, StackPane contentPane, StackPane carousel, int direction) {
            if (newIndex == currentIndex[0]) return;
            Node currentItem = items.get(currentIndex[0]);
            Node newItem = items.get(newIndex);

            if (effect == Effect.FADE) {
                FadeTransition fadeOut = new FadeTransition(AnimationDuration.SLOW, currentItem);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    currentItem.setVisible(false);
                    newItem.setVisible(true);
                    newItem.setOpacity(0);
                    FadeTransition fadeIn = new FadeTransition(AnimationDuration.SLOW, newItem);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                });
                fadeOut.play();
            } else {
                // SCROLL：两张同时动画
                // - direction=+1 (next)：旧 slide 向左滑出 (0 → -w)，新 slide 从右滑入 (+w → 0)
                // - direction=-1 (prev)：旧 slide 向右滑出 (0 → +w)，新 slide 从左滑入 (-w → 0)
                double w = contentPane.getWidth();
                if (w <= 0) w = 560; // 容错：尚未布局完成时给个默认值
                double newFromX = direction >= 0 ? w : -w;
                double oldToX   = direction >= 0 ? -w : w;

                newItem.setVisible(true);
                newItem.setTranslateX(newFromX);

                TranslateTransition slideOut = new TranslateTransition(AnimationDuration.SLIDE, currentItem);
                slideOut.setFromX(0);
                slideOut.setToX(oldToX);
                slideOut.setInterpolator(Interpolator.EASE_OUT);

                TranslateTransition slideIn = new TranslateTransition(AnimationDuration.SLIDE, newItem);
                slideIn.setFromX(newFromX);
                slideIn.setToX(0);
                slideIn.setInterpolator(Interpolator.EASE_OUT);

                slideOut.setOnFinished(e -> {
                    // 动画完后：复位 currentItem 的 translateX，并隐藏（下次复用时再 setVisible(true)）
                    currentItem.setTranslateX(0);
                    currentItem.setVisible(false);
                });

                slideOut.play();
                slideIn.play();
            }
            currentIndex[0] = newIndex;

            // 更新 dots active 状态
            @SuppressWarnings("unchecked")
            List<Circle> dotCircles = (List<Circle>) carousel.getProperties().get("dotCircles");
            if (dotCircles != null) {
                updateDots(dotCircles, newIndex);
            }
        }

        /** 更新 dot 活跃状态：通过 styleClass 切换填充色与半径，不再 inline setStyle */
        private void updateDots(List<Circle> dots, int activeIndex) {
            for (int i = 0; i < dots.size(); i++) {
                Circle dot = dots.get(i);
                if (i == activeIndex) {
                    if (!dot.getStyleClass().contains(JfxStyles.CAROUSEL_DOT_ACTIVE)) {
                        dot.getStyleClass().add(JfxStyles.CAROUSEL_DOT_ACTIVE);
                    }
                    dot.setRadius(5);
                } else {
                    dot.getStyleClass().remove(JfxStyles.CAROUSEL_DOT_ACTIVE);
                    dot.setRadius(4);
                }
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
