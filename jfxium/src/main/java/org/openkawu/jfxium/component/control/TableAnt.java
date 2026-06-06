package org.openkawu.jfxium.component.control;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JFXium 表格组件 - 对标 Ant Design Table（组合式，Builder 模式，M11 高级化重构）。
 *
 * <p><b>定位</b>：数据表格控件，包装 JavaFX {@link TableView}，
 * 提供列级链式配置 + 表级链式配置的两层 API（路 B 设计）。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>列类型</b>：文本列（{@code column}）/ 数字列（{@code numberColumn}，右对齐）/
 *       布尔列（{@code booleanColumn}，勾选框）/ 节点列（{@code nodeColumn}，自定义渲染）/
 *       操作列（{@code actionColumn}，按钮组）</li>
 *   <li><b>列级能力</b>：宽度、resizable、sortable、自定义 sorter、对齐（LEFT/CENTER/RIGHT）、可见性</li>
 *   <li><b>表级能力</b>：resize 策略、默认排序、全局排序开关、斑马纹、边框模式、紧凑模式、隐藏表头</li>
 *   <li><b>交互</b>：行选择 / 多选、行双击回调、选择变化监听</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>admin 列表页（用户列表、订单列表、日志列表）</li>
 *   <li>数据报表（带排序、斑马纹）</li>
 *   <li>CRUD 操作（配合 CrudTemplate + actionColumn）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * TableView<Person> table = TableAnt.<Person>create()
 *     .column("ID", Person::getId)
 *         .width(60).align(Align.RIGHT).end()
 *
 *     .column("姓名", Person::getName)
 *         .width(120).end()
 *
 *     .numberColumn("年龄", Person::getAge)
 *         .sorter(Comparator.naturalOrder())  // 自定义排序
 *         .end()
 *
 *     .booleanColumn("在职", Person::isActive)
 *         .end()
 *
 *     .actionColumn("操作")
 *         .action("编辑", p -> openEdit(p))
 *         .action("删除", p -> doDelete(p)).type(ButtonAnt.Type.LINK).danger()
 *         .end()
 *
 *     .resizePolicy(Resize.UNCONSTRAINED)
 *     .defaultSortBy("年龄", TableColumn.SortType.DESCENDING)
 *     .data(people)
 *     .striped(true)
 *     .selectable(true)
 *     .build();
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>列级 + 表级两层 API</b>：{@code .column(...)} 返回 {@link ColumnBuilder}，
 *       {@code .end()} 回到表 Builder，避免“魔法”状态混淆</li>
 *   <li><b>取值是快照</b>：内部 cell value 是 {@code SimpleObjectProperty}，
 *       不会自动响应 JavaFX Property 变化。如需实时刷新，请重新 setItems</li>
 *   <li><b>Node 列默认不可排序</b>：Node 不可比较，自动设 sortable=false</li>
 * </ul>
 *
 * @param <T> 行数据类型
 */
public class TableAnt<T> {

    public static <T> Builder<T> create() {
        return new Builder<>();
    }

    /** 单元格水平对齐 */
    public enum Align {
        LEFT("align-left"),
        CENTER("align-center"),
        RIGHT("align-right");

        private final String styleClass;

        Align(String styleClass) { this.styleClass = styleClass; }
        public String getStyleClass() { return styleClass; }
    }

    /** 列宽 resize 策略 */
    public enum Resize {
        /** 列宽总和 = 表格宽度，自动均分（默认） */
        CONSTRAINED,
        /** 列宽自由，超出滚动 */
        UNCONSTRAINED
    }

    /**
     * 内容区分割线模式（M11.x 增强）。
     * 取代老 {@code .bordered(boolean)} 的二态开关。
     */
    public enum Border {
        /** 无任何分割线（极简风格、Card 嵌套场景） */
        NONE,
        /** 仅横线，行与行之间（默认推荐，admin 标配） */
        HORIZONTAL,
        /** 仅竖线，列与列之间（数据列对齐重要时） */
        VERTICAL,
        /** 横竖都有（Excel 风格，等价老 {@code .bordered(true)}） */
        BOTH
    }

