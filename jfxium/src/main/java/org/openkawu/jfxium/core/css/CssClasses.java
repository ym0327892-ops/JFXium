package org.openkawu.jfxium.core.css;

/**
 * JFXium CSS Class Constants
 * Inspired by AtlantaFX
 */
public final class CssClasses {

    private CssClasses() {}

    /* ============================================
       通用背景层级（M19.35）— 容器组件用 Background 枚举挑层级，挂对应 styleClass
       ============================================ */
    public static final String BG_DEFAULT     = "jfx-bg-default";
    public static final String BG_SUBTLE      = "jfx-bg-subtle";
    public static final String BG_LAYOUT      = "jfx-bg-layout";
    public static final String BG_INSET       = "jfx-bg-inset";
    public static final String BG_TRANSPARENT = "jfx-bg-transparent";

    /* ============================================
       Button Classes
       ============================================ */

    /** Button variants */
    public static final String BUTTON_DEFAULT = "default";
    /** Accent / Primary 视觉变体（PRIMARY 与 ACCENT 共用此类，LESS 中 .button.accent 即可命中）。 */
    public static final String BUTTON_ACCENT = "accent";
    public static final String BUTTON_OUTLINED = "outlined";
    public static final String BUTTON_DASHED = "dashed";
    public static final String BUTTON_TEXT = "text";
    public static final String BUTTON_LINK = "link";
    /** Ghost 修饰类：透明背景 + 反色边框/文字，与 type 类组合使用（M19.28 改用 styleClass 取代 inline style）。 */
    public static final String BUTTON_GHOST = "ghost";

    /** Button sizes */
    public static final String SIZE_SMALL = "small";
    public static final String SIZE_LARGE = "large";

    /** Button shapes */
    public static final String SHAPE_ROUNDED = "rounded";
    public static final String SHAPE_SQUARE = "square";

    /* ============================================
       Text Field Classes
       ============================================ */

    /* Use the same SIZE_* constants */

    /* ============================================
       Card Classes
       ============================================ */

    public static final String CARD = "card";
    public static final String CARD_BORDERED = "bordered";
    public static final String CARD_HOVERABLE = "hoverable";
    public static final String CARD_SHADOW_SM = "shadow-sm";
    public static final String CARD_SHADOW_MD = "shadow-md";
    public static final String CARD_SHADOW_LG = "shadow-lg";
    public static final String CARD_TITLE = "card-title";
    public static final String CARD_CONTENT = "card-content";
    
    // M10 新增：Card 增强功能
    public static final String CARD_SMALL = "card-small";
    public static final String CARD_INNER = "card-inner";
    public static final String CARD_HEADER = "card-header";
    public static final String CARD_BODY = "card-body";
    public static final String CARD_COVER = "card-cover";
    public static final String CARD_ACTIONS = "card-actions";
    public static final String CARD_ACTION_ITEM = "card-action-item";
    public static final String CARD_TAB_BAR = "card-tab-bar";
    public static final String CARD_TAB_LIST = "card-tab-list";
    public static final String CARD_TAB_ITEM = "card-tab-item";
    public static final String CARD_TAB_ITEM_ACTIVE = "active";

    /* ============================================
       Page / Layout Classes
       ============================================ */

    public static final String SURFACE = "surface";
    public static final String SURFACE_HEADER = "surface-header";
    public static final String SURFACE_TITLE = "surface-title";
    public static final String SURFACE_CONTENT = "surface-content";

    /** FilterBarAnt 通用筛选+操作工具条（admin 列表页上栏标配） */
    public static final String FILTER_BAR = "filter-bar";

    public static final String APP_SHELL = "app-shell";
    public static final String APP_SHELL_HEADER = "app-shell-header";
    public static final String APP_SHELL_SIDER = "app-shell-sider";
    public static final String APP_SHELL_CONTENT = "app-shell-content";
    public static final String APP_SHELL_FOOTER = "app-shell-footer";

    public static final String SPLIT_PANE = "split-pane-ant";

    public static final String RESIZABLE_PANEL = "resizable-panel";
    public static final String RESIZABLE_PANEL_CONTENT = "resizable-panel-content";
    public static final String RESIZABLE_PANEL_HANDLE = "resizable-panel-handle";
    public static final String RESIZABLE_PANEL_HANDLE_HORIZONTAL = "horizontal";
    public static final String RESIZABLE_PANEL_HANDLE_VERTICAL = "vertical";
    public static final String RESIZABLE_PANEL_HANDLE_BOTH = "both";

