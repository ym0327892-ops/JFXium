package org.openkawu.jfxium.core.css;

/**
 * JFXium 样式类名常量。
 *
 * <p>所有组件的 styleClass 名称集中定义在此处，与 LESS（{@code components/_index.less}）中的选择器一一对应。
 * Java 端通过 {@code node.getStyleClass().add(JfxStyles.XXX)} 挂载，LESS 端通过同名选择器控制视觉。</p>
 *
 * <h2>命名规范</h2>
 * <ul>
 *   <li><b>强制 {@code jfx-} 前缀</b>：所有新组件必须使用 {@code jfx-} 前缀，避免与 JavaFX modena 内置选择器冲突</li>
 *   <li><b>常量名 = CSS 类名的大写下划线形式</b>：{@code jfx-my-component} → {@code MY_COMPONENT}</li>
 *   <li><b>修饰类不加前缀</b>：状态修饰类（{@code small}、{@code active}、{@code selected}）不加 jfx- 前缀，
 *       因为它们总是伴随父选择器使用（{@code .jfx-button.small}）</li>
 * </ul>
 *
 * <h2>三步接线（新组件开发必经流程）</h2>
 * <ol>
 *   <li>在此处添加 {@code public static final String} 常量</li>
 *   <li>在 {@code components/_xxx.less} 中编写同名选择器的样式</li>
 *   <li>在 {@code components/_index.less} 中 {@code @import} 注册</li>
 * </ol>
 */
public final class JfxStyles {

    private JfxStyles() {}

    /* ============================================
       PanelHeader（base/）— 面板头部基础组件
       ============================================ */
    public static final String PANEL_HEADER = "jfx-panel-header";
    public static final String PANEL_TITLE = "jfx-panel-title";
    public static final String PANEL_CLOSE_BTN = "jfx-panel-close-btn";

    /* ============================================
       通用背景层级（M19.35）— 容器组件用 Background 枚举挑层级，挂对应 styleClass
       ============================================ */
    public static final String BG_DEFAULT     = "jfx-bg-default";
    public static final String BG_SUBTLE      = "jfx-bg-subtle";
    public static final String BG_LAYOUT      = "jfx-bg-layout";
    public static final String BG_INSET       = "jfx-bg-inset";
    public static final String BG_TRANSPARENT = "jfx-bg-transparent";

    /* ============================================
       Button 按钮
       ============================================ */

    /** 按钮变体 */
    public static final String BUTTON_DEFAULT = "default";
    /** Accent / Primary 视觉变体（PRIMARY 与 ACCENT 共用此类，LESS 中 .button.accent 即可命中）。 */
    public static final String BUTTON_ACCENT = "accent";
    public static final String BUTTON_OUTLINED = "outlined";
    public static final String BUTTON_DASHED = "dashed";
    public static final String BUTTON_TEXT = "text";
    public static final String BUTTON_LINK = "link";
    /** Ghost 修饰类：透明背景 + 反色边框/文字，与 type 类组合使用（M19.28 改用 styleClass 取代 inline style）。 */
    public static final String BUTTON_GHOST = "ghost";

    /** 按钮尺寸 */
    public static final String SIZE_SMALL = "small";
    public static final String SIZE_LARGE = "large";

    /** 按钮形状 */
    public static final String SHAPE_ROUNDED = "rounded";
    public static final String SHAPE_SQUARE = "square";

    /* ============================================
       GroupBox 分组框
       ============================================ */

    public static final String GROUP_BOX = "jfx-group-box";
    public static final String GROUP_BOX_BORDERED = "bordered";
    public static final String GROUP_BOX_HOVERABLE = "hoverable";
    public static final String GROUP_BOX_TITLE = "jfx-group-box-title";
    public static final String GROUP_BOX_CONTENT = "jfx-group-box-content";
    public static final String GROUP_BOX_SMALL = "jfx-group-box-small";
    public static final String GROUP_BOX_INNER = "jfx-group-box-inner";
    public static final String GROUP_BOX_HEADER = "jfx-group-box-header";
    public static final String GROUP_BOX_HEADER_BG = "jfx-group-box-header-bg";
    public static final String GROUP_BOX_HEADER_BORDER = "jfx-group-box-header-border";
    public static final String GROUP_BOX_BODY = "jfx-group-box-body";
    public static final String GROUP_BOX_ACTIONS = "jfx-group-box-actions";
    public static final String GROUP_BOX_ACTION_ITEM = "jfx-group-box-action-item";
    public static final String GROUP_BOX_TAB_BAR = "jfx-group-box-tab-bar";
    public static final String GROUP_BOX_TAB_LIST = "jfx-group-box-tab-list";
    public static final String GROUP_BOX_TAB_ITEM = "jfx-group-box-tab-item";
    public static final String GROUP_BOX_TAB_ITEM_ACTIVE = "active";

    /* ============================================
       Page / Layout 页面布局
       ============================================ */

    public static final String SURFACE = "jfx-surface";
    public static final String SURFACE_HEADER = "jfx-surface-header";
    public static final String SURFACE_TITLE = "jfx-surface-title";
    public static final String SURFACE_CONTENT = "jfx-surface-content";

    /** FilterBarAnt 通用筛选+操作工具条（admin 列表页上栏标配） */
    public static final String FILTER_BAR = "jfx-filter-bar";

    public static final String APP_SHELL = "jfx-app-shell";
    public static final String APP_SHELL_HEADER = "jfx-app-shell-header";
    public static final String APP_SHELL_SIDER = "jfx-app-shell-sider";
    public static final String APP_SHELL_CONTENT = "jfx-app-shell-content";
    public static final String APP_SHELL_FOOTER = "jfx-app-shell-footer";

    public static final String SPLIT_PANE = "jfx-split-pane-ant";

