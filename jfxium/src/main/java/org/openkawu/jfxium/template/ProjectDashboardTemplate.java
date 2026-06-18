package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.composite.ProgressAnt;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.composite.TimelineAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.theme.ThemeDensity;
import org.openkawu.jfxium.core.theme.ThemeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ProjectDashboardTemplate - 工程项目首页成品模板。
 *
 * <p>它把工程首页最常重复的组合继续上提一层：
 * 首屏门面、项目概览、发布节奏和快捷入口一页打包，
 * 让 demo / showcase 页面不再手写同一套拼装逻辑。</p>
 *
 * <p>默认状态下会自动根据 {@link ThemeManager} 当前状态填充主题、密度和主题色，
 * 同时保留每个区块的覆写入口，方便示例页按需替换。</p>
 */
public final class ProjectDashboardTemplate {

    public static final String ACTION_PROJECT_CONSOLE = "project.console";
    public static final String ACTION_WORKSPACE_TEMPLATE = "project.workspace";
    public static final String ACTION_PROJECT_OVERVIEW = "project.overview";
    public static final String ACTION_PROJECT_RELEASE = "project.release";
    public static final String ACTION_PROJECT_SHOWCASE = "project.showcase";
    public static final String ACTION_PROJECT_MENU = "project.menu";
    public static final String ACTION_PROJECT_MODAL = "project.modal";
    public static final String ACTION_WATERMARK = "project.watermark";

    private static final String DEFAULT_TITLE = "工程首页";
    private static final String DEFAULT_DESCRIPTION = "把工程门面、概览、发布节奏和快捷入口组织成一页展示。";
    private static final String DEFAULT_PAGE_TITLE = "JFXium UI Example";
    private static final String DEFAULT_PAGE_SUBTITLE = "工程项目展示首页";
    private static final String DEFAULT_PAGE_DESCRIPTION = "这不是简单的控件列表，而是一个完整工程壳样板：顶部菜单、头像设置、左侧导航、右侧展示、底部状态栏和全局水印都在这里一起演示。";
    private static final String DEFAULT_CURRENT_USER = "开发者";
    private static final String DEFAULT_VERSION = "1.0-SNAPSHOT";
    private static final String DEFAULT_BRANCH = "main";
    private static final String DEFAULT_BUILD_STATUS = "通过";
    private static final String DEFAULT_MODE = "Showcase";
    private static final String DEFAULT_TEMPLATE_COUNT = "14";
    private static final String DEFAULT_COMPONENT_COUNT = "112+";
    private static final String DEFAULT_UPDATED_AT = "2026-06-18";

    private ProjectDashboardTemplate() {}

    /**
     * 工程首页展示快照：把首页最常出现的标题、版本、分支、统计口径收口成一个数据对象。
     */
    public static final class Snapshot {
        private String pageTitle = DEFAULT_PAGE_TITLE;
        private String pageSubtitle = DEFAULT_PAGE_SUBTITLE;
        private String pageDescription = DEFAULT_PAGE_DESCRIPTION;
        private String currentUser = DEFAULT_CURRENT_USER;
        private String version = DEFAULT_VERSION;
        private String branch = DEFAULT_BRANCH;
        private String buildStatus = DEFAULT_BUILD_STATUS;
        private String mode = DEFAULT_MODE;
        private String templateCount = DEFAULT_TEMPLATE_COUNT;
        private String componentCount = DEFAULT_COMPONENT_COUNT;
        private String updatedAt = DEFAULT_UPDATED_AT;
        private double completion = 0.92;
        private IconAnt.Path icon = IconAnt.Path.DASHBOARD;

        private Snapshot() {}

        public static Snapshot create() {
            return new Snapshot();
        }

        public static Snapshot demo() {
            return create();
        }

        public Snapshot pageTitle(String pageTitle) {
            this.pageTitle = pageTitle != null ? pageTitle : DEFAULT_PAGE_TITLE;
            return this;
        }

        public String pageTitle() {
            return pageTitle;
        }

