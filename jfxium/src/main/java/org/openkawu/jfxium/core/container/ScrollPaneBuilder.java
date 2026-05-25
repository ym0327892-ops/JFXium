package org.openkawu.jfxium.core.container;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

import java.util.ArrayList;
import java.util.List;

/**
 * ScrollPane 容器构建器。
 * 用于快速创建滚动容器。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * ScrollPane scroll = ScrollPaneBuilder.create()
 *     .content(longContent)
 *     .fitToWidth(true)
 *     .vbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED)
 *     .build();
 * }</pre>
 */
public class ScrollPaneBuilder {
    private Node content;
    private boolean fitToWidth = false;
    private boolean fitToHeight = false;
    private ScrollPane.ScrollBarPolicy hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED;
    private ScrollPane.ScrollBarPolicy vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED;
    private Insets padding = Insets.EMPTY;
    private String style = "";
    private final List<String> styleClasses = new ArrayList<>();

    public static ScrollPaneBuilder create() {
        return new ScrollPaneBuilder();
    }

    public ScrollPaneBuilder content(Node content) {
        this.content = content;
        return this;
    }

    public ScrollPaneBuilder fitToWidth(boolean fitToWidth) {
        this.fitToWidth = fitToWidth;
        return this;
    }

    public ScrollPaneBuilder fitToHeight(boolean fitToHeight) {
        this.fitToHeight = fitToHeight;
        return this;
    }

    public ScrollPaneBuilder hbarPolicy(ScrollPane.ScrollBarPolicy policy) {
        this.hbarPolicy = policy;
        return this;
    }

    public ScrollPaneBuilder vbarPolicy(ScrollPane.ScrollBarPolicy policy) {
        this.vbarPolicy = policy;
        return this;
    }

    public ScrollPaneBuilder padding(double padding) {
        this.padding = new Insets(padding);
        return this;
    }

    public ScrollPaneBuilder padding(double top, double right, double bottom, double left) {
        this.padding = new Insets(top, right, bottom, left);
        return this;
    }

    public ScrollPaneBuilder style(String style) {
        this.style = style;
        return this;
    }

    public ScrollPaneBuilder styleClass(String styleClass) {
        if (styleClass != null && !styleClass.isEmpty()) {
            this.styleClasses.add(styleClass);
        }
        return this;
    }

    public ScrollPane build() {
        ScrollPane pane = new ScrollPane();
        if (content != null) {
            pane.setContent(content);
        }
        pane.setFitToWidth(fitToWidth);
        pane.setFitToHeight(fitToHeight);
        pane.setHbarPolicy(hbarPolicy);
        pane.setVbarPolicy(vbarPolicy);
        pane.setPadding(padding);
        pane.getStyleClass().addAll(styleClasses);
        if (!style.isEmpty()) {
            pane.setStyle(style);
        }
        return pane;
    }
}
