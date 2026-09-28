package org.openkawu.jfxium.component.control;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.layout.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 回归测试：DatePickerAnt 弹窗的「今日」标记。
 *
 * <p>JavaFX 给今天的单元格挂的是 <b>class {@code today}</b>（DatePickerContent
 * {@code dayCell.getStyleClass().add("today")}），不是 {@code :today} 伪类。
 * 早期 JFXium 写成 {@code .day-cell:today}，该规则永不匹配 → 今日无任何标记。</p>
 */
@DisplayName("DatePickerAnt 今日标记")
class DatePickerTodayMarkerTest extends JfxTestBase {

    private static final Color ACCENT = Color.web("#1677ff");

    @Test
    @DisplayName("今日格只描 accent 边：不动背景、不改文字色")
    void todayCell_borderOnly() {
        AtomicReference<String> failed = new AtomicReference<>(null);
        runOnFxThreadAndWait(() -> {
            // 复刻 JavaFX DatePickerContent 的真实结构：
            // .date-picker-popup > .calendar-grid > .day-cell.today
            StackPane grid = new StackPane();
            grid.getStyleClass().add("calendar-grid");
            javafx.scene.control.DateCell today = new javafx.scene.control.DateCell();
            today.getStyleClass().addAll("day-cell", "today");
            today.setText("28");
            javafx.scene.control.DateCell normal = new javafx.scene.control.DateCell();
            normal.getStyleClass().add("day-cell");
            normal.setText("15");
            grid.getChildren().addAll(today, normal);

            StackPane popup = new StackPane(grid);
            popup.getStyleClass().add("date-picker-popup");

            Scene scene = new Scene(popup, 300, 120);
            scene.getStylesheets().add(DatePickerAnt.class.getResource(
                    "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
            popup.applyCss();
            popup.layout();

            Paint border = today.getBorder() == null || today.getBorder().getStrokes().isEmpty()
                    ? null : today.getBorder().getStrokes().get(0).getTopStroke();
            Paint normalBorder = normal.getBorder() == null || normal.getBorder().getStrokes().isEmpty()
                    ? null : normal.getBorder().getStrokes().get(0).getTopStroke();
            Paint todayBg = today.getBackground() == null || today.getBackground().getFills().isEmpty()
                    ? null : today.getBackground().getFills().get(0).getFill();
            Paint normalBg = normal.getBackground() == null || normal.getBackground().getFills().isEmpty()
                    ? null : normal.getBackground().getFills().get(0).getFill();
            String info = "today.border=" + border + " normal.border=" + normalBorder
                    + " today.bg=" + todayBg + " today.textFill=" + today.getTextFill()
                    + " normal.textFill=" + normal.getTextFill();

            if (border == null) {
                failed.set("今日格无描边（.today 规则未生效）：" + info);
            } else if (!ACCENT.equals(border)) {
                failed.set("今日格描边非 accent(" + ACCENT + ")，实为 " + border + "：" + info);
            } else if (normalBorder != null && normalBorder.equals(border)) {
                failed.set("普通格与今日格描边相同，今日无区分：" + info);
            } else if (!java.util.Objects.equals(todayBg, normalBg)) {
                failed.set("今日格背景不应与普通格不同（.today 只描边）：" + info);
            } else if (!java.util.Objects.equals(today.getTextFill(), normal.getTextFill())) {
                failed.set("今日格文字色不应被改动，应同普通格：" + info);
            }
        });
        if (failed.get() != null) throw new AssertionError(failed.get());
    }

    /**
     * 今日 + 选中：两者特异性相同（(0,3,0)），若顺序不当，后写的 {@code .today} 会把
     * 白字覆盖成蓝字 → accent 底 + accent 字，对比度为 1:1（不可见）。
     */
    @Test
    @DisplayName(".day-cell.today:selected 保持 accent 底 + 白字")
    void todaySelected_keepsReadableContrast() {
        AtomicReference<String> failed = new AtomicReference<>(null);
        runOnFxThreadAndWait(() -> {
            StackPane grid = new StackPane();
            javafx.scene.control.DateCell todaySelected = new javafx.scene.control.DateCell();
            todaySelected.getStyleClass().addAll("day-cell", "today", "selected");
            todaySelected.setText("28");
            javafx.scene.control.DateCell plainSelected = new javafx.scene.control.DateCell();
            plainSelected.getStyleClass().addAll("day-cell", "selected");
            plainSelected.setText("15");
            grid.getChildren().addAll(todaySelected, plainSelected);
            StackPane popup = new StackPane(grid);
            popup.getStyleClass().add("date-picker-popup");

            Scene scene = new Scene(popup, 300, 120);
            scene.getStylesheets().add(DatePickerAnt.class.getResource(
                    "/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
            popup.applyCss();
            popup.layout();

            Paint todayText = todaySelected.getTextFill();
            Paint plainText = plainSelected.getTextFill();
            String info = "todaySelected.textFill=" + todayText
                    + " plainSelected.textFill=" + plainText;
            // 今日选中与普通选中应为同一可读配色（accent 底 -> 白字）
            if (!Color.WHITE.equals(todayText)) {
                failed.set("今日选中文字色应为白（accent 底上可读），实为 " + todayText + "：" + info);
            } else if (plainText != null && !plainText.equals(todayText)) {
                failed.set("今日选中与普通选中文字色不一致：" + info);
            }
        });
        if (failed.get() != null) throw new AssertionError(failed.get());
    }
}