        public Snapshot pageSubtitle(String pageSubtitle) {
            this.pageSubtitle = pageSubtitle != null ? pageSubtitle : DEFAULT_PAGE_SUBTITLE;
            return this;
        }

        public String pageSubtitle() {
            return pageSubtitle;
        }

        public Snapshot pageDescription(String pageDescription) {
            this.pageDescription = pageDescription != null ? pageDescription : DEFAULT_PAGE_DESCRIPTION;
            return this;
        }

        public String pageDescription() {
            return pageDescription;
        }

        public Snapshot currentUser(String currentUser) {
            this.currentUser = currentUser != null ? currentUser : DEFAULT_CURRENT_USER;
            return this;
        }

        public String currentUser() {
            return currentUser;
        }

        public Snapshot version(String version) {
            this.version = version != null ? version : DEFAULT_VERSION;
            return this;
        }

        public String version() {
            return version;
        }

        public Snapshot branch(String branch) {
            this.branch = branch != null ? branch : DEFAULT_BRANCH;
            return this;
        }

        public String branch() {
            return branch;
        }

        public Snapshot buildStatus(String buildStatus) {
            this.buildStatus = buildStatus != null ? buildStatus : DEFAULT_BUILD_STATUS;
            return this;
        }

        public String buildStatus() {
            return buildStatus;
        }

        public Snapshot mode(String mode) {
            this.mode = mode != null ? mode : DEFAULT_MODE;
            return this;
        }

        public String mode() {
            return mode;
        }

        public Snapshot templateCount(String templateCount) {
            this.templateCount = templateCount != null ? templateCount : DEFAULT_TEMPLATE_COUNT;
            return this;
        }

        public String templateCount() {
            return templateCount;
        }

        public Snapshot componentCount(String componentCount) {
            this.componentCount = componentCount != null ? componentCount : DEFAULT_COMPONENT_COUNT;
            return this;
        }

        public String componentCount() {
            return componentCount;
        }

        public Snapshot updatedAt(String updatedAt) {
            this.updatedAt = updatedAt != null ? updatedAt : DEFAULT_UPDATED_AT;
            return this;
        }

        public String updatedAt() {
            return updatedAt;
        }

        public Snapshot completion(double completion) {
            this.completion = Double.isFinite(completion) ? Math.max(0, Math.min(1, completion)) : 0.92;
            return this;
        }

        public double completion() {
            return completion;
        }

        public Snapshot icon(IconAnt.Path icon) {
            this.icon = icon != null ? icon : IconAnt.Path.DASHBOARD;
            return this;
        }

