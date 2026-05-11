package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Tabs 组件 - 全面对标 Ant Design Tabs
 *
 * 支持形态：
 * - line: 下划线指示条样式（默认）
 * - card: 卡片式页签
 *
 * 使用示例：
 * <pre>{@code
 * Node tabs = TabsAnt.create()
 *     .tab("tab1", "Tab 1", new Label("Content 1"))
 *     .tab("tab2", "Tab 2", new Label("Content 2"))
 *     .build();
 * }</pre>
 */
public class TabsAnt {

    public enum Type { LINE, CARD }
    public enum Size { SMALL, MIDDLE, LARGE }
    public enum TabPlacement { TOP, BOTTOM, LEFT, RIGHT }

    public static Builder create() { return new Builder(); }

    public static class Builder {
        private final List<TabItem> tabs = new ArrayList<>();
        private Type type = Type.LINE;
        private Size size = Size.MIDDLE;
        private TabPlacement placement = TabPlacement.TOP;
        private boolean centered = false;
        private Node extraLeft = null;
        private Node extraRight = null;
        private Consumer<String> onChange = null;
        private int activeIndex = 0;

        public Builder tab(String key, String label, Node content) {
            tabs.add(new TabItem(key, label, content, false));
            return this;
        }

        public Builder tab(String key, String label, Node content, boolean disabled) {
            tabs.add(new TabItem(key, label, content, disabled));
            return this;
        }

        public Builder type(Type type) { this.type = type; return this; }
        public Builder size(Size size) { this.size = size; return this; }
        public Builder tabPlacement(TabPlacement p) { this.placement = p; return this; }
        public Builder centered(boolean c) { this.centered = c; return this; }
        public Builder extraLeft(Node n) { this.extraLeft = n; return this; }
        public Builder extraRight(Node n) { this.extraRight = n; return this; }
        public Builder onChange(Consumer<String> c) { this.onChange = c; return this; }

        public Node build() {
            if (tabs.isEmpty()) return new VBox();

            // 根容器
            VBox root = new VBox(0);
            root.setAlignment(Pos.TOP_LEFT);
            root.getStyleClass().add("jfx-tabs");

            boolean isVertical = placement == TabPlacement.LEFT || placement == TabPlacement.RIGHT;

            // 创建标签栏
            Node tabBar = createTabBar();

            // 创建内容区域
            StackPane contentArea = createContentArea();

            if (isVertical) {
                HBox layout = new HBox(0);
                layout.setAlignment(Pos.TOP_LEFT);
                if (placement == TabPlacement.LEFT) {
                    layout.getChildren().addAll(tabBar, contentArea);
                } else {
                    layout.getChildren().addAll(contentArea, tabBar);
                }
                HBox.setHgrow(contentArea, Priority.ALWAYS);
                root.getChildren().add(layout);
            } else {
                if (placement == TabPlacement.TOP) {
                    root.getChildren().addAll(tabBar, contentArea);
                } else {
                    root.getChildren().addAll(contentArea, tabBar);
                }
            }

            VBox.setVgrow(contentArea, Priority.ALWAYS);
            return root;
        }

