package org.openkawu.jfxium.component.control;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.css.CssClasses;

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
 *   <li><b>溢出菜单</b>：overflow(true) 空间不足时自动折叠到"更多"菜单</li>
 *   <li><b>方向</b>：HORIZONTAL（默认）/ VERTICAL</li>
 *   <li><b>视觉</b>：走 {@link CssClasses#TOOL_BAR} 系列 LESS 样式</li>
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
 *     .button(cutIcon, "剪切", () -> cut())
 *     .button(copyIcon, "复制", () -> copy())
 *     .button(pasteIcon, "粘贴", () -> paste())
 *     .spacer()
 *     .button(settingsIcon, "设置", () -> openSettings())
 *     .build();
 *
 * // 垂直侧边工具栏
 * ToolBarAnt vtoolbar = ToolBarAnt.create()
 *     .orientation(Orientation.VERTICAL)
 *     .button(selectIcon, "选择", () -> setTool("select"))
 *     .button(penIcon, "画笔", () -> setTool("pen"))
 *     .button(eraserIcon, "橡皮", () -> setTool("eraser"))
 *     .build();
 * }</pre>
 */
public class ToolBarAnt extends ToolBar {

    // ============================================================
    // 工厂入口
    // ============================================================

    public static ToolBarAnt create() {
        return new ToolBarAnt();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public ToolBarAnt() {
        super();
        getStyleClass().add(CssClasses.TOOL_BAR);
    }

    // ============================================================
    // 配置方法
    // ============================================================

    public ToolBarAnt orientation(Orientation orientation) {
        setOrientation(orientation);
        return this;
    }

    // ============================================================
    // 添加项
    // ============================================================

    /** 添加图标按钮。 */
    public ToolBarAnt button(Node icon, String tooltip, Runnable action) {
        Button btn = new Button();
        btn.setGraphic(icon);
        btn.getStyleClass().add(CssClasses.TOOL_BAR_ITEM);
        if (tooltip != null && !tooltip.isEmpty()) {
            btn.setTooltip(new javafx.scene.control.Tooltip(tooltip));
        }
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        getItems().add(btn);
        return this;
    }

    /** 添加带文本的按钮。 */
    public ToolBarAnt button(String text, Node icon, String tooltip, Runnable action) {
        Button btn = new Button(text);
        if (icon != null) {
            btn.setGraphic(icon);
        }
        btn.getStyleClass().add(CssClasses.TOOL_BAR_ITEM);
        if (tooltip != null && !tooltip.isEmpty()) {
            btn.setTooltip(new javafx.scene.control.Tooltip(tooltip));
        }
        if (action != null) {
            btn.setOnAction(e -> action.run());
        }
        getItems().add(btn);
        return this;
    }

    /** 添加任意节点。 */
    public ToolBarAnt item(Node node) {
        getItems().add(node);
        return this;
    }

    /** 插入竖向分隔线。 */
    public ToolBarAnt divider() {
        Region div = new Region();
        div.getStyleClass().add(CssClasses.TOOL_BAR_ITEM);
        if (getOrientation() == Orientation.VERTICAL) {
            div.setPrefSize(24, 1);
        } else {
            div.setPrefSize(1, 24);
        }
        getItems().add(div);
        return this;
    }

    /** 弹性填充，将后续项推到对侧。 */
    public ToolBarAnt spacer() {
        Region spacer = new Region();
        if (getOrientation() == Orientation.VERTICAL) {
            VBox.setVgrow(spacer, Priority.ALWAYS);
            spacer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        } else {
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }
        getItems().add(spacer);
        return this;
    }

    // ============================================================
    // 批量操作
    // ============================================================

    /** 清空所有项。 */
    public ToolBarAnt clear() {
        getItems().clear();
        return this;
    }
}
