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

            // Hour spinner
            Spinner<Integer> hourSpinner = new Spinner<>(0, 23, value.getHour());
            hourSpinner.setPrefWidth(80);
            hourSpinner.setMinWidth(80);
            hourSpinner.getStyleClass().add("time-spinner");
            hourSpinner.setDisable(disabled);
            
            // 设置编辑器样式，确保数字能完整显示
            hourSpinner.getEditor().setAlignment(javafx.geometry.Pos.CENTER);
            hourSpinner.getEditor().setStyle("-fx-font-size: 14px; -fx-padding: 4px 8px;");

            container.getChildren().add(hourSpinner);

            if (showMinutes) {
                container.getChildren().add(new javafx.scene.control.Label(":") {{
                    setStyle("-fx-font-size: 16px; -fx-text-fill: -color-fg-muted;");
                }});

                Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, value.getMinute());
                minuteSpinner.setPrefWidth(80);
                minuteSpinner.setMinWidth(80);
                minuteSpinner.getStyleClass().add("time-spinner");
                minuteSpinner.setDisable(disabled);
                minuteSpinner.getEditor().setAlignment(javafx.geometry.Pos.CENTER);
                minuteSpinner.getEditor().setStyle("-fx-font-size: 14px; -fx-padding: 4px 8px;");
                container.getChildren().add(minuteSpinner);
            }

            if (showSeconds) {
                container.getChildren().add(new javafx.scene.control.Label(":") {{
                    setStyle("-fx-font-size: 16px; -fx-text-fill: -color-fg-muted;");
                }});

                Spinner<Integer> secondSpinner = new Spinner<>(0, 59, value.getSecond());
                secondSpinner.setPrefWidth(80);
                secondSpinner.setMinWidth(80);
                secondSpinner.getStyleClass().add("time-spinner");
                secondSpinner.setDisable(disabled);
                secondSpinner.getEditor().setAlignment(javafx.geometry.Pos.CENTER);
                secondSpinner.getEditor().setStyle("-fx-font-size: 14px; -fx-padding: 4px 8px;");
                container.getChildren().add(secondSpinner);
            }

            if (onChange != null) {
                javafx.beans.value.ChangeListener<Number> listener = (obs, oldVal, newVal) -> {
                    int h = hourSpinner.getValue();
                    int m = showMinutes ? ((Spinner<Integer>) container.getChildren().get(2)).getValue() : 0;
                    int s = showSeconds ? ((Spinner<Integer>) container.getChildren().get(showMinutes ? 4 : 2)).getValue() : 0;
                    onChange.accept(LocalTime.of(h, m, s));
                };

                hourSpinner.valueProperty().addListener(listener);
                if (showMinutes) {
                    ((Spinner<Integer>) container.getChildren().get(2)).valueProperty().addListener(listener);
                }
                if (showSeconds) {
                    int secIndex = showMinutes ? 4 : 2;
                    ((Spinner<Integer>) container.getChildren().get(secIndex)).valueProperty().addListener(listener);
                }
            }

            return container;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
