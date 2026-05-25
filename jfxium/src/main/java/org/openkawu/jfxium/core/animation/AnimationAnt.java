package org.openkawu.jfxium.core.animation;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * JFXium Animation System
 * 提供常用的 UI 动画效果，对标 Ant Design 动画规范
 *
 * Usage:
 * <pre>{@code
 * // 淡入
 * AnimationAnt.fadeIn(node, Duration.millis(250)).play();
 *
 * // 缩放进入
 * AnimationAnt.scaleIn(node, Duration.millis(200)).play();
 *
 * // 从底部滑入
 * AnimationAnt.slideInFromBottom(node, Duration.millis(300)).play();
 *
 * // 自定义组合动画
 * AnimationAnt.builder(node)
 *     .fade(0, 1)
 *     .translateY(20, 0)
 *     .scale(0.9, 1)
 *     .duration(Duration.millis(250))
 *     .play();
 * }</pre>
 */
public class AnimationAnt {

    // 动画持续时间常量（对标 Ant Design）
    public static final Duration DURATION_FAST = Duration.millis(150);
    public static final Duration DURATION_NORMAL = Duration.millis(250);
    public static final Duration DURATION_SLOW = Duration.millis(350);

    // 缓动函数（对标 Ant Design ease-in-out）
    private static final Interpolator EASE_IN_OUT = Interpolator.SPLINE(0.4, 0, 0.2, 1);
    private static final Interpolator EASE_OUT = Interpolator.SPLINE(0, 0, 0.2, 1);

    /**
     * 淡入动画
     */
    public static FadeTransition fadeIn(Node node, Duration duration) {
        FadeTransition transition = new FadeTransition(duration, node);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.setInterpolator(EASE_OUT);
        return transition;
    }

    /**
     * 淡入动画（使用默认时长）
     */
    public static FadeTransition fadeIn(Node node) {
        return fadeIn(node, DURATION_NORMAL);
    }

    /**
     * 淡出动画
     */
    public static FadeTransition fadeOut(Node node, Duration duration) {
        FadeTransition transition = new FadeTransition(duration, node);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setInterpolator(EASE_IN_OUT);
        return transition;
    }

    /**
     * 淡出动画（使用默认时长）
     */
    public static FadeTransition fadeOut(Node node) {
        return fadeOut(node, DURATION_NORMAL);
    }

    /**
     * 缩放进入动画
     */
    public static ScaleTransition scaleIn(Node node, Duration duration) {
        ScaleTransition transition = new ScaleTransition(duration, node);
        transition.setFromX(0.9);
        transition.setFromY(0.9);
        transition.setToX(1);
        transition.setToY(1);
        transition.setInterpolator(EASE_OUT);
        return transition;
    }

    /**
     * 缩放进入动画（使用默认时长）
     */
    public static ScaleTransition scaleIn(Node node) {
        return scaleIn(node, DURATION_NORMAL);
    }

    /**
     * 缩放退出动画
     */
    public static ScaleTransition scaleOut(Node node, Duration duration) {
        ScaleTransition transition = new ScaleTransition(duration, node);
        transition.setFromX(1);
        transition.setFromY(1);
        transition.setToX(0.9);
        transition.setToY(0.9);
        transition.setInterpolator(EASE_IN_OUT);
        return transition;
    }

    /**
     * 缩放退出动画（使用默认时长）
     */
    public static ScaleTransition scaleOut(Node node) {
        return scaleOut(node, DURATION_NORMAL);
    }

    /**
     * 从底部滑入
     */
    public static TranslateTransition slideInFromBottom(Node node, Duration duration) {
        TranslateTransition transition = new TranslateTransition(duration, node);
        transition.setFromY(30);
        transition.setToY(0);
        transition.setInterpolator(EASE_OUT);
        return transition;
    }

    /**
     * 从底部滑入（使用默认时长）
     */
    public static TranslateTransition slideInFromBottom(Node node) {
        return slideInFromBottom(node, DURATION_NORMAL);
    }

    /**
     * 从顶部滑入
     */
    public static TranslateTransition slideInFromTop(Node node, Duration duration) {
        TranslateTransition transition = new TranslateTransition(duration, node);
        transition.setFromY(-30);
        transition.setToY(0);
        transition.setInterpolator(EASE_OUT);
        return transition;
    }

    /**
     * 从顶部滑入（使用默认时长）
     */
    public static TranslateTransition slideInFromTop(Node node) {
        return slideInFromTop(node, DURATION_NORMAL);
    }

    /**
     * 从左侧滑入
     */
    public static TranslateTransition slideInFromLeft(Node node, Duration duration) {
        TranslateTransition transition = new TranslateTransition(duration, node);
        transition.setFromX(-30);
        transition.setToX(0);
        transition.setInterpolator(EASE_OUT);
        return transition;
    }

    /**
     * 从左侧滑入（使用默认时长）
     */
    public static TranslateTransition slideInFromLeft(Node node) {
        return slideInFromLeft(node, DURATION_NORMAL);
    }

    /**
     * 从右侧滑入
     */
    public static TranslateTransition slideInFromRight(Node node, Duration duration) {
        TranslateTransition transition = new TranslateTransition(duration, node);
        transition.setFromX(30);
        transition.setToX(0);
        transition.setInterpolator(EASE_OUT);
        return transition;
    }

