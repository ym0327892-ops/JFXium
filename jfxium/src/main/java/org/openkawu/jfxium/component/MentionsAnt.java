package org.openkawu.jfxium.component;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 提及组件 - 对标 Ant Design Mentions。
 *
 * 重构：TextArea 视觉与 popup options 全部 styleClass 化（{@link CssClasses#MENTIONS_AREA}
 * + {@link CssClasses#POPUP_MENU} + {@link CssClasses#POPUP_MENU_ITEM}），hover 由 LESS 控制。
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

        public Builder placeholder(String placeholder) { this.placeholder = placeholder; return this; }
        public Builder option(String value, String label) { this.options.add(new Option(value, label)); return this; }
        public Builder options(List<Option> options) { this.options = options; return this; }
        public Builder prefix(String prefix) { this.prefix = prefix; return this; }
        public Builder onSelect(Consumer<String> onSelect) { this.onSelect = onSelect; return this; }
        public Builder onChange(Consumer<String> onChange) { this.onChange = onChange; return this; }
        public Builder rows(int rows) { this.rows = rows; return this; }

        public TextArea build() {
            TextArea textArea = new TextArea();
            textArea.getStyleClass().addAll(CssClasses.MENTIONS, CssClasses.MENTIONS_AREA);
            textArea.setPromptText(placeholder);
            textArea.setPrefRowCount(rows);

            Popup popup = new Popup();
            popup.setAutoHide(true);

            VBox optionsPanel = new VBox(0);
            optionsPanel.getStyleClass().add(CssClasses.POPUP_MENU);
            optionsPanel.setPrefWidth(200);
            popup.getContent().add(optionsPanel);

            textArea.textProperty().addListener((obs, oldVal, newVal) -> {
                if (onChange != null) onChange.accept(newVal);

                if (newVal.endsWith(prefix)) {
                    optionsPanel.getChildren().clear();
                    for (Option option : options) {
                        HBox row = new HBox(8);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.getStyleClass().add(CssClasses.POPUP_MENU_ITEM);
                        Label label = new Label(option.getLabel());
                        row.getChildren().add(label);
                        // hover 由 LESS 控制
                        row.setOnMouseClicked(e -> {
                            String currentText = textArea.getText();
                            String newText = currentText.substring(0, currentText.length() - prefix.length())
                                    + prefix + option.getValue() + " ";
                            textArea.setText(newText);
                            textArea.positionCaret(newText.length());
                            popup.hide();
                            if (onSelect != null) onSelect.accept(option.getValue());
                        });
                        optionsPanel.getChildren().add(row);
                    }

                    if (!options.isEmpty()) {
                        Bounds bounds = textArea.localToScreen(textArea.getBoundsInLocal());
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
