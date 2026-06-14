package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CalendarAnt 单元测试 —— 覆盖运行时刷新和 Builder 样式接线。
 */
@DisplayName("CalendarAnt")
class CalendarAntTest extends JfxTestBase {

    @Test
    @DisplayName("build() 返回 VBox 并挂默认样式")
    void build_returnsVBox() {
        VBox calendar = CalendarAnt.create()
                .value(LocalDate.of(2026, 6, 15))
                .build();

        assertNotNull(calendar);
        assertTrue(calendar.getStyleClass().contains(JfxStyles.CALENDAR));
        assertEquals(2, calendar.getChildren().size());
    }

    @Test
    @DisplayName("controller 更新 value/mode 后刷新根内容")
    void controller_refreshesCalendar() {
        VBox calendar = CalendarAnt.create()
                .value(LocalDate.of(2026, 6, 15))
                .build();

        CalendarAnt.Controller controller = CalendarAnt.controllerOf(calendar);
        controller.setMode(CalendarAnt.Mode.YEAR);
        assertEquals(CalendarAnt.Mode.YEAR, controller.getMode());
        assertEquals(2, calendar.getChildren().size());

        controller.setValue(LocalDate.of(2026, 7, 1));
        assertEquals(LocalDate.of(2026, 7, 1), controller.getValue());
        assertEquals(2, calendar.getChildren().size());
    }

    @Test
    @DisplayName("styleClass / prefWidth 应用到返回容器")
    void builderStyles_applied() {
        VBox calendar = CalendarAnt.create()
                .styleClass("calendar-extra")
                .prefWidth(360)
                .build();

        assertTrue(calendar.getStyleClass().contains("calendar-extra"));
        assertEquals(360, calendar.getPrefWidth());
    }
}
