package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * BorderPane 系继承式组件的自类型基类。
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractBorderPaneAnt<SELF extends AbstractBorderPaneAnt<SELF>>
        extends BorderPane implements LayoutCommon<SELF> {

    protected AbstractBorderPaneAnt() {
        super();
    }

    protected AbstractBorderPaneAnt(Node center) {
        super(center);
    }

    protected AbstractBorderPaneAnt(Node center, Node top, Node right, Node bottom, Node left) {
        super(center, top, right, bottom, left);
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    /** 设置顶部节点。 */
    public SELF top(Node node) {
        setTop(node);
        return self();
    }

    /** 设置中心节点。 */
    public SELF center(Node node) {
        setCenter(node);
        return self();
    }

    /** 设置底部节点。 */
    public SELF bottom(Node node) {
        setBottom(node);
        return self();
    }

    /** 设置左侧节点。 */
    public SELF left(Node node) {
        setLeft(node);
        return self();
    }

    /** 设置右侧节点。 */
    public SELF right(Node node) {
        setRight(node);
        return self();
    }

    /**
     * BorderPane 只能稳定承载五个命名区域。多节点兼容模式会把节点顺序打包进 center 区。
     */
    @Deprecated(forRemoval = false)
    public SELF children(Node... nodes) {
        if (nodes == null) {
            return self();
        }

        Node onlyNode = null;
        int nonNullCount = 0;
        for (Node node : nodes) {
            if (node != null) {
                onlyNode = node;
                nonNullCount++;
            }
        }

        if (nonNullCount == 0) {
            return self();
        }
        if (nonNullCount == 1) {
            setCenter(onlyNode);
            return self();
        }

        VBox fallbackCenter = new VBox();
        fallbackCenter.setSpacing(0);
        for (Node node : nodes) {
            if (node != null) {
                fallbackCenter.getChildren().add(node);
            }
        }
        setCenter(fallbackCenter);
        return self();
    }

    /** 设置指定子节点在 BorderPane 内的对齐方式。 */
    public SELF align(Node child, Pos alignment) {
        if (child != null && alignment != null) {
            BorderPane.setAlignment(child, alignment);
        }
        return self();
    }

    /** 给指定子节点设置外边距。 */
    public SELF margin(Node child, Insets margin) {
        if (child != null) {
            BorderPane.setMargin(child, margin);
        }
        return self();
    }

    /** Builder 模式终结调用——返回自身。 */
    public SELF build() {
        return self();
    }
}
