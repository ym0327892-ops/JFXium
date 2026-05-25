package org.openkawu.jfxium.core.container;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * StackPane 容器构建器。
 * 用于快速创建叠层布局容器（子节点在 z 轴叠加）。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * StackPane stack = StackPaneBuilder.create()
 *     .align(Pos.CENTER)
 *     .children(background, content, overlay)
 *     .build();
 * }</pre>
 */
public class StackPaneBuilder {
    private Pos alignment = Pos.CENTER;
    private Insets padding = Insets.EMPTY;
    private final List<Node> children = new ArrayList<>();
    private String style = "";
    private final List<String> styleClasses = new ArrayList<>();

    public static StackPaneBuilder create() {
        return new StackPaneBuilder();
    }

    public StackPaneBuilder align(Pos alignment) {
        this.alignment = alignment;
        return this;
    }

    public StackPaneBuilder padding(double padding) {
        this.padding = new Insets(padding);
        return this;
    }

    public StackPaneBuilder padding(double top, double right, double bottom, double left) {
        this.padding = new Insets(top, right, bottom, left);
        return this;
    }

    public StackPaneBuilder children(Node... nodes) {
        for (Node node : nodes) {
            if (node != null) {
                this.children.add(node);
            }
        }
        return this;
    }

    public StackPaneBuilder style(String style) {
        this.style = style;
        return this;
    }

    public StackPaneBuilder styleClass(String styleClass) {
        if (styleClass != null && !styleClass.isEmpty()) {
            this.styleClasses.add(styleClass);
        }
        return this;
    }

    public StackPane build() {
        StackPane pane = new StackPane();
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
