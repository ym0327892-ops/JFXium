package org.openkawu.jfxium.core.builder;

import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 组件 Builder 公共基类：承载 {@code style}、{@code styleClass} 和 {@code padding} 的公共字段和方法。
 *
 * <h2>背景</h2>
 * 之前 {@code ButtonAnt / AppShellAnt / PageAnt / SurfaceAnt / ActionBarAnt /
 * GroupBoxAnt / SplitPaneAnt / ResizablePanelAnt / ScrollContainerAnt / GroupBoxAnt} 等 9+ 个 Builder
 * 各自重复实现：
 * <pre>{@code
 * private String style = "";
 * private final List<String> extraStyleClasses = new ArrayList<>();
 * public Builder style(String s) { this.style = s; return this; }
 * public Builder styleClass(String c) { this.extraStyleClasses.add(c); return this; }
 * // build() 末尾：node.getStyleClass().addAll(extraStyleClasses);
 * //              if (!style.isEmpty()) node.setStyle(style);
 * }</pre>
 *
 * 抽到这里后，每个 Builder 减少 ~10 行样板代码，且修改风格统一时只动一处。
 *
 * <h2>M7 新增：padding 支持</h2>
 * 添加 {@code padding(Insets)} 和 {@code padding(double)} 方法，统一管理内边距设置。
 * 5+ 个组件（SurfaceAnt/ActionBarAnt/ScrollContainerAnt/PageAnt/ResizablePanelAnt）
 * 重复实现了相同的 padding 逻辑，现在统一到基类。
 *
 * <h2>self-bounded 泛型</h2>
 * 使用 {@code <SELF extends AbstractStyleBuilder<SELF>>} 让链式 API 返回子类自己的类型，
 * 不会因为继承父类导致 {@code .style().size()} 这种链式调用因为父方法返回 {@code AbstractStyleBuilder}
 * 而无法继续调用子类方法。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * public class MyComponentAnt {
 *   public static class Builder extends AbstractStyleBuilder<Builder> {
 *       private String title;
 *       public Builder title(String t) { this.title = t; return this; }
 *       public VBox build() {
 *           VBox v = new VBox();
 *           applyStyles(v);  // ← 复用父类工具方法（包含 padding）
 *           return v;
 *       }
 *   }
 * }
 * }</pre>
 *
 * @param <SELF> 子类自身类型，用于让 setter 链式返回子类引用
 */
public abstract class AbstractStyleBuilder<SELF extends AbstractStyleBuilder<SELF>> {

    /** 用户通过 {@link #style(String)} 注入的 inline 样式。空串表示未设置。*/
    protected String style = "";

    /** 用户通过 {@link #styleClass(String)} 注入的额外样式类名。*/
    protected final List<String> extraStyleClasses = new ArrayList<>();

    /** 用户通过 {@link #padding(Insets)} 或 {@link #padding(double)} 设置的内边距。null 表示未设置。*/
    protected Insets padding = null;

    /** 用户通过 {@link #maxWidth(double)} 设置的最大宽度。NaN 表示未设置。 */
    protected double maxWidth = Double.NaN;

    /** 用户通过 {@link #minWidth(double)} 设置的最小宽度。NaN 表示未设置。 */
    protected double minWidth = Double.NaN;

    /** 用户通过 {@link #prefWidth(double)} 设置的首选宽度。NaN 表示未设置。 */
    protected double prefWidth = Double.NaN;

    /** 用户通过 {@link #maxHeight(double)} 设置的最大高度。NaN 表示未设置。 */
    protected double maxHeight = Double.NaN;

    /** 用户通过 {@link #minHeight(double)} 设置的最小高度。NaN 表示未设置。 */
    protected double minHeight = Double.NaN;

    /** 用户通过 {@link #prefHeight(double)} 设置的首选高度。NaN 表示未设置。 */
    protected double prefHeight = Double.NaN;

    /** 用户通过 {@link #prefSize(double, double)} 设置的同步首选宽高。NaN 表示未设置。 */
    protected double prefSizeW = Double.NaN;
    /** 用户通过 {@link #prefSize(double, double)} 设置的同步首选宽高。NaN 表示未设置。 */
    protected double prefSizeH = Double.NaN;
    /** 用户通过 {@link #maxSize(double, double)} 设置的同步最大宽高。NaN 表示未设置。 */
    protected double maxSizeW = Double.NaN;
    /** 用户通过 {@link #maxSize(double, double)} 设置的同步最大宽高。NaN 表示未设置。 */
    protected double maxSizeH = Double.NaN;
    /** 用户通过 {@link #minSize(double, double)} 设置的同步最小宽高。NaN 表示未设置。 */
    protected double minSizeW = Double.NaN;
    /** 用户通过 {@link #minSize(double, double)} 设置的同步最小宽高。NaN 表示未设置。 */
    protected double minSizeH = Double.NaN;

    /** 顶部边框线。默认 false。 */
    protected boolean borderTop = false;
    /** 底部边框线。默认 false。 */
    protected boolean borderBottom = false;
    /** 左侧边框线。默认 false。 */
    protected boolean borderLeft = false;
    /** 右侧边框线。默认 false。 */
    protected boolean borderRight = false;

