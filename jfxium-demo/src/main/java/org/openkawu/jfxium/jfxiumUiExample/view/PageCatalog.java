package org.openkawu.jfxium.jfxiumUiExample.view;

import javafx.scene.Node;
import org.openkawu.jfxium.jfxiumUiExample.pages.HomePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.AutoCompleteExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.CascaderExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.CheckboxExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.ChoiceBoxExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.ColorPickerExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.DatePickerExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.FormExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.InputExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.InputNumberExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.MentionsExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.RadioExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.RateExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.SelectExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.SliderExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.SwitchExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.TextAreaExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.TimePickerExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.ToggleButtonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.TransferExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.TreeSelectExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.dataentry.UploadExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.AccordionExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.AvatarBadgeExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.CalendarExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.CanvasExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.CarouselExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.CollapseExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.DescriptionsExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.DividerExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.EmptyExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.GroupBoxExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.ImageExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.ListExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.ListViewExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.PopoverExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.ProgressExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.QRCodeExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.SeparatorExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.SkeletonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.StatisticExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.TableExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.TagExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.TimelineExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.TitledPaneExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.TreeExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.TreeTableExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.ContextMenuExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.DrawerExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.DesktopNotificationExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.FloatButtonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.MessageExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.ModalExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.NotificationExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.PopconfirmExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay.PopoverExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.PromptDialogExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.ResultExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.SpinExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.SpinnerExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.TooltipExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.ButtonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.CodeBlockExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.HyperlinkExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.IconExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.LabelExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.MenuButtonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.SelectableTextExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.SegmentedExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.SplitButtonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.SplitMenuButtonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.TypographyExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.WatermarkExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.FlexExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.GridExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.LayoutContainerExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.ProjectChangelogExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.ProjectConsoleExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.ProjectFeatureExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.ProjectHeroExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.ProjectQuickStartExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.HBarExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.ProjectOverviewExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.ProjectReleaseExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.ProjectShowcaseExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.VBarExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.WorkspaceTemplateExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.AnchorExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.BackTopExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.BreadcrumbExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.DropdownExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.MenuBarExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.MenuExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.PaginationExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.StatusBarExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.StepsExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.TabsExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.ToolBarExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.UiExampleConstants;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 示例页面目录，负责把所有示例页注册进 PageRegistry。
 */
final class PageCatalog {

    static final String HOME_KEY = UiExampleConstants.ROUTE_HOME;

    private PageCatalog() {}

    static PageRegistry create() {
        return create(null);
    }

    static PageRegistry create(Consumer<String> onNavigate) {
        PageRegistry registry = new PageRegistry();

        registry.register(HOME_KEY, "首页", null,
                () -> new HomePage(onNavigate));

        registerGeneral(registry);
        registerLayout(registry);
        registerTemplate(registry);
        registerNavigation(registry);
        registerDataEntry(registry);
        registerDataDisplay(registry);
        registerFeedback(registry);

        return registry;
    }

    private static void registerGeneral(PageRegistry registry) {
        register(registry, "general.button", "Button 按钮", PageRegistry.Category.GENERAL, ButtonExamplePage::new);
        register(registry, "general.label", "Label 文本", PageRegistry.Category.GENERAL, LabelExamplePage::new);
        register(registry, "general.codeblock", "CodeBlock 代码块", PageRegistry.Category.GENERAL, CodeBlockExamplePage::new);
        register(registry, "general.typography", "Typography 排版", PageRegistry.Category.GENERAL, TypographyExamplePage::new);
        register(registry, "general.icon", "Icon 图标", PageRegistry.Category.GENERAL, IconExamplePage::new);
        register(registry, "general.segmented", "Segmented 分段器", PageRegistry.Category.GENERAL, SegmentedExamplePage::new);
        register(registry, "general.selectabletext", "SelectableText 可选文本", PageRegistry.Category.GENERAL, SelectableTextExamplePage::new);
        register(registry, "general.menubutton", "MenuButton 菜单按钮", PageRegistry.Category.GENERAL, MenuButtonExamplePage::new);
        register(registry, "general.splitbutton", "SplitButton 分裂按钮", PageRegistry.Category.GENERAL, SplitButtonExamplePage::new);
        register(registry, "general.splitmenu", "SplitMenuButton 分裂菜单", PageRegistry.Category.GENERAL, SplitMenuButtonExamplePage::new);
        register(registry, "general.hyperlink", "Hyperlink 超链接", PageRegistry.Category.GENERAL, HyperlinkExamplePage::new);
    }