    /**
     * 表格尺寸（M11.2 增强，对齐 Ant Design Table size 三态）。
     * <ul>
     *   <li>SMALL：表头 40 / 行高 36 / padding 6×8 / 字号 13（紧凑）</li>
     *   <li>MIDDLE：表头 48 / 行高 48 / padding 8×12 / 字号 14（默认）</li>
     *   <li>LARGE：表头 56 / 行高 56 / padding 14×16 / 字号 15（宽松）</li>
     * </ul>
     */
    public enum Size {
        SMALL,
        MIDDLE,
        LARGE
    }

    // ===========================================================
    // 表 Builder
    // ===========================================================
    public static class Builder<T> extends AbstractStyleBuilder<Builder<T>> {
        private final List<TableColumn<T, ?>> columns = new ArrayList<>();
        private ObservableList<T> data = FXCollections.observableArrayList();
        private boolean striped = false;
        private boolean selectable = false;
        private Size size = Size.MIDDLE;                // 默认 middle 尺寸
        private boolean showHeader = true;             // 是否显示表头（默认显示）
        private Border border = Border.HORIZONTAL;     // 内容区分割线（默认仅横线）

        // 表级排序与列宽策略
        private Resize resizePolicy = Resize.CONSTRAINED;
        private boolean globalSortable = true;
        private String defaultSortColumnTitle;
        private TableColumn.SortType defaultSortType = TableColumn.SortType.ASCENDING;

        private Builder() {}

        // ---------------- 列 API（路 B：返回 ColumnBuilder） ----------------

        /**
         * 添加文本列。
         * @return 列 Builder，调 {@code .end()} 回到表 Builder
         */
        public ColumnBuilder<T, String> column(String title, Function<T, String> valueExtractor) {
            return column(title, valueExtractor, Align.LEFT);
        }

        /**
         * 添加文本列（带对齐）。
         */
        public ColumnBuilder<T, String> column(String title, Function<T, String> valueExtractor, Align align) {
            TableColumn<T, String> col = new TableColumn<>(title);
            col.setCellValueFactory(cd -> new SimpleObjectProperty<>(valueExtractor.apply(cd.getValue())));
            col.getStyleClass().add(align.getStyleClass());
            columns.add(col);
            return new ColumnBuilder<>(this, col);
        }

        /**
         * 添加数字列（默认右对齐）。
         */
        public ColumnBuilder<T, Number> numberColumn(String title, Function<T, Number> valueExtractor) {
            TableColumn<T, Number> col = new TableColumn<>(title);
            col.setCellValueFactory(cd -> new SimpleObjectProperty<>(valueExtractor.apply(cd.getValue())));
            col.getStyleClass().add(Align.RIGHT.getStyleClass());
            columns.add(col);
            return new ColumnBuilder<>(this, col);
        }

        /**
         * 添加布尔列（渲染为勾选框，默认居中）。
         */
        public ColumnBuilder<T, Boolean> booleanColumn(String title, Function<T, Boolean> valueExtractor) {
            TableColumn<T, Boolean> col = new TableColumn<>(title);
            col.setCellValueFactory(cd -> new SimpleBooleanProperty(valueExtractor.apply(cd.getValue())));
            col.setCellFactory(CheckBoxTableCell.forTableColumn(col));
            col.getStyleClass().add(Align.CENTER.getStyleClass());  // 默认居中
            columns.add(col);
            return new ColumnBuilder<>(this, col);
        }

