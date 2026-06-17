package org.openkawu.jfxium.component.composite;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 标签页组件 - 对标 Ant Design Tabs(组合式,Builder 模式)。
 *
 * <p><b>定位</b>:内容切换标签页,支持多个标签页之间的内容切换,
 * 无需页面跳转。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>形态</b>:LINE(下划线指示条) / CARD(卡片式页签)</li>
 *   <li><b>尺寸</b>:SMALL / MIDDLE / LARGE</li>
 *   <li><b>位置</b>:TOP / BOTTOM / LEFT / RIGHT</li>
 *   <li><b>可关闭</b>:closable 支持关闭标签页</li>
 *   <li><b>禁用</b>:单个标签页可禁用</li>
 *   <li><b>额外操作区</b>:tabBarExtra 放右侧操作按钮</li>
 *   <li><b>运行时切换</b>:通过 {@link Controller} 编程式切换当前 tab(对齐 StepsAnt.Controller)</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>详情页多标签(基本信息 / 日志 / 配置)</li>
 *   <li>设置页分组(通用 / 外观 / 插件)</li>
 *   <li>多文档标签(IDE 风格)</li>
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
 *
 * // 运行时切换(BUG #51 推广,无需 rebuild)
 * TabsAnt.Controller ctrl = TabsAnt.controllerOf((Node) tabs);
 * ctrl.selectByKey("logs");
 * ctrl.next();
 * }</pre>
 */
public class TabsAnt {

    public enum Type { LINE, CARD }
    public enum Size { SMALL, MIDDLE, LARGE }
    public enum TabPlacement { TOP, BOTTOM, LEFT, RIGHT }

    public static Builder create() { return new Builder(); }

    /**
     * 从已 build 的 Node 树中拿回 Controller(无需持有 Builder 引用)。
     * 节点未由 TabsAnt 构建时会抛 IllegalStateException。
     */
    public static Controller controllerOf(Node root) {
        if (root == null || root.getProperties() == null) {
            throw new IllegalStateException("controllerOf(root): root 为 null");
        }
        Object ctrl = root.getProperties().get(Controller.PROPERTY_KEY);
        if (!(ctrl instanceof Controller)) {
            throw new IllegalStateException(
                "controllerOf(root): 该节点不是 TabsAnt 构建的,或 controller 已被覆盖");
        }
        return (Controller) ctrl;
    }

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
        // runtime 控制器:build() 后装配,支持不重建节点切换当前 tab(BUG #51 推广, 对齐 StepsAnt.Controller)
        private Controller controller;

        public Builder tab(String key, String label, Node content) {
            tabs.add(new TabItem(key, safeLabel(label), safeContent(content), false));
            return this;
        }

        public Builder tab(String key, String label, Node content, boolean disabled) {
            tabs.add(new TabItem(key, safeLabel(label), safeContent(content), disabled));
            return this;
        }

        public Builder type(Type type) { this.type = type != null ? type : Type.LINE; return this; }
        public Builder size(Size size) { this.size = size != null ? size : Size.MIDDLE; return this; }
        public Builder tabPlacement(TabPlacement p) { this.placement = p != null ? p : TabPlacement.TOP; return this; }
        public Builder centered(boolean c) { this.centered = c; return this; }
        public Builder extraLeft(Node n) { this.extraLeft = n; return this; }
        public Builder extraRight(Node n) { this.extraRight = n; return this; }
        public Builder onChange(Consumer<String> c) { this.onChange = c; return this; }

        public Node build() {
            if (tabs.isEmpty()) {
                VBox empty = new VBox();
                applyStyles(empty);
                return empty;
            }

            // 装配 Controller(每次 build 新建一个,与已构造节点树绑定)
            this.controller = new Controller(tabs.size(), activeIndex);
            return buildTabsTree();
        }

        /**
         * 拿到 runtime 控制器(必须在 {@link #build()} 之后调用)。
         *
         * <p>用例:业务方在外部根据数据变化编程式切换 tab,只需调 {@link Controller#setCurrent(int)}
         * 或 {@link Controller#selectByKey(String)},不必 rebuild 整个 tabs 节点树
         * (对齐 StepsAnt.Controller 的 runtime 模式,BUG #51)。</p>
         */
        public Controller controller() {
            if (controller == null) {
                throw new IllegalStateException("controller() 必须在 build() 之后调用");
            }
            return controller;
        }