    public static final String SCROLL_CONTAINER = "scroll-container";
    public static final String SCROLL_CONTAINER_VIEWPORT = "scroll-container-viewport";

    /* ============================================
       Layout Primitive Classes (FlexAnt / GridAnt / SpaceAnt / DividerAnt)
       这些是布局原语类，对标 Ant Design 的 Flex/Row/Col/Space/Divider
       ============================================ */

    /** FlexAnt 弹性布局容器 */
    public static final String FLEX = "flex";
    public static final String FLEX_HORIZONTAL = "flex-horizontal";
    public static final String FLEX_VERTICAL = "flex-vertical";
    public static final String FLEX_WRAP = "flex-wrap";

    /** GridAnt 24 栅格容器 */
    public static final String GRID = "grid";
    public static final String GRID_ROW = "grid-row";
    public static final String GRID_COL = "grid-col";

    /** SpaceAnt 间距容器 */
    public static final String SPACE = "space";
    public static final String SPACE_HORIZONTAL = "space-horizontal";
    public static final String SPACE_VERTICAL = "space-vertical";
    public static final String SPACE_SPLIT = "space-split";

    /** DividerAnt 分割线 */
    public static final String DIVIDER = "divider";
    public static final String DIVIDER_HORIZONTAL = "divider-horizontal";
    public static final String DIVIDER_VERTICAL = "divider-vertical";
    public static final String DIVIDER_TEXT = "divider-text";
    public static final String DIVIDER_LINE = "divider-line";

    /** FormAnt 表单 */
    public static final String FORM = "form";
    public static final String FORM_HORIZONTAL = "form-horizontal";
    public static final String FORM_VERTICAL = "form-vertical";
    public static final String FORM_INLINE = "form-inline";
    public static final String FORM_LABEL = "form-label";
    public static final String FORM_LABEL_REQUIRED = "form-label-required";
    public static final String FORM_ITEM_WRAPPER = "form-item-wrapper";
    public static final String FORM_HELP_TEXT = "form-help-text";
    public static final String FORM_HELP_ERROR = "form-help-error";
    public static final String FORM_HELP_WARNING = "form-help-warning";
    public static final String FORM_HELP_SUCCESS = "form-help-success";
    public static final String FORM_FOOTER = "form-footer";
    public static final String FORM_HEADER = "form-header";
    public static final String FORM_SECTION_TITLE = "form-section-title";

    /** TableAnt 表格附加常量（jfx-table 等已直接以字符串使用，此处补充） */
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
       SwitchAnt
       ============================================ */
    public static final String SWITCH = "jfx-switch";
    public static final String SWITCH_CONTAINER = "jfx-switch-container";
    public static final String SWITCH_TRACK = "jfx-switch-track";
    public static final String SWITCH_THUMB = "jfx-switch-thumb";
    public static final String SWITCH_SELECTED = "switch-selected";
    public static final String SWITCH_DISABLED = "switch-disabled";
    public static final String SWITCH_STATUS_LABEL = "jfx-switch-status-label";

    /* ============================================
       BadgeAnt
       ============================================ */
    public static final String BADGE = "jfx-badge";
    public static final String BADGE_INDICATOR = "jfx-badge-indicator";
    /** count 形态：圆角矩形 + 数字文本 */
    public static final String BADGE_COUNT = "badge-count";
    /** dot 形态：纯小圆点（默认 danger 色）*/
    public static final String BADGE_DOT = "badge-dot";
    /** status 形态：小圆点 + 状态色，配合 BADGE_STATUS_* 修饰类使用 */
    public static final String BADGE_STATUS = "badge-status";
    public static final String BADGE_STATUS_SUCCESS = "badge-status-success";
    public static final String BADGE_STATUS_WARNING = "badge-status-warning";
    public static final String BADGE_STATUS_ERROR = "badge-status-error";
    public static final String BADGE_STATUS_DEFAULT = "badge-status-default";

    /* ============================================
       AlertAnt
       ============================================ */
    public static final String ALERT = "jfx-alert";
    public static final String ALERT_SUCCESS = "alert-success";
    public static final String ALERT_INFO = "alert-info";
    public static final String ALERT_WARNING = "alert-warning";
    public static final String ALERT_ERROR = "alert-error";
    /** banner 形态：占满宽度、无圆角 */
    public static final String ALERT_BANNER = "alert-banner";
    public static final String ALERT_TITLE = "alert-title";
    public static final String ALERT_MESSAGE = "alert-message";
    public static final String ALERT_ICON = "alert-icon";
    public static final String ALERT_CLOSE_BTN = "alert-close-btn";

