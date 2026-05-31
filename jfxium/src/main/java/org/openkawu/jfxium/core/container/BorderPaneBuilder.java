package org.openkawu.jfxium.core.container;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;

/**
 * BorderPane 容器构建器。
 * 用于快速创建五区位布局容器（top/right/bottom/left/center）。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * BorderPane border = BorderPaneBuilder.create()
 *     .top(header)
 *     .left(sidebar)
 *     .center(content)
 *     .right(aside)
 *     .bottom(footer)
 *     .build();
 * }</pre>
 */
public class BorderPaneBuilder {
    private Node top, right, bottom, left, center;
    private Insets padding = Insets.EMPTY;
    private String style = "";
    private final List<String> styleClasses = new ArrayList<>();

    public static BorderPaneBuilder create() {
        return new BorderPaneBuilder();
    }

    public BorderPaneBuilder top(Node node) {
        this.top = node;
        return this;
    }

    public BorderPaneBuilder right(Node node) {
        this.right = node;
        return this;
    }

    public BorderPaneBuilder bottom(Node node) {
        this.bottom = node;
        return this;
    }

    public BorderPaneBuilder left(Node node) {
        this.left = node;
        return this;
    }

    public BorderPaneBuilder center(Node node) {
        this.center = node;
        return this;
    }

    public BorderPaneBuilder padding(double padding) {
        this.padding = new Insets(padding);
        return this;
    }

    public BorderPaneBuilder padding(double top, double right, double bottom, double left) {
        this.padding = new Insets(top, right, bottom, left);
        return this;
    }

    public BorderPaneBuilder style(String style) {
        this.style = style;
        return this;
    }

    public BorderPaneBuilder styleClass(String styleClass) {
        if (styleClass != null && !styleClass.isEmpty()) {
            this.styleClasses.add(styleClass);
        }
        return this;
    }

    /** 批量挂多个 styleClass（M19.35 新增变长重载，跟 *Ant 风格一致）。 */
    public BorderPaneBuilder styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) {
                if (c != null && !c.isEmpty()) this.styleClasses.add(c);
            }
        }
        return this;
    }

    /** 设置背景层级（M19.35 集成 Background 体系）。 */
    public BorderPaneBuilder background(org.openkawu.jfxium.core.css.Background bg) {
        if (bg != null) this.styleClasses.add(bg.styleClass());
        return this;
    }

    public BorderPane build() {
        // M19.36 委托 BorderPaneAnt：共享创建逻辑 + 让旧 Builder 产物也是 BorderPaneAnt（向上兼容 BorderPane）
        org.openkawu.jfxium.component.layout.BorderPaneAnt pane =
                new org.openkawu.jfxium.component.layout.BorderPaneAnt();
        if (top != null) pane.setTop(top);
        if (right != null) pane.setRight(right);
        if (bottom != null) pane.setBottom(bottom);
        if (left != null) pane.setLeft(left);
        if (center != null) pane.setCenter(center);
        pane.setPadding(padding);
        pane.getStyleClass().addAll(styleClasses);
        if (!style.isEmpty()) {
            pane.setStyle(style);
        }
        return pane;
    }
}
