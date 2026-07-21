package org.openkawu.jfxium.jfxiumUiExample.view;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.composite.MenuAnt;
import org.openkawu.jfxium.component.composite.SegmentedAnt;
import org.openkawu.jfxium.core.theme.ThemeDensity;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.component.layout.BorderPaneAnt;
import org.openkawu.jfxium.component.layout.ScrollPaneAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.jfxiumUiExample.UiExampleConstants;
import org.openkawu.jfxium.template.ProjectConsoleTemplate;
import org.openkawu.jfxium.template.WorkspaceSettingsTemplate;
import org.openkawu.jfxium.template.WorkspaceTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 主窗口：工程壳展示页。
 *
 * <p>这个页面不再只是“组件目录”，而是把 JFXium 的常用工程骨架串在一起：
 * 顶部菜单栏、头部品牌区、头像入口、右侧抽屉设置、左侧导航、右侧内容区、
 * 底部状态栏，以及整体水印层。</p>
 */
public class MainView {

    private final String currentUser;
    private final Runnable onLogout;
    private final Consumer<Boolean> onWatermarkVisibleChanged;
    private final Supplier<Boolean> watermarkVisibleSupplier;
    private final PageRegistry registry;
    private final BorderPane contentHost = BorderPaneAnt.create().build();
    private MenuAnt.Controller menuController;
    private WorkspaceTemplate.Result workspaceResult;

    public MainView(String currentUser, Runnable onLogout) {
        this(currentUser, onLogout, null, null);
    }

    public MainView(String currentUser,
                    Runnable onLogout,
                    Consumer<Boolean> onWatermarkVisibleChanged,
                    Supplier<Boolean> watermarkVisibleSupplier) {
        this.currentUser = currentUser != null ? currentUser : "";
        this.onLogout = onLogout;
        this.onWatermarkVisibleChanged = onWatermarkVisibleChanged;
        this.watermarkVisibleSupplier = watermarkVisibleSupplier;
        this.registry = PageCatalog.create(this::navigate);
    }

    public BorderPane build() {
        navigate(PageCatalog.HOME_KEY);

        ScrollPaneAnt contentScroll = ScrollPaneAnt.create()
                .content(contentHost)
                .fitToWidth(true)
                .build();
        contentScroll.getStyleClass().add(Background.LAYOUT.styleClass());

        ButtonAnt settingsBtn = ButtonAnt.create("设置")
                .type(ButtonAnt.Type.LINK)
                .size(Size.XS)
                .onClick(e -> openSettingsDrawer(contentHost))
                .build();

        // 顶栏密度开关：默认 / 紧凑。切换时由 ThemeManager 整套切换 UA 样式表（§15.3 P3）。
        ThemeManager themeManager = ThemeManager.getInstance();
        Node densityToggle = SegmentedAnt.create()
                .size(Size.SMALL)
                .option("default", "默认")
                .option("compact", "紧凑")
                .selected(themeManager.getDensity() == ThemeDensity.COMPACT ? "compact" : "default")
                .onChange(val -> themeManager.setDensity(
                        "compact".equals(val) ? ThemeDensity.COMPACT : ThemeDensity.DEFAULT))
                .build();

        workspaceResult = ProjectConsoleTemplate.create()
                .brand(UiExampleConstants.APP_TITLE, "工程壳展示")
                .brandIcon(IconAnt.Path.DASHBOARD)
                .menuBar(ProjectConsoleTemplate.standardMenuBar(this::handleMenuAction))
                .headerRight(densityToggle, settingsBtn)
                .userMenu(currentUser, this::handleUserMenuAction)
                .sider(buildSider(), UiExampleConstants.MAIN_SIDER_WIDTH)
                .content(contentScroll)
                .footer(ProjectConsoleTemplate.statusBar("就绪", UiExampleConstants.APP_TITLE + " | Java 21 | JavaFX 21"))
                .collapsible()
                .buildResult();

        if (menuController != null) {
            menuController.setCollapsed(workspaceResult.isCollapsed());
            workspaceResult.collapsedProperty().addListener((obs, oldVal, newVal) -> {
                if (menuController != null) {
                    menuController.setCollapsed(newVal);
                }
            });
        }

        return workspaceResult.getRoot();
    }

