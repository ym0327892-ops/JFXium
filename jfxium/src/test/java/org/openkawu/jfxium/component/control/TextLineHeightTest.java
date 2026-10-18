package org.openkawu.jfxium.component.control;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 文本行高令牌（{@code @line-height-*}）回归测试 —— 见 PROJECT_BUG.md #172。
 *
 * <p>只锁三件真正会回归、且只看源码发现不了的事：</p>
 * <ol>
 *   <li><b>行距只作用在行间</b>：JavaFX 的 {@code -fx-line-spacing} 语义与 Web 的
 *       {@code line-height} 不同 —— 它是「额外行距」，单行文本必须<strong>零影响</strong>。
 *       这条一旦破了（例如有人改用 padding / 放大字体来实现行高），全站单行标签会集体长高，
 *       表单与列表布局静默错位。</li>
 *   <li><b>行距随密度自动缩放</b>：令牌用 {@code em} 而非 {@code px}，紧凑主题把字号
 *       14→13px 时行距必须等比收缩。换成 {@code px} 就退化成「三份 compact 主题各抄一遍」。</li>
 *   <li><b>构建产物真的带了行距</b>：LESS 改了不生效是本项目踩过的坑（BUG #64
 *       groovy-maven-plugin 假成功），故对 7 份 CSS 产物逐一断言，而不是看 {@code .less} 源文件。</li>
 * </ol>
 */
@DisplayName("文本行高令牌 (@line-height-* 语义 + 密度派生 + 产物完整性)")
class TextLineHeightTest extends JfxTestBase {

    private static final String LIGHT = "/org/openkawu/jfxium/css/theme-light.css";
    private static final String LIGHT_COMPACT = "/org/openkawu/jfxium/css/theme-light-compact.css";

    private static final String WIRED = "jfx-typography-text";
    private static final String LONG = "项目内部综合规范是一份长期演进的约束文档,涵盖动画、边框、构建、布局与主题五个领域,"
            + "所有新增组件都必须遵守其中的强制条约,否则会在跨主题一致性检查中被判定为不合格。";

    // ---------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------

    /** 把真实主题 CSS 挂到 Scene 上（不用 setUserAgentStylesheet —— 那是 JVM 全局状态，会污染同 fork）。 */
    private static Scene scene(String css, Region root) {
        Scene scene = new Scene(root, 320, 600);
        var url = TextLineHeightTest.class.getResource(css);
        assertNotNull(url, "缺少 CSS 资源: " + css);
        scene.getStylesheets().add(url.toExternalForm());
        return scene;
    }

    private static String read(String css) {
        try (InputStream in = TextLineHeightTest.class.getResourceAsStream(css)) {
            assertNotNull(in, "缺少 CSS 资源: " + css);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            throw new AssertionError("读取 CSS 失败: " + css, e);
        }
    }

    // ---------------------------------------------------------------
    // 1. 行距只作用在行间：单行零影响 + 多行确实变高
    // ---------------------------------------------------------------

    @Test
    @DisplayName("行距只加在行间：单行高度不变，多行才变高")
    void leadingOnlyAppliesBetweenLines() {
        runOnFxThreadAndWait(() -> {
            // 同一类、同一字号，唯一差别是有没有行距（inline style 覆盖 class 上的值）
            LabelAnt withLeading = LabelAnt.create("");
            LabelAnt withoutLeading = LabelAnt.create("");
            withoutLeading.setStyle("-fx-line-spacing: 0;");

            withLeading.setWrapText(true);
            withoutLeading.setWrapText(true);
            withLeading.setPrefWidth(280);
            withoutLeading.setPrefWidth(280);

            StackPane root = new StackPane(withLeading, withoutLeading);
            scene(LIGHT, root);

            // 单行：必须完全一致（行距是「行间」距，单行无行间）
            withLeading.setText("单行");
            withoutLeading.setText("单行");
            root.applyCss();
            root.layout();
            double oneWith = withLeading.prefHeight(280);
            double oneWithout = withoutLeading.prefHeight(280);
            assertEquals(oneWithout, oneWith, 0.01,
                    "单行标签被行距抬高了 → 行距语义用错（应只作用于行间）");

            // 多行：必须明显变高（否则行距根本没生效）
            withLeading.setText(LONG);
            withoutLeading.setText(LONG);
            root.applyCss();
            root.layout();
            double manyWith = withLeading.prefHeight(280);
            double manyWithout = withoutLeading.prefHeight(280);
            assertTrue(manyWith > manyWithout + 1,
                    "多行标签套上行距后没有变高 → 行距没生效（有行距=" + manyWith
                            + " 无行距=" + manyWithout + "）");
        });
    }

