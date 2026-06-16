package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * VBox 系继承式组件的自类型基类。
 *
 * <p>公开的 {@link VBoxAnt} 保持无泛型 API；需要扩展纵向布局并保留子类链式返回时，
 * 可以直接继承本类并传入真实子类类型。</p>
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractVBoxAnt<SELF extends AbstractVBoxAnt<SELF>> extends VBox implements LayoutCommon<SELF> {

    protected AbstractVBoxAnt() {
        super();
    }

    protected AbstractVBoxAnt(Node... children) {
        super();
        addChildren(children);
    }

    protected AbstractVBoxAnt(double spacing) {
        super(clampSpacing(spacing));
    }

    protected AbstractVBoxAnt(double spacing, Node... children) {
        super(clampSpacing(spacing));
        addChildren(children);
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    /** 设置子节点之间的垂直间距。 */
    public SELF spacing(double spacing) {
        setSpacing(clampSpacing(spacing));
        return self();
    }

    /** 设置子节点对齐方式。 */
    public SELF align(Pos alignment) {
        if (alignment != null) {
            setAlignment(alignment);
        }
        return self();
    }

    /** 添加子节点（追加，不清旧）。null 节点会被过滤。 */
    public SELF children(Node... nodes) {
        addChildren(nodes);
        return self();
    }

    /** VBox 是否让子节点水平撑满（默认 true）。 */
    public SELF fillWidth(boolean fill) {
        setFillWidth(fill);
        return self();
    }

    /** 给指定子节点设置垂直拉伸优先级。 */
    public SELF vgrow(Node child, Priority priority) {
        if (child != null && priority != null) {
            VBox.setVgrow(child, priority);
        }
        return self();
    }

    /** 给指定子节点设置外边距。 */
    public SELF margin(Node child, Insets margin) {
        if (child != null) {
            VBox.setMargin(child, margin);
        }
        return self();
    }

    /** Builder 模式终结调用——返回自身。 */
    public SELF build() {
        return self();
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

    private static double clampSpacing(double spacing) {
        return Math.max(0, spacing);
    }
}
