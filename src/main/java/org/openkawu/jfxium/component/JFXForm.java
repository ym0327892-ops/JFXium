package org.openkawu.jfxium.component;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * JFXium Form Component
 * Inspired by Ant Design Form
 *
 * Usage:
 * <pre>{@code
 * VBox form = JFXForm.create()
 *     .item("Username", JFXInput.create().placeholder("Enter username").build())
 *     .item("Password", JFXInput.create().password().placeholder("Enter password").build())
 *     .item("Email", JFXInput.create().placeholder("Enter email").build())
 *     .layout(FormLayout.VERTICAL)
 *     .onSubmit(data -> System.out.println(data))
 *     .build();
 * }</pre>
 */
public class JFXForm {

    public enum Layout {
        VERTICAL,      // 标签在上方
        HORIZONTAL,    // 标签在左侧
        INLINE         // 行内排列
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private final List<FormItem> items = new ArrayList<>();
        private Layout layout = Layout.VERTICAL;
        private int labelWidth = 120;
        private String submitText = "Submit";
        private Consumer<Map<String, Object>> onSubmit;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

        private Builder() {}

        /**
         * 添加表单项
         */
        public Builder item(String label, Node control) {
            items.add(new FormItem(label, control, false));
            return this;
        }

        /**
         * 添加必填表单项
         */
        public Builder itemRequired(String label, Node control) {
            items.add(new FormItem(label, control, true));
            return this;
        }

        /**
         * 添加带提示的表单项
         */
        public Builder item(String label, Node control, String helpText) {
            items.add(new FormItem(label, control, false, helpText));
            return this;
        }

        /**
         * 设置布局方式
         */
        public Builder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        /**
         * 设置标签宽度（仅 HORIZONTAL 布局有效）
         */
        public Builder labelWidth(int width) {
            this.labelWidth = width;
            return this;
        }

        /**
         * 设置提交按钮文本
         */
        public Builder submitText(String text) {
            this.submitText = text;
            return this;
        }

        /**
         * 设置提交回调
         */
        public Builder onSubmit(Consumer<Map<String, Object>> handler) {
            this.onSubmit = handler;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public Builder styleClass(String styleClass) {
            this.extraStyleClasses.add(styleClass);
            return this;
        }

        public VBox build() {
            VBox form = new VBox();
            form.setSpacing(16);
            form.setPadding(new Insets(16));
            form.getStyleClass().add("jfx-form");

            // Add form items
            for (FormItem item : items) {
                Node itemNode = createFormItem(item);
                form.getChildren().add(itemNode);
            }

            // Add submit button if onSubmit is set
            if (onSubmit != null) {
                HBox buttonBox = new HBox();
                buttonBox.setAlignment(Pos.CENTER_LEFT);

                if (layout == Layout.HORIZONTAL) {
                    Region spacer = new Region();
                    spacer.setPrefWidth(labelWidth);
                    buttonBox.getChildren().add(spacer);
                }

                javafx.scene.control.Button submitBtn = JFXButton.create(submitText)
                    .type(JFXButton.Type.PRIMARY)
                    .onClick(e -> {
                        Map<String, Object> data = collectFormData();
                        onSubmit.accept(data);
                    })
                    .build();

                buttonBox.getChildren().add(submitBtn);
                form.getChildren().add(buttonBox);
            }

            // Extra classes
            form.getStyleClass().addAll(extraStyleClasses);

            if (!style.isEmpty()) {
                form.setStyle(style);
            }

            return form;
        }

        private Node createFormItem(FormItem item) {
            switch (layout) {
                case HORIZONTAL:
                    return createHorizontalItem(item);
                case INLINE:
                    return createInlineItem(item);
                case VERTICAL:
                default:
                    return createVerticalItem(item);
            }
        }

        private Node createVerticalItem(FormItem item) {
            VBox container = new VBox(4);

            // Label
            Label label = new Label(item.label + (item.required ? " *" : ""));
            label.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: rgba(0, 0, 0, 0.88);");
            container.getChildren().add(label);

            // Control
            container.getChildren().add(item.control);

            // Help text
            if (item.helpText != null && !item.helpText.isEmpty()) {
                Label helpLabel = new Label(item.helpText);
                helpLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(0, 0, 0, 0.45);");
                container.getChildren().add(helpLabel);
            }

            return container;
        }

        private Node createHorizontalItem(FormItem item) {
            GridPane grid = new GridPane();
            grid.setHgap(8);
            grid.setVgap(4);

            // Label
            Label label = new Label(item.label + (item.required ? " *" : ""));
            label.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: rgba(0, 0, 0, 0.88);");
            label.setPrefWidth(labelWidth);
            label.setAlignment(Pos.CENTER_RIGHT);
            GridPane.setHalignment(label, HPos.RIGHT);

            grid.add(label, 0, 0);

            // Control
            grid.add(item.control, 1, 0);

            // Help text
            if (item.helpText != null && !item.helpText.isEmpty()) {
                Label helpLabel = new Label(item.helpText);
                helpLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(0, 0, 0, 0.45);");
                grid.add(helpLabel, 1, 1);
            }

            return grid;
        }

        private Node createInlineItem(FormItem item) {
            HBox container = new HBox(8);
            container.setAlignment(Pos.CENTER_LEFT);

            // Label
            if (!item.label.isEmpty()) {
                Label label = new Label(item.label + (item.required ? " *" : ""));
                label.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: rgba(0, 0, 0, 0.88);");
                container.getChildren().add(label);
            }

            // Control
            container.getChildren().add(item.control);

            return container;
        }

        private Map<String, Object> collectFormData() {
            Map<String, Object> data = new LinkedHashMap<>();
            for (FormItem item : items) {
                // Try to get value from common control types
                Object value = extractValue(item.control);
                data.put(item.label, value);
            }
            return data;
        }

        private Object extractValue(Node control) {
            if (control instanceof javafx.scene.control.TextInputControl) {
                return ((javafx.scene.control.TextInputControl) control).getText();
            } else if (control instanceof javafx.scene.control.ComboBox) {
                return ((javafx.scene.control.ComboBox<?>) control).getValue();
            } else if (control instanceof javafx.scene.control.CheckBox) {
                return ((javafx.scene.control.CheckBox) control).isSelected();
            }
            return null;
        }
    }

    /**
     * 表单项内部类
     */
    private static class FormItem {
        final String label;
        final Node control;
        final boolean required;
        final String helpText;

        FormItem(String label, Node control, boolean required) {
            this(label, control, required, null);
        }

        FormItem(String label, Node control, boolean required, String helpText) {
            this.label = label;
            this.control = control;
            this.required = required;
            this.helpText = helpText;
        }
    }
}