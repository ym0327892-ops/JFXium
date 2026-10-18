package org.openkawu.jfxium.component.control;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 键盘焦点环（:focus-visible）回归测试 —— 见 PROJECT_BUG.md #169。
 *
 * <p>这些用例存在的唯一理由：框架里多处「静默失效」只能在<strong>解析后的 CSS</strong>上看出来，
 * 源码/编译产物检查都发现不了。</p>
 *
 * <h2>锁定的四条不变量</h2>
 * <ol>
 *   <li><b>变体按钮也必须拿到环</b>：JavaFX 特异度给伪类记 0x10、类选择器记 0x100，
 *       故 {@code .button.accent}(0x200) 会压过 {@code .button:focus-visible}(0x110)。
 *       焦点环必须带 {@code !important} 才能落地 —— 本用例一旦失败，几乎一定是有人
 *       「顺手清理」掉了 {@code !important}。</li>
 *   <li><b>环是双色（外深内浅）</b>：单色环在主色填充的按钮上与填充同色，等于零反馈。</li>
 *   <li><b>语义色优先于环</b>：{@code .jfx-input-status-error} 的校验红边不能被环压掉
 *       —— {@code !important} 只能限缩在按钮组。</li>
 *   <li><b>零布局位移</b>：外环走负 {@code -fx-border-insets}，不申请布局空间；
 *       {@code layoutX} / {@code layoutBounds} 在聚焦前后必须一致。</li>
 * </ol>
 *
 * <h2>为什么用 Scene 样式表而不是 setUserAgentStylesheet</h2>
 * {@code setUserAgentStylesheet} 是 JVM 全局状态，会污染同 fork 内的其它用例（本项目已多次
 * 因此类共享状态踩坑）。本仓库既有做法（如 {@code DatePickerTodayMarkerTest}）是把真实主题 CSS
 * 挂到 Scene 上，特异度之争发生在同一张表内，结论与 UA 表场景一致。
 */
@DisplayName("键盘焦点环 (:focus-visible 双色环 + 零位移)")
class KeyboardFocusRingTest extends JfxTestBase {

    private static final PseudoClass FV = PseudoClass.getPseudoClass("focus-visible");

    private static final String LIGHT = "/org/openkawu/jfxium/css/theme-light.css";
    private static final String DARK  = "/org/openkawu/jfxium/css/theme-dark.css";

    // 与 variables-base.less / variables-dark.less 的 @color-focus-* 同值
    private static final Color LIGHT_OUTER = Color.rgb(0, 0, 0, 0.9);
    private static final Color LIGHT_INNER = Color.rgb(255, 255, 255, 0.95);
    private static final Color DARK_OUTER  = Color.rgb(255, 255, 255, 0.9);
    private static final Color DARK_INNER  = Color.rgb(0, 0, 0, 0.9);

    // ---------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------

    private static List<BorderStroke> strokes(Node n) {
        if (n instanceof Region r && r.getBorder() != null) {
            return new ArrayList<>(r.getBorder().getStrokes());
        }
        return List.of();
    }

    private static Paint topStroke(Node n, int index) {
        List<BorderStroke> s = strokes(n);
        return s.size() > index ? s.get(index).getTopStroke() : null;
    }

    /** 把主题 CSS 挂到 Scene 上，返回已 applyCss+layout 的容器。 */
    private static StackPane mount(Scene[] sceneOut, String themeCss, Node... children) {
        StackPane root = new StackPane(children);
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(
                KeyboardFocusRingTest.class.getResource(themeCss).toExternalForm());
        sceneOut[0] = scene;
        root.applyCss();
        root.layout();
        return root;
    }

    /** 断言某节点在 :focus-visible 下解析出「外环 + 内环」两条描边色。 */
    private static void assertTwoToneRing(Node n, Color outer, Color inner, String label) {
        List<BorderStroke> s = strokes(n);
        assertEquals(2, s.size(),
                label + " 应有 2 条描边（外环 + 内环），实为 " + s.size()
                        + "：" + describe(n) + " —— 焦点环被更高特异度的规则压掉了？");
        assertEquals(outer, s.get(0).getTopStroke(), label + " 外环色不符：" + describe(n));
        assertEquals(inner, s.get(1).getTopStroke(), label + " 内环色不符：" + describe(n));
    }

    private static String describe(Node n) {
        List<BorderStroke> s = strokes(n);
        StringBuilder sb = new StringBuilder(n.getClass().getSimpleName());
        sb.append(n instanceof Region r ? String.valueOf(r.getStyleClass()) : "");
        sb.append(" borders=[");
        for (BorderStroke b : s) {
            sb.append(b.getTopStroke()).append("@insets").append(b.getInsets().getTop()).append(' ');
        }
        return sb.append(']').toString();
    }