    private Pane buildSider() {
        List<ProjectConsoleTemplate.SidebarLink> topLevelItems = new ArrayList<>();
        Map<PageRegistry.Category, List<ProjectConsoleTemplate.SidebarLink>> grouped = new LinkedHashMap<>();
        for (PageRegistry.Category category : PageRegistry.Category.values()) {
            grouped.put(category, new ArrayList<>());
        }

        for (PageRegistry.Entry entry : registry.entries().values()) {
            if (entry.category() == null) {
                Node icon = PageCatalog.HOME_KEY.equals(entry.key())
                        ? IconAnt.path(IconAnt.Path.HOME, 16)
                        : null;
                topLevelItems.add(ProjectConsoleTemplate.sidebarLink(entry.key(), entry.title(), icon));
            } else {
                grouped.get(entry.category()).add(ProjectConsoleTemplate.sidebarLink(
                        entry.key(),
                        entry.title(),
                        iconForCategory(entry.category())));
            }
        }

        List<ProjectConsoleTemplate.SidebarSection> sections = new ArrayList<>();
        for (PageRegistry.Category category : PageRegistry.Category.values()) {
            List<ProjectConsoleTemplate.SidebarLink> bucket = grouped.get(category);
            if (bucket.isEmpty()) {
                continue;
            }
            sections.add(ProjectConsoleTemplate.sidebarSection(
                    "group." + category.name().toLowerCase(),
                    category.label(),
                    iconForCategory(category),
                    true,
                    bucket.toArray(ProjectConsoleTemplate.SidebarLink[]::new)));
        }

        ProjectConsoleTemplate.SidebarResult sidebar = ProjectConsoleTemplate.sidebarPane(
                PageCatalog.HOME_KEY,
                this::navigate,
                topLevelItems.toArray(ProjectConsoleTemplate.SidebarLink[]::new),
                sections.toArray(ProjectConsoleTemplate.SidebarSection[]::new));
        menuController = sidebar.controller();

        ScrollPaneAnt menuScroll = ScrollPaneAnt.create(sidebar.root()).fitToWidth(true);
        menuScroll.hbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        menuScroll.getStyleClass().add(Background.SUBTLE.styleClass());

        BorderPane wrapper = BorderPaneAnt.create().center(menuScroll.build()).build();
        wrapper.getStyleClass().add(Background.SUBTLE.styleClass());
        return wrapper;
    }

    private static Node iconForCategory(PageRegistry.Category category) {
        return switch (category) {
            case GENERAL -> IconAnt.path(IconAnt.Path.SETTINGS, 16);
            case LAYOUT -> IconAnt.path(IconAnt.Path.DASHBOARD, 16);
            case NAVIGATION -> IconAnt.path(IconAnt.Path.HOME, 16);
            case DATA_ENTRY -> IconAnt.path(IconAnt.Path.EDIT, 16);
            case DATA_DISPLAY -> IconAnt.path(IconAnt.Path.CHART, 16);
            case FEEDBACK -> IconAnt.path(IconAnt.Path.BELL, 16);
        };
    }

    private void navigate(String key) {
        Node page = registry.build(key);
        if (page == null) {
            return;
        }
        contentHost.setCenter(page);
        if (menuController != null) {
            menuController.setSelectedKey(key);
        }
    }

    private void openSettingsDrawer(Node owner) {
        if (owner == null) {
            return;
        }

        WorkspaceSettingsTemplate.drawer((int) UiExampleConstants.DRAWER_WIDTH,
                        watermarkVisibleSupplier,
                        onWatermarkVisibleChanged)
                .open(owner);
    }

    private void handleUserMenuAction(String action) {
        if (action == null) {
            return;
        }
        switch (action) {
            case "settings" -> openSettingsDrawer(contentHost);
            case "logout" -> doLogout();
            default -> MessageAnt.info("未知动作: " + action);
        }
    }

    private void handleMenuAction(String action) {
        if (action == null) {
            return;
        }
        switch (action) {
            case "home" -> navigate(PageCatalog.HOME_KEY);
            case "logout" -> doLogout();
            case "settings" -> openSettingsDrawer(contentHost);
            case "toggleSidebar" -> toggleSidebar();
            case "undo" -> MessageAnt.info("演示项目暂未实现撤销");
            case "redo" -> MessageAnt.info("演示项目暂未实现重做");
            case "about" -> MessageAnt.info("JFXium v1.0-SNAPSHOT · Java 21 · JavaFX 21");
            default -> MessageAnt.info("未知动作: " + action);
        }
    }

    private void doLogout() {
        if (onLogout != null) {
            onLogout.run();
        }
    }

    private void toggleSidebar() {
        if (workspaceResult != null) {
            workspaceResult.toggle();
        }
    }
}
