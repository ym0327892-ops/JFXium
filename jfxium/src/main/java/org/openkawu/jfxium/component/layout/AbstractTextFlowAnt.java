package org.openkawu.jfxium.component.layout;

import javafx.scene.Node;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * TextFlow 系继承式组件的自类型基类。
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractTextFlowAnt<SELF extends AbstractTextFlowAnt<SELF>>
        extends TextFlow implements LayoutCommon<SELF> {

    protected AbstractTextFlowAnt() {
        super();
    }

    protected AbstractTextFlowAnt(Node... children) {
        super();
        addChildren(children);
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    /** 批量添加子节点（追加，不清旧）。null 节点会被过滤。 */
    public SELF children(Node... nodes) {
        addChildren(nodes);
        return self();
    }

    /** 设置行间距。 */
    public SELF lineSpacing(double lineSpacing) {
        setLineSpacing(Math.max(0, lineSpacing));
        return self();
    }

    /** 设置文本对齐方式。 */
    public SELF textAlignment(TextAlignment alignment) {
        if (alignment != null) {
            setTextAlignment(alignment);
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
