package org.openkawu.jfxium.component.composite;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.overlay.DropdownAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.A11y;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
 *   <li><b>位置</b>：TOP / BOTTOM（LINE 形态带下划线指示条）</li>
 *   <li><b>初始选中</b>：activeIndex / activeKey 指定初始激活的 tab，自动跳过禁用项</li>
 *   <li><b>禁用</b>：单个标签页可禁用</li>
 *   <li><b>可关闭</b>：closable 渲染 × 按钮，支持 onBeforeClose 拦截 / onClose 通知</li>
 *   <li><b>动态增删</b>：{@link Controller#addTab} / {@link Controller#removeTab} / {@link Controller#updateTab}</li>
 *   <li><b>右侧操作菜单</b>：标签列表 + 关闭当前 / 关闭其他 / 关闭全部（overflowMenu / showClose* 控制显示）</li>
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
 *
 * // 可关闭 + 动态增删
 * Node closable = TabsAnt.create()
 *     .closable(true)                       // 所有 tab 默认带 ×
 *     .tab("a", "A", a)
 *     .tab("b", "B", b)
 *     .onBeforeClose(key -> canClose(key))  // 返回 false 阻止关闭
 *     .onClose(key -> cleanup(key))
 *     .build();
 * TabsAnt.Controller c2 = TabsAnt.controllerOf(closable);
 * c2.addTab("c", "C", c);
 * c2.updateTab("a", "A2", a2);
 * c2.removeTab("b");
 * c2.closeAll();
 * }</pre>
 */
public class TabsAnt {

    public enum Type { LINE, CARD }
    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。
    public enum TabPlacement { TOP, BOTTOM }

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
        // 所有 tab 的默认可关闭状态（单个 tab 可用 5 参 tab(...) 覆盖）
        private boolean closable = false;
        // 右侧「标签操作」菜单：标签列表 + 关闭当前 / 关闭其他 / 关闭全部
        private boolean overflowMenu = true;
        private boolean showCloseCurrent = true;
        private boolean showCloseOthers = true;
        private boolean showCloseAll = true;
        // 用户点 × 关闭前拦截：返回 false 阻止关闭
        private Predicate<String> onBeforeClose = null;
        // 关闭完成后通知（closeTab / closeAll / closeOthers 触发）
        private Consumer<String> onClose = null;
        // runtime 控制器:build() 后装配,支持不重建节点切换当前 tab(BUG #51 推广, 对齐 StepsAnt.Controller)
        private Controller controller;

        public Builder tab(String key, String label, Node content) {
            tabs.add(new TabItem(key, TextUtils.safeText(label), safeContent(content), false, null));
            return this;
        }

        public Builder tab(String key, String label, Node content, boolean disabled) {
            tabs.add(new TabItem(key, TextUtils.safeText(label), safeContent(content), disabled, null));
            return this;
        }

        /**
         * 单个 tab 独立指定 disabled / closable。
         * closable 为 false 时，即使全局 {@link #closable(boolean)} 为 true，该 tab 也不渲染 ×。
         */
        public Builder tab(String key, String label, Node content, boolean disabled, boolean closable) {
            tabs.add(new TabItem(key, TextUtils.safeText(label), safeContent(content), disabled, closable));
            return this;
        }

        public Builder type(Type type) { this.type = type != null ? type : Type.LINE; return this; }
        public Builder size(Size size) { this.size = size != null ? size : Size.MIDDLE; return this; }
        public Builder tabPlacement(TabPlacement p) { this.placement = p != null ? p : TabPlacement.TOP; return this; }
        public Builder centered(boolean c) { this.centered = c; return this; }
        public Builder extraLeft(Node n) { this.extraLeft = n; return this; }
        public Builder extraRight(Node n) { this.extraRight = n; return this; }
        public Builder onChange(Consumer<String> c) { this.onChange = c; return this; }

        /** 所有 tab 默认是否可关闭（单个 tab 可用 5 参 {@code tab(...)} 覆盖）。 */
        public Builder closable(boolean closable) { this.closable = closable; return this; }

        /** 所有 tab 默认渲染 × 关闭按钮。 */
        public Builder closable() { return closable(true); }

        /** 是否显示右侧「标签操作」菜单（默认 true）。 */
        public Builder overflowMenu(boolean overflowMenu) { this.overflowMenu = overflowMenu; return this; }

