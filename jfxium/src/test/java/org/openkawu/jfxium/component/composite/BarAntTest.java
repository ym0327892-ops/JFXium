package org.openkawu.jfxium.component.composite;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.builder.Radius;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BarAnt 单元测试")
class BarAntTest extends JfxTestBase {

    @Test
    @DisplayName("BarAnt.class 保留 background(Background): BarAnt 精确签名")
    void backgroundBridgeMethodKeepsBinarySignature() throws Exception {
        var method = BarAnt.class.getDeclaredMethod("background", Background.class);

        assertEquals(BarAnt.class, method.getReturnType());
    }

    @Test
    @DisplayName("LayoutCommon 兼容桥接方法返回 BarAnt，避免旧调用方 NoSuchMethodError")
    void layoutCommonBridgeMethodsReturnBarAnt() {
        BarAnt bar = BarAnt.create();

        assertSame(bar, bar.background(Background.SUBTLE));
        assertSame(bar, bar.padding(1, 2, 3, 4));
        assertSame(bar, bar.borderRadius(Radius.LG));
        assertSame(bar, bar.styleClass("jfx-test-bar"));

        assertTrue(bar.getStyleClass().contains(Background.SUBTLE.styleClass()));
        assertEquals(new Insets(1, 2, 3, 4), bar.getPadding());
        assertTrue(bar.getStyleClass().contains(JfxStyles.RADIUS_LG));
        assertTrue(bar.getStyleClass().contains("jfx-test-bar"));
    }

    @Test
    @DisplayName("LayoutCommon 方法后仍可继续链 BarAnt 专属 API")
    void layoutCommonMethodsKeepBarSpecificChain() {
        Label left = new Label("L");
        Label right = new Label("R");

        BarAnt bar = BarAnt.create()
                .background(Background.SUBTLE)
                .padding(1, 2, 3, 4)
                .left(left)
                .right(right)
                .build();

        assertSame(left, bar.getChildren().get(0));
        assertSame(right, bar.getChildren().get(2));
        assertTrue(bar.getStyleClass().contains(Background.SUBTLE.styleClass()));
        assertEquals(new Insets(1, 2, 3, 4), bar.getPadding());
    }

    @Test
    @DisplayName("负 gap 钳制为 0")
    void negativeGapIsClamped() {
        BarAnt bar = BarAnt.create().gap(-8);

        assertEquals(0, bar.getSpacing(), 0.0);
    }

    @Test
    @DisplayName("重复 build 不追加多余 spacer")
    void repeatedBuildDoesNotAppendDuplicateChildren() {
        BarAnt bar = BarAnt.create()
                .left(new Label("L"))
                .right(new Label("R"))
                .build();
        int firstCount = bar.getChildren().size();

        bar.build();

        assertEquals(firstCount, bar.getChildren().size());
    }

    @Test
    @DisplayName("build 后继续追加节点，再 build 可刷新布局")
    void buildCanRefreshAfterAddingMoreNodes() {
        Label left = new Label("L");
        Label right = new Label("R");
        BarAnt bar = BarAnt.create()
                .left(left)
                .build();

        bar.right(right).build();

        assertTrue(bar.getChildren().contains(left));
        assertTrue(bar.getChildren().contains(right));
    }
}
