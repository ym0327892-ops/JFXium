package org.openkawu.jfxium.template;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.openkawu.jfxium.component.composite.AvatarAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.MenuBarAnt;
import org.openkawu.jfxium.component.control.StatusBarAnt;
import org.openkawu.jfxium.component.control.TooltipAnt;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.overlay.DropdownAnt;
import org.openkawu.jfxium.component.overlay.DropdownAnt.DropdownResult;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ProjectConsoleTemplate - 工程项目控制台壳模板。
 *
 * <p>它把工程类 demo 里最常重复的一层统一收口：
 * 顶部系统菜单、左上角软件图标、右上角用户入口、左侧导航、右侧展示区、底部状态栏。
 * 这样调用方只需要提供业务菜单和内容，壳层结构可以直接复用。</p>
 *
 * <p>底层委托给 {@link WorkspaceTemplate}，因此它继承了工作台壳的折叠、响应式断点
 * 和 header 编排能力，但上层语义更贴近“工程项目展示 / 控制台首页”。</p>
 */
public final class ProjectConsoleTemplate {

    private ProjectConsoleTemplate() {}

    public static Builder create() {
        return new Builder();
    }

    /**
     * 复用工程展示页常见的“头像 + 用户名 + 下拉菜单”入口。
     */
    public static DropdownResult userMenu(String currentUser, Consumer<String> onAction) {
        return WorkspaceTemplate.userMenu(currentUser, onAction);
    }

    /**
     * 一行构建工程控制台右上角常用动作。
     *
     * <p>默认组合为「外观设置图标按钮 + 用户入口」，比 demo 里单独拼一个
     * 文字“设置”链接更像真实项目壳，也更容易在多个页面复用。</p>
     */
    public static Node headerActions(String currentUser, Consumer<String> onAction) {
        ButtonAnt settingsButton = ButtonAnt.iconOnly(IconAnt.path(IconAnt.Path.SETTINGS, 16)).build();
        TooltipAnt.create("外观设置").install(settingsButton);
        settingsButton.setOnAction(e -> emit(onAction, "settings"));

        Node userMenu = userMenu(currentUser, onAction).getTrigger();

        return HBoxAnt.create()
                .spacing(8)
                .align(Pos.CENTER_LEFT)
                .children(settingsButton, userMenu)
                .build();
    }

    /**
     * 复用工程控制台底部状态栏。
     */
    public static StatusBarAnt statusBar(String info, String status) {
        return StatusBarAnt.create()
                .info(info)
                .status(status)
                .build();
    }

    /**
     * 一行构建工程控制台常用系统菜单栏。
     *
     * <p>统一收口 File / Edit / View / Help 这组最常见菜单，调用方只需要处理 action 回调即可。</p>
     */
    public static MenuBarAnt standardMenuBar(Consumer<String> onAction) {
        return MenuBarAnt.create()
                .menu("文件")
                    .item("首页", () -> emit(onAction, "home"))
                    .item("退出", () -> emit(onAction, "logout"))
                    .endMenu()
                .menu("编辑")
                    .item("撤销", () -> emit(onAction, "undo"))
                    .item("重做", () -> emit(onAction, "redo"))
                    .endMenu()
                .menu("视图")
                    .item("外观设置", () -> emit(onAction, "settings"))
                    .item("切换侧栏", () -> emit(onAction, "toggleSidebar"))
                    .endMenu()
                .menu("帮助")
                    .item("关于", () -> emit(onAction, "about"))
                    .endMenu();
    }

    /** 侧栏单项数据。 */
    public record SidebarLink(String key, String label, Node icon) {}

    /** 侧栏分组数据。 */
    public record SidebarSection(String key, String label, Node icon, boolean expandedByDefault, List<SidebarLink> items) {}

    /** 侧栏构建结果：菜单节点 + runtime controller。 */
    public record SidebarResult(Pane root, org.openkawu.jfxium.component.composite.MenuAnt.Controller controller) {}

    public static SidebarLink sidebarLink(String key, String label) {
        return sidebarLink(key, label, null);
    }

    public static SidebarLink sidebarLink(String key, String label, Node icon) {
        return new SidebarLink(TextUtils.safeText(key), TextUtils.safeText(label), icon);
    }

