package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Anchor - 对标 Ant Design Anchor。
 *
 * 重构：active/hover 状态全部走 LESS（{@code .anchor-link} + {@code :hover} + {@code .anchor-link-active}），
 * 不再用 {@code label.setStyle().replace()} 这种字符串替换的反模式。
 *
 * <h2>已知遗留</h2>
 * 保留 {@code inkColor(Color)} API 作向下兼容入口；如传入非空 Color，会以 inline style 注入主色——
 * 这是该 API 的设计妥协（让用户用 Color 而非 styleClass 是反模式）。新代码请改用 styleClass 自定义。
 */
public class AnchorAnt {

    public enum Direction {
        VERTICAL, HORIZONTAL
    }

    public static class AnchorItem {
        private final String key;
        private final String title;
        private final String href;
        private final List<AnchorItem> children;

        public AnchorItem(String key, String title, String href) {
            this(key, title, href, null);
        }

        public AnchorItem(String key, String title, String href, List<AnchorItem> children) {
            this.key = key;
            this.title = title;
            this.href = href;
            this.children = children != null ? children : new ArrayList<>();
        }

        public String getKey() { return key; }
        public String getTitle() { return title; }
        public String getHref() { return href; }
        public List<AnchorItem> getChildren() { return children; }
    }

    public static class Builder {
        private List<AnchorItem> items = new ArrayList<>();
        private Direction direction = Direction.VERTICAL;
        private int offsetTop = 0;
        private String activeKey = null;
        private Consumer<String> onChange = null;
        private boolean affix = false;
        private Color inkColor = null;

        public Builder item(String key, String title, String href) {
            this.items.add(new AnchorItem(key, title, href));
            return this;
        }

        public Builder item(String key, String title, String href, List<AnchorItem> children) {
            this.items.add(new AnchorItem(key, title, href, children));
            return this;
        }

        public Builder items(List<AnchorItem> items) {
            this.items = items;
            return this;
        }

        public Builder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder offsetTop(int offsetTop) {
            this.offsetTop = offsetTop;
            return this;
        }

        public Builder activeKey(String activeKey) {
            this.activeKey = activeKey;
            return this;
        }

        public Builder onChange(Consumer<String> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder affix(boolean affix) {
            this.affix = affix;
            return this;
        }

        /**
         * 自定义 inkColor。
         * 
         * @deprecated 此 API 已废弃，建议使用 styleClass 自定义样式。
         *             当传入非空时会回退到 inline style 注入，破坏主题切换。
         *             新代码请改用 {@code .anchor-link-active} styleClass 自定义。
         */
        @Deprecated(since = "1.0", forRemoval = true)
        public Builder inkColor(Color color) {
            this.inkColor = color;
            return this;
        }

        public VBox build() {
            VBox anchor = new VBox(4);
            anchor.getStyleClass().add(CssClasses.ANCHOR);
            anchor.getStyleClass().add(direction == Direction.HORIZONTAL
                    ? CssClasses.ANCHOR_HORIZONTAL : CssClasses.ANCHOR_VERTICAL);

            for (AnchorItem item : items) {
                anchor.getChildren().add(createItemNode(item));
                if (!item.getChildren().isEmpty()) {
                    VBox subBox = new VBox(2);
                    subBox.setPadding(new Insets(4, 0, 4, 16));
                    for (AnchorItem child : item.getChildren()) {
                        subBox.getChildren().add(createItemNode(child));
                    }
                    anchor.getChildren().add(subBox);
                }
            }
            return anchor;
        }

        private Node createItemNode(AnchorItem item) {
            boolean isActive = activeKey != null && activeKey.equals(item.getKey());
            Label label = new Label(item.getTitle());
            label.getStyleClass().add(CssClasses.ANCHOR_LINK);
            if (isActive) {
                label.getStyleClass().add(CssClasses.ANCHOR_LINK_ACTIVE);
            }
            // 仅当用户显式设置 inkColor 时才注入 inline，作为兼容回退路径
            if (inkColor != null && isActive) {
                String hex = toHex(inkColor);
                label.setStyle("-fx-text-fill: " + hex
                        + "; -fx-border-color: transparent transparent transparent " + hex
                        + "; -fx-border-width: 0 0 0 2px;");
            }

            label.setOnMouseClicked(e -> {
                if (onChange != null) {
                    onChange.accept(item.getKey());
                }
            });
            // hover 由 LESS 控制，不再用 setStyle().replace() 字符串替换
            return label;
        }

        private static String toHex(Color color) {
            return String.format("#%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
