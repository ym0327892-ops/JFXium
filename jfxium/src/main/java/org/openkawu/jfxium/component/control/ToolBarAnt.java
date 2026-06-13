package org.openkawu.jfxium.component.control;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 工具栏组件 - 对标 Ant Design 无直接对应，补齐 PC 桌面软件刚需。
 *
 * <p><b>定位</b>：窗口顶部可定制工具栏，支持图标按钮组、溢出菜单、
 * 可拖拽自定义。常用于 IDE、Office 风格应用的顶部操作区。</p>
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
 * <h2>用法</h2>
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
 */
public class ToolBarAnt extends ToolBar {

    // ============================================================
    // 工厂入口
    // ============================================================

    public static Builder create() {
        return new Builder();
    }

    // ============================================================
    // 构造函数（公开，便于业务继承）
    // ============================================================

    public ToolBarAnt() {
        super();
        getStyleClass().add(JfxStyles.TOOL_BAR);
    }

    // ============================================================
    // 运行时方法（构建后动态增删）
    // ============================================================

    /** 运行时追加图标按钮。 */
    public void addButton(Node icon, String tooltip, Runnable action) {
        getItems().add(createButton(icon, tooltip, action));
    }

    /** 运行时追加带文本按钮。 */
    public void addButton(String text, Node icon, String tooltip, Runnable action) {
        getItems().add(createButton(text, icon, tooltip, action));
    }

    /** 运行时追加任意节点。 */
    public void addItem(Node node) {
        getItems().add(node);
    }

    /** 运行时插入分隔线。 */
    public void addDivider() {
        Region div = new Region();
        div.getStyleClass().add(JfxStyles.TOOL_BAR_ITEM);
        if (getOrientation() == Orientation.VERTICAL) {
            div.setPrefSize(24, 1);
        } else {
            div.setPrefSize(1, 24);
        }
        getItems().add(div);
    }

    /** 运行时追加弹性填充。 */
    public void addSpacer() {
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

    /** 清空所有项。 */
    public void clear() {
        getItems().clear();
    }

    // ============================================================
    // 内部工厂方法
    // ============================================================

    private static Button createButton(Node icon, String tooltip, Runnable action) {
        Button btn = new Button();
        btn.setGraphic(icon);
        btn.getStyleClass().add(JfxStyles.TOOL_BAR_ITEM);
        if (tooltip != null && !tooltip.isEmpty()) {
            btn.setTooltip(new javafx.scene.control.Tooltip(tooltip));
        }
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        return btn;
    }

    private static Button createButton(String text, Node icon, String tooltip, Runnable action) {
        Button btn = new Button(text);
        if (icon != null) {
            btn.setGraphic(icon);
        }
        btn.getStyleClass().add(JfxStyles.TOOL_BAR_ITEM);
        if (tooltip != null && !tooltip.isEmpty()) {
            btn.setTooltip(new javafx.scene.control.Tooltip(tooltip));
        }
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        return btn;
    }

    // ============================================================
    // Builder
    // ============================================================

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Orientation orientation = Orientation.HORIZONTAL;
        private final List<Node> items = new ArrayList<>();

        /** 设置工具栏方向。 */
        public Builder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        /** 添加图标按钮。 */
        public Builder button(Node icon, String tooltip, Runnable action) {
            items.add(createButton(icon, tooltip, action));
            return this;
        }

        /** 添加带文本的按钮。 */
        public Builder button(String text, Node icon, String tooltip, Runnable action) {
            items.add(createButton(text, icon, tooltip, action));
            return this;
        }

        /** 添加任意节点。 */
        public Builder item(Node node) {
            items.add(node);
            return this;
        }

        /** 插入竖向分隔线。 */
        public Builder divider() {
            Region div = new Region();
            div.getStyleClass().add(JfxStyles.TOOL_BAR_ITEM);
            // orientation 只在 build() 时被应用到 toolbar.setOrientation()，
            // divider 尺寸需在构建时决定，这里用 Builder 记录的 orientation
            if (orientation == Orientation.VERTICAL) {
                div.setPrefSize(24, 1);
            } else {
                div.setPrefSize(1, 24);
            }
            items.add(div);
            return this;
        }

        /** 弹性填充，将后续项推到对侧。 */
        public Builder spacer() {
            Region spacer = new Region();
            if (orientation == Orientation.VERTICAL) {
                VBox.setVgrow(spacer, Priority.ALWAYS);
                spacer.setPrefHeight(Region.USE_COMPUTED_SIZE);
            } else {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                spacer.setPrefWidth(Region.USE_COMPUTED_SIZE);
            }
            items.add(spacer);
            return this;
        }

        /** 构建 ToolBarAnt。 */
        public ToolBarAnt build() {
            ToolBarAnt toolbar = new ToolBarAnt();
            toolbar.setOrientation(orientation);
            toolbar.getItems().addAll(items);
            applyStyles(toolbar);
            return toolbar;
        }
    }
}