    public static SidebarSection sidebarSection(String key,
                                                String label,
                                                Node icon,
                                                boolean expandedByDefault,
                                                SidebarLink... items) {
        List<SidebarLink> resolved = new ArrayList<>();
        if (items != null) {
            for (SidebarLink item : items) {
                if (item != null) {
                    resolved.add(item);
                }
            }
        }
        return new SidebarSection(TextUtils.safeText(key), TextUtils.safeText(label), icon, expandedByDefault, List.copyOf(resolved));
    }

    public static SidebarSection sidebarSection(String key, String label, Node icon, SidebarLink... items) {
        return sidebarSection(key, label, icon, true, items);
    }

    public static SidebarResult sidebarPane(String selectedKey,
                                            Consumer<String> onSelect,
                                            SidebarLink[] topLevelItems,
                                            SidebarSection[] sections) {
        org.openkawu.jfxium.component.composite.MenuAnt.Builder builder = org.openkawu.jfxium.component.composite.MenuAnt.create()
                .selectedKey(selectedKey)
                .onSelect(onSelect);

        if (topLevelItems != null) {
            for (SidebarLink item : topLevelItems) {
                if (item != null) {
                    builder.item(item.key(), item.label(), item.icon(), () -> emit(onSelect, item.key()));
                }
            }
        }

        if (sections != null) {
            for (SidebarSection section : sections) {
                if (section == null) {
                    continue;
                }
                org.openkawu.jfxium.component.composite.MenuAnt.SubMenuBuilder sub = builder.subMenu(
                                section.key(),
                                section.label(),
                                section.icon())
                        .defaultExpanded(section.expandedByDefault());
                for (SidebarLink item : section.items()) {
                    if (item != null) {
                        sub.item(item.key(), item.label(), item.icon(), () -> emit(onSelect, item.key()));
                    }
                }
                builder.endSubMenu();
            }
        }

        Pane menu = builder.build();
        return new SidebarResult(menu, builder.controller());
    }

    public static SidebarResult sidebarPane(String selectedKey,
                                            Consumer<String> onSelect,
                                            SidebarLink... topLevelItems) {
        return sidebarPane(selectedKey, onSelect, topLevelItems, new SidebarSection[0]);
    }

