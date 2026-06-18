package org.openkawu.jfxium.jfxiumUiExample;

/**
 * jfxiumUiExample 的共享常量，集中管理高频使用的窗口尺寸与标题。
 */
public final class UiExampleConstants {

    private UiExampleConstants() {}

    public static final String APP_TITLE = "JFXium UI Example";
    public static final double LOGIN_SCENE_WIDTH = 760;
    public static final double LOGIN_SCENE_HEIGHT = 520;
    public static final double MAIN_SCENE_WIDTH = 1280;
    public static final double MAIN_SCENE_HEIGHT = 800;
    public static final double MAIN_SIDER_WIDTH = 240;
    public static final double DEFAULT_WATERMARK_OPACITY = 0.12;
    public static final double DRAWER_WIDTH = 420;

    /** Demo 路由 key：集中管理，避免首页 / 目录页散落魔法值。 */
    public static final String ROUTE_HOME = "home";
    public static final String ROUTE_PROJECT_CONSOLE = "layout.projectconsole";
    public static final String ROUTE_PROJECT_HERO = "layout.projecthero";
    public static final String ROUTE_WORKSPACE_TEMPLATE = "layout.workspace";
    public static final String ROUTE_PROJECT_OVERVIEW = "layout.projectoverview";
    public static final String ROUTE_PROJECT_RELEASE = "layout.projectrelease";
    public static final String ROUTE_PROJECT_SHOWCASE = "layout.projectshowcase";
    public static final String ROUTE_PROJECT_FEATURE = "layout.projectfeature";
    public static final String ROUTE_PROJECT_CHANGELOG = "layout.projectchangelog";
    public static final String ROUTE_PROJECT_QUICKSTART = "layout.projectquickstart";
    public static final String ROUTE_MENU = "navigation.menu";
    public static final String ROUTE_MODAL = "feedback.modal";
    public static final String ROUTE_WATERMARK = "datadisplay.watermark";
}
