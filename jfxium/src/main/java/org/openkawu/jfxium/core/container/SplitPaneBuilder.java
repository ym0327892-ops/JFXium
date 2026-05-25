package org.openkawu.jfxium.core.container;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

import java.util.ArrayList;
import java.util.List;

/**
 * SplitPane 容器构建器。
 * 用于快速创建可拖拽分栏容器。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * SplitPane split = SplitPaneBuilder.create()
 *     .orientation(Orientation.HORIZONTAL)
 *     .items(leftPanel, rightPanel)
 *     .dividerPositions(0.3)
 *     .build();
 * }</pre>
 */
public class SplitPaneBuilder {
    private Orientation orientation = Orientation.HORIZONTAL;
    private final List<Node> items = new ArrayList<>();
    private double[] dividerPositions = null;
    private String style = "";
    private final List<String> styleClasses = new ArrayList<>();

    public static SplitPaneBuilder create() {
        return new SplitPaneBuilder();
    }

    public SplitPaneBuilder orientation(Orientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public SplitPaneBuilder items(Node... nodes) {
        for (Node node : nodes) {
            if (node != null) {
                this.items.add(node);
            }
        }
        return this;
    }

    public SplitPaneBuilder dividerPositions(double... positions) {
        this.dividerPositions = positions;
        return this;
    }

    public SplitPaneBuilder style(String style) {
        this.style = style;
        return this;
    }

    public SplitPaneBuilder styleClass(String styleClass) {
        if (styleClass != null && !styleClass.isEmpty()) {
            this.styleClasses.add(styleClass);
        }
        return this;
    }

    public SplitPane build() {
        SplitPane pane = new SplitPane();
        pane.setOrientation(orientation);
        pane.getItems().addAll(items);
        if (dividerPositions != null && dividerPositions.length > 0) {
            pane.setDividerPositions(dividerPositions);
        }
        pane.getStyleClass().addAll(styleClasses);
        if (!style.isEmpty()) {
            pane.setStyle(style);
        }
        return pane;
    }
}
