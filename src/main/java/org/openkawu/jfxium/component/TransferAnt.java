package org.openkawu.jfxium.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Transfer Component
 * Inspired by Ant Design Transfer
 * Double column transfer choice box.
 */
public class TransferAnt<T> {

    public static class Builder<T> {
        private List<T> dataSource = new ArrayList<>();
        private List<T> targetKeys = new ArrayList<>();
        private List<T> selectedSourceKeys = new ArrayList<>();
        private List<T> selectedTargetKeys = new ArrayList<>();
        private String titles = "Source;Target";
        private java.util.function.Function<T, String> render = Object::toString;
        private Consumer<List<T>> onChange = null;
        private Consumer<List<T>> onSelectChange = null;
        private boolean showSearch = false;
        private boolean disabled = false;

        public Builder<T> dataSource(List<T> dataSource) {
            this.dataSource = new ArrayList<>(dataSource);
            return this;
        }

        public Builder<T> targetKeys(List<T> targetKeys) {
            this.targetKeys = new ArrayList<>(targetKeys);
            return this;
        }

        public Builder<T> titles(String sourceTitle, String targetTitle) {
            this.titles = sourceTitle + ";" + targetTitle;
            return this;
        }

        public Builder<T> render(java.util.function.Function<T, String> render) {
            this.render = render;
            return this;
        }

        public Builder<T> onChange(Consumer<List<T>> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder<T> onSelectChange(Consumer<List<T>> onSelectChange) {
            this.onSelectChange = onSelectChange;
            return this;
        }

        public Builder<T> showSearch(boolean showSearch) {
            this.showSearch = showSearch;
            return this;
        }

        public Builder<T> showSearch() {
            return showSearch(true);
        }

        public Builder<T> disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public HBox build() {
            HBox transfer = new HBox(12);
            transfer.getStyleClass().add("transfer");
            transfer.setAlignment(Pos.CENTER);

            // Source list
            List<T> sourceItems = new ArrayList<>();
            for (T item : dataSource) {
                if (!targetKeys.contains(item)) {
                    sourceItems.add(item);
                }
            }

            VBox sourceBox = buildListBox("Source", sourceItems, true);
            HBox.setHgrow(sourceBox, Priority.ALWAYS);

            // Middle buttons
            VBox middleBox = buildMiddleButtons();

            // Target list
            VBox targetBox = buildListBox("Target", targetKeys, false);
            HBox.setHgrow(targetBox, Priority.ALWAYS);

            transfer.getChildren().addAll(sourceBox, middleBox, targetBox);

            return transfer;
        }

        private VBox buildListBox(String title, List<T> items, boolean isSource) {
            VBox box = new VBox(0);
            box.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;"
            );
            box.setPrefWidth(200);
            box.setPrefHeight(300);

            // Header
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setStyle("-fx-padding: 8px 12px; -fx-border-color: transparent transparent -color-border-muted transparent; -fx-border-width: 0 0 1px 0;");

            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px; -fx-font-weight: 500;");

            Label countLabel = new Label(items.size() + " items");
            countLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");

            header.getChildren().addAll(titleLabel, countLabel);
            box.getChildren().add(header);

            // Search (optional)
            if (showSearch) {
                javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();
                searchField.setPromptText("Search");
                searchField.setStyle("-fx-padding: 4px 8px; -fx-font-size: 12px;");
                VBox searchBox = new VBox(searchField);
                searchBox.setStyle("-fx-padding: 8px 12px;");
                box.getChildren().add(searchBox);
            }

            // List
            ObservableList<String> displayItems = FXCollections.observableArrayList();
            for (T item : items) {
                displayItems.add(render.apply(item));
            }

            ListView<String> listView = new ListView<>(displayItems);
            listView.getStyleClass().add("transfer-list");
            listView.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            VBox.setVgrow(listView, Priority.ALWAYS);

            listView.setOnMouseClicked(e -> {
                if (isSource) {
                    selectedSourceKeys.clear();
                    for (String selected : listView.getSelectionModel().getSelectedItems()) {
                        for (T item : items) {
                            if (render.apply(item).equals(selected)) {
                                selectedSourceKeys.add(item);
                                break;
                            }
                        }
                    }
                } else {
                    selectedTargetKeys.clear();
                    for (String selected : listView.getSelectionModel().getSelectedItems()) {
                        for (T item : items) {
                            if (render.apply(item).equals(selected)) {
                                selectedTargetKeys.add(item);
                                break;
                            }
                        }
                    }
                }

                if (onSelectChange != null) {
                    List<T> allSelected = new ArrayList<>();
                    allSelected.addAll(selectedSourceKeys);
                    allSelected.addAll(selectedTargetKeys);
                    onSelectChange.accept(allSelected);
                }
            });

            box.getChildren().add(listView);

            return box;
        }

        private VBox buildMiddleButtons() {
            VBox box = new VBox(8);
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-padding: 8px;");

            // To right button
            javafx.scene.control.Button toRightBtn = createTransferButton(">");
            toRightBtn.setOnAction(e -> {
                if (!selectedSourceKeys.isEmpty()) {
                    targetKeys.addAll(selectedSourceKeys);
                    selectedSourceKeys.clear();
                    if (onChange != null) {
                        onChange.accept(new ArrayList<>(targetKeys));
                    }
                }
            });

            // To left button
            javafx.scene.control.Button toLeftBtn = createTransferButton("<");
            toLeftBtn.setOnAction(e -> {
                if (!selectedTargetKeys.isEmpty()) {
                    targetKeys.removeAll(selectedTargetKeys);
                    selectedTargetKeys.clear();
                    if (onChange != null) {
                        onChange.accept(new ArrayList<>(targetKeys));
                    }
                }
            });

            box.getChildren().addAll(toRightBtn, toLeftBtn);
            return box;
        }

        private javafx.scene.control.Button createTransferButton(String text) {
            javafx.scene.control.Button btn = new javafx.scene.control.Button(text);
            btn.setStyle(
                "-fx-background-color: -color-bg-subtle;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 4px;" +
                "-fx-background-radius: 4px;" +
                "-fx-min-width: 32px;" +
                "-fx-min-height: 32px;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            );
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: -color-accent-emphasis;" +
                "-fx-border-color: -color-accent-emphasis;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 4px;" +
                "-fx-background-radius: 4px;" +
                "-fx-min-width: 32px;" +
                "-fx-min-height: 32px;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: -color-bg-subtle;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 4px;" +
                "-fx-background-radius: 4px;" +
                "-fx-min-width: 32px;" +
                "-fx-min-height: 32px;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            ));
            return btn;
        }
    }

    public static <T> Builder<T> create() {
        return new Builder<>();
    }
}
