package org.openkawu.jfxium.core.container;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

/**
 * GridPane 容器构建器。
 * 用于快速创建网格布局容器。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * GridPane grid = GridPaneBuilder.create()
 *     .cols(2)
 *     .gap(8, 16)
 *     .children(label1, input1, label2, input2)
 *     .build();
 * }</pre>
 */
public class GridPaneBuilder {
    private int cols = 2;
    private double hgap = 8;
    private double vgap = 8;
    private Insets padding = Insets.EMPTY;
    private final List<Node> children = new ArrayList<>();
    private String style = "";
    private final List<String> styleClasses = new ArrayList<>();

    public static GridPaneBuilder create() {
        return new GridPaneBuilder();
    }

    public GridPaneBuilder cols(int cols) {
        this.cols = cols;
        return this;
    }

    public GridPaneBuilder gap(double gap) {
        this.hgap = gap;
        this.vgap = gap;
        return this;
    }

    public GridPaneBuilder gap(double hgap, double vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
        return this;
    }

    public GridPaneBuilder padding(double padding) {
        this.padding = new Insets(padding);
        return this;
    }

    public GridPaneBuilder padding(double top, double right, double bottom, double left) {
        this.padding = new Insets(top, right, bottom, left);
        return this;
    }

    public GridPaneBuilder children(Node... nodes) {
        for (Node node : nodes) {
            if (node != null) {
                this.children.add(node);
            }
        }
        return this;
    }

    public GridPaneBuilder style(String style) {
        this.style = style;
        return this;
    }

    public GridPaneBuilder styleClass(String styleClass) {
        if (styleClass != null && !styleClass.isEmpty()) {
            this.styleClasses.add(styleClass);
        }
        return this;
    }

    /** 批量挂多个 styleClass（M19.35 新增变长重载，跟 *Ant 风格一致）。 */
    public GridPaneBuilder styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) {
                if (c != null && !c.isEmpty()) this.styleClasses.add(c);
            }
        }
        return this;
    }

    /** 设置背景层级（M19.35 集成 Background 体系）。 */
    public GridPaneBuilder background(org.openkawu.jfxium.core.css.Background bg) {
        if (bg != null) this.styleClasses.add(bg.styleClass());
        return this;
    }

    public GridPane build() {
        // M19.36 委托 GridPaneAnt
        org.openkawu.jfxium.component.layout.GridPaneAnt grid =
                new org.openkawu.jfxium.component.layout.GridPaneAnt();
        grid.setHgap(hgap);
        grid.setVgap(vgap);
        grid.setPadding(padding);

        int row = 0;
        int col = 0;
        for (Node child : children) {
            grid.add(child, col, row);
            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
        }

        grid.getStyleClass().addAll(styleClasses);
        if (!style.isEmpty()) {
            grid.setStyle(style);
        }
        return grid;
    }
}