    private static void registerLayout(PageRegistry registry) {
        register(registry, "layout.grid", "Grid 栅格", PageRegistry.Category.LAYOUT, GridExamplePage::new);
        register(registry, "layout.flex", "Flex 弹性布局", PageRegistry.Category.LAYOUT, FlexExamplePage::new);
        register(registry, "layout.bar", "HBar 横向条状容器", PageRegistry.Category.LAYOUT, HBarExamplePage::new);
        register(registry, "layout.vbar", "VBar 竖向条状容器", PageRegistry.Category.LAYOUT, VBarExamplePage::new);
        register(registry, "layout.divider", "Divider 分割线", PageRegistry.Category.LAYOUT, DividerExamplePage::new);
        register(registry, "layout.separator", "Separator 分隔符", PageRegistry.Category.LAYOUT, SeparatorExamplePage::new);
        // 布局容器综合页（VBox/HBox/BorderPane/StackPane/SplitPane/ScrollPane/... 的 spacing/align/grow/padding 用法）
        register(registry, "layout.container", "布局容器", PageRegistry.Category.LAYOUT, LayoutContainerExamplePage::new);
    }

    private static void registerTemplate(PageRegistry registry) {
        register(registry, "template.workspace", "WorkspaceTemplate 工作台模板", PageRegistry.Category.TEMPLATE, WorkspaceTemplateExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_PROJECT_CONSOLE, "ProjectConsole 工程控制台壳模板", PageRegistry.Category.TEMPLATE, ProjectConsoleExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_PROJECT_FEATURE, "ProjectFeature 工程特色模块模板", PageRegistry.Category.TEMPLATE, ProjectFeatureExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_PROJECT_QUICKSTART, "ProjectQuickStart 快速上手模板", PageRegistry.Category.TEMPLATE, ProjectQuickStartExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_PROJECT_CHANGELOG, "ProjectChangelog 更新日志模板", PageRegistry.Category.TEMPLATE, ProjectChangelogExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_PROJECT_HERO, "ProjectHero 工程门面模板", PageRegistry.Category.TEMPLATE, ProjectHeroExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_PROJECT_OVERVIEW, "ProjectOverview 工程概览模板", PageRegistry.Category.TEMPLATE, ProjectOverviewExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_PROJECT_RELEASE, "ProjectRelease 发布节奏模板", PageRegistry.Category.TEMPLATE, ProjectReleaseExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_PROJECT_SHOWCASE, "ProjectShowcase 工程项目展示首页模板", PageRegistry.Category.TEMPLATE, ProjectShowcaseExamplePage::new);
    }

    private static void registerNavigation(PageRegistry registry) {
        register(registry, "navigation.menu", "Menu 菜单", PageRegistry.Category.NAVIGATION, MenuExamplePage::new);
        register(registry, "navigation.tabs", "Tabs 标签页", PageRegistry.Category.NAVIGATION, TabsExamplePage::new);
        register(registry, "navigation.breadcrumb", "Breadcrumb 面包屑", PageRegistry.Category.NAVIGATION, BreadcrumbExamplePage::new);
        register(registry, "navigation.steps", "Steps 步骤条", PageRegistry.Category.NAVIGATION, StepsExamplePage::new);
        register(registry, "navigation.dropdown", "Dropdown 下拉菜单", PageRegistry.Category.NAVIGATION, DropdownExamplePage::new);
        register(registry, "navigation.pagination", "Pagination 分页", PageRegistry.Category.NAVIGATION, PaginationExamplePage::new);
        register(registry, "navigation.anchor", "Anchor 锚点", PageRegistry.Category.NAVIGATION, AnchorExamplePage::new);
        register(registry, "navigation.menubar", "MenuBar 菜单栏", PageRegistry.Category.NAVIGATION, MenuBarExamplePage::new);
        register(registry, "navigation.toolbar", "ToolBar 工具栏", PageRegistry.Category.NAVIGATION, ToolBarExamplePage::new);
        register(registry, "navigation.statusbar", "StatusBar 状态栏", PageRegistry.Category.NAVIGATION, StatusBarExamplePage::new);
        register(registry, "navigation.backtop", "BackTop 回到顶部", PageRegistry.Category.NAVIGATION, BackTopExamplePage::new);
    }

