package org.openkawu.jfxium.core.container;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;

/**
 * FlowPane 容器构建器。
 * 用于快速创建流式布局容器（自动换行）。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * FlowPane flow = FlowPaneBuilder.create()
 *     .gap(8)
 *     .align(Pos.CENTER)
 *     .children(tag1, tag2, tag3, tag4)
 *     .build();
 * }</pre>
 */
public class FlowPaneBuilder {
    private Orientation orientation = Orientation.HORIZONTAL;
    private double hgap = 8;
    private double vgap = 8;
    private Pos alignment = Pos.TOP_LEFT;
    private Insets padding = Insets.EMPTY;
    private final List<Node> children = new ArrayList<>();
    private String style = "";
    private final List<String> styleClasses = new ArrayList<>();

    public static FlowPaneBuilder create() {
        return new FlowPaneBuilder();
    }

    public FlowPaneBuilder orientation(Orientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public FlowPaneBuilder hgap(double hgap) {
        this.hgap = hgap;
        return this;
    }

    public FlowPaneBuilder vgap(double vgap) {
        this.vgap = vgap;
        return this;
    }

    public FlowPaneBuilder gap(double gap) {
        this.hgap = gap;
        this.vgap = gap;
        return this;
    }

    public FlowPaneBuilder gap(double hgap, double vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
        return this;
    }

    public FlowPaneBuilder align(Pos alignment) {
        this.alignment = alignment;
        return this;
    }

    public FlowPaneBuilder padding(double padding) {
        this.padding = new Insets(padding);
        return this;
    }

    public FlowPaneBuilder padding(double top, double right, double bottom, double left) {
        this.padding = new Insets(top, right, bottom, left);
        return this;
    }

    public FlowPaneBuilder children(Node... nodes) {
        for (Node node : nodes) {
            if (node != null) {
                this.children.add(node);
            }
        }
        return this;
    }

    public FlowPaneBuilder style(String style) {
        this.style = style;
        return this;
    }

    public FlowPaneBuilder styleClass(String styleClass) {
        if (styleClass != null && !styleClass.isEmpty()) {
            this.styleClasses.add(styleClass);
        }
        return this;
    }

    /** 批量挂多个 styleClass（M19.35 新增变长重载，跟 *Ant 风格一致）。 */
    public FlowPaneBuilder styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) {
                if (c != null && !c.isEmpty()) this.styleClasses.add(c);
            }
        }
        return this;
    }

    /** 设置背景层级（M19.35 集成 Background 体系）。 */
    public FlowPaneBuilder background(org.openkawu.jfxium.core.css.Background bg) {
        if (bg != null) this.styleClasses.add(bg.styleClass());
        return this;
    }

    public FlowPane build() {
        // M19.36 委托 FlowPaneAnt
        org.openkawu.jfxium.component.layout.FlowPaneAnt pane =
                new org.openkawu.jfxium.component.layout.FlowPaneAnt(orientation);
        pane.setHgap(hgap);
        pane.setVgap(vgap);
        pane.setAlignment(alignment);
        pane.setPadding(padding);
        pane.getChildren().addAll(children);
        pane.getStyleClass().addAll(styleClasses);
        if (!style.isEmpty()) {
            pane.setStyle(style);
        }
        return pane;
    }
}
