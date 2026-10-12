package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Labeled;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CalendarAnt 单元测试。
 *
 * <p>月视图已统一到原生 {@code DatePickerSkin} 面板，因此这里的断言围绕
 * 「原生面板真的被内嵌、且与 DatePickerAnt 同源」展开 —— 不再逐像素校验自绘网格。</p>
 */
@DisplayName("CalendarAnt")
class CalendarAntTest extends JfxTestBase {

    @Test
    @DisplayName("build() 返回 VBox；月视图内嵌原生 date-picker-popup 面板")
    void build_returnsVBoxWithNativePanel() {
        runOnFxThreadAndWait(() -> {
            VBox calendar = CalendarAnt.create()
                    .value(LocalDate.of(2026, 6, 15))
                    .fullscreen(false)
                    .build();
            Pane root = new Pane(calendar);
            Scene scene = new Scene(root, 420, 480);
            scene.getStylesheets().add(CalendarAntTest.class.getResource(
                    "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
            root.applyCss();
            root.layout();

            assertTrue(calendar.getStyleClass().contains(JfxStyles.CALENDAR));
            Node nativePanel = calendar.lookup(".date-picker-popup");
            assertNotNull(nativePanel, "月视图必须内嵌原生 date-picker-popup 面板");
            assertTrue(nativePanel.getStyleClass().contains("jfx-calendar-embedded"),
                    "内嵌面板应带 jfx-calendar-embedded，供 CSS 压平自带边框");
            assertNotNull(calendar.lookup(".calendar-grid"), "原生面板应含 .calendar-grid");
            stage.hide();
        });
    }

    @Nested
    @DisplayName("与 DatePickerAnt 同源契约")
    class NativeConsistencyContract {

        /**
         * CalendarAnt 的日期网格必须与 DatePickerAnt 弹层**同一个实现、同一份几何**。
         *
         * <p>这是本组件的存在理由：历史上 CalendarAnt 自绘网格，导致同框架内两套日历 ——
         * 英文 {@code Su/Mo} + 周日起始 vs 原生中文 {@code 周一…周日} + 周一起始，格子尺寸也不同。
         * 本用例把「同源」钉死：两边的日期网格几何、星期表头文本必须逐项相等。</p>
         */
        @Test
        @DisplayName("日期网格几何 / 星期表头文本与原生 DatePicker 弹层完全一致")
        void matchesNativeDatePickerPopup() {
            for (String css : new String[]{"theme-light.css", "theme-light-compact.css"}) {
                runOnFxThreadAndWait(() -> {
                    // A：CalendarAnt 内嵌的原生面板
                    VBox calendar = CalendarAnt.create()
                            .value(LocalDate.of(2026, 10, 12))
                            .fullscreen(false)
                            .build();
                    Pane paneA = new Pane(calendar);
                    Scene sceneA = new Scene(paneA, 420, 480);
                    sceneA.getStylesheets().add(CalendarAntTest.class.getResource(
                            "/org/openkawu/jfxium/css/" + css).toExternalForm());
                    Stage stageA = new Stage();
                    stageA.setScene(sceneA);
                    stageA.show();
                    paneA.applyCss();
                    paneA.layout();

                    // B：DatePickerAnt 的弹层（原生面板本体）
                    DatePicker host = new DatePicker(LocalDate.of(2026, 10, 12));
                    Node popupContent = new DatePickerSkin(host).getPopupContent();
                    Pane paneB = new Pane(popupContent);
                    Scene sceneB = new Scene(paneB, 420, 480);
                    sceneB.getStylesheets().add(CalendarAntTest.class.getResource(
                            "/org/openkawu/jfxium/css/" + css).toExternalForm());
                    Stage stageB = new Stage();
                    stageB.setScene(sceneB);
                    stageB.show();
                    paneB.applyCss();
                    paneB.layout();

                    Node gridA = calendar.lookup(".calendar-grid");
                    Node gridB = popupContent.lookup(".calendar-grid");
                    assertNotNull(gridA, css + " 下 CalendarAnt 应有原生日期网格");
                    assertEquals(gridB.getLayoutBounds().getWidth(), gridA.getLayoutBounds().getWidth(), 0.5,
                            css + " 下两处日期网格宽必须一致");
                    assertEquals(gridB.getLayoutBounds().getHeight(), gridA.getLayoutBounds().getHeight(), 0.5,
                            css + " 下两处日期网格高必须一致");

                    Labeled weekdayA = (Labeled) calendar.lookup(".day-name-cell");
                    Labeled weekdayB = (Labeled) popupContent.lookup(".day-name-cell");
                    assertNotNull(weekdayA, css + " 下 CalendarAnt 应有星期表头");
                    assertEquals(weekdayB.getText(), weekdayA.getText(),
                            css + " 下星期表头文本必须一致（本地化，默认 zh_CN 为「周一」）");

                    stageA.hide();
                    stageB.hide();
                });
            }
        }

        @Test
        @DisplayName("星期表头随 Locale 本地化，且以周一起始（非硬编码英文 Su/Mo）")
        void weekdayHeadersAreLocalized() {
            runOnFxThreadAndWait(() -> {
                VBox calendar = CalendarAnt.create()
                        .value(LocalDate.of(2026, 10, 12))
                        .fullscreen(false)
                        .build();
                Pane root = new Pane(calendar);
                Scene scene = new Scene(root, 420, 480);
                scene.getStylesheets().add(CalendarAntTest.class.getResource(
                        "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
                root.applyCss();
                root.layout();

                Node first = calendar.lookup(".day-name-cell");
                assertNotNull(first, "应有星期表头");
                // JavaFX Calendar 表头用 SHORT 样式（中文即「周一」）；与 Locale 派生的期望值对齐
                String expected = java.time.DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                assertEquals(expected, ((Labeled) first).getText(),
                        "首个星期表头应为本地化的周一（默认 Locale=" + Locale.getDefault() + "）");
                stage.hide();
            });
        }
    }

    @Test
    @DisplayName("controller 更新 value/mode 后刷新内容")
    void controller_refreshesCalendar() {
        VBox calendar = CalendarAnt.create()
                .value(LocalDate.of(2026, 6, 15))
                .fullscreen(false)
                .build();

        CalendarAnt.Controller controller = CalendarAnt.controllerOf(calendar);
        assertEquals(LocalDate.of(2026, 6, 15), controller.getValue());

        controller.setMode(CalendarAnt.Mode.YEAR);
        assertEquals(CalendarAnt.Mode.YEAR, controller.getMode());
        assertNotNull(calendar.lookup("." + JfxStyles.CALENDAR_YEAR_VIEW), "年视图应出现月份按钮网格");
        assertNotNull(calendar.lookup("." + JfxStyles.CALENDAR_MONTH_BTN), "年视图应有月份按钮");

        controller.setMode(CalendarAnt.Mode.MONTH);
        assertNotNull(calendar.lookup(".calendar-grid"), "切回月视图应恢复原生日期网格");

        controller.setValue(LocalDate.of(2026, 7, 1));
        assertEquals(LocalDate.of(2026, 7, 1), controller.getValue());
        assertNotNull(calendar.lookup(".calendar-grid"));
    }

    @Test
    @DisplayName("程序化写值不触发 onSelect（只有用户点日期才回调）")
    void programmaticValueDoesNotFireOnSelect() {
        int[] calls = {0};
        VBox calendar = CalendarAnt.create()
                .value(LocalDate.of(2026, 6, 15))
                .onSelect(d -> calls[0]++)
                .fullscreen(false)
                .build();

        CalendarAnt.Controller controller = CalendarAnt.controllerOf(calendar);
        controller.setSelectedDate(LocalDate.of(2026, 7, 1));
        controller.setValue(LocalDate.of(2026, 8, 1));
        assertEquals(0, calls[0], "Controller 写值属于程序化更新，不应触发 onSelect");
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

    @Nested
    @DisplayName("视图比例契约（月/年两块面板同宽同高）")
    class ViewProportionContract {

        /**
         * 月视图是原生 7 列网格（由 modena 定死 32px 格），年视图是自绘 3×4 栅格
         * （宽 @calendar-month-btn-width、高 @calendar-month-btn-height，行 vgrow 等分）。
         * 二者必须**同宽同高**，否则月↔年切换时整块日历跳尺寸。
         */
        @Test
        @DisplayName("切到年视图后日历宽高均不变")
        void yearViewSharesMonthViewSize() {
            for (String css : new String[]{"theme-light.css", "theme-light-compact.css"}) {
                runOnFxThreadAndWait(() -> {
                    VBox calendar = CalendarAnt.create()
                            .value(LocalDate.of(2026, 10, 11))
                            .fullscreen(false)
                            .build();
                    Pane root = new Pane(calendar);
                    Scene scene = new Scene(root, 460, 560);
                    scene.getStylesheets().add(CalendarAntTest.class.getResource(
                            "/org/openkawu/jfxium/css/" + css).toExternalForm());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                    CalendarAnt.Controller ctrl = CalendarAnt.controllerOf(calendar);

                    ctrl.setMode(CalendarAnt.Mode.MONTH);
                    root.applyCss();
                    root.layout();
                    double monthWidth = calendar.getWidth();
                    double monthHeight = calendar.getHeight();

                    ctrl.setMode(CalendarAnt.Mode.YEAR);
                    root.applyCss();
                    root.layout();
                    double yearWidth = calendar.getWidth();
                    double yearHeight = calendar.getHeight();

                    // 宽高都要断言：只测宽度会让「年视图比月视图矮一截」溜过去
                    assertEquals(monthWidth, yearWidth, 1.0,
                            css + " 下月视图与年视图必须同宽（" + monthWidth + " / " + yearWidth + "）");
                    assertEquals(monthHeight, yearHeight, 1.0,
                            css + " 下月视图与年视图必须同高（" + monthHeight + " / " + yearHeight + "）");
                    stage.hide();
                });
            }
        }
    }
}
