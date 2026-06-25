package org.openkawu.jfxium.component.composite;

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
}