        /** 显示右侧「标签操作」菜单。 */
        public Builder overflowMenu() { return overflowMenu(true); }

        /** 右侧菜单是否显示「关闭当前」（默认 true）。 */
        public Builder showCloseCurrent(boolean show) { this.showCloseCurrent = show; return this; }

        /** 右侧菜单是否显示「关闭其他」（默认 true）。 */
        public Builder showCloseOthers(boolean show) { this.showCloseOthers = show; return this; }

        /** 右侧菜单是否显示「关闭全部」（默认 true）。 */
        public Builder showCloseAll(boolean show) { this.showCloseAll = show; return this; }

        /** 点 × 关闭前拦截；返回 false 阻止本次关闭（null = 不拦截）。 */
        public Builder onBeforeClose(Predicate<String> onBeforeClose) { this.onBeforeClose = onBeforeClose; return this; }

        /** 关闭完成后通知（tab key）；仅 {@link Controller#closeTab} / closeAll / closeOthers 触发。 */
        public Builder onClose(Consumer<String> onClose) { this.onClose = onClose; return this; }

        /** 解析单个 tab 的最终 closable：显式值优先，否则继承全局默认。 */
        private boolean resolveClosable(TabItem item) {
            return item.closable != null ? item.closable : closable;
        }

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
            // 空 tabs 也构建完整骨架：允许 build 后通过 Controller.addTab(...) 动态填充
            // 装配 Controller(每次 build 新建一个,与已构造节点树绑定)；
            // 初始下标经 resolveInitialIndex 解析（禁用自动让位，BUG #144）
            this.controller = new Controller(this, new ArrayList<>(tabs), resolveInitialIndex());
            return buildTabsTree();
        }

        /**
         * 解析初始激活下标：activeKey（找到时）> activeIndex > 0，
         * 指向禁用项时向后让位优先，其次向前；全部禁用则保留钳制后的下标。
         * 空 tabs 返回 -1（无选中）。
         */
        private int resolveInitialIndex() {
            int total = tabs.size();
            if (total == 0) {
                return -1;
            }
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
         * <p>用例:业务方在外部根据数据变化编程式切换 / 增删 / 关闭 tab,只需调
         * {@link Controller} 的对应方法,不必 rebuild 整个 tabs 节点树
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

            // 创建标签栏
            Node tabBar = createTabBar();

            // 创建内容区域
            StackPane contentArea = createContentArea();
            controller.contentAreaRef = contentArea;
            // 右侧「标签操作」菜单按当前 items 装配
            controller.rebuildActionsMenu();

            if (placement == TabPlacement.TOP) {
                root.getChildren().addAll(tabBar, contentArea);
            } else {
                root.getChildren().addAll(contentArea, tabBar);
            }
            VBox.setVgrow(contentArea, Priority.ALWAYS);
            applyStyles(root);
            return root;
        }

        private Node createTabBar() {
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

            // 创建标签（Controller 统一负责标签外观 / 点击 / × 关闭，动态增删复用同一套）
            for (int i = 0; i < controller.items.size(); i++) {
                TabItem item = controller.items.get(i);
                Label label = controller.createLabel(item, i == controller.getCurrent());
                controller.tabLabels.add(label);
                controller.tabKeys.add(item.key);
                tabBar.getChildren().add(label);
            }

            // 右侧「标签操作」菜单 + extraRight 统一浮在标签栏右侧：
            // tabBar 只负责标签横向排布，操作入口不会被溢出的标签挤出可视区。
            StackPane actionsHost = new StackPane();
            actionsHost.getStyleClass().add(JfxStyles.TABS_ACTIONS_HOST);
            controller.actionsHost = actionsHost;

            HBox trailing = new HBox(0);
            trailing.setAlignment(Pos.CENTER_RIGHT);
            trailing.setPickOnBounds(false);   // 空白区域不拦截标签点击
            trailing.getChildren().add(actionsHost);
            if (extraRight != null) {
                trailing.getChildren().add(extraRight);
            }

            StackPane barHost = new StackPane(tabBar, trailing);
            StackPane.setAlignment(trailing, Pos.CENTER_RIGHT);
            // 标签溢出时裁掉多余部分，避免画到内容区
            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(barHost.widthProperty());
            clip.heightProperty().bind(barHost.heightProperty());
            barHost.setClip(clip);

            // LINE 模式：指示条贴着标签栏下方。
            // BUG #144：指示条节点直接存到 Controller（wireIndicator），不再靠反查节点树。
            if (type == Type.LINE) {
                VBox wrapper = new VBox(0);
                wrapper.setAlignment(Pos.TOP_LEFT);
                wrapper.getChildren().add(barHost);

                // 指示条容器 - 使用 Pane 实现绝对定位
                Pane indicatorPane = new Pane();
                indicatorPane.getStyleClass().add(JfxStyles.TABS_INDICATOR_PANE);
                Region indicator = newIndicator();
                indicatorPane.getChildren().add(indicator);
                wrapper.getChildren().add(indicatorPane);

                // 绑定容器宽度到标签栏
                indicatorPane.prefWidthProperty().bind(barHost.widthProperty());
                wireIndicator(wrapper, indicatorPane);
                return wrapper;
            }

            return barHost;
        }

        /** 创建指示条节点并记到 Controller（初始隐藏，首次定位成功后才显示）。 */
        private Region newIndicator() {
            Region indicator = new Region();
            indicator.getStyleClass().add(JfxStyles.TABS_INDICATOR_BAR);
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
        private void wireIndicator(Node wrapper, Pane indicatorPane) {
            controller.indicatorPaneRef = indicatorPane;

            // 标签自身的 layoutBounds 监听在 Controller.createLabel 中挂载（动态新增标签同样生效）
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
            updateIndicator(controller.indicatorRef, controller.tabLabels, controller.getCurrent());
        }

        private StackPane createContentArea() {
            StackPane contentArea = new StackPane();
            // 背景与内边距走 .jfx-tabs-content 修饰类(LESS 中 -color-bg-transparent + 16px padding)
            contentArea.getStyleClass().add(JfxStyles.TABS_CONTENT);

            for (int i = 0; i < controller.items.size(); i++) {
                Node content = controller.items.get(i).content;
                // BUG #144：二次 build() 复用同一批 content 时先释放旧 parent（JavaFX 单亲规则）
                detachFromParent(content);
                boolean active = i == controller.getCurrent();
                content.setVisible(active);
                content.setManaged(active);
                contentArea.getChildren().add(content);
                controller.tabContents.add(content);
            }

            return contentArea;
        }

        private void updateIndicator(Region indicator, List<Label> labels, int index) {
            if (index < 0 || index >= labels.size()) return;

            Label label = labels.get(index);

            // 获取标签的实际边界（相对标签栏，与指示条容器的坐标系原点一致）
            javafx.geometry.Bounds bounds = label.getBoundsInParent();
            double labelWidth = bounds.getWidth();
            // 宽度为 0 说明未布局完成，跳过等下次触发（BUG #144：取代 prefWidth=100 魔法兑底）
            if (labelWidth <= 0) return;

            indicator.setPrefWidth(labelWidth);
            indicator.setLayoutX(bounds.getMinX());

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
     * 标签页运行时控制器:切换选中、动态增删改、关闭语义(BUG #51 推广, 对齐 StepsAnt.Controller)。
     *
     * <ul>
     *   <li><b>切换</b>：{@link #setCurrent(int)} / {@link #selectByKey(String)} / next / prev</li>
     *   <li><b>增删改</b>：{@link #addTab} / {@link #removeTab} / {@link #updateTab} / setTabDisabled / setTabClosable</li>
     *   <li><b>用户关闭</b>：{@link #closeTab} / closeCurrent / closeOthers / closeAll（尊重 closable + onBeforeClose）</li>
     * </ul>
     *
     * <pre>{@code
     * Node tabs = TabsAnt.create().tab("a", "A", a).tab("b", "B", b).build();
     * TabsAnt.Controller ctrl = TabsAnt.controllerOf(tabs);
     * ctrl.selectByKey("b");       // 切到 key="b" 的 tab
     * ctrl.addTab("c", "C", c);    // 动态新增
     * ctrl.closeTab("a");          // 用户语义关闭
     * }</pre>
     */
    public static class Controller {
        /** root.getProperties() 的 key,支持 {@link TabsAnt#controllerOf(Node)} 静态查找 */
        public static final String PROPERTY_KEY = "jfxium.tabs.controller";

        private static final String ACTION_TAB_PREFIX = "tab:";
        private static final String ACTION_CLOSE_CURRENT = "__close_current";
        private static final String ACTION_CLOSE_OTHERS = "__close_others";
        private static final String ACTION_CLOSE_ALL = "__close_all";

        // 与 items 一一对应的运行时节点（动态增删时同步维护）
        final List<TabItem> items;
        final List<Label> tabLabels = new ArrayList<>();
        final List<Node> tabContents = new ArrayList<>();
        final List<String> tabKeys = new ArrayList<>();
        HBox tabBarRef;          // 标签栏容器(指示条布局监听用)
        StackPane contentAreaRef; // 内容容器
        StackPane actionsHost;    // 右侧标签操作菜单容器
        Region indicatorRef;      // 指示条节点（BUG #144：直接引用，取代反查节点树）
        Pane indicatorPaneRef;    // 指示条容器（Pane 绝对定位）

        private final Builder owner;
        private int current;
        // 注入:onChange / 指示条移动（package-private setter，外部不可直接赋 null 破坏回调）
        private Runnable onChangeCallback;
        private Runnable onIndicatorMove;

        void setOnChangeCallback(Runnable r) { this.onChangeCallback = r; }
        void setOnIndicatorMove(Runnable r) { this.onIndicatorMove = r; }

        Controller(Builder owner, List<TabItem> items, int current) {
            this.owner = owner;
            this.items = items;
            this.current = items.isEmpty() ? -1 : Math.max(0, Math.min(current, items.size() - 1));
        }

        // ============================================================
        // 只读 API
        // ============================================================

        /** 当前 tab 下标(0-based)；无 tab 时为 -1。 */
        public int getCurrent() { return current; }

        /** 总 tab 数（动态变化）。 */
        public int getTotal() { return items.size(); }

        /** 是否没有任何 tab。 */
        public boolean isEmpty() { return items.isEmpty(); }

        /** 当前 tab 的 key(未注册或无 tab 时返回 null)。 */
        public String getCurrentKey() { return getKey(current); }

        /** 指定下标的 tab key(越界返回 null)。 */
        public String getKey(int index) {
            return (index >= 0 && index < tabKeys.size()) ? tabKeys.get(index) : null;
        }

        /** 所有 tab key 的快照（顺序与展示一致）。 */
        public List<String> getKeys() { return List.copyOf(tabKeys); }

        /** key 对应下标；不存在返回 -1。 */
        public int indexOf(String key) { return key == null ? -1 : tabKeys.indexOf(key); }

        /** 指定下标的 tab 是否禁用。 */
        public boolean isDisabled(int index) {
            return index >= 0 && index < items.size() && items.get(index).disabled;
        }

        /** 指定 key 的 tab 是否禁用。 */
        public boolean isDisabled(String key) {
            int idx = indexOf(key);
            return idx >= 0 && isDisabled(idx);
        }

        /** 当前 tab 是否处于禁用态。 */
        public boolean isCurrentDisabled() { return isDisabled(current); }

        /** 指定下标的 tab 是否可关闭。 */
        public boolean isClosable(int index) {
            return index >= 0 && index < items.size() && owner.resolveClosable(items.get(index));
        }

        /** 指定 key 的 tab 是否可关闭。 */
        public boolean isClosable(String key) {
            int idx = indexOf(key);
            return idx >= 0 && isClosable(idx);
        }

        // ============================================================
        // 写入 API：选中
        // ============================================================

        /**
         * 切换当前 tab 下标(0-based)。越界 / 禁用 / 与当前相同均返回 false。
         * 切换成功后同步 active styleClass、内容 visible/managed、指示条与 onChange。
         */
        public boolean setCurrent(int newIndex) {
            if (newIndex < 0 || newIndex >= items.size()) return false;
            if (newIndex == current) return false;
            if (items.get(newIndex).disabled) return false;

            current = newIndex;
            refreshActiveState();
            if (onChangeCallback != null) onChangeCallback.run();
            // 菜单里的「关闭当前 / 关闭其他」可用态依赖当前 tab
            rebuildActionsMenu();
            return true;
        }

        /** 按 key 切换当前 tab。null / 空串 / 不存在 / 被拒绝均返回 false。 */
        public boolean selectByKey(String key) {
            int idx = indexOf(key);
            return idx >= 0 && setCurrent(idx);
        }

        /** 前进到下一个<b>可用</b> tab（跳过禁用项）。 */
        public boolean next() {
            for (int i = current + 1; i < items.size(); i++) {
                if (!items.get(i).disabled && setCurrent(i)) return true;
            }
            return false;
        }

        /** 后退到上一个<b>可用</b> tab（跳过禁用项）。 */
        public boolean prev() {
            for (int i = current - 1; i >= 0; i--) {
                if (!items.get(i).disabled && setCurrent(i)) return true;
            }
            return false;
        }

        // ============================================================
        // 写入 API：动态增删改
        // ============================================================

        /** 追加一个 tab（使用全局 closable 默认值）。key 为空或重复返回 false。 */
        public boolean addTab(String key, String label, Node content) {
            return addTab(key, label, content, false, owner.closable);
        }

        /** 追加一个 tab。key 为空或重复返回 false。 */
        public boolean addTab(String key, String label, Node content, boolean disabled, boolean closable) {
            if (key == null || key.isEmpty() || indexOf(key) >= 0) return false;

            TabItem item = new TabItem(key, TextUtils.safeText(label), Builder.safeContent(content),
                    disabled, closable);
            items.add(item);
            tabKeys.add(key);

            Label labelNode = createLabel(item, items.size() - 1 == current);
            tabLabels.add(labelNode);
            insertLabelNode(labelNode);

            Node contentNode = item.content;
            Builder.detachFromParent(contentNode);
            boolean active = items.size() - 1 == current;
            contentNode.setVisible(active);
            contentNode.setManaged(active);
            tabContents.add(contentNode);
            if (contentAreaRef != null) {
                contentAreaRef.getChildren().add(contentNode);
            }

            if (current < 0) {
                current = firstEnabled(0);
                refreshActiveState();
                if (current >= 0 && onChangeCallback != null) onChangeCallback.run();
            } else {
                requestIndicatorRefresh();
            }
            rebuildActionsMenu();
            return true;
        }

        /** 按 key 程序化移除（不触发 onBeforeClose / onClose）。 */
        public boolean removeTab(String key) {
            int idx = indexOf(key);
            return idx >= 0 && removeTab(idx);
        }

        /** 按下标程序化移除（不触发 onBeforeClose / onClose）。 */
        public boolean removeTab(int index) {
            if (index < 0 || index >= items.size()) return false;

            int oldCurrent = current;
            items.remove(index);
            tabKeys.remove(index);
            Label label = tabLabels.remove(index);
            Node content = tabContents.remove(index);

            if (tabBarRef != null && label != null) {
                tabBarRef.getChildren().remove(label);
            }
            if (contentAreaRef != null && content != null) {
                contentAreaRef.getChildren().remove(content);
            }

            boolean selectionChanged = false;
            if (items.isEmpty()) {
                current = -1;
            } else if (index == oldCurrent) {
                // 优先选右邻，其次左邻；全禁用则无选中
                int candidate = firstEnabled(index);
                if (candidate < 0) candidate = firstEnabled(index - 1);
                current = candidate;
                selectionChanged = current >= 0;
            } else if (index < oldCurrent) {
                // 当前 tab 下标左移，仍然选中同一个
                current = oldCurrent - 1;
            }

            refreshActiveState();
            if (selectionChanged && onChangeCallback != null) onChangeCallback.run();
            rebuildActionsMenu();
            return true;
        }

        /** 移除全部 tab（程序化，不触发 onClose）。 */
        public int removeAllTabs() {
            int total = items.size();
            for (int i = items.size() - 1; i >= 0; i--) {
                removeTab(i);
            }
            return total;
        }

        /** 更新 tab 的标题和/或内容；content 为 null 时保留原内容。 */
        public boolean updateTab(String key, String label, Node content) {
            int idx = indexOf(key);
            if (idx < 0) return false;

            TabItem item = items.get(idx);
            item.label = TextUtils.safeText(label);
            tabLabels.get(idx).setText(item.label);

            if (content != null) {
                Node newContent = Builder.safeContent(content);
                Node oldContent = tabContents.get(idx);
                if (oldContent != newContent) {
                    Builder.detachFromParent(newContent);
                    boolean active = idx == current;
                    newContent.setVisible(active);
                    newContent.setManaged(active);
                    item.content = newContent;
                    tabContents.set(idx, newContent);
                    if (contentAreaRef != null) {
                        contentAreaRef.getChildren().set(idx, newContent);
                    }
                }
            }

            requestIndicatorRefresh();
            rebuildActionsMenu();
            return true;
        }

        /** 只更新标题。 */
        public boolean setTabLabel(String key, String label) {
            int idx = indexOf(key);
            if (idx < 0) return false;
            items.get(idx).label = TextUtils.safeText(label);
            tabLabels.get(idx).setText(items.get(idx).label);
            requestIndicatorRefresh();
            rebuildActionsMenu();
            return true;
        }

        /** 只更新内容。 */
        public boolean setTabContent(String key, Node content) {
            int idx = indexOf(key);
            return idx >= 0 && updateTab(key, items.get(idx).label, content);
        }

        /** 运行时切换禁用态；禁用当前 tab 时自动让位到最近可用 tab。 */
        public boolean setTabDisabled(String key, boolean disabled) {
            int idx = indexOf(key);
            if (idx < 0) return false;

            TabItem item = items.get(idx);
            item.disabled = disabled;
            Label label = tabLabels.get(idx);
            label.setDisable(disabled);
            if (disabled) {
                if (!label.getStyleClass().contains(JfxStyles.TABS_DISABLED)) {
                    label.getStyleClass().add(JfxStyles.TABS_DISABLED);
                }
            } else {
                label.getStyleClass().remove(JfxStyles.TABS_DISABLED);
            }

            if (disabled && idx == current) {
                int candidate = firstEnabled(idx + 1);
                if (candidate < 0) candidate = firstEnabled(idx - 1);
                current = candidate;
                refreshActiveState();
                if (current >= 0 && onChangeCallback != null) onChangeCallback.run();
            }
            rebuildActionsMenu();
            return true;
        }

        /** 运行时切换可关闭态（同步增删 × 按钮）。 */
        public boolean setTabClosable(String key, boolean closable) {
            int idx = indexOf(key);
            if (idx < 0) return false;
            items.get(idx).closable = closable;
            applyCloseAffordance(tabLabels.get(idx), items.get(idx));
            requestIndicatorRefresh();
            rebuildActionsMenu();
            return true;
        }

        // ============================================================
        // 写入 API：关闭（用户语义：尊重 closable / onBeforeClose / onClose）
        // ============================================================

        /**
         * 关闭指定 tab。与 {@link #removeTab(String)} 的区别：
         * 只有 closable 且未被 onBeforeClose 拦截时才关闭，并触发 onClose 通知。
         */
        public boolean closeTab(String key) {
            int idx = indexOf(key);
            if (idx < 0) return false;
            TabItem item = items.get(idx);
            if (item.disabled || !owner.resolveClosable(item)) return false;
            if (owner.onBeforeClose != null && !owner.onBeforeClose.test(key)) return false;
            if (!removeTab(idx)) return false;
            if (owner.onClose != null) owner.onClose.accept(key);
            return true;
        }

        /** 关闭当前 tab（用户语义）。 */
        public boolean closeCurrent() { return closeTab(getCurrentKey()); }

        /** 关闭除 keepKey 外的所有可关闭 tab（用户语义）。返回实际关闭数量。 */
        public int closeOthers(String keepKey) {
            int closed = 0;
            for (String key : getKeys()) {
                if (key.equals(keepKey)) continue;
                if (closeTab(key)) closed++;
            }
            return closed;
        }

        /** 关闭所有可关闭 tab（用户语义）。返回实际关闭数量。 */
        public int closeAll() {
            int closed = 0;
            for (String key : getKeys()) {
                if (closeTab(key)) closed++;
            }
            return closed;
        }

        // ============================================================
        // 内部：标签节点 / 选中态 / 右侧操作菜单
        // ============================================================

        /** 创建标签节点。动态 add 与 build 共用，保证外观 / 交互一致。 */
        Label createLabel(TabItem item, boolean isActive) {
            Label label = new Label(TextUtils.safeText(item.label));
            // padding 完全由 LESS token 驱动（@tabs-label-padding-* / @tabs-card-padding-*，BUG #144）。
            // Java 端不得 setPadding —— 直设属性优先级高于样式表，会盖掉 LESS，破坏紧凑模式联动。
            label.getStyleClass().add(JfxStyles.TABS_LABEL);
            label.getStyleClass().add(owner.type == Type.CARD ? JfxStyles.TABS_LABEL_CARD : JfxStyles.TABS_LABEL_LINE);
            if (owner.size == Size.LARGE) {
                label.getStyleClass().add(JfxStyles.TABS_LABEL_LARGE);
            } else if (owner.size == Size.SMALL) {
                label.getStyleClass().add(JfxStyles.TABS_LABEL_SMALL);
            }
            if (isActive) {
                label.getStyleClass().add(JfxStyles.TABS_ACTIVE);
            }
            if (item.disabled) {
                label.setDisable(true);
                label.getStyleClass().add(JfxStyles.TABS_DISABLED);
            }

            applyCloseAffordance(label, item);

            label.setOnMouseClicked(e -> {
                if (item.disabled) return;
                // 点 × 不触发「选中该 tab」；点标题区域才切换
                if (isDescendantOf(e.getTarget(), label.getGraphic())) return;
                int idx = items.indexOf(item);
                if (idx >= 0) setCurrent(idx);
            });
            // 指示条随标签的尺寸/位置变化重定位（动态增删标签同样生效）。
            // 注意用 boundsInParent 而不是 layoutBounds：删除前面的 tab 时，剩余标签
            // 尺寸不变但位置会左移，只监听 layoutBounds 会导致指示条停留在旧位置。
            label.boundsInParentProperty().addListener((obs, oldVal, newVal) -> {
                if (onIndicatorMove != null) onIndicatorMove.run();
            });

            return label;
        }

        /** 按当前 closable 状态挂载/移除 × 按钮。 */
        private void applyCloseAffordance(Label label, TabItem item) {
            boolean closable = owner.resolveClosable(item);
            if (closable && label.getGraphic() == null) {
                A11y.FirePane closeBtn = A11y.button("a11y.close", () -> closeTab(item.key));
                closeBtn.getStyleClass().add(JfxStyles.TABS_CLOSE);
                // 用 Unicode × 渲染（IconPath.CLOSE_X 是 3×3 的填充小块，放大后会变成一个点）
                Node closeIcon = IconAnt.symbol(IconAnt.Symbol.CLOSE, 12);
                closeIcon.getStyleClass().add(JfxStyles.TABS_CLOSE_ICON);
                closeBtn.getChildren().add(closeIcon);
                label.setGraphic(closeBtn);
                label.setContentDisplay(ContentDisplay.RIGHT);
            } else if (!closable && label.getGraphic() != null) {
                label.setGraphic(null);
            }
        }

        /** 事件目标是否在指定节点子树内（用于区分「点标题」和「点 ×」）。 */
        private static boolean isDescendantOf(javafx.event.EventTarget target, Node ancestor) {
            if (ancestor == null || !(target instanceof Node node)) return false;
            Node cursor = node;
            while (cursor != null) {
                if (cursor == ancestor) return true;
                cursor = cursor.getParent();
            }
            return false;
        }

        /** 把标签插到所有 tab 之后、actionsHost / extraRight 之前。 */
        private void insertLabelNode(Label label) {
            if (!(tabBarRef instanceof Pane pane)) return;
            int idx = pane.getChildren().size();
            if (actionsHost != null) {
                int ai = pane.getChildren().indexOf(actionsHost);
                if (ai >= 0) idx = Math.min(idx, ai);
            }
            if (owner.extraRight != null) {
                int ei = pane.getChildren().indexOf(owner.extraRight);
                if (ei >= 0) idx = Math.min(idx, ei);
            }
            if (idx < 0) idx = 0;
            if (idx > pane.getChildren().size()) idx = pane.getChildren().size();
            pane.getChildren().add(idx, label);
        }

        /** 按 current 统一刷新 active styleClass + 内容 visible/managed + 指示条。 */
        private void refreshActiveState() {
            for (int i = 0; i < tabLabels.size(); i++) {
                Label label = tabLabels.get(i);
                if (i == current) {
                    if (!label.getStyleClass().contains(JfxStyles.TABS_ACTIVE)) {
                        label.getStyleClass().add(JfxStyles.TABS_ACTIVE);
                    }
                } else {
                    label.getStyleClass().remove(JfxStyles.TABS_ACTIVE);
                }
                Node content = tabContents.get(i);
                if (content != null) {
                    boolean active = i == current;
                    content.setVisible(active);
                    content.setManaged(active);
                }
            }
            requestIndicatorRefresh();
        }

        /**
         * 结构变化后立即刷新一次，并在本轮布局结束后再刷新一次（runLater）。
         * 动态删 tab 时，剩余标签的位置要等布局脉冲后才更新，仅立即刷新会停留在旧位置。
         */
        private void requestIndicatorRefresh() {
            if (onIndicatorMove == null) return;
            onIndicatorMove.run();
            Platform.runLater(onIndicatorMove);
        }

        /** 从 from 开始向右找第一个可用 tab，找不到再向左找；全禁用返回 -1。 */
        private int firstEnabled(int from) {
            for (int i = Math.max(0, from); i < items.size(); i++) {
                if (!items.get(i).disabled) return i;
            }
            for (int i = Math.min(items.size() - 1, from - 1); i >= 0; i--) {
                if (!items.get(i).disabled) return i;
            }
            return -1;
        }

        /**
         * 重建右侧「标签操作」菜单：当前 tab 列表 + 关闭当前 / 关闭其他 / 关闭全部。
         * 菜单项是 build 期快照，因此增删 tab / 切换禁用或可关闭态后都会重建。
         */
        void rebuildActionsMenu() {
            if (actionsHost == null) return;
            actionsHost.getChildren().clear();

            if (!owner.overflowMenu || items.isEmpty()) {
                actionsHost.setVisible(false);
                actionsHost.setManaged(false);
                return;
            }

            String currentKey = getCurrentKey();
            boolean anyClosable = false;
            boolean othersClosable = false;
            for (TabItem item : items) {
                if (owner.resolveClosable(item)) {
                    anyClosable = true;
                    if (!item.key.equals(currentKey)) othersClosable = true;
                }
            }
            boolean anyCloseActionShown = owner.showCloseCurrent || owner.showCloseOthers || owner.showCloseAll;
            // 只有 1 个 tab 且没有任何可用关闭动作时，右侧菜单没有意义 → 隐藏
            boolean show = items.size() >= 2 || (anyClosable && anyCloseActionShown);
            actionsHost.setVisible(show);
            actionsHost.setManaged(show);
            if (!show) return;

            ButtonAnt trigger = ButtonAnt.create();
            trigger.icon(IconAnt.symbol(IconAnt.Symbol.MENU));
            trigger.type(ButtonAnt.Type.TEXT);
            trigger.size(Size.SMALL);
            trigger.getStyleClass().add(JfxStyles.TABS_ACTIONS);
            trigger.setAccessibleText(Messages.get("tabs.actions"));

            DropdownAnt.Builder menu = DropdownAnt.create()
                    .trigger(trigger)
                    .placement(DropdownAnt.Placement.BOTTOM_RIGHT)
                    .onSelect(this::handleActionsSelect);
            for (TabItem item : items) {
                menu.item(ACTION_TAB_PREFIX + item.key, TextUtils.safeText(item.label));
            }
            if (anyCloseActionShown) {
                menu.divider();
            }
            if (owner.showCloseCurrent) {
                menu.item(ACTION_CLOSE_CURRENT, Messages.get("tabs.closeCurrent"), !isClosable(currentKey));
            }
            if (owner.showCloseOthers) {
                menu.item(ACTION_CLOSE_OTHERS, Messages.get("tabs.closeOthers"), !othersClosable);
            }
            if (owner.showCloseAll) {
                menu.item(ACTION_CLOSE_ALL, Messages.get("tabs.closeAll"), !anyClosable);
            }

            actionsHost.getChildren().add(menu.build().getTrigger());
        }

        private void handleActionsSelect(String actionKey) {
            if (actionKey == null) return;
            if (actionKey.startsWith(ACTION_TAB_PREFIX)) {
                selectByKey(actionKey.substring(ACTION_TAB_PREFIX.length()));
                return;
            }
            switch (actionKey) {
                case ACTION_CLOSE_CURRENT -> closeCurrent();
                case ACTION_CLOSE_OTHERS -> closeOthers(getCurrentKey());
                case ACTION_CLOSE_ALL -> closeAll();
                default -> { }
            }
        }
    }

    private static class TabItem {
        final String key;
        String label;
        Node content;
        boolean disabled;
        /** null = 继承 Builder 的全局 closable 默认值 */
        Boolean closable;

        TabItem(String key, String label, Node content, boolean disabled, Boolean closable) {
            this.key = key;
            this.label = label;
            this.content = content;
            this.disabled = disabled;
            this.closable = closable;
        }
    }
}
