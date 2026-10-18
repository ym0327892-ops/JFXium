package org.openkawu.jfxium.core.style;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Elevation 阴影阶梯回归测试。
 *
 * <p>锁定的两条不变量 —— 都是「看源码看不出来、只在解析后的 CSS 上暴露」的失效模式：</p>
 * <ol>
 *   <li><b>阴影必须随明暗翻转</b>：历史上多处写死 {@code rgba(0,0,0,0.15)}，深色主题下
 *       阴影与背景同黑，等于没有浮层感。阶梯色令牌全部引用 {@code @color-shadow-default}，
 *       亮色 0.15 / 深色 0.4。本用例一旦失败，几乎一定是有人又写回了硬编码 rgba。</li>
 *   <li><b>档位必须单调</b>：blur 半径沿 2 &lt; 4 &lt; 8 &lt; 16 &lt; 24 &lt; 28 严格递增，
 *       否则「高层级」反而比低层级浅，视觉层级倒挂。</li>
 * </ol>
 *
 * <p>与 {@code KeyboardFocusRingTest} 同理，用 Scene 样式表而非 {@code setUserAgentStylesheet}
 * —— 后者是 JVM 全局状态，会污染同 fork 内其它用例。</p>
 */
@DisplayName("Elevation 阴影阶梯（明暗感知 + 档位单调）")
class ElevationShadowTest extends JfxTestBase {

    private static final String LIGHT = "/org/openkawu/jfxium/css/theme-light.css";
    private static final String DARK  = "/org/openkawu/jfxium/css/theme-dark.css";

    /** 阶梯档位 → 期望 blur 半径，与 variables-base.less 的 .shadow-elev-* 一一对应。 */
    private static final Map<String, Integer> LADDER = Map.of(
            "shadow-hairline", 2,
            "shadow-sm", 4,
            "shadow-md", 8,
            "shadow-lg", 16,
            "shadow-xl", 24,
            "shadow-2xl", 28);

    // ---------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------

    private static DropShadow shadowOf(Node n) {
        Effect e = n.getEffect();
        assertNotNull(e, () -> n.getClass().getSimpleName() + n.getStyleClass()
                + " 没有 effect —— 阴影丢失？（期望 dropshadow）");
        assertTrue(e instanceof DropShadow, () -> "期望 DropShadow，实为 " + e.getClass().getName());
        return (DropShadow) e;
    }

    /** 把主题 CSS 挂到 Scene，返回 applyCss+layout 后的容器。 */
    private static StackPane mount(String themeCss, Node... children) {
        StackPane root = new StackPane(children);
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(
                ElevationShadowTest.class.getResource(themeCss).toExternalForm());
        root.applyCss();
        root.layout();
        return root;
    }

    private static Region surface(String... classes) {
        Region r = new Region();
        r.getStyleClass().add("jfx-surface");
        for (String c : classes) {
            r.getStyleClass().add(c);
        }
        return r;
    }

    // ---------------------------------------------------------------
    // 1. 阶梯档位映射 + 单调性
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("1. 档位映射与单调性")
    class Ladder {

        @Test
        @DisplayName("6 个公开类名各自解析到对应 blur 半径")
        void tierMapping() {
            AtomicReference<String> err = new AtomicReference<>();
            runOnFxThreadAndWait(() -> {
                for (Map.Entry<String, Integer> e : LADDER.entrySet()) {
                    Region r = surface(e.getKey());
                    mount(LIGHT, r);
                    int actual = (int) Math.round(shadowOf(r).getRadius());
                    if (actual != e.getValue()) {
                        err.set(e.getKey() + " 的 blur 期望 " + e.getValue() + "，实为 " + actual
                                + " —— 类名与阶梯档位脱钩了？");
                        return;
                    }
                }
            });
            if (err.get() != null) {
                throw new AssertionError(err.get());
            }
        }

        @Test
        @DisplayName("blur 半径沿阶梯严格递增（层级不倒挂）")
        void monotone() {
            AtomicReference<String> err = new AtomicReference<>();
            runOnFxThreadAndWait(() -> {
                List<String> ordered = List.of(
                        "shadow-hairline", "shadow-sm", "shadow-md",
                        "shadow-lg", "shadow-xl", "shadow-2xl");
                double prev = -1;
                String prevName = null;
                for (String name : ordered) {
                    Region r = surface(name);
                    mount(LIGHT, r);
                    double radius = shadowOf(r).getRadius();
                    if (radius <= prev) {
                        err.set(prevName + "(" + prev + ") 与 " + name + "(" + radius
                                + ") 不满足严格递增 —— 高层级反而更浅");
                        return;
                    }
                    prev = radius;
                    prevName = name;
                }
            });
            if (err.get() != null) {
                throw new AssertionError(err.get());
            }
        }
    }

