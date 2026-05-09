package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Tabs Component
 * Inspired by Ant Design Tabs
 *
 * Usage:
 * <pre>{@code
 * TabPane tabs = JFXTabs.create()
 *     .tab("Tab 1", new Label("Content 1"))
 *     .tab("Tab 2", new Label("Content 2"))
 *     .tab("Tab 3", new Label("Content 3"))
 *     .closable(false)
 *     .build();
 * }</pre>
 */
public class JFXTabs {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private final List<Tab> tabs = new ArrayList<>();
        private boolean closable = false;
        private TabPane.TabClosingPolicy closingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE;
        private TabPane.TabDragPolicy dragPolicy = TabPane.TabDragPolicy.FIXED;
        private String style = "";

        private Builder() {}

        /**
         * 添加标签页
         */
        public Builder tab(String title, Node content) {
            Tab tab = new Tab(title, content);
            tab.setClosable(closable);
            tabs.add(tab);
            return this;
        }

        /**
         * 添加标签页（带提示文本）
         */
        public Builder tab(String title, Node content, String tooltip) {
            Tab tab = new Tab(title, content);
            tab.setClosable(closable);
            tab.setTooltip(new javafx.scene.control.Tooltip(tooltip));
            tabs.add(tab);
            return this;
        }

        /**
         * 添加标签页（自定义 Tab）
         */
        public Builder tab(Tab tab) {
            tabs.add(tab);
            return this;
        }

        /**
         * 设置标签页是否可关闭
         */
        public Builder closable(boolean closable) {
            this.closable = closable;
            this.closingPolicy = closable ? TabPane.TabClosingPolicy.SELECTED_TAB : TabPane.TabClosingPolicy.UNAVAILABLE;
            for (Tab tab : tabs) {
                tab.setClosable(closable);
            }
            return this;
        }

        /**
         * 设置关闭策略
         */
        public Builder closingPolicy(TabPane.TabClosingPolicy policy) {
            this.closingPolicy = policy;
            return this;
        }

        /**
         * 设置拖拽策略
         */
        public Builder dragPolicy(TabPane.TabDragPolicy policy) {
            this.dragPolicy = policy;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public TabPane build() {
            TabPane tabPane = new TabPane();
            tabPane.getTabs().addAll(tabs);
            tabPane.setTabClosingPolicy(closingPolicy);
            tabPane.setTabDragPolicy(dragPolicy);

            if (!style.isEmpty()) {
                tabPane.setStyle(style);
            }

            return tabPane;
        }
    }
}