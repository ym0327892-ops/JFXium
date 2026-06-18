package org.openkawu.jfxium.jfxiumUiExample.pages;

import java.util.function.Consumer;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.UiExampleConstants;
import org.openkawu.jfxium.template.ProjectDashboardTemplate;

/**
 * 首页 —— 工程概览 + 快捷入口。
 *
 * <p>把 JFXium 当成一个真实桌面工程来展示：工程首页、主题联动、
 * 水印和工作台壳层都收口到一个更成品化的项目展示模板里。</p>
 */
public class HomePage extends VBoxAnt {

    private final Consumer<String> onNavigate;

    public HomePage() {
        this(null);
    }

    public HomePage(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;

        spacing(0).children(ProjectDashboardTemplate.create()
                .snapshot(ProjectDashboardTemplate.Snapshot.demo())
                .onAction(this::handleDashboardAction)
                .build());
    }

    private void navigate(String route) {
        if (onNavigate != null && route != null) {
            onNavigate.accept(route);
        }
    }

    private void handleDashboardAction(String action) {
        if (action == null) {
            return;
        }

        switch (action) {
            case ProjectDashboardTemplate.ACTION_PROJECT_CONSOLE -> navigate(UiExampleConstants.ROUTE_PROJECT_CONSOLE);
            case ProjectDashboardTemplate.ACTION_WORKSPACE_TEMPLATE -> navigate(UiExampleConstants.ROUTE_WORKSPACE_TEMPLATE);
            case ProjectDashboardTemplate.ACTION_PROJECT_OVERVIEW -> navigate(UiExampleConstants.ROUTE_PROJECT_OVERVIEW);
            case ProjectDashboardTemplate.ACTION_PROJECT_RELEASE -> navigate(UiExampleConstants.ROUTE_PROJECT_RELEASE);
            case ProjectDashboardTemplate.ACTION_PROJECT_SHOWCASE -> navigate(UiExampleConstants.ROUTE_PROJECT_SHOWCASE);
            case ProjectDashboardTemplate.ACTION_PROJECT_MENU -> navigate(UiExampleConstants.ROUTE_MENU);
            case ProjectDashboardTemplate.ACTION_PROJECT_MODAL -> navigate(UiExampleConstants.ROUTE_MODAL);
            case ProjectDashboardTemplate.ACTION_WATERMARK -> navigate(UiExampleConstants.ROUTE_WATERMARK);
            case "settings" -> navigate(UiExampleConstants.ROUTE_WORKSPACE_TEMPLATE);
            case "logout" -> navigate(UiExampleConstants.ROUTE_HOME);
            default -> navigate(action);
        }
    }
}