        /**
         * 添加自定义节点列（默认 sortable=false，Node 不可比较）。
         */
        public ColumnBuilder<T, Node> nodeColumn(String title, Function<T, Node> nodeExtractor) {
            TableColumn<T, Node> col = new TableColumn<>(title);
            col.setCellValueFactory(cd -> new SimpleObjectProperty<>(nodeExtractor.apply(cd.getValue())));
            col.setCellFactory(c -> new TableCell<>() {
                @Override protected void updateItem(Node item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty || item == null ? null : item);
                }
            });
            col.setSortable(false);  // Node 默认不可排序
            columns.add(col);
            return new ColumnBuilder<>(this, col);
        }

        /**
         * 添加操作列（语法糖：内部用 nodeColumn + HBox 按钮组）。
         */
        public ActionColumnBuilder<T> actionColumn(String title) {
            return new ActionColumnBuilder<>(this, title);
        }

        // ---------------- 表级配置 ----------------

        public Builder<T> data(List<T> data) {
            this.data = FXCollections.observableArrayList(data);
            return this;
        }

        public Builder<T> data(ObservableList<T> data) {
            this.data = data;
            return this;
        }

        public Builder<T> striped(boolean striped) { this.striped = striped; return this; }

        /** 内容区分割线模式（NONE/HORIZONTAL/VERTICAL/BOTH）。 */
        public Builder<T> borders(Border border) {
            this.border = border;
            return this;
        }

        public Builder<T> selectable(boolean selectable) { this.selectable = selectable; return this; }

        /** 表格尺寸（SMALL/MIDDLE/LARGE，对齐 Ant Design size）。 */
        public Builder<T> size(Size size) {
            this.size = size;
            return this;
        }

        /** 是否显示表头（默认 true）。设为 false 完全隐藏表头行。 */
        public Builder<T> showHeader(boolean show) {
            this.showHeader = show;
            return this;
        }

        /** 列宽策略。默认 CONSTRAINED（列宽总和=表格宽度）。 */
        public Builder<T> resizePolicy(Resize policy) {
            this.resizePolicy = policy;
            return this;
        }

        /** 全局排序开关（false 时所有列都不可排序）。 */
        public Builder<T> sortable(boolean sortable) {
            this.globalSortable = sortable;
            return this;
        }

        /** 启动时默认排序的列与方向。 */
        public Builder<T> defaultSortBy(String columnTitle, TableColumn.SortType type) {
            this.defaultSortColumnTitle = columnTitle;
            this.defaultSortType = type;
            return this;
        }

        // ---------------- 构建 ----------------

        public TableView<T> build() {
            TableView<T> table = new TableView<>();

            // 选择列
            if (selectable) {
                TableColumn<T, Boolean> selectColumn = new TableColumn<>("");
                selectColumn.setPrefWidth(40);
                selectColumn.setSortable(false);
                selectColumn.setResizable(false);
                selectColumn.getStyleClass().add(Align.CENTER.getStyleClass());  // 勾选框居中
                selectColumn.setCellValueFactory(cd -> {
                    T item = cd.getValue();
                    SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
                    selected.addListener((obs, oldVal, newVal) -> {
                        if (newVal) table.getSelectionModel().select(item);
                        else table.getSelectionModel().clearSelection(table.getItems().indexOf(item));
                    });
                    return selected;
                });
                selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
                table.getColumns().add(selectColumn);
            }

            // 业务列
            table.getColumns().addAll(columns);

            // 全局排序开关：覆盖所有列
            if (!globalSortable) {
                for (TableColumn<T, ?> c : columns) {
                    c.setSortable(false);
                }
            }

            // 数据
            table.setItems(data);

            // 样式
            table.getStyleClass().add("jfx-table");
            if (striped)  table.getStyleClass().add("jfx-table-striped");

            // 尺寸（M11.2 新增，对齐 Ant Design size 三态）
            switch (size) {
                case SMALL  -> table.getStyleClass().add("jfx-table-small");
                case MIDDLE -> table.getStyleClass().add("jfx-table-middle");
                case LARGE  -> table.getStyleClass().add("jfx-table-large");
            }

            // 内容区分割线模式（独立 styleClass，不与 .bordered 冲突）
            switch (border) {
                case NONE       -> table.getStyleClass().add("jfx-table-border-none");
                case HORIZONTAL -> table.getStyleClass().add("jfx-table-border-h");
                case VERTICAL   -> table.getStyleClass().add("jfx-table-border-v");
                case BOTH       -> table.getStyleClass().add("jfx-table-border-both");
            }

            // 是否显示表头：通过 styleClass 让 LESS 把 column-header-background 高度收为 0
            if (!showHeader) {
                table.getStyleClass().add("jfx-table-no-header");
            }

            applyStyles(table);

            // resize 策略
            switch (resizePolicy) {
                case CONSTRAINED   -> table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                case UNCONSTRAINED -> table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
            // 注：不再硬编码 setPrefHeight(300)。表格默认高度由父容器/ScrollPane 决定，
            // 用户需要固定高度时通过 .style("-fx-pref-height: 300px") 或外层容器约束。

            // 默认排序
            if (defaultSortColumnTitle != null) {
                for (TableColumn<T, ?> c : columns) {
                    if (defaultSortColumnTitle.equals(c.getText())) {
                        c.setSortType(defaultSortType);
                        table.getSortOrder().add(c);
                        break;
                    }
                }
            }

            return table;
        }
    }

