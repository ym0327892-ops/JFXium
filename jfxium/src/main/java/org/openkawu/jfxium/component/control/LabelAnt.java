package org.openkawu.jfxium.component.control;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.FontTokens;

/**
 * LabelAnt - 继承式原生 Label 封装（M19.48 引入）。
 *
 * <p><b>定位</b>：项目最基础的文本控件，补齐「连最简单的 Label 都没有封装」的缺口。
 * 跟 {@link VBoxAnt} 同款「双工厂模式」——既能当工厂链式构建，也能被业务继承。</p>
 *
 * <h2>跟 TypographyAnt 的区别</h2>
 * <ul>
 *   <li>{@link TypographyAnt} —— 富排版快捷工厂（{@code title/paragraph/text}），{@code build()} 返回原生 {@link Label}。
 *       内部已统一委托给 {@code LabelAnt}，仅做结构性属性封装（标题字号、段落最大高度）。</li>
 *   <li>{@code LabelAnt} —— 轻量「带链式的 Label」，{@code extends Label} 本身，
 *       支持所有富排版装饰（type/strong/italic/underline/delete/code/mark/copyable/ellipsis），
 *       适合「就想要一个能链式配置、还能继承当基类」的最朴素场景。</li>
 * </ul>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式</h3>
 * <pre>{@code
 * Label title = LabelAnt.create("用户名")
 *     .secondary()        // 次要文字色（复用 typography 语义）
 *     .wrap(true)
 *     .build();            // build() 返回自身（也是 Label，可省略）
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class FieldLabel extends LabelAnt {
 *     public FieldLabel(String text) {
 *         setText(text);
 *         secondary();
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link Label} 也是工厂——可继续被业务继承。</li>
 *   <li><b>样式分层</b>：文字色 / 装饰一律走 styleClass（复用 {@code typography-*} 系列 LESS），
 *       Java 端不 setStyle 拼颜色（项目约束 SKILL §5.1）。</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式。</li>
 * </ul>
 */
public class LabelAnt extends Label implements LayoutCommon<LabelAnt> {

    // ============================================================
    // 工厂入口
    // ============================================================

    public static LabelAnt create() {
        return new LabelAnt();
    }

    public static LabelAnt create(String text) {
        return new LabelAnt(text);
    }

