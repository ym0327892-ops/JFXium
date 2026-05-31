package org.openkawu.jfxium.core.container;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * VBox 容器构建器。
 * 用于快速创建垂直布局容器。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * VBox vbox = VBoxBuilder.create()
 *     .spacing(16)
 *     .padding(24)
 *     .align(Pos.CENTER)
 *     .children(node1, node2, node3)
 *     .build();
 * }</pre>
 */
public class VBoxBuilder {
    private double spacing = 8;
    private Insets padding = Insets.EMPTY;
    private Pos alignment = Pos.TOP_LEFT;
    private final List<Node> children = new ArrayList<>();
    private String style = "";
    private final List<String> styleClasses = new ArrayList<>();

    public static VBoxBuilder create() {
        return new VBoxBuilder();
    }

    public VBoxBuilder spacing(double spacing) {
        this.spacing = spacing;
        return this;
    }

    public VBoxBuilder padding(double padding) {
        this.padding = new Insets(padding);
        return this;
    }

    public VBoxBuilder padding(double top, double right, double bottom, double left) {
        this.padding = new Insets(top, right, bottom, left);
        return this;
    }

    public VBoxBuilder align(Pos alignment) {
        this.alignment = alignment;
        return this;
    }

    public VBoxBuilder children(Node... nodes) {
        for (Node node : nodes) {
            if (node != null) {
                this.children.add(node);
            }
        }
        return this;
    }

    public VBoxBuilder style(String style) {
        this.style = style;
        return this;
    }

    public VBoxBuilder styleClass(String styleClass) {
        if (styleClass != null && !styleClass.isEmpty()) {
            this.styleClasses.add(styleClass);
        }
        return this;
    }

    /** 批量挂多个 styleClass（M19.35 新增变长重载，跟 *Ant 风格一致）。 */
    public VBoxBuilder styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) {
                if (c != null && !c.isEmpty()) this.styleClasses.add(c);
            }
        }
        return this;
    }

    /** 设置背景层级（M19.35 集成 Background 体系）。 */
    public VBoxBuilder background(org.openkawu.jfxium.core.css.Background bg) {
        if (bg != null) this.styleClasses.add(bg.styleClass());
        return this;
    }

    public VBox build() {
        // M19.36 委托 VBoxAnt：共享创建逻辑 + 让旧 Builder 产物也是 VBoxAnt（向上兼容 VBox）
        org.openkawu.jfxium.component.layout.VBoxAnt vbox =
                new org.openkawu.jfxium.component.layout.VBoxAnt(spacing);
        vbox.setPadding(padding);
        vbox.setAlignment(alignment);
        vbox.getChildren().addAll(children);
        vbox.getStyleClass().addAll(styleClasses);
        if (!style.isEmpty()) {
            vbox.setStyle(style);
        }
        return vbox;
    }
}
