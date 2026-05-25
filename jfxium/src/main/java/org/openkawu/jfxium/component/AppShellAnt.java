package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium 应用骨架组件。
 *
 * <pre>{@code
 * BorderPane shell = AppShellAnt.create()
 *     .header(header)
 *     .sider(menu, 240)
 *     .content(page)
 *     .build();
 * }</pre>
 */
public class AppShellAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Node header;
        private Node sider;
        private double siderWidth = 240;
        private Node content;
        private Node footer;

        private Builder() {}

        public Builder header(Node header) {
            this.header = header;
            return this;
        }

        public Builder sider(Node sider) {
            return sider(sider, siderWidth);
        }

        public Builder sider(Node sider, double width) {
            this.sider = sider;
            this.siderWidth = width;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        public BorderPane build() {
            BorderPane shell = new BorderPane();
            shell.getStyleClass().add(CssClasses.APP_SHELL);

            if (header != null) {
                header.getStyleClass().add(CssClasses.APP_SHELL_HEADER);
                shell.setTop(header);
            }

            if (footer != null) {
                footer.getStyleClass().add(CssClasses.APP_SHELL_FOOTER);
                shell.setBottom(footer);
            }

            if (sider != null) {
                sider.getStyleClass().add(CssClasses.APP_SHELL_SIDER);
                if (sider instanceof Region region) {
                    region.setPrefWidth(siderWidth);
                    region.setMinWidth(siderWidth);
                }

                HBox center = new HBox();
                center.getChildren().add(sider);
                if (content != null) {
                    content.getStyleClass().add(CssClasses.APP_SHELL_CONTENT);
                    center.getChildren().add(content);
                    HBox.setHgrow(content, Priority.ALWAYS);
                }
                shell.setCenter(center);
            } else if (content != null) {
                content.getStyleClass().add(CssClasses.APP_SHELL_CONTENT);
                shell.setCenter(content);
            }

            // 用户自定义 style/styleClass 在内置类之后应用，便于覆盖
            applyStyles(shell);
            return shell;
        }
    }
}