        public IconAnt.Path icon() {
            return icon;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder extends AbstractStyleBuilder<Builder> {
        private String title = DEFAULT_TITLE;
        private String description = DEFAULT_DESCRIPTION;
        private String pageTitle = DEFAULT_PAGE_TITLE;
        private String pageSubtitle = DEFAULT_PAGE_SUBTITLE;
        private String pageDescription = DEFAULT_PAGE_DESCRIPTION;
        private String currentUser = DEFAULT_CURRENT_USER;
        private String version = DEFAULT_VERSION;
        private String branch = DEFAULT_BRANCH;
        private String buildStatus = DEFAULT_BUILD_STATUS;
        private String mode = DEFAULT_MODE;
        private String templateCount = DEFAULT_TEMPLATE_COUNT;
        private String componentCount = DEFAULT_COMPONENT_COUNT;
        private String updatedAt = DEFAULT_UPDATED_AT;
        private double completion = 0.92;
        private IconAnt.Path icon = IconAnt.Path.DASHBOARD;
        private Node hero;
        private Node overview;
        private Node release;
        private Node launchPad;
        private final List<Node> extraSections = new ArrayList<>();
        private Consumer<String> onAction;

        private Builder() {}

        public Builder title(String title) {
            this.title = title != null ? title : DEFAULT_TITLE;
            return this;
        }

        public Builder description(String description) {
            this.description = description != null ? description : DEFAULT_DESCRIPTION;
            return this;
        }

        public Builder pageTitle(String pageTitle) {
            this.pageTitle = pageTitle != null ? pageTitle : DEFAULT_PAGE_TITLE;
            return this;
        }

        public Builder pageSubtitle(String pageSubtitle) {
            this.pageSubtitle = pageSubtitle != null ? pageSubtitle : DEFAULT_PAGE_SUBTITLE;
            return this;
        }

        public Builder pageDescription(String pageDescription) {
            this.pageDescription = pageDescription != null ? pageDescription : DEFAULT_PAGE_DESCRIPTION;
            return this;
        }

        public Builder currentUser(String currentUser) {
            this.currentUser = currentUser != null ? currentUser : DEFAULT_CURRENT_USER;
            return this;
        }

        public Builder version(String version) {
            this.version = version != null ? version : DEFAULT_VERSION;
            return this;
        }

        public Builder branch(String branch) {
            this.branch = branch != null ? branch : DEFAULT_BRANCH;
            return this;
        }

        public Builder buildStatus(String buildStatus) {
            this.buildStatus = buildStatus != null ? buildStatus : DEFAULT_BUILD_STATUS;
            return this;
        }

        public Builder mode(String mode) {
            this.mode = mode != null ? mode : DEFAULT_MODE;
            return this;
        }

        public Builder templateCount(String templateCount) {
            this.templateCount = templateCount != null ? templateCount : DEFAULT_TEMPLATE_COUNT;
            return this;
        }

        public Builder componentCount(String componentCount) {
            this.componentCount = componentCount != null ? componentCount : DEFAULT_COMPONENT_COUNT;
            return this;
        }

        public Builder updatedAt(String updatedAt) {
            this.updatedAt = updatedAt != null ? updatedAt : DEFAULT_UPDATED_AT;
            return this;
        }

        public Builder completion(double completion) {
            this.completion = Double.isFinite(completion) ? Math.max(0, Math.min(1, completion)) : 0.92;
            return this;
        }

        public Builder icon(IconAnt.Path icon) {
            this.icon = icon != null ? icon : IconAnt.Path.DASHBOARD;
            return this;
        }

        public Builder snapshot(Snapshot snapshot) {
            if (snapshot == null) {
                return this;
            }
            this.pageTitle = snapshot.pageTitle;
            this.pageSubtitle = snapshot.pageSubtitle;
            this.pageDescription = snapshot.pageDescription;
            this.currentUser = snapshot.currentUser;
            this.version = snapshot.version;
            this.branch = snapshot.branch;
            this.buildStatus = snapshot.buildStatus;
            this.mode = snapshot.mode;
            this.templateCount = snapshot.templateCount;
            this.componentCount = snapshot.componentCount;
            this.updatedAt = snapshot.updatedAt;
            this.completion = snapshot.completion;
            this.icon = snapshot.icon;
            return this;
        }

        public Builder onAction(Consumer<String> onAction) {
            this.onAction = onAction;
            return this;
        }

        public Builder hero(Node hero) {
            this.hero = hero;
            return this;
        }

        public Builder overview(Node overview) {
            this.overview = overview;
            return this;
        }

        public Builder release(Node release) {
            this.release = release;
            return this;
        }

        public Builder launchPad(Node launchPad) {
            this.launchPad = launchPad;
            return this;
        }

        public Builder section(Node section) {
            if (section != null) {
                extraSections.add(section);
            }
            return this;
        }

        public Builder sections(Node... sectionNodes) {
            if (sectionNodes != null) {
                for (Node sectionNode : sectionNodes) {
                    section(sectionNode);
                }
            }
            return this;
        }

        public VBox build() {
            List<Node> sections = new ArrayList<>();
            sections.add(hero != null ? hero : buildHero());
            sections.add(overview != null ? overview : buildOverview());
            sections.add(release != null ? release : buildRelease());
            sections.add(launchPad != null ? launchPad : buildLaunchPad());
            sections.addAll(extraSections);

            VBox root = ProjectShowcaseTemplate.create()
                    .title(title)
                    .description(description)
                    .sections(sections.toArray(Node[]::new))
                    .build();
            applyStyles(root);
            return root;
        }

        private Node buildHero() {
            ThemeManager mgr = ThemeManager.getInstance();
            String currentTheme = mgr.getCurrentFamily().getDisplayName();
            String currentPreset = mgr.getCurrentPrimaryPreset() != null
                    ? mgr.getCurrentPrimaryPreset().getDisplayName()
                    : "自定义";
            String themeHex = mgr.getCurrentThemeColor() != null ? mgr.getCurrentThemeColor().getHexColor() : "";
            String density = mgr.getDensity() == ThemeDensity.COMPACT ? "紧凑" : "默认";

            ButtonAnt consoleButton = ButtonAnt.compactLink("工程控制台")
                    .onClick(e -> fire(ACTION_PROJECT_CONSOLE))
                    .build();
            consoleButton.setDisable(onAction == null);

            ButtonAnt workspaceButton = ButtonAnt.compactLink("工作台模板")
                    .onClick(e -> fire(ACTION_WORKSPACE_TEMPLATE))
                    .build();
            workspaceButton.setDisable(onAction == null);

            ButtonAnt watermarkButton = ButtonAnt.compactLink("水印示例")
                    .onClick(e -> fire(ACTION_WATERMARK))
                    .build();
            watermarkButton.setDisable(onAction == null);

            return ProjectHeroTemplate.create()
                    .title(pageTitle)
                    .subtitle(pageSubtitle)
                    .description(pageDescription)
                    .icon(icon)
                    .status(buildStatus, TagAnt.Type.SUCCESS)
                    .status("零 FXML", TagAnt.Type.PRIMARY)
                    .status("工程展示", TagAnt.Type.SUCCESS)
                    .meta("当前主题", currentTheme + " / " + currentPreset)
                    .meta("当前密度", density)
                    .meta("主题色", themeHex)
                    .action(consoleButton)
                    .action(workspaceButton)
                    .action(watermarkButton)
                    .action(ProjectHeroTemplate.userMenu(currentUser, this::fire).getTrigger())
                    .build();
        }

        private Node buildOverview() {
            ThemeManager mgr = ThemeManager.getInstance();
            String currentTheme = mgr.getCurrentFamily().getDisplayName();
            String currentPreset = mgr.getCurrentPrimaryPreset() != null
                    ? mgr.getCurrentPrimaryPreset().getDisplayName()
                    : "自定义";
            String themeHex = mgr.getCurrentThemeColor() != null ? mgr.getCurrentThemeColor().getHexColor() : "";
            String density = mgr.getDensity() == ThemeDensity.COMPACT ? "紧凑" : "默认";

            return ProjectOverviewTemplate.create()
                    .title("项目概览")
                    .description("运行态、技术栈、里程碑与近期动作。")
                    .status(buildStatus, TagAnt.Type.SUCCESS)
                    .status("工程展示", TagAnt.Type.PRIMARY)
                    .meta("版本", version)
                    .meta("分支", branch)
                    .meta("构建", TagAnt.create(buildStatus).type(TagAnt.Type.SUCCESS).build())
                    .meta("模式", mode)
                    .metric("当前主题", currentTheme, currentPreset + " / " + themeHex, IconAnt.Path.SETTINGS)
                    .metric("当前密度", density, "default / compact 由 ThemeManager 统一切换", IconAnt.Path.DASHBOARD)
                    .metric("模板沉淀", templateCount,
                            "ProjectConsole / ProjectHero / ProjectOverview / ProjectRelease / ProjectShowcase / LaunchPad / Workspace 等模板已可复用",
                            IconAnt.Path.FILE)
                    .metric("组件总数", componentCount,
                            "97 个 *Ant 组件 + " + templateCount + " 个 *Template + 1 个 FilterBarAnt",
                            IconAnt.Path.HOME)
                    .tech("Java 21", TagAnt.Type.DEFAULT)
                    .tech("JavaFX 21", TagAnt.Type.DEFAULT)
                    .tech("JFxium", TagAnt.Type.PRIMARY)
                    .progress("工程展示完成度", completion, ProgressAnt.Status.SUCCESS,
                            "核心展示链路已经收口成一页。")
                    .milestone("把首页改成工程控制台", "完成", TimelineAnt.DotColor.GREEN)
                    .milestone("抽出 ProjectConsoleTemplate", "完成", TimelineAnt.DotColor.GREEN)
                    .milestone("抽出 ProjectDashboardTemplate", "完成", TimelineAnt.DotColor.GREEN)
                    .milestone("继续沉淀公共 section / 状态块 / 近期活动", "进行中", TimelineAnt.DotColor.BLUE)
                    .activity(ACTION_WORKSPACE_TEMPLATE, "查看工作台模板",
                            "header / sider / footer 的完整壳层", "打开")
                    .activity(ACTION_PROJECT_OVERVIEW, "查看工程概览模板",
                            "工程首页总览板的可复用实现", "打开")
                    .activity(ACTION_PROJECT_RELEASE, "查看发布节奏模板",
                            "版本、变更和发布准备度面板", "打开")
                    .activity(ACTION_PROJECT_SHOWCASE, "查看工程展示首页模板",
                            "概览、发布和快捷入口的一页组合", "打开")
                    .onAction(this::fire)
                    .build();
        }

        private Node buildRelease() {
            return ProjectReleaseTemplate.create()
                    .title("发布节奏")
                    .description("版本、分支、变更和发布准备度。")
                    .status(buildStatus, TagAnt.Type.SUCCESS)
                    .status("可演示", TagAnt.Type.PRIMARY)
                    .meta("版本", version)
                    .meta("分支", branch)
                    .meta("更新", updatedAt)
                    .release(version, "当前演示版本", TimelineAnt.DotColor.GREEN)
                    .release("1.0.1", "引入工程首页成品模板", TimelineAnt.DotColor.BLUE)
                    .release("1.0.2", "把公共 section / 入口沉淀回 JFxium", TimelineAnt.DotColor.BLUE)
                    .change(ACTION_PROJECT_OVERVIEW, "工程概览模板", "展示运行态、技术栈和里程碑", "打开")
                    .change(ACTION_WORKSPACE_TEMPLATE, "工作台模板", "继续收口 header / sider / footer 的工程壳层", "打开")
                    .change(ACTION_PROJECT_SHOWCASE, "工程项目展示首页模板", "把概览、发布和快捷入口组合成首页", "打开")
                    .readiness("当前发布准备度", completion, ProgressAnt.Status.SUCCESS,
                            "核心展示链路已经稳定，可以继续加细节区块。")
                    .onAction(this::fire)
                    .build();
        }

        private Node buildLaunchPad() {
            return LaunchPadTemplate.create()
                    .title("快捷入口")
                    .description("从首页直接跳到最常看的模板页和示例页。")
                    .onAction(this::fire)
                    .item(ACTION_PROJECT_CONSOLE, "工程控制台", "查看顶部菜单、头像设置、左侧导航和状态栏的完整壳层。",
                            IconAnt.Path.DASHBOARD)
                    .item(ACTION_WORKSPACE_TEMPLATE, "工作台模板", "查看完整工程壳层", IconAnt.Path.DASHBOARD)
                    .item(ACTION_PROJECT_OVERVIEW, "工程概览", "查看工程状态总览板", IconAnt.Path.FILE)
                    .item(ACTION_PROJECT_RELEASE, "发布节奏", "查看版本演进与准备度", IconAnt.Path.CHART)
                    .item(ACTION_PROJECT_SHOWCASE, "工程展示首页", "查看概览、发布和快捷入口的一页组合", IconAnt.Path.DASHBOARD)
                    .item(ACTION_PROJECT_MENU, "菜单示例", "看 MenuAnt 的分组、折叠和运行时高亮切换", IconAnt.Path.HOME)
                    .item(ACTION_PROJECT_MODAL, "模态示例", "查看 Modal / Drawer / Prompt 的反馈链路", IconAnt.Path.BELL)
                    .item(ACTION_WATERMARK, "水印示例", "确认全局叠层和工作台水印是否一致", IconAnt.Path.SETTINGS)
                    .build();
        }

        private void fire(String action) {
            if (onAction != null && action != null) {
                onAction.accept(action);
            }
        }
    }
}
