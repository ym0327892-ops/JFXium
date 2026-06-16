package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

/**
 * SplitPane 系继承式组件的自类型基类。
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractSplitPaneAnt<SELF extends AbstractSplitPaneAnt<SELF>>
        extends SplitPane implements LayoutCommon<SELF> {

    protected AbstractSplitPaneAnt() {
        super();
    }

    protected AbstractSplitPaneAnt(Node... items) {
        super();
        addItems(items);
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    public SELF direction(SplitPaneAnt.Direction direction) {
        if (direction != null) {
            setOrientation(direction == SplitPaneAnt.Direction.VERTICAL
                    ? Orientation.VERTICAL : Orientation.HORIZONTAL);
        }
        return self();
    }

    /** 添加单个窗格。 */
    public SELF item(Node item) {
        if (item != null) {
            getItems().add(item);
        }
        return self();
    }

    /** 批量添加窗格。 */
    public SELF items(Node... items) {
        addItems(items);
        return self();
    }

    /** 设置分隔条位置（0.0 ~ 1.0 比例，可设多个；items 数 - 1 个分隔条）。 */
    public SELF dividerPositions(double... positions) {
        if (positions != null && positions.length > 0) {
            double[] safePositions = sanitizeDividerPositions(positions);
            if (safePositions.length > 0) {
                setDividerPositions(safePositions);
            }
        }
        return self();
    }

    /** 设置某个子节点是否随父容器调整大小。 */
    public SELF resizableWithParent(Node node, boolean resizable) {
        if (node != null) {
            SplitPane.setResizableWithParent(node, resizable);
        }
        return self();
    }

    /** Builder 模式终结调用——返回自身。 */
    public SELF build() {
        return self();
    }

    protected static double[] sanitizeDividerPositions(double[] positions) {
        return java.util.Arrays.stream(positions)
                .filter(Double::isFinite)
                .map(position -> Math.max(0, Math.min(1, position)))
                .toArray();
    }

    private void addItems(Node... items) {
        if (items != null) {
            for (Node node : items) {
                if (node != null) {
                    getItems().add(node);
                }
            }
        }
    }
}
