package org.openkawu.jfxium.component;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * JFXium Form Component
 * Inspired by Ant Design Form
 * A form component with data validation and layout capabilities.
 */
public class FormAnt {

    public enum Layout {
        HORIZONTAL, VERTICAL, INLINE
    }

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public static class FormItem {
        String label;
        Node control;
        boolean required;
        String helpText;
        String validateStatus; // "success", "warning", "error", "validating"
        int labelCol;
        int wrapperCol;

        public FormItem(String label, Node control) {
            this.label = label;
            this.control = control;
            this.required = false;
            this.helpText = "";
            this.validateStatus = "";
            this.labelCol = 6;
            this.wrapperCol = 18;
        }
    }

    public static class Builder {
        private List<FormItem> items = new ArrayList<>();
        private Layout layout = Layout.HORIZONTAL;
        private Size size = Size.DEFAULT;
        private boolean colon = true;
        private String labelAlign = "right";
        private int labelCol = 6;
        private int wrapperCol = 18;
        private Node footer = null;

        public Builder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder colon(boolean colon) {
            this.colon = colon;
            return this;
        }

        public Builder labelAlign(String align) {
            this.labelAlign = align;
            return this;
        }

        public Builder labelCol(int labelCol) {
            this.labelCol = labelCol;
            return this;
        }

        public Builder wrapperCol(int wrapperCol) {
            this.wrapperCol = wrapperCol;
            return this;
        }

        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        public Builder item(String label, Node control) {
            FormItem item = new FormItem(label, control);
            item.labelCol = this.labelCol;
            item.wrapperCol = this.wrapperCol;
            items.add(item);
            return this;
        }

        public Builder item(String label, Node control, boolean required) {
            FormItem item = new FormItem(label, control);
            item.required = required;
            item.labelCol = this.labelCol;
            item.wrapperCol = this.wrapperCol;
            items.add(item);
            return this;
        }

        public Builder item(String label, Node control, boolean required, String helpText) {
            FormItem item = new FormItem(label, control);
            item.required = required;
            item.helpText = helpText;
            item.labelCol = this.labelCol;
            item.wrapperCol = this.wrapperCol;
            items.add(item);
            return this;
        }

        public VBox build() {
            VBox form = new VBox(0);
            form.getStyleClass().add("form");

            if (layout == Layout.HORIZONTAL) {
                form.getChildren().add(buildHorizontalForm());
            } else if (layout == Layout.VERTICAL) {
                form.getChildren().add(buildVerticalForm());
            } else {
                form.getChildren().add(buildInlineForm());
            }

            // Footer
            if (footer != null) {
                HBox footerBox = new HBox(footer);
                footerBox.setAlignment(Pos.CENTER_RIGHT);
                footerBox.setStyle("-fx-padding: 16px 0 0 0;");
                form.getChildren().add(footerBox);
            }

            return form;
        }

        private GridPane buildHorizontalForm() {
            GridPane grid = new GridPane();
            grid.getStyleClass().add("form-horizontal");
            grid.setHgap(16);
            grid.setVgap(getVerticalGap());
            grid.setAlignment(Pos.TOP_LEFT);

            for (int i = 0; i < items.size(); i++) {
                FormItem item = items.get(i);
                int row = i;

                // Label
                Label label = createLabel(item);
                GridPane.setHalignment(label, labelAlign.equals("right") ? HPos.RIGHT : HPos.LEFT);
                grid.add(label, 0, row);

                // Control wrapper
                VBox wrapper = createWrapper(item);
                grid.add(wrapper, 1, row);
            }

            // Column constraints - 使用百分比约束，对标 Ant Design labelCol/wrapperCol
            // Ant Design 默认 labelCol=8, wrapperCol=16，即 33.3% : 66.7%
            // JFXium 默认 labelCol=6, wrapperCol=18，即 25% : 75%
            double totalCol = labelCol + wrapperCol;
            double labelPercent = (labelCol / totalCol) * 100;
            double wrapperPercent = (wrapperCol / totalCol) * 100;

            ColumnConstraints labelConstraint = new ColumnConstraints();
            labelConstraint.setPercentWidth(labelPercent);
            ColumnConstraints controlConstraint = new ColumnConstraints();
            controlConstraint.setPercentWidth(wrapperPercent);
            controlConstraint.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(labelConstraint, controlConstraint);

            return grid;
        }

        private VBox buildVerticalForm() {
            VBox container = new VBox(getVerticalGap());
            container.getStyleClass().add("form-vertical");

            for (FormItem item : items) {
                VBox itemBox = new VBox(4);

                // Label
                Label label = createLabel(item);
                label.setStyle(label.getStyle() + "-fx-padding: 0 0 4px 0;");
                itemBox.getChildren().add(label);

                // Control wrapper
                VBox wrapper = createWrapper(item);
                itemBox.getChildren().add(wrapper);

                container.getChildren().add(itemBox);
            }

            return container;
        }

        private HBox buildInlineForm() {
            HBox container = new HBox(16);
            container.getStyleClass().add("form-inline");
            container.setAlignment(Pos.CENTER_LEFT);

            for (FormItem item : items) {
                VBox itemBox = new VBox(4);

                // Label
                if (!item.label.isEmpty()) {
                    Label label = createLabel(item);
                    label.setStyle(label.getStyle() + "-fx-padding: 0 0 4px 0;");
                    itemBox.getChildren().add(label);
                }

                // Control
                itemBox.getChildren().add(item.control);

                container.getChildren().add(itemBox);
            }

            return container;
        }

        private Label createLabel(FormItem item) {
            String labelText = item.label;
            if (colon && !labelText.isEmpty()) {
                labelText += ":";
            }

            Label label = new Label(labelText);
            label.getStyleClass().add("form-label");
            String style = "-fx-text-fill: -color-fg-default; -fx-font-size: " + getFontSize() + ";";

            if (item.required) {
                style += " -fx-font-weight: 600;";
            }

            label.setStyle(style);
            return label;
        }

        private VBox createWrapper(FormItem item) {
            VBox wrapper = new VBox(4);
            wrapper.getStyleClass().add("form-item-wrapper");

            // Control
            wrapper.getChildren().add(item.control);

            // Help text
            if (!item.helpText.isEmpty()) {
                Label helpLabel = new Label(item.helpText);
                helpLabel.getStyleClass().add("form-help-text");
                String helpColor = "-color-fg-muted";
                if (item.validateStatus.equals("error")) {
                    helpColor = "-color-danger-emphasis";
                } else if (item.validateStatus.equals("warning")) {
                    helpColor = "-color-warning-emphasis";
                } else if (item.validateStatus.equals("success")) {
                    helpColor = "-color-success-emphasis";
                }
                helpLabel.setStyle("-fx-text-fill: " + helpColor + "; -fx-font-size: 12px;");
                wrapper.getChildren().add(helpLabel);
            }

            // Required indicator
            if (item.required) {
                // Add red asterisk before label (handled in createLabel)
            }

            return wrapper;
        }

        private String getFontSize() {
            return switch (size) {
                case SMALL -> "12px";
                case LARGE -> "16px";
                default -> "14px";
            };
        }

        private int getVerticalGap() {
            return switch (size) {
                case SMALL -> 12;
                case LARGE -> 24;
                default -> 16;
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
