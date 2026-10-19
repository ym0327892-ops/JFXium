package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.component.layout.AbstractHBoxAnt;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * TitleBarAnt —— 自绘窗口标题栏（Windows 11 / WinUI 3 风格）。
 *
 * <p><b>定位</b>：{@code StageStyle.UNDECORATED} 自绘外壳的顶部条 ——
 * [窗口图标] [标题] [left] ─空隙─ [center] ─空隙─ [right] [{@link WindowControlAnt} 最小化/最大化/关闭]。
 * 负责标题栏的三件交互：<b>拖动窗口</b>、<b>双击最大化/还原</b>、
 * <b>窗口按钮接线</b>；配色/几何全部走 {@code _window-control.less} 的主题 token。</p>
 *
 * <h2>三段式插槽</h2>
 * <pre>
 *  [图标?] [标题?] [left...]  ─spacer─  [center...]  ─spacer─  [right...]  [窗口按钮]
 * </pre>
 * <p>与 {@link HBarAnt} 同一套 left / center / right 语义：</p>
 * <ul>
 *   <li>{@link #left} —— 标题右侧的操作区，最常放 {@code ToolBarAnt} / {@code HBarAnt}
 *       （新建/保存/搜索等）；标题为空时它就是整条左侧</li>
 *   <li>{@link #center} —— 真正居中（左右各一个弹性空隙），适合标签页 / 视图切换器；
 *       留空则退化为「左 + spacer + 右」二段式</li>
 *   <li>{@link #right} —— 窗口按钮之前的右区（账号、通知、设置）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Stage stage = new Stage(StageStyle.UNDECORATED);
 * TitleBarAnt titleBar = TitleBarAnt.create()
 *     .icon(IconPath.create(IconPath.FOLDER))
 *     .title("我的应用")
 *     .left(ToolBarAnt.create()
 *             .button(saveIcon, "保存", () -> save())
 *             .padding(0, 4, 0, 4)
 *             .build())
 *     .center(SegmentedAnt.create().option("edit", "编辑").option("view", "预览").build())
 *     .right(ButtonAnt.iconOnly(settingsIcon).build())
 *     .stage(stage)                 // 接线：拖动 + 双击 + 三个窗口按钮
 *     .build();
 *
 * BorderPane root = BorderPane.create().top(titleBar).center(content).build();
 * stage.setScene(new Scene(root, 960, 640));
 * stage.show();
 * }</pre>
 *
 * <p><b>标题为空时不占位</b>：{@code title(null)} / {@code title("")} 或根本不调 {@code title()}
 * 时，标题节点不可见且不参与布局，左侧直接交给 {@code left(...)}。</p>
 *
 * <h2>行为边界</h2>
 * <ul>
 *   <li><b>不读取也不回写 {@link Stage#titleProperty()}</b>：自绘标题栏的文字由 {@link #title}
 *       显式给出（窗口标题属于操作系统/任务栏语义，二者不必一致）。</li>
 *   <li><b>拖动只移动位置</b>：已最大化时拖动不会像 Windows 那样先还原再跟手
 *       （那需要按宽度比例反算还原后的窗口位置，行为边界复杂且平台相关，暂不实现）。</li>
 *   <li><b>栏内控件不触发拖动</b>：按下点落在按钮 / 输入框 / 下拉框等控件上时，
 *       拖动与双击最大化都不生效，避免「点按钮顺手把窗口拖走」。</li>
 *   <li>未绑定 {@code stage} 时拖动/双击/窗口按钮全部为无操作（便于在普通容器里预览外观）。</li>
 * </ul>
 *
 * <p><b>线程</b>：仅在 FX 应用线程使用。</p>
 */
public class TitleBarAnt extends AbstractHBoxAnt<TitleBarAnt> {

    private final LabelAnt titleLabel;

    /** 弹性空隙：把窗口按钮推到最右，标题/左区靠左。 */
    private final Region spacer;

    /** center 非空时的第二个空隙，保证 center 真正居中。 */
    private final Region centerSpacer;

    private final WindowControlAnt controls;

    private final List<Node> leftNodes = new ArrayList<>();
    private final List<Node> centerNodes = new ArrayList<>();
    private final List<Node> rightNodes = new ArrayList<>();

    private Node icon;
    private Stage stage;
    private boolean draggable = true;
    private boolean dragging;
    private double dragOffsetX;
    private double dragOffsetY;

    /**
     * 构造标题栏。
     *
     * <p>公开构造便于业务 {@code extends TitleBarAnt}；常规入口是 {@link #create()}。</p>
     */
    public TitleBarAnt() {
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add(JfxStyles.TITLE_BAR);
        borderBottom(); // 默认底部分隔线（Win11 标题栏与内容区同色，靠 1px 线分层）

        titleLabel = LabelAnt.create("").build();
        titleLabel.getStyleClass().add(JfxStyles.TITLE_BAR_TITLE);

        spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        centerSpacer = new Region();
        HBox.setHgrow(centerSpacer, Priority.ALWAYS);

        controls = WindowControlAnt.create().build();

        addEventHandler(MouseEvent.MOUSE_PRESSED, this::handlePressed);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleDragged);
        addEventHandler(MouseEvent.MOUSE_RELEASED, e -> dragging = false);
        addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClicked);
    }

    /** 工厂入口。 */
    public static TitleBarAnt create() {
        return new TitleBarAnt();
    }

    // ============================================================
    // 配置
    // ============================================================

    /** 标题文本（{@code null} → 空串）。想跟随窗口标题请显式传 {@code stage.getTitle()}。 */
    public TitleBarAnt title(String title) {
        titleLabel.setText(TextUtils.safeText(title));
        return this;
    }

    /**
     * 窗口图标（可为 {@code null}）。图标节点会自动挂 {@code .jfx-title-bar-icon}
     * 类以获得主题前景色（Shape 走 {@code -fx-fill}，文本型图标走 {@code -fx-text-fill}）。
     */
    public TitleBarAnt icon(Node icon) {
        this.icon = icon;
        return this;
    }

    /**
     * 绑定目标窗口 —— 同时接线窗口按钮（{@link WindowControlAnt#target}）、
     * 拖动与双击最大化。
     */
    public TitleBarAnt stage(Stage stage) {
        this.stage = stage;
        controls.target(stage);
        return this;
    }

    /**
     * 左侧自定义节点（标题右侧、弹性空隙之前）。多次调用累加。
     *
     * <p>典型内容：{@code ToolBarAnt} / {@code HBarAnt}（操作按钮、搜索框、面包屑）。
     * 标题为空时这些节点直接占据左侧。</p>
     */
    public TitleBarAnt left(Node... nodes) {
        addAll(leftNodes, nodes);
        return this;
    }

    /**
     * 中间自定义节点（左右各一个弹性空隙，真正居中）。传空表示不使用中段，
     * 退化为「左 + spacer + 右」二段式。多次调用累加。
     */
    public TitleBarAnt center(Node... nodes) {
        addAll(centerNodes, nodes);
        return this;
    }

    /**
     * 右侧自定义节点（位于窗口按钮之前）。多次调用累加。
     *
     * <p>窗口按钮组永远贴最右，{@code right(...)} 放在它左边。</p>
     */
    public TitleBarAnt right(Node... nodes) {
        addAll(rightNodes, nodes);
        return this;
    }

    /** 整组窗口按钮显示开关（默认显示）。关闭后不占位，整条右侧可自由使用。 */
    public TitleBarAnt windowControls(boolean visible) {
        controls.setVisible(visible);
        controls.setManaged(visible);
        return this;
    }

    /**
     * 窗口按钮组（永不为 {@code null}）。用于调整可见性，例如固定尺寸窗口：
     * <pre>{@code titleBar.controls().maximizeVisible(false);}</pre>
     */
    public WindowControlAnt controls() {
        return controls;
    }

    /** 是否允许拖动/双击最大化（默认开启）。关闭后标题栏只是普通横条。 */
    public TitleBarAnt draggable(boolean draggable) {
        this.draggable = draggable;
        if (!draggable) {
            dragging = false;
        }
        return this;
    }

    // ============================================================
    // 构建
    // ============================================================

    /**
     * 组装 [图标?] + [标题?] + [left...] + spacer + [center...] + [right...] + 窗口按钮。
     * 可重复调用，不累积子节点。
     */
    @Override
    public TitleBarAnt build() {
        List<Node> children = new ArrayList<>(
                6 + leftNodes.size() + centerNodes.size() + rightNodes.size());

        if (icon != null) {
            if (!icon.getStyleClass().contains(JfxStyles.TITLE_BAR_ICON)) {
                icon.getStyleClass().add(JfxStyles.TITLE_BAR_ICON);
            }
            children.add(icon);
        }

        // 标题为空 → 不占位（但保留为子节点，lookup / 后续改文案仍然有效）
        boolean hasTitle = titleLabel.getText() != null && !titleLabel.getText().isEmpty();
        titleLabel.setVisible(hasTitle);
        titleLabel.setManaged(hasTitle);
        children.add(titleLabel);

        children.addAll(leftNodes);
        children.add(spacer);

        if (!centerNodes.isEmpty()) {
            children.addAll(centerNodes);
            children.add(centerSpacer);
        }

        children.addAll(rightNodes);
        children.add(controls);

        getChildren().setAll(children);
        return this;
    }

    // ============================================================
    // 拖动 / 双击
    // ============================================================

    private static void addAll(List<Node> target, Node[] nodes) {
        if (nodes == null) {
            return;
        }
        for (Node n : nodes) {
            if (n != null) {
                target.add(n);
            }
        }
    }

    private void handlePressed(MouseEvent e) {
        dragging = false;
        if (!draggable || stage == null || e.getButton() != MouseButton.PRIMARY) {
            return;
        }
        if (isInteractive(e.getTarget())) {
            return;
        }
        dragOffsetX = e.getScreenX() - stage.getX();
        dragOffsetY = e.getScreenY() - stage.getY();
        dragging = true;
    }

    private void handleDragged(MouseEvent e) {
        if (!dragging || stage == null) {
            return;
        }
        stage.setX(e.getScreenX() - dragOffsetX);
        stage.setY(e.getScreenY() - dragOffsetY);
    }

    private void handleClicked(MouseEvent e) {
        if (!draggable || stage == null || e.getButton() != MouseButton.PRIMARY) {
            return;
        }
        if (e.getClickCount() < 2 || isInteractive(e.getTarget())) {
            return;
        }
        if (stage.isResizable()) {
            stage.setMaximized(!stage.isMaximized());
        }
    }

    /**
     * 按下点是否落在「吃鼠标事件」的控件上。
     *
     * <p>不能用 {@code instanceof Control} —— {@link javafx.scene.control.Label} 也是 Control，
     * 而标题文字区域恰恰是最常用的拖动把手。这里只认真正会消费点击的控件族
     * （按钮 / 文本输入 / 下拉），以及窗口控制按钮。</p>
     */
    private boolean isInteractive(Object target) {
        if (!(target instanceof Node node)) {
            return false;
        }
        for (Node n = node; n != null && n != this; n = n.getParent()) {
            if (n instanceof ButtonBase || n instanceof TextInputControl || n instanceof ComboBoxBase) {
                return true;
            }
            if (n.getStyleClass().contains(JfxStyles.WINDOW_CONTROL)
                    || n.getStyleClass().contains(JfxStyles.WINDOW_CONTROL_BUTTON)) {
                return true;
            }
        }
        return false;
    }
}
