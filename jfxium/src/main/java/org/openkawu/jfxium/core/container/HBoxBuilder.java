package org.openkawu.jfxium.core.container;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

/**
 * HBox 容器构建器。
 * 用于快速创建水平布局容器。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * HBox hbox = HBoxBuilder.create()
 *     .spacing(8)
 *     .padding(12)
 *     .align(Pos.CENTER_LEFT)
 *     .children(label, input, button)
 *     .build();
 * }</pre>
 */
public class HBoxBuilder {
    private double spacing = 8;
    private Insets padding = Insets.EMPTY;
    private Pos alignment = Pos.CENTER_LEFT;
    private final List<Node> children = new ArrayList<>();
    private String style = "";
    private final List<String> styleClasses = new ArrayList<>();

    public static HBoxBuilder create() {
        return new HBoxBuilder();
    }

    public HBoxBuilder spacing(double spacing) {
        this.spacing = spacing;
        return this;
    }

    public HBoxBuilder padding(double padding) {
        this.padding = new Insets(padding);
        return this;
    }

    public HBoxBuilder padding(double top, double right, double bottom, double left) {
        this.padding = new Insets(top, right, bottom, left);
        return this;
    }

    public HBoxBuilder align(Pos alignment) {
        this.alignment = alignment;
        return this;
    }

    public HBoxBuilder children(Node... nodes) {
        for (Node node : nodes) {
            if (node != null) {
                this.children.add(node);
            }
        }
        return this;
    }

    public HBoxBuilder style(String style) {
        this.style = style;
        return this;
    }

    public HBoxBuilder styleClass(String styleClass) {
        if (styleClass != null && !styleClass.isEmpty()) {
            this.styleClasses.add(styleClass);
        }
        return this;
    }

    /** 批量挂多个 styleClass（M19.35 新增变长重载，跟 *Ant 风格一致）。 */
    public HBoxBuilder styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) {
                if (c != null && !c.isEmpty()) this.styleClasses.add(c);
            }
        }
        return this;
    }

    /** 设置背景层级（M19.35 集成 Background 体系）。 */
    public HBoxBuilder background(org.openkawu.jfxium.core.css.Background bg) {
        if (bg != null) this.styleClasses.add(bg.styleClass());
        return this;
    }

    public HBox build() {
        // M19.36 委托 HBoxAnt：共享创建逻辑 + 让旧 Builder 产物也是 HBoxAnt（向上兼容 HBox）
        org.openkawu.jfxium.component.layout.HBoxAnt hbox =
                new org.openkawu.jfxium.component.layout.HBoxAnt(spacing);
        hbox.setPadding(padding);
        hbox.setAlignment(alignment);
        hbox.getChildren().addAll(children);
        hbox.getStyleClass().addAll(styleClasses);
        if (!style.isEmpty()) {
            hbox.setStyle(style);
        }
        return hbox;
    }
}
