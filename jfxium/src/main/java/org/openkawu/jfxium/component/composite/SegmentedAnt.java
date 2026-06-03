package org.openkawu.jfxium.component.composite;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Segmented - 对标 Ant Design Segmented。
 *
 * 重构：容器/item 选中态/hover/label 颜色全部走 LESS（{@link CssClasses#SEGMENTED} 系列），
 * 不再用 setOnMouseEntered/Exited 拼字符串。
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

    public static class Builder {
        private List<Option> options = new ArrayList<>();
        private String selectedValue = null;
        private Size size = Size.DEFAULT;
        private boolean disabled = false;
        private boolean block = false;
        private Consumer<String> onChange = null;

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

        public HBox build() {
            HBox segmented = new HBox(2);
            segmented.setAlignment(Pos.CENTER);
            segmented.getStyleClass().add(CssClasses.SEGMENTED);
            if (disabled) segmented.getStyleClass().add(CssClasses.SEGMENTED_DISABLED);
            if (size == Size.SMALL) segmented.getStyleClass().add(CssClasses.SEGMENTED_SMALL);
            else if (size == Size.LARGE) segmented.getStyleClass().add(CssClasses.SEGMENTED_LARGE);
            if (block) segmented.setMaxWidth(Double.MAX_VALUE);

            // padding 仍由 size 决定（结构性 inset）
            double padding = size == Size.SMALL ? 4 : size == Size.LARGE ? 12 : 8;

            for (Option option : options) {
                boolean isSelected = selectedValue != null && selectedValue.equals(option.getValue());

                StackPane optionPane = new StackPane();
                optionPane.setAlignment(Pos.CENTER);
                optionPane.getStyleClass().add(CssClasses.SEGMENTED_ITEM);
                if (isSelected) {
                    optionPane.getStyleClass().add(CssClasses.SEGMENTED_ITEM_SELECTED);
                }

                HBox content = new HBox(4);
                content.setAlignment(Pos.CENTER);
                content.setPadding(new Insets(padding, padding + 4, padding, padding + 4));
                if (option.getIcon() != null) {
                    content.getChildren().add(option.getIcon());
                }
                Label label = new Label(option.getLabel());
                label.getStyleClass().add(CssClasses.SEGMENTED_ITEM_LABEL);
                content.getChildren().add(label);

                optionPane.getChildren().add(content);

                if (!disabled) {
                    optionPane.setOnMouseClicked(e -> {
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
