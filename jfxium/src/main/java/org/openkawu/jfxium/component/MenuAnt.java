package org.openkawu.jfxium.component;

import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 高级菜单组件 - 对标 Ant Design Menu。
 *
 * <h2>支持模式（M14）</h2>
 * <ul>
 *   <li>{@link Mode#INLINE}（默认）：竖向嵌入式，子菜单行内展开（admin 侧边栏典型）</li>
 *   <li>{@link Mode#HORIZONTAL}：横向，顶级横排，子菜单从下方 Popup 弹出（顶导航栏）</li>
 * </ul>
 *
 * <h2>选中态（M14）</h2>
 * <p>每个 item 可指定 key；通过 {@code selectedKey(...)} 设置当前选中项。
 * 选中项会挂上 {@code .menu-item-selected} styleClass，由 LESS 渲染高亮。</p>
 *
 * <h2>缩进策略</h2>
 * 每级 {@code level * 16} 像素的左边距由 Java 设置（与 level 动态相关，不易在 LESS 表达），
 * 这是结构性属性而非视觉样式，inline 在此可接受。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 1. INLINE 侧边菜单（默认）
 * VBox sider = MenuAnt.create()
 *     .item("dashboard", "首页", icon1, () -> router.go("dashboard"))
 *     .subMenu("user", "用户管理")
 *         .item("user.list", "列表", () -> router.go("user.list"))
 *         .item("user.add", "新增", () -> router.go("user.add"))
 *         .endSubMenu()
 *     .selectedKey("dashboard")
 *     .onSelect(key -> System.out.println("切到：" + key))
 *     .build();
 *
 * // 2. HORIZONTAL 顶导航
 * HBox topNav = (HBox) MenuAnt.create()
 *     .mode(MenuAnt.Mode.HORIZONTAL)
 *     .item("home", "首页", () -> ...)
 *     .subMenu("products", "产品中心")
 *         .item("p1", "产品 A", () -> ...)
 *         .item("p2", "产品 B", () -> ...)
 *         .endSubMenu()
 *     .item("about", "关于我们", () -> ...)
 *     .build();
 * }</pre>
 */
public class MenuAnt {

    /** 子菜单展开箭头：右指三角（INLINE 模式中向下旋转 90°） */
    private static final String ARROW_RIGHT = "M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z";

    /** 菜单模式。 */
    public enum Mode {
        /** 竖向嵌入式：子菜单行内展开，缩进 level*16（admin 侧栏典型） */
        INLINE,
        /** 横向：顶级横排，子菜单从下方 Popup 弹出（顶导航栏） */
        HORIZONTAL
    }

    /** 菜单主题（M14.3）。 */
    public enum Theme {
        /** 亮色（默认）：白底深字 */
        LIGHT,
        /** 暗色：深色底白字（admin 侧栏经典暗色风格） */
        DARK
    }

    /**
     * INLINE 模式下 subMenu 的展开行为（M19.19）。
     * <p>
     * <b>UX 经典选择</b>：
     * <ul>
     *   <li>{@link #MULTIPLE}（默认）：可同时展开多个分类，点击 header 各自切换。
     *       适合分类多、用户习惯纵览所有可用项的场景。</li>
     *   <li>{@link #EXCLUSIVE}：手风琴效果——展开任一分类时其他分类自动收起。
     *       适合分类多但屏幕高度有限、希望聚焦在当前分类的场景。</li>
     * </ul>
     * 通过 {@link Builder#expandMode(ExpandMode)} 配置。
     */
    public enum ExpandMode {
        MULTIPLE,
        EXCLUSIVE
    }

    public static Builder create() {
        return new Builder();
    }

    // ============================================================
    // Builder
    // ============================================================
    public static class Builder {
        private final List<MenuItem> items = new ArrayList<>();
        private SubMenuBuilder currentSubMenu = null;

        private Mode mode = Mode.INLINE;
        private Theme theme = Theme.LIGHT;
        private boolean collapsed = false;
        private String selectedKey;
        private Consumer<String> onSelect;
        // 展开模式 + 当前已展开 keys（用户外部传入持久化状态）
        private ExpandMode expandMode = ExpandMode.MULTIPLE;
        private final java.util.Set<String> expandedKeys = new java.util.HashSet<>();
        private Consumer<java.util.Set<String>> onExpandChange;

        private Builder() {}

        // ---------------- item ----------------
        public Builder item(String text, Runnable onClick) {
            return item(null, text, null, onClick);
        }

        public Builder item(String text, Node icon, Runnable onClick) {
            return item(null, text, icon, onClick);
        }

        public Builder item(String key, String text, Runnable onClick) {
            return item(key, text, null, onClick);
        }

        public Builder item(String key, String text, Node icon, Runnable onClick) {
            MenuItem mi = new MenuItem(key, text, icon, onClick, 0);
            if (currentSubMenu != null) {
                currentSubMenu.children.add(new MenuItem(key, text, icon, onClick, currentSubMenu.level + 1));
            } else {
                items.add(mi);
            }
            return this;
        }

        // ---------------- subMenu ----------------
        public SubMenuBuilder subMenu(String text) { return subMenu(null, text, null); }
        public SubMenuBuilder subMenu(String text, Node icon) { return subMenu(null, text, icon); }
        public SubMenuBuilder subMenu(String key, String text) { return subMenu(key, text, null); }

        public SubMenuBuilder subMenu(String key, String text, Node icon) {
            int level = currentSubMenu == null ? 0 : currentSubMenu.level + 1;
            SubMenuBuilder sub = new SubMenuBuilder(key, text, icon, level, this, currentSubMenu);
            if (currentSubMenu != null) {
                currentSubMenu.children.add(sub);
            } else {
                items.add(sub);
            }
            currentSubMenu = sub;
            return sub;
        }

        // ---------------- group / divider ----------------
        public Builder group(String title) {
            int level = currentSubMenu == null ? 0 : currentSubMenu.level + 1;
            MenuGroup g = new MenuGroup(title, level);
            if (currentSubMenu != null) currentSubMenu.children.add(g);
            else items.add(g);
            return this;
        }

        public Builder divider() {
            int level = currentSubMenu == null ? 0 : currentSubMenu.level + 1;
            MenuDivider d = new MenuDivider(level);
            if (currentSubMenu != null) currentSubMenu.children.add(d);
            else items.add(d);
            return this;
        }

        // ---------------- 表级 API ----------------
        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        /** 主题（LIGHT/DARK，M14.3）。 */
        public Builder theme(Theme theme) {
            this.theme = theme;
            return this;
        }

        /**
         * 折叠模式（仅 INLINE 有效，M14.4）。
         * 折叠后：宽度 64px、隐藏文字/箭头/group/divider 文字，只显示图标。
         * subMenu 改用 Popup 浮层（点击图标时从右侧弹出）。
         */
        public Builder collapsed(boolean collapsed) {
            this.collapsed = collapsed;
            return this;
        }

        public Builder selectedKey(String key) {
            this.selectedKey = key;
            return this;
        }

        public Builder onSelect(Consumer<String> handler) {
            this.onSelect = handler;
            return this;
        }

        /**
         * INLINE 子菜单展开模式（M19.19，默认 MULTIPLE 可同时展开多个）。
         * @see ExpandMode
         */
        public Builder expandMode(ExpandMode mode) {
            this.expandMode = mode != null ? mode : ExpandMode.MULTIPLE;
            return this;
        }

        /** 初始已展开的 subMenu key 集合（持久化场景：路由切换后保留用户手动展开的状态）。 */
        public Builder expandedKeys(java.util.Collection<String> keys) {
            this.expandedKeys.clear();
            if (keys != null) this.expandedKeys.addAll(keys);
            return this;
        }

        /** 展开/收起变化回调（接收完整的当前已展开 keys 集合）。 */
        public Builder onExpandChange(Consumer<java.util.Set<String>> handler) {
            this.onExpandChange = handler;
            return this;
        }

        /**
         * 关闭当前打开的 subMenu（无参快捷版）。
         * 用于多层嵌套场景下连续调用：{@code .endSubMenu().endSubMenu()}。
         * 等价于 {@code currentSubMenu.endSubMenu()} 但允许从外层 Builder 直接调用。
         */
        public Builder endSubMenu() {
            if (currentSubMenu != null) {
                currentSubMenu = currentSubMenu.parent;
            }
            return this;
        }

        void endSubMenu(SubMenuBuilder subMenu) {
            if (currentSubMenu == subMenu) {
                currentSubMenu = subMenu.parent;
            }
        }

        // ---------------- 构建 ----------------
        public Pane build() {
            // collapsed 仅 INLINE 有效（HORIZONTAL 没"折叠"语义）
            boolean effectiveCollapsed = (mode == Mode.INLINE) && collapsed;
            BuildContext ctx = new BuildContext(mode, theme, effectiveCollapsed, selectedKey, onSelect,
                    expandMode, new java.util.HashSet<>(expandedKeys), onExpandChange);

            if (mode == Mode.HORIZONTAL) {
                HBox menu = new HBox(0);
                menu.getStyleClass().addAll(CssClasses.MENU, CssClasses.MENU_HORIZONTAL);
                if (theme == Theme.DARK) menu.getStyleClass().add(CssClasses.MENU_DARK);
                menu.setAlignment(Pos.CENTER_LEFT);
                for (MenuItem item : items) {
                    Node node = item.buildHorizontal(ctx);
                    if (node != null) menu.getChildren().add(node);
                }
                return menu;
            } else {
                VBox menu = new VBox(0);
                menu.getStyleClass().addAll(CssClasses.MENU, CssClasses.MENU_INLINE);
                if (theme == Theme.DARK)         menu.getStyleClass().add(CssClasses.MENU_DARK);
                if (effectiveCollapsed)          menu.getStyleClass().add(CssClasses.MENU_COLLAPSED);
                for (MenuItem item : items) {
                    Node node = item.buildInline(ctx);
                    if (node != null) menu.getChildren().add(node);
                }
                return menu;
            }
        }
    }

    /**
     * 构建上下文：把 mode / theme / collapsed / selectedKey / onSelect 这些
     * "全菜单共用"的状态打包传递，避免 MenuItem 等内部类要持有 Builder 引用（解耦 + 易测）。
     */
    static class BuildContext {
        final Mode mode;
        final Theme theme;
        final boolean collapsed;
        final String selectedKey;
        final Consumer<String> onSelect;
        // 展开状态共享（M19.19）
        final ExpandMode expandMode;
        final java.util.Set<String> expandedKeys;
        final Consumer<java.util.Set<String>> onExpandChange;
        /** 顶级 subMenu 渲染产物收集（用于 EXCLUSIVE 模式下通知兄弟节点收起）。 */
        final java.util.List<SubMenuRenderInfo> topLevelSubMenus = new java.util.ArrayList<>();

        BuildContext(Mode mode, Theme theme, boolean collapsed, String selectedKey, Consumer<String> onSelect,
                     ExpandMode expandMode, java.util.Set<String> expandedKeys,
                     Consumer<java.util.Set<String>> onExpandChange) {
            this.mode = mode;
            this.theme = theme;
            this.collapsed = collapsed;
            this.selectedKey = selectedKey;
            this.onSelect = onSelect;
            this.expandMode = expandMode;
            this.expandedKeys = expandedKeys;
            this.onExpandChange = onExpandChange;
        }

        /** 触发选择回调（item 被点击时调用）。 */
        void fireSelect(String key) {
            if (onSelect != null && key != null) {
                onSelect.accept(key);
            }
        }

        /** 触发展开变更回调（可空安全）。 */
        void fireExpandChange() {
            if (onExpandChange != null) {
                onExpandChange.accept(new java.util.HashSet<>(expandedKeys));
            }
        }
    }

    /** 顶级 subMenu 渲染产物，用于 EXCLUSIVE 模式互斥折叠。 */
    static class SubMenuRenderInfo {
        final String key;
        final Runnable collapse;

        SubMenuRenderInfo(String key, Runnable collapse) {
            this.key = key;
            this.collapse = collapse;
        }
    }

    // ============================================================
    // SubMenuBuilder
    // ============================================================
    public static class SubMenuBuilder extends MenuItem {
        final List<MenuItem> children = new ArrayList<>();
        private final Builder rootBuilder;
        final SubMenuBuilder parent;
        /** 是否默认展开（INLINE 模式 + 非 collapsed 时生效）。null = 由 build 时自动判断（含 selectedKey 时展开）。 */
        Boolean defaultExpanded = null;

        SubMenuBuilder(String key, String text, Node icon, int level, Builder rootBuilder, SubMenuBuilder parent) {
            super(key, text, icon, null, level);
            this.rootBuilder = rootBuilder;
            this.parent = parent;
        }

        /**
         * 默认展开状态（INLINE 模式 + 非 collapsed 时生效）。
         * 不设置时，build 阶段会自动判断：含 selectedKey 的分支自动展开。
         */
        public SubMenuBuilder defaultExpanded(boolean expanded) {
            this.defaultExpanded = expanded;
            return this;
        }

        public SubMenuBuilder item(String text, Runnable onClick) {
            children.add(new MenuItem(null, text, null, onClick, level + 1));
            return this;
        }

        public SubMenuBuilder item(String text, Node icon, Runnable onClick) {
            children.add(new MenuItem(null, text, icon, onClick, level + 1));
            return this;
        }

        public SubMenuBuilder item(String key, String text, Runnable onClick) {
            children.add(new MenuItem(key, text, null, onClick, level + 1));
            return this;
        }

        public SubMenuBuilder item(String key, String text, Node icon, Runnable onClick) {
            children.add(new MenuItem(key, text, icon, onClick, level + 1));
            return this;
        }

        public SubMenuBuilder subMenu(String text) { return subMenu(null, text, null); }
        public SubMenuBuilder subMenu(String text, Node icon) { return subMenu(null, text, icon); }
        public SubMenuBuilder subMenu(String key, String text) { return subMenu(key, text, null); }

        public SubMenuBuilder subMenu(String key, String text, Node icon) {
            SubMenuBuilder sub = new SubMenuBuilder(key, text, icon, level + 1, rootBuilder, this);
            children.add(sub);
            rootBuilder.currentSubMenu = sub;
            return sub;
        }

        public SubMenuBuilder group(String title) {
            children.add(new MenuGroup(title, level + 1));
            return this;
        }

        public SubMenuBuilder divider() {
            children.add(new MenuDivider(level + 1));
            return this;
        }

        public Builder endSubMenu() {
            rootBuilder.endSubMenu(this);
            return rootBuilder;
        }

        // -------- INLINE 模式：子菜单行内展开（保留原来的 click 切换 + 旋转箭头） --------
        @Override
        Node buildInline(BuildContext ctx) {
            // collapsed 模式：subMenu 改用 popup 从右侧弹出（行内展开模式失效）
            if (ctx.collapsed) {
                return buildInlineCollapsed(ctx);
            }

            VBox container = new VBox(0);

            HBox header = createInlineHeader();
            container.getChildren().add(header);

            VBox childrenContainer = new VBox(0);
            childrenContainer.getStyleClass().add(CssClasses.MENU_SUBMENU_BODY);
            for (MenuItem child : children) {
                Node node = child.buildInline(ctx);
                if (node != null) childrenContainer.getChildren().add(node);
            }
            childrenContainer.setVisible(false);
            childrenContainer.setManaged(false);
            container.getChildren().add(childrenContainer);

            // 右侧箭头
            SVGPath arrow = createArrow();
            HBox arrowContainer = new HBox(arrow);
            arrowContainer.setAlignment(Pos.CENTER_RIGHT);
            arrowContainer.getStyleClass().add(CssClasses.MENU_SUBMENU_ARROW_BOX);
            HBox.setHgrow(arrowContainer, Priority.NEVER);
            header.getChildren().add(arrowContainer);

            // 决定初始展开状态：
            // 1) ctx.expandedKeys 包含本节点 key → 展开（持久化场景，如路由切换后保留）
            // 2) 用户显式设置 defaultExpanded → 用之
            // 3) 否则自动判断：当前 selectedKey 落在本子树内 → 默认展开（让用户能看到自己在哪）
            boolean fromPersisted = key != null && ctx.expandedKeys.contains(key);
            boolean initialExpanded = fromPersisted
                    || (defaultExpanded != null ? defaultExpanded : containsKey(ctx.selectedKey));
            if (initialExpanded) {
                childrenContainer.setVisible(true);
                childrenContainer.setManaged(true);
                arrow.setRotate(90);
                if (key != null) ctx.expandedKeys.add(key);
            }

            final boolean[] expanded = {initialExpanded};
            // 提供一个 collapse 闭包，给 EXCLUSIVE 模式下兄弟节点互斥用
            Runnable doCollapse = () -> {
                if (!expanded[0]) return;
                expanded[0] = false;
                childrenContainer.setVisible(false);
                childrenContainer.setManaged(false);
                RotateTransition r = new RotateTransition(Duration.millis(200), arrow);
                r.setToAngle(0);
                r.play();
                if (key != null) ctx.expandedKeys.remove(key);
            };
            // 仅顶级 subMenu（level==0）参与互斥；嵌套子菜单不互斥（用户多半希望保留父级展开）
            if (level == 0 && key != null) {
                ctx.topLevelSubMenus.add(new SubMenuRenderInfo(key, doCollapse));
            }

            header.setOnMouseClicked(e -> {
                expanded[0] = !expanded[0];
                childrenContainer.setVisible(expanded[0]);
                childrenContainer.setManaged(expanded[0]);
                RotateTransition rotate = new RotateTransition(Duration.millis(200), arrow);
                rotate.setToAngle(expanded[0] ? 90 : 0);
                rotate.play();

                if (key != null) {
                    if (expanded[0]) {
                        // EXCLUSIVE：先把其他顶级 subMenu 收起来
                        if (level == 0 && ctx.expandMode == ExpandMode.EXCLUSIVE) {
                            for (SubMenuRenderInfo other : ctx.topLevelSubMenus) {
                                if (!key.equals(other.key)) {
                                    other.collapse.run();
                                }
                            }
                        }
                        ctx.expandedKeys.add(key);
                    } else {
                        ctx.expandedKeys.remove(key);
                    }
                    ctx.fireExpandChange();
                }
            });
            return container;
        }

        /** 递归检查本子树中是否包含给定 key（用于自动展开判断）。 */
        private boolean containsKey(String key) {
            if (key == null) return false;
            for (MenuItem child : children) {
                if (key.equals(child.key)) return true;
                if (child instanceof SubMenuBuilder sub && sub.containsKey(key)) return true;
            }
            return false;
        }

        /** Collapsed 模式下的 subMenu：仅显示图标，点击从右侧 Popup 弹出子项。 */
        private Node buildInlineCollapsed(BuildContext ctx) {
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER);
            header.setMinHeight(40);
            header.setPrefHeight(40);
            header.getStyleClass().add(CssClasses.MENU_SUBMENU_HEADER);
            header.setStyle("-fx-cursor: hand;");

            // 仅显示图标（如果有）；没有图标时显示 text 首字符
            if (icon != null) {
                header.getChildren().add(icon);
            } else if (text != null && !text.isEmpty()) {
                Label fallback = new Label(text.substring(0, 1));
                fallback.getStyleClass().add(CssClasses.MENU_ITEM_LABEL);
                header.getChildren().add(fallback);
            }

            // Popup 弹层（从右侧弹出）
            Popup popup = new Popup();
            VBox popupBody = new VBox(0);
            popupBody.getStyleClass().addAll(CssClasses.MENU, CssClasses.POPUP_MENU);
            // 子菜单内部用非 collapsed 上下文渲染（弹层里要显示完整文字）
            BuildContext expandCtx = new BuildContext(ctx.mode, ctx.theme, false, ctx.selectedKey, ctx.onSelect,
                    ExpandMode.MULTIPLE, new java.util.HashSet<>(), null);
            for (MenuItem child : children) {
                Node node = child.buildInline(expandCtx);
                if (node != null) popupBody.getChildren().add(node);
            }
            popup.getContent().add(popupBody);
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            header.setOnMouseClicked(e -> {
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    Bounds b = header.localToScreen(header.getBoundsInLocal());
                    popup.show(header, b.getMaxX(), b.getMinY());
                }
            });

            return header;
        }

        private HBox createInlineHeader() {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 16, 10, 16 + level * 16));
            row.getStyleClass().add(CssClasses.MENU_SUBMENU_HEADER);

            if (icon != null) row.getChildren().add(icon);
            Label label = new Label(text);
            label.getStyleClass().add(CssClasses.MENU_ITEM_LABEL);
            row.getChildren().add(label);
            // 用独立 Region spacer 把右侧箭头推到最右（Label 默认 maxWidth=USE_PREF_SIZE
            // 给它设 Hgrow=ALWAYS 不会拉伸；详见 SKILL §4.1 / §20.1）
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMaxWidth(Double.MAX_VALUE);
            row.getChildren().add(spacer);
            return row;
        }

        // -------- HORIZONTAL 模式：顶级横排 + 子菜单 Popup 下拉 --------
        @Override
        Node buildHorizontal(BuildContext ctx) {
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(0, 16, 0, 16));
            header.setMinHeight(48);
            header.setPrefHeight(48);
            header.getStyleClass().add(CssClasses.MENU_SUBMENU_HEADER);
            header.setStyle("-fx-cursor: hand;");

            if (icon != null) header.getChildren().add(icon);
            Label label = new Label(text);
            label.getStyleClass().add(CssClasses.MENU_ITEM_LABEL);
            header.getChildren().add(label);

            // 下拉箭头：与 INLINE 共用一个 shape，但默认旋转 90° 朝下
            SVGPath arrow = createArrow();
            arrow.setRotate(90);
            arrow.getStyleClass().add(CssClasses.MENU_SUBMENU_ARROW);
            header.getChildren().add(arrow);

            // Popup 弹层（点击切换显隐；INLINE 模式中是行内展开，这里改 popup）
            Popup popup = new Popup();
            VBox popupBody = new VBox(0);
            popupBody.getStyleClass().addAll(CssClasses.MENU, CssClasses.POPUP_MENU);
            for (MenuItem child : children) {
                // 子菜单内部走 INLINE 渲染（VBox 一列）
                Node node = child.buildInline(ctx);
                if (node != null) popupBody.getChildren().add(node);
            }
            popup.getContent().add(popupBody);
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            header.setOnMouseClicked(e -> {
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    Bounds b = header.localToScreen(header.getBoundsInLocal());
                    popup.show(header, b.getMinX(), b.getMaxY());
                }
            });

            return header;
        }
    }

    // ============================================================
    // 普通 MenuItem
    // ============================================================
    static class MenuItem {
        protected final String key;
        protected final String text;
        protected final Node icon;
        protected final Runnable onClick;
        protected final int level;

        MenuItem(String key, String text, Node icon, Runnable onClick, int level) {
            this.key = key;
            this.text = text;
            this.icon = icon;
            this.onClick = onClick;
            this.level = level;
        }

        // 默认提供两种渲染：buildInline / buildHorizontal。子类可覆盖。

        Node buildInline(BuildContext ctx) {
            HBox row = new HBox(12);
            row.setPadding(new Insets(10, 16, 10, 16 + level * 16));
            applyItemStyles(row, ctx);

            if (ctx.collapsed) {
                // 折叠模式：只显示图标，无 padding-left 偏移
                row.setAlignment(Pos.CENTER);
                row.setPadding(new Insets(10, 0, 10, 0));
                if (icon != null) {
                    row.getChildren().add(icon);
                } else if (text != null && !text.isEmpty()) {
                    // 兜底：显示文字首字符（如果该 item 没图标）
                    Label fallback = new Label(text.substring(0, 1));
                    fallback.getStyleClass().add(CssClasses.MENU_ITEM_LABEL);
                    row.getChildren().add(fallback);
                }
            } else {
                row.setAlignment(Pos.CENTER_LEFT);
                if (icon != null) row.getChildren().add(icon);
                Label label = new Label(text);
                label.getStyleClass().add(CssClasses.MENU_ITEM_LABEL);
                row.getChildren().add(label);
            }

            attachClick(row, ctx);
            return row;
        }

        Node buildHorizontal(BuildContext ctx) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER);
            row.setPadding(new Insets(0, 16, 0, 16));
            row.setMinHeight(48);
            row.setPrefHeight(48);
            applyItemStyles(row, ctx);

            if (icon != null) row.getChildren().add(icon);
            Label label = new Label(text);
            label.getStyleClass().add(CssClasses.MENU_ITEM_LABEL);
            row.getChildren().add(label);

            attachClick(row, ctx);
            return row;
        }

        /** 挂 styleClass：基础 menu-item + 选中态。 */
        private void applyItemStyles(HBox row, BuildContext ctx) {
            row.getStyleClass().add(CssClasses.MENU_ITEM);
            if (key != null && key.equals(ctx.selectedKey)) {
                row.getStyleClass().add(CssClasses.MENU_ITEM_SELECTED);
            }
        }

        /** 绑定点击：触发 onClick + onSelect 回调。 */
        private void attachClick(HBox row, BuildContext ctx) {
            if (onClick == null && key == null) return;
            row.setStyle((row.getStyle() == null ? "" : row.getStyle()) + "-fx-cursor: hand;");
            row.setOnMouseClicked(e -> {
                if (onClick != null) onClick.run();
                ctx.fireSelect(key);
            });
        }
    }

    /** 分组标题 - 对标 Ant Design Menu.ItemGroup */
    static class MenuGroup extends MenuItem {
        MenuGroup(String title, int level) {
            super(null, title, null, null, level);
        }

        @Override
        Node buildInline(BuildContext ctx) {
            // 折叠模式：分组标题隐藏（只剩图标列没意义显示文字）
            if (ctx.collapsed) return null;

            Label label = new Label(text);
            label.getStyleClass().add(CssClasses.MENU_GROUP_LABEL);
            label.setPadding(new Insets(16, 16, 8, 16 + level * 16));
            return label;
        }

        @Override
        Node buildHorizontal(BuildContext ctx) {
            // 横向模式没有"分组标题"语义，跳过
            return null;
        }
    }

    /** 分割线 - 对标 Ant Design Menu.Divider */
    static class MenuDivider extends MenuItem {
        MenuDivider(int level) {
            super(null, "", null, null, level);
        }

        @Override
        Node buildInline(BuildContext ctx) {
            Region line = new Region();
            line.getStyleClass().add(CssClasses.MENU_DIVIDER);
            // 折叠模式：分割线左右无 padding 偏移
            if (ctx.collapsed) {
                line.setPadding(new Insets(8, 0, 8, 0));
            } else {
                line.setPadding(new Insets(8, 16, 8, 16 + level * 16));
            }
            return line;
        }

        @Override
        Node buildHorizontal(BuildContext ctx) {
            return null;
        }
    }

    // ============================================================
    // 辅助：创建一个右指箭头（SVGPath）。
    // ============================================================
    private static SVGPath createArrow() {
        SVGPath arrow = new SVGPath();
        arrow.setContent(ARROW_RIGHT);
        arrow.getStyleClass().add(CssClasses.MENU_SUBMENU_ARROW);
        arrow.setScaleX(0.8);
        arrow.setScaleY(0.8);
        return arrow;
    }
}
