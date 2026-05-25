package org.openkawu.jfxium.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.css.CssClasses;
import org.openkawu.jfxium.core.i18n.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JFXium Transfer - 对标 Ant Design Transfer。
 *
 * 重构：列表容器/header/搜索框/方向按钮全部走 LESS（{@code transfer-*}），
 * 中间方向按钮 hover 状态由 LESS 伪类控制，不再用 setOnMouseEntered/Exited。
 */
public class TransferAnt<T> {

    public static class Builder<T> {
        private List<T> dataSource = new ArrayList<>();
        private List<T> targetKeys = new ArrayList<>();
        private List<T> selectedSourceKeys = new ArrayList<>();
        private List<T> selectedTargetKeys = new ArrayList<>();
        // null = 用 i18n 默认值；非 null 走调用方指定（titles(src, tgt) 设置时为 "src;tgt" 形式）
        private String titles = null;
        private Function<T, String> render = Object::toString;
        private Consumer<List<T>> onChange = null;
        private Consumer<List<T>> onSelectChange = null;
        private boolean showSearch = false;
        private boolean disabled = false;

        public Builder<T> dataSource(List<T> dataSource) { this.dataSource = new ArrayList<>(dataSource); return this; }
        public Builder<T> targetKeys(List<T> targetKeys) { this.targetKeys = new ArrayList<>(targetKeys); return this; }
        public Builder<T> titles(String sourceTitle, String targetTitle) { this.titles = sourceTitle + ";" + targetTitle; return this; }
        public Builder<T> render(Function<T, String> render) { this.render = render; return this; }
        public Builder<T> onChange(Consumer<List<T>> onChange) { this.onChange = onChange; return this; }
        public Builder<T> onSelectChange(Consumer<List<T>> onSelectChange) { this.onSelectChange = onSelectChange; return this; }
        public Builder<T> showSearch(boolean showSearch) { this.showSearch = showSearch; return this; }
        public Builder<T> showSearch() { return showSearch(true); }
        public Builder<T> disabled(boolean disabled) { this.disabled = disabled; return this; }

        public HBox build() {
            HBox transfer = new HBox(12);
            transfer.getStyleClass().add(CssClasses.TRANSFER);
            transfer.setAlignment(Pos.CENTER);

            String[] titleArr = titles != null ? titles.split(";", 2) : new String[0];
            String sourceTitle = titleArr.length > 0 ? titleArr[0] : Messages.get("transfer.source");
            String targetTitle = titleArr.length > 1 ? titleArr[1] : Messages.get("transfer.target");

            // 计算源列表项（dataSource - targetKeys）
            List<T> sourceItems = new ArrayList<>();
            for (T item : dataSource) {
                if (!targetKeys.contains(item)) {
                    sourceItems.add(item);
                }
            }

            VBox sourceBox = buildListBox(sourceTitle, sourceItems, true);
            HBox.setHgrow(sourceBox, Priority.ALWAYS);

            VBox middleBox = buildMiddleButtons();

            VBox targetBox = buildListBox(targetTitle, targetKeys, false);
            HBox.setHgrow(targetBox, Priority.ALWAYS);

            transfer.getChildren().addAll(sourceBox, middleBox, targetBox);
            return transfer;
        }

        private VBox buildListBox(String title, List<T> items, boolean isSource) {
            VBox box = new VBox(0);
            box.getStyleClass().add(CssClasses.TRANSFER_LIST);
            box.setPrefWidth(200);
            box.setPrefHeight(300);

            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);
            header.getStyleClass().add(CssClasses.TRANSFER_LIST_HEADER);

            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add(CssClasses.TRANSFER_LIST_TITLE);
            Label countLabel = new Label(Messages.get("transfer.items", items.size()));
            countLabel.getStyleClass().add(CssClasses.TRANSFER_LIST_COUNT);

            header.getChildren().addAll(titleLabel, countLabel);
            box.getChildren().add(header);

            if (showSearch) {
                TextField searchField = new TextField();
                searchField.setPromptText(Messages.get("transfer.search"));
                searchField.getStyleClass().add(CssClasses.TRANSFER_LIST_SEARCH);
                VBox searchBox = new VBox(searchField);
                searchBox.getStyleClass().add(CssClasses.TRANSFER_LIST_SEARCH_WRAPPER);
                box.getChildren().add(searchBox);
            }

            ObservableList<String> displayItems = FXCollections.observableArrayList();
            for (T item : items) {
                displayItems.add(render.apply(item));
            }

            ListView<String> listView = new ListView<>(displayItems);
            listView.getStyleClass().add(CssClasses.TRANSFER_LIST_VIEW);
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            VBox.setVgrow(listView, Priority.ALWAYS);

            listView.setOnMouseClicked(e -> {
                List<T> targetSelection = isSource ? selectedSourceKeys : selectedTargetKeys;
                targetSelection.clear();
                for (String selected : listView.getSelectionModel().getSelectedItems()) {
                    for (T item : items) {
                        if (render.apply(item).equals(selected)) {
                            targetSelection.add(item);
                            break;
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
            box.setPadding(new javafx.geometry.Insets(8));

            Button toRightBtn = createTransferButton(">");
            toRightBtn.setOnAction(e -> {
                if (!selectedSourceKeys.isEmpty()) {
                    targetKeys.addAll(selectedSourceKeys);
                    selectedSourceKeys.clear();
                    if (onChange != null) onChange.accept(new ArrayList<>(targetKeys));
                }
            });

            Button toLeftBtn = createTransferButton("<");
            toLeftBtn.setOnAction(e -> {
                if (!selectedTargetKeys.isEmpty()) {
                    targetKeys.removeAll(selectedTargetKeys);
                    selectedTargetKeys.clear();
                    if (onChange != null) onChange.accept(new ArrayList<>(targetKeys));
                }
            });

            box.getChildren().addAll(toRightBtn, toLeftBtn);
            return box;
        }

        /** Transfer 方向按钮：视觉与 hover 由 LESS 控制 */
        private Button createTransferButton(String text) {
            Button btn = new Button(text);
            btn.getStyleClass().add(CssClasses.TRANSFER_ARROW_BTN);
            return btn;
        }
    }

    public static <T> Builder<T> create() {
        return new Builder<>();
    }
}
