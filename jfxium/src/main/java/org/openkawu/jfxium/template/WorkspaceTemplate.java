package org.openkawu.jfxium.template;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.HBarAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.LabelAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;
import org.openkawu.jfxium.core.util.TextUtils;
import org.openkawu.jfxium.layout.AppShellAnt;
import org.openkawu.jfxium.component.layout.GridAnt;

import javafx.beans.property.BooleanProperty;
import org.openkawu.jfxium.component.composite.AvatarAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.overlay.DropdownAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.overlay.DropdownAnt.DropdownResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * WorkspaceTemplate - 高可用工作台壳模板。
 *
 * <p><b>定位</b>：在 {@link AppShellAnt} 这种低层应用骨架上，再补一层“可直接拿去做生产系统”的
 * 工作台模板。它适合管理后台、运营台、CMS / CRM、桌面工具类系统的主框架。</p>
 *
 * <p><b>边界</b>：</p>
 * <ul>
 *   <li>{@link AppShellAnt} 负责 header / sider / content / footer 的骨架、折叠、响应式。</li>
 *   <li>{@code WorkspaceTemplate} 负责把 header 编排成“品牌 + 导航 + 操作区”的工作台样子。</li>
 *   <li>{@code headerTop(...)} 可放系统菜单栏或通知条，形成“顶栏 + 工具栏”双层头部。</li>
 *   <li>需要完全自定义 header 时，可直接调用 {@link #header(Node)} 覆盖默认编排。</li>
 * </ul>
 *
 * <h2>结构</h2>
 * <pre>
 * ┌ brand / breadcrumb / actions ┐
 * ├────────────── sider + content ┤
 * └────────────── footer/status ───┘
 * </pre>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * BorderPane shell = WorkspaceTemplate.create()
 *     .brand("JFXium Workspace", "高可用工作台")
 *     .headerTop(MenuBarAnt.create().menu("File").item("Exit", this::exit).endMenu())
 *     .headerCenter(BreadcrumbAnt.create().items("首页", "工作台").build())
 *     .headerRight(refreshBtn, collapseBtn)
 *     .sider(nav, 220)
 *     .content(mainPage)
 *     .footer(statusBar)
 *     .collapsible(true)
 *     .breakpoint(GridAnt.Breakpoint.LG)
 *     .build();
 * }</pre>
 */
public class WorkspaceTemplate {

    public static Builder create() {
        return new Builder();
    }

