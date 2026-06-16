package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * HBox 系继承式组件的自类型基类。
 *
 * <p>给 {@code BarAnt} 这类「基于 HBox，但需要返回自身类型继续链式调用」的组件复用。
 * 公开的 {@link HBoxAnt} 仍保持无泛型 API，避免普通调用方被 SELF 泛型打扰。</p>
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractHBoxAnt<SELF extends AbstractHBoxAnt<SELF>> extends HBox implements LayoutCommon<SELF> {

    protected AbstractHBoxAnt() {
        super();
    }

    protected AbstractHBoxAnt(Node... children) {
        super();
        addChildren(children);
    }

    protected AbstractHBoxAnt(double spacing) {
        super(clampSpacing(spacing));
    }

    protected AbstractHBoxAnt(double spacing, Node... children) {
        super(clampSpacing(spacing));
        addChildren(children);
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    /** 设置子节点之间的水平间距。 */
    public SELF spacing(double spacing) {
        setSpacing(clampSpacing(spacing));
        return self();
    }

    /** 设置子节点对齐方式（默认 {@code CENTER_LEFT}）。 */
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

    /** HBox 是否让子节点垂直撑满（默认 true）。 */
    public SELF fillHeight(boolean fill) {
        setFillHeight(fill);
        return self();
    }

    /** 给指定子节点设置水平拉伸优先级。 */
    public SELF hgrow(Node child, Priority priority) {
        if (child != null && priority != null) {
            HBox.setHgrow(child, priority);
        }
        return self();
    }

    /** 给指定子节点设置外边距。 */
    public SELF margin(Node child, Insets margin) {
        if (child != null) {
            HBox.setMargin(child, margin);
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