    public static SidebarResult sidebarPane(String selectedKey,
                                            Consumer<String> onSelect,
                                            SidebarSection... sections) {
        return sidebarPane(selectedKey, onSelect, new SidebarLink[0], sections);
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private String brandTitle = "工程控制台";
        private String brandSubtitle = "项目展示壳";
        private Node brandIcon;
        private final List<Node> headerLeft = new ArrayList<>();
        private final List<Node> headerTop = new ArrayList<>();
        private final List<Node> headerCenter = new ArrayList<>();
        private final List<Node> headerRight = new ArrayList<>();
        private Node sider;
        private double siderWidth = 240;
        private Node content;
        private Node footer;
        private boolean collapsible = true;
        private boolean collapsed = false;
        private boolean trigger = true;
        private double collapsedWidth = 64;
        private GridAnt.Breakpoint breakpoint = GridAnt.Breakpoint.LG;
        private Background headerBackground;
        private Background siderBackground = Background.SUBTLE;
        private Background contentBackground = Background.DEFAULT;
        private Background footerBackground;

        private Builder() {}

        /** 顶部品牌标题。 */
        public Builder brand(String title) {
            this.brandTitle = TextUtils.safeText(title, "工程控制台");
            return this;
        }

        /** 顶部品牌标题 + 副标题。 */
        public Builder brand(String title, String subtitle) {
            this.brandTitle = TextUtils.safeText(title, "工程控制台");
            this.brandSubtitle = TextUtils.safeText(subtitle, "项目展示壳");
            return this;
        }

        /** 顶部品牌副标题。 */
        public Builder brandSubtitle(String subtitle) {
            this.brandSubtitle = TextUtils.safeText(subtitle, "项目展示壳");
            return this;
        }

        /** 顶部软件图标，直接传节点。 */
        public Builder brandIcon(Node icon) {
            this.brandIcon = icon;
            return this;
        }

        /** 顶部软件图标，直接传图标路径。 */
        public Builder brandIcon(IconAnt.Path icon) {
            this.brandIcon = icon != null
                    ? AvatarAnt.create()
                        .icon(IconAnt.path(icon, 18))
                        .shape(AvatarAnt.Shape.SQUARE)
                        .size(36)
                        .build()
                    : null;
            return this;
        }

        /** 顶部系统菜单栏 / 通知条。 */
        public Builder headerTop(Node... nodes) {
            TextUtils.addNonNull(headerTop, nodes);
            return this;
        }

        /** 顶部中间区域。 */
        public Builder headerCenter(Node... nodes) {
            TextUtils.addNonNull(headerCenter, nodes);
            return this;
        }

        /** 顶部右侧区域。 */
        public Builder headerRight(Node... nodes) {
            TextUtils.addNonNull(headerRight, nodes);
            return this;
        }

        /** 直接把菜单栏放到顶部区域。 */
        public Builder menuBar(Node menuBar) {
            return headerTop(menuBar);
        }

        /** 品牌区左侧附加节点。 */
        public Builder headerLeft(Node... nodes) {
            TextUtils.addNonNull(headerLeft, nodes);
            return this;
        }

        /** 头像下拉入口放到右侧区域。 */
        public Builder userMenu(Node userMenu) {
            return headerRight(userMenu);
        }

        /** 头像下拉入口放到右侧区域，并按用户名统一构建。 */
        public Builder userMenu(String currentUser, Consumer<String> onAction) {
            DropdownResult result = ProjectConsoleTemplate.userMenu(currentUser, onAction);
            return userMenu(result.getTrigger());
        }

        /** 左侧导航区。 */
        public Builder sider(Node sider) {
            this.sider = sider;
            return this;
        }

        /** 左侧导航区 + 宽度。 */
        public Builder sider(Node sider, double width) {
            this.sider = sider;
            this.siderWidth = TextUtils.safePositive(width, 240);
            return this;
        }

        /** 右侧展示区。 */
        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        /** 底部状态栏。 */
        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        /** 启用侧栏折叠。 */
        public Builder collapsible() {
            this.collapsible = true;
            return this;
        }

        /** 是否启用侧栏折叠。 */
        public Builder collapsible(boolean collapsible) {
            this.collapsible = collapsible;
            return this;
        }

        /** 初始折叠状态。 */
        public Builder collapsed(boolean collapsed) {
            this.collapsed = collapsed;
            return this;
        }

        /** 是否显示内置 trigger。 */
        public Builder trigger(boolean trigger) {
            this.trigger = trigger;
            return this;
        }

        /** 折叠后宽度。 */
        public Builder collapsedWidth(double width) {
            this.collapsedWidth = TextUtils.safePositive(width, 64);
            return this;
        }

        /** 响应式断点。 */
        public Builder breakpoint(GridAnt.Breakpoint breakpoint) {
            this.breakpoint = breakpoint;
            return this;
        }

        /** Header 背景层级。 */
        public Builder headerBackground(Background background) {
            this.headerBackground = background;
            return this;
        }

        /** Sider 背景层级。 */
        public Builder siderBackground(Background background) {
            this.siderBackground = background;
            return this;
        }

        /** Content 背景层级。 */
        public Builder contentBackground(Background background) {
            this.contentBackground = background;
            return this;
        }

        /** Footer 背景层级。 */
        public Builder footerBackground(Background background) {
            this.footerBackground = background;
            return this;
        }

        /** 直接构建 BorderPane。 */
        public BorderPane build() {
            return buildResult().getRoot();
        }

        /** 构建并返回工作台壳结果。 */
        public WorkspaceTemplate.Result buildResult() {
            WorkspaceTemplate.Builder builder = WorkspaceTemplate.create()
                    .brand(brandTitle, brandSubtitle)
                    .brandIcon(brandIcon)
                    .headerLeft(headerLeft.toArray(Node[]::new))
                    .headerTop(headerTop.toArray(Node[]::new))
                    .headerCenter(headerCenter.toArray(Node[]::new))
                    .headerRight(headerRight.toArray(Node[]::new))
                    .sider(sider, siderWidth)
                    .content(content)
                    .footer(footer)
                    .collapsible(collapsible)
                    .collapsed(collapsed)
                    .collapsedWidth(collapsedWidth)
                    .trigger(trigger)
                    .breakpoint(breakpoint)
                    .headerBackground(headerBackground)
                    .siderBackground(siderBackground)
                    .contentBackground(contentBackground)
                    .footerBackground(footerBackground);
            WorkspaceTemplate.Result result = builder.buildResult();
            applyStyles(result.getRoot());
            return result;
        }

        // safeWidth 统一改用 TextUtils.safePositive,见 P0-23。
    }

    private static void emit(Consumer<String> onAction, String action) {
        if (onAction != null) {
            onAction.accept(action);
        }
    }
}
