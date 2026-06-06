package org.openkawu.jfxium.component.composite;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 标签页组件 - 对标 Ant Design Tabs（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：内容切换标签页，支持多个标签页之间的内容切换，
 * 无需页面跳转。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>形态</b>：LINE（下划线指示条）/ CARD（卡片式页签）</li>
 *   <li><b>尺寸</b>：SMALL / MIDDLE / LARGE</li>
 *   <li><b>位置</b>：TOP / BOTTOM / LEFT / RIGHT</li>
 *   <li><b>可关闭</b>：closable 支持关闭标签页</li>
 *   <li><b>禁用</b>：单个标签页可禁用</li>
 *   <li><b>额外操作区</b>：tabBarExtra 放右侧操作按钮</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>详情页多标签（基本信息 / 日志 / 配置）</li>
 *   <li>设置页分组（通用 / 外观 / 插件）</li>
 *   <li>多文档标签（IDE 风格）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node tabs = TabsAnt.create()
 *     .tab("basic", "基本信息", basicPanel)
 *     .tab("logs", "日志", logsPanel)
 *     .tab("config", "配置", configPanel)
 *     .type(TabsAnt.Type.CARD)
 *     .size(TabsAnt.Size.MIDDLE)
 *     .build();
 * }</pre>
 */
public class TabsAnt {

    public enum Type { LINE, CARD }
    public enum Size { SMALL, MIDDLE, LARGE }
    public enum TabPlacement { TOP, BOTTOM, LEFT, RIGHT }

    public static Builder create() { return new Builder(); }

    public static class Builder extends AbstractStyleBuilder<Builder> {
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
            tabBar.getStyleClass().addAll(JfxStyles.TABS_BAR);
            if (type == Type.CARD) {
                tabBar.getStyleClass().add(JfxStyles.TABS_BAR_CARD);
            }

            // 左侧附加内容
            if (extraLeft != null) {
                tabBar.getChildren().add(extraLeft);
            }

            // 创建标签
            List<Label> tabLabels = new ArrayList<>();
            for (int i = 0; i < tabs.size(); i++) {
                final TabItem item = tabs.get(i);
                final int idx = i;
                Label label = createTabLabel(item, i == activeIndex);
                tabLabels.add(label);
                tabBar.getChildren().add(label);

                label.setOnMouseClicked(e -> {
                    if (item.disabled) return;
                    switchTab(idx, tabLabels);
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

                // 指示条容器 - 使用 Pane 实现绝对定位
                Pane indicatorPane = new Pane();
                indicatorPane.setPrefHeight(3);
                indicatorPane.setMinHeight(3);
                indicatorPane.setMaxHeight(3);
                indicatorPane.getStyleClass().add(org.openkawu.jfxium.core.css.JfxStyles.TABS_INDICATOR_PANE);

                // 创建指示条
                Region indicator = new Region();
                indicator.setPrefHeight(3);
                indicator.setMinHeight(3);
                indicator.setMaxHeight(3);
                indicator.setPrefWidth(100); // 初始宽度
                indicator.getStyleClass().add(org.openkawu.jfxium.core.css.JfxStyles.TABS_INDICATOR_BAR);
                indicatorPane.getChildren().add(indicator);

                wrapper.getChildren().add(indicatorPane);

                // 绑定容器宽度到 tabBar
                indicatorPane.prefWidthProperty().bind(tabBar.widthProperty());

                // 初始指示条位置（在布局完成后）
                final Region finalIndicator = indicator;
                final List<Label> finalLabels = tabLabels;
                final Pane finalPane = indicatorPane;
                
                // 监听每个标签的布局变化
                for (Label label : finalLabels) {
                    label.layoutBoundsProperty().addListener((obs, old, val) -> {
                        updateIndicator(finalIndicator, finalLabels, finalPane, activeIndex);
                    });
                }
                
                // 监听 wrapper 添加到场景
                wrapper.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        // 使用 PauseTransition 延迟更新，确保布局完成
                        PauseTransition delay = new PauseTransition(Duration.millis(300));
                        delay.setOnFinished(e -> updateIndicator(finalIndicator, finalLabels, finalPane, activeIndex));
                        delay.play();
                    }
                });
                
                // 立即尝试更新一次（如果已经添加到场景）
                Platform.runLater(() -> {
                    Platform.runLater(() -> {
                        updateIndicator(finalIndicator, finalLabels, finalPane, activeIndex);
                    });
                });
                
                // 监听 tabBar 布局变化
                tabBar.layoutBoundsProperty().addListener((obs, old, val) -> {
                    updateIndicator(finalIndicator, finalLabels, finalPane, activeIndex);
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

        private void switchTab(int newIndex, List<Label> tabLabels) {
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
            if (type == Type.LINE) {
                // 找到指示条并更新位置
                Node tabBar = tabLabels.get(0).getParent();
                if (tabBar != null) {
                    Node wrapper = tabBar.getParent();
                    if (wrapper instanceof VBox) {
                        VBox vbox = (VBox) wrapper;
                        if (vbox.getChildren().size() > 1) {
                            Node indicatorContainer = vbox.getChildren().get(1);
                            if (indicatorContainer instanceof Pane) {
                                Pane container = (Pane) indicatorContainer;
                                if (!container.getChildren().isEmpty()) {
                                    Region indicator = (Region) container.getChildren().get(0);
                                    updateIndicator(indicator, tabLabels, container, newIndex);
                                }
                            }
                        }
                    }
                }
            }

            activeIndex = newIndex;

            if (onChange != null) {
                onChange.accept(tabs.get(newIndex).key);
            }
        }

        private Label createTabLabel(TabItem item, boolean isActive) {
            Label label = new Label(item.label);
            label.setPadding(getTabPadding());

            // 基础 styleClass
            label.getStyleClass().add(JfxStyles.TABS_LABEL);
            label.getStyleClass().add(type == Type.CARD ? JfxStyles.TABS_LABEL_CARD : JfxStyles.TABS_LABEL_LINE);
            // 尺寸 styleClass
            if (size == Size.LARGE) {
                label.getStyleClass().add(JfxStyles.TABS_LABEL_LARGE);
            } else if (size == Size.SMALL) {
                label.getStyleClass().add(JfxStyles.TABS_LABEL_SMALL);
            }
            // 激活态 styleClass
            if (isActive) {
                label.getStyleClass().add(JfxStyles.TABS_ACTIVE);
            }

            if (item.disabled) {
                label.setDisable(true);
                label.getStyleClass().add(JfxStyles.TABS_DISABLED);
            }

            return label;
        }

        private void updateTabStyle(Label label, TabItem item, boolean isActive) {
            if (isActive) {
                if (!label.getStyleClass().contains(JfxStyles.TABS_ACTIVE)) {
                    label.getStyleClass().add(JfxStyles.TABS_ACTIVE);
                }
            } else {
                label.getStyleClass().remove(JfxStyles.TABS_ACTIVE);
            }
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

        private void updateIndicator(Region indicator, List<Label> labels, Pane container, int index) {
            if (index < 0 || index >= labels.size()) return;

            Label label = labels.get(index);

            // 获取标签的实际边界
            javafx.geometry.Bounds bounds = label.getBoundsInParent();
            double labelX = bounds.getMinX();
            double labelWidth = bounds.getWidth();

            // 如果宽度为 0，说明还未布局完成，使用默认值确保指示条可见
            if (labelWidth <= 0) {
                indicator.setPrefWidth(100);
                indicator.setLayoutX(0);
                return;
            }

            // 设置指示条宽度和位置
            indicator.setPrefWidth(labelWidth);
            indicator.setLayoutX(labelX);
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
