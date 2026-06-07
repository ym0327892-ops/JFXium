package org.openkawu.jfxium.component.composite;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.time.LocalTime;
import java.util.function.Consumer;

/**
 * JFXium 时间选择器组件 - 对标 Ant Design TimePicker。
 *
 * <p><b>定位</b>：时/分/秒选择器，支持 12/24 小时制，常用于日程安排、
 * 提醒设置、营业时间配置等场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>时间格式</b>：支持 HH:mm:ss / HH:mm</li>
 *   <li><b>默认值</b>：defaultValue(LocalTime)</li>
 *   <li><b>回调</b>：onChange(LocalTime)</li>
 *   <li><b>禁用</b>：disabled(true)</li>
 *   <li><b>视觉</b>：编辑器 + 分隔符走 LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * HBox timePicker = TimePickerAnt.create()
 *     .defaultValue(LocalTime.of(9, 30))
 *     .onChange(time -> System.out.println("时间：" + time))
 *     .build();
 * }</pre>
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
        private ObjectProperty<LocalTime> bindProperty = null;

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

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(ObjectProperty<LocalTime> property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(8);
            container.getStyleClass().add(JfxStyles.TIME_PICKER);
            container.setAlignment(Pos.CENTER_LEFT);

            boolean showSeconds = format.contains("ss");
            boolean showMinutes = format.contains("mm");

            Spinner<Integer> hourSpinner = createTimeSpinner(0, 23, value.getHour());
            container.getChildren().add(hourSpinner);

            Spinner<Integer> minuteSpinner = null;
            if (showMinutes) {
                container.getChildren().add(makeSeparator());
                minuteSpinner = createTimeSpinner(0, 59, value.getMinute());
                container.getChildren().add(minuteSpinner);
            }

            Spinner<Integer> secondSpinner = null;
            if (showSeconds) {
                container.getChildren().add(makeSeparator());
                secondSpinner = createTimeSpinner(0, 59, value.getSecond());
                container.getChildren().add(secondSpinner);
            }

            // M19.42 修复：原实现 onChange 字段从未被任何 spinner 触发（死回调），
            // 导致调用方无法拿到用户选中的时间。这里把时/分/秒三个 spinner 的值变化
            // 汇聚成 LocalTime 后回调，未显示的段（分/秒）按 0 计。
            if (onChange != null || bindProperty != null) {
                final Spinner<Integer> h = hourSpinner;
                final Spinner<Integer> m = minuteSpinner;
                final Spinner<Integer> s = secondSpinner;
                Runnable notify = () -> {
                    int hh = h.getValue();
                    int mm = m != null ? m.getValue() : 0;
                    int ss = s != null ? s.getValue() : 0;
                    LocalTime newTime = LocalTime.of(hh, mm, ss);
                    if (onChange != null) {
                        onChange.accept(newTime);
                    }
                    if (bindProperty != null) {
                        bindProperty.set(newTime);
                    }
                };
                hourSpinner.valueProperty().addListener((obs, ov, nv) -> notify.run());
                if (minuteSpinner != null) {
                    minuteSpinner.valueProperty().addListener((obs, ov, nv) -> notify.run());
                }
                if (secondSpinner != null) {
                    secondSpinner.valueProperty().addListener((obs, ov, nv) -> notify.run());
                }

                // 双向绑定：外部 property 变化时同步更新 spinner
                if (bindProperty != null) {
                    bindProperty.addListener((obs, ov, nv) -> {
                        if (nv != null && !nv.equals(ov)) {
                            h.getValueFactory().setValue(nv.getHour());
                            if (m != null) m.getValueFactory().setValue(nv.getMinute());
                            if (s != null) s.getValueFactory().setValue(nv.getSecond());
                        }
                    });
                }
            }

            applyStyles(container);
            return container;
        }

        /** 创建一个":"分隔符 Label，颜色字号由 LESS 控制 */
        private Label makeSeparator() {
            Label sep = new Label(":");
            sep.getStyleClass().add(JfxStyles.TIME_PICKER_SEPARATOR);
            return sep;
        }

        private Spinner<Integer> createTimeSpinner(int min, int max, int value) {
            Spinner<Integer> spinner = new Spinner<>(min, max, value);
            spinner.setPrefWidth(60);
            spinner.setMinWidth(60);
            spinner.setMaxWidth(60);
            spinner.getStyleClass().add(JfxStyles.TIME_PICKER_SPINNER);
            spinner.setDisable(disabled);

            spinner.getEditor().setAlignment(Pos.CENTER);
            // 编辑器视觉样式（字号/padding/边框等）走 LESS
            spinner.getEditor().getStyleClass().add(JfxStyles.TIME_PICKER_EDITOR);

            return spinner;
        }
    }
}
