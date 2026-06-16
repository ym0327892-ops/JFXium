package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 承载面组件（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：轻量的内容承载面板，带可选标题 + 额外操作区，
 * 用于在页面中创建视觉上分组的区域。比 GroupBoxAnt 更轻量。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>标题 + 额外操作区（extra）头部布局</li>
 *   <li>阴影控制（NONE / SMALL / MEDIUM / LARGE）</li>
 *   <li>边框可选（bordered）</li>
 * </ul>
 *
 * <h2>与 GroupBoxAnt 的区别</h2>
 * <ul>
 *   <li>{@code SurfaceAnt} —— 轻量分组面板，无封面 / 标签页 / 操作区</li>
 *   <li>{@code GroupBoxAnt} —— 全功能面板，支持标签页 / 底部操作区</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * VBox panel = SurfaceAnt.create()
 *     .title("筛选条件")
 *     .extra(ButtonAnt.create("重置").type(ButtonAnt.Type.TEXT).build())
 *     .content(form)
 *     .bordered(true)
 *     .shadow(SurfaceAnt.Shadow.SMALL)
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
            this.title = title != null ? title : "";
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
            this.shadow = shadow != null ? shadow : Shadow.NONE;
            return this;
        }

        public Builder gap(double gap) {
            this.gap = Math.max(0, gap);
            return this;
        }

        public VBox build() {
            VBox surface = new VBox(gap);
            surface.getStyleClass().add(JfxStyles.SURFACE);
            if (bordered) {
                surface.getStyleClass().add(JfxStyles.SURFACE_BORDERED);
            }
            switch (shadow) {
                case SMALL -> surface.getStyleClass().add(JfxStyles.SURFACE_SHADOW_SM);
                case MEDIUM -> surface.getStyleClass().add(JfxStyles.SURFACE_SHADOW_MD);
                case LARGE -> surface.getStyleClass().add(JfxStyles.SURFACE_SHADOW_LG);
                case NONE -> {
                }
            }

            if (!title.isEmpty() || extra != null) {
                // 用 BarAnt 二段式（左标题 + 右 extra）
                javafx.scene.control.Label titleLabel = null;
                if (!title.isEmpty()) {
                    titleLabel = new javafx.scene.control.Label(title);
                    titleLabel.getStyleClass().add(JfxStyles.SURFACE_TITLE);
                }
                HBox header = BarAnt.create()
                        .left(titleLabel)
                        .right(extra)
                        .gap(8)
                        .build();
                header.getStyleClass().add(JfxStyles.SURFACE_HEADER);
                surface.getChildren().add(header);
            }

            if (!content.isEmpty()) {
                VBox body = new VBox(gap);
                body.getStyleClass().add(JfxStyles.SURFACE_CONTENT);
                body.getChildren().addAll(content);
                surface.getChildren().add(body);
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(surface);
            return surface;
        }
    }
}
