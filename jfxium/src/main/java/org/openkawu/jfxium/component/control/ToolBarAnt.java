package org.openkawu.jfxium.component.control;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * JFXium 工具栏组件（M19.50 重构）— 包装 JavaFX {@link ToolBar}（继承式 + 双工厂模式）。
 *
 * <p><b>定位</b>：窗口顶部可定制工具栏，支持图标按钮组、溢出菜单、可拖拽自定义。
 * 常用于 IDE、Office 风格应用的顶部操作区。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>按钮项</b>：button(icon, tooltip, action) 添加图标按钮</li>
 *   <li><b>自定义节点</b>：item(Node) 添加任意节点（下拉框、分隔线等）</li>
 *   <li><b>分隔线</b>：divider() 插入竖向分隔线</li>
 *   <li><b>弹性填充</b>：spacer() 将右侧内容推到最右</li>
 *   <li><b>方向</b>：HORIZONTAL（默认）/ VERTICAL</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#TOOL_BAR} 系列 LESS 样式</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式</h2>
 * <pre>{@code
 * // 水平工具栏
 * ToolBarAnt toolbar = ToolBarAnt.create()
 *     .button(newIcon, "新建", () -> newFile())
 *     .button(openIcon, "打开", () -> openFile())
 *     .button(saveIcon, "保存", () -> saveFile())
 *     .divider()
 *     .spacer()
 *     .button(settingsIcon, "设置", () -> openSettings())
 *     .build();
 *
 * // 垂直侧边工具栏
 * ToolBarAnt vtoolbar = ToolBarAnt.create()
 *     .orientation(Orientation.VERTICAL)
 *     .button(selectIcon, "选择", () -> setTool("select"))
 *     .button(penIcon, "画笔", () -> setTool("pen"))
 *     .build();
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class EditorToolBar extends ToolBarAnt {
 *     public EditorToolBar() {
 *         button(newIcon, "新建", () -> newFile());
 *         button(openIcon, "打开", () -> openFile());
 *         divider();
 *         button(saveIcon, "保存", () -> saveFile());
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link ToolBar} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>运行时 + 构建时双 API</b>：{@code addXxx} 运行时追加，{@code xxx} 构建时链式</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class ToolBarAnt extends ToolBar
        implements LayoutCommon<ToolBarAnt>, DisabledSupport<ToolBarAnt> {

    /** divider 长度（水平 toolbar 时高 1×宽 24，垂直 toolbar 时高 24×宽 1）。 */
    private static final double DIVIDER_LENGTH = 24;
    /** divider 线宽。 */
    private static final double DIVIDER_THICKNESS = 1;

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（默认水平）。 */
    public static ToolBarAnt create() {
        return new ToolBarAnt();
    }

    /** 工厂入口（指定方向）。 */
    public static ToolBarAnt create(Orientation orientation) {
        ToolBarAnt tb = new ToolBarAnt();
        if (orientation != null) {
            tb.setOrientation(orientation);
        }
        return tb;
    }

    // ============================================================
    // 构造函数（公开，便于业务继承）
    // ============================================================

    public ToolBarAnt() {
        super();
        getStyleClass().add(JfxStyles.TOOL_BAR);
    }

    // ============================================================
    // 流式 API（构建时）
    // ============================================================

    /** 设置工具栏方向。 */
    public ToolBarAnt orientation(Orientation orientation) {
        if (orientation != null) {
            setOrientation(orientation);
        }
        return this;
    }

    /** 添加图标按钮。 */
    public ToolBarAnt button(Node icon, String tooltip, Runnable action) {
        getItems().add(createIconButton(icon, tooltip, action));
        return this;
    }

    /** 添加带文本的按钮。 */
    public ToolBarAnt button(String text, Node icon, String tooltip, Runnable action) {
        getItems().add(createTextButton(text, icon, tooltip, action));
        return this;
    }

    /** 添加任意节点（下拉框、分隔线等）。 */
    public ToolBarAnt item(Node node) {
        if (node != null) {
            getItems().add(node);
        }
        return this;
    }

    /** 插入分隔线。 */
    public ToolBarAnt divider() {
        addDividerRegion();
        return this;
    }

    /** 弹性填充，将后续项推到对侧。 */
    public ToolBarAnt spacer() {
        addSpacerRegion();
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 运行时方法（构建后动态增删）
    // ============================================================

    /** 运行时追加图标按钮。 */
    public void addButton(Node icon, String tooltip, Runnable action) {
        getItems().add(createIconButton(icon, tooltip, action));
    }

    /** 运行时追加带文本按钮。 */
    public void addButton(String text, Node icon, String tooltip, Runnable action) {
        getItems().add(createTextButton(text, icon, tooltip, action));
    }

    /** 运行时追加任意节点。 */
    public void addItem(Node node) {
        getItems().add(node);
    }

    /** 运行时插入分隔线。委托 {@link #addDividerRegion()}。 */
    public void addDivider() {
        addDividerRegion();
    }

    /** 运行时追加弹性填充。委托 {@link #addSpacerRegion()}。 */
    public void addSpacer() {
        addSpacerRegion();
    }

    /** 清空所有项。 */
    public void clear() {
        getItems().clear();
    }

    // ============================================================
    // 内部布局 helper（消除 divider/spacer 重复）
    // ============================================================

    /** 创建并添加一个 divider Region。 */
    private void addDividerRegion() {
        Region div = new Region();
        div.getStyleClass().add(JfxStyles.TOOL_BAR_ITEM);
        if (getOrientation() == Orientation.VERTICAL) {
            div.setPrefSize(DIVIDER_LENGTH, DIVIDER_THICKNESS);
        } else {
            div.setPrefSize(DIVIDER_THICKNESS, DIVIDER_LENGTH);
        }
        getItems().add(div);
    }

    /** 创建并添加一个弹性填充 Region。 */
    private void addSpacerRegion() {
        Region spacer = new Region();
        if (getOrientation() == Orientation.VERTICAL) {
            VBox.setVgrow(spacer, Priority.ALWAYS);
            spacer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        } else {
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }
        getItems().add(spacer);
    }

    // ============================================================
    // 内部按钮工厂（统一走 ButtonAnt，享受主题化继承链）
    // ============================================================

    private static Button createIconButton(Node icon, String tooltip, Runnable action) {
        return createButton(null, icon, tooltip, action);
    }

    private static Button createTextButton(String text, Node icon, String tooltip, Runnable action) {
        return createButton(text, icon, tooltip, action);
    }

    /**
     * 统一按钮工厂：text 为 null 时创建纯图标按钮，否则创建带文本按钮。
     * 委托 {@link ButtonAnt} 享受主题化继承链，额外挂 {@link JfxStyles#TOOL_BAR_ITEM}。
     */
    private static Button createButton(String text, Node icon, String tooltip, Runnable action) {
        ButtonAnt btn = (text != null)
                ? ButtonAnt.create(TextUtils.safeText(text)).type(ButtonAnt.Type.TEXT)
                : ButtonAnt.create().type(ButtonAnt.Type.TEXT);
        if (icon != null) {
            btn.setGraphic(icon);
        }
        btn.getStyleClass().add(JfxStyles.TOOL_BAR_ITEM);
        if (tooltip != null && !tooltip.isEmpty()) {
            btn.setTooltip(TooltipAnt.create(tooltip).build());
        }
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        return btn;
    }

    // ============================================================
    // 构建
    // ============================================================

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>ToolBarAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public ToolBarAnt build() {
        return this;
    }
}