        private Node createTabBar() {
            boolean isVertical = placement == TabPlacement.LEFT || placement == TabPlacement.RIGHT;

            // 标签栏容器
            HBox tabBar = new HBox(0);
            tabBar.setAlignment(centered ? Pos.CENTER : Pos.TOP_LEFT);
            tabBar.setStyle(getTabBarStyle());

            // 左侧附加内容
            if (extraLeft != null) {
                tabBar.getChildren().add(extraLeft);
            }

            // 创建指示条（仅 line 模式）
            Rectangle indicator = null;
            if (type == Type.LINE) {
                indicator = new Rectangle(0, 2, Color.web("#1677ff"));
                indicator.setArcWidth(2);
                indicator.setArcHeight(2);
            }

            // 创建标签
            List<Label> tabLabels = new ArrayList<>();
            for (int i = 0; i < tabs.size(); i++) {
                final TabItem item = tabs.get(i);
                final int idx = i;
                final Rectangle finalIndicator = indicator;
                Label label = createTabLabel(item, i == activeIndex);
                tabLabels.add(label);
                tabBar.getChildren().add(label);

                label.setOnMouseClicked(e -> {
                    if (item.disabled) return;
                    switchTab(idx, tabLabels, finalIndicator);
                });
            }

            // 右侧附加内容
            if (extraRight != null) {
                tabBar.getChildren().add(extraRight);
            }

            // Line 模式下，指示条放在标签栏下方
            if (type == Type.LINE && !isVertical) {
                VBox wrapper = new VBox(0);
                wrapper.setAlignment(Pos.TOP_LEFT);
                wrapper.getChildren().add(tabBar);

                // 指示条容器
                HBox indicatorBox = new HBox(0);
                indicatorBox.setAlignment(Pos.TOP_LEFT);
                indicatorBox.setPrefHeight(2);
                indicatorBox.setMinHeight(2);
                indicatorBox.setMaxHeight(2);
                indicatorBox.setStyle("-fx-background-color: -color-border-muted;");

                if (indicator != null) {
                    indicatorBox.getChildren().add(indicator);
                }
                wrapper.getChildren().add(indicatorBox);

                // 初始指示条位置（在布局完成后）
                final Rectangle finalIndicator = indicator;
                final List<Label> finalLabels = tabLabels;
                tabBar.layoutBoundsProperty().addListener((obs, old, val) -> {
                    if (finalIndicator != null && activeIndex < finalLabels.size()) {
                        updateIndicator(finalIndicator, finalLabels, activeIndex);
                    }
                });

                return wrapper;
            }

            return tabBar;
        }

        private StackPane contentAreaRef;

        private StackPane createContentArea() {
            contentAreaRef = new StackPane();
            contentAreaRef.setStyle("-fx-background-color: transparent; -fx-padding: 16px;");

            for (int i = 0; i < tabs.size(); i++) {
                Node content = tabs.get(i).content;
                content.setVisible(i == activeIndex);
                content.setManaged(i == activeIndex);
                contentAreaRef.getChildren().add(content);
            }

            return contentAreaRef;
        }

        private void switchTab(int newIndex, List<Label> tabLabels, Rectangle indicator) {
            if (newIndex == activeIndex) return;

            // 更新旧标签样式
            updateTabStyle(tabLabels.get(activeIndex), tabs.get(activeIndex), false);
            // 更新新标签样式
            updateTabStyle(tabLabels.get(newIndex), tabs.get(newIndex), true);

            // 切换内容
            tabs.get(activeIndex).content.setVisible(false);
            tabs.get(activeIndex).content.setManaged(false);
            tabs.get(newIndex).content.setVisible(true);
            tabs.get(newIndex).content.setManaged(true);

            // 移动指示条
            if (indicator != null && type == Type.LINE) {
                updateIndicator(indicator, tabLabels, newIndex);
            }

            activeIndex = newIndex;

            if (onChange != null) {
                onChange.accept(tabs.get(newIndex).key);
            }
        }

        private Label createTabLabel(TabItem item, boolean isActive) {
            Label label = new Label(item.label);
            label.setPadding(getTabPadding());
            label.setStyle(getTabStyle(item, isActive));

            if (item.disabled) {
                label.setDisable(true);
                label.setOpacity(0.5);
            }

            return label;
        }

        private void updateTabStyle(Label label, TabItem item, boolean isActive) {
            label.setStyle(getTabStyle(item, isActive));
        }

