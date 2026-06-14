package org.openkawu.jfxium.component.control;

import javafx.scene.control.TextArea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MentionsAnt 单元测试 —— 覆盖 Builder 样式接线。
 */
@DisplayName("MentionsAnt")
class MentionsAntTest extends JfxTestBase {

    @Test
    @DisplayName("build() 返回 TextArea 并挂默认样式")
    void build_returnsTextArea() {
        TextArea mentions = MentionsAnt.create()
                .placeholder("输入 @")
                .build();

        assertNotNull(mentions);
        assertTrue(mentions.getStyleClass().contains(JfxStyles.MENTIONS));
        assertTrue(mentions.getStyleClass().contains(JfxStyles.MENTIONS_AREA));
        assertEquals("输入 @", mentions.getPromptText());
    }

    @Test
    @DisplayName("styleClass / prefWidth 应用到返回 TextArea")
    void builderStyles_applied() {
        TextArea mentions = MentionsAnt.create()
                .styleClass("mentions-extra")
                .prefWidth(320)
                .build();

        assertTrue(mentions.getStyleClass().contains("mentions-extra"));
        assertEquals(320, mentions.getPrefWidth());
    }
}
