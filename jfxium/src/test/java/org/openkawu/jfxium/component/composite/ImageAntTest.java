package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ImageAnt 单元测试 —— 覆盖 Builder 样式接线与运行时 Controller。
 */
@DisplayName("ImageAnt")
class ImageAntTest extends JfxTestBase {

    @Test
    @DisplayName("build() 返回 StackPane 并挂默认样式")
    void build_returnsStackPane() {
        StackPane image = ImageAnt.create()
                .width(160)
                .height(100)
                .placeholder("暂无图片")
                .build();

        assertNotNull(image);
        assertTrue(image.getStyleClass().contains(JfxStyles.IMAGE));
        assertEquals(160, image.getPrefWidth());
        assertEquals(100, image.getPrefHeight());
        assertEquals("暂无图片", fallbackOf(image).getText());
    }

    @Test
    @DisplayName("borderRadius 挂载 rounded 样式并设置裁剪")
    void borderRadius_applied() {
        StackPane image = ImageAnt.create()
                .width(120)
                .height(80)
                .borderRadius(12)
                .placeholder("圆角")
                .build();

        assertTrue(image.getStyleClass().contains(JfxStyles.IMAGE_ROUNDED));
        assertNotNull(image.getClip());
    }

    @Test
    @DisplayName("styleClass / prefWidth 应用到返回容器")
    void builderStyles_applied() {
        StackPane image = ImageAnt.create()
                .placeholder("占位")
                .styleClass("image-extra")
                .prefWidth(240)
                .build();

        assertTrue(image.getStyleClass().contains("image-extra"));
        assertEquals(240, image.getPrefWidth());
    }

    @Test
    @DisplayName("controllerOf 获取运行时控制器")
    void controllerOf_returnsController() {
        StackPane image = ImageAnt.create()
                .width(160)
                .height(100)
                .placeholder("占位图")
                .build();

        ImageAnt.Controller controller = ImageAnt.controllerOf(image);
        assertEquals("占位图", controller.getPlaceholder());
        assertEquals(0, controller.getBorderRadius());

        controller.setBorderRadius(16);
        controller.setPlaceholder("圆角");

        assertEquals(16, controller.getBorderRadius());
        assertEquals("圆角", fallbackOf(image).getText());
        assertTrue(image.getStyleClass().contains(JfxStyles.IMAGE_ROUNDED));
    }

    @Test
    @DisplayName("controllerOf 非 ImageAnt 节点抛出明确异常")
    void controllerOf_invalidNode() {
        assertThrows(IllegalArgumentException.class, () -> ImageAnt.controllerOf(new StackPane()));
    }

    private static Label fallbackOf(StackPane image) {
        return image.getChildren().stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .findFirst()
                .orElseThrow(() -> new AssertionError("ImageAnt should contain a fallback Label"));
    }
}
