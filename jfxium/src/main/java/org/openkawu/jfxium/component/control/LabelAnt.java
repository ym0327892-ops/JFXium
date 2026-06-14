package org.openkawu.jfxium.component.control;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * LabelAnt - 继承式原生 Label 封装（M19.48 引入）。
 *
 * <p><b>定位</b>：项目最基础的文本控件，补齐「连最简单的 Label 都没有封装」的缺口。
 * 跟 {@link VBoxAnt} 同款「双工厂模式」——既能当工厂链式构建，也能被业务继承。</p>
 *
 * <h2>跟 TypographyAnt 的区别</h2>
 * <ul>
 *   <li>{@link TypographyAnt} —— 富排版（title/paragraph/text + copyable/strong/code 等装饰），
 *       Builder 模式，{@code build()} 返回原生 {@link Label}（不可继承）。</li>
 *   <li>{@code LabelAnt} —— 轻量「带链式的 Label」，{@code extends Label} 本身，
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
        getStyleClass().add(JfxStyles.TYPOGRAPHY_TEXT);
    }

    public LabelAnt(String text) {
        super(text);
        getStyleClass().add(JfxStyles.TYPOGRAPHY_TEXT);
    }

    public LabelAnt(String text, Node graphic) {
        super(text, graphic);
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

    /** 设置文字语义色（直接收 {@link TypographyAnt.Type}，不拷贝枚举）。 */
    public LabelAnt type(TypographyAnt.Type type) {
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
    public LabelAnt secondary() { return type(TypographyAnt.Type.SECONDARY); }
    public LabelAnt success()   { return type(TypographyAnt.Type.SUCCESS); }
    public LabelAnt warning()   { return type(TypographyAnt.Type.WARNING); }
    public LabelAnt danger()    { return type(TypographyAnt.Type.DANGER); }
    public LabelAnt disabledColor() { return type(TypographyAnt.Type.DISABLED); }

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
