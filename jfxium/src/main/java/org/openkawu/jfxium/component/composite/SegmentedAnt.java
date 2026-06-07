package org.openkawu.jfxium.component.composite;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 分段控制器组件 - 对标 Ant Design Segmented。
 *
 * <p><b>定位</b>：轻量级切换控件，类似 Tabs 但更紧凑，常用于视图切换、
 * 筛选条件切换、时间范围切换等场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>选项</b>：option(value, label) / option(value, label, icon)</li>
 *   <li><b>尺寸</b>：SMALL / DEFAULT / LARGE</li>
 *   <li><b>回调</b>：onChange(value) 切换时触发</li>
 *   <li><b>默认值</b>：defaultValue(value)</li>
 *   <li><b>禁用</b>：option 级别 disabled</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * HBox segmented = SegmentedAnt.create()
 *     .option("day", "日")
 *     .option("week", "周")
 *     .option("month", "月")
 *     .defaultValue("week")
 *     .onChange(val -> refreshView(val))
 *     .build();
 * }</pre>
 */
public class SegmentedAnt {

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public static class Option {
        private final String value;
        private final String label;
        private final Node icon;

        public Option(String value, String label) { this(value, label, null); }
        public Option(String value, String label, Node icon) {
            this.value = value;
            this.label = label;
            this.icon = icon;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
        public Node getIcon() { return icon; }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private List<Option> options = new ArrayList<>();
        private String selectedValue = null;
        private Size size = Size.DEFAULT;
        private boolean disabled = false;
        private boolean block = false;
        private Consumer<String> onChange = null;
        private StringProperty bindProperty = null;

        public Builder option(String value, String label) {
            this.options.add(new Option(value, label));
            return this;
        }

        public Builder option(String value, String label, Node icon) {
            this.options.add(new Option(value, label, icon));
            return this;
        }

        public Builder options(List<Option> options) { this.options = options; return this; }
        public Builder selected(String value) { this.selectedValue = value; return this; }
        public Builder size(Size size) { this.size = size; return this; }
        public Builder disabled(boolean disabled) { this.disabled = disabled; return this; }
        public Builder disabled() { return disabled(true); }
        public Builder block(boolean block) { this.block = block; return this; }
        public Builder block() { return block(true); }
        public Builder onChange(Consumer<String> onChange) { this.onChange = onChange; return this; }

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(StringProperty property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            HBox segmented = new HBox(2);
            segmented.setAlignment(Pos.CENTER);
            segmented.getStyleClass().add(JfxStyles.SEGMENTED);
            if (disabled) segmented.getStyleClass().add(JfxStyles.SEGMENTED_DISABLED);
            if (size == Size.SMALL) segmented.getStyleClass().add(JfxStyles.SEGMENTED_SMALL);
            else if (size == Size.LARGE) segmented.getStyleClass().add(JfxStyles.SEGMENTED_LARGE);
            if (block) segmented.setMaxWidth(Double.MAX_VALUE);

            // padding 仍由 size 决定（结构性 inset）
            double padding = size == Size.SMALL ? 4 : size == Size.LARGE ? 12 : 8;

            for (Option option : options) {
                boolean isSelected = selectedValue != null && selectedValue.equals(option.getValue());

                StackPane optionPane = new StackPane();
                optionPane.setAlignment(Pos.CENTER);
                optionPane.getStyleClass().add(JfxStyles.SEGMENTED_ITEM);
                if (isSelected) {
                    optionPane.getStyleClass().add(JfxStyles.SEGMENTED_ITEM_SELECTED);
                }

                HBox content = new HBox(4);
                content.setAlignment(Pos.CENTER);
                content.setPadding(new Insets(padding, padding + 4, padding, padding + 4));
                if (option.getIcon() != null) {
                    content.getChildren().add(option.getIcon());
                }
                Label label = new Label(option.getLabel());
                label.getStyleClass().add(JfxStyles.SEGMENTED_ITEM_LABEL);
                content.getChildren().add(label);

                optionPane.getChildren().add(content);

                if (!disabled) {
                    optionPane.setOnMouseClicked(e -> {
                        selectedValue = option.getValue();
                        if (bindProperty != null) {
                            bindProperty.set(option.getValue());
                        }
                        if (onChange != null) onChange.accept(option.getValue());
                    });
                }
                if (block) {
                    HBox.setHgrow(optionPane, javafx.scene.layout.Priority.ALWAYS);
                }
                segmented.getChildren().add(optionPane);
            }
            return segmented;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
