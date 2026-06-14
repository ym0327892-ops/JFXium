package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SegmentedAnt 单元测试 —— 覆盖选中态刷新和 Builder 样式接线。
 */
@DisplayName("SegmentedAnt")
class SegmentedAntTest extends JfxTestBase {

    @Test
    @DisplayName("点击选项会刷新 selected styleClass")
    void click_updatesSelectedClass() {
        HBox segmented = SegmentedAnt.create()
                .option("day", "日")
                .option("week", "周")
                .selected("day")
                .build();

        StackPane day = (StackPane) segmented.getChildren().get(0);
        StackPane week = (StackPane) segmented.getChildren().get(1);
        assertTrue(day.getStyleClass().contains(JfxStyles.SEGMENTED_ITEM_SELECTED));
        assertFalse(week.getStyleClass().contains(JfxStyles.SEGMENTED_ITEM_SELECTED));

        week.getOnMouseClicked().handle(null);

        assertFalse(day.getStyleClass().contains(JfxStyles.SEGMENTED_ITEM_SELECTED));
        assertTrue(week.getStyleClass().contains(JfxStyles.SEGMENTED_ITEM_SELECTED));
        assertEquals("week", SegmentedAnt.controllerOf(segmented).getSelected());
    }

    @Test
    @DisplayName("styleClass / prefWidth 应用到返回容器")
    void builderStyles_applied() {
        HBox segmented = SegmentedAnt.create()
                .option("a", "A")
                .styleClass("seg-extra")
                .prefWidth(240)
                .build();

        assertTrue(segmented.getStyleClass().contains("seg-extra"));
        assertEquals(240, segmented.getPrefWidth());
    }
}
