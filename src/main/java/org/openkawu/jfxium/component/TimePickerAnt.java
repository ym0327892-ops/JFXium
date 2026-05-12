package org.openkawu.jfxium.component;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * JFXium 时间选择器组件 - 对标 Ant Design TimePicker
 *
 * 提供时分秒选择功能
 *
 * 使用示例：
 * <pre>{@code
 * // 基础时间选择
 * HBox timePicker = TimePickerAnt.create()
 *     .value(LocalTime.now())
 *     .onChange(time -> System.out.println(time))
 *     .build();
 *
 * // 仅选择时分
 * HBox timePicker = TimePickerAnt.create()
 *     .format("HH:mm")
 *     .build();
 * }</pre>
 */
public class TimePickerAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private LocalTime value = LocalTime.now();
        private String format = "HH:mm:ss";
        private boolean disabled = false;
        private java.util.function.Consumer<LocalTime> onChange = null;

        public Builder value(LocalTime value) {
            this.value = value;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder onChange(java.util.function.Consumer<LocalTime> onChange) {
            this.onChange = onChange;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(8);
            container.getStyleClass().add("time-picker");

            boolean showSeconds = format.contains("ss");
            boolean showMinutes = format.contains("mm");

            Spinner<Integer> hourSpinner = createTimeSpinner(0, 23, value.getHour());
            container.getChildren().add(hourSpinner);

            if (showMinutes) {
                container.getChildren().add(new javafx.scene.control.Label(":") {{
                    setStyle("-fx-font-size: 16px; -fx-text-fill: -color-fg-muted;");
                }});

                Spinner<Integer> minuteSpinner = createTimeSpinner(0, 59, value.getMinute());
                container.getChildren().add(minuteSpinner);
            }

            if (showSeconds) {
                container.getChildren().add(new javafx.scene.control.Label(":") {{
                    setStyle("-fx-font-size: 16px; -fx-text-fill: -color-fg-muted;");
                }});

                Spinner<Integer> secondSpinner = createTimeSpinner(0, 59, value.getSecond());
                container.getChildren().add(secondSpinner);
            }

            return container;
        }

        private Spinner<Integer> createTimeSpinner(int min, int max, int value) {
            Spinner<Integer> spinner = new Spinner<>(min, max, value);
            spinner.setPrefWidth(60);
            spinner.setMinWidth(60);
            spinner.setMaxWidth(60);
            spinner.getStyleClass().add("time-spinner");
            spinner.setDisable(disabled);

            spinner.getEditor().setAlignment(javafx.geometry.Pos.CENTER);
            spinner.getEditor().setStyle(
                "-fx-font-size: 14px; " +
                "-fx-padding: 4px 2px; " +
                "-fx-pref-width: 40px; " +
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent transparent -color-border-muted transparent; " +
                "-fx-border-width: 0 0 1 0;"
            );

            return spinner;
        }
    }
}
