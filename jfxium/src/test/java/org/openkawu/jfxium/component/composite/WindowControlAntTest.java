package org.openkawu.jfxium.component.composite;

import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.IconPath;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link WindowControlAnt} —— Win11 标题栏按钮组。
 *
 * <p>锁四类契约：</p>
 * <ol>
 *   <li><b>结构</b>：三个按钮、顺序、样式类、幂等 build</li>
 *   <li><b>窗口联动</b>：最小化/最大化/还原、最大化状态双向同步、解绑后不再跟随</li>
 *   <li><b>关闭语义</b>：发 {@code WINDOW_CLOSE_REQUEST}，应用 consume 时窗口不关</li>
 *   <li><b>解析后的 CSS</b>：46×32 几何、静止透明、悬停/按压/关闭红 —— 防「选择器打不过
 *       .button.text:hover」这类只在真机解析时才暴露的回归</li>
 * </ol>
 */
@DisplayName("WindowControlAnt 单元测试")
class WindowControlAntTest extends JfxTestBase {

    private static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
    private static final PseudoClass PRESSED = PseudoClass.getPseudoClass("pressed");
    private static final PseudoClass FOCUS_VISIBLE = PseudoClass.getPseudoClass("focus-visible");

    private static final String FLUENT = "/org/openkawu/jfxium/css/theme-fluent.css";
    private static final String FLUENT_DARK = "/org/openkawu/jfxium/css/theme-fluent-dark.css";
    private static final String LIGHT_COMPACT = "/org/openkawu/jfxium/css/theme-light-compact.css";

    // ---------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------

    /** 统计 hide() 调用次数的测试 Stage —— 不依赖真实窗口即可断言「关掉了」。 */
    private static final class CountingStage extends Stage {
        private final AtomicInteger hides = new AtomicInteger();

        @Override
        public void hide() {
            hides.incrementAndGet();
            super.hide();
        }
    }

    private static StackPane mount(Scene[] holder, String themeCss, Node content) {
        StackPane root = new StackPane(content);
        Scene scene = new Scene(root, 400, 200);
        scene.getStylesheets().add(
                WindowControlAntTest.class.getResource(themeCss).toExternalForm());
        holder[0] = scene;
        root.applyCss();
        root.layout();
        return root;
    }

    private record Panel(Button minimize, Button maximize, Button close) { }

    /**
     * 按位置取三个按钮 —— 最大化按钮的样式类会在 maximize/restore 之间切换，
     * 用类名 lookup 在切换后会取不到（那正是被测行为本身），故按固定顺序取。
     */
    private static Panel buttons(WindowControlAnt controls) {
        assertEquals(3, controls.getChildren().size(), "调用前应先 build()");
        return new Panel(
                (Button) controls.getChildren().get(0),
                (Button) controls.getChildren().get(1),
                (Button) controls.getChildren().get(2));
    }

    private static Paint fillOf(Region region) {
        if (region.getBackground() == null || region.getBackground().getFills().isEmpty()) {
            return null;
        }
        return region.getBackground().getFills().get(0).getFill();
    }

    private static Paint glyphFill(Button button) {
        SVGPath glyph = (SVGPath) button.lookup("." + JfxStyles.WINDOW_CONTROL_GLYPH);
        assertNotNull(glyph, "按钮内应有 .jfx-window-control-glyph 字形");
        return glyph.getFill();
    }

    private static void assertColor(Color expected, Paint actual, String label) {
        assertNotNull(actual, label + "：没有解析到颜色");
        assertTrue(actual instanceof Color, label + "：不是纯色，实为 " + actual);
        Color c = (Color) actual;
        assertEquals(expected.getRed(), c.getRed(), 0.01, label + " 红分量不符：" + c);
        assertEquals(expected.getGreen(), c.getGreen(), 0.01, label + " 绿分量不符：" + c);
        assertEquals(expected.getBlue(), c.getBlue(), 0.01, label + " 蓝分量不符：" + c);
        assertEquals(expected.getOpacity(), c.getOpacity(), 0.01, label + " 透明度不符：" + c);
    }