    /* ============================================
       ProgressAnt
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
       SliderAnt
       ============================================ */
    public static final String SLIDER = "jfx-slider";
    public static final String SLIDER_WRAPPER = "jfx-slider-wrapper";
    public static final String SLIDER_RANGE = "jfx-slider-range";
    public static final String SLIDER_TIP = "jfx-slider-tip";
    public static final String SLIDER_RANGE_LABEL = "jfx-slider-range-label";
    public static final String SLIDER_RANGE_SEPARATOR = "jfx-slider-range-separator";
    public static final String SLIDER_MARKS = "jfx-slider-marks";
    public static final String SLIDER_MARK_LABEL = "jfx-slider-mark-label";
    public static final String SLIDER_DISABLED = "slider-disabled";

    /* ============================================
       CodeBlockAnt
       ============================================ */
    public static final String CODEBLOCK = "jfx-codeblock";
    public static final String CODEBLOCK_HEADER = "jfx-codeblock-header";
    public static final String CODEBLOCK_TITLE = "jfx-codeblock-title";
    public static final String CODEBLOCK_LANG = "jfx-codeblock-lang";
    public static final String CODEBLOCK_COPY_BTN = "jfx-codeblock-copy-btn";
    public static final String CODEBLOCK_SCROLL = "jfx-codeblock-scroll";
    public static final String CODEBLOCK_CONTENT = "jfx-codeblock-content";

    /* ============================================
       Tier 1 简单组件：IconAnt / EmptyAnt / BackTopAnt / SpinAnt / TimePickerAnt
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
    public static final String SPIN_FULLSCREEN = "spin-fullscreen";
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
       Batch 1 Tier 2：DropdownAnt / AnchorAnt / AutoCompleteAnt /
                       ImageAnt / MentionsAnt / StatisticAnt
       ============================================ */
    public static final String DROPDOWN_TRIGGER = "jfx-dropdown-trigger";

    public static final String ANCHOR = "anchor";
    public static final String ANCHOR_HORIZONTAL = "anchor-horizontal";
    public static final String ANCHOR_VERTICAL = "anchor-vertical";
    public static final String ANCHOR_LINK = "anchor-link";
    public static final String ANCHOR_LINK_ACTIVE = "anchor-link-active";

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
    public static final String STATISTIC_SMALL = "statistic-small";
    public static final String STATISTIC_LARGE = "statistic-large";

    /* ============================================
       Batch 2 Tier 2/3：TypographyAnt / CollapseAnt / TreeSelectAnt /
                         SegmentedAnt / InputNumberAnt / CarouselAnt
       ============================================ */

    /** TypographyAnt */
    public static final String TYPOGRAPHY_TITLE = "typography-title";
    public static final String TYPOGRAPHY_PARAGRAPH = "typography-paragraph";
    public static final String TYPOGRAPHY_TEXT = "typography-text";
    public static final String TYPOGRAPHY_SECONDARY = "typography-secondary";
    public static final String TYPOGRAPHY_SUCCESS = "typography-success";
    public static final String TYPOGRAPHY_WARNING = "typography-warning";
    public static final String TYPOGRAPHY_DANGER = "typography-danger";
    public static final String TYPOGRAPHY_DISABLED = "typography-disabled";
    public static final String TYPOGRAPHY_ITALIC = "typography-italic";
    public static final String TYPOGRAPHY_UNDERLINE = "typography-underline";
    public static final String TYPOGRAPHY_DELETE = "typography-delete";
    public static final String TYPOGRAPHY_CODE = "typography-code";
    public static final String TYPOGRAPHY_MARK = "typography-mark";
    public static final String TYPOGRAPHY_COPYABLE = "typography-copyable";

    /** CollapseAnt */
    public static final String COLLAPSE = "collapse";
    public static final String COLLAPSE_PANEL = "collapse-panel";
    public static final String COLLAPSE_HEADER = "collapse-header";
    public static final String COLLAPSE_HEADER_LABEL = "collapse-header-label";
    public static final String COLLAPSE_ARROW = "collapse-arrow";
    public static final String COLLAPSE_CONTENT = "collapse-content";
    public static final String COLLAPSE_DIVIDER = "collapse-divider";
    public static final String COLLAPSE_DISABLED = "collapse-disabled";

