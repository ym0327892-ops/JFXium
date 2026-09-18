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
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JFXium 表格组件 - 对标 Ant Design Table（继承式 + 双工厂模式，M19.x 重构）。
 *
 * <p><b>定位</b>：数据表格控件，继承自 {@link TableView}，
 * 跟 {@link ListViewAnt} / {@link TreeTableAnt} / {@link ComboBoxAnt} 同款「双工厂模式」——
 * 既能当工厂链式构建，也能被业务继承。</p>
 *
 * <h2>重构要点（M19.x）</h2>
 * <ul>
 *   <li><b>继承式</b>：从组合式（Builder + 内部 {@code new TableView<>()}）改为继承式，
 *       与兄弟组件 ListViewAnt / TreeTableAnt 心智模型一致</li>
 *   <li><b>无中间 Builder</b>：删除 {@code ColumnBuilder} / {@code ActionColumnBuilder}，
 *       列级配置 API 直接挂在 TableAnt 上，使用"currentColumn 游标"隐式追踪正在配置的列</li>
 *   <li><b>{@code build()} 返回自身</b>：类型不撒谎（修复红线 #7），
 *       调用方既可 {@code TableAnt<T> t = ...build()} 也可直接 {@code TableView<T> t = ...build()}</li>
 * </ul>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>列类型</b>：文本列（{@code column}）/ 数字列（{@code numberColumn}，右对齐）/
 *       布尔列（{@code booleanColumn}，勾选框）/ 节点列（{@code nodeColumn}，自定义渲染）/
 *       操作列（{@code actionColumn}，按钮组）</li>
 *   <li><b>列级能力</b>：宽度、resizable、sortable、自定义 sorter、对齐（LEFT/CENTER/RIGHT）、可见性</li>
 *   <li><b>表级能力</b>：resize 策略、默认排序、全局排序开关、斑马纹、边框模式、紧凑模式、隐藏表头</li>
 *   <li><b>交互</b>：行选择 / 多选、行双击回调、选择变化监听</li>
 *   <li><b>继承 {@link LayoutCommon}</b>：自动获得 styleClass / style / background / padding /
 *       borderXxx / borderRadius / 尺寸 / visible / disable 等通用能力</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式</h2>
 * <pre>{@code
 * TableAnt<Person> table = TableAnt.<Person>create()
 *     .column("姓名", Person::getName).columnWidth(120)
 *
 *     .numberColumn("年龄", Person::getAge)
 *         .columnAlign(Align.RIGHT).columnSorter(Comparator.naturalOrder())
 *
 *     .booleanColumn("在职", Person::isActive)
 *
 *     .actionColumn("操作")
 *         .action("编辑", p -> openEdit(p))
 *         .action("删除", p -> doDelete(p)).type(ButtonAnt.Type.LINK).danger()
 *         .actionWidth(180)
 *
 *     .resizePolicy(Resize.UNCONSTRAINED)
 *     .defaultSortBy("年龄", TableColumn.SortType.DESCENDING)
 *     .data(people)
 *     .striped(true)
 *     .selectable(true)
 *     .build();
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class UserTable extends TableAnt<User> {
 *     public UserTable() {
 *         column("ID", User::getId).width(60).align(Align.RIGHT);
 *         column("姓名", User::getName).width(120);
 *         actionColumn("操作").action("编辑", this::onEdit);
 *         data(loadUsers());
 *         striped(true);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>列级方法作用在"currentColumn"</b>：紧跟在 {@code column/numberColumn/booleanColumn/nodeColumn}
 *       之后的 {@code columnWidth/columnAlign/columnSortable/...} 作用在该列上。下一次列 API 调用会切换 currentColumn</li>
 *   <li><b>列级 vs 表级同名方法</b>：列级宽度/对齐/排序方法全部加 {@code column} 前缀
 *       （{@code columnWidth/columnMinWidth/columnMaxWidth/columnResizable/columnAlign/columnSortable/
 *       columnSorter/columnVisible}），避免与 {@link javafx.scene.layout.Region} final 方法及
 *       {@link LayoutCommon} 通用方法冲突</li>
 *   <li><b>操作列模式</b>：{@code action/type/danger/spacing/actionWidth} 仅在 {@code actionColumn(title)}
 *       之后调用才合法</li>
 *   <li><b>取值是快照</b>：内部 cell value 是 {@code SimpleObjectProperty}，
 *       不会自动响应 JavaFX Property 变化。如需实时刷新，请重新 setItems</li>
 *   <li><b>Node 列默认不可排序</b>：Node 不可比较，自动设 sortable=false</li>
 * </ul>
 *
 * @param <T> 行数据类型
 */
public class TableAnt<T> extends TableView<T>
        implements LayoutCommon<TableAnt<T>>, DisabledSupport<TableAnt<T>> {

    // ============================================================
    // 布局常量（P1 抽取，消除魔法值）
    // ============================================================

    /** 选择列默认宽度。 */
    static final double SELECT_COL_WIDTH = 40;
    /** 操作列默认宽度。 */
    static final double ACTION_COL_WIDTH = 160;
    /** 操作列按钮默认间距。 */
    static final double ACTION_COL_SPACING = 8;

    // ============================================================
    // 工厂入口 + 构造函数（双工厂模式）
    // ============================================================

    /** 工厂入口（空数据列表）。 */
    public static <T> TableAnt<T> create() {
        return new TableAnt<>();
    }

    /** 无参构造（业务继承用）。 */
    public TableAnt() {
        super();
        getStyleClass().add(JfxStyles.TABLE);
    }

    // ============================================================
    // 内部状态（列 + 表配置 + 操作列模式）
    // ============================================================

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

    // 列级"游标"：最近添加的列正在被配置
    private TableColumn<T, ?> currentColumn;

    // 操作列模式状态（null 表示不在操作列模式）
    private ActionColumnState<T> actionState;
    // 操作列内"游标"：最近添加的 action 正在被配置（type/danger 修饰它）
    private ActionSpec<T> currentAction;

    // ============================================================
    // 枚举
    // ============================================================

    /** 单元格水平对齐。 */
    public enum Align {
        LEFT(JfxStyles.TABLE_ALIGN_LEFT),
        CENTER(JfxStyles.TABLE_ALIGN_CENTER),
        RIGHT(JfxStyles.TABLE_ALIGN_RIGHT);

        private final String styleClass;

        Align(String styleClass) { this.styleClass = styleClass; }
        public String getStyleClass() { return styleClass; }
    }

    /** 列宽 resize 策略。 */
    public enum Resize {
        /** 列宽总和 = 表格宽度，自动均分（默认）。 */
        CONSTRAINED,
        /** 列宽自由，超出滚动。 */
        UNCONSTRAINED
    }

    /** 内容区分割线模式。 */
    public enum Border {
        /** 无任何分割线（极简风格、Card 嵌套场景）。 */
        NONE,
        /** 仅横线，行与行之间（默认推荐，admin 标配）。 */
        HORIZONTAL,
        /** 仅竖线，列与列之间（数据列对齐重要时）。 */
        VERTICAL,
        /** 横竖都有（Excel 风格）。 */
        BOTH
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 列添加 API（每次调用切换 currentColumn）
    // ============================================================

    /** 添加文本列。 */
    public TableAnt<T> column(String title, Function<T, String> valueExtractor) {
        return column(title, valueExtractor, Align.LEFT);
    }

    /** 添加文本列（带对齐）。 */
    public TableAnt<T> column(String title, Function<T, String> valueExtractor, Align align) {
        TableColumn<T, String> col = newTableColumn(title, valueExtractor, align);
        columns.add(col);
        currentColumn = col;
        actionState = null;
        currentAction = null;
        return this;
    }

    /** 添加数字列（默认右对齐）。 */
    public TableAnt<T> numberColumn(String title, Function<T, Number> valueExtractor) {
        TableColumn<T, Number> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleObjectProperty<>(valueExtractor.apply(cd.getValue())));
        col.getStyleClass().add(Align.RIGHT.getStyleClass());
        columns.add(col);
        currentColumn = col;
        actionState = null;
        currentAction = null;
        return this;
    }

    /** 添加布尔列（渲染为勾选框，默认居中）。 */
    public TableAnt<T> booleanColumn(String title, Function<T, Boolean> valueExtractor) {
        TableColumn<T, Boolean> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleBooleanProperty(valueExtractor.apply(cd.getValue())));
        col.setCellFactory(CheckBoxTableCell.forTableColumn(col));
        col.getStyleClass().add(Align.CENTER.getStyleClass());
        columns.add(col);
        currentColumn = col;
        actionState = null;
        currentAction = null;
        return this;
    }

    /** 添加自定义节点列（默认 sortable=false，Node 不可比较）。 */
    public TableAnt<T> nodeColumn(String title, Function<T, Node> nodeExtractor) {
        TableColumn<T, Node> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleObjectProperty<>(nodeExtractor.apply(cd.getValue())));
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : item);
            }
        });
        col.setSortable(false);
        columns.add(col);
        currentColumn = col;
        actionState = null;
        currentAction = null;
        return this;
    }

    /**
     * 进入操作列模式。之后的 {@code action/type/danger/spacing/actionWidth} 作用在本列上。
     */
    public TableAnt<T> actionColumn(String title) {
        actionState = new ActionColumnState<>(title);
        currentColumn = null;
        currentAction = null;
        return this;
    }

    // ============================================================
    // 列级配置 API（修饰 currentColumn）
    // ============================================================

    /** 设置当前列首选宽度。 */
    public TableAnt<T> columnWidth(double pref) {
        requireCurrentColumn().setPrefWidth(pref);
        return this;
    }

    /** 同时设置当前列首选/最小/最大宽度。 */
    public TableAnt<T> columnWidth(double pref, double min, double max) {
        TableColumn<T, ?> col = requireCurrentColumn();
        col.setPrefWidth(pref);
        col.setMinWidth(min);
        col.setMaxWidth(max);
        return this;
    }

    /** 当前列最小宽度。 */
    public TableAnt<T> columnMinWidth(double min) {
        requireCurrentColumn().setMinWidth(min);
        return this;
    }

    /** 当前列最大宽度。 */
    public TableAnt<T> columnMaxWidth(double max) {
        requireCurrentColumn().setMaxWidth(max);
        return this;
    }

    /** 当前列是否允许鼠标拖动列头改宽。 */
    public TableAnt<T> columnResizable(boolean resizable) {
        requireCurrentColumn().setResizable(resizable);
        return this;
    }

    /**
     * 当前列是否允许点列头排序（列级）。
     *
     * <p>与表级 {@link #sortable(boolean)} 区分：表级控制"是否启用列头排序 UI"，
     * 列级控制"该列能否排序"。两个开关都允许时才可排序。</p>
     */
    public TableAnt<T> columnSortable(boolean sortable) {
        requireCurrentColumn().setSortable(sortable);
        return this;
    }

    /**
     * 当前列自定义比较器。比较的是该列的 cell 值（V 类型）。
     * 对 Node 列、复合数据列尤其有用。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TableAnt<T> columnSorter(Comparator<?> comparator) {
        TableColumn col = requireCurrentColumn();
        col.setComparator((Comparator) comparator);
        col.setSortable(true);  // 显式设了排序器，自动启用
        return this;
    }

    /** 当前列对齐（同时设表头和内容）。 */
    public TableAnt<T> columnAlign(Align align) {
        TableColumn<T, ?> col = requireCurrentColumn();
        // 移除旧 align-* 类，避免叠加
        col.getStyleClass().removeIf(s -> s.startsWith(JfxStyles.ALIGN_PREFIX)
                && !s.startsWith(JfxStyles.ALIGN_HEADER_PREFIX)
                && !s.startsWith(JfxStyles.ALIGN_CONTENT_PREFIX));
        col.getStyleClass().add(align.getStyleClass());
        return this;
    }

    /** 当前列仅设置表头对齐（不影响行内容）。 */
    public TableAnt<T> headerAlign(Align align) {
        TableColumn<T, ?> col = requireCurrentColumn();
        col.getStyleClass().removeIf(s -> s.startsWith(JfxStyles.ALIGN_HEADER_PREFIX));
        col.getStyleClass().add(switch (align) {
            case LEFT -> JfxStyles.TABLE_ALIGN_HEADER_LEFT;
            case CENTER -> JfxStyles.TABLE_ALIGN_HEADER_CENTER;
            case RIGHT -> JfxStyles.TABLE_ALIGN_HEADER_RIGHT;
        });
        return this;
    }

    /** 当前列仅设置行内容对齐（不影响表头）。 */
    public TableAnt<T> contentAlign(Align align) {
        TableColumn<T, ?> col = requireCurrentColumn();
        col.getStyleClass().removeIf(s -> s.startsWith(JfxStyles.ALIGN_CONTENT_PREFIX));
        col.getStyleClass().add(switch (align) {
            case LEFT -> JfxStyles.TABLE_ALIGN_CONTENT_LEFT;
            case CENTER -> JfxStyles.TABLE_ALIGN_CONTENT_CENTER;
            case RIGHT -> JfxStyles.TABLE_ALIGN_CONTENT_RIGHT;
        });
        return this;
    }

    /**
     * 当前列可见性。
     *
     * <p>与 {@link LayoutCommon#visible(boolean)} 区分：列级仅控制该列是否可见，
     * LayoutCommon 的 {@code visible} 控制整个表格节点是否可见。</p>
     */
    public TableAnt<T> columnVisible(boolean visible) {
        requireCurrentColumn().setVisible(visible);
        return this;
    }

    // ============================================================
    // 操作列内按钮配置 API（修饰 actionState / currentAction）
    // ============================================================

    /** 添加一个操作按钮（操作列模式）。 */
    public TableAnt<T> action(String label, Consumer<T> handler) {
        requireActionState();
        ActionSpec<T> spec = new ActionSpec<>(label, handler);
        actionState.actions.add(spec);
        currentAction = spec;
        return this;
    }

    /** 修饰最近添加的 action 按钮类型（默认 LINK）。 */
    public TableAnt<T> type(ButtonAnt.Type type) {
        requireLastAction().type = type;
        return this;
    }

    /** 修饰最近添加的 action 为危险样式（红色文字）。 */
    public TableAnt<T> danger() {
        requireLastAction().danger = true;
        return this;
    }

    /** 操作列按钮间距（默认 8）。 */
    public TableAnt<T> spacing(double spacing) {
        requireActionState().spacing = spacing;
        return this;
    }

    /** 操作列宽度（默认 160）。 */
    public TableAnt<T> actionWidth(double width) {
        requireActionState().prefWidth = width;
        return this;
    }

    // ============================================================
    // 表级配置 API
    // ============================================================

    public TableAnt<T> data(List<T> data) {
        this.data = FXCollections.observableArrayList(data);
        return this;
    }

    public TableAnt<T> data(ObservableList<T> data) {
        this.data = data;
        return this;
    }

    public TableAnt<T> striped(boolean striped) {
        this.striped = striped;
        return this;
    }

    /** 内容区分割线模式（NONE/HORIZONTAL/VERTICAL/BOTH）。 */
    public TableAnt<T> borders(Border border) {
        this.border = border;
        return this;
    }

    public TableAnt<T> selectable(boolean selectable) {
        this.selectable = selectable;
        return this;
    }

    /** 表格尺寸（SMALL/MIDDLE/LARGE，对齐 Ant Design size）。 */
    public TableAnt<T> size(Size size) {
        this.size = size;
        return this;
    }

    /** 是否显示表头（默认 true）。设为 false 完全隐藏表头行。 */
    public TableAnt<T> showHeader(boolean show) {
        this.showHeader = show;
        return this;
    }

    /** 列宽策略。默认 CONSTRAINED（列宽总和=表格宽度）。 */
    public TableAnt<T> resizePolicy(Resize policy) {
        this.resizePolicy = policy;
        return this;
    }

    /** 全局排序开关（false 时所有列都不可排序）。 */
    public TableAnt<T> sortable(boolean sortable) {
        this.globalSortable = sortable;
        return this;
    }

    /** 启动时默认排序的列与方向。 */
    public TableAnt<T> defaultSortBy(String columnTitle, TableColumn.SortType type) {
        this.defaultSortColumnTitle = columnTitle;
        this.defaultSortType = type;
        return this;
    }

    // ============================================================
    // 构建
    // ============================================================

    /**
     * 应用所有配置并返回自身。
     *
     * <p>与老 Builder 模式不同：本方法不做"创建新节点"，而是把累积的配置应用到当前 TableAnt 实例。
     * 由于 {@code TableAnt extends TableView}，返回值既可赋给 {@code TableAnt<T>}，
     * 也可直接当 {@code TableView<T>} 用（多态向上转型）。</p>
     */
    public TableAnt<T> build() {
        // 样式类
        if (striped) getStyleClass().add(JfxStyles.TABLE_STRIPED);

        // 尺寸
        switch (size) {
            case SMALL  -> getStyleClass().add(JfxStyles.TABLE_SIZE_SMALL);
            case MIDDLE -> getStyleClass().add(JfxStyles.TABLE_SIZE_MIDDLE);
            case LARGE  -> getStyleClass().add(JfxStyles.TABLE_SIZE_LARGE);
        }

        // 内容区分割线模式
        switch (border) {
            case NONE       -> getStyleClass().add(JfxStyles.TABLE_BORDER_NONE);
            case HORIZONTAL -> getStyleClass().add(JfxStyles.TABLE_BORDER_H);
            case VERTICAL   -> getStyleClass().add(JfxStyles.TABLE_BORDER_V);
            case BOTH       -> getStyleClass().add(JfxStyles.TABLE_BORDER_BOTH);
        }

        // 是否显示表头
        if (!showHeader) {
            getStyleClass().add(JfxStyles.TABLE_NO_HEADER);
        }

        // 选择列（永远在第一列）
        if (selectable) {
            TableColumn<T, Boolean> selectColumn = new TableColumn<>("");
            selectColumn.setPrefWidth(SELECT_COL_WIDTH);
            selectColumn.setSortable(false);
            selectColumn.setResizable(false);
            selectColumn.getStyleClass().add(Align.CENTER.getStyleClass());
            selectColumn.setCellValueFactory(cd -> {
                T item = cd.getValue();
                SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
                selected.addListener((obs, oldVal, newVal) -> {
                    if (newVal) getSelectionModel().select(item);
                    else getSelectionModel().clearSelection(getItems().indexOf(item));
                });
                return selected;
            });
            selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
            getColumns().add(selectColumn);
        }

        // 操作列（业务列）
        if (actionState != null) {
            ActionColumnState<T> ac = actionState;
            TableColumn<T, Node> acCol = new TableColumn<>(ac.title);
            acCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(buildActionBar(cd.getValue(), ac)));
            acCol.setCellFactory(c -> new TableCell<>() {
                @Override protected void updateItem(Node item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty || item == null ? null : item);
                }
            });
            acCol.setSortable(false);
            acCol.setResizable(true);
            acCol.setPrefWidth(ac.prefWidth);
            acCol.getStyleClass().add(Align.CENTER.getStyleClass());
            getColumns().add(acCol);
        }

        // 业务列
        for (TableColumn<T, ?> c : columns) {
            getColumns().add(c);
        }

        // 全局排序开关：覆盖所有列
        if (!globalSortable) {
            for (TableColumn<T, ?> c : columns) {
                c.setSortable(false);
            }
        }

        // 数据
        setItems(data);

        // resize 策略
        switch (resizePolicy) {
            case CONSTRAINED   -> setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            case UNCONSTRAINED -> setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        }
        // 注：不再硬编码 setPrefHeight(300)。表格默认高度由父容器/ScrollPane 决定，
        // 用户需要固定高度时通过 .style("-fx-pref-height: 300px") 或外层容器约束。

        // 默认排序
        if (defaultSortColumnTitle != null) {
            for (TableColumn<T, ?> c : columns) {
                if (defaultSortColumnTitle.equals(c.getText())) {
                    c.setSortType(defaultSortType);
                    getSortOrder().add(c);
                    break;
                }
            }
        }

        return this;
    }

    // ============================================================
    // 私有辅助
    // ============================================================

    /** 通用：建一个文本列（含 cellValueFactory + 对齐 styleClass）。 */
    private TableColumn<T, String> newTableColumn(String title, Function<T, String> extractor, Align align) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleObjectProperty<>(extractor.apply(cd.getValue())));
        col.getStyleClass().add(align.getStyleClass());
        return col;
    }

    /** 校验 currentColumn 不为 null。 */
    private TableColumn<T, ?> requireCurrentColumn() {
        if (currentColumn == null) {
            throw new IllegalStateException(
                    "列级方法（columnWidth/columnAlign/columnSortable/...）必须在 .column/.numberColumn/.booleanColumn/.nodeColumn 之后调用");
        }
        return currentColumn;
    }

    /** 校验当前在操作列模式。 */
    private ActionColumnState<T> requireActionState() {
        if (actionState == null) {
            throw new IllegalStateException(
                    ".action/.type/.danger/.spacing/.actionWidth 必须在 .actionColumn(title) 之后调用");
        }
        return actionState;
    }

    /** 校验当前有最近添加的 action（type/danger 修饰它）。 */
    private ActionSpec<T> requireLastAction() {
        requireActionState();
        if (currentAction == null) {
            throw new IllegalStateException(
                    ".type()/.danger() 必须紧跟在 .action(label, handler) 之后调用");
        }
        return currentAction;
    }

    /** 操作列：构建单行的按钮 HBox。 */
    private HBox buildActionBar(T row, ActionColumnState<T> ac) {
        HBox bar = new HBox(ac.spacing);
        bar.setAlignment(Pos.CENTER);
        for (ActionSpec<T> a : ac.actions) {
            ButtonAnt bb = ButtonAnt.create(a.label).type(a.type);
            Button btn = bb.build();
            if (a.danger) {
                btn.getStyleClass().add(JfxStyles.BUTTON_DANGER_TEXT);
            }
            EventHandler<ActionEvent> h = e -> a.handler.accept(row);
            btn.setOnAction(h);
            bar.getChildren().add(btn);
        }
        return bar;
    }

    // ============================================================
    // 内部状态类
    // ============================================================

    /** 操作列模式的状态。 */
    private static final class ActionColumnState<T> {
        final String title;
        final List<ActionSpec<T>> actions = new ArrayList<>();
        double prefWidth = ACTION_COL_WIDTH;
        double spacing = ACTION_COL_SPACING;

        ActionColumnState(String title) {
            this.title = title;
        }
    }

    /** 单个操作按钮的规格。 */
    private static final class ActionSpec<T> {
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
