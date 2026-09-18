package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AutoCompleteAnt 单元测试 —— 覆盖 Builder 样式接线。
 */
@DisplayName("AutoCompleteAnt")
class AutoCompleteAntTest extends JfxTestBase {

    @Test
    @DisplayName("build() 返回 HBox 并挂默认样式")
    void build_returnsHBox() {
        HBox autoComplete = AutoCompleteAnt.<String>create()
                .options(List.of("北京", "上海"))
                .build();

        assertNotNull(autoComplete);
        assertTrue(autoComplete.getStyleClass().contains(JfxStyles.AUTO_COMPLETE));
    }

    @Test
    @DisplayName("styleClass / prefWidth 应用到返回容器")
    void builderStyles_applied() {
        HBox autoComplete = AutoCompleteAnt.<String>create()
                .options(List.of("北京", "上海"))
                .styleClass("auto-extra")
                .prefWidth(260)
                .build();

        assertTrue(autoComplete.getStyleClass().contains("auto-extra"));
        assertEquals(260, autoComplete.getPrefWidth());
    }
}