    // ---------------------------------------------------------------
    // 1. 结构
    // ---------------------------------------------------------------

    @Test
    @DisplayName("build 后为 [最小化, 最大化, 关闭] 三个标题栏按钮")
    void structure() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create().build();

            assertEquals(3, controls.getChildren().size());
            assertTrue(controls.getStyleClass().contains(JfxStyles.WINDOW_CONTROL));

            Panel p = buttons(controls);
            assertNotNull(p.minimize());
            assertNotNull(p.maximize());
            assertNotNull(p.close());
            assertSame(p.minimize(), controls.getChildren().get(0));
            assertSame(p.maximize(), controls.getChildren().get(1));
            assertSame(p.close(), controls.getChildren().get(2));

            // 类名查找也必须命中同一批节点（样式表的挂点契约）
            assertSame(p.minimize(), controls.lookup("." + JfxStyles.WINDOW_CONTROL_MINIMIZE));
            assertSame(p.maximize(), controls.lookup("." + JfxStyles.WINDOW_CONTROL_MAXIMIZE));
            assertSame(p.close(), controls.lookup("." + JfxStyles.WINDOW_CONTROL_CLOSE));

            for (Button b : List.of(p.minimize(), p.maximize(), p.close())) {
                assertTrue(b.getStyleClass().contains(JfxStyles.WINDOW_CONTROL_BUTTON),
                        "每个按钮都应挂 .jfx-window-control-button：" + b.getStyleClass());
                assertNotNull(b.getGraphic(), "标题栏按钮必须是字形按钮");
                assertEquals(IconPath.WINDOW_GLYPH_SCALE,
                        ((SVGPath) b.getGraphic()).getScaleX(), 1e-9,
                        "字形应按 WinUI 2048 网格缩放到 10px");
            }
            assertTrue(p.maximize().getStyleClass().contains(JfxStyles.WINDOW_CONTROL_MAXIMIZE));
            assertEquals(IconPath.WINDOW_MINIMIZE, ((SVGPath) p.minimize().getGraphic()).getContent());
            assertEquals(IconPath.WINDOW_MAXIMIZE, ((SVGPath) p.maximize().getGraphic()).getContent());
            assertEquals(IconPath.WINDOW_CLOSE, ((SVGPath) p.close().getGraphic()).getContent());
        });
    }

    @Test
    @DisplayName("build 幂等：重复调用不累积子节点")
    void buildIsIdempotent() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create();

            controls.build();
            controls.build();

            assertEquals(3, controls.getChildren().size());
        });
    }

    @Test
    @DisplayName("三个按钮都有可访问名字，最大化按钮名字随状态在「最大化/还原」间切换")
    void accessibleNamesFollowState() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create().build();
            Panel p = buttons(controls);

            for (Button b : List.of(p.minimize(), p.maximize(), p.close())) {
                assertNotNull(b.getAccessibleText(), "标题栏按钮必须有可访问名字");
                assertFalse(b.getAccessibleText().isBlank(), "可访问名字不能为空");
            }

            String restName = p.maximize().getAccessibleText();
            controls.maximized(true);
            String maximizedName = p.maximize().getAccessibleText();

            assertNotEquals(restName, maximizedName,
                    "最大化后读屏应播报「还原」而不是继续念「最大化」");
            assertEquals(Messages.get("a11y.maximize"), restName);
            assertEquals(Messages.get("a11y.restore"), maximizedName);
        });
    }

    @Test
    @DisplayName("i18n：窗口按钮语义键在 zh_CN / en 下都已定义（不是回退成 key 本身）")
    void windowButtonKeysAreTranslated() {
        runOnFxThreadAndWait(() -> {
            Locale original = Messages.getLocale();
            try {
                Messages.setLocale(Locale.SIMPLIFIED_CHINESE);
                assertEquals("最小化", Messages.get("a11y.minimize"));
                assertEquals("最大化", Messages.get("a11y.maximize"));
                assertEquals("还原", Messages.get("a11y.restore"));

                Messages.setLocale(Locale.ENGLISH);
                assertEquals("Minimize", Messages.get("a11y.minimize"));
                assertEquals("Maximize", Messages.get("a11y.maximize"));
                assertEquals("Restore", Messages.get("a11y.restore"));
            } finally {
                Messages.setLocale(original);
            }
        });
    }

    // ---------------------------------------------------------------
    // 2. 窗口联动
    // ---------------------------------------------------------------

    @Test
    @DisplayName("点击最小化 → stage.setIconified(true)")
    void minimizeIconifiesStage() {
        runOnFxThreadAndWait(() -> {
            Stage stage = new CountingStage();
            WindowControlAnt controls = WindowControlAnt.create().target(stage).build();

            buttons(controls).minimize().fire();

            assertTrue(stage.isIconified());
        });
    }

    @Test
    @DisplayName("点击最大化 → 窗口最大化；再点一次还原")
    void maximizeTogglesStage() {
        runOnFxThreadAndWait(() -> {
            Stage stage = new CountingStage();
            WindowControlAnt controls = WindowControlAnt.create().target(stage).build();

            buttons(controls).maximize().fire();
            assertTrue(stage.isMaximized());

            buttons(controls).maximize().fire();
            assertFalse(stage.isMaximized());
        });
    }

    @Test
    @DisplayName("最大化状态由 Stage 驱动：外部改 maximized → 字形/类名/名字全同步")
    void stageMaximizedDrivesVisualState() {
        runOnFxThreadAndWait(() -> {
            Stage stage = new CountingStage();
            WindowControlAnt controls = WindowControlAnt.create().target(stage).build();
            Panel p = buttons(controls);

            stage.setMaximized(true);

            assertTrue(controls.isMaximized());
            assertEquals(IconPath.WINDOW_RESTORE, ((SVGPath) p.maximize().getGraphic()).getContent(),
                    "已最大化时应显示「还原」叠框字形");
            assertTrue(p.maximize().getStyleClass().contains(JfxStyles.WINDOW_CONTROL_RESTORE));
            assertFalse(p.maximize().getStyleClass().contains(JfxStyles.WINDOW_CONTROL_MAXIMIZE));

            stage.setMaximized(false);

            assertFalse(controls.isMaximized());
            assertEquals(IconPath.WINDOW_MAXIMIZE, ((SVGPath) p.maximize().getGraphic()).getContent());
            assertTrue(p.maximize().getStyleClass().contains(JfxStyles.WINDOW_CONTROL_MAXIMIZE));
        });
    }

    @Test
    @DisplayName("解绑后不再跟随原窗口（监听已移除，不叠加）")
    void unbindRemovesListener() {
        runOnFxThreadAndWait(() -> {
            Stage stage = new CountingStage();
            WindowControlAnt controls = WindowControlAnt.create().target(stage).build();
            stage.setMaximized(true);
            assertTrue(controls.isMaximized());

            controls.target(null);
            assertFalse(controls.isMaximized(), "解绑时应回到未最大化视觉态");

            stage.setMaximized(false);
            stage.setMaximized(true);

            assertFalse(controls.isMaximized(), "解绑后旧窗口的变化不应再影响按钮");
        });
    }

    @Test
    @DisplayName("未绑定 Stage 时点击无副作用（不抛异常）")
    void withoutStageClicksAreNoOps() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create().build();

            buttons(controls).minimize().fire();
            buttons(controls).maximize().fire();
            buttons(controls).close().fire();

            assertFalse(controls.isMaximized());
            assertNull(controls.getTarget());
        });
    }

    // ---------------------------------------------------------------
    // 3. 关闭语义
    // ---------------------------------------------------------------

    @Test
    @DisplayName("关闭按钮：未被消费的 WINDOW_CLOSE_REQUEST → 隐藏窗口")
    void closeHidesWindowWhenNotConsumed() {
        runOnFxThreadAndWait(() -> {
            CountingStage stage = new CountingStage();
            AtomicBoolean received = new AtomicBoolean();
            stage.setOnCloseRequest(e -> received.set(true));
            WindowControlAnt controls = WindowControlAnt.create().target(stage).build();

            buttons(controls).close().fire();

            assertTrue(received.get(), "关闭按钮必须走标准 WINDOW_CLOSE_REQUEST 流程");
            assertEquals(1, stage.hides.get());
        });
    }

    @Test
    @DisplayName("关闭按钮：应用 consume 后可拦截关闭（如未保存提示）")
    void closeCanBeVetoedByApp() {
        runOnFxThreadAndWait(() -> {
            CountingStage stage = new CountingStage();
            stage.setOnCloseRequest(e -> e.consume());
            WindowControlAnt controls = WindowControlAnt.create().target(stage).build();

            buttons(controls).close().fire();

            assertEquals(0, stage.hides.get(), "事件被 consume 时窗口不能关");
        });
    }

    // ---------------------------------------------------------------
    // 4. 解析后的 CSS
    // ---------------------------------------------------------------

    @Test
    @DisplayName("几何：按钮 46×32，字形 10×10 且居中（WinUI caption 口径）")
    void geometryFromCss() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create().build();
            Scene[] holder = new Scene[1];
            mount(holder, FLUENT, controls);
            holder[0].getRoot().layout();

            Panel p = buttons(controls);
            for (Button b : List.of(p.minimize(), p.maximize(), p.close())) {
                assertEquals(46.0, b.getWidth(), 0.01, "按钮宽应为 46：" + b);
                assertEquals(32.0, b.getHeight(), 0.01, "按钮高应为 32：" + b);
            }

            Bounds maximizeGlyph = p.maximize().getGraphic().getBoundsInParent();
            assertEquals(10.0, maximizeGlyph.getWidth(), 0.2, "最大化字形宽应为 10px");
            assertEquals(10.0, maximizeGlyph.getHeight(), 0.2, "最大化字形高应为 10px");

            Bounds minimizeGlyph = p.minimize().getGraphic().getBoundsInParent();
            assertEquals(10.0, minimizeGlyph.getWidth(), 0.2, "最小化字形宽应为 10px");
            assertEquals(1.0, minimizeGlyph.getHeight(), 0.2, "最小化字形应为 1px 细线（Win11）");

            // 居中：字形视觉中心 == 按钮中心（场景坐标，含缩放变换）
            Button minimize = p.minimize();
            Bounds glyphScene = minimize.getGraphic().localToScene(
                    minimize.getGraphic().getBoundsInLocal());
            double glyphCenterX = (glyphScene.getMinX() + glyphScene.getMaxX()) / 2;
            double glyphCenterY = (glyphScene.getMinY() + glyphScene.getMaxY()) / 2;
            double buttonCenterX = minimize.localToScene(minimize.getWidth() / 2, 0).getX();
            double buttonCenterY = minimize.localToScene(0, minimize.getHeight() / 2).getY();

            assertEquals(buttonCenterX, glyphCenterX, 0.5, "字形应水平居中");
            assertEquals(buttonCenterY, glyphCenterY, 0.5, "字形应垂直居中");
        });
    }

    @Test
    @DisplayName("紧凑密度：按钮按 46:32 同比例收窄为 40×28")
    void compactGeometryFollowsDensity() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create().build();
            Scene[] holder = new Scene[1];
            mount(holder, LIGHT_COMPACT, controls);
            holder[0].getRoot().layout();

            Button minimize = buttons(controls).minimize();
            assertEquals(40.0, minimize.getWidth(), 0.01);
            assertEquals(28.0, minimize.getHeight(), 0.01);
        });
    }

    @Test
    @DisplayName("状态色：静止透明 → 悬停 6% 前景 → 按压 4% 前景（Fluent 亮色）")
    void hoverAndPressedFills() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create().build();
            Scene[] holder = new Scene[1];
            StackPane root = mount(holder, FLUENT, controls);
            Button minimize = buttons(controls).minimize();

            Paint rest = fillOf(minimize);
            assertTrue(rest == null || ((Color) rest).getOpacity() == 0,
                    "静止态必须透明，实为 " + rest);

            minimize.pseudoClassStateChanged(HOVER, true);
            root.applyCss();
            assertColor(Color.rgb(26, 26, 26, 0.06), fillOf(minimize),
                    "悬停背景应为 WinUI SubtleFillColorSecondary 等效层（fg 6%）");

            minimize.pseudoClassStateChanged(HOVER, false);
            minimize.pseudoClassStateChanged(PRESSED, true);
            root.applyCss();
            assertColor(Color.rgb(26, 26, 26, 0.04), fillOf(minimize),
                    "按压背景应为 SubtleFillColorTertiary 等效层（fg 4%）");
        });
    }

    @Test
    @DisplayName("关闭按钮：悬停转主题危险色，字形切对偶留白色")
    void closeButtonUsesDangerPair() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create().build();
            Scene[] holder = new Scene[1];
            StackPane root = mount(holder, FLUENT, controls);
            Button close = buttons(controls).close();

            assertColor(Color.web("#1a1a1a"), glyphFill(close), "静止字形应为 fg-default");

            close.pseudoClassStateChanged(HOVER, true);
            root.applyCss();

            assertColor(Color.web("#c50f1f"), fillOf(close),
                    "关闭悬停背景应为 Fluent 危险色（WinUI #C42B1C 同族）");
            assertColor(Color.WHITE, glyphFill(close), "悬停时字形应为对偶留白色");
        });
    }

    @Test
    @DisplayName("暗色主题：悬停层反转为 6% 白，关闭按钮取暗色危险槽")
    void darkThemeFlipsOverlay() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create().build();
            Scene[] holder = new Scene[1];
            StackPane root = mount(holder, FLUENT_DARK, controls);
            Panel p = buttons(controls);

            p.minimize().pseudoClassStateChanged(HOVER, true);
            root.applyCss();
            assertColor(Color.rgb(255, 255, 255, 0.06), fillOf(p.minimize()),
                    "暗色下悬停层应为 6% 白（WinUI #0FFFFFFF 同构）");

            p.close().pseudoClassStateChanged(HOVER, true);
            root.applyCss();
            assertColor(Color.web("#ff99a4"), fillOf(p.close()),
                    "Fluent 暗色的危险槽是提亮粉（danger-5 = #ff99a4）");
            assertColor(Color.BLACK, glyphFill(p.close()),
                    "暗色 emphasis 的对偶留白是黑色（palette-fluent-dark fg-on-emphasis）");
        });
    }

    @Test
    @DisplayName("键盘焦点：标题栏按钮可聚焦，且 :focus-visible 解析出双色焦点环")
    void keyboardFocusRing() {
        runOnFxThreadAndWait(() -> {
            WindowControlAnt controls = WindowControlAnt.create().build();
            Scene[] holder = new Scene[1];
            StackPane root = mount(holder, FLUENT, controls);
            Button minimize = buttons(controls).minimize();

            assertTrue(minimize.isFocusTraversable(), "自绘标题栏按钮必须可 Tab 到（a11y）");

            minimize.pseudoClassStateChanged(FOCUS_VISIBLE, true);
            root.applyCss();

            assertNotNull(minimize.getBorder(), "聚焦后应有焦点环边框");
            assertEquals(2, minimize.getBorder().getStrokes().size(),
                    "应为「外深内浅」双色环，实为 " + minimize.getBorder().getStrokes());
        });
    }
}