    /**
     * 构建工程壳里常见的“头像 + 用户名 + 下拉菜单”入口。
     *
     * <p>这是工程展示页里最常重复的头部动作之一：外观设置、关于、退出。
     * 把它收口到 WorkspaceTemplate，主窗口和后台壳都能直接复用。</p>
     */
    public static DropdownResult userMenu(String currentUser, Consumer<String> onAction) {
        String resolvedUser = currentUser == null || currentUser.isBlank() ? Messages.get("workspace.guest_user") : currentUser;
        String avatarText = resolvedUser.isBlank() ? "U" : resolvedUser;

        HBox trigger = HBoxAnt.create()
                .spacing(8)
                .align(Pos.CENTER_LEFT)
                .children(
                        AvatarAnt.create()
                                .text(avatarText)
                                .size(AvatarAnt.Size.DEFAULT)
                                .build(),
                        TypographyAnt.text(resolvedUser)
                                .type(TypographyAnt.TextColor.SECONDARY)
                                .build()
                )
                .build();

        return DropdownAnt.create()
                .trigger(trigger)
                .showArrow()
                .placement(DropdownAnt.Placement.BOTTOM_RIGHT)
                .item("settings", Messages.get("workspace.menu_appearance"), IconAnt.path(IconAnt.Path.SETTINGS, 16))
                .item("about", Messages.get("workspace.menu_about"), IconAnt.symbol(IconAnt.Symbol.INFO, 16))
                .divider()
                .item("logout", Messages.get("workspace.menu_logout"), IconAnt.path(IconAnt.Path.LOGOUT, 16))
                .onSelect(key -> {
                    if (onAction != null) {
                        onAction.accept(key);
                    }
                })
                .build();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private String brandTitle;
        private String brandSubtitle;
        private Node brandIcon;
        private final List<Node> headerLeft = new ArrayList<>();
        private final List<Node> headerCenter = new ArrayList<>();
        private final List<Node> headerRight = new ArrayList<>();
        private final List<Node> headerTop = new ArrayList<>();

        private Node headerOverride;
        private Node sider;
        private double siderWidth = 240;
        private Node content;
        private Node footer;

        private boolean collapsible = false;
        private boolean collapsed = false;
        private boolean trigger = true;
        private double collapsedWidth = 64;
        private GridAnt.Breakpoint breakpoint;

        // AppShell 默认的区域背景层级：保持和低层布局一致
        private Background headerBackground;
        private Background siderBackground = Background.SUBTLE;
        private Background contentBackground = Background.DEFAULT;
        private Background footerBackground;

        private double headerGap = 12;

        private Builder() {}

        // ============================================================
        // 品牌 / Header 编排
        // ============================================================

        /** 品牌标题（左上角主文案）。null 会视为不设置。 */
        public Builder brand(String title) {
            this.brandTitle = title;
            return this;
        }

        /** 品牌标题 + 副标题。标题或副标题都可为空。 */
        public Builder brand(String title, String subtitle) {
            this.brandTitle = title;
            this.brandSubtitle = subtitle;
            return this;
        }

        /** 品牌副标题。 */
        public Builder brandSubtitle(String subtitle) {
            this.brandSubtitle = subtitle;
            return this;
        }

        /** 品牌区图标节点。 */
        public Builder brandIcon(Node icon) {
            this.brandIcon = icon;
            return this;
        }

        /** 品牌区图标，直接传图标路径。 */
        public Builder brandIcon(IconAnt.Path icon) {
            this.brandIcon = icon != null
                    ? AvatarAnt.create()
                        .icon(IconAnt.path(icon, 18))
                        .shape(AvatarAnt.Shape.SQUARE)
                        .size(32)
                        .build()
                    : null;
            return this;
        }

        /** 自定义 header 整体节点。设置后会覆盖品牌 / center / actions 编排。 */
        public Builder header(Node header) {
            this.headerOverride = header;
            return this;
        }

        /** Header 左侧附加节点。 */
        public Builder headerLeft(Node... nodes) {
            TextUtils.addNonNull(headerLeft, nodes);
            return this;
        }

        /** Header 顶部附加节点，通常用于系统菜单栏。 */
        public Builder headerTop(Node... nodes) {
            TextUtils.addNonNull(headerTop, nodes);
            return this;
        }

        /** Header 中间节点（通常是 Breadcrumb / Tabs / page status）。 */
        public Builder headerCenter(Node... nodes) {
            TextUtils.addNonNull(headerCenter, nodes);
            return this;
        }

        /** Header 右侧节点（通常是操作按钮 / 用户菜单）。 */
        public Builder headerRight(Node... nodes) {
            TextUtils.addNonNull(headerRight, nodes);
            return this;
        }

        /** Header 内 left/center/right 的主间距（默认 12）。 */
        public Builder headerGap(double gap) {
            this.headerGap = TextUtils.safeNonNegative(gap, 12);
            return this;
        }

        // ============================================================
        // Shell 区域
        // ============================================================

        /** 侧栏节点。 */
        public Builder sider(Node sider) {
            this.sider = sider;
            return this;
        }

        /** 侧栏节点 + 宽度。 */
        public Builder sider(Node sider, double width) {
            this.sider = sider;
            this.siderWidth = TextUtils.safePositive(width, 240);
            return this;
        }

        /** 侧栏宽度。 */
        public Builder siderWidth(double width) {
            this.siderWidth = TextUtils.safePositive(width, 240);
            return this;
        }

        /** 主内容区。 */
        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        /** 底部区域（通常放 StatusBarAnt）。 */
        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        // ============================================================
        // AppShell 行为
        // ============================================================

        /** 是否启用侧栏折叠。 */
        public Builder collapsible(boolean collapsible) {
            this.collapsible = collapsible;
            return this;
        }

        /** 启用侧栏折叠。 */
        public Builder collapsible() {
            return collapsible(true);
        }

        /** 初始折叠状态。 */
        public Builder collapsed(boolean collapsed) {
            this.collapsed = collapsed;
            return this;
        }

        /** 折叠后宽度。 */
        public Builder collapsedWidth(double width) {
            this.collapsedWidth = TextUtils.safePositive(width, 64);
            return this;
        }

        /** 是否显示内置 trigger 按钮。 */
        public Builder trigger(boolean trigger) {
            this.trigger = trigger;
            return this;
        }

        /** 响应式断点。 */
        public Builder breakpoint(GridAnt.Breakpoint breakpoint) {
            this.breakpoint = breakpoint;
            return this;
        }

        /** Header 背景层级。默认 null，保持 AppShell 默认外观。 */
        public Builder headerBackground(Background bg) {
            this.headerBackground = bg;
            return this;
        }

        /** Sider 背景层级。默认 {@code Background.SUBTLE}。 */
        public Builder siderBackground(Background bg) {
            this.siderBackground = bg;
            return this;
        }

        /** Content 背景层级。默认 {@code Background.DEFAULT}。 */
        public Builder contentBackground(Background bg) {
            this.contentBackground = bg;
            return this;
        }

        /** Footer 背景层级。 */
        public Builder footerBackground(Background bg) {
            this.footerBackground = bg;
            return this;
        }

        /** 直接构建 BorderPane。 */
        public BorderPane build() {
            return buildResult().getRoot();
        }

        /** 构建并返回结果包装，便于外部控制 sider 折叠状态。 */
        public Result buildResult() {
            Node header = headerOverride != null ? headerOverride : buildStructuredHeader();
            Node topHeader = packNodes(headerTop, JfxStyles.WORKSPACE_TEMPLATE_HEADER_CENTER, Pos.CENTER_LEFT);
            if (topHeader != null) {
                if (header != null) {
                    VBox wrapper = new VBox(0, topHeader, header);
                    header = wrapper;
                } else {
                    header = topHeader;
                }
            }
            if (header != null) {
                header.getStyleClass().add(JfxStyles.WORKSPACE_TEMPLATE_HEADER);
            }

            AppShellAnt.Result shellResult = AppShellAnt.create()
                    .header(header)
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
                    .footerBackground(footerBackground)
                    .buildResult();

            BorderPane root = shellResult.getRoot();
            root.getStyleClass().add(JfxStyles.WORKSPACE_TEMPLATE);
            applyStyles(root);
            return new Result(root, shellResult);
        }

        private Node buildStructuredHeader() {
            Node brandBox = buildBrandBox();
            List<Node> left = new ArrayList<>();
            if (brandBox != null) {
                left.add(brandBox);
            }
            left.addAll(headerLeft);

            Node center = packNodes(headerCenter, JfxStyles.WORKSPACE_TEMPLATE_HEADER_CENTER, Pos.CENTER_LEFT);
            Node actions = packNodes(headerRight, JfxStyles.WORKSPACE_TEMPLATE_HEADER_ACTIONS, Pos.CENTER_RIGHT);

            if (left.isEmpty() && center == null && actions == null) {
                return null;
            }

            HBarAnt bar = HBarAnt.create()
                    .left(left.toArray(Node[]::new))
                    .center(center)
                    .right(actions)
                    .gap(headerGap)
                    .borderBottom(false)
                    .build();
            bar.getStyleClass().add(JfxStyles.WORKSPACE_TEMPLATE_HEADER);
            return bar;
        }

        private Node buildBrandBox() {
            String resolvedTitle = TextUtils.safeText(brandTitle);
            String resolvedSubtitle = TextUtils.safeText(brandSubtitle);
            if ((resolvedTitle.isEmpty() && resolvedSubtitle.isEmpty()) && brandIcon == null) {
                return null;
            }

            HBox box = new HBox();
            box.setAlignment(Pos.CENTER_LEFT);
            box.setSpacing(8);
            box.getStyleClass().add(JfxStyles.WORKSPACE_TEMPLATE_HEADER_BRAND);

            if (brandIcon != null) {
                box.getChildren().add(brandIcon);
            }

            VBox textBox = new VBox();
            if (!resolvedTitle.isEmpty()) {
                LabelAnt title = LabelAnt.create(resolvedTitle);
                title.getStyleClass().add(JfxStyles.WORKSPACE_TEMPLATE_HEADER_TITLE);
                textBox.getChildren().add(title);
            }

            if (!resolvedSubtitle.isEmpty()) {
                LabelAnt subtitle = LabelAnt.create(resolvedSubtitle);
                subtitle.getStyleClass().add(JfxStyles.WORKSPACE_TEMPLATE_HEADER_SUBTITLE);
                textBox.getChildren().add(subtitle);
            }

            if (!textBox.getChildren().isEmpty()) {
                box.getChildren().add(textBox);
            }

            return box;
        }

        private static Node packNodes(List<Node> nodes, String styleClass, Pos alignment) {
            List<Node> resolved = nodes.stream().filter(Objects::nonNull).toList();
            if (resolved.isEmpty()) {
                return null;
            }
            if (resolved.size() == 1) {
                return resolved.get(0);
            }
            HBox box = new HBox();
            box.setAlignment(alignment != null ? alignment : Pos.CENTER_LEFT);
            box.getStyleClass().add(styleClass);
            box.getChildren().addAll(resolved);
            return box;
        }

        // safeText / safeSpacing / safeWidth 已统一收口到 TextUtils,见 P0-23。

    }

    /**
     * WorkspaceTemplate 构建结果。
     *
     * <p>保留 {@link AppShellAnt.Result} 的折叠控制能力，方便 header 里的自定义按钮
     * 或外部控制器直接驱动侧栏展开/收起。</p>
     */
    public static final class Result {
        private final BorderPane root;
        private final AppShellAnt.Result shellResult;

        private Result(BorderPane root, AppShellAnt.Result shellResult) {
            this.root = root;
            this.shellResult = shellResult;
        }

        public BorderPane getRoot() {
            return root;
        }

        public boolean isCollapsed() {
            return shellResult.isCollapsed();
        }

        public void toggle() {
            shellResult.toggle();
        }

        public void setCollapsed(boolean value) {
            shellResult.setCollapsed(value);
        }

        public BooleanProperty collapsedProperty() {
            return shellResult.collapsedProperty();
        }
    }
}