    private static void registerDataEntry(PageRegistry registry) {
        register(registry, "dataentry.form", "Form 表单", PageRegistry.Category.DATA_ENTRY, FormExamplePage::new);
        register(registry, "dataentry.input", "Input 输入框", PageRegistry.Category.DATA_ENTRY, InputExamplePage::new);
        register(registry, "dataentry.switch", "Switch 开关", PageRegistry.Category.DATA_ENTRY, SwitchExamplePage::new);
        register(registry, "dataentry.select", "ComboBox 下拉框", PageRegistry.Category.DATA_ENTRY, SelectExamplePage::new);
        register(registry, "dataentry.checkbox", "Checkbox 复选框", PageRegistry.Category.DATA_ENTRY, CheckboxExamplePage::new);
        register(registry, "dataentry.radio", "Radio 单选框", PageRegistry.Category.DATA_ENTRY, RadioExamplePage::new);
        register(registry, "dataentry.slider", "Slider 滑块", PageRegistry.Category.DATA_ENTRY, SliderExamplePage::new);
        register(registry, "dataentry.datepicker", "DatePicker 日期", PageRegistry.Category.DATA_ENTRY, DatePickerExamplePage::new);
        register(registry, "dataentry.inputnumber", "InputNumber 数字输入", PageRegistry.Category.DATA_ENTRY, InputNumberExamplePage::new);
        register(registry, "dataentry.transfer", "Transfer 穿梭框", PageRegistry.Category.DATA_ENTRY, TransferExamplePage::new);
        register(registry, "dataentry.upload", "Upload 上传", PageRegistry.Category.DATA_ENTRY, UploadExamplePage::new);
        register(registry, "dataentry.autocomplete", "AutoComplete 自动完成", PageRegistry.Category.DATA_ENTRY, AutoCompleteExamplePage::new);
        register(registry, "dataentry.cascader", "Cascader 级联选择", PageRegistry.Category.DATA_ENTRY, CascaderExamplePage::new);
        register(registry, "dataentry.treeselect", "TreeSelect 树选择", PageRegistry.Category.DATA_ENTRY, TreeSelectExamplePage::new);
        register(registry, "dataentry.colorpicker", "ColorPicker 颜色选择", PageRegistry.Category.DATA_ENTRY, ColorPickerExamplePage::new);
        register(registry, "dataentry.timepicker", "TimePicker 时间选择", PageRegistry.Category.DATA_ENTRY, TimePickerExamplePage::new);
        register(registry, "dataentry.rate", "Rate 评分", PageRegistry.Category.DATA_ENTRY, RateExamplePage::new);
        register(registry, "dataentry.textarea", "TextArea 多行输入", PageRegistry.Category.DATA_ENTRY, TextAreaExamplePage::new);
        register(registry, "dataentry.togglebutton", "ToggleButton 切换按钮", PageRegistry.Category.DATA_ENTRY, ToggleButtonExamplePage::new);
        register(registry, "dataentry.choicebox", "ChoiceBox 选择框", PageRegistry.Category.DATA_ENTRY, ChoiceBoxExamplePage::new);
        register(registry, "dataentry.mentions", "Mentions 提及", PageRegistry.Category.DATA_ENTRY, MentionsExamplePage::new);
    }

