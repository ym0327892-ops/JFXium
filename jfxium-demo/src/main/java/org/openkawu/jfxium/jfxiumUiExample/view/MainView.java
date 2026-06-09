package org.openkawu.jfxium.jfxiumUiExample.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;



import org.openkawu.jfxium.component.layout.ScrollContainerAnt;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.theme.ThemeColor;
import org.openkawu.jfxium.core.theme.ThemeDensity;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.jfxiumUiExample.pages.HomePage;
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
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.AlertExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.ContextMenuExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.DrawerExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.FloatButtonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.MessageExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.ModalExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.NotificationExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.feedback.PopconfirmExamplePage;
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
import org.openkawu.jfxium.jfxiumUiExample.pages.general.SegmentedExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.SelectableTextExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.SplitButtonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.SplitMenuButtonExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.TypographyExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.general.WatermarkExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.BarExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.FlexExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.layout.GridExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.AnchorExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.BackTopExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.BreadcrumbExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.DropdownExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.MenuBarExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.MenuExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.PaginationExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.StepsExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.TabsExamplePage;
import org.openkawu.jfxium.jfxiumUiExample.pages.navigation.ToolBarExamplePage;
import org.openkawu.jfxium.layout.AppShellAnt;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.ComboBoxAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.MenuAnt;
import org.openkawu.jfxium.component.composite.BarAnt;
import org.openkawu.jfxium.component.composite.AvatarAnt;

/**
 * 主窗口 —— admin 风格三段式：顶栏 + 左侧菜单 + 中间内容区。
 *
 * <h2>架构（README 「类型 2：独立窗口 + AppShell」）</h2>
 * <pre>
 *  ┌────────────────────────────────────────────────────────┐
 *  │  Header（左：品牌 / 右：用户菜单 + 主题切换）           │
 *  ├──────────────┬─────────────────────────────────────────┤
 *  │              │                                         │
 *  │  Sider       │   Content（路由 outlet，按 selectedKey  │
 *  │  Menu        │            渲染 PageRegistry 里的页）   │
 *  │              │                                         │
 *  └──────────────┴─────────────────────────────────────────┘
 * </pre>
 *
 * <h2>菜单分类（对齐 Ant Design 文档侧栏）</h2>
 * <p>使用 {@link PageRegistry.Category} 枚举把示例页归入 6 大类：
 * 通用 / 布局 / 导航 / 数据录入 / 数据展示 / 反馈。新增页面时，只需在
 * {@link #registerPages()} 里追加 register —— 菜单和路由会自动反映。</p>
 *
 * <h2>路由实现要点</h2>
 * <ul>
 *   <li>菜单只 build 一次（避免切页面时滚动条跳回顶部 —— SKILL #22）</li>
 *   <li>用 {@link MenuAnt.Controller#setSelectedKey(String)} 运行时切高亮，不重建菜单</li>
 *   <li>每次切页都 new 一个新 Node 调用 setCenter —— 旧 Node 直接被 GC，状态干净</li>
 *   <li>内容区外面包一层 ScrollPane，让长内容能滚动</li>
 * </ul>
 */
public class MainView {

    private final String currentUser;
    /** 登出回调 —— 由 App 处理（切回登录 Scene）。 */
    private final Runnable onLogout;

    /** 路由表。注册顺序决定菜单顺序。 */
    private final PageRegistry registry = new PageRegistry();

    /** 内容区切换的目标 BorderPane —— setCenter 即换页。 */
    private final BorderPane contentHost = new BorderPane();

    /** 当前 MenuAnt 的运行时控制器，用于 setSelectedKey 切高亮。 */
    private MenuAnt.Controller menuController;

    public MainView(String currentUser, Runnable onLogout) {
        this.currentUser = currentUser;
        this.onLogout = onLogout;
        registerPages();
    }