    public static final String RESIZABLE_PANEL = "jfx-resizable-panel";
    public static final String RESIZABLE_PANEL_CONTENT = "jfx-resizable-panel-content";
    public static final String RESIZABLE_PANEL_HANDLE = "jfx-resizable-panel-handle";
    public static final String RESIZABLE_PANEL_HANDLE_HORIZONTAL = "horizontal";
    public static final String RESIZABLE_PANEL_HANDLE_VERTICAL = "vertical";
    public static final String RESIZABLE_PANEL_HANDLE_BOTH = "both";

    public static final String SCROLL_CONTAINER = "jfx-scroll-container";
    public static final String SCROLL_CONTAINER_VIEWPORT = "jfx-scroll-container-viewport";

    /* ============================================
       布局原语（FlexAnt / GridAnt / SpaceAnt / DividerAnt）
       对标 Ant Design 的 Flex/Row/Col/Space/Divider
       ============================================ */

    /** FlexAnt 弹性布局容器 */
    public static final String FLEX = "jfx-flex";
    public static final String FLEX_HORIZONTAL = "jfx-flex-horizontal";
    public static final String FLEX_VERTICAL = "jfx-flex-vertical";
    public static final String FLEX_WRAP = "jfx-flex-wrap";

    /** GridAnt 24 栅格容器 */
    public static final String GRID = "jfx-grid";
    public static final String GRID_ROW = "jfx-grid-row";
    public static final String GRID_COL = "jfx-grid-col";

    /** SpaceAnt 间距容器 */
    public static final String SPACE = "jfx-space";
    public static final String SPACE_HORIZONTAL = "jfx-space-horizontal";
    public static final String SPACE_VERTICAL = "jfx-space-vertical";
    public static final String SPACE_SPLIT = "jfx-space-split";

    /** DividerAnt 分割线 */
    public static final String DIVIDER = "jfx-divider";
    public static final String DIVIDER_HORIZONTAL = "jfx-divider-horizontal";
    public static final String DIVIDER_VERTICAL = "jfx-divider-vertical";
    public static final String DIVIDER_TEXT = "jfx-divider-text";
    public static final String DIVIDER_LINE = "jfx-divider-line";

    /** FormAnt 表单 */
    public static final String FORM = "jfx-form";
    public static final String FORM_HORIZONTAL = "jfx-form-horizontal";
    public static final String FORM_VERTICAL = "jfx-form-vertical";
    public static final String FORM_INLINE = "jfx-form-inline";
    public static final String FORM_LABEL = "jfx-form-label";
    public static final String FORM_LABEL_REQUIRED = "jfx-form-label-required";
    public static final String FORM_ITEM_WRAPPER = "jfx-form-item-wrapper";
    public static final String FORM_HELP_TEXT = "jfx-form-help-text";
    public static final String FORM_HELP_ERROR = "jfx-form-help-error";
    public static final String FORM_HELP_WARNING = "jfx-form-help-warning";
    public static final String FORM_HELP_SUCCESS = "jfx-form-help-success";
    public static final String FORM_FOOTER = "jfx-form-footer";
    public static final String FORM_HEADER = "jfx-form-header";
    public static final String FORM_SECTION_TITLE = "jfx-form-section-title";

    /** TableAnt 表格 */
    public static final String TABLE = "jfx-table";
    public static final String TABLE_STRIPED = "jfx-table-striped";
    public static final String TABLE_BORDERED = "jfx-table-bordered";
    public static final String TABLE_COMPACT = "jfx-table-compact";

    /** TableAnt 内容区分割线模式（M11.x 增强） */
    public static final String TABLE_BORDER_NONE = "jfx-table-border-none";
    public static final String TABLE_BORDER_H    = "jfx-table-border-h";
    public static final String TABLE_BORDER_V    = "jfx-table-border-v";
    public static final String TABLE_BORDER_BOTH = "jfx-table-border-both";

    /** TableAnt 隐藏表头（showHeader(false)） */
    public static final String TABLE_NO_HEADER = "jfx-table-no-header";

    /** TableAnt 尺寸三态（M11.2 对齐 Ant Design size：SMALL/MIDDLE/LARGE） */
    public static final String TABLE_SIZE_SMALL  = "jfx-table-small";
    public static final String TABLE_SIZE_MIDDLE = "jfx-table-middle";
    public static final String TABLE_SIZE_LARGE  = "jfx-table-large";

    /* ============================================
       SwitchAnt 开关
       ============================================ */
    public static final String SWITCH = "jfx-switch";
    public static final String SWITCH_CONTAINER = "jfx-switch-container";
    public static final String SWITCH_TRACK = "jfx-switch-track";
    public static final String SWITCH_THUMB = "jfx-switch-thumb";
    public static final String SWITCH_SELECTED = "switch-selected";
    public static final String SWITCH_DISABLED = "switch-disabled";
    public static final String SWITCH_STATUS_LABEL = "jfx-switch-status-label";

    /* ============================================
       BadgeAnt 徽标
       ============================================ */
    public static final String BADGE = "jfx-badge";
    public static final String BADGE_INDICATOR = "jfx-badge-indicator";
    /** count 形态：圆角矩形 + 数字文本 */
    public static final String BADGE_COUNT = "jfx-badge-count";
    /** dot 形态：纯小圆点（默认 danger 色）*/
    public static final String BADGE_DOT = "jfx-badge-dot";
    /** status 形态：小圆点 + 状态色，配合 BADGE_STATUS_* 修饰类使用 */
    public static final String BADGE_STATUS = "jfx-badge-status";
    public static final String BADGE_STATUS_SUCCESS = "jfx-badge-status-success";
    public static final String BADGE_STATUS_WARNING = "jfx-badge-status-warning";
    public static final String BADGE_STATUS_ERROR = "jfx-badge-status-error";
    public static final String BADGE_STATUS_DEFAULT = "jfx-badge-status-default";

