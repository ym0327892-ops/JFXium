package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.css.CssClasses;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * JFXium Calendar - 对标 Ant Design Calendar。
 *
 * 重构：calendar 容器 / header / nav 按钮 / day header / day cell / month btn 全部走 LESS。
 *
 * <h2>状态机</h2>
 * 日期 cell 三种状态通过修饰类切换：
 * <ul>
 *   <li>{@code calendar-cell-today} —— 今天，浅色主题底</li>
 *   <li>{@code calendar-cell-selected} —— 选中，深色主题底 + 反白文字</li>
 *   <li>{@code calendar-cell-other-month} —— 非当前月，模糊文字</li>
 * </ul>
 * hover 由 LESS {@code .calendar-cell:hover} 控制，不再用 setOnMouseEntered/Exited 拼字符串。
 */
public class CalendarAnt {

    public enum Mode {
        MONTH, YEAR
    }

    public static class Builder {
        private LocalDate value = LocalDate.now();
        private LocalDate selectedDate = null;
        private Mode mode = Mode.MONTH;
        private Consumer<LocalDate> onSelect = null;
        private Consumer<LocalDate> onPanelChange = null;
        private boolean fullscreen = true;

        public Builder value(LocalDate value) { this.value = value; return this; }
        public Builder selectedDate(LocalDate selectedDate) { this.selectedDate = selectedDate; return this; }
        public Builder mode(Mode mode) { this.mode = mode; return this; }
        public Builder onSelect(Consumer<LocalDate> onSelect) { this.onSelect = onSelect; return this; }
        public Builder onPanelChange(Consumer<LocalDate> onPanelChange) { this.onPanelChange = onPanelChange; return this; }
        public Builder fullscreen(boolean fullscreen) { this.fullscreen = fullscreen; return this; }

        public VBox build() {
            VBox calendar = new VBox(0);
            calendar.getStyleClass().add(CssClasses.CALENDAR);

            if (fullscreen) {
                HBox.setHgrow(calendar, Priority.ALWAYS);
                VBox.setVgrow(calendar, Priority.ALWAYS);
            }

            calendar.getChildren().add(buildHeader());

            if (mode == Mode.MONTH) {
                calendar.getChildren().add(buildMonthView());
            } else {
                calendar.getChildren().add(buildYearView());
            }
            return calendar;
        }

        private HBox buildHeader() {
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER);
            header.getStyleClass().add(CssClasses.CALENDAR_HEADER);
            HBox.setHgrow(header, Priority.ALWAYS);

            Button prevBtn = createNavButton("<");
            prevBtn.setOnAction(e -> {
                value = (mode == Mode.MONTH) ? value.minusMonths(1) : value.minusYears(1);
                if (onPanelChange != null) onPanelChange.accept(value);
            });

            Button nextBtn = createNavButton(">");
            nextBtn.setOnAction(e -> {
                value = (mode == Mode.MONTH) ? value.plusMonths(1) : value.plusYears(1);
                if (onPanelChange != null) onPanelChange.accept(value);
            });

            String headerText = (mode == Mode.MONTH)
                    ? value.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
                    : String.valueOf(value.getYear());
            Label monthYearLabel = new Label(headerText);
            monthYearLabel.getStyleClass().add(CssClasses.CALENDAR_HEADER_LABEL);

            header.getChildren().addAll(prevBtn, monthYearLabel, nextBtn);
            return header;
        }

        /** 导航按钮（上/下一月）：视觉与 hover 由 LESS .calendar-nav-btn 控制 */
        private Button createNavButton(String text) {
            Button btn = new Button(text);
            btn.getStyleClass().add(CssClasses.CALENDAR_NAV_BTN);
            return btn;
        }

