package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * JFXium Calendar Component
 * Inspired by Ant Design Calendar
 * A container for displaying data in calendar form.
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

        public Builder value(LocalDate value) {
            this.value = value;
            return this;
        }

        public Builder selectedDate(LocalDate selectedDate) {
            this.selectedDate = selectedDate;
            return this;
        }

        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder onSelect(Consumer<LocalDate> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        public Builder onPanelChange(Consumer<LocalDate> onPanelChange) {
            this.onPanelChange = onPanelChange;
            return this;
        }

        public Builder fullscreen(boolean fullscreen) {
            this.fullscreen = fullscreen;
            return this;
        }

        public VBox build() {
            VBox calendar = new VBox(0);
            calendar.getStyleClass().add("calendar");
            calendar.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;"
            );

            if (fullscreen) {
                HBox.setHgrow(calendar, Priority.ALWAYS);
                VBox.setVgrow(calendar, Priority.ALWAYS);
            }

            // Header
            HBox header = buildHeader();
            calendar.getChildren().add(header);

            // Calendar grid
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
            header.setPadding(new javafx.geometry.Insets(12, 16, 12, 16));
            header.setStyle("-fx-border-color: transparent transparent -color-border-muted transparent; -fx-border-width: 0 0 1px 0;");

            HBox.setHgrow(header, Priority.ALWAYS);

            // Previous month button
            javafx.scene.control.Button prevBtn = createNavButton("<");
            prevBtn.setOnAction(e -> {
                if (mode == Mode.MONTH) {
                    value = value.minusMonths(1);
                } else {
                    value = value.minusYears(1);
                }
                if (onPanelChange != null) {
                    onPanelChange.accept(value);
                }
            });

            // Next month button
            javafx.scene.control.Button nextBtn = createNavButton(">");
            nextBtn.setOnAction(e -> {
                if (mode == Mode.MONTH) {
                    value = value.plusMonths(1);
                } else {
                    value = value.plusYears(1);
                }
                if (onPanelChange != null) {
                    onPanelChange.accept(value);
                }
            });

            // Month/Year label
            String headerText;
            if (mode == Mode.MONTH) {
                headerText = value.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()));
            } else {
                headerText = String.valueOf(value.getYear());
            }
            Label monthYearLabel = new Label(headerText);
            monthYearLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default;");

            header.getChildren().addAll(prevBtn, monthYearLabel, nextBtn);

            return header;
        }

        private javafx.scene.control.Button createNavButton(String text) {
            javafx.scene.control.Button btn = new javafx.scene.control.Button(text);
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 4px 12px;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 4px;"
            );
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: -color-bg-subtle;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 4px 12px;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 4px;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 4px 12px;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 4px;"
            ));
            return btn;
        }

        private GridPane buildMonthView() {
            GridPane grid = new GridPane();
            grid.getStyleClass().add("calendar-grid");
            grid.setHgap(0);
            grid.setVgap(0);
            grid.setPadding(new javafx.geometry.Insets(8));

            // 设置列约束 - 7 列等宽，自适应
            for (int i = 0; i < 7; i++) {
                ColumnConstraints colConstraints = new ColumnConstraints();
                colConstraints.setPercentWidth(100.0 / 7.0);
                colConstraints.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().add(colConstraints);
            }

            // 设置行约束 - 自适应高度
            for (int i = 0; i < 7; i++) {
                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setVgrow(Priority.ALWAYS);
                rowConstraints.setMinHeight(40);
                grid.getRowConstraints().add(rowConstraints);
            }

            // Day headers
            String[] dayNames = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (int i = 0; i < 7; i++) {
                Label dayLabel = new Label(dayNames[i]);
                dayLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px; -fx-font-weight: 500; -fx-padding: 8px 4px; -fx-alignment: center;");
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

            // Previous month days
            YearMonth prevMonth = yearMonth.minusMonths(1);
            int prevDays = prevMonth.lengthOfMonth();
            for (int i = dayOfWeek - 1; i >= 0; i--) {
                int day = prevDays - (dayOfWeek - 1 - i);
                StackPane dayCell = createDayCell(day, false, false, prevMonth.atDay(day));
                dayCell.getStyleClass().add("calendar-cell-other-month");
                grid.add(dayCell, i, row);
            }

            // Current month days
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate currentDate = yearMonth.atDay(day);
                boolean isToday = currentDate.equals(LocalDate.now());
                boolean isSelected = selectedDate != null && currentDate.equals(selectedDate);

                StackPane dayCell = createDayCell(day, isToday, isSelected, currentDate);
                dayCell.setMaxWidth(Double.MAX_VALUE);
                dayCell.setMaxHeight(Double.MAX_VALUE);
                grid.add(dayCell, col, row);

                col++;
                if (col > 6) {
                    col = 0;
                    row++;
                }
            }

            // Next month days
            int nextDay = 1;
            while (col <= 6 && row <= 6) {
                YearMonth nextMonth = yearMonth.plusMonths(1);
                StackPane dayCell = createDayCell(nextDay, false, false, nextMonth.atDay(nextDay));
                dayCell.getStyleClass().add("calendar-cell-other-month");
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
            yearView.setStyle("-fx-padding: 16px;");

            GridPane grid = new GridPane();
            grid.setHgap(8);
            grid.setVgap(8);

            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for (int i = 0; i < 12; i++) {
                int month = i + 1;
                javafx.scene.control.Button monthBtn = new javafx.scene.control.Button(months[i]);
                monthBtn.setPrefWidth(80);
                monthBtn.setPrefHeight(40);

                boolean isCurrentMonth = value.getMonthValue() == month;
                String baseStyle = "-fx-background-radius: 4px; -fx-font-size: 14px; -fx-cursor: hand;";

                if (isCurrentMonth) {
                    monthBtn.setStyle(baseStyle + "-fx-background-color: -color-accent-emphasis; -fx-text-fill: white;");
                } else {
                    monthBtn.setStyle(baseStyle + "-fx-background-color: transparent; -fx-text-fill: -color-fg-default;");
                }

                monthBtn.setOnMouseEntered(e -> {
                    if (!isCurrentMonth) {
                        monthBtn.setStyle(baseStyle + "-fx-background-color: -color-bg-subtle; -fx-text-fill: -color-fg-default;");
                    }
                });
                monthBtn.setOnMouseExited(e -> {
                    if (!isCurrentMonth) {
                        monthBtn.setStyle(baseStyle + "-fx-background-color: transparent; -fx-text-fill: -color-fg-default;");
                    }
                });

                monthBtn.setOnAction(e -> {
                    value = value.withMonth(month);
                    mode = Mode.MONTH;
                    if (onPanelChange != null) {
                        onPanelChange.accept(value);
                    }
                });

                grid.add(monthBtn, i % 4, i / 4);
            }

            yearView.getChildren().add(grid);
            return yearView;
        }

        private Label createDayLabel(int day, boolean isOtherMonth) {
            Label label = new Label(String.valueOf(day));
            label.setPrefWidth(48);
            label.setPrefHeight(40);
            label.setAlignment(Pos.CENTER);
            String color = isOtherMonth ? "-color-fg-subtle" : "-color-fg-default";
            label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-padding: 4px;");
            return label;
        }

        private StackPane createDayCell(int day, boolean isToday, boolean isSelected, LocalDate date) {
            Label label = new Label(String.valueOf(day));
            label.setAlignment(Pos.CENTER);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setMaxHeight(Double.MAX_VALUE);
            label.setStyle("-fx-font-size: 14px;");

            String bgColor = "transparent";
            String textColor = "-color-fg-default";

            if (isSelected) {
                bgColor = "-color-accent-emphasis";
                textColor = "white";
            } else if (isToday) {
                bgColor = "-color-accent-subtle";
                textColor = "-color-accent-emphasis";
            }

            StackPane cell = new StackPane();
            cell.setMaxWidth(Double.MAX_VALUE);
            cell.setMaxHeight(Double.MAX_VALUE);
            cell.setAlignment(Pos.CENTER);
            cell.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 4px;" +
                "-fx-cursor: hand;"
            );

            final String finalBgColor = bgColor;
            cell.setOnMouseEntered(e -> {
                if (!isSelected) {
                    cell.setStyle(
                        "-fx-background-color: -color-bg-subtle;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-cursor: hand;"
                    );
                }
            });
            cell.setOnMouseExited(e -> {
                if (!isSelected) {
                    cell.setStyle(
                        "-fx-background-color: " + finalBgColor + ";" +
                        "-fx-background-radius: 4px;" +
                        "-fx-cursor: hand;"
                    );
                }
            });
            cell.setOnMouseClicked(e -> {
                selectedDate = date;
                if (onSelect != null) {
                    onSelect.accept(date);
                }
            });

            cell.getChildren().add(label);
            return cell;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
