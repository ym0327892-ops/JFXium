package org.openkawu.jfxium.component.layout;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

/**
 * FlowPane 系继承式组件的自类型基类。
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractFlowPaneAnt<SELF extends AbstractFlowPaneAnt<SELF>>
        extends FlowPane implements LayoutCommon<SELF> {

    protected AbstractFlowPaneAnt() {
        super();
    }

    protected AbstractFlowPaneAnt(Node... children) {
        super();
        addChildren(children);
    }

    protected AbstractFlowPaneAnt(Orientation orientation) {
        super(orientation == null ? Orientation.HORIZONTAL : orientation);
    }

    protected AbstractFlowPaneAnt(double hgap, double vgap) {
        super(clampGap(hgap), clampGap(vgap));
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    /** 子节点之间的水平间距。 */
    public SELF hgap(double hgap) {
        setHgap(clampGap(hgap));
        return self();
    }

    /** 子节点之间的垂直间距。 */
    public SELF vgap(double vgap) {
        setVgap(clampGap(vgap));
        return self();
    }

    /** 同时设置 hgap 和 vgap。 */
    public SELF gap(double gap) {
        double safeGap = clampGap(gap);
        setHgap(safeGap);
        setVgap(safeGap);
        return self();
    }

    /** 间距（等同 gap，与 HBoxAnt/VBoxAnt 命名统一）。 */
    public SELF spacing(double spacing) {
        return gap(spacing);
    }

    /** 设置子节点排列方向。 */
    public SELF orientation(Orientation orientation) {
        if (orientation != null) {
            setOrientation(orientation);
        }
        return self();
    }

    /** 设置子节点对齐方式。 */
    public SELF align(Pos alignment) {
        if (alignment != null) {
            setAlignment(alignment);
        }
        return self();
    }

    /** 批量添加子节点（null 节点会被过滤）。 */
    public SELF children(Node... nodes) {
        addChildren(nodes);
        return self();
    }

    /** 触发换行的首选宽度（水平方向时）或高度（垂直方向时）。 */
    public SELF prefWrapLength(double length) {
        setPrefWrapLength(Math.max(0, length));
        return self();
    }

    /** 行内节点的垂直对齐方式（水平方向时生效）。 */
    public SELF rowValignment(VPos vpos) {
        if (vpos != null) {
            setRowValignment(vpos);
        }
        return self();
    }

    /** 列内节点的水平对齐方式（垂直方向时生效）。 */
    public SELF columnHalignment(HPos hpos) {
        if (hpos != null) {
            setColumnHalignment(hpos);
        }
        return self();
    }

    /** 给指定子节点设置外边距。 */
    public SELF margin(Node child, Insets margin) {
        if (child != null) {
            FlowPane.setMargin(child, margin);
        }
        return self();
    }

    /** Builder 模式终结调用——返回自身。 */
    public SELF build() {
        return self();
    }

    protected static double clampGap(double gap) {
        return Double.isFinite(gap) ? Math.max(0, gap) : 0;
    }

    private void addChildren(Node... nodes) {
        if (nodes != null) {
            for (Node node : nodes) {
                if (node != null) {
                    getChildren().add(node);
                }
            }
        }
    }
}
