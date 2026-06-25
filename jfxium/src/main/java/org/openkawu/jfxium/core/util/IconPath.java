package org.openkawu.jfxium.core.util;

import javafx.scene.shape.SVGPath;

/**
 * 内联 SVG 图标路径工具。
 * 收口项目中散落在各 {@code *Ant} 组件里的 SVG path 字符串常量与
 * {@code enum → path/styleClass} switch 映射（{@code NotificationCard / MessageCard / ResultDisplay}
 * 三个组件的 {@code getIconPath + getIconStyleClass} 双 switch 是同一模式的副本）。
 *
 * <h2>解决的重复模式</h2>
 *
 * <h3>模式 F：内联 SVG path 字符串（8 处）</h3>
 * <pre>{@code
 * // 原样板（出现在 TagAnt / RateAnt / EmptyAnt / CollapseAnt /
 * //            CascaderAnt / BackTopAnt / PopconfirmPanel / UploadAnt）
 * SVGPath x = new SVGPath();
 * x.setContent("M6 4.5L4.5 6 6 7.5 7.5 6 6 4.5z");
 * }</pre>
 *
 * <h3>模式 A：enum → icon 映射（3 处）</h3>
 * <pre>{@code
 * // 原样板（出现在 NotificationCard / MessageCard / ResultDisplay）
 * private String getIconPath(Type type) {
 *     return switch (type) {
 *         case SUCCESS -> "M12 2C6.48 2 2 6.48...";
 *         case ERROR -> "M12 2C6.48 2 2 6.48...";
 *         // ...
 *     };
 * }
 * private String getIconStyleClass(Type type) {
 *     return switch (type) {
 *         case SUCCESS -> JfxStyles.ICON_SUCCESS;
 *         case ERROR -> JfxStyles.ICON_DANGER;
 *         // ...
 *     };
 * }
 * }</pre>
 *
 * <p>用本工具类后，组件代码变为单行调用：
 * <pre>{@code
 * SVGPath x = IconPath.closeX();
 * // 或更彻底
 * SVGPath x = IconPath.create(IconPath.CLOSE_X);
 *
 * // enum 映射
 * SVGPath icon = IconPath.statusIcon(Status.SUCCESS);
 * icon.getStyleClass().add(IconPath.statusStyleClass(Status.SUCCESS));
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>常量集中</b>：所有 path 字符串集中维护，避免散落在多个组件中重复维护同一份 SVG。</li>
 *   <li><b>工厂方法</b>：{@link #create(String)} 返回新 {@link SVGPath}（不共享实例，避免主题切换相互干扰）。</li>
 *   <li><b>enum 映射</b>：{@link #statusIcon(StatusStyle)} 一站式提供 SVGPath+styleClass，调用方零样板。</li>
 *   <li><b>无副作用</b>：纯函数，不持有任何节点引用。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 1. 直接用预定义常量
 * SVGPath close = IconPath.closeX();
 * close.getStyleClass().add(JfxStyles.TAG_CLOSE_ICON);
 *
 * // 2. 自定义内容
 * SVGPath custom = IconPath.create("M0 0L10 10");
 *
 * // 3. enum → 完整图标节点（含样式类）
 * StatusStyle style = IconPath.styleFor(Status.SUCCESS);
 * SVGPath icon = IconPath.create(style.path());
 * icon.getStyleClass().add(style.styleClass());
 * }</pre>
 */
public final class IconPath {

    private IconPath() {
        // 工具类禁止实例化
    }

    // ============================================================
    // 通用图标常量（按"语义"命名，对应组件中使用点）
    // ============================================================

    /** Tag 关闭按钮的 X 形小图标（TagAnt）。*/
    public static final String CLOSE_X = "M6 4.5L4.5 6 6 7.5 7.5 6 6 4.5z";

    /** RateAnt 评分星形。*/
    public static final String STAR = "M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z";

    /** EmptyAnt 空状态文件夹图标。*/
    public static final String FOLDER = "M20 6h-8l-2-2H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm0 12H4V8h16v10z";

    /** CollapseAnt 折叠面板右侧箭头（默认朝下，旋转 180° 后朝上）。*/
    public static final String CHEVRON_DOWN_COLLAPSE = "M4 6L8 10L12 6";

