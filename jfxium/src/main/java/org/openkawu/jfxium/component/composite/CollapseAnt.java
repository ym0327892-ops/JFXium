package org.openkawu.jfxium.component.composite;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.AnimationDuration;
import org.openkawu.jfxium.core.util.IconPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * JFXium 折叠面板组件 - 对标 Ant Design Collapse。
 *
 * <p><b>定位</b>：可展开/收缩的内容面板，支持多个 Panel，常用于 FAQ、详情分组、
 * 设置分组等场景。视觉走 LESS，支持展开动画。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>多 Panel</b>：panel(key, header, content) 添加多个折叠项</li>
 *   <li><b>手风琴模式</b>：accordion(true) 同时只展开一个</li>
 *   <li><b>默认展开</b>：defaultActiveKeys(keys)</li>
 *   <li><b>禁用</b>：panel 级别 disabled</li>
 *   <li><b>展开动画</b>：内容区高度动画过渡</li>
 *   <li><b>运行时控制</b>：通过 {@link Controller} 编程式展开/折叠面板</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * CollapseAnt.Builder builder = CollapseAnt.create()
 *     .panel("q1", "什么是 JFXium？", new Label("JavaFX 组件库..."))
 *     .panel("q2", "如何安装？", new Label("Maven 依赖..."))
 *     .accordion(true);
 * VBox collapse = builder.build();
 *
 * // 运行时控制
 * CollapseAnt.Controller ctrl = builder.controller();
 * ctrl.expand("q1");
 * ctrl.collapse("q2");
 * }</pre>
 */
public class CollapseAnt {

    public static class Panel {
        private final String key;
        private final String header;
        private final Node content;
        private boolean disabled;

        public Panel(String key, String header, Node content) {
            this.key = key;
            this.header = header;
            this.content = content;
        }

        public Panel disabled(boolean disabled) { this.disabled = disabled; return this; }

        public String getKey() { return key; }
        public String getHeader() { return header; }
        public Node getContent() { return content; }
        public boolean isDisabled() { return disabled; }
    }

