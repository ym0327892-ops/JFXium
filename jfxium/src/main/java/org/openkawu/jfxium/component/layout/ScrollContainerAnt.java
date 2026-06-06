package org.openkawu.jfxium.component.layout;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 统一滚动容器组件。
 *
 * <pre>{@code
 * ScrollPane scroll = ScrollContainerAnt.create()
 *     .content(pageBody)
 *     .fitToWidth(true)
 *     .padding(new Insets(24))
 *     .build();
 * }</pre>
 */
public class ScrollContainerAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Node content;
        private boolean fitToWidth = true;
        private boolean fitToHeight = false;
        private boolean pannable = false;
        private ScrollPane.ScrollBarPolicy hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED;
        private ScrollPane.ScrollBarPolicy vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED;

        private Builder() {}

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder fitToWidth(boolean fitToWidth) {
            this.fitToWidth = fitToWidth;
            return this;
        }

        public Builder fitToHeight(boolean fitToHeight) {
            this.fitToHeight = fitToHeight;
            return this;
        }

        public Builder pannable(boolean pannable) {
            this.pannable = pannable;
            return this;
        }

        public Builder hbarPolicy(ScrollPane.ScrollBarPolicy policy) {
            this.hbarPolicy = policy;
            return this;
        }

        public Builder vbarPolicy(ScrollPane.ScrollBarPolicy policy) {
            this.vbarPolicy = policy;
            return this;
        }

        public ScrollPane build() {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.getStyleClass().add(JfxStyles.SCROLL_CONTAINER);
            scrollPane.setFitToWidth(fitToWidth);
            scrollPane.setFitToHeight(fitToHeight);
            scrollPane.setPannable(pannable);
            scrollPane.setHbarPolicy(hbarPolicy);
            scrollPane.setVbarPolicy(vbarPolicy);

            if (content != null) {
                StackPane viewport = new StackPane(content);
                viewport.getStyleClass().add(JfxStyles.SCROLL_CONTAINER_VIEWPORT);
                // padding 应用到 viewport（通过父类 applyStyles）
                if (padding != null) {
                    viewport.setPadding(padding);
                }
                scrollPane.setContent(viewport);
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(scrollPane);
            return scrollPane;
        }
    }
}
