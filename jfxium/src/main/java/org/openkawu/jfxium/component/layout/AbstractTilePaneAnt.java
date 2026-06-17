package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;

/**
 * TilePane 系继承式组件的自类型基类。
 *
 * @param <SELF> 真实子类类型
 */
public abstract class AbstractTilePaneAnt<SELF extends AbstractTilePaneAnt<SELF>>
        extends TilePane implements LayoutCommon<SELF> {

    protected AbstractTilePaneAnt() {
        super();
    }

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    public SELF prefColumns(int columns) {
        setPrefColumns(Math.max(1, columns));
        return self();
    }

    public SELF prefRows(int rows) {
        setPrefRows(Math.max(1, rows));
        return self();
    }

    public SELF orientation(Orientation orientation) {
        if (orientation != null) {
            setOrientation(orientation);
        }
        return self();
    }

    public SELF hgap(double gap) {
        setHgap(clampGap(gap));
        return self();
    }

    public SELF vgap(double gap) {
        setVgap(clampGap(gap));
        return self();
    }

    public SELF gap(double gap) {
        double safeGap = clampGap(gap);
        setHgap(safeGap);
        setVgap(safeGap);
        return self();
    }

    public SELF alignment(Pos pos) {
        if (pos != null) {
            setAlignment(pos);
        }
        return self();
    }

    public SELF children(Node... nodes) {
        addChildren(nodes);
        return self();
    }

    public SELF add(Node node) {
        if (node != null) {
            getChildren().add(node);
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
