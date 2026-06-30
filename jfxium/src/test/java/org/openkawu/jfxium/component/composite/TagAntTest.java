package org.openkawu.jfxium.component.composite;

import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TagAnt")
class TagAntTest extends JfxTestBase {

    @Test
    @DisplayName("Builder null 枚举和文本回退默认值")
    void builderNullValues_fallbackToDefaults() {
        HBox tag = TagAnt.create(null)
                .type(null)
                .size(null)
                .shape(null)
                .build();

        Label label = (Label) tag.getChildren().get(0);
        assertEquals("", label.getText());
        assertTrue(tag.getStyleClass().contains(JfxStyles.TAG_DEFAULT));
    }

    @Test
    @DisplayName("modify null 枚举和文本回退默认值")
    void modifyNullValues_fallbackToDefaults() {
        HBox tag = TagAnt.create("待处理")
                .type(TagAnt.Type.SUCCESS)
                .size(Size.LARGE)
                .shape(TagAnt.Shape.ROUND)
                .build();

        TagAnt.modify(tag)
                .type(null)
                .size(null)
                .shape(null)
                .text(null)
                .apply();

        Label label = (Label) tag.getChildren().get(0);
        assertEquals("", label.getText());
        assertTrue(tag.getStyleClass().contains(JfxStyles.TAG_DEFAULT));
    }

    @Test
    @DisplayName("modify color 保留调用方已有 inline style")
    void modifyColor_preservesExistingInlineStyle() {
        HBox tag = TagAnt.create("处理中")
                .style("-fx-opacity: 0.5;")
                .build();

        TagAnt.modify(tag)
                .color(Color.DODGERBLUE)
                .apply();

        assertTrue(tag.getStyle().contains("-fx-opacity: 0.5;"));
        assertTrue(tag.getStyle().contains("-fx-background-color: #1e90ff;"));

        TagAnt.modify(tag)
                .color(null)
                .apply();

        assertTrue(tag.getStyle().contains("-fx-opacity: 0.5;"));
    }
}