        private Node buildTabsTree() {
            // 根容器
            VBox root = new VBox(0);
            root.setAlignment(Pos.TOP_LEFT);
            root.getStyleClass().add(JfxStyles.TABS_ROOT);

            // 把 controller 挂到 root 的 properties,支持 TabsAnt.controllerOf(root) 静态查找
            root.getProperties().put(Controller.PROPERTY_KEY, controller);

            // 注入回调:Controller.setCurrent 触发 onChange + 指示条移动
            final Runnable changeCallback = () -> {
                if (onChange != null) {
                    String key = controller.getCurrentKey();
                    if (key != null) onChange.accept(key);
                }
            };
            controller.onChangeCallback = changeCallback;
            controller.onIndicatorMove = () -> {
                Node tabBar = controller.tabBarRef;
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
                                    updateIndicator(indicator, controller.tabLabels, container, controller.getCurrent());
                                }
                            }
                        }
                    }
                }
            };

            boolean isVertical = placement == TabPlacement.LEFT || placement == TabPlacement.RIGHT;

            // 创建标签栏
            Node tabBar = createTabBar();

            // 创建内容区域
            StackPane contentArea = createContentArea();
            controller.contentAreaRef = contentArea;

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
            applyStyles(root);
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
            controller.tabBarRef = tabBar;

            // 左侧附加内容
            if (extraLeft != null) {
                tabBar.getChildren().add(extraLeft);
            }

            // 创建标签
            for (int i = 0; i < tabs.size(); i++) {
                final TabItem item = tabs.get(i);
                final int idx = i;
                Label label = createTabLabel(item, i == controller.getCurrent());
                controller.tabLabels.add(label);
                controller.tabKeys.add(item.key);
                controller.tabDisabled[i] = item.disabled;
                tabBar.getChildren().add(label);

                label.setOnMouseClicked(e -> {
                    if (item.disabled) return;
                    controller.setCurrent(idx);
                });
            }

            // 右侧附加内容
            if (extraRight != null) {
                tabBar.getChildren().add(extraRight);
            }

            // Line 模式下,指示条放在标签栏下方
            if (type == Type.LINE && !isVertical) {
                VBox wrapper = new VBox(0);
                wrapper.setAlignment(Pos.TOP_LEFT);
                wrapper.getChildren().add(tabBar);

                // 指示条容器 - 使用 Pane 实现绝对定位
                Pane indicatorPane = new Pane();
                indicatorPane.getStyleClass().add(JfxStyles.TABS_INDICATOR_PANE);

                // 创建指示条
                Region indicator = new Region();
                indicator.getStyleClass().add(JfxStyles.TABS_INDICATOR_BAR);
                indicatorPane.getChildren().add(indicator);

                wrapper.getChildren().add(indicatorPane);

                // 绑定容器宽度到 tabBar
                indicatorPane.prefWidthProperty().bind(tabBar.widthProperty());

                // 初始指示条位置(在布局完成后)
                final Region finalIndicator = indicator;
                final List<Label> finalLabels = controller.tabLabels;
                final Pane finalPane = indicatorPane;

                // 监听每个标签的布局变化
                for (Label label : finalLabels) {
                    label.layoutBoundsProperty().addListener((obs, old, val) -> {
                        updateIndicator(finalIndicator, finalLabels, finalPane, controller.getCurrent());
                    });
                }

                // 监听 wrapper 添加到场景
                wrapper.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        // 使用 PauseTransition 延迟更新,确保布局完成
                        PauseTransition delay = new PauseTransition(Duration.millis(300));
                        delay.setOnFinished(e -> updateIndicator(finalIndicator, finalLabels, finalPane, controller.getCurrent()));
                        delay.play();
                    }
                });

                // 立即尝试更新一次(如果已经添加到场景)
                Platform.runLater(() -> {
                    Platform.runLater(() -> {
                        updateIndicator(finalIndicator, finalLabels, finalPane, controller.getCurrent());
                    });
                });

                // 监听 tabBar 布局变化
                tabBar.layoutBoundsProperty().addListener((obs, old, val) -> {
                    updateIndicator(finalIndicator, finalLabels, finalPane, controller.getCurrent());
                });

                return wrapper;
            }

            return tabBar;
        }

        private StackPane createContentArea() {
            StackPane contentArea = new StackPane();
            // 背景与内边距走 .jfx-tabs-content 修饰类(LESS 中 -color-bg-transparent + 16px padding)
            contentArea.getStyleClass().add(JfxStyles.TABS_CONTENT);

            for (int i = 0; i < tabs.size(); i++) {
                Node content = tabs.get(i).content;
                content.setVisible(i == controller.getCurrent());
                content.setManaged(i == controller.getCurrent());
                contentArea.getChildren().add(content);
                controller.tabContents.add(content);
            }

            return contentArea;
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

        private Insets getTabPadding() {
            // 返回 Insets,但样式中也会设置 padding,以样式为准
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

            // 如果宽度为 0,说明还未布局完成,使用默认值确保指示条可见
            if (labelWidth <= 0) {
                indicator.setPrefWidth(100);
                indicator.setLayoutX(0);
                return;
            }

            // 设置指示条宽度和位置
            indicator.setPrefWidth(labelWidth);
            indicator.setLayoutX(labelX);
        }

        private static String safeLabel(String label) {
            return label != null ? label : "";
        }

        private static Node safeContent(Node content) {
            if (content != null) {
                return content;
            }
            Region placeholder = new Region();
            placeholder.setMinSize(0, 0);
            placeholder.setPrefSize(0, 0);
            placeholder.setMaxSize(0, 0);
            return placeholder;
        }
    }

    /**
     * 标签页运行时控制器:在不重建节点的前提下切换当前 tab(BUG #51 推广, 对齐 StepsAnt.Controller)。
     *
     * <p>典型场景:外部数据变化后编程式切换 tab(如点击"下一步"按钮跳到下一段),直接调
     * {@link #setCurrent(int)} / {@link #selectByKey(String)} 即可,Node 引用始终有效,
     * 不丢动画/布局状态。</p>
     *
     * <pre>{@code
     * Node tabs = TabsAnt.create().tab("a", "A", a).tab("b", "B", b).build();
     * TabsAnt.Controller ctrl = TabsAnt.controllerOf(tabs);
     * ctrl.selectByKey("b");   // 切到 key="b" 的 tab
     * ctrl.next();             // 前进到下一 tab
     * }</pre>
     */
    public static class Controller {
        /** root.getProperties() 的 key,支持 {@link TabsAnt#controllerOf(Node)} 静态查找 */
        public static final String PROPERTY_KEY = "jfxium.tabs.controller";

        // 由 Builder 装配时填充(package-private,无 getter 暴露)
        final List<Label> tabLabels = new ArrayList<>();
        final List<Node> tabContents = new ArrayList<>();
        final List<String> tabKeys = new ArrayList<>();
        final boolean[] tabDisabled;
        Node tabBarRef;          // 标签栏容器(指示条更新用)
        StackPane contentAreaRef; // 内容容器(预留扩展)

        private final int total;
        private int current;
        // 注入:onChange / 指示条移动
        Runnable onChangeCallback;
        Runnable onIndicatorMove;

        Controller(int total, int current) {
            this.total = total;
            this.current = Math.max(0, Math.min(current, total - 1));
            this.tabDisabled = new boolean[total];
        }

        // -------- 只读 API --------

        /** 当前 tab 下标(0-based)。 */
        public int getCurrent() { return current; }

        /** 总 tab 数。 */
        public int getTotal() { return total; }

        /** 当前 tab 的 key(未注册时返回 null)。 */
        public String getCurrentKey() {
            return (current >= 0 && current < tabKeys.size()) ? tabKeys.get(current) : null;
        }

        /** 指定下标的 tab key(越界返回 null)。 */
        public String getKey(int index) {
            return (index >= 0 && index < tabKeys.size()) ? tabKeys.get(index) : null;
        }

        /** 指定下标的 tab 是否禁用。 */
        public boolean isDisabled(int index) {
            return index >= 0 && index < total && tabDisabled[index];
        }

        /** 当前 tab 是否处于禁用态(几乎不会出现,但作为防御性 API 暴露)。 */
        public boolean isCurrentDisabled() {
            return isDisabled(current);
        }

        // -------- 写入 API --------

        /**
         * 切换当前 tab 下标(0-based)。越界(小于 0 或大于等于 total)时无操作。
         *
         * <p>禁用 tab 拒绝切换(返回 false 表示未切换);新下标等于当前下标时也无操作。
         * 切换成功触发:
         * <ul>
         *   <li>旧/新 tab label 的 {@code TABS_ACTIVE} 修饰类切换</li>
     *   <li>旧/新 tab content 的 visible/managed 切换</li>
     *   <li>Line 模式下指示条重定位</li>
     *   <li>Builder 注入的 onChange 回调(回传新 tab 的 key)</li>
     * </ul>
         * </p>
         *
         * @return true 表示切换成功,false 表示被拒绝(越界/禁用/无变化)
         */
        public boolean setCurrent(int newIndex) {
            if (newIndex < 0 || newIndex >= total) return false;
            if (newIndex == current) return false;
            if (tabDisabled[newIndex]) return false;

            int oldIndex = current;
            current = newIndex;

            // 切换 styleClass
            Label oldLabel = tabLabels.get(oldIndex);
            Label newLabel = tabLabels.get(newIndex);
            if (oldLabel != null) {
                oldLabel.getStyleClass().remove(JfxStyles.TABS_ACTIVE);
            }
            if (newLabel != null && !newLabel.getStyleClass().contains(JfxStyles.TABS_ACTIVE)) {
                newLabel.getStyleClass().add(JfxStyles.TABS_ACTIVE);
            }

            // 切换内容可见
            Node oldContent = tabContents.get(oldIndex);
            Node newContent = tabContents.get(newIndex);
            if (oldContent != null) {
                oldContent.setVisible(false);
                oldContent.setManaged(false);
            }
            if (newContent != null) {
                newContent.setVisible(true);
                newContent.setManaged(true);
            }

            // 触发指示条重定位
            if (onIndicatorMove != null) onIndicatorMove.run();
            // 触发 onChange 回调
            if (onChangeCallback != null) onChangeCallback.run();
            return true;
        }

        /**
         * 按 key 切换当前 tab。未找到 key 时无操作。
         * @return true 表示切换成功,false 表示 key 不存在或被拒绝
         */
        public boolean selectByKey(String key) {
            int idx = tabKeys.indexOf(key);
            if (idx < 0) return false;
            return setCurrent(idx);
        }

        /** 前进到下一 tab(已是最后一项则无操作)。 */
        public boolean next() { return setCurrent(current + 1); }

        /** 后退到上一 tab(已是第一项则无操作)。 */
        public boolean prev() { return setCurrent(current - 1); }
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