    // ===========================================================
    // 列 Builder（路 B 核心）
    // ===========================================================
    /**
     * 单列配置 Builder。链式配置完后调 {@link #end()} 回到表 Builder。
     *
     * @param <T> 行数据类型
     * @param <V> 列值类型
     */
    public static class ColumnBuilder<T, V> {
        private final Builder<T> parent;
        private final TableColumn<T, V> col;

        ColumnBuilder(Builder<T> parent, TableColumn<T, V> col) {
            this.parent = parent;
            this.col = col;
        }

        /** 设置首选宽度。 */
        public ColumnBuilder<T, V> width(double pref) {
            col.setPrefWidth(pref);
            return this;
        }

        /** 同时设置首选/最小/最大宽度。 */
        public ColumnBuilder<T, V> width(double pref, double min, double max) {
            col.setPrefWidth(pref);
            col.setMinWidth(min);
            col.setMaxWidth(max);
            return this;
        }

        public ColumnBuilder<T, V> minWidth(double min) { col.setMinWidth(min); return this; }
        public ColumnBuilder<T, V> maxWidth(double max) { col.setMaxWidth(max); return this; }

        /** 是否允许鼠标拖动列头改宽。 */
        public ColumnBuilder<T, V> resizable(boolean resizable) {
            col.setResizable(resizable);
            return this;
        }

        /** 是否允许点列头排序。 */
        public ColumnBuilder<T, V> sortable(boolean sortable) {
            col.setSortable(sortable);
            return this;
        }

        /**
         * 自定义比较器。比较的是该列的 cell 值（V 类型）。
         * 对 Node 列、复合数据列尤其有用。
         */
        public ColumnBuilder<T, V> sorter(Comparator<V> comparator) {
            col.setComparator(comparator);
            col.setSortable(true);  // 显式设了排序器，自动启用
            return this;
        }

        /** 列对齐（同时设表头和内容，追加 align-* styleClass）。 */
        public ColumnBuilder<T, V> align(Align align) {
            // 移除旧 align-* 类，避免叠加
            col.getStyleClass().removeIf(s -> s.startsWith("align-")
                    && !s.startsWith("align-header-") && !s.startsWith("align-content-"));
            col.getStyleClass().add(align.getStyleClass());
            return this;
        }

        /** 仅设置表头对齐（不影响行内容）。 */
        public ColumnBuilder<T, V> headerAlign(Align align) {
            col.getStyleClass().removeIf(s -> s.startsWith("align-header-"));
            col.getStyleClass().add("align-header-" + align.name().toLowerCase());
            return this;
        }