    private static void registerDataDisplay(PageRegistry registry) {
        register(registry, "datadisplay.table", "Table 表格", PageRegistry.Category.DATA_DISPLAY, TableExamplePage::new);
        register(registry, "datadisplay.tag", "Tag 标签", PageRegistry.Category.DATA_DISPLAY, TagExamplePage::new);
        register(registry, "datadisplay.card", "GroupBox 分组框", PageRegistry.Category.DATA_DISPLAY, GroupBoxExamplePage::new);
        register(registry, "datadisplay.avatar", "Avatar 头像 + Badge", PageRegistry.Category.DATA_DISPLAY, AvatarBadgeExamplePage::new);
        register(registry, "datadisplay.progress", "Progress 进度条", PageRegistry.Category.DATA_DISPLAY, ProgressExamplePage::new);
        register(registry, "datadisplay.collapse", "Collapse 折叠面板", PageRegistry.Category.DATA_DISPLAY, CollapseExamplePage::new);
        register(registry, "datadisplay.descriptions", "Descriptions 描述列表", PageRegistry.Category.DATA_DISPLAY, DescriptionsExamplePage::new);
        register(registry, "datadisplay.tree", "Tree 树形控件", PageRegistry.Category.DATA_DISPLAY, TreeExamplePage::new);
        register(registry, "datadisplay.list", "List 列表", PageRegistry.Category.DATA_DISPLAY, ListExamplePage::new);
        register(registry, "datadisplay.timeline", "Timeline 时间轴", PageRegistry.Category.DATA_DISPLAY, TimelineExamplePage::new);
        register(registry, "datadisplay.carousel", "Carousel 走马灯", PageRegistry.Category.DATA_DISPLAY, CarouselExamplePage::new);
        register(registry, "datadisplay.empty", "Empty 空状态", PageRegistry.Category.DATA_DISPLAY, EmptyExamplePage::new);
        register(registry, "datadisplay.statistic", "Statistic 统计数值", PageRegistry.Category.DATA_DISPLAY, StatisticExamplePage::new);
        register(registry, "datadisplay.qrcode", "QRCode 二维码", PageRegistry.Category.DATA_DISPLAY, QRCodeExamplePage::new);
        register(registry, "datadisplay.image", "Image 图片", PageRegistry.Category.DATA_DISPLAY, ImageExamplePage::new);
        register(registry, "datadisplay.calendar", "Calendar 日历", PageRegistry.Category.DATA_DISPLAY, CalendarExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_WATERMARK, "Watermark 水印", PageRegistry.Category.DATA_DISPLAY, WatermarkExamplePage::new);
        register(registry, "datadisplay.skeleton", "Skeleton 骨架屏", PageRegistry.Category.DATA_DISPLAY, SkeletonExamplePage::new);
        register(registry, "datadisplay.accordion", "Accordion 手风琴", PageRegistry.Category.DATA_DISPLAY, AccordionExamplePage::new);
        register(registry, "datadisplay.titledpane", "TitledPane 标题面板", PageRegistry.Category.DATA_DISPLAY, TitledPaneExamplePage::new);
        register(registry, "datadisplay.treetable", "TreeTable 树表格", PageRegistry.Category.DATA_DISPLAY, TreeTableExamplePage::new);
        register(registry, "datadisplay.listview", "ListView 列表视图", PageRegistry.Category.DATA_DISPLAY, ListViewExamplePage::new);
        register(registry, "datadisplay.canvas", "Canvas 画布", PageRegistry.Category.DATA_DISPLAY, CanvasExamplePage::new);
    }

    private static void registerFeedback(PageRegistry registry) {
        register(registry, "feedback.message", "Message 全局消息", PageRegistry.Category.FEEDBACK, MessageExamplePage::new);
        register(registry, "feedback.notification", "Notification 通知提醒", PageRegistry.Category.FEEDBACK, NotificationExamplePage::new);
        register(registry, "feedback.desktopnotification", "DesktopNotification 桌面通知", PageRegistry.Category.FEEDBACK, DesktopNotificationExamplePage::new);
        register(registry, UiExampleConstants.ROUTE_MODAL, "Modal 模态对话框", PageRegistry.Category.FEEDBACK, ModalExamplePage::new);
        register(registry, "feedback.drawer", "Drawer 抽屉", PageRegistry.Category.FEEDBACK, DrawerExamplePage::new);
        // AlertAnt 已并入 FormExamplePage 第 9 个 section 演示
        register(registry, "feedback.spin", "Spin 加载", PageRegistry.Category.FEEDBACK, SpinExamplePage::new);
        register(registry, "feedback.popconfirm", "Popconfirm 确认", PageRegistry.Category.FEEDBACK, PopconfirmExamplePage::new);
        register(registry, "feedback.result", "Result 结果页", PageRegistry.Category.FEEDBACK, ResultExamplePage::new);
        register(registry, "feedback.tooltip", "Tooltip 文字提示", PageRegistry.Category.FEEDBACK, TooltipExamplePage::new);
        register(registry, "feedback.contextmenu", "ContextMenu 右键菜单", PageRegistry.Category.FEEDBACK, ContextMenuExamplePage::new);
        register(registry, "feedback.floatbutton", "FloatButton 悬浮按钮", PageRegistry.Category.FEEDBACK, FloatButtonExamplePage::new);
        register(registry, "feedback.promptdialog", "PromptDialog 输入弹框", PageRegistry.Category.FEEDBACK, PromptDialogExamplePage::new);
        register(registry, "feedback.spinner", "Spinner 旋转加载", PageRegistry.Category.FEEDBACK, SpinnerExamplePage::new);
        register(registry, "feedback.popover", "Popover 气泡卡片", PageRegistry.Category.FEEDBACK, PopoverExamplePage::new);
    }

    private static void register(PageRegistry registry, String key, String title, PageRegistry.Category category, Supplier<Node> factory) {
        registry.register(key, title, category, factory);
    }
}