    public static LabelAnt create(String text, Node graphic) {
        return new LabelAnt(text, graphic);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public LabelAnt() {
        super();
        init();
    }

    public LabelAnt(String text) {
        super(text);
        init();
    }

    public LabelAnt(String text, Node graphic) {
        super(text, graphic);
        init();
    }

    private void init() {
        getStyleClass().add(JfxStyles.TYPOGRAPHY_TEXT);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置文本。 */
    public LabelAnt text(String text) {
        setText(text);
        return this;
    }

    /** 设置文字语义色（直接收 {@link TypographyAnt.TextColor}，不拷贝枚举）。 */
    public LabelAnt type(TypographyAnt.TextColor type) {
        // 先清掉可能已挂的语义色修饰类，避免多次调用叠加
        getStyleClass().removeAll(
                JfxStyles.TYPOGRAPHY_SECONDARY, JfxStyles.TYPOGRAPHY_SUCCESS,
                JfxStyles.TYPOGRAPHY_WARNING, JfxStyles.TYPOGRAPHY_DANGER,
                JfxStyles.TYPOGRAPHY_DISABLED);
        switch (type) {
            case SECONDARY -> getStyleClass().add(JfxStyles.TYPOGRAPHY_SECONDARY);
            case SUCCESS -> getStyleClass().add(JfxStyles.TYPOGRAPHY_SUCCESS);
            case WARNING -> getStyleClass().add(JfxStyles.TYPOGRAPHY_WARNING);
            case DANGER -> getStyleClass().add(JfxStyles.TYPOGRAPHY_DANGER);
            case DISABLED -> getStyleClass().add(JfxStyles.TYPOGRAPHY_DISABLED);
            default -> { /* PRIMARY 无修饰 */ }
        }
        return this;
    }

    /** 语义色快捷方法。 */
    public LabelAnt secondary() { return type(TypographyAnt.TextColor.SECONDARY); }
    public LabelAnt success()   { return type(TypographyAnt.TextColor.SUCCESS); }
    public LabelAnt warning()   { return type(TypographyAnt.TextColor.WARNING); }
    public LabelAnt danger()    { return type(TypographyAnt.TextColor.DANGER); }
    public LabelAnt disabledColor() { return type(TypographyAnt.TextColor.DISABLED); }

    /** 文本是否换行。 */
    public LabelAnt wrap(boolean wrap) {
        setWrapText(wrap);
        return this;
    }

    /** 设置图标节点（graphic）。 */
    public LabelAnt graphic(Node graphic) {
        setGraphic(graphic);
        return this;
    }

    /** 图标相对文字的摆放位置。 */
    public LabelAnt contentDisplay(ContentDisplay display) {
        setContentDisplay(display);
        return this;
    }

    /** 文本对齐方式。 */
    public LabelAnt align(Pos alignment) {
        setAlignment(alignment);
        return this;
    }

    // ============================================================
    // 富排版装饰（与 TypographyAnt.TextBuilder 对齐，委托给同一套 typography-* styleClass）
    // ============================================================

    /**
     * 字体加粗 toggle。
     *
     * <p>使用 {@link Font} API 设置 FontWeight，保留当前字号与字体族（避免 setStyle 写 -fx-font-weight）。</p>
     *
     * @param strong true=加粗（BOLD），false=还原（NORMAL）
     */
    public LabelAnt strong(boolean strong) {
        Font cur = getFont();
        String family = cur != null ? cur.getFamily() : Font.getDefault().getFamily();
        double size = cur != null ? cur.getSize() : Font.getDefault().getSize();
        setFont(Font.font(family, strong ? FontWeight.BOLD : FontWeight.NORMAL, size));
        return this;
    }

    /** 字体加粗（强类型快捷）。 */
    public LabelAnt strong() { return strong(true); }

    /** 斜体 toggle（挂 {@code jfx-typography-italic}）。 */
    public LabelAnt italic(boolean italic) {
        if (italic) getStyleClass().add(JfxStyles.TYPOGRAPHY_ITALIC);
        else getStyleClass().remove(JfxStyles.TYPOGRAPHY_ITALIC);
        return this;
    }

    /** 斜体（强类型快捷）。 */
    public LabelAnt italic() { return italic(true); }

    /** 下划线 toggle（挂 {@code jfx-typography-underline}）。 */
    public LabelAnt underline(boolean underline) {
        if (underline) getStyleClass().add(JfxStyles.TYPOGRAPHY_UNDERLINE);
        else getStyleClass().remove(JfxStyles.TYPOGRAPHY_UNDERLINE);
        return this;
    }

    /** 下划线（强类型快捷）。 */
    public LabelAnt underline() { return underline(true); }

    /** 删除线 toggle（挂 {@code jfx-typography-delete}）。 */
    public LabelAnt delete(boolean delete) {
        if (delete) getStyleClass().add(JfxStyles.TYPOGRAPHY_DELETE);
        else getStyleClass().remove(JfxStyles.TYPOGRAPHY_DELETE);
        return this;
    }

    /** 删除线（强类型快捷）。 */
    public LabelAnt delete() { return delete(true); }

    /** 代码片段样式 toggle（挂 {@code jfx-typography-code}：等宽字体 + 浅灰底 + 圆角边框）。 */
    public LabelAnt code(boolean code) {
        if (code) getStyleClass().add(JfxStyles.TYPOGRAPHY_CODE);
        else getStyleClass().remove(JfxStyles.TYPOGRAPHY_CODE);
        return this;
    }

    /** 代码片段样式（强类型快捷）。 */
    public LabelAnt code() { return code(true); }

    /** 黄色高亮 toggle（挂 {@code jfx-typography-mark}）。 */
    public LabelAnt mark(boolean mark) {
        if (mark) getStyleClass().add(JfxStyles.TYPOGRAPHY_MARK);
        else getStyleClass().remove(JfxStyles.TYPOGRAPHY_MARK);
        return this;
    }

    /** 黄色高亮（强类型快捷）。 */
    public LabelAnt mark() { return mark(true); }

    /**
     * 可复制 toggle：挂 {@code jfx-typography-copyable} 修饰类 + 接管 {@code onMouseClicked} 复制文本到剪贴板。
     *
     * <p><b>注意</b>：copyable=true 时会独占 {@code onMouseClicked}；若业务需要额外的 click 行为，
     * 请勿同时设置 copyable。</p>
     *
     * @param copyable true=启用，false=关闭（同时清掉 click handler）
     */
    public LabelAnt copyable(boolean copyable) {
        if (copyable) {
            getStyleClass().add(JfxStyles.TYPOGRAPHY_COPYABLE);
            setOnMouseClicked(e -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(getText());
                clipboard.setContent(content);
            });
        } else {
            getStyleClass().remove(JfxStyles.TYPOGRAPHY_COPYABLE);
            setOnMouseClicked(null);
        }
        return this;
    }

    /** 可复制（强类型快捷）。 */
    public LabelAnt copyable() { return copyable(true); }

    /**
     * 单行省略 toggle。
     *
     * <p>启用时关闭换行并钳制最大高度为 1 行（{@code rows * @font-size-md * 1.4 ≈ 20}，与 TypographyAnt 一致）；
     * 关闭时还原最大高度为 {@link Region#USE_COMPUTED_SIZE}。</p>
     */
    public LabelAnt ellipsis(boolean ellipsis) {
        if (ellipsis) {
            setWrapText(false);
            setMaxHeight(FontTokens.LINE_HEIGHT_NORMAL);
        } else {
            setMaxHeight(Region.USE_COMPUTED_SIZE);
        }
        return this;
    }

    /**
     * 多行省略：rows 限制最大显示行数。
     *
     * <p>启用时开启换行并钳制最大高度为 {@code rows * FontTokens.LINE_HEIGHT_NORMAL}（与 TypographyAnt 一致）；rows&lt;=0 等同于关闭省略。</p>
     */
    public LabelAnt ellipsis(int rows) {
        if (rows > 0) {
            setWrapText(true);
            setMaxHeight(rows * FontTokens.LINE_HEIGHT_NORMAL);
        } else {
            setMaxHeight(Region.USE_COMPUTED_SIZE);
        }
        return this;
    }

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>LabelAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟其它 *Ant 的 {@code build()} 对齐。业务继承场景无需调 build()。</p>
     */
    public LabelAnt build() {
        return this;
    }
}