    /* ============================================
       AlertAnt 警告提示
       ============================================ */
    public static final String ALERT = "jfx-alert";
    public static final String ALERT_SUCCESS = "jfx-alert-success";
    public static final String ALERT_INFO = "jfx-alert-info";
    public static final String ALERT_WARNING = "jfx-alert-warning";
    public static final String ALERT_ERROR = "jfx-alert-error";
    /** banner 形态：占满宽度、无圆角 */
    public static final String ALERT_BANNER = "jfx-alert-banner";
    public static final String ALERT_TITLE = "jfx-alert-title";
    public static final String ALERT_MESSAGE = "jfx-alert-message";
    public static final String ALERT_ICON = "jfx-alert-icon";
    public static final String ALERT_CLOSE_BTN = "jfx-alert-close-btn";

    /* ============================================
       ProgressAnt 进度条
       ============================================ */
    public static final String PROGRESS_BAR = "jfx-progress-bar";
    public static final String PROGRESS_CIRCLE = "jfx-progress-circle";
    /** ProgressAnt 旁边的百分比文字 */
    public static final String PROGRESS_INFO = "jfx-progress-info";
    /** 状态修饰类，配合 progress-bar / progress-indicator JavaFX 原生选择器使用 */
    public static final String PROGRESS_SUCCESS = "success";
    public static final String PROGRESS_WARNING = "warning";
    public static final String PROGRESS_ERROR = "error";

    /* ============================================
       SliderAnt 滑动输入条
       ============================================ */
    public static final String SLIDER = "jfx-slider";
    public static final String SLIDER_WRAPPER = "jfx-slider-wrapper";
    public static final String SLIDER_RANGE = "jfx-slider-range";
    public static final String SLIDER_TIP = "jfx-slider-tip";
    public static final String SLIDER_RANGE_LABEL = "jfx-slider-range-label";
    public static final String SLIDER_RANGE_SEPARATOR = "jfx-slider-range-separator";
    public static final String SLIDER_MARKS = "jfx-slider-marks";
    public static final String SLIDER_MARK_LABEL = "jfx-slider-mark-label";
    public static final String SLIDER_DISABLED = "jfx-slider-disabled";

    /* ============================================
       CodeBlockAnt 代码块
       ============================================ */
    public static final String CODEBLOCK = "jfx-codeblock";
    public static final String CODEBLOCK_HEADER = "jfx-codeblock-header";
    public static final String CODEBLOCK_TITLE = "jfx-codeblock-title";
    public static final String CODEBLOCK_LANG = "jfx-codeblock-lang";
    public static final String CODEBLOCK_COPY_BTN = "jfx-codeblock-copy-btn";
    public static final String CODEBLOCK_SCROLL = "jfx-codeblock-scroll";
    public static final String CODEBLOCK_CONTENT = "jfx-codeblock-content";

    /* ============================================
       IconAnt / EmptyAnt / BackTopAnt / SpinAnt / TimePickerAnt
       ============================================ */

    /** IconAnt 内置极简符号图标 */
    public static final String ICON = "jfx-icon";

    /** IconAnt SVG path 业务图标（区别于 Symbol 模式，颜色走 -fx-background-color） */
    public static final String ICON_PATH = "jfx-icon-path";

    /** EmptyAnt 空状态 */
    public static final String EMPTY = "jfx-empty";
    public static final String EMPTY_ICON = "jfx-empty-icon";
    public static final String EMPTY_DESCRIPTION = "jfx-empty-description";

    /** BackTopAnt 回到顶部 */
    public static final String BACK_TOP = "jfx-back-top";
    public static final String BACK_TOP_ARROW = "jfx-back-top-arrow";

    /** SpinAnt 加载中 */
    public static final String SPIN = "jfx-spin";
    public static final String SPIN_FULLSCREEN = "jfx-spin-fullscreen";
    public static final String SPIN_TIP = "jfx-spin-tip";
    public static final String SPIN_INDICATOR_SPINNER = "jfx-spin-indicator-spinner";
    public static final String SPIN_INDICATOR_DOT = "jfx-spin-indicator-dot";
    public static final String SPIN_INDICATOR_BAR = "jfx-spin-indicator-bar";

    /** TimePickerAnt 时间选择器 */
    public static final String TIME_PICKER = "jfx-time-picker";
    public static final String TIME_PICKER_SEPARATOR = "jfx-time-picker-separator";
    public static final String TIME_PICKER_SPINNER = "jfx-time-picker-spinner";
    public static final String TIME_PICKER_EDITOR = "jfx-time-picker-editor";

    /* ============================================
       通用 Popup 菜单（DropdownAnt / AutoCompleteAnt / MentionsAnt 共用）
       浮层背景 + 圆角 + 边框 + 阴影 + 菜单项 hover/禁用
       ============================================ */
    public static final String POPUP_MENU = "jfx-popup-menu";
    public static final String POPUP_MENU_ITEM = "jfx-popup-menu-item";
    public static final String POPUP_MENU_ITEM_DISABLED = "jfx-popup-menu-item-disabled";
    public static final String POPUP_MENU_DIVIDER = "jfx-popup-menu-divider";

    /* ============================================
       DropdownAnt / AnchorAnt / AutoCompleteAnt / ImageAnt / MentionsAnt / StatisticAnt
       ============================================ */
    public static final String DROPDOWN_TRIGGER = "jfx-dropdown-trigger";

    public static final String ANCHOR = "jfx-anchor";
    public static final String ANCHOR_HORIZONTAL = "jfx-anchor-horizontal";
    public static final String ANCHOR_VERTICAL = "jfx-anchor-vertical";
    public static final String ANCHOR_LINK = "jfx-anchor-link";
    public static final String ANCHOR_LINK_ACTIVE = "jfx-anchor-link-active";

    public static final String AUTO_COMPLETE = "jfx-auto-complete";
    public static final String AUTO_COMPLETE_FIELD = "jfx-auto-complete-field";

    public static final String IMAGE = "jfx-image";
    public static final String IMAGE_FALLBACK = "jfx-image-fallback";

    public static final String WATERMARK = "jfx-watermark";
    public static final String WATERMARK_LAYER = "jfx-watermark-layer";
    public static final String WATERMARK_TEXT = "jfx-watermark-text";
    public static final String WATERMARK_TEXT_GROUP = "jfx-watermark-text-group";
    public static final String WATERMARK_IMAGE = "jfx-watermark-image";