    private static boolean isRingColor(Paint p) {
        return LIGHT_OUTER.equals(p) || LIGHT_INNER.equals(p)
                || DARK_OUTER.equals(p) || DARK_INNER.equals(p);
    }

    // ---------------------------------------------------------------
    // 1. 变体按钮必须拿到环（#169 的核心）
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("1. 按钮变体（靠 !important 压过 .button.* 的 2 类选择器）")
    class ButtonVariants {

        @Test
        @DisplayName("button.accent 聚焦时拿到双色环（静止态则没有）")
        void accentButton() {
            AtomicReference<String> err = new AtomicReference<>();
            runOnFxThreadAndWait(() -> {
                Scene[] holder = new Scene[1];
                Button accent = new Button("Accent");
                accent.getStyleClass().add("accent");
                mount(holder, LIGHT, accent);

                if (strokes(accent).size() != 1) {
                    err.set("静止态应以自身 1px 边框为准，实为 " + describe(accent));
                    return;
                }
                accent.pseudoClassStateChanged(FV, true);
                holder[0].getRoot().applyCss();
                holder[0].getRoot().layout();
                try {
                    assertTwoToneRing(accent, LIGHT_OUTER, LIGHT_INNER, "button.accent");
                } catch (AssertionError e) {
                    err.set(e.getMessage());
                }
            });
            if (err.get() != null) throw new AssertionError(err.get());
        }

        @Test
        @DisplayName("default / outlined / text / link / danger 变体同样拿到环")
        void allVariants() {
            AtomicReference<String> err = new AtomicReference<>();
            runOnFxThreadAndWait(() -> {
                Scene[] holder = new Scene[1];
                String[] variants = {"default", "outlined", "text", "link", "danger", "success"};
                List<Button> buttons = new ArrayList<>();
                for (String v : variants) {
                    Button b = new Button(v);
                    b.getStyleClass().add(v);
                    buttons.add(b);
                }
                Button plain = new Button("plain");
                buttons.add(plain);
                mount(holder, LIGHT, buttons.toArray(new Node[0]));
                for (Button b : buttons) b.pseudoClassStateChanged(FV, true);
                holder[0].getRoot().applyCss();
                holder[0].getRoot().layout();

                for (int i = 0; i < buttons.size(); i++) {
                    String label = i < variants.length ? "button." + variants[i] : "button(plain)";
                    try {
                        assertTwoToneRing(buttons.get(i), LIGHT_OUTER, LIGHT_INNER, label);
                    } catch (AssertionError e) {
                        err.set(e.getMessage());
                        return;
                    }
                }
            });
            if (err.get() != null) throw new AssertionError(err.get());
        }

        @Test
        @DisplayName("暗色主题翻转环色（外白内黑）")
        void darkInverts() {
            AtomicReference<String> err = new AtomicReference<>();
            runOnFxThreadAndWait(() -> {
                Scene[] holder = new Scene[1];
                Button accent = new Button("Accent");
                accent.getStyleClass().add("accent");
                mount(holder, DARK, accent);
                accent.pseudoClassStateChanged(FV, true);
                holder[0].getRoot().applyCss();
                holder[0].getRoot().layout();
                try {
                    assertTwoToneRing(accent, DARK_OUTER, DARK_INNER, "button.accent @dark");
                } catch (AssertionError e) {
                    err.set(e.getMessage());
                }
            });
            if (err.get() != null) throw new AssertionError(err.get());
        }
    }

    // ---------------------------------------------------------------
    // 2. 语义色优先（!important 的作用域边界）
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("2. !important 不得越界压掉语义色")
    class SemanticBorderWins {

        @Test
        @DisplayName("status-error 聚焦时仍是校验红边，而不是焦点环")
        void statusErrorKeepsRed() {
            AtomicReference<String> err = new AtomicReference<>();
            runOnFxThreadAndWait(() -> {
                Scene[] holder = new Scene[1];
                TextField bad = new TextField("bad");
                bad.getStyleClass().add("jfx-input-status-error");
                mount(holder, LIGHT, bad);
                bad.pseudoClassStateChanged(FV, true);
                holder[0].getRoot().applyCss();
                holder[0].getRoot().layout();

                Paint p = topStroke(bad, 0);
                String info = "status-error 聚焦后边框 = " + p + "（" + describe(bad) + "）";
                if (!(p instanceof Color)) {
                    err.set("期望 Color，实为 " + info);
                } else if (isRingColor(p)) {
                    err.set("校验红边被焦点环压掉了 —— !important 越界到输入控件：" + info);
                } else if (strokes(bad).size() != 1) {
                    err.set("状态输入框不应出现多层环：" + describe(bad));
                }
            });
            if (err.get() != null) throw new AssertionError(err.get());
        }