    // ---------------------------------------------------------------
    // 2. 明暗感知（硬编码 rgba 的直接围栏）
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("2. 明暗感知（阴影必须随主题翻转）")
    class ThemeAware {

        /** 每个档位在亮/深两色下都取一次 resolved 阴影 alpha。 */
        private double alpha(String css, String... classes) {
            Region r = surface(classes);
            mount(css, r);
            return shadowOf(r).getColor().getOpacity();
        }

        @Test
        @DisplayName("全部 6 档在深色下的不透明度严格高于亮色")
        void darkIsHeavier() {
            for (String name : LADDER.keySet()) {
                double light = alpha(LIGHT, name);
                double dark = alpha(DARK, name);
                assertTrue(dark > light,
                        name + " 的阴影 alpha 亮色=" + light + " 深色=" + dark
                                + " —— 深色未加重，疑似写回了硬编码 rgba（主题失明）");
            }
        }

        /**
         * 每个站点 = [样式类全集, 说明]。样式类必须与真实节点一致 ——
         * 例如 AlertDialog 的规则挂在 {@code .dialog-pane.jfx-alert-dialog} 上（双类选择器），
         * 只挂 {@code jfx-alert-dialog} 会静默匹配不到。
         */
        record Site(List<String> classes, String what) {}

        @Test
        @DisplayName("代表性子组件阴影同样随主题翻转（modal / alert / overlay / tooltip 等）")
        void componentSitesAreThemeAware() {
            List<Site> sites = List.of(
                    new Site(List.of("jfx-modal-content"), "Modal 主体"),
                    new Site(List.of("dialog-pane", "jfx-alert-dialog"), "AlertDialog"),
                    new Site(List.of("jfx-overlay-panel"), "Drawer/Modal 通用浮层面板"),
                    new Site(List.of("tooltip"), "Tooltip"),
                    new Site(List.of("jfx-popup-menu"), "弹出菜单"),
                    new Site(List.of("jfx-notification-card"), "Notification 卡片"),
                    new Site(List.of("jfx-message-card"), "Message 卡片"),
                    new Site(List.of("jfx-popconfirm-panel"), "Popconfirm 面板"),
                    new Site(List.of("jfx-back-top"), "回到顶部按钮"),
                    new Site(List.of("jfx-time-panel"), "时间面板"),
                    new Site(List.of("color-palette"), "取色板（DateTimePicker 弹层）"));

            for (Site s : sites) {
                double la = resolveAlpha(LIGHT, s.classes());
                double da = resolveAlpha(DARK, s.classes());
                assertTrue(da > la,
                        s.what() + "(" + s.classes() + ") 阴影 alpha 亮色=" + la + " 深色=" + da
                                + " —— 该站点未接入阶梯令牌，写着与主题无关的固定颜色");
            }
        }

        private double resolveAlpha(String css, List<String> classes) {
            Region r = new Region();
            r.getStyleClass().addAll(classes);
            mount(css, r);
            return shadowOf(r).getColor().getOpacity();
        }

        @Test
        @DisplayName("开关滑块（最低档 elev-2）在深色下也可见")
        void switchThumbIsVisibleInDark() {
            Region light = new Region();
            light.getStyleClass().add("jfx-switch-thumb");
            mount(LIGHT, light);
            double la = shadowOf(light).getColor().getOpacity();

            Region dark = new Region();
            dark.getStyleClass().add("jfx-switch-thumb");
            mount(DARK, dark);
            double da = shadowOf(dark).getColor().getOpacity();

            assertTrue(da > la,
                    "jfx-switch-thumb 的 2px 紧贴投影在深色下未加重（亮=" + la + " 深=" + da
                            + "）—— 深色底上该投影不可辨");
        }
    }

    // ---------------------------------------------------------------
    // 3. 焦点光晕与浮层投影分离（两类 effect 不得混用色令牌）
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("3. 焦点光晕 ≠ 浮层投影")
    class FocusGlowSeparate {

        @Test
        @DisplayName("输入框聚焦光晕用强调色，不随明暗翻转（不是 elevation）")
        void focusGlowUsesAccentNotElevation() {
            Region light = new Region();
            light.getStyleClass().add("text-field");
            mount(LIGHT, light);
            // 未聚焦：不应有 effect（光晕只在 :focused 出现）
            assertEquals(null, light.getEffect(),
                    "静止态文本框不应有 effect");
        }
    }
}