    @Test
    @DisplayName("解析出的行距为正（说明 class 上的令牌确实解析到了节点）")
    void leadingResolvesOnWiredClasses() {
        runOnFxThreadAndWait(() -> {
            List<String> wired = List.of(
                    "jfx-typography-text",
                    "jfx-page-template-desc",
                    "jfx-form-label",
                    "jfx-popconfirm-desc",
                    "jfx-notification-card-desc",
                    "jfx-timeline-content",
                    "jfx-result-subtitle");

            StackPane root = new StackPane();
            scene(LIGHT, root);
            for (String cls : wired) {
                LabelAnt label = LabelAnt.create("文本");
                label.getStyleClass().add(cls);
                root.getChildren().add(label);
                root.applyCss();
                root.layout();
                assertTrue(label.getLineSpacing() > 0,
                        cls + " 未解析出行距（getLineSpacing=" + label.getLineSpacing() + "）");
            }
        });
    }

    // ---------------------------------------------------------------
    // 2. 密度派生：em 而非 px
    // ---------------------------------------------------------------

    @Test
    @DisplayName("行距用 em：紧凑密度下随字号等比收缩，而不是固定 px")
    void leadingScalesWithDensity() {
        runOnFxThreadAndWait(() -> {
            double normal = resolvedLeading(LIGHT, null);
            double compact = resolvedLeading(LIGHT_COMPACT, null);

            assertTrue(normal > 0 && compact > 0, "两档密度都应解析出正行距");
            assertTrue(compact < normal,
                    "紧凑密度下行距没有收缩 → 令牌被写成了 px（default=" + normal
                            + " compact=" + compact + "）");

            // 字号 14 → 13px，行距应按同比例收缩（容差放宽到 5%，避免字体/取整差异误报）
            double expectedRatio = 13.0 / 14.0;
            double actualRatio = compact / normal;
            assertEquals(expectedRatio, actualRatio, 0.05,
                    "行距未按字号比例收缩，em 派生失效（实际比=" + actualRatio + "）");
        });
    }

    /** 在某主题下解析 jfx-typography-text 的行距。 */
    private static double resolvedLeading(String css, Runnable ignored) {
        LabelAnt label = LabelAnt.create("文本");
        StackPane root = new StackPane(label);
        Scene scene = scene(css, root);
        label.applyCss();
        double v = label.getLineSpacing();
        scene.getWindow(); // 保持引用，避免被优化掉
        return v;
    }

    // ---------------------------------------------------------------
    // 3. 构建产物完整性（BUG #64：LESS 改了可能静默不生效）
    // ---------------------------------------------------------------

    @Test
    @DisplayName("7 份 CSS 产物都带上了行距（构建管线未被 jlessc 静默跳过）")
    void allArtifactsCarryLeading() {
        List<String> artifacts = List.of(
                "/org/openkawu/jfxium/css/theme-light.css",
                "/org/openkawu/jfxium/css/theme-dark.css",
                "/org/openkawu/jfxium/css/theme-light-compact.css",
                "/org/openkawu/jfxium/css/theme-dark-compact.css",
                "/org/openkawu/jfxium/css/theme-custom.css",
                "/org/openkawu/jfxium/css/theme-custom-compact.css",
                "/org/openkawu/jfxium/css/theme-light-tool.css");

        // 每个接线过的选择器都必须在其块内声明 -fx-line-spacing
        List<String> selectors = List.of(
                ".jfx-typography-text {",
                ".jfx-page-template-desc {",
                ".jfx-form-label {",
                ".jfx-popconfirm-desc",
                ".jfx-notification-card-desc",
                ".jfx-timeline-content {",
                ".jfx-result-subtitle");

        for (String artifact : artifacts) {
            String css = read(artifact);
            for (String selector : selectors) {
                String message = artifact + " 缺少 " + selector + " 的行距声明";
                int at = css.indexOf(selector);
                assertTrue(at >= 0, message);
                String block = css.substring(at, Math.min(css.length(), at + 320));
                assertTrue(block.contains("-fx-line-spacing"),
                        message + "（LESS 改了但产物没更新？见 BUG #64）");
            }
            assertNotEquals(0, css.indexOf("-fx-line-spacing"), artifact + " 完全没有行距声明");
        }
    }
}
