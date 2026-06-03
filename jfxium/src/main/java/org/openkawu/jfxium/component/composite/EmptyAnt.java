package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * JFXium Empty Component - 对标 Ant Design Empty。
 *
 * <h2>修复说明</h2>
 * 原实现 4 处 inline {@code setStyle}：
 * <ul>
 *   <li>{@code empty.setStyle("-fx-padding: 48px;")}</li>
 *   <li>{@code icon.setStyle("-fx-fill: -color-fg-subtle;")}</li>
 *   <li>{@code descLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px;")}</li>
 * </ul>
 * 全部搬到 LESS 的 {@code .jfx-empty}/{@code .jfx-empty-icon}/{@code .jfx-empty-description} 选择器。
 */
public class EmptyAnt {

    public static class Builder extends AbstractStyleBuilder<Builder> {
        // null = 用 i18n 默认值（Messages.get("empty.description")）；非 null = 调用方覆盖
        private String description = null;
        private String image = null;
        private Node extra = null;

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder extraButton(String text, Runnable action) {
            this.extra = ButtonAnt.create(text)
                    .type(ButtonAnt.Type.PRIMARY)
                    .onClick(e -> action.run())
                    .build();
            return this;
        }

        public VBox build() {
            VBox empty = new VBox(16);
            empty.setAlignment(Pos.CENTER);
            empty.getStyleClass().add(CssClasses.EMPTY);

            // SVG 图标：颜色由 LESS 控制
            SVGPath icon = new SVGPath();
            icon.setContent("M20 6h-8l-2-2H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm0 12H4V8h16v10z");
            // scale 是结构性属性，保留在 Java
            icon.setScaleX(2);
            icon.setScaleY(2);
            icon.getStyleClass().add(CssClasses.EMPTY_ICON);
            empty.getChildren().add(icon);

            // 描述文字：颜色与字号由 LESS 控制；文案走 i18n（未显式指定时）
            String effectiveDescription = description != null
                    ? description
                    : Messages.get("empty.description");
            Label descLabel = new Label(effectiveDescription);
            descLabel.getStyleClass().add(CssClasses.EMPTY_DESCRIPTION);
            // 仅当未显式指定 description 时订阅 locale 变化
            if (description == null) {
                Messages.localeProperty().addListener((obs, ov, nv) ->
                        descLabel.setText(Messages.get("empty.description")));
            }
            empty.getChildren().add(descLabel);

            if (extra != null) {
                empty.getChildren().add(extra);
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(empty);
            return empty;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder create(String description) {
        return new Builder().description(description);
    }
}