    // ============================================================
    // 高频节点属性
    // ============================================================

    /** 可见性。null 表示未设置（不覆盖默认值）。 */
    protected Boolean visible = null;
    /** 禁用状态。null 表示未设置。 */
    protected Boolean disable = null;
    /** 是否受布局管理。null 表示未设置。 */
    protected Boolean managed = null;
    /** 透明度。NaN 表示未设置。 */
    protected double opacity = Double.NaN;
    /** 鼠标光标。null 表示未设置。 */
    protected Cursor cursor = null;
    /** 节点 ID。null 表示未设置。 */
    protected String id = null;

    /**
     * 设置 inline 样式。慎用，建议优先使用 {@link #styleClass(String)} + LESS。
     * 多次调用会覆盖前一次（与 setter 一贯语义保持一致）。
     */
    @SuppressWarnings("unchecked")
    public SELF style(String style) {
        this.style = style != null ? style : "";
        return (SELF) this;
    }

    /**
     * 追加额外的 styleClass。多次调用会按顺序累积，不去重。
     * 推荐用法：把组件级修饰类（如 "compact"、"bordered"）通过此方法外挂。
     */
    @SuppressWarnings("unchecked")
    public SELF styleClass(String styleClass) {
        if (styleClass != null && !styleClass.isEmpty()) {
            this.extraStyleClasses.add(styleClass);
        }
        return (SELF) this;
    }

    /**
     * 设置内边距（四边相同）。
     */
    @SuppressWarnings("unchecked")
    public SELF padding(double padding) {
        this.padding = new Insets(padding);
        return (SELF) this;
    }

    /**
     * 设置内边距（四边独立）。
     */
    @SuppressWarnings("unchecked")
    public SELF padding(double top, double right, double bottom, double left) {
        this.padding = new Insets(top, right, bottom, left);
        return (SELF) this;
    }

    /**
     * 设置内边距（Insets 对象）。
     */
    @SuppressWarnings("unchecked")
    public SELF padding(Insets padding) {
        this.padding = padding;
        return (SELF) this;
    }

