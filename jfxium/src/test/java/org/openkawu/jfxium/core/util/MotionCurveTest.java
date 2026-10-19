package org.openkawu.jfxium.core.util;

import javafx.animation.Interpolator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 动效曲线令牌（{@link MotionCurve}）回归测试 —— 见 PROJECT_BUG.md #175。
 *
 * <p>只锁三件「看源码发现不了、破了才疼」的事：</p>
 * <ol>
 *   <li><b>曲线就是 Fluent 官方三次贝塞尔</b>：控制点一旦被手改（例如换成内置近似档），
 *       全站出入场节奏静默变味。这里按定义独立求值 cubic-bezier 逐点比对。</li>
 *   <li><b>相对内置档确有区别</b>：{@code EASE_OUT} 近似线性（x=0.1 只走 0.111），
 *       本类入场曲线应「快起」（x=0.1 走 ≥0.4）—— 这条防的是有人"图省事"改回内置常量，
 *       改回后测试仍会"通过"（只要能跑），但缓动语义已丢失。</li>
 *   <li><b>曲线端点严格 0/1</b>：端点不严格会导致动画起止跳变。</li>
 * </ol>
 */
class MotionCurveTest {

    /** CSS cubic-bezier 的标准求值：先由 x 二分反解参数 t，再取 y(t)。 */
    private static double cubicBezier(double x1, double y1, double x2, double y2, double x) {
        double lo = 0, hi = 1;
        for (int i = 0; i < 60; i++) {
            double t = (lo + hi) / 2;
            double xt = 3 * (1 - t) * (1 - t) * t * x1 + 3 * (1 - t) * t * t * x2 + t * t * t;
            if (xt < x) {
                lo = t;
            } else {
                hi = t;
            }
        }
        double t = (lo + hi) / 2;
        return 3 * (1 - t) * (1 - t) * t * y1 + 3 * (1 - t) * t * t * y2 + t * t * t;
    }

    private static void assertCurve(Interpolator actual, double x1, double y1, double x2, double y2) {
        for (int i = 0; i <= 50; i++) {
            double x = i / 50.0;
            double expected = cubicBezier(x1, y1, x2, y2, x);
            double got = actual.interpolate(0.0, 1.0, x);
            assertEquals(expected, got, 1e-6,
                    "曲线在 x=" + x + " 处与 cubic-bezier(" + x1 + "," + y1 + "," + x2 + "," + y2 + ") 不符");
        }
    }

    @Nested
    @DisplayName("曲线值 = Fluent 2 官方控制点")
    class FluentParity {

        @Test
        @DisplayName("ENTER = curveDecelerateMid (0,0,0,1)")
        void enter() {
            assertCurve(MotionCurve.ENTER, 0.0, 0.0, 0.0, 1.0);
        }

        @Test
        @DisplayName("EXIT = curveAccelerateMid (1,0,1,1)")
        void exit() {
            assertCurve(MotionCurve.EXIT, 1.0, 0.0, 1.0, 1.0);
        }

        @Test
        @DisplayName("STANDARD = curveEasyEase (0.33,0,0.67,1)")
        void standard() {
            assertCurve(MotionCurve.STANDARD, 0.33, 0.0, 0.67, 1.0);
        }
    }

    @Nested
    @DisplayName("语义与端点")
    class Semantics {

        @Test
        @DisplayName("入场「快起缓收」：前 10% 时间走 ≥40% 行程")
        void enterIsSnappy() {
            double at01 = MotionCurve.ENTER.interpolate(0.0, 1.0, 0.1);
            assertTrue(at01 >= 0.4, "入场应快起，实测 x=0.1 -> " + at01);
        }

        @Test
        @DisplayName("退场「缓起快收」：前 10% 时间走 ≤5% 行程")
        void exitIsSlowStart() {
            double at01 = MotionCurve.EXIT.interpolate(0.0, 1.0, 0.1);
            assertTrue(at01 <= 0.05, "退场应缓起，实测 x=0.1 -> " + at01);
        }

        @Test
        @DisplayName("内置 EASE_OUT 近似线性，与入场曲线明显不同（防改回内置档）")
        void differsFromBuiltin() {
            double builtin = Interpolator.EASE_OUT.interpolate(0.0, 1.0, 0.1);
            double ours = MotionCurve.ENTER.interpolate(0.0, 1.0, 0.1);
            assertTrue(Math.abs(ours - builtin) > 0.2,
                    "入场曲线与内置 EASE_OUT 过于接近（builtin=" + builtin + " ours=" + ours + "），"
                            + "缓动语义可能已丢失");
        }

        @Test
        @DisplayName("三档曲线端点严格 0 / 1（避免起止跳变）")
        void endpointsExact() {
            for (Interpolator c : new Interpolator[] {MotionCurve.ENTER, MotionCurve.EXIT, MotionCurve.STANDARD}) {
                assertEquals(0.0, c.interpolate(0.0, 1.0, 0.0), 1e-9, "起点应为 0");
                assertEquals(1.0, c.interpolate(0.0, 1.0, 1.0), 1e-9, "终点应为 1");
            }
        }
    }
}