    public static final String MENTIONS = "jfx-mentions";
    public static final String MENTIONS_AREA = "jfx-mentions-area";

    public static final String STATISTIC = "jfx-statistic";
    public static final String STATISTIC_TITLE = "jfx-statistic-title";
    public static final String STATISTIC_VALUE = "jfx-statistic-value";
    public static final String STATISTIC_PREFIX = "jfx-statistic-prefix";
    public static final String STATISTIC_SUFFIX = "jfx-statistic-suffix";
    /** Statistic 尺寸修饰类 */
    public static final String STATISTIC_SMALL = "jfx-statistic-small";
    public static final String STATISTIC_LARGE = "jfx-statistic-large";

    /* ============================================
       TypographyAnt / CollapseAnt / TreeSelectAnt / SegmentedAnt / InputNumberAnt / CarouselAnt
       ============================================ */

    /** TypographyAnt 排版 */
    public static final String TYPOGRAPHY_TITLE = "jfx-typography-title";
    public static final String TYPOGRAPHY_PARAGRAPH = "jfx-typography-paragraph";
    public static final String TYPOGRAPHY_TEXT = "jfx-typography-text";
    public static final String TYPOGRAPHY_SECONDARY = "jfx-typography-secondary";
    public static final String TYPOGRAPHY_SUCCESS = "jfx-typography-success";
    public static final String TYPOGRAPHY_WARNING = "jfx-typography-warning";
    public static final String TYPOGRAPHY_DANGER = "jfx-typography-danger";
    public static final String TYPOGRAPHY_DISABLED = "jfx-typography-disabled";
    public static final String TYPOGRAPHY_ITALIC = "jfx-typography-italic";
    public static final String TYPOGRAPHY_UNDERLINE = "jfx-typography-underline";
    public static final String TYPOGRAPHY_DELETE = "jfx-typography-delete";
    public static final String TYPOGRAPHY_CODE = "jfx-typography-code";
    public static final String TYPOGRAPHY_MARK = "jfx-typography-mark";
    public static final String TYPOGRAPHY_COPYABLE = "jfx-typography-copyable";

    /** CollapseAnt 折叠面板 */
    public static final String COLLAPSE = "jfx-collapse";
    public static final String COLLAPSE_PANEL = "jfx-collapse-panel";
    public static final String COLLAPSE_HEADER = "jfx-collapse-header";
    public static final String COLLAPSE_HEADER_LABEL = "jfx-collapse-header-label";
    public static final String COLLAPSE_ARROW = "jfx-collapse-arrow";
    public static final String COLLAPSE_CONTENT = "jfx-collapse-content";
    public static final String COLLAPSE_DIVIDER = "jfx-collapse-divider";
    public static final String COLLAPSE_DISABLED = "jfx-collapse-disabled";

    /** TreeSelectAnt 树选择 */
    public static final String TREE_SELECT = "jfx-tree-select";
    public static final String TREE_SELECT_FIELD = "jfx-tree-select-field";
    public static final String TREE_SELECT_ROW = "jfx-tree-select-row";
    public static final String TREE_SELECT_ARROW = "jfx-tree-select-arrow";
    public static final String TREE_SELECT_LABEL = "jfx-tree-select-label";
    public static final String TREE_SELECT_DISABLED = "jfx-tree-select-disabled";
    public static final String TREE_SELECT_SELECTED = "jfx-tree-select-selected";

    /** SegmentedAnt 分段控件 */
    public static final String SEGMENTED = "jfx-segmented";
    public static final String SEGMENTED_DISABLED = "jfx-segmented-disabled";
    public static final String SEGMENTED_SMALL = "jfx-segmented-small";
    public static final String SEGMENTED_LARGE = "jfx-segmented-large";
    public static final String SEGMENTED_ITEM = "jfx-segmented-item";
    public static final String SEGMENTED_ITEM_SELECTED = "jfx-segmented-item-selected";
    public static final String SEGMENTED_ITEM_LABEL = "jfx-segmented-item-label";

    /** InputNumberAnt 数字输入框 */
    public static final String INPUT_NUMBER = "jfx-input-number";
    public static final String INPUT_NUMBER_DISABLED = "jfx-input-number-disabled";
    public static final String INPUT_NUMBER_PREFIX = "jfx-input-number-prefix";
    public static final String INPUT_NUMBER_SUFFIX = "jfx-input-number-suffix";
    public static final String INPUT_NUMBER_FIELD = "jfx-input-number-field";
    public static final String INPUT_NUMBER_BTN = "jfx-input-number-btn";
    public static final String INPUT_NUMBER_ARROW = "jfx-input-number-arrow";

    /** CarouselAnt 走马灯 */
    public static final String CAROUSEL = "jfx-carousel";
    public static final String CAROUSEL_CONTENT = "jfx-carousel-content";
    public static final String CAROUSEL_ARROW_BTN = "jfx-carousel-arrow-btn";
    public static final String CAROUSEL_DOTS = "jfx-carousel-dots";
    public static final String CAROUSEL_DOT = "jfx-carousel-dot";
    public static final String CAROUSEL_DOT_ACTIVE = "jfx-carousel-dot-active";

    /* ============================================
       DrawerAnt / ModalAnt / CascaderAnt / TimelineAnt / TransferAnt
       ============================================ */

    /** 通用 overlay 遮罩 + 面板：DrawerAnt / ModalAnt 复用 */
    public static final String OVERLAY_MASK = "jfx-overlay-mask";
    public static final String OVERLAY_PANEL = "jfx-overlay-panel";
    public static final String OVERLAY_PANEL_ROUNDED = "jfx-overlay-panel-rounded";
    public static final String OVERLAY_HEADER = "jfx-overlay-header";
    public static final String OVERLAY_TITLE = "jfx-overlay-title";
    public static final String OVERLAY_CLOSE_BTN = "jfx-overlay-close-btn";
    public static final String OVERLAY_BODY = "jfx-overlay-body";
    public static final String OVERLAY_FOOTER = "jfx-overlay-footer";