    /** CascaderAnt 级联选择右侧箭头。*/
    public static final String CHEVRON_RIGHT_CASCADER = "M6 4L10 8L6 12";

    /** BackTopAnt 回到顶部上箭头。*/
    public static final String ARROW_UP = "M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6z";

    /** PopconfirmPanel 警告三角图标。*/
    public static final String WARNING_TRIANGLE = "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";

    /** UploadAnt 拖拽区域云上传图标。*/
    public static final String CLOUD_UPLOAD = "M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z";

    // ============================================================
    // 状态图标（enum → SVG path）— 覆盖 NotificationCard / MessageCard / ResultDisplay
    // ============================================================

    /** 成功（✓ 在圆圈中）。*/
    public static final String ICON_SUCCESS = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";

    /** 错误（! 在圆圈中）。*/
    public static final String ICON_ERROR = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";

    /** 信息（i 在圆圈中）。*/
    public static final String ICON_INFO = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";

    /** 加载中（旋转箭头）。*/
    public static final String ICON_LOADING = "M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6 0 1.01-.25 1.97-.7 2.8l1.46 1.46C19.54 15.03 20 13.57 20 12c0-4.42-3.58-8-8-8zm0 14c-3.31 0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24 7.74C4.46 8.97 4 10.43 4 12c0 4.42 3.58 8 8 8v3l4-4-4-4v3z";

    /** 404 / 未找到（问号占位）。*/
    public static final String ICON_NOT_FOUND = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z";

    /** 403 / 禁止（盾形）。*/
    public static final String ICON_FORBIDDEN = "M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z";

    /** 500 / 服务器内部错误（人形 + 卡片）。*/
    public static final String ICON_INTERNAL_ERROR = "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z";

    // ============================================================
    // 工厂方法
    // ============================================================

    /**
     * 创建一个包含指定 SVG 路径内容的 {@link SVGPath}。
     *
     * <p>每次返回<b>新实例</b>，避免共享节点导致的主题切换相互干扰。
     * {@code content} 为 null 时返回的 SVGPath 内容为空字符串（行为等同"未设置"）。</p>
     */
    public static SVGPath create(String content) {
        SVGPath path = new SVGPath();
        path.setContent(content == null ? "" : content);
        return path;
    }

    /**
     * 创建带缩放的 SVGPath。{@code scale} 应用于 X/Y 两个轴。
     */
    public static SVGPath create(String content, double scale) {
        SVGPath path = create(content);
        path.setScaleX(scale);
        path.setScaleY(scale);
        return path;
    }

    // ----- 常用图标快捷方法（保持 API 一致、便于阅读）-----

    /** Tag 关闭按钮的 X 形。*/
    public static SVGPath closeX() {
        return create(CLOSE_X);
    }

    /** RateAnt 评分星形。*/
    public static SVGPath star() {
        return create(STAR);
    }

    /** EmptyAnt 空状态文件夹。*/
    public static SVGPath folder() {
        return create(FOLDER);
    }

    /** CollapseAnt 折叠面板箭头。*/
    public static SVGPath chevronDownCollapse() {
        return create(CHEVRON_DOWN_COLLAPSE);
    }

    /** CascaderAnt 级联选择箭头。*/
    public static SVGPath chevronRightCascader() {
        return create(CHEVRON_RIGHT_CASCADER);
    }

    /** BackTopAnt 回到顶部上箭头。*/
    public static SVGPath arrowUp() {
        return create(ARROW_UP);
    }

    /** PopconfirmPanel 警告三角。*/
    public static SVGPath warningTriangle() {
        return create(WARNING_TRIANGLE);
    }

    /** UploadAnt 云上传图标（默认缩放 2x，匹配原实现）。*/
    public static SVGPath cloudUpload() {
        return create(CLOUD_UPLOAD, 2.0);
    }

    /** EmptyAnt 空状态文件夹（默认缩放 2x，匹配原实现）。*/
    public static SVGPath folderScaled() {
        return create(FOLDER, 2.0);
    }

    /** BackTopAnt 上箭头（默认缩放 1.5x，匹配原实现）。*/
    public static SVGPath arrowUpScaled() {
        return create(ARROW_UP, 1.5);
    }
}