        @Test
        @DisplayName("selectable-text 焦点光晕的 accent 边不被环替换")
        void haloKeepsAccent() {
            AtomicReference<String> err = new AtomicReference<>();
            runOnFxThreadAndWait(() -> {
                Scene[] holder = new Scene[1];
                TextField halo = new TextField("halo");
                halo.getStyleClass().addAll("jfx-selectable-text", "jfx-selectable-text-focus-halo");
                mount(holder, LIGHT, halo);
                halo.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
                holder[0].getRoot().applyCss();
                holder[0].getRoot().layout();

                Paint p = topStroke(halo, 0);
                if (!(p instanceof Color c) || !Color.web("#1677ff").equals(c)) {
                    err.set("焦点光晕的 accent 边应保留，实为 " + p + "（" + describe(halo) + "）");
                }
            });
            if (err.get() != null) throw new AssertionError(err.get());
        }
    }

    // ---------------------------------------------------------------
    // 3. 零布局位移（负 inset 契约）
    // ---------------------------------------------------------------

    @Test
    @DisplayName("3. 聚焦不改变 layoutX / layoutBounds（外环不占布局空间）")
    void noLayoutShift() {
        AtomicReference<String> err = new AtomicReference<>();
        runOnFxThreadAndWait(() -> {
            Scene[] holder = new Scene[1];
            Button a = new Button("Default");
            Button b = new Button("Default");
            TextField ta = new TextField("t");
            TextField tb = new TextField("t");
            mount(holder, LIGHT, a, b, ta, tb);
            b.pseudoClassStateChanged(FV, true);
            tb.pseudoClassStateChanged(FV, true);
            holder[0].getRoot().applyCss();
            holder[0].getRoot().layout();

            // 聚焦的 b 必须真的拿到环，否则下面的比较毫无意义
            try {
                assertTwoToneRing(b, LIGHT_OUTER, LIGHT_INNER, "button（位移用例）");
                assertTwoToneRing(tb, LIGHT_OUTER, LIGHT_INNER, "text-field（位移用例）");
            } catch (AssertionError e) {
                err.set(e.getMessage());
                return;
            }
            assertEquals(a.getLayoutX(), b.getLayoutX(), "按钮聚焦后 layoutX 漂移");
            assertEquals(ta.getLayoutY(), tb.getLayoutY(), "输入框聚焦后 layoutY 漂移");
            assertEquals(a.getLayoutBounds().getWidth(), b.getLayoutBounds().getWidth(),
                    "按钮聚焦后 layoutBounds 宽度变化 —— 焦点环占用了布局空间？");
            assertEquals(ta.getLayoutBounds().getHeight(), tb.getLayoutBounds().getHeight(),
                    "输入框聚焦后 layoutBounds 高度变化 —— 焦点环占用了布局空间？");
            // boundsInParent 反而应该外扩 1px（外环画在盒子外）
            assertTrue(b.getBoundsInParent().getWidth() > a.getBoundsInParent().getWidth(),
                    "外环应扩到 boundsInParent 之外 1px");
        });
        if (err.get() != null) throw new AssertionError(err.get());
    }

    @Test
    @DisplayName("4. 基于 class 的焦点环同样生效（jfx-hyperlink / 搜索清除按钮）")
    void classBasedTargets() {
        AtomicReference<String> err = new AtomicReference<>();
        runOnFxThreadAndWait(() -> {
            Scene[] holder = new Scene[1];
            Hyperlink link = new Hyperlink("link");
            link.getStyleClass().add("jfx-hyperlink");
            Button clear = new Button("x");
            clear.getStyleClass().addAll("jfx-search-input-clear", "button");
            mount(holder, LIGHT, link, clear);
            link.pseudoClassStateChanged(FV, true);
            clear.pseudoClassStateChanged(FV, true);
            holder[0].getRoot().applyCss();
            holder[0].getRoot().layout();

            // 超链接只取单色外环（圆角不贴合文字基线，故用方框）
            List<BorderStroke> ls = strokes(link);
            if (ls.size() != 1 || !LIGHT_OUTER.equals(ls.get(0).getTopStroke())) {
                err.set("jfx-hyperlink 焦点环不符（选择器没匹配上？）：" + describe(link));
                return;
            }
            List<BorderStroke> cs = strokes(clear);
            if (cs.size() != 2) {
                err.set("搜索清除按钮应有双色环（.button.* 变体同理需 !important）：" + describe(clear));
                return;
            }
            assertNotEquals(null, cs.get(0).getTopStroke());
        });
        if (err.get() != null) throw new AssertionError(err.get());
    }
}