    /**
     * 注册所有示例页面 —— 按 Ant Design 6 大类组织。
     *
     * <p>当前阶段先把「通用」「反馈」两类充实（覆盖你点名的 Button / Message / Modal），
     * 其余 4 类先留空白结构，便于后续逐个补内容。</p>
     */
    private void registerPages() {
        // 顶层（无分类）
        registry.register("home", "首页", null, HomePage::new);

        // ============ 通用（General）============
        registry.register("general.button", "Button 按钮",
                PageRegistry.Category.GENERAL, ButtonExamplePage::new);
        registry.register("general.label", "Label 文本",
                PageRegistry.Category.GENERAL, LabelExamplePage::new);
        registry.register("general.codeblock", "CodeBlock 代码块",
                PageRegistry.Category.GENERAL, CodeBlockExamplePage::new);
        registry.register("general.typography", "Typography 排版",
                PageRegistry.Category.GENERAL, TypographyExamplePage::new);
        registry.register("general.icon", "Icon 图标",
                PageRegistry.Category.GENERAL, IconExamplePage::new);
        registry.register("general.segmented", "Segmented 分段器",
                PageRegistry.Category.GENERAL, SegmentedExamplePage::new);
        registry.register("general.watermark", "Watermark 水印",
                PageRegistry.Category.GENERAL, WatermarkExamplePage::new);
        registry.register("general.selectabletext", "SelectableText 可选文本",
                PageRegistry.Category.GENERAL, SelectableTextExamplePage::new);
        registry.register("general.menubutton", "MenuButton 菜单按钮",
                PageRegistry.Category.GENERAL, MenuButtonExamplePage::new);
        registry.register("general.splitbutton", "SplitButton 分裂按钮",
                PageRegistry.Category.GENERAL, SplitButtonExamplePage::new);
        registry.register("general.splitmenu", "SplitMenuButton 分裂菜单",
                PageRegistry.Category.GENERAL, SplitMenuButtonExamplePage::new);
        registry.register("general.hyperlink", "Hyperlink 超链接",
                PageRegistry.Category.GENERAL, HyperlinkExamplePage::new);

        // ============ 布局（Layout）============
        registry.register("layout.grid", "Grid 栅格",
                PageRegistry.Category.LAYOUT, GridExamplePage::new);
        registry.register("layout.flex", "Flex 弹性布局",
                PageRegistry.Category.LAYOUT, FlexExamplePage::new);
        registry.register("layout.bar", "Bar 横向栏",
                PageRegistry.Category.LAYOUT, BarExamplePage::new);

        // ============ 导航（Navigation）============
        registry.register("navigation.menu", "Menu 菜单",
                PageRegistry.Category.NAVIGATION, MenuExamplePage::new);
        registry.register("navigation.tabs", "Tabs 标签页",
                PageRegistry.Category.NAVIGATION, TabsExamplePage::new);
        registry.register("navigation.breadcrumb", "Breadcrumb 面包屑",
                PageRegistry.Category.NAVIGATION, BreadcrumbExamplePage::new);
        registry.register("navigation.steps", "Steps 步骤条",
                PageRegistry.Category.NAVIGATION, StepsExamplePage::new);
        registry.register("navigation.dropdown", "Dropdown 下拉菜单",
                PageRegistry.Category.NAVIGATION, DropdownExamplePage::new);
        registry.register("navigation.pagination", "Pagination 分页",
                PageRegistry.Category.NAVIGATION, PaginationExamplePage::new);
        registry.register("navigation.anchor", "Anchor 锚点",
                PageRegistry.Category.NAVIGATION, AnchorExamplePage::new);
        registry.register("navigation.menubar", "MenuBar 菜单栏",
                PageRegistry.Category.NAVIGATION, MenuBarExamplePage::new);
        registry.register("navigation.toolbar", "ToolBar 工具栏",
                PageRegistry.Category.NAVIGATION, ToolBarExamplePage::new);
        registry.register("navigation.backtop", "BackTop 回到顶部",
                PageRegistry.Category.NAVIGATION, BackTopExamplePage::new);

        // ============ 数据录入（Data Entry）============
        registry.register("dataentry.form", "Form 表单",
                PageRegistry.Category.DATA_ENTRY, FormExamplePage::new);
        registry.register("dataentry.input", "Input 输入框",
                PageRegistry.Category.DATA_ENTRY, InputExamplePage::new);
        registry.register("dataentry.switch", "Switch 开关",
                PageRegistry.Category.DATA_ENTRY, SwitchExamplePage::new);
        registry.register("dataentry.select", "ComboBox 下拉框",
                PageRegistry.Category.DATA_ENTRY, SelectExamplePage::new);
        registry.register("dataentry.checkbox", "Checkbox 复选框",
                PageRegistry.Category.DATA_ENTRY, CheckboxExamplePage::new);
        registry.register("dataentry.radio", "Radio 单选框",
                PageRegistry.Category.DATA_ENTRY, RadioExamplePage::new);
        registry.register("dataentry.slider", "Slider 滑块",
                PageRegistry.Category.DATA_ENTRY, SliderExamplePage::new);
        registry.register("dataentry.datepicker", "DatePicker 日期",
                PageRegistry.Category.DATA_ENTRY, DatePickerExamplePage::new);
        registry.register("dataentry.inputnumber", "InputNumber 数字输入",
                PageRegistry.Category.DATA_ENTRY, InputNumberExamplePage::new);
        registry.register("dataentry.transfer", "Transfer 穿梭框",
                PageRegistry.Category.DATA_ENTRY, TransferExamplePage::new);
        registry.register("dataentry.upload", "Upload 上传",
                PageRegistry.Category.DATA_ENTRY, UploadExamplePage::new);
        registry.register("dataentry.autocomplete", "AutoComplete 自动完成",
                PageRegistry.Category.DATA_ENTRY, AutoCompleteExamplePage::new);
        registry.register("dataentry.cascader", "Cascader 级联选择",
                PageRegistry.Category.DATA_ENTRY, CascaderExamplePage::new);
        registry.register("dataentry.treeselect", "TreeSelect 树选择",
                PageRegistry.Category.DATA_ENTRY, TreeSelectExamplePage::new);
        registry.register("dataentry.colorpicker", "ColorPicker 颜色选择",
                PageRegistry.Category.DATA_ENTRY, ColorPickerExamplePage::new);
        registry.register("dataentry.timepicker", "TimePicker 时间选择",
                PageRegistry.Category.DATA_ENTRY, TimePickerExamplePage::new);
        registry.register("dataentry.rate", "Rate 评分",
                PageRegistry.Category.DATA_ENTRY, RateExamplePage::new);
        registry.register("dataentry.textarea", "TextArea 多行输入",
                PageRegistry.Category.DATA_ENTRY, TextAreaExamplePage::new);
        registry.register("dataentry.togglebutton", "ToggleButton 切换按钮",
                PageRegistry.Category.DATA_ENTRY, ToggleButtonExamplePage::new);
        registry.register("dataentry.choicebox", "ChoiceBox 选择框",
                PageRegistry.Category.DATA_ENTRY, ChoiceBoxExamplePage::new);
        registry.register("dataentry.mentions", "Mentions 提及",
                PageRegistry.Category.DATA_ENTRY, MentionsExamplePage::new);

        // ============ 数据展示（Data Display）============
        registry.register("datadisplay.table", "Table 表格",
                PageRegistry.Category.DATA_DISPLAY, TableExamplePage::new);
        registry.register("datadisplay.tag", "Tag 标签",
                PageRegistry.Category.DATA_DISPLAY, TagExamplePage::new);
        registry.register("datadisplay.card", "GroupBox 分组框",
                PageRegistry.Category.DATA_DISPLAY, GroupBoxExamplePage::new);
        registry.register("datadisplay.avatar", "Avatar 头像 + Badge",
                PageRegistry.Category.DATA_DISPLAY, AvatarBadgeExamplePage::new);
        registry.register("datadisplay.progress", "Progress 进度条",
                PageRegistry.Category.DATA_DISPLAY, ProgressExamplePage::new);
        registry.register("datadisplay.collapse", "Collapse 折叠面板",
                PageRegistry.Category.DATA_DISPLAY, CollapseExamplePage::new);
        registry.register("datadisplay.descriptions", "Descriptions 描述列表",
                PageRegistry.Category.DATA_DISPLAY, DescriptionsExamplePage::new);
        registry.register("datadisplay.tree", "Tree 树形控件",
                PageRegistry.Category.DATA_DISPLAY, TreeExamplePage::new);
        registry.register("datadisplay.list", "List 列表",
                PageRegistry.Category.DATA_DISPLAY, ListExamplePage::new);
        registry.register("datadisplay.timeline", "Timeline 时间轴",
                PageRegistry.Category.DATA_DISPLAY, TimelineExamplePage::new);
        registry.register("datadisplay.carousel", "Carousel 走马灯",
                PageRegistry.Category.DATA_DISPLAY, CarouselExamplePage::new);
        registry.register("datadisplay.empty", "Empty 空状态",
                PageRegistry.Category.DATA_DISPLAY, EmptyExamplePage::new);
        registry.register("datadisplay.statistic", "Statistic 统计数值",
                PageRegistry.Category.DATA_DISPLAY, StatisticExamplePage::new);
        registry.register("datadisplay.qrcode", "QRCode 二维码",
                PageRegistry.Category.DATA_DISPLAY, QRCodeExamplePage::new);
        registry.register("datadisplay.image", "Image 图片",
                PageRegistry.Category.DATA_DISPLAY, ImageExamplePage::new);
        registry.register("datadisplay.calendar", "Calendar 日历",
                PageRegistry.Category.DATA_DISPLAY, CalendarExamplePage::new);
        registry.register("datadisplay.skeleton", "Skeleton 骨架屏",
                PageRegistry.Category.DATA_DISPLAY, SkeletonExamplePage::new);
        registry.register("datadisplay.popover", "Popover 气泡卡片",
                PageRegistry.Category.DATA_DISPLAY, PopoverExamplePage::new);
        registry.register("datadisplay.divider", "Divider 分割线",
                PageRegistry.Category.DATA_DISPLAY, DividerExamplePage::new);
        registry.register("datadisplay.separator", "Separator 分隔符",
                PageRegistry.Category.DATA_DISPLAY, SeparatorExamplePage::new);
        registry.register("datadisplay.accordion", "Accordion 手风琴",
                PageRegistry.Category.DATA_DISPLAY, AccordionExamplePage::new);
        registry.register("datadisplay.titledpane", "TitledPane 标题面板",
                PageRegistry.Category.DATA_DISPLAY, TitledPaneExamplePage::new);
        registry.register("datadisplay.treetable", "TreeTable 树表格",
                PageRegistry.Category.DATA_DISPLAY, TreeTableExamplePage::new);
        registry.register("datadisplay.listview", "ListView 列表视图",
                PageRegistry.Category.DATA_DISPLAY, ListViewExamplePage::new);
        registry.register("datadisplay.canvas", "Canvas 画布",
                PageRegistry.Category.DATA_DISPLAY, CanvasExamplePage::new);

        // ============ 反馈（Feedback）============
        registry.register("feedback.message",      "Message 全局消息",
                PageRegistry.Category.FEEDBACK, MessageExamplePage::new);
        registry.register("feedback.notification", "Notification 通知提醒",
                PageRegistry.Category.FEEDBACK, NotificationExamplePage::new);
        registry.register("feedback.modal",        "Modal 模态对话框",
                PageRegistry.Category.FEEDBACK, ModalExamplePage::new);
        registry.register("feedback.drawer",       "Drawer 抽屉",
                PageRegistry.Category.FEEDBACK, DrawerExamplePage::new);
        registry.register("feedback.alert",        "Alert 警告提示",
                PageRegistry.Category.FEEDBACK, AlertExamplePage::new);
        registry.register("feedback.spin",         "Spin 加载",
                PageRegistry.Category.FEEDBACK, SpinExamplePage::new);
        registry.register("feedback.popconfirm",   "Popconfirm 确认",
                PageRegistry.Category.FEEDBACK, PopconfirmExamplePage::new);
        registry.register("feedback.result",       "Result 结果页",
                PageRegistry.Category.FEEDBACK, ResultExamplePage::new);
        registry.register("feedback.tooltip",      "Tooltip 文字提示",
                PageRegistry.Category.FEEDBACK, TooltipExamplePage::new);
        registry.register("feedback.contextmenu",  "ContextMenu 右键菜单",
                PageRegistry.Category.FEEDBACK, ContextMenuExamplePage::new);
        registry.register("feedback.floatbutton",  "FloatButton 悬浮按钮",
                PageRegistry.Category.FEEDBACK, FloatButtonExamplePage::new);
        registry.register("feedback.promptdialog", "PromptDialog 输入弹框",
                PageRegistry.Category.FEEDBACK, PromptDialogExamplePage::new);
        registry.register("feedback.spinner",      "Spinner 旋转加载",
                PageRegistry.Category.FEEDBACK, SpinnerExamplePage::new);
    }

