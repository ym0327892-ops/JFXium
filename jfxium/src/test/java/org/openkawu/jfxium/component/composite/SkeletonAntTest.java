package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SkeletonAnt 单元测试。
 *
 * <p>覆盖：</p>
 * <ul>
 *   <li>所有 variant 的 clip 形状（防止 shimmer 动画超出容器边界）</li>
 *   <li>rect / shimmer 的 variant 形状参数</li>
 *   <li>非有限 width / height 回退默认值</li>
 *   <li>null variant 回退 TEXT</li>
 *   <li>noAnimation() 不创建 shimmer</li>
 *   <li>paragraph() / avatarText() 辅助方法</li>
 * </ul>
 */
@DisplayName("SkeletonAnt")
class SkeletonAntTest extends JfxTestBase {

    // -------------------- clip 形状（核心修复 #143） --------------------

    @Test
    @DisplayName("TEXT variant：clip 圆角矩形（arc=4），rect/shimmer 形状一致")
    void textVariant_hasRoundedRectClip() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.TEXT)
                .width(160).height(16)
                .build();

        Rectangle clip = assertAndCastClip(s, 160, 16);
        assertEquals(4, clip.getArcWidth(), 0.001);
        assertEquals(4, clip.getArcHeight(), 0.001);

        // rect / shimmer 形状与 clip 一致
        Rectangle rect = (Rectangle) s.getChildren().get(0);
        Rectangle shimmer = (Rectangle) s.getChildren().get(1);
        assertVariantShape(rect, 4, 4, 160, 16);
        assertVariantShape(shimmer, 4, 4, 160, 16);
    }

    @Test
    @DisplayName("ROUNDED variant：clip 圆角矩形（arc=8）")
    void roundedVariant_hasRoundedClip() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.ROUNDED)
                .width(120).height(48)
                .build();

        Rectangle clip = assertAndCastClip(s, 120, 48);
        assertEquals(8, clip.getArcWidth(), 0.001);
        assertEquals(8, clip.getArcHeight(), 0.001);
    }

    @Test
    @DisplayName("RECTANGULAR variant：clip 直角矩形（arc=0）")
    void rectangularVariant_hasRectClip() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.RECTANGULAR)
                .width(200).height(120)
                .build();

        Rectangle clip = assertAndCastClip(s, 200, 120);
        assertEquals(0, clip.getArcWidth(), 0.001);
        assertEquals(0, clip.getArcHeight(), 0.001);
    }

    @Test
    @DisplayName("CIRCULAR variant：clip 圆角矩形（arc=min(w,h)），width≠height 也安全")
    void circularVariant_hasCircularClip() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.CIRCULAR)
                .width(48).height(48)
                .build();

        Rectangle clip = assertAndCastClip(s, 48, 48);
        assertEquals(48, clip.getArcWidth(), 0.001);
        assertEquals(48, clip.getArcHeight(), 0.001);

        // rect 宽高被钳制到 min(w,h)
        Rectangle rect = (Rectangle) s.getChildren().get(0);
        Rectangle shimmer = (Rectangle) s.getChildren().get(1);
        assertEquals(48, rect.getWidth(), 0.001);
        assertEquals(48, rect.getHeight(), 0.001);
        assertEquals(48, shimmer.getArcWidth(), 0.001);
        assertEquals(48, shimmer.getArcHeight(), 0.001);
    }

    @Test
    @DisplayName("CIRCULAR variant 在 width≠height 时：clip arc=min(w,h) 仍能完整覆盖 StackPane 矩形")
    void circularVariant_nonSquare_keepsCircularClip() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.CIRCULAR)
                .width(80).height(40)
                .build();

        Rectangle clip = assertAndCastClip(s, 80, 40);
        // arc = min(80, 40) = 40
        assertEquals(40, clip.getArcWidth(), 0.001);
        assertEquals(40, clip.getArcHeight(), 0.001);
    }

    // -------------------- shimmer 动画起点 --------------------

    @Test
    @DisplayName("animated=true：shimmer 初始 translateX=-width（从左侧外开始）")
    void shimmer_startsOutsideLeftEdge() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.TEXT)
                .width(200).height(16)
                .build();

        // children: [rect, shimmer]
        Rectangle shimmer = (Rectangle) s.getChildren().get(1);
        assertEquals(-200, shimmer.getTranslateX(), 0.001);
    }

    @Test
    @DisplayName("noAnimation()：不创建 shimmer 子节点")
    void noAnimation_skipsShimmerChild() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.RECTANGULAR)
                .width(100).height(20)
                .noAnimation()
                .build();

        assertEquals(1, s.getChildren().size(), "noAnimation 模式下应只有 rect 一个子节点");
        Rectangle rect = (Rectangle) s.getChildren().get(0);
        assertTrue(rect.getStyleClass().contains(JfxStyles.SKELETON_RECT));
    }

    // -------------------- 空值 / 非法值回退 --------------------

    @Test
    @DisplayName("variant(null) 回退为 TEXT")
    void nullVariant_fallbackToText() {
        StackPane s = SkeletonAnt.create()
                .variant(null)
                .width(160).height(16)
                .build();

        // TEXT 圆角 = 4
        Rectangle clip = assertAndCastClip(s, 160, 16);
        assertEquals(4, clip.getArcWidth(), 0.001);
        assertEquals(4, clip.getArcHeight(), 0.001);
    }

    @Test
    @DisplayName("width / height 非有限值回退默认值（200/16）")
    void nonFiniteSize_fallbackToDefault() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.TEXT)
                .width(Double.NaN)
                .height(Double.POSITIVE_INFINITY)
                .build();

        assertEquals(200, s.getPrefWidth(), 0.001);
        assertEquals(16, s.getPrefHeight(), 0.001);
        Rectangle clip = assertAndCastClip(s, 200, 16);
        assertEquals(4, clip.getArcWidth(), 0.001);
    }

    @Test
    @DisplayName("width / height 负数回退默认值（200/16）")
    void negativeSize_fallbackToDefault() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.RECTANGULAR)
                .width(-50)
                .height(-20)
                .build();

        assertEquals(200, s.getPrefWidth(), 0.001);
        assertEquals(16, s.getPrefHeight(), 0.001);
    }

    // -------------------- 基础约定 --------------------

    @Test
    @DisplayName("build() 挂 jfx-skeleton styleClass，rect 挂 jfx-skeleton-rect，shimmer 挂 jfx-skeleton-shimmer")
    void build_appliesExpectedStyleClasses() {
        StackPane s = SkeletonAnt.create()
                .variant(SkeletonAnt.Variant.TEXT)
                .width(200).height(16)
                .build();

        assertTrue(s.getStyleClass().contains(JfxStyles.SKELETON));
        Rectangle rect = (Rectangle) s.getChildren().get(0);
        Rectangle shimmer = (Rectangle) s.getChildren().get(1);
        assertTrue(rect.getStyleClass().contains(JfxStyles.SKELETON_RECT));
        assertTrue(shimmer.getStyleClass().contains(JfxStyles.SKELETON_SHIMMER));
    }

    // -------------------- 辅助方法 --------------------

    @Test
    @DisplayName("paragraph(lines)：VBox 含 lines 个 TEXT skeleton，最后一行宽度 60%")
    void paragraph_buildsLinesWithLastShorter() {
        VBox paragraph = SkeletonAnt.paragraph(3, 200, 16);
        assertEquals(3, paragraph.getChildren().size());
        assertTrue(paragraph.getStyleClass().contains(JfxStyles.SKELETON_PARAGRAPH));

        // 前两行满宽，最后一行 60%
        StackPane line0 = (StackPane) paragraph.getChildren().get(0);
        StackPane line1 = (StackPane) paragraph.getChildren().get(1);
        StackPane line2 = (StackPane) paragraph.getChildren().get(2);
        assertEquals(200, line0.getPrefWidth(), 0.001);
        assertEquals(200, line1.getPrefWidth(), 0.001);
        assertEquals(120, line2.getPrefWidth(), 0.001);
    }

    @Test
    @DisplayName("avatarText()：HBox 含 CIRCULAR skeleton + paragraph(2)")
    void avatarText_buildsCircleAndParagraph() {
        HBox box = SkeletonAnt.avatarText();
        assertTrue(box.getStyleClass().contains(JfxStyles.SKELETON_AVATAR_TEXT));
        // [CIRCULAR skeleton, paragraph VBox]
        Node circle = box.getChildren().get(0);
        Node paragraph = box.getChildren().get(1);
        assertTrue(circle instanceof StackPane);
        assertTrue(paragraph instanceof VBox);
        // circle 是 CIRCULAR variant，clip 圆角 = min(40,40)=40
        StackPane circlePane = (StackPane) circle;
        Rectangle clip = assertAndCastClip(circlePane, 40, 40);
        assertEquals(40, clip.getArcWidth(), 0.001);
        assertEquals(40, clip.getArcHeight(), 0.001);
    }

    // -------------------- helpers --------------------

    /**
     * 取出 clip 并断言它是 Rectangle，尺寸与期望一致。
     */
    private Rectangle assertAndCastClip(StackPane s, double expectedW, double expectedH) {
        Node clip = s.getClip();
        assertNotNull(clip, "StackPane 应设置 clip 防止 shimmer 超出容器边界");
        assertTrue(clip instanceof Rectangle, "clip 必须是 Rectangle 实例，实际: " + clip.getClass().getName());
        Rectangle r = (Rectangle) clip;
        assertEquals(expectedW, r.getWidth(), 0.001, "clip 宽度应等于 StackPane 宽度");
        assertEquals(expectedH, r.getHeight(), 0.001, "clip 高度应等于 StackPane 高度");
        return r;
    }

    /**
     * 断言 Rectangle 的 variant 形状参数：arcWidth / arcHeight / width / height。
     */
    private void assertVariantShape(Rectangle r, double arcW, double arcH, double expectedW, double expectedH) {
        assertEquals(arcW, r.getArcWidth(), 0.001);
        assertEquals(arcH, r.getArcHeight(), 0.001);
        assertEquals(expectedW, r.getWidth(), 0.001);
        assertEquals(expectedH, r.getHeight(), 0.001);
    }
}