        /** 仅设置行内容对齐（不影响表头）。 */
        public ColumnBuilder<T, V> contentAlign(Align align) {
            col.getStyleClass().removeIf(s -> s.startsWith("align-content-"));
            col.getStyleClass().add("align-content-" + align.name().toLowerCase());
            return this;
        }

        /** 列可见性。 */
        public ColumnBuilder<T, V> visible(boolean visible) {
            col.setVisible(visible);
            return this;
        }

        /** 结束列配置，回到表 Builder。 */
        public Builder<T> end() {
            return parent;
        }
    }

    // ===========================================================
    // 操作列 Builder（语法糖：底层是 nodeColumn）
    // ===========================================================
    /**
     * 操作列 Builder。
     *
     * <p>用法：</p>
     * <pre>{@code
     * .actionColumn("操作")
     *     .action("编辑", p -> openEdit(p))
     *     .action("删除", p -> doDelete(p)).type(ButtonAnt.Type.LINK).danger()
     *     .end()
     * }</pre>
     */
    public static class ActionColumnBuilder<T> {
        private final Builder<T> parent;
        private final String title;
        private final List<ActionSpec<T>> actions = new ArrayList<>();
        private double prefWidth = 160;
        private double spacing = 8;

        ActionColumnBuilder(Builder<T> parent, String title) {
            this.parent = parent;
            this.title = title;
        }

        /**
         * 添加一个操作按钮。
         * @return 当前 Builder（支持继续 .type() / .danger() 修饰**最后**添加的 action）
         */
        public ActionColumnBuilder<T> action(String label, Consumer<T> handler) {
            actions.add(new ActionSpec<>(label, handler));
            return this;
        }

        /** 修饰最后一个 action 的按钮类型（默认 LINK）。 */
        public ActionColumnBuilder<T> type(ButtonAnt.Type type) {
            requireLast().type = type;
            return this;
        }

        /** 修饰最后一个 action 为危险样式（红色文字）。 */
        public ActionColumnBuilder<T> danger() {
            requireLast().danger = true;
            return this;
        }

        /** 列宽（默认 160）。 */
        public ActionColumnBuilder<T> width(double width) {
            this.prefWidth = width;
            return this;
        }

        /** 按钮间距（默认 8）。 */
        public ActionColumnBuilder<T> spacing(double spacing) {
            this.spacing = spacing;
            return this;
        }

        public Builder<T> end() {
            // 利用 nodeColumn 实现：每行渲染一个 HBox，里面放按钮
            ColumnBuilder<T, Node> cb = parent.nodeColumn(title, row -> buildActionBar(row));
            cb.width(prefWidth);
            cb.sortable(false);
            cb.resizable(true);
            cb.align(Align.CENTER);
            return cb.end();
        }

        // ---- 私有 ----

        private HBox buildActionBar(T row) {
            HBox bar = new HBox(spacing);
            bar.setAlignment(Pos.CENTER);  // 水平+垂直居中（之前是 CENTER_LEFT，按钮看起来贴左上）
            for (ActionSpec<T> a : actions) {
                ButtonAnt bb = ButtonAnt.create(a.label).type(a.type);
                Button btn = bb.build();
                if (a.danger) {
                    btn.getStyleClass().add("button-danger-text");
                }
                EventHandler<ActionEvent> h = e -> a.handler.accept(row);
                btn.setOnAction(h);
                bar.getChildren().add(btn);
            }
            return bar;
        }

        private ActionSpec<T> requireLast() {
            if (actions.isEmpty()) {
                throw new IllegalStateException(
                        ".type()/.danger() 必须紧跟在 .action(...) 之后调用");
            }
            return actions.get(actions.size() - 1);
        }

        /** 单个操作按钮的规格。 */
        private static class ActionSpec<T> {
            final String label;
            final Consumer<T> handler;
            ButtonAnt.Type type = ButtonAnt.Type.LINK;
            boolean danger = false;

            ActionSpec(String label, Consumer<T> handler) {
                this.label = label;
                this.handler = handler;
            }
        }
    }
}