    /** TreeSelectAnt */
    public static final String TREE_SELECT = "tree-select";
    public static final String TREE_SELECT_FIELD = "tree-select-field";
    public static final String TREE_SELECT_ROW = "tree-select-row";
    public static final String TREE_SELECT_ARROW = "tree-select-arrow";
    public static final String TREE_SELECT_LABEL = "tree-select-label";
    public static final String TREE_SELECT_DISABLED = "tree-select-disabled";
    public static final String TREE_SELECT_SELECTED = "tree-select-selected";

    /** SegmentedAnt */
    public static final String SEGMENTED = "segmented";
    public static final String SEGMENTED_DISABLED = "segmented-disabled";
    public static final String SEGMENTED_SMALL = "segmented-small";
    public static final String SEGMENTED_LARGE = "segmented-large";
    public static final String SEGMENTED_ITEM = "segmented-item";
    public static final String SEGMENTED_ITEM_SELECTED = "segmented-item-selected";
    public static final String SEGMENTED_ITEM_LABEL = "segmented-item-label";

    /** InputNumberAnt */
    public static final String INPUT_NUMBER = "input-number";
    public static final String INPUT_NUMBER_DISABLED = "input-number-disabled";
    public static final String INPUT_NUMBER_PREFIX = "input-number-prefix";
    public static final String INPUT_NUMBER_SUFFIX = "input-number-suffix";
    public static final String INPUT_NUMBER_FIELD = "input-number-field";
    public static final String INPUT_NUMBER_BTN = "input-number-btn";
    public static final String INPUT_NUMBER_ARROW = "input-number-arrow";

    /** CarouselAnt */
    public static final String CAROUSEL = "carousel";
    public static final String CAROUSEL_CONTENT = "carousel-content";
    public static final String CAROUSEL_ARROW_BTN = "carousel-arrow-btn";
    public static final String CAROUSEL_DOTS = "carousel-dots";
    public static final String CAROUSEL_DOT = "carousel-dot";
    public static final String CAROUSEL_DOT_ACTIVE = "carousel-dot-active";

    /* ============================================
       Batch 3 Tier 3：DrawerAnt / ModalAnt / CascaderAnt /
                       TimelineAnt / TransferAnt
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
    public static final String DRAWER = "drawer";
    public static final String MODAL = "modal";

    /** CascaderAnt */
    public static final String CASCADER = "cascader";
    public static final String CASCADER_FIELD = "cascader-field";
    public static final String CASCADER_COLUMN = "cascader-column";
    public static final String CASCADER_ITEM = "cascader-item";
    public static final String CASCADER_ITEM_DISABLED = "cascader-item-disabled";
    public static final String CASCADER_ITEM_LABEL = "cascader-item-label";
    public static final String CASCADER_ARROW = "cascader-arrow";
    public static final String CASCADER_DIVIDER = "cascader-divider";

    /** TimelineAnt */
    public static final String TIMELINE = "timeline";
    public static final String TIMELINE_ITEM = "timeline-item";
    public static final String TIMELINE_LABEL = "timeline-label";
    public static final String TIMELINE_CONTENT = "timeline-content";
    public static final String TIMELINE_LINE = "timeline-line";
    public static final String TIMELINE_DOT = "timeline-dot";
    public static final String TIMELINE_DOT_BLUE = "timeline-dot-blue";
    public static final String TIMELINE_DOT_RED = "timeline-dot-red";
    public static final String TIMELINE_DOT_GREEN = "timeline-dot-green";
    public static final String TIMELINE_DOT_GRAY = "timeline-dot-gray";
    public static final String TIMELINE_DOT_PENDING = "timeline-dot-pending";
    public static final String TIMELINE_PENDING_TEXT = "timeline-pending-text";

    /** TransferAnt */
    public static final String TRANSFER = "transfer";
    public static final String TRANSFER_LIST = "transfer-list";
    public static final String TRANSFER_LIST_HEADER = "transfer-list-header";
    public static final String TRANSFER_LIST_TITLE = "transfer-list-title";
    public static final String TRANSFER_LIST_COUNT = "transfer-list-count";
    public static final String TRANSFER_LIST_SEARCH_WRAPPER = "transfer-list-search-wrapper";
    public static final String TRANSFER_LIST_SEARCH = "transfer-list-search";
    public static final String TRANSFER_LIST_VIEW = "transfer-list-view";
    public static final String TRANSFER_ARROW_BTN = "transfer-arrow-btn";