        private String getTabBarStyle() {
            StringBuilder sb = new StringBuilder();
            sb.append("-fx-background-color: transparent;");

            if (type == Type.CARD) {
                sb.append("-fx-background-color: rgba(0,0,0,0.02);");
                sb.append("-fx-border-color: transparent transparent -color-border-muted transparent;");
                sb.append("-fx-border-width: 0 0 1px 0;");
            }

            return sb.toString();
        }

        private String getTabStyle(TabItem item, boolean isActive) {
            StringBuilder sb = new StringBuilder();

            // 字体大小
            int fontSize = size == Size.LARGE ? 16 : (size == Size.SMALL ? 12 : 14);
            sb.append("-fx-font-size: ").append(fontSize).append("px;");

            if (type == Type.LINE) {
                sb.append("-fx-background-color: transparent;");
                sb.append("-fx-cursor: hand;");

                int paddingV = size == Size.LARGE ? 16 : (size == Size.SMALL ? 8 : 12);
                int paddingH = size == Size.LARGE ? 20 : (size == Size.SMALL ? 12 : 16);
                sb.append("-fx-padding: ").append(paddingV).append("px ").append(paddingH).append("px;");

                if (isActive) {
                    sb.append("-fx-text-fill: -color-accent-emphasis;");
                    sb.append("-fx-font-weight: 600;");
                } else {
                    sb.append("-fx-text-fill: -color-fg-default;");
                }
            } else if (type == Type.CARD) {
                sb.append("-fx-cursor: hand;");

                int paddingV = size == Size.LARGE ? 11 : (size == Size.SMALL ? 4 : 8);
                int paddingH = size == Size.LARGE ? 16 : (size == Size.SMALL ? 8 : 16);
                sb.append("-fx-padding: ").append(paddingV).append("px ").append(paddingH).append("px;");

                if (isActive) {
                    sb.append("-fx-background-color: -color-bg-default;");
                    sb.append("-fx-text-fill: -color-accent-emphasis;");
                    sb.append("-fx-border-color: -color-border-muted -color-border-muted transparent -color-border-muted;");
                    sb.append("-fx-border-width: 1px 1px 0 1px;");
                    sb.append("-fx-background-radius: 8px 8px 0 0;");
                    sb.append("-fx-border-radius: 8px 8px 0 0;");
                } else {
                    sb.append("-fx-background-color: transparent;");
                    sb.append("-fx-text-fill: -color-fg-default;");
                }
            }

            if (item.disabled) {
                sb.append("-fx-opacity: 0.5;");
                sb.append("-fx-cursor: default;");
            }

            return sb.toString();
        }

        private Insets getTabPadding() {
            // 返回 Insets，但样式中也会设置 padding，以样式为准
            if (type == Type.LINE) {
                int v = size == Size.LARGE ? 16 : (size == Size.SMALL ? 8 : 12);
                int h = size == Size.LARGE ? 20 : (size == Size.SMALL ? 12 : 16);
                return new Insets(v, h, v, h);
            } else {
                int v = size == Size.LARGE ? 11 : (size == Size.SMALL ? 4 : 8);
                int h = size == Size.LARGE ? 16 : (size == Size.SMALL ? 8 : 16);
                return new Insets(v, h, v, h);
            }
        }

        private void updateIndicator(Rectangle indicator, List<Label> labels, int index) {
            if (index < 0 || index >= labels.size()) return;

            Label label = labels.get(index);

            // 获取标签在父容器中的位置
            double labelX = label.getLayoutX();
            double labelWidth = label.getWidth();

            // 如果 layoutX 为 0（还未布局），使用 bounds
            if (labelX == 0) {
                labelX = label.getBoundsInParent().getMinX();
            }

            indicator.setWidth(labelWidth);
            indicator.setTranslateX(labelX);
        }
    }

    private static class TabItem {
        final String key;
        final String label;
        final Node content;
        final boolean disabled;

        TabItem(String key, String label, Node content, boolean disabled) {
            this.key = key;
            this.label = label;
            this.content = content;
            this.disabled = disabled;
        }
    }
}