    /** DrawerAnt / ModalAnt 自身命名（用于差异化扩展） */
    public static final String DRAWER = "jfx-drawer";
    public static final String MODAL = "jfx-modal";

    /** CascaderAnt 级联选择 */
    public static final String CASCADER = "jfx-cascader";
    public static final String CASCADER_FIELD = "jfx-cascader-field";
    public static final String CASCADER_COLUMN = "jfx-cascader-column";
    public static final String CASCADER_ITEM = "jfx-cascader-item";
    public static final String CASCADER_ITEM_DISABLED = "jfx-cascader-item-disabled";
    public static final String CASCADER_ITEM_LABEL = "jfx-cascader-item-label";
    public static final String CASCADER_ARROW = "jfx-cascader-arrow";
    public static final String CASCADER_DIVIDER = "jfx-cascader-divider";

    /** TimelineAnt 时间轴 */
    public static final String TIMELINE = "jfx-timeline";
    public static final String TIMELINE_ITEM = "jfx-timeline-item";
    public static final String TIMELINE_LABEL = "jfx-timeline-label";
    public static final String TIMELINE_CONTENT = "jfx-timeline-content";
    public static final String TIMELINE_LINE = "jfx-timeline-line";
    public static final String TIMELINE_DOT = "jfx-timeline-dot";
    public static final String TIMELINE_DOT_BLUE = "jfx-timeline-dot-blue";
    public static final String TIMELINE_DOT_RED = "jfx-timeline-dot-red";
    public static final String TIMELINE_DOT_GREEN = "jfx-timeline-dot-green";
    public static final String TIMELINE_DOT_GRAY = "jfx-timeline-dot-gray";
    public static final String TIMELINE_DOT_PENDING = "jfx-timeline-dot-pending";
    public static final String TIMELINE_PENDING_TEXT = "jfx-timeline-pending-text";

    /** TransferAnt 穿梭框 */
    public static final String TRANSFER = "jfx-transfer";
    public static final String TRANSFER_LIST = "jfx-transfer-list";
    public static final String TRANSFER_LIST_HEADER = "jfx-transfer-list-header";
    public static final String TRANSFER_LIST_TITLE = "jfx-transfer-list-title";
    public static final String TRANSFER_LIST_COUNT = "jfx-transfer-list-count";
    public static final String TRANSFER_LIST_SEARCH_WRAPPER = "jfx-transfer-list-search-wrapper";
    public static final String TRANSFER_LIST_SEARCH = "jfx-transfer-list-search";
    public static final String TRANSFER_LIST_VIEW = "jfx-transfer-list-view";
    public static final String TRANSFER_ARROW_BTN = "jfx-transfer-arrow-btn";

    /* ============================================
       ListAnt / MenuAnt / UploadAnt / StepsAnt / BreadcrumbAnt
       ============================================ */

    /** ListAnt 列表 */
    public static final String LIST = "jfx-list";
    public static final String LIST_BORDERED = "jfx-list-bordered";
    public static final String LIST_LOADING = "jfx-list-loading";
    public static final String LIST_HEADER = "jfx-list-header";
    public static final String LIST_FOOTER = "jfx-list-footer";
    public static final String LIST_ITEM = "jfx-list-item";
    public static final String LIST_ITEM_CLICKABLE = "jfx-list-item-clickable";
    public static final String LIST_ITEM_TITLE = "jfx-list-item-title";
    public static final String LIST_ITEM_DESCRIPTION = "jfx-list-item-description";
    public static final String LIST_DIVIDER = "jfx-list-divider";

    /** MenuAnt 菜单 */
    public static final String MENU = "jfx-menu";
    public static final String MENU_ITEM = "jfx-menu-item";
    public static final String MENU_ITEM_LABEL = "jfx-menu-item-label";
    public static final String MENU_SUBMENU_HEADER = "jfx-menu-submenu-header";
    public static final String MENU_SUBMENU_BODY = "jfx-menu-submenu-body";
    public static final String MENU_SUBMENU_ARROW = "jfx-menu-submenu-arrow";
    public static final String MENU_GROUP_LABEL = "jfx-menu-group-label";
    public static final String MENU_GROUP = "jfx-menu-group";
    public static final String MENU_DIVIDER = "jfx-menu-divider";

    /** MenuAnt M14 增强：模式 + 选中态 + 主题 + 折叠 */
    public static final String MENU_INLINE        = "jfx-menu-inline";
    public static final String MENU_HORIZONTAL    = "jfx-menu-horizontal";
    public static final String MENU_DARK          = "jfx-menu-dark";
    public static final String MENU_COLLAPSED     = "jfx-menu-collapsed";
    public static final String MENU_ITEM_SELECTED = "jfx-menu-item-selected";
    public static final String MENU_SUBMENU_ARROW_BOX = "jfx-menu-submenu-arrow-box";

    /** UploadAnt 上传 */
    public static final String UPLOAD = "jfx-upload";
    public static final String UPLOAD_DRAG = "jfx-upload-drag";
    public static final String UPLOAD_DRAG_ACTIVE = "jfx-upload-drag-active";
    public static final String UPLOAD_DRAG_ICON = "jfx-upload-drag-icon";
    public static final String UPLOAD_DRAG_TEXT = "jfx-upload-drag-text";
    public static final String UPLOAD_HINT_TEXT = "jfx-upload-hint-text";
    public static final String UPLOAD_LIST = "jfx-upload-list";
    public static final String UPLOAD_FILE_ITEM = "jfx-upload-file-item";
    public static final String UPLOAD_FILE_NAME = "jfx-upload-file-name";
    public static final String UPLOAD_FILE_ERROR = "jfx-upload-file-error";
    public static final String UPLOAD_REMOVE_BTN = "jfx-upload-remove-btn";