    /**
     * 从右侧滑入（使用默认时长）
     */
    public static TranslateTransition slideInFromRight(Node node) {
        return slideInFromRight(node, DURATION_NORMAL);
    }

    /**
     * 弹出动画（缩放 + 淡入）
     */
    public static ParallelTransition popIn(Node node, Duration duration) {
        FadeTransition fade = fadeIn(node, duration);
        ScaleTransition scale = scaleIn(node, duration);
        ParallelTransition parallel = new ParallelTransition(node, fade, scale);
        return parallel;
    }

    /**
     * 弹出动画（使用默认时长）
     */
    public static ParallelTransition popIn(Node node) {
        return popIn(node, DURATION_NORMAL);
    }

    /**
     * 消失动画（缩放 + 淡出）
     */
    public static ParallelTransition popOut(Node node, Duration duration) {
        FadeTransition fade = fadeOut(node, duration);
        ScaleTransition scale = scaleOut(node, duration);
        ParallelTransition parallel = new ParallelTransition(node, fade, scale);
        return parallel;
    }

    /**
     * 消失动画（使用默认时长）
     */
    public static ParallelTransition popOut(Node node) {
        return popOut(node, DURATION_NORMAL);
    }

    /**
     * 创建自定义动画构建器
     */
    public static AnimationBuilder builder(Node node) {
        return new AnimationBuilder(node);
    }

    /**
     * 动画构建器类，支持链式调用组合多种动画效果
     */
    public static class AnimationBuilder {
        private final Node node;
        private Duration duration = DURATION_NORMAL;
        private double fromOpacity = -1;
        private double toOpacity = -1;
        private double fromTranslateX = -1;
        private double toTranslateX = -1;
        private double fromTranslateY = -1;
        private double toTranslateY = -1;
        private double fromScaleX = -1;
        private double toScaleX = -1;
        private double fromScaleY = -1;
        private double toScaleY = -1;
        private Runnable onFinished;

        private AnimationBuilder(Node node) {
            this.node = node;
        }

        public AnimationBuilder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public AnimationBuilder fade(double from, double to) {
            this.fromOpacity = from;
            this.toOpacity = to;
            return this;
        }

        public AnimationBuilder translateX(double from, double to) {
            this.fromTranslateX = from;
            this.toTranslateX = to;
            return this;
        }

        public AnimationBuilder translateY(double from, double to) {
            this.fromTranslateY = from;
            this.toTranslateY = to;
            return this;
        }

        public AnimationBuilder scale(double from, double to) {
            this.fromScaleX = from;
            this.toScaleX = to;
            this.fromScaleY = from;
            this.toScaleY = to;
            return this;
        }

        public AnimationBuilder scaleX(double from, double to) {
            this.fromScaleX = from;
            this.toScaleX = to;
            return this;
        }

        public AnimationBuilder scaleY(double from, double to) {
            this.fromScaleY = from;
            this.toScaleY = to;
            return this;
        }

        public AnimationBuilder onFinished(Runnable handler) {
            this.onFinished = handler;
            return this;
        }

        /**
         * 构建并播放动画
         */
        public void play() {
            ParallelTransition parallel = new ParallelTransition();
            parallel.setNode(node);

            // 添加淡入淡出动画
            if (fromOpacity >= 0 && toOpacity >= 0) {
                FadeTransition fade = new FadeTransition(duration, node);
                fade.setFromValue(fromOpacity);
                fade.setToValue(toOpacity);
                fade.setInterpolator(EASE_OUT);
                parallel.getChildren().add(fade);
            }

            // 添加 X 轴位移动画
            if (fromTranslateX >= 0 && toTranslateX >= 0) {
                TranslateTransition translateX = new TranslateTransition(duration, node);
                translateX.setFromX(fromTranslateX);
                translateX.setToX(toTranslateX);
                translateX.setInterpolator(EASE_OUT);
                parallel.getChildren().add(translateX);
            }

            // 添加 Y 轴位移动画
            if (fromTranslateY >= 0 && toTranslateY >= 0) {
                TranslateTransition translateY = new TranslateTransition(duration, node);
                translateY.setFromY(fromTranslateY);
                translateY.setToY(toTranslateY);
                translateY.setInterpolator(EASE_OUT);
                parallel.getChildren().add(translateY);
            }

            // 添加 X 轴缩放动画
            if (fromScaleX >= 0 && toScaleX >= 0) {
                ScaleTransition scaleX = new ScaleTransition(duration, node);
                scaleX.setFromX(fromScaleX);
                scaleX.setToX(toScaleX);
                scaleX.setInterpolator(EASE_OUT);
                parallel.getChildren().add(scaleX);
            }

            // 添加 Y 轴缩放动画
            if (fromScaleY >= 0 && toScaleY >= 0) {
                ScaleTransition scaleY = new ScaleTransition(duration, node);
                scaleY.setFromY(fromScaleY);
                scaleY.setToY(toScaleY);
                scaleY.setInterpolator(EASE_OUT);
                parallel.getChildren().add(scaleY);
            }

            // 设置完成回调
            if (onFinished != null) {
                parallel.setOnFinished(e -> onFinished.run());
            }

            parallel.play();
        }
    }
}