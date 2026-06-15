package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

/**
 * FlowPaneAnt - 继承式 FlowPane 容器（M19.36 引入）。
 *
 * <p>流式布局（自动换行）的双工厂模式——典型场景：标签云、自适应按钮组、
 * 不定数量子节点 wrap 排列。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * FlowPaneAnt tagCloud = FlowPaneAnt.create()
 *     .hgap(8).vgap(8)
 *     .children(tag1, tag2, tag3, tag4, ...);
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~120 行重复模板代码，行为 100% 等价原 FlowPaneAnt）</li>
 *   <li><b>双重身份</b>：是 FlowPane 也是工厂——继承自 {@link FlowPane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 * </ul>
 */
public class FlowPaneAnt extends FlowPane implements LayoutCommon<FlowPaneAnt> {

    /** 工厂入口。 */
    public static FlowPaneAnt create() {
        return new FlowPaneAnt();
    }

    /** 工厂入口（带初始子节点）。 */
    public static FlowPaneAnt create(Node... children) {
        return new FlowPaneAnt(children);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public FlowPaneAnt() {
        super();
    }

    public FlowPaneAnt(Node... children) {
        super();
        this.children(children);
    }

    public FlowPaneAnt(Orientation orientation) {
        super(orientation);
    }

    public FlowPaneAnt(double hgap, double vgap) {
        super(hgap, vgap);
    }

    // ============================================================
    // 流式 API（FlowPane 特有业务方法）
    // ============================================================

    /** 子节点之间的水平间距。 */
    public FlowPaneAnt hgap(double hgap) {
        setHgap(hgap);
        return this;
    }

    /** 子节点之间的垂直间距。 */
    public FlowPaneAnt vgap(double vgap) {
        setVgap(vgap);
        return this;
    }

    /** 同时设置 hgap 和 vgap。 */
    public FlowPaneAnt gap(double gap) {
        setHgap(gap);
        setVgap(gap);
        return this;
    }

    /** 间距（等同 gap，与 HBoxAnt/VBoxAnt 命名统一）。 */
    public FlowPaneAnt spacing(double spacing) {
        setHgap(spacing);
        setVgap(spacing);
        return this;
    }

    /** 设置子节点排列方向。 */
    public FlowPaneAnt orientation(Orientation orientation) {
        if (orientation != null) {
            setOrientation(orientation);
        }
        return this;
    }

    /** 设置子节点对齐方式。 */
    public FlowPaneAnt align(Pos alignment) {
        if (alignment != null) {
            setAlignment(alignment);
        }
        return this;
    }

    /** 批量添加子节点（null 节点会被过滤）。 */
    public FlowPaneAnt children(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) getChildren().add(n);
            }
        }
        return this;
    }

    /** 触发换行的首选宽度（水平方向时）或高度（垂直方向时）。 */
    public FlowPaneAnt prefWrapLength(double length) {
        setPrefWrapLength(length);
        return this;
    }

    /** 行内节点的垂直对齐方式（水平方向时生效）。 */
    public FlowPaneAnt rowValignment(javafx.geometry.VPos vpos) {
        if (vpos != null) {
            setRowValignment(vpos);
        }
        return this;
    }

    /** 列内节点的水平对齐方式（垂直方向时生效）。 */
    public FlowPaneAnt columnHalignment(javafx.geometry.HPos hpos) {
        if (hpos != null) {
            setColumnHalignment(hpos);
        }
        return this;
    }

    /** 给指定子节点设置外边距。 */
    public FlowPaneAnt margin(Node child, Insets margin) {
        if (child != null) {
            FlowPane.setMargin(child, margin);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子、方向性边框线、尺寸、高频节点属性统一继承自
    // LayoutCommon<FlowPaneAnt> 默认实现
    // （节省 ~120 行重复模板代码，行为 100% 等价原 FlowPaneAnt）
    // ============================================================

    /** Builder 模式终结调用——返回自身。详见 {@link VBoxAnt#build()}。 */
    public FlowPaneAnt build() {
        return this;
    }
}
