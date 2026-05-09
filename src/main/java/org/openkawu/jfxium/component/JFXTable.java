package org.openkawu.jfxium.component;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * JFXium Table Component
 * Inspired by Ant Design Table
 *
 * Usage:
 * <pre>{@code
 * TableView<Person> table = JFXTable.<Person>create()
 *     .column("Name", Person::getName)
 *     .column("Age", Person::getAge)
 *     .column("Action", person -> JFXButton.create("Edit").build())
 *     .data(personList)
 *     .striped(true)
 *     .bordered(true)
 *     .selectable(true)
 *     .build();
 * }</pre>
 */
public class JFXTable<T> {

    public static <T> Builder<T> create() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private final List<TableColumn<T, ?>> columns = new ArrayList<>();
        private ObservableList<T> data = FXCollections.observableArrayList();
        private boolean striped = false;
        private boolean bordered = false;
        private boolean selectable = false;
        private boolean compact = false;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

        private Builder() {}

        /**
         * 添加文本列
         */
        public Builder<T> column(String title, Function<T, String> valueExtractor) {
            return column(title, valueExtractor, Align.LEFT);
        }

        /**
         * 添加文本列（带对齐）
         */
        public Builder<T> column(String title, Function<T, String> valueExtractor, Align align) {
            TableColumn<T, String> column = new TableColumn<>(title);
            column.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(valueExtractor.apply(cellData.getValue()))
            );
            column.getStyleClass().add(align.getStyleClass());
            columns.add(column);
            return this;
        }

        /**
         * 添加自定义节点列
         */
        public Builder<T> nodeColumn(String title, Function<T, Node> nodeExtractor) {
            TableColumn<T, Node> column = new TableColumn<>(title);
            column.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(nodeExtractor.apply(cellData.getValue()))
            );
            column.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Node item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(item);
                    }
                }
            });
            columns.add(column);
            return this;
        }

        /**
         * 添加数字列（右对齐）
         */
        public Builder<T> numberColumn(String title, Function<T, Number> valueExtractor) {
            TableColumn<T, Number> column = new TableColumn<>(title);
            column.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(valueExtractor.apply(cellData.getValue()))
            );
            column.setStyle("-fx-alignment: CENTER-RIGHT;");
            columns.add(column);
            return this;
        }

        /**
         * 添加布尔列（显示为勾选框）
         */
        public Builder<T> booleanColumn(String title, Function<T, Boolean> valueExtractor) {
            TableColumn<T, Boolean> column = new TableColumn<>(title);
            column.setCellValueFactory(cellData ->
                new SimpleBooleanProperty(valueExtractor.apply(cellData.getValue()))
            );
            column.setCellFactory(CheckBoxTableCell.forTableColumn(column));
            columns.add(column);
            return this;
        }

        /**
         * 设置表格数据
         */
        public Builder<T> data(List<T> data) {
            this.data = FXCollections.observableArrayList(data);
            return this;
        }

        /**
         * 设置表格数据（ObservableList）
         */
        public Builder<T> data(ObservableList<T> data) {
            this.data = data;
            return this;
        }

        /**
         * 启用斑马纹
         */
        public Builder<T> striped(boolean striped) {
            this.striped = striped;
            return this;
        }

        /**
         * 启用边框
         */
        public Builder<T> bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        /**
         * 启用选择列
         */
        public Builder<T> selectable(boolean selectable) {
            this.selectable = selectable;
            return this;
        }

        /**
         * 紧凑模式
         */
        public Builder<T> compact(boolean compact) {
            this.compact = compact;
            return this;
        }

        public Builder<T> style(String style) {
            this.style = style;
            return this;
        }

        public Builder<T> styleClass(String styleClass) {
            this.extraStyleClasses.add(styleClass);
            return this;
        }

        public TableView<T> build() {
            TableView<T> table = new TableView<>();

            // Add selection column if enabled
            if (selectable) {
                TableColumn<T, Boolean> selectColumn = new TableColumn<>("");
                selectColumn.setPrefWidth(40);
                selectColumn.setCellValueFactory(cellData -> {
                    T item = cellData.getValue();
                    SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
                    selected.addListener((obs, oldVal, newVal) -> {
                        if (newVal) {
                            table.getSelectionModel().select(item);
                        } else {
                            table.getSelectionModel().clearSelection(table.getItems().indexOf(item));
                        }
                    });
                    return selected;
                });
                selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
                table.getColumns().add(selectColumn);
            }

            // Add all columns
            table.getColumns().addAll(columns);

            // Set data
            table.setItems(data);

            // Apply styles
            table.getStyleClass().add("jfx-table");

            if (striped) {
                table.getStyleClass().add("jfx-table-striped");
            }

            if (bordered) {
                table.getStyleClass().add("jfx-table-bordered");
            }

            if (compact) {
                table.getStyleClass().add("jfx-table-compact");
            }

            table.getStyleClass().addAll(extraStyleClasses);

            if (!style.isEmpty()) {
                table.setStyle(style);
            }

            // Default settings
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            table.setPrefHeight(300);

            return table;
        }
    }
}