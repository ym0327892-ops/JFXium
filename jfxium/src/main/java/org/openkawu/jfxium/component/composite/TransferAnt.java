package org.openkawu.jfxium.component.composite;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JFXium 穿梭框组件 - 对标 Ant Design Transfer（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：双列列表控件，用于在两个列表之间移动数据项，
 * 比下拉多选更直观。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>数据源 + 已选目标列表</li>
 *   <li>搜索过滤（showSearch）</li>
 *   <li>自定义渲染（render 函数）</li>
 *   <li>自定义标题（titles）</li>
 *   <li>变化回调（onChange）+ 选中变化回调（onSelectChange）</li>
 *   <li>视觉样式走 LESS（{@code .transfer-*} 系列）</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>权限分配（可选权限 vs 已分配权限）</li>
 *   <li>成员分配（可选成员 vs 已加入成员）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node transfer = TransferAnt.<String>create()
 *     .dataSource(List.of("用户 A", "用户 B", "用户 C", "用户 D"))
 *     .targetKeys(List.of("用户 A"))
 *     .showSearch(true)
 *     .titles("可选", "已选")
 *     .onChange(keys -> System.out.println("目标：" + keys))
 *     .build();
 * }</pre>
 */
public class TransferAnt<T> {

    public static class Builder<T> extends AbstractStyleBuilder<Builder<T>> {
        private List<T> dataSource = new ArrayList<>();
        private List<T> targetKeys = new ArrayList<>();
        private List<T> selectedSourceKeys = new ArrayList<>();
        private List<T> selectedTargetKeys = new ArrayList<>();
        // null = 用 i18n 默认值；非 null 走调用方指定（titles(src, tgt) 设置时为 "src;tgt" 形式）
        private String titles = null;
        private Function<T, String> render = item -> item == null ? "" : String.valueOf(item);
        private Consumer<List<T>> onChange = null;
        private Consumer<List<T>> onSelectChange = null;
        private boolean showSearch = false;
        // disabled 复用父类 AbstractStyleBuilder.disable 字段（P2-S7.4 抽取）
        private ObjectProperty<List<T>> bindProperty = null;
        private HBox transferRoot;

        public Builder<T> dataSource(List<T> dataSource) {
            this.dataSource = dataSource != null ? new ArrayList<>(dataSource) : new ArrayList<>();
            return this;
        }
        public Builder<T> targetKeys(List<T> targetKeys) {
            this.targetKeys = targetKeys != null ? new ArrayList<>(targetKeys) : new ArrayList<>();
            return this;
        }
        public Builder<T> titles(String sourceTitle, String targetTitle) {
            this.titles = TextUtils.safeText(sourceTitle) + ";" + TextUtils.safeText(targetTitle);
            return this;
        }
        public Builder<T> render(Function<T, String> render) {
            this.render = render != null ? render : (item -> item == null ? "" : String.valueOf(item));
            return this;
        }
        public Builder<T> onChange(Consumer<List<T>> onChange) { this.onChange = onChange; return this; }
        public Builder<T> onSelectChange(Consumer<List<T>> onSelectChange) { this.onSelectChange = onSelectChange; return this; }
        public Builder<T> showSearch(boolean showSearch) { this.showSearch = showSearch; return this; }
        public Builder<T> showSearch() { return showSearch(true); }

        /** 双向绑定：控件值（目标列表 targetKeys）↔ Property 值实时同步。 */
        public Builder<T> bindValue(ObjectProperty<List<T>> property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            transferRoot = new HBox();
            transferRoot.getStyleClass().add(JfxStyles.TRANSFER);
            transferRoot.setAlignment(Pos.CENTER);
            refreshTransferView();
            applyStyles(transferRoot);
            return transferRoot;
        }

        private void refreshTransferView() {
            if (transferRoot == null) {
                return;
            }
            transferRoot.getChildren().clear();

            String[] titleArr = titles != null && !titles.isBlank() ? titles.split(";", 2) : new String[0];
            String sourceTitle = titleArr.length > 0 && !titleArr[0].isBlank()
                    ? titleArr[0]
                    : Messages.get("transfer.source");
            String targetTitle = titleArr.length > 1 && !titleArr[1].isBlank()
                    ? titleArr[1]
                    : Messages.get("transfer.target");

            List<T> sourceItems = new ArrayList<>();
            for (T item : dataSource) {
                if (!targetKeys.contains(item)) {
                    sourceItems.add(item);
                }
            }

            selectedSourceKeys.retainAll(sourceItems);
            selectedTargetKeys.retainAll(targetKeys);

            VBox sourceBox = buildListBox(sourceTitle, sourceItems, true);
            HBox.setHgrow(sourceBox, Priority.ALWAYS);

            VBox middleBox = buildMiddleButtons();

            VBox targetBox = buildListBox(targetTitle, targetKeys, false);
            HBox.setHgrow(targetBox, Priority.ALWAYS);

            transferRoot.getChildren().addAll(sourceBox, middleBox, targetBox);
            transferRoot.setDisable(Boolean.TRUE.equals(disable));
        }

