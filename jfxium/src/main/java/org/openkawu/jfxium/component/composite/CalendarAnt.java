package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * JFXium 日历组件 - 对标 Ant Design Calendar（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：完整日历面板，支持月 / 年视图切换、日期选择，
 * 与 DatePickerAnt（带输入框的日期选择）严格区分。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>视图模式</b>：MONTH（月视图）/ YEAR（年视图）</li>
 *   <li><b>日期选择</b>：onSelect 回调</li>
 *   <li><b>面板切换</b>：onPanelChange（月/年切换时触发）</li>
 *   <li><b>日期状态</b>：今日 / 选中 / 非本月 三态走 LESS 修饰类</li>
 *   <li>视觉样式走 LESS（{@code .calendar-*} 系列）</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>仪表盘日历视图</li>
 *   <li>日程管理页</li>
 *   <li>日期选择器（嵌入式，非弹出）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node calendar = CalendarAnt.create()
 *     .value(LocalDate.now())
 *     .mode(CalendarAnt.Mode.MONTH)
 *     .onSelect(date -> System.out.println("选中：" + date))
 *     .build();
 * }</pre>
 */
public class CalendarAnt {
    private static final String CONTROLLER_KEY = CalendarAnt.class.getName() + ".controller";

    public enum Mode {
        MONTH, YEAR
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private LocalDate value = LocalDate.now();
        private LocalDate selectedDate = null;
        private Mode mode = Mode.MONTH;
        private Consumer<LocalDate> onSelect = null;
        private Consumer<LocalDate> onPanelChange = null;
        private boolean fullscreen = true;
        private VBox root;

        public Builder value(LocalDate value) { this.value = value; return this; }
        public Builder selectedDate(LocalDate selectedDate) { this.selectedDate = selectedDate; return this; }
        public Builder mode(Mode mode) { this.mode = mode; return this; }
        public Builder onSelect(Consumer<LocalDate> onSelect) { this.onSelect = onSelect; return this; }
        public Builder onPanelChange(Consumer<LocalDate> onPanelChange) { this.onPanelChange = onPanelChange; return this; }
        public Builder fullscreen(boolean fullscreen) { this.fullscreen = fullscreen; return this; }

        public VBox build() {
            VBox calendar = new VBox(0);
            root = calendar;
            calendar.getStyleClass().add(JfxStyles.CALENDAR);

            if (fullscreen) {
                HBox.setHgrow(calendar, Priority.ALWAYS);
                VBox.setVgrow(calendar, Priority.ALWAYS);
            }

            rebuild();
            calendar.getProperties().put(CONTROLLER_KEY, new Controller(this));
            applyStyles(calendar);
            return calendar;
        }

        private void rebuild() {
            if (root == null) return;
            root.getChildren().setAll(buildHeader(), buildBody());
        }

        private Node buildBody() {
            return mode == Mode.MONTH ? buildMonthView() : buildYearView();
        }

        private HBox buildHeader() {
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER);
            header.getStyleClass().add(JfxStyles.CALENDAR_HEADER);
            HBox.setHgrow(header, Priority.ALWAYS);

            Button prevBtn = createNavButton("<");
            prevBtn.setOnAction(e -> {
                value = (mode == Mode.MONTH) ? value.minusMonths(1) : value.minusYears(1);
                rebuild();
                if (onPanelChange != null) onPanelChange.accept(value);
            });

            Button nextBtn = createNavButton(">");
            nextBtn.setOnAction(e -> {
                value = (mode == Mode.MONTH) ? value.plusMonths(1) : value.plusYears(1);
                rebuild();
                if (onPanelChange != null) onPanelChange.accept(value);
            });

            String headerText = (mode == Mode.MONTH)
                    ? value.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
                    : String.valueOf(value.getYear());
            Label monthYearLabel = new Label(headerText);
            monthYearLabel.getStyleClass().add(JfxStyles.CALENDAR_HEADER_LABEL);

            header.getChildren().addAll(prevBtn, monthYearLabel, nextBtn);
            return header;
        }

        /** 导航按钮（上/下一月）：视觉与 hover 由 LESS .calendar-nav-btn 控制 */
        private Button createNavButton(String text) {
            Button btn = new Button(text);
            btn.getStyleClass().add(JfxStyles.CALENDAR_NAV_BTN);
            return btn;
        }

        private GridPane buildMonthView() {
            GridPane grid = new GridPane();
            grid.getStyleClass().add(JfxStyles.CALENDAR_GRID);
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
                dayLabel.getStyleClass().add(JfxStyles.CALENDAR_DAY_HEADER);
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
            yearView.getStyleClass().add(JfxStyles.CALENDAR_YEAR_VIEW);

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
                monthBtn.getStyleClass().add(JfxStyles.CALENDAR_MONTH_BTN);

                boolean isCurrentMonth = value.getMonthValue() == month;
                if (isCurrentMonth) {
                    monthBtn.getStyleClass().add(JfxStyles.CALENDAR_MONTH_BTN_CURRENT);
                }

                monthBtn.setOnAction(e -> {
                    value = value.withMonth(month);
                    mode = Mode.MONTH;
                    rebuild();
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
            label.getStyleClass().add(JfxStyles.CALENDAR_DAY_LABEL);

            StackPane cell = new StackPane();
            cell.setMaxWidth(Double.MAX_VALUE);
            cell.setMaxHeight(Double.MAX_VALUE);
            cell.setAlignment(Pos.CENTER);
            cell.getStyleClass().add(JfxStyles.CALENDAR_CELL);
            if (isOtherMonth) cell.getStyleClass().add(JfxStyles.CALENDAR_CELL_OTHER_MONTH);
            if (isSelected) cell.getStyleClass().add(JfxStyles.CALENDAR_CELL_SELECTED);
            else if (isToday) cell.getStyleClass().add(JfxStyles.CALENDAR_CELL_TODAY);

            cell.setOnMouseClicked(e -> {
                selectedDate = date;
                rebuild();
                if (onSelect != null) onSelect.accept(date);
            });

            cell.getChildren().add(label);
            return cell;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Controller controllerOf(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("CalendarAnt.controllerOf(node) 的 node 不能为 null");
        }
        Object controller = node.getProperties().get(CONTROLLER_KEY);
        if (controller instanceof Controller calendarController) {
            return calendarController;
        }
        throw new IllegalArgumentException("node 不是 CalendarAnt.build() 返回的日历组件");
    }

    public static class Controller {
        private final Builder builder;

        private Controller(Builder builder) {
            this.builder = builder;
        }

        public LocalDate getValue() {
            return builder.value;
        }

        public void setValue(LocalDate value) {
            builder.value = value != null ? value : LocalDate.now();
            builder.rebuild();
        }

        public LocalDate getSelectedDate() {
            return builder.selectedDate;
        }

        public void setSelectedDate(LocalDate selectedDate) {
            builder.selectedDate = selectedDate;
            builder.rebuild();
        }

        public Mode getMode() {
            return builder.mode;
        }

        public void setMode(Mode mode) {
            builder.mode = mode != null ? mode : Mode.MONTH;
            builder.rebuild();
        }
    }
}