    /** 设置最大宽度。 */
    @SuppressWarnings("unchecked")
    public SELF maxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
        return (SELF) this;
    }

    /** 设置最小宽度。 */
    @SuppressWarnings("unchecked")
    public SELF minWidth(double minWidth) {
        this.minWidth = minWidth;
        return (SELF) this;
    }

    /** 设置首选宽度。 */
    @SuppressWarnings("unchecked")
    public SELF prefWidth(double prefWidth) {
        this.prefWidth = prefWidth;
        return (SELF) this;
    }

    /** 设置最大高度。 */
    @SuppressWarnings("unchecked")
    public SELF maxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
        return (SELF) this;
    }

    /** 设置最小高度。 */
    @SuppressWarnings("unchecked")
    public SELF minHeight(double minHeight) {
        this.minHeight = minHeight;
        return (SELF) this;
    }

    /** 设置首选高度。 */
    @SuppressWarnings("unchecked")
    public SELF prefHeight(double prefHeight) {
        this.prefHeight = prefHeight;
        return (SELF) this;
    }

    /** 同时设置首选宽高。 */
    @SuppressWarnings("unchecked")
    public SELF prefSize(double width, double height) {
        this.prefSizeW = width;
        this.prefSizeH = height;
        return (SELF) this;
    }

    /** 同时设置最大宽高。 */
    @SuppressWarnings("unchecked")
    public SELF maxSize(double width, double height) {
        this.maxSizeW = width;
        this.maxSizeH = height;
        return (SELF) this;
    }

    /** 同时设置最小宽高。 */
    @SuppressWarnings("unchecked")
    public SELF minSize(double width, double height) {
        this.minSizeW = width;
        this.minSizeH = height;
        return (SELF) this;
    }

    // ============================================================
    // 边框线（方向性分割线）—— 所有继承此 Builder 的组件零成本获得
    // ============================================================

    /** 顶部分割线。 */
    @SuppressWarnings("unchecked")
    public SELF borderTop(boolean on) {
        this.borderTop = on;
        return (SELF) this;
    }

    /** 顶部分割线（无参重载，等价 borderTop(true)）。 */
    public SELF borderTop() { return borderTop(true); }

    /** 底部分割线。 */
    @SuppressWarnings("unchecked")
    public SELF borderBottom(boolean on) {
        this.borderBottom = on;
        return (SELF) this;
    }

    /** 底部分割线（无参重载，等价 borderBottom(true)）。 */
    public SELF borderBottom() { return borderBottom(true); }

    /** 左侧分割线。 */
    @SuppressWarnings("unchecked")
    public SELF borderLeft(boolean on) {
        this.borderLeft = on;
        return (SELF) this;
    }

    /** 左侧分割线（无参重载，等价 borderLeft(true)）。 */
    public SELF borderLeft() { return borderLeft(true); }

    /** 右侧分割线。 */
    @SuppressWarnings("unchecked")
    public SELF borderRight(boolean on) {
        this.borderRight = on;
        return (SELF) this;
    }

    /** 右侧分割线（无参重载，等价 borderRight(true)）。 */
    public SELF borderRight() { return borderRight(true); }

    // ============================================================
    // 高频节点属性 —— 所有继承此 Builder 的组件零成本获得
    // ============================================================

    /** 设置可见性。 */
    @SuppressWarnings("unchecked")
    public SELF visible(boolean visible) {
        this.visible = visible;
        return (SELF) this;
    }

    /** 设置禁用状态。 */
    @SuppressWarnings("unchecked")
    public SELF disable(boolean disable) {
        this.disable = disable;
        return (SELF) this;
    }

    /** 设置是否受布局管理。 */
    @SuppressWarnings("unchecked")
    public SELF managed(boolean managed) {
        this.managed = managed;
        return (SELF) this;
    }

    /** 设置透明度（0.0 完全透明 ~ 1.0 不透明）。 */
    @SuppressWarnings("unchecked")
    public SELF opacity(double opacity) {
        this.opacity = opacity;
        return (SELF) this;
    }

    /** 设置鼠标光标。 */
    @SuppressWarnings("unchecked")
    public SELF cursor(Cursor cursor) {
        this.cursor = cursor;
        return (SELF) this;
    }

    /** 设置节点 ID。 */
    @SuppressWarnings("unchecked")
    public SELF id(String id) {
        this.id = id;
        return (SELF) this;
    }

    /**
     * 在 build() 末尾调用，把累积的 extraStyleClasses、inline style 和 padding 应用到目标 Node。
     * 子类负责确保此方法在所有内置 styleClass 添加之后调用，
     * 这样用户通过 {@link #styleClass(String)} 添加的类会出现在内置类之后，方便覆盖。
     */
    protected final void applyStyles(Node node) {
        if (node == null) return;
        node.getStyleClass().addAll(extraStyleClasses);
        if (!style.isEmpty()) {
            node.setStyle(style);
        }
        // 高频节点属性
        if (visible != null)  node.setVisible(visible);
        if (disable != null)  node.setDisable(disable);
        if (managed != null)  node.setManaged(managed);
        if (!Double.isNaN(opacity)) node.setOpacity(opacity);
        if (cursor != null)   node.setCursor(cursor);
        if (id != null)       node.setId(id);
        if (node instanceof Region region) {
            if (borderTop)    node.getStyleClass().add(JfxStyles.BORDER_TOP);
            if (borderBottom) node.getStyleClass().add(JfxStyles.BORDER_BOTTOM);
            if (borderLeft)   node.getStyleClass().add(JfxStyles.BORDER_LEFT);
            if (borderRight)  node.getStyleClass().add(JfxStyles.BORDER_RIGHT);
            if (padding != null) region.setPadding(padding);
            if (!Double.isNaN(maxWidth)) region.setMaxWidth(maxWidth);
            if (!Double.isNaN(minWidth)) region.setMinWidth(minWidth);
            if (!Double.isNaN(prefWidth)) region.setPrefWidth(prefWidth);
            if (!Double.isNaN(maxHeight)) region.setMaxHeight(maxHeight);
            if (!Double.isNaN(minHeight)) region.setMinHeight(minHeight);
            if (!Double.isNaN(prefHeight)) region.setPrefHeight(prefHeight);
            // 同步尺寸
            if (!Double.isNaN(prefSizeW)) region.setPrefSize(prefSizeW, prefSizeH);
            if (!Double.isNaN(maxSizeW)) region.setMaxSize(maxSizeW, maxSizeH);
            if (!Double.isNaN(minSizeW)) region.setMinSize(minSizeW, minSizeH);
        }
    }

    /**
     * Styleable 版本的 applyStyles，覆盖 {@link javafx.scene.control.Tooltip} 这种
     * 不直接继承 Node 但实现 Styleable 的控件。
     *
     * <p>本重载只应用 {@link #extraStyleClasses}。inline style（{@link #style}）
     * 不在此处处理，因为 {@link Styleable} 接口没有 setStyle 方法。
     * 如需应用 inline style，子类 build() 应当在调用 {@code super.applyStyles(styleable)}
     * 之后自己调用 {@code controlInstance.setStyle(getStyle())} 完成。
     *
     * <p>大多数 Styleable 实际是 Node 子类，应优先使用 {@link #applyStyles(Node)}。
     */
    protected final void applyStyles(Styleable styleable) {
        if (styleable == null) return;
        styleable.getStyleClass().addAll(extraStyleClasses);
        // 不在此处处理 inline style：Styleable 接口没有 setStyle 方法，
        // 强行用反射调用会引入隐形依赖。子类如需应用 inline style，
        // 在 build() 里直接读取 protected 字段 {@link #style} 自行处理即可。
    }

    /** 暴露 inline style 给子类（部分子类的容器是 Styleable 而非 Node 时需要）。*/
    protected final String getStyle() {
        return style;
    }
}
