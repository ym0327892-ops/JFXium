package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatisticAnt 单元测试")
class StatisticAntTest extends JfxTestBase {

    @Test
    @DisplayName("title/value/size 空值安全回落默认")
    void nullOptionsAreSafe() {
        VBox statistic = assertDoesNotThrow(() -> StatisticAnt.create()
                .title(null)
                .value((String) null)
                .size(null)
                .build());

        assertTrue(statistic.getStyleClass().contains(JfxStyles.STATISTIC));
        assertFalse(statistic.getStyleClass().contains(JfxStyles.STATISTIC_SMALL));
        assertFalse(statistic.getStyleClass().contains(JfxStyles.STATISTIC_LARGE));
    }

    @Test
    @DisplayName("controller 可读取并更新空值构建的节点")
    void controllerWorksAfterNullOptions() {
        VBox statistic = StatisticAnt.create()
                .title(null)
                .value((String) null)
                .build();

        StatisticAnt.Controller controller = StatisticAnt.controllerOf(statistic);
        assertEquals("", controller.getTitle());
        assertEquals("", controller.getValue());

        controller.setTitle("Users");
        controller.setValue("42");

        assertEquals("Users", controller.getTitle());
        assertEquals("42", controller.getValue());
    }
}