    /** StepsAnt 步骤条 */
    public static final String STEPS = "jfx-steps";
    public static final String STEPS_VERTICAL = "jfx-steps-vertical";
    public static final String STEPS_ITEM = "jfx-steps-item";
    public static final String STEPS_CIRCLE = "jfx-steps-circle";
    public static final String STEPS_NUMBER = "jfx-steps-number";
    public static final String STEPS_TITLE = "jfx-steps-title";
    public static final String STEPS_DESCRIPTION = "jfx-steps-description";
    public static final String STEPS_LINE = "jfx-steps-line";
    /** Steps 状态修饰类 */
    public static final String STEPS_STATE_FINISHED = "jfx-steps-finished";
    public static final String STEPS_STATE_CURRENT = "jfx-steps-current";
    public static final String STEPS_STATE_WAIT = "jfx-steps-wait";

    /** BreadcrumbAnt 面包屑 */
    public static final String BREADCRUMB = "jfx-breadcrumb";
    public static final String BREADCRUMB_ITEM = "jfx-breadcrumb-item";
    public static final String BREADCRUMB_LINK = "jfx-breadcrumb-link";
    public static final String BREADCRUMB_LAST = "jfx-breadcrumb-last";
    public static final String BREADCRUMB_SEPARATOR = "jfx-breadcrumb-separator";

    /* ============================================
       DescriptionsAnt / CalendarAnt
       ============================================ */

    /** DescriptionsAnt 描述列表 */
    public static final String DESCRIPTIONS = "jfx-descriptions";
    public static final String DESCRIPTIONS_VERTICAL = "jfx-descriptions-vertical";
    public static final String DESCRIPTIONS_GRID = "jfx-descriptions-grid";
    public static final String DESCRIPTIONS_TITLE = "jfx-descriptions-title";
    public static final String DESCRIPTIONS_LABEL = "jfx-descriptions-label";
    public static final String DESCRIPTIONS_CONTENT = "jfx-descriptions-content";
    public static final String DESCRIPTIONS_BORDERED = "jfx-descriptions-bordered";
    public static final String DESCRIPTIONS_SIZE_SMALL = "jfx-descriptions-small";
    public static final String DESCRIPTIONS_SIZE_MIDDLE = "jfx-descriptions-middle";
    public static final String DESCRIPTIONS_SIZE_LARGE = "jfx-descriptions-large";

    /** CalendarAnt 日历 */
    public static final String CALENDAR = "jfx-calendar";
    public static final String CALENDAR_HEADER = "jfx-calendar-header";
    public static final String CALENDAR_HEADER_LABEL = "jfx-calendar-header-label";
    public static final String CALENDAR_NAV_BTN = "jfx-calendar-nav-btn";
    public static final String CALENDAR_GRID = "jfx-calendar-grid";
    public static final String CALENDAR_DAY_HEADER = "jfx-calendar-day-header";
    public static final String CALENDAR_CELL = "jfx-calendar-cell";
    public static final String CALENDAR_CELL_TODAY = "jfx-calendar-cell-today";
    public static final String CALENDAR_CELL_SELECTED = "jfx-calendar-cell-selected";
    public static final String CALENDAR_CELL_OTHER_MONTH = "jfx-calendar-cell-other-month";
    public static final String CALENDAR_DAY_LABEL = "jfx-calendar-day-label";
    public static final String CALENDAR_YEAR_VIEW = "jfx-calendar-year-view";
    public static final String CALENDAR_MONTH_BTN = "jfx-calendar-month-btn";
    public static final String CALENDAR_MONTH_BTN_CURRENT = "jfx-calendar-month-btn-current";

    /* ============================================
       业务模板（template/）— M18 通用 CRUD 三段式骨架
       ============================================ */
    public static final String CRUD_TEMPLATE         = "jfx-crud-template";
    public static final String CRUD_TEMPLATE_TITLE   = "jfx-crud-template-title";
    public static final String CRUD_TEMPLATE_TOPBAR  = "jfx-crud-template-topbar";
    public static final String CRUD_TEMPLATE_BODY    = "jfx-crud-template-body";
    public static final String CRUD_TEMPLATE_BOTTOMBAR = "jfx-crud-template-bottombar";

    /* ============================================
       PageTemplate（M19.33）— 通用展示页骨架
       ============================================ */
    public static final String PAGE_TEMPLATE         = "jfx-page-template";
    public static final String PAGE_TEMPLATE_TITLE   = "jfx-page-template-title";
    public static final String PAGE_TEMPLATE_DESC    = "jfx-page-template-desc";
    public static final String PAGE_TEMPLATE_HEADER  = "jfx-page-template-header";
    public static final String PAGE_TEMPLATE_BODY    = "jfx-page-template-body";

    /* ============================================
       BarAnt（M19）— 横向左/中/右三段式布局
       ============================================ */
    public static final String SPLIT_BAR        = "jfx-split-bar";
    public static final String SPLIT_BAR_SPACER = "jfx-split-bar-spacer";

    /* ============================================
       MenuBarAnt（PC 软件刚需）— 系统菜单栏
       ============================================ */
    public static final String MENU_BAR = "jfx-menu-bar";
    public static final String MENU_BAR_MENU = "jfx-menu-bar-menu";
    public static final String MENU_BAR_ITEM = "jfx-menu-bar-item";
    public static final String MENU_BAR_SUBMENU = "jfx-menu-bar-submenu";
    public static final String MENU_BAR_DIVIDER = "jfx-menu-bar-divider";

    /* ============================================
       ToolBarAnt（PC 软件刚需）— 可定制工具栏
       ============================================ */
    public static final String TOOL_BAR = "jfx-tool-bar";
    public static final String TOOL_BAR_ITEM = "jfx-tool-bar-item";
    public static final String TOOL_BAR_OVERFLOW = "jfx-tool-bar-overflow";

