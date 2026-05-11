package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 提及组件 - 对标 Ant Design Mentions
 *
 * 在文本输入中通过 @ 提及某人或某事
 *
 * 使用示例：
 * <pre>{@code
 * // 基础提及
 * TextArea mentions = MentionsAnt.create()
 *     .placeholder("Type @ to mention someone")
 *     .option("john", "John Doe")
 *     .option("jane", "Jane Smith")
 *     .option("bob", "Bob Wilson")
 *     .onSelect(user -> System.out.println("Mentioned: " + user))
 *     .build();
 * }</pre>
 */
public class MentionsAnt {

    public static class Option {
        private final String value;
        private final String label;

        public Option(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
    }

    public static class Builder {
        private String placeholder = "";
        private List<Option> options = new ArrayList<>();
        private String prefix = "@";
        private Consumer<String> onSelect = null;
        private Consumer<String> onChange = null;
        private int rows = 4;

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder option(String value, String label) {
            this.options.add(new Option(value, label));
            return this;
        }

        public Builder options(List<Option> options) {
            this.options = options;
            return this;
        }

        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder onSelect(Consumer<String> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        public Builder onChange(Consumer<String> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public TextArea build() {
            TextArea textArea = new TextArea();
            textArea.getStyleClass().add("mentions");
            textArea.setPromptText(placeholder);
            textArea.setPrefRowCount(rows);
            textArea.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-padding: 8px 12px;"
            );

            Popup popup = new Popup();
            popup.setAutoHide(true);

            VBox optionsPanel = new VBox(0);
            optionsPanel.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-radius: 8px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);"
            );
            optionsPanel.setPrefWidth(200);
            optionsPanel.setPadding(new Insets(4, 0, 4, 0));

            popup.getContent().add(optionsPanel);

            textArea.textProperty().addListener((obs, oldVal, newVal) -> {
                if (onChange != null) {
                    onChange.accept(newVal);
                }

                // Check if user just typed the prefix
                if (newVal.endsWith(prefix)) {
                    optionsPanel.getChildren().clear();
                    for (Option option : options) {
                        HBox row = new HBox(8);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.setPadding(new Insets(8, 12, 8, 12));
                        row.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");

                        Label label = new Label(option.getLabel());
                        label.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-default;");
                        row.getChildren().add(label);

                        row.setOnMouseEntered(e -> row.setStyle("-fx-cursor: hand; -fx-background-color: -color-bg-subtle;"));
                        row.setOnMouseExited(e -> row.setStyle("-fx-cursor: hand; -fx-background-color: transparent;"));
                        row.setOnMouseClicked(e -> {
                            String currentText = textArea.getText();
                            String newText = currentText.substring(0, currentText.length() - prefix.length())
                                + prefix + option.getValue() + " ";
                            textArea.setText(newText);
                            textArea.positionCaret(newText.length());
                            popup.hide();
                            if (onSelect != null) {
                                onSelect.accept(option.getValue());
                            }
                        });

                        optionsPanel.getChildren().add(row);
                    }

                    if (!options.isEmpty()) {
                        javafx.geometry.Bounds bounds = textArea.localToScreen(textArea.getBoundsInLocal());
                        popup.show(textArea, bounds.getMinX(), bounds.getMaxY() + 4);
                    }
                } else if (!newVal.contains(prefix)) {
                    popup.hide();
                }
            });

            return textArea;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
