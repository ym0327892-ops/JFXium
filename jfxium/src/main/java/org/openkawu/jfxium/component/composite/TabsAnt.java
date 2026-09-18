package org.openkawu.jfxium.component.composite;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.layout.Region;

import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.TextUtils;

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
 *   <li><b>位置</b>：TOP / BOTTOM / LEFT / RIGHT（LINE 形态四个方向都带指示条）</li>
 *   <li><b>初始选中</b>：activeIndex / activeKey 指定初始激活的 tab，自动跳过禁用项</li>
 *   <li><b>禁用</b>：单个标签页可禁用</li>
 *   <li><b>额外操作区</b>：extraLeft / extraRight 放标签栏两侧附加节点</li>
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
 *     .size(org.openkawu.jfxium.core.token.Size.MIDDLE)
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
    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。
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
        private String activeKey = null;
        // runtime 控制器:build() 后装配,支持不重建节点切换当前 tab(BUG #51 推广, 对齐 StepsAnt.Controller)
        private Controller controller;

        public Builder tab(String key, String label, Node content) {
            tabs.add(new TabItem(key, TextUtils.safeText(label), safeContent(content), false));
            return this;
        }

        public Builder tab(String key, String label, Node content, boolean disabled) {
            tabs.add(new TabItem(key, TextUtils.safeText(label), safeContent(content), disabled));
            return this;
        }

        public Builder type(Type type) { this.type = type != null ? type : Type.LINE; return this; }
        public Builder size(Size size) { this.size = size != null ? size : Size.MIDDLE; return this; }
        public Builder tabPlacement(TabPlacement p) { this.placement = p != null ? p : TabPlacement.TOP; return this; }
        public Builder centered(boolean c) { this.centered = c; return this; }
        public Builder extraLeft(Node n) { this.extraLeft = n; return this; }
        public Builder extraRight(Node n) { this.extraRight = n; return this; }
        public Builder onChange(Consumer<String> c) { this.onChange = c; return this; }

        /**
         * 指定初始激活的 tab 下标（0-based）。越界钳制到 0；指向禁用 tab 时自动让位到最近的可用 tab。
         * <p>{@link #activeKey(String)} 已设置且 key 存在时以 activeKey 为准。</p>
         */
        public Builder activeIndex(int index) { this.activeIndex = index; return this; }

        /**
         * 按 key 指定初始激活的 tab。优先于 {@link #activeIndex(int)}；key 未找到时退回 activeIndex。
         */
        public Builder activeKey(String key) { this.activeKey = key; return this; }

        public Node build() {
            if (tabs.isEmpty()) {
                VBox empty = new VBox();
                applyStyles(empty);
                return empty;
            }

            // 装配 Controller(每次 build 新建一个,与已构造节点树绑定)；
            // 初始下标经 resolveInitialIndex 解析（禁用自动让位，BUG #144）
            this.controller = new Controller(tabs.size(), resolveInitialIndex());
            return buildTabsTree();
        }

        /**
         * 解析初始激活下标：activeKey（找到时）> activeIndex > 0，
         * 指向禁用项时向后让位优先，其次向前；全部禁用则保留钳制后的下标。
         */
        private int resolveInitialIndex() {
            int total = tabs.size();
            int candidate = activeIndex;
            if (activeKey != null) {
                int byKey = -1;
                for (int i = 0; i < total; i++) {
                    if (activeKey.equals(tabs.get(i).key)) {
                        byKey = i;
                        break;
                    }
                }
                if (byKey >= 0) {
                    candidate = byKey;
                }
            }
            if (candidate < 0 || candidate >= total) {
                candidate = 0;
            }
            if (!tabs.get(candidate).disabled) {
                return candidate;
            }
            for (int i = candidate + 1; i < total; i++) {
                if (!tabs.get(i).disabled) {
                    return i;
                }
            }
            for (int i = candidate - 1; i >= 0; i--) {
                if (!tabs.get(i).disabled) {
                    return i;
                }
            }
            return candidate;
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
            controller.setOnChangeCallback(changeCallback);
            // BUG #144：指示条节点由 wireIndicator 直接存到 Controller，
            // 不再通过 tabBar.getParent() + children 强转链反查（结构一改就静默失效）
            controller.setOnIndicatorMove(this::refreshIndicator);

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
                // BUG #144-P0：垂直场景 contentArea 的 parent 是 HBox 不是 root VBox，
                // 必须对 layout 设 VBox.setVgrow 才能在 root 内垂直撑满
                VBox.setVgrow(layout, Priority.ALWAYS);
                root.getChildren().add(layout);
            } else {
                if (placement == TabPlacement.TOP) {
                    root.getChildren().addAll(tabBar, contentArea);
                } else {
                    root.getChildren().addAll(contentArea, tabBar);
                }
                // 水平场景 contentArea 直接是 root VBox 的子节点
                VBox.setVgrow(contentArea, Priority.ALWAYS);
            }
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

            // LINE 模式：指示条贴着标签栏 —— 水平形态在下方，垂直形态（LEFT/RIGHT）在内侧。
            // BUG #144：此前垂直形态完全没有指示条，现补齐；
            // 指示条节点直接存到 Controller（wireIndicator），不再靠反查节点树。
            if (type == Type.LINE) {
                if (!isVertical) {
                    VBox wrapper = new VBox(0);
                    wrapper.setAlignment(Pos.TOP_LEFT);
                    wrapper.getChildren().add(tabBar);

                    // 指示条容器 - 使用 Pane 实现绝对定位
                    Pane indicatorPane = new Pane();
                    indicatorPane.getStyleClass().add(JfxStyles.TABS_INDICATOR_PANE);
                    Region indicator = newIndicator(false);
                    indicatorPane.getChildren().add(indicator);
                    wrapper.getChildren().add(indicatorPane);

                    // 绑定容器宽度到 tabBar
                    indicatorPane.prefWidthProperty().bind(tabBar.widthProperty());
                    wireIndicator(wrapper, indicatorPane, false);
                    return wrapper;
                }

                HBox wrapper = new HBox(0);
                wrapper.setAlignment(Pos.TOP_LEFT);

                Pane indicatorPane = new Pane();
                indicatorPane.getStyleClass().add(JfxStyles.TABS_INDICATOR_PANE_VERTICAL);
                Region indicator = newIndicator(true);
                indicatorPane.getChildren().add(indicator);

                // LEFT：指示条在标签栏右侧（紧邻内容区）；RIGHT：在标签栏左侧
                if (placement == TabPlacement.LEFT) {
                    wrapper.getChildren().addAll(tabBar, indicatorPane);
                } else {
                    wrapper.getChildren().addAll(indicatorPane, tabBar);
                }

                // 绑定容器高度到 tabBar
                indicatorPane.prefHeightProperty().bind(tabBar.heightProperty());
                wireIndicator(wrapper, indicatorPane, true);
                return wrapper;
            }

            return tabBar;
        }

        /** 创建指示条节点并记到 Controller（初始隐藏，首次定位成功后才显示）。 */
        private Region newIndicator(boolean vertical) {
            Region indicator = new Region();
            indicator.getStyleClass().add(vertical ? JfxStyles.TABS_INDICATOR_BAR_VERTICAL : JfxStyles.TABS_INDICATOR_BAR);
            indicator.setVisible(false);
            controller.indicatorRef = indicator;
            return indicator;
        }

        /**
         * 为指示条挂布局监听并在挂入场景后完成首次定位。
         *
         * <p>BUG #144：取代旧「PauseTransition 300ms 延迟 + 双重 runLater + 多套监听重复触发」实现 ——
         * 场景挂接后双 runLater（保证在布局脉冲之后执行）是唯一首次定位入口，
         * label / 标签栏的 layoutBounds 监听负责后续动态重定位。</p>
         */
        private void wireIndicator(Node wrapper, Pane indicatorPane, boolean vertical) {
            controller.indicatorPaneRef = indicatorPane;
            controller.verticalIndicator = vertical;

            for (Label label : controller.tabLabels) {
                label.layoutBoundsProperty().addListener((obs, old, val) -> refreshIndicator());
            }
            controller.tabBarRef.layoutBoundsProperty().addListener((obs, old, val) -> refreshIndicator());

            wrapper.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    Platform.runLater(() -> Platform.runLater(() -> refreshIndicator()));
                }
            });
            // 边缘场景：build 时节点已挂场景（如先建子树再重组）
            if (wrapper.getScene() != null) {
                Platform.runLater(() -> Platform.runLater(() -> refreshIndicator()));
            }
        }

        /** 指示条重定位统一入口（直接读 Controller 持有引用，不反查节点树）。 */
        private void refreshIndicator() {
            if (controller == null || controller.indicatorRef == null) {
                return;
            }
            updateIndicator(controller.indicatorRef, controller.tabLabels, controller.getCurrent(), controller.verticalIndicator);
        }

        private StackPane createContentArea() {
            StackPane contentArea = new StackPane();
            // 背景与内边距走 .jfx-tabs-content 修饰类(LESS 中 -color-bg-transparent + 16px padding)
            contentArea.getStyleClass().add(JfxStyles.TABS_CONTENT);

            for (int i = 0; i < tabs.size(); i++) {
                Node content = tabs.get(i).content;
                // BUG #144：二次 build() 复用同一批 content 时先释放旧 parent（JavaFX 单亲规则）
                detachFromParent(content);
                content.setVisible(i == controller.getCurrent());
                content.setManaged(i == controller.getCurrent());
                contentArea.getChildren().add(content);
                controller.tabContents.add(content);
            }

            return contentArea;
        }

        private Label createTabLabel(TabItem item, boolean isActive) {
            Label label = new Label(item.label);
            // padding 完全由 LESS token 驱动（@tabs-label-padding-* / @tabs-card-padding-*，BUG #144）。
            // Java 端不得 setPadding —— 直设属性优先级高于样式表，会盖掉 LESS，破坏紧凑模式联动。

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

        private void updateIndicator(Region indicator, List<Label> labels, int index, boolean vertical) {
            if (index < 0 || index >= labels.size()) return;

            Label label = labels.get(index);

            // 获取标签的实际边界（相对标签栏，与指示条容器的坐标系原点一致）
            javafx.geometry.Bounds bounds = label.getBoundsInParent();

            if (vertical) {
                double labelHeight = bounds.getHeight();
                // 高度为 0 说明未布局完成，跳过等下次触发（BUG #144：取代 prefWidth=100 魔法兑底）
                if (labelHeight <= 0) return;
                indicator.setPrefHeight(labelHeight);
                indicator.setLayoutY(bounds.getMinY());
            } else {
                double labelWidth = bounds.getWidth();
                if (labelWidth <= 0) return;
                indicator.setPrefWidth(labelWidth);
                indicator.setLayoutX(bounds.getMinX());
            }

            // 首次定位成功后显示指示条（newIndicator 初始隐藏）
            if (!indicator.isVisible()) {
                indicator.setVisible(true);
            }
        }

        /**
         * 挂载前释放节点的旧 parent（JavaFX 单亲规则）。
         * BUG #144：同一 Builder 二次 build() 时若 content 仍挂在旧树上，
         * 直接 add 会抛 IllegalArgumentException。
         */
        private static void detachFromParent(Node node) {
            Parent parent = node.getParent();
            if (parent instanceof Pane pane) {
                pane.getChildren().remove(node);
            }
        }

        // safeContent 保留本地:语义不同于 TextUtils.safeText(s, fb),
        // null 时需要返回一个 0×0 的占位 Region,避免 TabItem.content 为 null 导致 NPE。
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
        Node tabBarRef;          // 标签栏容器(指示条布局监听用)
        StackPane contentAreaRef; // 内容容器(预留扩展)
        Region indicatorRef;      // 指示条节点（BUG #144：直接引用，取代反查节点树）
        Pane indicatorPaneRef;    // 指示条容器（Pane 绝对定位）
        boolean verticalIndicator; // 指示条是否垂直形态（LEFT/RIGHT placement）

        private final int total;
        private int current;
        // 注入:onChange / 指示条移动（package-private setter，外部不可直接赋 null 破坏回调）
        private Runnable onChangeCallback;
        private Runnable onIndicatorMove;

        void setOnChangeCallback(Runnable r) { this.onChangeCallback = r; }
        void setOnIndicatorMove(Runnable r) { this.onIndicatorMove = r; }

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
     *   <li>LINE 模式下指示条重定位（含垂直形态）</li>
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
         * 按 key 切换当前 tab。null 或空串直接返回 false；未找到 key 时也返回 false。
         * @return true 表示切换成功,false 表示 key 无效或不存在或被拒绝
         */
        public boolean selectByKey(String key) {
            if (key == null || key.isEmpty()) return false;
            int idx = tabKeys.indexOf(key);
            if (idx < 0) return false;
            return setCurrent(idx);
        }

        /**
         * 前进到下一个<b>可用</b> tab（跳过禁用项，已是末尾则无操作）。
         * @return true 表示切换成功,false 表示后方无可用 tab
         */
        public boolean next() {
            for (int i = current + 1; i < total; i++) {
                if (!tabDisabled[i] && setCurrent(i)) return true;
            }
            return false;
        }

        /**
         * 后退到上一个<b>可用</b> tab（跳过禁用项，已是开头则无操作）。
         * @return true 表示切换成功,false 表示前方无可用 tab
         */
        public boolean prev() {
            for (int i = current - 1; i >= 0; i--) {
                if (!tabDisabled[i] && setCurrent(i)) return true;
            }
            return false;
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
