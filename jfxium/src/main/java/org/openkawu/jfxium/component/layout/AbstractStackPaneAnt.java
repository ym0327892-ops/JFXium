package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * StackPane 系继承式组件的自类型基类。
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractStackPaneAnt<SELF extends AbstractStackPaneAnt<SELF>>
        extends StackPane implements LayoutCommon<SELF> {

    protected AbstractStackPaneAnt() {
        super();
    }

    protected AbstractStackPaneAnt(Node... children) {
        super();
        addChildren(children);
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    /** 设置子节点对齐方式（StackPane 默认 CENTER）。 */
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

    /** 设置指定子节点在 StackPane 内的对齐方式（覆盖容器级 align）。 */
    public SELF childAlign(Node child, Pos alignment) {
        if (child != null && alignment != null) {
            StackPane.setAlignment(child, alignment);
        }
        return self();
    }

    /** 给指定子节点设置外边距。 */
    public SELF margin(Node child, Insets margin) {
        if (child != null) {
            StackPane.setMargin(child, margin);
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
}