    /**
     * 从已构建的 VBox 中获取 Controller。
     * @param root 由 CollapseAnt 构建的 VBox 节点
     * @return Controller 实例，若 root 不是 CollapseAnt 构建的则返回 null
     */
    public static Controller controllerOf(VBox root) {
        if (root == null) return null;
        Object ctrl = root.getProperties().get(Controller.PROPERTY_KEY);
        return ctrl instanceof Controller ? (Controller) ctrl : null;
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private List<Panel> panels = new ArrayList<>();
        private boolean accordion = false;
        private List<String> activeKeys = new ArrayList<>();
        private Node expandIcon = null;  // null = 用默认右箭头
        private Controller controller;

        public Builder panel(String key, String header, Node content) {
            this.panels.add(new Panel(key, header, content));
            return this;
        }

        public Builder panel(String key, String header, Node content, boolean disabled) {
            Panel panel = new Panel(key, header, content);
            panel.disabled(disabled);
            this.panels.add(panel);
            return this;
        }

        public Builder panels(List<Panel> panels) { this.panels = panels != null ? panels : new ArrayList<>(); return this; }
        public Builder accordion(boolean accordion) { this.accordion = accordion; return this; }
        public Builder accordion() { return accordion(true); }
        public Builder activeKey(String key) { this.activeKeys.add(key); return this; }
        public Builder activeKeys(List<String> keys) { this.activeKeys = keys != null ? keys : new ArrayList<>(); return this; }

        /** 自定义展开箭头图标（null = 默认右箭头，展开时旋转 180°）。 */
        public Builder expandIcon(Node icon) { this.expandIcon = icon; return this; }

        public VBox build() {
            VBox collapse = new VBox(0);
            collapse.getStyleClass().add(JfxStyles.COLLAPSE);

            this.controller = new Controller(collapse, this);

            for (int i = 0; i < panels.size(); i++) {
                Panel panel = panels.get(i);
                boolean isActive = activeKeys.contains(panel.getKey());

                VBox panelBox = new VBox(0);
                panelBox.getStyleClass().add(JfxStyles.COLLAPSE_PANEL);

                HBox header = new HBox();
                header.setAlignment(Pos.CENTER_LEFT);
                header.getStyleClass().add(JfxStyles.COLLAPSE_HEADER);
                if (panel.isDisabled()) {
                    header.getStyleClass().add(JfxStyles.COLLAPSE_DISABLED);
                }

                Node arrowNode;
                if (expandIcon != null) {
                    arrowNode = expandIcon;
                    header.getChildren().add(arrowNode);
                } else {
                    SVGPath defaultArrow = IconPath.chevronDownCollapse();
                    defaultArrow.getStyleClass().add(JfxStyles.COLLAPSE_ARROW);
                    arrowNode = defaultArrow;
                    header.getChildren().add(defaultArrow);
                }
                if (isActive) arrowNode.setRotate(180);

                Label headerLabel = new Label(panel.getHeader());
                headerLabel.getStyleClass().add(JfxStyles.COLLAPSE_HEADER_LABEL);
                header.getChildren().add(headerLabel);

                VBox contentBox = new VBox(0);
                contentBox.getStyleClass().add(JfxStyles.COLLAPSE_CONTENT);
                if (panel.getContent() != null) {
                    contentBox.getChildren().add(panel.getContent());
                }
                contentBox.setVisible(isActive);
                contentBox.setManaged(isActive);
                contentBox.setOpacity(isActive ? 1 : 0);

                // 注册到 controller
                controller.registerPanel(panel.getKey(), panelBox, arrowNode, contentBox, panel.isDisabled());

                if (!panel.isDisabled()) {
                    final String panelKey = panel.getKey();
                    header.setOnMouseClicked(e -> {
                        boolean expanding = !contentBox.isVisible();
                        if (accordion && expanding) {
                            controller.collapseAllExcept(panelKey);
                        }
                        controller.toggle(panelKey);
                    });
                }

                panelBox.getChildren().addAll(header, contentBox);

                if (i < panels.size() - 1) {
                    Region divider = new Region();
                    divider.getStyleClass().add(JfxStyles.COLLAPSE_DIVIDER);
                    panelBox.getChildren().add(divider);
                }

                collapse.getChildren().add(panelBox);
            }

            // 把 controller 挂到 root 的 properties 上
            collapse.getProperties().put(Controller.PROPERTY_KEY, controller);
            applyStyles(collapse);
            return collapse;
        }

        /**
         * 获取运行时控制器。
         * 必须在 {@link #build()} 之后调用。
         */
        public Controller controller() {
            if (controller == null) {
                throw new IllegalStateException("controller() must be called after build()");
            }
            return controller;
        }
    }

    /**
     * 折叠面板运行时控制器。
     *
     * <p>提供编程式控制面板展开/折叠的功能，无需重建整个组件树。
     *
     * <pre>{@code
     * CollapseAnt.Controller ctrl = builder.controller();
     * ctrl.expand("q1");
     * ctrl.collapse("q2");
     * }</pre>
     */
    public static class Controller {
        public static final String PROPERTY_KEY = "jfxium.collapse.controller";

        private final VBox root;
        private final Builder builder;
        private final List<PanelHandle> panelHandles = new ArrayList<>();
        private final Set<String> activeKeys = new HashSet<>();

        Controller(VBox root, Builder builder) {
            this.root = root;
            this.builder = builder;
        }

        void registerPanel(String key, VBox panelBox, Node arrowNode, VBox contentBox, boolean disabled) {
            PanelHandle handle = new PanelHandle(key, panelBox, arrowNode, contentBox, disabled);
            panelHandles.add(handle);
            if (contentBox.isVisible()) {
                activeKeys.add(key);
            }
        }