    /* ============================================
       StatusBarAnt（PC 软件刚需）— 底部状态栏
       ============================================ */
    public static final String STATUS_BAR = "jfx-status-bar";
    public static final String STATUS_BAR_LEFT = "jfx-status-bar-left";
    public static final String STATUS_BAR_CENTER = "jfx-status-bar-center";
    public static final String STATUS_BAR_RIGHT = "jfx-status-bar-right";

    /* ============================================
       ContextMenuAnt（PC 软件刚需）— 右键菜单
       ============================================ */
    public static final String CONTEXT_MENU = "jfx-context-menu";
    public static final String CONTEXT_MENU_ITEM = "jfx-context-menu-item";
    public static final String CONTEXT_MENU_ITEM_DISABLED = "jfx-context-menu-item-disabled";
    public static final String CONTEXT_MENU_DIVIDER = "jfx-context-menu-divider";

    /* ============================================
       HyperlinkAnt — 超链接
       ============================================ */
    public static final String HYPERLINK = "jfx-hyperlink";

    /* ============================================
       TreeTableAnt — 树形表格
       ============================================ */
    public static final String TREE_TABLE = "jfx-tree-table";
    public static final String TREE_TABLE_ROW = "jfx-tree-table-row";
    public static final String TREE_TABLE_CELL = "jfx-tree-table-cell";
    public static final String TREE_TABLE_HEADER = "jfx-tree-table-header";

    /* ============================================
       InputAnt 密码模式（M19.55）
       ============================================ */
    public static final String INPUT_PASSWORD = "jfx-input-password";
    public static final String INPUT_PASSWORD_EYE = "jfx-input-password-eye";
    public static final String INPUT_PASSWORD_MASKED = "jfx-input-password-masked";
    public static final String INPUT_PASSWORD_VISIBLE = "jfx-input-password-visible";

    /* ============================================
       ColorPickerAnt — 颜色选择器
       ============================================ */
    public static final String COLOR_PICKER = "jfx-color-picker";

    /* ============================================
       CanvasAnt — 自绘图形容器
       ============================================ */
    public static final String CANVAS = "jfx-canvas";

    /* ============================================
       PromptDialogAnt — 快速输入弹框
       ============================================ */
    public static final String PROMPT_DIALOG = "jfx-prompt-dialog";
    public static final String PROMPT_DIALOG_MESSAGE = "jfx-prompt-dialog-message";
    public static final String PROMPT_DIALOG_INPUT = "jfx-prompt-dialog-input";
    public static final String PROMPT_DIALOG_FOOTER = "jfx-prompt-dialog-footer";

    /* ============================================
       TilePaneAnt — 平铺布局
       ============================================ */
    public static final String TILE_PANE = "jfx-tile-pane";

    /* ============================================
       AnchorPaneAnt — 绝对定位布局
       ============================================ */
    public static final String ANCHOR_PANE = "jfx-anchor-pane";

    /* ============================================
       SelectableTextAnt（M19.7）— 只读可选可复制文本
       ============================================ */
    public static final String SELECTABLE_TEXT           = "jfx-selectable-text";
    public static final String SELECTABLE_TEXT_BORDERED  = "jfx-selectable-text-bordered";
    public static final String SELECTABLE_TEXT_FOCUS_HALO = "jfx-selectable-text-focus-halo";
    public static final String SELECTABLE_TEXT_SECONDARY = "jfx-selectable-text-secondary";
    public static final String SELECTABLE_TEXT_SUCCESS   = "jfx-selectable-text-success";
    public static final String SELECTABLE_TEXT_WARNING   = "jfx-selectable-text-warning";
    public static final String SELECTABLE_TEXT_ERROR     = "jfx-selectable-text-error";

    /* ============================================
       LoginTemplate（M19.16，M19.39 LESS 化）— 登录页模板
       ============================================ */
    public static final String LOGIN_ROOT            = "jfx-login-template";
    public static final String LOGIN_BANNER          = "jfx-login-template-banner";
    public static final String LOGIN_BANNER_LOGO_BOX = "jfx-login-template-banner-logo";
    public static final String LOGIN_BANNER_BRAND    = "jfx-login-template-banner-brand";
    public static final String LOGIN_BANNER_TAGLINE  = "jfx-login-template-banner-tagline";
    public static final String LOGIN_BANNER_FEATURE_CHECK = "jfx-login-template-banner-feature-check";
    public static final String LOGIN_BANNER_FEATURE_TEXT  = "jfx-login-template-banner-feature-text";
    public static final String LOGIN_BANNER_COPYRIGHT     = "jfx-login-template-banner-copyright";
    public static final String LOGIN_FORM            = "jfx-login-template-form";
    public static final String LOGIN_FORM_TITLE      = "jfx-login-template-form-title";
    public static final String LOGIN_FORM_SUBTITLE   = "jfx-login-template-form-subtitle";
    public static final String LOGIN_FORM_ERROR      = "jfx-login-template-form-error";
    public static final String LOGIN_FORM_INPUT_ROW  = "jfx-login-template-input-row";
    public static final String LOGIN_FORM_INPUT_FIELD = "jfx-login-template-input-field";
    public static final String LOGIN_FORM_REMEMBER   = "jfx-login-template-remember";
    public static final String LOGIN_FORM_LINK_SMALL = "jfx-login-template-link-small";
    public static final String LOGIN_FORM_SUBMIT     = "jfx-login-template-submit";
    public static final String LOGIN_FORM_NO_ACCOUNT = "jfx-login-template-no-account";

    /* ============================================
       TabsAnt — 标签页
       ============================================ */
    public static final String TABS_BAR         = "jfx-tabs-bar";
    public static final String TABS_BAR_CARD    = "jfx-tabs-bar-card";
    public static final String TABS_LABEL       = "jfx-tabs-label";
    public static final String TABS_LABEL_LINE  = "jfx-tabs-label-line";
    public static final String TABS_LABEL_CARD  = "jfx-tabs-label-card";
    public static final String TABS_ACTIVE      = "jfx-tabs-active";
    public static final String TABS_DISABLED    = "jfx-tabs-disabled";
    public static final String TABS_LABEL_LARGE = "jfx-tabs-large";
    public static final String TABS_LABEL_SMALL = "jfx-tabs-small";
    public static final String TABS_INDICATOR_PANE = "jfx-tabs-indicator-pane";
    public static final String TABS_INDICATOR_BAR = "jfx-tabs-indicator-bar";

