package org.openkawu.jfxium.component.composite;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 锚点导航组件 - 对标 Ant Design Anchor（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：页内锚点导航，点击链接滚动到对应区域，
 * 支持多级嵌套、水平/垂直布局。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>多级锚点项（支持子层级）</li>
 *   <li>方向：VERTICAL（默认）/ HORIZONTAL</li>
 *   <li>选中状态高亮（active/hover 走 LESS）</li>
 *   <li>自定义滚动目标容器</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node anchor = AnchorAnt.create()
 *     .item("intro", "介绍", "#intro")
 *     .item("features", "功能", "#features")
 *     .item("api", "API", "#api")
 *     .build();
 * }</pre>
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

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private List<AnchorItem> items = new ArrayList<>();
        private Direction direction = Direction.VERTICAL;
        private int offsetTop = 0;
        private String activeKey = null;
        private Consumer<String> onChange = null;
        private boolean affix = false;
        // runtime 控制器：build() 后装配，支持点击/调 API 切换高亮（BUG #52）
        private Controller controller;

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

        public VBox build() {
            VBox anchor = new VBox(4);
            anchor.getStyleClass().add(JfxStyles.ANCHOR);
            anchor.getStyleClass().add(direction == Direction.HORIZONTAL
                    ? JfxStyles.ANCHOR_HORIZONTAL : JfxStyles.ANCHOR_VERTICAL);

            // 装配 Controller：持有 key→label 引用，支持 runtime 切高亮（BUG #52）
            this.controller = new Controller(activeKey);

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

        /**
         * 拿到 runtime 控制器（必须在 {@link #build()} 之后调用）。
         *
         * <p>用例：滚动定位时调 {@link Controller#setActiveKey(String)} 移动高亮条，
         * 不必 rebuild 整个锚点列表（对齐 MenuAnt.Controller 的 runtime 模式，BUG #52）。</p>
         */
        public Controller controller() {
            if (controller == null) {
                throw new IllegalStateException("controller() 必须在 build() 之后调用");
            }
            return controller;
        }

        private Node createItemNode(AnchorItem item) {
            boolean isActive = activeKey != null && activeKey.equals(item.getKey());
            Label label = new Label(item.getTitle());
            label.getStyleClass().add(JfxStyles.ANCHOR_LINK);
            if (isActive) {
                label.getStyleClass().add(JfxStyles.ANCHOR_LINK_ACTIVE);
            }
            // 注册到 Controller：让 setActiveKey() 能找到该 label 改 styleClass
            controller.register(item.getKey(), label);

            label.setOnMouseClicked(e -> {
                // 点击即移动高亮（BUG #52：原实现点击只回调 onChange，高亮条不动）
                controller.setActiveKey(item.getKey());
                if (onChange != null) {
                    onChange.accept(item.getKey());
                }
            });
            // hover 由 LESS 控制，不再用 setStyle().replace() 字符串替换
            return label;
        }
    }

    /**
     * 锚点运行时控制器：在不重建节点的前提下切换高亮项（BUG #52）。
     *
     * <p>对齐 MenuAnt.Controller 的 runtime 模式。原实现 activeKey 只在构建期生效，
     * 点击锚点只回调 onChange、高亮条不动；现在点击会自动调 {@link #setActiveKey(String)} 移动高亮。
     * 滚动定位场景也可由业务直接调 {@code setActiveKey} 同步高亮。</p>
     *
     * <pre>{@code
     * AnchorAnt.Builder b = AnchorAnt.create()
     *     .item("intro", "介绍", "#intro")
     *     .item("usage", "用法", "#usage")
     *     .activeKey("intro");
     * VBox anchor = b.build();
     * AnchorAnt.Controller ctrl = b.controller();
     * ctrl.setActiveKey("usage");   // 滚动到 usage 时同步高亮
     * }</pre>
     */
    public static class Controller {
        // key → 对应的 label 节点（一个 key 理论上唯一，重复 key 以最后注册的为准）
        private final java.util.Map<String, Label> links = new java.util.HashMap<>();
        private String activeKey;

        Controller(String activeKey) {
            this.activeKey = activeKey;
        }

        /** 注册一个锚点 label（build 期内部调用）。 */
        void register(String key, Label label) {
            if (key != null) {
                links.put(key, label);
            }
        }

        /** 当前高亮的 key（可能为 null）。 */
        public String getActiveKey() {
            return activeKey;
        }

        /**
         * 切换高亮项：移除老节点的 active styleClass，给新 key 对应 label 挂上。不触发 onChange。
         * 与当前 activeKey 相同时无操作。
         */
        public void setActiveKey(String key) {
            if (java.util.Objects.equals(activeKey, key)) return;
            // 取消老高亮
            if (activeKey != null) {
                Label old = links.get(activeKey);
                if (old != null) old.getStyleClass().remove(JfxStyles.ANCHOR_LINK_ACTIVE);
            }
            // 挂新高亮
            activeKey = key;
            if (key != null) {
                Label cur = links.get(key);
                if (cur != null && !cur.getStyleClass().contains(JfxStyles.ANCHOR_LINK_ACTIVE)) {
                    cur.getStyleClass().add(JfxStyles.ANCHOR_LINK_ACTIVE);
                }
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