        /**
         * 展开指定面板。
         * @param key 面板的 key
         */
        public void expand(String key) {
            PanelHandle handle = findHandle(key);
            if (handle == null || handle.disabled || handle.contentBox.isVisible()) return;

            if (builder.accordion) {
                collapseAllExcept(key);
            }

            animatePanel(handle.contentBox, true);
            animateArrow(handle.arrowNode, true);
            activeKeys.add(key);
        }

        /**
         * 折叠指定面板。
         * @param key 面板的 key
         */
        public void collapse(String key) {
            PanelHandle handle = findHandle(key);
            if (handle == null || handle.disabled || !handle.contentBox.isVisible()) return;

            animatePanel(handle.contentBox, false);
            animateArrow(handle.arrowNode, false);
            activeKeys.remove(key);
        }

        /**
         * 切换指定面板的展开/折叠状态。
         * @param key 面板的 key
         */
        public void toggle(String key) {
            PanelHandle handle = findHandle(key);
            if (handle == null || handle.disabled) return;

            if (handle.contentBox.isVisible()) {
                collapse(key);
            } else {
                expand(key);
            }
        }

        /**
         * 展开所有面板。
         * 注意：在手风琴模式下这与 accordion 语义冲突，不会生效。
         */
        public void expandAll() {
            if (builder.accordion) return;
            for (PanelHandle handle : panelHandles) {
                if (!handle.disabled) {
                    expand(handle.key);
                }
            }
        }

        /**
         * 折叠所有面板。
         */
        public void collapseAll() {
            for (PanelHandle handle : panelHandles) {
                if (!handle.disabled) {
                    collapse(handle.key);
                }
            }
        }

        /**
         * 折叠除指定 key 外的所有面板。
         * 用于手风琴模式。
         */
        void collapseAllExcept(String exceptKey) {
            for (PanelHandle handle : panelHandles) {
                if (!handle.key.equals(exceptKey) && !handle.disabled) {
                    collapse(handle.key);
                }
            }
        }

        /**
         * 检查指定面板是否已展开。
         * @param key 面板的 key
         * @return true 表示已展开
         */
        public boolean isExpanded(String key) {
            return activeKeys.contains(key);
        }

        /**
         * 获取当前所有已展开面板的 key 集合（只读）。
         */
        public Set<String> getActiveKeys() {
            return Set.copyOf(activeKeys);
        }

        private PanelHandle findHandle(String key) {
            return panelHandles.stream()
                    .filter(h -> h.key.equals(key))
                    .findFirst()
                    .orElse(null);
        }

        private void animatePanel(VBox contentBox, boolean show) {
            contentBox.setVisible(true);
            contentBox.setManaged(true);
            Timeline timeline = new Timeline();
            if (show) {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(contentBox.opacityProperty(), 0)),
                        new KeyFrame(AnimationDuration.FAST, new KeyValue(contentBox.opacityProperty(), 1))
                );
            } else {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(contentBox.opacityProperty(), 1)),
                        new KeyFrame(AnimationDuration.FAST, new KeyValue(contentBox.opacityProperty(), 0))
                );
            }
            timeline.setOnFinished(e -> {
                if (!show) {
                    contentBox.setVisible(false);
                    contentBox.setManaged(false);
                }
            });
            timeline.play();
        }

        private void animateArrow(Node arrowNode, boolean expanding) {
            Timeline arrowAnim = new Timeline(
                    new KeyFrame(AnimationDuration.FAST,
                            new KeyValue(arrowNode.rotateProperty(), expanding ? 180 : 0))
            );
            arrowAnim.play();
        }

        private static class PanelHandle {
            final String key;
            final VBox panelBox;
            final Node arrowNode;
            final VBox contentBox;
            final boolean disabled;

            PanelHandle(String key, VBox panelBox, Node arrowNode, VBox contentBox, boolean disabled) {
                this.key = key;
                this.panelBox = panelBox;
                this.arrowNode = arrowNode;
                this.contentBox = contentBox;
                this.disabled = disabled;
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
