package org.openkawu.jfxium.component.composite;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 内容承载面组件。
 *
 * <pre>{@code
 * VBox panel = SurfaceAnt.create()
 *     .title("筛选条件")
 *     .content(form)
 *     .build();
 * }</pre>
 */
public class SurfaceAnt {

    public enum Shadow {
        NONE,
        SMALL,
        MEDIUM,
        LARGE
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String title = "";
        private Node extra;
        private final List<Node> content = new ArrayList<>();
        private boolean bordered = true;
        private Shadow shadow = Shadow.NONE;
        private double gap = 12;

        private Builder() {}

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder content(Node node) {
            if (node != null) {
                this.content.add(node);
            }
            return this;
        }

        public Builder children(Node... nodes) {
            if (nodes != null) {
                for (Node node : nodes) {
                    content(node);
                }
            }
            return this;
        }

        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public Builder shadow(Shadow shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder gap(double gap) {
            this.gap = gap;
            return this;
        }

        public VBox build() {
            VBox surface = new VBox(gap);
            surface.getStyleClass().add(CssClasses.SURFACE);
            if (bordered) {
                surface.getStyleClass().add(CssClasses.CARD_BORDERED);
            }
            switch (shadow) {
                case SMALL -> surface.getStyleClass().add(CssClasses.CARD_SHADOW_SM);
                case MEDIUM -> surface.getStyleClass().add(CssClasses.CARD_SHADOW_MD);
                case LARGE -> surface.getStyleClass().add(CssClasses.CARD_SHADOW_LG);
                case NONE -> {
                }
            }

            if (!title.isEmpty() || extra != null) {
                // 用 BarAnt 二段式（左标题 + 右 extra）
                javafx.scene.control.Label titleLabel = null;
                if (!title.isEmpty()) {
                    titleLabel = new javafx.scene.control.Label(title);
                    titleLabel.getStyleClass().add(CssClasses.SURFACE_TITLE);
                }
                HBox header = BarAnt.create()
                        .left(titleLabel)
                        .right(extra)
                        .gap(8)
                        .build();
                header.getStyleClass().add(CssClasses.SURFACE_HEADER);
                surface.getChildren().add(header);
            }

            if (!content.isEmpty()) {
                VBox body = new VBox(gap);
                body.getStyleClass().add(CssClasses.SURFACE_CONTENT);
                body.getChildren().addAll(content);
                surface.getChildren().add(body);
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(surface);
            return surface;
        }
    }
}
