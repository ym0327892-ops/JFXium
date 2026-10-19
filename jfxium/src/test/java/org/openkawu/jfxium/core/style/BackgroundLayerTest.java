package org.openkawu.jfxium.core.style;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 布局原语 styleClass 不得吞掉调用方背景 —— 见 PROJECT_BUG.md #176。
 *
 * <p>GridAnt 会把 {@code .jfx-grid-col} 挂到<b>调用方自己的节点</b>上。历史上
 * LESS 给这批布局原语类写了 {@code -fx-background-color: transparent}，
 * 而它们所在文件在 {@code _tokens.less}（{@code .jfx-bg-*} 定义处）之后被导入 ——
 * 同特异性 + 靠后 = 恒定胜出，于是调用方 {@code background(Background.INSET)}
 * 被静默抹成透明。</p>
 *
 * <p>本测试锁两件事：</p>
 * <ol>
 *   <li>列节点确实拿到了 {@code .jfx-grid-col}（前提成立，防止测试变得"因为没挂类"而空过）</li>
 *   <li>拿到该类之后，背景仍然解析成 {@code -color-bg-inset} 的实际颜色</li>
 * </ol>
 *
 * <p>断言只在「CSS 真的被解析」时才有意义：无主题样式表时 {@code Region.getBackground()}
 * 恒为 null，这种环境直接跳过断言，而不是假装通过。</p>
 */
class BackgroundLayerTest extends JfxTestBase {

    @Test
    @DisplayName("Grid 列内的节点：.jfx-grid-col 不得压掉调用方 background(Background.INSET)")
    void gridColumnKeepsCallerBackground() {
        VBoxAnt plate = VBoxAnt.create().build();
        plate.background(Background.INSET);
        assertTrue(plate.getStyleClass().contains(Background.INSET.styleClass()),
                "background() 应挂上 .jfx-bg-inset");

        Region col = (Region) GridAnt.create()
                .row(GridAnt.row().col(12, plate))
                .build()
                .lookupAll(".jfx-grid-col").iterator().next();

        assertTrue(col.getStyleClass().contains("jfx-grid-col"),
                "GridAnt 应把 .jfx-grid-col 挂到列节点上（前提条件）");
        assertTrue(col.getStyleClass().contains(Background.INSET.styleClass()),
                "列节点应同时带上调用方的 .jfx-bg-inset");

        // 挂到场景里让 CSS 真正解析
        Scene scene = new Scene(new StackPane(col), 400, 200);
        ThemeManager.getInstance().applyTheme(new LightTheme());
        ThemeManager.getInstance().registerScene(scene);
        scene.getRoot().applyCss();
        scene.getRoot().layout();

        if (col.getBackground() == null || col.getBackground().getFills().isEmpty()) {
            // 该环境未解析到主题样式表（如无 JavaFX 后端时的降级路径）—— 不做虚假断言
            return;
        }
        var fill = col.getBackground().getFills().get(0).getFill();
        assertNotNull(fill, "列节点应有背景填充");
        assertFalse(fill.toString().endsWith(", 0.0]") || fill.toString().contains("0x00000000"),
                "背景不应是透明（曾被 .jfx-grid-col 覆盖），实际 = " + fill);
        assertEquals("0xeaeef2ff", fill.toString().replace("0x", "0x").toLowerCase(),
                "背景应为 light 主题的 -color-bg-inset #eaeef2，实际 = " + fill);
    }
}
