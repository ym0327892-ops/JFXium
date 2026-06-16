package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ListAnt")
class ListAntTest extends JfxTestBase {

    @Test
    @DisplayName("items(null) 按空列表处理")
    void itemsNull_treatedAsEmpty() {
        VBox list = ListAnt.create()
                .items(null)
                .build();

        assertNotNull(list);
        assertEquals(0, list.getChildren().size());
    }

    @Test
    @DisplayName("null item 被跳过，null title 渲染为空字符串")
    void nullItemAndTitle_safe() {
        VBox list = ListAnt.create()
                .items(Arrays.asList(
                        null,
                        new ListAnt.ListItem(null, null, null, null, null)
                ))
                .build();

        HBox row = (HBox) list.getChildren().get(0);
        VBox content = (VBox) row.getChildren().get(0);
        Label title = (Label) content.getChildren().get(0);
        assertEquals("", title.getText());
    }
}