    /* ============================================
       Batch 4 Tier 3/4：ListAnt / MenuAnt / UploadAnt /
                         StepsAnt / BreadcrumbAnt
       ============================================ */

    /** ListAnt */
    public static final String LIST = "list";
    public static final String LIST_BORDERED = "list-bordered";
    public static final String LIST_LOADING = "list-loading";
    public static final String LIST_HEADER = "list-header";
    public static final String LIST_FOOTER = "list-footer";
    public static final String LIST_ITEM = "list-item";
    public static final String LIST_ITEM_CLICKABLE = "list-item-clickable";
    public static final String LIST_ITEM_TITLE = "list-item-title";
    public static final String LIST_ITEM_DESCRIPTION = "list-item-description";
    public static final String LIST_DIVIDER = "list-divider";

    /** MenuAnt */
    public static final String MENU = "menu";
    public static final String MENU_ITEM = "menu-item";
    public static final String MENU_ITEM_LABEL = "menu-item-label";
    public static final String MENU_SUBMENU_HEADER = "menu-submenu-header";
    public static final String MENU_SUBMENU_BODY = "menu-submenu-body";
    public static final String MENU_SUBMENU_ARROW = "menu-submenu-arrow";
    public static final String MENU_GROUP_LABEL = "menu-group-label";
    public static final String MENU_DIVIDER = "menu-divider";

    /** MenuAnt M14 增强：模式 + 选中态 + 主题 + 折叠 */
    public static final String MENU_INLINE        = "menu-inline";
    public static final String MENU_HORIZONTAL    = "menu-horizontal";
    public static final String MENU_DARK          = "menu-dark";
    public static final String MENU_COLLAPSED     = "menu-collapsed";
    public static final String MENU_ITEM_SELECTED = "menu-item-selected";
    public static final String MENU_SUBMENU_ARROW_BOX = "menu-submenu-arrow-box";

    /** UploadAnt */
    public static final String UPLOAD = "upload";
    public static final String UPLOAD_DRAG = "upload-drag";
    public static final String UPLOAD_DRAG_ACTIVE = "upload-drag-active";
    public static final String UPLOAD_DRAG_ICON = "upload-drag-icon";
    public static final String UPLOAD_DRAG_TEXT = "upload-drag-text";
    public static final String UPLOAD_HINT_TEXT = "upload-hint-text";
    public static final String UPLOAD_LIST = "upload-list";
    public static final String UPLOAD_FILE_ITEM = "upload-file-item";
    public static final String UPLOAD_FILE_NAME = "upload-file-name";
    public static final String UPLOAD_FILE_ERROR = "upload-file-error";
    public static final String UPLOAD_REMOVE_BTN = "upload-remove-btn";

    /** StepsAnt */
    public static final String STEPS = "steps";
    public static final String STEPS_VERTICAL = "steps-vertical";
    public static final String STEPS_ITEM = "steps-item";
    public static final String STEPS_CIRCLE = "steps-circle";
    public static final String STEPS_NUMBER = "steps-number";
    public static final String STEPS_TITLE = "steps-title";
    public static final String STEPS_DESCRIPTION = "steps-description";
    public static final String STEPS_LINE = "steps-line";
    /** Steps 状态修饰类 */
    public static final String STEPS_STATE_FINISHED = "steps-finished";
    public static final String STEPS_STATE_CURRENT = "steps-current";
    public static final String STEPS_STATE_WAIT = "steps-wait";

    /** BreadcrumbAnt */
    public static final String BREADCRUMB = "breadcrumb";
    public static final String BREADCRUMB_ITEM = "breadcrumb-item";
    public static final String BREADCRUMB_LINK = "breadcrumb-link";
    public static final String BREADCRUMB_LAST = "breadcrumb-last";
    public static final String BREADCRUMB_SEPARATOR = "breadcrumb-separator";

    /* ============================================
       Batch 5 Tier 4：DescriptionsAnt / CalendarAnt
       ============================================ */

    /** DescriptionsAnt */
    public static final String DESCRIPTIONS = "descriptions";
    public static final String DESCRIPTIONS_VERTICAL = "descriptions-vertical";
    public static final String DESCRIPTIONS_GRID = "descriptions-grid";
    public static final String DESCRIPTIONS_TITLE = "descriptions-title";
    public static final String DESCRIPTIONS_LABEL = "descriptions-label";
    public static final String DESCRIPTIONS_CONTENT = "descriptions-content";
    public static final String DESCRIPTIONS_BORDERED = "descriptions-bordered";
    public static final String DESCRIPTIONS_SIZE_SMALL = "descriptions-small";
    public static final String DESCRIPTIONS_SIZE_MIDDLE = "descriptions-middle";
    public static final String DESCRIPTIONS_SIZE_LARGE = "descriptions-large";