        private GridPane buildMonthView() {
            GridPane grid = new GridPane();
            grid.getStyleClass().add(CssClasses.CALENDAR_GRID);
            grid.setHgap(0);
            grid.setVgap(0);

            // 7 列等宽 + 7 行自适应
            for (int i = 0; i < 7; i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(100.0 / 7.0);
                col.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().add(col);
            }
            for (int i = 0; i < 7; i++) {
                RowConstraints row = new RowConstraints();
                row.setVgrow(Priority.ALWAYS);
                row.setMinHeight(40);
                grid.getRowConstraints().add(row);
            }

            // 周日标题行
            String[] dayNames = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (int i = 0; i < 7; i++) {
                Label dayLabel = new Label(dayNames[i]);
                dayLabel.getStyleClass().add(CssClasses.CALENDAR_DAY_HEADER);
                dayLabel.setAlignment(Pos.CENTER);
                dayLabel.setMaxWidth(Double.MAX_VALUE);
                dayLabel.setMaxHeight(Double.MAX_VALUE);
                grid.add(dayLabel, i, 0);
            }

            YearMonth yearMonth = YearMonth.from(value);
            LocalDate firstOfMonth = yearMonth.atDay(1);
            int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
            int daysInMonth = yearMonth.lengthOfMonth();

            int row = 1;
            int col = dayOfWeek;

            // 上个月的尾巴
            YearMonth prevMonth = yearMonth.minusMonths(1);
            int prevDays = prevMonth.lengthOfMonth();
            for (int i = dayOfWeek - 1; i >= 0; i--) {
                int day = prevDays - (dayOfWeek - 1 - i);
                StackPane dayCell = createDayCell(day, false, false, prevMonth.atDay(day), true);
                grid.add(dayCell, i, row);
            }

            // 当月日期
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate currentDate = yearMonth.atDay(day);
                boolean isToday = currentDate.equals(LocalDate.now());
                boolean isSelected = selectedDate != null && currentDate.equals(selectedDate);

                StackPane dayCell = createDayCell(day, isToday, isSelected, currentDate, false);
                dayCell.setMaxWidth(Double.MAX_VALUE);
                dayCell.setMaxHeight(Double.MAX_VALUE);
                grid.add(dayCell, col, row);

                col++;
                if (col > 6) {
                    col = 0;
                    row++;
                }
            }

            // 下个月的开头补齐 6 行
            int nextDay = 1;
            while (col <= 6 && row <= 6) {
                YearMonth nextMonth = yearMonth.plusMonths(1);
                StackPane dayCell = createDayCell(nextDay, false, false, nextMonth.atDay(nextDay), true);
                dayCell.setMaxWidth(Double.MAX_VALUE);
                dayCell.setMaxHeight(Double.MAX_VALUE);
                grid.add(dayCell, col, row);
                col++;
                if (col > 6) {
                    col = 0;
                    row++;
                }
                nextDay++;
            }

            VBox.setVgrow(grid, Priority.ALWAYS);
            return grid;
        }

        private VBox buildYearView() {
            VBox yearView = new VBox(8);
            yearView.getStyleClass().add(CssClasses.CALENDAR_YEAR_VIEW);

            GridPane grid = new GridPane();
            grid.setHgap(8);
            grid.setVgap(8);

            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for (int i = 0; i < 12; i++) {
                int month = i + 1;
                Button monthBtn = new Button(months[i]);
                monthBtn.setPrefWidth(80);
                monthBtn.setPrefHeight(40);
                monthBtn.getStyleClass().add(CssClasses.CALENDAR_MONTH_BTN);

                boolean isCurrentMonth = value.getMonthValue() == month;
                if (isCurrentMonth) {
                    monthBtn.getStyleClass().add(CssClasses.CALENDAR_MONTH_BTN_CURRENT);
                }

                monthBtn.setOnAction(e -> {
                    value = value.withMonth(month);
                    mode = Mode.MONTH;
                    if (onPanelChange != null) onPanelChange.accept(value);
                });
                grid.add(monthBtn, i % 4, i / 4);
            }
            yearView.getChildren().add(grid);
            return yearView;
        }

        /**
         * 创建日期 cell。状态通过 styleClass 切换：
         * - other-month: 非当前月，文字模糊
         * - today: 今天，浅色主题背景
         * - selected: 选中，深色主题背景 + 反白文字
         */
        private StackPane createDayCell(int day, boolean isToday, boolean isSelected, LocalDate date, boolean isOtherMonth) {
            Label label = new Label(String.valueOf(day));
            label.setAlignment(Pos.CENTER);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setMaxHeight(Double.MAX_VALUE);
            label.getStyleClass().add(CssClasses.CALENDAR_DAY_LABEL);

            StackPane cell = new StackPane();
            cell.setMaxWidth(Double.MAX_VALUE);
            cell.setMaxHeight(Double.MAX_VALUE);
            cell.setAlignment(Pos.CENTER);
            cell.getStyleClass().add(CssClasses.CALENDAR_CELL);
            if (isOtherMonth) cell.getStyleClass().add(CssClasses.CALENDAR_CELL_OTHER_MONTH);
            if (isSelected) cell.getStyleClass().add(CssClasses.CALENDAR_CELL_SELECTED);
            else if (isToday) cell.getStyleClass().add(CssClasses.CALENDAR_CELL_TODAY);

            cell.setOnMouseClicked(e -> {
                selectedDate = date;
                if (onSelect != null) onSelect.accept(date);
            });

            cell.getChildren().add(label);
            return cell;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