        private VBox buildListBox(String title, List<T> items, boolean isSource) {
            VBox box = new VBox(0);
            box.getStyleClass().add(JfxStyles.TRANSFER_LIST);
            box.setPrefWidth(200);
            box.setPrefHeight(300);

            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);
            header.getStyleClass().add(JfxStyles.TRANSFER_LIST_HEADER);

            Label titleLabel = new Label(effectiveTitle(title, isSource));
            titleLabel.getStyleClass().add(JfxStyles.TRANSFER_LIST_TITLE);
            Label countLabel = new Label(Messages.get("transfer.items", items.size()));
            countLabel.getStyleClass().add(JfxStyles.TRANSFER_LIST_COUNT);

            header.getChildren().addAll(titleLabel, countLabel);
            box.getChildren().add(header);

            ObservableList<T> displayItems = FXCollections.observableArrayList(items);

            if (showSearch) {
                TextField searchField = new TextField();
                searchField.setPromptText(Messages.get("transfer.search"));
                searchField.getStyleClass().add(JfxStyles.TRANSFER_LIST_SEARCH);
                searchField.textProperty().addListener((obs, oldVal, newVal) ->
                        displayItems.setAll(filterItems(items, newVal)));
                VBox searchBox = new VBox(searchField);
                searchBox.getStyleClass().add(JfxStyles.TRANSFER_LIST_SEARCH_WRAPPER);
                box.getChildren().add(searchBox);
            }

            ListView<T> listView = new ListView<>(displayItems);
            listView.getStyleClass().add(JfxStyles.TRANSFER_LIST_VIEW);
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listView.setCellFactory(ignored -> new ListCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : renderText(item));
                }
            });
            VBox.setVgrow(listView, Priority.ALWAYS);

            listView.setOnMouseClicked(e -> {
                List<T> targetSelection = isSource ? selectedSourceKeys : selectedTargetKeys;
                targetSelection.clear();
                targetSelection.addAll(listView.getSelectionModel().getSelectedItems());
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
            VBox box = new VBox();
            box.setAlignment(Pos.CENTER);
            box.getStyleClass().add(JfxStyles.TRANSFER_MIDDLE);

            Button toRightBtn = createTransferButton(">");
            toRightBtn.setOnAction(e -> {
                if (!selectedSourceKeys.isEmpty()) {
                    targetKeys.addAll(selectedSourceKeys);
                    selectedSourceKeys.clear();
                    if (bindProperty != null) bindProperty.set(new ArrayList<>(targetKeys));
                    if (onChange != null) onChange.accept(new ArrayList<>(targetKeys));
                    refreshTransferView();
                }
            });
            toRightBtn.setDisable(Boolean.TRUE.equals(disable));

            Button toLeftBtn = createTransferButton("<");
            toLeftBtn.setOnAction(e -> {
                if (!selectedTargetKeys.isEmpty()) {
                    targetKeys.removeAll(selectedTargetKeys);
                    selectedTargetKeys.clear();
                    if (bindProperty != null) bindProperty.set(new ArrayList<>(targetKeys));
                    if (onChange != null) onChange.accept(new ArrayList<>(targetKeys));
                    refreshTransferView();
                }
            });
            toLeftBtn.setDisable(Boolean.TRUE.equals(disable));

            box.getChildren().addAll(toRightBtn, toLeftBtn);
            return box;
        }

        private List<T> filterItems(List<T> items, String keyword) {
            if (keyword == null || keyword.isBlank()) {
                return new ArrayList<>(items);
            }
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            List<T> filtered = new ArrayList<>();
            for (T item : items) {
                if (renderText(item).toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
                    filtered.add(item);
                }
            }
            return filtered;
        }

        /** Transfer 方向按钮：视觉与 hover 由 LESS 控制 */
        private Button createTransferButton(String text) {
            Button btn = new Button(text);
            btn.getStyleClass().add(JfxStyles.TRANSFER_ARROW_BTN);
            return btn;
        }

        private String effectiveTitle(String title, boolean isSource) {
            if (title != null && !title.isBlank()) {
                return title;
            }
            return isSource ? Messages.get("transfer.source") : Messages.get("transfer.target");
        }

        private String renderText(T item) {
            String text = render != null ? render.apply(item) : null;
            return TextUtils.safeText(text);
        }

        // safeText 统一改用 TextUtils.safeText,见 P0-23。
    }

    public static <T> Builder<T> create() {
        return new Builder<>();
    }
}
