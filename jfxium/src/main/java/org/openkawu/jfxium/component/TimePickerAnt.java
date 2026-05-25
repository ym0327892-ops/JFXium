package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.time.LocalTime;
import java.util.function.Consumer;

/**
 * JFXium 时间选择器组件 - 对标 Ant Design TimePicker。
 *
 * <h2>修复说明</h2>
 * 原实现 5 处 inline {@code setStyle}：
 * <ul>
 *   <li>2 个分隔符 Label（":"）使用匿名子类 + 实例初始化块拼字号/颜色</li>
 *   <li>spinner 编辑器拼字号/padding/背景/底部边框（实现 Material 风格）</li>
 * </ul>
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>分隔符 styleClass 化（{@link CssClasses#TIME_PICKER_SEPARATOR}），
 *       使用普通 {@code new Label(":")} 替代匿名子类 + 实例初始化块的反模式</li>
 *   <li>spinner 编辑器样式搬到 LESS 的 {@code .jfx-time-picker-editor}</li>
 *   <li>接入 {@link AbstractStyleBuilder}</li>
 * </ul>
 */
public class TimePickerAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private LocalTime value = LocalTime.now();
        private String format = "HH:mm:ss";
        private boolean disabled = false;
        private Consumer<LocalTime> onChange = null;

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

        public Builder onChange(Consumer<LocalTime> onChange) {
            this.onChange = onChange;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(8);
            container.getStyleClass().add(CssClasses.TIME_PICKER);
            container.setAlignment(Pos.CENTER_LEFT);

            boolean showSeconds = format.contains("ss");
            boolean showMinutes = format.contains("mm");

            Spinner<Integer> hourSpinner = createTimeSpinner(0, 23, value.getHour());
            container.getChildren().add(hourSpinner);

            if (showMinutes) {
                container.getChildren().add(makeSeparator());
                Spinner<Integer> minuteSpinner = createTimeSpinner(0, 59, value.getMinute());
                container.getChildren().add(minuteSpinner);
            }

            if (showSeconds) {
                container.getChildren().add(makeSeparator());
                Spinner<Integer> secondSpinner = createTimeSpinner(0, 59, value.getSecond());
                container.getChildren().add(secondSpinner);
            }

            applyStyles(container);
            return container;
        }

        /** 创建一个":"分隔符 Label，颜色字号由 LESS 控制 */
        private Label makeSeparator() {
            Label sep = new Label(":");
            sep.getStyleClass().add(CssClasses.TIME_PICKER_SEPARATOR);
            return sep;
        }

        private Spinner<Integer> createTimeSpinner(int min, int max, int value) {
            Spinner<Integer> spinner = new Spinner<>(min, max, value);
            spinner.setPrefWidth(60);
            spinner.setMinWidth(60);
            spinner.setMaxWidth(60);
            spinner.getStyleClass().add(CssClasses.TIME_PICKER_SPINNER);
            spinner.setDisable(disabled);

            spinner.getEditor().setAlignment(Pos.CENTER);
            // 编辑器视觉样式（字号/padding/边框等）走 LESS
            spinner.getEditor().getStyleClass().add(CssClasses.TIME_PICKER_EDITOR);

            return spinner;
        }
    }
}