    /** CalendarAnt */
    public static final String CALENDAR = "calendar";
    public static final String CALENDAR_HEADER = "calendar-header";
    public static final String CALENDAR_HEADER_LABEL = "calendar-header-label";
    public static final String CALENDAR_NAV_BTN = "calendar-nav-btn";
    public static final String CALENDAR_GRID = "calendar-grid";
    public static final String CALENDAR_DAY_HEADER = "calendar-day-header";
    public static final String CALENDAR_CELL = "calendar-cell";
    public static final String CALENDAR_CELL_TODAY = "calendar-cell-today";
    public static final String CALENDAR_CELL_SELECTED = "calendar-cell-selected";
    public static final String CALENDAR_CELL_OTHER_MONTH = "calendar-cell-other-month";
    public static final String CALENDAR_DAY_LABEL = "calendar-day-label";
    public static final String CALENDAR_YEAR_VIEW = "calendar-year-view";
    public static final String CALENDAR_MONTH_BTN = "calendar-month-btn";
    public static final String CALENDAR_MONTH_BTN_CURRENT = "calendar-month-btn-current";

    /* ============================================
       业务模板（template/）— M18 通用 CRUD 三段式骨架
       ============================================ */
    public static final String CRUD_TEMPLATE         = "crud-template";
    public static final String CRUD_TEMPLATE_TITLE   = "crud-template-title";
    public static final String CRUD_TEMPLATE_TOPBAR  = "crud-template-topbar";
    public static final String CRUD_TEMPLATE_BODY    = "crud-template-body";
    public static final String CRUD_TEMPLATE_BOTTOMBAR = "crud-template-bottombar";

    /* ============================================
       PageTemplate（M19.33）— 通用展示页骨架
       「大标题 + 描述 + 内容区」最简结构，覆盖 ShowcasePage / 文档页 / 设置页等场景。
       ============================================ */
    public static final String PAGE_TEMPLATE         = "page-template";
    public static final String PAGE_TEMPLATE_TITLE   = "page-template-title";
    public static final String PAGE_TEMPLATE_DESC    = "page-template-desc";
    public static final String PAGE_TEMPLATE_HEADER  = "page-template-header";
    public static final String PAGE_TEMPLATE_BODY    = "page-template-body";

    /* ============================================
       SplitBarAnt（M19）— 横向左/中/右三段式布局
       ============================================ */
    public static final String SPLIT_BAR        = "split-bar";
    public static final String SPLIT_BAR_SPACER = "split-bar-spacer";

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
       LoginTemplate（M19.16，M19.39 LESS 化）
       ============================================ */
    public static final String LOGIN_ROOT            = "login-template";
    public static final String LOGIN_BANNER          = "login-template-banner";
    public static final String LOGIN_BANNER_LOGO_BOX = "login-template-banner-logo";
    public static final String LOGIN_BANNER_BRAND    = "login-template-banner-brand";
    public static final String LOGIN_BANNER_TAGLINE  = "login-template-banner-tagline";
    public static final String LOGIN_BANNER_FEATURE_CHECK = "login-template-banner-feature-check";
    public static final String LOGIN_BANNER_FEATURE_TEXT  = "login-template-banner-feature-text";
    public static final String LOGIN_BANNER_COPYRIGHT     = "login-template-banner-copyright";
    public static final String LOGIN_FORM            = "login-template-form";
    public static final String LOGIN_FORM_TITLE      = "login-template-form-title";
    public static final String LOGIN_FORM_SUBTITLE   = "login-template-form-subtitle";
    public static final String LOGIN_FORM_ERROR      = "login-template-form-error";
    public static final String LOGIN_FORM_INPUT_ROW  = "login-template-input-row";
    public static final String LOGIN_FORM_INPUT_FIELD = "login-template-input-field";
    public static final String LOGIN_FORM_REMEMBER   = "login-template-remember";
    public static final String LOGIN_FORM_LINK_SMALL = "login-template-link-small";
    public static final String LOGIN_FORM_SUBMIT     = "login-template-submit";
    public static final String LOGIN_FORM_NO_ACCOUNT = "login-template-no-account";
}