    /** 构建主页 root —— App 拿去 setScene。 */
    public BorderPane build() {
        navigate("home");

        ScrollPane contentScroll = ScrollContainerAnt.create()
                .content(contentHost)
                .fitToWidth(true)
                .build();
        contentScroll.getStyleClass().add(Background.LAYOUT.styleClass());

        return AppShellAnt.create()
                .header(buildHeader())
                .sider(buildSider(), 240)
                .content(contentScroll)
                .collapsible()
                .build();
    }

    // ============================================================
    // Header
    // ============================================================

    private HBox buildHeader() {
        Label brand = TypographyAnt.title("JFXium UI Example", 4).build();
        Node logo = AvatarAnt.create()
                .text("⚡").size(36).shape(AvatarAnt.Shape.SQUARE).build();
        HBox brandBox = new HBox(10, logo, brand);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        ThemeManager tm = ThemeManager.getInstance();

        // 主题风格选择（设计语言：Ant Design / MUI）
        ComboBox<ThemeManager.Family> styleSelect = ComboBoxAnt.<ThemeManager.Family>create()
                .items(ThemeManager.Family.values())
                .value(tm.getCurrentFamily())
                .size(ComboBoxAnt.Size.SMALL)
                .onChange(family -> { if (family != null) tm.setFamily(family); })
                .build();
        // 下拉显示中文名而非枚举名
        styleSelect.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(ThemeManager.Family f) { return f == null ? "" : f.getDisplayName(); }
            @Override public ThemeManager.Family fromString(String s) { return null; }
        });

        // 明暗选择（亮色 / 暗色）
        ComboBox<String> modeSelect = ComboBoxAnt.<String>create()
                .items("亮色", "暗色")
                .value(tm.isDark() ? "暗色" : "亮色")
                .size(ComboBoxAnt.Size.SMALL)
                .onChange(mode -> tm.setDark("暗色".equals(mode)))
                .build();

        // 密度切换（DEFAULT / COMPACT 正交于 family × dark）
        // ButtonAnt 链式调用 lambda 内要回调设的按钮引用，用 Button[] 数组占位供 lambda 捕获
        final Button[] densityBtn = {null};
        densityBtn[0] = ButtonAnt.create(densityLabel(tm.getDensity()))
                .size(ButtonAnt.Size.SMALL)
                .onClick(e -> {
                    ThemeDensity next = tm.getDensity() == ThemeDensity.DEFAULT
                            ? ThemeDensity.COMPACT
                            : ThemeDensity.DEFAULT;
                    tm.setDensity(next);
                    densityBtn[0].setText(densityLabel(next));
                })
                .build();

        // 主题色选择（11 个 Ant Design 预设色）
        ComboBox<ThemeColor.Preset> colorSelect = ComboBoxAnt.<ThemeColor.Preset>create()
                .items(ThemeColor.Preset.values())
                .value(ThemeColor.Preset.BLUE)
                .size(ComboBoxAnt.Size.SMALL)
                .onChange(preset -> { if (preset != null) tm.setPrimaryColor(preset); })
                .build();
        colorSelect.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(ThemeColor.Preset p) { return p == null ? "" : p.getDisplayName(); }
            @Override public ThemeColor.Preset fromString(String s) { return null; }
        });

        Label userLabel = TypographyAnt.text("👤 " + currentUser)
                .type(TypographyAnt.Type.SECONDARY).build();
        Button logout = ButtonAnt.create("退出")
                .type(ButtonAnt.Type.LINK).onClick(e -> doLogout()).build();

        HBox header = BarAnt.create()
                .left(brandBox)
                .right(styleSelect, modeSelect, densityBtn[0], colorSelect, userLabel, logout)
                .gap(12).build();
        // 背景色 + 底边框由 AppShellAnt 给 header 挂的 .app-shell-header styleClass 控制；
        // 这里只补 padding（结构性属性），不挂 Background.DEFAULT —— 避免覆盖 LESS 的容器色阶。
        header.setPadding(new Insets(12, 24, 12, 24));
        return header;
    }

    /** 密度切换按钮的 label 文案，反映当前生效的密度。 */
    private static String densityLabel(ThemeDensity d) {
        return d == ThemeDensity.COMPACT ? "密度: 紧凑" : "密度: 默认";
    }

    // ============================================================
    // Sider
    // ============================================================

    /**
     * 按 Category 分桶后构造菜单：
     * <ul>
     *   <li>无分类（home）→ 顶层 leaf item</li>
     *   <li>每个 Category → 一个 subMenu，里面是该类的所有页</li>
     *   <li>没有页面的 Category 不渲染 —— 避免空菜单组</li>
     * </ul>
     */
    private Pane buildSider() {
        MenuAnt.Builder builder = MenuAnt.create()
                .selectedKey("home")
                .onSelect(this::navigate);

        // 按 Category 分桶
        Map<PageRegistry.Category, List<PageRegistry.Entry>> grouped = new LinkedHashMap<>();
        // 预先按 Category 声明顺序占好桶（保证菜单顺序稳定，不依赖 registerPages 中的偶然顺序）
        for (PageRegistry.Category c : PageRegistry.Category.values()) {
            grouped.put(c, new java.util.ArrayList<>());
        }
        java.util.List<PageRegistry.Entry> topLevel = new java.util.ArrayList<>();
        for (PageRegistry.Entry e : registry.entries().values()) {
            if (e.category() == null) topLevel.add(e);
            else grouped.get(e.category()).add(e);
        }

        // 1. 顶层 leaf 项
        for (PageRegistry.Entry e : topLevel) {
            Node icon = "home".equals(e.key()) ? IconAnt.path(IconAnt.Path.HOME, 16) : null;
            builder.item(e.key(), e.title(), icon, () -> { /* onSelect 统一处理 */ });
        }

        // 2. 每个 Category 一个 subMenu
        for (PageRegistry.Category category : PageRegistry.Category.values()) {
            List<PageRegistry.Entry> bucket = grouped.get(category);
            if (bucket.isEmpty()) continue;

            MenuAnt.SubMenuBuilder sub = builder.subMenu(
                            "group." + category.name().toLowerCase(),
                            category.label(),
                            iconForCategory(category))
                    .defaultExpanded(true);
            for (PageRegistry.Entry e : bucket) {
                sub.item(e.key(), e.title(), () -> {});
            }
            builder.endSubMenu();
        }

        Pane menu = builder.build();
        menuController = builder.controller();

        // 侧栏可滚动 + 灰底
        ScrollPane menuScroll = new ScrollPane(menu);
        menuScroll.setFitToWidth(true);
        menuScroll.getStyleClass().add(Background.SUBTLE.styleClass());
        BorderPane wrapper = new BorderPane(menuScroll);
        wrapper.getStyleClass().add(Background.SUBTLE.styleClass());
        return wrapper;
    }

    /**
     * 为每个分类挑一个图标——纯视觉装饰，无业务语义。
     * 当前 IconAnt.Path 枚举有限，复用现有图标避免引入图标库。
     */
    private static Node iconForCategory(PageRegistry.Category c) {
        return switch (c) {
            case GENERAL      -> IconAnt.path(IconAnt.Path.SETTINGS,  16);
            case LAYOUT       -> IconAnt.path(IconAnt.Path.DASHBOARD, 16);
            case NAVIGATION   -> IconAnt.path(IconAnt.Path.HOME,      16);
            case DATA_ENTRY   -> IconAnt.path(IconAnt.Path.EDIT,      16);
            case DATA_DISPLAY -> IconAnt.path(IconAnt.Path.CHART,     16);
            case FEEDBACK     -> IconAnt.path(IconAnt.Path.BELL,      16);
        };
    }

    // ============================================================
    // 路由切换
    // ============================================================

    private void navigate(String key) {
        Node page = registry.build(key);
        if (page == null) return;
        contentHost.setCenter(page);
        if (menuController != null) {
            menuController.setSelectedKey(key);
        }
    }

    private void doLogout() {
        if (onLogout != null) {
            onLogout.run();
        }
    }
}