    /* ============================================
       PanelFooter — 面板底部
       ============================================ */
    public static final String PANEL_FOOTER          = "jfx-panel-footer";
    public static final String PANEL_FOOTER_BORDERED = "jfx-panel-footer-bordered";

    /* ============================================
       QRCodeAnt — 二维码
       ============================================ */
    public static final String QR_CODE           = "jfx-qr-code";
    public static final String QR_CODE_BORDERED  = "jfx-qr-code-bordered";

    /* ============================================
       RateAnt — 评分
       ============================================ */
    public static final String RATE = "jfx-rate";
    public static final String RATE_STAR     = "jfx-rate-star";
    public static final String RATE_ACTIVE   = "jfx-rate-active";
    public static final String RATE_INACTIVE = "jfx-rate-inactive";

    /* ============================================
       FloatButtonAnt — 浮动按钮
       ============================================ */
    public static final String FLOAT_BUTTON = "jfx-float-button";
    public static final String FLOAT_BUTTON_PRIMARY  = "jfx-float-button-primary";
    public static final String FLOAT_BUTTON_DEFAULT  = "jfx-float-button-default";

    /* ============================================
       SkeletonAnt — 骨架屏
       ============================================ */
    public static final String SKELETON = "jfx-skeleton";
    public static final String SKELETON_RECT = "jfx-skeleton-rect";

    /* ============================================
       AvatarAnt — 头像
       ============================================ */
    public static final String AVATAR = "jfx-avatar";
    public static final String AVATAR_BG_DEFAULT = "jfx-avatar-bg-default";
    public static final String AVATAR_FG_DEFAULT = "jfx-avatar-fg-default";

    /* ============================================
       共享图标颜色 — 消息/通知/结果页通用
       ============================================ */
    public static final String ICON_SUCCESS = "jfx-icon-success";
    public static final String ICON_DANGER  = "jfx-icon-danger";
    public static final String ICON_WARNING = "jfx-icon-warning";
    public static final String ICON_INFO    = "jfx-icon-info";
    public static final String ICON_MUTED   = "jfx-icon-muted";

    /* ============================================
       PopoverPanel / PopoverAnt — 气泡卡片
       ============================================ */
    public static final String POPOVER_PANEL      = "jfx-popover-panel";
    public static final String POPOVER_TITLE_BOX  = "jfx-popover-title-box";
    public static final String POPOVER_TITLE_LABEL = "jfx-popover-title-label";

    /* ============================================
       Popconfirm / Message / Notification / Result 基础卡片
       ============================================ */
    public static final String POPCONFIRM_PANEL = "jfx-popconfirm-panel";
    public static final String POPCONFIRM_ICON  = "jfx-popconfirm-icon";
    public static final String POPCONFIRM_TITLE = "jfx-popconfirm-title";
    public static final String POPCONFIRM_DESC  = "jfx-popconfirm-desc";
    public static final String MESSAGE_CARD         = "jfx-message-card";
    public static final String MESSAGE_CARD_CONTENT = "jfx-message-card-content";
    public static final String NOTIFICATION_CARD       = "jfx-notification-card";
    public static final String NOTIFICATION_CARD_TITLE = "jfx-notification-card-title";
    public static final String NOTIFICATION_CARD_DESC  = "jfx-notification-card-desc";
    public static final String RESULT_TITLE    = "jfx-result-title";
    public static final String RESULT_SUBTITLE = "jfx-result-subtitle";

    /* ============================================
       模板 / 工具类组件
       ============================================ */
    public static final String FILTER_BAR_LABEL = "jfx-filter-bar-label";
    public static final String DASHBOARD_ROOT         = "jfx-dashboard-root";
    public static final String DASHBOARD_STAT_ICON_BOX = "jfx-dashboard-stat-icon-box";
    public static final String DASHBOARD_STAT_TITLE   = "jfx-dashboard-stat-title";
    public static final String DASHBOARD_STAT_TREND_UP   = "jfx-dashboard-stat-trend-up";
    public static final String DASHBOARD_STAT_TREND_DOWN = "jfx-dashboard-stat-trend-down";
    public static final String DASHBOARD_STAT_TREND_HINT = "jfx-dashboard-stat-trend-hint";
    public static final String DASHBOARD_WELCOME    = "jfx-dashboard-welcome";
    public static final String DASHBOARD_STAT_VALUE = "jfx-dashboard-stat-value";
    public static final String BAR_BORDER_BOTTOM = "jfx-bar-border-bottom";
    public static final String BAR_BORDER_TOP    = "jfx-bar-border-top";

    /* ============================================
       通用四向边框 — 任意组件可用
       ============================================ */
    public static final String BORDER_TOP    = "jfx-border-top";
    public static final String BORDER_BOTTOM = "jfx-border-bottom";
    public static final String BORDER_LEFT   = "jfx-border-left";
    public static final String BORDER_RIGHT  = "jfx-border-right";

    public static final String FOCUS_VISIBLE      = "jfx-focus-visible";
    public static final String CODEBLOCK_HIGHLIGHT = "jfx-codeblock-highlight";

    /* ============================================
       CodeEditor（VS Code 风格演示用）— 配套 TextAreaAnt
       区别于 CodeBlockAnt：CodeBlockAnt 是「只读 display」，
       CodeEditor 是「可编辑编辑器」——用于演示页/业务 demo。
       ============================================ */

    /** 可编辑代码区（透明底 + 等宽字体，融入 IDE 编辑器背景）。 */
    public static final String CODE_EDITOR = "jfx-code-editor";

    /** 终端区（inset 底 + 等宽字体，主题切换自动反色）。 */
    public static final String CODE_EDITOR_TERMINAL = "jfx-code-terminal";
}
