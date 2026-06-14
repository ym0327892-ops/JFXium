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
 * <h2>裸名 vs {@code jfx-} 前缀 —— P4 术语统一边界表</h2>
 * <p>本文件 990 行常量中，约 543 个带 {@code jfx-} 前缀（根类 / 容器 / 子结构），约 30 个裸名（修饰类）。</p>
 * <table border="1" cellpadding="4">
 *   <tr><th>类别</th><th>裸名（修饰类）</th><th>jfx- 前缀（根类/子结构）</th><th>统一目标</th></tr>
 *   <tr><td>按钮变体</td><td>{@code default / accent / outlined / dashed / text / link / ghost}</td><td>—</td><td>✅ 裸名（与 LESS 选择器同步）</td></tr>
 *   <tr><td>尺寸</td><td>{@code small / large}</td><td>{@code jfx-table-small / jfx-table-large / jfx-tag-small / jfx-avatar-small / jfx-steps-small / jfx-statistic-small / jfx-segmented-small / jfx-badge-small / jfx-selectable-text-bordered / jfx-table-compact / jfx-radius-sm/md/lg/none}</td><td>⚠️ 不一致：尺寸 4 套不同前缀（{@code small} 裸名 / {@code jfx-table-small} / {@code jfx-tag-small} / {@code jfx-radius-sm}）</td></tr>
 *   <tr><td>形状</td><td>{@code rounded / square / shape-rounded / shape-square}</td><td>{@code jfx-tag-rounded / jfx-tag-square / jfx-avatar-square / jfx-image-rounded / jfx-image-circle}</td><td>⚠️ 不一致：{@code SHAPE_*} 用裸名，{@code TAG_*} 用 jfx- 前缀</td></tr>
 *   <tr><td>边框</td><td>{@code bordered / borderless} (大多错位)</td><td>{@code jfx-table-bordered / jfx-list-bordered / jfx-descriptions-bordered / jfx-crud-template-bordered / jfx-qr-code-bordered / jfx-panel-footer-bordered / jfx-selectable-text-bordered / jfx-tag-borderless}</td><td>⚠️ 不一致：{@code GROUP_BOX_BORDERED} / {@code SURFACE_BORDERED} 用裸名，{@code TABLE_BORDERED} / {@code LIST_BORDERED} 等用 jfx- 前缀</td></tr>
 *   <tr><td>状态色</td><td>{@code success / warning / error / info / processing / default} (裸名,部分)</td><td>{@code jfx-alert-success/info/warning/error / jfx-tag-success/processing/error/warning/default / jfx-badge-status-success/warning/error/default / jfx-result-status-success/info/warning/error/404/403/500}</td><td>⚠️ 不一致：{@code PROGRESS_SUCCESS/WARNING/ERROR} 裸名, {@code ALERT_SUCCESS} 已 jfx- 前缀</td></tr>
 *   <tr><td>选中/激活</td><td>{@code active / selected / disabled / hoverable / checkable / checked} (裸名,部分)</td><td>{@code jfx-menu-item-selected / jfx-segmented-item-selected / jfx-collapse-disabled / jfx-steps-finished/current/wait / jfx-tabs-active/disabled / jfx-tag-checkable/checked}</td><td>⚠️ 不一致：{@code SWITCH_SELECTED=switch-selected}、{@code GROUP_BOX_TAB_ITEM_ACTIVE=active}、{@code GROUP_BOX_HOVERABLE=hoverable} 裸名</td></tr>
 *   <tr><td>方向</td><td>{@code horizontal / vertical / both} (裸名)</td><td>{@code jfx-flex-horizontal/vertical / jfx-flex-wrap / jfx-space-horizontal/vertical/split / jfx-anchor-horizontal/vertical / jfx-divider-horizontal/vertical / jfx-form-horizontal/vertical/inline / jfx-steps-vertical / jfx-collapse-*}</td><td>✅ 一致：{@code RESIZABLE_PANEL_HANDLE_*} 用裸名，{@code FLEX_*} / {@code SPACE_*} 用 jfx- 前缀（不统一但语义清晰）</td></tr>
 *   <tr><td>阴影</td><td>{@code shadow-sm / shadow-md / shadow-lg}</td><td>—</td><td>✅ 裸名（仅 {@code SURFACE_SHADOW_*} 用）</td></tr>
 *   <tr><td>类名前缀</td><td>—</td><td>{@code JFX_LIST_VIEW / JFX_CHOICE_BOX / JFX_SEPARATOR / JFX_SPLIT_MENU_BUTTON}</td><td>⚠️ 命名异类：JFX_xxx_yyy 大写 JFX_ 前缀，常量名风格不统一（其他都用 JFX_xxx_yyy 不带连字符 JFX_）</td></tr>
 * </table>
 * <p><b>统一建议</b>（不在本次 P4 范围内，仅作未来参考）：</p>
 * <ol>
 *   <li>新组件尺寸修饰类统一用 {@code jfx-<comp>-small / jfx-<comp>-large}（参考 {@code jfx-table-small} 范式）</li>
 *   <li>新组件形状修饰类统一用 {@code jfx-<comp>-rounded / jfx-<comp>-square}（参考 {@code jfx-tag-rounded} 范式）</li>
 *   <li>新组件边框修饰类统一用 {@code jfx-<comp>-bordered / jfx-<comp>-borderless}（参考 {@code jfx-list-bordered} 范式）</li>
 *   <li>新组件状态色统一用 {@code jfx-<comp>-success/warning/error/info}（参考 {@code jfx-alert-*} 范式）</li>
 *   <li>新组件选中/激活统一用 {@code jfx-<comp>-selected/active/disabled}（参考 {@code jfx-menu-item-selected} 范式）</li>
 *   <li>已有裸名修饰类保留（与 LESS 选择器硬绑定，改名需同步改 11 套主题文件，性价比低）</li>
 * </ol>
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
       密度修饰类（ThemeManager 运行时切换）
       挂在 scene.getRoot() 上，全局生效。
       LESS 端见 theme-base.less 末尾的 .root.jfx-compact 覆盖块。
       ============================================ */
    public static final String DENSITY_COMPACT = "jfx-compact";

    /* ============================================
       通用圆角修饰 —— 任意组件可挂，LESS 端统一覆盖
       ============================================ */
    public static final String RADIUS_NONE = "jfx-radius-none";
    public static final String RADIUS_SM   = "jfx-radius-sm";
    public static final String RADIUS_LG   = "jfx-radius-lg";
    // MD 为默认值，不需要额外 class

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
    /** 内联文字按钮：透明背景、无边框、微 padding、12px 字号，适合嵌入状态栏/文本行。 */
    public static final String BUTTON_INLINE = "inline";
    /** JavaFX modena 内置根类名（裸名：与 jfx-* 前缀根类不同，这里指 JavaFX 自带 .button 选择器）。jfx- 修饰类必须搭配此裸名使用，组合为 .button.<modifier> 命中 _*.less 规则 */
    public static final String BUTTON_BASE = "button";
    /** 状态色变体：成功（绿）。参考 AntLantaFx antdesign-light.css 行 1259 + Ant Design 6.x Button status color。Color：@color-success-5 */
    public static final String BUTTON_SUCCESS = "success";
    /** 状态色变体：警告（黄）。JFXium 扩展（AntLantaFx 未实现 warning 状态）。Color：@color-warning-5 */
    public static final String BUTTON_WARNING = "warning";
    /** 状态色变体：危险（红）。参考 AntLantaFx antdesign-light.css 行 1283 + Ant Design 6.x Button danger。Color：@color-danger-5 */
    public static final String BUTTON_DANGER = "danger";

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
    /** Surface 边框/阴影修饰类 */
    public static final String SURFACE_BORDERED = "bordered";
    public static final String SURFACE_SHADOW_SM = "shadow-sm";
    public static final String SURFACE_SHADOW_MD = "shadow-md";
    public static final String SURFACE_SHADOW_LG = "shadow-lg";

    /** FilterBarAnt 通用筛选+操作工具条（admin 列表页上栏标配） */
    public static final String FILTER_BAR = "jfx-filter-bar";

    public static final String APP_SHELL = "jfx-app-shell";
    public static final String APP_SHELL_HEADER = "jfx-app-shell-header";
    public static final String APP_SHELL_SIDER = "jfx-app-shell-sider";
    /** 折叠/展开触发按钮专用修饰类（搭配 JavaFX modena 内置 .button 根类使用，与 _layout.less 行 147 .button.jfx-app-shell-sider-trigger 严格对齐） */
    public static final String APP_SHELL_SIDER_TRIGGER = "jfx-app-shell-sider-trigger";
    public static final String APP_SHELL_CONTENT = "jfx-app-shell-content";
    public static final String APP_SHELL_FOOTER = "jfx-app-shell-footer";

    /** LayoutAnt 页面布局（同 APP_SHELL 视觉，别名便于业务选择） */
    public static final String LAYOUT = "jfx-layout";
    public static final String LAYOUT_HEADER = "jfx-layout-header";
    public static final String LAYOUT_SIDER = "jfx-layout-sider";
    public static final String LAYOUT_CONTENT = "jfx-layout-content";
    public static final String LAYOUT_FOOTER = "jfx-layout-footer";

    /** PaginationAnt 分页根容器 */
    public static final String PAGINATION = "jfx-pagination";
    /** TextAreaAnt 只读形态（M19.6.2） */
    public static final String TEXT_AREA_READ_ONLY = "jfx-text-area-read-only";

    public static final String SPLIT_PANE = "jfx-split-pane-ant";

    public static final String RESIZABLE_PANEL = "jfx-resizable-panel";
    public static final String RESIZABLE_PANEL_CONTENT = "jfx-resizable-panel-content";
    public static final String RESIZABLE_PANEL_HANDLE = "jfx-resizable-panel-handle";
    public static final String RESIZABLE_PANEL_HANDLE_HORIZONTAL = "horizontal";
    public static final String RESIZABLE_PANEL_HANDLE_VERTICAL = "vertical";
    public static final String RESIZABLE_PANEL_HANDLE_BOTH = "both";

    public static final String SCROLL_PANE = "jfx-scroll-pane";
    public static final String SCROLL_PANE_VIEWPORT = "jfx-scroll-pane-viewport";

    /** AccordionAnt 手风琴（仅作容器选择器，标题/内容走 .titled-pane 内置） */
    public static final String ACCORDION = "jfx-accordion";

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

    /** 统一下拉箭头（ComboBox / Dropdown / MenuButton 等） */
    public static final String ARROW_DROPDOWN = "jfx-arrow-dropdown";

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
    public static final String FORM_SIZE_SMALL = "jfx-form-size-small";
    public static final String FORM_SIZE_LARGE = "jfx-form-size-large";

    /** TableAnt 表格 */
    public static final String TABLE = "jfx-table";
    public static final String TABLE_STRIPED = "jfx-table-striped";
    public static final String TABLE_BORDERED = "jfx-table-bordered";
    public static final String TABLE_COMPACT = "jfx-table-compact";
    public static final String TABLE_ALIGN_LEFT = "jfx-align-left";
    public static final String TABLE_ALIGN_CENTER = "jfx-align-center";
    public static final String TABLE_ALIGN_RIGHT = "jfx-align-right";
    public static final String TABLE_ALIGN_HEADER_LEFT = "jfx-align-header-left";
    public static final String TABLE_ALIGN_HEADER_CENTER = "jfx-align-header-center";
    public static final String TABLE_ALIGN_HEADER_RIGHT = "jfx-align-header-right";
    public static final String TABLE_ALIGN_CONTENT_LEFT = "jfx-align-content-left";
    public static final String TABLE_ALIGN_CONTENT_CENTER = "jfx-align-content-center";
    public static final String TABLE_ALIGN_CONTENT_RIGHT = "jfx-align-content-right";

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
    /** CheckBox/Radio shape 修饰类（M19.20） */
    public static final String CHECKBOX_SHAPE_CIRCLE = "jfx-shape-circle";
    public static final String CHECKBOX_SHAPE_ROUNDED = "jfx-shape-rounded";
    public static final String CHECKBOX_SHAPE_SQUARE = "jfx-shape-square";

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
    /** text 形态：含文本的胶囊形徽标，配合 success/warning/error/info 修饰类使用 */
    public static final String BADGE_TEXT = "jfx-badge-text";
    /** small/large 尺寸修饰类（与 BADGE 复合使用） */
    public static final String BADGE_SMALL = "jfx-badge-small";
    public static final String BADGE_LARGE = "jfx-badge-large";

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
    public static final String ALERT_WITH_ICON = "jfx-alert-with-icon";
    public static final String ALERT_DESCRIPTION = "jfx-alert-description";
    public static final String TOOLTIP = "jfx-tooltip";

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
    public static final String CODEBLOCK_TEXTAREA = "jfx-codeblock-textarea";
    public static final String CODE_THEME_LIGHT = "jfx-code-theme-light";
    public static final String CODE_THEME_DARK = "jfx-code-theme-dark";
    public static final String CODE_TOKEN_KEYWORD = "jfx-code-keyword";
    public static final String CODE_TOKEN_STRING = "jfx-code-string";
    public static final String CODE_TOKEN_COMMENT = "jfx-code-comment";
    public static final String CODE_TOKEN_NUMBER = "jfx-code-number";
    public static final String CODE_TOKEN_TEXT = "jfx-code-text";
    /** 代码块行号容器（左侧侧栏） */
    public static final String CODE_LINE_NUMBERS = "jfx-code-line-numbers";
    /** 代码块单个行号节点 */
    public static final String CODE_LINE_NUMBER = "jfx-code-line-number";

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
    public static final String SPIN_OVERLAY = "jfx-spin-overlay";
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

    /** TreeAnt 树形组件 */
    public static final String TREE_CELL = "jfx-tree-cell";

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
    public static final String INPUT_NUMBER_SMALL = "jfx-input-number-small";
    public static final String INPUT_NUMBER_LARGE = "jfx-input-number-large";
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
    public static final String CRUD_TEMPLATE           = "jfx-crud-template";
    public static final String CRUD_TEMPLATE_TITLE     = "jfx-crud-template-title";
    public static final String CRUD_TEMPLATE_TOPBAR    = "jfx-crud-template-topbar";
    public static final String CRUD_TEMPLATE_BODY      = "jfx-crud-template-body";
    public static final String CRUD_TEMPLATE_BOTTOMBAR = "jfx-crud-template-bottombar";
    /** 边框修饰类（jfx- 前缀避免与 .bordered 裸名冲突；与 LESS 中 .jfx-crud-template.jfx-crud-template-bordered 联动） */
    public static final String CRUD_TEMPLATE_BORDERED  = "jfx-crud-template-bordered";

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
    public static final String STATUS_BAR_ACTION = "jfx-status-bar-action";

    /* ============================================
       ContextMenuAnt（PC 软件刚需）— 右键菜单
       ============================================ */
    public static final String CONTEXT_MENU = "jfx-context-menu";
    public static final String CONTEXT_MENU_ITEM = "jfx-context-menu-item";
    public static final String CONTEXT_MENU_ITEM_DISABLED = "jfx-context-menu-item-disabled";
    public static final String CONTEXT_MENU_DIVIDER = "jfx-context-menu-divider";
    public static final String CONTEXT_MENU_ACCELERATOR = "jfx-context-menu-accelerator";

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
    public static final String CLOSE_BUTTON          = "jfx-close-button";

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
    public static final String SKELETON_SHIMMER = "jfx-skeleton-shimmer";

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
    public static final String POPOVER_CONTENT    = "jfx-popover-content";

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
    /** TableAnt 危险动作按钮（红色文字按钮） */
    public static final String BUTTON_DANGER_TEXT = "jfx-button-danger-text";

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

    /* ============================================
       TagAnt — 标签
       ============================================ */
    public static final String TAG              = "jfx-tag";
    public static final String TAG_LABEL        = "jfx-tag-label";
    public static final String TAG_CLOSE        = "jfx-tag-close";
    public static final String TAG_CLOSE_ICON   = "jfx-tag-close-icon";
    /** Tag 形状修饰类（与 .jfx-tag 复合：.jfx-tag.jfx-tag-rounded） */
    public static final String TAG_ROUNDED      = "jfx-tag-rounded";
    public static final String TAG_SQUARE       = "jfx-tag-square";
    /** Tag 状态色修饰类 */
    public static final String TAG_DEFAULT      = "jfx-tag-default";
    public static final String TAG_PRIMARY      = "jfx-tag-primary";
    public static final String TAG_SUCCESS      = "jfx-tag-success";
    public static final String TAG_PROCESSING   = "jfx-tag-processing";
    public static final String TAG_ERROR        = "jfx-tag-error";
    public static final String TAG_WARNING      = "jfx-tag-warning";
    public static final String TAG_HAS_COLOR    = "jfx-tag-has-color";
    /** Tag 无边框样式（带默认色时使用） */
    public static final String TAG_BORDERLESS   = "jfx-tag-borderless";
    /** Tag 可选中状态 */
    public static final String TAG_CHECKABLE    = "jfx-tag-checkable";
    public static final String TAG_CHECKED      = "jfx-tag-checked";
    /** Tag 尺寸修饰类 */
    public static final String TAG_SMALL        = "jfx-tag-small";
    public static final String TAG_LARGE        = "jfx-tag-large";

    /* ============================================
       ResultDisplay — 结果页（M19 标准化）
       既有 RESULT_TITLE/RESULT_SUBTITLE 已在上面，本块补齐根类 + icon/extra
       ============================================ */
    public static final String RESULT           = "jfx-result";
    public static final String RESULT_ICON      = "jfx-result-icon";
    public static final String RESULT_ICON_BOX  = "jfx-result-icon-box";
    public static final String RESULT_BODY      = "jfx-result-body";
    public static final String RESULT_EXTRA     = "jfx-result-extra";
    /** Result 状态修饰类（颜色用 5 套语义色，与 Alert 保持一致） */
    public static final String RESULT_STATUS_SUCCESS = "jfx-result-status-success";
    public static final String RESULT_STATUS_INFO    = "jfx-result-status-info";
    public static final String RESULT_STATUS_WARNING = "jfx-result-status-warning";
    public static final String RESULT_STATUS_ERROR   = "jfx-result-status-error";
    public static final String RESULT_STATUS_404     = "jfx-result-status-404";
    public static final String RESULT_STATUS_403     = "jfx-result-status-403";
    public static final String RESULT_STATUS_500     = "jfx-result-status-500";

    /* ============================================
       AvatarAnt — 头像尺寸/形状变体
       已有 AVATAR/AVATAR_BG_DEFAULT/AVATAR_FG_DEFAULT
       补尺寸和形状修饰类
       ============================================ */
    public static final String AVATAR_SMALL      = "jfx-avatar-small";
    public static final String AVATAR_LARGE      = "jfx-avatar-large";
    public static final String AVATAR_SQUARE     = "jfx-avatar-square";
    public static final String AVATAR_GROUP      = "jfx-avatar-group";
    /** Avatar 文字根类 + 字号修饰类（避免 Java setStyle 拼 -fx-font-size） */
    public static final String AVATAR_TEXT       = "jfx-avatar-text";
    public static final String AVATAR_TEXT_24    = "jfx-avatar-text-24";
    public static final String AVATAR_TEXT_32    = "jfx-avatar-text-32";
    public static final String AVATAR_TEXT_40    = "jfx-avatar-text-40";
    public static final String AVATAR_TEXT_64    = "jfx-avatar-text-64";

    /* ============================================
       ImageAnt — 圆角修饰类（border-radius 由 LESS 钳制）
       已有 IMAGE/IMAGE_FALLBACK
       ============================================ */
    public static final String IMAGE_ROUNDED     = "jfx-image-rounded";
    public static final String IMAGE_CIRCLE      = "jfx-image-circle";
    public static final String IMAGE_PREVIEW     = "jfx-image-preview";

    /* ============================================
       Overlay — 全屏遮罩层
       ============================================ */
    public static final String OVERLAY           = "jfx-overlay";

    /* ============================================
       TabsAnt — 标签页根类（content 区域用 jfx-tabs-content）
       ============================================ */
    public static final String TABS_ROOT         = "jfx-tabs";
    public static final String TABS_CONTENT      = "jfx-tabs-content";

    /* ============================================
       StepsAnt — 步骤条尺寸修饰类
       ============================================ */
    public static final String STEPS_SMALL       = "jfx-steps-small";

    /* ============================================
       TreeSelectAnt — 行内缩进修饰类（深度变体由 Java 端生成 jfx-tree-select-row-N）
       已有 TREE_SELECT/TREE_SELECT_ROW/TREE_SELECT_FIELD/TREE_SELECT_LABEL/TREE_SELECT_ARROW
       补选中/禁用/激活
       ============================================ */
    public static final String TREE_SELECT_ACTIVE     = "jfx-tree-select-active";
    public static final String TREE_SELECT_LEAF       = "jfx-tree-select-leaf";
    public static final String TREE_SELECT_INDENT     = "jfx-tree-select-indent";

    /* ============================================
       ListViewAnt — ListView 包装
       ============================================ */
    public static final String JFX_LIST_VIEW = "jfx-list-view";

    /* ============================================
       ChoiceBoxAnt — ChoiceBox 包装
       ============================================ */
    public static final String JFX_CHOICE_BOX = "jfx-choice-box";

    /* ============================================
       ComboBoxAnt / DatePickerAnt — 原生控件包装
       ============================================ */
    public static final String JFX_COMBO_BOX = "jfx-combo-box";
    public static final String JFX_DATE_PICKER = "jfx-date-picker";

    /* ============================================
       SeparatorAnt — Separator 包装
       ============================================ */
    public static final String JFX_SEPARATOR = "jfx-separator";

    /* ============================================
       TitledPaneAnt / ToggleButtonAnt — 原生控件包装
       ============================================ */
    public static final String JFX_TITLED_PANE = "jfx-titled-pane";
    public static final String JFX_TOGGLE_BUTTON = "jfx-toggle-button";

    /* ============================================
       MenuButtonAnt — MenuButton 包装
       ============================================ */
    public static final String JFX_MENU_BUTTON = "jfx-menu-button";

    /* ============================================
       SplitMenuButtonAnt — SplitMenuButton 包装
       ============================================ */
    public static final String JFX_SPLIT_MENU_BUTTON = "jfx-split-menu-button";
    /** MenuButton/SplitMenuButton arrow style 修饰类（M19.6.1） */
    public static final String JFX_ARROW_TRIANGLE = "jfx-arrow-triangle";
    public static final String JFX_NO_ARROW = "jfx-no-arrow";
}
